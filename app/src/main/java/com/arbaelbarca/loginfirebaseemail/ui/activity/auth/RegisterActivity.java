package com.arbaelbarca.loginfirebaseemail.ui.activity.auth;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.arbaelbarca.loginfirebaseemail.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
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
    @BindView(R.id.txtNama)
    EditText txtNama;
    @BindView(R.id.txtPhone)
    EditText txtPhone;
    @BindView(R.id.rbMale)
    RadioButton rbMale;
    @BindView(R.id.rbFemale)
    RadioButton rbFemale;
    @BindView(R.id.rbGroup)
    RadioGroup rbGroup;
    RadioButton rbGroupButton;

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

        btnDaftar.setOnClickListener(view -> {
            String userName = txtUsername.getText().toString();
            String email = txtEmailReg.getText().toString();
            String password = txtPassReg.getText().toString();
            String nama = txtNama.getText().toString();
            String phone = txtPhone.getText().toString();

            int selectId = rbGroup.getCheckedRadioButtonId();
            rbGroupButton = findViewById(selectId);

            if (userName.isEmpty() || email.isEmpty() || password.isEmpty()) {

            } else {
                register(userName, email, password, nama, phone, rbGroupButton.getText().toString());
            }

        });
    }

    void register(final String username, final String email, final String password, final String getNama, final String getPhone, final String getGender) {

        progressDialog.show();
        progressDialog.setMessage("Loading");
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = auth.getCurrentUser();
                        assert user != null;
                        final String userId = user.getUid();
                        reference = FirebaseDatabase.getInstance().getReference("User_Hos").child(userId);

                        user.sendEmailVerification().addOnCompleteListener(task12 -> {
                            if (task12.isSuccessful()) {
                                HashMap<String, String> hashMap = new HashMap<>();
                                hashMap.put("id", userId);
                                hashMap.put("nama", getNama);
                                hashMap.put("phone", getPhone);
                                hashMap.put("username", username);
                                hashMap.put("imageUrl", "default");
                                hashMap.put("email", email);
                                hashMap.put("pass", password);
                                hashMap.put("gender", getGender);
                                hashMap.put("alamat", "");
                                hashMap.put("status", "null");
                                hashMap.put("saldoku", "0");
                                hashMap.put("nosaldoku", "3322");


                                reference.setValue(hashMap).addOnCompleteListener(task1 -> {
                                    if (task1.isSuccessful()) {
                                        new AlertDialog.Builder(RegisterActivity.this)
                                                .setCancelable(false)
                                                .setMessage("Silahkan cek email anda untuk verifikasi " + email)
                                                .setPositiveButton("Ok", (dialogInterface, i) -> {
                                                    dialogInterface.dismiss();
                                                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                                    startActivity(intent);
                                                    finish();

                                                }).show();
                                    }
                                });

                            }
                        });


                    } else {
                        Toast.makeText(getApplicationContext(), "gagal daftar", Toast.LENGTH_LONG).show();
                    }
                });
    }
}
