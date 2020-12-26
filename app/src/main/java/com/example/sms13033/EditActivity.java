package com.example.sms13033;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.sms13033.models.TransportReason;

import java.util.ArrayList;

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
        transportReasons = db.getTransportReasons();

        adapter = new Adapter(this, transportReasons);
        rv.setAdapter(adapter);
        rv.setLayoutManager(new LinearLayoutManager(this));
    }

    public void back(View view) {
        finish();
    }

    public void add(View view) {
        View dialog_view = this.getLayoutInflater().inflate(R.layout.transport_reason_dialog, null);
        final EditText dialog_code = dialog_view.findViewById(R.id.dialog_code);
        final EditText dialog_description = dialog_view.findViewById(R.id.dialog_description);

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
                            db.addTransportReason(tr);
                            transportReasons.add(tr);
                            adapter.notifyDataSetChanged();


                        }
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}