package com.wallet.unhandled_exception.model;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.ECGenParameterSpec;
import java.util.Base64;
import java.util.HashMap;

import com.wallet.unhandled_exception.protocol.base.CredentialWrapper;
import com.wallet.unhandled_exception.protocol.base.IdPrivateKey;
import com.wallet.unhandled_exception.protocol.request.CreateCredentialArgs;
import com.wallet.unhandled_exception.protocol.response.ResponseData;
import com.wallet.unhandled_exception.service.CredentialServiceImpl;

public class VerCred {
	
	public static ResponseData<CredentialWrapper> response;
	public static ResponseData<CredentialWrapper> selectiveCredentialWrapper;
	
	public static boolean sign(ResponseData<CredentialWrapper> response)
	{
		CredentialServiceImpl credentialServiceImpl2 = new CredentialServiceImpl();
        String disclosure = 
        		"{\"name\":0,\"gender\":1,\"age\":1, \"licence_info\":0, \"id\":1}";
        selectiveCredentialWrapper = credentialServiceImpl2.
        		createSelectiveCredential(response.getResult().getCredential(), disclosure);
        
        ResponseData<String> proofJson = credentialServiceImpl2.
        		getCredentialJson(selectiveCredentialWrapper.getResult().getCredential());       
//        System.out.println("Created proof by prover: ");
//       return proofJson.getResult();
        if(proofJson.getResult()!=null)
        	return true;
        else
        	return false;
	}
	
	public static boolean issue() throws Exception {
		//Issuer cryptomaterial generation
		ECGenParameterSpec ecSpec = new ECGenParameterSpec("secp256k1");
		KeyPairGenerator g = KeyPairGenerator.getInstance("EC");
//		g.initialize(ecSpec, new SecureRandom());
		KeyPair keypair = g.generateKeyPair();
		PublicKey publicKey = keypair.getPublic();
		PrivateKey privateKey = keypair.getPrivate();
		
		String issuerPublicKeyString = Base64.getEncoder().encodeToString(publicKey.getEncoded());
		String issuerPrivateKeyString = Base64.getEncoder().encodeToString(privateKey.getEncoded());
		
		//Prover cryptomaterial generation
//		ECGenParameterSpec ecSpecP = new ECGenParameterSpec("secp256k1");
		KeyPairGenerator gP = KeyPairGenerator.getInstance("EC");
//		gP.initialize(ecSpecP, new SecureRandom());
		KeyPair keypairP = gP.generateKeyPair();
		PublicKey publicKeyP = keypairP.getPublic();
//		PrivateKey privateKeyP = keypairP.getPrivate();
		
		String proverPublicKeyString = Base64.getEncoder().encodeToString(publicKeyP.getEncoded());
//		String proverPrivateKeyString = Base64.getEncoder().encodeToString(privateKeyP.getEncoded());
		
		//Issuer Operation
		HashMap<String, Object> cptJsonSchemaData = new HashMap<String, Object>();
        cptJsonSchemaData.put("name", "Rupal_Goyal_99");
        cptJsonSchemaData.put("gender", "F");
        cptJsonSchemaData.put("age", 22);
        cptJsonSchemaData.put("licence_info", "TOI_Prime_Subscriber");
        cptJsonSchemaData.put("id", proverPublicKeyString);
        
        IdPrivateKey idPrivateKey = new IdPrivateKey(issuerPrivateKeyString);

        CreateCredentialArgs createCredentialArgs = new CreateCredentialArgs();
        
        createCredentialArgs.setIssuer(issuerPublicKeyString);      
        createCredentialArgs.setIssuanceDate(System.currentTimeMillis());
        createCredentialArgs.setExpirationDate(System.currentTimeMillis() + 100000000000L);      
        createCredentialArgs.setIdPrivateKey(idPrivateKey);
        createCredentialArgs.setClaim(cptJsonSchemaData);
        
        createCredentialArgs.setCptId(Integer.valueOf(1001));
       
        CredentialServiceImpl credentialServiceImpl = new CredentialServiceImpl();
        
        response =
        		credentialServiceImpl.createCredential(createCredentialArgs);               
        ResponseData<String> credentialJsonDataIsuer = credentialServiceImpl.
        		getCredentialJson(response.getResult().getCredential());
        
       System.out.println("Created credential by Issuer: ");
//       return credentialJsonDataIsuer.getResult();
        if(credentialJsonDataIsuer.getResult() != null)
        	return true;
        else 
        	return false;
        //Sign after Operation file
//        return createProof(response);
	}	
	
	public static Boolean verify(ResponseData<CredentialWrapper> response, ResponseData<CredentialWrapper> selectiveCredentialWrapper) throws Exception
	{
		CredentialServiceImpl credentialServiceImplVerify = new CredentialServiceImpl();      
//        ResponseData<String> proofJsonData = credentialServiceImplVerify.getCredentialJson(response.getResult().getCredential());
//        System.out.println("Proof received by verifier: ");
//        System.out.println(proofJsonData.getResult());
        
        ResponseData<Boolean> verify =
        		credentialServiceImplVerify.verify(selectiveCredentialWrapper.getResult());
        System.out.println("Verifying Status: ");
        return verify.getResult();
	}
}
