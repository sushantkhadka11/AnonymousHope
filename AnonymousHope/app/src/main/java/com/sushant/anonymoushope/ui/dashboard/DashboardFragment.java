package com.sushant.anonymoushope.ui.dashboard;

import static android.app.Activity.RESULT_OK;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.sushant.anonymoushope.MainActivity;
import com.sushant.anonymoushope.Model.User;
import com.sushant.anonymoushope.R;

import java.util.ArrayList;
import java.util.HashMap;

public class DashboardFragment extends Fragment {

    ImageButton addPhoto, addVideo;
    ImageView eventImage;
    Button btnPost;
    FirebaseUser firebaseUser;
    StorageReference storageReference;
    EditText content;
    private Uri imageURl;
    VideoView videoView;
    private StorageTask<UploadTask.TaskSnapshot> uploadsTask;
    ProgressDialog progressDialog;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view=  inflater.inflate(R.layout.fragment_dashboard, container, false);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        addPhoto = view.findViewById(R.id.addPhoto);
        addVideo = view.findViewById(R.id.addVideo);
        eventImage = view.findViewById(R.id.showEventImage);
        content = view.findViewById(R.id.addEventContent);
        btnPost = view.findViewById(R.id.btnPostEvent);
        videoView = view.findViewById(R.id.videoView);
        storageReference = FirebaseStorage.getInstance().getReference("Post");
        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog= new ProgressDialog(getContext());

                if(content.getText().equals(null) && imageURl == null)
                {
                    Toast.makeText(getContext(), "Please provide content.", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    uploadMedia();
                }
            }
        });
        addPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openImage();
            }
        });
        addVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openVideo();
            }
        });

        return view;
    }
    private void openImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,1);
    }
    private void openVideo() {
        Intent intent = new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,5);
    }
    Uri videouri;

    public String getFileExtension(Uri uri)
    {
        Context applicationContext = MainActivity.getContextOfApplication();
        ContentResolver contentResolver = applicationContext.getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if( requestCode ==1 && resultCode == RESULT_OK && data != null )
        {
            imageURl =data.getData();
            eventImage.setVisibility(View.VISIBLE);
            videoView.setVisibility(View.GONE);
            eventImage.setImageURI(imageURl);
        }
        if (requestCode == 5 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            videouri = data.getData();
            eventImage.setVisibility(View.GONE);
            videoView.setVisibility(View.VISIBLE);
            videoView.setVideoURI(videouri);
            videoView.start();
            videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.setLooping(true);
                }
            });

        }
    }
    private String getfiletype(Uri videouri) {
        Context applicationContext = MainActivity.getContextOfApplication();
        ContentResolver r = applicationContext.getContentResolver();
        // get the file type ,in this case its mp4
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(r.getType(videouri));
    }

    private void uploadMedia()
    {
        progressDialog.setTitle("Uploading...");
        progressDialog.show();
        final String text = content.getText().toString();
        if(videouri!=null && imageURl!=null)
        {
            videouri=null;
            imageURl=null;
            Toast.makeText(getContext(), "Can only upload one media video or image.", Toast.LENGTH_SHORT).show();
            Toast.makeText(getContext(), "Please select again.", Toast.LENGTH_SHORT).show();
            return;
        }
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
                    String text = content.getText().toString();

                    if (task.isSuccessful())
                    {
                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
                        String postId = reference.push().getKey();
                        HashMap<String,Object> hashMap = new HashMap<>();
                        hashMap.put("content",text);
                        hashMap.put("media",mUri);
                        hashMap.put("userId",firebaseUser.getUid());
                        hashMap.put("postId",postId);
                        hashMap.put("type","image");
                        hashMap.put("accepted",false);
                        reference.child(postId).setValue(hashMap);
                        Toast.makeText(getContext(), "Uploaded", Toast.LENGTH_SHORT).show();
                        content.setText("");
                        eventImage.setVisibility(View.GONE);
                    }
                    else
                    {
                        Toast.makeText(getContext(), "Failed..", Toast.LENGTH_SHORT).show();
                    }
                    progressDialog.dismiss();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                    eventImage.setVisibility(View.GONE);
                }
            });
        }
        else if ( videouri != null)
        {
            final StorageReference reference = storageReference.child(System.currentTimeMillis()
                    +"."+getfiletype(videouri));
            reference.putFile(videouri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                    while (!uriTask.isSuccessful()) ;
                    // get the link of video
                    String downloadUri = uriTask.getResult().toString();
                    DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("Posts");
                    HashMap<String, String> map = new HashMap<>();
                    String postId = reference1.push().getKey();
                    HashMap<String,Object> hashMap = new HashMap<>();
                    hashMap.put("content",text);
                    hashMap.put("media",downloadUri);
                    hashMap.put("userId",firebaseUser.getUid());
                    hashMap.put("postId",postId);
                    hashMap.put("type","video");
                    hashMap.put("accepted",false);
                    reference1.child(postId).setValue(hashMap);
                    Toast.makeText(getContext(), "Uploaded", Toast.LENGTH_SHORT).show();
                    reference1.child("" + System.currentTimeMillis()).setValue(map);
                    // Video uploaded successfully
                    // Dismiss dialog
                    progressDialog.dismiss();
                    Toast.makeText(getContext(), "Video Uploaded!!", Toast.LENGTH_SHORT).show();
                    videoView.setVisibility(View.GONE);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    // Error, Image not uploaded
                    progressDialog.dismiss();
                    Toast.makeText(getContext(), "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    videoView.setVisibility(View.GONE);

                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                // Progress Listener for loading
                // percentage on the dialog box
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    // show the progress bar
                    double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());

                }
            });
        }
        else {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
            String postId = reference.push().getKey();
            HashMap<String,Object> hashMap = new HashMap<>();
            hashMap.put("postId",postId);
            hashMap.put("content",text);
            hashMap.put("media","Blank");
            hashMap.put("userId",firebaseUser.getUid());
            hashMap.put("type","text");
            hashMap.put("accepted",false);
            reference.child("Posts").child(postId).setValue(hashMap);
            Toast.makeText(getContext(), "Uploaded", Toast.LENGTH_SHORT).show();
            content.setText("");
            eventImage.setVisibility(View.GONE);
            videoView.setVisibility(View.GONE);
            progressDialog.dismiss();
        }

    }

}