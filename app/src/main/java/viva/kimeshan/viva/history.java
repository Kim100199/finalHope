package viva.kimeshan.viva;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.vivala.vivaladestino.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class history extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FirebaseAuth mAuth;
    String[] History; // array containing data to send to the recycler view for displaying
    String readHistory;//Contains the data from firebase
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference docRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        mAuth = FirebaseAuth.getInstance();
        recyclerView = findViewById(R.id.rec);

        //Buttons for navigation
        findViewById(R.id.map).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(history.this, MainActivity.class));
            }
        });

        findViewById(R.id.weather).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(history.this, weather.class));
            }
        });

        findViewById(R.id.profile).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(history.this, profile.class));
            }
        });

        findViewById(R.id.edit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                check();
            }
        });

    }

    //Method taken from/adapted from Youtube
    //Author: Stevdza-San
    //Link: https://www.youtube.com/watch?v=18VcnYN5_LM
    public void check(){
        //Reading data from firebase
        FirebaseUser user = mAuth.getCurrentUser();
        docRef = db.collection("users").document(user.getUid());
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                readHistory = documentSnapshot.getString("History");
                History = readHistory.split("-");

                //Instantiating recycler class and sending through data to be displayed via an array
                recyclerAdapter recyclerAdapter = new recyclerAdapter(history.this,History);
                recyclerView.setAdapter(recyclerAdapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(history.this));
            }
        });

    }

}