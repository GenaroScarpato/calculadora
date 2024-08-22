package com.genaro.myapplication

import android.os.Bundle
import android.view.MotionEvent
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.genaro.myapplication.databinding.ActivityMainBinding
import kotlinx.coroutines.*
import org.json.JSONObject
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
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

        // Manejar toques en el mapa
        binding.mapView.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                val geoPoint = binding.mapView.projection.fromPixels(event.x.toInt(), event.y.toInt()) as GeoPoint
                addMarkerAtPoint(geoPoint)
            }
            true
        }

        binding.addButton.setOnClickListener {
            val nuevoDestino = binding.inputEditText.text.toString()
            if (nuevoDestino.isNotEmpty()) {
                buscarCoordenadas(nuevoDestino)
            }
        }
    }

    private fun buscarCoordenadas(direccion: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val urlString = "$baseUrl&q=${direccion.replace(" ", "+")}&format=json"
            val url = URL(urlString)
            val connection = url.openConnection() as HttpURLConnection
            try {
                val inputStream = connection.inputStream
                val result = inputStream.bufferedReader().use { it.readText() }
                val jsonArray = JSONObject(result).optJSONArray("results")

                if (jsonArray != null && jsonArray.length() > 0) {
                    val jsonObject = jsonArray.getJSONObject(0)
                    val lat = jsonObject.getDouble("lat")
                    val lon = jsonObject.getDouble("lon")
                    withContext(Dispatchers.Main) {
                        val geoPoint = GeoPoint(lat, lon)
                        addMarkerAtPoint(geoPoint)
                        destinos.add(direccion)
                        destinoAdapter.notifyItemInserted(destinos.size - 1)
                        binding.inputEditText.text.clear()
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@MainActivity, "No se encontró la dirección", Toast.LENGTH_SHORT).show()
                    }
                }
            } finally {
                connection.disconnect()
            }
        }
    }

    private fun addMarkerAtPoint(point: GeoPoint) {
        val marker = Marker(binding.mapView)
        marker.position = point
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        binding.mapView.overlays.add(marker)
        binding.mapView.invalidate()  // Refresca el mapa para mostrar el marcador
    }
}
