package com.example.crudstorage.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.crudstorage.R;
import com.example.crudstorage.adapter.ImageAdapter;
import com.example.crudstorage.model.Upload;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class ImageActivity extends AppCompatActivity implements ImageAdapter.OnItemClickListener {
    private RecyclerView recyclerView;
    private ImageAdapter adapter;

    private FirebaseStorage firebaseStorage;
    private DatabaseReference databaseReference;
    private ValueEventListener dbListener;

    private List<Upload> uploads;

    private ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        progressBar = findViewById(R.id.progress_bar);
        recyclerView = findViewById(R.id.rv);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        uploads = new ArrayList<>();

        adapter = new ImageAdapter(ImageActivity.this, uploads);
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(ImageActivity.this);

        firebaseStorage = FirebaseStorage.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("uploads");
        dbListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                uploads.clear();

                for (DataSnapshot postSnap : snapshot.getChildren()) {
                    Upload upload = postSnap.getValue(Upload.class);
                    //get key from daabase
                    upload.setKey(postSnap.getKey());
                    uploads.add(upload);
                }

                adapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ImageActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onItemClick(int position) {
        Toast.makeText(this, "normal clicked : " + position, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onWhatEverClicked(int position) {
        Toast.makeText(this, "whatever", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDeleteClicked(int position) {
        Upload selectedItem = uploads.get(position);
        String selectedKey = selectedItem.getKey();

        //deleted storage
        StorageReference imageReference = firebaseStorage.getReferenceFromUrl(selectedItem.getImageUrl());
        imageReference.delete().addOnSuccessListener(aVoid -> {
          databaseReference.child(selectedKey).removeValue();
            Toast.makeText(this, "Item deleted", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        databaseReference.removeEventListener(dbListener);
    }
}