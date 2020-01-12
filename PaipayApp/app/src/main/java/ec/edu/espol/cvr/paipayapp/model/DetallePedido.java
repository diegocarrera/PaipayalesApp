package ec.edu.espol.cvr.paipayapp.model;

/**
 * Esta clase define los componentes de un Items de un Pedido.
 * @author: Mauricio Leiton Lázaro(mdleiton)
 * @version: 1.0
 */
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