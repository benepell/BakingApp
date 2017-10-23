package it.instantapps.bakingapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

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
class DbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "baking.db";

    private final Context mContext;

    private static final int DATABASE_VERSION = 1;

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        final String SQL_CREATE_RECIPE_TABLE =
                "CREATE TABLE IF NOT EXISTS " + Contract.RecipeEntry.TABLE_NAME + " (" +
                        Contract.RecipeEntry._ID + " INTEGER PRIMARY KEY, " +
                        Contract.RecipeEntry.COLUMN_NAME_NAME + " TEXT NOT NULL, " +
                        Contract.RecipeEntry.COLUMN_NAME_SERVINGS + " REAL NOT NULL, " +
                        Contract.RecipeEntry.COLUMN_NAME_IMAGE + " TEXT, " +
                        Contract.RecipeEntry.COLUMN_NAME_WIDGET + " INTEGER DEFAULT 0 " +
                        ");";

        final String SQL_CREATE_INGREDIENT_TABLE =
                "CREATE TABLE IF NOT EXISTS " + Contract.IngredientEntry.TABLE_NAME + " (" +

                        BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +

                        Contract.IngredientEntry.COLUMN_NAME_RECIPES_ID + " INTEGER NOT NULL, " +

                        Contract.IngredientEntry.COLUMN_NAME_QUANTITY + " REAL NOT NULL, " +
                        Contract.IngredientEntry.COLUMN_NAME_MEASURE + " TEXT NOT NULL, " +
                        Contract.IngredientEntry.COLUMN_NAME_INGREDIENT + " TEXT NOT NULL, " +

                        " FOREIGN KEY (" + Contract.IngredientEntry.COLUMN_NAME_RECIPES_ID + ") REFERENCES " +
                        Contract.RecipeEntry.TABLE_NAME + "(" + Contract.RecipeEntry._ID + ")," +

                        " UNIQUE (" + Contract.IngredientEntry.COLUMN_NAME_RECIPES_ID + "," + Contract.IngredientEntry.COLUMN_NAME_INGREDIENT + ")" + " ON CONFLICT REPLACE " +
                        ");";

        final String SQL_CREATE_STEP_TABLE =
                "CREATE TABLE IF NOT EXISTS " + Contract.StepEntry.TABLE_NAME + " (" +
                        BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +

                        Contract.StepEntry.COLUMN_NAME_ID + " INTEGER NOT NULL, " +
                        Contract.StepEntry.COLUMN_NAME_RECIPES_ID + " INTEGER NOT NULL, " +
                        Contract.StepEntry.COLUMN_NAME_SHORT_DESCRIPTION + " TEXT NOT NULL, " +
                        Contract.StepEntry.COLUMN_NAME_DESCRIPTION + " TEXT NOT NULL, " +
                        Contract.StepEntry.COLUMN_NAME_VIDEO_URL + " TEXT NOT NULL, " +
                        Contract.StepEntry.COLUMN_NAME_THUMBNAIL_URL + " TEXT NOT NULL, " +

                        " FOREIGN KEY (" + Contract.StepEntry.COLUMN_NAME_RECIPES_ID + ") REFERENCES " +
                        Contract.RecipeEntry.TABLE_NAME + "(" + Contract.RecipeEntry._ID + "), " +

                        " UNIQUE (" + Contract.StepEntry.COLUMN_NAME_RECIPES_ID + "," + Contract.StepEntry.COLUMN_NAME_DESCRIPTION + ")" + " ON CONFLICT REPLACE " +
                        ");";


        db.execSQL(SQL_CREATE_RECIPE_TABLE);
        db.execSQL(SQL_CREATE_INGREDIENT_TABLE);
        db.execSQL(SQL_CREATE_STEP_TABLE);

        Timber.d("SQL STATEMENT:  " + SQL_CREATE_RECIPE_TABLE + " " + SQL_CREATE_INGREDIENT_TABLE + " " + SQL_CREATE_STEP_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + Contract.RecipeEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + Contract.IngredientEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + Contract.StepEntry.TABLE_NAME);
        onCreate(db);
        new DataUtils(mContext).clearPreferenceDb();
    }


}
