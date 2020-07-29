package com.arbaelbarca.loginfirebaseemail.ui.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.arbaelbarca.loginfirebaseemail.R;
import com.arbaelbarca.loginfirebaseemail.basedata.BaseFragment;
import com.arbaelbarca.loginfirebaseemail.model.ModelUser;
import com.arbaelbarca.loginfirebaseemail.ui.activity.auth.ChangePassword;
import com.arbaelbarca.loginfirebaseemail.ui.activity.profile.EditProfileActivity;
import com.arbaelbarca.loginfirebaseemail.ui.activity.auth.LoginActivity;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentProfile extends BaseFragment {

    FirebaseUser user;
    @BindView(R.id.btnLoginAkun)
    Button btnLoginAkun;
    @BindView(R.id.llNotLogin)
    LinearLayout llNotLogin;
    @BindView(R.id.img_avatar)
    CircleImageView imgAvatar;
    @BindView(R.id.txtUsername)
    TextView txtUsername;
    @BindView(R.id.txtEmailAkun)
    TextView txtEmailAkun;
    @BindView(R.id.llUbahProfile)
    LinearLayout llUbahProfile;
    @BindView(R.id.llUbahKataSandi)
    LinearLayout llUbahKataSandi;
    @BindView(R.id.llLogout)
    LinearLayout llLogout;
    @BindView(R.id.llLogin)
    LinearLayout llLogin;
    Unbinder unbinder;
    ModelUser modelUser;
    @BindView(R.id.imgVerify)
    ImageView imgVerify;

    FirebaseAuth firebaseAuth;
    DatabaseReference reference;

    public FragmentProfile() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_fragment_profile, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        user = FirebaseAuth.getInstance().getCurrentUser();
        firebaseAuth = FirebaseAuth.getInstance();
        if (user != null) {
            getDataProfile();
            llLogin.setVisibility(View.VISIBLE);
        } else {
            llNotLogin.setVisibility(View.VISIBLE);
            btnLoginAkun.setOnClickListener(view1 -> startActivity(new Intent(getActivity(), LoginActivity.class)));
        }

        llLogout.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

            builder.setMessage("Apakah anda yakin ingin keluar !!");
            builder.setPositiveButton("Ya", (dialog, which) -> logout()).setNegativeButton("Tidak", (dialog, which) -> dialog.dismiss());
            builder.create();
            builder.show();
        });

    }

    void logout() {
        dialogLoading().show();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("token_id", "null");
        reference = FirebaseDatabase.getInstance().getReference("User_Hos").child(user.getUid())
                .child("Notification");
        reference.updateChildren(hashMap).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (firebaseAuth != null) {
                    FirebaseAuth.getInstance().signOut();
                    startActivity(new Intent(getActivity(), LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                    getActivity().finish();
                    dialogLoading().dismiss();
                }
            }
        });


    }


    @Override
    public void onResume() {
        super.onResume();
        checkEmailValidation();
    }

    void checkEmailValidation() {
        if (user.isEmailVerified()) {
            imgVerify.setVisibility(View.VISIBLE);
        } else {
            imgVerify.setVisibility(View.GONE);
        }
    }


    private void getDataProfile() {
        FirebaseDatabase.getInstance().getReference("User_Hos").child(user.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        modelUser = dataSnapshot.getValue(ModelUser.class);
                        txtEmailAkun.setText(modelUser.getEmail());
                        txtUsername.setText(modelUser.getUsername());

                        if (modelUser.getImageUrl().equals("default")) {
                            imgAvatar.setImageResource(R.drawable.ic_user_no_photo);
                        } else {
                            showGlideUser(modelUser.getImageUrl(), imgAvatar);

                        }

                        llUbahProfile.setOnClickListener(view -> {
                            Intent intent = new Intent(getActivity(), EditProfileActivity.class);
                            intent.putExtra("data", modelUser);
                            startActivity(intent);
                        });

                        llUbahKataSandi.setOnClickListener(view -> {
                            Intent intent = new Intent(getActivity(), ChangePassword.class);
                            intent.putExtra("data", modelUser);
                            startActivity(intent);
                        });

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

}
