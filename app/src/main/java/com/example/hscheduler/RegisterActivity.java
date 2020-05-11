package com.example.hscheduler;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.Objects;


public class RegisterActivity extends AppCompatActivity {

    private Button register;
    private EditText emailText,passwordText;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    private DatabaseReference databaseReference;
    private CheckBox doctorCheckBox;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initializeVariables();
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNewAccount();
            }
        });
    }
    private void createNewAccount(){
        final String email = emailText.getText().toString();
        final String parola = passwordText.getText().toString();
        if(!TextUtils.isEmpty(email) && !TextUtils.isEmpty(parola)) {
            firebaseAuth.createUserWithEmailAndPassword(email, parola)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(RegisterActivity.this, "Contul a fost creat cu succes!", Toast.LENGTH_LONG).show();
                                user = firebaseAuth.getCurrentUser();
                                if(user != null) {
                                    user.sendEmailVerification()
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Toast.makeText(RegisterActivity.this, "Un email de verificare a fost trimis la " + email, Toast.LENGTH_SHORT).show();

                                                        String deviceToken = FirebaseInstanceId.getInstance().getToken();
                                                        String currentUserId = firebaseAuth.getCurrentUser().getUid();
                                                        if(doctorCheckBox.isChecked()){
                                                            databaseReference.child("Users").child(currentUserId).setValue("");
                                                            databaseReference.child("Users").child(currentUserId).child("Doctor")
                                                                    .setValue("yes");
                                                            databaseReference.child("Users").child(currentUserId).child("device_token")
                                                                    .setValue(deviceToken);
                                                            databaseReference.child("Users").child(currentUserId).child("New Account?")
                                                                    .setValue("new");

                                                        } else {
                                                            databaseReference.child("Users").child(currentUserId).setValue("");
                                                            databaseReference.child("Users").child(currentUserId).child("Doctor")
                                                                    .setValue("no");
                                                            databaseReference.child("Users").child(currentUserId).child("device_token")
                                                                    .setValue(deviceToken);
                                                            databaseReference.child("Users").child(currentUserId).child("New Account?")
                                                                    .setValue("new");
                                                        }

                                                        SendUserToLoginActivity();
                                                    } else {
                                                        Toast.makeText(RegisterActivity.this, "Error", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                }
                            } else {
                                Toast.makeText(RegisterActivity.this, "Contul nu a fost creat!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } else {
            if (TextUtils.isEmpty(email)) {
                Toast.makeText(this, "Va rog completati casuta cu email!", Toast.LENGTH_LONG).show();
            } else if(TextUtils.isEmpty(parola)){
                Toast.makeText(this, "Va rog completati casuta cu parola!", Toast.LENGTH_LONG).show();
            } else if (TextUtils.isEmpty(email) && TextUtils.isEmpty(parola)){
                Toast.makeText(this, "Va rog completati casutele!", Toast.LENGTH_LONG).show();
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void initializeVariables(){

        emailText = findViewById(R.id.emailText);
        passwordText = findViewById(R.id.passwordText);
        register = findViewById(R.id.register);
        doctorCheckBox = findViewById(R.id.checkBox);
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
    }
    private void SendUserToLoginActivity(){
        Intent intent = new Intent(getBaseContext(),LoginActivity.class);
        startActivity(intent);
    }
}
