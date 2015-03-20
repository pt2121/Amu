/*
 * Copyright (c) 2015 Prat Tanapaisankit and Intellibins authors
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *  Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 *  Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *  Neither the name of The Intern nor the names of its contributors may
 * be used to endorse or promote products derived from this software
 * without specific prior written permission.
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE LISTED COPYRIGHT HOLDERS BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.prt2121.amu.ui;


import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import com.prt2121.amu.AmuApp;
import com.prt2121.amu.MapUtils;
import com.prt2121.amu.R;
import com.prt2121.amu.model.Loc;
import com.prt2121.amu.userlocation.IUserLocation;

import android.app.Activity;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MapFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MapFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MapFragment extends Fragment {

    private static final String TAG = MapFragment.class.getSimpleName();

    @Inject
    IUserLocation mUserLocation;

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.

    private Loc mLoc;

    private OnFragmentInteractionListener mListener;

    private static final float ZOOM = 17f;

    private static final int MAX_LOCATION = Integer.MAX_VALUE;

    private Subscription mUserLocationSubscription, mMarkerSubscription;

    //Test Location : New York City Department of Health and Mental Hygiene
//    private final Loc mUserLoc = new Loc.Builder("Your Location")
//            .address("")
//            .latitude(40.715522)
//            .longitude(-74.002452)
//            .type(-1)
//            .build();

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameter.
     *
     * @return A new instance of fragment MapFragment.
     */
    public static MapFragment newInstance() {
        return new MapFragment();
    }

    public MapFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AmuApp.getInstance().getGraph().inject(this);
        findUserLocation();

        // TODO remove this hardcoded user loc
//        mLoc = mUserLoc;
    }

    private void findUserLocation() {
        mUserLocationSubscription = mUserLocation.locate()
                .filter(location -> location != null)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(location -> {
                    if (location == null) {
                        Log.d(TAG, "location is null");
                    } else {
                        Log.d(TAG, "lat " + location.getLatitude() + " lng " + location.getLongitude());
                        mLoc = new Loc.Build("Your Location", location.getLatitude(), location.getLongitude())
                                .type("User Location")
                                .build();
                        setUpMapIfNeeded(mLoc);
                    }
                }, t -> {
                    Log.d(TAG, t.getLocalizedMessage());
                });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (!mUserLocationSubscription.isUnsubscribed()) {
            mUserLocationSubscription.unsubscribe();
        }
        if (!mMarkerSubscription.isUnsubscribed()) {
            mMarkerSubscription.unsubscribe();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_map, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {

        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

    private void setUpMapIfNeeded(Loc loc) {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap(loc);
            }
        }
    }

    /**
     * Init map
     *
     * @param userLoc user's location
     */
    private void setUpMap(Loc userLoc) {
        mMarkerSubscription = updateMarkers(userLoc);
    }

    private Subscription updateMarkers(Loc userLoc) {
        LatLng latLng = new LatLng(userLoc.getLatitude(), userLoc.getLongitude());
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, ZOOM));
        mMap.addMarker(new MarkerOptions().position(latLng).title(userLoc.getShortName()));

        Location userLocation = new Location(userLoc.getShortName());
        userLocation.setLatitude(userLoc.getLatitude());
        userLocation.setLongitude(userLoc.getLongitude());
        Observable<Location> mockObservable = Observable.just(userLocation);

//        RecycleMachine recycleMachine = RecycleApp.getRecycleMachine(MapFragment.this.getActivity());
//
//        Observable<Loc> bin = ((flag & LocType.BIN) == LocType.BIN) ?
//                recycleMachine
//                        .findBin()
//                        .getLocs() : Observable.<Loc>empty();
//
//        Observable<Loc> dropOff = ((flag & LocType.DROPOFF) == LocType.DROPOFF) ?
//                recycleMachine
//                        .findDropOff()
//                        .getLocs() : Observable.<Loc>empty();
//
//        Observable<Loc> wholeFoods = ((flag & LocType.WHOLE_FOODS) == LocType.WHOLE_FOODS) ?
//                recycleMachine
//                        .findWholeFoods()
//                        .getLocs() : Observable.<Loc>empty();
        // TODO: remove Observable.empty()
        return MapUtils.showPins(getActivity(), mockObservable,
                Observable.empty(), mMap, MAX_LOCATION);
    }

}
