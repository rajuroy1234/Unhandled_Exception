package com.wallet.unhandled_exception.response;

import java.io.IOException;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.wallet.unhandled_exception.exceptions.Inaccurate;
import com.wallet.unhandled_exception.exceptions.NullExceptions;
import com.wallet.unhandled_exception.model.DID_Doc;
import com.wallet.unhandled_exception.populate.ObjectConverter;
import com.wallet.unhandled_exception.service.Codec;
import com.wallet.unhandled_exception.service.Conversion;
import com.wallet.unhandled_exception.service.CreateDIDWeb;
import com.wallet.unhandled_exception.service.FileEdit;
import com.wallet.unhandled_exception.utility.FTPD;

public class Response {	
	static final String domain = "http://localhost:2121";
	private static final org.slf4j.Logger logger = LoggerFactory.getLogger(Response.class);  
	
	public static JsonObject convertToJsonObject(String request)
	{
		Gson gson = new Gson();
		JsonElement element = gson.fromJson(request, JsonElement.class);
		JsonObject jsonObj = element.getAsJsonObject();
		
		return jsonObj;
	}
	
	public static FinalResponse keyResponse(String publicKey) throws DecoderException
	{
		String didKey = "did:key:" + Codec.encode(publicKey);
		String didURL = didKey + "#" + "keys-1";
		
		DID_Doc did_Doc = new DID_Doc();
		did_Doc = ObjectConverter.populatedoc(publicKey, didURL);			
		
		FinalResponse response = new FinalResponse();
		response.setDid_key(didKey);
		response.setDid_document(did_Doc);
		
		return response;
	}
	
	public static FinalResponse webResponse(String publicKey) throws DecoderException, IOException
	{
		String rand_identifier = RandomStringUtils.randomAlphanumeric(16);			
		String didWeb = CreateDIDWeb.create(domain) + ":dids:" + rand_identifier;
		String didWebURL = didWeb + "#" + "keys-1";
		
		DID_Doc did_Doc = new DID_Doc();
		did_Doc = ObjectConverter.populatedoc(publicKey, didWebURL);			
		FileEdit.uploadFTPD(rand_identifier, did_Doc);
		
		FinalResponse response = new FinalResponse();
		response.setDid_web(didWeb);
		response.setDid_document(did_Doc);
		
		return response;
	}
	
	public static FinalResponse genResponse(String request) throws DecoderException, NullExceptions, IOException, Inaccurate
	{		
		String publicKey, method;		
		JsonObject jsonObj = convertToJsonObject(request);
		
		publicKey = jsonObj.get("publicKey").toString();		
		publicKey = Conversion.toString(publicKey);			
		
		method = jsonObj.get("method").toString();			
		method =  Conversion.toString(method);
		
		if(StringUtils.isEmpty(method) || StringUtils.isEmpty(publicKey)) 				
			throw new NullExceptions();		
		else if(!(method.equalsIgnoreCase("web") || method.equalsIgnoreCase("key")))
			throw new Inaccurate();
		
		if(method.equalsIgnoreCase("key"))
			return keyResponse(publicKey);
		
		else if(method.equalsIgnoreCase("web"))
			return webResponse(publicKey);
				
		return null;
	}
}
