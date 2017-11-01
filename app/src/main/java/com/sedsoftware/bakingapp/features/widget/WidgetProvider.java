package com.sedsoftware.bakingapp.features.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.widget.RemoteViews;
import com.sedsoftware.bakingapp.BakingApp;
import com.sedsoftware.bakingapp.R;
import com.sedsoftware.bakingapp.data.model.Ingredient;
import com.sedsoftware.bakingapp.utils.StringUtils;
import java.util.List;
import javax.inject.Inject;
import timber.log.Timber;

public class WidgetProvider extends AppWidgetProvider {

  @Inject
  WidgetDataHelper widgetDataHelper;

  @Override
  public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
    super.onUpdate(context, appWidgetManager, appWidgetIds);

    Timber.d("onUpdate");

    DaggerWidgetDataHelperComponent.builder()
        .recipeRepositoryComponent(
            ((BakingApp) context.getApplicationContext()).getRecipeRepositoryComponent())
        .build()
        .inject(this);

    for (int appWidgetId : appWidgetIds) {
      String recipeName = widgetDataHelper.getRecipeNameFromPrefs(appWidgetId);

      widgetDataHelper
          .getIngredientsList(recipeName)
          .take(1)
          .subscribe(
              // OnNext
              ingredients ->
                  WidgetProvider
                      .updateAppWidgetContent(context, appWidgetManager, appWidgetId, recipeName,
                          ingredients),
              // OnError
              throwable ->
                  Timber.d("Error: unable to populate widget data."));
    }
  }

  @Override
  public void onDeleted(Context context, int[] appWidgetIds) {
    super.onDeleted(context, appWidgetIds);

    DaggerWidgetDataHelperComponent.builder()
        .recipeRepositoryComponent(
            ((BakingApp) context.getApplicationContext()).getRecipeRepositoryComponent())
        .build()
        .inject(this);

    for (int appWidgetId : appWidgetIds) {
      widgetDataHelper.deleteRecipeFromPrefs(appWidgetId);
    }
  }

  public static void updateAppWidgetContent(Context context, AppWidgetManager appWidgetManager,
      int appWidgetId, String recipeName, List<Ingredient> ingredients) {

    Timber.d("updateAppWidgetContent call...");
    Timber.d("id: " + appWidgetId + ", name: " + recipeName + "ingredients: " + ingredients.size());

    RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_ingredients_list);
    views.setTextViewText(R.id.widget_recipe_name, recipeName);
    views.removeAllViews(R.id.widget_ingredients_container);

    for (Ingredient ingredient : ingredients) {
      RemoteViews ingredientView = new RemoteViews(context.getPackageName(),
          R.layout.widget_ingredients_list_item);

      String line = StringUtils.formatIngdedient(
          context, ingredient.ingredient(), ingredient.quantity(), ingredient.measure());

      ingredientView.setTextViewText(R.id.widget_ingredient_name, line);
      views.addView(R.id.widget_ingredients_container, ingredientView);
    }

    appWidgetManager.updateAppWidget(appWidgetId, views);
  }
}