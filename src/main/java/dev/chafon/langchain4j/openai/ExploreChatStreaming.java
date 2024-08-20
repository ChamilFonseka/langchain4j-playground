package dev.chafon.langchain4j.openai;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.StreamingResponseHandler;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import dev.langchain4j.model.output.Response;

public class ExploreChatStreaming {

    public static void main(String[] args) {
        String apiKey = System.getenv("OPENAI_API_KEY");
        OpenAiStreamingChatModel model = OpenAiStreamingChatModel.withApiKey(apiKey);

        UserMessage userMessage = UserMessage.from("Tell me a joke.");

        model.generate(userMessage, new StreamingResponseHandler<AiMessage>() {
            @Override
            public void onNext(String token) {
                System.out.println("onNext: " + token);
            }

            @Override
            public void onComplete(Response<AiMessage> response) {
                System.out.println("onComplete: " + response);
                System.exit(0);
            }

            @Override
            public void onError(Throwable error) {
                System.err.println(error.getMessage());
            }
        });
    }
}
