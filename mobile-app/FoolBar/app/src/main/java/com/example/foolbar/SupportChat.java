package com.example.foolbar;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DownloadManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class SupportChat extends AppCompatActivity {
    String clientProblemText;
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_support_chat);
        long currentTime = Calendar.getInstance().getTimeInMillis();
        final long TIME_WINDOW = 1000 * 60 * 8; // 8 minutes
        final SharedPreferences sharedPreferences = getSharedPreferences("settings", Context.MODE_PRIVATE);
        long appfail = sharedPreferences.getLong("application_fail", 0);
        long inact_click = sharedPreferences.getLong("last_inactive_click", 0);

        final ArrayList<String> clientProblems = new ArrayList<>();
        boolean anyProblem = false;

        Log.d("chat_currenttime", String.valueOf(currentTime));
        Log.d("chat_appfail", String.valueOf(appfail));
        Log.d("chat_inactive_click", String.valueOf(inact_click));
        if (currentTime - appfail < TIME_WINDOW) {
            clientProblems.add("приложение упало");
            anyProblem = true;
        }
        if (currentTime - inact_click < TIME_WINDOW) {
            clientProblems.add("таблица умножения не работает");
            anyProblem = true;
        }

        TextView tv = findViewById(R.id.support_message_init);
        if (anyProblem) {

            clientProblemText = "Наш оператор уже знает, что у вас " + String.join(" и ", clientProblems) + ". Он уже работает над вашей проблемой";
            tv.setText(clientProblemText);
        } else {
            tv.setText("Расскажите о своей проблеме");
        }

        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://192.168.43.76:8080/newError";
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("network-response", response);
                    }
                },
                new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("network-error", error.getMessage());
            }
        }) {
            @Override
            public byte[] getBody() throws AuthFailureError {
                Log.d("network", "getBody");
                return clientProblemText.getBytes();
            }
        };

        queue.add(postRequest);

    }
}
