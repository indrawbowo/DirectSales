package com.djarum.directsales.View;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.djarum.directsales.Model.Buyer;
import com.djarum.directsales.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


@Deprecated
public class SignUpActivity extends AppCompatActivity {
    private EditText txtUsername, txtFullName, txtEmail, txtDomisili, txtAddress, txtPassword, txtConfirmPassword;
    private RadioButton rbLakilaki, rbPerempuan;
    private CheckBox cbTerms;
    private Button btnRegister;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        txtUsername = (EditText) findViewById(R.id.txtUsername);
        txtFullName = (EditText) findViewById(R.id.txtFullName);
        txtEmail = (EditText) findViewById(R.id.txtEmail);
        txtDomisili = (EditText) findViewById(R.id.txtDomisili);
        txtAddress = (EditText) findViewById(R.id.txtAddress);
        txtPassword = (EditText) findViewById(R.id.txtPassword);
        rbLakilaki = (RadioButton) findViewById(R.id.rbLakilaki);
        rbPerempuan = (RadioButton) findViewById(R.id.rbPerempuan);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference table_user = database.getReference("Buyer");

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog mDialog = new ProgressDialog(SignUpActivity.this);
                mDialog.setMessage("Please wait...");
                mDialog.show();
                table_user.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.child(txtUsername.getText().toString()).exists()){
                            mDialog.dismiss();
                            Toast.makeText(SignUpActivity.this, "Username already exist", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            mDialog.dismiss();
                            Buyer buyer = new Buyer();
                            buyer.setAlamat(txtAddress.getText().toString());
                            buyer.setDomisili(txtDomisili.getText().toString());
                            buyer.setEmail(txtEmail.getText().toString());
                            if(rbLakilaki.isChecked()){
                                buyer.setGender("Laki-Laki");
                            }
                            else if (rbPerempuan.isChecked()){
                                buyer.setGender("Perempuan");
                            }
                            buyer.setNama(txtFullName.getText().toString());
                            table_user.child(txtUsername.getText().toString()).setValue(buyer);
                            Toast.makeText(SignUpActivity.this, "Sign up success", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });
    }
}
