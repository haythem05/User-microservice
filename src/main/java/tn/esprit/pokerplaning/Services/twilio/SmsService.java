package tn.esprit.pokerplaning.Services.twilio;


import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.esprit.pokerplaning.Entities.SmsRequest;


@Service
public class SmsService {

	private final Twilioproperties twilioproperties;
	
	@Autowired
	public SmsService(Twilioproperties twilioproperties)
	{
		this.twilioproperties=twilioproperties;
	}
	
	//send message to number
	public String sendSms(SmsRequest smsRequest) {
		Twilio.init(this.twilioproperties.getAccountSid(), this.twilioproperties.getAuthToken());
		Message message = Message.creator(
						new PhoneNumber(smsRequest.getNumber()),
						new PhoneNumber(this.twilioproperties.getFromNumber()),
						smsRequest.getMessage())
				.create();
		return message.getStatus().toString();
	}

}
