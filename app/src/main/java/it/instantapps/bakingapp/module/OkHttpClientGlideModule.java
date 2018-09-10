
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

package it.instantapps.bakingapp.module;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;


import com.bumptech.glide.Glide;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.module.AppGlideModule;

import java.io.File;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import it.instantapps.bakingapp.utility.Costants;
import it.instantapps.bakingapp.utility.Utility;
import okhttp3.Cache;
import okhttp3.OkHttpClient;

@SuppressWarnings({"WeakerAccess", "unused"})
@GlideModule
public class OkHttpClientGlideModule extends AppGlideModule {

    @Override
    public void registerComponents(@NonNull Context context, @NonNull Glide glide, @NonNull Registry registry) {

        File file;
        long fileCacheMax;
        if (Build.VERSION.SDK_INT < 23) {
            file = new File(Environment.getExternalStoragePublicDirectory(context.getPackageName()) + Costants.PATH_SEPARATOR +
                    context.getCacheDir().getName() + Costants.PATH_SEPARATOR + Costants.CACHE_VIDEO_DIR);
            fileCacheMax = Costants.EXT_CACHE_SIZE_MAX;

        } else if (Utility.isPermissionExtStorage(context) && Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {

            file = new File(Environment.getExternalStoragePublicDirectory(context.getPackageName()) + Costants.PATH_SEPARATOR +
                    context.getCacheDir().getName() + Costants.PATH_SEPARATOR + Costants.CACHE_VIDEO_DIR);
            fileCacheMax = Costants.EXT_CACHE_SIZE_MAX;
        } else {
            file = new File(context.getCacheDir().toString() + Costants.PATH_SEPARATOR + Costants.CACHE_VIDEO_DIR);
            fileCacheMax = Costants.CACHE_SIZE_MAX;

        }

        Cache cache = new Cache(file, fileCacheMax);

        OkHttpClient client = new OkHttpClient.Builder()
                .cache(cache)
                .readTimeout(15, TimeUnit.SECONDS)
                .connectTimeout(15, TimeUnit.SECONDS)
                .build();

        OkHttpUrlLoader.Factory factory = new OkHttpUrlLoader.Factory(client);

        glide.getRegistry().replace(GlideUrl.class, InputStream.class, factory);
    }
}