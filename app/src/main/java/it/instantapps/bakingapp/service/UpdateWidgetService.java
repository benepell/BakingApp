
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

package it.instantapps.bakingapp.service;

import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;

import it.instantapps.bakingapp.widget.RecipeAppWidget;
import timber.log.Timber;

import static it.instantapps.bakingapp.utility.Costants.RECIPE_WIDGET_UPDATE;

public class UpdateWidgetService extends IntentService {
    public UpdateWidgetService() {
        super("UpdateWidgetService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        widgetUpdate(getApplicationContext());
    }

    public static void startWidgetService(Context context) {
        Intent intent = new Intent(context, UpdateWidgetService.class);
        context.startService(intent);
    }

    private static void widgetUpdate(Context context) {
        try {
            Intent intent = new Intent(context, RecipeAppWidget.class);
            intent.setAction(RECIPE_WIDGET_UPDATE);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context.getApplicationContext(), 0,
                    intent, PendingIntent.FLAG_UPDATE_CURRENT);
            pendingIntent.send();
        } catch (PendingIntent.CanceledException e) {
            Timber.e("pending%s", e.getMessage());
        }
    }

}
