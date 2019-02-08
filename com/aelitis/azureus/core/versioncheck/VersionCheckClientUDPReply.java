/*    */ package com.aelitis.azureus.core.versioncheck;
/*    */ 
/*    */ import com.aelitis.net.udp.uc.PRUDPPacketReply;
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
/*    */ 
/*    */ 
/*    */ public class VersionCheckClientUDPReply
/*    */   extends PRUDPPacketReply
/*    */ {
/*    */   private Map<String, Object> payload;
/*    */   
/*    */   public VersionCheckClientUDPReply(int trans_id)
/*    */   {
/* 42 */     super(33, trans_id);
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   protected VersionCheckClientUDPReply(DataInputStream is, int trans_id)
/*    */     throws IOException
/*    */   {
/* 52 */     super(33, trans_id);
/*    */     
/* 54 */     short len = is.readShort();
/*    */     
/* 56 */     if (len <= 0)
/*    */     {
/* 58 */       throw new IOException("invalid length");
/*    */     }
/*    */     
/* 61 */     byte[] bytes = new byte[len];
/*    */     
/* 63 */     is.read(bytes);
/*    */     
/* 65 */     this.payload = BDecoder.decode(bytes);
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
/* 76 */     byte[] bytes = BEncoder.encode(this.payload);
/*    */     
/* 78 */     os.writeShort((short)bytes.length);
/*    */     
/* 80 */     os.write(bytes);
/*    */   }
/*    */   
/*    */ 
/*    */   public Map<String, Object> getPayload()
/*    */   {
/* 86 */     return this.payload;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public void setPayload(Map<String, Object> _payload)
/*    */   {
/* 93 */     this.payload = _payload;
/*    */   }
/*    */   
/*    */ 
/*    */   public String getString()
/*    */   {
/* 99 */     return super.getString();
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/versioncheck/VersionCheckClientUDPReply.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */