package es.gva.edu.iesjuandegaray.bicis;

import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;

public class ValenbiciAPI {
    private static final String API_URL = 
        "https://valencia.opendatasoft.com/api/explore/v2.1/catalog/datasets/valenbisi-disponibilitat-valenbisi-dsiponibilidad/records?limit=20";

    public static void main(String[] args) {
        System.out.println("🚴 Iniciando Valenbisi API...");
        
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(API_URL);
            try (CloseableHttpResponse response = httpClient.execute(request)) {
                int statusCode = response.getCode();
                System.out.println("✅ Status: " + statusCode);

                if (statusCode == 200) {
                    String result;
                    try {
                        result = EntityUtils.toString(response.getEntity());
                    } catch (ParseException e) {
                        System.err.println("❌ Error parseando: " + e.getMessage());
                        return;
                    }
                    
                    try {
                        JSONObject json = new JSONObject(result);
                        JSONArray results = json.getJSONArray("results");

                        System.out.println("\n🚴=== ESTACIONES VALENBISI (" + results.length() + ") ===");
                        System.out.println(String.format("%-3s | %-25s | %4s | %5s | %s",
                            "ID", "ESTACIÓN", "BICIS", "APARC.", "DIRECCIÓN"));
                        System.out.println("-------------------------------------------------------");

                        for (int i = 0; i < Math.min(10, results.length()); i++) { // Solo 10 primeras
                            JSONObject est = results.getJSONObject(i);
                            int id = est.optInt("id_estacio", 0);
                            String nom = est.optString("nom_estacio", "N/A");
                            int bicis = est.optInt("bicis_disponibles", 0);
                            int aparc = est.optInt("aparcaments_disponibles", 0);
                            String dir = est.optString("adreca", "N/A");

                            System.out.printf("%-3d | %-25s | %4d | %5d | %s%n", 
                                id, nom, bicis, aparc, dir);
                        }
                        if (results.length() > 10) {
                            System.out.println("... y " + (results.length() - 10) + " más");
                        }

                    } catch (org.json.JSONException e) {
                        System.err.println("❌ Error JSON: " + e.getMessage());
                        System.out.println("🔍 Preview: " + result.substring(0, 500));
                    }
                } else {
                    System.err.println("❌ HTTP " + statusCode);
                }
            }
        } catch (IOException e) {
            System.err.println("🌐 Error conexión: " + e.getMessage());
        }
        System.out.println("🏁 Fin programa");
    }
}