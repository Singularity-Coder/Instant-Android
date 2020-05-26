package com.singularitycoder.mutliviewlist;

import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private RecyclerView recyclerChat;
    private ChatAdapter chatAdapter;
    private RecyclerView.LayoutManager chatLayoutManager;
    private ArrayList<ChatItem> chatList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatuBarColor();
        setContentView(R.layout.activity_main);
        setUpRecyclerView();
    }

    private void setStatuBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = this.getWindow();
            // Clear FLAG_TRANSLUCENT_STATUS flag
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            // Add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        }
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN, WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    private void setUpRecyclerView() {
        recyclerChat = findViewById(R.id.recycler_view);
        chatList = new ArrayList<>();
        chatList.add(new ChatItem(R.mipmap.ic_launcher, "Hey Random Man. What's your super power?", "Normal Man", "12 Dec, 2047", "text", "sender"));
        chatList.add(new ChatItem(R.mipmap.ic_launcher, "I can spit random numbers faster than you can think.", "Random Man", "12 Dec, 2047", "text", "receiver", ""));
        chatList.add(new ChatItem(R.mipmap.ic_launcher, "I dare you try!", "Normal Man", "12 Dec, 2047", "text", "sender"));
        chatList.add(new ChatItem(R.mipmap.ic_launcher, "87341413729643197391104701012342361324", "Random Man", "12 Dec, 2047", "textBroken", "receiver", "", ""));
        chatList.add(new ChatItem(R.mipmap.ic_launcher, R.drawable.whiteflag, "Normal Man", "12 Dec, 2047", "image", "sender"));
        chatAdapter = new ChatAdapter(this, chatList);
        chatLayoutManager = new LinearLayoutManager(this);
        recyclerChat.setLayoutManager(chatLayoutManager);
        recyclerChat.setAdapter(chatAdapter);
    }
}
