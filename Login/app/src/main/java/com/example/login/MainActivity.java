package com.example.login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    EditText edt_Email;
    EditText edt_Pass;
    ImageButton bnt_Login;
    LinearLayout linearLayout;
    String url ="http://192.168.1.5/androidwebservice/login.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AnhXa();
            bnt_Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(checkEmptyFormat() == true) {
                    checkLogin(url);
                }
            }
        });

    }

    private boolean checkEmptyFormat(){
        String EMAIL_REGEX = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";
        boolean OK = true;
        String email = edt_Email.getText().toString().trim();
        String password = edt_Pass.getText().toString().trim();
        if(email.isEmpty() || password.isEmpty()){
            Toast.makeText(MainActivity.this,"Input full information",Toast.LENGTH_SHORT).show();
            return false;
        }
        if(email.matches(EMAIL_REGEX) == false){
            Toast.makeText(MainActivity.this,"Wrong email",Toast.LENGTH_SHORT).show();
            return false;
        }
        if(password.length() < 6 || password.length() > 20) {
            Toast.makeText(MainActivity.this, "Password must less than 6 and more than 20", Toast.LENGTH_SHORT).show();
        }
        return true;
    }

    private void checkLogin(String url){
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for(int i = 0; i < response.length(); i++){
                    try {
                        JSONObject object = response.getJSONObject(i);
                        String email = object.getString("user_email");
                        String password = object.getString("user_password");

                        if(email.equals(edt_Email.getText().toString().trim()) == false || password.equals(edt_Pass.getText().toString()) == false){
                            Toast.makeText(MainActivity.this,"Wrong password",Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(MainActivity.this,"Success",Toast.LENGTH_SHORT).show();
                            break;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this,error.toString(),Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue.add(jsonArrayRequest);
    }

    private void AnhXa(){
        edt_Email = (EditText) findViewById(R.id.edtEmail);
        edt_Pass = (EditText) findViewById(R.id.edtPass);
        bnt_Login = (ImageButton) findViewById(R.id.btn_Login);
        linearLayout = (LinearLayout) findViewById(R.id.linear);
    }
}
