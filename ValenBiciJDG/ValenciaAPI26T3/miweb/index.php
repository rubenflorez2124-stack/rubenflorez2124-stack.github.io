<!DOCTYPE html>
<html lang="es">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Disponibilidad de ValenBisi</title>
<style>
    body {
        font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
        background-color: #f4f7f6;
        color: #333;
        line-height: 1.6;
        margin: 40px;
    }
    h1 {
        text-align: center;
        color: #2c3e50;
        font-weight: 300;
    }
    table {
        width: 90%;
        margin: 20px auto;
        border-collapse: collapse;
        background-color: #ffffff;
        box-shadow: 0 2px 5px rgba(0,0,0,0.05);
        border-radius: 8px;
        overflow: hidden;
    }
    th {
        background-color: #607d8b;
        color: #ffffff;
        padding: 15px;
        text-transform: uppercase;
        font-size: 0.85rem;
        letter-spacing: 1px;
    }
    td {
        border-bottom: 1px solid #eee;
        padding: 12px;
        text-align: center;
    }
    tr:hover {
        background-color: #f9f9f9;
    }
    /* Estilo elegante para el botón */
    .btn-mapa {
        display: block;
        width: 220px;
        margin: 30px auto;
        padding: 12px;
        text-align: center;
        background-color: #5d6d7e;
        color: white;
        text-decoration: none;
        border-radius: 4px;
        transition: background 0.3s ease;
    }
    .btn-mapa:hover {
        background-color: #34495e;
    }
</style>
</head>
<body>
<h1>Disponibilidad de ValenBisi</h1>

<?php
$baseUrl = "https://geoportal.valencia.es/server/rest/services/OPENDATA/Trafico/MapServer/228/query?where=1=1&outFields=*&returnGeometry=true&outSR=4326&f=json";



function epsg25830ToWgs84(float $easting, float $northing): array {
    $a = 6378137.0; $f = 1 / 298.257222101; $k0 = 0.9996;
    $zone = 30; $falseEasting = 500000.0; $falseNorthing = 0.0;
    $e = sqrt($f * (2 - $f)); $e1sq = ($e * $e) / (1 - $e * $e);
    $x = $easting - $falseEasting; $y = $northing - $falseNorthing;
    $m = $y / $k0;
    $mu = $m / ($a * (1 - pow($e, 2) / 4 - 3 * pow($e, 4) / 64 - 5 * pow($e, 6) / 256));
    $e1 = (1 - sqrt(1 - $e * $e)) / (1 + sqrt(1 - $e * $e));
    $j1 = 3 * $e1 / 2 - 27 * pow($e1, 3) / 32;
    $j2 = 21 * pow($e1, 2) / 16 - 55 * pow($e1, 4) / 32;
    $j3 = 151 * pow($e1, 3) / 96; $j4 = 1097 * pow($e1, 4) / 512;
    $fp = $mu + $j1 * sin(2 * $mu) + $j2 * sin(4 * $mu) + $j3 * sin(6 * $mu) + $j4 * sin(8 * $mu);
    $sinFp = sin($fp); $cosFp = cos($fp); $tanFp = tan($fp);
    $c1 = $e1sq * $cosFp * $cosFp; $t1 = $tanFp * $tanFp;
    $r1 = $a * (1 - $e * $e) / pow(1 - ($e * $e * $sinFp * $sinFp), 1.5);
    $n1 = $a / sqrt(1 - ($e * $e * $sinFp * $sinFp));
    $d = $x / ($n1 * $k0);
    $latRad = $fp - ($n1 * $tanFp / $r1) * (pow($d, 2) / 2 - (5 + 3 * $t1 + 10 * $c1 - 4 * $c1 * $c1 - 9 * $e1sq) * pow($d, 4) / 24 + (61 + 90 * $t1 + 298 * $c1 + 45 * $t1 * $t1 - 252 * $e1sq - 3 * $c1 * $c1) * pow($d, 6) / 720);
    $lonOrigin = deg2rad(($zone - 1) * 6 - 180 + 3);
    $lonRad = $lonOrigin + ($d - (1 + 2 * $t1 + $c1) * pow($d, 3) / 6 + (5 - 2 * $c1 + 28 * $t1 - 3 * $c1 * $c1 + 8 * $e1sq + 24 * $t1 * $t1) * pow($d, 5) / 120) / $cosFp;
    return ['latitude' => rad2deg($latRad), 'longitude' => rad2deg($lonRad)];
}

function normalizeValenbisiGeometry(array $geometry): array {
    $x = isset($geometry['x']) ? (float)$geometry['x'] : 0.0;
    $y = isset($geometry['y']) ? (float)$geometry['y'] : 0.0;
    if ($x >= -180 && $x <= 180 && $y >= -90 && $y <= 90) {
        return ['latitude' => $y, 'longitude' => $x, 'source_x' => $x, 'source_y' => $y];
    }
    $conv = epsg25830ToWgs84($x, $y);
    return ['latitude' => $conv['latitude'], 'longitude' => $conv['longitude'], 'source_x' => $x, 'source_y' => $y];
}

$allStations = [];
$ch = curl_init($baseUrl);
curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
curl_setopt($ch, CURLOPT_SSL_VERIFYPEER, false);
$response = curl_exec($ch);
curl_close($ch);
$data = json_decode($response, true);

if (isset($data["features"])) {
    foreach ($data["features"] as $station) {
        $geo = normalizeValenbisiGeometry($station['geometry'] ?? []);
        $allStations[$station['attributes']['number']] = [
            'address' => $station['attributes']['address'],
            'open' => ($station['attributes']['open'] == "T"),
            'available' => (int)$station['attributes']['available'],
            'free' => (int)$station['attributes']['free'],
            'total' => (int)$station['attributes']['total'],
            'updated_at' => $station['attributes']['updated_at'],
            'latitude' => round($geo['latitude'], 7),
            'longitude' => round($geo['longitude'], 7)
        ];
    }
    file_put_contents('data.json', json_encode($allStations));
    
    echo "<table><tr><th>Dirección</th><th>Número</th><th>Abierto</th><th>Disponibles</th><th>Libres</th><th>Total</th><th>Actualizado</th><th>Latitud</th><th>Longitud</th></tr>";
    foreach ($allStations as $num => $s) {
        echo "<tr><td>{$s['address']}</td><td>{$num}</td><td>" . ($s['open'] ? "Sí" : "No") . "</td><td>{$s['available']}</td><td>{$s['free']}</td><td>{$s['total']}</td><td>{$s['updated_at']}</td><td>{$s['latitude']}</td><td>{$s['longitude']}</td></tr>";
    }
    echo "</table>";
    
    echo '<a href="mapearbicis.php" class="btn-mapa">Ver Mapa de Estaciones</a>';
}
?>

</body>
</html>