package ui.controls;

import org.libsdl.app.SDL;
import org.libsdl.app.SDLActivity;

import ui.activity.GameActivity;
import ui.screen.ScreenScaler;

import com.libopenmw.openmw.R;

import constants.Constants;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;

public class ScreenControls {

	public static boolean showControls = false;
	private boolean enableTouch = false;
	private boolean crouchFlag = false;
	private boolean hideControls;
	Activity a;
	private static ScreenControls instance = null;

	public ScreenControls(Activity a) {
		super();
		this.a = a;
		instance=this;
	}

	public static ScreenControls getInstance() {
		return instance;
	}
	public void showControls(boolean hide) {
		if (!hide) {


			SharedPreferences Settings;

			Settings = a.getSharedPreferences(Constants.APP_PREFERENCES,
					Context.MODE_PRIVATE);
			int controlsFlag = Settings.getInt(
					Constants.APP_PREFERENCES_RESET_CONTROLS, -1);
			enableTouch = false;

			DisplayMetrics displaymetrics;
			displaymetrics = new DisplayMetrics();
			a.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
			ScreenScaler.height = displaymetrics.heightPixels;
			ScreenScaler.width = displaymetrics.widthPixels;
			LayoutInflater inflater = a.getLayoutInflater();
			a.getWindow().addContentView(
					inflater.inflate(R.layout.screencontrols, null),
					new ViewGroup.LayoutParams(
							ViewGroup.LayoutParams.FILL_PARENT,
							ViewGroup.LayoutParams.FILL_PARENT));

			final Joystick joystickLeft = (Joystick) a.findViewById(R.id.joystickLeft);
			joystickLeft.setStick(0);

			final ImageButton buttonRun = (ImageButton) a
					.findViewById(R.id.buttonrun1);

			buttonRun.setOnTouchListener(new ButtonTouchListener(
					115,false));

			final ImageButton buttonConsole = (ImageButton) a
					.findViewById(R.id.buttonconsole);

			buttonConsole.setOnTouchListener(new ButtonTouchListener(
					132,false));

			final ImageButton buttonChangePerson = (ImageButton) a
					.findViewById(R.id.buttonchangeperson);

			buttonChangePerson.setOnTouchListener(new ButtonTouchListener(
					KeyEvent.KEYCODE_TAB,false));

			final ImageButton buttonWait = (ImageButton) a
					.findViewById(R.id.buttonwait);

			buttonWait.setOnTouchListener(new ButtonTouchListener(
					KeyEvent.KEYCODE_T,false));


			final ImageButton buttonLoad = (ImageButton) a
					.findViewById(R.id.buttonsuperload);

			buttonLoad.setOnTouchListener(new ButtonTouchListener(
					139,false));

			final ImageButton buttonSave = (ImageButton) a
					.findViewById(R.id.buttonsupersave);

			buttonSave.setOnTouchListener(new ButtonTouchListener(
					135,false));

			final ImageButton buttonWeapon = (ImageButton) a
					.findViewById(R.id.buttonweapon);

			buttonWeapon.setOnTouchListener(new ButtonTouchListener(
					KeyEvent.KEYCODE_F,false));

			final ImageButton buttonInventory = (ImageButton) a
					.findViewById(R.id.buttoninventory);

			buttonInventory.setOnTouchListener(new ButtonTouchListener(
					KeyEvent.KEYCODE_B,false));

			final ImageButton buttonJump = (ImageButton) a
					.findViewById(R.id.buttonsuperjump);

			buttonJump.setOnTouchListener(new ButtonTouchListener(
					KeyEvent.KEYCODE_E,false));

			final ImageButton buttonFire = (ImageButton) a
					.findViewById(R.id.buttonFire);

			buttonFire.setOnTouchListener(new ButtonTouchListener(
					KeyEvent.KEYCODE_N,false));
			final ImageButton buttonMagic = (ImageButton) a
					.findViewById(R.id.buttonMagic);

			buttonMagic.setOnTouchListener(new ButtonTouchListener(
					KeyEvent.KEYCODE_R,false));
			crouchFlag = false;

			final ImageButton buttonCrouch = (ImageButton) a
					.findViewById(R.id.buttoncrouch);

			buttonCrouch.setOnTouchListener(new View.OnTouchListener() {
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN:
						ScaleSimulation.onTouchDown(v);

						if (crouchFlag == false) {
							SDLActivity
									.onNativeKeyDown(113);
							crouchFlag = true;
						} else {
							SDLActivity
									.onNativeKeyUp(113);
							crouchFlag = false;
						}
						return true;
					case MotionEvent.ACTION_UP:
						ScaleSimulation.onTouchUp(v);
						return true;
					}
					return false;
				}
			});

