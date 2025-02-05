package com.example.sms13033;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sms13033.models.TransportReason;

import java.util.ArrayList;

/**
 * <b>Adapter</b> is a custom adapter for EditView's RecyclerView.
 * <p>The data source of the adapter is a list with all the
 * TransportReason objects.</p>
 * <p>Each item is displayed as a card with
 * both edit and delete capabilities.</p>
 *
 * @author George Spyropoulos
 * */
public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {

    Context context;
    DBHelper db;
    ArrayList<TransportReason> transportReasons;

    Adapter(Context context, ArrayList<TransportReason> transportReasons) {
        this.context = context;
        this.transportReasons = transportReasons;

        db = DBHelper.getInstance(context);
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.transport_reason_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        // Get the TransportReason objects
        final TransportReason tr = transportReasons.get(position);

        // Pass the values of the TransportReason object to the codeText
        // and descriptionText fields in the transport_reason_card layout
        holder.codeText.setText(String.valueOf(tr.getCode()));
        holder.descriptionText.setText(tr.getDescription());

        // Edit button
        holder.edit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View dialog_view = LayoutInflater.from(context).inflate(R.layout.transport_reason_dialog, null);
                final EditText dialog_code = dialog_view.findViewById(R.id.dialog_code);
                final EditText dialog_description = dialog_view.findViewById(R.id.dialog_description);

                // Open a dialog to edit an existing transport reason
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setCancelable(true)
                        .setTitle(context.getString(R.string.edit))
                        .setMessage(R.string.prompt_edit)
                        .setView(dialog_view)
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        })
                        .setPositiveButton(R.string.edit, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String code_text = dialog_code.getText().toString();
                                String description = dialog_description.getText().toString();

                                if (!code_text.isEmpty()) {
                                    int code = Integer.parseInt(code_text);
                                    tr.setCode(code);
                                    tr.setDescription(description);
                                    db.updateTransportReason(tr); // Save the updated TransportReason object to SQLite
                                    notifyItemChanged(position); // Notify the adapter to refresh the RecyclerView

                                }
                            }
                        });

                // Display the current values
                dialog_code.setText(String.valueOf(tr.getCode()));
                dialog_description.setText(tr.getDescription());

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        // Delete button
        holder.delete_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setCancelable(true)
                        .setTitle(context.getString(R.string.delete))
                        .setMessage(R.string.prompt_delete)
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        })
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                db.deleteTransportReason(tr.getId());
                                transportReasons.remove(position); // Delete the TransportReason object from SQLite
                                notifyItemRemoved(position); // Notify the adapter to refresh the RecyclerView
                            }
                        });

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return transportReasons.size();
    }

    /**
     * Inner class of the adapter for the ViewHolder of
     * every TransportReason object in transportReasons.
     * */
    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView codeText, descriptionText;
        ImageButton edit_button, delete_button;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            codeText = itemView.findViewById(R.id.dialog_code);
            descriptionText = itemView.findViewById(R.id.dialog_description);
            edit_button = itemView.findViewById(R.id.edit_button);
            delete_button = itemView.findViewById(R.id.delete_button);
        }
    }
}
