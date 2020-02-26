package com.djarum.directsales.View;


import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.djarum.directsales.Interface.IItemClickListener;
import com.djarum.directsales.Model.Order;
import com.djarum.directsales.R;
import com.djarum.directsales.ViewHolder.OrderViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 */
public class OrderDatabaseFragment extends Fragment {
    public RecyclerView rvOrderList;
    public RecyclerView.LayoutManager layoutManager;

    FirebaseDatabase database;
    DatabaseReference order;
    FirebaseUser firebaseUser;


    public OrderDatabaseFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_order_database, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        database = FirebaseDatabase.getInstance();
        order = database.getReference("Order");
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        rvOrderList = (RecyclerView) view.findViewById(R.id.rvOrderDatabaseList);
        rvOrderList.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity());
        rvOrderList.setLayoutManager(layoutManager);
        load();
    }

    private void load() {

        FirebaseRecyclerOptions<Order> options = new FirebaseRecyclerOptions.Builder<Order>().setQuery(order, Order.class).build();
        final FirebaseRecyclerAdapter<Order, OrderViewHolder> adapter = new FirebaseRecyclerAdapter<Order, OrderViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final OrderViewHolder holder, int position, @NonNull final Order model) {
                holder.txtOrderId.setText("Order #" + model.getOrderId());
                holder.txtOrderAddress.setText(model.getAddress());
                holder.txtOrderBuyerName.setText(model.getBuyerName());
                holder.btnDeliver.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        order.orderByChild("orderId").equalTo(model.getOrderId()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    for (DataSnapshot datas : dataSnapshot.getChildren()) {
                                        String key = datas.getKey();
                                        Order orderItem = datas.getValue(Order.class);
                                        Boolean isDelivered = !orderItem.getDelivered();
                                        order.child(key).child("delivered").setValue(isDelivered);
//                                        if (!isDelivered) {
//                                            holder.layout.setBackgroundColor(Color.parseColor("#ff303030"));
//                                        } else {
//                                            holder.layout.setBackgroundColor(Color.parseColor("#008F11"));
//                                        }
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                    }
                });

                holder.btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        order.orderByChild("orderId").equalTo(model.getOrderId()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    for (DataSnapshot datas : dataSnapshot.getChildren()) {
                                        String key = datas.getKey();
                                        Order orderItem = datas.getValue(Order.class);
                                        order.child(key).child("cancelled").setValue(!orderItem.getCancelled());
//                                        if (!orderItem.getCancelled()) {
//                                            holder.layout.setBackgroundColor(Color.parseColor("#ff303030"));
//                                        } else {
//                                            holder.layout.setBackgroundColor(Color.parseColor("#ff0000"));
//                                        }
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                });
                holder.setItemClickListener(new IItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        Toast.makeText(getActivity(), "order database", Toast.LENGTH_SHORT).show();
                    }
                });
                if (model.getDelivered()) {
                    holder.layout.setBackgroundColor(Color.parseColor("#008F11"));
                    holder.btnCancel.setVisibility(View.INVISIBLE);
                    holder.btnDeliver.setVisibility(View.VISIBLE);
                } else if (model.getCancelled()) {
                    holder.layout.setBackgroundColor(Color.parseColor("#ff0000"));
                    holder.btnDeliver.setVisibility(View.INVISIBLE);
                    holder.btnCancel.setVisibility(View.VISIBLE);
                } else if (!model.getCancelled() && !model.getDelivered()) {
                    holder.layout.setBackgroundColor(Color.parseColor("#ff303030"));
                    holder.btnCancel.setVisibility(View.VISIBLE);
                    holder.btnDeliver.setVisibility(View.VISIBLE);
                }
            }

            @NonNull
            @Override
            public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_order_list, parent, false);
                return new OrderViewHolder(view);
            }
        };

        adapter.startListening();
        rvOrderList.setAdapter(adapter);

    }


    public static String getTimeDate(long timestamp) {
        try {
            DateFormat dateFormat = DateFormat.getDateTimeInstance();
            Date netDate = (new Date(timestamp));
            return dateFormat.format(netDate);
        } catch (Exception e) {
            return "date";
        }
    }

}
