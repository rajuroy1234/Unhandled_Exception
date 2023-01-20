package com.vc.unhandled_exception.service;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.danubetech.verifiablecredentials.CredentialSubject;
import com.danubetech.verifiablecredentials.VerifiableCredential;
import com.danubetech.verifiablecredentials.jsonld.VerifiableCredentialContexts;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import com.vc.unhandled_exception.encryption.Base58;
import com.vc.unhandled_exception.model.VerifyResponse;

import foundation.identity.jsonld.JsonLDException;
import info.weboftrust.ldsignatures.LdProof;
import info.weboftrust.ldsignatures.jsonld.LDSecurityKeywords;
import info.weboftrust.ldsignatures.signer.Ed25519Signature2018LdSigner;
import info.weboftrust.ldsignatures.verifier.Ed25519Signature2018LdVerifier;

public class Issue_Verify {
	
	private static final Logger logger = LoggerFactory.getLogger(Issue_Verify.class);

	
	public static Date expir(Date date, int hours) {
	    Calendar calendar = Calendar.getInstance();
	    calendar.setTime(date);
	    calendar.add(Calendar.HOUR_OF_DAY, hours);
	    return calendar.getTime();
	}
	
	public static JsonObject convertToJsonObject(String request)
	{
		Gson gson = new Gson();
		JsonElement element = gson.fromJson(request, JsonElement.class);
		JsonObject jsonObj = element.getAsJsonObject();
		
		return jsonObj;
	}
	
	private static final byte[] HEX_ARRAY = "0123456789abcdef".getBytes(StandardCharsets.US_ASCII);
	
	public static String bytesToHex(byte[] bytes) {
	    byte[] hexChars = new byte[bytes.length * 2];
	    for (int j = 0; j < bytes.length; j++) {
	        int v = bytes[j] & 0xFF;
	        hexChars[j * 2] = HEX_ARRAY[v >>> 4];
	        hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
	    }
	    return new String(hexChars, StandardCharsets.UTF_8);
	}
	
	public static String readJsonforWeb(String location)
	{
	      try {
	    	 JsonElement publicKeyJson;
			JsonElement publicKey;
			String publicKeyBase58;
	         //Object obj = parser.parse(new FileReader(location));
	    	 //URL url = URL.
	    	 //String doc = IOUtils.resourceToString(location, Charset.forName("UTF-8"));
	    	 //String doc = new String(Files.readAllBytes(Paths.get(location)));
	    	 
	    	 URL url = new URL(location);
	    	 String doc = IOUtils.toString(url.openStream(), Charset.forName("UTF-8"));
	    	 JsonObject jsonObj = convertToJsonObject(doc);
	         publicKeyJson = jsonObj.get("publickey");
	         //publicKeyJson =  Conversion.toString(publicKeyJson);
	         
	         //jsonObj = convertToJsonObject(publicKeyJson);
	         publicKey = ((JsonArray) publicKeyJson).get(0);			
	         //publicKeyBase58 =  Conversion.toString(publicKeyBase58);
	         publicKeyBase58 = ((JsonObject) publicKey).get("publicKeyBase58").toString();
	         publicKeyBase58 =  Conversion.toString(publicKeyBase58);
	         
	         return publicKeyBase58;             
	      } 
	      catch(Exception e) {
	    	  logger.error("Public Key could not be found for DID WEB", e.getMessage(),
						e);
	      }
	      return null;
	}	
	
	public static byte[] getPublicKeyfromDoc(String issuer)
	{
		byte[] publickey = new byte[32];
		String uri;
		if(issuer.contains(":key:"))
		{
			issuer = issuer.replaceAll("did:key:", "");				
			
			try {
				publickey = Codec.multibase_decode(issuer);
			} 
			catch (DecoderException e) {
				logger.error("Could not decode the public key from DID KEY", e.getMessage(),
						e);
			}
		}
		else if(issuer.contains(":web:"))
		{
			uri = CreateDIDWeb.read(issuer);
			//String json = IOUtils.toString(uri, Charset.forName("UTF-8"));
			String publicKeyBase58 = readJsonforWeb(uri);
			publickey = Base58.decode(publicKeyBase58);
		}
//		if(StringUtils.isEmpty(didpublickeyBase58))			
//		{
//			didpublickeyBase58 = res.getDid_web();	
//			String uri = CreateDIDWeb.read(didpublickeyBase58);
//			didpublickeyBase58 = getPublicKeyfromWeb(uri);
//			publickey = Base58.decode(didpublickeyBase58);
//		}
//		else
//		{
//			didpublickeyBase58 = didpublickeyBase58.replaceAll("did:key:", "");				
//			
//			try {
//				publickey = Codec.multibase_decode(didpublickeyBase58);
//			} 
//			catch (DecoderException e) {
//				logger.error("Could not decode the public key from DID KEY", e.getMessage(),
//						e);
//			}
//		}
		
		return publickey;
	}
	
