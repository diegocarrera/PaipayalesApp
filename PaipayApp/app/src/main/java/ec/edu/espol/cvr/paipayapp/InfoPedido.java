package ec.edu.espol.cvr.paipayapp;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import ec.edu.espol.cvr.paipayapp.adapters.DetallePedidoAdapter;
import ec.edu.espol.cvr.paipayapp.adapters.PedidoAdapter;
import ec.edu.espol.cvr.paipayapp.model.DetallePedido;
import ec.edu.espol.cvr.paipayapp.model.Pedido;
import ec.edu.espol.cvr.paipayapp.model.Producto;
import ec.edu.espol.cvr.paipayapp.utils.Invariante;

import static android.os.SystemClock.sleep;

public class InfoPedido extends Activity {
    private Pedido pedido;
    private Button finalizar, cancelar, iniciar;
    private File foto_pedido = null;
    private File dir;
    TextView view_cliente, view_direccion, view_fecha, view_barras;
    int route_id;
    boolean route_created,end_route, on_my_way;
    LocationManager lm;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_pedido_repartidor);
        TextView viewid_pedido = (TextView) findViewById(R.id.editcodigo);
        view_fecha = (TextView) findViewById(R.id.fechaPedido_tv);
        view_cliente = (TextView) findViewById(R.id.cliente_tv);
        view_direccion = (TextView) findViewById(R.id.direccion_tv);
        view_barras = (TextView) findViewById(R.id.cod_barras_tv);
        route_created = false;
        end_route = false; // flag que indica si el repartidor presiono el boton finalizar entrega
        on_my_way = false; // flag que indica si el repartidor esta en medio de una ruta


        finalizar = (Button) findViewById(R.id.finalizarRuta);
        cancelar = (Button) findViewById(R.id.cancelarRuta);
        iniciar = (Button) findViewById(R.id.iniciarRuta);
        cancelar.setBackgroundColor(getResources().getColor(R.color.verde_deshabilitado));
        finalizar.setBackgroundColor(getResources().getColor(R.color.verde_deshabilitado));


        Intent intent = getIntent();
        if (intent.getIntExtra("id_pedido", 0) != 0) {
            int codigo = intent.getIntExtra("id_pedido", 0);

            String direccion = intent.getStringExtra("direccion");
            pedido = new Pedido(codigo, direccion);
            viewid_pedido.setText(viewid_pedido.getText() + String.valueOf(pedido.getCodigo()));
            view_direccion.setText(view_direccion.getText() + pedido.getDireccion());
            // Obtengo del servidor el nombre del cliente y otros datos del pedido...
            obtener_info_pedido(codigo);


        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, ListarPedidosRepartidor.class));
        finish();
    }

    public void tomarFoto(View view) {
        Intent tomar_foto = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (tomar_foto.resolveActivity(getPackageManager()) != null) {
            foto_pedido = new File(dir, "pedido_" + pedido.getCodigo() + ".jpeg");
            tomar_foto.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(foto_pedido));
            startActivityForResult(tomar_foto, Invariante.REQUEST_IMAGE_CAPTURE);
        }
    }

    public void asociarTag(View view) {
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
            switch (requestCode) {
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
                    if (result != null) {
                        //Verificar que el codigo de barras del pedido entregado sea el correcto
                        if (result.getContents() != null) {
                            String codigo_barra = result.getContents();
                            if (pedido.getCodigo_barra().equals(codigo_barra)) {
                                Toast.makeText(InfoPedido.this, "Codigo de barras correcto. Pedido entregado satisfactoriamente.", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(InfoPedido.this, "El código de barras no coincide con el asociado al pedido. Este no es el pedido del cliente", Toast.LENGTH_LONG).show();
                            }

                        }
                    } else {
                        Toast.makeText(InfoPedido.this, "Ocurrió un problema, intente de nuevo.", Toast.LENGTH_SHORT).show();
                    }
            }
        }
    }

    public void iniciarRuta(View view) {

        /*funcion para mandar por POST la ubicación del usuario
        debe verificar que se tiene encendido el GPS, caso contrario, encenderlo automaticamente
        */
        //boton de iniciarRuta debe estar deshabilitado inicialmente

        // Acquire a reference to the system Location Manager
        lm = (LocationManager) InfoPedido.this.getSystemService(InfoPedido.LOCATION_SERVICE);
        long latitude, longitude;

        boolean gps_enabled = false;
        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
            System.out.println("Cannot check if gps is enabled");
        }

        if (!gps_enabled) {
            Toast.makeText(InfoPedido.this, "Debe encender el GPS para poder iniciar la ruta.", Toast.LENGTH_SHORT).show();
        } else {

            //Crear una ruta
            boolean result = crearRuta();

            if (result) {
                //cuando se inicia la ruta, el botón de iniciar se desactiva
                this.iniciar.setEnabled(false);
                this.iniciar.setBackgroundColor(getResources().getColor(R.color.verde_deshabilitado));

                //y se activan los botones de finalizar y cancelar ruta
                this.finalizar.setEnabled(true);
                this.finalizar.setBackgroundColor(getResources().getColor(R.color.color_botones));
                this.cancelar.setEnabled(true);
                this.cancelar.setBackgroundColor(getResources().getColor(R.color.color_botones));

                //se indica que el repartidor esta en una ruta
                this.on_my_way = true;

                // Define a listener that responds to location updates
                LocationListener locationListener = new LocationListener() {
                    public void onLocationChanged(Location location) {
                        if(on_my_way){
                            System.out.println("TOMANDO COORDENADAS");
                            // Called when a new location is found by the GPS location provider.
                            Toast.makeText(
                                    getBaseContext(),
                                    "Location changed: Lat: " + location.getLatitude() + " Lng: "
                                            + location.getLongitude(), Toast.LENGTH_SHORT).show();
                        }
                        else{
                            // stop location updating
                            lm.removeUpdates(this);
                            lm = null;
                        }

                    }

                    public void onStatusChanged(String provider, int status, Bundle extras) {
                    }

                    public void onProviderEnabled(String provider) {
                    }

                    public void onProviderDisabled(String provider) {
                    }
                };

                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }

                lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 8000, 3, locationListener); //milliseconds, meters

            }
            else{
                System.out.println("FALLO AL CREAR RUTA");
            }

        }
    }

    void obtener_info_pedido(int id){
        /*
        Funcion que hace un requerimiento al servidor para obtener datos de un pedido
        Parametros: id del pedido
        Respuesta:
        {
            "id": 1,
            "products": "{}",
            "barCode": "{}",
            "totalPrice": 0,
            "dateCreated": "2019-04-29T18:28:00Z",
            "user": {
                "name": "Belen GC",
                "phoneNumber": "+5939688888888",
                "address": "adfasdfsdaf",
                "userZone": "zona1"
            }
        }

        */
        SharedPreferences sharedpreferences = getSharedPreferences(Invariante.MyPREFERENCES, this.MODE_PRIVATE);
        String ip = sharedpreferences.getString("ip","");
        int port = sharedpreferences.getInt("port",0);

        try {

            if(port != 0 && !ip.equals("")){

                //Armo el request
                RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                String server = Invariante.get_server(ip, port);
                String new_path = server + "/api/v1/purchases/info/"+id;
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                        (Request.Method.GET, new_path, null, new Response.Listener<JSONObject>() {

                            @Override
                            public void onResponse(JSONObject response) {
                                try {

                                    JSONObject user_dict = response.getJSONObject("user");
                                    String client_name = user_dict.getString("name");
                                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
                                    /*Date fecha = formatter.parse(response.getString("dateCreated").replaceAll("Z$", "+0000"));
                                    DateFormat dateFormat = new SimpleDateFormat(Invariante.format_date);
                                    String fecha_str = dateFormat.format(fecha);*/
                                    String codigo_barra = response.getString("barCode");
                                    pedido.setUser(client_name);
                                    pedido.setFecha(response.getString("dateCreated"));
                                    pedido.setCodigo_barra(codigo_barra);

                                    view_cliente.setText(view_cliente.getText()+client_name);
                                    view_fecha.setText(view_fecha.getText()+pedido.getFecha());
                                    view_barras.setText(view_barras.getText()+codigo_barra);


                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    Toast.makeText(InfoPedido.this, Invariante.ERROR_LOGIN_RED, Toast.LENGTH_SHORT).show();
                                }
                            }
                        }, new Response.ErrorListener() {

                            @Override
                            public void onErrorResponse(VolleyError error) {
                                try {
                                    int code = error.networkResponse.statusCode;
                                    JSONObject json = new JSONObject(new String(error.networkResponse.data));
                                    String message = "Error " + String.valueOf(code) + json.getString("message");
                                    Toast.makeText(InfoPedido.this, message, Toast.LENGTH_SHORT).show();
                                }catch (Exception e) {
                                    e.printStackTrace();
                                    Toast.makeText(InfoPedido.this, Invariante.ERROR_LOGIN_RED_ACCESO, Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                {

                    /**
                     * Passing some request headers

                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        HashMap<String, String> headers = new HashMap<String, String>();
                        //headers.put("Content-Type", "application/json");
                        headers.put("Authorization", user_token);
                        return headers;
                    }*/
                };
                requestQueue.add(jsonObjectRequest);

            }else{
                Toast.makeText(this, "IP y/o puerto del servidor no configurado. ", Toast.LENGTH_LONG).show();
            }

        }catch (Exception e) {
            e.printStackTrace();
        }


    }

    boolean crearRuta(){

        /*
        Funcion que crea una ruta, mandando un requerimiento POST al servidor.
        Headers:
            Authorization: token
        Parametros:
            {"purchase":1}
        Respuesta:
            {"id": 1}
        */
        final SharedPreferences sharedpreferences = getSharedPreferences(Invariante.MyPREFERENCES, this.MODE_PRIVATE);
        String ip = sharedpreferences.getString("ip","");
        int port = sharedpreferences.getInt("port",0);

        try {

            if(port != 0 && !ip.equals("")){

                //Armo el request
                RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                String server = Invariante.get_server(ip, port);
                String new_path = server + "/tracks/api/v1/routes";
                //agregar los parametros necesarios
                JSONObject jsonBody = new JSONObject();
                jsonBody.put("purchase", Integer.toString(pedido.getCodigo()));


                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                        (Request.Method.POST, new_path, jsonBody, new Response.Listener<JSONObject>() {

                            @Override
                            public void onResponse(JSONObject response) {
                                try {

                                    route_id = Integer.parseInt(response.getString("id"));
                                    route_created = true;
                                    SharedPreferences.Editor editor = sharedpreferences.edit();
                                    //put route_id in shared preferences
                                    editor.putInt(Invariante.ROUTE, route_id);
                                    editor.apply();


                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    Toast.makeText(InfoPedido.this, Invariante.ERROR_LOGIN_RED, Toast.LENGTH_SHORT).show();
                                }
                            }
                        }, new Response.ErrorListener() {

                            @Override
                            public void onErrorResponse(VolleyError error) {
                                try {
                                    int code = error.networkResponse.statusCode;
                                    JSONObject json = new JSONObject(new String(error.networkResponse.data));
                                    String message = "Error " + String.valueOf(code) + json.getString("message");
                                    Toast.makeText(InfoPedido.this, message, Toast.LENGTH_SHORT).show();
                                }catch (Exception e) {
                                    e.printStackTrace();
                                    Toast.makeText(InfoPedido.this, Invariante.ERROR_LOGIN_RED_ACCESO, Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                {

                    /**
                     * Passing some request headers
                    */
                     @Override
                     public Map<String, String> getHeaders() throws AuthFailureError {
                     HashMap<String, String> headers = new HashMap<String, String>();
                     headers.put("Authorization", sharedpreferences.getString(Invariante.TOKEN,""));
                     return headers;
                     }

                    /**
                     * Passing some parameters
                     */

                    /*
                    protected Map<String, String> getParams()
                    {
                        Map<String, String>  params = new HashMap<String, String>();
                        params.put("purchase", Integer.toString(pedido.getCodigo()));

                        return params;
                    }*/
                };
                requestQueue.add(jsonObjectRequest);

            }else{
                Toast.makeText(this, "IP y/o puerto del servidor no configurado. ", Toast.LENGTH_LONG).show();
            }

        }catch (Exception e) {
            e.printStackTrace();

        }
        System.out.println(route_created);
        return route_created;


    }

    void postSteps(String latitude, String longitude){
        /*Funcion que envía coordenadas de la ruta al servidor
        Header:
            Authorization: xxx
        Parametros:
            lat: x
            lon: y
        Respuesta:
            id(del step creado)
         */
        final SharedPreferences sharedpreferences = getSharedPreferences(Invariante.MyPREFERENCES, this.MODE_PRIVATE);
        String ip = sharedpreferences.getString("ip","");
        int port = sharedpreferences.getInt("port",0);
        int route = sharedpreferences.getInt(Invariante.ROUTE,0); //obtener el id de la ruta de los shared preferences
        try {

            if(port != 0 && !ip.equals("")){

                //Armo el request
                RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                String server = Invariante.get_server(ip, port);
                String new_path = server + "/tracks/api/v1/steps";
                //agregar los parametros necesarios
                JSONObject jsonBody = new JSONObject();
                jsonBody.put("latitude", latitude);
                jsonBody.put("longitude", longitude);
                jsonBody.put("route", Integer.toString(route));
                System.out.println("POST STEPSSSSSSSSSSSSSSS");
                System.out.println(latitude+" "+longitude+" "+route_id);



                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                        (Request.Method.POST, new_path, jsonBody, new Response.Listener<JSONObject>() {

                            @Override
                            public void onResponse(JSONObject response) {
                                try {

                                    int step_id = Integer.parseInt(response.getString("id"));
                                    System.out.println("PUDE POSTEAR STEP CON ID: "+ step_id);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    Toast.makeText(InfoPedido.this, Invariante.ERROR_LOGIN_RED, Toast.LENGTH_SHORT).show();
                                }
                            }
                        }, new Response.ErrorListener() {

                            @Override
                            public void onErrorResponse(VolleyError error) {
                                try {
                                    int code = error.networkResponse.statusCode;
                                    JSONObject json = new JSONObject(new String(error.networkResponse.data));
                                    String message = "Error " + String.valueOf(code) + json.getString("message");
                                    Toast.makeText(InfoPedido.this, message, Toast.LENGTH_SHORT).show();
                                }catch (Exception e) {
                                    e.printStackTrace();
                                    Toast.makeText(InfoPedido.this, Invariante.ERROR_LOGIN_RED_ACCESO, Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                {

                    /**
                     * Passing some request headers
                     */
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        HashMap<String, String> headers = new HashMap<String, String>();
                        headers.put("Authorization", sharedpreferences.getString(Invariante.TOKEN,""));
                        return headers;
                    }

                    /**
                     * Passing some parameters
                     */

                    /*
                    protected Map<String, String> getParams()
                    {
                        Map<String, String>  params = new HashMap<String, String>();
                        params.put("purchase", Integer.toString(pedido.getCodigo()));

                        return params;
                    }*/
                };
                requestQueue.add(jsonObjectRequest);

            }else{
                Toast.makeText(this, "IP y/o puerto del servidor no configurado. ", Toast.LENGTH_LONG).show();
            }

        }catch (Exception e) {
            e.printStackTrace();
        }




    }
    void finalizarRuta(){
        /*
        Funcion que se activa cuando el repartidor presiona el boton de finalizar ruta.

        */
        this.on_my_way = false;

        asociarTag(InfoPedido.this.getCurrentFocus());
    }



}

