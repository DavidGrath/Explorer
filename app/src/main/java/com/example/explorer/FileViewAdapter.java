package com.example.explorer;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static com.example.explorer.Constants.CURRENT_DIRECTORY;


public class FileViewAdapter extends RecyclerView.Adapter<FileViewAdapter.ViewHolder> /*implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener*/ {

    private  final int EMPTY_VIEW = 10000;
    private OnItemClickListener onItemClickListener;
    private OnItemLongClickListener onItemLongClickListener;
    LayoutInflater inflater;
    private List<File> fileList;
    Context context;
    SharedPreferences sharedPreferences;
    ViewHolder holder;
    SparseBooleanArray checkBoxes;
    int checkedCount;

    public FileViewAdapter(List<File> fileList) {
        this.fileList = fileList;
    }

    public void setFileList(List<File> fileList) {
        this.fileList = fileList;
    }

    @NonNull
    @Override
    public FileViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ViewHolder viewHolder;
        View view;

        context = parent.getContext();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        //if(view == null) {
            inflater = LayoutInflater.from(context);
            if(viewType == EMPTY_VIEW) {
                view = inflater.inflate(R.layout.empty_directory_view, null);
            } else {
                view = inflater.inflate(R.layout.file_view_grid, null);
            }
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
        //} else {
            //viewHolder = (ViewHolder) view.getTag();
        //}
        holder = viewHolder;
        checkBoxes = new SparseBooleanArray(fileList.size());
        for (int i = 0; i < checkBoxes.size(); i++) {
            checkBoxes.put(i, false);
        }
        checkedCount = 0;
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final FileViewAdapter.ViewHolder holder, final int position) {
        final File file = fileList.get(position);
        holder.setFile(file);
        holder.setCheckBoxVisiblity(checkBoxes.get(holder.getAdapterPosition(), false));
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onItemClickListener != null) {
                    onItemClickListener.onItemSelected(holder.getAdapterPosition(), v, file);
                }
            }
        });

        holder.view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (onItemLongClickListener != null) {
                    onItemLongClickListener.onItemLongSelected(holder.getAdapterPosition(), v, file);
                }
                return true;
            }
        });
    }

    public void highlightSearchText(String query) {
        for (File f: fileList) {

        }
    }

    public void toggleChecked(int position) {
        boolean value = checkBoxes.get(position);
        checkBoxes.put(position, !value);
        checkedCount = !value ? checkedCount + 1: checkedCount - 1;
    }

    public boolean getChecked(int position) {
        return checkBoxes.get(position);
    }

    public int getCheckedCount() {
        //This was slightly buggy, I believe it's a threading issue
        /*int count = 0;
        for(int i = 0; i < checkBoxes.size(); i++) {
            Log.d("Check at File", Boolean.toString(checkBoxes.get(i)));
            if(checkBoxes.get(i)) count++;
        }
        return count;*/
        return checkedCount;
    }

    @Override
    public int getItemCount() {
        return fileList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if(fileList.size() == 0) {
            return EMPTY_VIEW;
        }
        return super.getItemViewType(position);
    }

    public interface OnItemClickListener {
        void onItemSelected(int position, View view, File file);
    }

    public interface OnItemLongClickListener {
        void onItemLongSelected(int position, View view, File file);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
        this.onItemLongClickListener = onItemLongClickListener;
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        private final ImageView imageView;
        private final TextView textView;
        private final ImageView checkBox;
        private View view;

        private ViewHolder(View v) {
            super(v);
            view = v;
            imageView = v.findViewById(R.id.imageView);
            textView = v.findViewById(R.id.textView);
            checkBox = v.findViewById(R.id.check_box);
        }

        void setCheckBoxVisiblity(boolean value) {
            if(value) {
                checkBox.setVisibility(View.VISIBLE);
            } else {
                checkBox.setVisibility(View.INVISIBLE);
            }
        }

        void setFile(File file) {
            //if(position == 0) file = file.getParentFile();
            //SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
            //SharedPreferences.Editor editor = preferences.edit();
            //editor.putString(CURRENT_DIRECTORY, file.toString()).commit();
            if ((file.isDirectory())) {
                imageView.setImageResource(R.drawable.folder);
            } else {
                int resID = MimeResolver.getResourceID(file, context);
                if (resID == 0) {
                    imageView.setImageResource(R.drawable.file);
                } else if(resID == -1) {
                    imageView.setImageDrawable(MimeResolver.icon);
                } else {
                    imageView.setImageResource(resID);
                }
            }
            //if(position == 0) {
            //textView.setText("...");
            //} else {
            textView.setText(file.getName());
            //}
        }
    }
}
