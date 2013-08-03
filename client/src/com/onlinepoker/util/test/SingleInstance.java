package com.onlinepoker.util.test;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class SingleInstance {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			System.out.println(InetAddress.getByAddress(new byte[] {127, 0, 0, 1}));
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
