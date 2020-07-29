package com.arbaelbarca.loginfirebaseemail.adapter;

import android.content.Context;
import android.media.Image;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.arbaelbarca.loginfirebaseemail.R;
import com.arbaelbarca.loginfirebaseemail.model.ModelJasaLayanan;
import com.arbaelbarca.loginfirebaseemail.onclick.OnClickItem;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class AdapterMenuLayanan extends RecyclerView.Adapter<AdapterMenuLayanan.ViewHolder> {
    private Context context;
    private ArrayList<ModelJasaLayanan> layananArrayList;

    public AdapterMenuLayanan(Context context) {
        this.context = context;
    }

    OnClickItem onClickItem;

    public void setOnClickItem(OnClickItem onClickItem) {
        this.onClickItem = onClickItem;
    }

    public void setLayananArrayList(ArrayList<ModelJasaLayanan> layananArrayList) {
        this.layananArrayList = layananArrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.layout_list_jasa, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int i) {
        ModelJasaLayanan modelJasaLayanan = layananArrayList.get(i);
        viewHolder.txtName.setText(modelJasaLayanan.getTextKategori());
        viewHolder.txtHargaJasa.setText(modelJasaLayanan.getTextHarga());

        Glide.with(context)
                .load(modelJasaLayanan.getImageJasa())
                .into(viewHolder.imgJasa);

        viewHolder.itemView.setOnClickListener(v -> onClickItem.clickItemAktif(i));

    }

    @Override
    public int getItemCount() {
        return layananArrayList == null ? 0 : layananArrayList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtName, txtHargaJasa;

        ImageView imgJasa;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            txtName = itemView.findViewById(R.id.txtNamaJasa);
            imgJasa = itemView.findViewById(R.id.imgJasa);
            txtHargaJasa = itemView.findViewById(R.id.txtHargaJasa);
        }
    }
}
