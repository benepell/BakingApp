package it.instantapps.bakingapp.service;



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

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

import it.instantapps.bakingapp.rest.RestExecute;

public class FirebaseJobService extends JobService {

    private RestExecute restExecute;

    @Override
    public boolean onStartJob(JobParameters job) {
        restExecute = new RestExecute();
        restExecute.syncData(getApplicationContext());
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters job) {
        if (restExecute != null) {
            restExecute.cancelRequest();
        }
        return true;
    }
}
