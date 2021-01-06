package com.dan.codessert

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar


class MapsFragment : Fragment() {
    private lateinit var auxView: View
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        auxView = inflater.inflate(R.layout.fragment_maps, container, false)
        return auxView
    }

    lateinit var mapFragment: SupportMapFragment

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mapFragment = (childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?)!!
        mapFragment.getMapAsync(callback)
    }




    private val callback = OnMapReadyCallback { googleMap ->
        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        var results1 = FloatArray(1)
        var results2 = FloatArray(1)

        val place1 = LatLng(28.685425667896176, -100.55803492575642)
        val place2 = LatLng(28.690641462148548, -100.54506871081861)
        val currentLocation = LatLng(this.arguments!!.getDouble("latitude"), this.arguments!!.getDouble("longitude"))
        googleMap.addMarker(MarkerOptions().position(currentLocation).title("Tu ubicacion"))


        googleMap.addMarker(MarkerOptions().position(place1).title("Main"))
        googleMap.addMarker(MarkerOptions().position(place2).title("Second"))
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 12f))


        Location.distanceBetween(place1.latitude, place1.longitude, currentLocation.latitude, currentLocation.longitude, results1)
        Location.distanceBetween(currentLocation.latitude, currentLocation.longitude, place2.latitude, place2.longitude, results2)

        //Toast.makeText(activity!!, "Distancia a Main: ${results1[0]}, Distancia a Second: ${results2[0]}", Toast.LENGTH_LONG).show()

        if (results1[0] > results2[0])
            auxView.findViewById<TextView>(R.id.tvRecommendedPlace).text = "Te recomendamos ir al local Second"

        else if (results2[0] > results1[0])
            auxView.findViewById<TextView>(R.id.tvRecommendedPlace).text = "Te recomendamos ir al local Main"
    }
}