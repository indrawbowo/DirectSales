package com.djarum.directsales.View;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.djarum.directsales.Interface.IItemClickListener;
import com.djarum.directsales.Model.Buyer;
import com.djarum.directsales.R;
import com.djarum.directsales.ViewHolder.BuyerViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

/**
 * A simple {@link Fragment} subclass.
 */
public class BuyerDatabaseFragment extends Fragment {
    public RecyclerView rvBuyerList;
    public RecyclerView.LayoutManager layoutManager;
    private static final String ARGS_BUYER_DATABASE = "BUYER_DATABASE";
    private static final String TAG = BuyerDatabaseFragment.class.getSimpleName();

    FirebaseDatabase database;
    DatabaseReference buyer;
    FirebaseUser firebaseUser;


    public BuyerDatabaseFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_buyer_database, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        database = FirebaseDatabase.getInstance();
        buyer = database.getReference("Buyer");
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        rvBuyerList = (RecyclerView) view.findViewById(R.id.rvBuyerDatabaseList);
        rvBuyerList.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity());
        rvBuyerList.setLayoutManager(layoutManager);
        load();
    }


    private void load() {
        Query query = buyer.orderByChild("nama");
        FirebaseRecyclerOptions<Buyer> options = new FirebaseRecyclerOptions.Builder<Buyer>().setQuery(query, Buyer.class).build();
        FirebaseRecyclerAdapter<Buyer, BuyerViewHolder> adapter = new FirebaseRecyclerAdapter<Buyer, BuyerViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull BuyerViewHolder holder, int position, @NonNull final Buyer model) {
                holder.txtBuyerName.setText(model.getNama());
                holder.txtBuyerEmail.setText(model.getEmail());
                holder.txtBuyerPhone.setText(model.getPhoneNumber());
                Picasso.get().load(model.getPhotoURL()).into(holder.imgBuyer);
                holder.setItemClickListener(new IItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        Bundle bundle = new Bundle();
                        bundle.putParcelable(ARGS_BUYER_DATABASE, model);
                        DialogBuyerAddEditFragment dialogBuyerAddEditFragment = new DialogBuyerAddEditFragment();
                        dialogBuyerAddEditFragment.setArguments(bundle);
                        dialogBuyerAddEditFragment.show(getFragmentManager(), TAG);
                    }
                });
            }

            @NonNull
            @Override
            public BuyerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_buyer_list, parent, false);
                return new BuyerViewHolder(view);
            }
        };
        adapter.startListening();
        rvBuyerList.setAdapter(adapter);

    }
}
