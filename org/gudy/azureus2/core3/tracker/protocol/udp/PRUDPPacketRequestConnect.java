/*    */ package org.gudy.azureus2.core3.tracker.protocol.udp;
/*    */ 
/*    */ import com.aelitis.net.udp.uc.PRUDPPacketRequest;
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
/*    */ public class PRUDPPacketRequestConnect
/*    */   extends PRUDPPacketRequest
/*    */ {
/*    */   public PRUDPPacketRequestConnect()
/*    */   {
/* 41 */     super(0, 4497486125440L);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   protected PRUDPPacketRequestConnect(DataInputStream is, long con_id, int trans_id)
/*    */   {
/* 50 */     super(0, con_id, trans_id);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public void serialise(DataOutputStream os)
/*    */     throws IOException
/*    */   {
/* 59 */     super.serialise(os);
/*    */   }
/*    */   
/*    */ 
/*    */   public String getString()
/*    */   {
/* 65 */     return super.getString();
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/tracker/protocol/udp/PRUDPPacketRequestConnect.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */