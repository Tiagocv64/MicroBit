package pt.up.fe.ni.microbit;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tiagocv on 19/02/18.
 */

public class ListAdapter extends ArrayAdapter<App> {

    private ArrayList<App> mlistApps = new ArrayList();

    public ListAdapter(Context context, int resource, List<App> listApps) {
        super(context, resource, listApps);
        mlistApps = (ArrayList) listApps;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.item_view, null);
        }

        ImageView iconView = (ImageView) v.findViewById(R.id.icon_app);
        TextView textView = (TextView) v.findViewById(R.id.name_app);
        Switch switchview = (Switch) v.findViewById(R.id.switch_app);
        iconView.setImageDrawable(mlistApps.get(position).getIcon());
        textView.setText(mlistApps.get(position).getName());
        switchview.setChecked(mlistApps.get(position).getActive());
        //Log.e("VIEW", mlistApps.get(position).getPackage() + ": " + mlistApps.get(position).getActive());

        switchview.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean b) {
                if (buttonView.isPressed()){
                    mlistApps.get(position).setActive(b);
                    MainActivity.apps.get(position).setActive(b);
                    Log.e("CHANGED", mlistApps.get(position).getPackage() + ": " + b);
                    SharedPreferences sharedPref = getContext().getSharedPreferences(getContext().getString(R.string.app_name), Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putBoolean(mlistApps.get(position).getPackage(), b);
                    editor.commit();
                }
            }
        });

        return v;
    }

}