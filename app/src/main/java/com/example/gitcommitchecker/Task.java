package com.example.gitcommitchecker;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class Task extends AsyncTask<String, Void, String> {

    String clientKey = "###############";
    private String str, receiveMsg;
    private final String ID = "######";

    @Override
    protected String doInBackground(String... strings) {
        URL url = null;
        try {
            url = new URL("https://api.github.com/repos/corn1200/Blind/commits");

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("Connect-Type", "application/x-www-form-urlencoded;charset=UTF-8");

            if (connection.getResponseCode() == connection.HTTP_OK) {
                InputStreamReader tmp = new InputStreamReader(connection.getInputStream(), "UTF-8");
                BufferedReader reader = new BufferedReader(tmp);
                StringBuffer buffer = new StringBuffer();
                while ((str = reader.readLine()) != null) {
                    buffer.append(str);
                }
                receiveMsg = buffer.toString();
                Log.i("receiveMsg : ", receiveMsg);

                reader.close();
            } else {
                Log.i("결과", connection.getResponseCode() + "Error");
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return receiveMsg;
    }
}
