package ui.controls;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.InputDevice;
import android.view.MotionEvent;
import android.view.View;

import org.libsdl.app.SDLActivity;

public class TouchCameraSimulation extends View {

    private float x1, x2, y1, y2;
    private DirectionListener directionListener;
    private MotionEvent event;
    private Context context;

    public TouchCameraSimulation(Context context) {
        super(context);
        initView();
        this.context = context;
    }

    public TouchCameraSimulation(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
        this.context = context;
    }

    public TouchCameraSimulation(Context context, AttributeSet attrs,
                                 int defStyle) {
        super(context, attrs, defStyle);
        initView();
        this.context = context;
    }

    private void initView() {
        setFocusable(true);
    }

}
