package com.trimble.paipay;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.trimble.paipay.adapters.DetallePedidoAdapter;
import com.trimble.paipay.model.DetallePedido;
import com.trimble.paipay.model.Pedido;
import com.trimble.paipay.model.Producto;
import com.trimble.paipay.utils.Invariante;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Info_pedido extends Activity {
    private Pedido pedido;
    TextView viewid_pedido, view_fecha;
    ListView listview_detalle_pedido;
    private ArrayList<DetallePedido> detalles_pedido = new ArrayList<DetallePedido>();
    private DetallePedidoAdapter detalles_pedidoadapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_pedido);
        viewid_pedido = (TextView) findViewById(R.id.editcodigo);
        view_fecha = (TextView) findViewById(R.id.editfecha);

        Intent intent = getIntent();
        if(intent.getIntExtra("id_pedido",0) != 0){
            int codigo = intent.getIntExtra("id_pedido",0);
            try {
                Date fecha = new SimpleDateFormat(Invariante.format_date).parse(intent.getStringExtra("fecha"));
                pedido = new Pedido(fecha, codigo);
                viewid_pedido.setText(pedido.getCodigo());
                view_fecha.setText(pedido.getFecha().toString());
                listview_detalle_pedido = (ListView) findViewById(R.id.listadetallepedidos);

                detalles_pedidoadapter = new DetallePedidoAdapter(this, detalles_pedido);
                listview_detalle_pedido.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        //check  y contador para saber que ya agrego todo
                    }
                });
                listview_detalle_pedido.setAdapter(detalles_pedidoadapter);
                detalles_pedidoadapter.notifyDataSetChanged();

                SharedPreferences sharedpreferences = getSharedPreferences(Invariante.MyPREFERENCES, this.MODE_PRIVATE);
                String ip = sharedpreferences.getString("ip","");
                int port = sharedpreferences.getInt("port",0);
                JSONObject pedidos_json = DetallePedido.get_detalles_pedido(ip, port, pedido.getCodigo());
                JSONArray jsonarr = pedidos_json.getJSONArray("detalles");
                for (int i = 0; i < jsonarr.length(); i++) {
                    JSONObject detalle_pedido = jsonarr.getJSONObject(i);
                    Producto producto = new Producto(detalle_pedido.getString("nombre"), detalle_pedido.getString("categoria"));
                    detalles_pedido.add(new DetallePedido(producto, detalle_pedido.getInt("precio")));
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, ArmarPedidos.class));
        finish();
    }
}
