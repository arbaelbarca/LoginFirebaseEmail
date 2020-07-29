package com.arbaelbarca.loginfirebaseemail.ui.fragment;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.arbaelbarca.loginfirebaseemail.R;
import com.arbaelbarca.loginfirebaseemail.model.ModelJasaLayanan;
import com.bumptech.glide.Glide;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
public class BSDDetailOrderFragment extends BottomSheetDialogFragment {


    @BindView(R.id.imgAddJasa)
    ImageView imgAddJasa;
    @BindView(R.id.txtNamaJasa)
    TextView txtNamaJasa;
    @BindView(R.id.txtHargaJasa)
    TextView txtHargaJasa;
    @BindView(R.id.txtKategoriJasa)
    TextView txtKategoriJasa;
    @BindView(R.id.txtDescJasa)
    TextView txtDescJasa;
    Unbinder unbinder;
    ModelJasaLayanan jasaLayanan;

    public static BSDDetailOrderFragment newInstance(ModelJasaLayanan modelJasaLayanan) {
        // Required empty public constructor
        BSDDetailOrderFragment detailOrderFragment = new BSDDetailOrderFragment();
        detailOrderFragment.jasaLayanan = modelJasaLayanan;
        return detailOrderFragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style.AppBottomSheetDialogTheme);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_bsddetail_order, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initial();
    }

    private void initial() {
        if (jasaLayanan != null)
        {
            Glide.with(Objects.requireNonNull(getActivity()))
                    .load(jasaLayanan.getImageJasa())
                    .into(imgAddJasa);



            txtNamaJasa.setText(jasaLayanan.getTextNama());
            txtHargaJasa.setText(jasaLayanan.getTextHarga());
            txtKategoriJasa.setText(jasaLayanan.getTextKategori());

        }
    }
}
