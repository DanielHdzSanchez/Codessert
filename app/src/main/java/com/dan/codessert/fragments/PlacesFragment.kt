package com.dan.codessert.fragments

import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.dan.codessert.R
import com.directions.route.*
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.material.snackbar.Snackbar
import java.util.jar.Manifest


class PlacesFragment : Fragment() {
    //google map object
    private lateinit var mMap: GoogleMap

    //current and destination location objects
    private lateinit var myLocation: Location
    private lateinit var destinationLocation: Location
    private lateinit var start: LatLng
    private lateinit var end: LatLng

    //to get location permissions.
    private val LOCATION_REQUEST_CODE = 23
    private var locationPermission = false
    private lateinit var auxView: View

    //polyline object
    private var polylines: ArrayList<Polyline>? = null

        override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {
            val view = inflater.inflate(R.layout.fragment_places, container, false)
            auxView = view
        return view
    }

}