package com.wallet.unhandled_exception.protocol.response;

import com.wallet.unhandled_exception.protocol.base.IdPrivateKey;
import com.wallet.unhandled_exception.protocol.base.IdPublicKey;

public class CreateIdDataResult {
    private String weId;
    private IdPublicKey userIdPublicKey;
    private IdPrivateKey userIdPrivateKey;

	public String getWeId() {
		return weId;
	}
	public void setWeId(String weId) {
		this.weId = weId;
	}
	public IdPublicKey getUserIdPublicKey() {
		return userIdPublicKey;
	}
	public void setUserIdPublicKey(IdPublicKey userIdPublicKey) {
		this.userIdPublicKey = userIdPublicKey;
	}
	public IdPrivateKey getUserIdPrivateKey() {
		return userIdPrivateKey;
	}
	public void setUserIdPrivateKey(IdPrivateKey userIdPrivateKey) {
		this.userIdPrivateKey = userIdPrivateKey;
	}
}
