package com.homeo.spacex;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.homeo.spacex.database.CrewEntity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class CrewRecyclerAdapter extends RecyclerView.Adapter<CrewRecyclerAdapter.ViewHolder> {

    List<CrewEntity> crewArrayList;
    Context context;

    public CrewRecyclerAdapter(List<CrewEntity> crewArrayList, Context context) {
        this.crewArrayList = crewArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.crew_recycler_single_item, parent, false);

        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull CrewRecyclerAdapter.ViewHolder holder, int position) {
        CrewEntity model = crewArrayList.get(position);

        holder.txtName.setText(model.getName());

        if (model.getStatus().equals("active")){
            holder.txtStatus.setTextColor(Color.GREEN);
        }else if(model.getStatus().equals("inactive")){
            holder.txtStatus.setTextColor(Color.RED);
        }

        holder.txtStatus.setText(model.getStatus());
        holder.txtAgency.setText("Agency : " + model.getAgency());

        Picasso.get().load(model.getImage())
                .error(R.drawable.ic_man)
                .placeholder(R.drawable.ic_man)
                .into(holder.imgProfile);

        holder.txtHyperlink.setMovementMethod(LinkMovementMethod.getInstance());
        holder.txtHyperlink.setText("Wikipedia : " + Html.fromHtml(model.getWikipedia()));

    }

    @Override
    public int getItemCount() {
        return crewArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView txtName, txtStatus, txtAgency, txtHyperlink;
        ImageView imgProfile;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txtName = itemView.findViewById(R.id.txtName);
            txtStatus = itemView.findViewById(R.id.txtStatus);
            txtAgency = itemView.findViewById(R.id.txtAgency);
            txtHyperlink = itemView.findViewById(R.id.txtHyperlink);
            imgProfile = itemView.findViewById(R.id.profile_image);
        }
    }
}
