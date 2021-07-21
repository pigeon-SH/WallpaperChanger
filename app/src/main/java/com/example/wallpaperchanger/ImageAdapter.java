package com.example.wallpaperchanger;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {
    private ArrayList<Uri> items = null;
    private Context mContext = null;
    ImageAdapter(ArrayList<Uri> list, Context context){
        items = list;
        mContext = context;
    }
    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView image;
        TextView text;
        ViewHolder(View itemView){
            super(itemView);
            image = itemView.findViewById(R.id.image);
            text = itemView.findViewById(R.id.textview_pos);
        }
    }
    @Override
    public ImageAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.image_item, parent, false);
        ImageAdapter.ViewHolder vh = new ImageAdapter.ViewHolder(view);
        return vh;
    }
    @Override
    public void onBindViewHolder(ImageAdapter.ViewHolder holder, int position){
        Uri image_uri = items.get(position);
        holder.image.setImageURI(image_uri);
        holder.text.setText("Pos: " + Integer.toString(position));
    }
    @Override
    public int getItemCount(){
        return items.size();
    }
    public void changeItems(ArrayList<Uri> lists){
        items = lists;
        this.notifyDataSetChanged();
    }
}
