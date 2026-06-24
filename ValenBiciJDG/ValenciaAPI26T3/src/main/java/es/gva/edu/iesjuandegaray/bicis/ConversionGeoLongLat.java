package es.gva.edu.iesjuandegaray.bicis;

import org.locationtech.proj4j.*;

public class ConversionGeoLongLat {
    public static double[] convertirUTMaGPS(double utmX, double utmY) {
        CRSFactory factory = new CRSFactory();
        CoordinateReferenceSystem utm = factory.createFromName("EPSG:25830");
        CoordinateReferenceSystem wgs84 = factory.createFromName("EPSG:4326"); 
        
        CoordinateTransformFactory ctFactory = new CoordinateTransformFactory();
        CoordinateTransform transform = ctFactory.createTransform(utm, wgs84);
        
        ProjCoordinate result = new ProjCoordinate();
        transform.transform(new ProjCoordinate(utmX, utmY), result);
        
        return new double[]{result.y, result.x};
    }
}