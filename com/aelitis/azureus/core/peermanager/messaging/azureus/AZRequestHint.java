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
/*     */ public class AZRequestHint
/*     */   implements AZMessage
/*     */ {
/*     */   private final byte version;
/*  37 */   private DirectByteBuffer buffer = null;
/*     */   
/*     */ 
/*     */   private final int piece_number;
/*     */   
/*     */ 
/*     */   private final int offset;
/*     */   
/*     */   private final int length;
/*     */   
/*     */   private final int life;
/*     */   
/*     */ 
/*     */   public AZRequestHint(int _piece_number, int _offset, int _length, int _life, byte _version)
/*     */   {
/*  52 */     this.piece_number = _piece_number;
/*  53 */     this.offset = _offset;
/*  54 */     this.length = _length;
/*  55 */     this.life = _life;
/*  56 */     this.version = _version;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getID()
/*     */   {
/*  62 */     return "AZ_REQUEST_HINT";
/*     */   }
/*     */   
/*     */ 
/*     */   public byte[] getIDBytes()
/*     */   {
/*  68 */     return AZMessage.ID_AZ_REQUEST_HINT_BYTES;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getFeatureID()
/*     */   {
/*  74 */     return "AZ1";
/*     */   }
/*     */   
/*     */ 
/*     */   public int getFeatureSubID()
/*     */   {
/*  80 */     return 3;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getType()
/*     */   {
/*  86 */     return 0;
/*     */   }
/*     */   
/*  89 */   public byte getVersion() { return this.version; }
/*     */   
/*     */ 
/*     */   public String getDescription()
/*     */   {
/*  94 */     return getID() + " piece #" + this.piece_number + ":" + this.offset + "->" + (this.offset + this.length - 1) + "/" + this.life;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getPieceNumber()
/*     */   {
/* 100 */     return this.piece_number;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getOffset()
/*     */   {
/* 106 */     return this.offset;
/*     */   }
/*     */   
/*     */   public int getLength()
/*     */   {
/* 111 */     return this.length;
/*     */   }
/*     */   
/*     */   public int getLife()
/*     */   {
/* 116 */     return this.life;
/*     */   }
/*     */   
/*     */ 
/*     */   public DirectByteBuffer[] getData()
/*     */   {
/* 122 */     if (this.buffer == null)
/*     */     {
/* 124 */       Map map = new HashMap();
/*     */       
/* 126 */       map.put("piece", new Long(this.piece_number));
/* 127 */       map.put("offset", new Long(this.offset));
/* 128 */       map.put("length", new Long(this.length));
/* 129 */       map.put("life", new Long(this.life));
/*     */       
/* 131 */       this.buffer = MessagingUtil.convertPayloadToBencodedByteStream(map, (byte)12);
/*     */     }
/*     */     
/* 134 */     return new DirectByteBuffer[] { this.buffer };
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Message deserialize(DirectByteBuffer data, byte version)
/*     */     throws MessageException
/*     */   {
/* 144 */     Map payload = MessagingUtil.convertBencodedByteStreamToPayload(data, 1, getID());
/*     */     
/* 146 */     int piece_number = ((Long)payload.get("piece")).intValue();
/* 147 */     int offset = ((Long)payload.get("offset")).intValue();
/* 148 */     int length = ((Long)payload.get("length")).intValue();
/* 149 */     int life = ((Long)payload.get("life")).intValue();
/*     */     
/* 151 */     return new AZRequestHint(piece_number, offset, length, life, version);
/*     */   }
/*     */   
/*     */ 
/*     */   public void destroy()
/*     */   {
/* 157 */     if (this.buffer != null)
/*     */     {
/* 159 */       this.buffer.returnToPool();
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/peermanager/messaging/azureus/AZRequestHint.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */