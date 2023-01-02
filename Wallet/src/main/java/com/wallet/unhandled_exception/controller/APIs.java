package com.wallet.unhandled_exception.controller;

import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.bouncycastle.asn1.crmf.ProofOfPossession;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.apicatalog.multibase.Multibase;
import com.apicatalog.multicodec.Multicodec;
import com.danubetech.keyformats.crypto.PrivateKeySigner;
import com.danubetech.keyformats.crypto.impl.Ed25519_EdDSA_PrivateKeySigner;
import com.danubetech.keyformats.crypto.impl.RSA_PS256_PrivateKeySigner;
import com.danubetech.verifiablecredentials.CredentialSubject;
import com.danubetech.verifiablecredentials.VerifiableCredential;
import com.danubetech.verifiablecredentials.VerifiablePresentation;
import com.danubetech.verifiablecredentials.jsonld.VerifiableCredentialContexts;
import com.danubetech.verifiablecredentials.jwt.JwtVerifiableCredential;
import com.danubetech.verifiablecredentials.jwt.ToJwtConverter;
import com.nimbusds.jose.JOSEException;
import com.wallet.unhandled_exception.VerifiablePresentationNew;

import foundation.identity.jsonld.ConfigurableDocumentLoader;
import foundation.identity.jsonld.JsonLDException;
import foundation.identity.jsonld.JsonLDUtils;
import info.weboftrust.ldsignatures.LdProof;
import info.weboftrust.ldsignatures.jsonld.LDSecurityKeywords;
import info.weboftrust.ldsignatures.signer.Ed25519Signature2018LdSigner;
import info.weboftrust.ldsignatures.signer.Ed25519Signature2020LdSigner;
import info.weboftrust.ldsignatures.verifier.Ed25519Signature2018LdVerifier;
import info.weboftrust.ldsignatures.verifier.Ed25519Signature2020LdVerifier;

import com.danubetech.keyformats.crypto.provider.Ed25519Provider;
import com.danubetech.keyformats.crypto.provider.impl.NaClSodiumEd25519Provider;

