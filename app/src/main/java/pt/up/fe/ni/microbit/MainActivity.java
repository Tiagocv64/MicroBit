package pt.up.fe.ni.microbit;

import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.phearme.macaddressedittext.MacAddressEditText;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "Microbit_Main";

    static public String MAC_DEVICE_BLUETOOTH;

    static public ArrayList<App> apps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);

        //Get list of installed apps
        final PackageManager pm = getPackageManager();
        List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);

        //ArrayList of Custom class App
        apps = new ArrayList<>();

        //Get SharedPreferences
        SharedPreferences sharedPref = this.getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        //For each application installed
        for (ApplicationInfo packageInfo : packages) {
            if (pm.getLaunchIntentForPackage(packageInfo.packageName) != null) {  //If app not a system app
                String packageName = packageInfo.packageName;
                if (!sharedPref.contains(packageName)) {  //If app preference (active/not active) not saved
                    editor.putBoolean(packageName, true);  //Save app preference (default: active)
                    Log.d(TAG, "Adding new item to SharedPref: " + packageName);
                }
                Boolean active = sharedPref.getBoolean(packageName, true);
                Drawable icon = pm.getApplicationIcon(packageInfo);
                String name = (String) pm.getApplicationLabel(packageInfo);
                apps.add(new App(icon, name, packageName, active));  //Add application to ArrayList<App>
            }
        }
        editor.apply();  //Apply changes made to SharedPreferences

        //Sort applications alphabetically
        Collections.sort(apps, new Comparator<App>() {
            @Override
            public int compare(App app, App t1) {
                return app.getName().compareTo(t1.getName());
            }
        });

        //Set listview with ArrayList<App> created previously
        ListView listView = (ListView) findViewById(R.id.apps_list_view);
        ListAdapter appsAdapter = new ListAdapter(this, R.layout.item_view, apps);
        listView.setAdapter(appsAdapter);

        //Check if Microbit has access to notifications
        ComponentName cn = new ComponentName(this, ForegroundService.class);
        String flat = Settings.Secure.getString(this.getContentResolver(), "enabled_notification_listeners");
        boolean hasAccess = flat != null && flat.contains(cn.flattenToString());

        //If it doesn't, ask for it
        if (!hasAccess){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Allow notification access to MicroBit?");
            builder.setMessage("In order to Microbit be able to send notifications to the server, it will need this permission. Microbit will be able to read all notifications, including personal information such as contact names and the text of messages you receive.");

            builder.setPositiveButton("Allow", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    startActivity(new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"));
                }
            });
            builder.setNegativeButton("Deny", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            builder.show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_menu, menu);
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            menu.findItem(R.id.action_bluetooth).setEnabled(false);
        }
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.action_bluetooth:
                BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                if (!mBluetoothAdapter.isEnabled()) {
                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBtIntent, 6464);
                }
                startService(new Intent(this,ForegroundService.class));
                return true;

            case R.id.device_bluetooth:

                final SharedPreferences sharedPref = this.getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE);
                final SharedPreferences.Editor editor = sharedPref.edit();

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("MAC Address of bluetooth device");

                final MacAddressEditText input = new MacAddressEditText(this);
                input.setText(sharedPref.getString("bluetooth_device", ""));
                builder.setView(input);

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (input.getText().toString().length() == 17) {
                            MAC_DEVICE_BLUETOOTH = input.getText().toString();
                            editor.putString("bluetooth_device", MAC_DEVICE_BLUETOOTH);
                            editor.apply();
                            Toast.makeText(getBaseContext(), "MAC Address successfully saved!",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getBaseContext(), "MAC Address not valid!",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
                return true;

            case R.id.action_help:
                AlertDialog.Builder helpBuilder = new AlertDialog.Builder(this);
                helpBuilder.setTitle("Instructions");
                helpBuilder.setMessage("1: Accept the notifications access permission\n"  +
                        "2: Turn Bluetooth ON on both devices (this one and the server which as installed ServerMicrobit)\n" +
                        "3: Input the Bluetooth address of the server in the textbox that appears when you click on the middle icon in the toolbar\n" +
                        "4: Start the server and try to connect to it (3º icon in the toolbar)");
                helpBuilder.show();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        //Bluetooth activated
        if (requestCode == 6464){
            if (resultCode == RESULT_OK){
                startService(new Intent(this,ForegroundService.class));
            }
        }
    }


}

