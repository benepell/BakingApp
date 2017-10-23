package it.instantapps.bakingapp.utility;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.preference.PreferenceManager;

import it.instantapps.bakingapp.R;

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
public class PrefManager {

    public static boolean isSharedPref(Context context, String key) {
        PreferenceManager.setDefaultValues(context, R.xml.pref_general_settings, false);

        SharedPreferences sharedPref =
                PreferenceManager.getDefaultSharedPreferences(context);
        return   sharedPref.getBoolean
                (key, false);
    }

    public static int getIntSharedPref(Context context) {
        PreferenceManager.setDefaultValues(context, R.xml.pref_general_settings, false);

        SharedPreferences sharedPref =
                PreferenceManager.getDefaultSharedPreferences(context);
        return  Integer.valueOf(
                sharedPref.getString(context.getString(R.string.pref_sync_frequency), "0"));
    }

    public static void clearSharedPref(Context context){
        PreferenceManager.setDefaultValues(context, R.xml.pref_general_settings, false);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPreferences.edit().clear().apply();

    }

}

