package com.jerry.message;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.json.simple.parser.ParseException;

public class MessageBuilder {
	public static Message build(byte[] byteData) throws FileNotFoundException, IOException, ParseException {
		MessageDefiner definer = new DefaultMessageDefiner();
		return new Message(definer).parse(byteData);
	}

	public static Message build() throws FileNotFoundException, IOException, ParseException {
		MessageDefiner definer = new DefaultMessageDefiner();
		return  new Message(definer).makeDefaultMessage();
	}
}
