package com.example.sms13033;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.telephony.SmsManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.sms13033.models.SMS;
import com.example.sms13033.models.TransportReason;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    public static final int VOICE_REC_RESULT=22342;
    private FirebaseAuth mAuth;
    private DatabaseReference userRef;

    RadioGroup choices;
    EditText fullNameEditText, addressEditText, smsEditText;

    private TransportReason transportReason;
    private double latitude, longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        fullNameEditText = findViewById(R.id.full_name);
        addressEditText = findViewById(R.id.address);
        smsEditText = findViewById(R.id.sms);
        choices = findViewById(R.id.choices);
        choices.setOrientation(LinearLayout.VERTICAL);

        loadChoices();

        // Location updates
        LocationManager locationManager = (LocationManager) this.
                getSystemService(Context.LOCATION_SERVICE);

        // Listen for location changes and update current latitude/longitude
        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                latitude = location.getLatitude();
                longitude = location.getLongitude();
            }
        };

        // Location permission check
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.
                    requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},234);
            return;
        }
        // Request for location updates
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000,
                0, locationListener);


        // Get the user's Firebase Realtime Database reference
        FirebaseUser user = mAuth.getCurrentUser();
        userRef = FirebaseDatabase.getInstance().getReference("users").child(user.getUid());

        // Fetch full name from Firebase if it is stored in the database
        userRef.child("full_name").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null)
                    fullNameEditText.setText(snapshot.getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // Fetch address from Firebase if it is stored in the database
        userRef.child("address").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null)
                    addressEditText.setText(snapshot.getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // Update SMS text whenever the full name changes
        fullNameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                smsEditText.setText(getString(R.string.SMS,
                        transportReason != null ? String.valueOf(transportReason.getCode()) : "",
                        s, addressEditText.getText().toString()));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        // Update SMS text whenever the address changes
        addressEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                smsEditText.setText(getString(R.string.SMS,
                        transportReason != null ? String.valueOf(transportReason.getCode()) : "",
                        fullNameEditText.getText().toString(), s));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    @Override
    public void onBackPressed() {
        // The user can only logout by pressing the "logout" button
    }

    @Override
    public void onResume() {
        super.onResume();
        loadChoices();
    }

    public void send(View view) {
        String full_name = fullNameEditText.getText().toString();
        String address = addressEditText.getText().toString();

        if (full_name.isEmpty()) {
            Toast.makeText(this, getString(R.string.full_name_validation_error), Toast.LENGTH_SHORT).show();
            return;
        }

        if (address.isEmpty()) {
            Toast.makeText(this, getString(R.string.address_validation_error), Toast.LENGTH_SHORT).show();
            return;
        }

        if (transportReason == null) {
            Toast.makeText(this, getString(R.string.transport_code_validation_error), Toast.LENGTH_SHORT).show();
            return;
        }

        userRef.child("full_name").setValue(full_name);
        userRef.child("address").setValue(address);

        // SMS Permission check
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)!=
                PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.SEND_SMS},5434);
            return;
        }

        SmsManager manager = SmsManager.getDefault();
        manager.sendTextMessage("+3013033",null,smsEditText.getText().toString(),null,null);

        SMS sms = new SMS(latitude, longitude, transportReason);

        userRef.child("messages").push().setValue(sms);

        clearChoice();

        Toast.makeText(this, getString(R.string.SMS_sent), Toast.LENGTH_SHORT).show();
    }

    public void logout(View view) {
        mAuth.signOut();
        this.finish();
    }

    public void edit(View view) {
        startActivity(new Intent(getApplicationContext(), EditActivity.class));
    }

    public void voice_command(View view) {
        Intent intent= new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,"Please say something!");
        startActivityForResult(intent,VOICE_REC_RESULT);
    }

    public void onActivityResult(int Requestcode, int Resultcode, Intent data) {
        if (Requestcode==VOICE_REC_RESULT && Resultcode==RESULT_OK){
            ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

            // Make sure "matches" is not null
            if (matches == null) return;

            // Press the edit button
            if (matches.contains("edit") || matches.contains("επεξεργασία"))
                edit(null);

            // Press the send button
            if (matches.contains("send") || matches.contains("αποστολή"))
                send(null);

            // Press the logout button
            if (matches.contains("logout") || matches.contains("αποσύνδεση"))
                logout(null);

            ArrayList<String> command = new ArrayList<String>(Arrays.asList(matches.get(0).split(" ")));

            // Set the name
            if ((command.get(0).equals("name") || command.get(0).equals("όνομα")) && command.size() > 1) {
                command.remove(0);

                // Join the remaining strings
                StringBuilder sb = new StringBuilder();
                for (String s : command)
                    sb.append(s).append(" ");

                fullNameEditText.setText(sb.toString().toUpperCase());
            }

            // Set the address
            if ((command.get(0).equals("address") || command.get(0).equals("διεύθυνση")) && command.size() > 1) {
                command.remove(0);

                // Join the remaining strings
                StringBuilder sb = new StringBuilder();
                for (String s : command)
                    sb.append(s).append(" ");

                addressEditText.setText(sb.toString().toUpperCase());
            }
        }

        super.onActivityResult(Requestcode, Resultcode, data);
    }

    private void loadChoices() {
        clearChoice();
        DBHelper db = DBHelper.getInstance(getApplicationContext());
        ArrayList<TransportReason> transportReasons = db.getTransportReasons();

        choices.removeAllViews();

        // Dynamically add radio button for transport reason choices
        for (final TransportReason tr : transportReasons) {
            RadioButton rb = new RadioButton(this);
            rb.setId(View.generateViewId());
            rb.setText(getString(R.string.transport_code_description,
                    String.valueOf(tr.getCode()),
                    tr.getDescription()));
            rb.setPadding(8, 24, 120, 24);
            rb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    transportReason = tr;
                    smsEditText.setText(getString(R.string.SMS, String.valueOf(transportReason.getCode()), fullNameEditText.getText().toString(), addressEditText.getText().toString()));
                }
            });
            choices.addView(rb);

        }
    }

    private void clearChoice() {
        choices.clearCheck();
        transportReason = null;
        smsEditText.setText(getString(R.string.SMS,
                "",
                fullNameEditText.getText().toString(),
                addressEditText.getText().toString())
        );
    }
}