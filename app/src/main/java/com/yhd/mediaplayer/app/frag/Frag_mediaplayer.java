package com.yhd.mediaplayer.app.frag;

import android.os.Handler;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;

import com.de.rocket.ue.frag.RoFragment;
import com.de.rocket.ue.injector.BindView;
import com.de.rocket.ue.injector.Event;
import com.yhd.mediaplayer.MediaPlayerHelper;
import com.yhd.mediaplayer.app.R;

/**
 * 双波浪曲线
 * Created by haide.yin(haide.yin@tcl.com) on 2019/6/6 16:12.
 */
public class Frag_mediaplayer extends RoFragment {

    private static final String TAG="MainActivity";
    private final static String URL="https://github.com/yinhaide/HDMediaPlayer/blob/master/app/src/main/assets/demo.mp4";

    @BindView(R.id.surfaceView)
    private SurfaceView surfaceView;

    @Override
    public int onInflateLayout() {
        return R.layout.frag_mediaplayer;
    }

    @Override
    public void initViewFinish(View inflateView) {
        MediaPlayerHelper
                .getInstance()              //单例
                .setSurfaceView(surfaceView)//设置预览区域
                .setProgressInterval(1000)  //设置进度回调间隔
                .setOnStatusCallbackListener((state, args) -> {
                    Log.v(TAG,"--"+state.toString());
                    if(state == MediaPlayerHelper.CallBackState.PROGRESS){
                        if(args.length > 0){
                            toast("进度:"+args[0]+"%");
                        }
                    }else if(state== MediaPlayerHelper.CallBackState.COMPLETE){
                        //MediaPlayerHelper.getInstance().playAsset(activity,"test.mp4");
                    }else if(state== MediaPlayerHelper.CallBackState.BUFFER_UPDATE){
                        if(args.length > 1){
                            toast("网络缓冲:"+args[1]+"%");
                        }
                    }else if(state== MediaPlayerHelper.CallBackState.EXCEPTION){
                        if(args.length > 0){
                            toast("播放异常:" + args[0]);
                        }
                    }else if(state== MediaPlayerHelper.CallBackState.ERROR){
                        if(args.length > 0){
                            toast("播放错误:" + args[0]);
                        }
                    }else if(state== MediaPlayerHelper.CallBackState.PREPARE){
                        if(args.length > 0){
                            activity.runOnUiThread(() -> toast("视频准备好了:" + args[0]));
                        }
                    }
                })
                .playAssetVideo(activity,"test.mp4");//开始播放
    }

    @Override
    public void onNexts(Object object) {

    }

    @Event(R.id.assetsMP3Button)
    private void playassetMP3(View view){
        MediaPlayerHelper.getInstance().playAssetMusic(activity,"test.mp3");
    }

    @Event(R.id.assetsMP4Button)
    private void playAssetMP4(View view){
        MediaPlayerHelper.getInstance().playAssetVideo(activity,"test.mp4");
    }

    @Event(R.id.urlButton)
    private void playNetMP4(View view){
        MediaPlayerHelper.getInstance().playUrl(URL);
    }

    @Event(R.id.stopButton)
    private void stop(View view){
        MediaPlayerHelper.getInstance().getMediaPlayer().pause();
    }

    @Event(R.id.startButton)
    private void start(View view){
        MediaPlayerHelper.getInstance().getMediaPlayer().start();
    }

    @Event(R.id.resetButton)
    private void reset(View view){
        MediaPlayerHelper.getInstance().playAssetVideo(activity,"test.mp4");
    }

    public void onStop(){
        super.onStop();
        MediaPlayerHelper.getInstance().stop();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        MediaPlayerHelper.getInstance().release();
    }
}
