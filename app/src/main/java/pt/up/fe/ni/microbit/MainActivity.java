package pt.up.fe.ni.microbit;

import android.content.Intent;
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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final PackageManager pm = getPackageManager();
        //get a list of installed apps.
        List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);

      /*  for (ApplicationInfo packageInfo : packages) {
            Log.d("DEBUG", "Installed package :" + packageInfo.packageName);
            Log.d("DEBUG", "Source dir : " + packageInfo.sourceDir);
            Log.d("DEBUG", "Launch Activity :" + pm.getLaunchIntentForPackage(packageInfo.packageName));
        }*/

        ArrayList<App> apps = new ArrayList<>();

        for (ApplicationInfo packageInfo : packages) {
            if( pm.getLaunchIntentForPackage(packageInfo.packageName) != null ){
                Drawable icon = pm.getApplicationIcon(packageInfo);
                String name = (String) pm.getApplicationLabel(packageInfo);
                apps.add(new App(icon, name));
            }
        }
        apps.sort(new Comparator<App>() {
            @Override
            public int compare(App app, App t1) {
                return app.getName().compareTo(t1.getName());
            }
        });
        Log.d("LOUCO", apps.toString());

        ListView listView = (ListView)findViewById(R.id.apps_list_view);
        ListAdapter appsAdapter = new ListAdapter(this, R.layout.item_view, apps);
        listView.setAdapter(appsAdapter);

    }
}

