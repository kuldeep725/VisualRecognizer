package com.ibm.mysampleapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.ibm.mysampleapp.R;
import static com.ibm.mysampleapp.activities.MainActivity.TAG;

public class LauncherActivity extends AppCompatActivity {

        public static String KEY = "key";
        public static final String CAMERA_FULL = "cameraFull";
        public static final String GALLERY_FULL = "galleryFull";
        private GoogleSignInClient mGoogleSignInClient;

        @Override
        protected void onCreate(@Nullable Bundle savedInstanceState) {

                super.onCreate(savedInstanceState);
                super.setContentView(R.layout.activity_launcher);

                GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(getString(R.string.default_web_client_id))
                        .requestEmail()
                        .build();
                mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
                ImageButton cameraFull = (ImageButton) findViewById(R.id.cameraFull);
                ImageButton galleryFull = (ImageButton) findViewById(R.id.galleryFull);
                Button logOut = (Button) findViewById(R.id.log_out);
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
                logOut.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                                mGoogleSignInClient.signOut()
                                        .addOnCompleteListener(LauncherActivity.this, new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                        Intent i = new Intent(LauncherActivity.this, Login.class);
                                                        startActivity(i);
                                                        finish();
                                                }
                                        });
                        }
                });

        }

        @Override
        protected void onStart() {
                super.onStart();
                // Check for existing Google Sign In account, if the user is already signed in
                // the GoogleSignInAccount will be non-null.
                GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
                Log.d(TAG, "account = "+account);
                if(account ==  null) {
                         Intent i = new Intent(LauncherActivity.this, Login.class);
                        startActivity(i);
                        finish();
                }
//                SharedPreferences loginPrefs = getSharedPreferences(Login.DATABASE, MODE_PRIVATE);
//                String account = loginPrefs.getString(Login.ACCOUNT, "null");
//                if(account.equals("null")) {
//                        Intent i = new Intent(LauncherActivity.this, Login.class);
//                        startActivity(i);
//                        finish();
//                }
        }
}
