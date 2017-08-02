package it.uniroma1.di.eulqinaku.hover;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;


public class HoverService extends Service {

    private WindowManager windowManager;
    private View viewBckg;
    private View listenerBtn;
    private boolean imgShown = false;

    long firstHover = 0;
    final Handler handler = new Handler();

    //screen dimensions
    int screenWidth;
    int screenHeight;


    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("Hover", "Service HoverService");
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        getDisplayDimensions();

        // background overlay params
        final WindowManager.LayoutParams paramsBckg = new WindowManager.LayoutParams(
                screenWidth,
                screenHeight,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                        | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);
        paramsBckg.gravity = Gravity.RIGHT | Gravity.BOTTOM;

        final WindowManager.LayoutParams paramsBckgOff = new WindowManager.LayoutParams();
        paramsBckgOff.copyFrom(paramsBckg);
        paramsBckgOff.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        // end background overlay params


        //start listener touch outside
        final WindowManager.LayoutParams listenerParams = new WindowManager.LayoutParams(
                0,
                0,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                    | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
                    | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);
        listenerParams.x = 0; listenerParams.y = 0;

        listenerBtn = new View(this);
        listenerBtn.setBackgroundColor(0x66555555); // set to fully-transparent in a real scenario
        windowManager.addView(listenerBtn, listenerParams);
        //end listener touch outside



        // Create ImageView
        viewBckg = new View(this);
        viewBckg.setBackgroundColor(0xaaff0000);    // set to fully-transparent in a real scenario

        try {
            viewBckg.setOnHoverListener(new View.OnHoverListener() {
                @Override
                public boolean onHover(View view, MotionEvent motionEvent) {
                    if (imgShown) {
                        if (firstHover == 0) {  // if first hover event after click
                            firstHover = 1;
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    if (imgShown) {
                                        imgShown = false;
                                        windowManager.removeViewImmediate(viewBckg);
                                        Log.i("Hover", "-1-2- Deactivate Overlay @: " + SystemClock.uptimeMillis());
                                    }
                                }
                            }, 70);
                        }
                        String tmplog = "EVENT_HOVER " + motionEvent.getRawX() + " " + motionEvent.getRawY()
                                + " " + motionEvent.getEventTime() + " 0 " + motionEvent.getToolMajor()
                                + " " + motionEvent.getToolMinor() + "\n";

                        Log.i("Hover", tmplog);    // print string in adb
                    }
                    return false;
                }
            });

            listenerBtn.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_OUTSIDE:
                            Log.i("Hover", "-1-2- Click happened somewhere @ " + SystemClock.uptimeMillis());

                            if (!imgShown) {
                                windowManager.addView(viewBckg, paramsBckg);
                                imgShown = true;
                                firstHover = 0;
                                Log.i("Hover", "-1-2- Activate Overlay @: " + SystemClock.uptimeMillis());
                            }
                            break;
                    }
                    return false;
                }
            });
        } catch (Exception e) {
            // TODO: handle exception
        }

        return;
    }

    /**
     * get dimensions of display
     */
    private void getDisplayDimensions() {
        Display display = windowManager.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screenWidth = size.x;
        screenHeight = size.y;

        DisplayMetrics outMetrics = new DisplayMetrics ();
        display.getMetrics(outMetrics);

        float density  = getResources().getDisplayMetrics().density;
        float dpHeight = outMetrics.heightPixels / density;
        float dpWidth  = outMetrics.widthPixels / density;

        Log.i("Hover", "Screen: " + screenHeight + "px x " + screenWidth + "px"
                + ", dip" + density + ", dpHeight: " + dpHeight + ", dpWidth: " + dpWidth);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (imgShown) {
            imgShown = false;
            windowManager.removeView(viewBckg);
            windowManager.removeView(listenerBtn);
        }
    }
}
