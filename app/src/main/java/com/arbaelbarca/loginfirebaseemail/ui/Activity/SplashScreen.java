package com.arbaelbarca.loginfirebaseemail.ui.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

import com.arbaelbarca.loginfirebaseemail.Constants;
import com.arbaelbarca.loginfirebaseemail.MainActivity;
import com.arbaelbarca.loginfirebaseemail.R;
import com.arbaelbarca.loginfirebaseemail.ui.Activity.LoginActivity.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashScreen extends AppCompatActivity {
    public static SharedPreferences sharedPreferences;
    public static SharedPreferences.Editor editor;

    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_splash_screen);

        user = FirebaseAuth.getInstance().getCurrentUser();

    }

    @Override
    protected void onStart() {
        super.onStart();
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }else {
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }else {
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            finish();
        }

    }


}
