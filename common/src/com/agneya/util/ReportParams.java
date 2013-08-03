package com.agneya.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.Enumeration;
import java.util.Properties;


public class ReportParams extends Properties {

	public static Param[] _param_reg;

	public ReportParams(File f) throws FileNotFoundException, IOException {
		FileInputStream fi = new FileInputStream(f);
		load(fi);
		fi.close();
		_param_reg = new Param[size()];
		int i = 0;
		for (Enumeration e = propertyNames(); e.hasMoreElements(); i++) {
			String prop = (String) e.nextElement();
			String val = (String) get(prop);
			Param p = new Param(prop, val);
			_param_reg[p._order] = p;
		}
		
	}

	public Param[] getParams() {
		return _param_reg;
	}

	public class Param {
		public String _name;

		public int _order;
		
		public String _display_name;
		
		public String _type;

		public String _default_value;

		public int _size;
		
		public String _value;

		public static final long DAY = 24 * 60 * 60 * 1000;

		public static final long WEEK = 7 * DAY;

		public static final long MONTH = 30 * DAY;

		public Param(String name, String val) {
			//System.out.println(val);
			String[] vals = val.split("\\|");
			_name = name;
			_order = Integer.parseInt(vals[0]);
			_display_name = vals[1];
			_type = vals[2];
			_size = Integer.parseInt(vals[3]);
			_default_value = vals.length < 5 ? "" : vals[4];
			//_value = _default_value;
		}

		public Object getDefaultValue() {
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd");
			if (_type.equals("Date")) {
				if (_default_value.startsWith("CURRENT")) {
					Date dt= new Date(System.currentTimeMillis());
					this._default_value=dt + "";
					return sdf.format(new Date(System.currentTimeMillis()));
				} else if (_default_value.startsWith("LASTWEEK")) {
					Date dt = new Date(System.currentTimeMillis() - WEEK);
					this._default_value=dt + "";
					return sdf.format(new Date(System.currentTimeMillis() - WEEK));
				}
			} else if (_type.equals("String")) {
				return new String(_default_value);
			} else if (_type.equals("Integer")) {
				return new Integer(_default_value);
			}
			return "-1";
		}

		public String toString() {
			return _name + "=" + getValue();
		}
		
		public void setValue(String val){
			this._value=val;
		}
		
		public String getValue(){
			if (_type.equals("Date")) {
					return this._value;
			} else if (_type.equals("String")) {
				return this._value;
			} else if (_type.equals("Integer")) {
				return this._value;
			}

			return this._value;
		}
		
	}

	public static void main(String args[]) throws Exception {
		ReportParams rp = new ReportParams(
				new File(
						"C:\\tools\\eclipse\\workspace\\octopus\\web\\JasperReports\\jrxml\\Player\\Transaction.param"));
		Param[] h = rp.getParams();

		for (int i = 0; i < h.length; i++) {
			Param p = h[i];
			System.out.println(p);
		}
	}
}