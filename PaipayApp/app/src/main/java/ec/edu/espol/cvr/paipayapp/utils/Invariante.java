package ec.edu.espol.cvr.paipayapp.utils;

/**
 * Esta clase define todas las cadenas de caracteres fijos que se utilizarán en la aplicación.
 * @author: Mauricio Leiton Lázaro(mdleiton)
 * @version: 1.0
 */
public class Invariante {
    public static final String MyPREFERENCES = "LoginPaipay";
    public static final String format_date = "dd/MM/yyyy";
    public static final String FORMAT_API_FECHA = "yyyy-MM-dd'T'HH:mm:ss.SSSSSS";
    public static final String PRUEBA = "Modo prueba activado.";
    public static final String TOKEN = "access-token";
    public static final String ROUTE = "route_id";
    public static final int LOCATION_INTERVAL_MIN = 0; //8000
    public static final int LOCATION_DISTANCE_MIN = 0;

    /*Login.java*/
    public static final String ERROR_CORREO = "Correo no válido.";
    public static final String ERROR_LOGIN_1 = "Usuario y contraseña no pueden quedar en blanco";
    public static final String ERROR_LOGIN_ROL = "Rol no disponible.";
        //roles de usuario
    public static final String USUARIO_ADMIN = "0";
    public static final String USUARIO_REPARTIDOR = "2";
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
    public static final String MENSAJE_ESCANEO = "Escanea el Código de Barra";

    public static final String ERROR_INTENTE_N = "Ocurrió un problema, intente de nuevo.";
    public static final String ERROR_INCOMPLETO_PEDIDO = "debe agregar todos los productos al pedido..";
    public static final String ERROR_INCOMPLETO_TAG = "Falta asociar un código de barras(tag)..";
    public static final String FINALIZAR_OK = "Pedido armado y asignado correctamente.";
    public static final String FOTO_OK = "Foto asociada correctamente al pedido.";
    public static final String CODIGO_OK = "Codigo de barras asociado correctamente al pedido.";

    public static final String PUNTO_REPARTO = "punto_reparto";
    public static final String ERROR_PUNTO_REPARTO = "No tiene configurado un punto de reparto. Debe configurar uno. ";


    /* InfoPedido.java*/
    public static final String CREATE_ROUTE_ERROR = "Requerimiento no procesado, inténtelo de nuevo";

    public static final int ESTADO_SOLICITADO = 0;
    public static final int ESTADO_ARMADO = 1;
    public static final int ESTADO_POR_ENTREGAR = 2;
    public static final int ESTADO_ENTREGADO = 3;
    public static final int ESTADO_CANCELADO = -1;


    public static String get_estado_str(int id){
        switch (id){
            case ESTADO_SOLICITADO:
                return "SOLICITADO";
            case ESTADO_ARMADO:
                return "ARMADO";
            case ESTADO_POR_ENTREGAR:
                return "POR ENTREGAR";
            case ESTADO_ENTREGADO:
                return "ENTREGADO";
            case ESTADO_CANCELADO:
                return "CANCELADO";
            default:
                return "Estado Desconocido";
        }
    }

    public static String get_server(String ip, int port){
        return "http://" + ip + ":" + port;
    }

    /* login -> roles de usuario*/
    public static final String TOKEN_PRUEBA = "token1";

    /*registro.java*/
    public static final String REGISTRO_OK = "Registro Satisfactorio.";
}
