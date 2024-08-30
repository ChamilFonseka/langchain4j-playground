package dev.chafon.langchain4j.openai;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.document.parser.TextDocumentParser;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.embedding.onnx.bgesmallenv15q.BgeSmallEnV15QuantizedEmbeddingModel;
import dev.langchain4j.model.input.Prompt;
import dev.langchain4j.model.input.PromptTemplate;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.store.embedding.EmbeddingSearchRequest;
import dev.langchain4j.store.embedding.EmbeddingSearchResult;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ExploreRAG_LowLevel {

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

        // Split the document into segments
        DocumentSplitter documentSplitter = DocumentSplitters
                .recursive(1000, 100);
        List<TextSegment> textSegments = documentSplitter.split(document);

        // Embed the segments
        EmbeddingModel embeddingModel = new BgeSmallEnV15QuantizedEmbeddingModel();
        List<Embedding> embeddings = embeddingModel.embedAll(textSegments).content();

        // Store the embeddings
        EmbeddingStore<TextSegment> embeddingStore = new InMemoryEmbeddingStore<>();
        embeddingStore.addAll(embeddings, textSegments);

        // Embed the question
        String question = "Who is John Doe?";
        Embedding queryEmbedding = embeddingModel.embed(question).content();

        // Initialize the search request
        EmbeddingSearchRequest embeddingSearchRequest = EmbeddingSearchRequest.builder()
                .queryEmbedding(queryEmbedding)
                .maxResults(2)
                .minScore(0.5)
                .build();

        // Search for the most relevant segments
        EmbeddingSearchResult<TextSegment> searchResult = embeddingStore.search(embeddingSearchRequest);

        // Extract the context
        String context = searchResult.matches().stream()
                .map(match -> match.embedded().text())
                .collect(Collectors.joining());

        // Initialize the prompt template
        PromptTemplate promptTemplate = PromptTemplate.from(
                "Answer the following question: \n"
                        + "Question: {{question}}\n"
                        + "Based on the following context: \n"
                        + "Context: {{context}}");

        // Apply the variables to the prompt template
        Map<String, Object> variables = new HashMap<>();
        variables.put("question", question);
        variables.put("context", context);

        // Generate the prompt
        Prompt prompt = promptTemplate.apply(variables);

        // Initialize the OpenAI chat model
        String apiKey = System.getenv("OPENAI_API_KEY");
        OpenAiChatModel chatLanguageModel = OpenAiChatModel.withApiKey(apiKey);

        // Chat with the assistant
        AiMessage aiMessage = chatLanguageModel.generate(prompt.toUserMessage()).content();

        System.out.println(aiMessage.text());
    }
}
