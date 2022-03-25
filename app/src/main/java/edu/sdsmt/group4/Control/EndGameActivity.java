package edu.sdsmt.group4.Control;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import edu.sdsmt.group4.R;

public class EndGameActivity extends AppCompatActivity {
    public final static String PLAYER1_MESSAGE = "edu.sdsmt.group4.PLAYER1_MESSAGE";
    public final static String PLAYER2_MESSAGE  = "edu.sdsmt.group4.PLAYER2_MESSAGE";
    public final static String WINNER_MESSAGE  = "edu.sdsmt.group4.WINNER_MESSAGE";
    TextView player1;
    TextView player2;
    TextView winner;

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
        finish();
    }
}
