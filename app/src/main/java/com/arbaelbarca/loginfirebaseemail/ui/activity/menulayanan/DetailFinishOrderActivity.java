package com.arbaelbarca.loginfirebaseemail.ui.activity.menulayanan;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.arbaelbarca.loginfirebaseemail.Constants;
import com.arbaelbarca.loginfirebaseemail.R;
import com.arbaelbarca.loginfirebaseemail.basedata.BaseActivity;
import com.arbaelbarca.loginfirebaseemail.model.ModelJasaLayanan;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class DetailFinishOrderActivity extends BaseActivity {

    @BindView(R.id.imgTukang)
    CircleImageView imgTukang;
    @BindView(R.id.txtNamaTukang)
    TextView txtNamaTukang;
    @BindView(R.id.txtPlatTukang)
    TextView txtPlatTukang;
    @BindView(R.id.txtLokasiOrderToko)
    TextView txtLokasiOrderToko;
    @BindView(R.id.txtLokasiOrderUser)
    TextView txtLokasiOrderUser;
    @BindView(R.id.ratingUlasan)
    RatingBar ratingUlasan;
    @BindView(R.id.imgHistoriesOrder)
    ImageView imgHistoriesOrder;
    @BindView(R.id.rlImage)
    RelativeLayout rlImage;
    @BindView(R.id.txtNamaHistoriesJasa)
    TextView txtNamaHistoriesJasa;
    @BindView(R.id.txtHargaHistoriesJasa)
    TextView txtHargaHistoriesJasa;
    @BindView(R.id.txtKatHistoriesJasa)
    TextView txtKatHistoriesJasa;
    @BindView(R.id.txtStatusHistoriesJasa)
    TextView txtStatusHistoriesJasa;
    @BindView(R.id.txtTotalOngkir)
    TextView txtTotalOngkir;
    @BindView(R.id.txtTotalPembayaran)
    TextView txtTotalPembayaran;
    @BindView(R.id.txtPayment)
    TextView txtPayment;

    ModelJasaLayanan modelJasaLayanan;
    @BindView(R.id.txtGetUlasan)
    TextView txtGetUlasan;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_finish_order);
        ButterKnife.bind(this);

        iniToolbar();
        iniIntent();
        init();
    }

    private void iniToolbar() {
        setToolbar(toolbar, "Detail Pesanan");
    }

    private void iniIntent() {
        modelJasaLayanan = getIntent().getParcelableExtra(Constants.DATA_LAYANAN);
    }

    private void init() {
        if (modelJasaLayanan != null) {

            showGlideUser(modelJasaLayanan.getImagePemesan(), imgTukang);

            txtNamaTukang.setText(modelJasaLayanan.getNamaPemesan());
            txtPlatTukang.setText(modelJasaLayanan.getGetPlatTukang());

            txtLokasiOrderToko.setText(modelJasaLayanan.getTextAlamatToko());
            txtLokasiOrderUser.setText(modelJasaLayanan.getTextAlamatPemesan());

            ratingUlasan.setRating(Float.parseFloat(modelJasaLayanan.getRatingUlasan()));
            txtGetUlasan.setText(modelJasaLayanan.getTextUlasan());

            txtNamaHistoriesJasa.setText(modelJasaLayanan.getTextNama());
            txtHargaHistoriesJasa.setText(modelJasaLayanan.getTextHarga());
            txtKatHistoriesJasa.setText(modelJasaLayanan.getTextKategori());


            showGlideUser(modelJasaLayanan.getImageJasa(), imgHistoriesOrder);


            txtTotalOngkir.setText(modelJasaLayanan.getTextOngkir());
            txtTotalPembayaran.setText(modelJasaLayanan.getTextTotalPembayaran());
            txtPayment.setText(modelJasaLayanan.getPayment());
        }
    }
}
