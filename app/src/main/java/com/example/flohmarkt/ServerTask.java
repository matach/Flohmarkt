package com.example.flohmarkt;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.EditText;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import static android.provider.ContactsContract.CommonDataKinds.Website.URL;

class ServerTask extends AsyncTask<String, Integer, String> {

    public static final String REQUEST_METHOD = "GET";
    public static final int READ_TIMEOUT = 15000;
    public static final int CONNECTION_TIMEOUT = 15000;

    private final String TAG = ServerTask.class.getSimpleName();

    @Override
    protected String doInBackground(String... strings) {
        String stringUrl = strings[0];
        String operation = strings[1];
        String sJson = "";

        Log.d(TAG, " entered doInBackground");
        Log.d(TAG, "url: " + stringUrl);

        StringBuilder urlBuilder = new StringBuilder();
        urlBuilder.append(stringUrl).append("?operation=").append(operation);
        try {
            String username = strings[2];
            String password = strings[3];
            urlBuilder.append("&username=").append(username)
                    .append("&password=").append(password);
            switch (operation) {
                case "get":
                    break;
                case "delete":
                    urlBuilder
                            .append("&id=").append(strings[4]);
                    break;
                case "add":
                    String email = strings[4];
                    String phone = strings[5];
                    String name = strings[6];
                    String price = strings[7];

                    urlBuilder.append("&email=").append(email)
                            .append("&phone=").append(phone)
                            .append("&name=").append(name)
                            .append("&price=").append(price);
                    break;
            }
            HttpURLConnection connection =
                    (HttpURLConnection) new URL(urlBuilder.toString()).openConnection();
            connection.setReadTimeout(READ_TIMEOUT);
            connection.setConnectTimeout(CONNECTION_TIMEOUT);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.connect();
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(connection.getInputStream())
                );
                sJson = readResponseStream(reader);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sJson;
    }

    private String readResponseStream(BufferedReader reader) throws IOException {
        Log.d(TAG, "entered readResponseStreaulat");
        StringBuilder stringBuilder = new StringBuilder();
        String line = "";
        while ((line = reader.readLine()) != null) {
            stringBuilder.append(line);
        }
        return stringBuilder.toString();
    }

    @Override
    protected void onPostExecute(String s) {
        Log.d(TAG, "entered on PostExecute");
        super.onPostExecute(s);
    }
}
