package com.example.mydictionary.view;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.mydictionary.R;
import com.example.mydictionary.adapter.SearchRecordsAdapter;
import com.example.mydictionary.db.WordsAction;
import com.example.mydictionary.https.HttpCallBackListener;
import com.example.mydictionary.https.HttpUtil;


import android.content.Intent;



import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;




import java.io.InputStream;
import java.util.ArrayList;

import static com.example.mydictionary.db.WordsAction.db;
import static com.example.mydictionary.util.ParseSentenceJSON.getJsonResult;
import static com.example.mydictionary.util.ParseSentenceJSON.pictureAddress;


/**
 * 搜索历史记录的活动
 * 1.显示搜索记录
 * 2.一键删除历史记录
 * 3.一个一个删除
 * 4.访问每日一句API，提前获得图片地址，解决先显示文字后显示图片的问题，实现同时显示
 *
 */


public class HistoryRecordsActivity extends AppCompatActivity {
    private SearchView searchView;
    public static ArrayList<String> arrayList ,arrayList2 ;                                       //存放历史记录内容的数组
    public static SearchRecordsAdapter searchRecordsAdapter;                                      //自定义适配器对象
    public  static ListView listView;                                                             //显示历史记录内容的listView
    public static WordsAction wordsAction;
    private Button button_empty_records;                                                         //清空历史记录按钮
    //    private Button mEmptyRecordsBtn;
    private  long exitTime;
    public static String pt2Address;                                                              //每日一句图片的地址

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_records);
        searchView = (SearchView) findViewById(R.id.Records_searchView);
        button_empty_records = (Button) findViewById(R.id.bt_empty_history);
        listView = (ListView) findViewById(R.id.history_list_view);

        wordsAction = WordsAction.getInstance(this);                                               //获得WordsAction对象

        setRecordsAdapter();                                                                        //打开应用就显示搜索记录页面
        loadSentence();                                                                             //为每日一句提前获取图片地址

        searchView.setSubmitButtonEnabled(true);                                                  //设置显示搜索按钮
        searchView.setIconifiedByDefault(false);//设置不自动缩小为图标，点搜索框就出现软键盘


        //搜索框查询事件
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                TranslateActivity.kWord = query;                                                    //输入的值赋值给kWord ，让翻译活动查询kWord
                Intent intent = new Intent(HistoryRecordsActivity.this, TranslateActivity.class);  //构建Intent，TranslateActivity.this为上下文,NoteActivity.class为活动目标
                startActivity(intent);
                return true;
            }

            //SearchView输入文本改变事件
            @Override
            public boolean onQueryTextChange(String newText) {
                setRecordsAdapter();                                                                //当输入内容改变时，显示搜索历史记录
                return false;
            }
        });

        //一键清空历史记录
        button_empty_records.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.delete("Words", null, null);                                                   //数据库中删除点击的单词
                setRecordsAdapter();                                                                //重新向数据库取值，获得arrayList2
                searchRecordsAdapter = new SearchRecordsAdapter(HistoryRecordsActivity.this, arrayList2);  //创建一个WordAdapter适配器类的对象，并传入参数进行初始化
                searchRecordsAdapter.notifyDataSetChanged();                                        //刷新数据视图
                listView.setAdapter(searchRecordsAdapter);                                          //显示被单词被删除后的页面

            }
        });

        //点击搜索记录listView的item时会切换到点击的单词的翻译页面
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                TranslateActivity.kWord =arrayList2.get(position);                                  //把点击的单词赋值给kWord，让翻译活动查询这个单词
                Intent intent = new Intent(HistoryRecordsActivity.this, TranslateActivity.class);  //构建Intent，TranslateActivity.this为上下文,NoteActivity.class为活动目标
                startActivity(intent);
            }
        });
    }

    //数据库中获取数据，显示出来
    public void setRecordsAdapter() {
        arrayList = new ArrayList<String>();
        arrayList2 = new ArrayList<String>();
        arrayList = wordsAction.getWordsFromSQLiteToRecordsList();                                 //创建ArrayList对象
        for (int i = arrayList.size(); i > 0; i--) {                                               //让最近放入数据库中的单词显示在前面
            arrayList2.add(arrayList.get(i - 1));
        }
        searchRecordsAdapter = new SearchRecordsAdapter(HistoryRecordsActivity.this,  arrayList2); //创建一个WordAdapter适配器类的对象，并传入参数进行初始化
        listView.setAdapter(searchRecordsAdapter);                                                  //利用setAdapter建立ListView与数据之间的关联
    }



    //创建菜单
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main,menu);
        return true;
    }

    //菜单响应事件
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.note_item:                                                                  //点击单词本，切换到单词本的活动
                Intent intent=new Intent(HistoryRecordsActivity.this,NoteActivity.class);         //切换活动
                startActivity(intent);
                break;
            case R.id.sentence_item:                                                              //点击每日一句，切换到每日一句的活动
                Intent intent2=new Intent(HistoryRecordsActivity.this,SentenceActivity.class);    //切换活动
                startActivity(intent2);
                break;
        }
        return true;
    }

    //双击返回键退出程序
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                finish();
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    /**
     * 解决每日一句活动中先显示文字再显示图片的问题
     * 在主活动中就获得图片的地址，就可以同时显示文字和图片
     */
    public void loadSentence() {
        String textAddress = "http://open.iciba.com/dsapi/";
        HttpUtil.sentHttpRequest(textAddress, new HttpCallBackListener() {
            @Override
            public void onFinish(InputStream inputStream) {
                String str = getJsonResult(inputStream);                                            //获得图片地址，这样启动每日一句时下载图片不用先等文字解析后获得图片地址，
                pt2Address = pictureAddress;                                                        // 而是直接使用这个地址
            }
            @Override
            public void onError() {

            }
        });
    }

}

