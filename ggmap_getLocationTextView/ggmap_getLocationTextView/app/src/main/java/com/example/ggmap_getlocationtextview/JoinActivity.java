package com.example.ggmap_getlocationtextview;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class JoinActivity extends AppCompatActivity {
    TextView txt_address;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join3);

        reflect();
        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            if(bundle.getString("address") != null){
                txt_address = (TextView) findViewById(R.id.txt_address);
                txt_address.setText(bundle.getString("address"));
            }
        }
    }

    private void reflect(){

    }
}
