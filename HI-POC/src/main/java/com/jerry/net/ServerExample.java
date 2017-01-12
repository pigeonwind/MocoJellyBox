package com.jerry.net;

import java.io.*;

import org.json.simple.parser.ParseException;

import com.jerry.message.Message;
import com.jerry.message.MessageBuilder;

public class ServerExample {

	public static void main(String[] args) throws IOException{
		int listenPort = 27001;
		System.out.println("start server");
		DefaultReceiver receiver = new DefaultReceiver(new NIOConnection(listenPort));
		DataHandler handelr = new DataHandler() {
			MessageBuilder builder = new MessageBuilder();
			@Override
			public byte[] process(byte[] inputData) {
				Message inputMsg=null;
				try {
					System.out.println("-+++++++++++++++++++++++++++++++++");
					inputMsg=builder.build(inputData);
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				String data=new String(inputMsg.getMessageBodyData());
				System.out.printf("recv data[%s]",data);
				byte[] outputData=inputMsg.getBytes();
				return outputData;
			}
		};
		receiver.setHandelr(handelr);
		receiver.startUp();
		System.out.println("wait client...");
		new Thread(receiver).start();
	}
}
