package ec.edu.espol.cvr.paipayapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
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

import java.util.ArrayList;
import java.util.List;

import ec.edu.espol.cvr.paipayapp.utils.Invariante;

public class Registro extends Activity implements AdapterView.OnItemSelectedListener{
    private SharedPreferences sharedpreferences;
    private String ip;
    private int port;
    private boolean test_mode;

    private Spinner spinnerZone;
    private Spinner spinnerRol;
    private EditText name, email, password, phoneNumber, address;
    private List<String> user_zone = new ArrayList<String>();
    private List<String> user_rol = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        sharedpreferences = getSharedPreferences(Invariante.MyPREFERENCES, this.MODE_PRIVATE);
        ip = sharedpreferences.getString("ip", "");
        port = sharedpreferences.getInt("port", 0);
        test_mode = sharedpreferences.getBoolean("test_mode", true);

        spinnerZone = (Spinner) findViewById(R.id.spinneruserZone);
        spinnerZone.setOnItemSelectedListener(this);

        spinnerRol = (Spinner) findViewById(R.id.spinnerrol);
        spinnerRol.setOnItemSelectedListener(this);

        name = (EditText) findViewById(R.id.userInputname);
        email = (EditText) findViewById(R.id.userInputemail);
        password = (EditText) findViewById(R.id.userInputpassword);
        phoneNumber = (EditText) findViewById(R.id.userInputphoneNumber);
        address = (EditText) findViewById(R.id.userInputaddress);


        //phoneNumber.setText("+59342820236");

        user_zone.add("Seleccione una zona");
        update_user_zone();
        user_rol.add("Seleccione un rol");
        update_rol();
    }

    /* función para retroceder al menú del administrador*/
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, MenuAdmin.class));
        finish();
    }

    /*-----------------------------handlers spinner----------------------------------------*/
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String item = parent.getItemAtPosition(position).toString();
        //Toast.makeText(parent.getContext(), "Zona seleccionado: " + item, Toast.LENGTH_SHORT).show();
    }

    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
    }

    /* realizar una solicitud de tipo post al api para registrar la información de un usuario nuevo */
    public void registrar(View view){
        JSONObject parameters = new JSONObject();
        try {
            parameters.put("name", name.getText().toString());
            parameters.put("email", email.getText().toString());
            parameters.put("password", password.getText().toString());
            parameters.put("phoneNumber", phoneNumber.getText().toString());
            parameters.put("address", address.getText().toString());

            int index_rol = user_rol.indexOf(spinnerRol.getSelectedItem().toString());
            int index_zone = user_zone.indexOf(spinnerZone.getSelectedItem().toString());
            if(index_rol < 0){
                Toast.makeText(Registro.this, "No ha seleccionado un rol", Toast.LENGTH_SHORT).show();
                return;
            }
            if(index_zone < 0){
                Toast.makeText(Registro.this, "No ha seleccionado una zona", Toast.LENGTH_SHORT).show();
                return;
            }
            parameters.put("userZone", spinnerZone.getSelectedItem().toString());
            parameters.put("role", index_rol - 1);

            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            String server = Invariante.get_server(ip, port);
            String url = server + "/api/v1/auth/register/";
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                    (Request.Method.POST, url, parameters, new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                System.out.println(response.toString());
                                Toast.makeText(Registro.this, Invariante.REGISTRO_OK, Toast.LENGTH_LONG).show();
                                menu();
                            } catch (Exception e) {
                                e.printStackTrace();
                                Toast.makeText(Registro.this, Invariante.ERROR_LOGIN_RED, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            try {
                                int code = error.networkResponse.statusCode;
                                JSONObject json = new JSONObject(new String(error.networkResponse.data));
                                String message = "Error " + String.valueOf(code) + json.getString("message");
                                Toast.makeText(Registro.this, message, Toast.LENGTH_SHORT).show();
                            }catch (Exception e) {
                                e.printStackTrace();
                                Toast.makeText(Registro.this, Invariante.ERROR_LOGIN_RED_ACCESO, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
            requestQueue.add(jsonObjectRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void menu(){
        startActivity(new Intent(this, MenuAdmin.class));
        finish();
    }

    /* consume el api para obtener una la lista de zonas de usuarios registradas  */
    public void update_user_zone(){
        if(ip!= "" && port != 0){
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            String server = Invariante.get_server(ip, port) + "/api/v1/users/user-zones/";
            JsonArrayRequest jsonObjectRequest = new JsonArrayRequest
                    (Request.Method.GET, server, null, new Response.Listener<JSONArray>() {

                        @Override
                        public void onResponse(JSONArray response) {
                            try {
                                for (int i = 0; i < response.length(); i++) {
                                    JSONObject zone = response.getJSONObject(i);
                                    user_zone.add(zone.getString("name"));
                                }
                                set_option();
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(Registro.this, Invariante.ERROR_LOGIN_RED, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            try {
                                int code = error.networkResponse.statusCode;
                                JSONObject json = new JSONObject(new String(error.networkResponse.data));
                                String message = "Error " + String.valueOf(code) + json.getString("message");
                                Toast.makeText(Registro.this, message, Toast.LENGTH_SHORT).show();
                            }catch (Exception e) {
                                e.printStackTrace();
                                Toast.makeText(Registro.this, Invariante.ERROR_LOGIN_RED_ACCESO, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
            requestQueue.add(jsonObjectRequest);
        }else{
            Toast.makeText(this, Invariante.CONF_ERROR_1, Toast.LENGTH_LONG).show();
        }
    }

    /* función que define los roles que se pueden registrar en el sistema */
    public void update_rol(){
        user_rol.add("Administrador");
        user_rol.add("Cliente");
        user_rol.add("Repartidor");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(Registro.this, R.layout.spinner, user_rol);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRol.setAdapter(dataAdapter);
    }

    /*  configuración spinner de las zonas de usuario */
    public void set_option(){
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(Registro.this, R.layout.spinner, user_zone);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerZone.setAdapter(dataAdapter);
    }

}
