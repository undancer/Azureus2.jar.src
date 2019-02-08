/*     */ package com.aelitis.azureus.core.peermanager.messaging.bittorrent;
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
/*     */ public class BTRawMessage
/*     */   implements BTMessage, RawMessage
/*     */ {
/*     */   private final DirectByteBuffer buffer;
/*     */   
/*     */   public BTRawMessage(DirectByteBuffer _buffer)
/*     */   {
/*  39 */     this.buffer = _buffer;
/*     */   }
/*     */   
/*     */   public String getID()
/*     */   {
/*  44 */     return "";
/*     */   }
/*     */   
/*     */ 
/*     */   public byte[] getIDBytes()
/*     */   {
/*  50 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getFeatureID()
/*     */   {
/*  56 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getFeatureSubID()
/*     */   {
/*  62 */     return 0;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getType()
/*     */   {
/*  68 */     return 1;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getDescription()
/*     */   {
/*  74 */     return "<raw bt data>";
/*     */   }
/*     */   
/*     */ 
/*     */   public byte getVersion()
/*     */   {
/*  80 */     return 1;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Message deserialize(DirectByteBuffer data, byte version)
/*     */     throws MessageException
/*     */   {
/*  90 */     throw new MessageException("not imp");
/*     */   }
/*     */   
/*     */ 
/*     */   public DirectByteBuffer[] getData()
/*     */   {
/*  96 */     return new DirectByteBuffer[] { this.buffer };
/*     */   }
/*     */   
/*     */ 
/*     */   public DirectByteBuffer[] getRawData()
/*     */   {
/* 102 */     return new DirectByteBuffer[] { this.buffer };
/*     */   }
/*     */   
/*     */ 
/*     */   public int getPriority()
/*     */   {
/* 108 */     return 2;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isNoDelay()
/*     */   {
/* 114 */     return true;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setNoDelay() {}
/*     */   
/*     */ 
/*     */ 
/*     */   public Message[] messagesToRemove()
/*     */   {
/* 125 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */   public Message getBaseMessage()
/*     */   {
/* 131 */     return this;
/*     */   }
/*     */   
/*     */ 
/*     */   public void destroy()
/*     */   {
/* 137 */     if (this.buffer != null) this.buffer.returnToPool();
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/peermanager/messaging/bittorrent/BTRawMessage.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */