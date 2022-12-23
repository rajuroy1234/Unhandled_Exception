package com.wallet.unhandled_exception.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wallet.unhandled_exception.model.VerCred;

@RestController
@RequestMapping("/wallet")
public class APIs {
	@GetMapping("/issue")
	public String issue() throws Exception
	{
		if(VerCred.issue())
		{
			if(VerCred.sign(VerCred.response))
				return "Signed with Proofs";
			else
				return "Signing error";
		}
		else
			return "Could not issue";
			
	}
	
	@GetMapping("/verify")
	public Boolean verify()
	{
		try {
			return VerCred.verify(VerCred.response,VerCred.selectiveCredentialWrapper);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
}
