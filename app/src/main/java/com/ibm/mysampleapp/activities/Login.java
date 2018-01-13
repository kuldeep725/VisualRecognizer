package com.ibm.mysampleapp.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.;
import com.google.firebase.auth.FirebaseAuth;
import com.ibm.mysampleapp.R;

public class Login extends AppCompatActivity {

        private static final int RC_SIGN_IN = 9001;

        // [START declare_auth]
        private FirebaseAuth mAuth;
        // [END declare_auth]

        private GoogleSignInClient mGoogleSignInClient;
        @Override
        protected void onCreate(@Nullable Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.login);
                // Configure Google Sign In
                GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(getString(R.string.default_web_client_id))
                        .requestEmail()
                        .build();

        }

        @Override
        protected void onStart() {
                super.onStart();
        }

        @Override
        protected void onDestroy() {
                super.onDestroy();
        }
}
