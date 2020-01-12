package ec.edu.espol.cvr.paipayapp;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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

import ec.edu.espol.cvr.paipayapp.utils.Invariante;

/**
 * Esta activity es para manejar consultas del estado de un pedido.
 * @author: Mauricio Leiton Lázaro(mdleiton)
 * @version: 1.0
 */
public class ConsultarEstadoPedido extends Activity {
    private SharedPreferences sharedpreferences;
    private EditText codigo;
    private TextView infoPedido, estadoPedido, repartidorPedido;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consultar_estado_pedido);
        sharedpreferences = getSharedPreferences(Invariante.MyPREFERENCES, this.MODE_PRIVATE);
        codigo = (EditText) findViewById(R.id.userInputPedidoConsultar);
        infoPedido = (TextView) findViewById(R.id.pedidoInfoCodigo);
        estadoPedido = (TextView) findViewById(R.id.pedidoInfoEstado);
        repartidorPedido = (TextView) findViewById(R.id.pedidoInfoRepartidor);
    }

    /**
     * Método que se ejecuta cuando se da tap en el boton de buscar.
     * @param view
     */
    public void consultar_pedido(View view){
        String ip = sharedpreferences.getString("ip", "");
        int port = sharedpreferences.getInt("port", 0);
        boolean test_mode = sharedpreferences.getBoolean("test_mode",true);
        if(test_mode){
            Toast.makeText(this, Invariante.PRUEBA, Toast.LENGTH_SHORT).show();
            infoPedido.setText("Pedido #1");
            estadoPedido.setText("Estado : " + Invariante.get_estado_str(0));
            repartidorPedido.setText("Repartidor: USER 1");
        }else{
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            String server = Invariante.get_server(ip, port);
            String url = server+ "/api/v1/purchases/status/" + codigo.getText().toString();
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                    (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                System.out.println(response.toString());
                                Toast.makeText(ConsultarEstadoPedido.this, R.string.pedidoEncontrado, Toast.LENGTH_SHORT).show();
                                infoPedido.setText("Pedido #" +  String.valueOf(response.getInt("id")));
                                estadoPedido.setText("Estado : " + Invariante.get_estado_str(response.getInt("status"))); //cambiar por string
                                //repartidorPedido.setText(R.string.RepartidorPedido + pedidoConsultado.getRepartidor());
                            } catch (JSONException e) {
                                e.printStackTrace();
                                clean(Invariante.ERROR_LOGIN_RED);
                                Toast.makeText(ConsultarEstadoPedido.this, Invariante.ERROR_LOGIN_RED, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            try {
                                int code = error.networkResponse.statusCode;
                                JSONObject json = new JSONObject(new String(error.networkResponse.data));
                                String message = "Error " + String.valueOf(code) + json.getString("message");
                                Toast.makeText(ConsultarEstadoPedido.this, message, Toast.LENGTH_SHORT).show();
                                clean("Error " + String.valueOf(code) + "\n" +  json.getString("message"));
                            }catch (Exception e) {
                                e.printStackTrace();
                                clean(Invariante.ERROR_LOGIN_RED_ACCESO);
                                Toast.makeText(ConsultarEstadoPedido.this, Invariante.ERROR_LOGIN_RED_ACCESO, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
            requestQueue.add(jsonObjectRequest);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, MenuAdmin.class));
        finish();
    }

    /**
     * Método para borrar los resultados de consultas previas.
     * @param message cadena de texto vacía.
     */
    public void clean(String message){
        infoPedido.setText(message);
        estadoPedido.setText("");
        repartidorPedido.setText("");
    }
}
