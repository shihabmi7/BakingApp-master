package com.sedsoftware.bakingapp.utils;

import android.content.ContentValues;
import android.database.Cursor;
import android.support.annotation.NonNull;
import com.sedsoftware.bakingapp.data.model.Ingredient;
import com.sedsoftware.bakingapp.data.model.Recipe;
import com.sedsoftware.bakingapp.data.model.Step;
import com.sedsoftware.bakingapp.data.source.local.db.IngredientPersistenceContract.IngredientEntry;
import com.sedsoftware.bakingapp.data.source.local.db.RecipePersistenceContract.RecipeEntry;
import com.sedsoftware.bakingapp.data.source.local.db.StepPersistenceContract.StepEntry;
import java.util.ArrayList;
import java.util.List;

public class DbUtils {

  public static String getSelectAllQuery(String tableName) {
    return "SELECT * FROM " + tableName;
  }

  public static String getSelectByIdQuery(String tableName, String column) {
    return "SELECT * FROM " + tableName + " WHERE " + column + " = ?";
  }

  public static ContentValues ingredientToContentValues(@NonNull Ingredient ingredient,
      int recipeId) {
    ContentValues cv = new ContentValues();

    cv.put(IngredientEntry.COLUMN_RECIPE_ID, recipeId);
    cv.put(IngredientEntry.COLUMN_QUANTITY, ingredient.quantity());
    cv.put(IngredientEntry.COLUMN_MEASURE, ingredient.measure());
    cv.put(IngredientEntry.COLUMN_INGREDIENT, ingredient.ingredient());

    return cv;
  }

  public static ContentValues stepToContentValues(@NonNull Step step, int recipeId) {
    ContentValues cv = new ContentValues();

    cv.put(StepEntry.COLUMN_RECIPE_ID, recipeId);
    cv.put(StepEntry.COLUMN_STEP_ID, step.id());
    cv.put(StepEntry.COLUMN_SHORT_DESC, step.shortDescription());
    cv.put(StepEntry.COLUMN_DESC, step.description());
    cv.put(StepEntry.COLUMN_VIDEO_URL, step.videoURL());
    cv.put(StepEntry.COLUMN_THUMB_URL, step.thumbnailURL());

    return cv;
  }

  public static ContentValues recipeToContentValues(@NonNull Recipe recipe) {
    ContentValues cv = new ContentValues();

    cv.put(RecipeEntry.COLUMN_RECIPE_ID, recipe.id());
    cv.put(RecipeEntry.COLUMN_NAME, recipe.name());
    cv.put(RecipeEntry.COLUMN_SERVINGS, recipe.servings());
    cv.put(RecipeEntry.COLUMN_IMAGE, recipe.image());

    return cv;
  }

  public static List<Ingredient> ingredientsFromCursor(@NonNull Cursor cursor) {

    List<Ingredient> ingredientsList = new ArrayList<>();

    // Cursor moved to -1 position so we won't miss the first
    // element because of moveToNext() call
    if (cursor.getCount() > 0) {
      cursor.moveToPosition(-1);

      while (cursor.moveToNext()) {

        float quantity = cursor.getFloat(cursor.getColumnIndexOrThrow(IngredientEntry.COLUMN_QUANTITY));
        String measure = cursor.getString(cursor.getColumnIndexOrThrow(IngredientEntry.COLUMN_MEASURE));
        String ingredient = cursor.getString(cursor.getColumnIndexOrThrow(IngredientEntry.COLUMN_INGREDIENT));

        Ingredient result = Ingredient.builder()
            .quantity(quantity)
            .measure(measure)
            .ingredient(ingredient)
            .build();

        ingredientsList.add(result);
      }
    }

    return ingredientsList;
  }

  public static List<Step> stepsFromCursor(@NonNull Cursor cursor) {

    List<Step> stepsList = new ArrayList<>();

    if (cursor.getCount() > 0) {
      cursor.moveToPosition(-1);

      while (cursor.moveToNext()) {

        int stepId = cursor.getInt(cursor.getColumnIndexOrThrow(StepEntry.COLUMN_STEP_ID));
        String sDesc = cursor.getString(cursor.getColumnIndexOrThrow(StepEntry.COLUMN_SHORT_DESC));
        String desc = cursor.getString(cursor.getColumnIndexOrThrow(StepEntry.COLUMN_DESC));
        String video = cursor.getString(cursor.getColumnIndexOrThrow(StepEntry.COLUMN_VIDEO_URL));
        String thumb = cursor.getString(cursor.getColumnIndexOrThrow(StepEntry.COLUMN_THUMB_URL));

        Step step = Step.builder()
            .id(stepId)
            .shortDescription(sDesc)
            .description(desc)
            .videoURL(video)
            .thumbnailURL(thumb)
            .build();

        stepsList.add(step);
      }
    }

    return stepsList;
  }

  public static List<Recipe> recipesFromCursor(@NonNull Cursor cursor) {

    List<Recipe> recipeList = new ArrayList<>();

    if (cursor.getCount() > 0) {
      cursor.moveToPosition(-1);

      while (cursor.moveToNext()) {

        int id = cursor.getInt(cursor.getColumnIndexOrThrow(RecipeEntry.COLUMN_RECIPE_ID));
        String name = cursor.getString(cursor.getColumnIndexOrThrow(RecipeEntry.COLUMN_NAME));
        int servings = cursor.getInt(cursor.getColumnIndexOrThrow(RecipeEntry.COLUMN_SERVINGS));
        String image = cursor.getString(cursor.getColumnIndexOrThrow(RecipeEntry.COLUMN_IMAGE));

        Recipe recipe = Recipe.builder()
            .id(id)
            .name(name)
            .ingredients(new ArrayList<>())
            .steps(new ArrayList<>())
            .servings(servings)
            .image(image)
            .build();

        recipeList.add(recipe);
      }
    }

    return recipeList;
  }
}
