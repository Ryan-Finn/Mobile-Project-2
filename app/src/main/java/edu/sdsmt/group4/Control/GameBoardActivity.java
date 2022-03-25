package edu.sdsmt.group4.Control;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import edu.sdsmt.group4.R;
import edu.sdsmt.group4.View.GameBoardView;

public class GameBoardActivity extends AppCompatActivity {
    private GameBoardView view;
    public static final String CAPTURED_INT = "edu.sdsmt.group4.RETURN_MESSAGE";
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

        Log.i("Reached here", "reached");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_board);
        view = this.findViewById(R.id.gameBoardView);

        //get player names and no of rounds from prev
        Intent intent = getIntent();
        String name1 = intent.getStringExtra(WelcomeActivity.PLAYER1NAME_MESSAGE);
        String name2 = intent.getStringExtra(WelcomeActivity.PLAYER2NAME_MESSAGE);
        String r = intent.getStringExtra(WelcomeActivity.ROUNDS_MESSAGE);

        if (name1.isEmpty())
            name1 = getString(R.string.Name1);
        if (name2.isEmpty())
            name2 = getString(R.string.Name2);
        if (r.isEmpty() || Integer.parseInt(r) <= 0)
            r = "5";

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
            String winner = "WINNER\n";
            int player1Score = Integer.parseInt(view.getPlayer1Score());
            int player2Score = Integer.parseInt(view.getPlayer2Score());

            Intent intent = new Intent(this, EndGameActivity.class);

            intent.putExtra(EndGameActivity.PLAYER1_MESSAGE, view.getPlayer1Name()
                    + "'s Score\n" + view.getPlayer1Score());
            intent.putExtra(EndGameActivity.PLAYER2_MESSAGE, view.getPlayer2Name()
                    + "'s Score\n" + view.getPlayer2Score());

            //get the winner
            if (player1Score > player2Score)
                winner += view.getPlayer1Name();
            else if (player1Score < player2Score)
                winner += view.getPlayer2Name();
            else
                winner = "TIE!";
            intent.putExtra(EndGameActivity.WINNER_MESSAGE, winner);
            startActivity(intent);
            finish();
        }
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
