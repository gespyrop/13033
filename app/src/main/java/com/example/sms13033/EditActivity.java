package com.example.sms13033;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.sms13033.models.TransportReason;

import java.util.ArrayList;

public class EditActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        final RecyclerView rv = findViewById(R.id.recyclerView);

        DBHelper db = DBHelper.getInstance(getApplicationContext());
        ArrayList<TransportReason> transportReasons = db.getTransportReasons();

        Adapter adapter = new Adapter(this, transportReasons);
        rv.setAdapter(adapter);
        rv.setLayoutManager(new LinearLayoutManager(this));

    }

    public void back(View view) {
        finish();
    }

    public void add(View view) {
        Toast.makeText(this, "Add pressed!", Toast.LENGTH_SHORT).show();
    }
}