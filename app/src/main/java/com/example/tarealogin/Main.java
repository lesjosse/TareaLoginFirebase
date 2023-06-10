package com.example.tarealogin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Main extends AppCompatActivity implements View.OnClickListener {

    EditText user, pass;
    Button btnEntrar, btnRegistrar;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        user = findViewById(R.id.User);
        pass = findViewById(R.id.Pass);
        btnEntrar = findViewById(R.id.btnEntrar);
        btnRegistrar = findViewById(R.id.btnRegistrar);

        btnEntrar.setOnClickListener(this);
        btnRegistrar.setOnClickListener(this);

        firebaseAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnEntrar:
                String u = user.getText().toString();
                String p = pass.getText().toString();
             //   if (TextUtils.isEmpty(u) || TextUtils.isEmpty(p)) {
             //       Toast.makeText(this, "ERROR: Campos vacíos", Toast.LENGTH_LONG).show();
             //   }
                if (u.isEmpty()) {
                    EditText campoUsuario = findViewById(R.id.User);
                    campoUsuario.setError("Campo de usuario vacío");
                }
                if (p.isEmpty()) {
                    EditText campoUsuario = findViewById(R.id.Pass);
                    campoUsuario.setError("Campo de contraseña vacío");
                }else {
                    firebaseAuth.signInWithEmailAndPassword(u, p)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    FirebaseUser currentUser = firebaseAuth.getCurrentUser();
                                    if (currentUser != null) {
                                        String userId = currentUser.getUid();
                                        Toast.makeText(this, "Inicio de sesión exitoso", Toast.LENGTH_LONG).show();
                                        Intent intent = new Intent(Main.this, Inicio.class);
                                        intent.putExtra("Id", (userId));
                                        startActivity(intent);
                                        finish();
                                    }
                                } else {
                                    Toast.makeText(this, "ERROR: Usuario o contraseña incorrectos", Toast.LENGTH_LONG).show();
                                }
                            });
                }
                break;
            case R.id.btnRegistrar:
                Intent intent = new Intent(Main.this, Registrar.class);
                startActivity(intent);
                break;
        }
    }
}