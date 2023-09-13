package com.kremnev8.electroniccookbook.components.recipe.importPage;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.util.Pair;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.hilt.work.HiltWorker;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.FutureTarget;
import com.google.common.base.Strings;
import com.kremnev8.electroniccookbook.common.KtUtil;
import com.kremnev8.electroniccookbook.components.ingredient.model.Ingredient;
import com.kremnev8.electroniccookbook.components.profile.ProfileModule;
import com.kremnev8.electroniccookbook.components.profile.model.Profile;
import com.kremnev8.electroniccookbook.components.recipe.model.Recipe;
import com.kremnev8.electroniccookbook.components.recipe.model.RecipeIngredient;
import com.kremnev8.electroniccookbook.components.recipe.model.RecipeStep;
import com.kremnev8.electroniccookbook.database.DatabaseExecutor;
import com.kremnev8.electroniccookbook.interfaces.IProfileProvider;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import javax.inject.Inject;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedInject;

@HiltWorker
public class ImportRecipeWorker extends Worker {

    private final String pageUrl;
    private final Map<String, String> dataDict = new HashMap<>();

    private final DatabaseExecutor databaseExecutor;
    private final IProfileProvider profileModule;

    @AssistedInject
    public ImportRecipeWorker(@Assisted @NonNull Context context, @Assisted @NonNull WorkerParameters workerParams,
                              DatabaseExecutor databaseExecutor, IProfileProvider profileModule) {
        super(context, workerParams);
        this.databaseExecutor = databaseExecutor;
        this.profileModule = profileModule;
        pageUrl = workerParams.getInputData().getString("url");
    }

    @NonNull
    @Override
    public Result doWork() {
        try {
            Document doc = Jsoup.connect(pageUrl).get();

            doc.select("script, style, .hidden").remove();

            Elements all = doc.select("*");
            for (Element el : all) {
                for (Attribute attr : el.attributes()) {
                    String attrKey = attr.getKey();
                    if (attrKey.equals("style") || attrKey.startsWith("on")) {
                        el.removeAttr(attrKey);
                    }
                }
            }

            Log.i("IMPORT", doc.title());

            Elements links = doc.select("a[href]"); // a with href
            Elements images = doc.select("img");

            checkProps(doc.select("[itemprop]"));


            checkClass(doc.select("[id~=(?i)subheading]"), "description");
            checkClass(doc.select("[id~=(?i)description]"), "description");
            checkClass(doc.select("[id~=(?i)ingredients]"), "ingredients");
            checkClass(doc.select("[id~=(?i)steps]"), "steps");
            checkClass(doc.select("[id~=(?i)nutrition]"), "nutrition");

            checkClass(doc.select("[class~=(?i)subheading]"), "description");
            checkClass(doc.select("[class~=(?i)description]"), "description");
            checkClass(doc.select("[class~=(?i)ingredients]"), "ingredients");
            checkClass(doc.select("[class~=(?i)steps]"), "steps");
            checkClass(doc.select("[class~=(?i)nutrition]"), "nutrition");


            Log.i("IMPORT", "My dict got:");
            for (var key : dataDict.keySet()) {

                Log.i("IMPORT", key + ": " + dataDict.get(key));
            }
            var data = new Data.Builder();

            parseSteps(data);
            parseIngredients(data);
            findGoodImage(images, data);

            data.putString("title", doc.title());
            data.putString("description", dataDict.get("description"));
            data.putString("nutrition", dataDict.get("nutrition"));

            return createRecipe(data.build());
        } catch (Exception e) {
            e.printStackTrace();
            return Result.failure();
        }
    }

    private Result createRecipe(Data data){

        Recipe recipe = new Recipe();
        recipe.lastModified = new Date();
        recipe.yield = 1;
        recipe.name = trimMaybe(data.getString("title"));
        recipe.description = trimMaybe(data.getString("description"));
        recipe.nutritionInfo = trimMaybe(data.getString("nutrition"));
        recipe.imageUri = data.getString("image");

        recipe.ingredients = new ArrayList<>();
        recipe.steps = new ArrayList<>();

        Profile profile = profileModule.getCurrentProfile().blockingFirst();
        recipe.profileId = profile.id;

        String[] ingredients = data.getStringArray("ingredients");
        String[] ingredientCounts = data.getStringArray("ingredientCounts");
        assert ingredients != null;
        assert ingredientCounts != null;

        for (int i = 0; i < ingredients.length; i++) {
            String name = ingredients[i];
            String amount = ingredientCounts[i];
            RecipeIngredient recipeIngredient = new RecipeIngredient();
            recipeIngredient.ingredientName = name.trim();
            recipeIngredient.setAmount(Ingredient.ParseAmountString(amount.trim()));

            recipe.ingredients.add(recipeIngredient);
        }

        String[] steps = data.getStringArray("steps");
        assert steps != null;
        int count = 1;

        for (String stepStr: steps) {
            RecipeStep step = new RecipeStep();
            step.text = stepStr;
            step.stepNumber = count++;

            recipe.steps.add(step);
        }

        int id = (int)databaseExecutor.insertWithDataBlocking(recipe);
        var retData = new Data.Builder();
        retData.putInt("recipeId", id);
        return Result.success(retData.build());
    }

