package com.example.explorer;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.text.format.Formatter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.File;

public class HomeFragment extends Fragment implements View.OnClickListener {
    Context context;
    MainActivity mainActivity;
    private String externalSD;
    private final int MAX = 10000;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onClick(View v) {
        FileBrowserFragment fileBrowserFragment = new FileBrowserFragment();
        switch (v.getId()) {
            case R.id.internal_storage_button:
                fileBrowserFragment.setFile(Environment.getExternalStorageDirectory());
                break;
            case R.id.external_storage_button:
                //I don't know how, but !(externalSD.equals("nil")) sends a null object to the FileBrowserFragment object
                if ((externalSD != "nil"))
                    fileBrowserFragment.setFile(new File(externalSD));
                Log.d("External", externalSD);
                break;
        }
        mainActivity.performFragTransaction(fileBrowserFragment);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        context = getContext();
        mainActivity = (MainActivity) context;
        Button internalStorageBtn = view.findViewById(R.id.internal_storage_button);
        Button externalStorageBtn = view.findViewById(R.id.external_storage_button);

        externalSD = "nil";
        File file = new File("storage");// + File.separator + "storage");
        for (File f : file.listFiles()) {
            if (f.getAbsolutePath().contains("-"))
                externalSD = f.getAbsolutePath();
            //else externalSD = "nil"
        }
        File fil = new File(externalSD);
        long availableSD = fil.getFreeSpace(), totalSD = fil.getTotalSpace();
        String formattedSD = Formatter.formatShortFileSize(context, availableSD);
        String formattedTotalSD = Formatter.formatShortFileSize(context, totalSD);
        ((TextView) view.findViewById(R.id.external_storage_size_left)).setText(formattedSD + "/" + formattedTotalSD + " left");
        ProgressBar externalIndicator = view.findViewById(R.id.external_indicator);
        externalIndicator.setMax(MAX);
        int extProg = (int) (((float) (totalSD - availableSD) / (float) totalSD) * MAX);
        externalIndicator.setProgress(extProg);

        File f = Environment.getExternalStorageDirectory();
        long availableSpace = f.getFreeSpace(), totalSpace = f.getTotalSpace();
        String formattedResult = Formatter.formatShortFileSize(context, availableSpace);
        String formattedTotalResult = Formatter.formatShortFileSize(context, totalSpace);
        ((TextView) view.findViewById(R.id.internal_storage_size_left)).setText(formattedResult + "/" + formattedTotalResult + " left");
        ProgressBar internalIndicator = view.findViewById(R.id.internal_indicator);
        internalIndicator.setMax(MAX);
        int intProg = (int) (((float) (totalSpace - availableSpace) / (float) totalSpace) * MAX);
        internalIndicator.setProgress(intProg);

        internalStorageBtn.setOnClickListener(this);
        externalStorageBtn.setOnClickListener(this);
        return view;
    }
}
