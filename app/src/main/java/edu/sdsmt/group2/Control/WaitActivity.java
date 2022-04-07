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
    private final FirebaseAuth userAuth = FirebaseAuth.getInstance();
    private final DatabaseReference gameRef = FirebaseDatabase.getInstance().getReference().child("game2");
    public final static String PLAYER = "edu.sdsmt.group2.PLAYER";
    public final static String PLAYER1 = "edu.sdsmt.group2.PLAYER1";
    public final static String PLAYER2 = "edu.sdsmt.group2.PLAYER2";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wait);
        Intent intent = new Intent(this, GameBoardActivity.class);
        gameRef.child("player2").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue(String.class) != null) {
                    intent.putExtra(PLAYER2, snapshot.getValue(String.class));
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

        gameRef.child("player1").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue(String.class) != null &&
                        !Objects.equals(snapshot.getValue(String.class), Objects.requireNonNull(userAuth.getCurrentUser()).getUid())) {
                    gameRef.child("player2").setValue(getIntent().getStringExtra(WelcomeActivity.NAME));
                    intent.putExtra(PLAYER1, snapshot.getValue(String.class));
                    intent.putExtra(PLAYER, 2);
                } else {
                    gameRef.child("player1").setValue(getIntent().getStringExtra(WelcomeActivity.NAME));
                    intent.putExtra(PLAYER, 1);
                    intent.putExtra(PLAYER1, getIntent().getStringExtra(WelcomeActivity.NAME));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }
}