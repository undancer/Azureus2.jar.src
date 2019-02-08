/*     */ package org.gudy.azureus2.pluginsimpl.local.network;
/*     */ 
/*     */ import java.nio.ByteBuffer;
/*     */ import org.gudy.azureus2.core3.util.DirectByteBuffer;
/*     */ import org.gudy.azureus2.pluginsimpl.local.messaging.MessageAdapter;
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
/*     */ public class RawMessageAdapter
/*     */   extends MessageAdapter
/*     */   implements org.gudy.azureus2.plugins.network.RawMessage, com.aelitis.azureus.core.networkmanager.RawMessage
/*     */ {
/*  35 */   private org.gudy.azureus2.plugins.network.RawMessage plug_msg = null;
/*  36 */   private com.aelitis.azureus.core.networkmanager.RawMessage core_msg = null;
/*     */   
/*     */   public RawMessageAdapter(org.gudy.azureus2.plugins.network.RawMessage plug_msg)
/*     */   {
/*  40 */     super(plug_msg);
/*  41 */     this.plug_msg = plug_msg;
/*     */   }
/*     */   
/*     */   public RawMessageAdapter(com.aelitis.azureus.core.networkmanager.RawMessage core_msg)
/*     */   {
/*  46 */     super(core_msg);
/*  47 */     this.core_msg = core_msg;
/*     */   }
/*     */   
/*     */ 
/*     */   public ByteBuffer[] getRawPayload()
/*     */   {
/*  53 */     if (this.core_msg == null) {
/*  54 */       return this.plug_msg.getRawPayload();
/*     */     }
/*     */     
/*  57 */     DirectByteBuffer[] dbbs = this.core_msg.getRawData();
/*  58 */     ByteBuffer[] bbs = new ByteBuffer[dbbs.length];
/*  59 */     for (int i = 0; i < dbbs.length; i++) {
/*  60 */       bbs[i] = dbbs[i].getBuffer(11);
/*     */     }
/*  62 */     return bbs;
/*     */   }
/*     */   
/*     */ 
/*     */   public DirectByteBuffer[] getRawData()
/*     */   {
/*  68 */     if (this.plug_msg == null) {
/*  69 */       return this.core_msg.getRawData();
/*     */     }
/*     */     
/*  72 */     ByteBuffer[] bbs = this.plug_msg.getRawPayload();
/*  73 */     DirectByteBuffer[] dbbs = new DirectByteBuffer[bbs.length];
/*  74 */     for (int i = 0; i < bbs.length; i++) {
/*  75 */       dbbs[i] = new DirectByteBuffer(bbs[i]);
/*     */     }
/*  77 */     return dbbs;
/*     */   }
/*     */   
/*     */   public int getPriority() {
/*  81 */     return 1;
/*     */   }
/*     */   
/*  84 */   public boolean isNoDelay() { return true; }
/*     */   
/*     */   public void setNoDelay() {}
/*     */   
/*  88 */   public com.aelitis.azureus.core.peermanager.messaging.Message[] messagesToRemove() { return null; }
/*     */   
/*     */   public org.gudy.azureus2.plugins.messaging.Message getOriginalMessage()
/*     */   {
/*  92 */     if (this.plug_msg == null) {
/*  93 */       return new MessageAdapter(this.core_msg.getBaseMessage());
/*     */     }
/*     */     
/*  96 */     return this.plug_msg.getOriginalMessage();
/*     */   }
/*     */   
/*     */   public com.aelitis.azureus.core.peermanager.messaging.Message getBaseMessage()
/*     */   {
/* 101 */     if (this.core_msg == null) {
/* 102 */       return new MessageAdapter(this.plug_msg.getOriginalMessage());
/*     */     }
/*     */     
/* 105 */     return this.core_msg.getBaseMessage();
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/pluginsimpl/local/network/RawMessageAdapter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */