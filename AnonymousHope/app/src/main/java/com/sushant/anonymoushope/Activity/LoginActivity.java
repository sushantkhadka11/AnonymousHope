package com.sushant.anonymoushope.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sushant.anonymoushope.MainActivity;
import com.sushant.anonymoushope.Model.User;
import com.sushant.anonymoushope.R;

public class LoginActivity extends AppCompatActivity {

    TextInputLayout email, password;
    Button btnLogin;
    TextView btnRegister, forgetPassword;
    FirebaseAuth firebaseAuth;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        email = findViewById(R.id.tvLogEmail);
        password = findViewById(R.id.tvLogPassword);
        btnLogin= findViewById(R.id.btnLogin);
        firebaseAuth = FirebaseAuth.getInstance();
        forgetPassword = findViewById(R.id.forgotPassword);
        btnRegister = findViewById(R.id.btnCreateAccount);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog= new ProgressDialog(LoginActivity.this);

                String emailTxt = email.getEditText().getText().toString();
                String passwordTxt = password.getEditText().getText().toString();
                if(TextUtils.isEmpty(emailTxt)){
                    email.setError("Email Not entered");
                    email.requestFocus();
                }
                else if(TextUtils.isEmpty(passwordTxt)){
                    password.setError("Password not Entered");
                    password.requestFocus();
                }
                else if(!emailTxt.matches(emailPattern)){
                    email.setError("Email format incorrect");
                    email.requestFocus();
                }

                else if(passwordTxt.length()<8){
                    password.setError("Password length at least 8 characters");
                    password.requestFocus();
                }
                else{
                    progressDialog.setTitle("Checking...");
                    progressDialog.show();
                    if(emailTxt.equals("admin@admin.com")&&passwordTxt.equals("admin123"))
                    {
                        Intent intent = new Intent(getApplicationContext(),AdminDashboardActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        progressDialog.dismiss();
                    }
                    else
                    {
                        firebaseAuth.signInWithEmailAndPassword(emailTxt, passwordTxt)
                                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users")
                                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                                            reference.addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    if(snapshot.exists())
                                                    {
                                                        User user =snapshot.getValue(User.class);
                                                        if(user.getAccepted()==true)
                                                        {
                                                            progressDialog.dismiss();
                                                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                                                            startActivity(intent);
                                                            finish();
                                                        }else 
                                                        {
                                                            progressDialog.dismiss();

                                                            Toast.makeText(getApplicationContext(), "Admin has not accepted your account yet.", Toast.LENGTH_SHORT).show();
                                                        }
                                                    
                                                    }
                                                    else
                                                    {
                                                        progressDialog.dismiss();
                                                        Toast.makeText(LoginActivity.this, "Authentication Failed", Toast.LENGTH_SHORT).show();
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });



                                        } else {
                                            progressDialog.dismiss();
                                            Toast.makeText(LoginActivity.this, "Authentication Failed", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    }

                }

            }
        });
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this,SignupActivity.class);
                startActivity(intent);
            }
        });
        forgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),ResetPasswordActivity.class);
                startActivity(intent);
            }
        });

    }

}