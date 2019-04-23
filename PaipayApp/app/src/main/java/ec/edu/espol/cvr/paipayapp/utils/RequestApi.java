package ec.edu.espol.cvr.paipayapp.utils;

import android.os.NetworkOnMainThreadException;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class RequestApi {
    private static String ip;
    private static int port;
    private static URL url;
    static String server;

    public static void set_network(String ip, int port){
        ip = ip;
        port = port;
        server = "http://" + ip + ":" + port;
    }

    public static JSONObject login(String email, String password){
        JSONObject respuesta = new JSONObject();
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("email", email);
        parameters.put("password", password);
        try {
            JSONObject response = request("/api/v1/auth/login", "POST", parameters);
            respuesta.put("response_code", response.getInt("response_code"));
            if(response.getInt("response_code") == 200){
                JSONObject contenido =  new JSONObject(response.getString("data"));
                respuesta.put("rol", contenido.getString("rol"));
                respuesta.put("access-token", contenido.getString("token"));
            }else if(response.getInt("response_code") == 401 ||response.getInt("response_code") == 400 ){
                respuesta.put("error", response.getString("message"));
            }else{
                respuesta.put("error", response.getString("error"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return respuesta;
    }

    public static JSONObject request(String path, String type, Map<String, String> parameters){
        JSONObject json = new JSONObject();
        String error = null;
        {
            try {
                System.out.println(server + path);
                url = new URL(server + path);
                HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();
                httpCon.setRequestMethod(type);
                httpCon.setConnectTimeout(10000);
                httpCon.setRequestProperty("Accept", "application/json");
                httpCon.setDoOutput(true);
                if (parameters != null){

                    DataOutputStream out = new DataOutputStream(httpCon.getOutputStream());
                    out.writeBytes(ParameterStringBuilder.getParamsString(parameters));
                    out.flush();
                    out.close();
                }
                httpCon.connect();

                int httpResponse = httpCon.getResponseCode();  // si es diferente de 0, deberia enviar error
                BufferedReader br = new BufferedReader(new InputStreamReader((httpCon.getInputStream())));
                StringBuilder sb = new StringBuilder();
                String output;
                while ((output = br.readLine()) != null)
                    sb.append(output);
                json.put("data", sb.toString());
                json.put("response_code", new Integer(httpResponse));
                httpCon.disconnect();
                return json;
            } catch (MalformedURLException e) {
                error = "MalformedURLException";
            } catch (IOException e) {
                error = "IOException";
            } catch (JSONException e) {
                error = "JSONException";
            }catch (NetworkOnMainThreadException e) {
                error = "No se pudo establecer conexión con el servidor";
            }
            catch (Exception e) {
                error = "Exception";
            }
        }
        try {
            json.put("response_code", new Integer(404));
            if(error != null){
                System.out.println(error);
                json.put("error", error);
            }
        } catch (JSONException e1) {
            e1.printStackTrace();
        }
        return json;
    }
}

