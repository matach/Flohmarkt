package com.example.flohmarkt;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.gson.Gson;

public class DetailActivity extends AppCompatActivity {

    private final String URL = "http://eaustria.no-ip.biz/flohmarkt/flohmarkt.php";
    private String USERNAME;
    private String PASSWORD;

    private MapView mapView;
    private GoogleMap gMap;

    ServerTask task = new ServerTask();

    TextView detailNameText,
            detailPriceText,
            detailUserText,
            detailEmailText,
            detailPhoneText;

    Article article;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        setContentView(R.layout.activity_detail);
        detailNameText = findViewById(R.id.detailNameText);
        detailPriceText = findViewById(R.id.detailPriceText);
        detailUserText = findViewById(R.id.detailUserText);
        detailEmailText = findViewById(R.id.detailEmailText);
        detailPhoneText = findViewById(R.id.detailPhoneText);
        mapView = findViewById(R.id.map_view);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        String json = bundle.getString("article");
        article = new Gson().fromJson(json, Article.class);

        detailNameText.setText(article.getName());
        detailPriceText.setText(String.valueOf(article.getPrice()));
        detailUserText.setText(article.getUsername());
        detailEmailText.setText(article.getEmail());
        detailPhoneText.setText(article.getPhone());

        USERNAME = bundle.getString("username");
        PASSWORD = bundle.getString("password");

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.mnu_call:
                handleCall();
                break;
            case R.id.mnu_delete:
                handleDelete();
                break;
            case R.id.mnu_map:
                openMap();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void handleDelete() {
        task = new ServerTask();
        task.execute(URL ,"delete", USERNAME, PASSWORD, Integer.toString(article.getId()));
        setResult(Activity.RESULT_OK);
        finish();
    }

    private void handleCall() {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + article.getPhone()));
        if (checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CALL_PHONE},1 );
            return;
        }
        startActivity(intent);
    }

    public void openMap() {
        if (!(article.getLon() == null) || (article.getLat() == null) ){
            String pos = "geo:"+article.getLat()+","+article.getLon();
            Uri uri = Uri.parse(pos);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(uri);
            startActivity(intent);
        } else {
            Toast.makeText(this, "Koordinaten nicht vorhanden", Toast.LENGTH_SHORT);
        }
    }
}
