package fi.arcada.sos_projekt_chart_sma;

import android.os.AsyncTask;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

public class CurrencyApi extends AsyncTask<String, Void, String> {

    @Override
    protected String doInBackground(String... strings) {
        String response = new String();

        try {
            URL url = new URL(strings[0]);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));

            String nextLine;

            while ((nextLine = reader.readLine()) != null) {
                response += nextLine;
            }

            return response;
        } catch (Exception e) {
            System.out.println("error in doInBackground: " + e.getMessage());
            return null;
        }
    }

    public ArrayList<Double> getCurrencyData(String jsonData, String currency) {
        ArrayList<String> entries = new ArrayList<>();
        ArrayList<Double> rateValues = new ArrayList<>();

        try {
            JSONObject jsobj = new JSONObject(jsonData.trim());

            JSONObject jsonObjRates = jsobj.getJSONObject("rates");
            Iterator<String> keys = jsonObjRates.keys();

            while (keys.hasNext()) {
                entries.add(keys.next());
            }

            Collections.sort(entries);

            for (int i = 0; i < entries.size(); i++) {
                String rate = jsonObjRates.getJSONObject(entries.get(i)).getString(currency);
                rateValues.add(Double.parseDouble(rate));
                //System.out.println(entries.get(i) + " : " + rateValues.get(i));
            }
        } catch (Exception e) {
            System.out.println("ERROR getCurrencyData: " + e.getMessage());
        }

        return rateValues;
    }

}