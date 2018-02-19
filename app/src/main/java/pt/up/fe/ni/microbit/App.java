package pt.up.fe.ni.microbit;

import android.graphics.drawable.Drawable;

/**
 * Created by tiagocv on 19/02/18.
 */

public class App {

    private Drawable mIcon;
    private String mName;

    public  App (Drawable icon, String name){
        mIcon = icon;
        mName = name;
    }

    public Drawable getIcon(){
        return this.mIcon;
    }

    public String getName(){
        return this.mName;
    }
}
