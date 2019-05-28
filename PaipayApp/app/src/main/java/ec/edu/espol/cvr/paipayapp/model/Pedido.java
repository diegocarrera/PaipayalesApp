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
    private String fecha;
    private int codigo;
    private File foto_pedido;
    private String codigo_barra;
    private String user;
    private String repartidor;
    private String direccion;
    private String estado;
    private ArrayList<DetallePedido> detallePedidos;

    public Pedido(int codigo) {
        this.codigo = codigo;
        this.codigo_barra = "";
    }

    public Pedido(String fecha, int codigo) {
        this.fecha = fecha.substring(0,19).replace("T"," ");
        this.codigo = codigo;
        this.codigo_barra = "";
    }
    public Pedido(int codigo, String direccion) {
        this.codigo = codigo;
        this.codigo_barra = "";
        this.direccion=direccion;
    }

    public Pedido(String fecha, int codigo, ArrayList<DetallePedido> detallePedidos) {
        this.fecha = fecha;
        this.codigo = codigo;
        this.detallePedidos = detallePedidos;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

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

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        if(fecha.length()> 19){
            fecha = fecha.substring(0,19).replace("T"," ");
        }
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
}
