package com.salesforce.tests;

import com.salesforce.utils.rag.RagService;
import org.testng.Assert;
import org.testng.annotations.Test;

public class RagTest {

    @Test(description = "Verify RAG system retrieves data correctly from the PRD")
    public void testRagRetrieval() {
        // Query 1: Information from sample_requirements.md
        String query1 = "What is the expected error message if a user submits the login form without a password?";
        String response1 = RagService.query(query1);
        System.out.println("Answer: " + response1);
        Assert.assertTrue(response1.contains("Please enter your password"),
                "LLM failed to retrieve validation rule 1.");

        System.out.println("-------------------------------------------------");

        // Query 2: Information from LoginPRD.md
        String query2 = "According to the Login Experience PRD, what happens in Path B (SSO) during the Verification step?";
        String response2 = RagService.query(query2);
        System.out.println("Answer: " + response2);

        // Make the assertion case-insensitive as the LLM might capitalize differently
        // (e.g., "redirected" vs "Redirect")
        String lowerResponse = response2.toLowerCase();
        Assert.assertTrue(lowerResponse.contains("redirect") &&
                (lowerResponse.contains("okta") || lowerResponse.contains("azure")),
                "LLM failed to retrieve SSO path.");
    }
}
