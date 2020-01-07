package com.yhd.mediaplayer.app.frag;

import android.Manifest;
import android.content.res.AssetManager;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;

import com.de.rocket.ue.frag.RoFragment;
import com.de.rocket.ue.injector.BindView;
import com.de.rocket.ue.injector.Event;
import com.dueeeke.videoplayer.controller.BaseVideoController;
import com.dueeeke.videoplayer.player.VideoView;
import com.yhd.mediaplayer.app.R;

import java.io.File;
import java.io.IOException;

/**
 * 类作用描述
 * Created by haide.yin(haide.yin@tcl.com) on 2020/1/6 9:36.
 */
public class Frag_test extends RoFragment {

    @BindView(R.id.player)
    private VideoView videoView;
    @BindView(R.id.iv_logo)
    private ImageView ivLogo;

    @Override
    public int onInflateLayout() {
        return R.layout.frag_test;
    }

    @Override
    public void initViewFinish(View view) {
        /*needPermissison(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, (i, b, list) -> {
            Log.v("yhd-","i："+i+" b:"+b+" list:"+list.toString());
        });*/
        videoView.setVideoController(new BaseVideoController(activity) {
            @Override
            protected int getLayoutId() {
                return 0/*R.layout.item_test*/;
            }
        }); //设置控制器
        videoView.setOnStateChangeListener(new VideoView.OnStateChangeListener() {
            @Override
            public void onPlayerStateChanged(int playerState) {
                Log.v("yhd-","playerState："+playerState);
            }

            @Override
            public void onPlayStateChanged(int playState) {
                Log.v("yhd-","playState："+playState);
                if(playState == 5){
                    playAssets();
                    ivLogo.setVisibility(View.VISIBLE);
                }if(playState == 3){
                    ivLogo.setVisibility(View.INVISIBLE);
                }
            }
        });
        playAssets();
    }

    @Override
    public void onNexts(Object o) {

    }

    @Event(R.id.assetsMP3Button)
    private void playassetMP3(View view){

    }

    @Event(R.id.assetsMP4Button)
    private void playAssetMP4(View view){
        playAssets();
    }

    @Event(R.id.urlButton)
    private void playNetMP4(View view){

    }

    @Event(R.id.stopButton)
    private void stop(View view){

    }

    @Event(R.id.startButton)
    private void start(View view){

    }

    @Event(R.id.encryButton)
    private void encry(View view){

    }

    public void onStop(){
        super.onStop();

    }

    @Override
    public void onDestroy(){
        super.onDestroy();

    }

    private void playAssets(){
        videoView.release();
        Log.v("yhd-","playAssets");
        AssetManager assetMg= activity.getAssets();
        //使用IjkPlayer解码
        //videoView.setPlayerFactory(IjkPlayerFactory.create());
        try {
            videoView.setAssetFileDescriptor(assetMg.openFd("await.mp4"));

        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.v("yhd-","/mnt/sdcard/haha.mp4 exist:"+new File("/mnt/sdcard/haha.mp4").exists());
        //videoView.setUrl("file:///mnt/sdcard/demo.mp4");
        //videoView.setUrl("file:///android_asset/" + "demo.mp4");
        videoView.start(); //开始播放，不调用则不自动播放
    }
}
