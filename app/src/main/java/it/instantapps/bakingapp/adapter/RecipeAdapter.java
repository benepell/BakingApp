package it.instantapps.bakingapp.adapter;

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

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.Nullable;
import android.support.v4.provider.FontRequest;
import android.support.v4.provider.FontsContractCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.instantapps.bakingapp.R;
import it.instantapps.bakingapp.data.Contract;
import it.instantapps.bakingapp.utility.Costants;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeHolder> {

    private final ListItemClickListener mOnClickListener;
    private Context mContext;
    private Cursor mCursor;
    private Handler mHandler = null;

    public RecipeAdapter(ListItemClickListener listener) {
        mOnClickListener = listener;
    }


    @Override
    public RecipeHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        mContext = parent.getContext();
        int layoutId = R.layout.list_recipe;
        LayoutInflater inflater = LayoutInflater.from(mContext);

        View view = inflater.inflate(layoutId, parent, false);

        return new RecipeHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecipeHolder holder, final int position) {
        mCursor.moveToPosition(position);

        String imageRecipe = mCursor.getString(mCursor.getColumnIndex(Contract.RecipeEntry.COLUMN_NAME_IMAGE));
        String nameRecipe = mCursor.getString(mCursor.getColumnIndex(Contract.RecipeEntry.COLUMN_NAME_NAME));
        int servingsRecipe = mCursor.getInt(mCursor.getColumnIndex(Contract.RecipeEntry.COLUMN_NAME_SERVINGS));

        int index = mCursor.getInt(mCursor.getColumnIndex(Contract.RecipeEntry._ID));

        final RequestOptions requestOptions;
        requestOptions = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .error(R.drawable.no_media)
                .fallback(R.drawable.no_media)
                .fitCenter()
                .placeholder(R.drawable.download_in_progress);

        Glide.with(holder.itemView.getContext())
                .asBitmap()
                .load(imageRecipe)
                .apply(requestOptions)
                .into(new SimpleTarget<Bitmap>(Costants.GLIDE_IMAGE_WIDTH_STEP, Costants.GLIDE_IMAGE_HEIGHT_STEP) {
                    @Override
                    public void onLoadFailed(@Nullable Drawable errorDrawable) {
                        super.onLoadFailed(errorDrawable);
                        holder.mTextViewRecipeName.setBackgroundResource(R.drawable.no_media);
                    }

                    @Override
                    public void onLoadStarted(@Nullable Drawable placeholder) {
                        super.onLoadStarted(placeholder);
                        holder.mTextViewRecipeName.setBackgroundResource(R.drawable.download_in_progress);
                    }

                    @Override
                    public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                        Drawable drawable = new BitmapDrawable(mContext.getResources(), resource);
                        holder.mTextViewRecipeName.setBackground(drawable);
                    }
                });


        FontRequest request = new FontRequest(
                "com.google.android.gms.fonts",
                "com.google.android.gms", "Permanent Marker",
                R.array.com_google_android_gms_fonts_certs);

        FontsContractCompat.FontRequestCallback callback = new FontsContractCompat
                .FontRequestCallback() {
            @Override
            public void onTypefaceRetrieved(Typeface typeface) {
                holder.mTextViewRecipeName.setTypeface(typeface);
            }

            @Override
            public void onTypefaceRequestFailed(int reason) {
                Toast.makeText(mContext,
                         R.string.text_font_failed, Toast.LENGTH_LONG)
                        .show();
            }
        };

        FontsContractCompat
                .requestFont(mContext, request, callback,
                       getHandlerThreadHandler());

        holder.mTextViewRecipeName.setText(nameRecipe);

        holder.mTextViewRecipeServings.setText(String.valueOf(servingsRecipe));

        int widget = mCursor.getInt(mCursor.getColumnIndex(Contract.RecipeEntry.COLUMN_NAME_WIDGET));

        holder.bind(index, nameRecipe, widget);
    }

    private Handler getHandlerThreadHandler() {
        if (mHandler == null) {
            HandlerThread handlerThread = new HandlerThread("fonts");
            handlerThread.start();
            mHandler = new Handler(handlerThread.getLooper());
        }
        return mHandler;
    }

    @Override
    public int getItemCount() {
        return (mCursor == null) ? 0 : mCursor.getCount();
    }

    public class RecipeHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @SuppressWarnings("unused")
        @BindView(R.id.tv_recipe_name)
        TextView mTextViewRecipeName;

        @SuppressWarnings("unused")
        @BindView(R.id.tv_recipe_servings)
        TextView mTextViewRecipeServings;

        private String mRecipeName;

        private int mIdData;
        private int mWidget;

        RecipeHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);

            itemView.setOnClickListener(this);
        }

        void bind(int idData, String recipeName, int widget) {
            mIdData = idData;
            mRecipeName = recipeName;
            mWidget = widget;

        }

        @Override
        public void onClick(View view) {
            mOnClickListener.onListItemClick(mIdData, mRecipeName, mWidget);
        }

    }

    @SuppressWarnings("UnusedReturnValue")
    public Cursor swapCursor(Cursor c) {
        if (mCursor == c) {
            return null;
        }
        Cursor temp = mCursor;
        this.mCursor = c;

        if (c != null) {
            this.notifyDataSetChanged();
        }
        return temp;
    }

    public interface ListItemClickListener {

        void onListItemClick(int clickItemIndex, String recipeName, int mWidget);
    }
}
