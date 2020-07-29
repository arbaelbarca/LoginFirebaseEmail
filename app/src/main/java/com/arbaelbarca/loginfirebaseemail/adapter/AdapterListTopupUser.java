package com.arbaelbarca.loginfirebaseemail.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.arbaelbarca.loginfirebaseemail.R;
import com.arbaelbarca.loginfirebaseemail.basedata.BaseActivity;
import com.arbaelbarca.loginfirebaseemail.model.modeltopup.ModelTopup;
import com.arbaelbarca.loginfirebaseemail.onclick.OnClickItem;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class AdapterListTopupUser extends RecyclerView.Adapter<AdapterListTopupUser.ViewHolder> {
    private Context context;
    private List<ModelTopup> modelTopupList;

    private OnClickItem onClickItem;

    public AdapterListTopupUser(Context context) {
        this.context = context;
        modelTopupList = new ArrayList<>();
    }

    public void setOnClickItem(OnClickItem onClickItem) {
        this.onClickItem = onClickItem;
    }

    public List<ModelTopup> getModelTopupList() {
        return modelTopupList;
    }

    public void setModelTopupList(List<ModelTopup> modelTopupList) {
        this.modelTopupList = modelTopupList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.layout_item_listtopup, viewGroup, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        ModelTopup modelTopup = modelTopupList.get(i);

        ((BaseActivity) context)
                .showGlideUser(modelTopup.getPhotoUser(), viewHolder.imgUserTopup);

        viewHolder.txtNameTopup.setText(modelTopup.getNamaUser());
        viewHolder.txtRequestTopup.setText("Permintaan saldo : " + modelTopup.getNominalTopup());

        if (modelTopup.getStatusTopup().equalsIgnoreCase("Received")) {
            viewHolder.txtStatusTopup.setBackgroundResource(R.drawable.bg_green_rounded);
        } else {
            viewHolder.txtStatusTopup.setBackgroundResource(R.drawable.bg_red_rounded);

        }
        viewHolder.txtStatusTopup.setText(modelTopup.getStatusTopup());

        viewHolder.itemView.setOnClickListener(v -> {
            if (onClickItem != null)
                onClickItem.clickItemAktif(i);
        });
    }

    @Override
    public int getItemCount() {
        return modelTopupList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgUserTopup;
        TextView txtNameTopup, txtRequestTopup, txtStatusTopup;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            imgUserTopup = itemView.findViewById(R.id.imgUserTopup);
            txtNameTopup = itemView.findViewById(R.id.txtUsernameTopup);
            txtRequestTopup = itemView.findViewById(R.id.txtRequesTopup);
            txtStatusTopup = itemView.findViewById(R.id.txtStatusTopup);


        }
    }
}
