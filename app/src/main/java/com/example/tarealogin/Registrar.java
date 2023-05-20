package com.example.tarealogin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Registrar extends AppCompatActivity implements View.OnClickListener {

    EditText us, pas, nom, ap;
    Button reg, can;
    FirebaseAuth mAuth;
    DatabaseReference usersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registrar);

        mAuth = FirebaseAuth.getInstance();
        usersRef = FirebaseDatabase.getInstance().getReference("usuarios");

        nom = findViewById(R.id.RegNombre);
        ap = findViewById(R.id.RegApellido);
        us = findViewById(R.id.RegUser);
        pas = findViewById(R.id.RegPass);
        reg = findViewById(R.id.btnRegRegistrar);
        can = findViewById(R.id.btnRegCancelar);

        reg.setOnClickListener(this);
        can.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnRegRegistrar:
                String nombre = nom.getText().toString().trim();
                String apellido = ap.getText().toString().trim();
                String usuario = us.getText().toString().trim();
                String password = pas.getText().toString().trim();

                if (nombre.isEmpty() || apellido.isEmpty() || usuario.isEmpty() || password.isEmpty()) {
                    Toast.makeText(this, "ERROR: Campos vacíos", Toast.LENGTH_LONG).show();
                } else {
                    Usuario u = new Usuario(nombre, apellido, usuario, password);
                    registrarUsuario(u);
                }
                break;

            case R.id.btnRegCancelar:
                Intent i = new Intent(Registrar.this, Main.class);
                startActivity(i);
                finish();

                break;
        }
    }
// Válidamos que el usuario sea un correo
    private boolean isValidUser(String email) {
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        return email.matches(emailPattern);
    }

    private void registrarUsuario(Usuario usuario) {
        String password = usuario.getPassword();
        String correo = usuario.getUsuario();
        if (!isValidUser(correo)) {
            Toast.makeText(this, "ERROR: Dirección de correo electrónico inválida", Toast.LENGTH_LONG).show();
            return;
        }
        if (password.length() < 6) {
            Toast.makeText(this, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_LONG).show();
            return;
        }
        if (TextUtils.isEmpty(correo) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_LONG).show();
            return;
        }

        mAuth.createUserWithEmailAndPassword(correo, password).addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                FirebaseUser user = mAuth.getCurrentUser();
                String userId = user.getUid();

                usersRef.child(userId).setValue(usuario);

                Toast.makeText(this, "Registro exitoso!", Toast.LENGTH_LONG).show();

                Intent intent = new Intent(Registrar.this, Main.class);
                startActivity(intent);
                finish();
            } else {
                String errorMessage = task.getException().getMessage();
                Toast.makeText(this, "Error al registrar el usuario: " + errorMessage, Toast.LENGTH_LONG).show();
            }
        });
    }

}
