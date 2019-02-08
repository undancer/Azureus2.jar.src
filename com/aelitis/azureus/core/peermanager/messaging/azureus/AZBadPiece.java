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
/*     */ public class AZBadPiece
/*     */   implements AZMessage
/*     */ {
/*     */   private final byte version;
/*  37 */   private DirectByteBuffer buffer = null;
/*     */   
/*     */ 
/*     */   private final int piece_number;
/*     */   
/*     */ 
/*     */ 
/*     */   public AZBadPiece(int _piece_number, byte _version)
/*     */   {
/*  46 */     this.piece_number = _piece_number;
/*  47 */     this.version = _version;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getID()
/*     */   {
/*  53 */     return "AZ_BAD_PIECE";
/*     */   }
/*     */   
/*     */ 
/*     */   public byte[] getIDBytes()
/*     */   {
/*  59 */     return AZMessage.ID_AZ_BAD_PIECE_BYTES;
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
/*  71 */     return 5;
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
/*     */ 
/*     */   public String getDescription()
/*     */   {
/*  86 */     return getID() + " " + this.piece_number;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getPieceNumber()
/*     */   {
/*  92 */     return this.piece_number;
/*     */   }
/*     */   
/*     */ 
/*     */   public DirectByteBuffer[] getData()
/*     */   {
/*  98 */     if (this.buffer == null)
/*     */     {
/* 100 */       Map map = new HashMap();
/*     */       
/* 102 */       map.put("piece", new Long(this.piece_number));
/*     */       
/* 104 */       this.buffer = MessagingUtil.convertPayloadToBencodedByteStream(map, (byte)12);
/*     */     }
/*     */     
/* 107 */     return new DirectByteBuffer[] { this.buffer };
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Message deserialize(DirectByteBuffer data, byte version)
/*     */     throws MessageException
/*     */   {
/* 117 */     Map payload = MessagingUtil.convertBencodedByteStreamToPayload(data, 1, getID());
/*     */     
/* 119 */     int piece_number = ((Long)payload.get("piece")).intValue();
/*     */     
/*     */ 
/* 122 */     AZBadPiece message = new AZBadPiece(piece_number, version);
/*     */     
/* 124 */     return message;
/*     */   }
/*     */   
/*     */ 
/*     */   public void destroy()
/*     */   {
/* 130 */     if (this.buffer != null)
/*     */     {
/* 132 */       this.buffer.returnToPool();
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/peermanager/messaging/azureus/AZBadPiece.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */