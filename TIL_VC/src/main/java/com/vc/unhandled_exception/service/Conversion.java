package com.vc.unhandled_exception.service;

public class Conversion {
	public static String toString(String str)
	{
		return str.replaceAll("\"", "");
	}
}
