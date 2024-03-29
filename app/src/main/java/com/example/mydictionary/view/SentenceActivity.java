package com.example.mydictionary.view;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;

import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mydictionary.R;
import com.example.mydictionary.https.HttpCallBackListener;
import com.example.mydictionary.https.HttpUtil;

import java.io.InputStream;

import static com.example.mydictionary.util.ParseSentenceJSON.audioAddress;
import static com.example.mydictionary.util.ParseSentenceJSON.getJsonResult;
import static com.example.mydictionary.view.HistoryRecordsActivity.pt2Address;

/**
 * 每日一句活动
 * 1.每日一句与翻译的显示
 * 2.图片的显示
 * 3.可以播放音频
 */

public class SentenceActivity extends AppCompatActivity implements View.OnClickListener {

    public TextView textView_sentence;
    public ImageView imageView_picture;
    public ImageButton imageButton_sound;
    private MediaPlayer player = null;

    // 网络查词完成后回调handleMessage方法
    private Handler handler = new  Handler(){
        @SuppressLint("LongLogTag")
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 211:
                    //网络访问成功
                    Log.i("-----------------------","网络访问成功");
                    textView_sentence.setText(Html.fromHtml(msg.obj.toString()));                 //显示文本
                    break;
                case 985:
                    Bitmap pictureBitmap = (Bitmap) msg.obj;
                    imageView_picture.setImageBitmap(pictureBitmap);                              //显示图片
                    break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sentence);
        textView_sentence = (TextView)findViewById(R.id.tv_sentence);
        imageView_picture = (ImageView)findViewById(R.id.iv_picture);
        imageButton_sound = (ImageButton)findViewById(R.id.ib_sound);
        imageButton_sound.setOnClickListener(this);

        loadPicture();                                                                              //下载图片
        loadSentence();                                                                             //下载文本
        Log.i("----------","执行网络访问");


    }

    //网络访问下载句子
    public void loadSentence() {
        String textAddress = "http://open.iciba.com/dsapi/";
        HttpUtil.sentHttpRequest(textAddress, new HttpCallBackListener() {
            @Override
            public void onFinish(InputStream inputStream) {
                Message message = new Message();
                message.obj = getJsonResult(inputStream);
                message.what = 211;
                handler.sendMessage(message);
            }

            @Override
            public void onError() {
                Log.i("-------------","回调onError");
            }
        });
    }
    //网络下载图片
    public void loadPicture(){

        HttpUtil.sentHttpRequest(pt2Address, new HttpCallBackListener() {
            @Override
            public void onFinish(InputStream inputStream) {
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);                            //解析出网络访问返回的字节
                Message message = new Message();
                message.obj = bitmap;
                message.what = 985;
                handler.sendMessage(message);
            }
            @Override
            public void onError() {
                System.out.println("下载图片失败--------------------------------");
                System.out.println("大图片的地址---------------------------->"+pt2Address);


            }
        });
    }
    //播放音频
    @Override
    public void onClick(View v) {

        if (player != null) {
            Log.i("----------------", "player不为空");
            if (player.isPlaying()) {
                player.stop();
            }
            player.release();
            player = null;
        }
        player = MediaPlayer.create(v.getContext(), Uri.parse(audioAddress));
        player.start();
    }
}
