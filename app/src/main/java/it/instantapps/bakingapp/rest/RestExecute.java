package it.instantapps.bakingapp.rest;

import android.content.Context;
import android.support.annotation.NonNull;

import java.util.ArrayList;

import it.instantapps.bakingapp.data.DataUtils;
import it.instantapps.bakingapp.model.Recipe;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
public class RestExecute {

    private final RestManager restManager;
    private ArrayList<Recipe> mRecipeArrayList;

    public RestExecute() {
        mRecipeArrayList = new ArrayList<>();
        restManager = RestManager.getInstance();
    }

    public void loadData(final RestData myCallBack) {
        Callback<ArrayList<Recipe>> callback = new Callback<ArrayList<Recipe>>() {
            @Override
            public void onResponse(@NonNull Call<ArrayList<Recipe>> call, @NonNull Response<ArrayList<Recipe>> response) {

                mRecipeArrayList = response.body();

                if (response.isSuccessful()) {
                    myCallBack.onRestData(mRecipeArrayList);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ArrayList<Recipe>> call, @NonNull Throwable t) {
                call.cancel();
                if (call.isCanceled()) {
                    myCallBack.onErrorData(t.getMessage());
                }
            }
        };
        restManager.getUdacityRecipe(callback);
    }

    public void syncData(final Context context) {
        Callback<ArrayList<Recipe>> callback = new Callback<ArrayList<Recipe>>() {
            @Override
            public void onResponse(@NonNull Call<ArrayList<Recipe>> call, @NonNull Response<ArrayList<Recipe>> response) {

                mRecipeArrayList = response.body();

                if (response.isSuccessful()) {
                    DataUtils dataUtils = new DataUtils(context);
                    dataUtils.saveDB(mRecipeArrayList);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ArrayList<Recipe>> call, @NonNull Throwable t) {
                call.cancel();
            }
        };
        restManager.getUdacityRecipeSync(callback);

    }

    public void cancelRequest() {
        if (restManager != null) {
            restManager.cancelRequest();
        }
    }

    public interface RestData {
        void onRestData(ArrayList<Recipe> listenerData);
        void onErrorData(String error);
    }
}
