package ec.edu.espol.cvr.paipayapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;

import ec.edu.espol.cvr.paipayapp.utils.Invariante;

public class Menu extends Activity {
    private AlphaAnimation buttonClick = new AlphaAnimation(1F, 0.4F);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
    }

    public void armar_pedidos(View view){
        view.startAnimation(buttonClick);
        Intent intent = new Intent(this, ArmarPedidos.class);
        startActivity(intent);
        finish();
    }

    public void consultar_pedido(View view){

    }

    public void opciones_bodega(View view){

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        SharedPreferences sharedpreferences = getSharedPreferences(Invariante.MyPREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        // recibir y pasar el user, para no iniciar sesion otra vez
        editor.clear(); // estoy es para que inicie sesion de nuevo y se borre lo anterior registrado
        editor.apply();
        startActivity(new Intent(this, Login.class));
        finish();
    }
}
