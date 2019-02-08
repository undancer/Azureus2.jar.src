/*     */ package com.aelitis.azureus.plugins.upnp;
/*     */ 
/*     */ import com.aelitis.net.upnp.UPnPDevice;
/*     */ import com.aelitis.net.upnp.UPnPRootDevice;
/*     */ import com.aelitis.net.upnp.UPnPService;
/*     */ import com.aelitis.net.upnp.services.UPnPWANConnection;
/*     */ import com.aelitis.net.upnp.services.UPnPWANConnectionPortMapping;
/*     */ import java.net.InetAddress;
/*     */ import java.net.URL;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.core3.util.AEMonitor;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.plugins.logging.LoggerChannel;
/*     */ import org.gudy.azureus2.plugins.ui.config.BooleanParameter;
/*     */ import org.gudy.azureus2.plugins.ui.config.StringParameter;
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
/*     */ public class UPnPPluginService
/*     */ {
/*     */   private UPnPWANConnection connection;
/*     */   private StringParameter desc_prefix;
/*     */   private BooleanParameter alert_success;
/*     */   private BooleanParameter grab_ports;
/*     */   private BooleanParameter alert_other_port_param;
/*     */   private BooleanParameter release_mappings;
/*  49 */   protected List<serviceMapping> service_mappings = new ArrayList();
/*     */   
/*  51 */   protected AEMonitor this_mon = new AEMonitor("UPnPPluginService");
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected UPnPPluginService(UPnPWANConnection _connection, UPnPWANConnectionPortMapping[] _ports, StringParameter _desc_prefix, BooleanParameter _alert_success, BooleanParameter _grab_ports, BooleanParameter _alert_other_port_param, BooleanParameter _release_mappings)
/*     */   {
/*  63 */     this.connection = _connection;
/*  64 */     this.desc_prefix = _desc_prefix;
/*  65 */     this.alert_success = _alert_success;
/*  66 */     this.grab_ports = _grab_ports;
/*  67 */     this.alert_other_port_param = _alert_other_port_param;
/*  68 */     this.release_mappings = _release_mappings;
/*     */     
/*  70 */     for (int i = 0; i < _ports.length; i++)
/*     */     {
/*  72 */       this.service_mappings.add(new serviceMapping(_ports[i]));
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public String getName()
/*     */   {
/*  79 */     return this.connection.getGenericService().getDevice().getRootDevice().getDevice().getFriendlyName();
/*     */   }
/*     */   
/*     */ 
/*     */   public String getInfo()
/*     */   {
/*  85 */     return this.connection.getGenericService().getDevice().getRootDevice().getInfo();
/*     */   }
/*     */   
/*     */   public String getAddress()
/*     */   {
/*  90 */     return this.connection.getGenericService().getDevice().getRootDevice().getLocation().getHost();
/*     */   }
/*     */   
/*     */ 
/*     */   public int getPort()
/*     */   {
/*  96 */     URL url = this.connection.getGenericService().getDevice().getRootDevice().getLocation();
/*     */     
/*  98 */     int port = url.getPort();
/*     */     
/* 100 */     if (port == -1)
/*     */     {
/* 102 */       port = url.getDefaultPort();
/*     */     }
/*     */     
/* 105 */     return port;
/*     */   }
/*     */   
/*     */   public String getExternalAddress()
/*     */   {
/*     */     try
/*     */     {
/* 112 */       return this.connection.getExternalIPAddress();
/*     */     }
/*     */     catch (Throwable e) {}
/*     */     
/* 116 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public UPnPWANConnection getService()
/*     */   {
/* 123 */     return this.connection;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected String getOldDescriptionForPort(int port)
/*     */   {
/* 133 */     return this.desc_prefix.getValue() + " " + port;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected String getDescriptionForPort(boolean TCP, int port)
/*     */   {
/* 141 */     return this.desc_prefix.getValue() + " " + port + " " + (TCP ? "TCP" : "UDP");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void checkMapping(LoggerChannel log, UPnPMapping mapping)
/*     */   {
/*     */     try
/*     */     {
/* 150 */       this.this_mon.enter();
/*     */       
/* 152 */       if (mapping.isEnabled())
/*     */       {
/*     */ 
/*     */ 
/* 156 */         for (int i = 0; i < this.service_mappings.size(); i++)
/*     */         {
/* 158 */           serviceMapping sm = (serviceMapping)this.service_mappings.get(i);
/*     */           
/* 160 */           if (sm.getMappings().contains(mapping))
/*     */           {
/* 162 */             if (sm.getPort() != mapping.getPort())
/*     */             {
/* 164 */               removeMapping(log, mapping, sm, false);
/*     */             }
/*     */           }
/*     */         }
/*     */         
/* 169 */         serviceMapping grab_in_progress = null;
/*     */         
/* 171 */         String local_address = this.connection.getGenericService().getDevice().getRootDevice().getLocalAddress().getHostAddress();
/*     */         
/* 173 */         for (int i = 0; i < this.service_mappings.size(); i++)
/*     */         {
/* 175 */           serviceMapping sm = (serviceMapping)this.service_mappings.get(i);
/*     */           
/* 177 */           if ((sm.isTCP() == mapping.isTCP()) && (sm.getPort() == mapping.getPort()))
/*     */           {
/*     */ 
/* 180 */             if (sm.getInternalHost().equals(local_address))
/*     */             {
/*     */ 
/*     */ 
/*     */ 
/* 185 */               sm.addMapping(mapping);
/*     */               
/* 187 */               if (!sm.getLogged(mapping))
/*     */               {
/* 189 */                 sm.setLogged(mapping);
/*     */                 
/* 191 */                 log.log("Mapping " + mapping.getString() + " already established");
/*     */               }
/*     */               
/*     */ 
/*     */               return;
/*     */             }
/*     */             
/* 198 */             if (!this.grab_ports.getValue())
/*     */             {
/* 200 */               if (!sm.getLogged(mapping))
/*     */               {
/* 202 */                 sm.setLogged(mapping);
/*     */                 
/* 204 */                 String text = MessageText.getString("upnp.alert.differenthost", new String[] { mapping.getString(), sm.getInternalHost() });
/*     */                 
/*     */ 
/*     */ 
/*     */ 
/* 209 */                 if (this.alert_other_port_param.getValue())
/*     */                 {
/* 211 */                   log.logAlertRepeatable(2, text);
/*     */                 }
/*     */                 else {
/* 214 */                   log.log(text);
/*     */                 }
/*     */               }
/*     */               
/*     */ 
/*     */ 
/*     */               return;
/*     */             }
/*     */             
/*     */ 
/* 224 */             sm.addMapping(mapping);
/*     */             
/* 226 */             grab_in_progress = sm;
/*     */           }
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 235 */         String error_text = null;
/*     */         try
/*     */         {
/* 238 */           this.connection.addPortMapping(mapping.isTCP(), mapping.getPort(), getDescriptionForPort(mapping.isTCP(), mapping.getPort()));
/*     */           
/*     */           String text;
/*     */           
/*     */           String text;
/*     */           
/* 244 */           if (grab_in_progress != null)
/*     */           {
/* 246 */             text = MessageText.getString("upnp.alert.mappinggrabbed", new String[] { mapping.getString(), grab_in_progress.getInternalHost() });
/*     */ 
/*     */           }
/*     */           else
/*     */           {
/* 251 */             text = MessageText.getString("upnp.alert.mappingok", new String[] { mapping.getString() });
/*     */           }
/*     */           
/*     */ 
/*     */ 
/* 256 */           log.log(text);
/*     */           
/* 258 */           if (this.alert_success.getValue())
/*     */           {
/* 260 */             log.logAlertRepeatable(1, text);
/*     */           }
/*     */         }
/*     */         catch (Throwable e)
/*     */         {
/* 265 */           error_text = Debug.getNestedExceptionMessage(e);
/*     */           
/* 267 */           String text = MessageText.getString("upnp.alert.mappingfailed", new String[] { mapping.getString() });
/*     */           
/*     */ 
/*     */ 
/*     */ 
/* 272 */           log.log(text);
/*     */           
/* 274 */           if (this.alert_other_port_param.getValue())
/*     */           {
/* 276 */             log.logAlertRepeatable(3, text);
/*     */           }
/*     */         }
/*     */         
/* 280 */         if (grab_in_progress == null)
/*     */         {
/* 282 */           serviceMapping new_mapping = new serviceMapping(mapping);
/*     */           
/* 284 */           new_mapping.setError(error_text);
/*     */           
/* 286 */           this.service_mappings.add(new_mapping);
/*     */         }
/*     */         else
/*     */         {
/* 290 */           grab_in_progress.setError(error_text);
/*     */         }
/*     */         
/*     */       }
/*     */       else
/*     */       {
/* 296 */         removeMapping(log, mapping, false);
/*     */       }
/*     */     }
/*     */     finally {
/* 300 */       this.this_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void removeMapping(LoggerChannel log, UPnPMapping mapping, boolean end_of_day)
/*     */   {
/*     */     try
/*     */     {
/* 311 */       this.this_mon.enter();
/*     */       
/* 313 */       for (i = 0; i < this.service_mappings.size();)
/*     */       {
/* 315 */         serviceMapping sm = (serviceMapping)this.service_mappings.get(i);
/*     */         
/* 317 */         if ((sm.isTCP() == mapping.isTCP()) && (sm.getPort() == mapping.getPort()) && (sm.getMappings().contains(mapping)))
/*     */         {
/*     */ 
/*     */ 
/* 321 */           removeMapping(log, mapping, sm, end_of_day); return;
/*     */         }
/* 313 */         i++;
/*     */       }
/*     */     }
/*     */     finally
/*     */     {
/*     */       int i;
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 328 */       this.this_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void removeMapping(LoggerChannel log, UPnPMapping upnp_mapping, serviceMapping service_mapping, boolean end_of_day)
/*     */   {
/* 339 */     if (service_mapping.isExternal())
/*     */     {
/* 341 */       log.log("Mapping " + service_mapping.getString() + " not removed as not created by Azureus");
/*     */     }
/*     */     else {
/* 344 */       int persistent = 1;
/*     */       
/* 346 */       List mappings = service_mapping.getMappings();
/*     */       
/* 348 */       for (int i = 0; i < mappings.size(); i++)
/*     */       {
/* 350 */         UPnPMapping map = (UPnPMapping)mappings.get(i);
/*     */         
/* 352 */         int p = map.getPersistent();
/*     */         
/* 354 */         if (p != 1)
/*     */         {
/*     */ 
/*     */ 
/* 358 */           if (p == 3)
/*     */           {
/*     */ 
/*     */ 
/* 362 */             if (persistent == 1)
/*     */             {
/* 364 */               persistent = p;
/*     */             }
/*     */             
/*     */ 
/*     */           }
/*     */           else {
/* 370 */             persistent = 2;
/*     */           }
/*     */         }
/*     */       }
/*     */       
/*     */ 
/* 376 */       if (persistent == 1)
/*     */       {
/* 378 */         persistent = this.release_mappings.getValue() ? 3 : 2;
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 384 */       if ((end_of_day) && (persistent == 2))
/*     */       {
/* 386 */         log.log("Mapping " + service_mapping.getString() + " not removed as mapping is persistent");
/*     */ 
/*     */ 
/*     */       }
/*     */       else
/*     */       {
/*     */ 
/*     */ 
/* 394 */         String service_name = service_mapping.getString();
/*     */         
/* 396 */         service_mapping.removeMapping(upnp_mapping);
/*     */         
/* 398 */         if (service_mapping.getMappings().size() == 0)
/*     */         {
/*     */           try {
/* 401 */             this.connection.deletePortMapping(service_mapping.isTCP(), service_mapping.getPort());
/*     */             
/*     */ 
/* 404 */             log.log("Mapping " + service_name + " removed");
/*     */           }
/*     */           catch (Throwable e)
/*     */           {
/* 408 */             log.log("Mapping " + service_name + " failed to delete", e);
/*     */           }
/*     */           
/* 411 */           this.service_mappings.remove(service_mapping);
/*     */         }
/*     */         else
/*     */         {
/* 415 */           log.log("Mapping " + service_mapping.getString() + " not removed as interest remains");
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public serviceMapping[] getMappings()
/*     */   {
/*     */     try
/*     */     {
/* 425 */       this.this_mon.enter();
/*     */       
/* 427 */       return (serviceMapping[])this.service_mappings.toArray(new serviceMapping[this.service_mappings.size()]);
/*     */     }
/*     */     finally
/*     */     {
/* 431 */       this.this_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public String getString()
/*     */   {
/* 438 */     String str = "name=" + getName() + ",info=" + getInfo() + ",int=" + getAddress() + ":" + getPort() + ",ext=" + getExternalAddress();
/*     */     
/* 440 */     serviceMapping[] sms = getMappings();
/*     */     
/* 442 */     for (serviceMapping sm : sms)
/*     */     {
/* 444 */       String error = sm.getError();
/*     */       
/* 446 */       if (error != null)
/*     */       {
/* 448 */         str = str + ":" + sm.getString() + " -> " + error;
/*     */       }
/*     */     }
/*     */     
/* 452 */     return str;
/*     */   }
/*     */   
/*     */ 
/*     */   public class serviceMapping
/*     */   {
/* 458 */     private List mappings = new ArrayList();
/*     */     
/*     */     private boolean tcp;
/*     */     
/*     */     private int port;
/*     */     
/*     */     private String internal_host;
/*     */     private boolean external;
/* 466 */     private List logged_mappings = new ArrayList();
/*     */     
/*     */ 
/*     */     private String error;
/*     */     
/*     */ 
/*     */     protected serviceMapping(UPnPWANConnectionPortMapping device_mapping)
/*     */     {
/* 474 */       this.tcp = device_mapping.isTCP();
/* 475 */       this.port = device_mapping.getExternalPort();
/* 476 */       this.internal_host = device_mapping.getInternalHost();
/*     */       
/* 478 */       String desc = device_mapping.getDescription();
/*     */       
/* 480 */       if ((desc == null) || ((!desc.equalsIgnoreCase(UPnPPluginService.this.getOldDescriptionForPort(this.port))) && (!desc.equalsIgnoreCase(UPnPPluginService.this.getDescriptionForPort(this.tcp, this.port)))))
/*     */       {
/*     */ 
/*     */ 
/* 484 */         this.external = true;
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     protected serviceMapping(UPnPMapping _mapping)
/*     */     {
/* 492 */       this.mappings.add(_mapping);
/*     */       
/* 494 */       this.tcp = _mapping.isTCP();
/* 495 */       this.port = _mapping.getPort();
/* 496 */       this.internal_host = UPnPPluginService.this.connection.getGenericService().getDevice().getRootDevice().getLocalAddress().getHostAddress();
/*     */     }
/*     */     
/*     */ 
/*     */     public boolean isExternal()
/*     */     {
/* 502 */       return this.external;
/*     */     }
/*     */     
/*     */ 
/*     */     protected List getMappings()
/*     */     {
/* 508 */       return this.mappings;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     protected void addMapping(UPnPMapping _mapping)
/*     */     {
/* 515 */       if (!this.mappings.contains(_mapping))
/*     */       {
/* 517 */         this.mappings.add(_mapping);
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     protected void removeMapping(UPnPMapping _mapping)
/*     */     {
/* 525 */       this.mappings.remove(_mapping);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     protected boolean getLogged(UPnPMapping mapping)
/*     */     {
/* 532 */       return this.logged_mappings.contains(mapping);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     protected void setLogged(UPnPMapping mapping)
/*     */     {
/* 539 */       if (!this.logged_mappings.contains(mapping))
/*     */       {
/* 541 */         this.logged_mappings.add(mapping);
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */     public boolean isTCP()
/*     */     {
/* 548 */       return this.tcp;
/*     */     }
/*     */     
/*     */ 
/*     */     public int getPort()
/*     */     {
/* 554 */       return this.port;
/*     */     }
/*     */     
/*     */ 
/*     */     public String getInternalHost()
/*     */     {
/* 560 */       return this.internal_host;
/*     */     }
/*     */     
/*     */ 
/*     */     private String getError()
/*     */     {
/* 566 */       return this.error;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     private void setError(String _error)
/*     */     {
/* 573 */       this.error = _error;
/*     */     }
/*     */     
/*     */ 
/*     */     public String getString()
/*     */     {
/* 579 */       if (this.mappings.size() == 0)
/*     */       {
/* 581 */         return "<external> (" + (isTCP() ? "TCP" : "UDP") + "/" + getPort() + ")";
/*     */       }
/*     */       
/*     */ 
/* 585 */       String str = "";
/*     */       
/* 587 */       for (int i = 0; i < this.mappings.size(); i++) {
/* 588 */         str = str + (i == 0 ? "" : ",") + ((UPnPMapping)this.mappings.get(i)).getString(getPort());
/*     */       }
/*     */       
/* 591 */       return str;
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/plugins/upnp/UPnPPluginService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */