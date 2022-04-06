package edu.sdsmt.group2.Model;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class Cloud {
    public final static Cloud INSTANCE = new Cloud();
    private static final String TAG = "monitor";
    // Firebase Instance Variables
    private final FirebaseAuth userAuth = FirebaseAuth.getInstance();
    private FirebaseUser firebaseUser;
    private final DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users");
    private boolean authenticated = false;
    private boolean created = false;

    public boolean isAuthenticated() {
        return authenticated;
    }

    public void createUser(String user, String pass) {
        String email = user + "@gmail.com";
        Task<AuthResult> result = userAuth.createUserWithEmailAndPassword(email, pass);
        result.addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());
                    if(task.isSuccessful()) {
                        Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());
                        firebaseUser = userAuth.getCurrentUser();
                        HashMap<String, Object> result = new HashMap<>();
                        result.put("/screenName/"+user, true);
                        result.put("/"+firebaseUser.getUid()+"/name", user);
                        result.put("/"+firebaseUser.getUid()+"/password", pass);
                        result.put("/"+firebaseUser.getUid()+"/email", email);
                        result.put("/"+firebaseUser.getUid()+"/lfg", true);
                        userRef.updateChildren(result);
                        created = true;
                    }
                } else if (task.getException().getMessage().equals("The email address is already in use by another account.")) {
                    created = false;
                }  else {
                    Log.d(TAG, "Problem: " + task.getException().getMessage());
                    authenticated = false;
                    created = false;
                }
            }
        });
    }

    private void startAuthListening() {
        userAuth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                firebaseUser = firebaseAuth.getCurrentUser();
                if ( firebaseUser != null) {

                    // User is signed in
                    authenticated = true;
                    Log.d(TAG, "onAuthStateChanged:signed_in:" +  firebaseUser.getUid());
                } else {

                    // User is signed out
                    authenticated = false;
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        });

    }

    public String getUserUid(){
        //stop people from getting the Uid if not logged in
        if(firebaseUser == null)
            return "";
        else
            return firebaseUser.getUid();
    }

    private void signIn(String user, String pass) {
        // use "username" already exists
        Task<AuthResult> result = userAuth.signInWithEmailAndPassword(user, pass);
        result.addOnCompleteListener(new OnCompleteListener<AuthResult>() {

            @Override
            public void onComplete(@NonNull Task task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());
                    firebaseUser = userAuth.getCurrentUser();
                    HashMap<String, Object> result = new HashMap<>();
                    result.put("/"+firebaseUser.getUid()+"/lfg", true);
                    authenticated = true;

                } else {
                    Log.w(TAG, "signInWithEmail:failed", task.getException());
                    authenticated = false;
                }
            }
        });
    }

    public void login(String user, String pass) {
        createUser(user, pass);
        if (created) {
            createUser(user, pass);
        } else {
            signIn(user, pass);
        }
    }

    private Cloud() {
        startAuthListening();
    }

}
