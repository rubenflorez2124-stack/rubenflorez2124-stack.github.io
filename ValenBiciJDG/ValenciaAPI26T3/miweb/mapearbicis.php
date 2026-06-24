<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Mapa Valenbisi - Personalizado</title>
    <link rel="stylesheet" href="https://unpkg.com/leaflet@1.9.4/dist/leaflet.css" />
    <script src="https://unpkg.com/leaflet@1.9.4/dist/leaflet.js"></script>
    <style>
        body { font-family: 'Segoe UI', sans-serif; background: #eef2f3; margin: 0; padding: 20px; }
        .controls { text-align: center; margin-bottom: 20px; }
        input { padding: 10px; width: 300px; border-radius: 20px; border: 1px solid #ccc; }
        #map { height: 500px; border-radius: 15px; box-shadow: 0 5px 15px rgba(0,0,0,0.2); }
        body {
        font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
        background-color: #f4f7f6;
        color: #333;
        line-height: 1.6;
        margin: 0;
        padding: 20px;
    }
    h1 {
        text-align: center;
        color: #2c3e50;
        font-weight: 300;
        margin-bottom: 30px;
    }
    #map { 
        height: 600px; 
        width: 90%; 
        margin: 0 auto; 
        border-radius: 8px; 
        box-shadow: 0 4px 10px rgba(0,0,0,0.1);
        border: 2px solid #ffffff;
    }
    .btn-volver {
        display: block;
        width: 200px;
        margin: 30px auto;
        padding: 10px;
        text-align: center;
        background-color: #95a5a6;
        color: white;
        text-decoration: none;
        border-radius: 4px;
        transition: background 0.3s ease;
    }
    .btn-volver:hover {
        background-color: #7f8c8d;
    }
    </style>
</head>
<body>

    <h1>Mapa de Valenbisi</h1>
    
    <div class="controls">
        <input type="text" id="searchInput" placeholder=" Buscar estación por nombre...">
    </div>
    
    <div id="map"></div>

    <script>
        var map = L.map('map').setView([39.47, -0.37], 13);
        L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png').addTo(map);

        let estaciones = []; // Array para guardar las estaciones

        fetch('data.json')
            .then(res => res.json())
            .then(data => {
                estaciones = Object.values(data);
                dibujarMarcadores(estaciones);
            });

        function dibujarMarcadores(lista) {
            map.eachLayer((layer) => { if (layer instanceof L.CircleMarker) map.removeLayer(layer); });
            
            lista.forEach(s => {
                L.circleMarker([s.latitude, s.longitude], {
                    color: s.available < 5 ? 'red' : 'green',
                    radius: 7
                }).addTo(map).bindPopup(`<b>${s.address}</b><br>Bicis: ${s.available}`);
            });
        }

        // Lógica del buscador
        document.getElementById('searchInput').addEventListener('input', (e) => {
            const busqueda = e.target.value.toLowerCase();
            const filtradas = estaciones.filter(s => s.address.toLowerCase().includes(busqueda));
            dibujarMarcadores(filtradas);
        });
    </script>
</body>
</html>