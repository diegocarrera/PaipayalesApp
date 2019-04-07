package com.trimble.paipay;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONObject;
import com.trimble.paipay.adapters.PedidoAdapter;
import com.trimble.paipay.model.Pedido;
import com.trimble.paipay.utils.Invariante;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ArmarPedidos extends Activity {
    private ArrayList<Pedido> pedidos = new ArrayList<Pedido>();
    private PedidoAdapter pedidoadapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_armar_pedidos);

        final ListView listview_pedido = (ListView) findViewById(R.id.listapedidos);

        pedidoadapter = new PedidoAdapter(this, pedidos);
        listview_pedido.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent intent = new Intent(ArmarPedidos.this, Info_pedido.class);
            Pedido tmp_pedido = pedidos.get(position);
            pedidos.clear();
            pedidoadapter.notifyDataSetChanged();
            intent.putExtra("id_pedido", tmp_pedido.getCodigo());
            intent.putExtra("fecha",tmp_pedido.getFecha());
            intent.putExtra("detalle",tmp_pedido.getDetallePedidos());
            startActivity(intent);
            finish();
            }
        });
        listview_pedido.setAdapter(pedidoadapter);
        pedidoadapter.notifyDataSetChanged();
        try {
            SharedPreferences sharedpreferences = getSharedPreferences(Invariante.MyPREFERENCES, this.MODE_PRIVATE);
            String ip = sharedpreferences.getString("ip","");
            int port = sharedpreferences.getInt("port",0);
            JSONObject pedidos_json = Pedido.get_pedidos(ip, port, "POR EMPAQUETAR");
            JSONArray jsonarr = pedidos_json.getJSONArray("pedidos");
            for (int i = 0; i < jsonarr.length(); i++) {
                JSONObject pedido = jsonarr.getJSONObject(i);
                Date fecha = new SimpleDateFormat("dd/MM/yyyy").parse(pedido.getString("fecha"));
                pedidos.add(new Pedido(fecha, pedido.getInt("codigo")));
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, Menu.class));
        finish();
    }
}
