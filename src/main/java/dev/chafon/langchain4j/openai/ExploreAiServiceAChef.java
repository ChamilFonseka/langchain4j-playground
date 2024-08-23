package dev.chafon.langchain4j.openai;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.input.structured.StructuredPrompt;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.output.structured.Description;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.SystemMessage;

import java.util.Arrays;
import java.util.List;

public class ExploreAiServiceAChef {

    public static void main(String[] args) {
        ChatLanguageModel model = OpenAiChatModel.builder()
                .apiKey(System.getenv("OPENAI_API_KEY"))
                .build();

        Chef chef = AiServices.create(Chef.class, model);

        String answer = chef.answer("How long should I grill chicken?");
        System.out.println(answer);
        System.out.println("-----------------------------------------------------------------------------");

        Recipe recipe = chef.createRecipeFrom("flour", "eggs", "sugar", "butter", "milk");
        System.out.println(recipe);
        System.out.println("-----------------------------------------------------------------------------");

        CreateRecipePrompt prompt = new CreateRecipePrompt("salad", Arrays.asList("tomato", "cucumber", "onion", "olive oil"));
        Recipe saladRecipe = chef.createRecipe(prompt);
        System.out.println(saladRecipe);
        System.out.println("-----------------------------------------------------------------------------");
    }
}

interface Chef {

    @SystemMessage("You are a professional chef. You are friendly, polite and concise.")
    String answer(String question);

    Recipe createRecipeFrom(String... ingredients);

    Recipe createRecipe(CreateRecipePrompt prompt);
}

class Recipe {

    @Description("short title, 5 words maximum")
    private String title;

    @Description("short description, 3 sentences maximum")
    private String description;

    @Description("each step should be described in 5 words, steps should rhyme")
    private List<String> steps;

    private Integer preparationTimeMinutes;

    @Override
    public String toString() {
        return "Recipe {" +
                " title = \"" + title + "\"" +
                ", description = \"" + description + "\"" +
                ", steps = " + steps +
                ", preparationTimeMinutes = " + preparationTimeMinutes +
                " }";
    }
}

@StructuredPrompt("Create a recipe of a {{dish}} that can be prepared using only {{ingredients}}")
class CreateRecipePrompt {

    private String dish;
    private List<String> ingredients;

    public CreateRecipePrompt(String dish, List<String> ingredients) {
        this.dish = dish;
        this.ingredients = ingredients;
    }

    public String getDish() {
        return dish;
    }

    public List<String> getIngredients() {
        return ingredients;
    }
}