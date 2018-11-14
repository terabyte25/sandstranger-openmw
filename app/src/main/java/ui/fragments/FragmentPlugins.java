package ui.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.afollestad.materialdialogs.folderselector.FileChooserDialog;
import com.afollestad.materialdialogs.folderselector.FolderChooserDialog;
import com.libopenmw.openmw.R;
import com.mobeta.android.dslv.DragSortListView;

import java.io.File;
import java.io.IOException;

import constants.Constants;
import plugins.PluginReader;
import plugins.PluginsAdapter;
import plugins.PluginsStorage;
import prefs.PreferencesHelper;
import ui.dialog.MaterialDialogInterface;
import ui.dialog.MaterialDialogManager;
import ui.game.GameState;

public class FragmentPlugins extends Fragment implements FileChooserDialog.FileCallback,FolderChooserDialog.FolderCallback{

    private boolean isDirMode =false;
    private PluginsAdapter adapter;
    private PluginsStorage pluginsStorage;
    protected MaterialDialogManager materialDialogManager;
    private static final int REQUEST_PATH = 12;
    private static FragmentPlugins Instance = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Instance = this;
        materialDialogManager = new MaterialDialogManager(FragmentPlugins.this.getActivity());
        pluginsStorage = new PluginsStorage(this.getActivity());
        View rootView = inflater.inflate(R.layout.listview, container, false);
        PreferencesHelper.getPrefValues(this.getActivity());
        setupViews(rootView);
        return rootView;
    }

    public void savePluginsDataToDisk() {
        if (pluginsStorage != null && pluginsStorage.getPluginsList() != null) {
            try {
                pluginsStorage.saveJson("");
                pluginsStorage.savePlugins();
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (!GameState.getGameState()) {
            savePluginsDataToDisk();
        }
        Instance = null;
    }

    public static FragmentPlugins getInstance() {
        return Instance;
    }

    public PluginsStorage getPluginsStorage() {
        return pluginsStorage;
    }

    private void setupViews(View rootView) {
        DragSortListView listView = (DragSortListView) rootView
                .findViewById(R.id.listView1);
        adapter = new PluginsAdapter(FragmentPlugins.this);
        listView.setAdapter(adapter);
        listView.setDropListener(onDrop);
    }

    private DragSortListView.DropListener onDrop = new DragSortListView.DropListener() {
        @Override
        public void drop(int from, int to) {
            if (pluginsStorage != null) {
                pluginsStorage.replacePlugins(from, to);
                reloadAdapter();
            }
        }
    };


    public void disableMods() {
        showModDialog(false, "Do you want to disable all mods ?");
    }

    public void enableMods() {
        showModDialog(true, "Do you want to enable all mods ?");
    }

    private void showModDialog(final boolean isModEnable, String message) {
        MaterialDialogInterface materialDialogInterface = new MaterialDialogInterface() {
            @Override
            public void onPositiveButtonPressed() {
                changeModsStatus(isModEnable);
            }

            @Override
            public void onNegativeButtonPressed() {
                reloadAdapter();
            }
        };
        materialDialogManager.showDialog("", message, "Cancel", "OK", materialDialogInterface);
    }

    public void importMods() {
        try {
            isDirMode =false;
            getFileOrFolder();
        } catch (Exception e) {

        }
    }

    public void exportMods() {
        try {
            isDirMode = true;
            getFileOrFolder();
        } catch (Exception e) {
        }
    }


    public void showDependenciesDialog(final int pos) {
        String dependencies = "";
        try {
            dependencies = PluginReader.read(Constants.APPLICATION_DATA_STORAGE_PATH + "/"
                    + pluginsStorage.getPluginsList().get(pos).name);
        } catch (IOException e) {
            e.printStackTrace();
        }
        materialDialogManager.showMessageDialogBox("Dependencies", dependencies, "Close");
    }


    private void changeModsStatus(boolean needEnableMods) {
        if (pluginsStorage != null) {
            pluginsStorage.updatePluginsStatus(needEnableMods);
        }
        reloadAdapter();
    }

    private void reloadAdapter() {
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    public void getFileOrFolder() {
        if (!isDirMode){
            buildFileChooserDialog();
        }
        else {
            buildFolderChooserDialog();
        }
    }

    private void buildFileChooserDialog (){
        new FileChooserDialog.Builder(FragmentPlugins.this.getActivity())
                .extensionsFilter(".json") // Optional extension filter, will override mimeType()
                .tag("optional-identifier")
                .goUpLabel("Up") // custom go up label, default label is "..."
                .show(FragmentPlugins.this.getActivity()); // an AppCompatActivity which implements FileCallback
    }

    private void buildFolderChooserDialog(){
        new FolderChooserDialog.Builder(this.getActivity())
                .chooseButton(R.string.md_choose_label)  // changes label of the choose button
                .tag("optional-identifier")
                .goUpLabel("Up") // custom go up label, default label is "..."
                .show(this.getActivity());
    }

    @Override
    public void onFileSelection(@NonNull FileChooserDialog dialog, @NonNull File file) {
        exportImportMods(file.getAbsolutePath());
    }

    @Override
    public void onFileChooserDismissed(@NonNull FileChooserDialog dialog) {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_PATH) {
            if (resultCode == Activity.RESULT_OK) {
            }
        }
        super.onActivityResult(requestCode, resultCode, data);

    }


    private void exportImportMods(String curPath) {

        if (!isDirMode) {
            if (curPath.endsWith(".json"))
                try {
                    pluginsStorage.loadPlugins(curPath);
                    reloadAdapter();
                } catch (Exception e) {

                }
        } else {
            try {
                pluginsStorage.saveJson(curPath + "/files.json");
                Toast.makeText(
                        FragmentPlugins.this.getActivity().getApplicationContext(),
                        "file exported to " + curPath + "/files.json", Toast.LENGTH_LONG).show();
            } catch (Exception e) {

            }

        }
    }

    @Override
    public void onFolderSelection(@NonNull FolderChooserDialog dialog, @NonNull File folder) {
        exportImportMods(folder.getAbsolutePath());
    }

    @Override
    public void onFolderChooserDismissed(@NonNull FolderChooserDialog dialog) {

    }
}
