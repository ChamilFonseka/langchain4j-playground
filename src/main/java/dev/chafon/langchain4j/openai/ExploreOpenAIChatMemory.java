package dev.chafon.langchain4j.openai;

import dev.langchain4j.chain.ConversationalChain;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.UserMessage;

public class ExploreOpenAIChatMemory {

    public static void main(String[] args) {
        String apiKey = System.getenv("OPENAI_API_KEY");
        OpenAiChatModel model = OpenAiChatModel.withApiKey(apiKey);

        ChatMemory chatMemory = MessageWindowChatMemory.withMaxMessages(10);
        //ChatMemory chatMemory = TokenWindowChatMemory.withMaxTokens(300, new OpenAiTokenizer(GPT_3_5_TURBO));

        withConversationalChain(model, chatMemory);
        withAiServices(model, chatMemory);
        withAiServicesWithMemoryId(model, chatMemory);
    }

    private static void withAiServicesWithMemoryId(OpenAiChatModel model, ChatMemory chatMemory) {
        AssistantWithMemoryId assistant = AiServices.builder(AssistantWithMemoryId.class)
                .chatLanguageModel(model)
                .chatMemoryProvider(memoryId -> MessageWindowChatMemory.withMaxMessages(10))
                .build();

        String firstUserMessage = "Hello, my name is Chamil";
        System.out.println(firstUserMessage);
        System.out.println(assistant.chat(1, firstUserMessage));

        String secondUserMessage = "Hello, my name is Ayan";
        System.out.println(secondUserMessage);
        System.out.println(assistant.chat(2, secondUserMessage));

        String secondMessage = "What is my name?";

        System.out.println(secondMessage);
        System.out.println(assistant.chat(1, secondMessage));

        System.out.println(secondMessage);
        System.out.println(assistant.chat(2, secondMessage));
    }

    private static void withAiServices(ChatLanguageModel model, ChatMemory chatMemory) {
        Assistant assistant = AiServices.builder(Assistant.class)
                .chatLanguageModel(model)
                .chatMemory(chatMemory)
                .build();

        String firstMessage = "Hello, my name is Chamil";
        System.out.println(firstMessage);
        System.out.println(assistant.chat(firstMessage));

        String secondMessage = "What is my name?";
        System.out.println(secondMessage);
        System.out.println(assistant.chat(secondMessage));
    }

    private static void withConversationalChain(ChatLanguageModel model, ChatMemory chatMemory) {
        ConversationalChain chain = ConversationalChain.builder()
                .chatLanguageModel(model)
                .chatMemory(chatMemory)
                .build();

        String firstMessage = "Hello, my name is Chamil";
        System.out.println(firstMessage);
        System.out.println(chain.execute(firstMessage));

        String secondMessage = "What is my name?";
        System.out.println(secondMessage);
        System.out.println(chain.execute(secondMessage));
    }

    interface Assistant {
        String chat(String userMessage);
    }

    interface AssistantWithMemoryId {
        String chat(@MemoryId int memoryId, @UserMessage String userMessage);
    }
}
