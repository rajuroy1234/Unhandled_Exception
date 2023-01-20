package com.vc.unhandled_exception.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class VerifyResponse {
	@JsonProperty("verified")
	private String verified;
	
	@JsonProperty("error")
	private String error;

	public String getVerified() {
		return verified;
	}

	public void setVerified(String verified) {
		this.verified = verified;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	@Override
	public String toString() {
		return "VerifyResponse [verified=" + verified + ", error=" + error + "]";
	}

	public VerifyResponse() {
		super();
	}

}
