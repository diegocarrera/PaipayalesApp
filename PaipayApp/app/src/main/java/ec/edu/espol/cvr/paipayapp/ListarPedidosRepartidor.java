package ec.edu.espol.cvr.paipayapp;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;

import org.json.JSONObject;

import ec.edu.espol.cvr.paipayapp.adapters.PedidoAdapter;
import ec.edu.espol.cvr.paipayapp.model.Pedido;
import ec.edu.espol.cvr.paipayapp.utils.Invariante;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class ListarPedidosRepartidor extends Activity {
    /*
        Actividad que muestra la lista de pedidos asociadas a un repartidor.
        Si un repartidor presiona sobre un pedido, se debe llamar a la actividad InfoPedido
    */

    private ArrayList<Pedido> pedidos = new ArrayList<Pedido>();
    private PedidoAdapter pedidoadapter;
    private ListView listview_pedido;
    private SharedPreferences sharedpreferences;
    private int port;
    private String ip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_armar_pedidos);

        sharedpreferences = getSharedPreferences(Invariante.MyPREFERENCES, this.MODE_PRIVATE);
        ip = sharedpreferences.getString("ip", "");
        port = sharedpreferences.getInt("port", 0);

        listview_pedido = (ListView) findViewById(R.id.listapedidos);
        listview_pedido.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Intent intent = new Intent(ListarPedidosRepartidor.this, InfoPedido.class);
                Pedido tmp_pedido = pedidos.get(position);
                pedidos.clear();
                pedidoadapter.notifyDataSetChanged();
                intent.putExtra("id_pedido", tmp_pedido.getCodigo());
                //DateFormat dateFormat = new SimpleDateFormat(Invariante.format_date);
                //intent.putExtra("fecha", dateFormat.format(tmp_pedido.getFecha()));
                intent.putExtra("direccion",tmp_pedido.getDireccion());
                startActivity(intent);
                finish();
            }
        });

        boolean test_mode = sharedpreferences.getBoolean("test_mode", true);
        if (test_mode) {
            for (int i = 0; i < 3; i++) {
                pedidos.add(new Pedido("23/23/2019", i + 1));
            }
            pedidoadapter = new PedidoAdapter(this, pedidos, true);
            pedidoadapter.notifyDataSetChanged();
            listview_pedido.setAdapter(pedidoadapter);
        } else {
            update_list();
        }
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    void update_list(){
        /*
            Funcion que actualiza la lista de los pedidos.
            Hace un requerimiento al servidor usando Pedido.get_pedidos_por_repartidor()
             para traerse la lista de pedidos relacionados a un repartidor
        */
        final String user_token = sharedpreferences.getString(Invariante.TOKEN,"");

        try {
            if(port != 0 && ip != ""){

                //Armo el request
                RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                String server = Invariante.get_server(ip, port);
                String new_path = server + "/tracks/api/v1/purchasesxworker";

                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                        (Request.Method.GET, new_path, null, new Response.Listener<JSONObject>() {

                            @Override
                            public void onResponse(JSONObject response) {

                                try {
                                    System.out.println(response);
                                    JSONArray pedidos_response = response.getJSONArray("data");

                                    for (int i = 0; i < pedidos_response.length(); i++) {

                                        JSONObject pedido = pedidos_response.getJSONObject(i);
                                        System.out.println(pedido);
                                        /*
                                        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
                                        Date fecha = formatter.parse(pedido.getString("dateCreated").replaceAll("Z$", "+0000"));
                                        System.out.println(fecha);
                                        */
                                        pedidos.add(new Pedido(pedido.getInt("id"),pedido.getString("user__address")));
                                    }
                                    pedidoadapter = new PedidoAdapter(ListarPedidosRepartidor.this, pedidos, true);
                                    listview_pedido.setAdapter(pedidoadapter);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    Toast.makeText(ListarPedidosRepartidor.this, Invariante.ERROR_LOGIN_RED, Toast.LENGTH_SHORT).show();
                                }
                            }
                        }, new Response.ErrorListener() {

                            @Override
                            public void onErrorResponse(VolleyError error) {
                                try {
                                    int code = error.networkResponse.statusCode;
                                    JSONObject json = new JSONObject(new String(error.networkResponse.data));
                                    String message = "Error " + String.valueOf(code) + json.getString("message");
                                    Toast.makeText(ListarPedidosRepartidor.this, message, Toast.LENGTH_SHORT).show();
                                }catch (Exception e) {
                                    e.printStackTrace();
                                    Toast.makeText(ListarPedidosRepartidor.this, Invariante.ERROR_LOGIN_RED_ACCESO, Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                {

                    /**
                     * Passing some request headers
                     */
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        HashMap<String, String> headers = new HashMap<String, String>();
                        //headers.put("Content-Type", "application/json");
                        headers.put("Authorization", user_token);
                        return headers;
                    }
                };
                requestQueue.add(jsonObjectRequest);

            }else{
                Toast.makeText(this, "IP y/o puerto del servidor no configurado. ", Toast.LENGTH_LONG).show();
            }
            //pedidoadapter.notifyDataSetChanged();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}


