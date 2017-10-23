package it.instantapps.bakingapp.utility;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

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

public class NetworkState {

    private final Context mContext;

    public NetworkState(Context context) {
        mContext = context;
    }

    public boolean isOnline() {
        ConnectivityManager connMgr = (ConnectivityManager)
                mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connMgr!=null){
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
            return (networkInfo != null) && (networkInfo.getState() == NetworkInfo.State.CONNECTED);
        }
        return false;
    }
}
