package viva.kimeshan.viva;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.vivala.vivaladestino.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class profile extends AppCompatActivity {

    FirebaseAuth mAuth;
    EditText name, measurement, modeOfTransport;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference docRef;
    boolean click = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        mAuth = FirebaseAuth.getInstance();
        modeOfTransport = findViewById(R.id.modeOfTransport);
        name = findViewById(R.id.name);
        measurement = findViewById(R.id.measurement);



        findViewById(R.id.map).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(profile.this, MainActivity.class));
            }
        });

        findViewById(R.id.weather).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(profile.this, weather.class));
            }
        });


        findViewById(R.id.profile).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(profile.this, history.class));
            }
        });

        //Method taken from/adapted from firbase
        //Author: firebase
        //Link: https://firebase.google.com/docs/firestore/query-data/get-data
        findViewById(R.id.refresh).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseUser user = mAuth.getCurrentUser();
                docRef = db.collection("users").document(user.getUid());
                docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        name.setText(documentSnapshot.getString("name").toUpperCase());
                        measurement.setText(documentSnapshot.getString("measurement"));
                        modeOfTransport.setText(documentSnapshot.getString("modeOfTransport"));
                        name.setEnabled(false);
                        measurement.setEnabled(false);
                        modeOfTransport.setEnabled(false);

                    }
                });

            }
        });

        findViewById(R.id.edit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                        name.setEnabled(true);
                        measurement.setEnabled(true);
                        modeOfTransport.setEnabled(true);

                    }
                });

        //Method taken from/adapted from firbase
        //Author: firebase
        //Link: https://firebase.google.com/docs/firestore/manage-data/add-data
        findViewById(R.id.save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseUser user = mAuth.getCurrentUser();
                DocumentReference docRef = db.collection("users").document(user.getUid());
                docRef.update("name", name.getText().toString().toUpperCase());
                docRef.update("modeOfTransport", modeOfTransport.getText().toString().toUpperCase());
                docRef.update("measurement", measurement.getText().toString().toUpperCase());
                name.setEnabled(false);
                measurement.setEnabled(false);
                modeOfTransport.setEnabled(false);
                    }
                });

    }

    boolean clicakv(View view){
        return  false;
    }

}
