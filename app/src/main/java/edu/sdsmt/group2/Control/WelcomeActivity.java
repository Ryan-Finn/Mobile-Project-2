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
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import edu.sdsmt.group2.Model.Cloud;
import edu.sdsmt.group2.R;

public class WelcomeActivity extends AppCompatActivity {
    private Cloud monitor;
    private CheckBox remember;
    private TextView user, pass;
    private SharedPreferences.Editor loginInfoEditor;
    public final static String NAME = "edu.sdsmt.group2.NAME";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        monitor = Cloud.INSTANCE;
        user = findViewById(R.id.user);
        pass = findViewById(R.id.password);
        remember = findViewById(R.id.remember);

        SharedPreferences loginInfo = getSharedPreferences("login", MODE_PRIVATE);
        loginInfoEditor = loginInfo.edit();
        if (loginInfo.getBoolean("remember", false)) {
            user.setText(loginInfo.getString("user", ""));
            pass.setText(loginInfo.getString("pass", ""));
            remember.setChecked(true);
        }
    }

    public void onStart(View view) {
        if (remember.isChecked()) {
            loginInfoEditor.putBoolean("remember", true);
            loginInfoEditor.putString("user", user.getText().toString());
            loginInfoEditor.putString("pass", pass.getText().toString());
        } else
            loginInfoEditor.clear();
        loginInfoEditor.commit();

        monitor.login(user.getText().toString(), pass.getText().toString(), view, this);
//        startActivity(new Intent(this, GameBoardActivity.class));
    }

    public void onHowToPlay(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(WelcomeActivity.this);
        builder.setTitle(R.string.HowToPlayTitle);
        builder.setMessage(R.string.HowToPlayMessage);
        builder.setPositiveButton(android.R.string.ok, null);
        builder.show();
    }

    public void onSignUp(View view) {
        startActivity(new Intent(this, SignUpActivity.class));
    }
}
