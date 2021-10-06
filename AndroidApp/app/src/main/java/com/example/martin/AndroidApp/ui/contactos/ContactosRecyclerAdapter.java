package com.example.martin.AndroidApp.ui.contactos;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.martin.AndroidApp.Contacto;
import com.example.martin.AndroidApp.R;

import java.util.ArrayList;

public class ContactosRecyclerAdapter extends RecyclerView.Adapter<ContactosRecyclerAdapter.ViewHolder>{

    private final Context mContexto;
    private ArrayList<Contacto> mContactos;
    private OnContactoListener mOnContactoListener;



    public ContactosRecyclerAdapter(Context contexto, ArrayList<Contacto> contactos, OnContactoListener onContactoListener) {
        mContexto = contexto;
        mContactos = contactos;
        mOnContactoListener = onContactoListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater gLayoutInflater = LayoutInflater.from(mContexto);
        View ivItemContacts = gLayoutInflater.inflate(R.layout.item_contact, parent, false );
        return new ViewHolder(ivItemContacts, mOnContactoListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Contacto contacto = mContactos.get(position);
        holder.gNombreContacto.setText(contacto.getNombre());
        holder.gTelefonoContacto.setText(contacto.getTelefono().toString());
        if(contacto.getRecibeNotificaciones()){
            holder.gNotificacion.setColorFilter(ContextCompat.getColor(mContexto,R.color.notificationColorTrue));
        }
        else{
            holder.gNotificacion.setColorFilter(ContextCompat.getColor(mContexto,R.color.notificationColorFalse));
        }
        if(contacto.getRecibeSMS()){
            holder.gMensaje.setColorFilter(ContextCompat.getColor(mContexto,R.color.messageColorTrue));
        }
        else{
            holder.gMensaje.setColorFilter(ContextCompat.getColor(mContexto,R.color.messageColorFalse));
        }


    }

    @Override
    public int getItemCount() {
        return mContactos.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView gNombreContacto;
        public TextView gTelefonoContacto;
        public ImageView gMensaje;
        public ImageView gNotificacion;
        OnContactoListener onContactoListener;


        public ViewHolder(@NonNull View itemView, final OnContactoListener onContactListener) {
            super(itemView);
            gNombreContacto = (TextView) itemView.findViewById(R.id.tv_contact_name);
            gTelefonoContacto = (TextView) itemView.findViewById(R.id.tv_contact_phone);
            gMensaje = (ImageView) itemView.findViewById(R.id.iv_message);
            gNotificacion = (ImageView) itemView.findViewById(R.id.iv_notification);
            this.onContactoListener = onContactoListener;
            gNombreContacto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onContactListener.onNombreClick(getAdapterPosition());
                }
            });
            gTelefonoContacto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onContactListener.onNombreClick(getAdapterPosition());
                }
            });
            gMensaje.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onContactListener.onMensajeClick(getAdapterPosition());
                }
            });
            gNotificacion.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onContactListener.onNotificacionClick(getAdapterPosition());
                }
            });
        }
    }
    public interface OnContactoListener{
        void onNombreClick(int position);
        void onMensajeClick(int position);
        void onNotificacionClick(int position);
    }

}
