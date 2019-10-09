package com.mvelasquezp.clife.clientes.tools;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AppHelper {

    public AppHelper() {

    }

    private SimpleDateFormat sdf;

    public String FormatDate(Date date, String mask) {
        sdf = new SimpleDateFormat(mask);
        return sdf.format(date);
    }

    public Date StringToDate(String string, String mask) {
        sdf = new SimpleDateFormat(mask);
        try {
            return sdf.parse(string);
        }
        catch (ParseException ex) {
            return null;
        }
    }
}
