package com.example.seg2105project.Activities;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.seg2105project.R;

public class MyViewHolder extends RecyclerView.ViewHolder {

    TextView emailView, firstNameView, lastNameView, phoneNumberView, addressView, employeeorhealthcardnumberView, specialtiesView;

    public MyViewHolder(@NonNull View itemView) {
        super(itemView);
        emailView = itemView.findViewById(R.id.txt_first);
        firstNameView = itemView.findViewById(R.id.txt_second);
        lastNameView = itemView.findViewById(R.id.txt_third);
        phoneNumberView = itemView.findViewById(R.id.txt_fourth);
        addressView = itemView.findViewById(R.id.txt_fifth);
        employeeorhealthcardnumberView = itemView.findViewById(R.id.txt_sixth);
        specialtiesView = itemView.findViewById(R.id.txt_seventh);

    }
}
