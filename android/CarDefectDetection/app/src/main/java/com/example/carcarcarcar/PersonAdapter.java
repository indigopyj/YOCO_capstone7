package com.example.carcarcarcar;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

interface OnPersonItemClickListener {
    public void onItemClick(PersonAdapter.ViewHolder holder, View view, int position);
}

public class PersonAdapter extends RecyclerView.Adapter<PersonAdapter.ViewHolder> implements OnPersonItemClickListener {
    ArrayList<History> items = new ArrayList<History>();
    OnPersonItemClickListener listener;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View itemView = inflater.inflate(R.layout.person_item, viewGroup, false);
        return new ViewHolder(itemView, this);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        History item = items.get(position);
        viewHolder.setItem(item);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setOnItemClicklistener(OnPersonItemClickListener listener) {
        this.listener = listener;
    }

    @Override
    public void onItemClick(ViewHolder holder, View view, int position) {
        if (listener != null) {
            listener.onItemClick(holder, view, position);
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView caridtxt;
        TextView rentdatetxt;
        TextView returndatetxt;


        public ViewHolder(View itemView, final OnPersonItemClickListener listener) {
            super(itemView);
            caridtxt = itemView.findViewById(R.id.caridText);
            rentdatetxt = itemView.findViewById(R.id.rentdateText);
            returndatetxt=itemView.findViewById(R.id.returndateText);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (listener != null) {
                        listener.onItemClick(ViewHolder.this, v, position);
                    }
                }
            });
        }

        public void setItem(History item) {
            caridtxt.setText("차량 번호 : "+ item.getCar_id());
            rentdatetxt.setText("대여 일시 : "+item.getRent_date());
            returndatetxt.setText("반납 일시 : "+item.getReturn_date());

        }
    }

    public void addItem(History item) {
        items.add(item);
    }

    public void setItems(ArrayList<History> items) {
        this.items = items;
    }

    public History getItem(int position) {
        return items.get(position);
    }

    public void setItem(int position, History item) {
        items.set(position, item);
    }

   }
