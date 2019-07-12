package com.example.explorer;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.example.explorer.Constants.CURRENT_DIRECTORY;
import static com.example.explorer.Constants.LIST_OR_GRID;

public class FileBrowserFragment extends Fragment implements FileViewAdapter.OnItemLongClickListener, FileViewAdapter.OnItemClickListener, ActionMode.Callback {

    private FileBrowserFragment fileBrowserFragment = this;
    private RecyclerView recyclerView;
    private List<File> fileList, selectedFiles, searchList, backStack;
    private File file, destDir;
    public FileViewAdapter fileViewAdapter;
    private LinearLayoutManager linearLayoutManager;
    private GridLayoutManager gridLayoutManager;
    Context context;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    MainActivity mainActivity;
    ActionMode actionMode;
    String actionModeTitle;
    int actionModeCount;
    boolean actionModeFlag;
    ItemTouchHelper.SimpleCallback simpleCallback;
    private final int REQUESTCODE = 100;
    private final int BT_REQ_CODE = 50;

    public FileBrowserFragment() {
    }

    public void setFile(File file) {
        this.file = file;
    }

    public List<File> getBackStack() {
        return backStack;
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container , Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.directory_frag_view, container, false);
        recyclerView =  v.findViewById(R.id.grid_view);
        context = getContext();
        actionModeFlag = false;
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        mainActivity = (MainActivity) context;
        selectedFiles = new ArrayList<>();
        gridLayoutManager = new GridLayoutManager(context, 3);
        linearLayoutManager = new LinearLayoutManager(context);

