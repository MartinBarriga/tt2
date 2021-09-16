package com.example.martin.AndroidApp.ui.contacts;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.martin.AndroidApp.ContactsInfo;
import com.example.martin.AndroidApp.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class ContactsFragment extends Fragment implements  ContactsRecyclerAdapter.OnContactListener{

    private static final int PICK_CONTACT = 1;
    private ContactsViewModel mContactsViewModel;
    private ContactsManager mContactsManager;
    private ContactsRecyclerAdapter mContactsRecyclerAdapter;
    private View root;
    private ArrayList<ContactsInfo> mContacts;
    FirebaseAuth mAuth;
    FirebaseUser user;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        //contactsViewModel =
        //        ViewModelProviders.of(this).get(ContactsViewModel.class);
        root = inflater.inflate(R.layout.fragment_contacts, container, false);
        mContactsManager = new ContactsManager(getContext());

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        displayContacts();


        return root;
    }

    private void displayContacts() {
        final RecyclerView contactsRecyclerView = (RecyclerView) root.findViewById(R.id.contactsRecyclerView);
        final LinearLayoutManager contactsLayoutManager = new LinearLayoutManager(getContext());
        contactsRecyclerView.setLayoutManager(contactsLayoutManager);
        //Aqui tengo duda si debo de agregar a la función de llenado del array desde la base de datos del contactManager;
        mContacts = mContactsManager.getArrayContacts();
        mContactsRecyclerAdapter = new ContactsRecyclerAdapter(getContext(), mContacts, this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(contactsRecyclerView);
        contactsRecyclerView.setAdapter(mContactsRecyclerAdapter);

        FloatingActionButton addButton = root.findViewById(R.id.button_add);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addContact(v, getContext());
            }
        });
    }


    private void addContact(View view, Context context){
        if (ContextCompat.checkSelfPermission(this.getContext(), Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this.getActivity(), new String[]{Manifest.permission.READ_CONTACTS}, PICK_CONTACT);
            Log.d("LOG", "no hay permisos");
        }else{
            Log.d("LOG", "sí hay permisos");
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
            startActivityForResult(intent, PICK_CONTACT);
        }


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case (PICK_CONTACT):
                Cursor cursor = null;
                try {


                    Uri uri = data.getData();
                    cursor = getContext().getContentResolver().query(uri, null, null, null, null);
                    cursor.moveToFirst();
                    int  phoneIndex =cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                    String number1 = cursor.getString(phoneIndex).replace(" ", "");
                    String number = number1.substring(number1.length()-10);

                    cursor = getContext().getContentResolver().query(uri, null, null, null, null);
                    cursor.moveToFirst();
                    int  nameIndex =cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
                    String name = cursor.getString(nameIndex);
                    String userID = user.getUid();

                    Toast.makeText(getContext(), "Contacto Agregado!", Toast.LENGTH_SHORT).show();
                    mContactsManager.addNewContact(new ContactsInfo(null, number, name , false, false, false, userID));


                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
        displayContacts();
    }


    ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT){

        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            int position = viewHolder.getAdapterPosition();
            mContactsManager.deleteContact(position);
            mContacts = mContactsManager.getArrayContacts();
            mContactsRecyclerAdapter.notifyItemRemoved(position);

        }
    };


    @Override
    public void onNameClick(final int position) {
        final AlertDialog.Builder newNameDialog = new AlertDialog.Builder(getContext());
        newNameDialog.setTitle(R.string.newNameDialogTitle);
        final EditText newNameEditText = new EditText(getContext());
        newNameEditText.setText(mContacts.get(position).getName());
        newNameEditText.setGravity(EditText.TEXT_ALIGNMENT_CENTER);
        newNameEditText.setInputType(InputType.TYPE_CLASS_TEXT);
        newNameEditText.setSelectAllOnFocus(true);
        newNameEditText.selectAll();
        newNameDialog.setView(newNameEditText);
        newNameDialog.setPositiveButton( getString(R.string.newNameDialogAcceptingButton), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        newNameDialog.setNegativeButton(getString(R.string.newNameDialogCancelingButton), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
               // InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                //imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams. SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                dialog.cancel();
            }
        });
        final AlertDialog sameNameDialog = newNameDialog.create();
        sameNameDialog.show();
        sameNameDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newName = newNameEditText.getText().toString();
                String currentName = mContacts.get(position).getName();
                if(newName.isEmpty() || newName.equals(currentName)){
                    Toast.makeText(getContext(), "El nombre no puede estar vacío.", Toast.LENGTH_LONG).show();
                    getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams. SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                }
                else{
                    mContactsManager.changeContactName(position, newName);
                    Toast.makeText(getContext(),  "Nombre cambiado", Toast.LENGTH_LONG).show();
                    getActivity().getWindow().setSoftInputMode(
                            WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
                    );
                    getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams. SOFT_INPUT_STATE_ALWAYS_HIDDEN);

                    sameNameDialog.dismiss();
                    mContactsRecyclerAdapter.notifyItemChanged(position);
                }
            }
        });

    }

    @Override
    public void onMessageClick(int position) {
        mContactsManager.changeIsMessageSelected(position);
        mContactsRecyclerAdapter.notifyItemChanged(position);

    }

    @Override
    public void onNotificationClick(final int position) {
        mContactsManager.changeIsNotificationSelected(position);
         (new Handler()).postDelayed(new Runnable() {
            public void run() {
                mContactsRecyclerAdapter.notifyItemChanged(position);
            }
        }, 500);


    }
}
