package dev.chafon.langchain4j.openai;

import dev.langchain4j.data.image.Image;
import dev.langchain4j.model.image.ImageModel;
import dev.langchain4j.model.openai.OpenAiImageModel;
import dev.langchain4j.model.output.Response;

public class ExploreImageModel {

    public static void main(String[] args) {
        ImageModel model = OpenAiImageModel.withApiKey(System.getenv("OPENAI_API_KEY"));

        Response<Image> response = model.generate("Cosmic landscape with the Milky Way galaxy, stars and planets. " +
                "Elements of this image furnished by NASA.");

        System.out.println(response.content().url());
    }
}
