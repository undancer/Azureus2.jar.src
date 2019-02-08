/*     */ package com.aelitis.net.upnp.impl.services;
/*     */ 
/*     */ import com.aelitis.net.upnp.UPnP;
/*     */ import com.aelitis.net.upnp.UPnPAction;
/*     */ import com.aelitis.net.upnp.UPnPActionArgument;
/*     */ import com.aelitis.net.upnp.UPnPActionInvocation;
/*     */ import com.aelitis.net.upnp.UPnPDevice;
/*     */ import com.aelitis.net.upnp.UPnPException;
/*     */ import com.aelitis.net.upnp.UPnPRootDevice;
/*     */ import com.aelitis.net.upnp.UPnPService;
/*     */ import com.aelitis.net.upnp.impl.UPnPImpl;
/*     */ import com.aelitis.net.upnp.impl.device.UPnPRootDeviceImpl;
/*     */ import com.aelitis.net.upnp.services.UPnPWANConnection;
/*     */ import com.aelitis.net.upnp.services.UPnPWANConnectionListener;
/*     */ import com.aelitis.net.upnp.services.UPnPWANConnectionPortMapping;
/*     */ import java.net.InetAddress;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import org.gudy.azureus2.core3.util.AEMonitor;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.SimpleTimer;
/*     */ import org.gudy.azureus2.core3.util.SystemTime;
/*     */ import org.gudy.azureus2.core3.util.TimerEvent;
/*     */ import org.gudy.azureus2.core3.util.TimerEventPerformer;
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
/*     */ public abstract class UPnPSSWANConnectionImpl
/*     */   implements UPnPWANConnection
/*     */ {
/*  43 */   private static AEMonitor class_mon = new AEMonitor("UPnPSSWANConnection");
/*  44 */   private static List services = new ArrayList();
/*     */   private UPnPServiceImpl service;
/*     */   
/*     */   static {
/*  48 */     SimpleTimer.addPeriodicEvent("UPnPSSWAN:checker", 600000L, new TimerEventPerformer()
/*     */     {
/*     */ 
/*     */       public void perform(TimerEvent ev)
/*     */       {
/*     */         try
/*     */         {
/*  55 */           List to_check = new ArrayList();
/*     */           try
/*     */           {
/*  58 */             UPnPSSWANConnectionImpl.class_mon.enter();
/*     */             
/*  60 */             Iterator it = UPnPSSWANConnectionImpl.services.iterator();
/*     */             
/*  62 */             while (it.hasNext())
/*     */             {
/*  64 */               UPnPSSWANConnectionImpl s = (UPnPSSWANConnectionImpl)it.next();
/*     */               
/*  66 */               if (s.getGenericService().getDevice().getRootDevice().isDestroyed())
/*     */               {
/*  68 */                 it.remove();
/*     */               }
/*     */               else
/*     */               {
/*  72 */                 to_check.add(s);
/*     */               }
/*     */             }
/*     */           }
/*     */           finally
/*     */           {
/*  78 */             UPnPSSWANConnectionImpl.class_mon.exit();
/*     */           }
/*     */           
/*  81 */           for (int i = 0; i < to_check.size(); i++) {
/*     */             try
/*     */             {
/*  84 */               ((UPnPSSWANConnectionImpl)to_check.get(i)).checkMappings();
/*     */ 
/*     */             }
/*     */             catch (Throwable e) {}
/*     */           }
/*     */           
/*     */         }
/*     */         catch (Throwable e)
/*     */         {
/*  93 */           Debug.printStackTrace(e);
/*     */         }
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/* 102 */   private List mappings = new ArrayList();
/* 103 */   private List listeners = new ArrayList();
/*     */   
/* 105 */   private boolean recheck_mappings = true;
/*     */   
/*     */ 
/*     */ 
/* 109 */   private boolean last_mapping_check_failed = true;
/*     */   
/*     */ 
/*     */ 
/*     */   protected UPnPSSWANConnectionImpl(UPnPServiceImpl _service)
/*     */   {
/* 115 */     this.service = _service;
/*     */     try
/*     */     {
/* 118 */       class_mon.enter();
/*     */       
/* 120 */       services.add(this);
/*     */     }
/*     */     finally
/*     */     {
/* 124 */       class_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public int getCapabilities()
/*     */   {
/* 131 */     String device_name = this.service.getDevice().getRootDevice().getDevice().getFriendlyName();
/*     */     
/* 133 */     int capabilities = -1;
/*     */     
/* 135 */     if (device_name.equals("WRT54G"))
/*     */     {
/* 137 */       capabilities = -2;
/*     */     }
/*     */     
/* 140 */     return capabilities;
/*     */   }
/*     */   
/*     */ 
/*     */   public UPnPService getGenericService()
/*     */   {
/* 146 */     return this.service;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public String[] getStatusInfo()
/*     */     throws UPnPException
/*     */   {
/* 154 */     UPnPAction act = this.service.getAction("GetStatusInfo");
/*     */     
/* 156 */     if (act == null)
/*     */     {
/* 158 */       log("Action 'GetStatusInfo' not supported, binding not established");
/*     */       
/* 160 */       throw new UPnPException("GetStatusInfo not supported");
/*     */     }
/*     */     
/*     */ 
/* 164 */     UPnPActionInvocation inv = act.getInvocation();
/*     */     
/* 166 */     UPnPActionArgument[] args = inv.invoke();
/*     */     
/* 168 */     String connection_status = null;
/* 169 */     String connection_error = null;
/* 170 */     String uptime = null;
/*     */     
/* 172 */     for (int i = 0; i < args.length; i++)
/*     */     {
/* 174 */       UPnPActionArgument arg = args[i];
/*     */       
/* 176 */       String name = arg.getName();
/*     */       
/* 178 */       if (name.equalsIgnoreCase("NewConnectionStatus"))
/*     */       {
/* 180 */         connection_status = arg.getValue();
/*     */       }
/* 182 */       else if (name.equalsIgnoreCase("NewLastConnectionError"))
/*     */       {
/* 184 */         connection_error = arg.getValue();
/*     */       }
/* 186 */       else if (name.equalsIgnoreCase("NewUptime"))
/*     */       {
/* 188 */         uptime = arg.getValue();
/*     */       }
/*     */     }
/*     */     
/* 192 */     return new String[] { connection_status, connection_error, uptime };
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void periodicallyRecheckMappings(boolean on)
/*     */   {
/* 200 */     this.recheck_mappings = on;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void checkMappings()
/*     */     throws UPnPException
/*     */   {
/* 208 */     if (!this.recheck_mappings) {
/*     */       return;
/*     */     }
/*     */     
/*     */     List mappings_copy;
/*     */     
/*     */     try
/*     */     {
/* 216 */       class_mon.enter();
/*     */       
/* 218 */       mappings_copy = new ArrayList(this.mappings);
/*     */     }
/*     */     finally
/*     */     {
/* 222 */       class_mon.exit();
/*     */     }
/*     */     
/* 225 */     UPnPWANConnectionPortMapping[] current = getPortMappings();
/*     */     
/* 227 */     Iterator it = mappings_copy.iterator();
/*     */     
/* 229 */     while (it.hasNext())
/*     */     {
/* 231 */       portMapping mapping = (portMapping)it.next();
/*     */       
/* 233 */       for (int j = 0; j < current.length; j++)
/*     */       {
/* 235 */         UPnPWANConnectionPortMapping c = current[j];
/*     */         
/* 237 */         if ((c.getExternalPort() == mapping.getExternalPort()) && (c.isTCP() == mapping.isTCP()))
/*     */         {
/*     */ 
/* 240 */           it.remove();
/*     */           
/* 242 */           break;
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 247 */     boolean log = false;
/*     */     
/* 249 */     if (mappings_copy.size() > 0)
/*     */     {
/* 251 */       if (!this.last_mapping_check_failed)
/*     */       {
/* 253 */         this.last_mapping_check_failed = true;
/*     */         
/* 255 */         log = true;
/*     */       }
/*     */     }
/*     */     else {
/* 259 */       this.last_mapping_check_failed = false;
/*     */     }
/*     */     
/* 262 */     it = mappings_copy.iterator();
/*     */     
/* 264 */     while (it.hasNext())
/*     */     {
/* 266 */       portMapping mapping = (portMapping)it.next();
/*     */       
/*     */ 
/*     */ 
/*     */       try
/*     */       {
/* 272 */         if (log)
/*     */         {
/* 274 */           log("Re-establishing mapping " + mapping.getString());
/*     */         }
/*     */         
/* 277 */         addPortMapping(mapping.isTCP(), mapping.getExternalPort(), mapping.getDescription());
/*     */       }
/*     */       catch (Throwable e)
/*     */       {
/* 281 */         Debug.printStackTrace(e);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void addPortMapping(boolean tcp, int port, String description)
/*     */     throws UPnPException
/*     */   {
/* 294 */     UPnPAction act = this.service.getAction("AddPortMapping");
/*     */     
/* 296 */     if (act == null)
/*     */     {
/* 298 */       log("Action 'AddPortMapping' not supported, binding not established");
/*     */     }
/*     */     else
/*     */     {
/* 302 */       UPnPActionInvocation add_inv = act.getInvocation();
/*     */       
/* 304 */       add_inv.addArgument("NewRemoteHost", "");
/* 305 */       add_inv.addArgument("NewExternalPort", "" + port);
/* 306 */       add_inv.addArgument("NewProtocol", tcp ? "TCP" : "UDP");
/* 307 */       add_inv.addArgument("NewInternalPort", "" + port);
/* 308 */       add_inv.addArgument("NewInternalClient", this.service.getDevice().getRootDevice().getLocalAddress().getHostAddress());
/* 309 */       add_inv.addArgument("NewEnabled", "1");
/* 310 */       add_inv.addArgument("NewPortMappingDescription", description);
/* 311 */       add_inv.addArgument("NewLeaseDuration", "0");
/*     */       
/* 313 */       boolean ok = false;
/*     */       try
/*     */       {
/* 316 */         add_inv.invoke();
/*     */         
/* 318 */         ok = true;
/*     */       }
/*     */       catch (UPnPException original_error)
/*     */       {
/*     */         try {
/*     */           int i;
/*     */           UPnPWANConnectionListener listener;
/* 325 */           log("Problem when adding port mapping - will try to see if an existing mapping is in the way");
/* 326 */           deletePortMapping(tcp, port);
/*     */         }
/*     */         catch (Throwable e)
/*     */         {
/* 330 */           throw original_error;
/*     */         }
/*     */         
/* 333 */         add_inv.invoke();
/*     */         
/* 335 */         ok = true;
/*     */       } finally {
/*     */         int i;
/*     */         UPnPWANConnectionListener listener;
/* 339 */         ((UPnPRootDeviceImpl)this.service.getDevice().getRootDevice()).portMappingResult(ok);
/*     */         
/* 341 */         for (int i = 0; i < this.listeners.size(); i++)
/*     */         {
/* 343 */           UPnPWANConnectionListener listener = (UPnPWANConnectionListener)this.listeners.get(i);
/*     */           try
/*     */           {
/* 346 */             listener.mappingResult(this, ok);
/*     */           }
/*     */           catch (Throwable e)
/*     */           {
/* 350 */             Debug.printStackTrace(e);
/*     */           }
/*     */         }
/*     */       }
/*     */       try
/*     */       {
/* 356 */         class_mon.enter();
/*     */         
/* 358 */         Iterator it = this.mappings.iterator();
/*     */         
/* 360 */         while (it.hasNext())
/*     */         {
/* 362 */           portMapping m = (portMapping)it.next();
/*     */           
/* 364 */           if ((m.getExternalPort() == port) && (m.isTCP() == tcp))
/*     */           {
/* 366 */             it.remove();
/*     */           }
/*     */         }
/*     */         
/* 370 */         this.mappings.add(new portMapping(port, tcp, "", description));
/*     */       }
/*     */       finally
/*     */       {
/* 374 */         class_mon.exit();
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void deletePortMapping(boolean tcp, int port)
/*     */     throws UPnPException
/*     */   {
/* 386 */     UPnPAction act = this.service.getAction("DeletePortMapping");
/*     */     
/* 388 */     if (act == null)
/*     */     {
/* 390 */       log("Action 'DeletePortMapping' not supported, binding not removed");
/*     */     }
/*     */     else
/*     */     {
/* 394 */       boolean mapping_found = false;
/*     */       try
/*     */       {
/* 397 */         class_mon.enter();
/*     */         
/* 399 */         Iterator it = this.mappings.iterator();
/*     */         
/* 401 */         while (it.hasNext())
/*     */         {
/* 403 */           portMapping mapping = (portMapping)it.next();
/*     */           
/* 405 */           if ((mapping.getExternalPort() == port) && (mapping.isTCP() == tcp))
/*     */           {
/*     */ 
/* 408 */             it.remove();
/*     */             
/* 410 */             mapping_found = true;
/*     */             
/* 412 */             break;
/*     */           }
/*     */         }
/*     */       }
/*     */       finally {
/* 417 */         class_mon.exit();
/*     */       }
/*     */       try
/*     */       {
/* 421 */         long start = SystemTime.getCurrentTime();
/*     */         
/* 423 */         UPnPActionInvocation inv = act.getInvocation();
/*     */         
/* 425 */         inv.addArgument("NewRemoteHost", "");
/* 426 */         inv.addArgument("NewProtocol", tcp ? "TCP" : "UDP");
/* 427 */         inv.addArgument("NewExternalPort", "" + port);
/*     */         
/* 429 */         inv.invoke();
/*     */         
/* 431 */         long elapsed = SystemTime.getCurrentTime() - start;
/*     */         
/* 433 */         if (elapsed > 4000L)
/*     */         {
/* 435 */           String info = this.service.getDevice().getRootDevice().getInfo();
/*     */           
/* 437 */           ((UPnPImpl)this.service.getDevice().getRootDevice().getUPnP()).logAlert("UPnP device '" + info + "' is taking a long time to release port mappings, consider disabling this via the UPnP configuration.", false, 3);
/*     */ 
/*     */         }
/*     */         
/*     */ 
/*     */       }
/*     */       catch (UPnPException e)
/*     */       {
/*     */ 
/* 446 */         if (mapping_found)
/*     */         {
/* 448 */           throw e;
/*     */         }
/*     */         
/*     */ 
/* 452 */         log("Removal of mapping failed but not established explicitly so ignoring error");
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public UPnPWANConnectionPortMapping[] getPortMappings()
/*     */     throws UPnPException
/*     */   {
/* 463 */     boolean ok = true;
/*     */     
/*     */ 
/*     */ 
/*     */     try
/*     */     {
/* 469 */       int entries = 0;
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 475 */       UPnPAction act = this.service.getAction("GetGenericPortMappingEntry");
/*     */       
/* 477 */       if (act == null)
/*     */       {
/* 479 */         log("Action 'GetGenericPortMappingEntry' not supported, can't enumerate bindings");
/*     */         int i;
/* 481 */         UPnPWANConnectionListener listener; return new UPnPWANConnectionPortMapping[0];
/*     */       }
/*     */       
/*     */ 
/* 485 */       Object res = new ArrayList();
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 490 */       portMapping prev_mapping = null;
/*     */       UPnPActionInvocation inv;
/* 492 */       for (int i = 0; i < (entries == 0 ? 512 : entries); i++)
/*     */       {
/* 494 */         inv = act.getInvocation();
/*     */         
/* 496 */         inv.addArgument("NewPortMappingIndex", "" + i);
/*     */         try
/*     */         {
/* 499 */           UPnPActionArgument[] outs = inv.invoke();
/*     */           
/* 501 */           int port = 0;
/* 502 */           boolean tcp = false;
/* 503 */           String internal_host = null;
/* 504 */           String description = "";
/*     */           
/* 506 */           for (int j = 0; j < outs.length; j++)
/*     */           {
/* 508 */             UPnPActionArgument out = outs[j];
/*     */             
/* 510 */             String out_name = out.getName();
/*     */             
/* 512 */             if (out_name.equalsIgnoreCase("NewExternalPort"))
/*     */             {
/* 514 */               port = Integer.parseInt(out.getValue());
/*     */             }
/* 516 */             else if (out_name.equalsIgnoreCase("NewProtocol"))
/*     */             {
/* 518 */               tcp = out.getValue().equalsIgnoreCase("TCP");
/*     */             }
/* 520 */             else if (out_name.equalsIgnoreCase("NewInternalClient"))
/*     */             {
/* 522 */               internal_host = out.getValue();
/*     */             }
/* 524 */             else if (out_name.equalsIgnoreCase("NewPortMappingDescription"))
/*     */             {
/* 526 */               description = out.getValue();
/*     */             }
/*     */           }
/*     */           
/* 530 */           if ((prev_mapping != null) && 
/*     */           
/* 532 */             (prev_mapping.getExternalPort() == port) && (prev_mapping.isTCP() == tcp)) {
/*     */             break;
/*     */           }
/*     */           
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 541 */           prev_mapping = new portMapping(port, tcp, internal_host, description);
/*     */           
/* 543 */           ((List)res).add(prev_mapping);
/*     */         }
/*     */         catch (UPnPException e)
/*     */         {
/* 547 */           if (entries != 0) break label374;
/*     */         }
/* 549 */         break;
/*     */         
/*     */         label374:
/* 552 */         ok = false;
/*     */         
/* 554 */         throw e;
/*     */       }
/*     */       
/*     */ 
/* 558 */       UPnPWANConnectionPortMapping[] res2 = new UPnPWANConnectionPortMapping[((List)res).size()];
/*     */       
/* 560 */       ((List)res).toArray(res2);
/*     */       int i;
/* 562 */       UPnPWANConnectionListener listener; return res2;
/*     */     }
/*     */     finally
/*     */     {
/* 566 */       for (int i = 0; i < this.listeners.size(); i++)
/*     */       {
/* 568 */         UPnPWANConnectionListener listener = (UPnPWANConnectionListener)this.listeners.get(i);
/*     */         try
/*     */         {
/* 571 */           listener.mappingsReadResult(this, ok);
/*     */         }
/*     */         catch (Throwable e)
/*     */         {
/* 575 */           Debug.printStackTrace(e);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getExternalIPAddress()
/*     */     throws UPnPException
/*     */   {
/* 587 */     UPnPAction act = this.service.getAction("GetExternalIPAddress");
/*     */     
/* 589 */     if (act == null)
/*     */     {
/* 591 */       log("Action 'GetExternalIPAddress' not supported, binding not established");
/*     */       
/* 593 */       throw new UPnPException("GetExternalIPAddress not supported");
/*     */     }
/*     */     
/*     */ 
/* 597 */     UPnPActionInvocation inv = act.getInvocation();
/*     */     
/* 599 */     UPnPActionArgument[] args = inv.invoke();
/*     */     
/* 601 */     String ip = null;
/*     */     
/* 603 */     for (int i = 0; i < args.length; i++)
/*     */     {
/* 605 */       UPnPActionArgument arg = args[i];
/*     */       
/* 607 */       String name = arg.getName();
/*     */       
/* 609 */       if (name.equalsIgnoreCase("NewExternalIPAddress"))
/*     */       {
/* 611 */         ip = arg.getValue();
/*     */       }
/*     */     }
/*     */     
/* 615 */     return ip;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void log(String str)
/*     */   {
/* 623 */     this.service.getDevice().getRootDevice().getUPnP().log(str);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void addListener(UPnPWANConnectionListener listener)
/*     */   {
/* 630 */     this.listeners.add(listener);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void removeListener(UPnPWANConnectionListener listener)
/*     */   {
/* 637 */     this.listeners.add(listener);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private static class portMapping
/*     */     implements UPnPWANConnectionPortMapping
/*     */   {
/*     */     protected int external_port;
/*     */     
/*     */     protected boolean tcp;
/*     */     
/*     */     protected String internal_host;
/*     */     
/*     */     protected String description;
/*     */     
/*     */ 
/*     */     protected portMapping(int _external_port, boolean _tcp, String _internal_host, String _description)
/*     */     {
/* 656 */       this.external_port = _external_port;
/* 657 */       this.tcp = _tcp;
/* 658 */       this.internal_host = _internal_host;
/* 659 */       this.description = _description;
/*     */     }
/*     */     
/*     */ 
/*     */     public boolean isTCP()
/*     */     {
/* 665 */       return this.tcp;
/*     */     }
/*     */     
/*     */ 
/*     */     public int getExternalPort()
/*     */     {
/* 671 */       return this.external_port;
/*     */     }
/*     */     
/*     */ 
/*     */     public String getInternalHost()
/*     */     {
/* 677 */       return this.internal_host;
/*     */     }
/*     */     
/*     */ 
/*     */     public String getDescription()
/*     */     {
/* 683 */       return this.description;
/*     */     }
/*     */     
/*     */ 
/*     */     protected String getString()
/*     */     {
/* 689 */       return getDescription() + " [" + getExternalPort() + ":" + (isTCP() ? "TCP" : "UDP") + "]";
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/net/upnp/impl/services/UPnPSSWANConnectionImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */