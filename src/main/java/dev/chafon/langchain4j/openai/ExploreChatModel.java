package dev.chafon.langchain4j.openai;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;

public class ExploreChatModel {
    public static void main(String[] args) {
        String apiKey = System.getenv("OPENAI_API_KEY");
        OpenAiChatModel model = OpenAiChatModel.withApiKey(apiKey);
        simpleChatWithTheModel(model);
    }

    private static void simpleChatWithTheModel(ChatLanguageModel model) {
        UserMessage firstUserMessage = UserMessage.from("Hello, my name is Chamil");
        System.out.println(firstUserMessage.singleText());
        AiMessage firstAiMessage = model.generate(firstUserMessage).content();
        System.out.println(firstAiMessage.text());

        UserMessage secondUserMessage = UserMessage.from("What is my name?");
        System.out.println(secondUserMessage.singleText());
        AiMessage secondAiMessage = model.generate(firstUserMessage, firstAiMessage, secondUserMessage).content();
        System.out.println(secondAiMessage.text());
    }
}
