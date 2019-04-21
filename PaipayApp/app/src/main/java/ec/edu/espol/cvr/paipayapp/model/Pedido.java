package ec.edu.espol.cvr.paipayapp.model;

import org.json.JSONException;
import org.json.JSONObject;
import ec.edu.espol.cvr.paipayapp.utils.RequestApi;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Pedido {
    private Date fecha;
    private int codigo;
    private File foto_pedido;
    private String codigo_barra;
    private String user;
    private String repartidor;

    public String getRepartidor() {
        return repartidor;
    }

    public void setRepartidor(String repartidor) {
        this.repartidor = repartidor;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    private String estado;

    public File getFoto_pedido() {
        return foto_pedido;
    }

    public void setFoto_pedido(File foto_pedido) {
        this.foto_pedido = foto_pedido;
    }

    public String getCodigo_barra() {
        return codigo_barra;
    }

    public void setCodigo_barra(String codigo_barra) {
        this.codigo_barra = codigo_barra;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    private ArrayList<DetallePedido> detallePedidos;

    public Pedido(int codigo) {
        this.codigo = codigo;
        this.codigo_barra = null;
    }

    public Pedido(Date fecha, int codigo) {
        this.fecha = fecha;
        this.codigo = codigo;
        this.codigo_barra = null;
    }

    public Pedido(Date fecha, int codigo, ArrayList<DetallePedido> detallePedidos) {
        this.fecha = fecha;
        this.codigo = codigo;
        this.detallePedidos = detallePedidos;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public int getCodigo() {
        return codigo;
    }

    public void setCodigo(int codigo) {
        this.codigo = codigo;
    }

    public ArrayList<DetallePedido> getDetallePedidos() {
        return detallePedidos;
    }

    public void setDetallePedidos(ArrayList<DetallePedido> detallePedidos) {
        this.detallePedidos = detallePedidos;
    }

    /* operaciones con la API */
    public static JSONObject get_pedidos(String ip, int port, String estado_pedidos, String bodega){
        RequestApi.set_network(ip, port);
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("estado", estado_pedidos);
        parameters.put("punto_reparto", bodega);
        JSONObject response = new JSONObject();
        try {
            response = RequestApi.request("/api/v1/pedidos/", "GET", parameters);
            if(response.getInt("response_code") == 200){
                //eliminar data
                response.put("pedidos", new JSONObject(response.getString("data")));
            }
            return response;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return response;
    }

    public boolean update(String ip, int port){
        RequestApi.set_network(ip, port);
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("codigo_barra", this.codigo_barra);
        parameters.put("codigo_pedido", String.valueOf(this.codigo));
        parameters.put("estado", "ARMADO");
        //FALTA ENVIAR FOTO
        JSONObject response = new JSONObject();
        try {
            response = RequestApi.request("/api/v1/pedidos/", "PUT", parameters);
            if(response.getInt("response_code") == 200){
                return true;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static Pedido consultar_pedido(int codigo, String ip, int port){
        RequestApi.set_network(ip, port);
        Pedido pedido = new Pedido(codigo);
        JSONObject response = new JSONObject();
        try {
            response = RequestApi.request("/api/v1/pedidos/" + String.valueOf(codigo) , "GET", null);
            if(response.getInt("response_code") == 200){
                JSONObject pedidoData =  new JSONObject(response.getString("data"));
                pedido.setEstado(pedidoData.getString("estado"));
                pedido.setRepartidor(pedidoData.getString("repartidor"));
                return pedido;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
