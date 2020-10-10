package viva.kimeshan.viva;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class information {

    //Sending data to firebase for storage
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public  void uploadData(String ID, String name, String measurement, String preferredModeOfTransport){
        // Create a new user with a first and last name
        Map<String, java.lang.Object> user = new HashMap<>();
        user.put("name", name);
        user.put("measurement", measurement);
        user.put("modeOfTransport",preferredModeOfTransport);
        user.put("History",null);


// Add a new document with userID as the document ID
        db.collection("users")
                .document(ID).set(user);

    }
}
