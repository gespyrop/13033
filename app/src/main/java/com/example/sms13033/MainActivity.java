package com.example.sms13033;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
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

public class MainActivity extends AppCompatActivity {
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

        DBHelper db = DBHelper.getInstance(getApplicationContext());
        ArrayList<TransportReason> transportReasons = db.getTransportReasons();

        choices.setOrientation(LinearLayout.VERTICAL);

        // Dynamically add radio button for transport reason choices
        for (final TransportReason tr : transportReasons) {
            RadioButton rb = new RadioButton(this);
            rb.setId(View.generateViewId());
            rb.setText(String.valueOf(tr.getCode()) + "  " + tr.getDescription());
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

    // TODO Send SMS and save message to Firebase
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

        SMS sms = new SMS(latitude, longitude, transportReason);

        userRef.child("messages").push().setValue(sms);

        Toast.makeText(this, getString(R.string.SMS_sent), Toast.LENGTH_SHORT).show();
    }

    public void logout(View view) {
        mAuth.signOut();
        this.finish();
    }

    // TODO Open SettingsActivity
    public void settings(View view) {
        Toast.makeText(this, "Settings pressed", Toast.LENGTH_SHORT).show();
    }
}