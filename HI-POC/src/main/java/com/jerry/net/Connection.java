package com.jerry.net;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
/**
 * client side
 * 	> init(InetSocketAddress remoteAddress)
 * 	> sendNReceive(byte[] messageBytes)
 *  > close
 * server side
 *  > init(int listenPort)
 *  > select
 *  > keyIterator
 *  > accept
 *  > process
 *  > 
 * @author jerry
 *
 */
public interface Connection {

	/**
	 * client side ( remote channel )
	 * @throws IOException
	 */
	void close() throws IOException;

	/**
	 * client side
	 * @param messageBytes
	 * @return
	 * @throws IOException
	 */
	byte[] sendNReceive(byte[] messageBytes) throws IOException;

	/**
	 * server side
	 * @param socketChannel
	 * @param sendByteArray
	 * @throws IOException
	 */
	void send(SocketChannel socketChannel, byte[] sendByteArray) throws IOException;

	/**
	 * server side
	 * channel not close
	 * @param channel
	 * @return
	 * @throws IOException
	 */
	byte[] read(SocketChannel channel) throws IOException;

	/**
	 * server side
	 * @return
	 */
	Selector getSelector();
	/**
	 * server side
	 * @return
	 * @throws IOException
	 */
	int select() throws IOException;

	Iterator<SelectionKey> keyIterator();
	/**
	 * server side
	 * @param selectedKey
	 * @throws IOException
	 */
	void accept(SelectionKey selectedKey) throws IOException;
	/**
	 * server side
	 * @param socketChannel
	 * @throws IOException
	 */
	void close(SocketChannel socketChannel) throws IOException;
}