package it.instantapps.bakingapp.activity;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.media.session.MediaButtonReceiver;
import android.support.v4.media.session.MediaSessionCompat;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.util.Util;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.instantapps.bakingapp.R;
import it.instantapps.bakingapp.data.Contract;
import it.instantapps.bakingapp.fragment.DetailStepFragment;
import it.instantapps.bakingapp.fragment.IngredientFragment;
import it.instantapps.bakingapp.fragment.StepFragment;
import it.instantapps.bakingapp.fragment.TabFragment;
import it.instantapps.bakingapp.media.ExoPlayerManager;
import it.instantapps.bakingapp.utility.Costants;
import it.instantapps.bakingapp.utility.PrefManager;
import it.instantapps.bakingapp.utility.Utility;
import timber.log.Timber;

@SuppressWarnings("unused")
public class StepActivity extends BaseActivity
        implements ExoPlayerManager.BakingExoPlayer, IngredientFragment.FragmentIngredientListener,
        StepFragment.FragmentInteractionListener {

    @SuppressWarnings("CanBeFinal")
    @BindView(R.id.exo_progressBar)
    ProgressBar mProgressBar;

    @SuppressWarnings("CanBeFinal")
    @BindView(R.id.exo__detail_step_fragment_player_view)
    SimpleExoPlayerView mSimpleExoPlayerView;

    @SuppressWarnings("CanBeFinal")
    @BindView(R.id.id_player_layout)
    LinearLayout mContainerLayout;

    @SuppressWarnings("CanBeFinal")
    @BindView(R.id.fragment_detail_step_container)
    FrameLayout mContainerFragment;

    @SuppressWarnings("CanBeFinal")
    @BindView(R.id.tv_error_video)
    TextView mTvErrorVideo;


    private BottomNavigationView mBottomNavigationView;


    @SuppressWarnings("FieldCanBeLocal")
    private SimpleExoPlayer mExoPlayer;

    private static int sIdData;

    private Context mContext;
    private ExoPlayerManager mExoPlayerManager;
    private Cursor mCursor;

    private String mVideoUri;
    private String mShortDescription;
    private String mThumbnailURL;
    private String mDescription;

    private int mNavigationIdMax;
    private int mNavigationId;
    private int mWidget;

    private boolean mIsVideoBackground = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setLayoutResource(R.layout.activity_step);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            setEnableNavigationView(true);
        } else if (isTablet()) {
            setEnableNavigationView(true);
        } else {
            setEnableNavigationView(false);
        }
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(getLayoutResource(), getFrameLayout());

        mContext = StepActivity.this;

        Timber.plant(new Timber.DebugTree());
        ButterKnife.bind(this);


        if ((getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)) {
            mBottomNavigationView = findViewById(R.id.navigation);
        }

        Intent intent = getIntent();

        if (savedInstanceState != null) {
            setRecipeName(savedInstanceState.getString(Costants.BUNDLE_RECIPE_NAME));
            setRecipeId(savedInstanceState.getInt(Costants.BUNDLE_RECIPE_ID));
            setWidget(savedInstanceState.getInt(Costants.BUNDLE_RECIPE_WIDGET));
            setNavigationId(savedInstanceState.getInt(Costants.BUNDLE_DETAIL_STEP_NAVIGATION_ID));
            setVideoUri(savedInstanceState.getString(Costants.BUNDLE_DETAIL_STEP_VIDEOURI));
            setThumbnailURL(savedInstanceState.getString(Costants.BUNDLE_DETAIL_STEP_THUMBNAILURL));
            setShortDescription(savedInstanceState.getString(Costants.BUNDLE_DETAIL_STEP_SHORT_DESCRIPTION));
            setDescription(savedInstanceState.getString(Costants.BUNDLE_DETAIL_STEP_DESCRIPTION));
            sIdData = savedInstanceState.getInt(Costants.BUNDLE_DETAIL_STEP_ID);
        } else if (intent != null) {
            sIdData = intent.getIntExtra(Costants.EXTRA_DETAIL_STEP_ID, -1);
            mWidget = intent.getIntExtra(Costants.EXTRA_RECIPE_WIDGET, -1);
            setRecipeName(intent.getStringExtra(Costants.EXTRA_RECIPE_NAME));
        }

        if (getRecipeName() != null) {
            setTitle(getRecipeName());
            new Utility(StepActivity.this, getSupportActionBar()).setColorOfflineActionBar();
        }

        mIsVideoBackground = PrefManager.isSharedPref(this,
                getString(R.string.pref_video_notification));

        if ((sIdData > 0)) {
            infoDbVideo(sIdData);
            showVideo();
            prefVideoUri();
        } else {
            infoDbVideo(getFirstRow(getRecipeId()));
            showVideo();
            prefVideoUri();
        }


        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            startFragmentTablet(getRecipeId(), Costants.TAB_ORDER_STEP);

            if ((mVideoUri != null) && (!mVideoUri.isEmpty())) {
                mContainerLayout.setVisibility(View.VISIBLE);

                if (isTablet()) {
                    mContainerFragment.setVisibility(View.VISIBLE);
                } else {
                    leanBackUI();
                }

            } else {
                mContainerFragment.setVisibility(View.VISIBLE);
            }

            if (isTablet()) {
                activeNavigation();
            }

        } else {
            activeNavigation();

            if ((mVideoUri != null) && (!mVideoUri.isEmpty())) {
                mContainerLayout.setVisibility(View.VISIBLE);
            }
            mContainerFragment.setVisibility(View.VISIBLE);
        }
        startFragmentDetail(sIdData, mDescription, mShortDescription, mThumbnailURL, mVideoUri);

        if (Timber.treeCount() < Costants.DEFAULT_TIMBER_COUNT) {
            if (!isTablet()) {
                startActivity(new Intent(this, MainActivity.class));
            }
        }

        if (savedInstanceState != null) {
            if (mExoPlayerManager != null) {
                mExoPlayerManager.setResume(
                        savedInstanceState.getInt(Costants.BUNDLE_EXOPLAYER_WINDOW, C.INDEX_UNSET),
                        savedInstanceState.getLong(Costants.BUNDLE_EXOPLAYER_POSITION, C.TIME_UNSET));
                mExoPlayerManager.setAutoPlay(
                        savedInstanceState.getBoolean(Costants.BUNDLE_EXOPLAYER_AUTOPLAY, false));
            }
        } else if (mExoPlayerManager != null) {
            mExoPlayerManager.clearResumePosition();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (Util.SDK_INT > 23) {
            startVideo();
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        if (mExoPlayerManager != null) {
            mExoPlayerManager.updateResumePosition(PrefManager.isSharedPref(this,
                    getString(R.string.pref_resume_video)));
        }
        if (!mIsVideoBackground) {
            destroyVideo();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (Util.SDK_INT <= 23) {
            startVideo();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        clearResources();
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState.getString(Costants.EXTRA_RECIPE_NAME) != null) {
            setRecipeName(savedInstanceState.getString(Costants.EXTRA_RECIPE_NAME));
        }

        setRecipeId(savedInstanceState.getInt(Costants.BUNDLE_RECIPE_ID));
        setRecipeName(savedInstanceState.getString(Costants.BUNDLE_RECIPE_NAME));
        setWidget(savedInstanceState.getInt(Costants.BUNDLE_RECIPE_WIDGET));
        setVideoUri(savedInstanceState.getString(Costants.BUNDLE_DETAIL_STEP_VIDEOURI));
        setThumbnailURL(savedInstanceState.getString(Costants.BUNDLE_DETAIL_STEP_THUMBNAILURL));
        setShortDescription(savedInstanceState.getString(Costants.BUNDLE_DETAIL_STEP_SHORT_DESCRIPTION));
        setDescription(savedInstanceState.getString(Costants.BUNDLE_DETAIL_STEP_DESCRIPTION));

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

        if (mExoPlayerManager != null) {
            outState.putInt(Costants.BUNDLE_EXOPLAYER_WINDOW, mExoPlayerManager.getResumeWindow());
            outState.putLong(Costants.BUNDLE_EXOPLAYER_POSITION, mExoPlayerManager.getResumePosition());
            outState.putBoolean(Costants.BUNDLE_EXOPLAYER_AUTOPLAY, mExoPlayerManager.isAutoPlay());
        }

        outState.putInt(Costants.BUNDLE_RECIPE_ID, getRecipeId());
        outState.putString(Costants.BUNDLE_RECIPE_NAME, getRecipeName());
        outState.putInt(Costants.BUNDLE_RECIPE_WIDGET, getWidget());
        outState.putInt(Costants.BUNDLE_DETAIL_STEP_ID, sIdData);
        outState.putInt(Costants.BUNDLE_DETAIL_STEP_NAVIGATION_ID, mNavigationId);
        outState.putString(Costants.BUNDLE_DETAIL_STEP_VIDEOURI, getVideoUri());
        outState.putString(Costants.BUNDLE_DETAIL_STEP_THUMBNAILURL, getThumbnailURL());
        outState.putString(Costants.BUNDLE_DETAIL_STEP_SHORT_DESCRIPTION, getShortDescription());
        outState.putString(Costants.BUNDLE_DETAIL_STEP_DESCRIPTION, getDescription());
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onExoPlayer(SimpleExoPlayer simpleExoPlayer) {
        mExoPlayer = simpleExoPlayer;
    }

    @Override
    public void onFragmentIngredient(int position) {

    }

    @Override
    public void onFragmentInteraction(int id, int position) {
        setPositionStep(position);
        Intent intent = new Intent(StepActivity.this, NavigationActivity.class);
        intent.putExtra(Costants.EXTRA_NAVIGATION_TYPE, R.string.device_type_tablet);
        intent.putExtra(Costants.EXTRA_DETAIL_STEP_ID, id);
        intent.putExtra(Costants.EXTRA_RECIPE_WIDGET, getWidget());
        intent.putExtra(Costants.EXTRA_RECIPE_NAME, getRecipeName());
        startActivity(intent);
    }


    public static class MediaReceiver extends BroadcastReceiver {

        public MediaReceiver() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            if (mMediaSession != null) {
                MediaButtonReceiver.handleIntent(mMediaSession, intent);
            }
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        closeVideoBackground(false);
        backToDetailActivity();

    }

    private void startFragmentDetail(int idData, String description, String shortDescription, String thumbnailURL, String videoUri) {

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_detail_step_container,
                        DetailStepFragment.newInstance(idData, description, shortDescription, thumbnailURL, videoUri))
                .commit();
    }

    private void infoDbVideo(int idData) {
        boolean isEmpty = false;
        if (getVideoUri() == null && getThumbnailURL() == null && getShortDescription() == null) {
            isEmpty = true;
        }

        if (isEmpty) {
            Uri uri = Contract.StepEntry.CONTENT_URI;

            String[] arrProjection = new String[]{
                    Contract.StepEntry.COLUMN_NAME_RECIPES_ID,
                    Contract.StepEntry.COLUMN_NAME_ID,
                    Contract.StepEntry.COLUMN_NAME_VIDEO_URL,
                    Contract.StepEntry.COLUMN_NAME_THUMBNAIL_URL,
                    Contract.StepEntry.COLUMN_NAME_SHORT_DESCRIPTION,
                    Contract.StepEntry.COLUMN_NAME_DESCRIPTION};

            String selection = Contract.StepEntry._ID + " = ?";

            String[] argSelection = new String[]{String.valueOf(idData)};

            mCursor = getContentResolver().query(uri, arrProjection, selection, argSelection, null);

            try {
                if (mCursor != null) {
                    while (mCursor.moveToNext()) {
                        setRecipeId(mCursor.getInt(mCursor.getColumnIndex(Contract.StepEntry.COLUMN_NAME_RECIPES_ID)));
                        setNavigationId(mCursor.getInt(mCursor.getColumnIndex(Contract.StepEntry.COLUMN_NAME_ID)));
                        setVideoUri(mCursor.getString(mCursor.getColumnIndex(Contract.StepEntry.COLUMN_NAME_VIDEO_URL)));
                        setThumbnailURL(mCursor.getString(mCursor.getColumnIndex(Contract.StepEntry.COLUMN_NAME_THUMBNAIL_URL)));
                        setShortDescription(mCursor.getString(mCursor.getColumnIndex(Contract.StepEntry.COLUMN_NAME_SHORT_DESCRIPTION)));
                        setDescription(mCursor.getString(mCursor.getColumnIndex(Contract.StepEntry.COLUMN_NAME_DESCRIPTION)));

                    }
                }
            } catch (Exception e) {
                Timber.e("Error query: " + new Throwable().getMessage());
            } finally {
                assert mCursor != null;
                mCursor.close();
            }
        }
    }

    public void setNavigationIdMax(int recipeId) {
        Uri uri = Contract.StepEntry.CONTENT_URI;

        String[] arrProjection = new String[]{
                Contract.StepEntry.COLUMN_NAME_ID};

        String selection = Contract.StepEntry.COLUMN_NAME_RECIPES_ID + " = ?";

        String[] argSelection = new String[]{String.valueOf(recipeId)};

        String strOrderBy = Contract.StepEntry._ID + " DESC ";

        mCursor = getContentResolver().query(uri, arrProjection, selection, argSelection, strOrderBy);

        try {
            if (mCursor != null) {
                mCursor.moveToNext();
                mNavigationIdMax = mCursor.getInt(mCursor.getColumnIndex(Contract.StepEntry.COLUMN_NAME_ID));

            }
        } catch (Exception e) {
            Timber.e("Error query: " + new Throwable().getMessage());
        } finally {
            assert mCursor != null;
            mCursor.close();
        }
    }

    private void startVideo() {
        if (mExoPlayerManager != null) {
            mExoPlayerManager.initializePlayer(Uri.parse(mVideoUri), this);

            if (mIsVideoBackground) {
                mExoPlayerManager.initializeMediaSession();
            }
        }
    }

    public void showVideo() {
        if ((mVideoUri != null) && (!mVideoUri.isEmpty())) {
            mExoPlayerManager = new ExoPlayerManager(mContext, mSimpleExoPlayerView,
                    mProgressBar, getShortDescription(), mTvErrorVideo);

        }
    }

    private void destroyVideo() {
        if (mExoPlayerManager != null) {
            mExoPlayerManager.releasePlayer();
        }
    }

    private void destroyVideoBackground() {
        if (mMediaSession != null) {
            mMediaSession.setActive(false);
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            if (notificationManager != null) {
                notificationManager.cancel(Costants.NOTIFICATION_CHANNEL_ID, Costants.NOTIFICATION_ID);
            }
        }
    }

    private void closeVideoBackground(boolean notBackPressed) {
        if (mContext != null) {
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            if (notificationManager != null) {
                notificationManager.cancel(Costants.NOTIFICATION_CHANNEL_ID, Costants.NOTIFICATION_ID);
            }
            if ((notBackPressed) && (getDescription() == null)) {
                startActivity(new Intent(mContext, MainActivity.class));
            }
        }
    }

    private void clearResources() {
        if ((mCursor != null) && (!mCursor.isClosed())) {
            mCursor.close();
        }
        destroyVideo();
        if (mMediaSession != null) {
            destroyVideoBackground();
        }
    }

    private void leanBackUI() {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE);
    }

    private void activeNavigation() {
        if ((getRecipeId() >= 0) && (mBottomNavigationView != null)) {
            setNavigationIdMax(getRecipeId());
            mBottomNavigationView.setVisibility(View.VISIBLE);
            mBottomNavigationView.getMenu().getItem(0).setCheckable(false);
            mBottomNavigationView.getMenu().getItem(2).setCheckable(false);

            if (getWidget() != 0) {
                mBottomNavigationView.getMenu().getItem(1).setCheckable(true);
                mBottomNavigationView.getMenu().getItem(1).setChecked(true);
                mBottomNavigationView.getMenu().getItem(1).setTitle(R.string.title_widget_remove);

            } else {
                mBottomNavigationView.getMenu().getItem(1).setCheckable(false);
                mBottomNavigationView.getMenu().getItem(1).setChecked(false);
                mBottomNavigationView.getMenu().getItem(1).setTitle(R.string.title_widget_add);

            }
            mBottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        }

    }

    private final BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            switch (item.getItemId()) {
                case R.id.navigation_back:
                    if (getNavigationId() > 0) {
                        navigationIntent(R.id.navigation_back);
                    } else {
                        item.setEnabled(false);
                    }
                    return true;

                case R.id.navigation_widget:
                    navigationIntent(R.id.navigation_widget);
                    return true;

                case R.id.navigation_forward:
                    if (getNavigationId() < getNavigationIdMax()) {
                        navigationIntent(R.id.navigation_forward);
                    } else {
                        item.setEnabled(false);
                    }
                    return true;
            }

            return false;
        }
    };

    private void navigationIntent(int navigationType) {
        clearResources();

        Intent intent = new Intent(mContext, NavigationActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        intent.putExtra(Costants.EXTRA_RECIPE_ID, getRecipeId());
        intent.putExtra(Costants.EXTRA_NAVIGATION_TYPE, navigationType);
        intent.putExtra(Costants.EXTRA_RECIPE_NAME, getRecipeName());
        intent.putExtra(Costants.EXTRA_DETAIL_STEP_ID, sIdData);
        intent.putExtra(Costants.EXTRA_RECIPE_WIDGET, getWidget());
        startActivity(intent);
    }

    private void prefVideoUri() {
        SharedPreferences pref = mContext.getSharedPreferences(mContext.getString(R.string.pref_video_uri), 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(mContext.getString(R.string.pref_video_uri), getVideoUri());
        editor.apply();

    }

    public void backToDetailActivity() {
        Class backActivity;
        if (isTablet() && getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            backActivity = MainActivity.class;
        } else {
            backActivity = DetailActivity.class;
        }
        Intent intent = new Intent(mContext, backActivity);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(Costants.EXTRA_RECIPE_NAME, getRecipeName());
        intent.putExtra(Costants.EXTRA_RECIPE_ID, getRecipeId());
        intent.putExtra(Costants.EXTRA_RECIPE_WIDGET, getWidget());
        intent.putExtra(Costants.EXTRA_TAB_ORDERTAB, Costants.TAB_ORDER_STEP);
        startActivity(intent);

    }

    private void visibleFrameTabletLayout() {
            if(PrefManager.isSharedPref(this, getString(R.string.pref_tab_layout))){
                FrameLayout frameTabLayout = findViewById(R.id.content_tablet_tab_fragment);
                frameTabLayout.setVisibility(View.VISIBLE);
            }else {
                FrameLayout frameIngredientLayout = findViewById(R.id.content_tablet_ingredient_fragment);
                frameIngredientLayout.setVisibility(View.VISIBLE);
                FrameLayout frameStepLayout = findViewById(R.id.content_tablet_step_fragment);
                frameStepLayout.setVisibility(View.VISIBLE);
            }
    }


    private void startFragmentTablet(int index, int orderTab) {
        if ((isTablet()) && (index >= 0)) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            Bundle bundle = new Bundle();

            if(PrefManager.isSharedPref(this, getString(R.string.pref_tab_layout))) {
                bundle.putInt(Costants.BUNDLE_TAB_RECIPE_ID, index);
                bundle.putInt(Costants.BUNDLE_TAB_ORDERTAB, orderTab);
                TabFragment fragment = new TabFragment();
                fragment.setArguments(bundle);
                transaction.replace(R.id.content_tablet_tab_fragment, fragment);
            }else {

                IngredientFragment ingredientFragment = new IngredientFragment();
                bundle.putString(Costants.EXTRA_RECIPE_NAME, getRecipeName());
                bundle.putInt(Costants.EXTRA_INGREDIENT_ID, index);
                ingredientFragment.setArguments(bundle);
                transaction.replace(R.id.content_tablet_ingredient_fragment, ingredientFragment);

                StepFragment stepFragment = new StepFragment();
                bundle.putString(Costants.EXTRA_RECIPE_NAME, getRecipeName());
                bundle.putInt(Costants.EXTRA_STEP_ID, index);

                stepFragment.setArguments(bundle);
                transaction.replace(R.id.content_tablet_step_fragment, stepFragment);

            }
            transaction.commit();
            visibleFrameTabletLayout();
        }
    }

    private int getFirstRow(int recipeId) {
        int row = -1;
        Uri uri = Contract.StepEntry.CONTENT_URI;

        String[] arrProjection = new String[]{
                Contract.StepEntry._ID};

        String selection = Contract.StepEntry.COLUMN_NAME_RECIPES_ID + " = ?";

        String[] argSelection = new String[]{String.valueOf(recipeId)};

        String sortOrder = Contract.StepEntry._ID + " ASC LIMIT 1 ";
        mCursor = getContentResolver().query(uri, arrProjection, selection, argSelection, sortOrder);

        try {
            if (mCursor != null) {
                mCursor.moveToNext();
                row = mCursor.getInt(mCursor.getColumnIndex(Contract.StepEntry._ID));
            }
        } catch (Exception e) {
            Timber.e("Error query single row: " + new Throwable().getMessage());
        } finally {
            if (mCursor != null) {
                mCursor.close();
            }
        }

        return row;
    }

    public int getNavigationIdMax() {
        return mNavigationIdMax;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        mDescription = description;
    }

    public String getThumbnailURL() {
        return mThumbnailURL;
    }

    public void setThumbnailURL(String thumbnailURL) {
        mThumbnailURL = thumbnailURL;
    }

    public void setNavigationId(int id) {
        mNavigationId = id;
    }

    public int getNavigationId() {
        return mNavigationId;
    }

    public int getWidget() {
        return mWidget;
    }

    public void setWidget(int widget) {
        mWidget = widget;
    }

    public static int getIdData() {
        return sIdData;
    }

    public static MediaSessionCompat mMediaSession;


    public String getVideoUri() {
        return mVideoUri;
    }

    public void setVideoUri(String videoUri) {
        mVideoUri = videoUri;
    }

    public String getShortDescription() {
        return mShortDescription;
    }

    public void setShortDescription(String shortDescription) {
        mShortDescription = shortDescription;
    }

}