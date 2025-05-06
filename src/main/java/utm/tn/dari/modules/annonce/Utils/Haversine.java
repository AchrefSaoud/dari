package utm.tn.dari.modules.annonce.Utils;

public class Haversine {

    private static final int EARTH_RADIUS_KM = 6371; // Radius of the earth

    public static double distance(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS_KM * c;
    }

    public static void main(String[] args) {
        System.out.println(distance(0,0,57.25530302115965,263.1249618530274));
    }
}