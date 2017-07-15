package co.intentservice.chatui.sample;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    Context context;
    MainActivity myActivity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        Button btn = (Button)findViewById(R.id.btn);
        context = this;
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.startService(new Intent(context,BeaconScaning.class));
            }
        });
    }
    public void onStart(){
        super.onStart();

    }

}