	public static String issueVC(Map<String, Object> claims, String privateKey, String subjectDID, String issuerDID, int duration, String type) 
			throws DecoderException, IOException, GeneralSecurityException, JsonLDException
	{		
		Date t = Calendar.getInstance().getTime();		
		CredentialSubject credentialSubject = CredentialSubject.builder()
		        .id(URI.create(subjectDID))
		        .claims(claims)
		        .build();

		VerifiableCredential verifiableCredential = VerifiableCredential.builder()
		        .context(VerifiableCredentialContexts.JSONLD_CONTEXT_W3C_2018_CREDENTIALS_EXAMPLES_V1)
		        .type(type)		        
		        .issuer(URI.create(issuerDID))
		        .issuanceDate(t)
		        .expirationDate(expir(t, duration))
		        .credentialSubject(credentialSubject)
		        .build();

		byte[] Ed25519PrivateKey = new byte[64];
		//Ed25519PrivateKey = Hex.decodeHex("984b589e121040156838303f107e13150be4a80fc5088ccba0b0bdc9b1d89090de8777a28f8da1a74e7a13090ed974d879bf692d001cddee16e4cc9f84b60580".toCharArray());
		Ed25519PrivateKey = Hex.decodeHex(privateKey);
		//Ed25519PublicKey = publicKey;
		
//		byte[] publicKey = new byte[32];
//		byte[] privateKey = new byte[64];
//		Ed25519Provider.get().generateEC25519KeyPair(Ed25519PublicKey,Ed25519PrivateKey);
//		
//		System.out.println("Paired!");
//		System.out.println("Privatekey :  " + Hex.encodeHexString(Ed25519PrivateKey));
//		System.out.println("Publickey :  " + Hex.encodeHexString(Ed25519PublicKey));
		
		Ed25519Signature2018LdSigner signer = new Ed25519Signature2018LdSigner(Ed25519PrivateKey);
		signer.setCreated(new Date());
		signer.setProofPurpose(LDSecurityKeywords.JSONLD_TERM_ASSERTIONMETHOD);
		signer.setVerificationMethod(URI.create("did:toi:76e12ec712ebc6f1c221ebfeb1f#keys-1"));
		try {
			LdProof ldProof = signer.sign(verifiableCredential);
		} catch (IOException | GeneralSecurityException | JsonLDException e) {
			e.printStackTrace();
		}
		return verifiableCredential.toJson(true);
	}
	

	public static VerifyResponse verifyVC(String vc) throws DecoderException, IOException, GeneralSecurityException, JsonLDException
	{
		byte[] publicKey = new byte[32];
		JsonObject jsonObj = convertToJsonObject(vc);
		String issuer, expirationDate;
		issuer = jsonObj.get("issuer").toString();			
		issuer =  Conversion.toString(issuer);	
		
		expirationDate = jsonObj.get("expirationDate").toString();			
		expirationDate =  Conversion.toString(expirationDate);	
		Date currentDate = Calendar.getInstance().getTime();
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
		Date date = null;
		try {
			 date = dateFormat.parse(expirationDate);
		} catch (ParseException e) {
			logger.error("Could not convert to date");
		}
		
		publicKey = getPublicKeyfromDoc(issuer);
		logger.info("Public key derieved : " + Hex.encodeHexString(publicKey));
		
		VerifiableCredential verifiableCredential = VerifiableCredential.fromJson(vc);
		Ed25519Signature2018LdVerifier verifier = new Ed25519Signature2018LdVerifier(publicKey);
		
		VerifyResponse vr = new VerifyResponse() ;
		if(verifier.verify(verifiableCredential) == true)
		{
			vr.setVerified("true");
		}
		else
		{
			vr.setVerified("false");
			if(currentDate.after(date))
			{
				vr.setError("vc expired");
			}
			else
				vr.setError("verification failed");			
		}
		
		return vr;		
	}
}
