package com.cleister.pdv.domain.util;

import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.util.DisplayMetrics;

import com.cleister.pdv.R;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.UUID;

/**
 * Created by Cleister on 12/03/2016.
 */
public class Util {

    public static String getCurrencyValue(double value){
        DecimalFormat ptBR = new DecimalFormat("#,##0.00");
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(new Locale("pt", "BR"));
        symbols.setDecimalSeparator(',');
        symbols.setGroupingSeparator(' ');
        ptBR.setDecimalFormatSymbols(symbols);

        if(value > 0.0d) {
            return " R$ " + ptBR.format(value);
        }else{
            return "";
        }
    }

    public static String getFormatedCurrency(String value){
        NumberFormat currency = NumberFormat.getCurrencyInstance();

        if(value == null){
            value="0.00";
        }
        return currency.format(Double.parseDouble(value));
    }

    /**
     * This method converts dp unit to equivalent pixels, depending on device density.
     *
     * @param dp A value in dp (density independent pixels) unit. Which we need to convert into pixels
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent px equivalent to dp depending on device density
     */
    public static float convertDpToPixel(float dp, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * ((float)metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return px;
    }

    /**
     * This method converts device specific pixels to density independent pixels.
     *
     * @param px A value in px (pixels) unit. Which we need to convert into db
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent dp equivalent to px value
     */
    public static int convertPixelsToDp(float px, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        int dp = (int)(px / ((float)metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT));

        return dp;
    }

    public static String getUniquePsuedoID() {

        String m_szDevIDShort = "35" + (Build.BOARD.length() % 10) + (Build.BRAND.length() % 10) + (Build.CPU_ABI.length() % 10) + (Build.DEVICE.length() % 10) + (Build.MANUFACTURER.length() % 10) + (Build.MODEL.length() % 10) + (Build.PRODUCT.length() % 10);
        String serial = null;
        try {
            serial = android.os.Build.class.getField("SERIAL").get(null).toString();
            return new UUID(m_szDevIDShort.hashCode(), serial.hashCode()).toString();
        } catch (Exception exception) {
            serial = "pdv"; // TODO put some default value here.
            //serial = Resources.getSystem().getString(R.string.app_name); // TODO put some default value here.
        }

        return new UUID(m_szDevIDShort.hashCode(), serial.hashCode()).toString();
    }
}
