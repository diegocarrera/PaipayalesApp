package ec.edu.espol.cvr.paipayapp;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import ec.edu.espol.cvr.paipayapp.utils.Invariante;

public class ConfigPuntoRepartoAdmin extends Activity implements AdapterView.OnItemSelectedListener {
    private Spinner spinner;
    private SharedPreferences sharedpreferences;
    List<String> puntos_reparto = new ArrayList<String>();
    private boolean test_mode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config_punto_reparto_admin);
        sharedpreferences = getSharedPreferences(Invariante.MyPREFERENCES, this.MODE_PRIVATE);
        spinner = (Spinner) findViewById(R.id.spinnerPuntoReparto);
        spinner.setOnItemSelectedListener(this);
        test_mode = sharedpreferences.getBoolean("test_mode",true);
        if(test_mode){
            Toast.makeText(this, Invariante.PRUEBA, Toast.LENGTH_SHORT).show();
            puntos_reparto.add("Principal, Paipay");
            puntos_reparto.add("Norte");
            set_option();
        }else{
            update_option();
        }
        String punto_reparto = sharedpreferences.getString(Invariante.PUNTO_REPARTO,"");
        if(punto_reparto != ""){
            spinner.setSelection(puntos_reparto.indexOf(punto_reparto));
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String item = parent.getItemAtPosition(position).toString();
        Toast.makeText(parent.getContext(), "Punto de reparto seleccionado: " + item, Toast.LENGTH_LONG).show();
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(Invariante.PUNTO_REPARTO, item);
        editor.apply();
    }
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
    }

    public void set_option(){
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(ConfigPuntoRepartoAdmin.this, R.layout.spinner, puntos_reparto);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);
    }

    public void update_option(){
        String ip = sharedpreferences.getString("ip","");
        int port = sharedpreferences.getInt("port",0);

        if(ip!= "" && port != 0){
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            String server = Invariante.get_server(ip, port);
            JsonArrayRequest jsonObjectRequest = new JsonArrayRequest
                    (Request.Method.GET,server + "/api/v1/delivery-centers/" , null, new Response.Listener<JSONArray>() {

                        @Override
                        public void onResponse(JSONArray response) {
                            try {
                                for (int i = 0; i < response.length(); i++) {
                                    JSONObject centro = response.getJSONObject(i);
                                    String name = String.valueOf(centro.getInt("id")) + "-" + centro.getString("name");
                                    puntos_reparto.add(name);
                                }
                                set_option();
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(ConfigPuntoRepartoAdmin.this, Invariante.ERROR_LOGIN_RED, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            try {
                                int code = error.networkResponse.statusCode;
                                JSONObject json = new JSONObject(new String(error.networkResponse.data));
                                String message = "Error " + String.valueOf(code) + json.getString("message");
                                Toast.makeText(ConfigPuntoRepartoAdmin.this, message, Toast.LENGTH_SHORT).show();
                            }catch (Exception e) {
                                e.printStackTrace();
                                Toast.makeText(ConfigPuntoRepartoAdmin.this, Invariante.ERROR_LOGIN_RED_ACCESO, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
            requestQueue.add(jsonObjectRequest);
        }else{
            Toast.makeText(this, Invariante.CONF_ERROR_1, Toast.LENGTH_LONG).show();
        }
    }

    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, MenuAdmin.class));
        finish();
    }
}
