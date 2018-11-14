package ui.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.folderselector.FolderChooserDialog;
import com.libopenmw.openmw.R;
import com.melnykov.fab.FloatingActionButton;

import java.io.File;

import constants.Constants;
import file.ConfigsFileStorageHelper;
import permission.PermissionHelper;
import plugins.bsa.BsaUtils;
import prefs.PreferencesHelper;
import ui.fragments.FragmentPlugins;
import ui.fragments.FragmentSettings;
import ui.game.GameState;
import ui.screen.ScreenScaler;

public class MainActivity extends AppCompatActivity implements FolderChooserDialog.FolderCallback {

    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    public TextView path;
    public Button browseButton;
    private Menu menu;
    private boolean isSettingsEnabled = true;
    private static final int REQUEST_PATH = 1;
    private SharedPreferences Settings;
    private TextListener listener;

    @Override
    public void onFolderSelection(@NonNull FolderChooserDialog dialog, @NonNull File folder) {
        String curDir = folder.getAbsolutePath();

        switch (editTextMode) {
            case DATA_PATH:
                Constants.APPLICATION_DATA_STORAGE_PATH = curDir;
                break;
        }
        setTexWatcher();
        path.setVisibility(EditText.VISIBLE);
        browseButton.setVisibility(Button.VISIBLE);
        path.setText(curDir);

    }

    @Override
    public void onFolderChooserDismissed(@NonNull FolderChooserDialog dialog) {
        setTexWatcher();
        path.setVisibility(EditText.VISIBLE);
        browseButton.setVisibility(Button.VISIBLE);
    }

    private enum TEXT_MODE {DATA_PATH, COMMAND_LINE}
    private static TEXT_MODE editTextMode;
    private ConfigsFileStorageHelper configsFileStorageHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        GameState.setGameState(false);
        super.onCreate(savedInstanceState);
        PermissionHelper.getWriteExternalStoragePermission(MainActivity.this);
        isSettingsEnabled = true;
        setContentView(R.layout.main);
        PreferencesHelper.getPrefValues(this);
        Settings = this.getSharedPreferences(
                Constants.APP_PREFERENCES, Context.MODE_PRIVATE);
        configsFileStorageHelper= new ConfigsFileStorageHelper(this,Settings);
        configsFileStorageHelper.checkAppFirstTimeRun();
        LinearLayout layout = (LinearLayout) findViewById(R.id.toolbarLayout);
        layout.setVisibility(LinearLayout.VISIBLE);
        path = (TextView) findViewById(R.id.path);
        browseButton = (Button) findViewById(R.id.buttonBrowse);

        disableToolBarViews();
        browseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFolder();
            }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_frame, new FragmentSettings()).commit();

        initializeNavigationView(toolbar);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                GameState.setGameState(true);
                if (FragmentPlugins.getInstance()!=null){
                    FragmentPlugins.getInstance().savePluginsDataToDisk();
                }
                startGame();
            }

        });

    }

    private void initializeNavigationView(Toolbar toolbar) {
        navigationView = (NavigationView) findViewById(R.id.navigation_drawer);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                if (menuItem.isChecked()) menuItem.setChecked(false);
                else menuItem.setChecked(true);

                drawerLayout.closeDrawers();

                switch (menuItem.getItemId()) {

                    case R.id.start_game:
                        startGame();
                        return true;

                    case R.id.plugins:
                        showOverflowMenu(true);
                        isSettingsEnabled = false;
                        disableToolBarViews();
                        MainActivity.this.getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new FragmentPlugins()).commit();
                        return true;
                    case R.id.settings:
                        disableToolBarViews();
                        showOverflowMenu(true);
                        isSettingsEnabled = true;
                        MainActivity.this.getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new FragmentSettings()).commit();

                        return true;
