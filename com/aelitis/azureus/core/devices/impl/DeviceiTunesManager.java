/*     */ package com.aelitis.azureus.core.devices.impl;
/*     */ 
/*     */ import com.aelitis.azureus.core.AzureusCore;
/*     */ import com.aelitis.azureus.core.AzureusCoreFactory;
/*     */ import com.aelitis.azureus.core.AzureusCoreRunningListener;
/*     */ import org.gudy.azureus2.plugins.PluginEvent;
/*     */ import org.gudy.azureus2.plugins.PluginEventListener;
/*     */ import org.gudy.azureus2.plugins.PluginInterface;
/*     */ import org.gudy.azureus2.plugins.PluginListener;
/*     */ import org.gudy.azureus2.plugins.PluginManager;
/*     */ import org.gudy.azureus2.plugins.PluginState;
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
/*     */ public class DeviceiTunesManager
/*     */ {
/*     */   private DeviceManagerImpl device_manager;
/*     */   private DeviceiTunes itunes_device;
/*     */   
/*     */   protected DeviceiTunesManager(DeviceManagerImpl _dm)
/*     */   {
/*  44 */     this.device_manager = _dm;
/*     */     
/*  46 */     AzureusCoreFactory.addCoreRunningListener(new AzureusCoreRunningListener() {
/*     */       public void azureusCoreRunning(AzureusCore core) {
/*  48 */         DeviceiTunesManager.this.init(core);
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private void init(AzureusCore azureus_core)
/*     */   {
/*  57 */     final PluginManager pm = azureus_core.getPluginManager();
/*     */     
/*  59 */     final PluginInterface default_pi = pm.getDefaultPluginInterface();
/*     */     
/*  61 */     default_pi.addListener(new PluginListener()
/*     */     {
/*     */ 
/*     */       public void initializationComplete()
/*     */       {
/*     */ 
/*  67 */         default_pi.addEventListener(new PluginEventListener()
/*     */         {
/*     */ 
/*     */ 
/*     */           public void handleEvent(PluginEvent ev)
/*     */           {
/*     */ 
/*  74 */             int type = ev.getType();
/*     */             
/*  76 */             if (type == 8)
/*     */             {
/*  78 */               DeviceiTunesManager.this.pluginAdded((PluginInterface)ev.getValue());
/*     */             }
/*  80 */             if (type == 9)
/*     */             {
/*  82 */               DeviceiTunesManager.this.pluginRemoved((PluginInterface)ev.getValue());
/*     */             }
/*     */             
/*     */           }
/*  86 */         });
/*  87 */         PluginInterface[] plugins = pm.getPlugins();
/*     */         
/*  89 */         for (PluginInterface pi : plugins)
/*     */         {
/*  91 */           if (pi.getPluginState().isOperational())
/*     */           {
/*  93 */             DeviceiTunesManager.this.pluginAdded(pi);
/*     */           }
/*     */         }
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */       public void closedownInitiated() {}
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */       public void closedownComplete() {}
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void pluginAdded(PluginInterface pi)
/*     */   {
/* 114 */     if (pi.getPluginState().isBuiltIn())
/*     */     {
/* 116 */       return;
/*     */     }
/*     */     
/* 119 */     String plugin_id = pi.getPluginID();
/*     */     
/* 121 */     if (plugin_id.equals("azitunes"))
/*     */     {
/*     */       DeviceiTunes new_device;
/*     */       
/* 125 */       synchronized (this)
/*     */       {
/* 127 */         if (this.itunes_device == null) {
/*     */           DeviceiTunes new_device;
/* 129 */           this.itunes_device = (new_device = new DeviceiTunes(this.device_manager, pi));
/*     */         }
/*     */         else
/*     */         {
/* 133 */           return;
/*     */         }
/*     */       }
/*     */       
/* 137 */       this.device_manager.addDevice(new_device, false);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void pluginRemoved(PluginInterface pi)
/*     */   {
/* 145 */     String plugin_id = pi.getPluginID();
/*     */     
/* 147 */     if (plugin_id.equals("azitunes"))
/*     */     {
/*     */       DeviceiTunes existing_device;
/*     */       
/* 151 */       synchronized (this)
/*     */       {
/* 153 */         if (this.itunes_device != null)
/*     */         {
/* 155 */           DeviceiTunes existing_device = this.itunes_device;
/*     */           
/* 157 */           this.itunes_device = null;
/*     */         }
/*     */         else
/*     */         {
/* 161 */           return;
/*     */         }
/*     */       }
/*     */       
/* 165 */       existing_device.remove();
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/devices/impl/DeviceiTunesManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */