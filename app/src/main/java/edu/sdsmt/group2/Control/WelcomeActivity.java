package edu.sdsmt.group2.Control;

/* *
 * Project 2 Grading

firebase login: ryan.finn.firebase@gmail.com
firebase password: Psr4&igk3$5EH@Qb
Time out period: 15s
How to reset database (file or button): file
Reminder: Mark where the timeout period is set with GRADING: TIMEOUT


Group:

__X__ 6pt Game still works and Database setup
__X__ 8pt Database setup\reset
__X__ 8pt New user activity
__X__ 18pt Opening\login activity
____ 5pt rotation


Individual:

	Sequencing
		__X__ 4pt Registration sequence
		__X__ 9pt Login Sequence
		__X__ 18pt Play Sequence
		__X__ 9pt Exiting menu, and handlers
		____ 5pt rotation


	Upload

		__X__ 6pt intial setup
		__X__ 6pt waiting
		__X__ 17pt store game state
		__X__ 11pt notify end/early exits
		____ 5pt rotation


	Download

		__X__ 6pt intial setup
		__X__ 6pt waiting
		__X__ 17pt store game state
		__X__ 11pt grab and forward end/early exits
		____ 5pt rotation


	Monitor Waiting
		__X__ 10pt inital setup
		__X__ 12pt Uploading the 3 state
		__X__ 12pt Downloading the 3 state
		__X__ 6pt UI update
		____ 5pt rotation

Please list any additional rules that may be needed to properly grade your project:
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
