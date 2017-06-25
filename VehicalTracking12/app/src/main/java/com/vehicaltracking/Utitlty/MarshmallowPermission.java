package com.vehicaltracking.Utitlty;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;

import com.vehicaltracking.R;

/**
 * Created by Himanshu PC on 25-06-2017.
 */

public class MarshmallowPermission
{
//class for all runtime permission for marshmallow
public static final int STORAGE_PERMISSION_CODE = 22;
    public static final int REQUEST_LOCATION = 10;

    public static MarshmallowPermission mpermsionObj;
    Activity mActivity;
    public static MarshmallowPermission getInstance(Activity mactivity){
        if(mpermsionObj==null){
            mpermsionObj=new MarshmallowPermission();
            mpermsionObj.mActivity=mactivity;
        }
        return mpermsionObj;
    }

    //Requesting permission
    public void requestStoragePermission(){

        if (ActivityCompat.shouldShowRequestPermissionRationale(mActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE)){
            //If the user has denied the permission previously your code will come to this block
            new AlertDialog.Builder(mActivity)
                    .setTitle(R.string.message_readwrite)
                    .setMessage(R.string.message_readwriteenable)
                    .setPositiveButton(R.string.message_ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //Prompt the user once explanation has been shown
                            ActivityCompat.requestPermissions(mActivity,
                                    new String[]{ Manifest.permission.READ_EXTERNAL_STORAGE,
                                            Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                    STORAGE_PERMISSION_CODE);
                        }
                    })
                    .create()
                    .show();


        }

        //And finally ask for the permission
        ActivityCompat.requestPermissions(mActivity,new String[]{ Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE},STORAGE_PERMISSION_CODE);
    }

    public boolean isWritetoStorageAllowed() {
        //Getting the permission status
        int result = ContextCompat.checkSelfPermission(mActivity, android.Manifest.permission.READ_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(mActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        //If permission is granted returning true
        if (result == PackageManager.PERMISSION_GRANTED && result1==PackageManager.PERMISSION_GRANTED)
            return true;

        //If permission is not granted returning false
        return false;
    }


    public boolean LocationPermission() {
        if (ContextCompat.checkSelfPermission(mActivity,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(mActivity,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(mActivity)
                        .setTitle(R.string.message_enableLocation)
                        .setMessage(R.string.message_locationdesc)
                        .setPositiveButton(R.string.message_ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(mActivity,
                                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                                        REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(mActivity,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }
}
