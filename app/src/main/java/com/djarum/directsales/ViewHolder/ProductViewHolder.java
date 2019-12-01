package com.djarum.directsales.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.djarum.directsales.Interface.IItemClickListener;
import com.djarum.directsales.R;

public class ProductViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView txtProductName,txtProductPrice;
    public ImageView imgProduct;

    private IItemClickListener itemClickListener;

    public ProductViewHolder(@NonNull View itemView) {
        super(itemView);
        txtProductName = (TextView)itemView.findViewById(R.id.txtProductName);
        txtProductPrice = (TextView)itemView.findViewById(R.id.txtProductPrice);
        imgProduct = (ImageView) itemView.findViewById(R.id.product_image);
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
