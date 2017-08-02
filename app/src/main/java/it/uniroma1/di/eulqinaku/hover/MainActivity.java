package it.uniroma1.di.eulqinaku.hover;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.os.SystemClock;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;


public class MainActivity extends Activity {

    long timediff;
    int kbd = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

        timediff = System.currentTimeMillis() - SystemClock.uptimeMillis();


        // start edit text
        final EditText txtBox = (EditText)findViewById(R.id.txtBox);

        txtBox.setOnKeyListener(new View.OnKeyListener() {

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_UP) {
                    System.out.println("-1-2- Key pressed: " + keyCode);
                }
                return false;
            }
        });
        // end edit text



        // start HoverService test
        final Button kbdFull = (Button)findViewById(R.id.fullScrn);
        kbdFull.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (kbd % 2 == 1) {
                    stopService(new Intent(MainActivity.this, HoverService.class));
                    Log.i("HoverActivity", "Stop HoverService Button");
                } else {
                    startService(new Intent(MainActivity.this, HoverService.class));
                    Log.i("HoverActivity", "Start HoverService Button");
                }
                kbd++;
            }
        });
        // end HoverService test

        Button submitBtn = (Button)findViewById(R.id.submitBtn);
        submitBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("HoverActivity", "-1-2-3- EVENT_SUBMIT 0 0 " + SystemClock.uptimeMillis() + " 0 0 0");
                txtBox.setText("");
                txtBox.setInputType(InputType.TYPE_CLASS_TEXT);
            }
        });
	}


	@Override
	protected void onResume() {
		super.onResume();
	}

    @Override
    protected void onPause() {
        super.onPause();
    }
}
