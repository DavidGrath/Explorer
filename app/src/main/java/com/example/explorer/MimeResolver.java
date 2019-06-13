package com.example.explorer;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import java.io.File;

public class MimeResolver {

    // TODO This is a quick fix, must change
    public static Drawable icon;
    public MimeResolver() {
    }

    private static String resolveMime(File file) {
        String extension;
        if (file.toString().contains(".")) {
             extension = file.toString().substring(file.toString().lastIndexOf(".") + 1).toLowerCase();
        } else {
            extension = "";
        }
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        String type = mimeTypeMap.getMimeTypeFromExtension(extension);
        return type;
    }


    public static int getResourceID(File file, Context context) {
        int resID;
        String prefix;
        String mime = resolveMime(file);
        if (mime != null) {
            prefix = mime.substring(0, mime.lastIndexOf("/"));
        } else {
            prefix = "";
        }
        switch(prefix) {
            case "text":
                resID = R.drawable.icon_text;
                break;
            case "application" :
                if (file.getName().substring(file.getName().lastIndexOf(".") + 1).equals("apk")) {
                    String APKPath = file.getAbsolutePath();
                    PackageManager packageManager = context.getPackageManager();
                    PackageInfo pi = packageManager.getPackageArchiveInfo(APKPath, 0);

                    pi.applicationInfo.sourceDir = APKPath;
                    pi.applicationInfo.publicSourceDir = APKPath;

                    Drawable APKIcon = pi.applicationInfo.loadIcon(packageManager);
                    String appName = (String) pi.applicationInfo.loadLabel(packageManager);
                    resID = APKIcon != null ? -1 : R.drawable.icon_apk;
                    icon = APKIcon;
                } else
                    resID = R.drawable.icon_apk;
                break;
                default:
                    resID = 0;
        }
        return resID;
    }
}
