package com.gtappdevelopers.wallpaperapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class ColorRVAdapter extends RecyclerView.Adapter<ColorRVAdapter.ViewHolder> {
    //creating variables for array list and context and interface.
    private ArrayList<ColorRVModal> colorRVModals;
    private Context context;
    private ColorRVAdapter.ColorClickInterface colorClickInterface;

    public ColorRVAdapter(ArrayList<ColorRVModal> colorRVModals, Context context, ColorRVAdapter.ColorClickInterface colorClickInterface) {
        this.colorRVModals = colorRVModals;
        this.context = context;
        this.colorClickInterface = colorClickInterface;
    }

    @NonNull
    @Override
    public ColorRVAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflating our layout file on below line.
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.color_rv_item, parent, false);
        return new ColorRVAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ColorRVAdapter.ViewHolder holder, int position) {
        //setting data to all our views.
        ColorRVModal modal = colorRVModals.get(position);
        holder.colorTV.setText(modal.getColor());
        if (!modal.getImgUrl1().isEmpty()) {
            Glide.with(context).load(modal.getImgUrl1()).into(holder.colorIV);

        } else {
            holder.colorIV.setImageResource(R.drawable.ic_launcher_background);
        }
        //adding on click listner to item view on below line.
        holder.itemView.setOnClickListener(v -> {
            //passing position with interface.
            colorClickInterface.onColorClick(position);
        });

    }


    @Override
    public int getItemCount() {
        return colorRVModals.size();
    }



    //creating an interface on below line.
    public interface ColorClickInterface {
        void onColorClick(int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        //creating variables on below line.
        private TextView colorTV;
        private ImageView colorIV;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            //initializing all variables on below line.
            colorIV = itemView.findViewById(R.id.idIVColor);
            colorTV = itemView.findViewById(R.id.idTVColor);
        }
    }

}