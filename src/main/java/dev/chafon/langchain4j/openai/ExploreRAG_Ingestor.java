package dev.chafon.langchain4j.openai;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.document.parser.TextDocumentParser;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.embedding.onnx.bgesmallenv15q.BgeSmallEnV15QuantizedEmbeddingModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ExploreRAG_Ingestor {

    public static void main(String[] args) {
        // Initialize the document path
        Path documentPath = null;
        try {
            URL fileUrl = ExploreRAG_Basic.class.getClassLoader().getResource(ExploreRAG_Basic.DOCUMENT_PATH);
            documentPath = Paths.get(fileUrl.toURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        // Load the document
        Document document = FileSystemDocumentLoader
                .loadDocument(documentPath, new TextDocumentParser());

        // Embed the segments
        EmbeddingModel embeddingModel = new BgeSmallEnV15QuantizedEmbeddingModel();

        // Store the embeddings
        EmbeddingStore<TextSegment> embeddingStore = new InMemoryEmbeddingStore<>();

        // Initialize the ingestor
        EmbeddingStoreIngestor ingestor = EmbeddingStoreIngestor.builder()
                .embeddingModel(embeddingModel)
                .embeddingStore(embeddingStore)
                .documentSplitter(DocumentSplitters
                        .recursive(1000, 100))
                .build();

        // Ingest the document
        ingestor.ingest(document);

        // Initialize the content retriever
        ContentRetriever contentRetriever = EmbeddingStoreContentRetriever.builder()
                .embeddingStore(embeddingStore)
                .embeddingModel(embeddingModel)
                .maxResults(2) // on each interaction we will retrieve the 2 most relevant segments
                .minScore(0.5) // we want to retrieve segments at least somewhat similar to user query
                .build();

        // Initialize chat memory
        ChatMemory chatMemory = MessageWindowChatMemory.withMaxMessages(10);

        // Initialize the OpenAI chat model
        String apiKey = System.getenv("OPENAI_API_KEY");
        OpenAiChatModel chatLanguageModel = OpenAiChatModel.withApiKey(apiKey);

        // Initialize the AI assistant
        Assistant assistant = AiServices.builder(Assistant.class)
                .chatLanguageModel(chatLanguageModel)
                .contentRetriever(contentRetriever)
                .chatMemory(chatMemory)
                .build();

        // Chat with the assistant
        System.out.println(assistant.answer("Who is John Doe?"));
        System.out.println("--------------------------------------------------------------------------------");
        System.out.println(assistant.answer("What did John Doe do?"));
        System.out.println("--------------------------------------------------------------------------------");
        System.out.println(assistant.answer("What are the achievements of John Doe?"));
    }

    interface Assistant {
        String answer(String query);
    }
}
