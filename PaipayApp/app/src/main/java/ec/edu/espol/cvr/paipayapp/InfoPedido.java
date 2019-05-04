package ec.edu.espol.cvr.paipayapp;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.location.LocationManager;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import ec.edu.espol.cvr.paipayapp.adapters.DetallePedidoAdapter;
import ec.edu.espol.cvr.paipayapp.model.DetallePedido;
import ec.edu.espol.cvr.paipayapp.model.Pedido;
import ec.edu.espol.cvr.paipayapp.model.Producto;
import ec.edu.espol.cvr.paipayapp.utils.Invariante;

public class InfoPedido extends Activity {
    private Pedido pedido;
    private Button finalizar, cancelar, iniciar;
    private File foto_pedido = null;
    private File dir;

    private String ip;
    private int port;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_pedido_repartidor);
        TextView viewid_pedido = (TextView) findViewById(R.id.editcodigo);
        TextView view_fecha = (TextView) findViewById(R.id.fechaPedido_tv);
        TextView view_cliente = (TextView) findViewById(R.id.cliente_tv);

        finalizar  = (Button) findViewById(R.id.finalizarRuta);
        cancelar  = (Button) findViewById(R.id.cancelarRuta);
        iniciar  = (Button) findViewById(R.id.iniciarRuta);


        Intent intent = getIntent();
        if(intent.getIntExtra("id_pedido",0) != 0){
            int codigo = intent.getIntExtra("id_pedido",0);
            try {
                Date fecha = new SimpleDateFormat(Invariante.format_date).parse(intent.getStringExtra("fecha"));
                pedido = new Pedido(fecha, codigo);
                viewid_pedido.setText( viewid_pedido.getText() + String.valueOf(pedido.getCodigo()));
                view_fecha.setText(view_fecha.getText() + pedido.getFecha().toString());
                view_cliente.setText(view_fecha.getText() + pedido.getUser().toString());

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

    public void asociarTag(View view){
        IntentIntegrator integrator = new IntentIntegrator(InfoPedido.this);
        integrator.setCaptureActivity(CaptureActivityPortrait.class);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ONE_D_CODE_TYPES);
        integrator.setPrompt("Escanea el Código de Barra");
        integrator.setCameraId(0);
        integrator.setBeepEnabled(true);
        integrator.initiateScan();
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
                        Toast.makeText(InfoPedido.this, "Ocurrió un problema, intente de nuevo.", Toast.LENGTH_SHORT).show();
                    }
                    Toast.makeText(InfoPedido.this, "Foto asociada correctamente al pedido.", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
                    if(result != null) {
                        //Verificar que el codigo de barras del pedido entregado sea el correcto
                        if(result.getContents() != null) {
                            String codigo_barra = result.getContents();
                            if(pedido.getCodigo_barra().equals(codigo_barra)){
                                Toast.makeText(InfoPedido.this, "Codigo de barras correcto. Pedido entregado satisfactoriamente.", Toast.LENGTH_LONG).show();
                            }
                            else{
                                Toast.makeText(InfoPedido.this, "El código de barras no coincide con el asociado al pedido. Este no es el pedido del cliente", Toast.LENGTH_LONG).show();
                            }

                        }
                    } else {
                        Toast.makeText(InfoPedido.this, "Ocurrió un problema, intente de nuevo.", Toast.LENGTH_SHORT).show();
                    }
            }
        }
    }

    public void iniciarRuta(){
        //funcion para mandar por POST la ubicación del usuario
        //debe verificar que se tiene encendido el GPS, caso contrario, encenderlo automaticamente
        //boton de iniciarRuta debe estar deshabilitado inicialmente
        LocationManager lm = (LocationManager)InfoPedido.this.getSystemService(InfoPedido.LOCATION_SERVICE);
        boolean gps_enabled = false;
        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        }
        catch(Exception ex) {
            System.out.println("Cannot check if gps is enabled");
        }

        if(!gps_enabled){
            Toast.makeText(InfoPedido.this, "Debe encender el GPS para poder iniciar la ruta.", Toast.LENGTH_SHORT).show();
        }
        else{
            //cuando se inicia la ruta, el botón de iniciar se desactiva
            this.iniciar.setEnabled(false);
            //y se activa el boton de finalizar ruta
            this.finalizar.setEnabled(true);
            //hacer los requerimientos POST
        }
    }



}

