package com.example.tarealogin;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Mostrar extends AppCompatActivity {

    ListView lista;
    DatabaseReference database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mostrar);

        lista = findViewById(R.id.lista);
        database = FirebaseDatabase.getInstance().getReference().child("usuarios");
        ArrayList<String> list = new ArrayList<>();

        database.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                list.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Usuario u = snapshot.getValue(Usuario.class);
                    if (u != null) {
                        list.add(u.getNombre() + " " + u.getApellido());
                    }
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(Mostrar.this, android.R.layout.simple_list_item_1, list);
                lista.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Manejar errores de base de datos
            }
        });
    }
}
