package com.example.tarealogin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.play.core.integrity.v;
import com.google.firebase.auth.FirebaseAuth;

public class Inicio extends AppCompatActivity implements View.OnClickListener {

    Button btnEditar, btnEliminar, btnMostrar, btnSalir;
    TextView nombre;
    String userId;
    Usuario u;
    daoUsuario dao;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);

        nombre = findViewById(R.id.nombreUsuario);
        btnEditar = findViewById(R.id.btnEditar);
        btnEliminar = findViewById(R.id.btnEliminar);
        btnMostrar = findViewById(R.id.btnMostrar);
        btnSalir = findViewById(R.id.btnSalir);
        btnEditar.setOnClickListener(this);
        btnEliminar.setOnClickListener(this);
        btnMostrar.setOnClickListener(this);
        btnSalir.setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null) {
            userId = getIntent().getStringExtra("Id");
            if (userId != null && !userId.isEmpty()) {
                dao = new daoUsuario(this);
                dao.getUsuarioById(userId, new daoUsuario.UsuarioListener() {
                    @Override
                    public void onUsuarioLoaded(Usuario usuario) {
                        u = usuario;
                        nombre.setText(u.getNombre() + " " + u.getApellido());
                    }

                    @Override
                    public void onUsuarioNotFound() {
                        // El usuario no se encontró
                    }
                    @Override
                    public void onLoadError(String errorMessage) {
                        // Ocurrió un error durante la carga del usuario
                        Toast.makeText(Inicio.this, "Error al cargar el usuario: " + errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
            }

        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnEditar:
                // Acción cuando se hace clic en el botón Editar
                Intent e = new Intent(Inicio.this, Editar.class);
                e.putExtra("Id", userId);
                startActivity(e);
                break;
            case R.id.btnEliminar:
                // Acción cuando se hace clic en el botón Eliminar
                AlertDialog.Builder b = new AlertDialog.Builder(this);
                b.setMessage("¿Seguro que quieres eliminar tu cuenta?");
                b.setCancelable(false);
                b.setPositiveButton("SI", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (dao.deleteUsuario(userId)) {
                            // Elimina el usuario de Firebase Authentication
                            mAuth.getCurrentUser().delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(Inicio.this, "Se eliminó correctamente", Toast.LENGTH_LONG).show();
                                        Intent e = new Intent(Inicio.this, Main.class);
                                        startActivity(e);
                                        finish();
                                    } else {
                                        Toast.makeText(Inicio.this, "ERROR: No se pudo eliminar la cuenta", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    }
                });

                b.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                b.show();
                break;
            case R.id.btnMostrar:
                // Acción cuando se hace clic en el botón Mostrar o listar
                Intent m = new Intent(Inicio.this, Mostrar.class);
                startActivity(m);
                break;
            case R.id.btnSalir:
                // Acción cuando se hace clic en el botón Salir o cerrar sesión en Firebase
                mAuth.signOut();
                Intent s = new Intent(Inicio.this, Main.class);
                startActivity(s);
                finish();
                break;
            default:
                // Acción por defecto si no se reconoce el botón
                Intent not = new Intent(Inicio.this, Inicio.class);
                startActivity(not);
                Toast.makeText(this, "Por favor hága clic en una opción del menú", Toast.LENGTH_SHORT).show();
                finish();
                break;
        }
    }
}
