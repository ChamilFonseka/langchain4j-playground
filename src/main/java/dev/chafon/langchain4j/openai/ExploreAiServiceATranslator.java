package dev.chafon.langchain4j.openai;

import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

import java.util.List;

public class ExploreAiServiceATranslator  {

    public static void main(String[] args) {
        OpenAiChatModel model = OpenAiChatModel.withApiKey(System.getenv("OPENAI_API_KEY"));

        Translator translator = AiServices.create(Translator.class, model);

        String translation = translator.translate("Hello, how are you?", "French");
        System.out.println(translation);
        System.out.println("-----------------------------------------------------------------------------");

        String text = "AI, or artificial intelligence, is a branch of computer science that aims to create " +
                "machines that mimic human intelligence. This can range from simple tasks such as recognizing " +
                "patterns or speech to more complex tasks like making decisions or predictions.";

        List<String> summary = translator.summarize(text, 5);
        summary.forEach(System.out::println);
    }
}

interface Translator {

    @SystemMessage("You are a professional translator into {{language}}")
    @UserMessage("Translate the following text: {{text}}")
    String translate(@V("text") String text, @V("language") String language);

    @SystemMessage("Summarize every message from user in {{n}} bullet points. Provide only bullet points.")
    List<String> summarize(@UserMessage String text, @V("n") int n);
}
