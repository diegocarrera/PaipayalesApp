package ec.edu.espol.cvr.paipayapp.model;

import org.json.JSONException;
import org.json.JSONObject;
import ec.edu.espol.cvr.paipayapp.utils.RequestApi;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Pedido {
    private Date fecha;
    private int codigo;
    private ArrayList<DetallePedido> detallePedidos;

    public Pedido(Date fecha, int codigo) {
        this.fecha = fecha;
        this.codigo = codigo;
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

    public static JSONObject get_pedidos(String ip, int port, String estado_pedidos){
        RequestApi.set_network(ip, port);
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("estado", estado_pedidos);
        JSONObject response = new JSONObject();
        try {
            //estado, bodega.
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
}
