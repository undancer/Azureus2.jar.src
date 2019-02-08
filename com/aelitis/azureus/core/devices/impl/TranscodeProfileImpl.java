/*     */ package com.aelitis.azureus.core.devices.impl;
/*     */ 
/*     */ import com.aelitis.azureus.core.devices.TranscodeException;
/*     */ import com.aelitis.azureus.core.devices.TranscodeProfile;
/*     */ import com.aelitis.azureus.core.devices.TranscodeProvider;
/*     */ import java.util.Map;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class TranscodeProfileImpl
/*     */   implements TranscodeProfile
/*     */ {
/*     */   private TranscodeManagerImpl manager;
/*     */   private int pid;
/*     */   private String uid;
/*     */   private String name;
/*     */   private Map<String, Object> properties;
/*     */   
/*     */   protected TranscodeProfileImpl(TranscodeManagerImpl _manager, int _provider_id, String _uid, String _name, Map<String, Object> _properties)
/*     */   {
/*  47 */     this.manager = _manager;
/*  48 */     this.pid = _provider_id;
/*  49 */     this.uid = _uid;
/*  50 */     this.name = _name;
/*  51 */     this.properties = _properties;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getUID()
/*     */   {
/*  57 */     return this.uid;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getName()
/*     */   {
/*  63 */     String displayName = (String)this.properties.get("display-name");
/*  64 */     return displayName == null ? this.name : displayName;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public TranscodeProvider getProvider()
/*     */     throws TranscodeException
/*     */   {
/*  72 */     return this.manager.getProvider(this.pid);
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isStreamable()
/*     */   {
/*  78 */     String res = (String)this.properties.get("streamable");
/*     */     
/*  80 */     return (res != null) && (res.equalsIgnoreCase("yes"));
/*     */   }
/*     */   
/*     */ 
/*     */   public String getFileExtension()
/*     */   {
/*  86 */     return (String)this.properties.get("file-ext");
/*     */   }
/*     */   
/*     */ 
/*     */   public String getDeviceClassification()
/*     */   {
/*  92 */     return (String)this.properties.get("device");
/*     */   }
/*     */   
/*     */ 
/*     */   public String getDescription()
/*     */   {
/*  98 */     String res = (String)this.properties.get("desc");
/*     */     
/* 100 */     return res == null ? "" : res;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getIconURL()
/*     */   {
/* 106 */     return (String)this.properties.get("icon-url");
/*     */   }
/*     */   
/*     */ 
/*     */   public int getIconIndex()
/*     */   {
/* 112 */     Object o = this.properties.get("icon-index");
/*     */     
/* 114 */     if ((o instanceof Number))
/*     */     {
/* 116 */       return ((Number)o).intValue();
/*     */     }
/*     */     
/* 119 */     return 0;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/devices/impl/TranscodeProfileImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */