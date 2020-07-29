package com.arbaelbarca.loginfirebaseemail.Utils;

import android.text.TextUtils;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class ConvertToRupiah {
    public static String toRupiah(String currency, String price, boolean diskon) {
        if (TextUtils.isEmpty(price)) {
            price = "0";
        }

        DecimalFormat kursIndonesia = (DecimalFormat) DecimalFormat.getCurrencyInstance(Locale.getDefault());
        //format.setCurrency(Currency.getInstance(currency));
        DecimalFormatSymbols formatRp = new DecimalFormatSymbols();

        formatRp.setCurrencySymbol(currency);
        formatRp.setMonetaryDecimalSeparator(',');
        formatRp.setGroupingSeparator('.');

        kursIndonesia.setMinimumFractionDigits(0);
        kursIndonesia.setDecimalFormatSymbols(formatRp);
        if (diskon) return kursIndonesia.format(Double.parseDouble(price) * 2);
        else return kursIndonesia.format(Double.parseDouble(price));
    }
}
