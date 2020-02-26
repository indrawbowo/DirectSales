package com.djarum.directsales.View;


import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.djarum.directsales.Model.Buyer;
import com.djarum.directsales.R;
import com.djarum.directsales.TesseractOCR;
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
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class DialogBuyerAddEditFragment extends DialogFragment {

    private EditText txtFullName, txtEmail, txtDomisili, txtAddress, txtPhone, txtNotes,curr;
    private RadioButton rbLakilaki, rbPerempuan;
    private RadioGroup rgGender;
    private ImageButton btnScanNama, btnScanDomisili, btnScanAddress;
    private ImageView ivPhotoAddEdit;
    private Button btnAddProduct, btnChoosePhoto;
    private TesseractOCR mTessOCR;

    public static final int REQUEST_IMAGE = 100;
    private static final String ARGS_BUYER_DATABASE = "BUYER_DATABASE";
    String photoUrl;
    private ProgressDialog mProgressDialog;
    StorageReference storageRef;
    FirebaseDatabase database;
    Buyer selectedBuyer;
    DatabaseReference buyer;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        buyer = FirebaseDatabase.getInstance().getReference().child("Buyer");

        btnAddProduct = (Button) view.findViewById(R.id.btnEditBuyer);
        btnChoosePhoto = (Button) view.findViewById(R.id.btnChoosePhotoAddEdit);
        txtFullName = (EditText) view.findViewById(R.id.txtFullNameAddEdit);
        txtEmail = (EditText) view.findViewById(R.id.txtEmailAddEdit);
        txtDomisili = (EditText) view.findViewById(R.id.txtDomisiliAddEdit);
        txtAddress = (EditText) view.findViewById(R.id.txtAddressAddEdit);
        txtPhone = (EditText) view.findViewById(R.id.txtPhoneNumberAddEdit);
        txtNotes = (EditText) view.findViewById(R.id.txtNotesAddEdit);
        rbLakilaki = (RadioButton) view.findViewById(R.id.rbLakilakiAddEdit);
        rbPerempuan = (RadioButton) view.findViewById(R.id.rbPerempuanAddEdit);
        rgGender = (RadioGroup) view.findViewById(R.id.rgGenderAddEdit);
        btnScanNama = (ImageButton) view.findViewById(R.id.btn_scan_name_add_edit);
        btnScanAddress = (ImageButton) view.findViewById(R.id.btn_scan_address_add_edit);
        btnScanDomisili = (ImageButton) view.findViewById(R.id.btn_scan_domisili_add_edit);
        ivPhotoAddEdit = (ImageView) view.findViewById(R.id.ivPhotoAddEdit);

        if (getArguments() != null) {
            selectedBuyer = (Buyer) getArguments().getParcelable(ARGS_BUYER_DATABASE);
            setData();
        }
        storageRef = FirebaseStorage.getInstance().getReference();

        btnChoosePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImagePickerOptions(200);
            }
        });

        String language = "ind";
        mTessOCR = new TesseractOCR(getActivity(), language);
        btnScanNama.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dexter.withActivity(getActivity())
                        .withPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        .withListener(new MultiplePermissionsListener() {
                            @Override
                            public void onPermissionsChecked(MultiplePermissionsReport report) {
                                if (report.areAllPermissionsGranted()) {
                                    curr = txtFullName;
                                    showImagePickerOptions(100);
                                }

                                if (report.isAnyPermissionPermanentlyDenied()) {
                                    showSettingsDialog();
                                }
                            }

                            @Override
                            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                                token.continuePermissionRequest();
                            }
                        }).check();
            }
        });
        btnScanAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dexter.withActivity(getActivity())
                        .withPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        .withListener(new MultiplePermissionsListener() {
                            @Override
                            public void onPermissionsChecked(MultiplePermissionsReport report) {
                                if (report.areAllPermissionsGranted()) {
                                    curr = txtAddress;
                                    showImagePickerOptions(100);
                                }

                                if (report.isAnyPermissionPermanentlyDenied()) {
                                    showSettingsDialog();
                                }
                            }

                            @Override
                            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                                token.continuePermissionRequest();
                            }
                        }).check();
            }
        });
        btnScanDomisili.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dexter.withActivity(getActivity())
                        .withPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        .withListener(new MultiplePermissionsListener() {
                            @Override
                            public void onPermissionsChecked(MultiplePermissionsReport report) {
                                if (report.areAllPermissionsGranted()) {
                                    curr = txtDomisili;
                                    showImagePickerOptions(100);
                                }

                                if (report.isAnyPermissionPermanentlyDenied()) {
                                    showSettingsDialog();
                                }
                            }

                            @Override
                            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                                token.continuePermissionRequest();
                            }
                        }).check();
            }
        });

        btnAddProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buyer.orderByChild("buyerId").equalTo(selectedBuyer.getBuyerId()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot datas : dataSnapshot.getChildren()) {
                                String key = datas.getKey();
                                doUpload(selectedBuyer.getBuyerId(), key);
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

    private void setData() {
        txtFullName.setText(selectedBuyer.getNama());
        txtEmail.setText(selectedBuyer.getEmail());
        txtAddress.setText(selectedBuyer.getAlamat());
        txtDomisili.setText(selectedBuyer.getDomisili());
        txtNotes.setText(selectedBuyer.getNotes());
        txtPhone.setText(selectedBuyer.getPhoneNumber());
        if (selectedBuyer.getGender().equals("Perempuan")) {
            rbPerempuan.setChecked(true);
        } else if (selectedBuyer.getGender().equals("Laki-Laki")) {
            rbLakilaki.setChecked(true);
        }
        Picasso.get().load(selectedBuyer.getPhotoURL()).into(ivPhotoAddEdit);

    }


    private void showImagePickerOptions(final int requestCode) {
        ImagePickerActivity.showImagePickerOptions(getActivity(), new ImagePickerActivity.PickerOptionListener() {
            @Override
            public void onTakeCameraSelected() {
                launchCameraIntent(requestCode);
            }

            @Override
            public void onChooseGallerySelected() {
                launchGalleryIntent(requestCode);
            }
        });
    }


    private void doUpload(String id, final String key) {
        if (mProgressDialog == null) {
            mProgressDialog = ProgressDialog.show(getActivity(), "Processing",
                    "Doing Upload...", true);
        } else {
            mProgressDialog.show();
        }
        final StorageReference storageReference = storageRef.child("buyer/images/" + id + ".jpg");
        ivPhotoAddEdit.setDrawingCacheEnabled(true);
        ivPhotoAddEdit.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable) ivPhotoAddEdit.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = storageReference.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(getActivity(), "Upload Failed", Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        buyer.child(key).child("alamat").setValue(txtAddress.getText().toString());
                        buyer.child(key).child("domisili").setValue(txtDomisili.getText().toString().toUpperCase());
                        buyer.child(key).child("email").setValue(txtEmail.getText().toString());
                        if (rbLakilaki.isChecked()) {
                            buyer.child(key).child("gender").setValue("Laki-Laki");
                        } else if (rbPerempuan.isChecked()) {
                            buyer.child(key).child("gender").setValue("Perempuan");
                        }
                        buyer.child(key).child("nama").setValue(txtFullName.getText().toString());
                        buyer.child(key).child("notes").setValue(txtNotes.getText().toString());
                        buyer.child(key).child("phoneNumber").setValue(txtPhone.getText().toString());
                        buyer.child(key).child("photoURL").setValue(uri.toString());

                    }
                });
            }
        });
        mProgressDialog.dismiss();
    }


    public DialogBuyerAddEditFragment() {
    }

    private void launchCameraIntent(int requestCode) {
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

        startActivityForResult(intent, requestCode);
    }

    private void launchGalleryIntent(int requestCode) {
        Intent intent = new Intent(getActivity(), ImagePickerActivity.class);
        intent.putExtra(ImagePickerActivity.INTENT_IMAGE_PICKER_OPTION, ImagePickerActivity.REQUEST_GALLERY_IMAGE);

        // setting aspect ratio
        intent.putExtra(ImagePickerActivity.INTENT_LOCK_ASPECT_RATIO, false);
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_X, 1); // 16x9, 1x1, 3:4, 3:2
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_Y, 1);
        startActivityForResult(intent, requestCode);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                Uri uri = data.getParcelableExtra("path");
                try {
                    // You can update this bitmap to your server
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
                    // loading profile image from local cache
//                    bmpLogo.setImageBitmap(bitmap);
                    doOCR(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else {
            if (resultCode == Activity.RESULT_OK) {
                Uri uri = data.getParcelableExtra("path");
                ivPhotoAddEdit.setImageURI(uri);
//                doUpload(uri, selectedBuyer.getBuyerId());
            }
        }
    }

    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Get Permission");
        builder.setMessage("Permission Message");
        builder.setPositiveButton("Go to setting", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                openSettings();
            }
        });
        builder.setNegativeButton(getString(android.R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();

    }

    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getActivity().getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }

    private void doOCR(final Bitmap bitmap) {
        if (mProgressDialog == null) {
            mProgressDialog = ProgressDialog.show(getActivity(), "Processing",
                    "Doing OCR...", true);
        } else {
            mProgressDialog.show();
        }
        new Thread(new Runnable() {
            public void run() {
                final String srcText = mTessOCR.getOCRResult(bitmap);

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (srcText != null && !srcText.equals("")) {
                            curr.setText(srcText);
                        }
                        mProgressDialog.dismiss();
                    }
                });
            }
        }).start();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dialog_buyer_add_edit, container, false);
    }

}