        simpleCallback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.DOWN | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT | ItemTouchHelper.UP, 0) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                final int from = viewHolder.getAdapterPosition();
                final int to = target.getAdapterPosition();

                File fromFile = fileList.get(from);
                File toFile = fileList.get(to);
                //FileHelper.copyFile(fromFile, toFile);
                //int backColor = target.itemView;
                if(!actionModeFlag) {
                    actionMode = mainActivity.startActionMode(fileBrowserFragment);
                    fileViewAdapter.toggleChecked(from);
                }
                //fileViewAdapter.toggleChecked(from);
                target.itemView.setSelected(true);
                return true;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);

        //recyclerView.setEmptyView(LayoutInflater.from(context).inflate(R.layout.empty_directory_view, null));
        editor = preferences.edit();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && mainActivity.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUESTCODE);
        } else {
            editor.putString(CURRENT_DIRECTORY, Environment.getExternalStorageDirectory().getAbsolutePath()).commit();

            //File f = Environment.getExternalStorageDirectory();
            fileList = file.isDirectory() ? Arrays.asList(file.listFiles()) : Arrays.asList(file);//getContext().getFilesDir().listFiles());//new File("storage").listFiles());
            backStack = new ArrayList<>();
            backStack.add(file);
            ((TextView)v.findViewById(R.id.current_directory)).setText(file.getAbsolutePath());
            fileViewAdapter = recyclerView.getAdapter() == null ? new FileViewAdapter(fileList) : (FileViewAdapter) recyclerView.getAdapter();
            fileViewAdapter.setOnItemClickListener(this);
            fileViewAdapter.setOnItemLongClickListener(this);
            toggleLayoutManager(preferences.getBoolean(LIST_OR_GRID, false));
            itemTouchHelper.attachToRecyclerView(recyclerView);
            recyclerView.setAdapter(fileViewAdapter);
        }
        return  v;
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == this.REQUESTCODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                editor.putString(CURRENT_DIRECTORY, Environment.getExternalStorageDirectory().getAbsolutePath()).commit();

                fileList = file.isDirectory() ? Arrays.asList(file.listFiles()) : Arrays.asList(file);//getContext().getFilesDir().listFiles());//new File("storage").listFiles());
                backStack = new ArrayList<>();
                backStack.add(file);
                fileViewAdapter = recyclerView.getAdapter() == null ? new FileViewAdapter(fileList) : (FileViewAdapter) recyclerView.getAdapter();
                fileViewAdapter.setOnItemClickListener(this);
                fileViewAdapter.setOnItemLongClickListener(this);
                toggleLayoutManager(preferences.getBoolean(LIST_OR_GRID, false));
                recyclerView.setAdapter(fileViewAdapter);
            } else {
                Toast.makeText(context, "Until you grant the permission, I cannot list the files", Toast.LENGTH_SHORT)
                        .show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public void toggleLayoutManager(boolean listOrGrid) {
        if(listOrGrid) {
            recyclerView.setLayoutManager(linearLayoutManager);
            simpleCallback.setDefaultDragDirs(ItemTouchHelper.UP | ItemTouchHelper.DOWN);
        } else {
            recyclerView.setLayoutManager(gridLayoutManager);
            simpleCallback.setDefaultDragDirs(ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        }
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        MenuInflater inflater = mode.getMenuInflater();
        inflater.inflate(R.menu.cab_main, menu);
        actionModeFlag = true;
        Toast.makeText(context, "Action Mode Created", Toast.LENGTH_SHORT).show();
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        switch (item.getItemId()) {
            case R.id.cab_main_copy:
                copyMethod();
                return true;
            case R.id.cab_main_cut:
                cutMethod();
                return true;
            case R.id.cab_main_share:
                shareMethod();
                return true;
            case R.id.cab_main_delete:
                deleteMethod();
        }
        return false;
    }

    private void copyMethod() {
        destDir = new File( preferences.getString(CURRENT_DIRECTORY, Environment.getExternalStorageDirectory().getAbsolutePath()) + "groot.svg");
        FileHelper.copyFiles((ArrayList<File>) selectedFiles, destDir);
    }

    private void cutMethod() {
        destDir = new File(Environment.getExternalStorageDirectory().toString());
        FileHelper.cutFiles((ArrayList<File>) selectedFiles, destDir);
    }

    private void shareMethod() {
        for(File f: fileList) {
            ConnectionHelper.activity = getActivity();
            ConnectionHelper.share(f, BT_REQ_CODE);
        }
    }

    private void deleteMethod() {
        FileHelper.deleteFiles((ArrayList<File>) selectedFiles);
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        actionModeFlag = false;
        selectedFiles.clear();
        refreshRecyclerAdapter(Arrays.asList(new File(preferences.getString(CURRENT_DIRECTORY, Environment.getRootDirectory().toString())).listFiles()));
    }


    public void performSearch(String query) {
        List<File> currentFiles, filterFiles;
        File file = new File(preferences.getString(CURRENT_DIRECTORY, Environment.getRootDirectory().toString()));
        searchList = new ArrayList<>();
        currentFiles = listFilesOut(file);
        filterFiles = new ArrayList<>();
        //filterFiles.addAll(fileList);
        for (File f: currentFiles) {
            if(f.getName().toLowerCase().contains(query)) filterFiles.add(f);
        }
        refreshRecyclerAdapter(filterFiles);
        fileViewAdapter.highlightSearchText(query);
        recyclerView.setAdapter(fileViewAdapter);
    }

    /*
     * Recursively lists all the files in the given File object after
     * verifying the object is a directory
     */
    private List<File> listFilesOut(File f) {
                                                 //This file object (lost+found) causes crashes, not sure why
        if(f.isDirectory() && !f.getName().equals("lost+found")) {
            File[] fileArray = f.listFiles();
            for (int i = 0; i < fileArray.length; i++) {
                if (fileArray[i].isDirectory()) {
                    searchList.add(fileArray[i]);
                    listFilesOut(fileArray[i]);
                } else {
                    searchList.add(fileArray[i]);
                }
            }
        } else {
            searchList.add(f);
        }

        return searchList;
    }

    @Override
    public void onItemLongSelected(int position, View view, File file) {
        if (!actionModeFlag) {
            actionMode = mainActivity.startActionMode(this);
        }
        fileViewAdapter.toggleChecked(position);
        if(fileViewAdapter.getChecked(position)) {
            view.findViewById(R.id.check_box).setVisibility(View.VISIBLE);
            selectedFiles.add(file);
        } else {
            view.findViewById(R.id.check_box).setVisibility(View.INVISIBLE);
            selectedFiles.remove(file);
        }

        actionModeCount = fileViewAdapter.getCheckedCount();
        actionModeTitle = actionModeCount == 1 ? "1 item checked" : Integer.toString(actionModeCount) + " items checked";
        actionMode.setTitle(actionModeTitle);
        if(actionModeCount == 0)
            actionMode.finish();
    }

    @Override
    public void onItemSelected(int position, View view, File file) {
        if(actionModeFlag) {
            fileViewAdapter.toggleChecked(position);
            if(fileViewAdapter.getChecked(position)) {
                view.findViewById(R.id.check_box).setVisibility(View.VISIBLE);
                selectedFiles.add(file);
            } else {
                view.findViewById(R.id.check_box).setVisibility(View.INVISIBLE);
                selectedFiles.remove(file);
            }
            actionModeCount = fileViewAdapter.getCheckedCount();
            actionModeTitle = actionModeCount == 1? "1 item checked": Integer.toString(actionModeCount) + " items checked";
            actionMode.setTitle(actionModeTitle);
            if (actionModeCount == 0)
                actionMode.finish();
        } else {
            //Toast.makeText(getContext(), "It's alive!", Toast.LENGTH_SHORT).show();
            this.file = file;
            editor.putString(CURRENT_DIRECTORY, file.toString()).commit();
            if ((file.isDirectory())) {
                if (file.getAbsoluteFile().listFiles() == null) {
                    recyclerView.setAdapter(null);
                    Toast.makeText(context, "No Files In here!", Toast.LENGTH_SHORT).show();
                } else {
                    backStack.add(file);
                    fileList = Arrays.asList(file.getAbsoluteFile().listFiles());
                    ((TextView) this.getActivity().findViewById(R.id.current_directory)).setText(file.getAbsolutePath());
                    refreshRecyclerAdapter(fileList);
                }
            } else {
                Intent intent = new Intent(Intent.ACTION_ALL_APPS);
                intent.setData(Uri.fromFile(file));

                if (intent.resolveActivity(context.getPackageManager()) != null)
                    startActivity(Intent.createChooser(intent, "Choose an App: "));
            }
        }
    }

    public void refreshRecyclerAdapter(List<File> fileList) {
        fileViewAdapter.setFileList(fileList);
        fileViewAdapter.setOnItemClickListener(this);
        fileViewAdapter.setOnItemLongClickListener(this);
        fileViewAdapter.notifyDataSetChanged();
    }
}
