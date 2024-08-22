package com.genaro.myapplication

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.genaro.myapplication.databinding.ActivityMainBinding
import kotlinx.coroutines.*
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Marker
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var destinoAdapter: DestinoAdapter
    private val destinos = mutableListOf<String>()
    private val baseUrl = "https://nominatim.openstreetmap.org/search?"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Configuración inicial de OSMDroid
        Configuration.getInstance().load(this, androidx.preference.PreferenceManager.getDefaultSharedPreferences(this))

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        destinoAdapter = DestinoAdapter(destinos)
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = destinoAdapter

        // Inicializar MapView
        binding.mapView.setTileSource(TileSourceFactory.MAPNIK)
        binding.mapView.controller.setZoom(15.0)
        val startPoint = GeoPoint(-34.599722222222, -58.381944444444) // Buenos Aires como punto de partida
        binding.mapView.controller.setCenter(startPoint)

        // Manejar el botón de agregar
        binding.addButton.setOnClickListener {
            val nuevoDestino = binding.inputEditText.text.toString()
            if (nuevoDestino.isNotEmpty()) {
                buscarCoordenadas(nuevoDestino)
            } else {
                Toast.makeText(this, "Por favor, ingresa una dirección.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun buscarCoordenadas(direccion: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val ciudad = "Capital+Federal,+Argentina"
            val urlString = "$baseUrl&q=${direccion.replace(" ", "+")},+$ciudad&format=json"
            val url = URL(urlString)
            val connection = url.openConnection() as HttpURLConnection
            try {
                val inputStream = connection.inputStream
                val result = inputStream.bufferedReader().use { it.readText() }
                val jsonArray = org.json.JSONArray(result)

                if (jsonArray.length() > 0) {
                    val jsonObject = jsonArray.getJSONObject(0)
                    val lat = jsonObject.getDouble("lat")
                    val lon = jsonObject.getDouble("lon")
                    withContext(Dispatchers.Main) {
                        val geoPoint = GeoPoint(lat, lon)
                        addMarkerAtPoint(geoPoint, direccion)
                        destinos.add(direccion)
                        destinoAdapter.notifyItemInserted(destinos.size - 1)
                        binding.inputEditText.text.clear()
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@MainActivity, "No se encontró la dirección en Capital Federal", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@MainActivity, "Error al buscar coordenadas: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            } finally {
                connection.disconnect()
            }
        }
    }

    private fun addMarkerAtPoint(point: GeoPoint, direccion: String) {
        val marker = Marker(binding.mapView)
        marker.position = point
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        marker.title = direccion
        binding.mapView.overlays.add(marker)
        binding.mapView.invalidate()  // Refresca el mapa para mostrar el marcador
    }
}
