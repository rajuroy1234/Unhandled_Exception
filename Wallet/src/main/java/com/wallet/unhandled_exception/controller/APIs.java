package com.wallet.unhandled_exception.controller;

import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.security.GeneralSecurityException;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.bouncycastle.asn1.crmf.ProofOfPossession;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.apicatalog.ld.signature.key.KeyPair;
import com.apicatalog.ld.signature.proof.ProofOptions;
import com.apicatalog.vc.Vc;
import com.danubetech.verifiablecredentials.CredentialSubject;
import com.danubetech.verifiablecredentials.VerifiableCredential;
import com.danubetech.verifiablecredentials.jsonld.VerifiableCredentialContexts;

import foundation.identity.jsonld.JsonLDException;
import foundation.identity.jsonld.JsonLDUtils;
import info.weboftrust.ldsignatures.LdProof;
import info.weboftrust.ldsignatures.jsonld.LDSecurityKeywords;
import info.weboftrust.ldsignatures.signer.Ed25519Signature2018LdSigner;
import info.weboftrust.ldsignatures.verifier.Ed25519Signature2018LdVerifier;

@RestController
@RequestMapping("/wallet")
public class APIs {
	@GetMapping("/sign")
	public String signin() throws DecoderException, IOException, GeneralSecurityException, JsonLDException
	{
		Map<String, Object> claims = new LinkedHashMap<>();
		Map<String, Object> degree = new LinkedHashMap<>();
		degree.put("name", "Rupal_Goyal");
		degree.put("type", "Prime_Subscriber");
		claims.put("company", "Times_of_India");
		claims.put("Exclusive", degree);

		CredentialSubject credentialSubject = CredentialSubject.builder()
		        .id(URI.create("did:toi:ebfeb1f712ebc6f1c276e12ec21"))
		        .claims(claims)
		        .build();

		VerifiableCredential verifiableCredential = VerifiableCredential.builder()
		        .context(VerifiableCredentialContexts.JSONLD_CONTEXT_W3C_2018_CREDENTIALS_EXAMPLES_V1)
		        .type("TOIPlusSubscription")
		        .id(URI.create("http://toi.com/credentials/3732"))
		        .issuer(URI.create("did:toi:76e12ec712ebc6f1c221ebfeb1f"))
		        .issuanceDate(JsonLDUtils.stringToDate("2023-01-13T18:56:59Z"))
		        .expirationDate(JsonLDUtils.stringToDate("2023-03-04T18:56:59Z"))
		        .credentialSubject(credentialSubject)
		        .build();

		byte[] testEd25519PrivateKey;	
		testEd25519PrivateKey = Hex.decodeHex("984b589e121040156838303f107e13150be4a80fc5088ccba0b0bdc9b1d89090de8777a28f8da1a74e7a13090ed974d879bf692d001cddee16e4cc9f84b60580".toCharArray());
		
		Ed25519Signature2018LdSigner signer = new Ed25519Signature2018LdSigner(testEd25519PrivateKey);
		signer.setCreated(new Date());
		signer.setProofPurpose(LDSecurityKeywords.JSONLD_TERM_ASSERTIONMETHOD);
		signer.setVerificationMethod(URI.create("did:toi:76e12ec712ebc6f1c221ebfeb1f#keys-1"));
		try {
			LdProof ldProof = signer.sign(verifiableCredential);
		} catch (IOException | GeneralSecurityException | JsonLDException e) {
			e.printStackTrace();
		}
		
//		
//		KeyPair kp = new KeyPair();
//		kp.setPrivateKey(testEd25519PrivateKey);
//		
//		ProofOptions po =new ProofOptions();
//		return Vc.sign(URI.create("http://toi.com/credentials/3732"), kp, signer).getCompacted();
		
		
//		 Vc.verify(verifiableCredential)
//		    .isValid();
		return verifiableCredential.toJson(true);
		
//		byte[] testEd25519PublicKey = Hex.decodeHex("de8777a28f8da1a74e7a13090ed974d879bf692d001cddee16e4cc9f84b60580".toCharArray());
//
////		VerifiableCredential verifiableCredential1 = VerifiableCredential.fromJson(new FileReader("input.jsonld"));
//		VerifiableCredential verifiableCredential1 = VerifiableCredential.fromJson(verifiableCredential.toJson());
//		Ed25519Signature2018LdVerifier verifier = new Ed25519Signature2018LdVerifier(testEd25519PublicKey);
//		return verifier.verify(verifiableCredential1);
	}
	
	@GetMapping("/verify")
	public void verify() throws DecoderException, IOException, GeneralSecurityException, JsonLDException
	{
//		byte[] testEd25519PublicKey = Hex.decodeHex("de8777a28f8da1a74e7a13090ed974d879bf692d001cddee16e4cc9f84b60580".toCharArray());
//
//		VerifiableCredential verifiableCredential = VerifiableCredential.fromJson(new FileReader("input.jsonld"));
//		Ed25519Signature2018LdVerifier verifier = new Ed25519Signature2018LdVerifier(testEd25519PublicKey);
//		return verifier.verify(verifiableCredential);
		


 
    

}
	
}
