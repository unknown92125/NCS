package com.mrex.ncs;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.error.AuthFailureError;
import com.android.volley.error.VolleyError;
import com.android.volley.request.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static com.mrex.ncs.MapFragment.currentLocation;

public class SearchFragment extends Fragment {

    private Context context;

    private ArrayList<AddressData> arrListData = new ArrayList<>();
    private SearchRecyclerAdapter searchRecyclerAdapter;

    private String address, addressName, placeName;
    private EditText etSearch;
    private InputMethodManager inputMethodManager;
    private JSONObject jsonObject;
    private Double x, y;

    public SearchFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        context = getActivity();
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        FragmentManager fragmentManager = getFragmentManager();

        RecyclerView recyclerView = view.findViewById(R.id.rv_search);
        searchRecyclerAdapter = new SearchRecyclerAdapter(arrListData, context, fragmentManager);
        recyclerView.setAdapter(searchRecyclerAdapter);

        inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);

        etSearch = getActivity().findViewById(R.id.et_address_search);
        etSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {

                    inputMethodManager.hideSoftInputFromWindow(etSearch.getWindowToken(), 0);
                    address = etSearch.getText().toString();
                    try {
                        address = URLEncoder.encode(address, "utf-8");

                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    arrListData.clear();
                    searchAddress();
                    searchRecyclerAdapter.notifyDataSetChanged();
                    etSearch.clearFocus();

                    return true;
                }
                return false;
            }
        });

        return view;
    }

    private void searchAddress() {
        String addressUrl = "https://dapi.kakao.com/v2/local/search/address.json";

        String getUrl = addressUrl + "?query=" + address;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, getUrl, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.e("SearchF","onResponse :"+ response.toString());
                parsingAddress(response);
            }

        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("SearchF", "onErrorResp:"+error );
            }
        }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map params = new HashMap();
                params.put("Authorization", "KakaoAK 0f1893776abf2a59938fbb2df9c478c2");
                return params;
            }

        };

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(jsonObjectRequest);
    }

    private void searchKeyword() {
        String keywordUrl = "https://dapi.kakao.com/v2/local/search/keyword.json";

        String getUrl = keywordUrl + "?query=" + address;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, getUrl, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.e("SearchF", "onResponse:"+response.toString());
                parsingKeyword(response);
            }

        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("SearchF","onErrorResp:"+ error );
            }
        }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map params = new HashMap();
                params.put("Authorization", "KakaoAK 0f1893776abf2a59938fbb2df9c478c2");
                return params;
            }

        };

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(jsonObjectRequest);
    }

    private void parsingAddress(JSONObject response) {

        try {
            JSONArray jsonArray = response.getJSONArray("documents");

            for (int i = 0; i < jsonArray.length(); i++) {
                jsonObject = jsonArray.getJSONObject(i);
                addressName = jsonObject.getString("address_name") + " ";
                placeName = "";
                x = jsonObject.getDouble("x");
                y = jsonObject.getDouble("y");

                arrListData.add(new AddressData(addressName, placeName, y, x));
            }

            searchKeyword();

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void parsingKeyword(JSONObject response) {

        try {
            JSONArray jsonArray = response.getJSONArray("documents");

            for (int i = 0; i < jsonArray.length(); i++) {
                jsonObject = jsonArray.getJSONObject(i);
                addressName = jsonObject.getString("address_name");
                placeName = " " + jsonObject.getString("place_name");
                x = jsonObject.getDouble("x");
                y = jsonObject.getDouble("y");

                arrListData.add(new AddressData(addressName, placeName, y, x));

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        sortData();
        searchRecyclerAdapter.notifyDataSetChanged();

        if (arrListData.size() == 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage("검색 결과가 없습니다");
            builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }

    }

    private void sortData() {
        Location locationA = new Location("A");
        Location locationB = new Location("B");
        AddressData addressData;

        locationA.setLatitude(currentLocation.latitude);
        locationA.setLongitude(currentLocation.longitude);
        for (int i = 0; i < arrListData.size(); i++) {
            addressData = arrListData.get(i);
            locationB.setLatitude(addressData.getLat());
            locationB.setLongitude(addressData.getLng());
            double distance = locationA.distanceTo(locationB);
            addressData.setDistance(distance);
            Log.e("SearchF", addressData.getDistance() + "");
        }

        Collections.sort(arrListData);

        for (int i = 0; i < arrListData.size(); i++) {
            addressData = arrListData.get(i);
            Log.e("SearchF", "2:"+addressData.getDistance() + "");
        }

    }

}
