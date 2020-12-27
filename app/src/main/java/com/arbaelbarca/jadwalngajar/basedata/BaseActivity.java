package com.arbaelbarca.jadwalngajar.basedata;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Window;
import android.widget.Toast;

public class BaseActivity extends AppCompatActivity {
    Dialog dialogLoad;
    AlertDialog alertDialog;
    Dialog dialogPopup;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dialogLoad = new Dialog(this);
        dialogLoad.requestWindowFeature(Window.FEATURE_NO_TITLE);

        dialogPopup = new Dialog(this);
    }

    public void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    public void setToolbar(Toolbar toolbar, String title) {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(title);
    }

    public AlertDialog loading() {
        alertDialog.setCancelable(false);
        alertDialog.setMessage("Loading");
        return alertDialog;
    }


//    public Dialog dialogLoading() {
//        dialogLoad.setContentView(R.layout.layout_progress_bar);
//        dialogLoad.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
//        dialogLoad.setCancelable(false);
//        return dialogLoad;
//    }

}
