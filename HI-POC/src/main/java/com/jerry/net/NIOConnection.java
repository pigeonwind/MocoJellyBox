package com.jerry.net;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

import com.jerry.hipoc.contants.MocomsysConstants;

public class NIOConnection implements Connection {
	Selector selector;
	SelectableChannel channel;
	Iterator<SelectionKey> selectKeyIterator;
	/**
	 * serverSide
	 * @param listenPort
	 * @throws IOException 
	 */
	public NIOConnection(int listenPort) throws IOException{
		init(listenPort);
	}
	/**
	 * clientSide
	 * @param remoteAddress
	 * @throws IOException 
	 */
	public NIOConnection(InetSocketAddress remoteAddress) throws IOException{
		init(remoteAddress);
	}
	private void openSelctor() throws IOException{
		selector=Selector.open();
	}
	/**
	 * serverSide
	 * @param listenPort
	 * @throws IOException
	 */
	public void init(int listenPort) throws IOException{
		// selector open
		openSelctor();
		ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
		bind(serverSocketChannel,new InetSocketAddress(listenPort));
		configureBlocking(serverSocketChannel,false);
		register(serverSocketChannel, serverSocketChannel.validOps());
	}
	private void register(SelectableChannel channel, int validOps) throws ClosedChannelException {
		channel.register(selector, validOps);
	}
	private void bind(ServerSocketChannel channel, InetSocketAddress endpointAddress) throws IOException {
		channel.socket().bind(endpointAddress);
	}
	private void configureBlocking(SelectableChannel channel, boolean block) throws IOException {
		channel.configureBlocking(block);
	}
	/**
	 * clientSide
	 * @param remoteAddress
	 * @throws IOException
	 */
	private void init(InetSocketAddress remoteAddress) throws IOException {
		openSelctor();
		channel = SocketChannel.open(remoteAddress);
		configureBlocking(channel,false);
	}
	/* (non-Javadoc)
	 * @see com.jerry.net.Connection#close()
	 */
	@Override
	public void close() throws IOException {
		channel.close();
	}
	/* (non-Javadoc)
	 * @see com.jerry.net.Connection#sendNReceive(byte[])
	 */
	@Override
	public byte[] sendNReceive(byte[] messageBytes) throws IOException{
		send(messageBytes);
		return receive(SelectionKey.OP_READ);
	}
	
	private void send(byte[] messageBytes) throws IOException {
		ByteBuffer buffers = ByteBuffer.wrap(messageBytes);
		((SocketChannel) channel).write(buffers);
		buffers.clear();
	}
	/* (non-Javadoc)
	 * @see com.jerry.net.Connection#send(java.nio.channels.SocketChannel, byte[])
	 */
	@Override
	public void send(SocketChannel socketChannel, byte[] sendByteArray) throws IOException {
		ByteBuffer dataBuffer = ByteBuffer.wrap(sendByteArray);
		socketChannel.write(dataBuffer);
		dataBuffer.clear();
	}
	
	/* (non-Javadoc)
	 * @see com.jerry.net.Connection#receive(int)
	 */
	private byte[] receive(int selectionKeyOpRead) throws IOException{
		//receive
		byte[] returnBytes=null;
		register(channel,selectionKeyOpRead);
		while(true){
			select();
			selectKeyIterator = keyIterator();
			SelectionKey key;
			while(selectKeyIterator.hasNext()){
				key = selectKeyIterator.next();
				if(key.isReadable()){
					returnBytes = read(key);
				}
				selectKeyIterator.remove();
			}
			if(returnBytes.length>MocomsysConstants.LENGTH_LENGTH){
				break;
			}
		}
		return returnBytes;
	}
	private byte[] read(SelectionKey key) throws IOException {
		byte[] result,sizeBytes,dataBytes;
		SocketChannel channel;
		/*read data from channel*/{
			channel= (SocketChannel) key.channel();
			sizeBytes = readData(channel,MocomsysConstants.LENGTH_LENGTH);
			int rtnDataSize = Integer.parseInt(new String(sizeBytes));
			dataBytes =readData(channel,rtnDataSize);
			channel.finishConnect();
			channel.close();
		}
		/*allocated result data*/{
			result =new byte[sizeBytes.length+dataBytes.length];
			System.arraycopy(sizeBytes, 0, result, 0, sizeBytes.length);
			System.arraycopy(dataBytes, 0, result, sizeBytes.length, dataBytes.length);
		}
		return result;
	}
	/* (non-Javadoc)
	 * @see com.jerry.net.Connection#read(java.nio.channels.SocketChannel)
	 */
	@Override
	public byte[] read(SocketChannel channel) throws IOException {
		byte[] result,sizeBytes,dataBytes;
		/*read data from channel*/{
			sizeBytes = readData(channel,MocomsysConstants.LENGTH_LENGTH);
			int rtnDataSize = Integer.parseInt(new String(sizeBytes));
			dataBytes =readData(channel,rtnDataSize);
		}
		/*allocated result data*/{
			result =new byte[sizeBytes.length+dataBytes.length];
			System.arraycopy(sizeBytes, 0, result, 0, sizeBytes.length);
			System.arraycopy(dataBytes, 0, result, sizeBytes.length, dataBytes.length);
		}
		return result;
	}
	private byte[] readData(SocketChannel chanel, int length) throws IOException {
		ByteBuffer dataByteBuffer = ByteBuffer.allocateDirect(length);
		chanel.read(dataByteBuffer);
		return getData(dataByteBuffer);
	}
	
	private byte[] getData(ByteBuffer targetByteBuffer) {
		byte[] result;
		targetByteBuffer.flip();
		result= new byte[targetByteBuffer.remaining()];
		targetByteBuffer.get(result);
		targetByteBuffer.clear();
		return result;
	}

	/* (non-Javadoc)
	 * @see com.jerry.net.Connection#getSelector()
	 */
	@Override
	public Selector getSelector() {
		return selector;
	}

	/* (non-Javadoc)
	 * @see com.jerry.net.Connection#select()
	 */
	@Override
	public int select() throws IOException {
		return selector.select();
	}
	private Set<SelectionKey> selectedKeys() {
		return selector.selectedKeys();
	}
	/* (non-Javadoc)
	 * @see com.jerry.net.Connection#keyIterator()
	 */
	@Override
	public Iterator<SelectionKey> keyIterator() {
		return selectedKeys().iterator();
	}
	/* (non-Javadoc)
	 * @see com.jerry.net.Connection#accept(java.nio.channels.SelectionKey)
	 */
	@Override
	public void accept(SelectionKey selectedKey) throws IOException {
		if(selectedKey.isAcceptable()){
			ServerSocketChannel serverChannel = (ServerSocketChannel) selectedKey.channel();
			SocketChannel socketChannel = serverChannel.accept();
			if (socketChannel == null) {
				System.out.println("\t## null server socket ##");
				return;
			} // end of if
			System.out.println("\t## socket accepted : " + socketChannel);
			socketChannel.configureBlocking(false);
			socketChannel.register(selector, SelectionKey.OP_CONNECT | SelectionKey.OP_READ | SelectionKey.OP_WRITE);
		}
	}
	/* (non-Javadoc)
	 * @see com.jerry.net.Connection#close(java.nio.channels.SocketChannel)
	 */
	@Override
	public void close(SocketChannel socketChannel) throws IOException {
		socketChannel.finishConnect();
		socketChannel.close();
	}
}