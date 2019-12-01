package com.djarum.directsales.View;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.djarum.directsales.Interface.IItemClickListener;
import com.djarum.directsales.Model.Buyer;
import com.djarum.directsales.Model.Product;
import com.djarum.directsales.R;
import com.djarum.directsales.ViewHolder.ProductViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProductListFragment extends Fragment {
    final private static String ARGS_BUYER = "BUYER";
    private final static String ARGS_PRODUCT = "PRODUCT";
    final private static String ARGS_READ = "READ";
    final private static String TAG = ProductListFragment.class.getSimpleName();

    public RecyclerView rvProductList;
    public RecyclerView.LayoutManager layoutManager;
    public Buyer buyer;

    FirebaseDatabase database;
    DatabaseReference product;
    FirebaseUser firebaseUser;
    Boolean readOnly=false;

    public ProductListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_product_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        database = FirebaseDatabase.getInstance();
        product = database.getReference("Product");
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if(!readOnly) {
            Toolbar toolbar = (Toolbar) view.findViewById(R.id.fragment_product_toolbar);
            toolbar.setTitle("Pilih product");
            toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getFragmentManager().popBackStack();
                }
            });
        }
        if (getArguments() != null) {
            buyer = (Buyer) getArguments().getParcelable(ARGS_BUYER);
            readOnly = getArguments().getBoolean(ARGS_READ);
        }

        rvProductList = (RecyclerView) view.findViewById(R.id.rvProductList);
        rvProductList.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity());
        rvProductList.setLayoutManager(layoutManager);
        load();
    }

    private void load() {
        FirebaseRecyclerOptions<Product> options = new FirebaseRecyclerOptions.Builder<Product>().setQuery(product, Product.class).build();
        FirebaseRecyclerAdapter<Product, ProductViewHolder> adapter = new FirebaseRecyclerAdapter<Product, ProductViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ProductViewHolder holder, int position, @NonNull final Product model) {
                holder.txtProductName.setText(model.getNama());
                holder.txtProductPrice.setText(String.valueOf(model.getHarga()));
                Picasso.get().load(model.getPhotoURL()).into(holder.imgProduct);
//                final Product clicked = model;
                holder.setItemClickListener(new IItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        Bundle bundle = new Bundle();
                        bundle.putParcelable(ARGS_BUYER, buyer);
                        bundle.putParcelable(ARGS_PRODUCT, model);

                        DialogOrderFragment dialogOrderFragment = new DialogOrderFragment();
                        dialogOrderFragment.setArguments(bundle);

                        dialogOrderFragment.show(getFragmentManager(), TAG);
//                        Toast.makeText(ProductList.this,Session.currentBuyer.getNama(), Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @NonNull
            @Override
            public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_product_list, parent, false);
                return new ProductViewHolder(view);
            }
        };
        adapter.startListening();
        rvProductList.setAdapter(adapter);

    }
}
