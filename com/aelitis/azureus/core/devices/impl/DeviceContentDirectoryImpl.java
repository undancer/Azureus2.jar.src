/*     */ package com.aelitis.azureus.core.devices.impl;
/*     */ 
/*     */ import com.aelitis.azureus.core.devices.DeviceContentDirectory;
/*     */ import com.aelitis.net.upnp.UPnPDevice;
/*     */ import com.aelitis.net.upnp.UPnPService;
/*     */ import java.io.IOException;
/*     */ import java.net.URL;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import org.gudy.azureus2.core3.util.Debug;
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
/*     */ public class DeviceContentDirectoryImpl
/*     */   extends DeviceUPnPImpl
/*     */   implements DeviceContentDirectory
/*     */ {
/*     */   private UPnPService upnp_service;
/*     */   
/*     */   protected DeviceContentDirectoryImpl(DeviceManagerImpl _manager, UPnPDevice _device, UPnPService _service)
/*     */   {
/*  47 */     super(_manager, _device, 2);
/*     */     
/*  49 */     this.upnp_service = _service;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected DeviceContentDirectoryImpl(DeviceManagerImpl _manager, Map _map)
/*     */     throws IOException
/*     */   {
/*  59 */     super(_manager, _map);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected boolean updateFrom(DeviceImpl _other, boolean _is_alive)
/*     */   {
/*  67 */     if (!super.updateFrom(_other, _is_alive))
/*     */     {
/*  69 */       return false;
/*     */     }
/*     */     
/*  72 */     if (!(_other instanceof DeviceContentDirectoryImpl))
/*     */     {
/*  74 */       Debug.out("Inconsistent");
/*     */       
/*  76 */       return false;
/*     */     }
/*     */     
/*  79 */     DeviceContentDirectoryImpl other = (DeviceContentDirectoryImpl)_other;
/*     */     
/*  81 */     if (other.upnp_service != null)
/*     */     {
/*  83 */       this.upnp_service = other.upnp_service;
/*     */     }
/*     */     
/*  86 */     return true;
/*     */   }
/*     */   
/*     */ 
/*     */   public List<URL> getControlURLs()
/*     */   {
/*  92 */     if (this.upnp_service != null) {
/*     */       try
/*     */       {
/*  95 */         return this.upnp_service.getControlURLs();
/*     */       }
/*     */       catch (Throwable e)
/*     */       {
/*  99 */         Debug.out(e);
/*     */       }
/*     */     }
/*     */     
/* 103 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setPreferredControlURL(URL url)
/*     */   {
/* 110 */     if (this.upnp_service != null)
/*     */     {
/* 112 */       this.upnp_service.setPreferredControlURL(url);
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/devices/impl/DeviceContentDirectoryImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */