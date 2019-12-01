package com.djarum.directsales.View;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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


public class BuyerListFragment extends Fragment {
    public RecyclerView rvBuyerList;
    public RecyclerView.LayoutManager layoutManager;
    final private static String ARGS_BUYER = "BUYER";
    final private static String ARGS_READ = "READ";

    FirebaseDatabase database;
    DatabaseReference buyer;
    FirebaseUser firebaseUser;
    Boolean readOnly=false;


    public BuyerListFragment() {
    }

    public static BuyerListFragment newInstance() {
        BuyerListFragment fragment = new BuyerListFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_buyer_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(getArguments()!=null){
            readOnly = getArguments().getBoolean(ARGS_READ);
        }
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.fragment_buyer_toolbar);
        toolbar.setTitle("Pilih buyer");
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStack();
            }
        });
        database = FirebaseDatabase.getInstance();
        buyer = database.getReference("Buyer");
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        rvBuyerList = (RecyclerView) view.findViewById(R.id.rvBuyerList);
        rvBuyerList.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity());
        rvBuyerList.setLayoutManager(layoutManager);
        load();
    }

    private void load(){
        Query query = buyer.orderByChild("nama");
        FirebaseRecyclerOptions<Buyer> options = new FirebaseRecyclerOptions.Builder<Buyer>().setQuery(query, Buyer.class).build();
        FirebaseRecyclerAdapter<Buyer, BuyerViewHolder> adapter = new FirebaseRecyclerAdapter<Buyer, BuyerViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull BuyerViewHolder holder, int position, @NonNull final Buyer model) {
                holder.txtBuyerName.setText(model.getNama());
                holder.txtBuyerEmail.setText(model.getEmail());
                holder.txtBuyerPhone.setText(model.getPhoneNumber());
                Picasso.get().load(model.getPhotoURL()).into(holder.imgBuyer);
                if(!readOnly) {
                    holder.setItemClickListener(new IItemClickListener() {
                        @Override
                        public void onClick(View view, int position, boolean isLongClick) {
                            Bundle bundle = new Bundle();
                            bundle.putParcelable(ARGS_BUYER, model);
                            Fragment productListFragment = new ProductListFragment();
                            productListFragment.setArguments(bundle);
                            FragmentTransaction transaction = getFragmentManager().beginTransaction();
                            transaction.add(R.id.rootLayout, productListFragment).commit();
                            transaction.addToBackStack(BuyerListFragment.class.getSimpleName());
                        }
                    });
                } else {
                    holder.setItemClickListener(new IItemClickListener() {
                        @Override
                        public void onClick(View view, int position, boolean isLongClick) {
                            Toast.makeText(getActivity(), "yyyyyy", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
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

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}
