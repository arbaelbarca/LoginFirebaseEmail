package com.arbaelbarca.jadwalngajar.ui.Activity.LoginActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.arbaelbarca.jadwalngajar.Constants;
import com.arbaelbarca.jadwalngajar.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RegisterActivity extends AppCompatActivity {
    FirebaseAuth auth;
    ProgressDialog progressDialog;
    DatabaseReference reference;
    @BindView(R.id.txtUsername)
    EditText txtUsername;
    @BindView(R.id.txtEmailReg)
    EditText txtEmailReg;
    @BindView(R.id.txtPassReg)
    EditText txtPassReg;
    @BindView(R.id.btnDaftar)
    Button btnDaftar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);

        initial();
    }

    private void initial() {
        progressDialog = new ProgressDialog(this);
        auth = FirebaseAuth.getInstance();

        btnDaftar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userName = txtUsername.getText().toString();
                String email = txtEmailReg.getText().toString();
                String password = txtPassReg.getText().toString();

                if (userName.isEmpty() || email.isEmpty() || password.isEmpty()) {

                } else {
                    register(userName, email, password);
                }

            }
        });
    }

    void register(final String username, final String email, final String password) {

        progressDialog.show();
        progressDialog.setMessage("Loading");
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = auth.getCurrentUser();
                            assert user != null;
                            final String userId = user.getUid();
                            reference = FirebaseDatabase.getInstance(Constants.URL_FIREBASE).getReference("User").child(userId);

                            user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        HashMap<String, String> hashMap = new HashMap<>();
                                        hashMap.put("id", userId);
                                        hashMap.put("username", username);
                                        hashMap.put("imageUrl", "default");
                                        hashMap.put("email", email);
                                        hashMap.put("pass", password);


                                        reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    new AlertDialog.Builder(RegisterActivity.this)
                                                            .setCancelable(false)
                                                            .setMessage("Silahkan cek email anda untuk verifikasi " + email)
                                                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                                @Override
                                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                                    dialogInterface.dismiss();
                                                                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                                                    startActivity(intent);
                                                                    finish();

                                                                }
                                                            }).show();
                                                }
                                            }
                                        });

                                    }
                                }
                            });


                        } else {
                            Toast.makeText(getApplicationContext(), "gagal daftar", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}
