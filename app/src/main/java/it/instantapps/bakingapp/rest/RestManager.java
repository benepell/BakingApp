package it.instantapps.bakingapp.rest;

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


import java.util.ArrayList;

import it.instantapps.bakingapp.model.Recipe;
import it.instantapps.bakingapp.utility.Costants;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RestManager {

    private static UdacityService sUdacityService;
    private static RestManager sRestManager;
    private Call<ArrayList<Recipe>> mCall;

    private RestManager() {

        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.dispatcher().setMaxRequestsPerHost(1);


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Costants.UDACITY_BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        sUdacityService = retrofit.create(UdacityService.class);


    }

    public static RestManager getInstance() {
        if (sRestManager == null) {
            sRestManager = new RestManager();
        }
        return sRestManager;
    }

    public void getUdacityRecipe(Callback<ArrayList<Recipe>> callback) {
        mCall = sUdacityService.getUdacityService();
        mCall.enqueue(callback);
    }

    public void getUdacityRecipeSync(Callback<ArrayList<Recipe>> callback) {
        Call<ArrayList<Recipe>> call = sUdacityService.getUdacityService();
        call.enqueue(callback);
    }

    public void cancelRequest() {
        if (mCall != null) {
            mCall.cancel();
        }
    }
}