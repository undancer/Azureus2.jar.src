/*     */ package com.aelitis.azureus.core.devices.impl;
/*     */ 
/*     */ import com.aelitis.azureus.core.devices.Device;
/*     */ import com.aelitis.azureus.core.devices.DeviceManagerListener;
/*     */ import com.aelitis.azureus.core.devices.DeviceMediaRenderer;
/*     */ import com.aelitis.azureus.core.devices.DeviceTemplate;
/*     */ import com.aelitis.azureus.core.drivedetector.DriveDetectedInfo;
/*     */ import com.aelitis.azureus.core.drivedetector.DriveDetectedListener;
/*     */ import com.aelitis.azureus.core.drivedetector.DriveDetector;
/*     */ import com.aelitis.azureus.core.drivedetector.DriveDetectorFactory;
/*     */ import com.aelitis.azureus.util.MapUtils;
/*     */ import java.io.File;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.Set;
/*     */ import org.gudy.azureus2.core3.util.AERunnable;
/*     */ import org.gudy.azureus2.core3.util.AsyncDispatcher;
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
/*     */ public class DeviceDriveManager
/*     */   implements DriveDetectedListener
/*     */ {
/*     */   private DeviceManagerImpl manager;
/*  44 */   private Map<String, DeviceMediaRendererManual> device_map = new HashMap();
/*     */   
/*  46 */   private AsyncDispatcher async_dispatcher = new AsyncDispatcher();
/*     */   
/*     */ 
/*     */   private boolean listener_added;
/*     */   
/*     */ 
/*     */   protected DeviceDriveManager(DeviceManagerImpl _manager)
/*     */   {
/*  54 */     this.manager = _manager;
/*     */     
/*  56 */     this.manager.addListener(new DeviceManagerListener()
/*     */     {
/*     */       public void deviceAdded(Device device) {}
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       public void deviceChanged(Device device) {}
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       public void deviceAttentionRequest(Device device) {}
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       public void deviceRemoved(Device device)
/*     */       {
/*  81 */         synchronized (DeviceDriveManager.this.device_map)
/*     */         {
/*  83 */           Iterator<Map.Entry<String, DeviceMediaRendererManual>> it = DeviceDriveManager.this.device_map.entrySet().iterator();
/*     */           
/*  85 */           while (it.hasNext())
/*     */           {
/*  87 */             Map.Entry<String, DeviceMediaRendererManual> entry = (Map.Entry)it.next();
/*     */             
/*  89 */             if (entry.getValue() == device)
/*     */             {
/*  91 */               it.remove();
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */       public void deviceManagerLoaded() {}
/*     */     });
/*     */     
/*     */ 
/* 103 */     if (this.manager.getAutoSearch())
/*     */     {
/* 105 */       this.listener_added = true;
/*     */       
/* 107 */       DriveDetectorFactory.getDeviceDetector().addListener(this);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   protected void search()
/*     */   {
/* 114 */     this.async_dispatcher.dispatch(new AERunnable()
/*     */     {
/*     */ 
/*     */       public void runSupport()
/*     */       {
/*     */ 
/* 120 */         if (DeviceDriveManager.this.listener_added)
/*     */         {
/* 122 */           DriveDetectedInfo[] info = DriveDetectorFactory.getDeviceDetector().getDetectedDriveInfo();
/*     */           
/* 124 */           for (DriveDetectedInfo i : info)
/*     */           {
/* 126 */             DeviceDriveManager.this.driveRemoved(i);
/*     */             
/* 128 */             DeviceDriveManager.this.driveDetected(i);
/*     */           }
/*     */           
/* 131 */           return;
/*     */         }
/*     */         
/*     */ 
/*     */         try
/*     */         {
/* 137 */           DriveDetectorFactory.getDeviceDetector().addListener(DeviceDriveManager.this);
/*     */         }
/*     */         finally
/*     */         {
/* 141 */           DriveDetectorFactory.getDeviceDetector().removeListener(DeviceDriveManager.this);
/*     */         }
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void driveDetected(final DriveDetectedInfo info)
/*     */   {
/* 152 */     this.async_dispatcher.dispatch(new AERunnable()
/*     */     {
/*     */       public void runSupport() {
/* 155 */         Map<String, Object> infoMap = info.getInfoMap();
/*     */         
/* 157 */         boolean isWritableUSB = MapUtils.getMapBoolean(infoMap, "isWritableUSB", false);
/*     */         
/* 159 */         File root = info.getLocation();
/*     */         
/* 161 */         String sProdID = MapUtils.getMapString(infoMap, "ProductID", MapUtils.getMapString(infoMap, "Product Name", "")).trim();
/*     */         
/* 163 */         String sVendor = MapUtils.getMapString(infoMap, "VendorID", MapUtils.getMapString(infoMap, "Vendor Name", "")).trim();
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 172 */         if (((sVendor.equalsIgnoreCase("htc")) && (sProdID.equalsIgnoreCase("android phone"))) || ((sVendor.toLowerCase().contains("motorola")) && (sProdID.length() > 0)) || (sVendor.equalsIgnoreCase("samsung")))
/*     */         {
/*     */ 
/*     */ 
/* 176 */           if ((isWritableUSB) && (sVendor.equalsIgnoreCase("samsung")))
/*     */           {
/*     */ 
/*     */ 
/* 180 */             isWritableUSB = (!sProdID.startsWith("Y")) && (sProdID.matches(".*[A-Z]-.*"));
/*     */           }
/*     */           
/*     */ 
/* 184 */           String name = sProdID.startsWith(sVendor) ? "" : sVendor;
/* 185 */           if (sVendor.length() > 0) {
/* 186 */             name = name + " ";
/*     */           }
/* 188 */           name = name + sProdID;
/*     */           
/* 190 */           String id = "android.";
/* 191 */           id = id + sProdID.replaceAll(" ", ".").toLowerCase();
/* 192 */           if (sVendor.length() > 0) {
/* 193 */             id = id + "." + sVendor.replaceAll(" ", ".").toLowerCase();
/*     */           }
/*     */           
/* 196 */           if (isWritableUSB) {
/* 197 */             DeviceDriveManager.this.addDevice(name, id, root, new File(root, "videos"), true);
/*     */           }
/*     */           else {
/* 200 */             Device existingDevice = DeviceDriveManager.this.getDeviceMediaRendererByClassification(id);
/* 201 */             if (existingDevice != null) {
/* 202 */               existingDevice.remove();
/*     */             }
/*     */           }
/* 205 */           return; }
/* 206 */         if ((isWritableUSB) && (sVendor.toLowerCase().equals("rim"))) {
/* 207 */           String name = sVendor;
/* 208 */           if (name.length() > 0) {
/* 209 */             name = name + " ";
/*     */           }
/* 211 */           name = name + sProdID;
/* 212 */           String id = "";
/* 213 */           id = id + sProdID.replaceAll(" ", ".").toLowerCase();
/* 214 */           if (sVendor.length() > 0) {
/* 215 */             id = id + "." + sVendor.replaceAll(" ", ".").toLowerCase();
/*     */           }
/* 217 */           DeviceMediaRendererManual device = DeviceDriveManager.this.addDevice(name, id, root, new File(root, "videos"), false);
/* 218 */           if (device != null) {
/* 219 */             device.setImageID("bb");
/*     */           }
/* 221 */           return;
/*     */         }
/*     */         
/* 224 */         if (!isWritableUSB) {
/* 225 */           return;
/*     */         }
/*     */         
/* 228 */         if (root.exists())
/*     */         {
/* 230 */           File[] folders = root.listFiles();
/*     */           
/* 232 */           if (folders != null)
/*     */           {
/* 234 */             Set<String> names = new HashSet();
/*     */             
/* 236 */             for (File file : folders)
/*     */             {
/* 238 */               names.add(file.getName().toLowerCase());
/*     */             }
/*     */             
/* 241 */             if ((names.contains("psp")) && (names.contains("video"))) {
/* 242 */               DeviceDriveManager.this.addDevice("PSP", "sony.PSP", root, new File(root, "VIDEO"), false);
/* 243 */               return;
/*     */             }
/*     */           }
/*     */         }
/*     */         
/* 248 */         String pid = MapUtils.getMapString(infoMap, "PID", null);
/* 249 */         String vid = MapUtils.getMapString(infoMap, "VID", null);
/* 250 */         if ((pid != null) && (vid != null)) {
/* 251 */           String name = sProdID.startsWith(sVendor) ? "" : sVendor;
/* 252 */           if (name.length() > 0) {
/* 253 */             name = name + " ";
/*     */           }
/* 255 */           name = name + sProdID;
/*     */           
/* 257 */           String id = "";
/* 258 */           id = id + sProdID.replaceAll(" ", ".").toLowerCase();
/* 259 */           id = id + "." + pid.toLowerCase();
/* 260 */           if (sVendor.length() > 0) {
/* 261 */             id = id + "." + sVendor.replaceAll(" ", ".").toLowerCase();
/*     */           }
/* 263 */           id = id + "." + vid.toLowerCase();
/*     */           
/*     */ 
/* 266 */           if ((id.equals("\"psp\".ms.02d2.sony.054c")) || (id.equals("\"psp\".ms.0381.sony.054c")))
/*     */           {
/* 268 */             if (DeviceDriveManager.this.addDevice("PSP", "sony.PSP", root, new File(root, "VIDEO"), false) != null) {
/* 269 */               return;
/*     */             }
/*     */           }
/*     */           
/* 273 */           DeviceDriveManager.this.addDevice(name, id, root, new File(root, "video"), true);
/*     */         }
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */   protected DeviceMediaRenderer getDeviceMediaRendererByClassification(String target_classification) {
/* 280 */     DeviceImpl[] devices = this.manager.getDevices();
/*     */     
/* 282 */     for (DeviceImpl device : devices)
/*     */     {
/* 284 */       if ((device instanceof DeviceMediaRenderer))
/*     */       {
/* 286 */         DeviceMediaRenderer renderer = (DeviceMediaRenderer)device;
/*     */         
/* 288 */         String classification = renderer.getClassification();
/*     */         
/* 290 */         if (classification.equalsIgnoreCase(target_classification))
/*     */         {
/* 292 */           return renderer;
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 297 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected DeviceMediaRendererManual addDevice(String target_name, String target_classification, File root, File target_directory, boolean generic)
/*     */   {
/* 308 */     DeviceMediaRenderer existingDevice = getDeviceMediaRendererByClassification(target_classification);
/* 309 */     if ((existingDevice instanceof DeviceMediaRendererManual)) {
/* 310 */       mapDevice((DeviceMediaRendererManual)existingDevice, root, target_directory);
/*     */       
/* 312 */       existingDevice.setGenericUSB(generic);
/* 313 */       return (DeviceMediaRendererManual)existingDevice;
/*     */     }
/*     */     
/* 316 */     DeviceTemplate[] templates = this.manager.getDeviceTemplates(3);
/*     */     
/* 318 */     DeviceMediaRendererManual renderer = null;
/*     */     
/* 320 */     for (DeviceTemplate template : templates)
/*     */     {
/* 322 */       if (template.getClassification().equalsIgnoreCase(target_classification)) {
/*     */         try
/*     */         {
/* 325 */           renderer = (DeviceMediaRendererManual)template.createInstance(target_name);
/*     */ 
/*     */         }
/*     */         catch (Throwable e)
/*     */         {
/*     */ 
/* 331 */           log("Failed to add device", e);
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 336 */     if (renderer == null)
/*     */     {
/*     */       try
/*     */       {
/*     */ 
/* 341 */         renderer = (DeviceMediaRendererManual)this.manager.createDevice(3, null, target_classification, target_name, true);
/*     */       }
/*     */       catch (Throwable e)
/*     */       {
/* 345 */         log("Failed to add device", e);
/*     */       }
/*     */     }
/*     */     
/* 349 */     if (renderer != null) {
/*     */       try
/*     */       {
/* 352 */         renderer.setAutoCopyToFolder(true);
/*     */         
/* 354 */         renderer.setGenericUSB(generic);
/*     */         
/* 356 */         mapDevice(renderer, root, target_directory);
/*     */         
/* 358 */         return renderer;
/*     */       }
/*     */       catch (Throwable e)
/*     */       {
/* 362 */         log("Failed to add device", e);
/*     */       }
/*     */     }
/* 365 */     return renderer;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void driveRemoved(final DriveDetectedInfo info)
/*     */   {
/* 372 */     this.async_dispatcher.dispatch(new AERunnable()
/*     */     {
/*     */ 
/*     */       public void runSupport()
/*     */       {
/*     */ 
/* 378 */         DeviceDriveManager.this.unMapDevice(info.getLocation());
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void mapDevice(DeviceMediaRendererManual renderer, File root, File copy_to)
/*     */   {
/*     */     DeviceMediaRendererManual existing;
/*     */     
/*     */ 
/* 391 */     synchronized (this.device_map)
/*     */     {
/* 393 */       existing = (DeviceMediaRendererManual)this.device_map.put(root.getAbsolutePath(), renderer);
/*     */     }
/*     */     
/* 396 */     if ((existing != null) && (existing != renderer))
/*     */     {
/* 398 */       log("Unmapped " + existing.getName() + " from " + root);
/*     */       
/* 400 */       existing.setCopyToFolder(null);
/*     */     }
/*     */     
/* 403 */     log("Mapped " + renderer.getName() + " to " + root);
/*     */     
/* 405 */     renderer.setCopyToFolder(copy_to);
/*     */     
/* 407 */     renderer.setLivenessDetectable(true);
/*     */     
/* 409 */     renderer.alive();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void unMapDevice(File root)
/*     */   {
/*     */     DeviceMediaRendererManual existing;
/*     */     
/* 418 */     synchronized (this.device_map)
/*     */     {
/* 420 */       existing = (DeviceMediaRendererManual)this.device_map.remove(root.getAbsolutePath());
/*     */     }
/*     */     
/* 423 */     if (existing != null)
/*     */     {
/* 425 */       log("Unmapped " + existing.getName() + " from " + root);
/*     */       
/* 427 */       existing.setCopyToFolder(null);
/*     */       
/* 429 */       existing.dead();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void log(String str)
/*     */   {
/* 437 */     this.manager.log("DriveMan: " + str);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void log(String str, Throwable e)
/*     */   {
/* 445 */     this.manager.log("DriveMan: " + str, e);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/devices/impl/DeviceDriveManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */