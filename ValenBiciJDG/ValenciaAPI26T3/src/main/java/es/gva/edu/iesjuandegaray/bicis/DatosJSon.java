package es.gva.edu.iesjuandegaray.bicis;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class DatosJSon {
    private static final String API_URL = "https://geoportal.valencia.es/server/rest/services/OPENDATA/Trafico/MapServer/228/query?where=1=1&outFields=*&f=json";

    public static String obtenerDatosValenbisi() {
    	try {
            URL url = new URL(API_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("User-Agent", "Mozilla/5.0");
            
            if (conn.getResponseCode() == 200) {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String linea;
                while ((linea = in.readLine()) != null) response.append(linea);
                in.close();
                
                String resultado = response.toString();
                
                System.out.println("DATOS RECIBIDOS DE LA API:");
                System.out.println(resultado); 
                
                return resultado;
            } else {
                System.out.println("Error en la conexión: Código " + conn.getResponseCode());
                return "ERROR";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "ERROR";
        }
    }
}