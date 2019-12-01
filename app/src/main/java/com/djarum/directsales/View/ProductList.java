package com.djarum.directsales.View;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.djarum.directsales.Interface.IItemClickListener;
import com.djarum.directsales.Model.Product;
import com.djarum.directsales.R;
import com.djarum.directsales.Session.Session;
import com.djarum.directsales.ViewHolder.ProductViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

@Deprecated
public class ProductList extends AppCompatActivity {

    public RecyclerView rvProductList;
    public RecyclerView.LayoutManager layoutManager;

    FirebaseDatabase database;
    DatabaseReference product;
    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.home_toolbar);
        setSupportActionBar(toolbar);

        database = FirebaseDatabase.getInstance();
        product = database.getReference("Product");
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        rvProductList = (RecyclerView) findViewById(R.id.rvProductList);
        rvProductList.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        rvProductList.setLayoutManager(layoutManager);
        load();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_menu,menu);
        MenuItem welcome = menu.findItem(R.id.welcome);
        welcome.setTitle("Welcome, " + firebaseUser.getDisplayName());

        return true;
    }

    private void load(){
        FirebaseRecyclerOptions<Product> options = new FirebaseRecyclerOptions.Builder<Product>().setQuery(product, Product.class).build();
        FirebaseRecyclerAdapter<Product, ProductViewHolder> adapter = new FirebaseRecyclerAdapter<Product, ProductViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ProductViewHolder holder, int position, @NonNull Product model) {
                holder.txtProductName.setText(model.getNama());
                holder.txtProductPrice.setText(model.getHarga());
                Picasso.get().load(model.getPhotoURL()).into(holder.imgProduct);
                final Product clicked = model;
                holder.setItemClickListener(new IItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        Toast.makeText(ProductList.this,Session.currentBuyer.getNama(), Toast.LENGTH_SHORT).show();
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
