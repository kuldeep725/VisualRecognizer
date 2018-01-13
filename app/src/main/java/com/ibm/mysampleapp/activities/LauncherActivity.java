package com.ibm.mysampleapp.activities;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.ibm.mysampleapp.R;

public class LauncherActivity extends AppCompatActivity {

        public static String KEY = "key";
        public static final String CAMERA_FULL = "cameraFull";
        public static final String GALLERY_FULL = "galleryFull";

        @Override
        protected void onCreate(@Nullable Bundle savedInstanceState) {

                super.onCreate(savedInstanceState);
                super.setContentView(R.layout.activity_launcher);

                ImageButton cameraFull = (ImageButton) findViewById(R.id.cameraFull);
                ImageButton galleryFull = (ImageButton) findViewById(R.id.galleryFull);
                cameraFull.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                                Intent i = new Intent(LauncherActivity.this, MainActivity.class);
                                i.putExtra(KEY, CAMERA_FULL);
                                startActivity(i);
                        }
                });
                galleryFull.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                                Intent i = new Intent(LauncherActivity.this, MainActivity.class);
                                i.putExtra(KEY, GALLERY_FULL);
                                startActivity(i);
                        }
                });
        }
}
