package edu.sdsmt.group2.Control;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import edu.sdsmt.group2.R;

public class EndGameActivity extends AppCompatActivity {
    public final static String PLAYER1_MESSAGE = "edu.sdsmt.group2.PLAYER1_MESSAGE";
    public final static String PLAYER2_MESSAGE  = "edu.sdsmt.group2.PLAYER2_MESSAGE";
    public final static String WINNER_MESSAGE  = "edu.sdsmt.group2.WINNER_MESSAGE";
    private final DatabaseReference gameRef = FirebaseDatabase.getInstance().getReference().child("game2");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end_game);

        TextView player1 = findViewById(R.id.player1Score);
        TextView player2 = findViewById(R.id.player2Score);
        TextView winner = findViewById(R.id.winnerTextView);

        // Get the message from the intent
        Intent intent = getIntent();
        player1.setText(intent.getStringExtra(PLAYER1_MESSAGE));
        player2.setText(intent.getStringExtra(PLAYER2_MESSAGE));
        winner.setText(intent.getStringExtra(WINNER_MESSAGE));
        gameRef.removeValue();
    }

    @Override
    public void onBackPressed() {
        //gameRef.removeValue();
        finish();
    }

    public void onReturnClick(View view) {
        //gameRef.removeValue();
        finish();
    }
}
