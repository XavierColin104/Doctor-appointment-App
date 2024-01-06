package com.example.seg2105project.Activities;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.seg2105project.R;
import com.example.seg2105project.Users.NonAdmin;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.ViewHolder> {

    private ArrayList<Listable> mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    // data is passed into the constructor
    MyRecyclerViewAdapter(Context context, ArrayList<Listable> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.recyclerview_row, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String fullName = "";
        if (mData.get(position) instanceof NonAdmin){fullName =
                ((NonAdmin)mData.get(position)).getFirstName()+
                " "+((NonAdmin)mData.get(position)).getLastName() ;
        }
        else if (mData.get(position) instanceof Appointment){


            fullName = new Date(((Appointment)mData.get(position)).getDateAndTime()).toString();

        }
        else if (mData.get(position) instanceof Shift && !GenericListActivity.state.equals("Shifts")){
            fullName = ((Shift) mData.get(position)).getDisplay() + " " + new Date((((Shift) mData.get(position)).getStartTime())) + " to " + new Date((((Shift) mData.get(position)).getEndTime()));
        }
        else if(mData.get(position) instanceof Shift && GenericListActivity.state.equals("Shifts")){
            fullName = new Date((((Shift) mData.get(position)).getStartTime())) + " to " + new Date((((Shift) mData.get(position)).getEndTime()));
        }

        holder.myTextView.setText(fullName);

    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void update(ArrayList<Listable> list) {
        mData.clear();
        mData.addAll(list);
        notifyDataSetChanged();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView myTextView;

        ViewHolder(View itemView) {
            super(itemView);
            myTextView = itemView.findViewById(R.id.txt_fullname);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // convenience method for getting data at click position
    Listable getItem(int id) {
        return mData.get(id);
    }

    // allows clicks events to be caught
    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);

    }


}
