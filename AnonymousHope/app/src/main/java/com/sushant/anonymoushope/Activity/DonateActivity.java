package com.sushant.anonymoushope.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sushant.anonymoushope.MainActivity;
import com.sushant.anonymoushope.Model.Notification;
import com.sushant.anonymoushope.Model.User;
import com.sushant.anonymoushope.R;

import java.util.HashMap;
import java.util.Objects;

public class DonateActivity extends AppCompatActivity {

    TextView tvAccountName, tvAccountNumber, tvBankName;
    TextInputLayout editTextAmount, editTextNumber, editTextRemarks,editTextExpireDate, editTextSecurityCode;
    Button btnDonate, btnCancel;
    FirebaseUser firebaseUser;
    ProgressDialog progressDialog;
    String donated, received;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donate);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        tvAccountName = findViewById(R.id.tvAccountName1);
        tvAccountNumber = findViewById(R.id.tvAccountNumber1);
        tvBankName = findViewById(R.id.tvBankName1);
        editTextAmount = findViewById(R.id.DonationAmount);
        editTextNumber = findViewById(R.id.DonationAccountNumber);
        editTextRemarks = findViewById(R.id.DonationRemarks);
        editTextExpireDate = findViewById(R.id.DonationCardExpireDate);
        editTextSecurityCode = findViewById(R.id.DonationCardSecurityCode);
        btnDonate = findViewById(R.id.btnDonateAmount);
        btnCancel = findViewById(R.id.btnCancel);
        final String postId = getIntent().getStringExtra("PostId");
        final String userId  = getIntent().getStringExtra("UserId");
        progressDialog= new ProgressDialog(DonateActivity.this);


        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });
        btnDonate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String remarks = editTextRemarks.getEditText().getText().toString();
                String amount = editTextAmount.getEditText().getText().toString();
                if(TextUtils.isEmpty(amount))
                {
                    editTextAmount.setError("Place a donation amount.");
                    editTextAmount.requestFocus();
                    return;
                }
                else if (TextUtils.isEmpty(remarks))
                {
                    editTextRemarks.setError("Remarks is necessary.");
                    editTextRemarks.requestFocus();
                    return;
                }
                else if (TextUtils.isEmpty(editTextNumber.getEditText().getText().toString()))
                {
                    editTextNumber.setError("Account number is necessary.");
                    editTextNumber.requestFocus();
                    return;
                }
                else if (TextUtils.isEmpty(editTextExpireDate.getEditText().getText().toString()))
                {
                    editTextExpireDate.setError("Expiration date is necessary.");
                    editTextExpireDate.requestFocus();
                    return;
                }
                else if (TextUtils.isEmpty(editTextSecurityCode.getEditText().getText().toString()))
                {
                    editTextSecurityCode.setError("Security code is necessary.");
                    editTextSecurityCode.requestFocus();
                    return;
                }
                else
                {
                    progressDialog.setTitle("Uploading...");
                    progressDialog.show();
                    Notification notification = new Notification(false,postId,remarks,userId,true,amount);
                    FirebaseDatabase.getInstance().getReference("Notifications").child(firebaseUser.getUid()).setValue(notification);

                    int dResult = Integer.parseInt(amount)+ Integer.parseInt(donated);
                    int rResult = Integer.parseInt(amount)+ Integer.parseInt(received);
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
                    HashMap<String,Object> hashMap = new HashMap<>();
                    hashMap.put("donated",dResult+"");
                    reference.updateChildren(hashMap);

                    if(!userId.equals(firebaseUser.getUid()))
                    {
                        DatabaseReference reference2 = FirebaseDatabase.getInstance().getReference("Users").child(userId);
                        HashMap<String,Object> hashMap2 = new HashMap<>();
                        hashMap.put("received",rResult+"");
                        reference2.updateChildren(hashMap2);
                    }
                    progressDialog.dismiss();
                    Toast.makeText(DonateActivity.this, "Donated completed.", Toast.LENGTH_SHORT).show();
                    editTextAmount.getEditText().setText("");
                    editTextRemarks.getEditText().setText("");
                    editTextNumber.getEditText().setText("");
                    editTextExpireDate.getEditText().setText("");
                    editTextSecurityCode.getEditText().setText("");
                }

            }
        });


        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(userId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                User user = snapshot.getValue(User.class);
                tvAccountName.setText(user.getAccountName());
                tvBankName.setText(user.getBankName());
                tvAccountNumber.setText(user.getAccountNumber());
                donated = user.getDonated();
                received =user.getReceived();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



    }
}