/*                    case R.id.textureDecoder:
                        showOverflowMenu(false);
                        disableToolBarViews();
                        MainActivity.this.getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new DDSDecoderFragment()).commit();
                        return true;*/

                    default:

                        return true;

                }
            }
        });

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.openDrawer, R.string.closeDrawer) {

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };

        drawerLayout.setDrawerListener(actionBarDrawerToggle);

        actionBarDrawerToggle.syncState();

    }

    public void getFolder() {
        new FolderChooserDialog.Builder(this)
                .chooseButton(R.string.md_choose_label)  // changes label of the choose button
                .tag("optional-identifier")
                .goUpLabel("Up") // custom go up label, default label is "..."
                .show(this);
    }

    private void startGame() {

        Intent intent = new Intent(MainActivity.this,
                GameActivity.class);
        finish();

        MainActivity.this.startActivity(intent);
    }

    private void setTexWatcher() {
        switch (editTextMode) {
            case COMMAND_LINE:
                removeTextlistener();
                listener = new TextListener(this
                        , "", "", Constants.COMMAND_LINE,
                        Constants.commandLineData, Settings, "commandLine");
                path.addTextChangedListener(listener);
                break;
            case DATA_PATH:
                removeTextlistener();
                listener = new TextListener(this,
                        "", "data", Constants.DATA_PATH, Constants.APPLICATION_DATA_STORAGE_PATH, Settings,
                        "data");
                path.addTextChangedListener(listener);
                break;
            default:
                break;
        }
    }


    public void showOverflowMenu(boolean showMenu) {
        if (menu == null)
            return;
        menu.setGroupVisible(R.id.main_menu_group, showMenu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        this.menu = menu;
        menu.clear();
        MenuInflater inflater = getMenuInflater();
        if (isSettingsEnabled)
            inflater.inflate(R.menu.menu_settings, menu);
        else
            inflater.inflate(R.menu.menu_plugins, menu);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (!isSettingsEnabled)
            switch (id) {
                case R.id.action_enable:
                    if (FragmentPlugins.getInstance()!=null) {
                        FragmentPlugins.getInstance().enableMods();
                    }
                    break;
                case R.id.action_disable:
                    if (FragmentPlugins.getInstance()!=null) {
                        FragmentPlugins.getInstance().disableMods();
                    }
                    break;
                case R.id.action_importMods:
                    if (FragmentPlugins.getInstance()!=null) {
                        FragmentPlugins.getInstance().importMods();
                    }
                    break;
                case R.id.action_export:
                    if (FragmentPlugins.getInstance()!=null) {
                        FragmentPlugins.getInstance().exportMods();
                    }
                    break;
                case R.id.action_enableBsa:
                    BsaUtils.setSaveAllBsaFilesValue(MainActivity.this,true);
                    break;
                case R.id.action_disableBsa:
                    BsaUtils.setSaveAllBsaFilesValue(MainActivity.this,false);
                    break;
                default:
                    break;
            }
        else
            switch (id) {
                case R.id.action_command_line:
                    editTextMode = TEXT_MODE.COMMAND_LINE;
                    enableToolbarViews();
                    browseButton.setVisibility(Button.GONE);
                    path.setText(Constants.commandLineData);
                    break;
                case R.id.action_browse_data_path:
                    editTextMode = TEXT_MODE.DATA_PATH;
                    enableToolbarViews();
                    path.setText(Constants.APPLICATION_DATA_STORAGE_PATH);
                    break;
                case R.id.action_copy_files:
                    configsFileStorageHelper.copyFiles();
                    break;

                case R.id.action_reset_screen_controls:
                    resetScreenControls();
                    break;
                case R.id.action_show_screen_controls:
                    startControlsActivity();
                    break;

                default:
                    break;


            }


        return super.onOptionsItemSelected(item);
    }

    private void resetScreenControls() {
        PreferencesHelper.setPreferences(
                Constants.APP_PREFERENCES_RESET_CONTROLS, 1,
                this);
        Toast toast = Toast.makeText(this.
                        getApplicationContext(),
                "Reset on-screen controls", Toast.LENGTH_LONG);
        toast.show();
    }

    private void startControlsActivity() {
        Intent intent = new Intent(this,
                ConfigureControls.class);
        this.startActivity(intent);

    }

    private void enableToolbarViews() {
        path.setVisibility(EditText.VISIBLE);
        browseButton.setVisibility(Button.VISIBLE);
        setTexWatcher();
    }

    private void disableToolBarViews() {
        browseButton.setVisibility(Button.GONE);
        path.setVisibility(EditText.GONE);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        ScreenScaler.textScaler(path, 3f);
        ScreenScaler.textScaler(browseButton, 4.8f);
    }

    private void removeTextlistener() {
        if (listener != null)
            path.removeTextChangedListener(listener);


    }
}