@RestController
@RequestMapping("/wallet")
public class APIs {
	
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
	@GetMapping("/issuejwt")
	public String issueJWT() throws JOSEException, DecoderException {
		Map<String, Object> claims = new LinkedHashMap<>();
		Map<String, Object> degree = new LinkedHashMap<String, Object>();
		degree.put("name", "Bachelor of Science and Arts");
		degree.put("type", "BachelorDegree");
		claims.put("college", "Test University");
		claims.put("degree", degree);

		CredentialSubject credentialSubject = CredentialSubject.builder()
		        .id(URI.create("did:example:ebfeb1f712ebc6f1c276e12ec21"))
		        .claims(claims)
		        .build();

		VerifiableCredential verifiableCredential = VerifiableCredential.builder()
		        .context(VerifiableCredentialContexts.JSONLD_CONTEXT_W3C_2018_CREDENTIALS_EXAMPLES_V1)
		        .type("UniversityDegreeCredential")
		        .id(URI.create("http://example.edu/credentials/3732"))
		        .issuer(URI.create("did:example:76e12ec712ebc6f1c221ebfeb1f"))
		        .issuanceDate(JsonLDUtils.stringToDate("2019-06-16T18:56:59Z"))
		        .expirationDate(JsonLDUtils.stringToDate("2019-06-17T18:56:59Z"))
		        .credentialSubject(credentialSubject)
		        .build();

		byte[] testEd25519PrivateKey = Hex.decodeHex("984b589e121040156838303f107e13150be4a80fc5088ccba0b0bdc9b1d89090de8777a28f8da1a74e7a13090ed974d879bf692d001cddee16e4cc9f84b60580".toCharArray());

		JwtVerifiableCredential jwtVerifiableCredential = ToJwtConverter.toJwtVerifiableCredential(verifiableCredential);

		String jwtPayload = jwtVerifiableCredential.getPayload().toString();
		System.out.println(jwtPayload);
		

		String jwtString = jwtVerifiableCredential.sign_Ed25519_EdDSA(testEd25519PrivateKey);
		System.out.println(jwtString);
		return jwtString;
	}
	@GetMapping("/sign")
	public String signin() throws DecoderException, IOException, GeneralSecurityException, JsonLDException
	{
		
		byte[] publicKey = new byte[32];
		byte[] privateKey = new byte[64];
		Ed25519Provider.get().generateEC25519KeyPair(publicKey,privateKey);
		
		
		//Demonstrate the encoding of data for an example ED25519 public key...
		byte[] multicodecEncoding = Multicodec.encode(Multicodec.Codec.Ed25519PublicKey, publicKey);
		String multibasePublicKey = Multibase.encode(Multibase.Algorithm.Base58Btc, multicodecEncoding);
		
		byte[] multicodecBytes = Multibase.decode(multibasePublicKey);
		byte [] decodedPublicKey =  Multicodec.decode(Multicodec.Type.Key,multicodecBytes);
		System.out.println("multibasePublicKey:" + multibasePublicKey);
		System.out.println("decodedPublicKey:" + bytesToHex(decodedPublicKey));
		Map<String, Object> claims = new LinkedHashMap<>();
		Map<String, Object> degree = new LinkedHashMap<>();
//		degree.put("name", "Rupal_Goyal");
//		degree.put("title", "Prime_Subscriber");
		claims.put("employer", "Times_of_India");
		claims.put("department", "IT");
		Map<String, Object> claims2 = new LinkedHashMap<>();
//		Map<String, Object> degree2 = new LinkedHashMap<>();

		claims2.put("employer", "TIL");
		claims2.put("department", "IT");

		CredentialSubject credentialSubject = CredentialSubject.builder()
		        .id(URI.create("did:key:"+multibasePublicKey))
		        .claims(claims)
		        .type("Employee")
		        .build();
		CredentialSubject credentialSubject2 = CredentialSubject.builder()
		        .id(URI.create("did:key:"+multibasePublicKey))
		        .claims(claims2)
		        .type("Employee")
		        .build();
		
		 ((ConfigurableDocumentLoader) VerifiableCredentialContexts.DOCUMENT_LOADER).setEnableHttps(true);
		
		VerifiableCredential verifiableCredential = VerifiableCredential.builder()
		        .context(VerifiableCredentialContexts.JSONLD_CONTEXT_W3C_2018_CREDENTIALS_V1)
		        .context(URI.create("https://danubetech.github.io/employment-vocab/context/v0.1"))
		        .type("EmploymentCredential")
		        .id(URI.create("did:key:"+multibasePublicKey))
		        .issuer(URI.create("did:key:"+multibasePublicKey))
		        .issuanceDate(JsonLDUtils.stringToDate("2022-01-13T18:56:59Z"))
		        .expirationDate(JsonLDUtils.stringToDate("2023-03-04T18:56:59Z"))
		        .credentialSubject(credentialSubject)
		        .build();
		VerifiableCredential verifiableCredential2 = VerifiableCredential.builder()
		        .context(VerifiableCredentialContexts.JSONLD_CONTEXT_W3C_2018_CREDENTIALS_V1)
		        .context(URI.create("https://danubetech.github.io/employment-vocab/context/v0.1"))
		        .type("EmploymentCredential")
		        .id(URI.create("did:key:"+multibasePublicKey))
		        .issuer(URI.create("did:key:"+multibasePublicKey))
		        .issuanceDate(JsonLDUtils.stringToDate("2022-01-13T18:56:59Z"))
		        .expirationDate(JsonLDUtils.stringToDate("2023-03-04T18:56:59Z"))
		        .credentialSubject(credentialSubject2)
		        .build();
		
		

		//Demonstrate the decoding...
//		DecodedData decodedData = null;
//		try {
//		    decodedData = Multicodec.de
//		} catch (AmbiguousCodecEncodingException exAmbiguousCodecEncoding) {
//		    System.err.println("AmbiguousCodecEncodingException:" + exAmbiguousCodecEncoding.getMessage());
//		}
//		if (decodedData != null) {
//		    System.out.printf("Codec:%s(%s)%n", decodedData.getCodec().name(), decodedData.getCodec().code);
//		    System.out.println("byte data length:" + decodedData.getDataAsBytes().length);
//		    System.out.println("hex data:" + decodedData.getDataAsHex());
//		}
		
		
		
		
		System.out.println("publickey "+ bytesToHex(publicKey));
		System.out.println("privateKey "+ bytesToHex(privateKey));
		Ed25519Signature2018LdSigner signer = new Ed25519Signature2018LdSigner(privateKey);
		signer.setCreated(new Date());
		signer.setProofPurpose(LDSecurityKeywords.JSONLD_TERM_ASSERTIONMETHOD);
		signer.setVerificationMethod(URI.create("did:key:"+multibasePublicKey+"#"+multibasePublicKey));
		try {
			LdProof ldProof = signer.sign(verifiableCredential);
			LdProof ldProofVC2 = signer.sign(verifiableCredential2);
		} catch (IOException | GeneralSecurityException | JsonLDException e) {
			e.printStackTrace();
		}
		
//		Map<String,Object> vcMap =  new HashMap<String,Object>();
//		vcMap.put("vc1",verifiableCredential );
//		vcMap.put("vc2",verifiableCredential2 );
		VerifiableCredential[] vcList= new VerifiableCredential[2];
		vcList[0] = verifiableCredential;
		vcList[1] = verifiableCredential2;
		VerifiablePresentation verifiablePresentation = VerifiablePresentationNew.buildernew()
		        .verifiableCredential(vcList)
//		        .verifiableCredential(verifiableCredential2)
		        .holder(URI.create("did:key:z6MkwBZ6oiJ71ovCohPfdsgBrQinMXnFn6wJxVZHpZEpSh8x"))
		        .build();

		byte[] testEd25519PrivateKey2 = Hex.decodeHex("984b589e121040156838303f107e13150be4a80fc5088ccba0b0bdc9b1d89090de8777a28f8da1a74e7a13090ed974d879bf692d001cddee16e4cc9f84b60580".toCharArray());

		Ed25519Signature2018LdSigner signer2 = new Ed25519Signature2018LdSigner(testEd25519PrivateKey2);
		signer2.setCreated(new Date());
		signer2.setProofPurpose(LDSecurityKeywords.JSONLD_TERM_AUTHENTICATION);
		signer2.setVerificationMethod(URI.create("did:key:z6MkwBZ6oiJ71ovCohPfdsgBrQinMXnFn6wJxVZHpZEpSh8x#z6MkwBZ6oiJ71ovCohPfdsgBrQinMXnFn6wJxVZHpZEpSh8x"));
		signer2.setDomain("example.com");
		signer2.setNonce("343s$FSFDa-");
		LdProof ldProof2 = signer2.sign(verifiablePresentation);

		System.out.println(verifiablePresentation.toJson(true));
		return verifiableCredential.toJson(true);
	}
	
