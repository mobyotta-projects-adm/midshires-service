package com.midshire.midshireservice;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MidshiresAuthActivity extends AppCompatActivity {

    private String API_KEY;
    private EditText et_auth;
    private Button btn_auth;
    private SharedPreferences sharedPreferences;
    private static final String SHARED_PREFS_KEY = "shared_prefs_auth_key";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_midshires_auth);

        API_KEY = new String(Base64.decode(BuildConfig.MIDHSIRES_AUTH_KEY, Base64.DEFAULT), Base64.DEFAULT);

        et_auth = findViewById(R.id.et_authtxt);
        btn_auth = findViewById(R.id.btn_auth);

        sharedPreferences = getSharedPreferences("MidAuthPrefs", Context.MODE_PRIVATE);

        btn_auth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String inputText = et_auth.getText().toString();
                if (inputText.equals(API_KEY)) {
                    // Store the correct API key in SharedPreferences
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(SHARED_PREFS_KEY, inputText);
                    editor.apply();
                    Toast.makeText(MidshiresAuthActivity.this, "Auth Success", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(MidshiresAuthActivity.this, MidshireLogin.class));
                    finish();
                } else {
                    Toast.makeText(MidshiresAuthActivity.this, "Invalid Auth key!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
