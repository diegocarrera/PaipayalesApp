package ec.edu.espol.cvr.paipayapp.utils;

public class Invariante {
    public static final String MyPREFERENCES = "LoginPaipay";
    public static final String format_date = "dd/MM/yyyy";
    public static final String PRUEBA = "Modo prueba activado.";
    public static final String TOKEN = "access-token";

    /*Login.java*/
    public static final String ERROR_CORREO = "Correo no válido.";
    public static final String ERROR_LOGIN_1 = "Usuario y contraseña no pueden quedar en blanco";
    public static final String ERROR_LOGIN_ROL = "Rol no disponible.";
        //roles de usuario
    public static final String USUARIO_ADMIN = "Admin";
    public static final String USUARIO_REPARTIDOR = "Repartidor";
        //configuracion servidor
    public static final String CONF_ACTUALIZADO = "IP y puerto actualizados exitosamente. ";
    public static final String CONF_ERROR_1 = "IP y/o puerto del servidor no configurado. ";
    public static final String CONF_ERROR_2 = "IP y/o puerto vacios.";

    /* error RED */
    public static final String ERROR_LOGIN_RED = "Respuesta no procesable. Intente más tarde.";
    public static final String ERROR_LOGIN_RED_ACCESO = "Sin respuesta del servidor. Intente más tarde.";

    /*InfoPedidoAdmin.java*/
    public static final int REQUEST_IMAGE_CAPTURE = 1;
    public static final String path_fotos_pedidos = "/paipay/";


    public static final String PUNTO_REPARTO = "punto_reparto";
    public static final String ERROR_PUNTO_REPARTO = "No tiene configurado un punto de reparto. Debe configurar uno. ";



    public static String get_server(String ip, int port){
        return "http://" + ip + ":" + port;
    }

}
