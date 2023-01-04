package com.wallet.unhandled_exception.controller;

import com.wallet.unhandled_exception.exceptions.Inaccurate;
import com.wallet.unhandled_exception.exceptions.NullExceptions;
import com.wallet.unhandled_exception.response.FinalResponse;
import com.wallet.unhandled_exception.response.Response;

import lombok.NonNull;

import java.io.IOException;
import org.apache.commons.codec.DecoderException;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;



@RestController
@RequestMapping("/generate")
public class IssueDID {
	
	@PostMapping(value = "/did", consumes = {MediaType.APPLICATION_JSON_VALUE},
	        produces = {MediaType.APPLICATION_JSON_VALUE})
	public static FinalResponse generateDID(@RequestBody @NonNull String request) throws DecoderException, NullExceptions, IOException, Inaccurate 
	{
		return Response.genResponse(request);
	}
	
}
