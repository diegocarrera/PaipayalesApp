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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import ec.edu.espol.cvr.paipayapp.utils.Invariante;
import ec.edu.espol.cvr.paipayapp.utils.RequestApi;

public class ConfigPuntoRepartoAdmin extends Activity implements AdapterView.OnItemSelectedListener {
    private Spinner spinner;
    private SharedPreferences sharedpreferences;
    List<String> puntos_reparto = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config_punto_reparto_admin);
        sharedpreferences = getSharedPreferences(Invariante.MyPREFERENCES, this.MODE_PRIVATE);
        spinner = (Spinner) findViewById(R.id.spinnerPuntoReparto);
        spinner.setOnItemSelectedListener(this);
        update_option();
        String punto_reparto = sharedpreferences.getString("punto_reparto","");
        if(punto_reparto != ""){
            spinner.setSelection(puntos_reparto.indexOf(punto_reparto));
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String item = parent.getItemAtPosition(position).toString();
        Toast.makeText(parent.getContext(), "Punto de reparto seleccionado: " + item, Toast.LENGTH_LONG).show();
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString("punto_reparto", item);
        editor.apply();
    }
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
    }

    public void update_option(){
        String ip = sharedpreferences.getString("ip","");
        int port = sharedpreferences.getInt("port",0);
        boolean test_mode = sharedpreferences.getBoolean("test_mode",true);
        if(ip!= "" && port != 0){
            RequestApi.set_network(ip, port);
            JSONObject response = new JSONObject();
            try {
                response = RequestApi.request("/api/v1/puntos_reparto/", "GET", null);
                if(response.getInt("response_code") == 200){
                    JSONObject puntos = new JSONObject(response.getString("data"));
                    JSONArray jsonarr = puntos.getJSONArray("puntos_reparto");
                    for (int i = 0; i < jsonarr.length(); i++) {
                        puntos_reparto.add( jsonarr.getString(i));
                    }
                }else{
                    Toast.makeText(this, response.getString("error"), Toast.LENGTH_SHORT).show();
                    if (test_mode){
                        puntos_reparto.add("Principal, Paipay");
                        puntos_reparto.add("Norte");
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else{
            Toast.makeText(this, "IP y/o puerto del servidor no configurado. ", Toast.LENGTH_LONG).show();
        }

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, R.layout.spinner, puntos_reparto);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);
    }

    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, MenuAdmin.class));
        finish();
    }
}
