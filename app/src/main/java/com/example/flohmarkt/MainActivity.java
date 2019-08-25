package com.example.flohmarkt;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.preference.PreferenceManager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity{

    ServerTask serverTask = new ServerTask();
    private final String URL = "http://eaustria.no-ip.biz/flohmarkt/flohmarkt.php";
    private String USERNAME;
    private String PASSWORD;

    private SharedPreferences prefs;

    private ListView listView;
    private List<Article> articles = new ArrayList<>();

    ArticleAdapter adapter;

    LocationManager locationManager;
    private static final int RQ_ACCESS_FINE_LOCATION = 123;
    private boolean isGpsAllowed = false;
    LocationListener locationListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        assertPreferencesInFile();
        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        USERNAME = prefs.getString("username","");
        PASSWORD = prefs.getString("password","");

        listView = findViewById(R.id.listView);

        registerForContextMenu(listView);
        getData();
        bindAdapterToListView(listView);
        listView.setOnItemClickListener((adapterView, view, i, l) -> {
            Intent intent = new Intent(view.getContext(), DetailActivity.class);
            intent.putExtra("article",new Gson().toJson(articles.get(i)));
            intent.putExtra("username", USERNAME);
            intent.putExtra("password", PASSWORD);
            startActivityForResult(intent,1);
        });
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        int viewId = v.getId();
        if (viewId == R.id.listView) getMenuInflater().inflate(R.menu.detail_menu, menu);
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        AdapterView.AdapterContextMenuInfo info =
                (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        Article article = null;
        if(info != null) {
            int pos = info.position;
            article = (Article) listView.getAdapter().getItem(pos);
        }
        if(item.getItemId() == R.id.mnu_call){
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + article.getPhone()));
            if (checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.CALL_PHONE},1 );
                return true;
            }
            startActivity(intent);
        }
        if (item.getItemId() == R.id.mnu_delete){
            serverTask = new ServerTask();
            serverTask.execute(URL ,"delete", USERNAME, PASSWORD, Integer.toString(article.getId()));
            getData();
            bindAdapterToListView(listView);
            return true;
        }
        return super.onContextItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        getData();
        bindAdapterToListView(listView);
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void assertPreferencesInFile() {
        try {
            String versionKey = "longVersionCode";
            long currentVersion = getPackageManager()
                    .getPackageInfo(getPackageName(), 0)
                    .getLongVersionCode();
            long lastStoredVerstion = prefs.getLong(versionKey, -1);
            if (lastStoredVerstion == currentVersion) return;
            prefs.edit()
                    .putLong(versionKey, currentVersion)
                    .putString("username", prefs.getString("username", "HamadeSev"))
                    .putString("password", prefs.getString("password", "31166"))
                    .apply();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.mnu_create:
                handleCreate();
                break;
            case R.id.mnu_settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivityForResult(intent, 1234);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void handleCreate() {
        final View dialogView = getLayoutInflater().inflate(R.layout.dialog_layout, null);
        new AlertDialog.Builder(this)
                .setMessage("new article")
                .setCancelable(true)
                .setView(dialogView)
                .setPositiveButton("add", (dialog, which) -> {
                    EditText eName = dialogView.findViewById(R.id.dialog_name);
                    String name = eName.getText().toString();
                    EditText ePrice = dialogView.findViewById(R.id.dialog_price);
                    String price = ePrice.getText().toString();
                    EditText eEmail =dialogView.findViewById(R.id.dialog_email);
                    String email = eEmail.getText().toString();
                    EditText ePhone = dialogView.findViewById(R.id.dialog_phone);
                    String phone = ePhone.getText().toString();
                    serverTask = new ServerTask();
                    serverTask.execute(URL,"add",USERNAME, PASSWORD, email, phone, name, price);
                    getData();
                    bindAdapterToListView(listView);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void bindAdapterToListView(ListView lv) {
        adapter = new ArticleAdapter(this,R.layout.list_item,articles);
        lv.setAdapter(adapter);
    }

    private void getData() {
        String result;
        try {
            serverTask = new ServerTask();
            result = serverTask.execute(URL,"get","","").get();
            fillList(result);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void fillList(String jString) {
        try {
            JSONArray jsonArray = new JSONObject(jString).getJSONArray("data");
            Gson gson = new GsonBuilder().create();
            Type type = new TypeToken<List<Article>>(){}.getType();
            articles = gson.fromJson(jsonArray.toString(), type);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void checkPermissionGPS() {
        String permission = Manifest.permission.ACCESS_FINE_LOCATION;
        if(ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this,
                    new String[]{ permission },RQ_ACCESS_FINE_LOCATION);
        else gpsGranted();
    }

    private void gpsGranted() {
        isGpsAllowed = true;
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(isGpsAllowed) locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                3000,
                0,
                locationListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isGpsAllowed) locationManager.removeUpdates(locationListener);
    }

    public void startGPSService(MenuItem menuItem){
        Intent intent = new Intent(this, GPSService.class);
        startService(intent);
    }

    public void startPriceService(MenuItem item) {
        Intent intent = new Intent(this, PriceService.class);
        startService(intent);
    }
}
