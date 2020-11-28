package com.kanyideveloper.simplevideoview;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;


public class MainActivity extends AppCompatActivity {

    private static final String VIDEO_SAMPLE = "https://developers.google.com/training/images/tacoma_narrows.mp4";
    private VideoView mVideoView;
    private int currentPosition = 0;
    private static final String PLAYBACK_TIME = "playback_time";
    private TextView mBufferingTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mVideoView = findViewById(R.id.videoview);
        mBufferingTextView = findViewById(R.id.buffering_textview);


        //check for the existence of the instance state bundle, and update the value of mCurrentTime with the value from that bundle.
        if (savedInstanceState != null){
            currentPosition = savedInstanceState.getInt(PLAYBACK_TIME);
        }

        MediaController mediaController = new MediaController(this);
        mediaController.setMediaPlayer(mVideoView);
        mVideoView.setMediaController(mediaController);
    }

    private Uri getMedia(String mediaName){

        if (URLUtil.isValidUrl(mediaName)) {
            return Uri.parse(mediaName);
        }else {
            return Uri.parse("android.resource://" + getPackageName() +
                    "/raw/" + mediaName);
        }
    }

    private void initializePlayer(){

        mBufferingTextView.setVisibility(View.VISIBLE);
        Uri videoUri = getMedia(VIDEO_SAMPLE);
        mVideoView.setVideoURI(videoUri);


        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mBufferingTextView.setVisibility(View.INVISIBLE);

                if (currentPosition > 0){
                    mVideoView.seekTo(currentPosition);
                }
                else {
                    mVideoView.seekTo(0);
                }

                mVideoView.start();

            }
        });

        mVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                Toast.makeText(MainActivity.this, "Media completed playing", Toast.LENGTH_SHORT).show();
                mVideoView.seekTo(0);
            }
        });

    }

    private void releasePlayer(){
        mVideoView.stopPlayback();
    }

    //have the value of mCurrentTime to the instance state bundle. Get the current playback position with the getCurrentPosition() method.
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt(PLAYBACK_TIME, mVideoView.getCurrentPosition());
    }

    @Override
    protected void onStart() {
        super.onStart();
        initializePlayer();
    }

    @Override
    protected void onStop() {
        super.onStop();
        releasePlayer();
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            mVideoView.pause();
        }
    }
}