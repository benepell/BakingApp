package it.instantapps.bakingapp.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.instantapps.bakingapp.R;
import it.instantapps.bakingapp.data.DataUtils;
import it.instantapps.bakingapp.fragment.RecipeFragment;
import it.instantapps.bakingapp.model.Recipe;
import it.instantapps.bakingapp.rest.RestExecute;
import it.instantapps.bakingapp.sync.SyncUtils;
import it.instantapps.bakingapp.utility.Costants;
import it.instantapps.bakingapp.utility.NetworkState;
import it.instantapps.bakingapp.utility.Utility;
import timber.log.Timber;

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
public class MainActivity extends BaseActivity implements
        RestExecute.RestData, RecipeFragment.FragmentInteractionListener, ActivityCompat.OnRequestPermissionsResultCallback {

    @SuppressWarnings({"WeakerAccess", "CanBeFinal", "unused"})
    @BindView(R.id.progress_bar_main)
    ProgressBar mProgressBar;

    @SuppressWarnings({"WeakerAccess", "CanBeFinal", "unused"})
    @BindView(R.id.error_text)
    TextView mErrorText;

    private Context mContext;

    private String mRecipeName;

    private int mId;
    private int mWidget;

    private boolean mStateProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setLayoutResource(R.layout.activity_main);
        setEnableNavigationView(true);
        super.onCreate(savedInstanceState);

        getLayoutInflater().inflate(getLayoutResource(), getFrameLayout());

        mContext = MainActivity.this;
        ButterKnife.bind(this);
        Timber.plant(new Timber.DebugTree());
        SyncUtils.initialize(this);

        Utility.RequestPermissionExtStorage(MainActivity.this);
        initializeMainJob();
        clearPosition();

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mId = savedInstanceState.getInt(Costants.EXTRA_RECIPE_ID);
        mWidget = savedInstanceState.getInt(Costants.EXTRA_RECIPE_WIDGET);
        mRecipeName = savedInstanceState.getString(Costants.EXTRA_RECIPE_NAME);
        mStateProgressBar = savedInstanceState.getBoolean(Costants.EXTRA_PROGRESSBAR_MAIN);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case Costants.PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    SharedPreferences pref = mContext.getSharedPreferences(mContext.getString(R.string.pref_write_external_storage), 0);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putBoolean(mContext.getString(R.string.pref_write_external_storage), true);
                    editor.apply();
                }
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(Costants.EXTRA_RECIPE_ID, mId);
        outState.putInt(Costants.EXTRA_RECIPE_WIDGET, mWidget);
        outState.putString(Costants.EXTRA_RECIPE_NAME, mRecipeName);
        outState.putBoolean(Costants.EXTRA_PROGRESSBAR_MAIN, mStateProgressBar);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onFragmentInteraction(int id, String recipeName, int widget) {
        Intent intent;
        if (isTablet() && getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            intent = new Intent(mContext, StepActivity.class);
            setRecipeId(id);
        } else {
            intent = new Intent(mContext, DetailActivity.class);
            intent.putExtra(Costants.EXTRA_RECIPE_ID, id);

        }
        intent.putExtra(Costants.EXTRA_RECIPE_WIDGET, widget);
        intent.putExtra(Costants.EXTRA_RECIPE_NAME, recipeName);
        startActivity(intent);
    }

    @Override
    public void onRestData(ArrayList<Recipe> listenerData) {
        if (listenerData != null) {
            hiddenProgressBar();
            new DataUtils(mContext).saveDB(listenerData);
        }

        try {
            startFragmentDb();
        } catch (IllegalStateException e) {
            Timber.e("on rest data: " + e.getMessage());
        }
    }

    public void restartNetwork(View view){
        if(view!=null){
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               initializeMainJob();
            }
        });
        }
    }

    private void initializeMainJob() {
        SharedPreferences pref = getSharedPreferences(getString(R.string.pref_insert_data), 0);
        boolean isPrefData = false;
        if(pref!=null){
            isPrefData = pref.getBoolean(getString(R.string.pref_insert_data), false);
        }
        if (!isPrefData) {
            if (new NetworkState(mContext).isOnline()) {
                showProgressBar();
                new RestExecute().loadData(this);
            } else {
                shownErrorNetwork();
            }
        } else {
            new Utility(mContext, getSupportActionBar()).setColorOfflineActionBar();

            startFragmentDb();
        }
    }

    private void startFragmentDb() {
        mErrorText.setVisibility(View.INVISIBLE);
        mStateProgressBar = false;

        RecipeFragment recipeFragment = new RecipeFragment();
        Bundle bundle = new Bundle();
        recipeFragment.setArguments(bundle);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_list_container, recipeFragment).commit();

    }

    private void shownErrorNetwork() {
        mErrorText.setVisibility(View.VISIBLE);
        mErrorText.setText(getString(R.string.network_state_not_connected));
    }

    private void showProgressBar() {
        mProgressBar.setVisibility(View.VISIBLE);
        mStateProgressBar = true;
    }

    private void hiddenProgressBar() {
        mProgressBar.setVisibility(View.INVISIBLE);
        mStateProgressBar = false;
    }
}
