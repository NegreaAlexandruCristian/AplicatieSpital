package com.example.hscheduler;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class PhoneLoginActivity extends AppCompatActivity {

    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private String verificationID,phoneNumber;
    private EditText phoneNumberID,phoneNumberCode;
    private Button getPhoneCode,verifyPhoneCode,numarGresit;
    private FirebaseAuth mAuth;
    private String currentUserID,doctor;
    private DatabaseReference databaseReference;
    private ProgressDialog loadingBar;
    private CheckBox checkBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_login);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        initializeVariables();
        getPhoneCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phoneNumber = phoneNumberID.getText().toString();
                loadingBar.setTitle("Verificare numar");
                loadingBar.setMessage("Va rugam asteptati..");
                loadingBar.setCanceledOnTouchOutside(false);
                loadingBar.show();
                SendVerificationCode(phoneNumber);

            }
        });
        verifyPhoneCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code = phoneNumberCode.getText().toString();
                if(code.isEmpty() || code.length()<6){
                    phoneNumberCode.setError("Introduceti codul");
                    phoneNumberCode.requestFocus();
                }
                VerifyCode(code);
            }
        });
    }

    private void VerifyCode(String code){
        loadingBar.setTitle("Logare");
        loadingBar.setMessage("Va rugam asteptati..");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationID,code);
        SignInWithCredential(credential);
    }

    private void SignInWithCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            if(checkBox.isChecked()){
                                doctor = "yes";
                            } else {
                                doctor = "no";
                            }
                            databaseReference.child("Users").child(currentUserID)
                                    .addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            loadingBar.dismiss();
                                            Toast.makeText(PhoneLoginActivity.this, "Logare cu succes!", Toast.LENGTH_LONG).show();
                                            if(dataSnapshot.child("New Account?").exists() && dataSnapshot.child("Doctor").exists()){
                                                if(dataSnapshot.child("New Account?").getValue().toString().equals("new")){
                                                    if(dataSnapshot.child("Doctor").getValue().toString().equals("yes")){
                                                        SendUserToDoctorProfileActivity();
                                                    } else {
                                                        SendUserToPacientProfileActivity();
                                                    }
                                                } else {
                                                    SendUserToMainActivity();
                                                }
                                            } else {
                                                databaseReference.child("Users").child(currentUserID).child("New Account?").setValue("new");
                                                databaseReference.child("Users").child(currentUserID).child("Doctor").setValue(doctor);
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                            SendUserToPacientProfileActivity();
                        } else {
                            Toast.makeText(PhoneLoginActivity.this, (CharSequence) task.getException(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void SendVerificationCode(String number){

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                number,
                60,
                TimeUnit.SECONDS,
                TaskExecutors.MAIN_THREAD,
                mCallback
        );
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks
            mCallback = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            verificationID = s;
            loadingBar.dismiss();

        }

        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
            String code = phoneAuthCredential.getSmsCode();
            if(code != null ){

                VerifyCode(code);
            }
        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            Toast.makeText(PhoneLoginActivity.this, "Error " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    };

    private void initializeVariables(){
        phoneNumberID = findViewById(R.id.phoneNumber);
        phoneNumberCode = findViewById(R.id.codePhone);
        getPhoneCode = findViewById(R.id.getVerificationCode);
        verifyPhoneCode = findViewById(R.id.verifyCode);
        mAuth = FirebaseAuth.getInstance();
        currentUserID = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        loadingBar = new ProgressDialog(this);
        numarGresit = findViewById(R.id.wrongPhoneNumber);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        checkBox = findViewById(R.id.checkBoxDoctor);

    }
    private void SendUserToMainActivity(){
        Intent sendUserToMainActivity = new Intent(getBaseContext(),MainActivity.class);
        startActivity(sendUserToMainActivity);
        finish();
    }

    private void SendUserToPacientProfileActivity(){
        Intent intent = new Intent(getBaseContext(),PacientUpdateProfileActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void SendUserToDoctorProfileActivity(){
        Intent intent = new Intent(getBaseContext(),DoctorUpdateProfileActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void SendUserToLoginActivity(){
        Intent intent = new Intent(getBaseContext(), LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
