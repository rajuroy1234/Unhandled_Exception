package com.vc.unhandled_exception.service;

import org.apache.commons.lang3.RandomStringUtils;

public class CreateDIDWeb {
	public static String randUser = "";
	public static String URL = "https://";
	
	public static String create(String url)
	{
		String did = "did:web:";
		url = url.replace("https://", "");
		url = url.replace("http://", "");
		url = url.replace("/did.json", "");
		url = url.replace("/.well-known", "");
		url = url.replace(":", "%3A");
		url = url.replaceAll("/", ":");	
		//url = url.replaceAll("\\", ":");
		did += url;
		
		return did;
	}
	
	public static String createDID(String url)
	{
		String did = "did:web:";
		url = url.replace("https://", "");
		url = url.replace("http://", "");
		url = url.replace("/did.json", "");
		url = url.replace("/.well-known", "");
		url = url.replace(":", "%3A");
		url = url.replaceAll("/", ":");	
		did += url;
		
		return did;
	}
	
	public static String read(String did)
	{
		String url = URL;
		//String def = "/.well-known";

		did = did.replace("did:web:", "");
//		if(did.indexOf(":")!= -1)
//			did += def;
		did = did.replaceAll(":", "/");
		did = did.replace("%3A", ":");
		url+=(did+"/did.json");
		
		return url;
	}
	
	public static void randUserGen()
	{
        String rand_identifier = RandomStringUtils.randomAlphanumeric(16);
        randUser = rand_identifier;          
	}
	
	public static String domainToUrl(String domain)
	{
		return domain +"/dids/" + randUser + "/did.json";
	}
	
	public static void update(String did)
	{
	
	}
}
