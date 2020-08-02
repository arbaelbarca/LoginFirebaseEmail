package com.arbaelbarca.loginfirebaseemail.ui.activity.menulayanan;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.arbaelbarca.loginfirebaseemail.Constants;
import com.arbaelbarca.loginfirebaseemail.MainActivity;
import com.arbaelbarca.loginfirebaseemail.R;
import com.arbaelbarca.loginfirebaseemail.Utils.Directions.GetDirectionsData;
import com.arbaelbarca.loginfirebaseemail.basedata.BaseActivity;
import com.arbaelbarca.loginfirebaseemail.model.ModelJasaLayanan;
import com.arbaelbarca.loginfirebaseemail.services.LocationService;
import com.arbaelbarca.loginfirebaseemail.ui.fragment.BSDDetailLayananFragment;
import com.arbaelbarca.loginfirebaseemail.ui.fragment.BSDDetailOrderFragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.arbaelbarca.loginfirebaseemail.Constants.UUID_ORDER;

public class DetailOrderLayananActivity extends BaseActivity implements OnMapReadyCallback, View.OnClickListener {

    BSDDetailLayananFragment bsdDetailLayananFragment;
    ModelJasaLayanan modelJasaLayanan;
    GoogleMap map;
    SupportMapFragment supportMapFragment;
    BottomSheetBehavior bottomSheetBehavior;
    FirebaseFirestore firebaseFirestore;
    FirebaseUser firebaseUser;
    @BindView(R.id.frameBottomSheet)
    RelativeLayout rlBottomSheet;
    @BindView(R.id.txtLokasiOrderToko)
    TextView txtLokasiOrderToko;
    @BindView(R.id.txtLokasiOrderUser)
    TextView txtLokasiOrderUser;
    @BindView(R.id.imgTukang)
    CircleImageView imgTukang;
    @BindView(R.id.txtNamaTukang)
    TextView txtNamaTukang;
    @BindView(R.id.txtPlatTukang)
    TextView txtPlatTukang;
    String getUuid;
    @BindView(R.id.txtTotalOngkir)
    TextView txtTotalOngkir;
    @BindView(R.id.txtTotalPembayaran)
    TextView txtTotalPembayaran;
    @BindView(R.id.txtTitleOrder)
    TextView txtTitleOrder;
    @BindView(R.id.txtViewDetailPesanan)
    TextView txtViewDetailPesanan;
    LocationBroadcastReceiver receiver;

    Query documentReference;
    @BindView(R.id.txtTitleTukang)
    TextView txtTitleTukang;
    @BindView(R.id.imgTelp)
    ImageView imgTelp;
    @BindView(R.id.txtCancelOrder)
    Button txtCancelOrder;
    @BindView(R.id.txtTerimaOrder)
    Button txtTerimaOrder;
    @BindView(R.id.txtPayment)
    TextView txtPayment;
    Dialog dialogCheckOrder;
    String ratingtext;
    @BindView(R.id.edAddNoteds)
    EditText edAddNoteds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_order_layanan);
        ButterKnife.bind(this);

