package com.example.martin.AndroidApp.ui.dashboard;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.martin.AndroidApp.NotificationInfo;
import com.example.martin.AndroidApp.R;

import java.util.ArrayList;

public class NotificationRecyclerAdapter extends RecyclerView.Adapter<NotificationRecyclerAdapter.ViewHolder>{

    private final Context gContext;
    private ArrayList<NotificationInfo> mNotifications;
    private OnNotificationListener mOnNotificationListener;



    public NotificationRecyclerAdapter(Context context, ArrayList<NotificationInfo> notifications, OnNotificationListener onNotificationListener) {
        gContext = context;
        mNotifications = notifications;
        mOnNotificationListener = onNotificationListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater gLayoutInflater = LayoutInflater.from(gContext);
        View ivItemContacts = gLayoutInflater.inflate(R.layout.item_notification, parent, false );
        return new ViewHolder(ivItemContacts, mOnNotificationListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        NotificationInfo notification = mNotifications.get(position);
        holder.mFecha.setText(notification.getFecha());
        holder.mDescripcion.setText("Mensaje de emergencia de " + notification.getNombre());
        if(!notification.getLeido()){
            holder.mLeido.setColorFilter(ContextCompat.getColor(gContext, R.color.leidoColorTrue));
        }
        else{
            holder.mLeido.setColorFilter(ContextCompat.getColor(gContext,R.color.leidoColorFalse));
            holder.mLeido.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return mNotifications.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mFecha;
        public TextView mDescripcion;
        public ImageView mLeido;
        OnNotificationListener onNotificationListener;

        public ViewHolder(@NonNull View itemView, final OnNotificationListener onNotificationListener) {
            super(itemView);
            mFecha = (TextView) itemView.findViewById(R.id.fechaNotificacion);
            mDescripcion = (TextView) itemView.findViewById(R.id.descripcionNotificacion);
            mLeido = (ImageView) itemView.findViewById(R.id.leidoNotificacion);
            this.onNotificationListener = onNotificationListener;
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onNotificationListener.onViewClick(getAdapterPosition());
                }
            });
        }


    }
    public interface OnNotificationListener{
        void onViewClick(int position);
    }

}
