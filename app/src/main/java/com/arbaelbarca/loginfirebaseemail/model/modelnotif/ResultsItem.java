package com.arbaelbarca.loginfirebaseemail.model.modelnotif;

import com.google.gson.annotations.SerializedName;

public class ResultsItem{

	@SerializedName("message_id")
	private String messageId;

	public void setMessageId(String messageId){
		this.messageId = messageId;
	}

	public String getMessageId(){
		return messageId;
	}

	@Override
 	public String toString(){
		return 
			"ResultsItem{" + 
			"message_id = '" + messageId + '\'' + 
			"}";
		}
}