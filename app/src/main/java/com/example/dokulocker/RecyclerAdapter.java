package com.example.dokulocker;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> implements Filterable {
    private static final String TAG = "MainActivity";
    private RecyclerViewClickInterface recyclerViewClickInterface;
    private ArrayList<String> list;
    private ArrayList<String> listAll;
    File file;

    public RecyclerAdapter(ArrayList<String> list,File file,RecyclerViewClickInterface recyclerViewClickInterface){
        this.list = list;
        this.file = file;
        this.recyclerViewClickInterface = recyclerViewClickInterface;
        this.listAll = new ArrayList<>(list);
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

    @Override
    public Filter getFilter() {
        return filter;
    }

    Filter filter = new Filter() {
        // run on background thread
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<String> filteredList = new ArrayList<>();

            if (constraint.toString().isEmpty()){
                filteredList.addAll(listAll);
            } else {
                for (String item: listAll){
                    if (item.toLowerCase().contains(constraint.toString().toLowerCase())){
                        filteredList.add(item);
                    }
                }
            }

            FilterResults filterResults = new FilterResults();
            filterResults.values = filteredList;

            return filterResults;
        }
        // run on UI thread
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            list.clear();
            list.addAll((Collection<? extends String>) results.values);
            notifyDataSetChanged();
        }
    };

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
