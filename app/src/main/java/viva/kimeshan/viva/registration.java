package viva.kimeshan.viva;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.vivala.vivaladestino.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class registration extends AppCompatActivity {

    private FirebaseAuth mAuth;
    Button register;
    EditText name, password, email;
    RadioButton km, mph;
    String TAG, modeOfTransport = null;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    information j = new information();
    String measurement;

    RadioButton walking, cycling, driving;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        mAuth = FirebaseAuth.getInstance();

        cycling = findViewById(R.id.cyclingRadio);
        driving = findViewById(R.id.drivingRadio);
        walking = findViewById(R.id.walkingRadio);




        register = findViewById(R.id.submit);
        name = findViewById(R.id.name);
        km = findViewById(R.id.kilometers);
        mph = findViewById(R.id.MILES);
        password = findViewById(R.id.password);
        email = findViewById(R.id.email);

        //Get the user's preferred measurement system
        km.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                measurement = km.getText().toString();
                mph.setEnabled(false);
            }
        });

        mph.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                measurement = km.getText().toString();
                km.setEnabled(false);
            }
        });


        //Get the user's preferred mode of transport
        cycling.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                modeOfTransport = cycling.getText().toString();
                walking.setEnabled(false);
                driving.setEnabled(false);
            }
        });

        walking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                modeOfTransport = walking.getText().toString();
                cycling.setEnabled(false);
                driving.setEnabled(false);
            }
        });

        driving.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                modeOfTransport = driving.getText().toString();
                walking.setEnabled(false);
                cycling.setEnabled(false);
            }
        });

        //Checks if any fields and empty to avoid crashing the app
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if((name.length() == 0) || (measurement == null)|| (password.length() == 0)|| (modeOfTransport == null)|| (email.length() == 0)){
                    Toast.makeText(registration.this, "A field is blank, please fill it and try again",
                            Toast.LENGTH_SHORT).show();
                }
                else{
                    newUser();
                }
            }
        });


    }


    public void newUser(){
        mAuth.createUserWithEmailAndPassword(email.getText().toString().trim(), password.getText().toString().trim())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            j.uploadData(user.getUid(),name.getText().toString().trim(), measurement, modeOfTransport); //Sends user data to the class and then in the class the data is sent to firbase
                            startActivity(new Intent(registration.this, login.class));


                            Toast toast = Toast. makeText(getApplicationContext(), "Registration Successful", Toast. LENGTH_SHORT);
                            toast.show();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(registration.this, "Registration Failed", Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
    }




    }
