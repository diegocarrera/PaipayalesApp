package ec.edu.espol.cvr.paipayapp;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
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

import ec.edu.espol.cvr.paipayapp.adapters.PedidoAdapter;
import ec.edu.espol.cvr.paipayapp.model.Pedido;
import ec.edu.espol.cvr.paipayapp.utils.Invariante;

import java.nio.file.attribute.FileTime;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ArmarPedidos extends Activity {
    private ArrayList<Pedido> pedidos = new ArrayList<Pedido>();
    private PedidoAdapter pedidoadapter;
    private  SharedPreferences sharedpreferences;
    private ListView listview_pedido;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_armar_pedidos);

        listview_pedido = (ListView) findViewById(R.id.listapedidos);
        listview_pedido.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
            Intent intent = new Intent(ArmarPedidos.this, InfoPedidoAdmin.class);
            Pedido tmp_pedido = pedidos.get(position);
            pedidos.clear();
            pedidoadapter.notifyDataSetChanged();
            intent.putExtra("id_pedido", tmp_pedido.getCodigo());
            DateFormat dateFormat = new SimpleDateFormat(Invariante.format_date);
            intent.putExtra("fecha",dateFormat.format(tmp_pedido.getFecha()));
            intent.putExtra("detalle",tmp_pedido.getDetallePedidos());
            startActivity(intent);
            finish();
            }
        });
        sharedpreferences = getSharedPreferences(Invariante.MyPREFERENCES, this.MODE_PRIVATE);
        boolean test_mode = sharedpreferences.getBoolean("test_mode",true);

        if (test_mode){
            for (int i = 0; i < 3; i++) {
                try {
                    Date fecha = new SimpleDateFormat(Invariante.format_date).parse(i+"/03/2019");
                    pedidos.add(new Pedido(fecha, i+1));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

            }
            pedidoadapter = new PedidoAdapter(this, pedidos);
            pedidoadapter.notifyDataSetChanged();
            listview_pedido.setAdapter(pedidoadapter);
        }else{
            update_list();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, MenuAdmin.class));
        finish();
    }

    void update_list(){
        try {
            String ip = sharedpreferences.getString("ip","");
            int port = sharedpreferences.getInt("port",0);
            String punto_reparto = sharedpreferences.getString("punto_reparto","");
            if(port != 0 && ip != ""){
                if (punto_reparto == ""){
                    Toast.makeText(this, Invariante.ERROR_PUNTO_REPARTO, Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(ArmarPedidos.this, ConfigPuntoRepartoAdmin.class);
                    startActivity(intent);
                    finish();
                }
                RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                String server = Invariante.get_server(ip, port);
                String url = server + "/api/v1/purchases/query?status=0&idCenter=" + punto_reparto.split("-")[0];
                JsonArrayRequest jsonObjectRequest = new JsonArrayRequest
                        (Request.Method.GET, url, null, new Response.Listener<JSONArray>() {

                            @Override
                            public void onResponse(JSONArray response) {
                                try {
                                    for (int i = 0; i < response.length(); i++) {
                                        JSONObject pedido = response.getJSONObject(i);
                                        String filter = pedido.getString("dateCreated");
                                        Date fecha = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS").parse(filter);
                                        pedidos.add(new Pedido(fecha, pedido.getInt("id")));
                                    }
                                    pedidoadapter = new PedidoAdapter(ArmarPedidos.this, pedidos);
                                    listview_pedido.setAdapter(pedidoadapter);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                    Toast.makeText(ArmarPedidos.this, "Error al procesar respuesta", Toast.LENGTH_SHORT).show();
                                }catch (JSONException e) {
                                    e.printStackTrace();
                                    Toast.makeText(ArmarPedidos.this, Invariante.ERROR_LOGIN_RED, Toast.LENGTH_SHORT).show();
                                }
                            }
                        }, new Response.ErrorListener() {

                            @Override
                            public void onErrorResponse(VolleyError error) {
                                try {
                                    int code = error.networkResponse.statusCode;
                                    JSONObject json = new JSONObject(new String(error.networkResponse.data));
                                    String message = "Error " + String.valueOf(code) + json.getString("message");
                                    Toast.makeText(ArmarPedidos.this, message, Toast.LENGTH_SHORT).show();
                                }catch (Exception e) {
                                    e.printStackTrace();
                                    Toast.makeText(ArmarPedidos.this, Invariante.ERROR_LOGIN_RED_ACCESO, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                requestQueue.add(jsonObjectRequest);
            }else{
                Toast.makeText(this, Invariante.CONF_ERROR_1, Toast.LENGTH_LONG).show();
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}


