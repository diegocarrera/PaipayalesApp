package ec.edu.espol.cvr.paipayapp.model;
/**
 * Esta clase define la información de un Usuario.
 * @author: Mauricio Leiton Lázaro(mdleiton)
 * @version: 1.0
 */
public class User {
    String rol, token;

    public User(String rol, String token) {
        this.rol = rol;
        this.token = token;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

}
