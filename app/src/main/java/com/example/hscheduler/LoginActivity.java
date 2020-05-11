package com.example.hscheduler;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    private EditText parolaUser,emailUser;
    private Button butonLogin,butonRegister,phoneLogin;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initializeVariables();
        mAuth = FirebaseAuth.getInstance();

        butonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AllowUserToLogin();
            }
        });
        butonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendUserToRegisterActivity();
            }
        });
        phoneLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendUserToPhoneLoginActivity();
            }
        });
    }

    public void onStart(){
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
    }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @SuppressLint("WrongViewCast")
    private void initializeVariables(){

        emailUser = findViewById(R.id.emailText);
        parolaUser = findViewById(R.id.passwordText);
        butonLogin = findViewById(R.id.loginButton);
        butonRegister = findViewById(R.id.registerButton);
        phoneLogin = findViewById(R.id.phoneLogin);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");

    }

    private void AllowUserToLogin(){

        String EmailText = emailUser.getText().toString();
        final String ParolaText = parolaUser.getText().toString();
        System.out.println(EmailText + " " + ParolaText);
        if(!TextUtils.isEmpty(EmailText) && !TextUtils.isEmpty(ParolaText)) {
            mAuth.signInWithEmailAndPassword(EmailText, ParolaText)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                if(Objects.requireNonNull(mAuth.getCurrentUser()).isEmailVerified()) {
                                    Toast.makeText(LoginActivity.this, "Logare cu succes!", Toast.LENGTH_LONG).show();

                                    final String currentUserId = mAuth.getCurrentUser().getUid();
                                    final String deviceToken = FirebaseInstanceId.getInstance().getToken();
                                    final DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
                                    rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            if(dataSnapshot.child("Users").child(currentUserId).exists()){
                                                databaseReference.child(currentUserId).child("device_token")
                                                        .setValue(deviceToken)
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()) {
                                                                    databaseReference.child(currentUserId).addValueEventListener(new ValueEventListener() {
                                                                        @Override
                                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                            if (Objects.requireNonNull(dataSnapshot.child("New Account?").getValue()).toString().equals("new") &&
                                                                                    Objects.requireNonNull(dataSnapshot.child("Doctor").getValue()).toString().equals("yes")) {
                                                                                SendUserToProfileDoctorActivity();
                                                                            } else if (Objects.requireNonNull(dataSnapshot.child("New Account?").getValue()).toString().equals("new") &&
                                                                                    Objects.requireNonNull(dataSnapshot.child("Doctor").getValue()).toString().equals("no")) {
                                                                                SendUserToProfilePacientActivity();
                                                                            } else {
                                                                                SendUserToMainActivity();
                                                                            }

                                                                        }

                                                                        @Override
                                                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                                                        }
                                                                    });
                                                                }
                                                            }
                                                        });
                                            }

                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });

                                } else {
                                    Toast.makeText(LoginActivity.this, "Verifica-ti-va email-ul", Toast.LENGTH_LONG).show();
                                }
                            } else {
                                Toast.makeText(LoginActivity.this, "Logarea a esuat", Toast.LENGTH_SHORT).show();

                            }
                        }
                    });
        } else {

            Toast.makeText(this, "Va rog completati casutele!", Toast.LENGTH_LONG).show();
        }

    }

    private void SendUserToProfilePacientActivity(){
        Intent intent = new Intent(getBaseContext(),PacientUpdateProfileActivity.class);
        startActivity(intent);
    }

    private void SendUserToProfileDoctorActivity(){
        Intent intent = new Intent(getBaseContext(),DoctorUpdateProfileActivity.class);
        startActivity(intent);
    }

    private void SendUserToPhoneLoginActivity(){
        Intent phoneLoginActivityIntent = new Intent(getBaseContext(),PhoneLoginActivity.class);
        startActivity(phoneLoginActivityIntent);
    }
    private void SendUserToRegisterActivity() {
        Intent registerActivityIntent = new Intent(getBaseContext(),RegisterActivity.class);
        startActivity(registerActivityIntent);
    }
    private void SendUserToMainActivity(){
        Intent sendUserToMainActivityIntent = new Intent(getBaseContext(),MainActivity.class);
        startActivity(sendUserToMainActivityIntent);
    }

}
