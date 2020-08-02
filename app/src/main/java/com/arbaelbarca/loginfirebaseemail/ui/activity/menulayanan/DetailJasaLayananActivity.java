package com.arbaelbarca.loginfirebaseemail.ui.activity.menulayanan;

import android.app.Dialog;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.arbaelbarca.loginfirebaseemail.Constants;
import com.arbaelbarca.loginfirebaseemail.MainActivity;
import com.arbaelbarca.loginfirebaseemail.Network.ServiceApiClient;
import com.arbaelbarca.loginfirebaseemail.Notification.RequestNotificaton;
import com.arbaelbarca.loginfirebaseemail.Notification.SendNotificationModel;
import com.arbaelbarca.loginfirebaseemail.R;
import com.arbaelbarca.loginfirebaseemail.Utils.Directions.GetDirectionsData;
import com.arbaelbarca.loginfirebaseemail.Utils.TrackGPS;
import com.arbaelbarca.loginfirebaseemail.basedata.BaseActivity;
import com.arbaelbarca.loginfirebaseemail.model.ModelJasaLayanan;
import com.arbaelbarca.loginfirebaseemail.model.ModelUser;
import com.arbaelbarca.loginfirebaseemail.model.modeldirectionmaps.ResponseDirectionMaps;
import com.arbaelbarca.loginfirebaseemail.model.modelnotif.ResponsePushNotif;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.arbaelbarca.loginfirebaseemail.Constants.NAME_COLLECTION_JASA;

public class DetailJasaLayananActivity extends BaseActivity implements OnMapReadyCallback, View.OnClickListener {

    ModelJasaLayanan jasaLayanan;
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
    SupportMapFragment supportMapFragment;
    @BindView(R.id.txtAlamat)
    TextView txtAlamatToko;
    @BindView(R.id.txtAlamat2)
    TextView txtAlamatUser;
    @BindView(R.id.btnOrder)
    Button btnOrder;
    @BindView(R.id.rlOrder)
    LinearLayout rlOrder;
    @BindView(R.id.btnGetLokasi)
    Button imgGetGPS;
    TrackGPS gps;

    double latitude = 0.0;
    double longitude = 0.0;
    GoogleMap map;
    String latDestination;
    String lotDestination;
    @BindView(R.id.edAddNoteds)
    EditText edAddNoteds;
    @BindView(R.id.txtOngkir)
    TextView txtOngkirJasa;
    @BindView(R.id.llOngkir)
    LinearLayout llOngkir;
    @BindView(R.id.txtTotalPembayaran)
    TextView txtTotalPembayaran;
    FirebaseFirestore firebaseFirestore;
    FirebaseUser firebaseUser;
    @BindView(R.id.txtDetailJarak)
    TextView txtDetailJarak;

    @BindView(R.id.rbGroupPayment)
    RadioGroup rbGroupPayment;
    RadioButton radioButton;
    @BindView(R.id.rbTunai)
    RadioButton rbTunai;
    @BindView(R.id.rbNonTunai)
    RadioButton rbNonTunai;

    DatabaseReference reference;
    @BindView(R.id.txtSaldoku)
    TextView txtSaldoku;
    @BindView(R.id.llSaldoku)
    LinearLayout llSaldoku;
    String address = "";
    @BindView(R.id.llTotalPemb)
    LinearLayout llTotalPemb;
    int paymentSaldo;

