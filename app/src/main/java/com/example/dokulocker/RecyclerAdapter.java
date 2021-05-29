package com.example.dokulocker;

import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {
    private static final String TAG = "MainActivity";
    private RecyclerViewClickInterface recyclerViewClickInterface;
    private ArrayList<String> list;
    File file;

    public RecyclerAdapter(ArrayList<String> list,File file,RecyclerViewClickInterface recyclerViewClickInterface){
        this.list = list;
        this.file = file;
        this.recyclerViewClickInterface = recyclerViewClickInterface;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.row_item,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder; //return new ViewHolder(view)
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.textView.setText(list.get(position));
        holder.imageView.setImageURI(Uri.fromFile(new File(file + "/" + list.get(position))));
    }

    @Override
    public int getItemCount() { // untuk mereturn data
        return (list!= null) ? list.size() : 0; // jika punya data 200 maka return 200
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            textView = itemView.findViewById(R.id.textView);

            itemView.setOnClickListener(v -> {
                recyclerViewClickInterface.onItemClick(getLayoutPosition());
            });
        }
    }
}
