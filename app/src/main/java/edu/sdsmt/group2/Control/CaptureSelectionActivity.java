package edu.sdsmt.group2.Control;

import androidx.appcompat.app.AppCompatActivity;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import edu.sdsmt.group2.R;

public class CaptureSelectionActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture_selection);
    }

    public void onCircleClick(View view) {
        clicked(0);
    }

    public void onRectangleClick(View view) {
        clicked(1);
    }

    public void onLineClick(View view) {
        clicked(2);
    }

    public void clicked(int captureType)
    {
        Intent resultIntent = new Intent();

        //send the type of button that was clicked
        resultIntent.putExtra(GameBoardActivity.CAPTURED_INT, captureType);

        //say everything went OK
        setResult(Activity.RESULT_OK, resultIntent);

        //stop this activity and return
        finish();
    }
}
