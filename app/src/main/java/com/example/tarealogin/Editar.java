package com.example.tarealogin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Editar extends AppCompatActivity implements View.OnClickListener {

    EditText ediUser, ediPass, ediNombre, ediApellido;
    Button btnActualizar, btnCancelar;
    String id = "";
    Usuario u;
    DatabaseReference usuariosRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editar);

        ediUser = findViewById(R.id.EdiUser);
        ediPass = findViewById(R.id.EdiPass);
        ediNombre = findViewById(R.id.EdiNombre);
        ediApellido = findViewById(R.id.EdiApellido);
        btnActualizar = findViewById(R.id.btnEdiActualizar);
        btnCancelar = findViewById(R.id.btnEdiCancelar);
        btnActualizar.setOnClickListener(this);
        btnCancelar.setOnClickListener(this);

        Bundle b = getIntent().getExtras();
        if (b != null) {
            id = b.getString("Id");
        }

        usuariosRef = FirebaseDatabase.getInstance().getReference().child("usuarios");
        usuariosRef.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    u = dataSnapshot.getValue(Usuario.class);
                    if (u != null) {
                        ediUser.setText(u.getUsuario());
                        ediPass.setText(u.getPassword());
                        ediNombre.setText(u.getNombre());
                        ediApellido.setText(u.getApellido());
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Manejar error de lectura de Firebase
                Toast.makeText(Editar.this, "Error al obtener los datos", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnEdiActualizar:
                String nuevoUsuario = ediUser.getText().toString();
                String nuevaPass = ediPass.getText().toString();
                String nuevoNombre = ediNombre.getText().toString();
                String nuevoApellido = ediApellido.getText().toString();


                if (nuevoUsuario.isEmpty() || nuevaPass.isEmpty() || nuevoNombre.isEmpty() || nuevoApellido.isEmpty()) {
                    Toast.makeText(this, "ERROR: Campos vacíos", Toast.LENGTH_LONG).show();
                } else {
                    if (u != null) {
                        u.setUsuario(nuevoUsuario);
                        u.setPassword(nuevaPass);
                        u.setNombre(nuevoNombre);
                        u.setApellido(nuevoApellido);
                        usuariosRef.child(id).setValue(u)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(Editar.this, "Actualización exitosa!", Toast.LENGTH_LONG).show();
                                        Intent intent = new Intent(Editar.this, Inicio.class);
                                        intent.putExtra("Id", u.getId());
                                        startActivity(intent);
                                        finish();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(Editar.this, "No se pudo actualizar", Toast.LENGTH_LONG).show();
                                    }
                                });
                    }
                }
                break;
            case R.id.btnEdiCancelar:

                finish();
                break;
        }
    }
}

