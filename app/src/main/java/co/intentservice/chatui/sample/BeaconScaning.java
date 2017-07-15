package co.intentservice.chatui.sample;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import java.util.HashMap;

public class BeaconScaning extends Service implements BluetoothAdapter.LeScanCallback {

    //final variable

    //
    NotificationManager Notifi_M;
    Notification Notifi ;
    Boolean runningFlag=false;
    BluetoothManager btManager;
    BluetoothAdapter btAdapter;
    HashMap<String,Integer> idMap;
    HashMap<Integer,Boolean> alarmFlags;
    Context context;
    GcmReceiver mReceiver;
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            String returnedValue =(String)msg.obj;
            String sub[] = returnedValue.split("/");
            final int id = Integer.parseInt(sub[0]);

            Log.d("BC","들어옴"+id);
            if(alarmFlags.containsKey(id))
            {
                Log.d("BC","2들어옴"+id);

                //  alarmFlags.put(id,true);
                String data = sub[1];
                Intent intent = new Intent(BeaconScaning.this, ChattingActivity.class);
                intent.putExtra("Data",data);
                PendingIntent pendingIntent = PendingIntent.getActivity(BeaconScaning.this, 0, intent,PendingIntent.FLAG_UPDATE_CURRENT);
                NotificationCompat.Builder builder = new NotificationCompat.Builder(BeaconScaning.this);
                builder.setSmallIcon(R.drawable.ic_launcher_background)
                        .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_close_drawable))
                        .setColor(getResources().getColor(R.color.blue))
                        .setContentTitle("작품 "+id+"가 말을 걸어옵니다.")
                        .setContentIntent(pendingIntent)
                        .setContentText(data)
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setPriority(Notification.PRIORITY_HIGH);

                builder.setAutoCancel(true);
                NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                mNotificationManager.notify(0, builder.build());
                if(ChattingActivity.inForeground)
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        alarmFlags.remove(id);
                    }
                },3000);
            }

        }
    };

    public BeaconScaning() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public void init()
    {
        Notifi_M = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        idMap = new HashMap<>();
        //aaaaf411-8cf1-440c-87cd-e367daf9c930
        //aaaaaaaa-8cf1-440c-87cd-e367daf9c93b
        idMap.put("AAAAAAAA8CF1440C87CDE368DAF9C93B",1000);
        //idMap.put("AAAAF4118CF1440C87CDE368DAF9C930",2000);
        alarmFlags = new HashMap<>();
        context = this;



    }
    public int onStartCommand(Intent intent , int flags, int startID)
    {
        init();
        if(!runningFlag)
        {
            btManager = (BluetoothManager)getApplicationContext().getSystemService(BLUETOOTH_SERVICE);
            btAdapter = btManager.getAdapter();
            btAdapter.startLeScan(this);
            runningFlag=!runningFlag;
        }
        return START_STICKY;
    }

    @Override
    public void onLeScan(BluetoothDevice bluetoothDevice, int i, byte[] bytes) {
        Beacon beacon = Beacon.fromScanData(bytes,i);
        if(beacon!=null&&beacon.getBeaconType()==0)
        {
           String beaconID = beacon.getProximityUuid().replace("-","").toUpperCase();
            Log.d("Debug",beaconID);
           if(idMap.containsKey(beaconID))
           {
               int id = idMap.get(beaconID);

               Log.d("Debug","Contains"+!alarmFlags.containsKey(id));
               if(!alarmFlags.containsKey(id))
               {
                   Log.d("Debug","Contains2");
                   alarmFlags.put(id,true);
                   final int param = id;
                   HTTPConnector.getDatas("id="+param,"init",param+"",mHandler);

               }
           }

        }
    }

    public class GcmReceiver extends BroadcastReceiver {
        public void onReceive(Context context, Intent intent) {

        }
    }
}
