package com.example.martin.AndroidApp.ui.contacts;

import android.content.ContentValues;
import android.content.Context;

import com.example.martin.AndroidApp.ContactsInfo;
import com.example.martin.AndroidApp.ManejadorBaseDeDatosLocal;
import com.example.martin.AndroidApp.ManejadorBaseDeDatosNube;

import java.util.ArrayList;

public class ContactsManager {

    private ManejadorBaseDeDatosLocal mManejadorBaseDeDatosLocal;
    private ManejadorBaseDeDatosNube mManejadorBaseDeDatosNube;
    private ArrayList<ContactsInfo> mContacts;
    private Context mContext;

    public ContactsManager(Context context) {
        mContext = context;
        mContacts = new ArrayList<ContactsInfo>();
        mManejadorBaseDeDatosLocal = new ManejadorBaseDeDatosLocal(context, null);
        mManejadorBaseDeDatosNube = new ManejadorBaseDeDatosNube();
        fillContactArrayList();
    }

    private void fillContactArrayList() {
        mContacts.clear();
        for (ContactsInfo contacto : mManejadorBaseDeDatosLocal
                .obtenerContactos(mManejadorBaseDeDatosNube.obtenerIdUsuario())) {
            mContacts.add(contacto);
        }
    }

    public void deleteContact(int position) {
        //delete from database
        ContentValues contacto = getContactValue(mContacts.get(position));
        mManejadorBaseDeDatosLocal
                .eliminarContacto(Long.toString(mContacts.get(position).getId()), contacto);
        mManejadorBaseDeDatosNube.eliminarContacto(contacto);
        fillContactArrayList();
    }

    public void changeContactName(int position, final String newName) {
        mContacts.get(position).setName(newName);
        ContentValues contacto = getContactValue(mContacts.get(position));
        mManejadorBaseDeDatosLocal
                .actualizarContacto(Long.toString(mContacts.get(position).getId()), contacto);
        mManejadorBaseDeDatosNube.actualizarContacto(contacto, "name");
    }

    public void changeIsMessageSelected(int position) {
        if (mContacts.get(position).getIsMessageSelected()) {
            mContacts.get(position).setIsMessageSelected(false);
        } else {
            mContacts.get(position).setIsMessageSelected(true);
        }
        ContentValues contacto = getContactValue(mContacts.get(position));
        mManejadorBaseDeDatosLocal
                .actualizarContacto(Long.toString(mContacts.get(position).getId()), contacto);
        mManejadorBaseDeDatosNube.actualizarContacto(contacto, "isMessageSelected");
    }

    private void updateNotifications(final int position) {
        ContentValues contacto = getContactValue(mContacts.get(position));
        mManejadorBaseDeDatosLocal
                .actualizarContacto(Long.toString(mContacts.get(position).getId()), contacto);
        mManejadorBaseDeDatosNube.actualizarContacto(contacto, "isNotificationSelected");
    }

    public void changeIsNotificationSelected(final int position) {
        if (mContacts.get(position).getIsNotificationSelected()) {
            mContacts.get(position).setIsNotificationSelected(false);
            updateNotifications(position);
        } else {
            String telefono = mContacts.get(position).getPhoneNumber();
            String telefonoSinCaracteres = "";
            for (int i = 0; i < telefono.length(); i++) {
                if (telefono.charAt(i) >= '0' && telefono.charAt(i) <= '9') {
                    telefonoSinCaracteres += "" + telefono.charAt(i);
                }
            }
            String telefonoFiltrado = "";
            for (int i = telefonoSinCaracteres.length() - 10; i < telefonoSinCaracteres.length();
                 i++) {
                telefonoFiltrado += "" + telefonoSinCaracteres.charAt(i);
            }

            Long telefonoLong = Long.parseLong(telefonoFiltrado);
            if (mManejadorBaseDeDatosNube
                    .existeNumeroDeContactoRegistradoEnFirebaseComoUsuario(telefonoLong, mContext)) {
                mContacts.get(position).setIsNotificationSelected(true);
                updateNotifications(position);
            }
        }
    }

    private ContentValues getContactValue(ContactsInfo contact) {
        ContentValues contactValues = new ContentValues();
        contactValues.put("phoneNumber", contact.getPhoneNumber());
        contactValues.put("name", contact.getName());
        if (contact.getIsMessageSelected()) {
            contactValues.put("isMessageSelected", 1);
        } else {
            contactValues.put("isMessageSelected", 0);
        }
        if (contact.getIsNotificationSelected()) {
            contactValues.put("isNotificationSelected", 1);
        } else {
            contactValues.put("isNotificationSelected", 0);
        }
        if (contact.getIsUser()) {
            contactValues.put("isUser", 1);
        } else {
            contactValues.put("isUser", 0);
        }
        contactValues.put("userID", contact.getUserID());
        return contactValues;
    }

    public void addNewContact(ContactsInfo contacto) {
        ContentValues contactoConFormato = getContactValue(contacto);
        Long idNuevoContacto = mManejadorBaseDeDatosLocal.agregarNuevoContacto(contactoConFormato);
        contacto.setId(idNuevoContacto);
        mManejadorBaseDeDatosNube.agregarContacto(contacto);
        mContacts.clear();
        fillContactArrayList();
    }

    public ArrayList<ContactsInfo> getArrayContacts() {
        return mContacts;
    }

}
