package ec.edu.espol.cvr.paipayapp.model;

/**
 * Esta clase define los componentes de un Producto.
 * @author: Mauricio Leiton Lázaro(mdleiton)
 * @version: 1.0
 */
public class Producto {
    private String id;
    private String name;
    private String category;
    private String unit;

    public Producto(String id) {
        this.id = id;
        this.name = "";
        this.category = "";
        this.unit = "";
    }

    public Producto(String name, String category) {
        this.name = name;
        this.category = category;
    }

    public Producto(String name, String category, String unit) {
        this.name = name;
        this.category = category;
        this.unit = unit;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
}
