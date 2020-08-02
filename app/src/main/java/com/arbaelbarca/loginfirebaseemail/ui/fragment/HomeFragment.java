package com.arbaelbarca.loginfirebaseemail.ui.fragment;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.arbaelbarca.loginfirebaseemail.Constants;
import com.arbaelbarca.loginfirebaseemail.MainActivity;
import com.arbaelbarca.loginfirebaseemail.R;
import com.arbaelbarca.loginfirebaseemail.Utils.ConvertToRupiah;
import com.arbaelbarca.loginfirebaseemail.adapter.AdapterMenuLayanan;
import com.arbaelbarca.loginfirebaseemail.basedata.BaseFragment;
import com.arbaelbarca.loginfirebaseemail.model.ModelJasaLayanan;
import com.arbaelbarca.loginfirebaseemail.onclick.OnClickItem;
import com.arbaelbarca.loginfirebaseemail.ui.activity.menulayanan.AddJasaLayananActivity;
import com.arbaelbarca.loginfirebaseemail.ui.activity.menulayanan.DetailJasaLayananActivity;
import com.arbaelbarca.loginfirebaseemail.ui.activity.menulayanan.EditJasaLayananActivity;
import com.arbaelbarca.loginfirebaseemail.ui.activity.topup.TopupSaldoActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends BaseFragment implements View.OnClickListener {


    @BindView(R.id.btnAddJasa)
    FloatingActionButton btnAddJasa;
    Unbinder unbinder;
    @BindView(R.id.rvListJasa)
    RecyclerView rvListJasa;

    ArrayList<ModelJasaLayanan> modelJasaLayananArrayList = new ArrayList<>();
    AdapterMenuLayanan adapterMenuLayanan;
    FirebaseFirestore firebaseFirestore;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    OnClickItem onClickItem = new OnClickItem() {
        @Override
        public void clickItemSelesai(int pos) {

        }

        @Override
        public void clickItemAktif(int pos) {
            if (MainActivity.modelUser.getStatus().equals("admin")) {
                ModelJasaLayanan modelJasaLayanan = modelJasaLayananArrayList.get(pos);
                Intent intent = new Intent(getActivity(), EditJasaLayananActivity.class);
                intent.putExtra(Constants.DATA_LAYANAN, modelJasaLayanan);
                startActivity(intent);
            } else {
                ModelJasaLayanan modelJasaLayanan = modelJasaLayananArrayList.get(pos);
                Intent intent = new Intent(getActivity(), DetailJasaLayananActivity.class);
                intent.putExtra(Constants.DATA_LAYANAN, modelJasaLayanan);
                startActivity(intent);
            }

        }
    };

    @BindView(R.id.txtMySaldo)
    TextView txtMySaldo;
    @BindView(R.id.txtNoSaldo)
    TextView txtNoSaldo;
    @BindView(R.id.btnTopup)
    Button btnTopup;

    FirebaseUser firebaseUser;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();
        init();
        initAdapter();
    }

    private void initAdapter() {
        adapterMenuLayanan = new AdapterMenuLayanan(getActivity());
        rvListJasa.setAdapter(adapterMenuLayanan);
        rvListJasa.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        rvListJasa.setHasFixedSize(true);

        setList();

        adapterMenuLayanan.setOnClickItem(onClickItem);

        btnTopup.setOnClickListener(this);


    }

    @Override
    public void onResume() {
        super.onResume();
        validateStatusProfile();

    }

    @SuppressLint("SetTextI18n")
    private void validateStatusProfile() {
        if (MainActivity.modelUser != null) {
            if (MainActivity.modelUser.getStatus().equalsIgnoreCase("admin")) {
                btnAddJasa.setVisibility(View.VISIBLE);
            } else {
                btnAddJasa.setVisibility(View.GONE);
            }

            txtMySaldo.setText(ConvertToRupiah.toRupiah("Rp ", MainActivity.modelUser.getSaldoku(), false));
            txtNoSaldo.setText("No Saldoku : " + MainActivity.modelUser.getNosaldoku() + " " + MainActivity.modelUser.getPhone());
        }


    }

    private void setList() {
        progressBar.setVisibility(View.VISIBLE);
        firebaseFirestore.collection("Kategori_Jasa")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful())
                        if (task.getResult() != null)
                            for (DocumentSnapshot snapshot : task.getResult()) {
                                ModelJasaLayanan modelJasaLayanan = snapshot.toObject(ModelJasaLayanan.class);
                                modelJasaLayananArrayList.add(modelJasaLayanan);

                            }

                    adapterMenuLayanan.setLayananArrayList(modelJasaLayananArrayList);
                    adapterMenuLayanan.notifyDataSetChanged();
                    progressBar.setVisibility(View.GONE);
                }).addOnFailureListener(e -> showToast("Failed load data"));

    }

    private void init() {
        btnAddJasa.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == btnAddJasa) {
            startActivity(new Intent(getActivity(), AddJasaLayananActivity.class));
        } else if (v == btnTopup) {
            if (firebaseUser.isEmailVerified())
                startActivity(new Intent(getActivity(), TopupSaldoActivity.class));
            else
                showToast("Akun anda belum verifikasi");
        }
    }


}
