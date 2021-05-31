package com.example.dokulocker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Random;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class MainActivity extends AppCompatActivity implements RecyclerViewClickInterface {
    private static final String TAG = "MainActivity";
    private static final int PICK_IMAGE = 100;
    private ArrayList<String> list = new ArrayList<>();
    File mydir;
    Button button;
    RecyclerView recyclerView;
    RecyclerAdapter recyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mydir = new File(getFilesDir()+File.separator);
        button = findViewById(R.id.btnTambah);

        // baca file dari direktori
        File[] files = mydir.listFiles();
        for (int i = 0; i < files.length; i++){
            list.add(files[i].getName());
        }

        recyclerView = findViewById(R.id.recyclerview);
        recyclerAdapter = new RecyclerAdapter(list,mydir,this);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(recyclerAdapter);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this,DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);

        button.setOnClickListener(v -> {
            // check permission
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent,PICK_IMAGE); // jump to onActivityResult Uri
            } else if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.READ_EXTERNAL_STORAGE)){
                new AlertDialog.Builder(this)
                        .setTitle("Permission Needed")
                        .setMessage("This Permission is needed because the way that app work")
                        .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(MainActivity.this,new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},1);
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .create()
                        .show();
            } else {
                ActivityCompat.requestPermissions(MainActivity.this,new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},1);
            }
        });

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    // swipe gesture
    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            int position = viewHolder.getAdapterPosition();
            switch (direction) {
                case ItemTouchHelper.LEFT:
                    // delete file
                    File path = new File(mydir + "/" + list.get(position));
                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle("Deleting File")
                            .setMessage("Are You Sure?")
                            .setPositiveButton("Yes", (dialog, which) -> {
                                if (path.exists()){
                                    list.remove(position);
                                    if (path.delete()) {
                                        recyclerAdapter.notifyItemRemoved(position);
                                    }
                                }
                            })
                            .setNegativeButton("No", (dialog, which) -> recyclerAdapter.notifyItemChanged(position))
                            .create().show();
                    break;
            }
        }

        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    .addSwipeLeftBackgroundColor(ContextCompat.getColor(MainActivity.this,R.color.design_default_color_error))
                    .addSwipeLeftActionIcon(R.drawable.ic_baseline_delete_24)
                    .create()
                    .decorate();
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    };

    // permission for accessing gallery
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        if (requestCode == 1){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                startActivityForResult(intent,PICK_IMAGE);
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // get image from file
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK){
            Uri uri = null;
            if (data != null){
                uri = data.getData();// mendapatkan image dengan uri
                
                if (mydir.exists()){
                    // membuat nama string dengan random
                    Random generator = new Random();
                    int n = 10000;
                    n = generator.nextInt(n);
                    String fname = uri.getLastPathSegment()+".jpg";

                    // menargetkan direktori file
                    File file = new File (mydir, fname);

                    try {
                        // tulis file
                        FileOutputStream out = new FileOutputStream(file);
                        Bitmap finalBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri); // melempar image ke bitmap
                        Log.d(TAG, String.valueOf(finalBitmap));
                        finalBitmap.compress(Bitmap.CompressFormat.JPEG,90, out); // mengcompress file ke image
                        out.flush();
                        out.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    // refresh module
                    Intent i = new Intent(this, MainActivity.class);
                    finish();
                    overridePendingTransition(0, 0);
                    startActivity(i);
                    overridePendingTransition(0, 0);
                }
            }
        }
    }

    // doing something when item clicked
    @Override
    public void onItemClick(int position) {
        // lempar ke intent baru dan tampilkan gambarnya
        Intent intent = new Intent(this,MainActivity2.class);
        intent.putStringArrayListExtra("list",list);
        intent.putExtra("position",position);
        startActivity(intent);
        //Toast.makeText(this, list.get(position), Toast.LENGTH_SHORT).show();
    }
}