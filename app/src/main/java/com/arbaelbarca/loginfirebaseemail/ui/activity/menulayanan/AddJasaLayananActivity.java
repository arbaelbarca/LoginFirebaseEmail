package com.arbaelbarca.loginfirebaseemail.ui.activity.menulayanan;

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

import com.arbaelbarca.loginfirebaseemail.R;
import com.arbaelbarca.loginfirebaseemail.basedata.BaseActivity;
import com.arbaelbarca.loginfirebaseemail.model.ModelJasaLayanan;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;

public class AddJasaLayananActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.imgAddJasa)
    ImageView imgAddJasa;
    @BindView(R.id.txtNamaJasa)
    EditText txtNamaJasa;
    @BindView(R.id.txtKatJasa)
    EditText txtKatJasa;
    @BindView(R.id.txtDescJasa)
    EditText txtDescJasa;
    @BindView(R.id.btnAddJasa)
    Button btnAddJasa;
    Uri imageUri, imageCamera;
    StorageReference storageReference;
    StorageTask task;

    FirebaseFirestore firebaseFirestore;
    InputStream imageStream;
    @BindView(R.id.txtNumberJasa)
    EditText txtNumberJasa;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_jasa_layanan);
        ButterKnife.bind(this);
        storageReference = FirebaseStorage.getInstance().getReference("image_jasa");
        firebaseFirestore = FirebaseFirestore.getInstance();

        initToolbar();
        init();
    }

    private void initToolbar() {
        setToolbar(toolbar, "Tambah Jasa Layanan");
    }

    private void init() {

        imgAddJasa.setOnClickListener(this);
        btnAddJasa.setOnClickListener(this);

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
                EasyImage.openCamera(AddJasaLayananActivity.this, 0);
            } else {
                EasyImage.openGallery(AddJasaLayananActivity.this, 0);
            }
        }).create().show();
    }

    void postJasa(final String textNama, final String textKategori, final String textDesc,
                  final String textHarga) {
        dialogLoading().show();
        final String uuid = UUID.randomUUID().toString();
        if (imageCamera != null) {
            final StorageReference referenceStore = storageReference.child(System.currentTimeMillis()
                    + "." + getFileExtention(imageCamera));

            task = referenceStore.putFile(imageCamera);
            task.continueWithTask((Continuation<UploadTask.TaskSnapshot, Task<Uri>>) task -> {
                if (!task.isSuccessful()) {
                    throw task.getException();


                }

                return referenceStore.getDownloadUrl();
            }).addOnCompleteListener((OnCompleteListener<Uri>) task -> {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    String mUri = downloadUri.toString();
                    ModelJasaLayanan modelJasaLayanan = new ModelJasaLayanan();
                    modelJasaLayanan.setImageJasa(mUri);
                    modelJasaLayanan.setTextNama(textNama);
                    modelJasaLayanan.setTextKategori(textKategori);
                    modelJasaLayanan.setTextDeskripsi(textDesc);
                    modelJasaLayanan.setTextHarga(textHarga);

                    firebaseFirestore.collection("Kategori_Jasa").document(uuid)
                            .set(modelJasaLayanan, SetOptions.merge()).addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()) {
                            dialogLoading().dismiss();
                            finish();
                            showToast("Sukses menambah jasa");
                        }
                    }).addOnFailureListener(e -> {
                        dialogLoading().dismiss();
                        showToast("Gagal menambah jasa");
                    });
                } else {
                    dialogLoading().dismiss();

                }
            }).addOnFailureListener(e -> {
                dialogLoading().dismiss();
                showToast("Terjadi kesalahan pada server");
            });


        } else {
            showToast("Belum ada image yg di pilih");
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
                    String photoPath = imageFile.getAbsolutePath();
                    imageCamera = Uri.fromFile(imageFile);
                    try {
                        imageStream = getContentResolver().openInputStream(imageCamera);
                        final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                        imgAddJasa.setImageBitmap(selectedImage);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }

                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v == imgAddJasa) {
            changeUpload();
        } else if (v == btnAddJasa) {
            String getTextNama = txtNamaJasa.getText().toString();
            String getKat = txtKatJasa.getText().toString();
            String getDesc = txtDescJasa.getText().toString();
            String getHarga = txtNumberJasa.getText().toString();


            if (getTextNama.isEmpty() || getKat.isEmpty() || getDesc.isEmpty()) {
                showToast("Form tidak boleh kosong");
            } else {
                postJasa(getTextNama, getKat, getDesc, getHarga);
            }

        }
    }
}
