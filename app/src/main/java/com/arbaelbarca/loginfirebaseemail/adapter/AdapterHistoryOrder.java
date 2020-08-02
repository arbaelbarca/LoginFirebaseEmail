package com.arbaelbarca.loginfirebaseemail.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.arbaelbarca.loginfirebaseemail.R;
import com.arbaelbarca.loginfirebaseemail.Utils.ConvertToRupiah;
import com.arbaelbarca.loginfirebaseemail.model.ModelJasaLayanan;
import com.arbaelbarca.loginfirebaseemail.onclick.OnClickItem;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class AdapterHistoryOrder extends RecyclerView.Adapter<AdapterHistoryOrder.ViewHolder> {
    private Context context;
    private ArrayList<ModelJasaLayanan> modelJasaLayananArrayList;

    public AdapterHistoryOrder(Context context) {
        this.context = context;
        modelJasaLayananArrayList = new ArrayList<>();
    }

    public ArrayList<ModelJasaLayanan> getModelJasaLayananArrayList() {
        return modelJasaLayananArrayList;
    }

    public void setModelJasaLayananArrayList(ArrayList<ModelJasaLayanan> modelJasaLayananArrayList) {
        this.modelJasaLayananArrayList = modelJasaLayananArrayList;
    }

    private OnClickItem onClickItem;

    public void setOnClickItem(OnClickItem onClickItem) {
        this.onClickItem = onClickItem;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.layout_item_histories, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        ModelJasaLayanan jasaLayanan = modelJasaLayananArrayList.get(i);

        Glide.with(context)
                .load(jasaLayanan.getImageJasa())
                .into(viewHolder.imgHistories);

        viewHolder.txtNamaHistories.setText(jasaLayanan.getTextNama());
        viewHolder.txtHargaHistories.setText(ConvertToRupiah.toRupiah("Rp ", jasaLayanan.getTextTotalPembayaran(), false));
        viewHolder.txtKatHistories.setText(jasaLayanan.getTextKategori());

        viewHolder.txtStatusHistories.setText(jasaLayanan.getStatusOrder());

        if (jasaLayanan.getStatusOrder().equalsIgnoreCase("Batal")) {
            viewHolder.txtStatusHistories.setTextColor(ContextCompat.getColor(context, R.color.yellow));
        } else if (jasaLayanan.getStatusOrder().equalsIgnoreCase("Aktif")) {
            viewHolder.txtStatusHistories.setTextColor(ContextCompat.getColor(context, R.color.red_actionbar_normal));
        } else if (jasaLayanan.getStatusOrder().equalsIgnoreCase("Selesai"))
            viewHolder.txtStatusHistories.setTextColor(ContextCompat.getColor(context, R.color.text_color_green));

        viewHolder.itemView.setOnClickListener(v -> {


            if (jasaLayanan.getStatusOrder().equalsIgnoreCase("Selesai")) {
                if (onClickItem != null)
                    onClickItem.clickItemSelesai(i);
            } else if (jasaLayanan.getStatusOrder().equalsIgnoreCase("Batal")) {
                if (onClickItem != null)
                    onClickItem.clickItemSelesai(i);
            } else {
                if (onClickItem != null)
                    onClickItem.clickItemAktif(i);
            }


        });

        viewHolder.txtNamaHistoryPemesan.setText("Nama Pemesan : " + jasaLayanan.getNamaPemesan());


    }

    @Override
    public int getItemCount() {
        return modelJasaLayananArrayList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgHistories;
        TextView txtNamaHistories, txtHargaHistories, txtKatHistories;
        TextView txtStatusHistories, txtNamaHistoryPemesan;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            imgHistories = itemView.findViewById(R.id.imgHistoriesOrder);
            txtNamaHistories = itemView.findViewById(R.id.txtNamaHistoriesJasa);
            txtHargaHistories = itemView.findViewById(R.id.txtHargaHistoriesJasa);
            txtKatHistories = itemView.findViewById(R.id.txtKatHistoriesJasa);
            txtStatusHistories = itemView.findViewById(R.id.txtStatusHistoriesJasa);
            txtNamaHistoryPemesan = itemView.findViewById(R.id.txtHistoryNamaPemesan);

        }
    }
}
