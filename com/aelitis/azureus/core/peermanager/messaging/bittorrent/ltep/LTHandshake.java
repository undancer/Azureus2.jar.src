/*     */ package com.aelitis.azureus.core.peermanager.messaging.bittorrent.ltep;
/*     */ 
/*     */ import com.aelitis.azureus.core.peermanager.messaging.Message;
/*     */ import com.aelitis.azureus.core.peermanager.messaging.MessageException;
/*     */ import com.aelitis.azureus.core.peermanager.messaging.MessagingUtil;
/*     */ import java.io.IOException;
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.net.InetAddress;
/*     */ import java.net.UnknownHostException;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import org.gudy.azureus2.core3.util.BEncoder;
/*     */ import org.gudy.azureus2.core3.util.Debug;
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
/*     */ public class LTHandshake
/*     */   implements LTMessage
/*     */ {
/*     */   private Map data_dict;
/*     */   private byte[] bencoded_data;
/*     */   private String bencoded_string;
/*     */   private String description;
/*     */   private final byte version;
/*     */   private DirectByteBuffer[] buffer_array;
/*     */   
/*     */   public LTHandshake(Map data_dict, byte version)
/*     */   {
/*  42 */     this.data_dict = (data_dict == null ? Collections.EMPTY_MAP : data_dict);
/*  43 */     this.version = version;
/*     */   }
/*     */   
/*     */   public Message deserialize(DirectByteBuffer data, byte version) throws MessageException {
/*  47 */     if (data == null) {
/*  48 */       throw new MessageException("[" + getID() + "] decode error: data == null");
/*     */     }
/*  50 */     if (data.remaining((byte)11) < 1) {
/*  51 */       throw new MessageException("[" + getID() + "] decode error: less than 1 byte in payload");
/*     */     }
/*     */     
/*     */ 
/*  55 */     Map res_data_dict = MessagingUtil.convertBencodedByteStreamToPayload(data, 1, getID());
/*     */     
/*  57 */     LTHandshake result = new LTHandshake(res_data_dict, this.version);
/*  58 */     return result;
/*     */   }
/*     */   
/*     */   public DirectByteBuffer[] getData() {
/*  62 */     if (this.buffer_array == null) {
/*  63 */       this.buffer_array = new DirectByteBuffer[1];
/*  64 */       DirectByteBuffer buffer = DirectByteBufferPool.getBuffer((byte)28, getBencodedData().length);
/*  65 */       this.buffer_array[0] = buffer;
/*     */       
/*  67 */       buffer.put((byte)11, getBencodedData());
/*  68 */       buffer.flip((byte)11);
/*     */     }
/*  70 */     return this.buffer_array;
/*     */   }
/*     */   
/*     */   public void destroy() {
/*  74 */     this.data_dict = null;
/*  75 */     this.bencoded_data = null;
/*  76 */     this.description = null;
/*  77 */     if (this.buffer_array != null) {
/*  78 */       this.buffer_array[0].returnToPool();
/*     */     }
/*  80 */     this.buffer_array = null;
/*     */   }
/*     */   
/*     */   public String getDescription() {
/*  84 */     if (this.description == null) {
/*  85 */       this.description = ("lt_handshake".toUpperCase() + ": " + getBencodedString());
/*     */     }
/*  87 */     return this.description;
/*     */   }
/*     */   
/*     */   public String getBencodedString() {
/*  91 */     if (this.bencoded_string == null) {
/*     */       try {
/*  93 */         this.bencoded_string = new String(getBencodedData(), "ISO-8859-1");
/*     */       }
/*     */       catch (UnsupportedEncodingException uee) {
/*  96 */         this.bencoded_string = "";
/*  97 */         Debug.printStackTrace(uee);
/*     */       }
/*     */     }
/* 100 */     return this.bencoded_string;
/*     */   }
/*     */   
/*     */   public byte[] getBencodedData() {
/* 104 */     if (this.bencoded_data == null) {
/* 105 */       try { this.bencoded_data = BEncoder.encode(this.data_dict);
/*     */       } catch (IOException ioe) {
/* 107 */         this.bencoded_data = new byte[0];
/* 108 */         Debug.printStackTrace(ioe);
/*     */       }
/*     */     }
/* 111 */     return this.bencoded_data;
/*     */   }
/*     */   
/*     */   public Map getDataMap() {
/* 115 */     return this.data_dict;
/*     */   }
/*     */   
/*     */   public String getClientName() {
/* 119 */     byte[] client_name = (byte[])this.data_dict.get("v");
/* 120 */     if (client_name == null) return null;
/* 121 */     try { return new String(client_name, "UTF8"); } catch (IOException ioe) {}
/* 122 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean isUploadOnly()
/*     */   {
/* 130 */     Object ulOnly = this.data_dict.get("upload_only");
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
/* 148 */     if (ulOnly == null)
/* 149 */       return false;
/* 150 */     if ((ulOnly instanceof Number)) {
/* 151 */       Number n_ulOnly = (Number)ulOnly;
/* 152 */       return n_ulOnly.longValue() > 0L;
/*     */     }
/*     */     
/*     */ 
/* 156 */     if ((ulOnly instanceof byte[]))
/*     */     {
/* 158 */       String str_val = new String((byte[])ulOnly);
/*     */       try
/*     */       {
/* 161 */         int i = Integer.parseInt(str_val);
/*     */         
/* 163 */         return i > 0;
/*     */       }
/*     */       catch (Throwable e) {}
/*     */     }
/*     */     
/*     */ 
/*     */     String debug;
/*     */     
/* 171 */     if ((ulOnly instanceof byte[]))
/*     */     {
/* 173 */       byte[] bytes = (byte[])ulOnly;
/*     */       
/* 175 */       String debug = new String(bytes) + "/";
/*     */       
/* 177 */       for (int i = 0; i < bytes.length; i++)
/*     */       {
/* 179 */         debug = debug + (i == 0 ? "" : ",") + (bytes[i] & 0xFF);
/*     */       }
/*     */     }
/*     */     else
/*     */     {
/* 184 */       debug = String.valueOf(ulOnly);
/*     */     }
/*     */     
/* 187 */     Debug.out("Invalid entry for 'upload_only' - " + debug + ", map=" + this.data_dict);
/*     */     
/* 189 */     return false;
/*     */   }
/*     */   
/*     */   public InetAddress getIPv6()
/*     */   {
/* 194 */     byte[] addr = (byte[])this.data_dict.get("ipv6");
/* 195 */     if ((addr != null) && (addr.length == 16))
/*     */     {
/*     */       try
/*     */       {
/* 199 */         return InetAddress.getByAddress(addr);
/*     */       }
/*     */       catch (UnknownHostException e)
/*     */       {
/* 203 */         e.printStackTrace();
/*     */       }
/*     */     }
/*     */     
/* 207 */     return null;
/*     */   }
/*     */   
/*     */   public int getTCPListeningPort()
/*     */   {
/* 212 */     Long port = (Long)this.data_dict.get("p");
/* 213 */     if (port == null)
/* 214 */       return 0;
/* 215 */     int val = port.intValue();
/* 216 */     if ((val <= 65535) && (val > 0))
/* 217 */       return val;
/* 218 */     return 0;
/*     */   }
/*     */   
/*     */   public Boolean isCryptoRequested()
/*     */   {
/* 223 */     Long crypto = (Long)this.data_dict.get("e");
/* 224 */     if (crypto == null)
/* 225 */       return null;
/* 226 */     return Boolean.valueOf(crypto.longValue() == 1L);
/*     */   }
/*     */   
/*     */   public Map getExtensionMapping() {
/* 230 */     Map result = (Map)this.data_dict.get("m");
/* 231 */     return result == null ? Collections.EMPTY_MAP : result;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getMetadataSize()
/*     */   {
/* 237 */     Long l = (Long)this.data_dict.get("metadata_size");
/*     */     
/* 239 */     if (l != null)
/*     */     {
/* 241 */       return l.intValue();
/*     */     }
/*     */     
/* 244 */     return 0;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void addDefaultExtensionMappings(boolean enable_pex, boolean enable_md, boolean enable_uo)
/*     */   {
/* 253 */     if ((enable_pex) || (enable_md) || (enable_uo)) {
/* 254 */       Map ext = (Map)this.data_dict.get("m");
/*     */       
/* 256 */       if (ext == null) {
/* 257 */         ext = new HashMap();
/* 258 */         this.data_dict.put("m", ext);
/*     */       }
/*     */       
/* 261 */       if (enable_pex)
/*     */       {
/* 263 */         ext.put("ut_pex", new Long(1L));
/*     */       }
/*     */       
/* 266 */       if (enable_md)
/*     */       {
/* 268 */         ext.put("ut_metadata", new Long(3L));
/*     */       }
/*     */       
/* 271 */       if (enable_uo)
/*     */       {
/* 273 */         ext.put("upload_only", new Long(4L));
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void addOptionalExtensionMapping(String id, long subid)
/*     */   {
/* 283 */     Map ext = (Map)this.data_dict.get("m");
/*     */     
/* 285 */     if (ext == null) {
/* 286 */       ext = new HashMap();
/* 287 */       this.data_dict.put("m", ext);
/*     */     }
/*     */     
/* 290 */     ext.put(id, new Long(subid));
/*     */   }
/*     */   
/* 293 */   public String getFeatureID() { return "LT1"; }
/* 294 */   public int getFeatureSubID() { return 0; }
/* 295 */   public String getID() { return "lt_handshake"; }
/* 296 */   public byte[] getIDBytes() { return ID_LT_HANDSHAKE_BYTES; }
/* 297 */   public int getType() { return 0; }
/* 298 */   public byte getVersion() { return this.version; }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/peermanager/messaging/bittorrent/ltep/LTHandshake.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */