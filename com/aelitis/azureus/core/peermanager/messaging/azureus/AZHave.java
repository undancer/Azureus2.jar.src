/*     */ package com.aelitis.azureus.core.peermanager.messaging.azureus;
/*     */ 
/*     */ import com.aelitis.azureus.core.peermanager.messaging.Message;
/*     */ import com.aelitis.azureus.core.peermanager.messaging.MessageException;
/*     */ import com.aelitis.azureus.core.peermanager.messaging.MessagingUtil;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
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
/*     */ public class AZHave
/*     */   implements AZMessage
/*     */ {
/*     */   private final byte version;
/*  39 */   private DirectByteBuffer buffer = null;
/*     */   
/*     */ 
/*     */   private final int[] piece_numbers;
/*     */   
/*     */ 
/*     */ 
/*     */   public AZHave(int[] _piece_numbers, byte _version)
/*     */   {
/*  48 */     this.piece_numbers = _piece_numbers;
/*  49 */     this.version = _version;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getID()
/*     */   {
/*  55 */     return "AZ_HAVE";
/*     */   }
/*     */   
/*     */ 
/*     */   public byte[] getIDBytes()
/*     */   {
/*  61 */     return AZMessage.ID_AZ_HAVE_BYTES;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getFeatureID()
/*     */   {
/*  67 */     return "AZ1";
/*     */   }
/*     */   
/*     */ 
/*     */   public int getFeatureSubID()
/*     */   {
/*  73 */     return 4;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getType()
/*     */   {
/*  79 */     return 0;
/*     */   }
/*     */   
/*  82 */   public byte getVersion() { return this.version; }
/*     */   
/*     */ 
/*     */   public String getDescription()
/*     */   {
/*  87 */     StringBuilder str = new StringBuilder(this.piece_numbers.length * 10);
/*     */     
/*  89 */     for (int i = 0; i < this.piece_numbers.length; i++)
/*     */     {
/*  91 */       if (i > 0) {
/*  92 */         str.append(",");
/*     */       }
/*     */       
/*  95 */       str.append(this.piece_numbers[i]);
/*     */     }
/*     */     
/*  98 */     return getID() + " " + str;
/*     */   }
/*     */   
/*     */ 
/*     */   public int[] getPieceNumbers()
/*     */   {
/* 104 */     return this.piece_numbers;
/*     */   }
/*     */   
/*     */ 
/*     */   public DirectByteBuffer[] getData()
/*     */   {
/* 110 */     if (this.buffer == null)
/*     */     {
/* 112 */       Map map = new HashMap();
/*     */       
/* 114 */       List l = new ArrayList(this.piece_numbers.length);
/*     */       
/* 116 */       for (int i = 0; i < this.piece_numbers.length; i++)
/*     */       {
/* 118 */         l.add(new Long(this.piece_numbers[i]));
/*     */       }
/*     */       
/* 121 */       map.put("pieces", l);
/*     */       
/* 123 */       this.buffer = MessagingUtil.convertPayloadToBencodedByteStream(map, (byte)12);
/*     */     }
/*     */     
/* 126 */     return new DirectByteBuffer[] { this.buffer };
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Message deserialize(DirectByteBuffer data, byte version)
/*     */     throws MessageException
/*     */   {
/* 136 */     Map payload = MessagingUtil.convertBencodedByteStreamToPayload(data, 1, getID());
/*     */     
/* 138 */     List l = (List)payload.get("pieces");
/*     */     
/* 140 */     int[] pieces = new int[l.size()];
/*     */     
/* 142 */     for (int i = 0; i < pieces.length; i++)
/*     */     {
/* 144 */       pieces[i] = ((Long)l.get(i)).intValue();
/*     */     }
/*     */     
/* 147 */     AZHave message = new AZHave(pieces, version);
/*     */     
/* 149 */     return message;
/*     */   }
/*     */   
/*     */ 
/*     */   public void destroy()
/*     */   {
/* 155 */     if (this.buffer != null)
/*     */     {
/* 157 */       this.buffer.returnToPool();
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/peermanager/messaging/azureus/AZHave.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */