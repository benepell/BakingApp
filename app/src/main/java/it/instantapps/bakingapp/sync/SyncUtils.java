package it.instantapps.bakingapp.sync;

import android.content.Context;
import android.support.annotation.NonNull;

import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.Driver;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.Trigger;

import it.instantapps.bakingapp.utility.Costants;
import it.instantapps.bakingapp.utility.PrefManager;

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
public class SyncUtils {


    private static boolean sInitialized;

    private static void scheduleFirebaseJobDispatcherSync(@NonNull final Context context, int sync_interval) {

        if (sync_interval > 0) {
            int sync_flex = sync_interval / 2;

            Driver driver = new GooglePlayDriver(context);
            FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(driver);
            Job syncBakingJob = dispatcher.newJobBuilder()
                    .setService(FirebaseJobService.class)
                    .setTag(Costants.BAKING_SYNC_TAG)
                    .setConstraints(Constraint.ON_ANY_NETWORK)
                    .setLifetime(Lifetime.FOREVER)
                    .setRecurring(true)
                    .setTrigger(Trigger.executionWindow(
                            sync_interval,
                            sync_interval + sync_flex))
                    .setReplaceCurrent(true)
                    .build();

            dispatcher.schedule(syncBakingJob);
        }
    }

    public static void initialize(Context context) {
        if (sInitialized) return;
        sInitialized = true;
        int interval = PrefManager.getIntSharedPref(context);
        scheduleFirebaseJobDispatcherSync(context, interval);
    }
}