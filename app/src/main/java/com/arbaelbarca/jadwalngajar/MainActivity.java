package com.arbaelbarca.jadwalngajar;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.arbaelbarca.jadwalngajar.Utils.TrackGPS;
import com.arbaelbarca.jadwalngajar.basedata.BaseActivity;
import com.arbaelbarca.jadwalngajar.model.ModelUser;
import com.arbaelbarca.jadwalngajar.reminder.BroadcastNotif;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomnavigation.LabelVisibilityMode;
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
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MainActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.btnWisata)
    Button btnWisata;
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
    @BindView(R.id.btnBudaya)
    Button btnBudaya;
    @BindView(R.id.btnCuaca)
    Button btnCuaca;
    @BindView(R.id.btnEvent)
    Button btnEvent;
    @BindView(R.id.btnTentang)
    Button btnTentang;
    @BindView(R.id.imgCuacaHome)
    ImageView imgCuacaHome;
    @BindView(R.id.txtCuacaHome)
    TextView txtCuacaHome;
    LocationManager locationManager;
    public static boolean GpsStatus;
    BroadcastNotif broadcastNotif;
    String getDateEvent;
    String getTgl;
    String lastDate, dateNow, saveDate;

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

        initToolbar();

        broadcastNotif = new BroadcastNotif(MainActivity.this);


        btnWisata.setOnClickListener(this);
        btnBudaya.setOnClickListener(this);
        btnEvent.setOnClickListener(this);
        btnCuaca.setOnClickListener(this);
        btnTentang.setOnClickListener(this);


        initial();
        dexterPermissionLocMulti();
        CheckGpsStatus();
        removeNavigationShiftMode(bottomNavigationView);


        if (GpsStatus) {

        } else {
            popupGps();
        }
    }

    void checkEmailValidation() {
        if (user.isEmailVerified()) {
            showToast("Email sudah verify");
        } else {
            showToast("Email belum verify");
        }
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
        firebaseFirestore = FirebaseFirestore.getInstance();

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
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (task.isSuccessful()) {
                            String tokenNotif = task.getResult().getToken();
                            Log.d("responToken", " t " + tokenNotif);
                            firebaseFirestore.collection("event")
                                    .document("tokennotif")
                                    .update("token_notif", tokenNotif).
                                    addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
//                                                Toast.makeText(getApplicationContext(), "Success dapat token", Toast.LENGTH_LONG).show();

                                            }
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d("responMEsssage", "m " + e.getMessage());
                                    e.printStackTrace();
                                }
                            });
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
            }
        });


    }

    void getTopics() {
        FirebaseMessaging.getInstance().subscribeToTopic("event")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
//                            Toast.makeText(getApplicationContext(), "succces", Toast.LENGTH_LONG).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
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
        checkEmailValidation();

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
        FirebaseDatabase.getInstance().getReference("Users").child(user.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        modelUser = dataSnapshot.getValue(ModelUser.class);

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
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }
        }).setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        }).create().show();
    }
}
