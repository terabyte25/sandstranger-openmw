
package ui.activity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Process;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.system.ErrnoException;
import android.system.Os;
import android.util.Log;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.libopenmw.openmw.R;

import org.libsdl.app.SDLActivity;
import org.libsdl.app.SDLInputConnection;

import constants.Constants;
import cursor.MouseCursor;
import parser.CommandlineParser;
import ui.controls.Joystick;
import ui.game.GameState;
import ui.screen.ScreenScaler;
import ui.controls.QuickPanel;
import ui.controls.ScreenControls;
import file.ConfigsFileStorageHelper;

public class GameActivity extends SDLActivity {
    private boolean showMouse;
    //  public static native void getPathToJni(String path);

    //   public static native void commandLine(int argc, String[] argv);

    private boolean hideControls = false;
    private ScreenControls screenControls;
    private MouseCursor cursor;

    @Override
    public void loadLibraries() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String graphicsLibrary = prefs.getString("pref_graphicsLibrary", "");
        String physicsFPS = prefs.getString("pref_physicsFPS", "");
        if (!physicsFPS.isEmpty()) {
            try {
                Os.setenv("OPENMW_PHYSICS_FPS", physicsFPS, true);
            } catch (ErrnoException e) {
                Log.e("OpenMW", "Failed setting environment variables.");
                e.printStackTrace();
            }
        }

        try {
            Os.setenv("OPENMW_PHYSICS_FPS", "15", true);
        } catch (Exception e) {

        }

        if (graphicsLibrary.equals("gles2")) {
            try {
                Os.setenv("OPENMW_GLES_VERSION", "2", true);
                Os.setenv("LIBGL_ES", "2", true);
            } catch (ErrnoException e) {
                Log.e("OpenMW", "Failed setting environment variables.");
                e.printStackTrace();
            }
        }
        System.loadLibrary("openal");
        System.loadLibrary("GL");
        System.loadLibrary("SDL2");
        System.loadLibrary("openmw");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        //    super.mSeparateMouseAndTouch = true;
        super.onCreate(savedInstanceState);
        GameState.setGameState(true);
//        NativeListener.initJavaVm();
        KeepScreenOn();
        //   parseCommandLineData();
        try {
            Os.setenv("DATA_FILES", ConfigsFileStorageHelper.CONFIGS_FILES_STORAGE_PATH, true);
        } catch (Exception e) {

        }
        currentApiVersion = android.os.Build.VERSION.SDK_INT;

        final int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

        // This work only for android 4.4+
        if (currentApiVersion >= Build.VERSION_CODES.KITKAT) {

            getWindow().getDecorView().setSystemUiVisibility(flags);

            // Code below is to handle presses of Volume up or Volume down.
            // Without this, after pressing volume buttons, the navigation bar will
            // show up and won't hide
            final View decorView = getWindow().getDecorView();
            decorView
                    .setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {

                        @Override
                        public void onSystemUiVisibilityChange(int visibility) {
                            if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                                decorView.setSystemUiVisibility(flags);
                            }
                        }
                    });
        }
//        (ConfigsFileStorageHelper.CONFIGS_FILES_STORAGE_PATH);
        showControls();
    }


    private void showControls() {
        hideControls = PreferenceManager.getDefaultSharedPreferences(this).getBoolean(Constants.HIDE_CONTROLS, false);
        if (!hideControls) {
            screenControls = new ScreenControls(this);
            screenControls.showControls(hideControls);
            QuickPanel panel = new QuickPanel(this);
            panel.showQuickPanel(hideControls);
            QuickPanel.getInstance().f1.setVisibility(Button.VISIBLE);
            cursor = new MouseCursor(this);
        }
    }

    private void KeepScreenOn() {
        boolean needKeepScreenOn = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("screen_keeper", false);
        if (needKeepScreenOn) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        finish();
        Process.killProcess(Process.myPid());
    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if (!hideControls) {
            ScreenScaler.textScaler(QuickPanel.getInstance().showPanel, 4);
            ScreenScaler.textScaler(QuickPanel.getInstance().f1, 4);
            QuickPanel.getInstance().f1.setVisibility(Button.GONE);
        }
        if(currentApiVersion >= Build.VERSION_CODES.KITKAT && hasFocus)
        {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }


    // Touch events
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN:
                if (numPointersDown == 0) {
                    startX = event.getX();
                    startY = event.getY();
                }
                ++numPointersDown;
                maxPointersDown = Math.max(numPointersDown, maxPointersDown);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                numPointersDown = Math.max(0, numPointersDown - 1);
                if (numPointersDown == 0) {
                    // everything's up, do the action
                    if (maxPointersDown == 3) {
                        showVirtualInput();
                    } else if (!isMoving && SDLActivity.isMouseShown() != 0) {
                        // only send clicks if we didn't move
                        int mouseX = SDLActivity.getMouseX();
                        int mouseY = SDLActivity.getMouseY();
                        int mouseButton = 0;

                        if (maxPointersDown == 1)
                            mouseButton = 1;
                        else if (maxPointersDown == 2)
                            mouseButton = 2;

                        if (mouseButton != 0) {
                            SDLActivity.onNativeMouse(mouseButton, MotionEvent.ACTION_DOWN, mouseX, mouseY);
                            final Handler handler = new Handler();
                            handler.postDelayed(() -> SDLActivity.onNativeMouse(0, MotionEvent.ACTION_UP, mouseX, mouseY), 100);
                        }
                    }

                    maxPointersDown = 0;
                    isMoving = false;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (maxPointersDown == 1) {
                    float diffX = event.getX() - startX;
                    float diffY = event.getY() - startY;
                    double distance = Math.sqrt(diffX * diffX + diffY * diffY);

                    if (distance > mouseDeadzone) {
                        isMoving = true;
                        startX = event.getX();
                        startY = event.getY();
                    } else if (isMoving) {
                        int mouseX = SDLActivity.getMouseX();
                        int mouseY = SDLActivity.getMouseY();

                        long newMouseX = Math.round(mouseX + diffX * mouseScalingFactor);
                        long newMouseY = Math.round(mouseY + diffY * mouseScalingFactor);

                        if (SDLActivity.isMouseShown() != 0)
                            SDLActivity.onNativeMouse(0, MotionEvent.ACTION_MOVE, newMouseX, newMouseY);

                        startX = event.getX();
                        startY = event.getY();
                    }
                }
                break;
        }

        return true;
    }

    private void showVirtualInput() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Virtual input");

        final EditText input = new EditText(this);
        builder.setView(input);

        builder.setPositiveButton("OK", (dialog, which) -> {
            String text = input.getText().toString();
            SDLInputConnection.nativeCommitText(text, 0);
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

}
