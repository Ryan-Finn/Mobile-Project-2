package edu.sdsmt.group2.Control;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import edu.sdsmt.group2.Model.Cloud;
import edu.sdsmt.group2.R;

public class EndGameActivity extends AppCompatActivity {
    public final static String PLAYER1_MESSAGE = "edu.sdsmt.group2.PLAYER1_MESSAGE";
    public final static String PLAYER2_MESSAGE  = "edu.sdsmt.group2.PLAYER2_MESSAGE";
    public final static String WINNER_MESSAGE  = "edu.sdsmt.group2.WINNER_MESSAGE";
    private final DatabaseReference gameRef = FirebaseDatabase.getInstance().getReference().child("game");
    TextView player1;
    TextView player2;
    TextView winner;
    private Activity back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end_game);

        player1 = findViewById(R.id.player1Score);
        player2 = findViewById(R.id.player2Score);
        winner = findViewById(R.id.winnerTextView);

        // Get the message from the intent
        Intent intent = getIntent();

        player1.setText(intent.getStringExtra(PLAYER1_MESSAGE));
        player2.setText(intent.getStringExtra(PLAYER2_MESSAGE));
        winner.setText(intent.getStringExtra(WINNER_MESSAGE));
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    public void onReturnClick(View view)
    {
        gameRef.removeValue();
        finish();
    }
}
