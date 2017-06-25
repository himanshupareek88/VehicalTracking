package com.vehicaltracking.Utitlty;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

/**
 * Created by Himanshu PC on 22-06-2017.
 */

public class SaveCard_Service extends IntentService {


    public SaveCard_Service() {
        super(SaveCard_Service.class.getName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        Log.d("SaveCard_Service","service started");
        String textdata = intent.getStringExtra("textdata");

        Bundle bundle = new Bundle();

        if (!TextUtils.isEmpty(textdata)) {
            /* Update UI: Download Service is Running */
            SaveSdCard msavecard=new SaveSdCard();
            msavecard.saveToCard(SaveCard_Service.this,textdata);
        }
        this.stopSelf();
    }


}