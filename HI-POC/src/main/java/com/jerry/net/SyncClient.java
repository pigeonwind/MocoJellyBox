package com.jerry.net;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import org.json.simple.parser.ParseException;

import com.jerry.hipoc.contants.MocomsysConstants;
import com.jerry.message.Message;
import com.jerry.message.MessageBuilder;
import com.jerry.util.TimeUtil;

public class SyncClient implements MocomsysConstants {
	public static void main(String[] args) throws IOException, ParseException {
//		String hostname = "10.10.1.160";
//		int port = 27011;
		String hostname = "localhost";
		int port = 27001;
		/**
		 * send n recv message
		 */
		Connection connection = new NIOConnection(new InetSocketAddress(hostname, port));
		byte[] recvBytes =connection.sendNReceive(makeMassage());
		System.out.printf("recvData[%d][%s]\n", recvBytes.length, new String(recvBytes));
		connection.close();
	}
	
	public static byte[] getDummyMessage() throws FileNotFoundException, IOException, ParseException{
		return makeMassage();
	}
	
	private static byte[] makeMassage() throws FileNotFoundException, IOException, ParseException {
		byte[] header;
		byte[] endString = "@@".getBytes();
		byte[] sendData = null;
		Message sendMsg;
		/**
		 * header 생성
		 */
		System.out.println("make header");
		{
			sendMsg = MessageBuilder.build();
			header = new byte[HEADER_LENGTH];
			Arrays.fill(header, (byte) ' ');
			// globalId
			String headerSizeBlock = "00000000";
			String headerIfID = "HIPHIPON0003";
			sendMsg.setMessageFeildValue("EAI_INTF_ID", headerIfID.getBytes());
			sendMsg.setMessageFeildValue("STND_CRTN_SYS_NM", "jerry001".getBytes());
			sendMsg.setMessageFeildValue("STND_TLG_SNO", makeGID());
			sendMsg.setMessageFeildValue("STND_TLG_PGRS_NO", "00".getBytes());
			sendMsg.setMessageFeildValue("TLG_IP", (byte[]) makeLocalIP());
			sendMsg.setMessageFeildValue("ENV_INF_DST_CD", "D".getBytes());
			sendMsg.setMessageFeildValue("XA_TR_DST_CD", "0".getBytes());
			
			sendMsg.setMessageFeildValue("RQS_RSPD_DST_CD", "S".getBytes()); // 요청 : S / 응답 :R
			sendMsg.setMessageFeildValue("TR_SYNC_DST_CD", "S".getBytes()); // 동기 : S / 비동기 : A
			sendMsg.setMessageFeildValue("TLG_RSPD_DTTM", "00000000000000000".getBytes());
			
			header = setHeaderData(header, LENGTH_OFFSET, LENGTH_LENGTH, headerSizeBlock.getBytes());
			header = setHeaderData(header, 8, 1, "5".getBytes());
			header = setHeaderData(header, GLOBALID_OFFSET, GLOBALID_LENGTH, makeGID(System.currentTimeMillis()));
			header = setHeaderData(header, SEQUENCE_OFFSET, SEQUENCE_LENGTH, "00".getBytes());
			header = setHeaderData(header, TRS_SYS_CD_OFFSET, TRS_SYS_CD_LENGTH, "EAI".getBytes());
			header = setHeaderData(header, INTFID_OFFSET, INTFID_LENGTH, headerIfID.getBytes());
			header = setHeaderData(header, SYNC_OFFSET, SYNC_LENGTH, SYNC_STRING.getBytes());
			header = setHeaderData(header, REQ_RES_OFFSET, REQ_RES_LENGTH, REQUEST_STRING.getBytes());
			header = setHeaderData(header, SUCCESS_OFFSET, SUCCESS_LENGTH, SUCCESS_STRING.getBytes());
			header = setHeaderData(header, 68, 1, "D".getBytes());
			header = setHeaderData(header, 79, 1, "0".getBytes());
			header = setHeaderData(header, 98, 2, "00".getBytes());
			header = setHeaderData(header, 103, 2, "00".getBytes());
			header = setHeaderData(header, 178, 1, "0".getBytes());
			header = setHeaderData(header, 179, 1, "1".getBytes());
			header = setHeaderData(header, 184, 3, "000".getBytes());
			header = setHeaderData(header, 199, 2, "KR".getBytes());
			header = setHeaderData(header, 206, 3, "000".getBytes());
			header = setHeaderData(header, 227, 4, "0000".getBytes());
			header = setHeaderData(header, 315, 1, "1".getBytes());
			header = setHeaderData(header, 327, 1, "1".getBytes());
			header = setHeaderData(header, 328, 1, "1".getBytes());
			
			System.out.printf("headerBytes[%d][%s]\n", header.length, new String(header));
		}

		/**
		 * make send data
		 */
		{
			String data = "1000000002Hello TEST SEND DATA";
			byte[] dataBytes = data.getBytes();
			int dataSize = dataBytes.length;

			sendMsg.setMessageBodyData(dataBytes);
			System.out.println("make send data");
			System.out.printf("header block length[%d]\n", header.length);
			System.out.printf("data block length[%s]\n", dataSize);
			System.out.printf("endString block length[%s]\n", endString.length);
			
			sendData = null;
			{
				sendData = new byte[header.length + dataSize + endString.length];
				System.out.printf("sendData block length[%s]\n", sendData.length);
				// header copy
				System.arraycopy(header, 0, sendData, 0, header.length);

				// 데이터 할당
				System.arraycopy(dataBytes, 0, sendData, header.length, dataSize);
				System.arraycopy(endString, 0, sendData, header.length + dataSize, endString.length);

				// 전문 길이 설정
				byte[] len = String.valueOf(sendData.length - 8).getBytes();
				
				System.arraycopy(len, 0, sendData, 8 - len.length, len.length);
			}
			System.out.printf("sendData[%d][%s]\n", sendData.length, new String(sendData));
		}
		return sendData;
	}
	private static Object makeLocalIP() throws UnknownHostException {
		String result = Inet4Address.getLocalHost().getHostAddress();
		System.out.println(result);
		return result.getBytes();
	}

	private static byte[] makeGID() {
		// TODO Auto-generated method stub
		return TimeUtil.getCurrentTime("yyyyMMddHHmmss").getBytes();
	}

	private static byte[] setHeaderData(byte[] distBytes, int distOffset, int length, byte[] srcBytes) {
		distBytes = arrayCopy(srcBytes, 0, distBytes, distOffset, length);
		return distBytes;
	}
	
	private static byte[] makeGID(long currentTimeMillis) {
		StringBuilder result = new StringBuilder(GLOBALID_LENGTH);
		result.append(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date(currentTimeMillis)));
		result.append(currentTimeMillis);
		
		result.toString();
		byte[] gidBytes = result.toString().getBytes();
		//gidBytes = arrayCopy(hostName.getBytes(), 0, gidBytes, GLOBALID_SYS_NM_OFFSET, GLOBALID_SYS_NM_LENGTH);
		String gid = new String(gidBytes);
		System.out.printf("GID : [%d][%s][%d] \n", gidBytes.length, gid, gid.length());
		return gidBytes;
	}
	private static byte[] arrayCopy(byte[] srcBytes, int srcPos, byte[] distBytes, int destPos, int length) {
		System.out.printf("arrayCopy() : srcByteStr[%s],srcPos[%d],distByteStr[%s],destPos[%d],length[%d] \n",
				new String(srcBytes), srcPos, new String(distBytes), destPos, length);
		System.arraycopy(srcBytes, 0, distBytes, destPos, length);
		return distBytes;
	}
}
