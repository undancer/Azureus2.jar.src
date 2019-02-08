/*     */ package com.aelitis.azureus.core.peermanager.messaging.bittorrent.ltep;
/*     */ 
/*     */ import com.aelitis.azureus.core.peermanager.messaging.Message;
/*     */ import com.aelitis.azureus.core.peermanager.messaging.MessageException;
/*     */ import com.aelitis.azureus.core.peermanager.messaging.MessagingUtil;
/*     */ import com.aelitis.azureus.core.peermanager.messaging.azureus.AZUTMetaData;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import org.gudy.azureus2.core3.util.BDecoder;
/*     */ import org.gudy.azureus2.core3.util.BEncoder;
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
/*     */ public class UTMetaData
/*     */   implements LTMessage, AZUTMetaData
/*     */ {
/*     */   private final byte version;
/*  41 */   private DirectByteBuffer buffer = null;
/*     */   
/*     */   private int msg_type;
/*     */   
/*     */   private int piece;
/*     */   
/*     */   private DirectByteBuffer metadata;
/*     */   
/*     */   private int total_size;
/*     */   
/*     */   public UTMetaData(int _piece, byte _version)
/*     */   {
/*  53 */     this.msg_type = 0;
/*  54 */     this.piece = _piece;
/*  55 */     this.version = _version;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public UTMetaData(int _piece, ByteBuffer _data, int _total_size, byte _version)
/*     */   {
/*  65 */     this.msg_type = (_data == null ? 2 : 1);
/*  66 */     this.piece = _piece;
/*  67 */     this.total_size = _total_size;
/*  68 */     this.version = _version;
/*     */     
/*  70 */     if (_data != null)
/*     */     {
/*  72 */       this.metadata = new DirectByteBuffer(_data);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public UTMetaData(Map map, DirectByteBuffer data, byte _version)
/*     */   {
/*  82 */     if (map != null)
/*     */     {
/*  84 */       this.msg_type = ((Long)map.get("msg_type")).intValue();
/*  85 */       this.piece = ((Long)map.get("piece")).intValue();
/*     */     }
/*     */     
/*  88 */     this.metadata = data;
/*  89 */     this.version = _version;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getID()
/*     */   {
/*  95 */     return "ut_metadata";
/*     */   }
/*     */   
/*     */ 
/*     */   public byte[] getIDBytes()
/*     */   {
/* 101 */     return ID_UT_METADATA_BYTES;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getFeatureID()
/*     */   {
/* 107 */     return "LT1";
/*     */   }
/*     */   
/*     */ 
/*     */   public int getFeatureSubID()
/*     */   {
/* 113 */     return 3;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getType()
/*     */   {
/* 119 */     return 0;
/*     */   }
/*     */   
/*     */ 
/*     */   public byte getVersion()
/*     */   {
/* 125 */     return this.version;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getDescription()
/*     */   {
/* 131 */     return "ut_metadata";
/*     */   }
/*     */   
/*     */ 
/*     */   public int getMessageType()
/*     */   {
/* 137 */     return this.msg_type;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getPiece()
/*     */   {
/* 143 */     return this.piece;
/*     */   }
/*     */   
/*     */ 
/*     */   public DirectByteBuffer getMetadata()
/*     */   {
/* 149 */     return this.metadata;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setMetadata(DirectByteBuffer b)
/*     */   {
/* 156 */     this.metadata = b;
/*     */   }
/*     */   
/*     */ 
/*     */   public DirectByteBuffer[] getData()
/*     */   {
/* 162 */     if (this.buffer == null)
/*     */     {
/* 164 */       Map payload_map = new HashMap();
/*     */       
/* 166 */       payload_map.put("msg_type", new Long(this.msg_type));
/* 167 */       payload_map.put("piece", new Long(this.piece));
/*     */       
/* 169 */       if (this.total_size > 0)
/*     */       {
/* 171 */         payload_map.put("total_size", Integer.valueOf(this.total_size));
/*     */       }
/*     */       
/* 174 */       this.buffer = MessagingUtil.convertPayloadToBencodedByteStream(payload_map, (byte)34);
/*     */     }
/*     */     
/* 177 */     if (this.msg_type == 1)
/*     */     {
/* 179 */       return new DirectByteBuffer[] { this.buffer, this.metadata };
/*     */     }
/*     */     
/*     */ 
/* 183 */     return new DirectByteBuffer[] { this.buffer };
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Message deserialize(DirectByteBuffer data, byte version)
/*     */     throws MessageException
/*     */   {
/* 196 */     int pos = data.position((byte)11);
/*     */     
/* 198 */     byte[] dict_bytes = new byte[Math.min(128, data.remaining((byte)11))];
/*     */     
/* 200 */     data.get((byte)11, dict_bytes);
/*     */     try
/*     */     {
/* 203 */       Map root = BDecoder.decode(dict_bytes);
/*     */       
/* 205 */       data.position((byte)11, pos + BEncoder.encode(root).length);
/*     */       
/* 207 */       return new UTMetaData(root, data, version);
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/* 211 */       e.printStackTrace();
/*     */       
/* 213 */       throw new MessageException("decode failed", e);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void destroy()
/*     */   {
/* 221 */     if (this.buffer != null)
/*     */     {
/* 223 */       this.buffer.returnToPool();
/*     */     }
/*     */     
/* 226 */     if (this.metadata != null)
/*     */     {
/* 228 */       this.metadata.returnToPool();
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/peermanager/messaging/bittorrent/ltep/UTMetaData.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */