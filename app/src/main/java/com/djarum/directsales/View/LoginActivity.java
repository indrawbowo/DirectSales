package com.djarum.directsales.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.djarum.directsales.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity {
    private EditText txtUserName, txtPassword;
    private Button btnLogin;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        if(firebaseUser!=null){
            Intent home = new Intent(LoginActivity.this, HomeActivity.class);
            startActivity(home);
            finish();
        } else {
            initial();
            animateStart();
        }
    }

    private void initial() {


        txtUserName = findViewById(R.id.txtUsername);
        txtPassword = findViewById(R.id.txtPassword);
        btnLogin = findViewById(R.id.btnLogin);



        FirebaseDatabase database = FirebaseDatabase.getInstance();


        btnLogin.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                final String email = txtUserName.getText().toString().trim();
                String password = txtPassword.getText().toString().trim();
                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Please insert your username and password", Toast.LENGTH_SHORT).show();
                } else {
                    final ProgressDialog mDialog = new ProgressDialog(LoginActivity.this);
                    mDialog.setMessage("Please wait...");
                    mDialog.show();
                    firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            mDialog.dismiss();
                            if(!task.isSuccessful()){
                                Toast.makeText(LoginActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                            else {
                                int index = email.indexOf("@");
                                String subEmail = email.substring(0,index);
                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setDisplayName(subEmail).build();
                                user.updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Intent home = new Intent(LoginActivity.this, HomeActivity.class);
                                        startActivity(home);
                                    }
                                });
                            }
                        }
                    });

//                    firebaseAuth.createUserWithEmailAndPassword(txtUserName.getText().toString(), txtPassword.getText().toString()).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
//                        @Override
//                        public void onComplete(@NonNull Task<AuthResult> task) {
//                            if(!task.isSuccessful()){
//
//
//                            } else {
//                                Toast.makeText(LoginActivity.this, "Sukses", Toast.LENGTH_SHORT).show();
//
//                            }
//                            mDialog.dismiss();
//                        }
//                    });
//                    table_user.addValueEventListener(new ValueEventListener() {
//
//                        @Override
//                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                            mDialog.dismiss();
//                            if (dataSnapshot.child(txtUserName.getText().toString()).exists()) {
//
//                                Buyer user = dataSnapshot.child(txtUserName.getText().toString()).getValue(Buyer.class);
//                                if (user.getPassword().equals(txtPassword.getText().toString())) {
//                                    Toast.makeText(LoginActivity.this, "Sign in Success", Toast.LENGTH_SHORT).show();
//                                    Session.currentBuyer = user;
//                                    Intent home = new Intent(LoginActivity.this, ProductList.class);
//                                    startActivity(home);
//                                    finish();
//                                } else {
//                                    Toast.makeText(LoginActivity.this, "Wrong password", Toast.LENGTH_SHORT).show();
//                                }
//                            } else {
//                                Toast.makeText(LoginActivity.this, "Buyer not exist", Toast.LENGTH_SHORT).show();
//                            }
//                        }
//
//                        @Override
//                        public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                        }
//                    });
                }
            }
        });

    }


    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        Window window = getWindow();
        window.setFormat(PixelFormat.RGBA_8888);
    }


    private void animateStart() {
        Animation a = AnimationUtils.loadAnimation(this, R.anim.animation);
        a.reset();
        LinearLayout ly = (LinearLayout) findViewById(R.id.splash);
        ly.clearAnimation();
        ly.startAnimation(a);

        a.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                txtUserName.setVisibility(View.INVISIBLE);
                txtPassword.setVisibility(View.INVISIBLE);
                btnLogin.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        txtUserName.setVisibility(View.VISIBLE);
                        txtPassword.setVisibility(View.VISIBLE);
                        btnLogin.setVisibility(View.VISIBLE);
                    }
                }, 0);

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });


    }

    @Override
    public void onBackPressed() {

    }
}
