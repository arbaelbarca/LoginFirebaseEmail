package com.arbaelbarca.loginfirebaseemail.ui.Activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.arbaelbarca.loginfirebaseemail.R;
import com.arbaelbarca.loginfirebaseemail.model.ModelUser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ChangePassword extends AppCompatActivity implements View.OnClickListener {

    String getEmail, getPass;
    FirebaseUser user;
    ProgressDialog dialog;
    AuthCredential credential;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.txtPassLama)
    EditText txtPassLama;
    @BindView(R.id.etPassLama)
    TextInputLayout etPassLama;
    @BindView(R.id.txtPassBaru)
    EditText txtPassBaru;
    @BindView(R.id.ti_password)
    TextInputLayout tiPassword;
    @BindView(R.id.btnKirimSandi)
    Button btnKirimSandi;
    ModelUser modelUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        ButterKnife.bind(this);

        dialog = new ProgressDialog(this);
        user = FirebaseAuth.getInstance().getCurrentUser();
        modelUser = (ModelUser) getIntent().getSerializableExtra("data");
        btnKirimSandi.setOnClickListener(this);
    }

    private void changePassword(final String sandiBaru) {
        dialog.setMessage("Loading");
        dialog.show();
        credential = EmailAuthProvider.getCredential(modelUser.getEmail(), modelUser.getPass());
        user.reauthenticate(credential)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            user.updatePassword(sandiBaru)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if (!task.isSuccessful()) {
                                                Toast.makeText(getApplicationContext(), "Terjadi kesalahan, silahkan coba lagi", Toast.LENGTH_LONG).show();
                                                dialog.dismiss();
                                            } else {
                                                Toast.makeText(getApplicationContext(), "Success ganti password baru", Toast.LENGTH_LONG).show();
                                                dialog.dismiss();
                                                pushPassBaru(sandiBaru);
                                                finish();
                                            }
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d("responFailureSandi", "s " + e.getMessage());
                                    alerDialog("Password baru minimal 6 character");
                                    dialog.dismiss();
                                }
                            });
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                dialog.dismiss();
                Log.d("responFailurePass", "s " + e.getMessage());
                alerDialog("Mohon maaf password yang lama tidak sesuai");
            }
        });
    }

    void alerDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ChangePassword.this);
        builder.setMessage(message);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

            }
        }).create().show();
    }

    void pushPassBaru(final String sandiBaru) {
        FirebaseDatabase.getInstance().getReference("Users").child(user.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        dataSnapshot.getRef().child("pass").setValue(sandiBaru);
                        dialog.dismiss();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.d("responDBEror", "e " + databaseError.getMessage());
                    }
                });

    }

    @Override
    public void onClick(View view) {
        if (view == btnKirimSandi) {
            String getPassLama = txtPassLama.getText().toString().trim();
            String getPassBaru = txtPassBaru.getText().toString().trim();

            if (getPassLama.isEmpty()) {
                alerDialog("Password lama tidak boleh kosong");

            } else if (getPassBaru.isEmpty()) {
                alerDialog("Password baru tidak boleh kosong");
            } else {
                changePassword(getPassBaru);
            }

        }
    }
}
