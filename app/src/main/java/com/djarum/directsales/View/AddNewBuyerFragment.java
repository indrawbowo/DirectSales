package com.djarum.directsales.View;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
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
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

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


public class AddNewBuyerFragment extends Fragment {
    private static final String TAG = AddNewBuyerFragment.class.getSimpleName();
    public static final int REQUEST_IMAGE = 100;
    private final static String ARGS_BUYER = "BUYER";

    private EditText txtFullName, txtEmail, txtDomisili, txtAddress, txtPhone, txtNotes, curr;
    private RadioButton rbLakilaki, rbPerempuan;
    private RadioGroup rgGender;
    private ImageButton btnScanNama, btnScanDomisili, btnScanAddress;
    private ImageView ivPhoto;
    private Button btnAdd, btnChoosePhoto;
    private ProgressDialog mProgressDialog;
    private TesseractOCR mTessOCR;
    private Uri uri;
    Buyer buyer;
    StorageReference storageRef;
    FirebaseDatabase database;
    DatabaseReference table_buyer;

    public AddNewBuyerFragment() {

    }

    public static AddNewBuyerFragment newInstance() {
        AddNewBuyerFragment fragment = new AddNewBuyerFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_add_new_buyer, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        txtFullName = (EditText) view.findViewById(R.id.txtFullName);
        txtEmail = (EditText) view.findViewById(R.id.txtEmail);
        txtDomisili = (EditText) view.findViewById(R.id.txtDomisili);
        txtAddress = (EditText) view.findViewById(R.id.txtAddress);
        txtPhone = (EditText) view.findViewById(R.id.txtPhoneNumber);
        txtNotes = (EditText) view.findViewById(R.id.txtNotes);
        rbLakilaki = (RadioButton) view.findViewById(R.id.rbLakilaki);
        rbPerempuan = (RadioButton) view.findViewById(R.id.rbPerempuan);
        rgGender = (RadioGroup) view.findViewById(R.id.rgGender);
        btnAdd = (Button) view.findViewById(R.id.btnAdd);
        btnChoosePhoto = (Button) view.findViewById(R.id.btnChoosePhotoBuyer);
        btnScanNama = (ImageButton) view.findViewById(R.id.btn_scan_name);
        btnScanAddress = (ImageButton) view.findViewById(R.id.btn_scan_address);
        btnScanDomisili = (ImageButton) view.findViewById(R.id.btn_scan_domisili);
        ivPhoto = (ImageView) view.findViewById(R.id.ivPhoto);
        clearField();
        init();
    }

    private void clearField() {
        txtFullName.getText().clear();
        rgGender.clearCheck();
        txtEmail.getText().clear();
        txtAddress.getText().clear();
        txtPhone.getText().clear();
        txtNotes.getText().clear();
        txtDomisili.getText().clear();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private void init() {
        buyer = new Buyer();
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

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Boolean valid = true;
                if (txtFullName.getText().toString().trim().isEmpty()) {
                    txtFullName.setError("Nama tidak boleh kosong");
                    valid = false;
                }
                if (rgGender.getCheckedRadioButtonId() == -1) {
                    Toast.makeText(getActivity(), "Pilih gender", Toast.LENGTH_SHORT).show();
                    valid = false;
                }
                if (txtEmail.getText().toString().trim().isEmpty()) {
                    txtEmail.setError("Email tidak boleh kosong");
                    valid = false;

                }
                if (txtDomisili.getText().toString().trim().isEmpty()) {
                    txtDomisili.setError("Domisili tidak boleh kosong");
                    valid = false;

                }
                if (txtPhone.getText().toString().trim().isEmpty()) {
                    txtPhone.setError("Nomor telpon tidak boleh kosong");
                    valid = false;
                }
                if (valid) {
                    doAddNewUser();
                }
            }
        });
        storageRef = FirebaseStorage.getInstance().getReference();
        database = FirebaseDatabase.getInstance();
        table_buyer = database.getReference("Buyer");
        table_buyer.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                buyer.setBuyerId(String.valueOf(dataSnapshot.getChildrenCount() + 1));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        btnChoosePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImagePickerOptions(200);
            }
        });
    }

    private void doAddNewUser() {
        final ProgressDialog mDialog = new ProgressDialog(getActivity());
        mDialog.setMessage("Please wait...");
        mDialog.show();
        table_buyer.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(txtPhone.getText().toString()).exists()) {
                    mDialog.dismiss();
                    Toast.makeText(getActivity(), "Already exist", Toast.LENGTH_SHORT).show();
                } else {
                    mDialog.dismiss();
                    buyer.setAlamat(txtAddress.getText().toString());
                    buyer.setDomisili(txtDomisili.getText().toString().toUpperCase());
                    buyer.setEmail(txtEmail.getText().toString());
                    buyer.setNama(txtFullName.getText().toString());
                    buyer.setPhoneNumber(txtPhone.getText().toString());
                    if (rbLakilaki.isChecked()) {
                        buyer.setGender("Laki-Laki");
                    } else if (rbPerempuan.isChecked()) {
                        buyer.setGender("Perempuan");
                    }
                    if(!txtNotes.getText().toString().isEmpty()){
                        buyer.setNotes(txtNotes.getText().toString());
                    }

                    table_buyer.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot newDataSnapshot) {
                            buyer.setBuyerId(String.valueOf(newDataSnapshot.getChildrenCount() + 1));

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                    buyer.setPhotoURL(doUpload(uri, buyer.getBuyerId()));
                    Toast.makeText(getActivity(), "Add success", Toast.LENGTH_SHORT).show();

                    clearField();
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage("Apakah anda ingin melakukan order?").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Bundle bundle = new Bundle();
                            bundle.putParcelable(ARGS_BUYER, buyer);
                            ProductListFragment productListFragment = new ProductListFragment();
                            productListFragment.setArguments(bundle);
                            FragmentTransaction transaction = getFragmentManager().beginTransaction();
                            transaction.add(R.id.rootLayout, productListFragment).commit();
                            transaction.addToBackStack(BuyerListFragment.class.getSimpleName());
                        }
                    }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    }).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

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
                uri = data.getParcelableExtra("path");
                ivPhoto.setImageURI(uri);

//                doUpload(uri, buyer.getBuyerId());
            }
        }
    }

    private String doUpload(final Uri uris, String id) {
        final String[] photoUrl = new String[1];

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

                table_buyer.orderByChild("buyerId").equalTo(buyer.getBuyerId()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot datas : dataSnapshot.getChildren()) {
                                final String key = datas.getKey();
                                storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        photoUrl[0] = uri.toString();
                                        buyer.setPhotoURL(photoUrl[0]);
                                        table_buyer.push().setValue(buyer);

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
        return photoUrl[0];
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
}
