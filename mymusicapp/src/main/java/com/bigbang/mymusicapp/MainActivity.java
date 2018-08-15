package com.bigbang.mymusicapp;

import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.bigbang.mymusicapp.service.MyMusicService;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private ListView mListView;
    private List<String> mList = new ArrayList<>();
    public static List<String> pathList = new ArrayList<>();

    private int index = 0;
    private Button btnPlayPause, btnStop;

    public static ProgressBar mProgressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnPlayPause = findViewById(R.id.btnPlayPause);
        btnStop = findViewById(R.id.btnStop);
        mListView = findViewById(R.id.listView);

        getMusic("/music/");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, mList);
        mListView.setAdapter(adapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, MyMusicService.class);
                intent.putExtra("index", position);
                intent.putExtra("tag", "play");
                startService(intent);
                btnPlayPause.setText("||");
            }
        });
    }

    private void getMusic(String path) {
        path = Environment.getExternalStorageDirectory() + path;
        Log.i(TAG, "!!! path:"+path);
        File file = new File(path);
        File[] files = file.listFiles();
        Log.i(TAG, "getMusic: ");
        for (int i = 0; i < files.length; i++) {
            String name = files[i].getName();
            //截取后缀
            String substring = name.substring(files[i].getName().length() - 3, files[i].getName().length());
            if (substring.equalsIgnoreCase("mp3")) {
                // 存储歌曲名
                mList.add(files[i].getName());
                // 存储歌曲路径
                pathList.add(files[i].getAbsolutePath());
            }
        }
    }
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnPlayPause: // 播放暂停
                Intent intent1 = new Intent(this, MyMusicService.class);
                if (btnPlayPause.getText().equals("▶")) {
                    //播放
                    intent1.putExtra("tag", "start");
                    btnPlayPause.setText("||");
                }else  {
                    //暂停
                    intent1.putExtra("tag", "pause");
                    btnPlayPause.setText("▶");
                }
                startService(intent1);
                break;
            case R.id.btnStop: //停止
                Intent intent2 = new Intent(this, MyMusicService.class);
                intent2.putExtra("tag", "stop");
                startService(intent2);
                btnPlayPause.setText("▶");
                break;
            case R.id.btnPre: //前一首
                if (index == 0) {
                    index = pathList.size() - 1;
                }else {
                    index--;
                }
                Intent intent3 = new Intent(MainActivity.this, MyMusicService.class);
                intent3.putExtra("index", index);
                intent3.putExtra("tag", "play");
                startService(intent3);
                btnPlayPause.setText("||");
                break;
            case R.id.btnNext: // 下一首
                if (index == pathList.size()-1){
                    index = 0;
                }else {
                    index++;
                }
                Intent intent4 = new Intent(MainActivity.this, MyMusicService.class);
                intent4.putExtra("index", index);
                intent4.putExtra("tag", "play");
                startService(intent4);
                btnPlayPause.setText("||");
                break;
                default:break;
        }
    }
}
