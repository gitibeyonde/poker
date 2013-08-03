package com.onlinepoker.resources;

import java.util.Locale;
import java.util.ResourceBundle;

import com.onlinepoker.util.CredentialManager;


public class Bundle {

	private static ResourceBundle bundle;

	public static ResourceBundle getBundle() {
		String language = CredentialManager.getLanguageInfo();
		if(language.toLowerCase().equals("english"))
	    {
	    	Locale.setDefault(new Locale("en", "US"));
	    }
	    else if(language.toLowerCase().equals("deutsch"))
	    {
	    	Locale.setDefault(new Locale("fr","FR"));
	    }
	    else if(language.toLowerCase().equals("magyar"))
	    {
	    	Locale.setDefault(new Locale("fr","FR"));
	    }
	    else if(language.toLowerCase().equals("spanish"))
	    {
	    	Locale.setDefault(new Locale("es", "ES"));
	    }
				
		bundle = ResourceBundle.getBundle(
			new Bundle().getClass().getPackage().getName() + ".messages", 
			Locale.getDefault());
		return bundle;
	}
	
	    
}