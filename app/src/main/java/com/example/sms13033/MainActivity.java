package com.example.sms13033;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.telephony.SmsManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.sms13033.models.TransportReason;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * <b>MainActivity</b> is the main activity of the app.
 * The user can insert their information and select a
 * transport reason. They can then send the SMS to 13033
 * with the press of a button. Voice commands
 * are also supported.
 *
 * @author George Spyropoulos
 * */
public class MainActivity extends AppCompatActivity {
    public static final int VOICE_REC_RESULT=22342;

    RadioGroup choices;
    EditText fullNameEditText, addressEditText, smsEditText;

    private TransportReason transportReason;
    private double latitude, longitude;
    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fullNameEditText = findViewById(R.id.full_name);
        addressEditText = findViewById(R.id.address);
        smsEditText = findViewById(R.id.sms);
        choices = findViewById(R.id.choices);
        choices.setOrientation(LinearLayout.VERTICAL);

        // Load the choices from SQLite
        loadChoices();

        // Initialize Shared Preferences
        sp = getSharedPreferences("13033", Context.MODE_PRIVATE);

        // Load full name
        String full_name = sp.getString("full_name", "");
        fullNameEditText.setText(full_name);

        // Load address
        String address = sp.getString("address", "");
        addressEditText.setText(address);

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
        /*
        Reload choices from SQLite in onResume.
        This is to ensure that the choices are
        up to date after being edited in EditActivity.
        */
        loadChoices();
    }

    // Send button
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

        // Store the full name and address to Shared Preferences
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("full_name", full_name);
        editor.putString("address", address);
        editor.apply();

        // SMS Permission check
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)!=
                PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.SEND_SMS},5434);
            return;
        }

        SmsManager manager = SmsManager.getDefault();
        manager.sendTextMessage("13033",null,smsEditText.getText().toString(),null,null);

        clearChoice();

        Toast.makeText(this, getString(R.string.SMS_sent), Toast.LENGTH_SHORT).show();
    }

    // Logout button
    public void logout(View view) {
        this.finish();
    }

    // Edit button
    public void edit(View view) {
        startActivity(new Intent(getApplicationContext(), EditActivity.class));
    }

    // Voice command button
    public void voice_command(View view) {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,"Please say something!");
        startActivityForResult(intent,VOICE_REC_RESULT); // Open speech recognition activity
    }

    public void onActivityResult(int Requestcode, int Resultcode, Intent data) {
        // Handle the result from the speech recognition activity
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

            ArrayList<String> command = new ArrayList<>(Arrays.asList(matches.get(0).split(" ")));

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

    /**
     * Reload the TransportReason objects from SQLite
     * and dynamically create the radio buttons
     * */
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

    /**
     * Clear the radio button choice
     * */
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
