package com.homeo.spacex;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.homeo.spacex.Util.ConnectionManager;
import com.homeo.spacex.database.CrewDatabase;
import com.homeo.spacex.database.CrewEntity;
import com.homeo.spacex.databinding.ActivityMainBinding;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;

    static List<CrewEntity> crewList = new ArrayList<>();

    private RequestQueue requestQueue;
    CrewRecyclerAdapter adapter;

    CrewDatabase db;
    String url = "https://api.spacexdata.com/v4/crew";

    Dialog progressDialog;
    TextView dialogText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        progressDialog = new Dialog(MainActivity.this, R.style.CustomProgressDialog);
        progressDialog.setContentView(R.layout.dialog_layout);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        /*progressDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);*/
        dialogText = progressDialog.findViewById(R.id.dialogText);
        dialogText.setText("Hang on...");

        adapter = new CrewRecyclerAdapter(crewList, this);
        binding.crewRecycler.setAdapter(adapter);

        LinearLayoutManager manager = new LinearLayoutManager(this);
        binding.crewRecycler.setLayoutManager(manager);

        requestQueue = Volley.newRequestQueue(MainActivity.this);

        db = CrewDatabase.getDatabase(this.getApplicationContext());

        if (new ConnectionManager().checkConnecctivity(this)) {
            progressDialog.show();
            requestQueue.add(new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                public void onResponse(String str) {
                    try {

                        db.getCrewDao().deleteAll();
                        /*crewList1.clear();*/
                        JSONArray jsonArray = new JSONArray(str);
                        Log.e("output...obj..", "\n" + jsonArray);

                        for (int i = 0; i < jsonArray.length(); i++) {

                            //Declaring a json object corresponding to every pdf object in our json Array
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            String name = jsonObject.getString("name");
                            String id = jsonObject.getString("id");
                            String image = jsonObject.getString("image");
                            String status = jsonObject.getString("status");
                            String wikipedia = jsonObject.getString("wikipedia");
                            String agency = jsonObject.getString("agency");

                            CrewEntity model = new CrewEntity(id, name, agency, image, wikipedia, status);

                            db.getCrewDao().insertCrew(model);

                            /*crewList1.add(model);*/
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                public void onErrorResponse(VolleyError volleyError) {
                    progressDialog.dismiss();
                    Toast.makeText(MainActivity.this, "Check Internet Connection", Toast.LENGTH_LONG).show();
                }
            }) {
                protected Map getParams() {
                    return new HashMap();
                }
            });

            loadCrewList();

            progressDialog.dismiss();

        } else {
            crewList.clear();
            db = CrewDatabase.getDatabase(this.getApplicationContext());
            crewList = db.getCrewDao().getAllCrew();
            adapter = new CrewRecyclerAdapter(crewList, this);
            binding.crewRecycler.setAdapter(adapter);

            /*loadCrewList();*/
            System.out.println("Size: " + crewList.size());
            if (crewList.size() == 0){
                noInternet();
            }else {
                adapter.notifyDataSetChanged();
            }
        }

        binding.imgRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (new ConnectionManager().checkConnecctivity(MainActivity.this)) {
                    progressDialog.show();
                    requestQueue.add(new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                        public void onResponse(String str) {
                            try {
                                db.getCrewDao().deleteAll();
                                /*crewList1.clear();*/
                                JSONArray jsonArray = new JSONArray(str);
                                Log.e("output...obj..", "\n" + jsonArray);

                                for (int i = 0; i < jsonArray.length(); i++) {

                                    //Declaring a json object corresponding to every pdf object in our json Array
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    String name = jsonObject.getString("name");
                                    String id = jsonObject.getString("id");
                                    String image = jsonObject.getString("image");
                                    String status = jsonObject.getString("status");
                                    String wikipedia = jsonObject.getString("wikipedia");
                                    String agency = jsonObject.getString("agency");

                                    CrewEntity model = new CrewEntity(id, name, agency, image, wikipedia, status);

                                    db.getCrewDao().insertCrew(model);

                                    /*crewList1.add(model);*/
                                }

                                loadCrewList();
                                progressDialog.dismiss();
                                Toast.makeText(MainActivity.this,
                                        "Refreshed Successfully...",
                                        Toast.LENGTH_SHORT)
                                        .show();

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                        public void onErrorResponse(VolleyError volleyError) {
                            progressDialog.dismiss();
                            Toast.makeText(MainActivity.this, "Check Internet Connection", Toast.LENGTH_LONG).show();
                        }
                    }) {
                        protected Map getParams() {
                            return new HashMap();
                        }
                    });
                } else {
                    noInternet();
                }
            }
        });

        binding.imgDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                crewList.clear();
                db.getCrewDao().deleteAll();
                adapter.notifyDataSetChanged();
                Toast.makeText(MainActivity.this,
                        "Database cleared Successfully...",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void noInternet(){
        new AlertDialog.Builder(MainActivity.this, R.style.CustomAlertDialog)
                .setTitle("Offline")
                .setIcon(R.drawable.ic_no_internet)
                .setMessage("Your network is unavailable.\nCheck your data or wifi connection.")
                .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (new ConnectionManager().checkConnecctivity(MainActivity.this)) {
                            progressDialog.show();
                            requestQueue.add(new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                                public void onResponse(String str) {
                                    try {
                                        db.getCrewDao().deleteAll();

                                        JSONArray jsonArray = new JSONArray(str);
                                        Log.e("output...obj..", "\n" + jsonArray);

                                        for (int i = 0; i < jsonArray.length(); i++) {

                                            //Declaring a json object corresponding to every pdf object in our json Array
                                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                                            String name = jsonObject.getString("name");
                                            String id = jsonObject.getString("id");
                                            String image = jsonObject.getString("image");
                                            String status = jsonObject.getString("status");
                                            String wikipedia = jsonObject.getString("wikipedia");
                                            String agency = jsonObject.getString("agency");

                                            CrewEntity model = new CrewEntity(id, name, agency, image, wikipedia, status);

                                            db.getCrewDao().insertCrew(model);
                                        }

                                        loadCrewList();
                                        progressDialog.dismiss();
                                        Toast.makeText(MainActivity.this,
                                                "Retry Successful...",
                                                Toast.LENGTH_SHORT)
                                                .show();

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }, new Response.ErrorListener() {
                                public void onErrorResponse(VolleyError volleyError) {
                                    progressDialog.dismiss();
                                    Toast.makeText(MainActivity.this, "Check Internet Connection", Toast.LENGTH_LONG).show();
                                }
                            }) {
                                protected Map getParams() {
                                    return new HashMap();
                                }
                            });

                        } else {
                            noInternet();
                        }
                    }
                }).setNegativeButton("Exit",
                (dialog, which) -> {
                    dialog.dismiss();
                    finish();
                })
                .show();
    }

    private void loadCrewList(){
        crewList.clear();
        db = CrewDatabase.getDatabase(this.getApplicationContext());
        crewList = db.getCrewDao().getAllCrew();
        adapter = new CrewRecyclerAdapter(crewList, this);
        binding.crewRecycler.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }
}