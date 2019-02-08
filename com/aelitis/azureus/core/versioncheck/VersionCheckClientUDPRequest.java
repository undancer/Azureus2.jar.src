/*    */ package com.aelitis.azureus.core.versioncheck;
/*    */ 
/*    */ import com.aelitis.net.udp.uc.PRUDPPacketRequest;
/*    */ import java.io.DataInputStream;
/*    */ import java.io.DataOutputStream;
/*    */ import java.io.IOException;
/*    */ import java.util.Map;
/*    */ import org.gudy.azureus2.core3.util.BDecoder;
/*    */ import org.gudy.azureus2.core3.util.BEncoder;
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
/*    */ public class VersionCheckClientUDPRequest
/*    */   extends PRUDPPacketRequest
/*    */ {
/*    */   private Map<String, Object> payload;
/*    */   
/*    */   public VersionCheckClientUDPRequest(long connection_id)
/*    */   {
/* 40 */     super(32, connection_id);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   protected VersionCheckClientUDPRequest(DataInputStream is, long connection_id, int trans_id)
/*    */     throws IOException
/*    */   {
/* 51 */     super(32, connection_id, trans_id);
/*    */     
/* 53 */     short len = is.readShort();
/*    */     
/* 55 */     if (len <= 0)
/*    */     {
/* 57 */       throw new IOException("invalid length");
/*    */     }
/*    */     
/* 60 */     byte[] bytes = new byte[len];
/*    */     
/* 62 */     is.read(bytes);
/*    */     
/* 64 */     this.payload = BDecoder.decode(bytes);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public void serialise(DataOutputStream os)
/*    */     throws IOException
/*    */   {
/* 73 */     super.serialise(os);
/*    */     
/* 75 */     byte[] bytes = BEncoder.encode(this.payload);
/*    */     
/* 77 */     os.writeShort((short)bytes.length);
/*    */     
/* 79 */     os.write(bytes);
/*    */   }
/*    */   
/*    */ 
/*    */   public Map<String, Object> getPayload()
/*    */   {
/* 85 */     return this.payload;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public void setPayload(Map<String, Object> _payload)
/*    */   {
/* 92 */     this.payload = _payload;
/*    */   }
/*    */   
/*    */ 
/*    */   public String getString()
/*    */   {
/* 98 */     return super.getString();
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/versioncheck/VersionCheckClientUDPRequest.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */