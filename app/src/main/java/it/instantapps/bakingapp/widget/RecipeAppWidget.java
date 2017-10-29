package it.instantapps.bakingapp.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.support.v4.content.res.ResourcesCompat;
import android.text.TextPaint;
import android.view.View;
import android.widget.RemoteViews;

import java.util.Objects;

import it.instantapps.bakingapp.R;
import it.instantapps.bakingapp.activity.MainActivity;
import it.instantapps.bakingapp.sync.RecipeWidgetService;
import it.instantapps.bakingapp.utility.Costants;
import it.instantapps.bakingapp.utility.PrefManager;

import static it.instantapps.bakingapp.utility.Costants.RECIPE_WIDGET_UPDATE;

public class RecipeAppWidget extends AppWidgetProvider {



    private void updateAppWidget(Context context) {
        handleActionUpdateRecipeWidget(context);
    }

    private void handleActionUpdateRecipeWidget(Context context) {

        String widgetRecipeName = PrefManager.getStringPref(context,R.string.pref_widget_name);

        int id = PrefManager.getIntPref(context);

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.recipe_app_widget);

        if (id > 0) {
            views.setViewVisibility(R.id.widget_text_error, View.GONE);
            views.setViewVisibility(R.id.widget_listView, View.VISIBLE);
            views.setViewVisibility(R.id.recipe_widget_name, View.VISIBLE);
            views.setImageViewBitmap(R.id.recipe_widget_name,
                    bitmapTitleImage(context, widgetRecipeName));

            Intent intent = new Intent(context, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NO_HISTORY);
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

    private static Bitmap bitmapTitleImage(Context context, String string) {

        Typeface typeface = ResourcesCompat.getFont(context, R.font.permanent_marker);
        int fontSizePx = (int) (30 * context.getResources().getDisplayMetrics().scaledDensity);

        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setSubpixelText(true);
        paint.setTypeface(typeface);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setColor(context.getResources().getColor(R.color.white));
        paint.setTextSize(fontSizePx);
        paint.setTextAlign(Paint.Align.LEFT);
        Paint.FontMetrics fontMetrics = paint.getFontMetrics();

        int textHeight = (int) (fontMetrics.descent - fontMetrics.ascent + fontMetrics.leading);

        TextPaint textPaint = new TextPaint(paint);
        Bitmap bitmap = Bitmap.createBitmap((int) textPaint.measureText(string),
                textHeight, Bitmap.Config.ARGB_8888);
        Canvas myCanvas = new Canvas(bitmap);
        myCanvas.drawText(string, 0, bitmap.getHeight(), paint);
        return bitmap;
    }

}

