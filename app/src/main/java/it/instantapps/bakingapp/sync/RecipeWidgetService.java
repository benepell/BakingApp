package it.instantapps.bakingapp.sync;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Binder;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.text.DecimalFormat;

import it.instantapps.bakingapp.R;
import it.instantapps.bakingapp.data.Contract;
import it.instantapps.bakingapp.utility.PrefManager;
import timber.log.Timber;

public class RecipeWidgetService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new RecipeRemoteViewsFactory(this.getApplicationContext());
    }

    class RecipeRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {
        final Context mContext;
        Cursor mCursor;


        RecipeRemoteViewsFactory(Context applicationContext) {
            mContext = applicationContext.getApplicationContext();
        }


        @Override
        public void onCreate() {

        }

        @Override
        public void onDataSetChanged() {
            int recipeId = PrefManager.getIntPref(mContext, R.string.pref_widget_id);

            if (mCursor != null) mCursor.close();

            final long identityToken = Binder.clearCallingIdentity();

            mCursor = mContext.getContentResolver().query(Contract.IngredientEntry.CONTENT_URI,
                    null,
                    Contract.IngredientEntry.COLUMN_NAME_RECIPES_ID + " = ?",
                    new String[]{String.valueOf(recipeId)},
                    null);

            Binder.restoreCallingIdentity(identityToken);
        }

        @Override
        public void onDestroy() {
            if (mCursor != null) mCursor.close();
        }

        @Override
        public int getCount() {
            if (mCursor == null) return 0;
            return mCursor.getCount();
        }

        @Override
        public RemoteViews getViewAt(int position) {

            if (mCursor == null || mCursor.getCount() == 0) return null;

            RemoteViews views = new RemoteViews(mContext.getPackageName(),
                    R.layout.recipe_list_widget);

            mCursor.moveToPosition(position);

            String ingredientName = mCursor.getString(mCursor.getColumnIndex(Contract.IngredientEntry.COLUMN_NAME_INGREDIENT));
            float quantityName = mCursor.getFloat(mCursor.getColumnIndex(Contract.IngredientEntry.COLUMN_NAME_QUANTITY));
            String measureName = mCursor.getString(mCursor.getColumnIndex(Contract.IngredientEntry.COLUMN_NAME_MEASURE));

            views.setTextViewText(R.id.recipe_widget_ingredient_name, ingredientName);

            DecimalFormat decimalFormat = new DecimalFormat("#.##");
            views.setTextViewText(R.id.recipe_widget_ingredient_quantity, String.valueOf(decimalFormat.format(quantityName)));

            measureName = "(" + measureName + ")";
            views.setTextViewText(R.id.recipe_widget_ingredient_measure, measureName);


            return views;
        }

        @Override
        public RemoteViews getLoadingView() {
            return null;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }


    }

}
