package edu.sdsmt.group2.Control;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import edu.sdsmt.group2.R;
import edu.sdsmt.group2.View.GameBoardView;

public class GameBoardActivity extends AppCompatActivity {
    private GameBoardView view;
    private final DatabaseReference gameRef = FirebaseDatabase.getInstance().getReference().child("game");
    public static final String CAPTURED_INT = "edu.sdsmt.group2.RETURN_MESSAGE";
    private TextView player1Name;
    private TextView player2Name;
    private TextView player1Score;
    private TextView player2Score;
    private TextView rounds;
    private Button capture;
    private ActivityResultLauncher<Intent> captureResultLauncher;
    @Override
    protected void onSaveInstanceState(@NonNull Bundle bundle) {
        super.onSaveInstanceState(bundle);
        view.saveInstanceState(bundle);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle bundle) {
        super.onRestoreInstanceState(bundle);
        view.loadInstanceState(bundle);
        updateGUI();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_board);
        view = this.findViewById(R.id.gameBoardView);

        //get player names and no of rounds from prev
        Intent intent = getIntent();
        String name1 = intent.getStringExtra(WaitActivity.PLAYER1);
        String name2 = intent.getStringExtra(WaitActivity.PLAYER2);
        String r = "5";

        view.addPlayer(name1,0);
        view.addPlayer(name2,1);
        view.setRounds(Integer.parseInt(r));
        view.setDefaultPlayer();

        player1Name = findViewById(R.id.player1Name);
        player2Name = findViewById(R.id.player2Name);
        player1Score = findViewById(R.id.player1Score);
        player2Score = findViewById(R.id.player2Score);
        capture = findViewById(R.id.captureButton);
        capture.setEnabled(false);
        rounds = findViewById(R.id.rounds);
        player1Name.setText(name1);
        player2Score.setText("0");
        player2Name.setText(name2);
        player1Score.setText("0");
        rounds.setText(r);
        player1Name.setTextColor(Color.parseColor("#FF0000"));


        //any target
        ActivityResultContracts.StartActivityForResult contract =
                new ActivityResultContracts.StartActivityForResult();
        captureResultLauncher = registerForActivityResult(contract, (result) -> {
            int resultCode = result.getResultCode();
            if (resultCode == Activity.RESULT_OK) {
                Intent data = result.getData();
                assert data != null;
                //if no capture option is selected
                capture.setEnabled(true);
                view.setCapture(data.getIntExtra(CAPTURED_INT, 0));
            }
        });
    }

    private void isEndGame() {
        if(view.isEndGame()) {
            endGame(null);
        }
    }

    private void endGame(String winner) {
        int player1Score = Integer.parseInt(view.getPlayer1Score());
        int player2Score = Integer.parseInt(view.getPlayer2Score());

        Intent intent = new Intent(this, EndGameActivity.class);

        intent.putExtra(EndGameActivity.PLAYER1_MESSAGE, view.getPlayer1Name()
                + "'s Score\n" + view.getPlayer1Score());
        intent.putExtra(EndGameActivity.PLAYER2_MESSAGE, view.getPlayer2Name()
                + "'s Score\n" + view.getPlayer2Score());

        if(winner == null) {
            //get the winner
            if (player1Score > player2Score)
                winner = view.getPlayer1Name();
            else if (player1Score < player2Score)
                winner = view.getPlayer2Name();
            else
                winner = "TIE!";
        }

        winner = "Winner:\n"+winner;

        intent.putExtra(EndGameActivity.WINNER_MESSAGE, winner);
        startActivity(intent);
        finish();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void updateGUI() {
        int red = Color.parseColor("#FF0000");
        int black = Color.parseColor("#FFFFFF");

        switch (view.getCurrentPlayerId()) {
            case 0:Log.i("Inside 0", String.valueOf(view.getCurrentPlayerId()));
                player1Name.setTextColor(red);
                player2Name.setTextColor(black);
                break;
            case 1:
                Log.i("Inside 1", String.valueOf(view.getCurrentPlayerId()));
                player2Name.setTextColor(red);
                player1Name.setTextColor(black);
                break;
        }

        player1Score.setText(view.getPlayer1Score());
        player2Score.setText(view.getPlayer2Score());
        rounds.setText(view.getRounds());
        capture.setEnabled(view.isCaptureEnabled());
    }

    public void onCaptureClick(View v) {
        view.captureClicked();
        updateGUI();
        isEndGame();
        // update the current player in firebase
        gameRef.child("player").setValue(getIntent().getStringExtra(WelcomeActivity.NAME));
        waitForOpponent();
    }

    private void waitForOpponent() {

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        String dialogMessage = "Waiting for opponent to finish their turn";
        builder.setMessage(dialogMessage);

        android.app.AlertDialog waitdlg = builder.create();
        waitdlg.show();


        // the following assumes that gameRef.child("player") has
        // already been changed to the opponent's id, and waits
        // for it to be changed back to this player's id
        // GRADING: TIMEOUT
        final boolean[] timedOut = {true};
        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // check if gameRef.child("player") has returned to this player's id/name
                if (Objects.equals(snapshot.getValue(String.class), getIntent().getStringExtra(WelcomeActivity.NAME))) {
                    timedOut[0] = false;
                    waitdlg.cancel();
                    // update values from database
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        };
        gameRef.child("player").addValueEventListener(listener);

        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                timer.cancel();
                if (timedOut[0]) {
                    gameRef.child("player").removeEventListener(listener);
                    waitdlg.cancel();
                    Log.d("Timeout", "Win because of timeout");
                    endGame(getIntent().getStringExtra(WelcomeActivity.NAME));
                }
            }
        };
        timer.schedule(timerTask, 10000);
    }



    //GRADING: BACK
    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(GameBoardActivity.this);
        builder.setTitle(R.string.QUIT_GAME);
        builder.setMessage(R.string.QUIT_GAME_MESSAGE);
        builder.setPositiveButton(android.R.string.ok, (dialog, which) -> finish());
        builder.setNegativeButton(android.R.string.cancel, (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    public void onCaptureOptionsClick(View view) {
        Intent switchActivityIntent = new Intent(this, CaptureSelectionActivity.class);
        captureResultLauncher.launch(switchActivityIntent);
    }
}
