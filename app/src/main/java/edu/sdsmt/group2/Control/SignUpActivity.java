package edu.sdsmt.group2.Control;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import edu.sdsmt.group2.Model.Cloud;
import edu.sdsmt.group2.R;

public class SignUpActivity  extends AppCompatActivity {
    private Cloud monitor;
    private TextView user, email, pass, pass2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        monitor = Cloud.INSTANCE;
        user = findViewById(R.id.user);
        email = findViewById(R.id.email);
        pass = findViewById(R.id.password);
        pass2 = findViewById(R.id.password2);
    }

    public void onCreateAccount(View view) {
        if (!pass.getText().toString().equals(pass2.getText().toString())) {
            view.post(() -> Toast.makeText(view.getContext(), R.string.badPass, Toast.LENGTH_LONG).show());
            pass.setText("");
            pass2.setText("");
            return;
        }

        monitor.createUser(user.getText().toString(), email.getText().toString(), pass.getText().toString(), view, this);
    }
}
