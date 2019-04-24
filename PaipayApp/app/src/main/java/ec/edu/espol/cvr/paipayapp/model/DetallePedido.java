package ec.edu.espol.cvr.paipayapp.model;

import ec.edu.espol.cvr.paipayapp.utils.RequestApi;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class DetallePedido {
    private Producto producto;
    private float cantidad;
    private boolean selected;

    public DetallePedido(Producto producto, float cantidad) {
        this.producto = producto;
        this.cantidad = cantidad;
        selected = false;
    }

    public boolean is_selected() {
        return selected;
    }

    public void set_selected(boolean selected) {
        this.selected = selected;
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

}