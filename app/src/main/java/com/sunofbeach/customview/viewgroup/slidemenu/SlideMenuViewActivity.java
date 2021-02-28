package com.sunofbeach.customview.viewgroup.slidemenu;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.sunofbeach.customview.R;

public class SlideMenuViewActivity extends AppCompatActivity {

    private final static String TAG = "SlideMenuViewActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slide_menu_view);
        SlideMenuView viewById = findViewById(R.id.slide_Menu_view);
        viewById.setOnEditClickListener(new SlideMenuView.OnEditClickListener() {
            @Override
            public void onReadClick() {
                Log.d(TAG, "onReadClick-->");
            }

            @Override
            public void onDeleteClick() {
                Log.d(TAG, "onDeleteClick-->");

            }

            @Override
            public void onTopClick() {
                Log.d(TAG, "onTopClick-->");

            }
        });
    }
}