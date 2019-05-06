package ec.edu.espol.cvr.paipayapp;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
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
import java.util.List;

public class InfoPedidoAdmin extends Activity implements AdapterView.OnItemSelectedListener{
    private SharedPreferences sharedpreferences;
    private String ip;
    private int port;
    private boolean test_mode;

    private Pedido pedido;
    private List<String> repartidores = new ArrayList<String>();
    private ArrayList<DetallePedido> detalles_pedido = new ArrayList<DetallePedido>();
    private File foto_pedido = null;
    private File dir;

    private Spinner spinner;
    private DetallePedidoAdapter detalles_pedidoadapter;
    TextView viewid_pedido;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_pedido);

        sharedpreferences = getSharedPreferences(Invariante.MyPREFERENCES, this.MODE_PRIVATE);
        ip = sharedpreferences.getString("ip", "");
        port = sharedpreferences.getInt("port", 0);
        test_mode = sharedpreferences.getBoolean("test_mode", true);

        viewid_pedido = (TextView) findViewById(R.id.editcodigo);
        TextView view_fecha = (TextView) findViewById(R.id.editfecha);
        spinner = (Spinner) findViewById(R.id.spinnerRepartidor);
        spinner.setOnItemSelectedListener(this);

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
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        if(test_mode){
            Toast.makeText(this, Invariante.PRUEBA, Toast.LENGTH_SHORT).show();
            repartidores.add("Repartidor 1");
            repartidores.add("Repartidor 2");
            set_option();
            for (int i = 0; i < 5; i++) {
                Producto producto = new Producto("papa", "verdura", "libra");
                detalles_pedido.add(new DetallePedido(producto, i + 1));
            }
            detalles_pedidoadapter.notifyDataSetChanged();
        }else{
            update_list();
            update_repartidor();
        }
    }

    /*----------------------------- spinner----------------------------------------*/
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String item = parent.getItemAtPosition(position).toString();
        Toast.makeText(parent.getContext(), "Repartidor seleccionado: " + item, Toast.LENGTH_LONG).show();
        pedido.setRepartidor(item);
    }

    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
    }

    public void set_option(){
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(InfoPedidoAdmin.this, R.layout.spinner, repartidores);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);
    }

    public void update_repartidor(){
        if(ip!= "" && port != 0){
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            String server = Invariante.get_server(ip, port) + "/api/v1/users/delivery-men/";
            JsonArrayRequest jsonObjectRequest = new JsonArrayRequest
                    (Request.Method.GET, server, null, new Response.Listener<JSONArray>() {

                        @Override
                        public void onResponse(JSONArray response) {
                            try {
                                for (int i = 0; i < response.length(); i++) {
                                    JSONObject repartidor = response.getJSONObject(i);
                                    //VERIFICAR CONTENIDO DEL JSON
                                    String name = String.valueOf(repartidor.getInt("id")) + "-" + repartidor.getString("name");
                                    repartidores.add(name);
                                }
                                set_option();
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(InfoPedidoAdmin.this, Invariante.ERROR_LOGIN_RED, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            try {
                                int code = error.networkResponse.statusCode;
                                JSONObject json = new JSONObject(new String(error.networkResponse.data));
                                String message = "Error " + String.valueOf(code) + json.getString("message");
                                Toast.makeText(InfoPedidoAdmin.this, message, Toast.LENGTH_SHORT).show();
                            }catch (Exception e) {
                                e.printStackTrace();
                                Toast.makeText(InfoPedidoAdmin.this, Invariante.ERROR_LOGIN_RED_ACCESO, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
            requestQueue.add(jsonObjectRequest);
        }else{
            Toast.makeText(this, Invariante.CONF_ERROR_1, Toast.LENGTH_LONG).show();
        }
    }

    /*----------------------------- retroceder----------------------------------------*/
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, ArmarPedidos.class));
        finish();
    }

    /*----------------------------- asociar ----------------------------------------*/
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
        integrator.setPrompt(Invariante.MENSAJE_ESCANEO);
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
                        Toast.makeText(InfoPedidoAdmin.this, Invariante.ERROR_INTENTE_N, Toast.LENGTH_SHORT).show();
                    }
                    Toast.makeText(InfoPedidoAdmin.this, Invariante.FOTO_OK, Toast.LENGTH_SHORT).show();
                    break;
                default:
                    IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
                    if(result != null) {
                        if(result.getContents() != null) {
                            String codigo_barra = result.getContents();
                            pedido.setCodigo_barra(codigo_barra);
                            viewid_pedido.setText( viewid_pedido.getText() + " code:"+ codigo_barra);
                            Toast.makeText(InfoPedidoAdmin.this, Invariante.CODIGO_OK, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(InfoPedidoAdmin.this, Invariante.ERROR_INTENTE_N, Toast.LENGTH_SHORT).show();
                    }
            }
        }
    }

    public void finalizar(View view){
        boolean test_mode = sharedpreferences.getBoolean("test_mode", true);
        if(detalles_pedidoadapter.armadoCompleto()){
            if (pedido.getCodigo_barra() != null){
                if(test_mode){
                    change_view();
                }else{
                    update_api();
                }
            }else{
                Toast.makeText(InfoPedidoAdmin.this, Invariante.ERROR_INCOMPLETO_TAG, Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(InfoPedidoAdmin.this, Invariante.ERROR_INCOMPLETO_PEDIDO, Toast.LENGTH_SHORT).show();
        }
    }

    public void change_view(){
        Toast.makeText(InfoPedidoAdmin.this, Invariante.FINALIZAR_OK, Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, ArmarPedidos.class);
        startActivity(intent);
        finish();
    }
//URL: /purchases/info/aquivaelcodigo
    public void update_api(){
        JSONObject parameters = new JSONObject();
        try {
            parameters.put("codigo_barra", pedido.getCodigo_barra());
            parameters.put("codigo_pedido", pedido.getCodigo());
            parameters.put("estado", "ARMADO");
            parameters.put("repartidor", pedido.getRepartidor());
            //FALTA ENVIAR FOTO

            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            String server = Invariante.get_server(ip, port);
            String url = server + "/api/v1/pedidos/" + String.valueOf(pedido.getCodigo());
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                    (Request.Method.PUT, url, parameters, new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                System.out.println(response.toString());
                                // que retorna?
                                change_view();
                            } catch (Exception e) {
                                e.printStackTrace();
                                Toast.makeText(InfoPedidoAdmin.this, Invariante.ERROR_LOGIN_RED, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            try {
                                int code = error.networkResponse.statusCode;
                                JSONObject json = new JSONObject(new String(error.networkResponse.data));
                                String message = "Error " + String.valueOf(code) + json.getString("message");
                                Toast.makeText(InfoPedidoAdmin.this, message, Toast.LENGTH_SHORT).show();
                            }catch (Exception e) {
                                e.printStackTrace();
                                Toast.makeText(InfoPedidoAdmin.this, Invariante.ERROR_LOGIN_RED_ACCESO, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
            requestQueue.add(jsonObjectRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    void update_list(){
        //ACTUALIZAR --------------- API RETORNA JSON O ARREGLO?
        /*
        try {
            JSONObject pedidos_json = new JSONObject();
            if (pedidos_json.getInt("response_code") == 200) {
                JSONArray jsonarr = pedidos_json.getJSONArray("detalles");
                for (int i = 0; i < jsonarr.length(); i++) {
                    JSONObject detalle_pedido = jsonarr.getJSONObject(i);
                    Producto producto = new Producto(detalle_pedido.getString("nombre"), detalle_pedido.getString("categoria"), detalle_pedido.getString("unidad"));
                    detalles_pedido.add(new DetallePedido(producto, detalle_pedido.getInt("cantidad")));
                }
            } else {
                Toast.makeText(this, pedidos_json.getString("error"), Toast.LENGTH_LONG).show();
            }
            detalles_pedidoadapter.notifyDataSetChanged();
            dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM),Invariante.path_fotos_pedidos);
            dir.mkdirs();
            foto_pedido = new File(dir,"pedido_" + pedido.getCodigo() + ".jpeg");
        }catch (JSONException e) {
            e.printStackTrace();
        }*/
    }

}
