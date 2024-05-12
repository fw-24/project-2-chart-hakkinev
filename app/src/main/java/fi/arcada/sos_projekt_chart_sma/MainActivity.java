package fi.arcada.sos_projekt_chart_sma;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    String currency, datefrom, dateto;
    LineChart chart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        chart = (LineChart) findViewById(R.id.chart);

        // TEMPORÄRA VÄRDEN
        currency = "USD";
        datefrom = "2024-01-01";
        dateto = "2024-03-31";

        // Hämta växelkurser från API
        ArrayList<Double> currencyValues = getCurrencyValues(currency, datefrom, dateto);
        // Skriv ut dem i konsolen (Logcat)
        System.out.println("CurrencyValues: " + currencyValues.toString());

        // Beräkna glidande medelvärden med hjälp av Statistics-klassen
        Statistics stats = new Statistics();
        // Konvertera ArrayList<Double> till double[]
        double[] currencyArray = new double[currencyValues.size()];
        for (int i = 0; i < currencyValues.size(); i++) {
            currencyArray[i] = currencyValues.get(i);
        }

        // Anropa movingAvg() med den konverterade arrayen
        double[] movingAverageValues = stats.movingAvg(currencyArray, 3);

        // Ritar charten med valutadata och glidande medelvärden
        drawLineChart(currencyValues, movingAverageValues);
    }


    // Färdig metod som hämtar växelkursdata
    public ArrayList<Double> getCurrencyValues(String currency, String from, String to) {

        CurrencyApi api = new CurrencyApi();
        ArrayList<Double> currencyData = null;

        String urlString = String.format("https://api.frankfurter.app/%s..%s",
                from.trim(),
                to.trim());

        try {
            String jsonData = api.execute(urlString).get();

            if (jsonData != null) {
                currencyData = api.getCurrencyData(jsonData, currency.trim());
                Toast.makeText(getApplicationContext(), String.format("Hämtade %s valutakursvärden från servern", currencyData.size()), Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Kunde inte hämta växelkursdata från servern: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

        return currencyData;
    }


    private void drawLineChart(ArrayList<Double> currencyValues, double[] movingAverageValues) {
        ArrayList<Entry> entries = new ArrayList<>();
        ArrayList<Entry> movingAverageEntries = new ArrayList<>();

        for (int i = 0; i < currencyValues.size(); i++) {
            entries.add(new Entry(i, currencyValues.get(i).floatValue()));
        }

        for (int i = 0; i < movingAverageValues.length; i++) {
            movingAverageEntries.add(new Entry(i, (float) movingAverageValues[i]));
        }

        // linje för ursprungliga datan (valutakursen)
        LineDataSet dataSet = new LineDataSet(entries, "Valutakurs (" + currency + ")");
        dataSet.setColor(Color.BLUE);
        dataSet.setLineWidth(2f);

        // linje för glidande medelvärden
        LineDataSet movingAverageDataSet = new LineDataSet(movingAverageEntries, "Glidande medelvärde (" + currency + ")");
        movingAverageDataSet.setColor(Color.RED);
        movingAverageDataSet.setLineWidth(2f);

        LineData lineData = new LineData(dataSet, movingAverageDataSet);
        chart.setData(lineData);
        chart.invalidate();
    }

    //test



}
