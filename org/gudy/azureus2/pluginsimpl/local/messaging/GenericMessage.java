/*     */ package org.gudy.azureus2.pluginsimpl.local.messaging;
/*     */ 
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
/*     */ public class GenericMessage
/*     */   implements Message
/*     */ {
/*  31 */   private DirectByteBuffer buffer = null;
/*     */   
/*     */ 
/*     */   private final String id;
/*     */   
/*     */ 
/*     */   private final String desc;
/*     */   
/*     */   private final boolean already_encoded;
/*     */   
/*     */ 
/*     */   protected GenericMessage(String _id, String _desc, DirectByteBuffer _buffer, boolean _already_encoded)
/*     */   {
/*  44 */     this.id = _id;
/*  45 */     this.desc = _desc;
/*  46 */     this.buffer = _buffer;
/*  47 */     this.already_encoded = _already_encoded;
/*     */   }
/*     */   
/*     */ 
/*     */   protected boolean isAlreadyEncoded()
/*     */   {
/*  53 */     return this.already_encoded;
/*     */   }
/*     */   
/*     */   public String getID()
/*     */   {
/*  58 */     return this.id;
/*     */   }
/*     */   
/*     */ 
/*     */   public byte[] getIDBytes()
/*     */   {
/*  64 */     return this.id.getBytes();
/*     */   }
/*     */   
/*     */ 
/*     */   public String getFeatureID()
/*     */   {
/*  70 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getFeatureSubID()
/*     */   {
/*  76 */     return 0;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getType()
/*     */   {
/*  82 */     return 1;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getDescription()
/*     */   {
/*  88 */     return this.desc;
/*     */   }
/*     */   
/*     */ 
/*     */   public byte getVersion()
/*     */   {
/*  94 */     return 1;
/*     */   }
/*     */   
/*     */ 
/*     */   public DirectByteBuffer getPayload()
/*     */   {
/* 100 */     return this.buffer;
/*     */   }
/*     */   
/*     */ 
/*     */   public DirectByteBuffer[] getData()
/*     */   {
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
/* 116 */     throw new MessageException("not imp");
/*     */   }
/*     */   
/*     */ 
/*     */   public void destroy()
/*     */   {
/* 122 */     this.buffer.returnToPool();
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/pluginsimpl/local/messaging/GenericMessage.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */