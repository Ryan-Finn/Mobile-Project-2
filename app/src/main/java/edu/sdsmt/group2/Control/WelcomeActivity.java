package edu.sdsmt.group2.Control;

/* *
 * Project 1 Grading
 *
 * Group:
 * Done 6pt No redundant activities
 * Done 6pt How to play dialog
 * Done 6pt Icons
 * Done 6pt End activity
 * Done 6pt Back button handled
 * How to open the "how to play dialog": Click on the how to play button
 *
 * Individual:
 *
 * 	Play activity and custom view
 *
 * 		Done 9pt Activity appearance
 * 		Done 16pt Static Custom View
 * 		Done 20pt Dynamic part of the Custom View
 * 		Done 15pt Rotation
 *
 * 	Welcome activity and Game Class
 *
 * 		Done 13pt Welcome activity appearance
 * 		Done 20pt Applying capture rules
 * 		Done 12pt Game state
 * 		Done 15pt Rotation
 * 		What is the probability of the rectangle capture: starts with 50% and changes proportional
 *       to the scaling. So if 2 times larger, probability is 25%
 *
 * 	Capture activity and activity sequencing
 *
 * 		Done 9pt Capture activity appearance
 * 		Done 16pt Player round sequencing
 * 		Done 20pt Move to next activity
 * 		Done 15pt Rotation
 *
 * 	Timer
 *
 * 		NA 9pt Timer activity
 * 		NA 24pt Graphic
 * 		NA 12pt Player turn end
 * 		NA 15pt Rotation
 *
 *
 * Please list any additional rules that may be needed to properly grade your project:
 */

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import edu.sdsmt.group2.Model.Cloud;
import edu.sdsmt.group2.R;

public class WelcomeActivity extends AppCompatActivity {
    public final static String USER = "edu.sdsmt.group2.PLAYER1NAME_MESSAGE";
    public final static String PASS  = "edu.sdsmt.group2.PLAYER2NAME_MESSAGE";
    private Cloud monitor;

    TextView user;
    TextView pass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        monitor = Cloud.INSTANCE;

        setContentView(R.layout.activity_welcome);

        user = findViewById(R.id.username);
        pass = findViewById(R.id.password);
    }

    public void onStart(View view) {

        monitor.login(user.getText().toString(), pass.getText().toString());
        if (monitor.isAuthenticated()) {
            Intent intent = new Intent(this, waitActivity.class);
            startActivity(intent);
        } else {
            view.post(() -> Toast.makeText(view.getContext(), R.string.loginFail, Toast.LENGTH_LONG).show());;
        }


        user.setText("");
        pass.setText("");
    }

    public void onHowToPlay(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(WelcomeActivity.this);
        builder.setTitle(R.string.HowToPlayTitle);
        builder.setMessage(R.string.HowToPlayMessage);
        builder.setPositiveButton(android.R.string.ok, null);
        builder.show();
    }
}
