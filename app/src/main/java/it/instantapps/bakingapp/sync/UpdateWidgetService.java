package it.instantapps.bakingapp.sync;

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
            Timber.e("pending" + e.getMessage());
        }
    }

}
