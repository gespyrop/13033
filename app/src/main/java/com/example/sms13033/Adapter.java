package com.example.sms13033;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sms13033.models.TransportReason;

import java.util.ArrayList;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {

    Context context;
    ArrayList<TransportReason> transportReasons;

    Adapter(Context context, ArrayList<TransportReason> transportReasons) {
        this.context = context;
        this.transportReasons = transportReasons;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.transport_reason_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final TransportReason tr = transportReasons.get(position);
        holder.codeText.setText(String.valueOf(tr.getCode()));
        holder.descriptionText.setText(tr.getDescription());

        holder.edit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, String.valueOf(tr.getCode()), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return transportReasons.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView codeText, descriptionText;
        ImageButton edit_button;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            codeText = itemView.findViewById(R.id.code);
            descriptionText = itemView.findViewById(R.id.description);
            edit_button = itemView.findViewById(R.id.edit_button);
        }
    }
}
