/*     */ package com.aelitis.azureus.core.messenger.config;
/*     */ 
/*     */ import com.aelitis.azureus.core.AzureusCore;
/*     */ import com.aelitis.azureus.core.AzureusCoreFactory;
/*     */ import com.aelitis.azureus.core.devices.Device;
/*     */ import com.aelitis.azureus.core.devices.DeviceMediaRenderer;
/*     */ import com.aelitis.azureus.core.messenger.PlatformMessage;
/*     */ import com.aelitis.azureus.core.messenger.PlatformMessenger;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.util.Constants;
/*     */ import org.gudy.azureus2.core3.util.SimpleTimer;
/*     */ import org.gudy.azureus2.core3.util.SystemTime;
/*     */ import org.gudy.azureus2.core3.util.TimerEvent;
/*     */ import org.gudy.azureus2.core3.util.TimerEventPerformer;
/*     */ import org.gudy.azureus2.plugins.PluginInterface;
/*     */ import org.gudy.azureus2.plugins.PluginManager;
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
/*     */ public class PlatformDevicesMessenger
/*     */ {
/*     */   public static final String CFG_SEND_QOS = "devices.sendQOS";
/*     */   public static final String LISTENER_ID = "devices";
/*     */   private static final String OP_QOS_TURN_ON = "qos-turn-on";
/*     */   private static final String OP_QOS_FOUND_DEVICE = "qos-found-device";
/*     */   private static final String OP_REPORT_DEVICES = "report-devices";
/*     */   
/*     */   public static void qosTurnOn(boolean withITunes, boolean bugFix)
/*     */   {
/*  53 */     if (!COConfigurationManager.getBooleanParameter("devices.sendQOS", false)) {
/*  54 */       return;
/*     */     }
/*     */     
/*  57 */     PlatformMessage message = new PlatformMessage("AZMSG", "devices", "qos-turn-on", new Object[] { "itunes", Boolean.valueOf(withITunes), "os-name", Constants.OSName + (bugFix ? ":BF" : "") }, 5000L);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  64 */     message.setSendAZID(false);
/*  65 */     PlatformMessenger.queueMessage(message, null);
/*     */   }
/*     */   
/*     */   public static void qosFoundDevice(Device device) {
/*  69 */     if ((device == null) || (!COConfigurationManager.getBooleanParameter("devices.sendQOS", false)))
/*     */     {
/*  71 */       return;
/*     */     }
/*     */     
/*  74 */     if ("ms_wmp.generic".equals(device.getClassification())) {
/*  75 */       return;
/*     */     }
/*     */     
/*  78 */     SimpleTimer.addEvent("qosFoundDevice", SystemTime.getOffsetTime(1000L), new TimerEventPerformer() {
/*     */       public void perform(TimerEvent event) {
/*  80 */         PlatformDevicesMessenger._qosFoundDevice(this.val$device);
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */   private static void _qosFoundDevice(Device device) {
/*  86 */     if ((device == null) || (!COConfigurationManager.getBooleanParameter("devices.sendQOS", false)))
/*     */     {
/*  88 */       return;
/*     */     }
/*     */     
/*  91 */     HashMap<String, Object> map = new HashMap();
/*     */     
/*  93 */     addPluginVersionsToMap(map);
/*     */     
/*  95 */     map.put("device-name", getDeviceName(device));
/*  96 */     map.put("device-type", new Integer(device.getType()));
/*  97 */     if ((device instanceof DeviceMediaRenderer)) {
/*  98 */       DeviceMediaRenderer renderer = (DeviceMediaRenderer)device;
/*  99 */       map.put("renderer-species", Integer.valueOf(renderer.getRendererSpecies()));
/*     */     }
/*     */     
/*     */ 
/* 103 */     PlatformMessage message = new PlatformMessage("AZMSG", "devices", "qos-found-device", map, 5000L);
/*     */     
/* 105 */     message.setSendAZID(false);
/* 106 */     PlatformMessenger.queueMessage(message, null);
/*     */   }
/*     */   
/*     */   private static void addPluginVersionsToMap(Map map) {
/* 110 */     if (AzureusCoreFactory.isCoreRunning()) {
/* 111 */       PluginManager pm = AzureusCoreFactory.getSingleton().getPluginManager();
/*     */       
/* 113 */       PluginInterface pi = pm.getPluginInterfaceByID("vuzexcode");
/* 114 */       if (pi != null) {
/* 115 */         map.put("xcode-plugin-version", pi.getPluginVersion());
/*     */       }
/* 117 */       pi = pm.getPluginInterfaceByID("azitunes");
/* 118 */       if (pi != null) {
/* 119 */         map.put("itunes-plugin-version", pi.getPluginVersion());
/*     */       }
/*     */     }
/* 122 */     map.put("os-name", Constants.OSName);
/*     */   }
/*     */   
/*     */   private static String getDeviceName(Device device) {
/* 126 */     return device.getClassification();
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/messenger/config/PlatformDevicesMessenger.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */