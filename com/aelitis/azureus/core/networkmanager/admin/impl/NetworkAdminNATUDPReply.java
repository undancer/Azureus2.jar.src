/*     */ package com.aelitis.azureus.core.networkmanager.admin.impl;
/*     */ 
/*     */ import com.aelitis.net.udp.uc.PRUDPPacketReply;
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
/*     */ 
/*     */ 
/*     */ public class NetworkAdminNATUDPReply
/*     */   extends PRUDPPacketReply
/*     */ {
/*     */   private Map payload;
/*     */   
/*     */   public NetworkAdminNATUDPReply(int trans_id)
/*     */   {
/*  45 */     super(41, trans_id);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected NetworkAdminNATUDPReply(DataInputStream is, int trans_id)
/*     */     throws IOException
/*     */   {
/*  55 */     super(41, trans_id);
/*     */     
/*  57 */     short len = is.readShort();
/*     */     
/*  59 */     if (len <= 0)
/*     */     {
/*  61 */       throw new IOException("invalid length");
/*     */     }
/*     */     
/*  64 */     byte[] bytes = new byte[len];
/*     */     
/*  66 */     is.read(bytes);
/*     */     
/*  68 */     this.payload = BDecoder.decode(bytes);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void serialise(DataOutputStream os)
/*     */     throws IOException
/*     */   {
/*  77 */     super.serialise(os);
/*     */     
/*  79 */     byte[] bytes = BEncoder.encode(this.payload);
/*     */     
/*  81 */     os.writeShort((short)bytes.length);
/*     */     
/*  83 */     os.write(bytes);
/*     */   }
/*     */   
/*     */ 
/*     */   public Map getPayload()
/*     */   {
/*  89 */     return this.payload;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setPayload(Map _payload)
/*     */   {
/*  96 */     this.payload = _payload;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getString()
/*     */   {
/* 102 */     return super.getString();
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/networkmanager/admin/impl/NetworkAdminNATUDPReply.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */