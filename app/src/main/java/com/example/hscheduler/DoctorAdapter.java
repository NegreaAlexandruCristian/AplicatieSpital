package com.example.hscheduler;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class DoctorAdapter extends RecyclerView.Adapter<DoctorAdapter.DoctorViewHolder> {

    private Context context;
    private ArrayList<Doctor> doctorArrayList;
    private FirebaseAuth mAuth ;
    private String currentUserID;


    DoctorAdapter(Context context, ArrayList<Doctor> doctors){
        this.context = context;
        this.doctorArrayList = doctors;
    }

    @NonNull
    @Override
    public DoctorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mAuth = FirebaseAuth.getInstance();
        currentUserID = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        return new DoctorViewHolder(LayoutInflater.from(context).inflate(R.layout.doctor_item, parent, false));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull DoctorViewHolder holder, int position) {

        holder.username.setText("Username: " + doctorArrayList.get(position).getUsername());
        holder.specializare.setText("Specializare: " + doctorArrayList.get(position).getSpecializare());
        currentUserID = doctorArrayList.get(position).getUserID();
        Picasso.get().load(doctorArrayList.get(position).getImage()).into(holder.profileImage);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SendUserToDoctorProfile();
            }
        });
    }

    private void SendUserToDoctorProfile(){
        Intent intent = new Intent(context,DoctorProfileActivity.class);
        intent.putExtra("ID",currentUserID);
        context.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        return doctorArrayList.size();
    }

    static class DoctorViewHolder extends RecyclerView.ViewHolder {

        TextView username,specializare;
        CircleImageView profileImage;

        private DoctorViewHolder(View itemView){
            super(itemView);
            username = itemView.findViewById(R.id.username);
            specializare = itemView.findViewById(R.id.specializare);
            profileImage = itemView.findViewById(R.id.profile_image);
        }
    }
}
