package pt.up.fe.ni.microbit;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.*;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    static public ArrayList<App> apps = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final PackageManager pm = getPackageManager();
        //get a list of installed apps.
        List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);

        SharedPreferences sharedPref = this.getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();


        for (ApplicationInfo packageInfo : packages) {
            if( pm.getLaunchIntentForPackage(packageInfo.packageName) != null ){
                String packageName = packageInfo.packageName;
                if(!sharedPref.contains(packageName)){
                    editor.putBoolean(packageName, true);
                    Log.d("SHAREDPREF", "Adding new item: " + packageName);
                }
                Boolean active = sharedPref.getBoolean(packageName, true);
                Drawable icon = pm.getApplicationIcon(packageInfo);
                String name = (String) pm.getApplicationLabel(packageInfo);
                apps.add(new App(icon, name, packageName, active));
            }
        }
        editor.commit();
        Collections.sort(apps, new Comparator<App>() {
            @Override
            public int compare(App app, App t1) {
                return app.getName().compareTo(t1.getName());
            }
        });

        ListView listView = (ListView)findViewById(R.id.apps_list_view);
        ListAdapter appsAdapter = new ListAdapter(this, R.layout.item_view, apps);
        listView.setAdapter(appsAdapter);

    }
}

