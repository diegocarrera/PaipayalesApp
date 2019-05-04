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
import java.util.Iterator;
import java.util.Map;

import ec.edu.espol.cvr.paipayapp.model.User;

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

    public static User login(String email, String password){
        /*Funcion que hace request al API del servidor para el login
        Parametros: email, password
        Respuesta: rol, user_id, token. Estos datos se encapsulan en una clase User */

        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("email", email);
        parameters.put("password", password);
        try {
            JSONObject response = request("/api/v1/auth/login/", "POST", parameters);
            if(response.getInt("response_code") == 200){
                JSONObject response_rol =  new JSONObject(response.getString("data"));
                String rol = response_rol.getString("rol");
                String token = response_rol.getString("token");
                User user = new User(rol,token);
                return user;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static JSONObject request(String path, String type, Map<String, String> parameters){
        JSONObject json = new JSONObject();
        String error = null;
        {
            try {
                url = new URL(server + path);
                HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();
                httpCon.setRequestMethod(type);
                httpCon.setConnectTimeout(3000);
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

    public static JSONObject request_with_headers(String path, String type, Map<String, String> parameters, Map<String, String> headers){
        JSONObject json = new JSONObject();
        String error = null;
        {
            try {
                url = new URL(server + path);
                HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();
                httpCon.setRequestMethod(type);
                httpCon.setConnectTimeout(3000);
                httpCon.setRequestProperty("Accept", "application/json");
                httpCon.setDoOutput(true);
                if (parameters != null){
                    DataOutputStream out = new DataOutputStream(httpCon.getOutputStream());
                    out.writeBytes(ParameterStringBuilder.getParamsString(parameters));
                    out.flush();
                    out.close();
                }
                if (headers!=null){
                    Iterator it = headers.entrySet().iterator();
                    while (it.hasNext()) {
                        Map.Entry pair = (Map.Entry)it.next();
                        String key = (String) pair.getKey();
                        String value = (String) pair.getValue();
                        System.out.println(pair.getKey() + " = " + pair.getValue());
                        httpCon.setRequestProperty(key,value);
                    }
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

