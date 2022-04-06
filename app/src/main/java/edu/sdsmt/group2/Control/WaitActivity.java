package edu.sdsmt.group2.Control;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

import edu.sdsmt.group2.R;

public class WaitActivity extends AppCompatActivity {
    private final WaitActivity thisActivity = this;
    private final FirebaseAuth userAuth = FirebaseAuth.getInstance();
    private final DatabaseReference gameRef = FirebaseDatabase.getInstance().getReference().child("game");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wait);
        waitForGame();
    }

    private void waitForGame() {
        gameRef.child("player1").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue(String.class) != null &&
                        !Objects.equals(snapshot.getValue(String.class), Objects.requireNonNull(userAuth.getCurrentUser()).getUid())) {
                    gameRef.child("player2").setValue(Objects.requireNonNull(userAuth.getCurrentUser()).getUid());
                    startActivity(new Intent(thisActivity, GameBoardActivity.class));
                } else {
                    gameRef.child("player1").setValue(Objects.requireNonNull(userAuth.getCurrentUser()).getUid());
                    waitForGame();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }
}