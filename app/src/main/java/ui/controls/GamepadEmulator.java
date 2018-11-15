package ui.controls;

import org.libsdl.app.SDLControllerManager;

class GamepadEmulator {

    private static Boolean registered = false;

    static void updateStick(int stickId, float x, float y) {
        int deviceId = 1;

        if (!registered) {
            registered = true;
            SDLControllerManager.nativeAddJoystick(deviceId, "Virtual", "Virtual",
                    0xbad, 0xf00d,
                    false, 0xFFFFFFFF,
                    4, 0, 0);
        }

        SDLControllerManager.onNativeJoy(deviceId, stickId * 2    , x);
        SDLControllerManager.onNativeJoy(deviceId, stickId * 2 + 1, y);
    }

}
