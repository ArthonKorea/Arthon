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
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import java.util.HashMap;

public class BeaconScaning extends Service implements BluetoothAdapter.LeScanCallback {

    //final variable

    //
    Boolean runningFlag=false;
    BluetoothManager btManager;
    BluetoothAdapter btAdapter;
    HashMap<String,Integer> idMap;
    HashMap<Integer,Boolean> alarmFlags;
    Context context;
    BroadcastReceiver mReceiver;
    public BeaconScaning() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public void init()
    {
        idMap = new HashMap<>();
        //aaaaf411-8cf1-440c-87cd-e367daf9c930
        //aaaaaaaa-8cf1-440c-87cd-e367daf9c93b
        idMap.put("AAAAAAAA8CF1440C87CDE368DAF9C93B",1000);
        idMap.put("AAAAF4118CF1440C87CDE368DAF9C930",2000);
        alarmFlags = new HashMap<>();
        context = this;
        IntentFilter intentfilter = new IntentFilter();
        intentfilter.addAction("init");
        mReceiver = new BroadcastReceiver(){
            @Override
            public void onReceive(Context context, Intent intent) {

                int id = intent.getIntExtra("ID",0);

                Log.d("BC","들어옴"+id);
                if(alarmFlags.containsKey(id))
                {
                    Log.d("BC","2들어옴"+id);

                    //  alarmFlags.put(id,true);
                    String data = intent.getStringExtra("Data");
                    notifyScanningBeacon(id,data+"");
                }
            }
        };
        registerReceiver(mReceiver, intentfilter);



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
               Log.d("Debug","Contains");
               int id = idMap.get(beaconID);
               if(!alarmFlags.containsKey(id))
               {
                   alarmFlags.put(id,true);
                   final int param = id;
                   Thread thread = new Thread(new Runnable() {
                       @Override
                       public void run() {
                           HTTPConnector.getDatas("id="+param,"init",param+"",context);
                       }
                   });
                   thread.start();

               }
           }

        }
    }
    public void notifyScanningBeacon(int ID,String Data){
        NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, ChattingActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
        Log.d("BC","이곳은 노티피케이션!");
        NotificationCompat.Builder mCompatBuilder = new NotificationCompat.Builder(this);
        mCompatBuilder.setSmallIcon(R.drawable.ic_launcher_background);
        mCompatBuilder.setTicker("NotificationCompat.Builder");
        mCompatBuilder.setWhen(System.currentTimeMillis());
        mCompatBuilder.setNumber(10);
        mCompatBuilder.setContentTitle("주위에 미술 작품이 있습니다.");
        mCompatBuilder.setContentText(Data);
        mCompatBuilder.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);
        mCompatBuilder.setContentIntent(pendingIntent);
        mCompatBuilder.setAutoCancel(true);
        nm.notify(ID, mCompatBuilder.build());

    }
}
