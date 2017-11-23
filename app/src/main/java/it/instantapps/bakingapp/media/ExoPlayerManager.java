
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

package it.instantapps.bakingapp.media;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.support.v4.media.session.MediaButtonReceiver;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;

import it.instantapps.bakingapp.R;
import it.instantapps.bakingapp.activity.StepActivity;
import it.instantapps.bakingapp.utility.Costants;
import it.instantapps.bakingapp.utility.PrefManager;

import static android.content.Context.NOTIFICATION_SERVICE;
import static it.instantapps.bakingapp.activity.StepActivity.mMediaSession;

public class ExoPlayerManager implements Player.EventListener {

    private PlaybackStateCompat.Builder mStateBuilder;
    private final SimpleExoPlayerView mSimpleExoPlayerView;
    private final Context mContext;
    private final TextView mTvErrorVideo;
    private SimpleExoPlayer mExoPlayer;
    private final ProgressBar mProgressBar;
    private final Handler mHandler;

    private final String mShortDescription;

    private int mResumeWindow;
    private int mProgressStatus = 0;
    private long mResumePosition;

    private boolean isAutoPlay;


    public ExoPlayerManager(Context context, SimpleExoPlayerView simpleExoPlayerView, ProgressBar progressBar,
                            String shortDescription, TextView tvErrorVideo) {
        mContext = context;
        mSimpleExoPlayerView = simpleExoPlayerView;
        mProgressBar = progressBar;
        mHandler = new Handler();
        mShortDescription = shortDescription;
        mTvErrorVideo = tvErrorVideo;

    }


    public void initializePlayer(Uri mediaUri, BakingExoPlayer bakingExoPlayer) {
        if (mExoPlayer == null) {

            BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
            TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveTrackSelection.Factory(bandwidthMeter);
            TrackSelector trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);
            LoadControl loadControl = new DefaultLoadControl();

            boolean isRenderingVideo = PrefManager.isGeneralSettings(mContext,mContext.getString(R.string.pref_rendering_video));

            int extensionRendererMode;
            if(isRenderingVideo){
                extensionRendererMode = DefaultRenderersFactory.EXTENSION_RENDERER_MODE_ON;
            }else {
                extensionRendererMode = DefaultRenderersFactory.EXTENSION_RENDERER_MODE_OFF;
            }

            mExoPlayer = ExoPlayerFactory.newSimpleInstance(
                    new DefaultRenderersFactory(mContext,null,extensionRendererMode),
                    trackSelector,
                    loadControl);
            mSimpleExoPlayerView.setPlayer(mExoPlayer);


            MediaSource mediaSource;
            mediaSource = new ExtractorMediaSource(mediaUri,
                    new CacheDataSourceFactory(mContext),
                    new DefaultExtractorsFactory(), null, null);
            final boolean isResume = mResumeWindow != C.INDEX_UNSET;
            if (isResume) {
                mExoPlayer.seekTo(mResumeWindow, mResumePosition);
            }

            mExoPlayer.prepare(mediaSource, !isResume, false);
            mExoPlayer.setPlayWhenReady(isAutoPlay);

            bakingExoPlayer.onExoPlayer(mExoPlayer);
            mExoPlayer.addListener(this);
        }

    }

    public void releasePlayer() {
        if (mExoPlayer != null) {
            mExoPlayer.stop();
            mExoPlayer.release();
            mExoPlayer = null;
        }
    }

    private void showPlayer() {
        mSimpleExoPlayerView.setVisibility(View.VISIBLE);
    }

    public void setAutoPlay(boolean autoPlay) {
        isAutoPlay = autoPlay;
    }

    public void setResume(int resumeWindow, long resumePosition) {
        mResumeWindow = resumeWindow;
        mResumePosition = resumePosition;
    }

    public boolean isAutoPlay() {
        return isAutoPlay;
    }

    public int getResumeWindow() {
        return mResumeWindow;
    }

    public long getResumePosition() {
        return mResumePosition;
    }

    public void updateResumePosition() {
        if (mExoPlayer != null) {
            mResumeWindow = mExoPlayer.getCurrentWindowIndex();
            mResumePosition = Math.max(0, mExoPlayer.getContentPosition());
        }
    }

    public void clearResumePosition() {
        mResumeWindow = C.INDEX_UNSET;
        mResumePosition = C.TIME_UNSET;
    }

    private void progressBar(final ExoPlayer exoPlayer) {
        if (mProgressStatus <= exoPlayer.getBufferedPercentage()) {
            visibilityProgressBar(true);
        } else if (exoPlayer.getBufferedPosition() < exoPlayer.getBufferedPercentage()) {
            visibilityProgressBar(false);
        }
        new Thread(() -> {
            while (mProgressStatus < mProgressBar.getMax()) {
                mProgressStatus = exoPlayer.getBufferedPercentage();
                mHandler.post(() -> mProgressBar.setProgress(mProgressStatus));
                try {
                    Thread.sleep(Costants.EXO_PROGRESSBAR_DELAY);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void visibilityProgressBar(boolean visibility) {
        int viewVisible = visibility ? View.VISIBLE : View.GONE;
        mProgressBar.setVisibility(viewVisible);
    }

    public void initializeMediaSession() {
        mMediaSession = new MediaSessionCompat(mContext, Costants.EXO_PLAYER_MANAGER_TAG);
        mMediaSession.setFlags(
                MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                        MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS |
                        Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        mMediaSession.setMediaButtonReceiver(null);
        mStateBuilder = new PlaybackStateCompat.Builder()
                .setActions(
                        PlaybackStateCompat.ACTION_PLAY |
                                PlaybackStateCompat.ACTION_PAUSE |
                                PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS |
                                PlaybackStateCompat.ACTION_PLAY_PAUSE);
        mMediaSession.setPlaybackState(mStateBuilder.build());
        mMediaSession.setCallback(new MySessionCallback());
        mMediaSession.setActive(true);
    }

    private void showNotification(PlaybackStateCompat state) {

        int icon;
        String play_pause;
        if (state.getState() == PlaybackStateCompat.STATE_PLAYING) {
            icon = R.drawable.exo_controls_pause;
            play_pause = mContext.getString(R.string.pause);
        } else {
            icon = R.drawable.exo_controls_play;
            play_pause = mContext.getString(R.string.play);
        }

        NotificationCompat.Action playPauseAction = new NotificationCompat.Action(
                icon, play_pause,
                MediaButtonReceiver.buildMediaButtonPendingIntent(mContext,
                        PlaybackStateCompat.ACTION_PLAY_PAUSE));

        NotificationCompat.Action restartAction = new android.support.v4.app.NotificationCompat
                .Action(R.drawable.exo_controls_previous, mContext.getString(R.string.restart),
                MediaButtonReceiver.buildMediaButtonPendingIntent
                        (mContext, PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS));

        PendingIntent contentPendingIntent = PendingIntent.getActivity
                (mContext, 0, new Intent(mContext, StepActivity.class), 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext, Costants.NOTIFICATION_CHANNEL_ID);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

            NotificationChannel notificationChannel = new NotificationChannel(Costants.NOTIFICATION_CHANNEL_ID,
                    Costants.NOTIFICATION_CHANNEL_NAME, NotificationManager.IMPORTANCE_MIN);
            notificationChannel.setDescription(mContext.getString(R.string.notification_text));

            notificationChannel.setShowBadge(true);
            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);

            NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(NOTIFICATION_SERVICE);
            if(notificationManager!=null){
                notificationManager.createNotificationChannel(notificationChannel);
            }

        }

        builder.setContentTitle(mShortDescription)
                .setContentText(mContext.getString(R.string.notification_text))
                .setAutoCancel(true)
                .setContentIntent(contentPendingIntent)
                .setSmallIcon(R.drawable.ic_free_breakfast_black_24dp)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .addAction(restartAction)
                .addAction(playPauseAction);

        NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(NOTIFICATION_SERVICE);
        if(notificationManager!=null){
            notificationManager.notify(Costants.NOTIFICATION_CHANNEL_ID, Costants.NOTIFICATION_ID, builder.build());
        }

    }

    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest) {

    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

    }

    @Override
    public void onLoadingChanged(boolean isLoading) {

    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        if (playbackState == Player.STATE_BUFFERING) {
            if (mExoPlayer != null) progressBar(mExoPlayer);
        }

        if (playbackState == Player.STATE_READY) {
            visibilityProgressBar(false);
        }

        if ((playbackState == Player.STATE_READY) && playWhenReady) {
            if (mStateBuilder != null) {
                mStateBuilder.setState(PlaybackStateCompat.STATE_PLAYING,
                        mExoPlayer.getCurrentPosition(), 1f);
            }
            setAutoPlay(true);
        } else if ((playbackState == Player.STATE_READY)) {
            if (mStateBuilder != null) {
                mStateBuilder.setState(PlaybackStateCompat.STATE_PAUSED,
                        mExoPlayer.getCurrentPosition(), 1f);
            }
            setAutoPlay(false);
        }

        if ((playbackState == Player.STATE_READY) || (playbackState == Player.STATE_ENDED)) {
            showPlayer();
        }

        if ((playbackState == Player.STATE_ENDED) && (!playWhenReady)) {
            mExoPlayer.seekToDefaultPosition();
        }

        if (playbackState == Player.STATE_ENDED) {
            showPlayer();
        }

        if ((mMediaSession != null) && (mStateBuilder != null)) {
            mMediaSession.setPlaybackState(mStateBuilder.build());
            showNotification(mStateBuilder.build());
        }
    }

    @Override
    public void onRepeatModeChanged(int repeatMode) {
    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {
        mTvErrorVideo.setText(R.string.error_video);
        mTvErrorVideo.setVisibility(View.VISIBLE);
    }

    @Override
    public void onPositionDiscontinuity() {
    }

    @Override
    public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {
    }

    private class MySessionCallback extends MediaSessionCompat.Callback implements BakingExoPlayer {

        @Override
        public void onExoPlayer(SimpleExoPlayer simpleExoPlayer) {
            mExoPlayer = simpleExoPlayer;
        }

        @Override
        public void onPlay() {
            if (mExoPlayer != null) mExoPlayer.setPlayWhenReady(true);
        }

        @Override
        public void onPause() {
            if (mExoPlayer != null) mExoPlayer.setPlayWhenReady(false);
        }

        @Override
        public void onSkipToPrevious() {
            if (mExoPlayer != null) mExoPlayer.seekToDefaultPosition();
        }
    }

    public interface BakingExoPlayer {
        void onExoPlayer(SimpleExoPlayer simpleExoPlayer);
    }
}
