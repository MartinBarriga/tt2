package com.example.martin.AndroidApp.ui.notificaciones;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.martin.AndroidApp.Notificacion;
import com.example.martin.AndroidApp.R;

import java.util.ArrayList;

public class NotificacionesRecyclerAdapter
        extends RecyclerView.Adapter<NotificacionesRecyclerAdapter.ViewHolder>{

    private final Context mContexto;
    private ArrayList<Notificacion> mNotificaciones;
    private OnNotificacionListener mOnNotificacionListener;



    public NotificacionesRecyclerAdapter(Context context, ArrayList<Notificacion> notificaciones, OnNotificacionListener onNotificacionListener) {
        mContexto = context;
        mNotificaciones = notificaciones;
        mOnNotificacionListener = onNotificacionListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater gLayoutInflater = LayoutInflater.from(mContexto);
        View itemNotificacion = gLayoutInflater.inflate(R.layout.item_notification, parent, false );
        return new ViewHolder(itemNotificacion, mOnNotificacionListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Notificacion notification = mNotificaciones.get(position);
        holder.mFecha.setText(notification.getFecha());
//        holder.mEstado.setText(notification.getEstado());
        holder.mTitulo.setText(notification.getTitulo());
        switch (notification.getEstado()){
            case 0: holder.mEstado.setText("En progreso");
                    holder.mEstado.setTextColor(ContextCompat.getColor(mContexto, R.color.notificationStateGreen));
                    break;
            case 1: holder.mEstado.setText("Terminada");
                    holder.mEstado.setTextColor(ContextCompat.getColor(mContexto, R.color.notificationStateRed));
                    break;
            case 2: holder.mEstado.setText("Alertas enviadas");
                    holder.mEstado.setTextColor(ContextCompat.getColor(mContexto, R.color.notificationStateRed));
                    break;
            case 3: holder.mEstado.setText("Cancelado manualmente");
                    holder.mEstado.setTextColor(ContextCompat.getColor(mContexto, R.color.notificationStateRed));
                    break;
            case 4: holder.mEstado.setText("No requirió envío de alertas");
                    holder.mEstado.setTextColor(ContextCompat.getColor(mContexto, R.color.notificationStateRed));
                    break;
        }
        if(!notification.getLeido()){
            holder.mLeido.setColorFilter(ContextCompat.getColor(mContexto, R.color.leidoColorTrue));
        }
        else{
            holder.mLeido.setColorFilter(ContextCompat.getColor(mContexto,R.color.leidoColorFalse));
            holder.mLeido.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return mNotificaciones.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mFecha;
        public TextView mEstado;
        public TextView mTitulo;
        public ImageView mLeido;
        OnNotificacionListener onNotificacionListener;

        public ViewHolder(@NonNull View itemView, final OnNotificacionListener onNotificacionListener) {
            super(itemView);
            mFecha = (TextView) itemView.findViewById(R.id.fechaNotificacion);
            mEstado = (TextView) itemView.findViewById(R.id.estadoNotificacion);
            mTitulo = (TextView) itemView.findViewById(R.id.tituloNotificacion);
            mLeido = (ImageView) itemView.findViewById(R.id.leidoNotificacion);
            this.onNotificacionListener = onNotificacionListener;
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onNotificacionListener.onNotificacionClick(getAdapterPosition());
                }
            });
        }


    }
    public interface OnNotificacionListener {
        void onNotificacionClick(int position);
    }

}
