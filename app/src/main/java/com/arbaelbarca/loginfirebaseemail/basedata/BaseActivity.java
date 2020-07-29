package com.arbaelbarca.loginfirebaseemail.basedata;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Window;
import android.widget.ImageView;
import android.widget.Toast;

import com.arbaelbarca.loginfirebaseemail.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.Objects;

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
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(title);

        if (toolbar.getNavigationIcon() != null)
            toolbar.setNavigationOnClickListener(v -> {
                finish();
            });
    }

    public AlertDialog loading() {
        alertDialog.setCancelable(false);
        alertDialog.setMessage("Loading");
        return alertDialog;
    }


    public Dialog dialogLoading() {
        dialogLoad.setContentView(R.layout.layout_progressbar_dialog);
        dialogLoad.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialogLoad.setCancelable(false);
        return dialogLoad;
    }

    public void showGlideUser(String url, ImageView imageView) {
        Glide.with(BaseActivity.this)
                .load(url)
                .apply(RequestOptions.placeholderOf(R.drawable.ic_user_no_photo)
                        .error(R.drawable.ic_user_no_photo))
                .into(imageView);

    }

    public void showGlideDefault(String url, ImageView imageView) {
        Glide.with(BaseActivity.this)
                .load(url)
                .apply(RequestOptions.placeholderOf(R.drawable.no_image)
                        .error(R.drawable.no_image))
                .into(imageView);

    }
}
