package it.instantapps.bakingapp.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;

import java.lang.ref.WeakReference;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.instantapps.bakingapp.R;
import it.instantapps.bakingapp.activity.BaseActivity;
import it.instantapps.bakingapp.adapter.StepAdapter;
import it.instantapps.bakingapp.data.Contract;
import it.instantapps.bakingapp.utility.Costants;
import it.instantapps.bakingapp.utility.PrefManager;
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
public class StepFragment extends Fragment implements StepAdapter.StepItemClickListener, LoaderManager.LoaderCallbacks<Cursor> {

    @SuppressWarnings({"WeakerAccess", "CanBeFinal", "unused"})
    @BindView(R.id.rv_step)
    RecyclerView mRecyclerView;

    private StepAdapter mAdapter;
    private FragmentInteractionListener mListener;
    private static WeakReference<Integer> sWeakReference;

    public interface FragmentInteractionListener {
        void onFragmentInteraction(int id, int position);
    }

    public static StepFragment newInstance(int index) {
        StepFragment stepFragment = new StepFragment();

        Bundle args = new Bundle();
        args.putInt(Costants.EXTRA_STEP_ID, index);
        stepFragment.setArguments(args);

        return stepFragment;
    }

    private int getShownIndex() {
        return getArguments().getInt(Costants.EXTRA_STEP_ID, 0);
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int mDataId = getShownIndex();

        if (mDataId > 0) {
            sWeakReference = new WeakReference<>(mDataId);
            getActivity().getSupportLoaderManager().initLoader(Costants.STEP_LOADER_ID, null, this);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_step, container, false);

        ButterKnife.bind(this, view);


        if (isTablet()) {

            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {

                GridLayoutManager gridLayoutManager;

                if (getActivity().findViewById(R.id.exo__detail_step_fragment_player_view) == null) {
                    gridLayoutManager = new GridLayoutManager(getActivity(), Integer.valueOf(getString(R.string.tablet_landscape_span_count_grid_step)));

                } else {
                    gridLayoutManager = new GridLayoutManager(getActivity(), 1);

                }
                mRecyclerView.setLayoutManager(gridLayoutManager);

            } else {

                GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(),
                        Integer.valueOf(getString(R.string.tablet_portrait_span_count_grid_step)));
                mRecyclerView.setLayoutManager(gridLayoutManager);
            }


        } else {

            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {

                if (getActivity().findViewById(R.id.content_detail_tab_fragment) != null) {
                    GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(),
                            Integer.valueOf(getString(R.string.tablet_portrait_span_count_grid_step)));
                    mRecyclerView.setLayoutManager(gridLayoutManager);
                } else {
                    LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
                    mRecyclerView.setLayoutManager(layoutManager);
                }

            } else {

                LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
                mRecyclerView.setLayoutManager(layoutManager);

            }
        }

        mRecyclerView.setHasFixedSize(true);

        mAdapter = new StepAdapter(this);

        mRecyclerView.setAdapter(mAdapter);

        if (PrefManager.isSharedPref(getActivity(), getActivity().getString(R.string.pref_tab_layout))) {
            LayoutAnimationController layoutAnimationController = AnimationUtils.loadLayoutAnimation(getActivity(), R.anim.layout_animation_from_right);
            mRecyclerView.setLayoutAnimation(layoutAnimationController);
        }
        return view;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new StepFragmentAsyncTask(getActivity());
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (mAdapter != null) {
            mAdapter.swapCursor(data);
        }

        int position = BaseActivity.getPositionStep();
        if (position == RecyclerView.NO_POSITION) position = 0;
        updatePosition(position);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if (mAdapter != null) {
            mAdapter.swapCursor(null);
        }
    }

    @Override
    public void onStepItemClick(int id, int position) {
        mListener.onFragmentInteraction(id, position);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (FragmentInteractionListener) getActivity();

        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().getLocalClassName() + "must implement OnFragmentStepInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private void updatePosition(int position) {
        if (mAdapter != null) {
            mRecyclerView.smoothScrollToPosition(position);
            mAdapter.notifyDataSetChanged();
        }
    }

    private boolean isTablet() {
        SharedPreferences pref;
        pref = getActivity().getSharedPreferences(getString(R.string.pref_device_tablet), 0);
        return pref.getBoolean(getString(R.string.pref_device_tablet), false);

    }

    private static class StepFragmentAsyncTask extends AsyncTaskLoader<Cursor> {

        Cursor stepData = null;

        final String[] mProjection = new String[]{
                Contract.StepEntry._ID,
                Contract.StepEntry.COLUMN_NAME_ID,
                Contract.StepEntry.COLUMN_NAME_SHORT_DESCRIPTION,
                Contract.StepEntry.COLUMN_NAME_DESCRIPTION,
                Contract.StepEntry.COLUMN_NAME_THUMBNAIL_URL,
                Contract.StepEntry.COLUMN_NAME_VIDEO_URL,

        };

        StepFragmentAsyncTask(Context context) {
            super(context);
        }

        @Override
        protected void onStartLoading() {
            if (stepData != null) {
                deliverResult(stepData);
            } else {
                forceLoad();
            }
        }

        @Override
        public Cursor loadInBackground() {
            try {
                if(sWeakReference!=null){
                    return getContext().getContentResolver().query(Contract.StepEntry.CONTENT_URI,
                            mProjection,
                            Contract.StepEntry.COLUMN_NAME_RECIPES_ID + " = ?",
                            new String[]{String.valueOf(sWeakReference.get())},
                            Contract.StepEntry._ID + " ASC");

                }else {
                    return null;
                }

            } catch (Exception e) {
                Timber.e("Failed to asynchronously load data. ");
                e.printStackTrace();
                return null;
            }
        }

        @Override
        public void deliverResult(Cursor data) {
            if ((data != null) && (data.getCount() > 0)) {
                stepData = data;
                super.deliverResult(data);
            } else {
                forceLoad();
            }
        }
    }
}
