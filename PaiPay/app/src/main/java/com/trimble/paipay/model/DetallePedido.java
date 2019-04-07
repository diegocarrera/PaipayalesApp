package com.trimble.paipay.model;

import com.trimble.paipay.utils.RequestApi;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class DetallePedido {
    private Producto producto;
    private float cantidad;

    public DetallePedido(Producto producto, float cantidad) {
        this.producto = producto;
        this.cantidad = cantidad;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public float getCantidad() {
        return cantidad;
    }

    public void setCantidad(float cantidad) {
        this.cantidad = cantidad;
    }

    public static JSONObject get_detalles_pedido(String ip, int port, int id_pedido){
        RequestApi.set_network(ip, port);
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("id_pedido", Integer.toString(id_pedido));
        try {
            JSONObject response = RequestApi.request("/api/v1/pedidos/", "GET", parameters);
            if(response.getInt("response_code") == 200){
                JSONObject json = new JSONObject(response.getString("data"));
                return json;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}