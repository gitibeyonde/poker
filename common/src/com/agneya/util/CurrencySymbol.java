package com.agneya.util;

import java.util.Currency;
import java.util.Locale;
public class CurrencySymbol {
	
	public static String getFranceCurrency(){
		Currency currency = Currency.getInstance(Locale.FRANCE);
        //System.out.println("Currency.getSymbol() = " + currency.getSymbol());
        return currency.getSymbol();
	}
	public static String getUSACurrency(){
		Currency currency = Currency.getInstance(Locale.US);
        //System.out.println("Currency.getSymbol() = " + currency.getSymbol());
        return currency.getSymbol();
	}
	
	/*public static void main(String[] a){
		CurrencySymbol as = new CurrencySymbol();
		System.out.println();
	}*/

}
