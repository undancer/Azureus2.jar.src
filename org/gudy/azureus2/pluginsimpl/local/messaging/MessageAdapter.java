/*     */ package org.gudy.azureus2.pluginsimpl.local.messaging;
/*     */ 
/*     */ import java.nio.ByteBuffer;
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
/*     */ public class MessageAdapter
/*     */   implements org.gudy.azureus2.plugins.messaging.Message, com.aelitis.azureus.core.peermanager.messaging.Message
/*     */ {
/*  31 */   private org.gudy.azureus2.plugins.messaging.Message plug_msg = null;
/*  32 */   private com.aelitis.azureus.core.peermanager.messaging.Message core_msg = null;
/*     */   
/*     */   public MessageAdapter(org.gudy.azureus2.plugins.messaging.Message plug_msg)
/*     */   {
/*  36 */     this.plug_msg = plug_msg;
/*     */   }
/*     */   
/*     */   public MessageAdapter(com.aelitis.azureus.core.peermanager.messaging.Message core_msg)
/*     */   {
/*  41 */     this.core_msg = core_msg;
/*     */   }
/*     */   
/*     */ 
/*  45 */   public org.gudy.azureus2.plugins.messaging.Message getPluginMessage() { return this.plug_msg; }
/*     */   
/*  47 */   public com.aelitis.azureus.core.peermanager.messaging.Message getCoreMessage() { return this.core_msg; }
/*     */   
/*     */ 
/*     */ 
/*     */   public ByteBuffer[] getPayload()
/*     */   {
/*  53 */     if (this.core_msg == null) {
/*  54 */       return this.plug_msg.getPayload();
/*     */     }
/*     */     
/*  57 */     DirectByteBuffer[] dbbs = this.core_msg.getData();
/*  58 */     ByteBuffer[] bbs = new ByteBuffer[dbbs.length];
/*  59 */     for (int i = 0; i < dbbs.length; i++) {
/*  60 */       bbs[i] = dbbs[i].getBuffer(11);
/*     */     }
/*  62 */     return bbs;
/*     */   }
/*     */   
/*     */   public org.gudy.azureus2.plugins.messaging.Message create(ByteBuffer data) throws org.gudy.azureus2.plugins.messaging.MessageException {
/*  66 */     if (this.core_msg == null) {
/*  67 */       return this.plug_msg.create(data);
/*     */     }
/*     */     try
/*     */     {
/*  71 */       return new MessageAdapter(this.core_msg.deserialize(new DirectByteBuffer(data), (byte)1));
/*     */     }
/*     */     catch (com.aelitis.azureus.core.peermanager.messaging.MessageException e) {
/*  74 */       throw new org.gudy.azureus2.plugins.messaging.MessageException(e.getMessage());
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public String getID()
/*     */   {
/*  82 */     return this.core_msg == null ? this.plug_msg.getID() : this.core_msg.getID();
/*     */   }
/*     */   
/*     */   public byte[] getIDBytes() {
/*  86 */     return this.core_msg == null ? this.plug_msg.getID().getBytes() : this.core_msg.getIDBytes();
/*     */   }
/*     */   
/*     */   public int getType() {
/*  90 */     return this.core_msg == null ? this.plug_msg.getType() : this.core_msg.getType();
/*     */   }
/*     */   
/*     */   public byte getVersion() {
/*  94 */     return this.core_msg == null ? 1 : this.core_msg.getVersion();
/*     */   }
/*     */   
/*     */   public String getDescription()
/*     */   {
/*  99 */     return this.core_msg == null ? this.plug_msg.getDescription() : this.core_msg.getDescription();
/*     */   }
/*     */   
/*     */   public void destroy() {
/* 103 */     if (this.core_msg == null) this.plug_msg.destroy(); else {
/* 104 */       this.core_msg.destroy();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/* 111 */   public String getFeatureID() { return "AZPLUGMSG"; }
/*     */   
/* 113 */   public int getFeatureSubID() { return -1; }
/*     */   
/*     */   public DirectByteBuffer[] getData()
/*     */   {
/* 117 */     if (this.plug_msg == null) {
/* 118 */       return this.core_msg.getData();
/*     */     }
/*     */     
/* 121 */     ByteBuffer[] bbs = this.plug_msg.getPayload();
/* 122 */     DirectByteBuffer[] dbbs = new DirectByteBuffer[bbs.length];
/* 123 */     for (int i = 0; i < bbs.length; i++) {
/* 124 */       dbbs[i] = new DirectByteBuffer(bbs[i]);
/*     */     }
/* 126 */     return dbbs;
/*     */   }
/*     */   
/*     */   public com.aelitis.azureus.core.peermanager.messaging.Message deserialize(DirectByteBuffer data, byte version) throws com.aelitis.azureus.core.peermanager.messaging.MessageException {
/* 130 */     if (this.plug_msg == null) {
/* 131 */       return this.core_msg.deserialize(data, version);
/*     */     }
/*     */     try
/*     */     {
/* 135 */       org.gudy.azureus2.plugins.messaging.Message message = this.plug_msg.create(data.getBuffer((byte)11));
/*     */       
/* 137 */       if (message == null)
/*     */       {
/* 139 */         throw new com.aelitis.azureus.core.peermanager.messaging.MessageException("Plugin message deserialisation failed");
/*     */       }
/*     */       
/* 142 */       return new MessageAdapter(message);
/*     */     }
/*     */     catch (org.gudy.azureus2.plugins.messaging.MessageException e) {
/* 145 */       throw new com.aelitis.azureus.core.peermanager.messaging.MessageException(e.getMessage());
/*     */     }
/*     */     finally {
/* 148 */       data.returnToPool();
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/pluginsimpl/local/messaging/MessageAdapter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */