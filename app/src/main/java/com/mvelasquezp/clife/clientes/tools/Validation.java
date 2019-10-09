package com.mvelasquezp.clife.clientes.tools;

import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;

import com.mvelasquezp.clife.clientes.R;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by culqi on 1/19/17.
 */

public class Validation {

    Context context;

    public Validation(Context context) {
        this.context = context;
    }

    /*public static boolean luhn(String number){
        int s1 = 0, s2 = 0;
        String reverse = new StringBuffer(number).reverse().toString();
        for(int i = 0 ;i < reverse.length();i++){
            int digit = Character.digit(reverse.charAt(i), 10);
            if(i % 2 == 0){//this is for odd digits, they are 1-indexed in the algorithm
                s1 += digit;
            }else{//add 2 * digit for 0-4, add 2 * digit - 9 for 5-9
                s2 += 2 * digit;
                if(digit >= 5){
                    s2 -= 9;
                }
            }
        }
        return (s1 + s2) % 10 == 0;
    }

    public int bin(String bin, final TextView kind_card, final ImageView logo) {

        if(bin.length() > 1) {
            if(Integer.valueOf(bin.substring(0,2)) == 41) {
                kind_card.setText("VISA");
                logo.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_logo_visa));
                return 3;
            } else if (Integer.valueOf(bin.substring(0,2)) == 51) {
                kind_card.setText("MasterCard");
                logo.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_logo_mastercard));
                return 3;
            } else {
                logo.setImageDrawable(null);
            }
        } else {
            kind_card.setText("");
            logo.setImageDrawable(null);
        }

        if(bin.length() > 1) {
            if(Integer.valueOf(bin.substring(0,2)) == 36){
                kind_card.setText("Diners Club");
                logo.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_logo_diners));
                return 3;
            } else if(Integer.valueOf(bin.substring(0,2)) == 38){
                kind_card.setText("Diners Club");
                logo.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_logo_diners));
                return 3;
            } else if(Integer.valueOf(bin.substring(0,2)) == 37){
                kind_card.setText("AMEX");
                logo.setImageDrawable(context.getResources().getDrawable(R.mipmap.ic_logo_amex));
                return 3;
            } else {
                logo.setImageDrawable(null);
            }
        }

        if(bin.length() > 2) {
            if(Integer.valueOf(bin.substring(0,3)) == 300){
                kind_card.setText("Diners Club");
                logo.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_logo_diners));
                return 3;
            } else if(Integer.valueOf(bin.substring(0,3)) == 305){
                kind_card.setText("Diners Club");
                logo.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_logo_diners));
                return 3;
            } else {
                logo.setImageDrawable(null);
            }
        }
        return 0;
    }

    public boolean month(String month) {
        if(!month.equals("")){
            if(Integer.valueOf(""+month) > 12) {
                return true;
            }
        }
        return false;
    }

    public boolean year(String year){
        Date today = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(today);
        if(!year.equals("")){
            if(Integer.valueOf("20"+year) < calendar.get(Calendar.YEAR)) {
                return true;
            }
        }
        return false;
    }*/

    public static boolean luhn(String number){
        int s1 = 0, s2 = 0;
        String reverse = new StringBuffer(number).reverse().toString();
        for(int i = 0 ;i < reverse.length();i++){
            int digit = Character.digit(reverse.charAt(i), 10);
            if(i % 2 == 0){//this is for odd digits, they are 1-indexed in the algorithm
                s1 += digit;
            }else{//add 2 * digit for 0-4, add 2 * digit - 9 for 5-9
                s2 += 2 * digit;
                if(digit >= 5){
                    s2 -= 9;
                }
            }
        }
        return (s1 + s2) % 10 == 0;
    }

    public int bin(String bin, final TextView kind_card, final ImageView logo) {

        if(bin.length() > 1) {
            if(Integer.valueOf(bin.substring(0,1)) == 4) {
                kind_card.setText("VISA");
                logo.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_logo_visa));
                return 3;
            } else if (Integer.valueOf(bin.substring(0,1)) == 5) {
                kind_card.setText("MasterCard");
                logo.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_logo_mastercard));
                return 3;
            } else {
            }
        } else {
            kind_card.setText("");
        }

        if(bin.length() > 1) {
            if(Integer.valueOf(bin.substring(0,2)) == 36){
                kind_card.setText("Diners Club");
                logo.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_logo_diners));
                return 3;
            } else if(Integer.valueOf(bin.substring(0,2)) == 38){
                kind_card.setText("Diners Club");
                logo.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_logo_diners));
                return 3;
            } else if(Integer.valueOf(bin.substring(0,2)) == 37){
                kind_card.setText("AMEX");
                logo.setImageDrawable(context.getResources().getDrawable(R.mipmap.ic_logo_amex));
                return 3;
            } else {
            }
        }

        if(bin.length() > 2) {
            if(Integer.valueOf(bin.substring(0,3)) == 300){
                kind_card.setText("Diners Club");
                logo.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_logo_diners));
                return 3;
            } else if(Integer.valueOf(bin.substring(0,3)) == 305){
                kind_card.setText("Diners Club");
                logo.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_logo_diners));
                return 3;
            } else {
            }
        }
        return 0;
    }

    public boolean month(String month) {
        if(!month.equals("")){
            if(Integer.valueOf(""+month) > 12) {
                return true;
            }
        }
        return false;
    }

    public boolean year(String year){
        Date today = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(today);
        if(!year.equals("")){
            if(Integer.valueOf("20"+year) < calendar.get(Calendar.YEAR)) {
                return true;
            }
        }
        return false;
    }

}
