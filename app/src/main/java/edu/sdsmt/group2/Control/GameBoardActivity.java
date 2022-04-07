package edu.sdsmt.group2.Control;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
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

import java.util.Timer;
import java.util.TimerTask;

import edu.sdsmt.group2.R;
import edu.sdsmt.group2.View.GameBoardView;

public class GameBoardActivity extends AppCompatActivity {
    private GameBoardView view;
    private final DatabaseReference gameRef = FirebaseDatabase.getInstance().getReference().child("game2");
    public static final String CAPTURED_INT = "edu.sdsmt.group2.RETURN_MESSAGE";
    private TextView player1Score;
    private TextView player2Score;
    private TextView rounds;
    private Button capture;
    private ActivityResultLauncher<Intent> captureResultLauncher;
    private ValueEventListener listener;

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
        view.init(this);

        //get player names and no of rounds from prev
        Intent intent = getIntent();
        int player = intent.getIntExtra(WaitActivity.PLAYER, 1);
        String name1 = intent.getStringExtra(WaitActivity.PLAYER1);
        String name2 = intent.getStringExtra(WaitActivity.PLAYER2);

        view.addPlayer(name1,1);
        view.addPlayer(name2,2);
        view.setRounds(5);
        view.setPlayer(player);

        if (player == 2)
            findViewById(R.id.button).setEnabled(false);

        TextView player1Name = findViewById(R.id.player1Name);
        TextView player2Name = findViewById(R.id.player2Name);
        player1Score = findViewById(R.id.player1Score);
        player2Score = findViewById(R.id.player2Score);
        capture = findViewById(R.id.captureButton);
        capture.setEnabled(false);
        rounds = findViewById(R.id.rounds);
        player1Name.setText(name1);
        player2Score.setText("0");
        player2Name.setText(name2);
        player1Score.setText("0");
        rounds.setText("5");

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

    public void isEndGame() {
        if (view.isEndGame())
            endGame(null);
        else
            waitForOpponent();
    }

    private void endGame(String winner) {
        gameRef.child("nextPlayer").removeEventListener(listener);
        view.destroy();

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

        winner = "Winner:\n" + winner;

        intent.putExtra(EndGameActivity.WINNER_MESSAGE, winner);
        startActivity(intent);
        finish();
    }

    @SuppressLint("ClickableViewAccessibility")
    public void updateGUI() {
        String text = "" + view.getRounds();
        player1Score.setText(view.getPlayer1Score());
        player2Score.setText(view.getPlayer2Score());
        rounds.setText(text);
        capture.setEnabled(view.isCaptureEnabled());
        isEndGame();
    }

    public void onCaptureClick(View v) {
        view.captureClicked();
        updateGUI();
    }

    private void waitForOpponent() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        String dialogMessage = "Waiting for opponent to finish their turn";
        builder.setMessage(dialogMessage);

        android.app.AlertDialog waitdlg = builder.create();
        waitdlg.show();

        // the following assumes that gameRef.child("nextPlayer") has
        // already been changed to the opponent's id, and waits
        // for it to be changed back to this player's id
        // GRADING: TIMEOUT
        final boolean[] timedOut = {true};
        listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // check if gameRef.child("nextPlayer") has returned to this player's id/name
                if (snapshot.exists() && snapshot.getValue(Integer.class) != null && snapshot.getValue(Integer.class) == getIntent().getIntExtra(WaitActivity.PLAYER, 1)) {
                    timedOut[0] = false;
                    waitdlg.cancel();
                    // update values from database
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        };
        gameRef.child("nextPlayer").addValueEventListener(listener);

        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                timer.cancel();
                if (timedOut[0]) {
                    gameRef.child("nextPlayer").removeEventListener(listener);
                    waitdlg.cancel();
                    Log.d("Timeout", "Win because of timeout");
                    endGame(getIntent().getStringExtra(WelcomeActivity.NAME));
                }
            }
        };
        timer.schedule(timerTask, 15000);
    }

    //GRADING: BACK
    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(GameBoardActivity.this);
        builder.setTitle(R.string.QUIT_GAME);
        builder.setMessage(R.string.QUIT_GAME_MESSAGE);
        builder.setPositiveButton(android.R.string.ok, (dialog, which) -> endGame("Opponent, due to forfeit"));
        builder.setNegativeButton(android.R.string.cancel, (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    public void onCaptureOptionsClick(View view) {
        Intent switchActivityIntent = new Intent(this, CaptureSelectionActivity.class);
        captureResultLauncher.launch(switchActivityIntent);
    }
}
