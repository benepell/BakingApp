/*
 *  ____        _    _                  _
 * | __ )  __ _| | _(_)_ __   __ _     / \   _ __  _ __
 * |  _ \ / _` | |/ / | '_ \ / _` |   / _ \ | '_ \| '_ \
 * | |_) | (_| |   <| | | | | (_| |  / ___ \| |_) | |_) |
 * |____/ \__,_|_|\_\_|_| |_|\__, | /_/   \_\ .__/| .__/
 *                           |___/          |_|   |_|
 *
 * Copyright (C) 2017 Benedetto Pellerito
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package it.instantapps.bakingapp.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.instantapps.bakingapp.R;
import it.instantapps.bakingapp.fragment.IngredientFragment;
import it.instantapps.bakingapp.fragment.StepFragment;
import it.instantapps.bakingapp.fragment.TabFragment;
import it.instantapps.bakingapp.utility.Costants;
import it.instantapps.bakingapp.utility.PrefManager;
import it.instantapps.bakingapp.utility.Utility;
import timber.log.Timber;


@SuppressWarnings("ALL")
public class DetailActivity extends BaseActivity
        implements StepFragment.FragmentInteractionListener, IngredientFragment.FragmentIngredientListener {

    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.content_detail_tab_fragment)
    FrameLayout mFrameTabLayout;

    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.content_detail_ingredient_fragment)
    FrameLayout mFrameIngredientLayout;

    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.content_detail_step_fragment)
    FrameLayout mFrameStepLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setLayoutResource(R.layout.activity_detail);
        setEnableNavigationView(true);

        super.onCreate(savedInstanceState);

        getLayoutInflater().inflate(getLayoutResource(), getFrameLayout(), false);

        Timber.plant(new Timber.DebugTree());

        ButterKnife.bind(this);

        Intent intent = getIntent();
        new Utility(DetailActivity.this, getSupportActionBar()).setColorOfflineActionBar();

        if (savedInstanceState != null) {
            setRecipeId(savedInstanceState.getInt(Costants.BUNDLE_RECIPE_ID, 0));
            setRecipeName(savedInstanceState.getString(Costants.BUNDLE_RECIPE_NAME));

        } else {
            if (intent != null) {
                setRecipeId(intent.getIntExtra(Costants.EXTRA_RECIPE_ID, 0));
                setRecipeName(intent.getStringExtra(Costants.EXTRA_RECIPE_NAME));
            }

            int orderTab = (intent != null) ? intent.getIntExtra(Costants.EXTRA_TAB_ORDERTAB, Costants.TAB_ORDER_INGREDIENT) : -1;
            startFragment(getRecipeId(), orderTab);
        }
        setTitle(getRecipeName());
        visibleFrameLayout();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem menuItemListShopping;
        menuItemListShopping = menu.getItem(0);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            menuItemListShopping.setVisible(true);
        } else if (PrefManager.isGeneralSettings(this, getString(R.string.pref_tab_layout))) {
            menuItemListShopping.setVisible(true);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.navigation_list_shopping:
                new ShoppingListAsyncTask().execute(getRecipeId());
                return true;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        MainActivity.homeActivity(this);
    }

    @Override
    public void onFragmentInteraction(int id, int position) {
        setPositionStep(position);
        Intent intent = new Intent(DetailActivity.this, StepActivity.class);
        intent.putExtra(Costants.EXTRA_DETAIL_STEP_ID, id);
        intent.putExtra(Costants.EXTRA_RECIPE_NAME, BaseActivity.getRecipeName());
        startActivity(intent);
    }


    @Override
    public void onFragmentIngredient(int position) {
        setPositionIngredient(position);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(Costants.BUNDLE_RECIPE_ID, getRecipeId());
        outState.putString(Costants.BUNDLE_RECIPE_NAME, getRecipeName());
        super.onSaveInstanceState(outState);
    }

    private void startFragment(int index, int orderTab) {
        if (index >= 0) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            Bundle bundle = new Bundle();

            if (PrefManager.isGeneralSettings(this, getString(R.string.pref_tab_layout))) {
                bundle.putInt(Costants.BUNDLE_TAB_RECIPE_ID, index);
                bundle.putInt(Costants.BUNDLE_TAB_ORDERTAB, orderTab);
                TabFragment fragment = new TabFragment();
                fragment.setArguments(bundle);
                transaction.replace(R.id.content_detail_tab_fragment, fragment);
            } else {
                IngredientFragment ingredientFragment = new IngredientFragment();
                bundle.putInt(Costants.EXTRA_INGREDIENT_ID, index);
                ingredientFragment.setArguments(bundle);
                transaction.replace(R.id.content_detail_ingredient_fragment, ingredientFragment);

                StepFragment stepFragment = new StepFragment();

                stepFragment.setArguments(bundle);
                bundle.putInt(Costants.EXTRA_STEP_ID, index);
                transaction.replace(R.id.content_detail_step_fragment, stepFragment);
            }

            transaction.commit();
        }
    }

    private void visibleFrameLayout() {
        if (PrefManager.isGeneralSettings(this, getString(R.string.pref_tab_layout))) {
            mFrameTabLayout.setVisibility(View.VISIBLE);
        } else {
            if (!Utility.isTablet(getApplicationContext()) && getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                mFrameIngredientLayout.setVisibility(View.VISIBLE);
            }
            if (Utility.isTablet(getApplicationContext()) && getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                mFrameIngredientLayout.setVisibility(View.VISIBLE);
            }

            mFrameStepLayout.setVisibility(View.VISIBLE);
        }
    }


}