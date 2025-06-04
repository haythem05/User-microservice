package tn.esprit.pokerplaning.Services.User.twilio;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
@ConfigurationProperties("twilio")
public class Twilioproperties {
	
	
	public Twilioproperties() {
		this.accountSid="AC0ee5594092accfe1a8828b3ef8e7b070";
		this.authToken="2045006392ea0f403ae93ad2bdf91019";
		this.fromNumber="+19382015306";
	}
	
	private String accountSid;
	private String authToken;
	private String fromNumber;

	public void setAccountSid(String accountSid) {
		this.accountSid = accountSid;
	}

	public void setAuthToken(String authToken) {
		this.authToken = authToken;
	}

	public void setFromNumber(String fromNumber) {
		this.fromNumber = fromNumber;
	}
	@Override
	public String toString() {
		return "Twilioproperties [accountSid=" + accountSid + ", authToken=" + authToken + ", fromNumber=" + fromNumber
				+ "]";
	}
	
	
	

}
