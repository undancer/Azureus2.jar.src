/*    */ package org.gudy.azureus2.core3.tracker.protocol.udp;
/*    */ 
/*    */ import com.aelitis.net.udp.uc.PRUDPPacketReply;
/*    */ import java.io.DataInputStream;
/*    */ import java.io.DataOutputStream;
/*    */ import java.io.IOException;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class PRUDPPacketReplyError
/*    */   extends PRUDPPacketReply
/*    */ {
/*    */   protected String message;
/*    */   
/*    */   public PRUDPPacketReplyError(int trans_id, String _message)
/*    */   {
/* 45 */     super(3, trans_id);
/*    */     
/* 47 */     this.message = _message;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   protected PRUDPPacketReplyError(DataInputStream is, int trans_id)
/*    */     throws IOException
/*    */   {
/* 57 */     super(3, trans_id);
/*    */     
/* 59 */     int avail = is.available();
/*    */     
/* 61 */     byte[] data = new byte[avail];
/*    */     
/* 63 */     is.read(data);
/*    */     
/* 65 */     this.message = new String(data);
/*    */   }
/*    */   
/*    */ 
/*    */   public String getMessage()
/*    */   {
/* 71 */     return this.message;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public void serialise(DataOutputStream os)
/*    */     throws IOException
/*    */   {
/* 80 */     super.serialise(os);
/*    */     
/* 82 */     byte[] data = this.message.getBytes();
/*    */     
/* 84 */     os.write(data);
/*    */   }
/*    */   
/*    */ 
/*    */   public String getString()
/*    */   {
/* 90 */     return super.getString().concat(",[msg=").concat(this.message).concat("]");
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/tracker/protocol/udp/PRUDPPacketReplyError.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */