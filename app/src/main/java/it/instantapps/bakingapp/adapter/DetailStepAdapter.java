package it.instantapps.bakingapp.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import butterknife.BindView;
import butterknife.ButterKnife;
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
public class DetailStepAdapter extends RecyclerView.Adapter<DetailStepAdapter.DetailStepHolder> {

    private final String mDescription;
    private final String mShortDescription;
    private final String mThumbnailUrl;
    private final String mVideoUri;


    public DetailStepAdapter(  String description, String shortDescription, String thumbnailUrl, String videoUri) {
        mDescription = description;
        mShortDescription = shortDescription;
        mThumbnailUrl = thumbnailUrl;
        mVideoUri = videoUri;
    }

    @Override
    public DetailStepHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context mContext = parent.getContext();
        int layoutId = R.layout.list_detail_step;
        LayoutInflater inflater = LayoutInflater.from(mContext);

        View view = inflater.inflate(layoutId, parent, false);
        return new DetailStepHolder(view);
    }


    @Override
    public void onBindViewHolder(final DetailStepHolder holder, int position) {

        if (mVideoUri != null && mVideoUri.isEmpty()) {
            holder.mImageViewDetailStep.setBackgroundResource(R.color.black);

            if (mThumbnailUrl != null && !mThumbnailUrl.isEmpty()) {
                final RequestOptions requestOptions;
                requestOptions = new RequestOptions()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .fallback(R.drawable.no_media)
                        .error(R.drawable.no_media)
                        .placeholder(R.drawable.download_in_progress);

                Glide.with(holder.itemView.getContext())
                        .load(mThumbnailUrl)
                        .apply(requestOptions)
                        .into(holder.mImageViewDetailStep);

                holder.mImageViewDetailStep.setVisibility(View.VISIBLE);
            } else {
                holder.mImageViewDetailStep.setBackgroundResource(R.color.colorBackgroundTextList);

                holder.mImageViewDetailStep.setImageResource(R.drawable.no_media);
                holder.mImageViewDetailStep.setVisibility(View.VISIBLE);
            }

        }

        holder.mTextViewShortDetailDescription.setText(mShortDescription);
        holder.mTextViewDetailDescription.setText(mDescription);
    }

    @Override
    public int getItemCount() {
        return 1;
    }

    public class DetailStepHolder extends RecyclerView.ViewHolder {

        @SuppressWarnings("unused")
        @BindView(R.id.tv_short_detail_step_description)
        TextView mTextViewShortDetailDescription;

        @SuppressWarnings("unused")
        @BindView(R.id.tv_detail_step_description)
        TextView mTextViewDetailDescription;

        @SuppressWarnings("unused")
        @BindView(R.id.image_detail_step_thumbnail)
        ImageView mImageViewDetailStep;


        public DetailStepHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
