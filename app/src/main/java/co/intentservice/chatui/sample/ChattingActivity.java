package co.intentservice.chatui.sample;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import co.intentservice.chatui.ChatView;
import co.intentservice.chatui.models.ChatMessage;

public class ChattingActivity extends AppCompatActivity {

    Context context;
    BroadcastReceiver mReceiver;
    ChatView chatView;
    public void receiveMessage(String data){
        chatView.addMessage(new ChatMessage(data, System.currentTimeMillis(), ChatMessage.Type.RECEIVED));

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("s", "start");
        setContentView(R.layout.activity_main);
        IntentFilter intentfilter = new IntentFilter();
        intentfilter.addAction("root");
        mReceiver = new BroadcastReceiver(){
            @Override
            public void onReceive(Context context, Intent intent) {

                    int id = intent.getIntExtra("ID",0);

                    //  alarmFlags.put(id,true);
                    String data = intent.getStringExtra("Data");
                    receiveMessage(data);

            }
        };
        context=this;
        context.registerReceiver(mReceiver, intentfilter);
        chatView = (ChatView) findViewById(R.id.chat_view);
        chatView.addMessage(new ChatMessage("Message received", System.currentTimeMillis(), ChatMessage.Type.RECEIVED));
        chatView.setOnSentMessageListener(new ChatView.OnSentMessageListener() {
            @Override
            public boolean sendMessage(ChatMessage chatMessage) {
                Log.d("msg", chatMessage.getMessage());
                HTTPConnector.getDatas("msg="+chatMessage.getMessage(),"root", "1", context);
                return true;
            }
        });
        chatView.setTypingListener(new ChatView.TypingListener() {
            @Override
            public void userStartedTyping() {
            }

            @Override
            public void userStoppedTyping() {

            }
        });
    }

}
