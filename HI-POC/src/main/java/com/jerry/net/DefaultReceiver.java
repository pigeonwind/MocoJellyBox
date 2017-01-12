package com.jerry.net;

import java.io.*;
import java.nio.channels.*;
import java.util.*;

public class DefaultReceiver implements Runnable{
	private DataHandler handler;
	private Connection connection;
	private boolean isStart;

	public DefaultReceiver(Connection serverConnection) throws IOException {
		setConnection(serverConnection);
		setReceiverStatus(false);
	}

	private void setConnection(Connection serverConnection) {
		this.connection = serverConnection;
	}
	public void setHandelr(DataHandler handler) {
		this.handler = handler;
	}

	public void startUp() {
		setReceiverStatus(true);
	}
	public void shutDown(){
		setReceiverStatus(false);
	}

	private void setReceiverStatus(boolean isStart) {
		this.isStart = isStart;
	}
	private void process(SelectionKey selectedKey) throws IOException {
		if (selectedKey.isReadable()) {
			SocketChannel socketChannel = (SocketChannel) selectedKey.channel();
			byte[] recvData = connection.read(socketChannel);
			System.out.printf("	수신 전문 [%s]\n", new String(recvData));
			byte[] respData = handler.process(recvData);
			System.out.printf("	응답 전문 [%s] \n", new String(respData));
			connection.send(socketChannel, respData);
			System.out.printf("\t##채널 종료 [%s]\n", socketChannel);
			connection.close(socketChannel);
		}
	}

	@Override
	public void run() {
		Iterator<SelectionKey> keyIter;
		try {
			while (isStart) {
				connection.select();
				keyIter=connection.keyIterator();
				while(keyIter.hasNext()){
					SelectionKey selectedKey = keyIter.next();
					connection.accept(selectedKey);
					process(selectedKey);
					keyIter.remove();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
