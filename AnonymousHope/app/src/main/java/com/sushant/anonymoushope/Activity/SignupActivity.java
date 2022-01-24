package com.sushant.anonymoushope.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.sushant.anonymoushope.Model.User;
import com.sushant.anonymoushope.R;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;


public class SignupActivity extends AppCompatActivity {


    Button btnRegister, btnUpdate;
    FirebaseAuth firebaseAuth;
    ProgressBar progressBar;
    Spinner genderSpinner;
    TextInputLayout firstname, lastname, email, password,confirmPassword, phoneNumber, bio,accountName ,accountNumber,bankName;
    ProgressDialog progressDialog;
    CircleImageView circleImageView;
    StorageReference storageReference;
    private StorageTask<UploadTask.TaskSnapshot> uploadsTask;
    String img = "Default";
    String imgBioMetrics = "Default";
    String activity = null;
    FirebaseUser firebaseUser;
    Boolean click = false;
    Button btnUploadMetrics;
    TextView tvMetrics;
    String uID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        btnRegister = findViewById(R.id.btnRegister);
        btnUpdate = findViewById(R.id.btnUpdate);
        firebaseAuth = FirebaseAuth.getInstance();
        firstname = findViewById(R.id.tvFirstName);
        lastname = findViewById(R.id.tvLastName);
        password = findViewById(R.id.tvPassword);
        confirmPassword = findViewById(R.id.tvConfirmPassword);
        email = findViewById(R.id.tvEmail);
        phoneNumber = findViewById(R.id.tvPhoneNumber);
        bio = findViewById(R.id.tvBio);
        bankName = findViewById(R.id.tvBankName);
        accountName = findViewById(R.id.tvAccountName);
        accountNumber = findViewById(R.id.tvAccountNumber);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        circleImageView = findViewById(R.id.addProfileImage);
        contextOfApplication = getApplicationContext();
        genderSpinner = findViewById(R.id.genderSpinner);
        tvMetrics = findViewById(R.id.tv_biometrics);
        btnUploadMetrics = findViewById(R.id.btnUploadMetrics);
        storageReference = FirebaseStorage.getInstance().getReference("ProfileImages");
        progressBar = findViewById(R.id.progressbarregister);
        String arrayGender[] = {"Gender","Male","Female"};
        ArrayAdapter batchAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, arrayGender);
        genderSpinner.setAdapter(batchAdapter);
        progressDialog= new ProgressDialog(SignupActivity.this);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String fName = firstname.getEditText().getText().toString();
                String lName = lastname.getEditText().getText().toString();
                String emailText = email.getEditText().getText().toString();
                String passwordText = password.getEditText().getText().toString();
                String confirmPasswordText = confirmPassword.getEditText().getText().toString();
                String phoneNo = phoneNumber.getEditText().getText().toString();
                String bioText = bio.getEditText().getText().toString();
                String gender = genderSpinner.getSelectedItem().toString();
                String actName = accountName.getEditText().getText().toString();
                String actNumber = accountNumber.getEditText().getText().toString();
                String bnkName = bankName.getEditText().getText().toString();


                if(TextUtils.isEmpty(fName)){
                    firstname.setError("Email Not entered");
                    firstname.requestFocus();

                    return;
                }
                else if(TextUtils.isEmpty(lName)){
                    lastname.setError("Full name not Entered");
                    lastname.requestFocus();
                    return;
                }
                else if (TextUtils.isEmpty(emailText))
                {
                    email.setError("Email improper format");
                    email.requestFocus();
                    return;
                }
                else if(TextUtils.isEmpty(phoneNo)){
                    phoneNumber.setError("Password not Entered");
                    phoneNumber.requestFocus();
                    return;
                }
                else if(!passwordText.equals(confirmPasswordText)){
                    confirmPassword.setError("Password doesn't matches");
                    confirmPassword.requestFocus();
                    return;
                }
                else if (TextUtils.isEmpty(bioText))
                {
                    bio.setError("Email improper format");
                    bio.requestFocus();
                    return;
                }
                else if (TextUtils.isEmpty(actName))
                {
                    accountName.setError("Bank account name is mandatory.");
                    accountName.requestFocus();
                    return;
                }
                else if (TextUtils.isEmpty(actNumber))
                {
                    accountNumber.setError("Bank account number is mandatory.");
                    accountNumber.requestFocus();
                    return;
                }
                else
                {
                    progressDialog.setTitle("Uploading...");
                    progressDialog.show();
                    firebaseAuth.createUserWithEmailAndPassword(emailText,passwordText).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                User user = new User(FirebaseAuth.getInstance().getCurrentUser().getUid(),fName,lName,emailText,phoneNo,gender,bioText,imgBioMetrics,bnkName,actName,actNumber,passwordText,img,"0","0",false,false);

                                FirebaseDatabase.getInstance().getReference("Users")
                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .setValue(user);

                                uploadImage(FirebaseAuth.getInstance().getCurrentUser().getUid());
                                uploadBiometrics(FirebaseAuth.getInstance().getCurrentUser().getUid());
                                progressDialog.dismiss();
                                Toast.makeText(getApplicationContext(), "User created", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                startActivity(intent);
                            } else {
                                Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                            }
                        }
                    });
                }


            }
        });
        circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                click = true;
                openImage();
            }
        });
        btnUploadMetrics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                openImage();
            }
        });

        Intent intent = getIntent();
        activity = intent.getStringExtra("activity");

        if(!(activity == null))
        {
            btnRegister.setVisibility(View.GONE);
            btnUpdate.setVisibility(View.VISIBLE);

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);
                    firstname.getEditText().setText(user.getFirstName());
                    lastname.getEditText().setText(user.getLastName());
                    email.getEditText().setText(user.getEmail());
                    bio.getEditText().setText(user.getBIO());
                    phoneNumber.getEditText().setText(user.getPhoneNumber());
                    for(int i= 0; i < genderSpinner.getAdapter().getCount(); i++)
                    {
                        if(genderSpinner.getAdapter().getItem(i).toString().contains(user.getGender()))
                        {
                            genderSpinner.setSelection(i);
                        }
                    }
                    accountName.getEditText().setText(user.getAccountName());
                    accountNumber.getEditText().setText(user.getAccountNumber());
                    bankName.getEditText().setText(user.getBankName());
                    tvMetrics.setText(user.getBioMetrics());
                    if(user.getProfileImage().equals("Default"))
                    {
                        circleImageView.setImageResource(R.drawable.download);
                    }
                    else
                    {
                        Glide.with(getApplicationContext()).load(user.getProfileImage()).into(circleImageView);
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String fName = firstname.getEditText().getText().toString();
                String lName = lastname.getEditText().getText().toString();
                String emailText = email.getEditText().getText().toString();
                String passwordText = password.getEditText().getText().toString();
                String confirmPasswordText = confirmPassword.getEditText().getText().toString();
                String phoneNo = phoneNumber.getEditText().getText().toString();
                String bioText = bio.getEditText().getText().toString();
                String gender = genderSpinner.getSelectedItem().toString();
                String actName = accountName.getEditText().getText().toString();
                String actNumber = accountNumber.getEditText().getText().toString();
                if(TextUtils.isEmpty(fName)){
                    firstname.setError("Email Not entered");
                    firstname.requestFocus();

                    return;
                }
                else if(TextUtils.isEmpty(lName)){
                    lastname.setError("Full name not Entered");
                    lastname.requestFocus();
                    return;
                }
                else if (TextUtils.isEmpty(emailText))
                {
                    email.setError("Email improper format");
                    email.requestFocus();
                    return;
                }
                else if(TextUtils.isEmpty(phoneNo)){
                    phoneNumber.setError("Password not Entered");
                    phoneNumber.requestFocus();
                    return;
                }
                else if (TextUtils.isEmpty(bioText))
                {
                    bio.setError("Email improper format");
                    bio.requestFocus();
                    return;
                }

                else if (TextUtils.isEmpty(actName))
                {
                    accountName.setError("Bank account name is mandatory.");
                    accountName.requestFocus();
                    return;
                }
                else if (TextUtils.isEmpty(actNumber))
                {
                    accountNumber.setError("Bank account number is mandatory.");
                    accountNumber.requestFocus();
                    return;
                }
                else if (passwordText.equals(confirmPasswordText))
                {

                    progressDialog.setTitle("Uploading...");
                    progressDialog.show();
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("email",email.getEditText().getText().toString());
                    hashMap.put("firstName",firstname.getEditText().getText().toString());
                    hashMap.put("lastName",lastname.getEditText().getText().toString());
                    hashMap.put("password",password.getEditText().getText().toString());
                    hashMap.put("gender",genderSpinner.getSelectedItem().toString());
                    hashMap.put("phoneNumber",phoneNumber.getEditText().getText().toString());
                    hashMap.put("profileImage",img);
                    hashMap.put("bio",bioText);
                    hashMap.put("bioMetrics",imgBioMetrics);
                    hashMap.put("accountName",actName);
                    hashMap.put("accountNumber",actNumber);
                    databaseReference.updateChildren(hashMap);


                    uploadImage(firebaseUser.getUid());
                    uploadBiometrics(firebaseUser.getUid());

                    firstname.getEditText().setText("");
                    lastname.getEditText().setText("");
                    password.getEditText().setText("");
                    confirmPassword.getEditText().setText("");
                    email.getEditText().setText("");
                    phoneNumber.getEditText().setText("");
                    bio.getEditText().setText("");
                    tvMetrics.setText("");
                    progressDialog.dismiss();
                    Toast.makeText(SignupActivity.this, "Update completed", Toast.LENGTH_SHORT).show();


                }
                else
                {
                    Toast.makeText(SignupActivity.this, "Passwords didn't match", Toast.LENGTH_SHORT).show();
                }

            }
        });


    }
    private Uri imageURl;
    private Uri biometricsUrl;
    public static Context contextOfApplication;
    public static Context getContextOfApplication()
    {
        return contextOfApplication;
    }


    private void openImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,1);
    }
    public String getFileExtension(Uri uri)
    {
        Context applicationContext = getContextOfApplication();
        ContentResolver contentResolver = applicationContext.getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if( resultCode == RESULT_OK && data != null )
        {
            if(click == true)
            {
                imageURl =data.getData();
                circleImageView.setImageURI(imageURl);
                click = false;
            }
            else
            {
                biometricsUrl =data.getData();
                tvMetrics.setText(biometricsUrl+"");
            }

        }

    }

    private void uploadImage(String uID)
    {
        if(imageURl !=null)
        {
            final StorageReference fileReference = storageReference.child(System.currentTimeMillis()
                    +"."+getFileExtension(imageURl));
            uploadsTask =fileReference.putFile(imageURl);
            uploadsTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>(){
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if(!task.isSuccessful())
                    {
                        throw task.getException();
                    }
                    return fileReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {

                    Uri downloadUri = task.getResult();
                    String mUri = downloadUri.toString();
                    if (task.isSuccessful())
                    {
                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                        HashMap<String,Object> hashMap = new HashMap<>();
                        hashMap.put("profileImage",mUri);
                        reference.child("Users").child(uID).updateChildren(hashMap);
                        progressBar.setVisibility(View.GONE);
                        img = mUri;
                    }
                    else
                    {

                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }
            });
        }

    }

    private void uploadBiometrics(String uID) {
        if(biometricsUrl !=null)
        {
            final StorageReference fileReference = storageReference.child(System.currentTimeMillis()
                    +"."+getFileExtension(biometricsUrl));
            uploadsTask =fileReference.putFile(biometricsUrl);
            uploadsTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>(){
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if(!task.isSuccessful())
                    {
                        throw task.getException();
                    }
                    return fileReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {

                    Uri downloadUri = task.getResult();
                    String mUri = downloadUri.toString();
                    if (task.isSuccessful())
                    {
                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                        HashMap<String,Object> hashMap = new HashMap<>();
                        hashMap.put("bioMetrics",mUri);
                        reference.child("Users").child(uID).updateChildren(hashMap);
                        progressBar.setVisibility(View.GONE);
                        imgBioMetrics = mUri;
                    }
                    else
                    {

                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }
            });
        }
    }
}