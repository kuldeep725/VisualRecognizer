package com.ibm.mysampleapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.ibm.mysampleapp.R;

import static com.ibm.mysampleapp.activities.MainActivity.TAG;
//import com.google.firebase.auth.FirebaseAuth;

public class Login extends AppCompatActivity {

        private static final int RC_SIGN_IN = 9001;
        public static String ACCOUNT = "account";
        public static final  String DATABASE = "database";
        // [START declare_auth]
//        private FirebaseAuth mAuth;
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
                // Build a GoogleSignInClient with the options specified by gso.
                mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
                // Set the dimensions of the sign-in button.
                SignInButton signInButton = (SignInButton) findViewById(R.id.sign_in_button);
                signInButton.setSize(SignInButton.SIZE_STANDARD);
                signInButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                                startActivityForResult(signInIntent, RC_SIGN_IN);
                        }
                });

        }

        @Override
        protected void onStart() {
                super.onStart();
                // Check for existing Google Sign In account, if the user is already signed in
// the GoogleSignInAccount will be non-null.
//                GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
//                updateUI(account);
//                if(account != null) {
//                        Intent i = new Intent(Login.this, LauncherActivity.class);
//                        startActivity(i);
//                        finish();
//                }
        }

        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
                super.onActivityResult(requestCode, resultCode, data);

                // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
                if (requestCode == RC_SIGN_IN) {
                        // The Task returned from this call is always completed, no need to attach
                        // a listener.
                        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                        handleSignInResult(task);
                }
        }
        private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
                try {
                        GoogleSignInAccount account = completedTask.getResult(ApiException.class);

                        // Signed in successfully, show authenticated UI.
                        Intent i = new Intent(Login.this, LauncherActivity.class);
//                        SharedPreferences prefs = getSharedPreferences(DATABASE, MODE_PRIVATE);
//                        SharedPreferences.Editor prefsEditor = prefs.edit();
//                        prefsEditor.putString(ACCOUNT, String.valueOf(account));
//                        Log.d(TAG, "String.valueOf(account) = "+String.valueOf(account));
//                        prefsEditor.apply();
                        startActivity(i);
                        finish();
//                        updateUI(account);
                } catch (ApiException e) {
                        // The ApiException status code indicates the detailed failure reason.
                        // Please refer to the GoogleSignInStatusCodes class reference for more information.
                        Log.d(TAG, "signInResult:failed code=" + e.getStatusCode());
//                        Toast.makeText(this, "Login failed", Toast.LENGTH_SHORT).show();
//                        updateUI(null);
                }
        }
        @Override
        protected void onDestroy() {
                super.onDestroy();
        }
}
