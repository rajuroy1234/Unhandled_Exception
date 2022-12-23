package com.wallet.unhandled_exception.exception;

import com.wallet.unhandled_exception.constant.ErrorCode;

@SuppressWarnings("serial")
public class DataTypeCastException extends BaseException {

	public DataTypeCastException(String msg) {
		super(msg);
	}
	
	public DataTypeCastException(Throwable cause) {
		super(ErrorCode.DATA_TYPE_CASE_ERROR.getCodeDesc(), cause);
	}
	
	public ErrorCode getErrorCode() {
		return ErrorCode.DATA_TYPE_CASE_ERROR;
	}

}