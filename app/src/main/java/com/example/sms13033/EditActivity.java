package com.example.sms13033;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sms13033.models.TransportReason;

import java.util.ArrayList;

/**
 * <b>EditActivity</b> provides an interface for
 * TransportReason CRUD operations.
 * The user can
 * <ul>
 *     <li>see all the available transport reasons</li>
 *     <li>create a new transport reason</li>
 *     <li>update existing transport reasons</li>
 *     <li>delete a transport reason</li>
 * </ul>
 *
 * @author George Spyropoulos
 * */
public class EditActivity extends AppCompatActivity {
    DBHelper db;
    Adapter adapter;
    ArrayList<TransportReason> transportReasons;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        final RecyclerView rv = findViewById(R.id.recyclerView);

        db = DBHelper.getInstance(getApplicationContext());
        transportReasons = db.getTransportReasons(); // Get all stored transport reasons

        // Create a custom adapter and set it as the RecyclerView's adapter
        adapter = new Adapter(this, transportReasons);
        rv.setAdapter(adapter);
        rv.setLayoutManager(new LinearLayoutManager(this));
    }

    // Back button
    public void back(View view) {
        finish();
    }

    // Add button
    public void add(View view) {
        View dialog_view = this.getLayoutInflater().inflate(R.layout.transport_reason_dialog, null);
        final EditText dialog_code = dialog_view.findViewById(R.id.dialog_code);
        final EditText dialog_description = dialog_view.findViewById(R.id.dialog_description);

        // Open a dialog to add a new transport reason
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true)
                .setTitle(getString(R.string.add))
                .setMessage(R.string.prompt_add)
                .setView(dialog_view)
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String code_text = dialog_code.getText().toString();
                        String description = dialog_description.getText().toString();

                        if (!code_text.isEmpty()) {
                            int code = Integer.parseInt(code_text);
                            TransportReason tr = new TransportReason(code, description);
                            db.addTransportReason(tr); // Save the new TransportReason object to SQLite
                            transportReasons.add(tr); // Add it to the RecyclerView's list
                            adapter.notifyDataSetChanged(); // Notify the adapter to refresh the RecyclerView
                        }
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}