//        modelJasaLayanan = getIntent().getParcelableExtra(Constants.DATA_LAYANAN_ORDER);
        bsdDetailLayananFragment = BSDDetailLayananFragment.newInstance(modelJasaLayanan);
        getUuid = getIntent().getStringExtra(UUID_ORDER);

        init();

    }

    private void init() {
        receiver = new LocationBroadcastReceiver();

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapFragment);
        bottomSheetBehavior = BottomSheetBehavior.from(rlBottomSheet);


        initPopup();


        if (supportMapFragment != null) {
            supportMapFragment.getMapAsync(this);
        }

        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View view, int i) {
                switch (i) {
                    case BottomSheetBehavior.STATE_HIDDEN:
                        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED: {
                    }
                    break;
                    case BottomSheetBehavior.STATE_COLLAPSED: {
                        break;
                    }
                    case BottomSheetBehavior.STATE_DRAGGING:
                    case BottomSheetBehavior.STATE_SETTLING:
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View view, float v) {

            }
        });

        if (getUuid != null) {
            Log.d("responUUId", " " + getUuid);
            getData(getUuid);
        }


        startLocService();

    }

    private void initPopup() {
        dialogCheckOrder = new Dialog(DetailOrderLayananActivity.this);
        dialogCheckOrder.setContentView(R.layout.layout_dialog_ulasan);
        dialogCheckOrder.setCancelable(false);

        RatingBar ratingBar = dialogCheckOrder.findViewById(R.id.ratingUlasan);
        EditText edRatingUlasan = dialogCheckOrder.findViewById(R.id.edRatingUlasan);
        Button btnKirimUlasan = dialogCheckOrder.findViewById(R.id.btnKirimUlasan);

        ratingBar.setOnRatingBarChangeListener((ratingBar1, rating, fromUser) -> {
            ratingtext = String.valueOf(rating);
        });

        btnKirimUlasan.setOnClickListener(v -> {
            String getUlasan = edRatingUlasan.getText().toString();

            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("ratingUlasan", ratingtext);
            hashMap.put("textUlasan", getUlasan);


            firebaseFirestore.collection(Constants.NAME_COLLECTION_JASA)
                    .document(getUuid)
                    .update(hashMap)
                    .addOnSuccessListener(aVoid -> {
                        finish();
                    }).addOnFailureListener(e -> {

            });
        });
    }

    private void getData(String getuuid) {
        firebaseFirestore.collection("Detail_Order")
                .document(getuuid)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    modelJasaLayanan = documentSnapshot.toObject(ModelJasaLayanan.class);
                    if (MainActivity.modelUser.getStatus().equals("admin")) {
                        txtTitleTukang.setText("Profil Pemesan");

                        showGlideUser(modelJasaLayanan.getImagePemesan(), imgTukang);

                        txtNamaTukang.setText(modelJasaLayanan.getNamaPemesan());
                        txtPlatTukang.setText(modelJasaLayanan.getNoTlpnPemesan());
                        txtLokasiOrderUser.setText(modelJasaLayanan.getTextAlamatPemesan());
                        txtLokasiOrderToko.setText(modelJasaLayanan.getTextAlamatToko());
                        txtTotalPembayaran.setText(modelJasaLayanan.getTextTotalPembayaran());

                        imgTelp.setOnClickListener(v -> {
                            dialPhoneNumber(modelJasaLayanan.getNoTlpnPemesan());
                        });

                    } else {
                        txtTitleTukang.setText("Profil Tukang");

                        if (modelJasaLayanan != null) {
                            txtNamaTukang.setText(modelJasaLayanan.getGetNamaTukang());
                            txtPlatTukang.setText(modelJasaLayanan.getGetPlatTukang());

                            txtLokasiOrderUser.setText(modelJasaLayanan.getTextAlamatPemesan());
                            txtLokasiOrderToko.setText(modelJasaLayanan.getTextAlamatToko());

                            showGlideUser(modelJasaLayanan.getGetImageTukang(), imgTukang);


                            txtTitleOrder.setText(modelJasaLayanan.getTextNama());
                            txtTotalOngkir.setText(modelJasaLayanan.getTextOngkir());
                            txtTotalPembayaran.setText(modelJasaLayanan.getTextTotalPembayaran());

                        }

                        imgTelp.setOnClickListener(v -> {
                            dialPhoneNumber(modelJasaLayanan.getGetNoTukang());
                        });

                    }

                    edAddNoteds.setText(modelJasaLayanan.getTextTambahan());

                    LatLng latLng = new LatLng(modelJasaLayanan.getLatToko(), modelJasaLayanan.getLotToko());
                    map.addMarker(new MarkerOptions().position(latLng).icon(
                            BitmapDescriptorFactory.fromResource(R.drawable.delivery)).title("Tukang Toko"));

                    LatLng latLngPemesan = new LatLng(modelJasaLayanan.getLatPemesan(), modelJasaLayanan.getLotPemesan());
                    map.addMarker(new MarkerOptions().position(latLngPemesan).icon(
                            BitmapDescriptorFactory.fromResource(R.drawable.marker_baru)).title("Pemesan"));

                    map.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                    map.animateCamera(CameraUpdateFactory.zoomTo(14));


                    txtPayment.setText(modelJasaLayanan.getPayment());


                    txtViewDetailPesanan.setOnClickListener(v -> {
                        BSDDetailOrderFragment detailOrderFragment = BSDDetailOrderFragment.newInstance(modelJasaLayanan);
                        detailOrderFragment.show(getSupportFragmentManager(), "dialogdetailorder");
                    });

                    getDirection(modelJasaLayanan.getLatPemesan(), modelJasaLayanan.getLotPemesan());

                    txtTerimaOrder.setOnClickListener(v -> {
                        updateOrder("Selesai");
                    });

                    txtCancelOrder.setOnClickListener(v -> {
                        new AlertDialog.Builder(this)
                                .setMessage("Apakah anda yakin ingin batalkan pesanan ?")
                                .setPositiveButton("Ya", (dialog, which) -> {
                                    updateOrder("Batal");
                                }).setNegativeButton("Tidak", (dialog, which) -> {
                            dialog.dismiss();
                        }).create().show();

                    });

                }).addOnFailureListener(e -> {


        });


    }

    public void dialPhoneNumber(String phoneNumber) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + phoneNumber));
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    private void updateOrder(String statusOrder) {
        dialogLoading().show();
        firebaseFirestore.collection(Constants.NAME_COLLECTION_JASA)
                .document(getUuid)
                .update("statusOrder", statusOrder)
                .addOnSuccessListener(aVoid -> {
                    dialogLoading().dismiss();
                    if (!MainActivity.modelUser.getStatus().equals("admin")) {
                        dialogCheckOrder.show();
                        dialogLoading().dismiss();
                        finish();
                    } else {
                        dialogCheckOrder.dismiss();
                        finish();
                        dialogLoading().dismiss();

                    }

                }).addOnFailureListener(e -> {
            dialogLoading().dismiss();

        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkUlasan();
    }

    private void checkUlasan() {
        dialogLoading().show();
        firebaseFirestore.collection(Constants.NAME_COLLECTION_JASA)
                .document(getUuid)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    String getStatus = documentSnapshot.getString("statusOrder");
                    String getUlasan = documentSnapshot.getString("textUlasan");

                    dialogLoading().dismiss();
                    if (getStatus != null) {
                        if (getStatus.equals("Selesai") && getUlasan == null && !MainActivity.modelUser.getStatus().equals("admin")) {
                            dialogCheckOrder.show();
                        } else if (getStatus.equals("Batal")) {
                            finish();
                        } else {
                            dialogCheckOrder.dismiss();
                        }
                    }
                }).addOnFailureListener(e -> {
            dialogLoading().dismiss();

        });
    }

    void startLocService() {
        IntentFilter filter = new IntentFilter("ACT_LOC");
        registerReceiver(receiver, filter);
        Intent intent = new Intent(DetailOrderLayananActivity.this, LocationService.class);
        startService(intent);
    }

    private void getDirection(double latDirection, double longDirection) {
        Object[] dataTransfer;
        dataTransfer = new Object[3];

        String url = getDirectionsUrl(latDirection, longDirection);
        GetDirectionsData getDirectionsData = new GetDirectionsData();
        dataTransfer[0] = map;
        dataTransfer[1] = url;
        dataTransfer[2] = new LatLng(latDirection, longDirection);
        getDirectionsData.execute(dataTransfer);
    }

    private String getDirectionsUrl(double lat, double lot) {
        return "https://maps.googleapis.com/maps/api/directions/json?" + "origin=" + "-6.2658892" + "," + "106.9004475" +
                "&destination=" + lat + "," + lot +
                "&key=" + "AIzaSyBpih80dTMhfLqMwjBpeiaReGJ2qpfJu6E";
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        if (modelJasaLayanan != null) {
            LatLng latLng = new LatLng(modelJasaLayanan.getLatToko(), modelJasaLayanan.getLotToko());
            map.addMarker(new MarkerOptions().position(latLng).icon(
                    BitmapDescriptorFactory.fromResource(R.drawable.ic_point_map)).title("Tukang Toko"));

            LatLng latLngPemesan = new LatLng(modelJasaLayanan.getLatPemesan(), modelJasaLayanan.getLotPemesan());
            map.addMarker(new MarkerOptions().position(latLngPemesan).icon(
                    BitmapDescriptorFactory.fromResource(R.drawable.marker_baru)).title("Pemesan"));

//            getDirection(modelJasaLayanan.getLatPemesan(), modelJasaLayanan.getLotPemesan());


        }

        map.setMyLocationEnabled(true);
        map.getUiSettings().setZoomGesturesEnabled(true);
        map.getUiSettings().setMapToolbarEnabled(true);
        map.getUiSettings().setZoomControlsEnabled(true);
        map.getUiSettings().setMyLocationButtonEnabled(true);

    }

    @Override
    public void onClick(View v) {

    }

    public class LocationBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("ACT_LOC")) {
                double lat = intent.getDoubleExtra("latitude", 0f);
                double longitude = intent.getDoubleExtra("longitude", 0f);

                if (MainActivity.modelUser.getStatus().equalsIgnoreCase("admin"))
                    if (map != null) {
                        LatLng latLng = new LatLng(lat, longitude);
                        map.addMarker(new MarkerOptions().position(latLng).icon(
                                BitmapDescriptorFactory.fromResource(R.drawable.delivery)).title("Tukang"));

                        map.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                        map.animateCamera(CameraUpdateFactory.zoomTo(14));
                    }


            }
        }
    }
}
