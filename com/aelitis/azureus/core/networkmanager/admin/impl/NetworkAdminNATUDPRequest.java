/*     */ package com.aelitis.azureus.core.networkmanager.admin.impl;
/*     */ 
/*     */ import com.aelitis.net.udp.uc.PRUDPPacketRequest;
/*     */ import java.io.DataInputStream;
/*     */ import java.io.DataOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.util.Map;
/*     */ import org.gudy.azureus2.core3.util.BDecoder;
/*     */ import org.gudy.azureus2.core3.util.BEncoder;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class NetworkAdminNATUDPRequest
/*     */   extends PRUDPPacketRequest
/*     */ {
/*     */   private Map payload;
/*     */   
/*     */   public NetworkAdminNATUDPRequest(long connection_id)
/*     */   {
/*  43 */     super(40, connection_id);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected NetworkAdminNATUDPRequest(DataInputStream is, long connection_id, int trans_id)
/*     */     throws IOException
/*     */   {
/*  54 */     super(40, connection_id, trans_id);
/*     */     
/*  56 */     short len = is.readShort();
/*     */     
/*  58 */     if (len <= 0)
/*     */     {
/*  60 */       throw new IOException("invalid length");
/*     */     }
/*     */     
/*  63 */     byte[] bytes = new byte[len];
/*     */     
/*  65 */     is.read(bytes);
/*     */     
/*  67 */     this.payload = BDecoder.decode(bytes);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void serialise(DataOutputStream os)
/*     */     throws IOException
/*     */   {
/*  76 */     super.serialise(os);
/*     */     
/*  78 */     byte[] bytes = BEncoder.encode(this.payload);
/*     */     
/*  80 */     os.writeShort((short)bytes.length);
/*     */     
/*  82 */     os.write(bytes);
/*     */   }
/*     */   
/*     */ 
/*     */   public Map getPayload()
/*     */   {
/*  88 */     return this.payload;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setPayload(Map _payload)
/*     */   {
/*  95 */     this.payload = _payload;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getString()
/*     */   {
/* 101 */     return super.getString();
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/networkmanager/admin/impl/NetworkAdminNATUDPRequest.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */