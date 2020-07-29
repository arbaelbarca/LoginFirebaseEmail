package com.arbaelbarca.loginfirebaseemail.ui.activity.topup;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.arbaelbarca.loginfirebaseemail.MainActivity;
import com.arbaelbarca.loginfirebaseemail.R;
import com.arbaelbarca.loginfirebaseemail.Utils.ConvertToRupiah;
import com.arbaelbarca.loginfirebaseemail.basedata.BaseActivity;
import com.arbaelbarca.loginfirebaseemail.model.modeltopup.ModelTopup;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.arbaelbarca.loginfirebaseemail.Constants.DATA_TOPUP_USER;

public class DetailTopupSaldoActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.btnCancelTopup)
    Button btnCancelTopup;
    @BindView(R.id.btnTerimaTopup)
    Button btnTerimaTopup;
    @BindView(R.id.imgUserDetailTopup)
    CircleImageView imgUserDetailTopup;
    @BindView(R.id.txtNamaUserDetailTopup)
    TextView txtNamaUserDetailTopup;
    @BindView(R.id.txtPhoneDetailTopup)
    TextView txtPhoneDetailTopup;
    @BindView(R.id.txtEmailDetailTopup)
    TextView txtEmailDetailTopup;
    @BindView(R.id.imgBuktiStrukDetail)
    ImageView imgBuktiStrukDetail;
    @BindView(R.id.txtGetSaldoku)
    TextView txtGetSaldoku;
    @BindView(R.id.txtNominalSaldoku)
    TextView txtNominalSaldoku;
    @BindView(R.id.edInputNominal)
    TextView edInputNominal;

    ModelTopup modelTopup;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    DatabaseReference databaseReferenceUser, databaseReferenceTopup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_topup_saldo);
        ButterKnife.bind(this);

        initIntent();
        iniToolbar();
        init();

    }

    private void iniToolbar() {
        setToolbar(toolbar, "Detail Pengajuan");
    }

    private void initIntent() {
        modelTopup = getIntent().getParcelableExtra(DATA_TOPUP_USER);
    }

    private void init() {
        if (modelTopup != null) {
            showGlideUser(modelTopup.getImageBukti(), imgBuktiStrukDetail);

            txtGetSaldoku.setText(modelTopup.getNominalTopup());
            edInputNominal.setText(modelTopup.getNamaUser() + " meminta pengajuan dengan saldo " +
                    ConvertToRupiah.toRupiah("", modelTopup.getNominalTopup(), false));

            showGlideUser(modelTopup.getPhotoUser(), imgUserDetailTopup);

            txtNamaUserDetailTopup.setText(modelTopup.getNamaUser());
            txtEmailDetailTopup.setText(modelTopup.getEmailUser());
            txtPhoneDetailTopup.setText(modelTopup.getPhoneUser());


        }

        btnTerimaTopup.setOnClickListener(this);
        btnCancelTopup.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == btnTerimaTopup) {
            acceptTopup();
        } else if (v == btnCancelTopup) {
            cancelTopup();
        }
    }

    private void cancelTopup() {
        dialogLoading().show();
        databaseReferenceTopup = FirebaseDatabase.getInstance().getReference("User_Topup")
                .child(modelTopup.getKeyId());


        databaseReferenceUser = FirebaseDatabase.getInstance().getReference("User_Hos")
                .child(modelTopup.getIdUser());


        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("statusTopup", "NonReceive");


        int totalSaldoku = Integer.parseInt(modelTopup.getNominalTopup()) - Integer.parseInt(modelTopup.getSaldoku());

        HashMap<String, Object> hashMapUser = new HashMap<>();
        hashMapUser.put("saldoku", String.valueOf(totalSaldoku));

        databaseReferenceTopup.updateChildren(hashMap)
                .addOnCompleteListener(task12 -> {
                    dialogLoading().dismiss();

                    databaseReferenceUser.updateChildren(hashMapUser)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    dialogLoading().dismiss();
                                    showToast("Dibatalkan");
                                    finish();
                                }
                            }).addOnFailureListener(e -> dialogLoading().dismiss());

                }).addOnFailureListener(e -> dialogLoading().dismiss());


    }

    private void acceptTopup() {
        dialogLoading().show();

        databaseReferenceTopup = FirebaseDatabase.getInstance().getReference("User_Topup")
                .child(modelTopup.getKeyId());

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("statusTopup", "Received");

        databaseReferenceUser = FirebaseDatabase.getInstance().getReference("User_Hos")
                .child(modelTopup.getIdUser());

        int totalSaldoku = Integer.parseInt(modelTopup.getNominalTopup()) + Integer.parseInt(modelTopup.getSaldoku());

        HashMap<String, Object> hashMapUser = new HashMap<>();
        hashMapUser.put("saldoku", String.valueOf(totalSaldoku));

        databaseReferenceTopup.updateChildren(hashMap)
                .addOnCompleteListener(task12 -> {
                    dialogLoading().dismiss();
                    databaseReferenceUser.updateChildren(hashMapUser)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    dialogLoading().dismiss();
                                    finish();
                                    showToast("Sukses menerima topup");
                                }
                            }).addOnFailureListener(e -> dialogLoading().dismiss());
                }).addOnFailureListener(e -> dialogLoading().dismiss());


    }
}
