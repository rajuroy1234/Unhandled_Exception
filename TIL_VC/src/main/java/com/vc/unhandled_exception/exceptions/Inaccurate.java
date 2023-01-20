package com.vc.unhandled_exception.exceptions;

public class Inaccurate extends Exception {
	public Inaccurate()
	{
		throw new IllegalArgumentException();
	}
}
