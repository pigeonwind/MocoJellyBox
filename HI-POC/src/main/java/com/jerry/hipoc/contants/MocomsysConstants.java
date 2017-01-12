package com.jerry.hipoc.contants;

public interface MocomsysConstants {
	/*
	 */
	public final static String CURRENT_PROJECT_NAME = "HIPOC";
	  public static final int HEADER_LENGTH = 512;
	  public static final int LENGTH_OFFSET = 0;
	  public static final int LENGTH_LENGTH = 8;
	  public static final boolean ISTOTAL_LENGTH = false;
	  public static final int GLOBALID_LENGTH = 30;
	  public static final int GLOBALID_OFFSET = 9;
	  public static final int GLOBALID_SYS_NM_LENGTH = 8;
	  public static final int GLOBALID_SYS_NM_OFFSET = 17;
	  
	  public static final int TRS_SYS_CD_LENGTH = 3;
	  public static final int TRS_SYS_CD_OFFSET = 100;
	  
	  public static final int SEQUENCE_OFFSET = 39;
	  public static final int SEQUENCE_LENGTH = 2;
	  
	  public static final int INTFID_LENGTH = 12;
	  public static final int INTFID_OFFSET = 145;
	  
	  public static final int SND_SVC_LENGTH = 12;
	  public static final int SND_SVC_OFFSET = 105;
	  
	  public static final int RTN_SVC_LENGTH = 12;
	  public static final int RTN_SVC_OFFSET = 117;
	  
	  public static final int REQ_RES_LENGTH = 1;
	  public static final int REQ_RES_OFFSET = 159;
	  public static final String REQUEST_STRING = "S";
	  public static final String RESPONSE_STRING = "R";
	  public static final int SUCCESS_LENGTH = 1;
	  public static final int SUCCESS_OFFSET = 204;
	  public static final String SUCCESS_STRING = "1";
	  public static final String FAIL_STRING = "2";
	  public static final int SYNC_LENGTH = 1;
	  public static final int SYNC_OFFSET = 158;
	  
	  public static final String SYNC_STRING = "S";
	  public static final String ASYNC_STRING = "A";
	  
	  public static final int LT_CHNL_TPCD_LENGTH = 2;
	  public static final int LT_CHNL_TPCD_OFFSET = 225;
	  
	  public static final int TIMEOUT_USE_OFFSET = 177;
	  public static final int TIMEOUT_USE_LENGTH = 1;
	  
	  public static final int TIMEOUT_START_OFFSET = 178;
	  public static final int TIMEOUT_START_LENGTH = 6;
	  public static final int TIMEOUT_TIME_OFFSET = 184;
	  public static final int TIMEOUT_TIME_LENGTH = 3;
	  
	  public static final int RTN_DATE_LENGTH = 17;
	  public static final int RTN_DATE_OFFSET = 187;
	  
	  public static final int NODE_LENGTH = 2;
	  public static final int NODE_OFFSET = 231;
	  
	  public static final int MESSAGE_TYPE_LENGTH = 1;
	  public static final int MESSAGE_TYPE_OFFSET = 205;
	  public static final int MESSAGE_INDEX_LENGTH = 3;
	  public static final int MESSAGE_INDEX_OFFSET = 206;
	  public static final String END_STRING = "@@";

}
