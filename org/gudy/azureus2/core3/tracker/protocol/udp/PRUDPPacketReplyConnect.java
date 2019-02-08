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
/*    */ public class PRUDPPacketReplyConnect
/*    */   extends PRUDPPacketReply
/*    */ {
/*    */   protected long connection_id;
/*    */   
/*    */   public PRUDPPacketReplyConnect(int trans_id, long conn_id)
/*    */   {
/* 45 */     super(0, trans_id);
/*    */     
/* 47 */     this.connection_id = conn_id;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   protected PRUDPPacketReplyConnect(DataInputStream is, int trans_id)
/*    */     throws IOException
/*    */   {
/* 57 */     super(0, trans_id);
/*    */     
/* 59 */     this.connection_id = is.readLong();
/*    */   }
/*    */   
/*    */ 
/*    */   public long getConnectionId()
/*    */   {
/* 65 */     return this.connection_id;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public void serialise(DataOutputStream os)
/*    */     throws IOException
/*    */   {
/* 74 */     super.serialise(os);
/*    */     
/* 76 */     os.writeLong(this.connection_id);
/*    */   }
/*    */   
/*    */ 
/*    */   public String getString()
/*    */   {
/* 82 */     return super.getString().concat(",[con=").concat(String.valueOf(this.connection_id)).concat("]");
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/tracker/protocol/udp/PRUDPPacketReplyConnect.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */