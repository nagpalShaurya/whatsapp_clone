package com.example.whatsapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class setdetails extends AppCompatActivity {

    private Button UpdateAccountSettings;
    private EditText userName, userStatus;

    private ProgressDialog loadingBar;

    private Toolbar SettingsToolBar;

    private String currentUserID;

    private FirebaseAuth mAuth;
    private DatabaseReference RootRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setdetails);

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        RootRef = FirebaseDatabase.getInstance().getReference();

        InitializeFields();

        UpdateAccountSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                loadingBar.setTitle("Finishing");
                loadingBar.setMessage("Setting up your account..");
                loadingBar.setCanceledOnTouchOutside(false);
                loadingBar.show();
                UpdateSettings();
            }
        });

    }
    private void InitializeFields() {
        UpdateAccountSettings = (Button) findViewById(R.id.submitbutton);
        userName = (EditText) findViewById(R.id.uname);
        userStatus = (EditText) findViewById(R.id.ustatus);
        loadingBar = new ProgressDialog(this);

        SettingsToolBar = (Toolbar) findViewById(R.id.settings_toolbar);
        setSupportActionBar(SettingsToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setTitle("Account");
    }
    private void UpdateSettings()
    {
        String setUserName = userName.getText().toString();
        String setStatus = userStatus.getText().toString();

        if (setUserName.isEmpty())
        {
            Toast.makeText(this, "Please write your user name first....", Toast.LENGTH_SHORT).show();
        }
        else
        {
            HashMap<String, Object> profileMap = new HashMap<>();
            profileMap.put("uid", currentUserID);
            profileMap.put("name", setUserName);
            profileMap.put("status", setStatus);
            RootRef.child("Users").child(currentUserID).updateChildren(profileMap)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task)
                        {

                            if (task.isSuccessful())
                            {
                                SendUserToMainActivity();
                                Toast.makeText(setdetails.this, "Profile Updated Successfully...", Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                String message = task.getException().toString();
                                Toast.makeText(setdetails.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }
    private void SendUserToMainActivity()
    {
        Intent mainIntent = new Intent(setdetails.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
    }
}
