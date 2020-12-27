package com.example.sms13033;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

/**
 * <b>LoginActivity</b> is the launcher activity of the app.
 * Here the user can either login to his account
 * or register a new account.
 * If the user is already logged in
 * the MainActivity automatically opens.
 *
 * @author George Spyropoulos
 * */
public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    EditText emailEditText, passwordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
        emailEditText = findViewById(R.id.username);
        passwordEditText = findViewById(R.id.password);

        // Go to the MainActivity if the user has already logged in
        if (mAuth.getCurrentUser() != null)
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
    }

    public void login(View view) {
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        if (email.isEmpty() || !validateEmail(email)) {
            Toast.makeText(this, getString(R.string.email_validation_error), Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.isEmpty()) {
            Toast.makeText(this, getString(R.string.password_validation_error), Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                // Log the user in
                if (task.isSuccessful()) {
                    emailEditText.setText("");
                    passwordEditText.setText("");
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));

                } else {
                    Toast.makeText(getApplicationContext(),
                            task.getException().getMessage(),Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void register (View view) {
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        if (email.isEmpty() || !validateEmail(email)) {
            Toast.makeText(this, getString(R.string.email_validation_error), Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.isEmpty()) {
            Toast.makeText(this, getString(R.string.password_validation_error), Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                // Register a new user
                if (task.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), getString(R.string.successful_user_creation), Toast.LENGTH_SHORT).show();
                    emailEditText.setText("");
                    passwordEditText.setText("");
                } else {
                    Toast.makeText(getApplicationContext(),
                            task.getException().getMessage(),Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private boolean validateEmail(String email) {
        return email.matches("^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$");
    }
}