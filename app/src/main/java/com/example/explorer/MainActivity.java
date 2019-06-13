package com.example.explorer;

import android.app.SearchManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.example.explorer.Constants.CURRENT_DIRECTORY;
import static com.example.explorer.Constants.LIST_OR_GRID;

public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    public List<Fragment> fragmentList;
    //public int fragIndex;
    private HomeFragment homeFragment;
    private Fragment currentFrag;
    private android.support.v7.widget.Toolbar toolbar;
    private Context context;
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    private boolean listOrGrid;
    SearchView searchView = null;
    private int requestCode = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;
        fragmentList = new ArrayList<>();
        homeFragment = new HomeFragment();
        fragmentList.add(homeFragment);
        fragmentList.add(currentFrag);
        //fragIndex = fragmentList.size() - 1;
        ImageView imageView = findViewById(R.id.imageView);
        toolbar = findViewById(R.id.toolbar);
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        editor = sharedPreferences.edit();
        listOrGrid = sharedPreferences.getBoolean(LIST_OR_GRID, false);
        setSupportActionBar(toolbar);
        fragmentTransaction.add(R.id.frame_layout, currentFrag).commit();
    }


    public void performFragTransaction(Fragment fragment) {
        fragmentManager.beginTransaction().replace(R.id.frame_layout, fragment).commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.all_options, menu);
        menu.findItem(R.id.toggle_grid_list).setTitle(listOrGrid?"Grid View":"List View");

        final MenuItem searchItem = menu.findItem(R.id.app_bar_search);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) searchItem.getActionView();
        //searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setOnQueryTextListener(this);
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                ((DirectoryFragView) currentFrag).refreshRecyclerAdapter(Arrays.asList(new File(sharedPreferences.getString(CURRENT_DIRECTORY, Environment.getExternalStorageDirectory().toString())).listFiles()));
                searchItem.collapseActionView();
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.undo_menu_item:
                Toast.makeText(context, "Undo Selected", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.redo_menu_item:
                Toast.makeText(context, "Redo Selected", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.toggle_grid_list:
                listOrGrid = !listOrGrid;
                editor.putBoolean(LIST_OR_GRID, listOrGrid).commit();
                ((DirectoryFragView) currentFrag).toggleLayoutManager(listOrGrid);
                menuItem.setTitle(listOrGrid ? "Grid View" : "List View");
            case R.id.app_bar_search:
                return false;
            default:
                return false;
        }
    }


    @Override
    public boolean onQueryTextSubmit(String query) {
        Toast.makeText(context, query, Toast.LENGTH_SHORT).show();
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        ((DirectoryFragView) currentFrag).performSearch(newText);
        return true;
    }

    @Override
    public void onBackPressed() {
        currentFrag = (DirectoryFragView) getSupportFragmentManager().findFragmentById(R.id.frame_layout);
        //The List of files to be used as a 'backstack' for the Fragment, soon to be a FragmentList
        List<File> backStack = ((DirectoryFragView)currentFrag).getBackStack();
        if(backStack.size() > 1) {
            File file = backStack.get(backStack.size() - 2);
            editor.putString(CURRENT_DIRECTORY, file.toString()).commit();
            ((DirectoryFragView) currentFrag).setFile(file);
            ((DirectoryFragView) currentFrag).refreshRecyclerAdapter(Arrays.asList(file.listFiles()));
        } else {
            super.onBackPressed();
        }
        backStack.remove(backStack.size() - 1);
        /*currentFrag.getFragmentManager().beginTransaction().replace(R.id.frame_layout, fragmentList.get(fragIndex)).remove(currentFrag).commit();//remove(currentFrag).commit();
        fragmentList.remove(fragIndex);
        fragIndex--;
        Toast.makeText(context, "DirectoryFragViewSize: " + Integer.toString(fragmentList.size()) + "\n FragIndex: " + Integer.toString(fragIndex), Toast.LENGTH_SHORT).show();
        return fragIndex;*/
        //fragmentList.onActivityBackPressed();
    }
}
