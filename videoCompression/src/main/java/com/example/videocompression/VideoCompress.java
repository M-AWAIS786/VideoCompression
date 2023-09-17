package com.example.videocompression;

import android.os.Handler;
import android.os.Looper;
import androidx.annotation.NonNull;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class VideoCompress {
    private static final String TAG = VideoCompress.class.getSimpleName();
    private static final Executor executor = Executors.newSingleThreadExecutor();
    private static final Handler mainHandler = new Handler(Looper.getMainLooper());

    public static void compressVideoHigh(String srcPath, String destPath, CompressListener listener) {
        compressVideo(srcPath, destPath, listener, VideoController.COMPRESS_QUALITY_HIGH);
    }

    public static void compressVideoMedium(String srcPath, String destPath, CompressListener listener) {
        compressVideo(srcPath, destPath, listener, VideoController.COMPRESS_QUALITY_MEDIUM);
    }

    public static void compressVideoLow(String srcPath, String destPath, CompressListener listener) {
        compressVideo(srcPath, destPath, listener, VideoController.COMPRESS_QUALITY_LOW);
    }

    private static void compressVideo(String srcPath, String destPath, CompressListener listener, int quality) {
        executor.execute(() -> {
            boolean result = VideoController.getInstance().convertVideo(srcPath, destPath, quality, new VideoController.CompressProgressListener() {
                @Override
                public void onProgress(float percent) {
                    mainHandler.post(() -> {
                        if (listener != null) {
                            listener.onProgress(percent);
                        }
                    });
                }
            });

            mainHandler.post(() -> {
                if (listener != null) {
                    if (result) {
                        listener.onSuccess();
                    } else {
                        listener.onFail();
                    }
                }
            });
        });
    }

    public interface CompressListener {
        void onStart();
        void onSuccess();
        void onFail();
        void onProgress(float percent);
    }
}
