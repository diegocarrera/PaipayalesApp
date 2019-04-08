package com.trimble.paipay;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.trimble.paipay.adapters.DetallePedidoAdapter;
import com.trimble.paipay.model.DetallePedido;
import com.trimble.paipay.model.Pedido;
import com.trimble.paipay.model.Producto;
import com.trimble.paipay.utils.Invariante;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Info_pedido extends Activity {
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private Pedido pedido;
    TextView viewid_pedido, view_fecha;
    ListView listview_detalle_pedido;
    private ArrayList<DetallePedido> detalles_pedido = new ArrayList<DetallePedido>();
    private DetallePedidoAdapter detalles_pedidoadapter;

    private File dir;
    private File output1=null;
    private File output2=null;
    private Button asociarFoto;
    private Button asociarTag;
    private Button finalizar;

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

        dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM),"/Etiquetador/");
        dir.mkdirs();
        asociarFoto = (Button) findViewById(R.id.asociarFoto);
        output1 = new File(dir,"pos_"+".jpeg");
        if(output1.exists()){
            asociarFoto.setBackgroundResource(R.drawable.poste_taken);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, ArmarPedidos.class));
        finish();
    }

    public void tomarFotos(View view){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            output1=new File(dir, "pos_"+".jpeg");
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(output1));
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    public void AsociarTag(View view){
        final CharSequence colors[] = new CharSequence[] {"Lector Barcode","Lector RFID"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Seleccione un método de lectura");
        builder.setItems(colors, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(colors[which].equals("Lector Barcode")){
                    IntentIntegrator integrator = new IntentIntegrator(Info_pedido.this);
                    integrator.setCaptureActivity(CaptureActivityPortrait.class);
                    integrator.setDesiredBarcodeFormats(IntentIntegrator.ONE_D_CODE_TYPES);
                    integrator.setPrompt("Escanea Codigo de Barra");
                    integrator.setCameraId(0);  // Use a specific camera of the device
                    integrator.setBeepEnabled(true);
                    integrator.initiateScan();
                }
                else{
                    Intent rfidIntent = new Intent(Info_pedido.this,registerRFID.class);
                    rfidIntent.putExtra("estado","finalizado");
                    startActivity(rfidIntent);
                    finish();
                }
            }
        });
        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch(requestCode){
                case REQUEST_IMAGE_CAPTURE:
                    final File file;
                    //asociarFoto.setBackgroundResource(R.drawable.poste_taken);
                    file = output1;
                    try {
                        Bitmap bm = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), Uri.fromFile(file));
                        FileOutputStream out = new FileOutputStream(file);
                        bm.compress(Bitmap.CompressFormat.JPEG, 45, out);
                        out.close();
                    } catch (Exception e) {
                        //asociarFoto.setBackgroundResource(R.drawable.poste_taken);
                        e.printStackTrace();
                        Toast toast = Toast.makeText(Info_pedido.this, "Ocurrió un problema, intente de nuevo", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.TOP | Gravity.LEFT, 25, 350);
                        toast.show();
                    }
                    break;
            }
        }
    }
}
