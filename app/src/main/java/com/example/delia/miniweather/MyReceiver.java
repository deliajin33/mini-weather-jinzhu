package com.example.delia.miniweather;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.delia.service.MyService;

public class MyReceiver extends BroadcastReceiver
{

    private Message message;
    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        //throw new UnsupportedOperationException("Not yet implemented");
        Log.d("MyReceiver","heheheh");
        message.getMsg("123");
        new Thread(new myRunable(context)).start();
    }

    interface Message {
        public void getMsg(String str);
    }
    public void setMessage(Message message) {
        this.message = message;
    }
}
class myRunable implements Runnable{
    private Context context;
    public myRunable(Context c)
    {
        this.context=c;
    }
    public void run()
    {
        try {
            Thread.sleep(1000*60*30);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        context.startService(new Intent(context,MyService.class));
        Intent intent=new Intent("android.appwidget.action.APPWIDGET_UPDATE");
        context.sendBroadcast(intent);
        Log.d("updatetest","123456");
    }
}
