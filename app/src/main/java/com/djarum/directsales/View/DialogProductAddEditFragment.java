package com.djarum.directsales.View;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.djarum.directsales.Model.Product;
import com.djarum.directsales.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

/**
 * A simple {@link Fragment} subclass.
 */
public class DialogProductAddEditFragment extends DialogFragment {
    public static final int REQUEST_IMAGE = 100;
    private static final String ARGS_PRODUCT_DATABASE = "PRODUCT_DATABASE";
    String photoUrl;
    private ProgressDialog mProgressDialog;
    StorageReference storageRef;
    FirebaseDatabase database;
    Product selectedProduct;
    DatabaseReference product;
    Boolean isAdd;

    public DialogProductAddEditFragment() {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Button btnAddProduct = (Button) view.findViewById(R.id.btnAddProduct);
        Button btnChoosePhoto = (Button) view.findViewById(R.id.btnChoosePhoto);
        product = FirebaseDatabase.getInstance().getReference().child("Product");

        final EditText txtProductNameAddEdit = (EditText) view.findViewById(R.id.txtProductNameAddEdit);
        final EditText txtProductPriceAddEdit = (EditText) view.findViewById(R.id.txtProductPriceAddEdit);
        final EditText txtProductStockAddEdit = (EditText) view.findViewById(R.id.txtProductStockAddEdit);
        isAdd = getArguments() == null;

        if (!isAdd) {
            selectedProduct = (Product) getArguments().getParcelable(ARGS_PRODUCT_DATABASE);
            btnAddProduct.setText("EDIT");
            txtProductNameAddEdit.setText(selectedProduct.getNama());
            txtProductPriceAddEdit.setText(String.valueOf(selectedProduct.getHarga()));
            txtProductStockAddEdit.setText(String.valueOf(selectedProduct.getStock()));
        } else {
            selectedProduct = new Product();
            product.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    selectedProduct.setProductId(String.valueOf(dataSnapshot.getChildrenCount() + 1));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            btnAddProduct.setText("ADD");
        }
        storageRef = FirebaseStorage.getInstance().getReference();

        btnChoosePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImagePickerOptions();
            }
        });

        btnAddProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                product.orderByChild("productId").equalTo(selectedProduct.getProductId()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot datas : dataSnapshot.getChildren()) {
                                String key = datas.getKey();
                                String harga = txtProductPriceAddEdit.getText().toString();
                                String nama = txtProductNameAddEdit.getText().toString();
                                String stock = txtProductStockAddEdit.getText().toString();
                                product.child(key).child("harga").setValue(Integer.valueOf(harga));
                                product.child(key).child("nama").setValue(nama);
                                product.child(key).child("stock").setValue(Integer.valueOf(stock));
                            }
                        } else {
                            selectedProduct.setNama(txtProductNameAddEdit.getText().toString());
                            selectedProduct.setHarga(Integer.valueOf(txtProductPriceAddEdit.getText().toString()));
                            selectedProduct.setStock(Integer.valueOf(txtProductStockAddEdit.getText().toString()));
                            product.push().setValue(selectedProduct);
                            if (isAdd) {
                                product.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        selectedProduct.setProductId(String.valueOf(dataSnapshot.getChildrenCount() + 1));
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }


                });
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_product_add_edit, container, false);
    }

    private void showImagePickerOptions() {
        ImagePickerActivity.showImagePickerOptions(getActivity(), new ImagePickerActivity.PickerOptionListener() {
            @Override
            public void onTakeCameraSelected() {
                launchCameraIntent();
            }

            @Override
            public void onChooseGallerySelected() {
                launchGalleryIntent();
            }
        });
    }

    private void launchCameraIntent() {
        Intent intent = new Intent(getActivity(), ImagePickerActivity.class);
        intent.putExtra(ImagePickerActivity.INTENT_IMAGE_PICKER_OPTION, ImagePickerActivity.REQUEST_IMAGE_CAPTURE);

        // setting aspect ratio
        intent.putExtra(ImagePickerActivity.INTENT_LOCK_ASPECT_RATIO, false);
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_X, 1); // 16x9, 1x1, 3:4, 3:2
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_Y, 1);

        // setting maximum bitmap width and height
        intent.putExtra(ImagePickerActivity.INTENT_SET_BITMAP_MAX_WIDTH_HEIGHT, true);
        intent.putExtra(ImagePickerActivity.INTENT_BITMAP_MAX_WIDTH, 1000);
        intent.putExtra(ImagePickerActivity.INTENT_BITMAP_MAX_HEIGHT, 1000);

        startActivityForResult(intent, REQUEST_IMAGE);
    }

    private void launchGalleryIntent() {
        Intent intent = new Intent(getActivity(), ImagePickerActivity.class);
        intent.putExtra(ImagePickerActivity.INTENT_IMAGE_PICKER_OPTION, ImagePickerActivity.REQUEST_GALLERY_IMAGE);

        // setting aspect ratio
        intent.putExtra(ImagePickerActivity.INTENT_LOCK_ASPECT_RATIO, false);
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_X, 1); // 16x9, 1x1, 3:4, 3:2
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_Y, 1);
        startActivityForResult(intent, REQUEST_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                Uri uri = data.getParcelableExtra("path");
                doUpload(uri, selectedProduct.getProductId());
            }
        }
    }

    private void doUpload(final Uri uris, String id) {
        if (mProgressDialog == null) {
            mProgressDialog = ProgressDialog.show(getActivity(), "Processing",
                    "Doing Upload...", true);
        } else {
            mProgressDialog.show();
        }


        final StorageReference storageReference = storageRef.child("product/images/" + id + ".jpg");

        storageReference.putFile(uris).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                product.orderByChild("productId").equalTo(selectedProduct.getProductId()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot datas : dataSnapshot.getChildren()) {
                                final String key = datas.getKey();
                                storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        photoUrl = uri.toString();
                                        product.child(key).child("photoURL").setValue(photoUrl);
                                    }
                                });
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }


                });
                mProgressDialog.dismiss();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                mProgressDialog.dismiss();
            }
        });
        mProgressDialog.dismiss();
    }
}


