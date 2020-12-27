package com.arbaelbarca.jadwalngajar.ui.Activity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.arbaelbarca.jadwalngajar.R;
import com.arbaelbarca.jadwalngajar.model.ModelUser;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;

public class EditProfileActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.iv_avatar)
    CircleImageView ivAvatar;
    @BindView(R.id.circleUpload)
    RelativeLayout circleUpload;
    @BindView(R.id.et_name)
    EditText etName;
    @BindView(R.id.et_phone)
    EditText etPhone;
    @BindView(R.id.et_email)
    EditText etEmail;
    @BindView(R.id.et_alamat)
    EditText etAlamat;
    @BindView(R.id.tv_save)
    TextView tvSave;
    @BindView(R.id.ll_save)
    RelativeLayout llSave;

    ModelUser modelUser;
    ProgressDialog dialog;
    Uri imageUri, imageCamera;
    StorageReference storageReference;
    StorageTask task;
    StorageReference referenceStore;
    Bitmap thumbnail;
    DatabaseReference reference;
    FirebaseUser user;
    String logBase64;
    InputStream imageStream;
    String stringPath;
    File file;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        ButterKnife.bind(this);

        initial();
    }

    private void initial() {
        user = FirebaseAuth.getInstance().getCurrentUser();
        modelUser = new ModelUser();
        modelUser = (ModelUser) getIntent().getSerializableExtra("data");
        storageReference = FirebaseStorage.getInstance().getReference("imageProfile");

        getProfile();

        circleUpload.setOnClickListener(this);
    }

    private void getProfile() {

        etName.setText(modelUser.getUsername());
        etEmail.setText(modelUser.getEmail());
        etPhone.setText(modelUser.getNomornip());
        etAlamat.setText(modelUser.getAlamat());

        llSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String txtName = etName.getText().toString();
                String txtPhone = etPhone.getText().toString();
                String txtAlamat = etAlamat.getText().toString();
                String txtEmail = etEmail.getText().toString();

                if (txtName.isEmpty() || txtPhone.isEmpty() || txtAlamat.isEmpty() || txtEmail.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Data tidak boleh kosong", Toast.LENGTH_LONG).show();
                } else {
                    uploadData(txtName, txtPhone, txtAlamat, txtEmail);
                }

            }
        });
    }


    @Override
    public void onClick(View view) {
        if (view == circleUpload) {
            changeUpload();
        }
    }

    private void changeUpload() {
        final CharSequence sequence[] = new CharSequence[]{"Camera", "Galery"};
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Pilih Upload");
        dialog.setItems(sequence, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (sequence[which].toString().equals("Camera")) {
                    EasyImage.openCamera(EditProfileActivity.this, 0);
                } else {
                    EasyImage.openGallery(EditProfileActivity.this, 0);
                }
            }
        }).create().show();
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
                        ivAvatar.setImageBitmap(selectedImage);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }

                }
            }
        });
    }

    String getFileExtention(Uri uri) {
        ContentResolver resolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(resolver.getType(uri));
    }

    void uploadData(final String name, final String phone, final String alamat, final String email) {
        dialog = new ProgressDialog(this);
        dialog.setMessage("Loading");
        dialog.show();
        dialog.setCancelable(false);
        if (imageCamera != null) {
            final StorageReference referenceStore = storageReference.child(System.currentTimeMillis()
                    + "." + getFileExtention(imageCamera));

            task = referenceStore.putFile(imageCamera);
            task.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();


                    }

                    return referenceStore.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        final String mUri = downloadUri.toString();

                        reference = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid());
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("imageUrl", mUri);
                        reference.updateChildren(hashMap);
                        dialog.dismiss();
                        finish();
                    } else {
                        dialog.dismiss();
                        Toast.makeText(getApplicationContext(), "Gagal Upload", Toast.LENGTH_LONG).show();

                    }
                }

            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();

                }
            });
        } else {
            dialog.show();
            dialog.setCancelable(false);
            reference = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid());
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("email", email);
            hashMap.put("username", name);
            hashMap.put("nomornip", phone);
            hashMap.put("alamat", alamat);
            reference.updateChildren(hashMap);
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    dialog.dismiss();
                    finish();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }
}
