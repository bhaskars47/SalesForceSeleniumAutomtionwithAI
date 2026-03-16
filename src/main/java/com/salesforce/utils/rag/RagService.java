package com.salesforce.utils.rag;

import com.salesforce.utils.LoggerUtils;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.store.embedding.EmbeddingStore;

import java.time.Duration;

public class RagService {

    private static final String OLLAMA_BASE_URL = "http://localhost:11434";
    private static final String CHAT_MODEL_NAME = "llama3.2";

    // Define the interface that LangChain4j will implement
    interface Assistant {
        @SystemMessage({
                "You are a helpful assistant for Salesforce Automation engineers.",
                "Your answers must be BASED ONLY on the provided documentation context.",
                "If the answer is not in the documentation, state that you do not know.",
                "DO NOT hallucinate or make up facts that are not explicitly mentioned in the provided chunks."
        })
        String answer(String query);
    }

    private static Assistant assistant;

    /**
     * Initializes the RAG assistant with the embedding store created by the indexer
     * and connects it to the local Ollama LLM.
     */
    public static void initRag() {
        if (assistant != null) {
            return;
        }

        try {
            LoggerUtils.info("Initializing RAG Service AI Assistant...");

            // 1. Get the populated vector store from our Indexer
            EmbeddingStore<TextSegment> embeddingStore = RagIndexer.getEmbeddingStore();

            // 2. Set up the Retriever to fetch the top 3 most relevant chunks
            ContentRetriever contentRetriever = EmbeddingStoreContentRetriever.builder()
                    .embeddingStore(embeddingStore)
                    .embeddingModel(dev.langchain4j.model.ollama.OllamaEmbeddingModel.builder()
                            .baseUrl(OLLAMA_BASE_URL)
                            .modelName("nomic-embed-text")
                            .timeout(Duration.ofSeconds(60))
                            .build())
                    .maxResults(3)
                    .minScore(0.5) // Only return chunks that are somewhat relevant
                    .build();

            // 3. Connect to the local Chat Model (Llama 3.2)
            ChatLanguageModel chatModel = OllamaChatModel.builder()
                    .baseUrl(OLLAMA_BASE_URL)
                    .modelName(CHAT_MODEL_NAME)
                    .temperature(0.0) // 0.0 means completely factual/deterministic answers
                    .timeout(Duration.ofSeconds(120))
                    .build();

            // 4. Build the LangChain4j Assistant
            assistant = AiServices.builder(Assistant.class)
                    .chatLanguageModel(chatModel)
                    .contentRetriever(contentRetriever)
                    .build();

            LoggerUtils.info("RAG Service initialized successfully.");

        } catch (Exception e) {
            LoggerUtils.error("Failed to initialize RAG Service: " + e.getMessage());
            throw new RuntimeException("RAG Service failed to connect. Is Ollama running 'llama3.2'?", e);
        }
    }

    /**
     * Sends a query to the LLM. The LLM will automatically search the Vector Store
     * for relevant documentation and use it to formulate the answer.
     *
     * @param question The question or prompt about the Salesforce requirements.
     * @return The AI-generated answer.
     */
    public static String query(String question) {
        if (assistant == null) {
            initRag();
        }

        LoggerUtils.info("Sending RAG Query: " + question);
        long startTime = System.currentTimeMillis();

        String response = assistant.answer(question);

        long endTime = System.currentTimeMillis();
        LoggerUtils.info("RAG Response received in " + (endTime - startTime) + "ms: \n" + response);

        return response;
    }
}
