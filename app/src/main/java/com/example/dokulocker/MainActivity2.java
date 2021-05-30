package com.example.dokulocker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

public class MainActivity2 extends AppCompatActivity {
    File mydir;
    private ArrayList<String> list;
    int position;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        imageView = findViewById(R.id.imgView);

        mydir = new File(getFilesDir()+File.separator);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Intent intent = getIntent();
        list = intent.getStringArrayListExtra("list");
        position = intent.getIntExtra("position",0);

        imageView.setImageURI(Uri.fromFile(new File(mydir + "/" + list.get(position))));
    }
}