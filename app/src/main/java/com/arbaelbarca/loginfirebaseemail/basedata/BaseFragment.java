package com.arbaelbarca.loginfirebaseemail.basedata;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.Toast;

import com.arbaelbarca.loginfirebaseemail.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

public class BaseFragment extends Fragment {
    Dialog dialogLoad;
    AlertDialog alertDialog;
    Dialog dialogPopup;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        dialogLoad = new Dialog(getActivity());
        dialogLoad.requestWindowFeature(Window.FEATURE_NO_TITLE);

    }

    public void showToast(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
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
        if (getActivity() != null)
            Glide.with(getActivity())
                    .load(url)
                    .apply(RequestOptions.placeholderOf(R.drawable.ic_user_no_photo)
                            .error(R.drawable.ic_user_no_photo))
                    .into(imageView);

    }

    public void showGlideDefault(String url, ImageView imageView) {
        if (getActivity() != null)
            Glide.with(getActivity())
                    .load(url)
                    .apply(RequestOptions.placeholderOf(R.drawable.no_image)
                            .error(R.drawable.no_image))
                    .into(imageView);

    }
}
