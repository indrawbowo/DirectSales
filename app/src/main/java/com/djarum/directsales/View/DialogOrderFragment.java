package com.djarum.directsales.View;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.djarum.directsales.Model.Buyer;
import com.djarum.directsales.Model.Order;
import com.djarum.directsales.Model.Product;
import com.djarum.directsales.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DialogOrderFragment extends DialogFragment {
    private EditText txtJumlah;
    private Buyer buyer;
    private Product product;
    private final static String ARGS_BUYER = "BUYER";
    private final static String ARGS_PRODUCT = "PRODUCT";
    private Context context;

    public static boolean isNumeric(String str) {
        return str.matches("-?\\d+(\\.\\d+)?");  //match a number with optional '-' and decimal.
    }

    private void doAddOrder() {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference table_buyer = database.getReference("Order");
        final ProgressDialog mDialog = new ProgressDialog(getActivity());
        mDialog.setMessage("Please wait...");
        mDialog.show();
        table_buyer.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mDialog.dismiss();
                Long id = dataSnapshot.getChildrenCount();
                String jumlah = txtJumlah.getText().toString();
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                Order newOrder = new Order();
                newOrder.setJumlah(Integer.valueOf(jumlah));
                newOrder.setCancelled(false);
                newOrder.setDelivered(false);
                newOrder.setOrderId(String.valueOf(id+1));
                newOrder.setProductId(product.getProductId());
                newOrder.setProductPrice(product.getHarga());
                newOrder.setProductName(product.getNama());
                newOrder.setBuyerName(buyer.getNama());
                newOrder.setBuyerId(buyer.getBuyerId());
                newOrder.setAddress(buyer.getAlamat());
                newOrder.setTotalPrice(product.getHarga() * Integer.valueOf(jumlah));
                newOrder.setOrderDate(sdf.format(new Date()));
                table_buyer.push().setValue(newOrder);
                Toast.makeText(context, "Add success", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        if (getArguments() != null) {
            buyer = getArguments().getParcelable(ARGS_BUYER);
            product = getArguments().getParcelable(ARGS_PRODUCT);
        }
        View view = inflater.inflate(R.layout.dialog_order, null);
        txtJumlah = (EditText) view.findViewById(R.id.txtJumlah);


        builder.setView(view).setTitle("Masukkan jumlah").setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

            }
        }).setPositiveButton("ORDER", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (isNumeric(txtJumlah.getText().toString())) {
                    doAddOrder();
                } else {
                    Toast.makeText(context, "Data Invalid", Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();
            }
        });

        return builder.create();
    }
}
