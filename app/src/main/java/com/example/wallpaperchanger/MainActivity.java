package com.example.wallpaperchanger;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.WallpaperManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.content.ClipData;

import java.io.IOException;
import java.util.ArrayList;

//다중이미지 불러오기 + recyclerview: https://stickode.tistory.com/61 - 그리드레이아웃으로 해서 안되는듯
//디스플레이 크기 구하기: https://lcw126.tistory.com/280
//GridLayoutManager: https://developer.android.com/reference/androidx/recyclerview/widget/GridLayoutManager?hl=ko
//Service(Foreground, Background...): https://junghun0.github.io/2019/06/09/android-service/
//wpm.setBitmap 너무 느림 -> Thread: https://developer.android.com/guide/components/processes-and-threads?hl=ko#Threads


// Need to do: Run in Background and change wallpaper when the screenlock button clicked

public class MainActivity extends AppCompatActivity {
    WallpaperManager wpm;
    ArrayList<Uri> images = new ArrayList<Uri>();
    RecyclerView recyclerView;
    ImageAdapter adapter;
    ImageView now_wall;
    EditText setwall_pos;
    ClipData clipData;

    ActivityResultLauncher<Intent> mStartForResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if(result.getResultCode() == Activity.RESULT_OK){
                Intent intent = result.getData();
                if(intent.getClipData() == null){
                    Toast.makeText(MainActivity.this, "No Image Choice", Toast.LENGTH_LONG).show();
                    return;
                }
                clipData = intent.getClipData();
                images.clear();
                for(int i=0; i<clipData.getItemCount(); ++i){
                    Uri imageUri = clipData.getItemAt(i).getUri();
                    images.add(imageUri);
                    adapter.changeItems(images);
                }
            }
        }
    });
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setwall_pos = (EditText) findViewById(R.id.edittext_pos);

        wpm = WallpaperManager.getInstance(getApplicationContext());

        now_wall = (ImageView)findViewById(R.id.imageview_nowwall);
        now_wall.setImageDrawable(wpm.getDrawable());

        button_init();
        adapter = new ImageAdapter(images, getApplicationContext());
        recyclerView = findViewById(R.id.recyclerview_images);
        recyclerView.setAdapter(adapter);
        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);
        float density = getResources().getDisplayMetrics().density;
        float dpWidth = outMetrics.widthPixels / density;
        float dp_image_width = 200;
        int num = (int)Math.floor(dpWidth / dp_image_width);
        recyclerView.setLayoutManager(new GridLayoutManager(this, num));
        //recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true));
        screen_onoff();
    }
    public void button_init(){
        Button btn_imgload = (Button) findViewById(R.id.imgload_multi_btn);
        btn_imgload.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                imageload();
            }
        });
        Button btn_changeWP = (Button) findViewById(R.id.change_wallpaper);
        btn_changeWP.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                changeWallpaper();
            }
        });
        Button btn_changeWP_R = (Button) findViewById(R.id.change_wallpaper_random);
        btn_changeWP_R.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                changeWallpaper_random();
            }
        });
    }
    public void imageload(){
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        mStartForResult.launch(intent);
    }
    public void changeWallpaper(){
        try {
            int pos = Integer.parseInt(setwall_pos.getText().toString());
            Bitmap bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), images.get(pos));
            wpm.setBitmap(bmp);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Toast.makeText(MainActivity.this, "Changed Wallpaper", Toast.LENGTH_LONG).show();
        now_wall.setImageDrawable(wpm.getDrawable());
    }
    public void changeWallpaper_random(){
        try {
            int array_size = images.size();
            int pos = (int)Math.floor(Math.random() * array_size);
            Bitmap bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), images.get(pos));
            wpm.setBitmap(bmp);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Toast.makeText(MainActivity.this, "Changed Wallpaper", Toast.LENGTH_LONG).show();
        now_wall.setImageDrawable(wpm.getDrawable());
    }
    public void screen_onoff(){
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(intent.getAction() == Intent.ACTION_SCREEN_OFF){

                } else if(intent.getAction() == Intent.ACTION_SCREEN_ON){
                    Toast.makeText(MainActivity.this, "Screen On", Toast.LENGTH_LONG).show();
                    changeWallpaper_random();
                }
            }
        };
        registerReceiver(receiver, intentFilter);
    }
}