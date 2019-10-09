package com.mvelasquezp.clife.clientes.tools;

public class Constantes {

    public static final String APP_NAME = "LifeApp";

    public static final String DEFAULT_STRING = "";
    public static final int DEFAULT_INT = 0;
    public static final double DEFAULT_DOUBLE = 0.0;
    public static final boolean DEFAULT_BOOLEAN = false;
    public static final String DEFAULT_DATE = "1970-01-01";

    public static final String USER_EMPRESA = "empresa";
    public static final String USER_CODIGO = "codigo";
    public static final String USER_NCOMERCIAL = "nombre_comercial";
    public static final String USER_RSOCIAL = "razon_social";
    public static final String USER_EMAIL = "email";
    public static final String USER_TELEFONO = "telefono";
    public static final String USER_FREGISTRO = "fecha_registro";
    public static final String USER_TOKEN = "jwt_token";
    public static final String USER_TPUSUARIO = "tp_usuario";
    public static final String USER_SALON = "salon";
    public static final String USER_LOCAL = "local";
    public static final String USER_LOCAL_SELECCIONADO = "local_seleccionado";
    public static final String USER_DEPENDIENTE = "dependiente";
    public static final String USER_HABILITA_PUNTOS = "st_puntos";
    public static final String USER_PUBLICIDAD = "st_publicidad";

    public static final String DATE_FORMAT = "yyyy-MM-dd";
    public static final String TIME_FORMAT = "HH:mm:ss";
    public static final String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public static final int REQUEST_DETALLE_PROMO = 1;
    public static final int REQUEST_DETALLE_DOCUMENTO = 2;
    public static final int REQUEST_PAGO_DOCUMENTO = 3;
    public static final int REQUEST_INICIO_LOCALES = 4;
    public static final int REQUEST_INICIO_DEPENDIENTES_LOCAL = 5;
    public static final int REQUEST_INICIO_DEPENDIENTES_NUEVO = 6;
    public static final int REQUEST_INICIO_LOCAL_NUEVO = 7;
    public static final int REQUEST_RECLAMA_PUNTAJE = 8;
    public static final int REQUEST_PUNTOS_LISTA_LOCALES = 9;
    public static final int REQUEST_ASIGNAR_PUNTOS_DEPENDIENTE = 10;
    public static final int REQUEST_RECLAMA_PREMIO = 11;
    public static final int REQUEST_CALIFICA_PEDIDO = 12;

    public static final int REQUEST_IMAGEN_CAMARA = 80;
    public static final int REQUEST_IMAGEN_GALERIA = 81;
    public static final int REQUEST_QR_INTENT = 90;

    public static final String WS_STATE_PARAM = "status";
    public static final String WS_SUCCESS = "success";
    public static final String WS_DATA_ATTR = "data";
    public static final String WS_MESSAGE = "message";
    public static final String WS_REQUEST_ID = "rqid";

    public static final int WS_REQUEST_BUSQUEDA = 1;
    public static final int WS_REQUEST_REGISTRO_CLIENTES = 2;
    public static final int WS_REQUEST_LOGIN = 3;
    public static final int WS_REQUEST_CUENTA_CORRIENTE = 4;
    public static final int WS_REQUEST_DETALLE_PAGOS = 5;
    public static final int WS_REQUEST_ULTIMOS_PEDIDOS = 6;
    public static final int WS_REQUEST_INFO_CLIENTE = 7;
    public static final int WS_REQUEST_LISTA_LOCALES = 8;
    public static final int WS_REQUEST_LISTA_DEPENDIENTES_LOCAL = 9;
    public static final int WS_REQUEST_REGISTRA_DEPENDIENTE = 10;
    public static final int WS_REQUEST_REGISTRA_LOCAL = 11;
    public static final int WS_REQUEST_DATOS_DEPENDIENTE = 12;
    public static final int WS_REQUEST_AUTH_DEPENDIENTE = 13;
    public static final int WS_REQUEST_INFO_DEPENDIENTE = 14;
    public static final int WS_REQUEST_INFO_REPARTE_PUNTOS = 15;
    public static final int WS_REQUEST_ASIGNA_PUNTAJE = 16;
    public static final int WS_REQUEST_INFO_ASIGNAR_PTS_DEP = 17;
    public static final int WS_REQUEST_ASIGNA_PTS_DEP = 18;
    public static final int WS_REQUEST_STOCK_PUNTAJE = 19;
    public static final int WS_REQUEST_LISTA_PREMIOS = 20;
    public static final int WS_REQUEST_DETALLE_PREMIOS = 21;
    public static final int WS_REQUEST_RECLAMA_PREMIOS = 22;
    public static final int WS_REQUEST_DETALLE_FACTURA = 23;
    public static final int WS_REQUEST_CALIFICA_PEDIDO = 24;
    public static final int WS_REQUEST_PROCESA_PAGO = 25;
    public static final int WS_REQUEST_INFO_PERFIL = 26;
    public static final int WS_REQUEST_LISTA_LOGROS = 27;
    public static final int WS_REQUEST_RECLAMA_LOGRO = 28;
    public static final int WS_REQUEST_ACTUALIZA_PRIVILEGIOS = 29;
    public static final int WS_REQUEST_CARGA_DIRECCION = 30;
    //
    public static final int WS_REQUEST_RUCDNI_CLIENTE = 31;
    public static final int WS_REQUEST_DATOS_CLIENTE = 32;
    public static final int WS_REQUEST_GUARDAR_INFO_CLIENTE = 33;

    public static final int PERMISSION_IMAGES = 1;
    public static final int PERMISSION_CALL_PHONE = 2;

    public static final double NU_lATITUD_CLIFE = -12.091664;
    public static final double NU_LONGITUD_CLIFE = -77.027380;

    public static final String QR_PACKAGE_NAME = "com.google.zxing.client.android.SCAN";
    public static final String QR_ACTION_RESULT = "SCAN_RESULT";
    public static final String QR_MARKET_URI = "market://details?id=com.google.zxing.client.android";
}
