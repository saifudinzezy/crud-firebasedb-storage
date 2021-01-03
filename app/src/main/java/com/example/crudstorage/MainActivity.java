package com.example.crudstorage;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;

    private Button btnChooseImage, btnUpload;
    private TextView txtShowUploads;
    private EditText fileName;
    private ImageView imageView;
    private ProgressBar progressBar;

    private Uri imageUri;

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

        btnChooseImage.setOnClickListener(v -> {
            openFileChooser();
        });

        btnUpload.setOnClickListener(v -> {

        });

        txtShowUploads.setOnClickListener(v -> {

        });
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
                && data != null && data.getData() != null){
            imageUri = data.getData();
            Picasso.get().load(imageUri).into(imageView);
        }
    }
}