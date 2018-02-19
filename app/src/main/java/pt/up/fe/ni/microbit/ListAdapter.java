package pt.up.fe.ni.microbit;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
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
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.item_view, null);
        }

        ImageView iconView = (ImageView) v.findViewById(R.id.icon_app);
        TextView textView = (TextView) v.findViewById(R.id.name_app);
        iconView.setImageDrawable(mlistApps.get(position).getIcon());
        textView.setText(mlistApps.get(position).getName());

        return v;
    }

}