package co.intentservice.chatui.sample;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by 남지니 on 2016-07-22.
 */
public class HTTPConnector extends Thread{

    static public Bundle MessageQue = new Bundle();

    static public void getDatas(final String Params, final String PHP , final String Key, final Handler handler) {

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                String Result="";
                URL targetURL = null;
                try {
                    Log.d("연결중123","입니다");
                    targetURL = new URL("http://192.168.10.32:8000");

                    URLConnection urlConn = targetURL.openConnection();
                    HttpURLConnection hurlc = (HttpURLConnection) urlConn;
                    // 헤더값을 설정한다.
                    hurlc.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    // 전달 방식을 설정한다. POST or GET, 기본값은 GET 이다.
                    hurlc.setRequestMethod("POST");
                    // 서버로 데이터를 전송할 수 있도록 한다. GET방식이면 사용될 일이 없으나, true로
                    // 설정하면 자동으로 POST로 설정된다. 기본값은 false이다.
                    hurlc.setDoOutput(true);
                    // 서버로부터 메세지를 받을 수 있도록 한다. 기본값은 true이다.
                    hurlc.setDoInput(true);
                    hurlc.setUseCaches(false);
                    hurlc.setDefaultUseCaches(false);
                    OutputStream opstrm = hurlc.getOutputStream();
                    opstrm.write(Params.getBytes());
                    opstrm.flush();
                    opstrm.close();
                    String buffer = null;
                    BufferedReader in = new BufferedReader(new InputStreamReader
                            (hurlc.getInputStream()));
                    while (true) {
                        while ((buffer = in.readLine()) != null) {
                            Result += buffer;
                        }
                        if(!Result.equals(""))
                            break;
                    }
                    in.close();



                    Message msg = handler.obtainMessage(1, (String)Result);
                    handler.sendMessage(msg);

                    Log.d("BC",Result);
                    //MessageQue.putString(Key,Result);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (ProtocolException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }
}