			final ImageButton buttonDiary = (ImageButton) a
					.findViewById(R.id.buttonDiary);

			final ImageButton buttonKeyboard = (ImageButton) a
					.findViewById(R.id.keyboard_image_button);

			buttonDiary.setOnTouchListener(new ButtonTouchListener(
					KeyEvent.KEYCODE_J,false));

			buttonKeyboard.setOnClickListener(view -> {
                GameActivity activity = (GameActivity) a;
                activity.showVirtualInput();
            });

			final ImageButton buttonBackspace = a.findViewById(R.id.buttonBackspace);
			buttonBackspace.setOnTouchListener(new ButtonTouchListener(
					KeyEvent.KEYCODE_DEL,false));

			final ImageButton buttonUse = (ImageButton) a
					.findViewById(R.id.buttonUse);

			buttonUse.setOnTouchListener(new ButtonTouchListener(
					KeyEvent.KEYCODE_SPACE,false));
			hideControls = false;
			final ImageButton buttonPause = (ImageButton) a
					.findViewById(R.id.buttonpause);

			buttonPause.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					showControls = !showControls;
				}
			});

			if (controlsFlag == -1 || controlsFlag == 1) {
				joystickLeft.setLayoutParams(ControlsParams.coordinates(joystickLeft,
						75, 400, 250, 250));

				buttonRun.setLayoutParams(ControlsParams.coordinates(buttonRun,
						65, 330, 70, 70));


				AlphaView.setAlphaForView(buttonRun, 0.5f);
				AlphaView.setAlphaForView(joystickLeft,0.5f);
				AlphaView.setAlphaForView(buttonCrouch,0.5f);
				AlphaView.setAlphaForView(buttonUse,0.5f);
				AlphaView.setAlphaForView(buttonMagic,0.5f);
				AlphaView.setAlphaForView(buttonFire,0.5f);
				AlphaView.setAlphaForView(buttonJump,0.5f);
				AlphaView.setAlphaForView(buttonJump,0.5f);
				AlphaView.setAlphaForView(buttonWeapon,0.5f);
				AlphaView.setAlphaForView(buttonInventory,0.5f);
				AlphaView.setAlphaForView(buttonLoad,0.5f);
				AlphaView.setAlphaForView(buttonSave,0.5f);
				AlphaView.setAlphaForView(buttonPause,0.5f);
				AlphaView.setAlphaForView(buttonDiary,0.5f);
				AlphaView.setAlphaForView(buttonKeyboard,0.5f);
				AlphaView.setAlphaForView(buttonBackspace,0.5f);
				AlphaView.setAlphaForView(buttonChangePerson,0.5f);
				AlphaView.setAlphaForView(buttonWait,0.5f);
				AlphaView.setAlphaForView(buttonConsole,0.5f);


				buttonConsole.setLayoutParams(ControlsParams.coordinates(
						buttonConsole, 140, 0, 70, 70));

				buttonChangePerson.setLayoutParams(ControlsParams.coordinates(
						buttonChangePerson, 212, 0, 70, 70));
				buttonWait.setLayoutParams(ControlsParams.coordinates(
						buttonWait, 274, 0, 70, 70));
				buttonWeapon.setLayoutParams(ControlsParams.coordinates(
						buttonWeapon, 880, 95, 70, 70));
				buttonDiary.setLayoutParams(ControlsParams.coordinates(
						buttonDiary, 414, 0, 70, 70));

				buttonKeyboard.setLayoutParams(ControlsParams.coordinates(
						buttonKeyboard, 500, 0, 70, 70));

				buttonBackspace.setLayoutParams(ControlsParams.coordinates(
						buttonBackspace, 600, 0, 70, 70));
				buttonPause.setLayoutParams(ControlsParams.coordinates(
						buttonPause, 950, 0, 60, 60));
				buttonLoad.setLayoutParams(ControlsParams.coordinates(
						buttonLoad, 880, 0, 60, 60));
				buttonSave.setLayoutParams(ControlsParams.coordinates(
						buttonSave, 820, 0, 60, 60));
				buttonInventory.setLayoutParams(ControlsParams.coordinates(
						buttonInventory, 950, 95, 70, 70));

				buttonJump.setLayoutParams(ControlsParams.coordinates(
						buttonJump, 920, 195, 90, 90));

				buttonFire.setLayoutParams(ControlsParams.coordinates(
						buttonFire, 790, 300, 100, 100));

				buttonMagic.setLayoutParams(ControlsParams.coordinates(
						buttonMagic, 940, 480, 80, 80));

				buttonUse.setLayoutParams(ControlsParams.coordinates(buttonUse,
						940, 368, 80, 80));

				buttonCrouch.setLayoutParams(ControlsParams.coordinates(
						buttonCrouch, 940, 670, 80, 80));

			} else if (controlsFlag == 0) {
				AlphaView.setAlphaForView(buttonRun,Settings.getFloat(
						Constants.APP_PREFERENCES_BUTTON_RUN_OPACITY, 0.5f));

				AlphaView.setAlphaForView(buttonConsole,Settings.getFloat(
						Constants.APP_PREFERENCES_BUTTON_CONSOLE_OPACITY, -1));

				AlphaView.setAlphaForView(buttonWait, Settings.getFloat(
						Constants.APP_PREFERENCES_BUTTON_WAIT_OPACITY, -1));

				AlphaView.setAlphaForView(buttonDiary, Settings.getFloat(
						Constants.APP_PREFERENCES_BUTTON_DIARY_OPACITY, -1));

				AlphaView.setAlphaForView(buttonKeyboard, Settings.getFloat(
						Constants.APP_PREFERENCES_BUTTON_KEYBOARD_OPACITY, -1));

				AlphaView.setAlphaForView(buttonBackspace, Settings.getFloat(
						Constants.APP_PREFERENCES_BUTTON_BACKSPACE_OPASITY, -1));

				AlphaView.setAlphaForView(buttonPause,Settings.getFloat(
						Constants.APP_PREFERENCES_BUTTON_PAUSE_OPACITY, -1));

				AlphaView.setAlphaForView(buttonLoad, Settings.getFloat(
						Constants.APP_PREFERENCES_BUTTON_LOAD_OPACITY, -1));

				AlphaView.setAlphaForView(buttonSave, Settings.getFloat(
						Constants.APP_PREFERENCES_BUTTON_SAVE_OPACITY, -1));

				AlphaView.setAlphaForView(buttonWeapon, Settings.getFloat(
						Constants.APP_PREFERENCES_BUTTON_WEAPON_OPACITY, -1));

				AlphaView.setAlphaForView(buttonJump, Settings.getFloat(
						Constants.APP_PREFERENCES_BUTTON_JUMP_OPACITY, -1));

				AlphaView.setAlphaForView(buttonInventory, Settings
						.getFloat(
								Constants.APP_PREFERENCES_BUTTON_INVENTORY_OPACITY,
								-1));

				AlphaView.setAlphaForView(buttonFire, Settings.getFloat(
						Constants.APP_PREFERENCES_BUTTON_FIRE_OPACITY, -1));

				AlphaView.setAlphaForView(buttonChangePerson, Settings.getFloat(
						Constants.APP_PREFERENCES_BUTTON_CHANGEPERSON_OPACITY, -1));


				AlphaView.setAlphaForView(buttonUse, Settings.getFloat(
						Constants.APP_PREFERENCES_BUTTON_USE_OPACITY, -1));

				AlphaView.setAlphaForView(buttonMagic, Settings.getFloat(
						Constants.APP_PREFERENCES_BUTTON_USE_OPACITY, -1));

				AlphaView.setAlphaForView(buttonCrouch, Settings.getFloat(
						Constants.APP_PREFERENCES_BUTTON_CROUCH_OPACITY, -1));

				AlphaView.setAlphaForView(joystickLeft, Settings.getFloat(
						Constants.APP_PREFERENCES_JOYSTICK_OPACITY, -1));


				joystickLeft.setLayoutParams(ControlsParams.coordinates(joystickLeft,
						Settings.getInt(
								Constants.APP_PREFERENCES_JOYSTICK_X, -1),
						Settings.getInt(Constants.APP_PREFERENCES_JOYSTICK_Y,
								-1), Settings.getInt(
								Constants.APP_PREFERENCES_JOYSTICK_SIZE, -1),
						Settings.getInt(
								Constants.APP_PREFERENCES_JOYSTICK_SIZE, -1)));

				buttonRun
						.setLayoutParams(ControlsParams
								.coordinatesConfigureControls(
										buttonRun,
										Settings.getInt(
												Constants.APP_PREFERENCES_BUTTON_RUN_X,
												-1),
										Settings.getInt(
												Constants.APP_PREFERENCES_BUTTON_RUN_Y,
												-1),
										Settings.getInt(
												Constants.APP_PREFERENCES_BUTTON_RUN_SIZE,
												-1),
										Settings.getInt(
												Constants.APP_PREFERENCES_BUTTON_RUN_SIZE,
												-1)));

				buttonConsole
						.setLayoutParams(ControlsParams
								.coordinatesConfigureControls(
										buttonConsole,
										Settings.getInt(
												Constants.APP_PREFERENCES_BUTTON_CONSOLE_X,
												-1),
										Settings.getInt(
												Constants.APP_PREFERENCES_BUTTON_CONSOLE_Y,
												-1),
										Settings.getInt(
												Constants.APP_PREFERENCES_BUTTON_CONSOLE_SIZE,
												-1),
										Settings.getInt(
												Constants.APP_PREFERENCES_BUTTON_CONSOLE_SIZE,
												-1)));
				buttonChangePerson
						.setLayoutParams(ControlsParams.coordinatesConfigureControls(
								buttonChangePerson,
								Settings.getInt(
										Constants.APP_PREFERENCES_BUTTON_CHANGEPERSON_X,
										-1),
								Settings.getInt(
										Constants.APP_PREFERENCES_BUTTON_CHANGEPERSON_Y,
										-1),
								Settings.getInt(
										Constants.APP_PREFERENCES_BUTTON_CHANGEPERSON_SIZE,
										-1),
								Settings.getInt(
										Constants.APP_PREFERENCES_BUTTON_CHANGEPERSON_SIZE,
										-1)));
				buttonWait
						.setLayoutParams(ControlsParams
								.coordinatesConfigureControls(
										buttonWait,
										Settings.getInt(
												Constants.APP_PREFERENCES_BUTTON_WAIT_X,
												-1),
										Settings.getInt(
												Constants.APP_PREFERENCES_BUTTON_WAIT_Y,
												-1),
										Settings.getInt(
												Constants.APP_PREFERENCES_BUTTON_WAIT_SIZE,
												-1),
										Settings.getInt(
												Constants.APP_PREFERENCES_BUTTON_WAIT_SIZE,
												-1)));
				buttonWeapon
						.setLayoutParams(ControlsParams
								.coordinatesConfigureControls(
										buttonWeapon,
										Settings.getInt(
												Constants.APP_PREFERENCES_BUTTON_WEAPON_X,
												-1),
										Settings.getInt(
												Constants.APP_PREFERENCES_BUTTON_WEAPON_Y,
												-1),
										Settings.getInt(
												Constants.APP_PREFERENCES_BUTTON_WEAPON_SIZE,
												-1),
										Settings.getInt(
												Constants.APP_PREFERENCES_BUTTON_WEAPON_SIZE,
												-1)));
				buttonDiary
						.setLayoutParams(ControlsParams
								.coordinatesConfigureControls(
										buttonDiary,
										Settings.getInt(
												Constants.APP_PREFERENCES_BUTTON_DIARY_X,
												-1),
										Settings.getInt(
												Constants.APP_PREFERENCES_BUTTON_DIARY_Y,
												-1),
										Settings.getInt(
												Constants.APP_PREFERENCES_BUTTON_DIARY_SIZE,
												-1),
										Settings.getInt(
												Constants.APP_PREFERENCES_BUTTON_DIARY_SIZE,
												-1)));

				buttonKeyboard
						.setLayoutParams(ControlsParams.coordinatesConfigureControls(
								buttonKeyboard,
								Settings.getInt(
										Constants.APP_PREFERENCES_BUTTON_KEYBOARD_X,
										-1),
								Settings.getInt(
										Constants.APP_PREFERENCES_BUTTON_KEYBOARD_Y,
										-1),
								Settings.getInt(
										Constants.APP_PREFERENCES_BUTTON_KEYBOARD_SIZE,
										-1),
								Settings.getInt(
										Constants.APP_PREFERENCES_BUTTON_KEYBOARD_SIZE,
										-1)));

				buttonBackspace
						.setLayoutParams(ControlsParams.coordinatesConfigureControls(
								buttonBackspace,
								Settings.getInt(
										Constants.APP_PREFERENCES_BUTTON_BACKSPACE_X,
										-1),
								Settings.getInt(
										Constants.APP_PREFERENCES_BUTTON_BACKSPACE_Y,
										-1),
								Settings.getInt(
										Constants.APP_PREFERENCES_BUTTON_BACKSPACE_SIZE,
										-1),
								Settings.getInt(
										Constants.APP_PREFERENCES_BUTTON_BACKSPACE_SIZE,
										-1)));

				buttonPause
						.setLayoutParams(ControlsParams
								.coordinatesConfigureControls(
										buttonPause,
										Settings.getInt(
												Constants.APP_PREFERENCES_BUTTON_PAUSE_X,
												-1),
										Settings.getInt(
												Constants.APP_PREFERENCES_BUTTON_PAUSE_Y,
												-1),
										Settings.getInt(
												Constants.APP_PREFERENCES_BUTTON_PAUSE_SIZE,
												-1),
										Settings.getInt(
												Constants.APP_PREFERENCES_BUTTON_PAUSE_SIZE,
												-1)));
				buttonLoad
						.setLayoutParams(ControlsParams
								.coordinatesConfigureControls(
										buttonLoad,
										Settings.getInt(
												Constants.APP_PREFERENCES_BUTTON_LOAD_X,
												-1),
										Settings.getInt(
												Constants.APP_PREFERENCES_BUTTON_LOAD_Y,
												-1),
										Settings.getInt(
												Constants.APP_PREFERENCES_BUTTON_LOAD_SIZE,
												-1),
										Settings.getInt(
												Constants.APP_PREFERENCES_BUTTON_LOAD_SIZE,
												-1)));
				buttonSave
						.setLayoutParams(ControlsParams
								.coordinatesConfigureControls(
										buttonSave,
										Settings.getInt(
												Constants.APP_PREFERENCES_BUTTON_SAVE_X,
												-1),
										Settings.getInt(
												Constants.APP_PREFERENCES_BUTTON_SAVE_Y,
												-1),
										Settings.getInt(
												Constants.APP_PREFERENCES_BUTTON_SAVE_SIZE,
												-1),
										Settings.getInt(
												Constants.APP_PREFERENCES_BUTTON_SAVE_SIZE,
												-1)));
				buttonInventory
						.setLayoutParams(ControlsParams
								.coordinatesConfigureControls(
										buttonInventory,
										Settings.getInt(
												Constants.APP_PREFERENCES_BUTTON_INVENTORY_X,
												-1),
										Settings.getInt(
												Constants.APP_PREFERENCES_BUTTON_INVENTORY_Y,
												-1),
										Settings.getInt(
												Constants.APP_PREFERENCES_BUTTON_INVENTORY_SIZE,
												-1),
										Settings.getInt(
												Constants.APP_PREFERENCES_BUTTON_INVENTORY_SIZE,
												-1)));
				buttonJump
						.setLayoutParams(ControlsParams
								.coordinatesConfigureControls(
										buttonJump,
										Settings.getInt(
												Constants.APP_PREFERENCES_BUTTON_JUMP_X,
												-1),
										Settings.getInt(
												Constants.APP_PREFERENCES_BUTTON_JUMP_Y,
												-1),
										Settings.getInt(
												Constants.APP_PREFERENCES_BUTTON_JUMP_SIZE,
												-1),
										Settings.getInt(
												Constants.APP_PREFERENCES_BUTTON_JUMP_SIZE,
												-1)));
				buttonFire
						.setLayoutParams(ControlsParams
								.coordinatesConfigureControls(
										buttonFire,
										Settings.getInt(
												Constants.APP_PREFERENCES_BUTTON_FIRE_X,
												-1),
										Settings.getInt(
												Constants.APP_PREFERENCES_BUTTON_FIRE_Y,
												-1),
										Settings.getInt(
												Constants.APP_PREFERENCES_BUTTON_FIRE_SIZE,
												-1),
										Settings.getInt(
												Constants.APP_PREFERENCES_BUTTON_FIRE_SIZE,
												-1)));
				buttonMagic
						.setLayoutParams(ControlsParams
								.coordinatesConfigureControls(
										buttonMagic,
										Settings.getInt(
												Constants.APP_PREFERENCES_BUTTON_MAGIC_X,
												-1),
										Settings.getInt(
												Constants.APP_PREFERENCES_BUTTON_MAGIC_Y,
												-1),
										Settings.getInt(
												Constants.APP_PREFERENCES_BUTTON_MAGIC_SIZE,
												-1),
										Settings.getInt(
												Constants.APP_PREFERENCES_BUTTON_MAGIC_SIZE,
												-1)));
				buttonUse
						.setLayoutParams(ControlsParams
								.coordinatesConfigureControls(
										buttonUse,
										Settings.getInt(
												Constants.APP_PREFERENCES_BUTTON_USE_X,
												-1),
										Settings.getInt(
												Constants.APP_PREFERENCES_BUTTON_USE_Y,
												-1),
										Settings.getInt(
												Constants.APP_PREFERENCES_BUTTON_USE_SIZE,
												-1),
										Settings.getInt(
												Constants.APP_PREFERENCES_BUTTON_USE_SIZE,
												-1)));
				buttonCrouch
						.setLayoutParams(ControlsParams
								.coordinatesConfigureControls(
										buttonCrouch,
										Settings.getInt(
												Constants.APP_PREFERENCES_BUTTON_CROUCH_X,
												-1),
										Settings.getInt(
												Constants.APP_PREFERENCES_BUTTON_CROUCH_Y,
												-1),
										Settings.getInt(
												Constants.APP_PREFERENCES_BUTTON_CROUCH_SIZE,
												-1),
										Settings.getInt(
												Constants.APP_PREFERENCES_BUTTON_CROUCH_SIZE,
												-1)));

			}
		}

	}
}
