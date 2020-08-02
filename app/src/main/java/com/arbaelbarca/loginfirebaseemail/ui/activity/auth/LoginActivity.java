package com.arbaelbarca.loginfirebaseemail.ui.activity.auth;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.arbaelbarca.loginfirebaseemail.Constants;
import com.arbaelbarca.loginfirebaseemail.MainActivity;
import com.arbaelbarca.loginfirebaseemail.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.orhanobut.hawk.Hawk;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.txtEmail)
    EditText txtEmail;
    @BindView(R.id.cbShowPassword)
    CheckBox cbShowPassword;
    @BindView(R.id.txtPassword)
    EditText txtPassword;
    @BindView(R.id.ti_password)
    TextInputLayout tiPassword;
    @BindView(R.id.checkPass)
    CheckBox checkPass;
    @BindView(R.id.btnLogin)
    Button btnLogin;
    @BindView(R.id.txtDaftar)
    TextView txtDaftar;

    FirebaseAuth auth;
    FirebaseUser user;
    ProgressDialog progressDialog;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        initial();
    }

    private void initial() {


        auth = FirebaseAuth.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        progressDialog = new ProgressDialog(this);


        txtDaftar.setOnClickListener(view -> startActivity(new Intent(getApplicationContext(), RegisterActivity.class)));
        btnLogin.setOnClickListener(view -> masuk());
    }

    private void masuk() {
        progressDialog.show();
        progressDialog.setMessage("Loading");
        final String email = txtEmail.getText().toString();
        String password = txtPassword.getText().toString();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Minimal password 5 caracter", Toast.LENGTH_LONG).show();
            progressDialog.dismiss();
        } else {
            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    getTokenId();
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Gagal Masuk", Toast.LENGTH_LONG).show();

                }
            });
        }

    }


    void getTokenId() {
        progressDialog.show();
        user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w("", "getInstanceId failed", task.getException());
                        return;
                    }
                    // Get new Instance ID token
                    String token = task.getResult().getToken();

                    // Log and toast
                    progressDialog.dismiss();
                    reference = FirebaseDatabase.getInstance().getReference("User_Hos")
                            .child(user.getUid()).child("Notification");
                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("token_id", token);
                    reference.updateChildren(hashMap).addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()) {
                            sharedPreferences = getSharedPreferences(Constants.USER_DATA, Context.MODE_PRIVATE);
                            editor = sharedPreferences.edit();
                            editor.putString(Constants.STATUS_LOGIN_USER, "login");
                            editor.apply();
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                        }
                    });
                    Log.d("responToken", token);
//                        Toast.makeText(LoginActivity.this, token, Toast.LENGTH_SHORT).show();
                });
    }


}
