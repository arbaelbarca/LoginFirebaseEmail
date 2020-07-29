package com.arbaelbarca.loginfirebaseemail;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.bottomnavigation.LabelVisibilityMode;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.arbaelbarca.loginfirebaseemail.Utils.TrackGPS;
import com.arbaelbarca.loginfirebaseemail.basedata.BaseActivity;
import com.arbaelbarca.loginfirebaseemail.model.ModelJasaLayanan;
import com.arbaelbarca.loginfirebaseemail.model.ModelUser;
import com.arbaelbarca.loginfirebaseemail.reminder.BroadcastNotif;
import com.arbaelbarca.loginfirebaseemail.ui.activity.menulayanan.DetailOrderLayananActivity;
import com.arbaelbarca.loginfirebaseemail.ui.fragment.FragmentProfile;
import com.arbaelbarca.loginfirebaseemail.ui.fragment.HistoryOrderFragment;
import com.arbaelbarca.loginfirebaseemail.ui.fragment.HomeFragment;
import com.arbaelbarca.loginfirebaseemail.ui.fragment.ListUserTopupFragment;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MainActivity extends BaseActivity implements View.OnClickListener, BottomNavigationView.OnNavigationItemSelectedListener {

    @BindView(R.id.navigation)
    BottomNavigationView bottomNavigationView;
    FirebaseFirestore firebaseFirestore;
    DocumentReference documentReference;
    Toolbar toolbar;
    public static SharedPreferences sharedPreferences;
    public static SharedPreferences.Editor editor;
    public static ModelUser modelUser;
    FirebaseUser user;
    public static double latitude = 0.0;
    public static double longitude = 0.0;
    double latWisata = 0.0;
    double lotWisata = 0.0;
    SupportMapFragment supportMapFragment;
    TrackGPS gps;
    GoogleMap map;
    Circle mCircle;

    LocationManager locationManager;
    public static boolean GpsStatus;
    BroadcastNotif broadcastNotif;
    String getDateEvent;
    String getTgl;
    String lastDate, dateNow, saveDate;
    String getUuid;
    Dialog dialogPopupCheckOrder;
    ModelJasaLayanan jasaLayanan;
    long totalTimeCountInMilliseconds;
    int time = 0;
    Dialog dialogCheckOrder;
    @BindView(R.id.rlVerify)
    RelativeLayout rlVerify;

    @SuppressLint("CommitPrefEdits")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        FirebaseApp.initializeApp(this);
        user = FirebaseAuth.getInstance().getCurrentUser();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        sharedPreferences = getSharedPreferences(Constants.USER_DATA, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        getUuid = getIntent().getStringExtra("uuidUser");

        initToolbar();

        broadcastNotif = new BroadcastNotif(MainActivity.this);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);

        initial();
        dexterPermissionLocMulti();
        CheckGpsStatus();

        if (!GpsStatus) {
            popupGps();
        }

        if (getUuid != null) checkOrder(getUuid);

        setUpNavigation();
    }

    private void setUpNavigation() {
//        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
//                .findFragmentById(R.id.nav_host_fragment);
//        if (navHostFragment != null) {
//            NavigationUI.setupWithNavController(bottomNavigationView,
//                    navHostFragment.getNavController());
//        }

    }

    private void checkOrder(String getUuid) {
        dialogPopupCheckOrder = new Dialog(this);
        dialogPopupCheckOrder.setContentView(R.layout.layout_popup_dialog_checkorder);
        dialogPopupCheckOrder.setCancelable(false);
        dialogPopupCheckOrder.show();

        Button btnTerima = dialogPopupCheckOrder.findViewById(R.id.btnTerima);
        ImageView btnCancel = dialogPopupCheckOrder.findViewById(R.id.imgClose);
        TextView titleOrder = dialogPopupCheckOrder.findViewById(R.id.txtTitle);
        TextView txtLokasiToko = dialogPopupCheckOrder.findViewById(R.id.textLokasiToko);
        TextView txtLokasiUser = dialogPopupCheckOrder.findViewById(R.id.txtLokasiUser);
        TextView txtOngkir = dialogPopupCheckOrder.findViewById(R.id.txtTotalOngkir);
        TextView txtTotal = dialogPopupCheckOrder.findViewById(R.id.txtTotalPembayaran);
        ProgressBar progressBarTimer = dialogPopupCheckOrder.findViewById(R.id.progressbar1_timerview);
        TextView txtTimer = dialogPopupCheckOrder.findViewById(R.id.txtTimerOrder);

        dialogLoading().show();
        firebaseFirestore.collection("Detail_Order")
                .document(getUuid)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    jasaLayanan = documentSnapshot.toObject(ModelJasaLayanan.class);
                    if (jasaLayanan != null) {
                        titleOrder.setText(jasaLayanan.getTextNama());
                        txtLokasiToko.setText(jasaLayanan.getTextAlamatToko());
                        txtLokasiUser.setText(jasaLayanan.getTextAlamatPemesan());
                        txtOngkir.setText(jasaLayanan.getTextOngkir());
                        txtTotal.setText(jasaLayanan.getTextTotalPembayaran());
                        dialogLoading().dismiss();
                    }


                }).addOnFailureListener(e -> {
            dialogLoading().dismiss();
        });

        btnTerima.setOnClickListener(v -> {
            prosesReceived(getUuid);

        });

        btnCancel.setOnClickListener(v -> {
            dialogPopupCheckOrder.dismiss();
        });

        getTimerDialog(txtTimer, dialogPopupCheckOrder, progressBarTimer);

    }

    void getTimerDialog(TextView textView, Dialog dialog, ProgressBar progressBar) {
        new CountDownTimer(15000, 1000) {
            public void onTick(long millisUntilFinished) {
                long seconds = millisUntilFinished / 1000;
                progressBar.setProgress((int) seconds);
                textView.setText(String.valueOf(millisUntilFinished / 1000));
            }

            public void onFinish() {
                dialog.dismiss();
                progressBar.setVisibility(View.GONE);
            }
        }.start();
    }


    private void prosesReceived(String getUuid) {
        dialogLoading().show();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("statusOrder", "Aktif");
        hashMap.put("getImageTukang", "https://firebasestorage.googleapis.com/v0/b/crudfirebase-b025d.appspot.com/o/imageProfile%2Fbussiness-man.png?alt=media&token=4d900dea-e67f-40d1-b605-3b74968fb811");
        hashMap.put("getNamaTukang", "Ronal");
        hashMap.put("getNoTukang", "081283797723");
        hashMap.put("getPlatTukang", "B 3198 KSW");


        firebaseFirestore.collection("Detail_Order")
                .document(getUuid)
                .update(hashMap)
                .addOnSuccessListener(aVoid -> {
                    dialogLoading().dismiss();
                    dialogPopupCheckOrder.dismiss();
                    Intent intent = new Intent(getApplicationContext(), DetailOrderLayananActivity.class);
                    intent.putExtra("uuid", getUuid);
                    startActivity(intent);
                }).addOnFailureListener(e -> {
            dialogLoading().dismiss();
        });
    }


    private void initToolbar() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Beranda");
        }
    }

    private void initial() {
        getTokenId();
        getTopics();

        loadHome(new HomeFragment());
        firebaseFirestore = FirebaseFirestore.getInstance();

        removeNavigationShiftMode(bottomNavigationView);

//        showRating();
    }


    @SuppressLint("RestrictedApi")
    void removeNavigationShiftMode(BottomNavigationView view) {
        BottomNavigationMenuView menuView = (BottomNavigationMenuView) view.getChildAt(0);
        menuView.setLabelVisibilityMode(LabelVisibilityMode.LABEL_VISIBILITY_LABELED);
        menuView.buildMenuView();
    }


    private boolean loadHome(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frameLayout, fragment)
                    .commit();
            return true;
        }

        return false;
    }

    public void getTokenId() {
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String tokenNotif = task.getResult().getToken();
                        Log.d("responToken", " t " + tokenNotif);
                        firebaseFirestore.collection("event")
                                .document("tokennotif")
                                .update("token_notif", tokenNotif).
                                addOnCompleteListener(task1 -> {
                                    if (task1.isSuccessful()) {
//                                                Toast.makeText(getApplicationContext(), "Success dapat token", Toast.LENGTH_LONG).show();

                                    }
                                }).addOnFailureListener(e -> {
                            Log.d("responMEsssage", "m " + e.getMessage());
                            e.printStackTrace();
                        });
                    }
                }).addOnFailureListener(e -> e.printStackTrace());


    }

    void getTopics() {
        FirebaseMessaging.getInstance().subscribeToTopic("event")
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
//                            Toast.makeText(getApplicationContext(), "succces", Toast.LENGTH_LONG).show();
                    }
                }).addOnFailureListener(e -> {

        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getTopics();
        getDataProfile();
        checkVerify();
        checkRound();
    }

    private void checkVerify() {
        Snackbar snackbar = Snackbar
                .make(rlVerify, "Akun anda belum verifikasi, silahkan verifikasi terlebih dahulu", Snackbar.LENGTH_LONG)
                .setAction("Kirim", v -> {
                    user.sendEmailVerification();
                });

        if (!user.isEmailVerified()) {
            snackbar.show();
            snackbar.setDuration(5000);
        }

    }

    private void checkRound() {
        double data2 = 1.8;
        double data3 = 2.1;

        Log.d("responData", "t " + Math.ceil(data2));
        Log.d("responData", "v " + Math.ceil(data3));

    }

    @Override
    protected void onPause() {
        getTopics();
        super.onPause();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getTopics();
    }


    private void getDataProfile() {
        FirebaseDatabase.getInstance()
                .getReference("User_Hos").child(user.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        modelUser = dataSnapshot.getValue(ModelUser.class);
                        if (modelUser != null) {
                            if (modelUser.getStatus().equalsIgnoreCase("admin")) {
                                bottomNavigationView.getMenu().findItem(R.id.ic_user_topup).setVisible(true);
                            } else {
                                bottomNavigationView.getMenu().findItem(R.id.ic_user_topup).setVisible(false);

                            }
                        }


                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }


    @Override
    public void onClick(View view) {

    }

    void dexterPermissionLocMulti() {
        Dexter.withActivity(MainActivity.this)
                .withPermissions(
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        // check if all permissions are granted
                        if (report.areAllPermissionsGranted()) {
                            // do you work now
                        }

                        // check for permanent denial of any permission
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            // permission is denied permenantly, navigate user to app settings
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                })
                .onSameThread()
                .check();
    }

    public void CheckGpsStatus() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        GpsStatus = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    public void popupGps() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setTitle("Oooppss...");
        builder.setMessage("Gps anda belum aktif, aktifkan terlebih dahulu");
        builder.setPositiveButton("Ok", (dialogInterface, i) -> startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))).setNegativeButton("Tidak", (dialogInterface, i) -> dialogInterface.dismiss()).create().show();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        Fragment fragment = null;
        switch (menuItem.getItemId()) {
            case R.id.ic_wisata:
                fragment = new HomeFragment();
                break;
            case R.id.ic_historyorder:
                fragment = new HistoryOrderFragment();
                break;
            case R.id.ic_event:
                fragment = new FragmentProfile();
                break;
            case R.id.ic_user_topup:
                fragment = new ListUserTopupFragment();
                break;

            default:
        }
        return loadHome(fragment);
    }

    void showRating() {
        dialogCheckOrder = new Dialog(MainActivity.this);
        dialogCheckOrder.setContentView(R.layout.layout_dialog_ulasan);
        dialogCheckOrder.setCancelable(false);
        dialogCheckOrder.show();
    }
}
