package it.instantapps.bakingapp.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DecimalFormat;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.instantapps.bakingapp.R;
import it.instantapps.bakingapp.activity.BaseActivity;
import it.instantapps.bakingapp.data.Contract;
import timber.log.Timber;

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
public class IngredientAdapter extends RecyclerView.Adapter<IngredientAdapter.DetailIngredientHolder> {

    private final IngredientItemClickListener mOnIngredientItemClickListener;
    private Cursor mCursor;

    public IngredientAdapter(IngredientItemClickListener ingredientItemClickListener) {
        mOnIngredientItemClickListener = ingredientItemClickListener;
    }

    public interface IngredientItemClickListener {
        void onIngredientItemClick(int position, int itemCount);
    }

    @Override
    public DetailIngredientHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context mContext = parent.getContext();
        int layoutId = R.layout.list_ingredient;
        LayoutInflater inflater = LayoutInflater.from(mContext);

        View view = inflater.inflate(layoutId, parent, false);

        return new DetailIngredientHolder(view);

    }

    @Override
    public void onBindViewHolder(IngredientAdapter.DetailIngredientHolder holder, int position) {

        if (!mCursor.moveToPosition(position)) {
            throw new IllegalStateException("couldn't move cursor to position " + position);
        }

        mCursor.moveToPosition(position);

        String ingredientName = mCursor.getString(mCursor.getColumnIndex(Contract.IngredientEntry.COLUMN_NAME_INGREDIENT));
        float quantityName = mCursor.getFloat(mCursor.getColumnIndex(Contract.IngredientEntry.COLUMN_NAME_QUANTITY));
        String measureName = mCursor.getString(mCursor.getColumnIndex(Contract.IngredientEntry.COLUMN_NAME_MEASURE));

        Timber.d("position: " + position + " - " + BaseActivity.getPositionIngredient());

        StringBuilder stringBuilder = new StringBuilder(ingredientName.trim().toLowerCase());
        stringBuilder.setCharAt(0,Character.toUpperCase(ingredientName.charAt(0)));
        holder.mTextViewIngredientName.setText(stringBuilder);

        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        holder.mTextViewQuantityName.setText(String.valueOf(decimalFormat.format(quantityName)));
        holder.mTextViewMeasureName.setText(measureName);

    }

    @Override
    public int getItemCount() {
        return (mCursor == null) ? 0 : mCursor.getCount();
    }

    public class DetailIngredientHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @SuppressWarnings("unused")
        @BindView(R.id.tv_ingredient_name)
        TextView mTextViewIngredientName;

        @SuppressWarnings("unused")
        @BindView(R.id.tv_quantity_name)
        TextView mTextViewQuantityName;

        @SuppressWarnings("unused")
        @BindView(R.id.tv_measure_name)
        TextView mTextViewMeasureName;

        public DetailIngredientHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            mOnIngredientItemClickListener.onIngredientItemClick(getAdapterPosition(), getItemCount());
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
}
