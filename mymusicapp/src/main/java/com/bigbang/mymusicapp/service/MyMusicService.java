package com.bigbang.mymusicapp.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;

import com.bigbang.mymusicapp.MainActivity;

import java.io.IOException;

import static com.bigbang.mymusicapp.MainActivity.pathList;

public class MyMusicService extends Service {

    private MediaPlayer mMediaPlayer;

    public MyMusicService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mMediaPlayer = new MediaPlayer();
        new Thread(){
            @Override
            public void run() {
                super.run();
                while (true) {
                    try {
                        sleep(200);
                        if (mMediaPlayer !=null) {
                            MainActivity.mProgressBar.setProgress(mMediaPlayer.getCurrentPosition());
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }

    int isPlaying = -1;
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String tag = intent.getStringExtra("tag");
        if (tag.equals("play")) {
            int index = intent.getIntExtra("index", 0);
            play(index);
            isPlaying = 1;
        } else if (tag.equals("pause")) {
            mMediaPlayer.pause();
            isPlaying = 1;
        } else if (tag.equals("start")) {
            //第一次进入点击播放 默认播放第一首
            if (isPlaying == -1) {
                play(0);
            } else {
                mMediaPlayer.start();
            }
        }
        return START_NOT_STICKY;
    }

    private void play(final int index) {

        String path = pathList.get(index);
        try {
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.stop();
            }
            mMediaPlayer.reset();
            mMediaPlayer.setDataSource(path);
            mMediaPlayer.prepare();
            mMediaPlayer.start();
            // 一首播放完 播放下一首
            /*mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {

                }
            });*/
            MainActivity.mProgressBar.setMax(mMediaPlayer.getDuration());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMediaPlayer.pause();
        mMediaPlayer.stop();
        mMediaPlayer.release();
        isPlaying = -1;
        mMediaPlayer = null;
    }
}
