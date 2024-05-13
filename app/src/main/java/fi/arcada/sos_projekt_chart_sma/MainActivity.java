package fi.arcada.sos_projekt_chart_sma;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    String currency, datefrom, dateto;
    LineChart chart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        chart = findViewById(R.id.chart);

        // TEMPORÄRA VÄRDEN
        currency = "USD";
        datefrom = "2024-01-01";
        dateto = "2024-05-15";

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

        double[] movingAverageValues = stats.movingAvg(currencyArray, 3);

        // displayar linjer valutadata och glidande medelvärden
        drawLineChart(currencyValues, movingAverageValues);

        // Hitta knapparna
        Button sma10Btn = findViewById(R.id.sma10Btn);
        Button sma30Btn = findViewById(R.id.sma30Btn);
        Button valutaBtn = findViewById(R.id.valutaBtn);

        // Set OnClickListener för varje knapp
        sma10Btn.setOnClickListener(this);
        sma30Btn.setOnClickListener(this);
        valutaBtn.setOnClickListener(this);
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

        // linje för ursprungliga datan från API
        LineDataSet dataSet = new LineDataSet(entries, "Valutakurs (" + currency + ")");
        dataSet.setColor(Color.BLUE);
        dataSet.setLineWidth(2f);
        dataSet.setDrawCircles(false);
        dataSet.setDrawValues(false);

        // linje för glidande medelvärden
        LineDataSet movingAverageDataSet = new LineDataSet(movingAverageEntries, "Glidande medelvärde (" + currency + ")");
        movingAverageDataSet.setColor(Color.RED);
        movingAverageDataSet.setLineWidth(2f);
        movingAverageDataSet.setDrawCircles(false);
        movingAverageDataSet.setDrawValues(false);


        LineData lineData = new LineData(dataSet, movingAverageDataSet);
        chart.setData(lineData);
        chart.invalidate();
    }

    // KNAPPAR
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.sma10Btn:
                if (datefrom.equals("2024-01-01")) {
                    datefrom = "2024-05-05";
                } else {
                    datefrom = "2024-01-01";
                }
                updateChart();
                break;

            case R.id.sma30Btn:
                if (datefrom.equals("2024-01-01")) {
                    datefrom = "2024-04-15";
                } else {
                    datefrom = "2024-01-01";
                }
                updateChart();
                break;

            case R.id.valutaBtn:
                if (currency.equals("USD")) {
                    currency = "SEK";
                } else {
                    currency = "USD";
                }
                updateChart();
                break;
        }
    }

    private void updateChart() {
        // Samma som i onCreate
        ArrayList<Double> currencyValues = getCurrencyValues(currency, datefrom, dateto);
        // Skriv ut dem i konsolen (Logcat)
        System.out.println("CurrencyValues: " + currencyValues.toString());

        // Beräkna glidande medelvärden med hjälp av Statistics-klassen
        Statistics stats = new Statistics();
        // Konvertera arrayList<Double> till double[]
        double[] currencyArray = new double[currencyValues.size()];
        for (int i = 0; i < currencyValues.size(); i++) {
            currencyArray[i] = currencyValues.get(i);
        }

        double[] movingAverageValues = stats.movingAvg(currencyArray, 3);

        // displayar linjerna valutadata och glidande medelvärden
        drawLineChart(currencyValues, movingAverageValues);
    }
}
