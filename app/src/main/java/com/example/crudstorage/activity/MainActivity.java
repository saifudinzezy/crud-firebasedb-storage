package com.example.crudstorage.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.crudstorage.R;
import com.example.crudstorage.model.Upload;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;

    private Button btnChooseImage, btnUpload;
    private TextView txtShowUploads;
    private EditText fileName;
    private ImageView imageView;
    private ProgressBar progressBar;

    private Uri imageUri;

    private StorageReference storageReference;
    private DatabaseReference databaseReference;

    private StorageTask uploadTaks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnChooseImage = findViewById(R.id.btn_chooese_image);
        btnUpload = findViewById(R.id.btn_upload);
        txtShowUploads = findViewById(R.id.txt_show_upload);
        fileName = findViewById(R.id.file_name);
        imageView = findViewById(R.id.image);
        progressBar = findViewById(R.id.progress_bar);

        storageReference = FirebaseStorage.getInstance().getReference("uploads");
        databaseReference = FirebaseDatabase.getInstance().getReference("uploads");

        btnChooseImage.setOnClickListener(v -> {
            openFileChooser();
        });

        btnUpload.setOnClickListener(v -> {
            if (uploadTaks != null && uploadTaks.isInProgress()) Toast.makeText(this, "Upload in progress", Toast.LENGTH_SHORT).show(); else uploadFile();
        });

        txtShowUploads.setOnClickListener(v -> {
            openImageActivity();
        });
    }

    private void openImageActivity() {
        startActivity(new Intent(this, ImageActivity.class));
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            imageUri = data.getData();
            Picasso.get().load(imageUri).into(imageView);
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void uploadFile() {
        if (imageUri != null) {
            StorageReference fileReference = storageReference.child(System.currentTimeMillis()
                    + "." + getFileExtension(imageUri));

            uploadTaks = fileReference.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        Handler handler = new Handler();
                        handler.postDelayed(() -> {
                            progressBar.setProgress(0);
                        }, 500);

                        Toast.makeText(this, "Upload successful", Toast.LENGTH_SHORT).show();

                        fileReference.getDownloadUrl()
                                .addOnSuccessListener(uri -> {
                                    Upload upload = new Upload(fileName.getText().toString().trim(), uri.toString());

                                    String uploadId = databaseReference.push().getKey();
                                    databaseReference.child(uploadId).setValue(upload);
                                });
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    })
                    .addOnProgressListener(snapshot -> {
                        double progress = (100.0 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                        progressBar.setProgress((int) progress);
                    });
        } else {
            Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();
        }
    }
}