package com.salesforce.utils.rag;

import com.salesforce.utils.LoggerUtils;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import org.openqa.selenium.By;

import java.time.Duration;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class SelfHealingUtils {

    private static final String OLLAMA_BASE_URL = "http://localhost:11434";
    private static final String CHAT_MODEL_NAME = "llama3.2";
    private static ChatLanguageModel chatModel;

    private static void initModel() {
        if (chatModel == null) {
            try {
                LoggerUtils.info("Initializing Ollama Chat Model for Self-Healing...");
                chatModel = OllamaChatModel.builder()
                        .baseUrl(OLLAMA_BASE_URL)
                        .modelName(CHAT_MODEL_NAME)
                        .temperature(0.0) // We want deterministic locator generation
                        .timeout(java.time.Duration.ofMinutes(2))
                        .build();
                LoggerUtils.info("Successfully connected to Ollama (llama3.2) for Self-Healing.");
            } catch (Exception e) {
                LoggerUtils.error("Failed to initialize Ollama Chat Model in SelfHealingUtils: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    /**
     * Attempts to heal a broken locator by analyzing the DOM with a local LLM.
     *
     * @param elementName The logical name of the element (e.g., "Login Button").
     * @param oldLocator  The Selenium locator that failed.
     * @param pageSource  The raw HTML DOM of the page where it failed.
     * @return A new org.openqa.selenium.By locator suggested by the AI, or null if
     *         it fails.
     */
    public static By healLocator(String elementName, By oldLocator, String pageSource) {
        initModel();

        LoggerUtils.info("Initiating Self-Healing for element: " + elementName + " (Old Locator: " + oldLocator + ")");

        // 1. Clean the DOM to fit into the LLM context window more efficiently
        String cleanedDom = cleanDom(pageSource);

        // 2. Construct the specialized Prompt with strict guardrails
        String prompt = String.format(
                "You are an expert Selenium Automation Engineer specializing in robust locators.\n" +
                        "CONTEXT:\n" +
                        "A test failed to find element '%s' with old locator: '%s'.\n\n" +
                        "GOAL:\n" +
                        "Suggest the most stable, unique CSS or XPath locator based ONLY on the DOM snippet below.\n\n"
                        +
                        "STRICT RULES:\n" +
                        "1. DO NOT hallucinate attributes that are not in the DOM.\n" +
                        "2. Favor IDs, then stable names/classes. Avoid long absolute XPaths.\n" +
                        "3. Format: XPATH: //your/xpath OR CSS: .your-css\n" +
                        "4. If no clear match is found, return: NONE\n" +
                        "5. Provide ONLY the locator string. No markdown, no prose.\n\n" +
                        "DOM SNIPPET:\n%s",
                elementName, oldLocator.toString(), cleanedDom);

        try {
            // 3. Query the Local LLM
            LoggerUtils.info(
                    "Sending DOM context to Llama 3.2 for analysis. Context size: " + cleanedDom.length() + " chars.");
            String aiResponse = chatModel.generate(prompt);
            LoggerUtils.info("LLM Suggested Locator: " + aiResponse);

            // 4. Parse the LLM response into a Selenium By object
            By parsedLocator = parseAiResponse(aiResponse);

            if (parsedLocator != null) {
                LoggerUtils.info("Successfully parsed AI response. Verifying against DOM...");
                if (verifyLocatorMatch(parsedLocator, pageSource)) {
                    LoggerUtils.info("Verification SUCCESS: Locator matches an element in the current DOM.");
                    return parsedLocator;
                } else {
                    LoggerUtils.error("Verification FAILURE: Suggested locator does not match any element in the DOM.");
                }
            }
            return null;

        } catch (Exception e) {
            LoggerUtils.error("Self-Healing failed completely: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Uses Jsoup to powerfully minify the DOM, extracting ONLY interactive elements
     * like inputs and buttons to ensure the LLM's context window is tiny (lightning
     * fast)
     * and hallucination-free.
     */
    private static String cleanDom(String rawHtml) {
        if (rawHtml == null)
            return "";

        try {
            Document doc = Jsoup.parse(rawHtml);

            // Extract inputs, buttons, selects, and anchors (often used as buttons)
            // Also include elements with 'role' attribute or 'onclick'
            Elements interactables = doc.select("input, button, select, a, [role], [onclick]");

            StringBuilder sb = new StringBuilder();
            for (Element el : interactables) {
                // Strip styles and bulky data attributes to save tokens
                el.removeAttr("style");
                el.removeAttr("data-aura-rendered-by");
                el.removeAttr("data-reactid");

                // Only take elements that have some identifying characteristics
                if (el.attributes().size() > 0 || !el.text().isEmpty()) {
                    sb.append(el.outerHtml()).append("\n");
                }
            }

            String cleaned = sb.toString();

            if (cleaned.length() > 2000) {
                cleaned = cleaned.substring(0, 2000) + "...[TRUNCATED]";
            }
            return cleaned.trim();

        } catch (Exception e) {
            return rawHtml.length() > 2000 ? rawHtml.substring(0, 2000) : rawHtml;
        }
    }

    /**
     * Uses Jsoup to verify if the suggested locator actually returns any elements
     * in the provided HTML source. This acts as a hallucination guardrail.
     */
    private static boolean verifyLocatorMatch(By locator, String html) {
        try {
            Document doc = Jsoup.parse(html);
            String selector = "";
            String locatorStr = locator.toString();

            if (locatorStr.contains("By.xpath: ")) {
                // Jsoup supports basic XPaths, but for complex ones we might need a better
                // library
                // However, for most self-healed locators, Jsoup's select() is safer
                selector = locatorStr.replace("By.xpath: ", "");
                // Note: Jsoup uses CSS selectors mainly. If it's XPath, we might need to
                // convert
                // or use a different verification strategy. For now, we'll try to find by
                // attributes.
                return !doc.selectXpath(selector).isEmpty();
            } else if (locatorStr.contains("By.cssSelector: ")) {
                selector = locatorStr.replace("By.cssSelector: ", "");
                return !doc.select(selector).isEmpty();
            }
            return false;
        } catch (Exception e) {
            LoggerUtils.error("Verification error: " + e.getMessage());
            return false;
        }
    }

    /**
     * Converts the string output from the LLM back into a functional Selenium
     * Locator.
     */
    private static By parseAiResponse(String aiResponse) {
        if (aiResponse == null || aiResponse.trim().isEmpty() || aiResponse.equalsIgnoreCase("NONE")) {
            return null;
        }

        String cleaned = aiResponse.trim();

        if (cleaned.startsWith("XPATH:")) {
            return By.xpath(cleaned.replace("XPATH:", "").trim());
        } else if (cleaned.startsWith("CSS:")) {
            return By.cssSelector(cleaned.replace("CSS:", "").trim());
        } else if (cleaned.startsWith("By.xpath:")) {
            return By.xpath(cleaned.replace("By.xpath:", "").trim());
        } else if (cleaned.startsWith("By.cssSelector:")) {
            return By.cssSelector(cleaned.replace("By.cssSelector:", "").trim());
        }

        // Fallback: If the LLM just returns an XPath starting with //
        if (cleaned.startsWith("//")) {
            return By.xpath(cleaned);
        }

        LoggerUtils.error("Could not parse LLM response into a locator: " + aiResponse);
        return null; // Let the original exception throw
    }
}