    FusedLocationProviderClient fusedLocationProviderClient;
    LocationCallback locationCallback;
    CountDownTimer countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_jasa_layanan);
        ButterKnife.bind(this);

        jasaLayanan = getIntent().getParcelableExtra(Constants.DATA_LAYANAN);
        supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapFragment);


        init();
    }

    private void init() {

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if (jasaLayanan != null) {
            txtNamaJasa.setText(jasaLayanan.getTextNama());
            txtHargaJasa.setText(jasaLayanan.getTextHarga());
            txtKategoriJasa.setText(jasaLayanan.getTextKategori());
            txtDescJasa.setText(jasaLayanan.getTextDeskripsi());

            showGlideDefault(jasaLayanan.getImageJasa(), imgAddJasa);
        }

        imgGetGPS.setOnClickListener(v -> {
            getPushLoc();
        });

        supportMapFragment.getMapAsync(this);
        btnOrder.setOnClickListener(this);

        rbGroupPayment.setOnCheckedChangeListener((group, checkedId) -> {
            if (address.isEmpty()) {
                showToast("Silahkan ambil lokasi terlebih dahulu");
                rbTunai.setChecked(false);
                rbNonTunai.setChecked(false);
            } else {
                int textHarga = Integer.parseInt(jasaLayanan.getTextHarga());
                int textOnkir = Integer.parseInt(txtOngkirJasa.getText().toString());
                int totalHarga = textHarga + textOnkir;
                int saldoku = Integer.parseInt(MainActivity.modelUser.getSaldoku());
                int totalPemb = saldoku - totalHarga;

                if (checkedId == R.id.rbNonTunai) {
                    llSaldoku.setVisibility(View.VISIBLE);
                    txtSaldoku.setText(" - " + String.valueOf(totalPemb));
                    txtTotalPembayaran.setText(String.valueOf(totalHarga));
                } else if (checkedId == R.id.rbTunai) {
                    llSaldoku.setVisibility(View.GONE);
                    txtTotalPembayaran.setText(String.valueOf(totalHarga));
                }
            }


        });

    }

    void popupDialogCheckOrder(ModelJasaLayanan modelJasaLayanan) {
        Dialog dialogOrder = new Dialog(this);
        dialogOrder.setContentView(R.layout.layout_dialog_order);
        dialogOrder.setCancelable(false);
        dialogOrder.show();

//        TextView txtTimer = dialogOrder.findViewById(R.id.txtCountTimer);
        getTimer(dialogOrder, modelJasaLayanan);

    }

    void getTimer(Dialog dialog, ModelJasaLayanan modelJasaLayanan) {
        countDownTimer = new CountDownTimer(15000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                validateAdmin(modelJasaLayanan.getUuid());
            }

            @Override
            public void onFinish() {
                dialog.dismiss();
                showToast("anda tidak dapat driver, mohon di order kembali");
                countDownTimer.cancel();
            }
        };
        countDownTimer.start();
    }


    void getPushLoc() {
        dialogLoading().show();
        gps = new TrackGPS(this);
        if (gps.canGetLocation()) {
            latitude = gps.getLatitude();
            longitude = gps.getLongitude();
        }

        Geocoder geocoder = new Geocoder(DetailJasaLayananActivity.this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null && addresses.size() > 0) {
                dialogLoading().dismiss();
                address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                latDestination = String.valueOf(addresses.get(0).getLatitude());
                lotDestination = String.valueOf(addresses.get(0).getLongitude());


                Log.d("resonAddres", "address : " + latDestination + " lot " + lotDestination + "" +
                        latitude + " lot " + longitude);

                txtAlamatUser.setText("Alamat Kamu : " + address);

                supportMapFragment.getMapAsync(googleMap -> {
                    map = googleMap;
                    LatLng latLng = new LatLng(latitude, longitude);
                    googleMap.addMarker(new MarkerOptions().position(latLng).icon(
                            BitmapDescriptorFactory.fromResource(R.drawable.marker_baru))
                            .title("Lokasi Anda").snippet(address));

                    googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                    googleMap.animateCamera(CameraUpdateFactory.zoomTo(14));
                });

                String getOrigin = Constants.LAT_TOKO_STRING + "," + Constants.LOT_TOKO_STRING;
                String getDestination = latitude + "," + longitude;

                hitApiDirection(getOrigin, getDestination);
                getDirection(latitude, longitude);
                dialogLoading().dismiss();
            } else {
                dialogLoading().dismiss();
            }
        } catch (IOException e) {
            dialogLoading().dismiss();
            e.printStackTrace();
        }


    }


    private void hitApiDirection(String getOrigin, String getDestination) {
        dialogLoading().show();
        ServiceApiClient
                .getInstance()
                .getApiServices()
                .requestDirectionMaps(getOrigin, getDestination, "AIzaSyBpih80dTMhfLqMwjBpeiaReGJ2qpfJu6E")
                .enqueue(new Callback<ResponseDirectionMaps>() {
                    @Override
                    public void onResponse(Call<ResponseDirectionMaps> call, Response<ResponseDirectionMaps> response) {
                        dialogLoading().dismiss();
                        if (response.body() != null) {
                            String getPolyline = response.body().getRoutes().get(0).getOverviewPolyline().getPoints();

                            String textKm = response.body().getRoutes().get(0).getLegs().get(0).getDistance().getText();
                            String textDuration = response.body().getRoutes().get(0).getLegs().get(0).getDuration().getText();

                            String textHarga = jasaLayanan.getTextHarga();

                            String subsKm = textKm.substring(0, 1);
                            txtDetailJarak.setText("Lokasi Toko berjarak dengan lokasi kamu yaitu " + subsKm + " Km dengan waktu " + textDuration);
                            txtDetailJarak.setVisibility(View.VISIBLE);

                            int ongkir = Integer.parseInt(subsKm) * 1000;
                            int total = Integer.parseInt(textHarga) + ongkir;

                            llOngkir.setVisibility(View.VISIBLE);
                            txtOngkirJasa.setText(String.valueOf(ongkir));
                            txtTotalPembayaran.setText(String.valueOf(total));

                            llTotalPemb.setVisibility(View.VISIBLE);

                        }
                        showToast("Sukses dapet lokasi anda");
                    }

                    @Override
                    public void onFailure(Call<ResponseDirectionMaps> call, Throwable t) {
                        dialogLoading().dismiss();
                        showToast("gagal koneksi");
                    }
                });

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

        LatLng latLng = new LatLng(Constants.LAT_TOKO, Constants.LOT_TOKO);
        map.addMarker(new MarkerOptions().position(latLng).icon(
                BitmapDescriptorFactory.fromResource(R.drawable.marker_baru)).title("Lokasi Toko")
                .snippet(Constants.ALAMAT_TOKO));

        map.setMyLocationEnabled(true);
        map.getUiSettings().setZoomGesturesEnabled(true);
        map.getUiSettings().setMapToolbarEnabled(true);
        map.getUiSettings().setZoomControlsEnabled(true);
        map.getUiSettings().setMyLocationButtonEnabled(true);
    }


    @Override
    public void onClick(View v) {
        if (v == btnOrder) {
            if (firebaseUser.isEmailVerified()) {
                if (MainActivity.modelUser.getStatus().equalsIgnoreCase("admin")) {
                    showToast("Admin tidak bisa memesan jasa");
                } else {
                    int selectId = rbGroupPayment.getCheckedRadioButtonId();
                    radioButton = findViewById(selectId);

                    if (rbGroupPayment.getCheckedRadioButtonId() == -1) {
                        showToast("Silahkan pilih metode pembayaran");
                    } else {
                        requestOrder();
                    }
                }
            } else {
                showToast("Akun anda belum terverifikasi, silahkan check email anda");
            }


        }
    }

    void getTokenIdAdmin(ModelJasaLayanan modelJasaLayanan) {
        dialogLoading().show();
        FirebaseDatabase
                .getInstance()
                .getReference("User_Hos")
                .child("i28AwJKM2IQ43ODgOcx8UniQvOC2")
                .child("Notification")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        ModelUser modelUser = dataSnapshot.getValue(ModelUser.class);
                        if (modelUser != null) {
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        dialogLoading().dismiss();
                    }
                });
    }

    private void sendPushAdmin(ModelJasaLayanan modelJasaLayanan) {
        SendNotificationModel sendNotificationModel = new SendNotificationModel("Order masuk", modelJasaLayanan.getTextNama());

        RequestNotificaton requestNotificaton = new RequestNotificaton();
        requestNotificaton.setSendNotificationModel(sendNotificationModel);

        sendNotificationModel.setUuid(modelJasaLayanan.getUuid());

        requestNotificaton.setData(sendNotificationModel);
        requestNotificaton.setToken("/topics/event");
        requestNotificaton.setContent_available(true);

        Call<ResponsePushNotif> call = ServiceApiClient.getApiNotif().sendChatNotification(requestNotificaton);
        call.enqueue(new Callback<ResponsePushNotif>() {
            @Override
            public void onResponse(Call<ResponsePushNotif> call, Response<ResponsePushNotif> response) {
                if (response.isSuccessful()) {
                    popupDialogCheckOrder(modelJasaLayanan);

                }
            }

            @Override
            public void onFailure(Call<ResponsePushNotif> call, Throwable t) {
                dialogLoading().dismiss();
                t.printStackTrace();
            }
        });

    }

    private void validateAdmin(String getuuid) {
        firebaseFirestore.collection(NAME_COLLECTION_JASA)
                .document(getuuid)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    jasaLayanan = documentSnapshot.toObject(ModelJasaLayanan.class);
                    if (jasaLayanan != null && jasaLayanan.getStatusOrder().equalsIgnoreCase("aktif")) {
                        Intent intent = new Intent(getApplicationContext(), DetailOrderLayananActivity.class);
                        intent.putExtra("uuid", getuuid);
                        startActivity(intent);
                        finish();
                        dialogLoading().dismiss();
                        countDownTimer.onFinish();
                    } else {
                        dialogLoading().dismiss();
                    }


                }).addOnFailureListener(e -> {
            dialogLoading().dismiss();
        });
    }

    private void requestOrder() {
        dialogLoading().show();
        final String uuid = UUID.randomUUID().toString();
        ModelJasaLayanan modelJasaLayanan = new ModelJasaLayanan();
        modelJasaLayanan.setIdUser(firebaseUser.getUid());
        modelJasaLayanan.setUuid(uuid);
        modelJasaLayanan.setImageJasa(jasaLayanan.getImageJasa());
        modelJasaLayanan.setTextNama(jasaLayanan.getTextNama());
        modelJasaLayanan.setTextKategori(jasaLayanan.getTextKategori());
        modelJasaLayanan.setTextDeskripsi(jasaLayanan.getTextDeskripsi());
        modelJasaLayanan.setTextHarga(jasaLayanan.getTextHarga());
        modelJasaLayanan.setTextTambahan(edAddNoteds.getText().toString());
        modelJasaLayanan.setTextOngkir(txtOngkirJasa.getText().toString());
        modelJasaLayanan.setTextTotalPembayaran(txtTotalPembayaran.getText().toString());
        modelJasaLayanan.setTextAlamatToko(txtAlamatToko.getText().toString());
        modelJasaLayanan.setTextAlamatPemesan(txtAlamatUser.getText().toString());
        modelJasaLayanan.setLatToko(Constants.LAT_TOKO);
        modelJasaLayanan.setLotToko(Constants.LOT_TOKO);
        modelJasaLayanan.setLatPemesan(latitude);
        modelJasaLayanan.setLotPemesan(longitude);
        modelJasaLayanan.setStatusOrder("");
        modelJasaLayanan.setNamaPemesan(MainActivity.modelUser.getNama());
        modelJasaLayanan.setNoTlpnPemesan(MainActivity.modelUser.getPhone());
        modelJasaLayanan.setImagePemesan(MainActivity.modelUser.getImageUrl());
        modelJasaLayanan.setPayment(radioButton.getText().toString());

        if (rbNonTunai.isChecked()) {
            showToast("Ke check");
            int saldoku = Integer.parseInt(MainActivity.modelUser.getSaldoku());
            String getTotal = txtTotalPembayaran.getText().toString();
            if (!getTotal.equalsIgnoreCase("")) {
                int totalPayment = Integer.parseInt(getTotal);
                paymentSaldo = saldoku - totalPayment;
            }
        } else {
            showToast("Tidak check");
        }


        firebaseFirestore.collection(NAME_COLLECTION_JASA).document(uuid)
                .set(modelJasaLayanan, SetOptions.merge()).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                reference = FirebaseDatabase.getInstance().getReference("User_Hos")
                        .child(firebaseUser.getUid());
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("saldoku", String.valueOf(paymentSaldo));
                reference.updateChildren(hashMap)
                        .addOnCompleteListener(task1 -> {
                            if (task1.isSuccessful()) {
//                                getTokenIdAdmin(modelJasaLayanan);
                                sendPushAdmin(modelJasaLayanan);

                            }
                        });

            }
        }).addOnFailureListener(e -> {
            dialogLoading().dismiss();
            showToast("Gagal menambah jasa");
        });

    }
}
