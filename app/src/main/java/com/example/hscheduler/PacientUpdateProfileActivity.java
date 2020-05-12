package com.example.hscheduler;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.time.LocalDate;
import java.time.Period;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class PacientUpdateProfileActivity extends AppCompatActivity {

    private int age = -1;
    private boolean casatorit = false;
    private int ziua, luna, anul;
    private String currentUserID, imageLink;
    private static final int GalleryPick = 1;
    private String spinnerValue = "Alegeti una din optiunile de mai jos";
    private String nume, prenume, adresa, cnp, numarTelefon, ocupatie, sex = null;

    private Button updateProfile;
    private Spinner spinnerAfectiuni;
    private TextView displayDateOfBirth;
    private DatePickerDialog.OnDateSetListener onDateSetListener;
    private ProgressDialog loadingBar;
    private CircleImageView profileImage;
    private CheckBox sexF, sexM, casatoritUser;
    private EditText numeFamilie, prenumeUser, adresaUser, cnpUser, numarTelefonUser, ocupatieUser;

    private DatabaseReference RootRef;
    private StorageReference UserProfileImagesRef;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pacient_profile);
        initializeVariables();
        RetrieveUserProfileImage();
        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent accesGallery = new Intent();
                accesGallery.setAction(Intent.ACTION_GET_CONTENT);
                accesGallery.setType("image/*");
                startActivityForResult(accesGallery, GalleryPick);
            }
        });
        updateProfile.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                if(CheckBoxex()) {
                    UpdateUserSettings();
                }
            }
        });

        displayDateOfBirth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                final DatePickerDialog datePickerDialog = new DatePickerDialog(
                        PacientUpdateProfileActivity.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        onDateSetListener,
                        year, month, day);
                Objects.requireNonNull(datePickerDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                datePickerDialog.show();
            }
        });
        onDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month++;
                ziua = dayOfMonth;
                luna = month;
                anul = year;
                String date = dayOfMonth + "/" + month + "/" + year;
                displayDateOfBirth.setText(date);
            }
        };

        spinnerAfectiuni.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (parent.getItemAtPosition(position).equals("Alegeti una din optiunile de mai jos")) {
                    Toast.makeText(PacientUpdateProfileActivity.this, "Selectati o afectiune!", Toast.LENGTH_SHORT).show();
                } else {
                    spinnerValue = parent.getItemAtPosition(position).toString();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private boolean checkCNP(String text) {
        if (sexF.isChecked() && (text.charAt(0) != '2' || text.charAt(0) != '6')) {
            return false;
        }
        StringBuilder data = new StringBuilder();
        for (int i = 6; i > 0; i = i - 2) {
            data.append(text.charAt(i - 1)).append(text.charAt(i));
            data.append("-");
        }

        int ziuaCNP = Integer.parseInt(data.substring(0, 2));
        int lunaCNP = Integer.parseInt(data.substring(3, 5));
        int anulCNP = Integer.parseInt(data.substring(6, 8));
        System.out.println(ziuaCNP + " " + lunaCNP + " " + anulCNP + " aici");
        System.out.println(ziua + " " + luna + " " + anul + " aici");

        if (text.charAt(0) == '1' || text.charAt(0) == '2') {
            if (ziua != ziuaCNP || luna != lunaCNP || anul != (anulCNP + 1900)) {
                return false;
            }
        } else if (text.charAt(0) == '5' || text.charAt(0) == '6') {
            if (ziua != ziuaCNP || luna != lunaCNP || anul != (anulCNP + 2000)) {
                return false;
            }
        }
        int suma = 0;
        String control = "279146358279";
        for(int i = 0 ; i < 12 ;  i++){
            suma += Integer.parseInt(String.valueOf(control.charAt(i)))*Integer.parseInt(String.valueOf(text.charAt(i)));
        }
        System.out.println(suma%11 + " AICI " + text.charAt(12) );
        if(suma%11==10 && text.charAt(12)!='1'){
            return false;
        }
        return suma % 11 == Integer.parseInt(String.valueOf(text.charAt(12)));
    }
    private void retrieveUserData() {
        //TODO
    }

    private boolean checkStrings(String text) {
        for (char c : text.toCharArray()) {
            if (Character.isDigit(c)) {
                System.out.print(c + ",");
                return false;
            }
        }
        return true;
    }

    private boolean checkNumeric(String text) {
        for (char c : text.toCharArray()) {
            if (Character.isAlphabetic(c)) {
                return false;
            }
        }
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private boolean CheckBoxex() {

        String nume = numeFamilie.getText().toString();
        String prenume = prenumeUser.getText().toString();
        String adresa = adresaUser.getText().toString();
        String cnp = cnpUser.getText().toString();
        String numarTelefon = numarTelefonUser.getText().toString();
        String specializare = ocupatieUser.getText().toString();
        boolean ok = true;

        if(imageLink == null){
            ok = false;
            Toast.makeText(this, "Trebuie sa introduceti o imagine de profil!", Toast.LENGTH_LONG).show();
        }
        if (nume.isEmpty()) {
            numeFamilie.setError("Va rog introduceti un nume de familie!");
            ok = false;
        }
        else if (!checkStrings(nume)) {
            numeFamilie.setError("Va rog introduceti numai carcatere in numele de familie!");
            ok = false;
        }
        if (prenume.isEmpty()) {
            prenumeUser.setError("Va rog introduceti un prenume!");
            ok = false;
        } else if (!checkStrings(prenume)) {
            prenumeUser.setError("Va rog introduceti numai carcatere in prenume!");
            ok = false;
        }
        if (adresa.isEmpty()) {
            adresaUser.setError("Va rog introduceti o adresa!");
            ok = false;
        }
        if (numarTelefon.isEmpty()) {
            numarTelefonUser.setError("Va rog introduceti un numar de telefon!");
            ok = false;
        } else if (!checkNumeric(numarTelefon)) {
            numarTelefonUser.setError("Va rof introduceti numai cifre !");
            ok = false;
        }
        if (specializare.isEmpty()) {
            ocupatieUser.setError("Va rog alegeti una din afectiunile disponibile!");
            ok = false;
        } else if (!checkStrings(specializare)) {
            numeFamilie.setError("Va rog introduceti numai carcatere in specializare!");
            ok = false;
        }
        String defaultSpinnerValue = "Alegeti una din optiunile de mai jos";
        if (cnp.isEmpty()) {
            cnpUser.setError("Va rog introduceti un cnp");
            ok = false;
        } else if (!checkCNP(cnp)) {
            cnpUser.setError("Introduceti un CNP valid!");
            ok = false;
        } else {
            age = extractAgeFromCNP(cnp);
        }
        if (casatoritUser.isChecked()) {
            casatorit = true;
        }
        if (sexF.isChecked() && sexM.isChecked()) {
            Toast.makeText(this, "Alegeti doar un sex, nu ambele!", Toast.LENGTH_LONG).show();
            ok = false;
        } else if (sexF.isChecked() && (cnp.charAt(0) == '2' || cnp.charAt(0) == '6')) {
            sex = "Feminin";
        } else if (sexM.isChecked() && (cnp.charAt(0) == '1' || cnp.charAt(0) == '5')) {
            sex = "Masculin";
        }
        return ok;
    }
    private void UpdateUserSettings() {

        final HashMap<String, Object> profilePacient = new HashMap<>();
        profilePacient.put("Nume", numeFamilie.getText().toString());
        profilePacient.put("Prenume", prenumeUser.getText().toString());
        profilePacient.put("Afectiune", spinnerValue);
        profilePacient.put("userID",currentUserID);
        profilePacient.put("DateAppointment","");
        profilePacient.put("TimeAppointment","");

        RootRef.child("Users").child("Pacienti").child(currentUserID).child("New Account?").setValue("old");
        RootRef.child("Users").child("Pacienti").child(currentUserID).updateChildren(profilePacient)

                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        RootRef.child("Users").child("Pacienti").child(currentUserID).child("New Account?").setValue(null);
                        HashMap<String, Object> profileMap = new HashMap<>();
                        profileMap.put("Nume", numeFamilie.getText().toString());
                        profileMap.put("Prenume", prenumeUser.getText().toString());
                        profileMap.put("Afectiune", spinnerValue);
                        profileMap.put("Sex", sex);
                        profileMap.put("Varsta", age);
                        profileMap.put("Casatorit?", casatorit);
                        profileMap.put("Adresa", adresaUser.getText().toString());
                        profileMap.put("CNP", cnpUser.getText().toString());
                        profileMap.put("Numar Telefon", numarTelefonUser.getText().toString());
                        profileMap.put("Ocupatie",ocupatieUser.getText().toString());

                        RootRef.child("Users").child(currentUserID).child("New Account?").setValue("old");
                        RootRef.child("Users").child(currentUserID).updateChildren(profileMap)

                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        SendUserToMainActivity();
                                    }
                                });
                    }
                });

    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void initializeVariables() {



        numeFamilie = findViewById(R.id.nume_familie_pacient);
        prenumeUser = findViewById(R.id.prenume_pacient);
        adresaUser = findViewById(R.id.adresa_pacient);
        cnpUser = findViewById(R.id.cnp_pacient);
        numarTelefonUser = findViewById(R.id.telefon_pacient);
        ocupatieUser = findViewById(R.id.ocupatie_pacient);
        profileImage = findViewById(R.id.set_profile_image_pacient);
        updateProfile = findViewById(R.id.update);
        sexF = findViewById(R.id.femeie);
        sexM = findViewById(R.id.barbat);
        casatoritUser = findViewById(R.id.Casatorit);
        loadingBar = new ProgressDialog(this);
        displayDateOfBirth = findViewById(R.id.dateOfBirth);

        RootRef = FirebaseDatabase.getInstance().getReference();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        currentUserID = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        UserProfileImagesRef = FirebaseStorage.getInstance().getReference().child("Profile Images");

        spinnerAfectiuni = findViewById(R.id.afectiuni_spinner);
        ArrayAdapter<String> stringArrayAdapter = new ArrayAdapter<>(PacientUpdateProfileActivity.this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.afectiuni));
        stringArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerAfectiuni.setAdapter(stringArrayAdapter);

    }


    private void RetrieveUserProfileImage() {

        RootRef.child("Users").child("Pacienti").child(currentUserID)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (!Objects.requireNonNull(dataSnapshot.child("image").exists())) {

                            // imagine default
                            imageLink = "https://firebasestorage.googleapis.com/v0/b/hschedulerproject.appspot.com/o/Profile%20Images%2Fprofile_image.png?alt=media&token=a3da03b8-9432-406f-ae95-4b685771c009";
                        } else {
                            imageLink = Objects.requireNonNull(dataSnapshot.child("image").getValue()).toString();
                            Picasso.get().load(imageLink).placeholder(R.drawable.profile_image).into(profileImage);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) { // In partea aceasta de cod se executa partea de preluare a imaginii din memoria locala a telefonului

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GalleryPick && resultCode == RESULT_OK && data != null) {

            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON) // Aici dau crop la imagine
                    .setAspectRatio(1, 1)
                    .start(this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                loadingBar.setTitle("Set Profile Image");
                loadingBar.setMessage("Please wait,your profile image is updating");
                loadingBar.setCanceledOnTouchOutside(false);
                loadingBar.show();
                Uri resultUri = result.getUri();
                final StorageReference filePath = UserProfileImagesRef.child(currentUserID + ".jpg");               // In partea aceasta iau referinta catre imaginea de profil al user-ului curent
                filePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                        if (task.isSuccessful()) {    // Daca totul e ok imaginea este decupata si pusa in baza de date

                            Toast.makeText(PacientUpdateProfileActivity.this, "Profile image has been uploaded succesfully", Toast.LENGTH_SHORT).show();

                            filePath.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    RootRef.child("Users").child("Pacienti").child(currentUserID).child("image")
                                            .setValue(Objects.requireNonNull(task.getResult()).toString())                  // In partea asta pun incarc imaginea
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {

                                                    if (task.isSuccessful()) {

                                                        Toast.makeText(PacientUpdateProfileActivity.this, "Image saved in database succesfully", Toast.LENGTH_SHORT).show();
                                                        loadingBar.dismiss();
                                                        // Daca totul e ok primesti un mesaj ca a fost incarcata

                                                    } else {

                                                        String message = Objects.requireNonNull(task.getException()).toString();
                                                        Toast.makeText(PacientUpdateProfileActivity.this, "Error" + message, Toast.LENGTH_SHORT).show();
                                                        loadingBar.dismiss();
                                                    }
                                                }
                                            });
                                }
                            });


                        } else {

                            String message = Objects.requireNonNull(task.getException()).toString();
                            Toast.makeText(PacientUpdateProfileActivity.this, "Error" + message, Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();
                        }
                    }
                });
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private int extractAgeFromCNP(String text) {
        LocalDate systemDate = LocalDate.now();
        StringBuilder data = new StringBuilder();
        for (int i = 6; i > 0; i = i - 2) {
            data.append(text.charAt(i - 1)).append(text.charAt(i));
            data.append("-");
        }
        data = new StringBuilder(data.substring(0, data.length() - 1));
        String day = data.substring(0, 2);
        int zi = Integer.parseInt(day);
        String month = data.substring(3, 5);
        int luna = Integer.parseInt(month);
        String year = data.substring(6, 8);
        int an = Integer.parseInt(year);
        LocalDate userDate = LocalDate.of(an, luna, zi);

        if (userDate != null && systemDate != null) {
            int userYears = Period.between(userDate, systemDate).getYears();
            if (text.charAt(0) == '1' || text.charAt(0) == '2') {
                userYears -= 1900;
            } else if (text.charAt(0) == '5' || text.charAt(0) == '6') {
                userYears -= 2000;
            }
            return userYears;
        }
        return -1;
    }
    private void SendUserToMainActivity(){
        Intent intent = new Intent(getBaseContext(),MainActivity.class);
        startActivity(intent);
    }
}