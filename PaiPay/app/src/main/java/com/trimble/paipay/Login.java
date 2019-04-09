package com.trimble.paipay;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.trimble.paipay.utils.Invariante;
import com.trimble.paipay.utils.RequestApi;

public class Login extends Activity {
    private AlphaAnimation buttonClick = new AlphaAnimation(1F, 0.4F);
    private String user, password;
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
        view.startAnimation(buttonClick);
        user = ((TextView) findViewById(R.id.user)).getText().toString();
        password = ((TextView) findViewById(R.id.password)).getText().toString();
        if (!user.contains("@")){
            Toast.makeText(this, "Correo no válido.", Toast.LENGTH_LONG).show();
            return;
        }
        if (!user.isEmpty() && !password.isEmpty()) {
            RequestApi.set_network(ip, port);
            boolean auth = RequestApi.login(user, password);
            if (test_mode){
                Toast.makeText(this, "Modo prueba activado.", Toast.LENGTH_LONG).show();
            }
            if (!auth && !test_mode) {   //sacar test
                Toast toast = Toast.makeText(this, "Usuario y/o contraseña incorrecta", Toast.LENGTH_SHORT);
                toast.show();
            } else {
                SharedPreferences sharedpreferences = getSharedPreferences(Invariante.MyPREFERENCES, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putString("user", user.toLowerCase());
                editor.putString("ip", ip);
                editor.putInt("port", port);

                editor.putBoolean("test_mode", test_mode); // sacar

                editor.apply();
                Intent intent = new Intent(Login.this, Menu.class);
                startActivity(intent);
                finish();
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
                    .setPositiveButton("Send", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogBox, int id) {
                        SharedPreferences sharedpreferences = getSharedPreferences(Invariante.MyPREFERENCES, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedpreferences.edit();
                        editor.putString("ip", userInputIP.getText().toString().toLowerCase());
                        editor.putInt("port", Integer.parseInt(userInputPort.getText().toString()));
                        editor.apply();
                        }
                    })
                    .setNegativeButton("Cancel",
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
