package it.instantapps.bakingapp.activity;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import java.lang.ref.WeakReference;

import it.instantapps.bakingapp.R;
import it.instantapps.bakingapp.data.Contract;
import it.instantapps.bakingapp.utility.Costants;

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
public class NavigationActivity extends AppCompatActivity {

    private static WeakReference<Context> sWeakReference;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sWeakReference = new WeakReference<>(getApplicationContext());
        Intent intent = getIntent();
        if (intent != null) {
            Navigation navigation = new Navigation();
            navigation.setIntendId(intent.getIntExtra(Costants.EXTRA_DETAIL_STEP_ID, -1));
            navigation.setRecipeName(intent.getStringExtra(Costants.EXTRA_RECIPE_NAME));
            navigation.setRecipeId(intent.getIntExtra(Costants.EXTRA_RECIPE_ID, -1));
            navigation.setWidget(intent.getIntExtra(Costants.EXTRA_RECIPE_WIDGET, 0));

            int navigationType = intent.getIntExtra(Costants.EXTRA_NAVIGATION_TYPE, 0);

            switch (navigationType) {

                case R.id.navigation_back:

                    new NavigationBackAsyncTask().execute(navigation);
                    break;

                case R.id.navigation_widget:

                    new NavigationWidgetAsyncTask().execute(navigation);
                    break;

                case R.id.navigation_forward:

                    new NavigationForwardAsyncTask().execute(navigation);
                    break;

                case R.string.device_type_tablet:
                    tabletResult(navigation.getIntendId(), navigation.getRecipeName(), navigation.getWidget());
            }

        }
    }

    private void tabletResult(int id, String recipeName, int widget) {
        Intent send = new Intent(NavigationActivity.this, StepActivity.class);
        send.putExtra(Costants.EXTRA_DETAIL_STEP_ID, id);
        send.putExtra(Costants.EXTRA_RECIPE_WIDGET, widget);
        send.putExtra(Costants.EXTRA_RECIPE_NAME, recipeName);
        send.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(send);
    }

    private static class NavigationBackAsyncTask extends AsyncTask<Navigation, Void, Cursor> {

        @Override
        protected Cursor doInBackground(Navigation... args) {
            Context context = sWeakReference.get();
            final Uri uri = Contract.StepEntry.CONTENT_URI;

            final String[] arrProjection = new String[]{
                    Contract.StepEntry._ID};

            final String selection = Contract.StepEntry.COLUMN_NAME_RECIPES_ID + "  = ?";

            final String[] argSelection = new String[]{String.valueOf(args[0].getRecipeId())};

            Cursor cursor = context.getContentResolver().query(uri, arrProjection, selection, argSelection,
                    Contract.StepEntry._ID + " DESC ");

            if ((cursor != null) && (!cursor.isClosed())) {
                int[] result = {-1};
                while (cursor.moveToNext()) {
                    result[0] = cursor.getInt(cursor.getColumnIndex(Contract.StepEntry._ID));
                    if (result[0] < args[0].getIntendId()) {
                        BaseActivity.setPositionStep(BaseActivity.getPositionStep() - 1);
                        Intent send = new Intent(context, StepActivity.class);
                        send.putExtra(Costants.EXTRA_DETAIL_STEP_ID, result[0]);
                        send.putExtra(Costants.EXTRA_RECIPE_WIDGET, args[0].getWidget());
                        send.putExtra(Costants.EXTRA_RECIPE_NAME, args[0].getRecipeName());
                        send.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        context.startActivity(send);
                        return cursor;
                    }
                }
            }
            return cursor;
        }

        @Override
        protected void onPostExecute(Cursor cursor) {
            super.onPostExecute(cursor);
            if ((cursor != null) && (!cursor.isClosed())) {
                cursor.close();
            }
        }
    }


    private static class NavigationWidgetAsyncTask extends AsyncTask<Navigation, Void, Void> {

        @Override
        protected Void doInBackground(Navigation... args) {

            final Uri uri = Contract.RecipeEntry.CONTENT_URI;

            ContentValues contentValues = new ContentValues(1);
            Context context = sWeakReference.get();

            if (args[0].getWidget() != 0) {
                args[0].setWidget(0);
            } else {
                args[0].setWidget(1);
            }

            contentValues.put(Contract.RecipeEntry.COLUMN_NAME_WIDGET, args[0].getWidget());

            final String selection = Contract.RecipeEntry._ID + "  = ?";

            final String[] argSelection = new String[]{String.valueOf(args[0].getRecipeId())};

            context.getContentResolver().update(uri, contentValues, selection, argSelection);

            Intent send = new Intent(context, StepActivity.class);
            send.putExtra(Costants.EXTRA_DETAIL_STEP_ID, args[0].getIntendId());
            send.putExtra(Costants.EXTRA_RECIPE_WIDGET, args[0].getWidget());
            send.putExtra(Costants.EXTRA_RECIPE_NAME, args[0].getRecipeName());
            send.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            context.startActivity(send);

            return null;
        }
    }

    private static class NavigationForwardAsyncTask extends AsyncTask<Navigation, Void, Cursor> {
        @Override
        protected Cursor doInBackground(Navigation... args) {

            Context context = sWeakReference.get();
            final Uri uri = Contract.StepEntry.CONTENT_URI;

            final String[] arrProjection = new String[]{
                    Contract.StepEntry._ID};

            final String selection = Contract.StepEntry.COLUMN_NAME_RECIPES_ID + "  = ?";

            final String[] argSelection = new String[]{String.valueOf(args[0].getRecipeId())};

            Cursor cursor = context.getContentResolver().query(uri, arrProjection, selection, argSelection,
                    Contract.StepEntry._ID + " ASC ");

            if ((cursor != null) && (!cursor.isClosed())) {
                int[] result = {-1};
                while (cursor.moveToNext()) {
                    result[0] = cursor.getInt(cursor.getColumnIndex(Contract.StepEntry._ID));
                    if (result[0] > args[0].getIntendId()) {
                        BaseActivity.setPositionStep(BaseActivity.getPositionStep() + 1);
                        Intent send = new Intent(context, StepActivity.class);
                        send.putExtra(Costants.EXTRA_DETAIL_STEP_ID, result[0]);
                        send.putExtra(Costants.EXTRA_RECIPE_WIDGET, args[0].getWidget());
                        send.putExtra(Costants.EXTRA_RECIPE_NAME, args[0].getRecipeName());
                        send.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        context.startActivity(send);

                        cursor.close();
                        return cursor;
                    }
                }
            }

            return cursor;
        }

        @Override
        protected void onPostExecute(Cursor cursor) {
            super.onPostExecute(cursor);
            if ((cursor != null) && (!cursor.isClosed())) {
                cursor.close();
            }
        }

    }

    private class Navigation {

        private int intendId;
        private int recipeId;
        private String recipeName;
        private int widget;

        int getIntendId() {
            return intendId;
        }

        void setIntendId(int intendId) {
            this.intendId = intendId;
        }

        int getRecipeId() {
            return recipeId;
        }

        void setRecipeId(int recipeId) {
            this.recipeId = recipeId;
        }

        String getRecipeName() {
            return recipeName;
        }

        void setRecipeName(String recipeName) {
            this.recipeName = recipeName;
        }

        int getWidget() {
            return widget;
        }

        void setWidget(int widget) {
            this.widget = widget;
        }
    }
}
