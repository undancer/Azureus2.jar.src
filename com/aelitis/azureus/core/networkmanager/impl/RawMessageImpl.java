/*     */ package com.aelitis.azureus.core.networkmanager.impl;
/*     */ 
/*     */ import com.aelitis.azureus.core.networkmanager.RawMessage;
/*     */ import com.aelitis.azureus.core.peermanager.messaging.Message;
/*     */ import com.aelitis.azureus.core.peermanager.messaging.MessageException;
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
/*     */ public class RawMessageImpl
/*     */   implements RawMessage
/*     */ {
/*     */   private final Message message;
/*     */   private final DirectByteBuffer[] payload;
/*     */   private final int priority;
/*     */   private boolean is_no_delay;
/*     */   private final Message[] to_remove;
/*     */   
/*     */   public RawMessageImpl(Message source, DirectByteBuffer[] raw_payload, int _priority, boolean _is_no_delay, Message[] _to_remove)
/*     */   {
/*  56 */     this.message = source;
/*  57 */     this.payload = raw_payload;
/*  58 */     this.priority = _priority;
/*  59 */     this.is_no_delay = _is_no_delay;
/*  60 */     this.to_remove = _to_remove;
/*     */   }
/*     */   
/*     */ 
/*  64 */   public String getID() { return this.message.getID(); }
/*  65 */   public byte[] getIDBytes() { return this.message.getIDBytes(); }
/*     */   
/*  67 */   public String getFeatureID() { return this.message.getFeatureID(); }
/*     */   
/*  69 */   public int getFeatureSubID() { return this.message.getFeatureSubID(); }
/*     */   
/*  71 */   public int getType() { return this.message.getType(); }
/*     */   
/*  73 */   public byte getVersion() { return this.message.getVersion(); }
/*     */   
/*  75 */   public String getDescription() { return this.message.getDescription(); }
/*     */   
/*  77 */   public DirectByteBuffer[] getData() { return this.message.getData(); }
/*     */   
/*     */   public Message deserialize(DirectByteBuffer data, byte version) throws MessageException {
/*  80 */     return this.message.deserialize(data, version);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*  85 */   public DirectByteBuffer[] getRawData() { return this.payload; }
/*     */   
/*  87 */   public int getPriority() { return this.priority; }
/*     */   
/*  89 */   public boolean isNoDelay() { return this.is_no_delay; }
/*     */   
/*  91 */   public void setNoDelay() { this.is_no_delay = true; }
/*     */   
/*  93 */   public Message[] messagesToRemove() { return this.to_remove; }
/*     */   
/*  95 */   public Message getBaseMessage() { return this.message; }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void destroy()
/*     */   {
/* 103 */     for (int i = 0; i < this.payload.length; i++) {
/* 104 */       this.payload[i].returnToPool();
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/networkmanager/impl/RawMessageImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */