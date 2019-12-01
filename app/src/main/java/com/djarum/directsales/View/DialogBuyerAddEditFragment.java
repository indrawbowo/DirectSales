package com.djarum.directsales.View;


import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
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
import android.widget.RadioButton;
import android.widget.RadioGroup;

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
    private Button btnAdd;
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
        Button btnAddProduct = (Button) view.findViewById(R.id.btnAddAddEdit);
        Button btnChoosePhoto = (Button) view.findViewById(R.id.btnChoosePhotoAddEdit);
        buyer = FirebaseDatabase.getInstance().getReference().child("Buyer");

        txtFullName = (EditText) view.findViewById(R.id.txtFullNameAddEdit);
        txtEmail = (EditText) view.findViewById(R.id.txtEmailAddEdit);
        txtDomisili = (EditText) view.findViewById(R.id.txtDomisiliAddEdit);
        txtAddress = (EditText) view.findViewById(R.id.txtAddressAddEdit);
        txtPhone = (EditText) view.findViewById(R.id.txtPhoneNumberAddEdit);
        txtNotes = (EditText) view.findViewById(R.id.txtNotesAddEdit);
        rbLakilaki = (RadioButton) view.findViewById(R.id.rbLakilakiAddEdit);
        rbPerempuan = (RadioButton) view.findViewById(R.id.rbPerempuanAddEdit);
        rgGender = (RadioGroup) view.findViewById(R.id.rgGenderAddEdit);
        btnAdd = (Button) view.findViewById(R.id.btnAddAddEdit);
        btnScanNama = (ImageButton) view.findViewById(R.id.btn_scan_name_add_edit);
        btnScanAddress = (ImageButton) view.findViewById(R.id.btn_scan_address_add_edit);
        btnScanDomisili = (ImageButton) view.findViewById(R.id.btn_scan_domisili_add_edit);

        if (getArguments() != null) {
            selectedBuyer = (Buyer) getArguments().getParcelable(ARGS_BUYER_DATABASE);
//            btnAddProduct.setText("EDIT");
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
                                String alamat = txtAddress.getText().toString();
                                String domisili = txtDomisili.getText().toString();
                                String email = txtEmail.getText().toString();
                                String nama = txtFullName.getText().toString();
                                String notes = txtNotes.getText().toString();
                                String phoneNumber = txtPhone.getText().toString();
                                if (rbLakilaki.isChecked()) {
                                    buyer.child(key).child("gender").setValue("Laki-Laki");
                                } else if (rbPerempuan.isChecked()) {
                                    buyer.child(key).child("gender").setValue("Perempuan");
                                }
                                buyer.child(key).child("alamat").setValue(alamat);
                                buyer.child(key).child("domisili").setValue(domisili);
                                buyer.child(key).child("email").setValue(email);
                                buyer.child(key).child("gender").setValue(alamat);
                                buyer.child(key).child("nama").setValue(nama);
                                buyer.child(key).child("notes").setValue(notes);
                                buyer.child(key).child("phoneNumber").setValue(phoneNumber);

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


    private void doUpload(final Uri uris, String id) {
        if (mProgressDialog == null) {
            mProgressDialog = ProgressDialog.show(getActivity(), "Processing",
                    "Doing Upload...", true);
        } else {
            mProgressDialog.show();
        }

        final StorageReference storageReference = storageRef.child("buyer/images/" + id + ".jpg");

        storageReference.putFile(uris).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                buyer.orderByChild("buyerId").equalTo(selectedBuyer.getBuyerId()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot datas : dataSnapshot.getChildren()) {
                                final String key = datas.getKey();
                                storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        photoUrl = uri.toString();
                                        buyer.child(key).child("photoURL").setValue(photoUrl);
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
                doUpload(uri, selectedBuyer.getBuyerId());
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
