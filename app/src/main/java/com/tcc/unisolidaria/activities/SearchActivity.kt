package com.tcc.unisolidaria.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.ListenerRegistration
import com.tcc.unisolidaria.databinding.ActivitySearchBinding
import com.tcc.unisolidaria.models.Booking
import com.tcc.unisolidaria.models.Driver
import com.tcc.unisolidaria.models.FCMBody
import com.tcc.unisolidaria.models.FCMResponse
import com.tcc.unisolidaria.providers.AuthProvider
import com.tcc.unisolidaria.providers.BookingProvider
import com.tcc.unisolidaria.providers.DriverProvider
import com.tcc.unisolidaria.providers.GeoProvider
import com.tcc.unisolidaria.providers.NotificationProvider
import org.imperiumlabs.geofirestore.callbacks.GeoQueryEventListener
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchActivity : AppCompatActivity() {

    private var listenerBooking: ListenerRegistration? = null
    private lateinit var binding: ActivitySearchBinding
    private var extraOriginName = ""
    private var extraDestinationName = ""
    private var extraOriginLat = 0.0
    private var extraOriginLng = 0.0
    private var extraDestinationLat = 0.0
    private var extraDestinationLng = 0.0
    private var extraTime = 0.0
    private var extraDistance = 0.0
    private var originLatLng: LatLng? = null
    private var destinationLatLng: LatLng? = null

    private val geoProvider = GeoProvider()
    private val authProvider = AuthProvider()
    private val bookingProvider = BookingProvider()
    private val notificationProvider = NotificationProvider()
    private val driverProvider = DriverProvider()


    // PESQUISA DE MOTORISTA
    private var radius = 0.2
    private var idDriver = ""
    private var driver: Driver? = null
    private var isDriverFound = false
    private var driverLatLng: LatLng? = null
    private var limitRadius = 20



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)

        // EXTRAS
        extraOriginName = intent.getStringExtra("origin")!!
        extraDestinationName = intent.getStringExtra("destination")!!
        extraOriginLat = intent.getDoubleExtra("origin_lat", 0.0)
        extraOriginLng = intent.getDoubleExtra("origin_lng", 0.0)
        extraDestinationLat = intent.getDoubleExtra("destination_lat", 0.0)
        extraDestinationLng = intent.getDoubleExtra("destination_lng", 0.0)
        extraTime = intent.getDoubleExtra("time", 0.0)
        extraDistance = intent.getDoubleExtra("distance", 0.0)
        originLatLng = LatLng(extraOriginLat, extraOriginLng)
        destinationLatLng = LatLng(extraDestinationLat, extraDestinationLng)

        getClosestDriver()
        checkIfDriverAccept()
    }

    private fun sendNotification() {

        val map = HashMap<String, String>()
        map.put("title", "PEDIDO DE VIAGEM")
        map.put(
            "body",
            "Um cliente está solicitando uma viagem para " +
                    "${String.format("%.1f",extraDistance)}km y " +
                    "${String.format("%.1f", extraTime)}Min"
        )
        map.put("idBooking", authProvider.getId())

        val body = FCMBody(
            to = driver?.token!!,
            priority = "high",
            ttl = "4500s",
            data = map
        )

        notificationProvider.sendNotification(body).enqueue(object: Callback<FCMResponse> {
            override fun onResponse(call: Call<FCMResponse>, response: Response<FCMResponse>) {
                if (response.body() != null) {

                    if (response.body()!!.success == 1) {
                        Toast.makeText(this@SearchActivity, "A notificação foi enviada", Toast.LENGTH_LONG).show()
                    }
                    else {
                        Toast.makeText(this@SearchActivity, "Não foi possível enviar notificação", Toast.LENGTH_LONG).show()
                    }

                }
                else {
                    Toast.makeText(this@SearchActivity, "ocorreu um erro ao enviar a notificação", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<FCMResponse>, t: Throwable) {
                Log.d("NOTIFICATION", "ERROR: ${t.message}")
            }

        })
    }

    private fun checkIfDriverAccept() {
        listenerBooking = bookingProvider.getBooking().addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.d("FIRESTORE", "ERROR: ${e.message}")
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists()) {
                val booking = snapshot.toObject(Booking::class.java)

                if (booking?.status == "accept") {
                    Toast.makeText(this@SearchActivity, "Viagem aceita", Toast.LENGTH_SHORT).show()
                    listenerBooking?.remove()
                    goToMapTrip()
                }
                else if (booking?.status == "cancel") {
                    Toast.makeText(this@SearchActivity, "Viagem cancelada", Toast.LENGTH_SHORT).show()
                    listenerBooking?.remove()
                    goToMap()
                }

            }
        }
    }

    private fun goToMapTrip() {
        val i = Intent(this, MapTripActivity::class.java)
        startActivity(i)
    }

    private fun goToMap() {
        val i = Intent(this, MapActivity::class.java)
        i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(i)
    }

    private fun createBooking(idDriver: String) {

        val booking = Booking(
            idClient = authProvider.getId(),
            idDriver = idDriver,
            status = "create",
            destination = extraDestinationName,
            origin = extraOriginName,
            time = extraTime,
            km = extraDistance,
            originLat = extraOriginLat,
            originLng = extraOriginLng,
            destinationLat = extraDestinationLat,
            destinationLng = extraDestinationLng
        )

        bookingProvider.create(booking).addOnCompleteListener {
            if (it.isSuccessful) {
                Toast.makeText(this@SearchActivity, "Dados de viagem criados", Toast.LENGTH_LONG).show()
            }
            else {
                Toast.makeText(this@SearchActivity, "Erro ao criar dados", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun getDriverInfo() {

        driverProvider.getDriver(idDriver).addOnSuccessListener { document ->
            if (document.exists()) {
                driver = document.toObject(Driver::class.java)
                sendNotification()
            }
         }

    }

    private fun getClosestDriver() {
        geoProvider.getNearbyDrivers(originLatLng!!, radius).addGeoQueryEventListener(object: GeoQueryEventListener {

            override fun onKeyEntered(documentID: String, location: GeoPoint) {
                if (!isDriverFound) {
                    isDriverFound = true
                    idDriver = documentID
                    getDriverInfo()
                    Log.d("FIRESTORE", "motorista id: $idDriver")
                    driverLatLng = LatLng(location.latitude, location.longitude)
                    binding.textViewSearch.text = "MOTORISTA ENCONTRADO\nESPERANDO RESPONDER"
                    createBooking(documentID)
                }
            }

            override fun onKeyExited(documentID: String) {

            }

            override fun onKeyMoved(documentID: String, location: GeoPoint) {

            }

            override fun onGeoQueryError(exception: Exception) {

            }

            override fun onGeoQueryReady() { // FINALIZAR PESQUISA
                if (!isDriverFound) {
                    radius = radius + 0.2

                    if (radius > limitRadius) {
                        binding.textViewSearch.text = "NENHUM MOTORISTA FOI ENCONTRADO"
                        return
                    }
                    else {
                        getClosestDriver()
                    }
                }
            }

        })
    }

    override fun onDestroy() {
        super.onDestroy()
        listenerBooking?.remove()
    }
}