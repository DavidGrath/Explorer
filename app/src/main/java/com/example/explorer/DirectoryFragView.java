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
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.example.explorer.Constants.CURRENT_DIRECTORY;
import static com.example.explorer.Constants.LIST_OR_GRID;

public class DirectoryFragView extends Fragment implements FileViewAdapter.OnItemLongClickListener, FileViewAdapter.OnItemClickListener, ActionMode.Callback {

    private DirectoryFragView directoryFragView = this;
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
    private int requestCode = 100;

    public DirectoryFragView() {
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
                    actionMode = mainActivity.startActionMode(directoryFragView);
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
            String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
            requestPermissions(permissions, requestCode);
        } else {
            editor.putString(CURRENT_DIRECTORY, Environment.getExternalStorageDirectory().getAbsolutePath()).commit();
        }

        //File f = Environment.getExternalStorageDirectory();
        fileList = file.isDirectory()? Arrays.asList(file.listFiles()): Arrays.asList(file);//getContext().getFilesDir().listFiles());//new File("storage").listFiles());
        backStack = new ArrayList<>();
        backStack.add(file);
        if(recyclerView.getAdapter() == null) {
            fileViewAdapter = new FileViewAdapter(fileList);
        } else {
            fileViewAdapter = (FileViewAdapter) recyclerView.getAdapter();
        }
        fileViewAdapter.setOnItemClickListener(this);
        fileViewAdapter.setOnItemLongClickListener(this);
        toggleLayoutManager(preferences.getBoolean(LIST_OR_GRID, false));
        itemTouchHelper.attachToRecyclerView(recyclerView);
        recyclerView.setAdapter(fileViewAdapter);
        return  v;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == this.requestCode) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                editor.putString(CURRENT_DIRECTORY, Environment.getExternalStorageDirectory().getAbsolutePath()).commit();
            } else {
                Toast.makeText(context, "Until you grant the permission, I cannot list the files", Toast.LENGTH_SHORT)
                        .show();
            }
        }
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
                Toast.makeText(context, "Copy Clicked!", Toast.LENGTH_SHORT).show();
                copyMethod();
                return true;
            case R.id.cab_main_cut:
                Toast.makeText(context, "Cut Clicked!", Toast.LENGTH_SHORT).show();
                cutMethod();
                return true;
        }
        return false;
    }

    private void copyMethod() {
        destDir = new File( Environment.getExternalStorageDirectory().toString() + "/Newolder");
        FileHelper.copyFiles((ArrayList<File>) selectedFiles, destDir);
    }

    private void cutMethod() {
        destDir = new File(Environment.getExternalStorageDirectory().toString());
        FileHelper.cutFiles((ArrayList<File>) selectedFiles, destDir);
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
                    refreshRecyclerAdapter(fileList);
                    /*DirectoryFragView fragView = new DirectoryFragView();
                     FragmentTransaction transaction = directoryFragView.getFragmentManager().beginTransaction();
                     mainActivity.fragmentList.add(fragView);
                     mainActivity.fragIndex++;
                     transaction.add(mainActivity.fragmentList.get(mainActivity.fragIndex), file.getName()).replace(R.id.frame_layout, directoryFragView).addToBackStack(null).commit();*/
                }
            } else {
                Intent intent = new Intent(Intent.ACTION_VIEW);
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

    /*public void onActivityBackPressed() {
        file = file.getParentFile();
        System.out.println(file.toString());
        fileList = Arrays.asList(file.listFiles());
        DirectoryFragView fragView = fragmentList;
        recyclerView.setAdapter(adapter);
        fragView.setGridView(recyclerView);
        fragmentList.getFragmentManager().beginTransaction().replace(R.id.frame_layout, fragView).commit();
    }*/

}
