package com.example.myapplication;

import android.os.AsyncTask;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.collect.Maps;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ParseTask extends AsyncTask<LatLng, Void, Map<LatLng, Integer>> {

    @Override
    protected Map<LatLng, Integer> doInBackground(LatLng... latLngs) {
        final String baseUrl = "http://maps.googleapis.com/maps/api/directions/json";// путь к Geocoding API по
        // HTTP
        final Map<String, String> params = Maps.newHashMap();
        params.put("sensor", "true");// указывает, исходит ли запрос на геокодирование от устройства с датчиком
        params.put("language", "ru");// язык данные на котором мы хотим получить
        params.put("mode", "walking");// способ перемещения, может быть driving, walking, bicycling
        params.put("origin", latLngs[0].toString());// адрес или текстовое значение широты и
        // долготы отправного пункта маршрута
        params.put("destination", latLngs[1].toString());// адрес или текстовое значение широты и
        // долготы конечного пункта маршрута
        final String url = baseUrl + '?' + CustomParser.encodeParams(params);// генерируем путь с параметрами
        JSONObject response = new JSONObject();// делаем запрос к вебсервису и получаем от него ответ
        try {
            response = CustomParser.read(url);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONObject location = null;
        try {
            location = response.getJSONArray("routes").getJSONObject(0);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            location = location.getJSONArray("legs").getJSONObject(0);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        int duration = 0;
        try {
            duration = location.getJSONObject("duration").getInt("value");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Map<LatLng, Integer> mapToReturn = new HashMap<>();
        mapToReturn.put(latLngs[1], duration);
        return mapToReturn;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(Map<LatLng, Integer> map) {
        super.onPostExecute(map);
    }
}