    private String trimMaybe(@Nullable String str){
        if (Strings.isNullOrEmpty(str))
            return "";
        return str.trim();
    }

    private void findGoodImage(Elements images, Data.Builder data) throws ExecutionException, InterruptedException {
        for (var image : images) {
            String urlStr = image.attr("src");
            if (Strings.isNullOrEmpty(urlStr)) continue;

            try {
                FutureTarget<Bitmap> futureBitmap = Glide.with(getApplicationContext())
                        .asBitmap()
                        .load(urlStr)
                        .submit();
                Bitmap bitmap = futureBitmap.get();
                if (bitmap.getWidth() > 400 && bitmap.getHeight() > 400) {
                    data.putString("image", urlStr);
                    break;
                }
            }catch (Exception e){
                // ignored
            }
        }
    }

    private void parseIngredients(Data.Builder data) {
        String ingredientsStr = null;

        if (dataDict.containsKey("recipeIngredient")) {
            ingredientsStr = dataDict.get("recipeIngredient");
        } else if (dataDict.containsKey("ingredients")) {
            ingredientsStr = dataDict.get("ingredients");
        }

        if (!Strings.isNullOrEmpty(ingredientsStr)) {
            String[] ingredients = ingredientsStr.split("\n");
            if (ingredients.length <= 1) {
                ingredients = ingredientsStr.split("\\. ");
            }

            List<Pair<String, String>> pairs = new ArrayList<>();

            for (var ingredient : ingredients) {
                Pattern pattern = Pattern.compile("([\\p{L}\\s]+)(\\s?-\\s?)?(.+)");
                Matcher matcher = pattern.matcher(ingredient);
                if (matcher.matches()) {
                    if (matcher.groupCount() == 1) {
                        pairs.add(new Pair<>(matcher.group(1), ""));
                    } else {
                        pairs.add(new Pair<>(matcher.group(1), matcher.group(3)));
                    }
                    continue;
                }

                StringBuilder builder = new StringBuilder();
                String name = null;

                String[] parts = ingredient.split("[-\t ]");

                for (String part : parts) {
                    try {
                        double number = KtUtil.fromVulgarFraction(part);
                        name = builder.toString();
                        builder = new StringBuilder();
                        builder.append(number);
                    } catch (NumberFormatException e) {
                        // ignored
                    }

                    builder.append(part);
                }
                if (name == null) {
                    pairs.add(new Pair<>(ingredient, ""));
                    continue;
                }

                pairs.add(new Pair<>(name, builder.toString()));
            }

            data.putStringArray("ingredients", pairs.stream().map(p -> p.first).toArray(String[]::new));
            data.putStringArray("ingredientCounts", pairs.stream().map(p -> p.second).toArray(String[]::new));
        }
    }

    private void parseSteps(Data.Builder data) {
        if (dataDict.containsKey("steps")) {
            String stepsStr = dataDict.get("steps");
            assert stepsStr != null;

            String[] steps = stepsStr.split("(?i)step [\\d\\\\/]+");
            if (steps.length <= 1) {
                steps = stepsStr.split("(?i)шаг [\\d\\\\/]+");
            }

            steps = Arrays.stream(steps).sequential().flatMap(step -> {
                if (step.length() > 40) {
                    String[] steps1 = step.split("\n");
                    if (steps1.length <= 1) {
                        steps1 = step.split("\\. ");
                    }
                    return Arrays.stream(steps1).sequential();
                }
                return Stream.of(step).sequential();
            }).toArray(String[]::new);

            data.putStringArray("steps", steps);
        }
    }

    private void checkClass(Elements elements, String kind) {
        for (var item : elements) {
            var text = item.text();
            item.select("script, style, .hidden").remove();
            if (Strings.isNullOrEmpty(text.trim())) continue;

            if (dataDict.containsKey(kind)) {
                return;
            } else {
                dataDict.put(kind, text);
            }
        }
    }

    private void checkProps(Elements elements) {
        for (var item : elements) {
            var kind = item.attr("itemprop");
            var text = item.attr("content");
            if (Strings.isNullOrEmpty(text)) {
                item.select("script, style, .hidden").remove();
                text = item.text();
            }
            if (Strings.isNullOrEmpty(text.trim())) continue;

            if (dataDict.containsKey(kind)) {
                var current = dataDict.get(kind);
                dataDict.put(kind, current + "\n" + text);
            } else {
                dataDict.put(kind, text);
            }
        }
    }

    private void printAll(Elements itemprops) {
        for (var item : itemprops) {
            var kind = item.attr("itemprop");
            if (Strings.isNullOrEmpty(kind))
                kind = item.className();
            Log.i("IMPORT", kind + ": " + item.text());
        }
    }
}
