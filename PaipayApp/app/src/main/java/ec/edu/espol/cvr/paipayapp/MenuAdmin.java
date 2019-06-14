package ec.edu.espol.cvr.paipayapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import ec.edu.espol.cvr.paipayapp.utils.Invariante;

public class MenuAdmin extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
    }


    public void armar_pedidos(View view){
        Intent intent = new Intent(this, ArmarPedidos.class);
        startActivity(intent);
        finish();
    }

    public void consultar_pedido(View view){
        Intent intent = new Intent(this, ConsultarEstadoPedido.class);
        startActivity(intent);
        finish();
    }

    public void opciones_bodega(View view){
        Intent intent = new Intent(this, ConfigPuntoRepartoAdmin.class);
        startActivity(intent);
        finish();
    }

    public void registro(View view){
        Intent intent = new Intent(this, Registro.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        SharedPreferences sharedpreferences = getSharedPreferences(Invariante.MyPREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.clear(); // o recibir y pasar el user, para no iniciar sesion otra vez?
        editor.apply();
        startActivity(new Intent(this, Login.class));
        finish();
    }

}
