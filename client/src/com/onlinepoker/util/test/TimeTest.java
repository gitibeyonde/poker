package com.onlinepoker.util.test;

import java.sql.Date;
import java.text.SimpleDateFormat;

public class TimeTest {

	public static void main(String[] args) {
		Date _d = new Date(1409035950);
		SimpleDateFormat sdf = new SimpleDateFormat("HH:ss:sss");
		System.out.println(sdf.format(_d));
	}

}
