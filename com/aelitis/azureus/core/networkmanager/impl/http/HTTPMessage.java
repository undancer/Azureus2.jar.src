/*     */ package com.aelitis.azureus.core.networkmanager.impl.http;
/*     */ 
/*     */ import com.aelitis.azureus.core.networkmanager.RawMessage;
/*     */ import com.aelitis.azureus.core.networkmanager.impl.RawMessageImpl;
/*     */ import com.aelitis.azureus.core.peermanager.messaging.Message;
/*     */ import com.aelitis.azureus.core.peermanager.messaging.MessageException;
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
/*     */ public class HTTPMessage
/*     */   implements Message
/*     */ {
/*     */   public static final String MSG_ID = "HTTP_DATA";
/*  36 */   private static final byte[] MSG_ID_BYTES = "HTTP_DATA".getBytes();
/*     */   
/*     */ 
/*     */   private static final String MSG_DESC = "HTTP data";
/*     */   
/*     */   private final DirectByteBuffer[] data;
/*     */   
/*     */ 
/*     */   protected HTTPMessage(String stuff)
/*     */   {
/*  46 */     this.data = new DirectByteBuffer[] { new DirectByteBuffer(ByteBuffer.wrap(stuff.getBytes())) };
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected HTTPMessage(byte[] stuff)
/*     */   {
/*  53 */     this.data = new DirectByteBuffer[] { new DirectByteBuffer(ByteBuffer.wrap(stuff)) };
/*     */   }
/*     */   
/*     */ 
/*     */   public String getID()
/*     */   {
/*  59 */     return "HTTP_DATA";
/*     */   }
/*     */   
/*     */ 
/*     */   public byte[] getIDBytes()
/*     */   {
/*  65 */     return MSG_ID_BYTES;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getFeatureID()
/*     */   {
/*  71 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getFeatureSubID()
/*     */   {
/*  77 */     return 0;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getType()
/*     */   {
/*  83 */     return 1;
/*     */   }
/*     */   
/*     */ 
/*     */   public byte getVersion()
/*     */   {
/*  89 */     return 1;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getDescription()
/*     */   {
/*  95 */     return "HTTP data";
/*     */   }
/*     */   
/*     */ 
/*     */   public DirectByteBuffer[] getData()
/*     */   {
/* 101 */     return this.data;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Message deserialize(DirectByteBuffer data, byte version)
/*     */     throws MessageException
/*     */   {
/* 111 */     throw new MessageException("not supported");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected RawMessage encode(Message message)
/*     */   {
/* 118 */     return new RawMessageImpl(message, this.data, 2, true, new Message[0]);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void destroy()
/*     */   {
/* 130 */     this.data[0].returnToPool();
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/networkmanager/impl/http/HTTPMessage.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */