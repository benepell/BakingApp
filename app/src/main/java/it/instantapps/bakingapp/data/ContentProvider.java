package it.instantapps.bakingapp.data;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

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
public class ContentProvider extends android.content.ContentProvider {

    private static final int RECIPES = 100;
    private static final int RECIPE_WITH_ID = 101;

    private static final int INGREDIENTS = 200;
    private static final int INGREDIENT_WITH_ID = 201;

    private static final int STEPS = 300;
    private static final int STEP_WITH_ID = 301;


    private static final UriMatcher sUriMatMATCHER = buildURIMatcher();

    private static UriMatcher buildURIMatcher() {

        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        uriMatcher.addURI(Contract.CONTENT_AUTHORITY, Contract.PATH_RECIPES, RECIPES);
        uriMatcher.addURI(Contract.CONTENT_AUTHORITY, Contract.PATH_RECIPES + "/#", RECIPE_WITH_ID);

        uriMatcher.addURI(Contract.CONTENT_AUTHORITY, Contract.PATH_INGREDIENTS, INGREDIENTS);
        uriMatcher.addURI(Contract.CONTENT_AUTHORITY, Contract.PATH_INGREDIENTS + "/#", INGREDIENT_WITH_ID);

        uriMatcher.addURI(Contract.CONTENT_AUTHORITY, Contract.PATH_STEPS, STEPS);
        uriMatcher.addURI(Contract.CONTENT_AUTHORITY, Contract.PATH_STEPS + "/#", STEP_WITH_ID);


        return uriMatcher;
    }


    private DbHelper mDbHelper;

    @Override
    public boolean onCreate() {
        Context context = getContext();
        mDbHelper = new DbHelper(context);
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        String mSelection;
        String[] mSelectionArgs;

        final SQLiteDatabase db = mDbHelper.getReadableDatabase();

        int match = sUriMatMATCHER.match(uri);

        Cursor returnCursor;

        switch (match) {

            case RECIPES:

                returnCursor = db.query(Contract.RecipeEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);

                break;

            case RECIPE_WITH_ID:

                String id = uri.getPathSegments().get(1);

                mSelection = " = ? ";
                mSelectionArgs = new String[]{id};

                returnCursor = db.query(Contract.RecipeEntry.TABLE_NAME,
                        projection,
                        mSelection,
                        mSelectionArgs,
                        null,
                        null,
                        sortOrder);
                break;

            case INGREDIENTS:

                returnCursor = db.query(Contract.IngredientEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);

                break;

            case INGREDIENT_WITH_ID:

                id = uri.getPathSegments().get(1);

                mSelection = "_id=?";
                mSelectionArgs = new String[]{id};

                returnCursor = db.query(Contract.IngredientEntry.TABLE_NAME,
                        projection,
                        mSelection,
                        mSelectionArgs,
                        null,
                        null,
                        sortOrder);

                break;

            case STEPS:
                returnCursor = db.query(Contract.StepEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);

                break;

            case STEP_WITH_ID:

                id = uri.getPathSegments().get(1);

                mSelection = "_id=?";
                mSelectionArgs = new String[]{id};

                returnCursor = db.query(Contract.StepEntry.TABLE_NAME,
                        projection,
                        mSelection,
                        mSelectionArgs,
                        null,
                        null,
                        sortOrder);

                break;
            default:
                throw new UnsupportedOperationException("Uri not found: " + uri);
        }

        if (getContext() != null) {

            returnCursor.setNotificationUri(getContext().getContentResolver(), uri);
        }

        return returnCursor;

    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {

        int match = sUriMatMATCHER.match(uri);

        switch (match) {

            case RECIPES:
                return Contract.RecipeEntry.CONTENT_TYPE;

            case RECIPE_WITH_ID:
                return Contract.RecipeEntry.CONTENT_ITEM_TYPE;

            case INGREDIENTS:
                return Contract.IngredientEntry.CONTENT_TYPE;

            case INGREDIENT_WITH_ID:
                return Contract.IngredientEntry.CONTENT_ITEM_TYPE;

            case STEPS:
                return Contract.StepEntry.CONTENT_TYPE;

            case STEP_WITH_ID:
                return Contract.StepEntry.CONTENT_ITEM_TYPE;

            default:
                throw new UnsupportedOperationException("Uri not found: " + uri);
        }

    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {

        long id;
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();

        int match = sUriMatMATCHER.match(uri);

        Uri returnUri;

        switch (match) {

            case RECIPES:

                // insert values into recipes table
                id = db.insert(Contract.RecipeEntry.TABLE_NAME, null, values);
                if (id > 0) {
                    returnUri = ContentUris.withAppendedId(Contract.RecipeEntry.CONTENT_URI, id);
                } else {
                    throw new android.database.SQLException("Failed to insert row .." + uri);
                }
                break;

            case INGREDIENTS:
                // insert values into table
                id = db.insert(Contract.IngredientEntry.TABLE_NAME, null, values);
                if (id > 0) {
                    returnUri = ContentUris.withAppendedId(Contract.IngredientEntry.CONTENT_URI, id);
                } else {
                    throw new android.database.SQLException("Failed to insert row .." + uri);
                }
                break;

            case STEPS:

                // insert values into table
                id = db.insert(Contract.StepEntry.TABLE_NAME, null, values);
                if (id > 0) {
                    returnUri = ContentUris.withAppendedId(Contract.StepEntry.CONTENT_URI, id);
                } else {
                    throw new android.database.SQLException("Failed to insert row .." + uri);
                }

                break;

            default:
                throw new UnsupportedOperationException("Uri not found: " + uri);
        }
        if (getContext() != null) {

            getContext().getContentResolver().notifyChange(uri, null);
        }

        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {

        int id;
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();

        int match = sUriMatMATCHER.match(uri);

        int recordDelete;

        switch (match) {

            case RECIPES:
                recordDelete = db.delete(Contract.RecipeEntry.TABLE_NAME,
                        selection,
                        selectionArgs);
                break;

            case RECIPE_WITH_ID:
                id = Integer.parseInt(uri.getPathSegments().get(1));

                recordDelete = db.delete(Contract.RecipeEntry.TABLE_NAME,
                        "_id=?",
                        new String[]{String.valueOf(id)});
                break;


            case INGREDIENTS:
                recordDelete = db.delete(Contract.IngredientEntry.TABLE_NAME,
                        selection,
                        selectionArgs);
                break;

            case INGREDIENT_WITH_ID:

                id = Integer.parseInt(uri.getPathSegments().get(1));

                recordDelete = db.delete(Contract.IngredientEntry.TABLE_NAME,
                        "_id=?",
                        new String[]{String.valueOf(id)});
                break;

            case STEPS:
                recordDelete = db.delete(Contract.StepEntry.TABLE_NAME,
                        selection,
                        selectionArgs);
                break;

            case STEP_WITH_ID:

                id = Integer.parseInt(uri.getPathSegments().get(1));

                recordDelete = db.delete(Contract.StepEntry.TABLE_NAME,
                        "_id=?",
                        new String[]{String.valueOf(id)});
                break;

            default:
                throw new UnsupportedOperationException("Uri not found: " + uri);
        }

        if ((getContext() != null) && (recordDelete != 0)) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return recordDelete;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {

        int id;
        int rowsUpdate;

        final SQLiteDatabase db = mDbHelper.getWritableDatabase();

        int match = sUriMatMATCHER.match(uri);


        switch (match) {

            case RECIPES:
                rowsUpdate = db.update(Contract.RecipeEntry.TABLE_NAME,
                        values,
                        selection,
                        selectionArgs);
                break;

            case RECIPE_WITH_ID:
                id = Integer.parseInt(uri.getPathSegments().get(1));

                rowsUpdate = db.update(Contract.RecipeEntry.TABLE_NAME,
                        values,
                        "_id=?",
                        new String[]{String.valueOf(id)});
                break;

            case INGREDIENTS:
                rowsUpdate = db.update(Contract.IngredientEntry.TABLE_NAME,
                        values,
                        selection,
                        selectionArgs);
                break;

            case INGREDIENT_WITH_ID:

                id = Integer.parseInt(uri.getPathSegments().get(1));

                rowsUpdate = db.update(Contract.IngredientEntry.TABLE_NAME,
                        values,
                        "_id=?",
                        new String[]{String.valueOf(id)});
                break;

            case STEPS:
                rowsUpdate = db.update(Contract.StepEntry.TABLE_NAME,
                        values,
                        selection,
                        selectionArgs);
                break;

            case STEP_WITH_ID:

                id = Integer.parseInt(uri.getPathSegments().get(1));

                rowsUpdate = db.update(Contract.StepEntry.TABLE_NAME,
                        values,
                        "_id=?",
                        new String[]{String.valueOf(id)});
                break;

            default:
                throw new UnsupportedOperationException("Uri not found: " + uri);
        }

        if (rowsUpdate != 0) {
            if (getContext() != null) {

                getContext().getContentResolver().notifyChange(uri, null);
            }
        }

        return rowsUpdate;
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {

        final SQLiteDatabase db = mDbHelper.getWritableDatabase();

        int match = sUriMatMATCHER.match(uri);


        int rowsInserted;
        switch (match) {

            case RECIPES:
                db.beginTransaction();
                rowsInserted = 0;

                try {
                    // insert all data
                    for (ContentValues value : values) {
                        if (value == null) {
                            throw new IllegalArgumentException("Cannot have null content values");
                        }

                        long _id = db.insertOrThrow(Contract.RecipeEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            rowsInserted++;
                        }
                    }
                    db.setTransactionSuccessful();

                } catch (SQLiteException e) {
                    Timber.v("Attempting to insert " + e.getMessage());
                } finally {
                    // execute after ..... when is complete
                    db.endTransaction();
                }
                if ((getContext() != null) && (rowsInserted > 0)) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }

                return rowsInserted;

            case INGREDIENTS:
                db.beginTransaction();
                rowsInserted = 0;

                try {
                    // insert all data
                    for (ContentValues value : values) {

                        if (value == null) {
                            throw new IllegalArgumentException("Cannot have null content values");
                        }


                        long _id = db.insertOrThrow(Contract.IngredientEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            rowsInserted++;
                        }
                    }
                    db.setTransactionSuccessful();

                } catch (SQLiteException e) {
                    Timber.v("Attempting to insert " + e.getMessage());
                } finally {
                    // execute after ..... when is complete
                    db.endTransaction();
                }
                if ((getContext() != null) && (rowsInserted > 0)) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }

                return rowsInserted;

            case STEPS:
                db.beginTransaction();
                rowsInserted = 0;

                try {
                    for (ContentValues value : values) {

                        if (value == null) {
                            throw new IllegalArgumentException("Cannot have null content values");
                        }

                        long _id = db.insertOrThrow(Contract.StepEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            rowsInserted++;
                        }
                    }
                    db.setTransactionSuccessful();

                } catch (SQLiteException e) {
                    Timber.v("Attempting to insert " + e.getMessage());
                } finally {
                    db.endTransaction();
                }
                if ((getContext() != null) && (rowsInserted > 0)) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }

                return rowsInserted;

            default:
                return super.bulkInsert(uri, values);
        }

    }
}
