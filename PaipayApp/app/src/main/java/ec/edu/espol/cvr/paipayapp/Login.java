package ec.edu.espol.cvr.paipayapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import ec.edu.espol.cvr.paipayapp.model.User;



import ec.edu.espol.cvr.paipayapp.utils.Invariante;

public class Login extends Activity {

    private int port = 5000;
    private String ip = "192.168.0.8";//"172.19.12.203"; //192.168.0.8 maria belen //172.19.12.203
    private boolean test_mode = false;  //sacar test
    private SharedPreferences sharedpreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        sharedpreferences = getSharedPreferences(Invariante.MyPREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putBoolean("test_mode", test_mode);  //sacar
        editor.putString("ip", ip);
        editor.putInt("port", port);
        editor.apply();

        return super.onCreateOptionsMenu(menu);
    }

    public void verificarUsuario(View view) {
        String email = ((TextView) findViewById(R.id.user)).getText().toString();
        String password = ((TextView) findViewById(R.id.password)).getText().toString();
        if (!email.contains("@")){
            Toast.makeText(this, Invariante.ERROR_CORREO, Toast.LENGTH_SHORT).show();
            return;
        }
        if (!email.isEmpty() && !password.isEmpty()) {
            //email = "beleng.c@hotmail.com";
            //password = "adminadmin";
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putString("email", email.toLowerCase());
            editor.apply();

            if (test_mode){
                Toast.makeText(this, Invariante.PRUEBA, Toast.LENGTH_SHORT).show();
                String rol = Invariante.USUARIO_REPARTIDOR;
                get_menu(rol);
            }else{
                api_login(email, password);
            }
        } else {
            Toast.makeText(this, Invariante.ERROR_LOGIN_1, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    public void get_menu(String rol){
        Intent intent;
        if (rol.equals(Invariante.USUARIO_ADMIN)){
            intent = new Intent(Login.this, MenuAdmin.class);
        }else if (rol.equals(Invariante.USUARIO_REPARTIDOR)){
            intent = new Intent(Login.this, ListarPedidosRepartidor.class);
        }else{
            Toast.makeText(this, Invariante.ERROR_LOGIN_ROL, Toast.LENGTH_SHORT).show();
            return;
        }
        startActivity(intent);
        finish();
    }

    public void api_login(String email, String password){
        JSONObject parameters = new JSONObject();
        try {
            parameters.put("email", email);
            parameters.put("password", password);
            System.out.println(email);
            System.out.println(password);
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            final String server = Invariante.get_server(ip, port);

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                    (Request.Method.POST,server+ "/api/v1/auth/login/" , parameters, new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            try {

                                System.out.println(response.toString());
                                String rol = response.getString("role");
                                System.out.println("ROOOOOL"+ rol);
                                //String rol = Invariante.USUARIO_ADMIN;
                                String token = response.getString(Invariante.TOKEN);
                                SharedPreferences.Editor editor = sharedpreferences.edit();
                                editor.putString(Invariante.TOKEN, token);
                                editor.apply();
                                get_menu(rol);
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(Login.this, Invariante.ERROR_LOGIN_RED, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            try {
                                int code = error.networkResponse.statusCode;
                                JSONObject json = new JSONObject(new String(error.networkResponse.data));
                                String message = "Error " + String.valueOf(code) + json.getString("message");
                                Toast.makeText(Login.this, message, Toast.LENGTH_SHORT).show();
                            }catch (Exception e) {
                                e.printStackTrace();
                                Toast.makeText(Login.this, Invariante.ERROR_LOGIN_RED_ACCESO, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
            requestQueue.add(jsonObjectRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.refresh_conf_server:
                LayoutInflater layoutInflaterAndroid = LayoutInflater.from(Login.this);
                View mView = layoutInflaterAndroid.inflate(R.layout.config_server, null);
                AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(Login.this);
                alertDialogBuilderUserInput.setView(mView);
                final EditText userInputIP = (EditText) mView.findViewById(R.id.userInputIp);
                final EditText userInputPort = (EditText) mView.findViewById(R.id.userInputPort);

                userInputIP.setText(ip);
                userInputPort.setText(String.valueOf(port));

                alertDialogBuilderUserInput
                        .setCancelable(false)
                        .setPositiveButton("Actualizar", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogBox, int id) {
                                String port = userInputPort.getText().toString();
                                String ip = userInputIP.getText().toString();
                                if (!port.isEmpty() && !ip.isEmpty()){
                                    try{
                                        int port_int = Integer.parseInt(port);
                                        SharedPreferences.Editor editor = sharedpreferences.edit();
                                        editor.putString("ip", ip.toLowerCase());
                                        editor.putInt("port", port_int);
                                        editor.apply();
                                        Toast.makeText(Login.this, Invariante.CONF_ACTUALIZADO, Toast.LENGTH_LONG).show();
                                    }catch (Exception e){
                                        Toast.makeText(Login.this, Invariante.CONF_ERROR_1, Toast.LENGTH_LONG).show();
                                    }
                                }else{
                                    Toast.makeText(Login.this, Invariante.CONF_ERROR_2, Toast.LENGTH_LONG).show();
                                }
                            }
                        })
                        .setNegativeButton("Cancelar",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialogBox, int id) {
                                        dialogBox.cancel();
                                    }
                                });
                AlertDialog alertDialogAndroid = alertDialogBuilderUserInput.create();
                alertDialogAndroid.show();
                return true;
            default:
                return true;
        }
    }


}
