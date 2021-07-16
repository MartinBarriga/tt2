package com.example.martin.AndroidApp.ui.contacts;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.martin.AndroidApp.R;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

public class ContactsRecyclerAdapter extends RecyclerView.Adapter<ContactsRecyclerAdapter.ViewHolder>{

    private final Context gContext;
    private ArrayList<ContactsInfo> gContacts;
    private OnContactListener mOnContactListener;



    public ContactsRecyclerAdapter(Context context, ArrayList<ContactsInfo> contacts, OnContactListener onContactListener) {
        gContext = context;
        gContacts = contacts;
        mOnContactListener = onContactListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater gLayoutInflater = LayoutInflater.from(gContext);
        View ivItemContacts = gLayoutInflater.inflate(R.layout.item_contact, parent, false );
        return new ViewHolder(ivItemContacts, mOnContactListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ContactsInfo contact = gContacts.get(position);
        holder.gContactNameTV.setText(contact.getName());
        holder.gContactPhoneTV.setText(contact.getPhoneNumber());
        if(contact.getIsNotificationSelected()){
            holder.gNotificationIV.setColorFilter(ContextCompat.getColor(gContext,R.color.notificationColorTrue));
        }
        else{
            holder.gNotificationIV.setColorFilter(ContextCompat.getColor(gContext,R.color.notificationColorFalse));
        }
        if(contact.getIsMessageSelected()){
            holder.gMessageIV.setColorFilter(ContextCompat.getColor(gContext,R.color.messageColorTrue));
        }
        else{
            holder.gMessageIV.setColorFilter(ContextCompat.getColor(gContext,R.color.messageColorFalse));
        }


    }

    @Override
    public int getItemCount() {
        return gContacts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView gContactNameTV;
        public TextView gContactPhoneTV;
        public ImageView gMessageIV;
        public ImageView gNotificationIV;
        OnContactListener onContactListener;

        public ViewHolder(@NonNull View itemView, final OnContactListener onContactListener) {
            super(itemView);
            gContactNameTV = (TextView) itemView.findViewById(R.id.tv_contact_name);
            gContactPhoneTV = (TextView) itemView.findViewById(R.id.tv_contact_phone);
            gMessageIV = (ImageView) itemView.findViewById(R.id.iv_message);
            gNotificationIV = (ImageView) itemView.findViewById(R.id.iv_notification);
            this.onContactListener = onContactListener;
            gContactNameTV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onContactListener.onNameClick(getAdapterPosition());
                }
            });
            gContactPhoneTV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onContactListener.onNameClick(getAdapterPosition());
                }
            });
            gMessageIV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onContactListener.onMessageClick(getAdapterPosition());
                }
            });
            gNotificationIV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onContactListener.onNotificationClick(getAdapterPosition());
                }
            });
        }


    }
    public interface OnContactListener{
        void onNameClick(int position);
        void onMessageClick(int position);
        void onNotificationClick(int position);
    }

}
