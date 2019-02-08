/*    */ package org.gudy.azureus2.core3.torrent;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class TOTorrentException
/*    */   extends Exception
/*    */ {
/*    */   public static final int RT_FILE_NOT_FOUND = 1;
/*    */   
/*    */ 
/*    */ 
/*    */   public static final int RT_ZERO_LENGTH = 2;
/*    */   
/*    */ 
/*    */ 
/*    */   public static final int RT_TOO_BIG = 3;
/*    */   
/*    */ 
/*    */ 
/*    */   public static final int RT_READ_FAILS = 4;
/*    */   
/*    */ 
/*    */ 
/*    */   public static final int RT_WRITE_FAILS = 5;
/*    */   
/*    */ 
/*    */   public static final int RT_DECODE_FAILS = 6;
/*    */   
/*    */ 
/*    */   public static final int RT_UNSUPPORTED_ENCODING = 7;
/*    */   
/*    */ 
/*    */   public static final int RT_HASH_FAILS = 8;
/*    */   
/*    */ 
/*    */   public static final int RT_CANCELLED = 9;
/*    */   
/*    */ 
/*    */   protected final int reason;
/*    */   
/*    */ 
/*    */ 
/*    */   public TOTorrentException(String _str, int _reason)
/*    */   {
/* 46 */     super(_str);
/*    */     
/* 48 */     this.reason = _reason;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public TOTorrentException(String _str, int _reason, Throwable cause)
/*    */   {
/* 57 */     this(_str, _reason);
/*    */     
/* 59 */     initCause(cause);
/*    */   }
/*    */   
/*    */ 
/*    */   public int getReason()
/*    */   {
/* 65 */     return this.reason;
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/torrent/TOTorrentException.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */