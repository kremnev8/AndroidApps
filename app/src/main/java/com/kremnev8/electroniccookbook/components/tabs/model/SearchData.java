package com.kremnev8.electroniccookbook.components.tabs.model;

import androidx.annotation.Nullable;
import androidx.databinding.Bindable;

import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.kremnev8.electroniccookbook.R;
import com.kremnev8.electroniccookbook.common.recycler.ISearchFilter;
import com.kremnev8.electroniccookbook.components.ingredient.model.Ingredient;
import com.kremnev8.electroniccookbook.components.recipe.model.Recipe;
import com.kremnev8.electroniccookbook.components.recipe.model.RecipeIngredient;

import org.apache.commons.lang3.StringUtils;

import java.util.Comparator;
import java.util.List;

public class SearchData {

    private static final Splitter splitter = Splitter.on(',').omitEmptyStrings();

    public String search = "";
    public SearchKind searchKind = SearchKind.None;

    private String include = "";
    private String exclude = "";

    private List<String> includeNames;
    private List<String> excludeNames;

    public String getInclude() {
        return include;
    }

    public void setInclude(String include) {
        this.include = include;
        splitInto(include, includeNames);
    }

    public String getExclude() {
        return exclude;
    }

    public void setExclude(String exclude) {
        this.exclude = exclude;
        splitInto(exclude, excludeNames);
    }

    public int getSearchButtonId(){
        if (searchKind == SearchKind.None)
            return R.id.noSearch;
        if (searchKind == SearchData.SearchKind.Name)
            return R.id.searchByName;
        if (searchKind == SearchData.SearchKind.Recent)
            return R.id.searchByRecent;
        return 0;
    }

    public void setSearchButtonId(int buttonId){

        if (buttonId == R.id.noSearch)
            searchKind = SearchKind.None;
        else if (buttonId == R.id.searchByName)
            searchKind = SearchData.SearchKind.Name;
        else if (buttonId == R.id.searchByRecent)
            searchKind = SearchData.SearchKind.Recent;
    }

    private void splitInto(String str, List<String> strings) {
        strings.clear();
        splitter.splitToStream(str).forEachOrdered(strings::add);
    }

    private boolean ingredientsMatches(@Nullable List<RecipeIngredient> ingredients) {
        if (ingredients == null)
            return true;
        if (Strings.isNullOrEmpty(include) &&
                Strings.isNullOrEmpty(exclude))
            return true;

        boolean includeMatches = includeNames.stream().allMatch(s -> {
            return ingredients.stream().anyMatch(ingredient -> {
                return StringUtils.containsIgnoreCase(ingredient.ingredientName, s);
            });
        });

        boolean excludeMatches = excludeNames.stream().allMatch(s -> {
            return  ingredients.stream().noneMatch(ingredient -> {
                return StringUtils.containsIgnoreCase(ingredient.ingredientName, s);
            });
        });

        return includeMatches && excludeMatches;
    }

    private boolean hasFilters(){
        return !Strings.isNullOrEmpty(search) ||
                !Strings.isNullOrEmpty(include) ||
                !Strings.isNullOrEmpty(exclude);
    }

    private boolean hasSort() {
        return searchKind != SearchKind.None;
    }

    private boolean filterIngredients(Ingredient item){
        return StringUtils.containsIgnoreCase(item.name, search);
    }

    public ISearchFilter<Ingredient> getIngredientSearchFilter(){
        return hasFilters() ? this::filterIngredients : null;
    }

    private boolean filterRecipes(Recipe item){
        return StringUtils.containsIgnoreCase(item.name, search) &&
                ingredientsMatches(item.ingredients);
    }

    public ISearchFilter<Recipe> getRecipeSearchFilter(){
        return hasFilters() ? this::filterRecipes : null;
    }

    private int compareIngredients(Ingredient i1, Ingredient i2){
        switch (searchKind){
            case Name:
                return i1.name.compareTo(i2.name);
            case Recent:
                return -i1.lastModified.compareTo(i2.lastModified);
            default:
                return 0;
        }
    }

    public Comparator<Ingredient> geIngredientComparator(){
        return hasSort() ? this::compareIngredients : null;
    }

    private int compareRecipes(Recipe i1, Recipe i2){
        switch (searchKind){
            case Name:
                return i1.name.compareTo(i2.name);
            case Recent:
                return -i1.lastModified.compareTo(i2.lastModified);
            default:
                return 0;
        }
    }

    public Comparator<Recipe> geRecipeComparator(){
        return hasSort() ? this::compareRecipes : null;
    }

    public enum SearchKind {
        None,
        Name,
        Recent
    }
}
