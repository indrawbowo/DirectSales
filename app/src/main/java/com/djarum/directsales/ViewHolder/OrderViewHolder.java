package com.djarum.directsales.ViewHolder;

import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.djarum.directsales.Interface.IItemClickListener;
import com.djarum.directsales.R;

public class OrderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView txtOrderId,txtOrderAddress,txtOrderBuyerName;
    public Button btnCancel, btnDeliver;
    public LinearLayout layout;

    private IItemClickListener itemClickListener;

    public OrderViewHolder(@NonNull View itemView) {
        super(itemView);
        txtOrderId = (TextView)itemView.findViewById(R.id.txtOrderId);
        txtOrderAddress = (TextView)itemView.findViewById(R.id.txtOrderAddress);
        txtOrderBuyerName = (TextView)itemView.findViewById(R.id.txtOrderBuyerName);
        layout = (LinearLayout) itemView.findViewById(R.id.wrapper);
        btnCancel = (Button) itemView.findViewById(R.id.btnCancelOrder);
        btnDeliver = (Button) itemView.findViewById(R.id.btnDeliverOrder);
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
