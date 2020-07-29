package com.arbaelbarca.loginfirebaseemail.ui.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.arbaelbarca.loginfirebaseemail.MainActivity;
import com.arbaelbarca.loginfirebaseemail.R;
import com.arbaelbarca.loginfirebaseemail.adapter.AdapterHistoryOrder;
import com.arbaelbarca.loginfirebaseemail.model.ModelJasaLayanan;
import com.arbaelbarca.loginfirebaseemail.onclick.OnClickItem;
import com.arbaelbarca.loginfirebaseemail.ui.activity.menulayanan.DetailFinishOrderActivity;
import com.arbaelbarca.loginfirebaseemail.ui.activity.menulayanan.DetailOrderLayananActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.arbaelbarca.loginfirebaseemail.Constants.DATA_LAYANAN;
import static com.arbaelbarca.loginfirebaseemail.Constants.NAME_COLLECTION_JASA;
import static com.arbaelbarca.loginfirebaseemail.Constants.UUID_ORDER;

/**
 * A simple {@link Fragment} subclass.
 */
public class HistoryOrderFragment extends Fragment implements OnClickItem {


    @BindView(R.id.rvListHistory)
    RecyclerView rvListHistory;
    Unbinder unbinder;
    FirebaseFirestore firebaseFirestore;
    FirebaseUser firebaseUser;
    ModelJasaLayanan modelJasaLayanan;
    AdapterHistoryOrder adapterHistoryOrder;
    ArrayList<ModelJasaLayanan> modelJasaLayananArrayList = new ArrayList<>();
    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    public HistoryOrderFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_history_order, container, false);
        ButterKnife.bind(this, view);

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        initial();

    }

    private void initRv() {
        adapterHistoryOrder = new AdapterHistoryOrder(getActivity());
        rvListHistory.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvListHistory.setHasFixedSize(true);
        rvListHistory.setAdapter(adapterHistoryOrder);
        adapterHistoryOrder.setOnClickItem(this);
    }

    private void initial() {
        initRv();
        getDataHistory();
    }

    void getDataHistory() {
        progressBar.setVisibility(View.VISIBLE);
        if (MainActivity.modelUser != null)
            if (MainActivity.modelUser.getStatus().equalsIgnoreCase("admin")) {
                firebaseFirestore.collection(NAME_COLLECTION_JASA)
                        .get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful())
                                for (DocumentSnapshot snapshot : Objects.requireNonNull(task.getResult())) {
                                    modelJasaLayanan = snapshot.toObject(ModelJasaLayanan.class);
                                    modelJasaLayananArrayList.add(modelJasaLayanan);
                                    adapterHistoryOrder.setModelJasaLayananArrayList(modelJasaLayananArrayList);
                                    adapterHistoryOrder.notifyDataSetChanged();
                                    progressBar.setVisibility(View.GONE);
                                }
                            else
                                progressBar.setVisibility(View.GONE);

                        }).addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);

                });
            } else {
                firebaseFirestore.collection(NAME_COLLECTION_JASA)
                        .whereEqualTo("idUser", firebaseUser.getUid())
                        .get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful())
                                for (DocumentSnapshot snapshot : Objects.requireNonNull(task.getResult())) {
                                    modelJasaLayanan = snapshot.toObject(ModelJasaLayanan.class);
                                    modelJasaLayananArrayList.add(modelJasaLayanan);
                                    adapterHistoryOrder.setModelJasaLayananArrayList(modelJasaLayananArrayList);
                                    adapterHistoryOrder.notifyDataSetChanged();
                                    progressBar.setVisibility(View.GONE);
                                }
                            else
                                progressBar.setVisibility(View.GONE);

                        }).addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);

                });
            }

    }


    @Override
    public void clickItemSelesai(int pos) {
        ModelJasaLayanan layanan = modelJasaLayananArrayList.get(pos);
        Intent intent = new Intent(getActivity(), DetailFinishOrderActivity.class);
        intent.putExtra(DATA_LAYANAN, layanan);
        startActivity(intent);
    }

    @Override
    public void clickItemAktif(int pos) {
        ModelJasaLayanan layanan = modelJasaLayananArrayList.get(pos);
        Intent intent = new Intent(getActivity(), DetailOrderLayananActivity.class);
        intent.putExtra(UUID_ORDER, layanan.getUuid());
        startActivity(intent);
    }
}
