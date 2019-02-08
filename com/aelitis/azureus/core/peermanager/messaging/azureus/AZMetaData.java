/*     */ package com.aelitis.azureus.core.peermanager.messaging.azureus;
/*     */ 
/*     */ import com.aelitis.azureus.core.peermanager.messaging.Message;
/*     */ import com.aelitis.azureus.core.peermanager.messaging.MessageException;
/*     */ import com.aelitis.azureus.core.peermanager.messaging.MessagingUtil;
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
/*     */ public class AZMetaData
/*     */   implements AZMessage, AZUTMetaData
/*     */ {
/*     */   private final byte version;
/*  40 */   private DirectByteBuffer buffer = null;
/*     */   
/*     */   private int msg_type;
/*     */   
/*     */   private int piece;
/*     */   
/*     */   private DirectByteBuffer metadata;
/*     */   
/*     */   private int total_size;
/*     */   
/*     */   public AZMetaData(int _piece, byte _version)
/*     */   {
/*  52 */     this.msg_type = 0;
/*  53 */     this.piece = _piece;
/*  54 */     this.version = _version;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public AZMetaData(int _piece, ByteBuffer _data, int _total_size, byte _version)
/*     */   {
/*  64 */     this.msg_type = (_data == null ? 2 : 1);
/*  65 */     this.piece = _piece;
/*  66 */     this.total_size = _total_size;
/*  67 */     this.version = _version;
/*     */     
/*  69 */     if (_data != null)
/*     */     {
/*  71 */       this.metadata = new DirectByteBuffer(_data);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public AZMetaData(Map map, DirectByteBuffer data, byte _version)
/*     */   {
/*  81 */     if (map != null)
/*     */     {
/*  83 */       this.msg_type = ((Long)map.get("msg_type")).intValue();
/*  84 */       this.piece = ((Long)map.get("piece")).intValue();
/*     */     }
/*     */     
/*  87 */     this.metadata = data;
/*  88 */     this.version = _version;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getID()
/*     */   {
/*  94 */     return "AZ_METADATA";
/*     */   }
/*     */   
/*     */ 
/*     */   public byte[] getIDBytes()
/*     */   {
/* 100 */     return ID_AZ_METADATA_BYTES;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getFeatureID()
/*     */   {
/* 106 */     return "AZ1";
/*     */   }
/*     */   
/*     */ 
/*     */   public int getFeatureSubID()
/*     */   {
/* 112 */     return 8;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getType()
/*     */   {
/* 118 */     return 0;
/*     */   }
/*     */   
/*     */ 
/*     */   public byte getVersion()
/*     */   {
/* 124 */     return this.version;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getDescription()
/*     */   {
/* 130 */     return getID() + " piece #" + this.piece + ", mt=" + this.msg_type;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getMessageType()
/*     */   {
/* 136 */     return this.msg_type;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getPiece()
/*     */   {
/* 142 */     return this.piece;
/*     */   }
/*     */   
/*     */ 
/*     */   public DirectByteBuffer getMetadata()
/*     */   {
/* 148 */     return this.metadata;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setMetadata(DirectByteBuffer b)
/*     */   {
/* 155 */     this.metadata = b;
/*     */   }
/*     */   
/*     */ 
/*     */   public DirectByteBuffer[] getData()
/*     */   {
/* 161 */     if (this.buffer == null)
/*     */     {
/* 163 */       Map payload_map = new HashMap();
/*     */       
/* 165 */       payload_map.put("msg_type", new Long(this.msg_type));
/* 166 */       payload_map.put("piece", new Long(this.piece));
/*     */       
/* 168 */       if (this.total_size > 0)
/*     */       {
/* 170 */         payload_map.put("total_size", Integer.valueOf(this.total_size));
/*     */       }
/*     */       
/* 173 */       this.buffer = MessagingUtil.convertPayloadToBencodedByteStream(payload_map, (byte)35);
/*     */     }
/*     */     
/* 176 */     if (this.msg_type == 1)
/*     */     {
/* 178 */       return new DirectByteBuffer[] { this.buffer, this.metadata };
/*     */     }
/*     */     
/*     */ 
/* 182 */     return new DirectByteBuffer[] { this.buffer };
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
/* 195 */     int pos = data.position((byte)11);
/*     */     
/* 197 */     byte[] dict_bytes = new byte[Math.min(128, data.remaining((byte)11))];
/*     */     
/* 199 */     data.get((byte)11, dict_bytes);
/*     */     try
/*     */     {
/* 202 */       Map root = BDecoder.decode(dict_bytes);
/*     */       
/* 204 */       data.position((byte)11, pos + BEncoder.encode(root).length);
/*     */       
/* 206 */       return new AZMetaData(root, data, version);
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/* 210 */       e.printStackTrace();
/*     */       
/* 212 */       throw new MessageException("decode failed", e);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void destroy()
/*     */   {
/* 220 */     if (this.buffer != null)
/*     */     {
/* 222 */       this.buffer.returnToPool();
/*     */     }
/*     */     
/* 225 */     if (this.metadata != null)
/*     */     {
/* 227 */       this.metadata.returnToPool();
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/peermanager/messaging/azureus/AZMetaData.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */