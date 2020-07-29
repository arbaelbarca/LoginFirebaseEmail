package com.arbaelbarca.loginfirebaseemail.ui.activity.topup;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.arbaelbarca.loginfirebaseemail.MainActivity;
import com.arbaelbarca.loginfirebaseemail.R;
import com.arbaelbarca.loginfirebaseemail.Utils.ConvertToRupiah;
import com.arbaelbarca.loginfirebaseemail.basedata.BaseActivity;
import com.arbaelbarca.loginfirebaseemail.model.modeltopup.ModelTopup;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask.TaskSnapshot;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;

public class TopupSaldoActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.imgAddStruk)
    ImageView imgAddStruk;
    @BindView(R.id.txtGetSaldoku)
    TextView txtGetSaldoku;
    @BindView(R.id.edInputNominal)
    EditText edInputNominal;
    @BindView(R.id.btnSendTopup)
    Button btnSendTopup;
    @BindView(R.id.txtNominalSaldoku)
    TextView txtNominalSaldoku;

    StorageReference storageReference;
    StorageTask task;
    FirebaseFirestore firebaseFirestore;
    Uri imageCamera;
    DatabaseReference databaseReference;
    InputStream imageStream;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topup_saldo);
        ButterKnife.bind(this);

        initToolbar();
        initial();
    }

    private void initToolbar() {
        setToolbar(toolbar, "Topup Saldo");
    }

    private void initial() {
        storageReference = FirebaseStorage.getInstance().getReference("image_upload_bukti");
        firebaseFirestore = FirebaseFirestore.getInstance();
        btnSendTopup.setOnClickListener(this);

        if (MainActivity.modelUser != null) {
            txtGetSaldoku.setText(MainActivity.modelUser.getNosaldoku() + " " + MainActivity.modelUser.getPhone());
            txtNominalSaldoku.setText(ConvertToRupiah.toRupiah("Rp ", MainActivity.modelUser.getSaldoku(), false));
        }

        imgAddStruk.setOnClickListener(this);
    }

    String getFileExtention(Uri uri) {
        ContentResolver resolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(resolver.getType(uri));
    }

    private void changeUpload() {
        final CharSequence[] sequence = new CharSequence[]{"Camera", "Galery"};
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Pilih Upload");
        dialog.setItems(sequence, (dialog1, which) -> {
            if (sequence[which].toString().equals("Camera")) {
                EasyImage.openCamera(TopupSaldoActivity.this, 0);
            } else {
                EasyImage.openGallery(TopupSaldoActivity.this, 0);
            }
        }).create().show();
    }


    @Override
    public void onClick(View v) {
        if (v == btnSendTopup) {
            String inputNominal = edInputNominal.getText().toString();
            if (inputNominal.equals("")) {
                showToast("Silahkan masukkan nominal topup");
            } else {
                sendNominalTopup(inputNominal);
            }
        } else if (v == imgAddStruk) {
            changeUpload();
        }

    }


    void sendNominalTopup(final String textNominal) {
        dialogLoading().show();
        final String uuid = UUID.randomUUID().toString();
        if (imageCamera != null) {
            final StorageReference referenceStore = storageReference.child(System.currentTimeMillis()
                    + "." + getFileExtention(imageCamera));

            task = referenceStore.putFile(imageCamera);
            task.continueWithTask((Continuation<TaskSnapshot, Task<Uri>>) task -> {
                if (!task.isSuccessful()) {
                    throw task.getException();

                }

                return referenceStore.getDownloadUrl();
            }).addOnCompleteListener((OnCompleteListener<Uri>) task -> {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    if (downloadUri != null) {
                        String mUri = downloadUri.toString();
                        databaseReference = FirebaseDatabase.getInstance()
                                .getReference("User_Topup");

                        String idKey = databaseReference.push().getKey();

                        ModelTopup modelTopup = new ModelTopup();
                        modelTopup.setImageBukti(mUri);
                        modelTopup.setNominalTopup(textNominal);
                        modelTopup.setIdUser(MainActivity.modelUser.getId());
                        modelTopup.setNamaUser(MainActivity.modelUser.getNama());
                        modelTopup.setStatusTopup("NonReceive");
                        modelTopup.setPhotoUser(MainActivity.modelUser.getImageUrl());
                        modelTopup.setPhoneUser(MainActivity.modelUser.getPhone());
                        modelTopup.setEmailUser(MainActivity.modelUser.getEmail());
                        modelTopup.setKeyId(idKey);
                        modelTopup.setSaldoku(MainActivity.modelUser.getSaldoku());

                        if (idKey != null) {
                            databaseReference.child(idKey).setValue(modelTopup)
                                    .addOnCompleteListener(task12 -> {
                                        if (task12.isSuccessful()) {
                                            dialogLoading().dismiss();
                                            finish();
                                            showToast("Sukses mengajukan topup");
                                        }
                                    }).addOnFailureListener(e -> dialogLoading().dismiss());
                        }
                    }


                } else {
                    dialogLoading().dismiss();

                }
            }).addOnFailureListener(e -> {
                dialogLoading().dismiss();
                showToast("Terjadi kesalahan pada server");
            });


        } else {
            dialogLoading().dismiss();
            showToast("Struk belum di upload");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        EasyImage.handleActivityResult(requestCode, resultCode, data, this, new DefaultCallback() {
            @Override
            public void onImagesPicked(@NonNull List<File> imageFiles, EasyImage.ImageSource source, int type) {
                File imageFile = imageFiles.get(0);
                if (imageFile != null) {
                    imageCamera = Uri.fromFile(imageFile);
                    try {
                        imageStream = getContentResolver().openInputStream(imageCamera);
                        final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                        imgAddStruk.setImageBitmap(selectedImage);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }

                }
            }
        });
    }
}
