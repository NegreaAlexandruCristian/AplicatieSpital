package com.example.hscheduler;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class DoctorProfileActivity extends AppCompatActivity {

    private TextView ratingDisplayText;
    private String currentUserId;
    private CircleImageView profileImage;
    private TextView numeFamilie,prenume,varsta,telefon,specializare,ratingDisplayTextView;
    private EditText studiiDoctor;
    private Button submitButton,updateButton;
    private RatingBar ratingBar;
    private DatabaseReference databaseReference;
    private float rating;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_profile);
        InitializeVariables();
        databaseReference.child("Users").child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {

                numeFamilie.setText((String) dataSnapshot.child("Nume").getValue());
                prenume.setText((String) dataSnapshot.child("Prenume").getValue());
                telefon.setText((String) dataSnapshot.child("Numar Telefon").getValue());
                specializare.setText((String) dataSnapshot.child("Specializare").getValue());
                varsta.setText(Objects.requireNonNull(Objects.requireNonNull(dataSnapshot.child("Varsta").getValue()).toString()));
                ratingDisplayTextView.setText(Objects.requireNonNull(dataSnapshot.child("Rating").getValue()).toString());
                studiiDoctor.setText(Objects.requireNonNull(dataSnapshot.child("Studii").getValue()).toString());

                databaseReference.child("Users").child("Doctors").child(currentUserId).child("image").addValueEventListener(new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String imageLink = dataSnapshot.getValue().toString();
                        System.out.println(imageLink);
                        Picasso.get().load(imageLink).into(profileImage);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                if (Objects.requireNonNull(dataSnapshot.child("Doctor").getValue()).toString().compareTo("yes") == 0) {
                    System.out.println("AICI : " + dataSnapshot.getKey());

                    updateButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            String textStudii = studiiDoctor.getText().toString();
                            databaseReference.child("Users").child(currentUserId).child("Studii").setValue(textStudii);
                        }
                    });

                    ratingBar.setEnabled(false);
                    ratingBar.setVisibility(View.GONE);
                    submitButton.setEnabled(false);
                    submitButton.setVisibility(View.GONE);
                    ratingDisplayTextView.setEnabled(false);
                    ratingDisplayTextView.setVisibility(View.GONE);

                } else {

                    submitButton.setOnClickListener(new View.OnClickListener() {
                        @SuppressLint("SetTextI18n")
                        @Override
                        public void onClick(View v) {

                            String variabila = (String) Objects.requireNonNull(dataSnapshot.child("Rating").getValue()).toString();
                            if (variabila.equals("0.0")) {

                                float overallRating = (Float) Float.parseFloat(variabila);
                                rating = ratingBar.getRating();
                                rating = (float) ((overallRating + rating));
                                ratingDisplayTextView.setText("Overall Rating is : " + rating);
                            } else {

                                float overallRating = (Float) Float.parseFloat(variabila);
                                rating = ratingBar.getRating();
                                rating = (float) ((overallRating + rating) / (2.0));
                                ratingDisplayTextView.setText("Overall Rating is : " + rating);
                            }

                            databaseReference.child("Users").child(currentUserId).child("Rating").setValue(Float.toString(rating));
                        }
                    });

                    studiiDoctor.setEnabled(false);
                    studiiDoctor.setInputType(InputType.TYPE_NULL);
                    updateButton.setEnabled(false);
                    updateButton.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @SuppressLint("CutPasteId")
    public void InitializeVariables(){

        ratingBar = findViewById(R.id.rating_bar);
        submitButton = findViewById(R.id.submit_button);
        numeFamilie = findViewById(R.id.nume_familie);
        prenume = findViewById(R.id.prenume);
        studiiDoctor = findViewById(R.id.studii_doctor);
        varsta = findViewById(R.id.varsta);
        telefon = findViewById(R.id.telefon);
        specializare = findViewById(R.id.specializare);
        ratingDisplayTextView = findViewById(R.id.rating_display_text_View);
        profileImage = findViewById(R.id.set_profile_image);
        updateButton = findViewById(R.id.update);

        currentUserId = (String) getIntent().getSerializableExtra("ID");
        databaseReference = FirebaseDatabase.getInstance().getReference();
    }
}
