
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

import org.libsdl.app.EscapeKeySimulation;
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
                Os.setenv("OSG_TEXT_SHADER_TECHNIQUE", "NO_TEXT_SHADER", true);
            } catch (ErrnoException e) {
                Log.e("OpenMW", "Failed setting environment variables.");
                e.printStackTrace();
            }
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
        System.loadLibrary("hidapi");
        System.loadLibrary("SDL2");
        System.loadLibrary("openmw");
    }

    private int currentApiVersion;
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
    }


    public void showVirtualInput() {
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

    @Override
    public void onBackPressed(){
        EscapeKeySimulation.onFakeBackPressed();
    }

}
