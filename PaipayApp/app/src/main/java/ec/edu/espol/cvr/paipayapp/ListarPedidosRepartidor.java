package ec.edu.espol.cvr.paipayapp;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import ec.edu.espol.cvr.paipayapp.adapters.PedidoAdapter;
import ec.edu.espol.cvr.paipayapp.model.Pedido;
import ec.edu.espol.cvr.paipayapp.utils.Invariante;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ListarPedidosRepartidor extends Activity {
    /*
        Actividad que muestra la lista de pedidos asociadas a un repartidor.
        Si un repartidor presiona sobre un pedido, se debe llamar a la actividad InfoPedido
    */

    private ArrayList<Pedido> pedidos = new ArrayList<Pedido>();
    private PedidoAdapter pedidoadapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_armar_pedidos);

        ListView listview_pedido = (ListView) findViewById(R.id.listapedidos);
        listview_pedido.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Intent intent = new Intent(ListarPedidosRepartidor.this, InfoPedido.class);
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
        pedidoadapter = new PedidoAdapter(this, pedidos);
        listview_pedido.setAdapter(pedidoadapter);
        update_list();
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
        try {
            SharedPreferences sharedpreferences = getSharedPreferences(Invariante.MyPREFERENCES, this.MODE_PRIVATE);
            String ip = sharedpreferences.getString("ip","");
            int port = sharedpreferences.getInt("port",0);
            //String punto_reparto = sharedpreferences.getString("punto_reparto","");
            String user_token = sharedpreferences.getString("token","");

            boolean test_mode = sharedpreferences.getBoolean("test_mode",true);
            if(port != 0 && ip != ""){

                JSONObject pedidos_json = Pedido.get_pedidos_por_repartidor(ip, port, user_token); //TRABAJAR EN ESTA FUNCIONNNNNNNN
                if(pedidos_json.getInt("response_code") == 200){
                    JSONArray jsonarr = pedidos_json.getJSONArray("data");
                    for (int i = 0; i < jsonarr.length(); i++) {
                        JSONObject pedido = jsonarr.getJSONObject(i);
                        Date fecha = new SimpleDateFormat(Invariante.format_date).parse(pedido.getString("dateCreated"));
                        pedidos.add(new Pedido(fecha, pedido.getInt("id")));
                    }
                }else{
                    Toast.makeText(this, pedidos_json.getString("error"), Toast.LENGTH_LONG).show();
                    if (test_mode){
                        for (int i = 0; i < 3; i++) {
                            Date fecha = new SimpleDateFormat(Invariante.format_date).parse(i+"/03/2019");
                            pedidos.add(new Pedido(fecha, i+1));
                            pedidoadapter.notifyDataSetChanged();
                        }
                    }
                }
            }else{
                Toast.makeText(this, "IP y/o puerto del servidor no configurado. ", Toast.LENGTH_LONG).show();
            }
            pedidoadapter.notifyDataSetChanged();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}


