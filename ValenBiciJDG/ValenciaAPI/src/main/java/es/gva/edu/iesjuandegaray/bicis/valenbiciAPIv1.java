package es.gva.edu.iesjuandegaray.bicis;


import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;

public class valenbiciAPIv1 {
	
	private static final String API_URL = "https://valencia.opendatasoft.com/api/explore/v2.1/catalog/datasets/valenbisi-disponibilitat-valenbisi-dsiponibilidad/records?limit=20";

    public static void main(String[] args) {
        if (API_URL.isEmpty()) {
            System.err.println("La URL de la API no está especificada.");
            return;
        }

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(API_URL);
            HttpResponse response = httpClient.execute(request);

            HttpEntity entity = response.getEntity();
            if (entity != null) {
                String result = EntityUtils.toString(entity);

                // Intentamos procesar la respuesta como JSON
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    // Accedemos al array "results" que contiene los datos
                    JSONArray resultsArray = jsonObject.getJSONArray("results");

                    System.out.println("--- Listado de Estaciones Valenbisi ---");
                    
                    // Recorremos el array
                    for (int i = 0; i < resultsArray.length(); i++) {
                        JSONObject estacion = resultsArray.getJSONObject(i);

                        // Obtenemos los campos solicitados
                        String nombre = estacion.getString("name");
                        int bicis = estacion.getInt("available");
                        int espacios = estacion.getInt("free");

                        // Mostramos los datos
                        System.out.println("Estación: " + nombre);
                        System.out.println("  > Bicicletas disponibles: " + bicis);
                        System.out.println("  > Espacios libres: " + espacios);
                        System.out.println("---------------------------------------");
                    }

                } catch (org.json.JSONException e) {
                    System.err.println("Error al procesar el formato JSON: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("Error de conexión: " + e.getMessage());
            e.printStackTrace();
        }
    }

}
