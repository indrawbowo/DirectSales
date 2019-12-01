package com.djarum.directsales.View;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.djarum.directsales.Interface.IItemClickListener;
import com.djarum.directsales.Model.Buyer;
import com.djarum.directsales.Model.Product;
import com.djarum.directsales.R;
import com.djarum.directsales.ViewHolder.ProductViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProductDatabaseFragment extends Fragment {
    private static final String TAG = ProductDatabaseFragment.class.getSimpleName();
    private static final String ARGS_PRODUCT_DATABASE = "PRODUCT_DATABASE";
    public RecyclerView rvProductList;
    public RecyclerView.LayoutManager layoutManager;
    FloatingActionButton fabAddProduct;
    public Buyer buyer;

    FirebaseDatabase database;
    DatabaseReference product;
    FirebaseUser firebaseUser;


    public ProductDatabaseFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_product_database, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        database = FirebaseDatabase.getInstance();
        product = database.getReference("Product");
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        rvProductList = (RecyclerView) view.findViewById(R.id.rvProductDatabaseList);
        fabAddProduct = (FloatingActionButton) view.findViewById(R.id.fabAddProduct);
        fabAddProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogProductAddEditFragment dialogProductAddEditFragment = new DialogProductAddEditFragment();
                dialogProductAddEditFragment.show(getFragmentManager(), TAG);
            }
        });
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
                holder.setItemClickListener(new IItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        Bundle bundle = new Bundle();
                        bundle.putParcelable(ARGS_PRODUCT_DATABASE, model);
                        DialogProductAddEditFragment dialogProductAddEditFragment = new DialogProductAddEditFragment();
                        dialogProductAddEditFragment.setArguments(bundle);
                        dialogProductAddEditFragment.show(getFragmentManager(), TAG);
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
