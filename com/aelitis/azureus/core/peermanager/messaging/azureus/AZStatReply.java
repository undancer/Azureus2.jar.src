/*     */ package com.aelitis.azureus.core.peermanager.messaging.azureus;
/*     */ 
/*     */ import com.aelitis.azureus.core.peermanager.messaging.Message;
/*     */ import com.aelitis.azureus.core.peermanager.messaging.MessageException;
/*     */ import com.aelitis.azureus.core.peermanager.messaging.MessagingUtil;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import org.gudy.azureus2.core3.util.DirectByteBuffer;
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
/*     */ public class AZStatReply
/*     */   implements AZMessage
/*     */ {
/*     */   private final byte version;
/*  37 */   private DirectByteBuffer buffer = null;
/*     */   
/*     */ 
/*     */   private final Map reply;
/*     */   
/*     */ 
/*     */ 
/*     */   public AZStatReply(Map _reply, byte _version)
/*     */   {
/*  46 */     this.reply = _reply;
/*  47 */     this.version = _version;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getID()
/*     */   {
/*  53 */     return "AZ_STAT_REP";
/*     */   }
/*     */   
/*     */ 
/*     */   public byte[] getIDBytes()
/*     */   {
/*  59 */     return AZMessage.ID_AZ_STAT_REPLY_BYTES;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getFeatureID()
/*     */   {
/*  65 */     return "AZ1";
/*     */   }
/*     */   
/*     */ 
/*     */   public int getFeatureSubID()
/*     */   {
/*  71 */     return 7;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getType()
/*     */   {
/*  77 */     return 0;
/*     */   }
/*     */   
/*  80 */   public byte getVersion() { return this.version; }
/*     */   
/*     */ 
/*     */   public String getDescription()
/*     */   {
/*  85 */     return getID() + ": " + this.reply;
/*     */   }
/*     */   
/*     */ 
/*     */   public Map getReply()
/*     */   {
/*  91 */     return this.reply;
/*     */   }
/*     */   
/*     */ 
/*     */   public DirectByteBuffer[] getData()
/*     */   {
/*  97 */     if (this.buffer == null)
/*     */     {
/*  99 */       Map map = new HashMap();
/*     */       
/* 101 */       map.put("reply", this.reply);
/*     */       
/* 103 */       this.buffer = MessagingUtil.convertPayloadToBencodedByteStream(map, (byte)12);
/*     */     }
/*     */     
/* 106 */     return new DirectByteBuffer[] { this.buffer };
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Message deserialize(DirectByteBuffer data, byte version)
/*     */     throws MessageException
/*     */   {
/* 116 */     Map payload = MessagingUtil.convertBencodedByteStreamToPayload(data, 1, getID());
/*     */     
/* 118 */     Map reply = (Map)payload.get("reply");
/*     */     
/* 120 */     return new AZStatReply(reply, version);
/*     */   }
/*     */   
/*     */ 
/*     */   public void destroy()
/*     */   {
/* 126 */     if (this.buffer != null)
/*     */     {
/* 128 */       this.buffer.returnToPool();
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/peermanager/messaging/azureus/AZStatReply.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */