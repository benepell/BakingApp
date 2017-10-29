package it.instantapps.bakingapp.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.RemoteViews;

import java.util.Objects;

import it.instantapps.bakingapp.R;
import it.instantapps.bakingapp.activity.MainActivity;
import it.instantapps.bakingapp.sync.RecipeWidgetService;
import it.instantapps.bakingapp.utility.Costants;
import it.instantapps.bakingapp.utility.PrefManager;
import timber.log.Timber;

import static it.instantapps.bakingapp.utility.Costants.RECIPE_WIDGET_UPDATE;
import static it.instantapps.bakingapp.utility.Utility.bitmapTitleImage;

public class RecipeAppWidget extends AppWidgetProvider {

    private void updateAppWidget(Context context) {
        handleActionUpdateRecipeWidget(context);
    }

    private void handleActionUpdateRecipeWidget(Context context) {

        String widgetRecipeName = PrefManager.getStringPref(context, R.string.pref_widget_name);

        int id = PrefManager.getIntPref(context, R.string.pref_widget_id);

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.recipe_app_widget);

        if ((id > 0) && (!widgetRecipeName.isEmpty())) {
            views.setViewVisibility(R.id.widget_text_error, View.GONE);
            views.setViewVisibility(R.id.widget_listView, View.VISIBLE);
            views.setViewVisibility(R.id.recipe_widget_name, View.VISIBLE);
            Bitmap bitmap = bitmapTitleImage(context.getApplicationContext(),widgetRecipeName);
            if (bitmap != null) {
                views.setImageViewBitmap(R.id.recipe_widget_name, bitmap);

            }else {
                views.setViewVisibility(R.id.recipe_widget_name,View.INVISIBLE);
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
            views.setViewVisibility(R.id.recipe_widget_name, View.GONE);

            views.setViewVisibility(R.id.widget_text_error, View.VISIBLE);
            views.setTextViewText(R.id.widget_text_error, context.getString(R.string.widget_text_error));

        }
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context, RecipeAppWidget.class));
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, views.getLayoutId());
        appWidgetManager.updateAppWidget(appWidgetIds, views);

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        String action = intent.getAction();
        if (Objects.equals(action, RECIPE_WIDGET_UPDATE)) {
            handleActionUpdateRecipeWidget(context);
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, appWidgetId);
            updateAppWidget(context);
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    @Override
    public void onEnabled(Context context) {
    }

    @Override
    public void onDisabled(Context context) {
    }

    public static void widgetUpdate(Context context) {
        try {
            Intent intent = new Intent(context, RecipeAppWidget.class);
            intent.setAction(RECIPE_WIDGET_UPDATE);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context.getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            pendingIntent.send();
        } catch (PendingIntent.CanceledException e) {
            Timber.e("pending" + e.getMessage());
        }
    }
}

