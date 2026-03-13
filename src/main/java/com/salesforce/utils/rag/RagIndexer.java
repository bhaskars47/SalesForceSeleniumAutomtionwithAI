package com.salesforce.utils.rag;

import com.salesforce.utils.LoggerUtils;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.document.parser.TextDocumentParser;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.ollama.OllamaEmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.List;

public class RagIndexer {

    private static final String OLLAMA_BASE_URL = "http://localhost:11434";
    private static final String EMBEDDING_MODEL_NAME = "nomic-embed-text";
    private static final String KNOWLEDGE_BASE_DIR = "src/main/resources/knowledge_base";

    // Singleton instance of the embedding store
    private static InMemoryEmbeddingStore<TextSegment> embeddingStore;

    /**
     * Initializes the embedding store by reading all documents in the knowledge
     * base,
     * splitting them into chunks, generating embeddings via Ollama, and storing
     * them.
     */
    public static void initIndex() {
        if (embeddingStore != null) {
            LoggerUtils.info("RAG Index already initialized.");
            return;
        }

        try {
            LoggerUtils.info("Initializing RAG Indexer...");
            embeddingStore = new InMemoryEmbeddingStore<>();

            // 1. Initialize the embedding model (connecting to local Ollama)
            EmbeddingModel embeddingModel = OllamaEmbeddingModel.builder()
                    .baseUrl(OLLAMA_BASE_URL)
                    .modelName(EMBEDDING_MODEL_NAME)
                    .timeout(Duration.ofSeconds(60))
                    .build();

            // 2. Load all documents from the knowledge_base directory
            Path kbPath = Paths.get(System.getProperty("user.dir"), KNOWLEDGE_BASE_DIR);
            List<Document> documents = FileSystemDocumentLoader.loadDocuments(kbPath, new TextDocumentParser());
            LoggerUtils.info("Loaded " + documents.size() + " documents from knowledge base.");

            // 3. Create an ingestor that splits text into 500-character chunks with a
            // 50-char overlap
            EmbeddingStoreIngestor ingestor = EmbeddingStoreIngestor.builder()
                    .documentSplitter(DocumentSplitters.recursive(500, 50))
                    .embeddingModel(embeddingModel)
                    .embeddingStore(embeddingStore)
                    .build();

            // 4. Ingest the documents (this calculates the vectors and saves them in
            // memory)
            ingestor.ingest(documents);
            LoggerUtils.info("Successfully ingested documents into the Vector Store.");

        } catch (Exception e) {
            LoggerUtils.error("Failed to initialize RAG Index: " + e.getMessage());
            throw new RuntimeException("RAG Initialization failed. Is Ollama running?", e);
        }
    }

    /**
     * Returns the populated EmbeddingStore for querying.
     */
    public static EmbeddingStore<TextSegment> getEmbeddingStore() {
        if (embeddingStore == null) {
            initIndex();
        }
        return embeddingStore;
    }
}
