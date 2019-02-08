/*     */ package com.aelitis.azureus.core.devices.impl;
/*     */ 
/*     */ import com.aelitis.azureus.core.devices.Device;
/*     */ import com.aelitis.azureus.core.devices.DeviceManagerException;
/*     */ import com.aelitis.azureus.core.devices.DeviceMediaRendererTemplate;
/*     */ import com.aelitis.azureus.core.devices.TranscodeProfile;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
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
/*     */ public class DeviceMediaRendererTemplateImpl
/*     */   implements DeviceMediaRendererTemplate
/*     */ {
/*  35 */   private List<TranscodeProfile> profiles = new ArrayList();
/*     */   
/*     */   private final DeviceManagerImpl manager;
/*     */   
/*     */   private final String classification;
/*     */   
/*     */   private final String name;
/*     */   
/*     */   private final String manufacturer;
/*     */   
/*     */   private final boolean auto;
/*     */   
/*     */   protected DeviceMediaRendererTemplateImpl(DeviceManagerImpl _manager, String _classification, boolean _auto)
/*     */   {
/*  49 */     this.manager = _manager;
/*  50 */     this.classification = _classification;
/*  51 */     this.auto = _auto;
/*     */     
/*  53 */     int pos = this.classification.indexOf('.');
/*     */     
/*  55 */     if (pos == -1)
/*     */     {
/*  57 */       this.manufacturer = this.classification;
/*     */     }
/*     */     else
/*     */     {
/*  61 */       this.manufacturer = this.classification.substring(0, pos);
/*     */     }
/*     */     
/*  64 */     pos = this.classification.lastIndexOf('.');
/*     */     
/*  66 */     if (pos == -1)
/*     */     {
/*  68 */       this.name = this.classification;
/*     */     }
/*     */     else
/*     */     {
/*  72 */       this.name = this.classification.substring(pos + 1);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void addProfile(TranscodeProfile profile)
/*     */   {
/*  80 */     this.profiles.add(profile);
/*     */   }
/*     */   
/*     */ 
/*     */   public TranscodeProfile[] getProfiles()
/*     */   {
/*  86 */     return (TranscodeProfile[])this.profiles.toArray(new TranscodeProfile[this.profiles.size()]);
/*     */   }
/*     */   
/*     */ 
/*     */   public int getType()
/*     */   {
/*  92 */     return 3;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getName()
/*     */   {
/*  98 */     return this.name;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getManufacturer()
/*     */   {
/* 104 */     return this.manufacturer;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getClassification()
/*     */   {
/* 110 */     return this.classification;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getShortDescription()
/*     */   {
/* 116 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getRendererSpecies()
/*     */   {
/* 122 */     return 6;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isAuto()
/*     */   {
/* 128 */     return this.auto;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public Device createInstance(String name)
/*     */     throws DeviceManagerException
/*     */   {
/* 137 */     return createInstance(name, null, true);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Device createInstance(String name, String uid, boolean manual)
/*     */     throws DeviceManagerException
/*     */   {
/* 148 */     if (this.auto)
/*     */     {
/* 150 */       throw new DeviceManagerException("Device can't be added manually");
/*     */     }
/*     */     
/* 153 */     Device res = this.manager.createDevice(3, uid, this.classification, name, manual);
/*     */     
/* 155 */     return res;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/devices/impl/DeviceMediaRendererTemplateImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */