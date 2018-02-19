package pt.up.fe.ni.microbit;

import android.graphics.drawable.Drawable;

/**
 * Created by tiagocv on 19/02/18.
 */

public class App {

    private Drawable mIcon;
    private String mName;
    private String mPackage;
    private Boolean mActive;

    public  App (Drawable micon, String mname, String mpackage, Boolean mactive){
        mIcon = micon;
        mName = mname;
        mPackage = mpackage;
        mActive = mactive;
    }

    public Drawable getIcon(){
        return this.mIcon;
    }

    public String getName(){
        return this.mName;
    }

    public  String getPackage(){
        return this.mPackage;
    }

    public Boolean getActive(){
        return this.mActive;
    }

    public void setActive(Boolean bool){
        this.mActive = bool;
    }
}
