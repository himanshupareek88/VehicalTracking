package com.vehicaltracking.Utitlty;

import android.content.Context;
import android.os.Environment;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

/**
 * Created by Himanshu PC on 22-06-2017.
 */

public class SaveSdCard {


    public void saveToCard(Context mcontext , String text)
    {    try {
        String path= "/sdcard/vehicaltracking.txt";
        File myFile = new File(path);
        if(!myFile.exists())
        myFile.createNewFile();

        FileOutputStream fOut = new FileOutputStream(myFile,true);
        OutputStreamWriter myOutWriter =
                new OutputStreamWriter(fOut);
        myOutWriter.append("\n"+text);
        myOutWriter.close();
        fOut.close();

    } catch (Exception e) {
      e.printStackTrace();
    }
    }
}
