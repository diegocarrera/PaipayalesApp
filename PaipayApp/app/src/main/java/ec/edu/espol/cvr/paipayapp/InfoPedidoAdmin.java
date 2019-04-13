package ec.edu.espol.cvr.paipayapp;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import ec.edu.espol.cvr.paipayapp.adapters.DetallePedidoAdapter;
import ec.edu.espol.cvr.paipayapp.model.DetallePedido;
import ec.edu.espol.cvr.paipayapp.model.Pedido;
import ec.edu.espol.cvr.paipayapp.model.Producto;
import ec.edu.espol.cvr.paipayapp.utils.Invariante;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class InfoPedidoAdmin extends Activity {
    private Pedido pedido;
    private ArrayList<DetallePedido> detalles_pedido = new ArrayList<DetallePedido>();
    private DetallePedidoAdapter detalles_pedidoadapter;
    private Button finalizar;
    private File foto_pedido = null;
    private File dir;

    private String ip;
    private int port;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_pedido);
        TextView viewid_pedido = (TextView) findViewById(R.id.editcodigo);
        TextView view_fecha = (TextView) findViewById(R.id.editfecha);
        finalizar  = (Button) findViewById(R.id.finalizar);

        Intent intent = getIntent();
        if(intent.getIntExtra("id_pedido",0) != 0){
            int codigo = intent.getIntExtra("id_pedido",0);
            try {
                Date fecha = new SimpleDateFormat(Invariante.format_date).parse(intent.getStringExtra("fecha"));
                pedido = new Pedido(fecha, codigo);
                viewid_pedido.setText( viewid_pedido.getText() + String.valueOf(pedido.getCodigo()));
                view_fecha.setText(view_fecha.getText() + pedido.getFecha().toString());
                ListView listview_detalle_pedido = (ListView) findViewById(R.id.listadetallepedidos);

                detalles_pedidoadapter = new DetallePedidoAdapter(this, detalles_pedido);
                listview_detalle_pedido.setAdapter(detalles_pedidoadapter);
                update_list();
            } catch (ParseException e) {
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

    public void tomarFoto(View view){
        Intent tomar_foto = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (tomar_foto.resolveActivity(getPackageManager()) != null) {
            foto_pedido = new File(dir, "pedido_" + pedido.getCodigo() + ".jpeg");
            tomar_foto.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(foto_pedido));
            startActivityForResult(tomar_foto, Invariante.REQUEST_IMAGE_CAPTURE);
        }
    }

    public void AsociarTag(View view){
        IntentIntegrator integrator = new IntentIntegrator(InfoPedidoAdmin.this);
        integrator.setCaptureActivity(CaptureActivityPortrait.class);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ONE_D_CODE_TYPES);
        integrator.setPrompt("Escanea el Código de Barra");
        integrator.setCameraId(0);
        integrator.setBeepEnabled(true);
        integrator.initiateScan();
    }

    public void finalizar(View view){
        if(detalles_pedidoadapter.armadoCompleto()){
            if (pedido.getCodigo_barra() != null){
                if(pedido.update(ip, port)){
                    Toast.makeText(InfoPedidoAdmin.this, "Pedido armado y asignado correctamente.", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(InfoPedidoAdmin.this, "Ocurrió un problema, intente de nuevo.", Toast.LENGTH_SHORT).show();
                }
            }else{
                Toast.makeText(InfoPedidoAdmin.this, "Falta asociar un código de barras(tag)..", Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(InfoPedidoAdmin.this, "debe agregar todos los productos al pedido..", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch(requestCode){
                case Invariante.REQUEST_IMAGE_CAPTURE:
                    final File file;
                    file = foto_pedido;
                    try {
                        Bitmap bm = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), Uri.fromFile(file));
                        FileOutputStream out = new FileOutputStream(file);
                        bm.compress(Bitmap.CompressFormat.JPEG, 45, out);
                        out.close();
                        pedido.setFoto_pedido(file);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(InfoPedidoAdmin.this, "Ocurrió un problema, intente de nuevo.", Toast.LENGTH_SHORT).show();
                    }
                    Toast.makeText(InfoPedidoAdmin.this, "Foto asociada correctamente al pedido.", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
                    if(result != null) {
                        if(result.getContents() != null) {
                            String codigo_barra = result.getContents();
                            pedido.setCodigo_barra(codigo_barra);
                            Toast.makeText(InfoPedidoAdmin.this, "Codigo de barras asociado correctamente al pedido.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(InfoPedidoAdmin.this, "Ocurrió un problema, intente de nuevo.", Toast.LENGTH_SHORT).show();
                    }
            }
        }
    }

    void update_list(){
        try {
            SharedPreferences sharedpreferences = getSharedPreferences(Invariante.MyPREFERENCES, this.MODE_PRIVATE);
            ip = sharedpreferences.getString("ip", "");
            port = sharedpreferences.getInt("port", 0);
            boolean test_mode = sharedpreferences.getBoolean("test_mode", true);
            JSONObject pedidos_json = DetallePedido.get_detalles_pedido(ip, port, pedido.getCodigo());
            if (pedidos_json.getInt("response_code") == 200) {
                JSONArray jsonarr = pedidos_json.getJSONArray("detalles");
                for (int i = 0; i < jsonarr.length(); i++) {
                    JSONObject detalle_pedido = jsonarr.getJSONObject(i);
                    Producto producto = new Producto(detalle_pedido.getString("nombre"), detalle_pedido.getString("categoria"), detalle_pedido.getString("unidad"));
                    detalles_pedido.add(new DetallePedido(producto, detalle_pedido.getInt("cantidad")));
                }
            } else {
                Toast.makeText(this, pedidos_json.getString("error"), Toast.LENGTH_LONG).show();
                if (test_mode) {
                    Toast.makeText(this, "modo test", Toast.LENGTH_LONG).show();
                    for (int i = 0; i < 5; i++) {
                        Producto producto = new Producto("papa", "verdura", "libra");
                        detalles_pedido.add(new DetallePedido(producto, i + 1));
                    }
                }
            }
            detalles_pedidoadapter.notifyDataSetChanged();
            dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM),Invariante.path_fotos_pedidos);
            dir.mkdirs();
            foto_pedido = new File(dir,"pedido_" + pedido.getCodigo() + ".jpeg");
        }catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
