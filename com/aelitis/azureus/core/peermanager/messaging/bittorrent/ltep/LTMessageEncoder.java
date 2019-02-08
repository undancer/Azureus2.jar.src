/*     */ package com.aelitis.azureus.core.peermanager.messaging.bittorrent.ltep;
/*     */ 
/*     */ import com.aelitis.azureus.core.networkmanager.RawMessage;
/*     */ import com.aelitis.azureus.core.peermanager.messaging.Message;
/*     */ import com.aelitis.azureus.core.peermanager.messaging.MessageStreamEncoder;
/*     */ import com.aelitis.azureus.core.peermanager.messaging.bittorrent.BTLTMessage;
/*     */ import com.aelitis.azureus.core.peermanager.messaging.bittorrent.BTMessageFactory;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.Set;
/*     */ import org.gudy.azureus2.core3.logging.LogEvent;
/*     */ import org.gudy.azureus2.core3.logging.LogIDs;
/*     */ import org.gudy.azureus2.core3.logging.Logger;
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
/*     */ public class LTMessageEncoder
/*     */   implements MessageStreamEncoder
/*     */ {
/*  41 */   protected static final LogIDs LOGID = LogIDs.PEER;
/*     */   private final Object log_object;
/*     */   private HashMap extension_map;
/*     */   
/*     */   public LTMessageEncoder(Object log_object) {
/*  46 */     this.log_object = log_object;
/*  47 */     this.extension_map = null;
/*     */   }
/*     */   
/*     */   public RawMessage[] encodeMessage(Message message) {
/*  51 */     if (!(message instanceof LTMessage)) {
/*  52 */       return new RawMessage[] { BTMessageFactory.createBTRawMessage(message) };
/*     */     }
/*     */     
/*     */ 
/*  56 */     if ((message instanceof LTHandshake)) {
/*  57 */       return new RawMessage[] { BTMessageFactory.createBTRawMessage(new BTLTMessage(message, 0)) };
/*     */     }
/*     */     
/*     */ 
/*  61 */     if (this.extension_map != null) {
/*  62 */       Byte ext_id = (Byte)this.extension_map.get(message.getID());
/*  63 */       if (ext_id != null)
/*     */       {
/*  65 */         return new RawMessage[] { BTMessageFactory.createBTRawMessage(new BTLTMessage(message, ext_id.byteValue())) };
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*  71 */     if (Logger.isEnabled()) {
/*  72 */       Logger.log(new LogEvent(this.log_object, LOGID, "Unable to send LT message of type " + message.getID() + ", not supported by peer - dropping message."));
/*     */     }
/*  74 */     return new RawMessage[0];
/*     */   }
/*     */   
/*     */   public void updateSupportedExtensions(Map map) {
/*     */     try {
/*  79 */       Iterator itr = map.entrySet().iterator();
/*  80 */       while (itr.hasNext()) {
/*  81 */         Map.Entry extension = (Map.Entry)itr.next();
/*     */         
/*  83 */         Object ext_key = extension.getKey();
/*  84 */         String ext_name; if ((ext_key instanceof byte[])) {
/*  85 */           ext_name = new String((byte[])ext_key, "UTF8");
/*     */         } else { String ext_name;
/*  87 */           if ((ext_key instanceof String)) {
/*  88 */             ext_name = (String)ext_key;
/*     */           }
/*     */           else {
/*  91 */             throw new RuntimeException("unexpected type for extension name: " + ext_key.getClass());
/*     */           }
/*     */         }
/*     */         String ext_name;
/*  95 */         Object ext_value_obj = extension.getValue();
/*     */         int ext_value;
/*  97 */         if ((ext_value_obj instanceof Long)) {
/*  98 */           ext_value = ((Long)extension.getValue()).intValue();
/*     */         } else { int ext_value;
/* 100 */           if ((ext_value_obj instanceof byte[])) {
/* 101 */             byte[] ext_value_bytes = (byte[])ext_value_obj;
/* 102 */             int ext_value; if (ext_value_bytes.length == 1) {
/* 103 */               ext_value = ext_value_bytes[0];
/*     */             }
/*     */             else {
/* 106 */               throw new RuntimeException("extension id byte array format length != 1: " + ext_value_bytes.length);
/*     */             }
/*     */           }
/*     */           else {
/* 110 */             throw new RuntimeException("unsupported extension id type: " + ext_value_obj.getClass().getName()); } }
/*     */         int ext_value;
/* 112 */         if (this.extension_map == null) {
/* 113 */           this.extension_map = new HashMap();
/*     */         }
/*     */         
/* 116 */         if (ext_value == 0) this.extension_map.remove(ext_name); else {
/* 117 */           this.extension_map.put(ext_name, new Byte((byte)ext_value));
/*     */         }
/*     */       }
/*     */     } catch (Exception e) {
/* 121 */       if (Logger.isEnabled())
/* 122 */         Logger.log(new LogEvent(this.log_object, LOGID, "Unable to update LT extension list for peer", e));
/*     */     }
/*     */   }
/*     */   
/*     */   public boolean supportsUTPEX() {
/* 127 */     return supportsExtension("ut_pex");
/*     */   }
/*     */   
/*     */   public boolean supportsUTMetaData() {
/* 131 */     return supportsExtension("ut_metadata");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean supportsExtension(String extension_name)
/*     */   {
/* 138 */     if (this.extension_map == null) { return false;
/*     */     }
/* 140 */     Number num = (Number)this.extension_map.get(extension_name);
/*     */     
/* 142 */     return (num != null) && (num.intValue() != 0);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static final int CET_PEX = 1;
/*     */   
/*     */ 
/*     */   private Map<Integer, CustomExtensionHandler> custom_handlers;
/*     */   
/*     */   public void addCustomExtensionHandler(int extension_type, CustomExtensionHandler handler)
/*     */   {
/* 154 */     if (this.custom_handlers == null)
/*     */     {
/* 156 */       this.custom_handlers = new HashMap();
/*     */     }
/*     */     
/* 159 */     this.custom_handlers.put(Integer.valueOf(extension_type), handler);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean hasCustomExtensionHandler(int extension_type)
/*     */   {
/* 166 */     if (this.custom_handlers == null)
/*     */     {
/* 168 */       return false;
/*     */     }
/*     */     
/* 171 */     return this.custom_handlers.containsKey(Integer.valueOf(extension_type));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public Object handleCustomExtension(int extension_type, Object[] args)
/*     */   {
/* 179 */     if (this.custom_handlers == null)
/*     */     {
/* 181 */       return null;
/*     */     }
/*     */     
/* 184 */     CustomExtensionHandler handler = (CustomExtensionHandler)this.custom_handlers.get(Integer.valueOf(extension_type));
/*     */     
/* 186 */     if (handler != null)
/*     */     {
/* 188 */       return handler.handleExtension(args);
/*     */     }
/*     */     
/* 191 */     return null;
/*     */   }
/*     */   
/*     */   public static abstract interface CustomExtensionHandler
/*     */   {
/*     */     public abstract Object handleExtension(Object[] paramArrayOfObject);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/peermanager/messaging/bittorrent/ltep/LTMessageEncoder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */