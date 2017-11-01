package it.instantapps.bakingapp.media;

import android.content.Context;
import android.os.Build;
import android.os.Environment;

import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.upstream.FileDataSource;
import com.google.android.exoplayer2.upstream.cache.CacheDataSink;
import com.google.android.exoplayer2.upstream.cache.CacheDataSource;
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor;
import com.google.android.exoplayer2.upstream.cache.SimpleCache;
import com.google.android.exoplayer2.util.Util;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

import it.instantapps.bakingapp.utility.Costants;
import it.instantapps.bakingapp.utility.Utility;

public class CacheDataSourceFactory implements DataSource.Factory {
    private final Context mContext;
    private final DefaultDataSourceFactory mDefaultDatasourceFactory;

    CacheDataSourceFactory(Context context) {
        super();
        mContext = context;

        String userAgent = Util.getUserAgent(mContext, Costants.USER_AGENT_CACHE);
        DefaultBandwidthMeter defaultBandwidthMeter = new DefaultBandwidthMeter();
        DefaultHttpDataSourceFactory defaultHttpDataSourceFactory = new DefaultHttpDataSourceFactory(
                userAgent,
                defaultBandwidthMeter);

        mDefaultDatasourceFactory = new DefaultDataSourceFactory(
                mContext,
                defaultBandwidthMeter,
                defaultHttpDataSourceFactory);
    }

    @Override
    public DataSource createDataSource() {


        File file;
        long fileCache;
        long fileCacheMax;
        if ((Build.VERSION.SDK_INT >= 23) &&
                (Utility.isPermissionExtStorage(mContext)) &&
                isExternalStorageWritable()) {

            file = new File(mContext.getExternalCacheDir(), Costants.CACHE_VIDEO_DIR);
            fileCache = Costants.EXT_CACHE_FILE_SIZE_MAX;
            fileCacheMax = Costants.EXT_CACHE_SIZE_MAX;

        } else {

            file = new File(mContext.getCacheDir(), Costants.CACHE_VIDEO_DIR);
            fileCache = Costants.CACHE_FILE_SIZE_MAX;
            fileCacheMax = Costants.CACHE_SIZE_MAX;

        }

        LeastRecentlyUsedCacheEvictor cacheEvictor = new LeastRecentlyUsedCacheEvictor(fileCacheMax);
        SimpleCache simpleCache = new SimpleCache(file, cacheEvictor);


        return new CacheDataSource(
                simpleCache,
                mDefaultDatasourceFactory.createDataSource(),
                new FileDataSource(),
                new CacheDataSink(simpleCache, fileCache),
                CacheDataSource.FLAG_BLOCK_ON_CACHE | CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR,
                null);
    }

    public static void getClearData(Context context) {
        File file;
        String path;
        if ((Build.VERSION.SDK_INT >= 23) && (Utility.isPermissionExtStorage(context))) {
            path = Environment.getExternalStorageState().concat(Costants.CACHE_VIDEO_DIR);
        } else {
            path = Environment.getDataDirectory().toString().concat(Costants.CACHE_VIDEO_DIR);
        }
        try {
            file = new File(path);
            FileUtils.deleteDirectory(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

   private boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
       return Environment.MEDIA_MOUNTED.equals(state);
   }


}