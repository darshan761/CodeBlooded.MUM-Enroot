package com.example.poojan.ezcommuter;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

public class Login extends AppCompatActivity {

    ProgressDialog loginProgress;
    TextView name;
    String email;
    private EditText inputEmail, inputPassword;
    private FirebaseAuth auth;
    private TextView btnReset;
    private Button btnLogin, btnSignup;
    DatabaseReference dbtoken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        auth = FirebaseAuth.getInstance();
        loginProgress = ProgressDialog.show(this, null, "Please wait...", true);
        loginProgress.setCancelable(false);

        if (auth.getCurrentUser() != null) {
            loginProgress.dismiss();
            startActivity(new Intent(Login.this, Home.class));
            finish();
            return;
        }

        loginProgress.dismiss();
        inputEmail = findViewById(R.id.email);
        inputPassword = findViewById(R.id.password);
        btnSignup = findViewById(R.id.btn_signup);
        btnLogin = findViewById(R.id.btn_login);
        btnReset = findViewById(R.id.btn_reset_password);

        /*btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Login.this, Home.class);
                startActivity(intent);
            }
        });*/
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //final EditText workspaceLink = findViewById(R.id.workspace_link);
                email = inputEmail.getText().toString();
                final String password = inputPassword.getText().toString();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                    return;
                }

                loginProgress = ProgressDialog.show(Login.this, null, "Logging in...", true);
                loginProgress.setCancelable(false);

                auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(Login.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                // If sign in fails, display a message to the user. If sign in succeeds
                                // the auth state listener will be notified and logic to handle the
                                // signed in user can be handled in the listener.
                                if (!task.isSuccessful()) {
                                    // there was an error
                                    if (password.length() < 6) {
                                        inputPassword.setError(getString(R.string.minimum_password));
                                    } else {
                                        Toast.makeText(Login.this, getString(R.string.auth_failed), Toast.LENGTH_LONG).show();
                                        loginProgress.dismiss();
                                    }
                                } else {
                                    loginProgress.dismiss();
                                    String token = FirebaseInstanceId.getInstance().getToken();
                                    dbtoken = FirebaseDatabase.getInstance().getReference().child("user_details")
                                            .child(auth.getUid()).child("token");
                                    dbtoken.setValue(token);
                                    startActivity(new Intent(Login.this, Home.class));
                                    finish();
                                }
                                loginProgress.dismiss();

                            }
                        });
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (loginProgress.isShowing()){
            loginProgress.dismiss();
        }
    }

    public void signup (View view) {
        Intent intent = new Intent(Login.this, Signup.class);
        startActivity(intent);
        finish();

    }

    protected void onResume() {
        super.onResume();
    }
}