	@GetMapping("/verify")
	public boolean verify() throws DecoderException, IOException, GeneralSecurityException, JsonLDException
	{
		String json = "{\r\n"
				+ "  \"@context\" : [ \"https://www.w3.org/2018/credentials/v1\", \"https://danubetech.github.io/employment-vocab/context/v0.1\" ],\r\n"
				+ "  \"type\" : [ \"VerifiableCredential\", \"EmploymentCredential\" ],\r\n"
				+ "  \"id\" : \"did:key:z6MkvhPSiKBrX8A7rxmxL3UF9u982qJTfMaQRaVAEeGYAhuS\",\r\n"
				+ "  \"issuer\" : \"did:key:z6MkvhPSiKBrX8A7rxmxL3UF9u982qJTfMaQRaVAEeGYAhuS\",\r\n"
				+ "  \"issuanceDate\" : \"2022-01-13T18:56:59Z\",\r\n"
				+ "  \"expirationDate\" : \"2023-03-04T18:56:59Z\",\r\n"
				+ "  \"credentialSubject\" : {\r\n"
				+ "    \"type\" : \"Employee\",\r\n"
				+ "    \"id\" : \"did:key:z6MkvhPSiKBrX8A7rxmxL3UF9u982qJTfMaQRaVAEeGYAhuS\",\r\n"
				+ "    \"employer\" : \"Times_of_India\",\r\n"
				+ "    \"department\" : \"IT\"\r\n"
				+ "  },\r\n"
				+ "  \"proof\" : {\r\n"
				+ "    \"type\" : \"Ed25519Signature2018\",\r\n"
				+ "    \"created\" : \"2022-12-26T15:16:56Z\",\r\n"
				+ "    \"proofPurpose\" : \"assertionMethod\",\r\n"
				+ "    \"verificationMethod\" : \"did:key:z6MkvhPSiKBrX8A7rxmxL3UF9u982qJTfMaQRaVAEeGYAhuS#z6MkvhPSiKBrX8A7rxmxL3UF9u982qJTfMaQRaVAEeGYAhuS\",\r\n"
				+ "    \"jws\" : \"eyJiNjQiOmZhbHNlLCJjcml0IjpbImI2NCJdLCJhbGciOiJFZERTQSJ9..7pRt1QRQIpytHcutifDZkRRs2qBObPxIOw4NCKGzC9iq8Y1CsjfNrmGc1K35c2Q6WU7J_tfEIgLPhxx9AIqWCg\"\r\n"
				+ "  }\r\n"
				+ "}";
		String hex = "f1597ce28f811aec20ef4f00f7f99bf3b499c883118ff23d253bacd470a322f9";
		byte[] testEd25519PublicKey = Hex.decodeHex(hex);

		VerifiableCredential verifiableCredential = VerifiableCredential.fromJson(json);
		Ed25519Signature2018LdVerifier verifier = new Ed25519Signature2018LdVerifier(testEd25519PublicKey);
		return verifier.verify(verifiableCredential);
		
    

}
	
}
