package viva.kimeshan.viva;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.vivala.vivaladestino.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class login extends AppCompatActivity {
    Button login,register;
    String TAG;
    EditText username, password;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        login = findViewById(R.id.login);
        register = findViewById(R.id.register);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);


        //Sends the user to the map main activity after login is successful
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               if((username.length() == 0) || (password.length() == 0)){
                   Toast.makeText(login.this, "Incorrect email or password, please try again",
                           Toast.LENGTH_SHORT).show();
               }
               else{
                   signIn();
               }
            }
        });

        //For new users to register
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(login.this, registration.class));
            }
        });

    }
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseAuth.getInstance().signOut();
    }

    //Firebase authentication used for user login
    public  void signIn(){
        mAuth.signInWithEmailAndPassword(username.getText().toString().trim(), password.getText().toString().trim())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            startActivity(new Intent(login.this,MainActivity.class));

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(login.this, "Incorrect email or password, please try again",
                                    Toast.LENGTH_SHORT).show();

                        }

                        // ...
                    }
                });
    }



}