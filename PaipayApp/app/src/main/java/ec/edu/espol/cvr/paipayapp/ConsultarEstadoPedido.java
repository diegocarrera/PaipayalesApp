package ec.edu.espol.cvr.paipayapp;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import ec.edu.espol.cvr.paipayapp.model.Pedido;
import ec.edu.espol.cvr.paipayapp.utils.Invariante;

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

    public void consultar_pedido(View view){
        try{
            String ip = sharedpreferences.getString("ip", "");
            int port = sharedpreferences.getInt("port", 0);
            boolean test_mode = sharedpreferences.getBoolean("test_mode",true);
            int pedido_id = Integer.parseInt(codigo.getText().toString());
            Pedido pedidoConsultado = Pedido.consultar_pedido(pedido_id, ip, port);
            if(pedidoConsultado == null && !test_mode){
                infoPedido.setText(R.string.errorConsultar);
                Toast.makeText(this, R.string.errorConsultar, Toast.LENGTH_SHORT).show();
            }else{
                if(test_mode){
                    Toast.makeText(this, "Modo test activado", Toast.LENGTH_SHORT).show();
                    infoPedido.setText("Pedido #1");
                    estadoPedido.setText("Estado : SOLICITADO");
                    repartidorPedido.setText("Repartidor: USER 1");
                }else{
                    Toast.makeText(this, R.string.pedidoEncontrado, Toast.LENGTH_SHORT).show();
                    infoPedido.setText(R.string.codigo_pedido +  String.valueOf(pedidoConsultado.getCodigo()));
                    estadoPedido.setText(R.string.estadoPedido + pedidoConsultado.getEstado());
                    repartidorPedido.setText(R.string.RepartidorPedido + pedidoConsultado.getRepartidor());
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, MenuAdmin.class));
        finish();
    }
}
