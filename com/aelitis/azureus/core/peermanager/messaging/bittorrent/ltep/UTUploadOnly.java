/*     */ package com.aelitis.azureus.core.peermanager.messaging.bittorrent.ltep;
/*     */ 
/*     */ import com.aelitis.azureus.core.peermanager.messaging.Message;
/*     */ import com.aelitis.azureus.core.peermanager.messaging.MessageException;
/*     */ import org.gudy.azureus2.core3.util.DirectByteBuffer;
/*     */ import org.gudy.azureus2.core3.util.DirectByteBufferPool;
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
/*     */ public class UTUploadOnly
/*     */   implements LTMessage
/*     */ {
/*     */   private final byte version;
/*  34 */   private DirectByteBuffer buffer = null;
/*     */   
/*     */ 
/*     */   private final boolean upload_only;
/*     */   
/*     */ 
/*     */ 
/*     */   public UTUploadOnly(boolean _upload_only, byte _version)
/*     */   {
/*  43 */     this.upload_only = _upload_only;
/*  44 */     this.version = _version;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getID()
/*     */   {
/*  50 */     return "upload_only";
/*     */   }
/*     */   
/*     */ 
/*     */   public byte[] getIDBytes()
/*     */   {
/*  56 */     return ID_UT_UPLOAD_ONLY_BYTES;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getFeatureID()
/*     */   {
/*  62 */     return "LT1";
/*     */   }
/*     */   
/*     */ 
/*     */   public int getFeatureSubID()
/*     */   {
/*  68 */     return 4;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getType()
/*     */   {
/*  74 */     return 0;
/*     */   }
/*     */   
/*     */ 
/*     */   public byte getVersion()
/*     */   {
/*  80 */     return this.version;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getDescription()
/*     */   {
/*  86 */     return "upload_only";
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isUploadOnly()
/*     */   {
/*  92 */     return this.upload_only;
/*     */   }
/*     */   
/*     */ 
/*     */   public DirectByteBuffer[] getData()
/*     */   {
/*  98 */     if (this.buffer == null)
/*     */     {
/* 100 */       this.buffer = DirectByteBufferPool.getBuffer((byte)28, 1);
/*     */       
/* 102 */       this.buffer.put((byte)11, (byte)(this.upload_only ? 1 : 0));
/* 103 */       this.buffer.flip((byte)11);
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
/* 116 */     byte[] dict_bytes = new byte[Math.min(128, data.remaining((byte)11))];
/*     */     
/* 118 */     data.get((byte)11, dict_bytes);
/*     */     
/* 120 */     if (dict_bytes.length != 1)
/*     */     {
/* 122 */       throw new MessageException("decode failed: incorrect length");
/*     */     }
/*     */     
/* 125 */     boolean ulo = dict_bytes[0] != 0;
/*     */     
/* 127 */     return new UTUploadOnly(ulo, version);
/*     */   }
/*     */   
/*     */ 
/*     */   public void destroy()
/*     */   {
/* 133 */     if (this.buffer != null)
/*     */     {
/* 135 */       this.buffer.returnToPool();
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/peermanager/messaging/bittorrent/ltep/UTUploadOnly.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */