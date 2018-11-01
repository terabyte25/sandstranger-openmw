package ui.controls;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.InputDevice;
import android.view.MotionEvent;
import android.view.View;

import org.libsdl.app.SDLActivity;
import org.libsdl.app.SDLGenericMotionListener_API12;

public class TouchCameraSimulation extends View{

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

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        final int touchDevId = event.getDeviceId();
        final int pointerCount = event.getPointerCount();
        int action = event.getActionMasked();
        int pointerFingerId;
        int mouseButton;
        int i = -1;
        float x,y,p;

        // !!! FIXME: dump this SDK check after 2.0.4 ships and require API14.
        // 12290 = Samsung DeX mode desktop mouse
        if ((event.getSource() == InputDevice.SOURCE_MOUSE || event.getSource() == 12290) && SDLActivity.mSeparateMouseAndTouch) {
            if (Build.VERSION.SDK_INT < 14) {
                mouseButton = 1; // all mouse buttons are the left button
            } else {
                try {
                    mouseButton = (Integer) event.getClass().getMethod("getButtonState").invoke(event);
                } catch(Exception e) {
                    mouseButton = 1;    // oh well.
                }
            }

            // We need to check if we're in relative mouse mode and get the axis offset rather than the x/y values
            // if we are.  We'll leverage our existing mouse motion listener
            SDLGenericMotionListener_API12 motionListener = SDLActivity.getMotionListener();
            x = motionListener.getEventX(event);
            y = motionListener.getEventY(event);

            SDLActivity.onNativeMouse(mouseButton, action, x, y, motionListener.inRelativeMode());
        } else {
            switch(action) {
                case MotionEvent.ACTION_MOVE:
                    for (i = 0; i < pointerCount; i++) {
                        pointerFingerId = event.getPointerId(i);
                        x = event.getX(i) / SDLActivity.getSurfaceWidth();
                        y = event.getY(i) / SDLActivity.getSurfaceHeight();
                        p = event.getPressure(i);
                        if (p > 1.0f) {
                            // may be larger than 1.0f on some devices
                            // see the documentation of getPressure(i)
                            p = 1.0f;
                        }
                        SDLActivity.onNativeTouch(touchDevId, 0, action, x, y, p);
                    }
                    break;

                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_DOWN:
                    // Primary pointer up/down, the index is always zero
                    i = 0;
                case MotionEvent.ACTION_POINTER_UP:
                case MotionEvent.ACTION_POINTER_DOWN:
                    // Non primary pointer up/down
                    if (i == -1) {
                        i = event.getActionIndex();
                    }

                    pointerFingerId = event.getPointerId(i);
                    x = event.getX(i) / SDLActivity.getSurfaceWidth();
                    y = event.getY(i) / SDLActivity.getSurfaceHeight();
                    p = event.getPressure(i);
                    if (p > 1.0f) {
                        // may be larger than 1.0f on some devices
                        // see the documentation of getPressure(i)
                        p = 1.0f;
                    }
                    SDLActivity.onNativeTouch(touchDevId, 0, action, x, y, p);
                    break;

                case MotionEvent.ACTION_CANCEL:
                    for (i = 0; i < pointerCount; i++) {
                        pointerFingerId = event.getPointerId(i);
                        x = event.getX(i) / SDLActivity.getSurfaceWidth();
                        y = event.getY(i) / SDLActivity.getSurfaceHeight();
                        p = event.getPressure(i);
                        if (p > 1.0f) {
                            // may be larger than 1.0f on some devices
                            // see the documentation of getPressure(i)
                            p = 1.0f;
                        }
                        SDLActivity.onNativeTouch(touchDevId, 0, MotionEvent.ACTION_UP, x, y, p);
                    }
                    break;

                default:
                    break;
            }
        }

        return true;
    }

}
