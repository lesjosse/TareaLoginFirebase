package com.example.tarealogin;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class daoUsuario {
    private Context context;
    private DatabaseReference databaseReference;

    public daoUsuario(Context context) {
        this.context = context;
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("usuarios");
    }
    public interface UsuariosListener {
        void onUsuarioLoaded(Usuario usuario);
        void onUsuariosLoaded(ArrayList<Usuario> usuarios);
        void onUsuarioNotFound();
        void onLoadError(String errorMessage);
    }

    public void selectUsuarios(final UsuariosListener listener) {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<Usuario> lista = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Usuario u = snapshot.getValue(Usuario.class);
                    lista.add(u);
                }
                listener.onUsuariosLoaded(lista);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(context, "Error al cargar los usuarios", Toast.LENGTH_SHORT).show();
            }
        });
    }
    public interface UsuarioListener {
        void onUsuarioLoaded(Usuario usuario);
        void onUsuarioNotFound();
        void onLoadError(String errorMessage);
    }

    public void getUsuarioById(final String id, final UsuarioListener listener) {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Usuario u = snapshot.getValue(Usuario.class);
                    if (u != null && id != null && u.getId() != null && u.getId().equals(id)) {
                        listener.onUsuarioLoaded(u);
                        return;
                    }

                }
                listener.onUsuarioNotFound();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                listener.onLoadError(databaseError.getMessage());
            }
        });
    }


    public boolean updateUsuario(Usuario u) {
        String userId = String.valueOf(u.getId());
        DatabaseReference usuarioRef = databaseReference.child(userId);
        usuarioRef.setValue(u);
        return true;
    }

    public boolean deleteUsuario(String id) {
        String userId = String.valueOf(id);
        DatabaseReference usuarioRef = databaseReference.child(userId);
        usuarioRef.removeValue();
        return true;
    }
}
