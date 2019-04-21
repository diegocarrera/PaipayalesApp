package ec.edu.espol.cvr.paipayapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import ec.edu.espol.cvr.paipayapp.utils.Invariante;
import ec.edu.espol.cvr.paipayapp.utils.RequestApi;

public class Login extends Activity {
    private int port = 9090;
    private String ip = "192.168.0.9";
    private boolean test_mode = true;  //sacar test

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public void verificarUsuario(View view) {
        String email = ((TextView) findViewById(R.id.user)).getText().toString();
        String password = ((TextView) findViewById(R.id.password)).getText().toString();
        if (!email.contains("@")){
            Toast.makeText(this, "Correo no válido.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!email.isEmpty() && !password.isEmpty()) {
            RequestApi.set_network(ip, port);
            JSONObject respuesta = RequestApi.login(email, password);
            try {
                String rol;
                if (test_mode){
                    Toast.makeText(this, "Modo prueba activado.", Toast.LENGTH_SHORT).show();
                    rol = Invariante.USUARIO_ADMIN;
                }else{
                    rol = respuesta.getString("rol");
                    String token = respuesta.getString("token");
                }
                if (rol == null && !test_mode) {   //sacar test
                    Toast toast = Toast.makeText(this, "Usuario y/o contraseña incorrecta", Toast.LENGTH_SHORT);
                    toast.show();
                } else {
                    SharedPreferences sharedpreferences = getSharedPreferences(Invariante.MyPREFERENCES, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putString("email", email.toLowerCase());
                    editor.putString("ip", ip);
                    editor.putInt("port", port);

                    editor.putBoolean("test_mode", test_mode); // sacar

                    editor.apply();
                    Intent intent;

                    if (rol == Invariante.USUARIO_ADMIN){
                        intent = new Intent(Login.this, MenuAdmin.class);
                    }else if (rol == Invariante.USUARIO_REPARTIDOR){
                        intent = new Intent(Login.this, MenuAdmin.class); // cambiar de activity
                    }else{
                        Toast toast = Toast.makeText(this, "Rol no disponible.", Toast.LENGTH_SHORT);
                        return;
                    }
                    startActivity(intent);
                    finish();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(this, "Usuario y contraseña no pueden quedar en blanco", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        finish();
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
                alertDialogBuilderUserInput
                        .setCancelable(false)
                        .setPositiveButton("Actualizar", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogBox, int id) {
                                String port = userInputPort.getText().toString();
                                String ip = userInputIP.getText().toString();
                                if (!port.isEmpty() && !ip.isEmpty()){
                                    try{
                                        int port_int = Integer.parseInt(port);
                                        SharedPreferences sharedpreferences = getSharedPreferences(Invariante.MyPREFERENCES, Context.MODE_PRIVATE);
                                        SharedPreferences.Editor editor = sharedpreferences.edit();
                                        editor.putString("ip", ip.toLowerCase());
                                        editor.putInt("port", port_int);
                                        editor.apply();
                                        Toast.makeText(Login.this, "IP y puerto actualizados exitosamente. ", Toast.LENGTH_LONG).show();
                                    }catch (Exception e){
                                        Toast.makeText(Login.this, "IP y/o puerto del servidor no configurado. ", Toast.LENGTH_LONG).show();
                                    }
                                }else{
                                    Toast.makeText(Login.this, "IP y/o puerto vacios. ", Toast.LENGTH_LONG).show();
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
