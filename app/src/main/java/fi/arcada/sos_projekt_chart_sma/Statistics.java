package fi.arcada.sos_projekt_chart_sma;

public class Statistics {

    public static double[] movingAvg(double[] values, int windowSize) {
        // Skapa en ny double-array för glidande medelvärden
        double[] sma = new double[values.length];

        // Loopa igenom datan för att beräkna glidande medelvärden
        for (int i = 0; i < values.length; i++) {
            // Variabel för summan av värdena inom fönstret
            double sum = 0;
            // Loopa igenom värdena inom fönstret
            int count = 0;
            for (int j = i; j >= 0 && j > i - windowSize; j--) {
                sum += values[j];
                count++;
            }
            // Räkna medelvärdet för fönstret och lägg till det i sma
            sma[i] = sum / count;
        }

        // Returnera arrayen med glidande medelvärden
        return sma;
    }
}
