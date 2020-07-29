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

import com.arbaelbarca.loginfirebaseemail.R;
import com.arbaelbarca.loginfirebaseemail.adapter.AdapterListTopupUser;
import com.arbaelbarca.loginfirebaseemail.model.modeltopup.ModelTopup;
import com.arbaelbarca.loginfirebaseemail.onclick.OnClickItem;
import com.arbaelbarca.loginfirebaseemail.ui.activity.topup.DetailTopupSaldoActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.arbaelbarca.loginfirebaseemail.Constants.DATA_TOPUP_USER;

/**
 * A simple {@link Fragment} subclass.
 */
public class ListUserTopupFragment extends Fragment {


    @BindView(R.id.rvListTopupUser)
    RecyclerView rvListTopupUser;
    Unbinder unbinder;
    AdapterListTopupUser adapterListTopupUser;
    ModelTopup modelTopup;
    List<ModelTopup> modelTopupList = new ArrayList<>();

    OnClickItem onClickItem = new OnClickItem() {
        @Override
        public void clickItemSelesai(int pos) {

        }

        @Override
        public void clickItemAktif(int pos) {
            ModelTopup topup = modelTopupList.get(pos);
            Intent intent = new Intent(getActivity(), DetailTopupSaldoActivity.class);
            intent.putExtra(DATA_TOPUP_USER, topup);
            startActivity(intent);
        }
    };

    public ListUserTopupFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_list_user_topup, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initRv();
        initial();
    }

    private void initRv() {
        adapterListTopupUser = new AdapterListTopupUser(getActivity());
        rvListTopupUser.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvListTopupUser.setHasFixedSize(true);
        rvListTopupUser.setAdapter(adapterListTopupUser);
        adapterListTopupUser.setOnClickItem(onClickItem);

    }

    private void initial() {
        FirebaseDatabase.getInstance()
                .getReference("User_Topup")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            modelTopup = snapshot.getValue(ModelTopup.class);
                            modelTopupList.add(modelTopup);
                            adapterListTopupUser.setModelTopupList(modelTopupList);
                            adapterListTopupUser.notifyDataSetChanged();

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        databaseError.getMessage();
                    }
                });
    }

}
