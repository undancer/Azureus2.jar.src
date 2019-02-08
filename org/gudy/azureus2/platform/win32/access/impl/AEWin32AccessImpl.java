/*     */ package org.gudy.azureus2.platform.win32.access.impl;
/*     */ 
/*     */ import com.aelitis.azureus.util.MapUtils;
/*     */ import java.io.File;
/*     */ import java.net.InetAddress;
/*     */ import java.net.UnknownHostException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Random;
/*     */ import java.util.StringTokenizer;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.util.Constants;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.platform.PlatformManagerPingCallback;
/*     */ import org.gudy.azureus2.platform.win32.access.AEWin32Access;
/*     */ import org.gudy.azureus2.platform.win32.access.AEWin32AccessException;
/*     */ import org.gudy.azureus2.platform.win32.access.AEWin32AccessListener;
/*     */ import org.gudy.azureus2.platform.win32.access.AEWin32Manager;
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
/*     */ public class AEWin32AccessImpl
/*     */   implements AEWin32Access, AEWin32AccessCallback
/*     */ {
/*     */   protected static AEWin32AccessImpl singleton;
/*     */   private boolean fully_initialise;
/*     */   
/*     */   public static synchronized AEWin32Access getSingleton(boolean fully_initialise)
/*     */   {
/*  52 */     if (singleton == null)
/*     */     {
/*  54 */       singleton = new AEWin32AccessImpl(fully_initialise);
/*     */     }
/*     */     
/*  57 */     return singleton;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*  62 */   private int trace_id_next = new Random().nextInt();
/*     */   
/*  64 */   private List listeners = new ArrayList();
/*     */   
/*     */ 
/*     */ 
/*     */   protected AEWin32AccessImpl(boolean _fully_initialise)
/*     */   {
/*  70 */     this.fully_initialise = _fully_initialise;
/*     */     
/*  72 */     if (isEnabled())
/*     */     {
/*  74 */       AEWin32AccessInterface.load(this, this.fully_initialise);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isEnabled()
/*     */   {
/*  81 */     return AEWin32AccessInterface.isEnabled(this.fully_initialise);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public long windowsMessage(int msg, int param1, long param2)
/*     */   {
/*  90 */     int type = -1;
/*     */     
/*  92 */     if (msg == 22)
/*     */     {
/*  94 */       type = 1;
/*     */     }
/*  96 */     else if (msg == 536)
/*     */     {
/*  98 */       if (param1 == 0)
/*     */       {
/* 100 */         type = 2;
/*     */       }
/* 102 */       else if (param1 == 7)
/*     */       {
/* 104 */         type = 3;
/*     */       }
/*     */     }
/*     */     
/* 108 */     int result = -1;
/*     */     
/* 110 */     if (type != -1)
/*     */     {
/* 112 */       for (int i = 0; i < this.listeners.size(); i++) {
/*     */         try
/*     */         {
/* 115 */           int temp = ((AEWin32AccessListener)this.listeners.get(i)).eventOccurred(type);
/*     */           
/* 117 */           if (temp != result)
/*     */           {
/* 119 */             if (result != -1)
/*     */             {
/* 121 */               Debug.out("Incompatible results received: " + result + "/" + temp);
/*     */             }
/*     */             
/* 124 */             result = temp;
/*     */           }
/*     */         }
/*     */         catch (Throwable e) {
/* 128 */           e.printStackTrace();
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 133 */     if (result == 1)
/*     */     {
/* 135 */       if ((param2 & 1L) != 0L)
/*     */       {
/* 137 */         return 1112363332L;
/*     */       }
/*     */       
/*     */ 
/* 141 */       Debug.out("Ignoring suspend deny request as not permitted");
/*     */     }
/*     */     
/*     */ 
/* 145 */     return result;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public long generalMessage(String str)
/*     */   {
/* 152 */     return 0L;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getVersion()
/*     */   {
/* 158 */     return AEWin32AccessInterface.getVersion();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String readStringValue(int type, String subkey, String value_name)
/*     */     throws AEWin32AccessException
/*     */   {
/* 169 */     return AEWin32AccessInterface.readStringValue(type, subkey, value_name);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void writeStringValue(int type, String subkey, String value_name, String value_value)
/*     */     throws AEWin32AccessException
/*     */   {
/* 181 */     AEWin32AccessInterface.writeStringValue(type, subkey, value_name, value_value);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int readWordValue(int type, String subkey, String value_name)
/*     */     throws AEWin32AccessException
/*     */   {
/* 193 */     return AEWin32AccessInterface.readWordValue(type, subkey, value_name);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void writeWordValue(int type, String subkey, String value_name, int value_value)
/*     */     throws AEWin32AccessException
/*     */   {
/* 205 */     AEWin32AccessInterface.writeWordValue(type, subkey, value_name, value_value);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void deleteKey(int type, String subkey)
/*     */     throws AEWin32AccessException
/*     */   {
/* 216 */     deleteKey(type, subkey, false);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void deleteKey(int type, String subkey, boolean recursive)
/*     */     throws AEWin32AccessException
/*     */   {
/* 227 */     AEWin32AccessInterface.deleteKey(type, subkey, recursive);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void deleteValue(int type, String subkey, String value_name)
/*     */     throws AEWin32AccessException
/*     */   {
/* 238 */     AEWin32AccessInterface.deleteValue(type, subkey, value_name);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public String getUserAppData()
/*     */     throws AEWin32AccessException
/*     */   {
/* 246 */     String app_data_key = "software\\microsoft\\windows\\currentversion\\explorer\\shell folders";
/* 247 */     String app_data_name = "appdata";
/*     */     
/* 249 */     return readStringValue(4, app_data_key, app_data_name);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getCommonAppData()
/*     */     throws AEWin32AccessException
/*     */   {
/* 261 */     String app_data_key = "software\\microsoft\\windows\\currentversion\\explorer\\shell folders";
/* 262 */     String app_data_name = "Common AppData";
/*     */     
/* 264 */     return readStringValue(3, app_data_key, app_data_name);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getLocalAppData()
/*     */     throws AEWin32AccessException
/*     */   {
/* 276 */     String app_data_key = "software\\microsoft\\windows\\currentversion\\explorer\\shell folders";
/* 277 */     String app_data_name = "Local AppData";
/*     */     
/* 279 */     return readStringValue(4, app_data_key, app_data_name);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getUserDocumentsDir()
/*     */     throws AEWin32AccessException
/*     */   {
/* 291 */     String app_data_key = "software\\microsoft\\windows\\currentversion\\explorer\\shell folders";
/* 292 */     String app_data_name = "personal";
/*     */     
/* 294 */     return readStringValue(4, app_data_key, app_data_name);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getUserMusicDir()
/*     */     throws AEWin32AccessException
/*     */   {
/* 306 */     String app_data_key = "software\\microsoft\\windows\\currentversion\\explorer\\shell folders";
/* 307 */     String app_data_name = "my music";
/*     */     try
/*     */     {
/* 310 */       return readStringValue(4, app_data_key, app_data_name);
/*     */ 
/*     */     }
/*     */     catch (AEWin32AccessException e)
/*     */     {
/*     */ 
/* 316 */       String s = getUserDocumentsDir();
/* 317 */       if (s != null) {}
/* 318 */       return s + "\\My Music";
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getUserVideoDir()
/*     */     throws AEWin32AccessException
/*     */   {
/* 331 */     String app_data_key = "software\\microsoft\\windows\\currentversion\\explorer\\shell folders";
/* 332 */     String app_data_name = "my video";
/*     */     try
/*     */     {
/* 335 */       return readStringValue(4, app_data_key, app_data_name);
/*     */ 
/*     */     }
/*     */     catch (AEWin32AccessException e)
/*     */     {
/*     */ 
/* 341 */       String s = getUserDocumentsDir();
/* 342 */       if (s != null) {}
/* 343 */       return s + "\\My Video";
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getProgramFilesDir()
/*     */     throws AEWin32AccessException
/*     */   {
/* 355 */     String app_data_key = "software\\microsoft\\windows\\currentversion";
/* 356 */     String app_data_name = "ProgramFilesDir";
/*     */     
/* 358 */     return readStringValue(3, app_data_key, app_data_name);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getApplicationInstallDir(String app_name)
/*     */     throws AEWin32AccessException
/*     */   {
/* 371 */     String res = "";
/*     */     try
/*     */     {
/* 374 */       res = readStringValue(4, "software\\" + app_name, null);
/*     */ 
/*     */ 
/*     */     }
/*     */     catch (AEWin32AccessException e)
/*     */     {
/*     */ 
/* 381 */       res = readStringValue(3, "software\\" + app_name, null);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 388 */     return res;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void createProcess(String command_line, boolean inherit_handles)
/*     */     throws AEWin32AccessException
/*     */   {
/* 398 */     AEWin32AccessInterface.createProcess(command_line, inherit_handles);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void moveToRecycleBin(String file_name)
/*     */     throws AEWin32AccessException
/*     */   {
/* 407 */     AEWin32AccessInterface.moveToRecycleBin(file_name);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void copyFilePermissions(String from_file_name, String to_file_name)
/*     */     throws AEWin32AccessException
/*     */   {
/* 417 */     AEWin32AccessInterface.copyPermission(from_file_name, to_file_name);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean testNativeAvailability(String name)
/*     */     throws AEWin32AccessException
/*     */   {
/* 426 */     return AEWin32AccessInterface.testNativeAvailability(name);
/*     */   }
/*     */   
/*     */   public int shellExecute(String operation, String file, String parameters, String directory, int SW_const) throws AEWin32AccessException
/*     */   {
/* 431 */     return AEWin32AccessInterface.shellExecute(operation, file, parameters, directory, SW_const);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int shellExecuteAndWait(String file, String params)
/*     */     throws AEWin32AccessException
/*     */   {
/* 442 */     return AEWin32AccessInterface.shellExecuteAndWait(file, params);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void traceRoute(InetAddress source_address, InetAddress target_address, PlatformManagerPingCallback callback)
/*     */     throws AEWin32AccessException
/*     */   {
/* 453 */     traceRoute(source_address, target_address, false, callback);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void ping(InetAddress source_address, InetAddress target_address, PlatformManagerPingCallback callback)
/*     */     throws AEWin32AccessException
/*     */   {
/* 464 */     if (Constants.compareVersions(getVersion(), "1.15") < 0)
/*     */     {
/* 466 */       throw new AEWin32AccessException("Sorry, ping is broken in versions < 1.15");
/*     */     }
/*     */     
/* 469 */     traceRoute(source_address, target_address, true, callback);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void traceRoute(InetAddress source_address, InetAddress target_address, boolean ping_mode, PlatformManagerPingCallback callback)
/*     */     throws AEWin32AccessException
/*     */   {
/*     */     int trace_id;
/*     */     
/*     */ 
/*     */ 
/* 483 */     synchronized (this)
/*     */     {
/* 485 */       trace_id = this.trace_id_next++;
/*     */     }
/*     */     
/* 488 */     AEWin32AccessCallback cb = new traceRouteCallback(ping_mode, callback);
/*     */     
/* 490 */     AEWin32AccessInterface.traceRoute(trace_id, addressToInt(source_address), addressToInt(target_address), ping_mode ? 1 : 0, cb);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private int addressToInt(InetAddress address)
/*     */   {
/* 502 */     byte[] bytes = address.getAddress();
/*     */     
/* 504 */     int resp = bytes[0] << 24 & 0xFF000000 | bytes[1] << 16 & 0xFF0000 | bytes[2] << 8 & 0xFF00 | bytes[3] & 0xFF;
/*     */     
/* 506 */     return resp;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private InetAddress intToAddress(int address)
/*     */   {
/* 513 */     byte[] bytes = { (byte)(address >> 24), (byte)(address >> 16), (byte)(address >> 8), (byte)address };
/*     */     try
/*     */     {
/* 516 */       return InetAddress.getByAddress(bytes);
/*     */     }
/*     */     catch (UnknownHostException e) {}
/*     */     
/*     */ 
/*     */ 
/* 522 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void addListener(AEWin32AccessListener listener)
/*     */   {
/* 530 */     this.listeners.add(listener);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void removeListener(AEWin32AccessListener listener)
/*     */   {
/* 537 */     this.listeners.remove(listener);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected class traceRouteCallback
/*     */     implements AEWin32AccessCallback
/*     */   {
/*     */     private boolean ping_mode;
/*     */     
/*     */     private PlatformManagerPingCallback cb;
/*     */     
/*     */ 
/*     */     protected traceRouteCallback(boolean _ping_mode, PlatformManagerPingCallback _cb)
/*     */     {
/* 552 */       this.ping_mode = _ping_mode;
/* 553 */       this.cb = _cb;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     public long windowsMessage(int msg, int param1, long param2)
/*     */     {
/* 562 */       return 0L;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     public long generalMessage(String msg)
/*     */     {
/* 569 */       StringTokenizer tok = new StringTokenizer(msg, ",");
/*     */       
/* 571 */       int ttl = Integer.parseInt(tok.nextToken().trim());
/* 572 */       int time = -1;
/*     */       
/*     */       InetAddress address;
/*     */       
/* 576 */       if (tok.hasMoreTokens())
/*     */       {
/* 578 */         int i_addr = Integer.parseInt(tok.nextToken().trim());
/*     */         
/* 580 */         InetAddress address = AEWin32AccessImpl.this.intToAddress(i_addr);
/*     */         
/* 582 */         time = Integer.parseInt(tok.nextToken().trim());
/*     */ 
/*     */ 
/*     */       }
/*     */       else
/*     */       {
/*     */ 
/* 589 */         address = null;
/*     */       }
/*     */       
/* 592 */       return this.cb.reportNode(this.ping_mode ? -1 : ttl, address, time) ? 1 : 0;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public Map<File, Map> getAllDrives()
/*     */   {
/* 600 */     String state_key = "awein32.getalldrives.state.2";
/*     */     
/* 602 */     int state = COConfigurationManager.getIntParameter(state_key, 0);
/*     */     
/* 604 */     if (state == 1)
/*     */     {
/* 606 */       Debug.out("Not enumerating system drives as it crashed last time we tried");
/*     */       
/* 608 */       return new HashMap();
/*     */     }
/*     */     try
/*     */     {
/* 612 */       COConfigurationManager.setParameter(state_key, 1);
/*     */       
/* 614 */       COConfigurationManager.save();
/*     */       
/* 616 */       Map<File, Map> mapDrives = new HashMap();
/*     */       try {
/* 618 */         List availableDrives = AEWin32AccessInterface.getAvailableDrives();
/* 619 */         if (availableDrives != null) {
/* 620 */           for (Object object : availableDrives) {
/* 621 */             File f = (File)object;
/* 622 */             Map driveInfo = AEWin32AccessInterface.getDriveInfo(f.getPath().charAt(0));
/* 623 */             boolean isWritableUSB = AEWin32Manager.getAccessor(false).isUSBDrive(driveInfo);
/* 624 */             driveInfo.put("isWritableUSB", Boolean.valueOf(isWritableUSB));
/* 625 */             mapDrives.put(f, driveInfo);
/*     */           }
/*     */         }
/*     */         
/* 629 */         return mapDrives;
/*     */       } catch (UnsatisfiedLinkError ue) {
/* 631 */         Debug.outNoStack("Old aereg.dll");
/*     */       } catch (Throwable e) {
/* 633 */         Debug.out(e);
/*     */       }
/* 635 */       return Collections.emptyMap();
/*     */     }
/*     */     finally {
/* 638 */       COConfigurationManager.setParameter(state_key, 2);
/*     */       
/* 640 */       COConfigurationManager.setDirty();
/*     */     }
/*     */   }
/*     */   
/*     */   public boolean isUSBDrive(Map driveInfo) {
/* 645 */     if (driveInfo == null) {
/* 646 */       return false;
/*     */     }
/* 648 */     boolean removeable = MapUtils.getMapBoolean(driveInfo, "Removable", false);
/*     */     
/* 650 */     long driveType = MapUtils.getMapLong(driveInfo, "DriveType", 0L);
/*     */     
/* 652 */     long busType = MapUtils.getMapLong(driveInfo, "BusType", 0L);
/*     */     
/* 654 */     long mediaType = MapUtils.getMapLong(driveInfo, "MediaType", -1L);
/*     */     
/* 656 */     if ((removeable) && (driveType == 2L) && (busType == 7L) && ((mediaType == 11L) || (mediaType == -1L)))
/*     */     {
/* 658 */       return true;
/*     */     }
/*     */     
/* 661 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setThreadExecutionState(int state)
/*     */   {
/* 668 */     AEWin32AccessInterface.setThreadExecutionState(state);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/platform/win32/access/impl/AEWin32AccessImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */