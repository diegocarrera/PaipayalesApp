package ec.edu.espol.cvr.paipayapp;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Esta activity es para manejar la lista de pedidos asignados a un administrador.
 * @author: Mauricio Leiton Lázaro(mdleiton)
 * @version: 1.0
 */
public class ArmarPedidos extends Activity implements AdapterView.OnItemSelectedListener  {
    private Spinner spinner;
    private ArrayList<Pedido> pedidos = new ArrayList<Pedido>();
    private PedidoAdapter pedidoadapter;
    private  SharedPreferences sharedpreferences;
    private SwipeRefreshLayout refresh;
    private ListView listview_pedido;
    private boolean test_mode;
    TextView pedidosText;

    private String punto_reparto = "";
    List<String> puntos_reparto = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_armar_pedidos);
        refresh = (SwipeRefreshLayout) findViewById(R.id.refresh);
        pedidosText = (TextView) findViewById(R.id.textView);

        listview_pedido = (ListView) findViewById(R.id.listapedidos);
        listview_pedido.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
            Intent intent = new Intent(ArmarPedidos.this, InfoPedidoAdmin.class);
            Pedido tmp_pedido = pedidos.get(position);
            pedidos.clear();
            pedidoadapter.notifyDataSetChanged();
            intent.putExtra("fecha",tmp_pedido.getFecha());
            intent.putExtra("id_pedido", tmp_pedido.getCodigo());
            startActivity(intent);
            finish();
            }
        });
        spinner = (Spinner) findViewById(R.id.spinnerPuntoReparto);
        spinner.setOnItemSelectedListener(this);

        sharedpreferences = getSharedPreferences(Invariante.MyPREFERENCES, this.MODE_PRIVATE);
        test_mode = sharedpreferences.getBoolean("test_mode",true);
        fill();

        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Toast.makeText(getApplicationContext(),"Actualizando", Toast.LENGTH_SHORT).show();
                pedidos.clear();
                puntos_reparto.clear();
                fill();
                refresh.setRefreshing(false);
                Toast.makeText(getApplicationContext(),"Actualizado", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Método que llama cada vez que se cambiar de punto de reparto.
     * @param parent lista que se modificó, en caso de tener varias listas.
     * @param view
     * @param position identificador del punto de reparto seleccionado.
     * @param id
     */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String item = parent.getItemAtPosition(position).toString();
        Toast.makeText(parent.getContext(), "Punto de reparto seleccionado: " + item, Toast.LENGTH_SHORT).show();
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(Invariante.PUNTO_REPARTO, item);
        editor.apply();
        Toast.makeText(getApplicationContext(),"Actualizando", Toast.LENGTH_SHORT).show();
        pedidos.clear();
        update_list();
        refresh.setRefreshing(false);
        Toast.makeText(getApplicationContext(),"Actualizado", Toast.LENGTH_SHORT).show();
    }

    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
    }

    public void set_option(){
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(ArmarPedidos.this, R.layout.spinner, puntos_reparto);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);
    }

    /**
     * Método para crear pedidos de prueba.
     */
    private void fill(){
        if (test_mode){
            for (int i = 0; i < 3; i++) {
                try {
                    Date fecha = new SimpleDateFormat(Invariante.format_date).parse(i+"/03/2019");
                    pedidos.add(new Pedido(i+1,"Norte" ));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            pedidoadapter = new PedidoAdapter(this, pedidos);
            pedidoadapter.notifyDataSetChanged();
            listview_pedido.setAdapter(pedidoadapter);
        }else{
            update_list();
            update_delivery_list();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, MenuAdmin.class));
        finish();
    }

    /**
     * Método para actualizar la lista de pedidos desde el api.
     */
    void update_list(){
        try {
            String ip = sharedpreferences.getString("ip","");
            int port = sharedpreferences.getInt("port",0);
            String punto_reparto = sharedpreferences.getString(Invariante.PUNTO_REPARTO,"");
            if(port != 0 && ip != ""){
                if (punto_reparto == ""){
                    Toast.makeText(this, Invariante.ERROR_PUNTO_REPARTO, Toast.LENGTH_SHORT).show();
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
                                        pedidos.add(new Pedido(pedido.getString("dateCreated"), pedido.getInt("id")));
                                    }
                                    pedidosText.setText("Seleccionar un pedido: " + Integer.toString(response.length())+ " pendientes.");
                                    if(response.length() == 0){
                                        Toast.makeText(ArmarPedidos.this, "No existe pedidos actualmente.", Toast.LENGTH_SHORT).show();
                                    }
                                    pedidoadapter = new PedidoAdapter(ArmarPedidos.this, pedidos);
                                    listview_pedido.setAdapter(pedidoadapter);
                                } catch (JSONException e) {
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

    /**
     * Método para actualizar lista de puntos de reparto.
     */
    public void update_delivery_list(){
        String ip = sharedpreferences.getString("ip","");
        int port = sharedpreferences.getInt("port",0);

        if(ip!= "" && port != 0){
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            String server = Invariante.get_server(ip, port);
            JsonArrayRequest jsonObjectRequest = new JsonArrayRequest
                    (Request.Method.GET,server + "/api/v1/delivery-centers/" , null, new Response.Listener<JSONArray>() {

                        @Override
                        public void onResponse(JSONArray response) {
                            try {
                                for (int i = 0; i < response.length(); i++) {
                                    JSONObject centro = response.getJSONObject(i);
                                    String name = String.valueOf(centro.getInt("id")) + "-" + centro.getString("name");
                                    puntos_reparto.add(name);
                                }
                                set_option();
                                String punto_reparto = sharedpreferences.getString(Invariante.PUNTO_REPARTO,"");
                                if(punto_reparto != ""){
                                    spinner.setSelection(puntos_reparto.indexOf(punto_reparto));
                                }
                            } catch (JSONException e) {
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
            Toast.makeText(this, Invariante.CONF_ERROR_1, Toast.LENGTH_SHORT).show();
        }
    }
}


