package com.djarum.directsales.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.djarum.directsales.Interface.IItemClickListener;
import com.djarum.directsales.R;

public class BuyerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public TextView txtBuyerName,txtBuyerPhone, txtBuyerEmail;
    public ImageView imgBuyer;

    private IItemClickListener itemClickListener;

    public BuyerViewHolder(@NonNull View itemView) {
        super(itemView);
        txtBuyerName = (TextView)itemView.findViewById(R.id.txtBuyerName);
        txtBuyerPhone = (TextView)itemView.findViewById(R.id.txtBuyerPhone);
        txtBuyerEmail = (TextView)itemView.findViewById(R.id.txtBuyerEmail);
        imgBuyer = (ImageView) itemView.findViewById(R.id.img_buyer);
        itemView.setOnClickListener(this);
    }

    public void setItemClickListener(IItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View v) {
        itemClickListener.onClick(v, getAdapterPosition(),false);
    }
}
