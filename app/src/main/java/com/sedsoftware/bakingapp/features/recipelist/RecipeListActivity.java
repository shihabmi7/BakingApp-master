package com.sedsoftware.bakingapp.features.recipelist;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.test.espresso.IdlingResource;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.sedsoftware.bakingapp.BakingApp;
import com.sedsoftware.bakingapp.R;
import com.sedsoftware.bakingapp.data.idlingresource.RecipesIdlingResource;
import com.sedsoftware.bakingapp.utils.FragmentUtils;

import javax.inject.Inject;

public class RecipeListActivity extends AppCompatActivity {

    @Inject
    RecipeListPresenter recipeListPresenter;

    @Nullable
    private RecipesIdlingResource idlingResource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_list);

        RecipeListFragment recipeListFragment =
                (RecipeListFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentContainer);

        if (recipeListFragment == null) {
            recipeListFragment = RecipeListFragment.newInstance();
            FragmentUtils.addFragmentTo(getSupportFragmentManager(), recipeListFragment,
                    R.id.fragmentContainer);
        }

        DaggerRecipeListComponent.builder()
                .recipeRepositoryComponent(((BakingApp) getApplication()).getRecipeRepositoryComponent())
                .recipeListPresenterModule(new RecipeListPresenterModule(recipeListFragment))
                .build()
                .inject(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_refresh) {
            recipeListPresenter.loadRecipesFromRepo(true, idlingResource);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @VisibleForTesting
    @NonNull
    public IdlingResource getIdlingResource() {
        if (idlingResource == null) {
            idlingResource = new RecipesIdlingResource();
        }
        return idlingResource;
    }
}
