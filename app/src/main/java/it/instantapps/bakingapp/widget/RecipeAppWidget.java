
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

package it.instantapps.bakingapp.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.RemoteViews;

import java.util.Objects;

import it.instantapps.bakingapp.R;
import it.instantapps.bakingapp.activity.MainActivity;
import it.instantapps.bakingapp.service.RecipeWidgetService;
import it.instantapps.bakingapp.utility.Costants;
import it.instantapps.bakingapp.utility.PrefManager;

import static it.instantapps.bakingapp.utility.Costants.RECIPE_WIDGET_UPDATE;

public class RecipeAppWidget extends AppWidgetProvider {

    private RemoteViews viewsUpdateRecipeWidget(Context context) {

        String widgetRecipeName = PrefManager.getStringPref(context, R.string.pref_widget_name);

        if(TextUtils.isEmpty(widgetRecipeName)) widgetRecipeName = context.getString(R.string.app_name);

        int id = PrefManager.getIntPref(context, R.string.pref_widget_id);
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.recipe_app_widget);

        if (id > 0) {

            views.setViewVisibility(R.id.widget_text_error, View.GONE);
            views.setViewVisibility(R.id.widget_listView, View.VISIBLE);

            if (!TextUtils.isEmpty(widgetRecipeName)) {
                views.setTextViewText(R.id.recipe_widget_name, widgetRecipeName);

            }


            Intent intent = new Intent(context, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY);
            intent.putExtra(Costants.EXTRA_RECIPE_WIDGET_ID, id);
            intent.putExtra(Costants.EXTRA_RECIPE_NAME, widgetRecipeName);
            PendingIntent pendingIntent = PendingIntent.getActivities(context, id, new Intent[]{intent}, 0);
            views.setOnClickPendingIntent(R.id.recipe_widget_name, pendingIntent);

            Intent serviceIntent = new Intent(context, RecipeWidgetService.class);
            views.setRemoteAdapter(R.id.widget_listView, serviceIntent);

        } else {
            views.setViewVisibility(R.id.widget_listView, View.GONE);
            views.setViewVisibility(R.id.widget_text_error, View.VISIBLE);
            views.setTextViewText(R.id.widget_text_error, context.getString(R.string.widget_text_error));

            if (TextUtils.isEmpty(widgetRecipeName)) {
                views.setTextViewText(R.id.recipe_widget_name, widgetRecipeName);

            }

            Intent intent = new Intent(context, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivities(context, 0, new Intent[]{intent}, 0);
            views.setOnClickPendingIntent(R.id.widget_text_error,pendingIntent);
        }
        return views;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context, RecipeAppWidget.class));

        String action = intent.getAction();
        if (Objects.equals(action, RECIPE_WIDGET_UPDATE)) {
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_listView);
            for(int appWidgetId:appWidgetIds){
               appWidgetManager.partiallyUpdateAppWidget(appWidgetId, viewsUpdateRecipeWidget(context));
           }
        }
        super.onReceive(context, intent);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        appWidgetManager.updateAppWidget(appWidgetIds, viewsUpdateRecipeWidget(context));
    }

    @Override
    public void onEnabled(Context context) {
    }

    @Override
    public void onDisabled(Context context) {
    }


}

