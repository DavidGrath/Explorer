package com.example.explorer;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
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
        String mime = resolveMime(file);
        if (mime != null) {

        } else {
            mime = "";
        }
        if(!mime.equals("")) {
            String prefix = mime.substring(0, mime.lastIndexOf("/"));
            String suffix = mime.substring(mime.lastIndexOf("/") + 1);
            switch (prefix) {
                case "text":
                    switch (suffix) {
                        case "plain":
                            resID = R.drawable.txt;
                            break;
                        case "css":
                            resID = R.drawable.css;
                            break;
                        case "csv":
                            resID = R.drawable.csv;
                            break;
                        case "html":
                            resID = R.drawable.html;
                            break;
                        default:
                            resID = 0;
                            break;
                    }
                    break;
                case "application":
                    switch (suffix) {
                        case "vnd.android.package-archive":
                            String APKPath = file.getAbsolutePath();
                            PackageManager packageManager = context.getPackageManager();
                            PackageInfo pi = packageManager.getPackageArchiveInfo(APKPath, 0);

                            pi.applicationInfo.sourceDir = APKPath;
                            pi.applicationInfo.publicSourceDir = APKPath;

                            Drawable APKIcon = pi.applicationInfo.loadIcon(packageManager);
                            String appName = (String) pi.applicationInfo.loadLabel(packageManager);
                            resID = APKIcon != null ? -1 : R.drawable.icon_apk;
                            icon = APKIcon;
                            break;
                        case "msword":
                            resID = R.drawable.doc;
                            break;
                        case "x-msdownload":
                            resID = R.drawable.exe;
                            break;
                        case "javascript":
                            resID = R.drawable.javascript;
                            break;
                        case "json":
                            resID = R.drawable.json_file;
                            break;
                        case "pdf":
                            resID = R.drawable.pdf;
                            break;
                        case "vnd.ms-powerpoint":
                            resID = R.drawable.ppt;
                            break;
                        case "rtf":
                            resID = R.drawable.rtf;
                            break;
                        case "vnd.ms-excel":
                            resID = R.drawable.xls;
                            break;
                        case "rss+xml":
                        case "xml":
                        case "atom+xml":
                            resID = R.drawable.xml;
                            break;
                        case "zip":
                            resID = R.drawable.zip;
                            break;
                        default:
                            resID = R.drawable.icon_apk;
                            break;
                    }
                    break;
                case "video":
                    switch (suffix) {
                        case "x-msvideo":
                            resID = R.drawable.avi;
                            break;
                        case "mp4":
                            resID = R.drawable.mp4;
                            break;
                        default:
                            resID = 0;
                            break;
                    }
                    break;
                case "image":
                    switch (suffix) {
                        case "vnd.dwg":
                            resID = R.drawable.dwg;
                            break;
                        case "jpeg":
                            resID = R.drawable.jpg;
                            break;
                        case "vnd.adobe.photoshop":
                            resID = R.drawable.psd;
                            break;
                        case "png":
                            resID = R.drawable.png;
                            break;
                        case "svg+xml":
                            resID = R.drawable.svg;
                            break;
                        default:
                            resID = 0;
                            break;
                    }
                    break;
                case "audio":
                    switch (suffix) {
                        case "mp4":
                            resID = R.drawable.mp4;
                            break;
                        default:
                            resID = 0;
                            break;
                    }
                    break;
                default:
                    resID = 0;
            }
        } else resID = 0;
        return resID;
    }
}
