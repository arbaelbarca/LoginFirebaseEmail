package com.arbaelbarca.loginfirebaseemail.ui.activity.profile;

import android.app.ProgressDialog;
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
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.arbaelbarca.loginfirebaseemail.R;
import com.arbaelbarca.loginfirebaseemail.basedata.BaseActivity;
import com.arbaelbarca.loginfirebaseemail.model.ModelUser;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
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

public class EditProfileActivity extends BaseActivity implements View.OnClickListener {

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
    @BindView(R.id.rbMale)
    RadioButton rbMale;
    @BindView(R.id.rbFemale)
    RadioButton rbFemale;
    @BindView(R.id.rbGroup)
    RadioGroup rbGroup;
    RadioButton rbGroupButton;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        ButterKnife.bind(this);

        initToolbar();
        initial();
    }

    private void initToolbar() {
        setToolbar(toolbar, "Edit Profile");
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
        etPhone.setText(modelUser.getPhone());
        etAlamat.setText(modelUser.getAlamat());

        if (modelUser.getGender().equals("Laki - laki")) {
            rbMale.setChecked(true);
        } else {
            rbFemale.setChecked(false);
        }

        llSave.setOnClickListener(view -> {
            String txtName = etName.getText().toString();
            String txtPhone = etPhone.getText().toString();
            String txtAlamat = etAlamat.getText().toString();
            String txtEmail = etEmail.getText().toString();
            int selectId = rbGroup.getCheckedRadioButtonId();
            rbGroupButton = findViewById(selectId);

            if (txtName.isEmpty() || txtPhone.isEmpty() || txtAlamat.isEmpty() || txtEmail.isEmpty()) {
                Toast.makeText(getApplicationContext(), "Data tidak boleh kosong", Toast.LENGTH_LONG).show();
            } else {
                uploadData(txtName, txtPhone, txtAlamat, txtEmail, txtName, rbGroupButton.getText().toString());
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
        dialog.setItems(sequence, (dialog1, which) -> {
            if (sequence[which].toString().equals("Camera")) {
                EasyImage.openCamera(EditProfileActivity.this, 0);
            } else {
                EasyImage.openGallery(EditProfileActivity.this, 0);
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

    void uploadData(final String username, final String phone, final String alamat, final String email,
                    final String namaFull, String gender) {
        dialog = new ProgressDialog(this);
        dialog.setMessage("Loading");
        dialog.show();
        dialog.setCancelable(false);
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
                    final String mUri = downloadUri.toString();

                    reference = FirebaseDatabase.getInstance().getReference("User_Hos").child(user.getUid());
                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("imageUrl", mUri);
                    reference.updateChildren(hashMap);
                    dialog.dismiss();
                    finish();
                } else {
                    dialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Gagal Upload", Toast.LENGTH_LONG).show();

                }
            }).addOnFailureListener(e -> Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show());
        } else {
            dialog.show();
            dialog.setCancelable(false);
            reference = FirebaseDatabase.getInstance().getReference("User_Hos").child(user.getUid());
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("email", email);
            hashMap.put("username", username);
            hashMap.put("phone", phone);
            hashMap.put("alamat", alamat);
            hashMap.put("nama", namaFull);
            hashMap.put("gender", gender);


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
