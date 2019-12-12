package com.yhd.mediaplayer.app.frag;

import android.content.res.AssetManager;
import android.media.MediaDataSource;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;

import com.de.rocket.ue.frag.RoFragment;
import com.de.rocket.ue.injector.BindView;
import com.de.rocket.ue.injector.Event;
import com.yhd.mediaplayer.MediaPlayerHelper;
import com.yhd.mediaplayer.app.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

/**
 * 双波浪曲线
 * Created by haide.yin(haide.yin@tcl.com) on 2019/6/6 16:12.
 */
public class Frag_mediaplayer extends RoFragment {

    private static final String TAG = "MainActivity";
    private final static String URL = "https://github.com/yinhaide/HDMediaPlayer/blob/master/app/src/main/assets/demo.mp4";
    private final static String URL2 = "https://devimages.apple.com.edgekey.net/streaming/examples/bipbop_4x3/gear1/fileSequence0.ts";

    @BindView(R.id.surfaceView)
    private SurfaceView surfaceView;

    private volatile byte[] videoBuffer;

    @Override
    public int onInflateLayout() {
        return R.layout.frag_mediaplayer;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
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
                });
                //.playAssetVideo(activity,"test.mp4");//开始播放
        AssetManager assetMg= activity.getAssets();
        InputStream inputStream = null;
        try {
            inputStream = assetMg.open("test_encry.mp4");
            videoBuffer = toByteArray(inputStream);
            Log.v("yhd-","videoBuffer:"+videoBuffer.length);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(videoBuffer != null){
            videoBuffer = deEncrypt(videoBuffer);
            UrlMediaDataSource mediaDataSource = new UrlMediaDataSource();
            MediaPlayerHelper.getInstance() .playVideoDataSource(mediaDataSource);
        }
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
        MediaPlayerHelper.getInstance().playUrl(activity,URL);
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

    @RequiresApi(api = Build.VERSION_CODES.M)
    public class UrlMediaDataSource extends MediaDataSource {

        public UrlMediaDataSource() {

        }

        @Override
        public long getSize() {
            synchronized (videoBuffer) {
                return videoBuffer.length;
            }
        }

        @Override
        public int readAt(long position, byte[] buffer, int offset, int size) {
            synchronized (videoBuffer){
                int length = videoBuffer.length;
                if (position >= length) {
                    return -1; // -1 indicates EOF
                }
                if (position + size > length) {
                    size -= (position + size) - length;
                }
                System.arraycopy(videoBuffer, (int)position, buffer, offset, size);
                return size;
            }
        }

        @Override
        public void close() {

        }
    }

    public static byte[] toByteArray(InputStream input) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        int n = 0;
        while (-1 != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
        }
        return output.toByteArray();
    }

    private final int REVERSE_LENGTH = 100;
    /**
     * 加解密
     *
     * @param strFile 源文件绝对路径
     * @return
     */
    private boolean encrypt(String strFile) {
        int len = REVERSE_LENGTH;
        try {
            File f = new File(strFile);
            RandomAccessFile raf = new RandomAccessFile(f, "rw");
            long totalLen = raf.length();
            if (totalLen < REVERSE_LENGTH)
                len = (int) totalLen;
            FileChannel channel = raf.getChannel();
            MappedByteBuffer buffer = channel.map(FileChannel.MapMode.READ_WRITE, 0, REVERSE_LENGTH);
            byte tmp;
            for (int i = 0; i < len; ++i) {
                byte rawByte = buffer.get(i);
                tmp = (byte) (rawByte ^ i);
                buffer.put(i, tmp);
            }
            buffer.force();
            buffer.clear();
            channel.close();
            raf.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 对流进行异或运算，两次运算就可以变回原来的
     *
     * @param bufferData 数据源字节流
     * @return 异或运算之后的字节流
     */
    private byte[] deEncrypt(byte[] bufferData){
        int REVERSE_LENGTH = 100;
        if(bufferData != null && bufferData.length > REVERSE_LENGTH){
            for(int i = 0;i < REVERSE_LENGTH ; ++i){
                bufferData[i] = (byte) (bufferData[i] ^ i);
            }
        }
        return bufferData;
    }

    /**
     * 将流写到指定文件
     *
     * @param buffer 数据源字节流
     * @param filePath 目标文件
     */
    private void writeToLocal(byte[] buffer,String filePath){
        OutputStream out = null;
        File file = new File(filePath);
        try {
            //创建文件
            if(file.exists()){
                file.delete();
            }
            file.createNewFile();
            out = new FileOutputStream(file);
            out.write(buffer);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
