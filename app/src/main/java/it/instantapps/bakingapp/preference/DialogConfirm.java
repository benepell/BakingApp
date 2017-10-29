package it.instantapps.bakingapp.preference;

import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.preference.DialogPreference;
import android.util.AttributeSet;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.lang.ref.WeakReference;

import it.instantapps.bakingapp.R;
import it.instantapps.bakingapp.activity.BaseActivity;
import it.instantapps.bakingapp.data.DataUtils;
import it.instantapps.bakingapp.media.CacheDataSourceFactory;
import it.instantapps.bakingapp.utility.PrefManager;

import static it.instantapps.bakingapp.widget.RecipeAppWidget.widgetUpdate;


public class DialogConfirm extends DialogPreference {

    private static WeakReference<Context> sWeakReference;

    public DialogConfirm(Context context, AttributeSet attrs) {
        super(context, attrs);
        sWeakReference = new WeakReference<>(context);
    }

    @Override
    protected void onClick() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(getContext(), R.style.confirmDialog);
        dialog.setTitle(R.string.title_dialog_confirm);
        dialog.setMessage(R.string.text_clear_data);
        dialog.setCancelable(true);
        dialog.setPositiveButton(R.string.text_positive_dialog_confirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                new ResetAsyncTask().execute();

            }
        });

        dialog.setNegativeButton(R.string.text_dialog_confirm_no_reset, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dlg, int which) {
                dlg.cancel();
            }
        });

        AlertDialog al = dialog.create();
        al.show();
    }

    private static class ResetAsyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            Context context = sWeakReference.get();
            if (context != null) {
                CacheDataSourceFactory.getClearData(context);
                new DataUtils(context).ClearDataPrivacy();
                PrefManager.clearSharedPref(context);
                PrefManager.clearPref(context);
                BaseActivity.clearRecipeId();
                Glide.get(context).clearDiskCache();
                widgetUpdate(context);
            }

            return null;
        }


        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (sWeakReference.get() != null) {
                Toast.makeText(sWeakReference.get(), R.string.text_dialog_confirm_reset, Toast.LENGTH_SHORT).show();
            }
        }
    }

} 
