package it.instantapps.bakingapp.utility;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.ActionBar;
import android.text.TextPaint;

import java.util.Locale;

import it.instantapps.bakingapp.R;

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
public class Utility {
    private final Context mContext;
    private final ActionBar mActionBar;

    public Utility(Context context, ActionBar actionBar) {
        mContext = context;
        mActionBar = actionBar;
    }

    public void setColorOfflineActionBar() {
        if (! NetworkState.isOnline(mContext)) {
            if (mActionBar != null) {
                mActionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor(Costants.COLOR_BACKGROUND_ACTIONBAR_OFFLINE)));
            }
        }
    }

    public String appVersionName() throws PackageManager.NameNotFoundException {
        return mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0).versionName;
    }

    public static boolean isPermissionExtStorage(Context context) {
        SharedPreferences pref = context.getSharedPreferences(context.getString(R.string.pref_write_external_storage), 0);
        return pref.getBoolean(context.getString(R.string.pref_write_external_storage), false);
    }

    public static void RequestPermissionExtStorage(Activity thisActivity) {
        if (ContextCompat.checkSelfPermission(thisActivity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(thisActivity,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                ActivityCompat.requestPermissions(thisActivity,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        Costants.PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
            }

        }
    }

    public static void isDeniedPermissionExtStorage(Activity thisActivity) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(thisActivity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            thisActivity.getSharedPreferences(thisActivity.getString(R.string.pref_write_external_storage), 0).edit().clear().apply();

        }
    }
    public static Bitmap bitmapTitleImage(Context context, String string) {


        if ((context == null) || (string == null)) return null;

        string = string.toUpperCase(Locale.ROOT);

        Typeface typeface = ResourcesCompat.getFont(context, R.font.indie_flower);
        int fontSizePx = (int) (Costants.BITMAT_FONT_SIZE_DP * context.getResources().getDisplayMetrics().scaledDensity);

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
        if ((bitmap.getHeight() > 0)) {
            myCanvas.drawText(string, 0, bitmap.getHeight(), paint);
            return bitmap;
        }

        return null;
    }

}