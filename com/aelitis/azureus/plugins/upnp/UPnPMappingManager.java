/*     */ package com.aelitis.azureus.plugins.upnp;
/*     */ 
/*     */ import com.aelitis.azureus.core.util.CopyOnWriteList;
/*     */ import com.aelitis.net.upnp.services.UPnPWANConnection;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import java.util.StringTokenizer;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.config.ParameterListener;
/*     */ import org.gudy.azureus2.core3.util.AERunnable;
/*     */ import org.gudy.azureus2.core3.util.AsyncDispatcher;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.RandomUtils;
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
/*     */ public class UPnPMappingManager
/*     */ {
/*     */   private static UPnPMappingManager singleton;
/*     */   private UPnPPlugin plugin;
/*     */   
/*     */   protected static synchronized UPnPMappingManager getSingleton(UPnPPlugin plugin)
/*     */   {
/*  48 */     if (singleton == null)
/*     */     {
/*  50 */       singleton = new UPnPMappingManager(plugin);
/*     */     }
/*     */     
/*  53 */     return singleton;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*  58 */   private List<UPnPMapping> mappings = new ArrayList();
/*     */   
/*  60 */   private CopyOnWriteList<UPnPMappingManagerListener> listeners = new CopyOnWriteList();
/*     */   
/*  62 */   private AsyncDispatcher async_dispatcher = new AsyncDispatcher();
/*     */   
/*     */ 
/*     */ 
/*     */   protected UPnPMappingManager(UPnPPlugin _plugin)
/*     */   {
/*  68 */     this.plugin = _plugin;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  76 */     addConfigPort("upnp.mapping.dataport", false, "UDP.Listen.Port.Enable", "UDP.Listen.Port");
/*     */     
/*     */ 
/*     */ 
/*  80 */     addConfigPort("upnp.mapping.trackerclientudp", false, "Server Enable UDP", "UDP.NonData.Listen.Port");
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*  85 */     addConfigPort("upnp.mapping.dataport", true, "TCP.Listen.Port.Enable", "TCP.Listen.Port");
/*     */     
/*  87 */     addConfigPort("upnp.mapping.dataport", true, "HTTP.Data.Listen.Port.Enable", "HTTP.Data.Listen.Port");
/*     */     
/*     */ 
/*     */ 
/*  91 */     addConfigPort("upnp.mapping.tcptrackerport", true, "Tracker Port Enable", "Tracker Port");
/*     */     
/*  93 */     addConfigPortX("upnp.mapping.tcptrackerport", true, "Tracker Port Enable", "Tracker Port Backups");
/*     */     
/*  95 */     addConfigPort("upnp.mapping.tcpssltrackerport", true, "Tracker Port SSL Enable", "Tracker Port SSL");
/*     */     
/*  97 */     addConfigPortX("upnp.mapping.tcpssltrackerport", true, "Tracker Port SSL Enable", "Tracker Port SSL Backups");
/*     */     
/*     */ 
/*     */ 
/* 101 */     addConfigPort("upnp.mapping.udptrackerport", false, "Tracker Port UDP Enable", "Tracker Port");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void serviceFound(UPnPWANConnection service)
/*     */   {
/* 108 */     boolean save_config = false;
/*     */     
/* 110 */     if ((service.getCapabilities() & 0x1) == 0)
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 116 */       UPnPMapping[] maps = getMappings();
/*     */       
/* 118 */       for (int i = 0; i < maps.length; i++)
/*     */       {
/* 120 */         UPnPMapping map = maps[i];
/*     */         
/* 122 */         if ((map.isEnabled()) && (map.isTCP()))
/*     */         {
/* 124 */           List others = getMappingEx(false, map.getPort());
/*     */           
/* 126 */           if (others.size() != 0)
/*     */           {
/*     */ 
/*     */ 
/*     */ 
/* 131 */             boolean enabled = false;
/*     */             
/* 133 */             for (int j = 0; j < others.size(); j++)
/*     */             {
/* 135 */               UPnPMapping other = (UPnPMapping)others.get(j);
/*     */               
/* 137 */               if (other.isEnabled())
/*     */               {
/* 139 */                 enabled = true;
/*     */               }
/*     */             }
/*     */             
/* 143 */             if (enabled)
/*     */             {
/*     */ 
/*     */ 
/*     */               for (;;)
/*     */               {
/*     */ 
/* 150 */                 int new_port = RandomUtils.generateRandomNetworkListenPort();
/*     */                 
/* 152 */                 if ((getMapping(true, new_port) == null) && (getMapping(false, new_port) == null))
/*     */                 {
/* 154 */                   int new_port_1 = new_port;
/*     */                   
/* 156 */                   break;
/*     */                 }
/*     */               }
/*     */               int new_port_1;
/*     */               for (;;)
/*     */               {
/* 162 */                 int new_port = RandomUtils.generateRandomNetworkListenPort();
/*     */                 
/* 164 */                 if ((getMapping(true, new_port) == null) && (getMapping(false, new_port) == null))
/*     */                 {
/* 166 */                   if (new_port_1 != new_port)
/*     */                   {
/* 168 */                     int new_port_2 = new_port;
/*     */                     
/* 170 */                     break;
/*     */                   }
/*     */                 }
/*     */               }
/*     */               int new_port_2;
/* 175 */               String others_str = "";
/*     */               
/* 177 */               for (int j = 0; j < others.size(); j++)
/*     */               {
/* 179 */                 UPnPMapping other = (UPnPMapping)others.get(j);
/*     */                 
/* 181 */                 if (other.isEnabled())
/*     */                 {
/* 183 */                   others_str = others_str + (others_str.length() == 0 ? "" : ",") + other.getString(new_port_2);
/*     */                 }
/*     */               }
/*     */               
/* 187 */               this.plugin.logAlert(2, "upnp.portchange.alert", new String[] { map.getString(new_port_1), String.valueOf(map.getPort()), others_str, String.valueOf(map.getPort()) });
/*     */               
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 196 */               map.setPort(new_port_1);
/*     */               
/* 198 */               for (int j = 0; j < others.size(); j++)
/*     */               {
/* 200 */                 UPnPMapping other = (UPnPMapping)others.get(j);
/*     */                 
/* 202 */                 if (other.isEnabled())
/*     */                 {
/* 204 */                   other.setPort(new_port_2);
/*     */                 }
/*     */               }
/*     */               
/* 208 */               save_config = true;
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/* 214 */     if (save_config)
/*     */     {
/* 216 */       COConfigurationManager.save();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected UPnPMapping addConfigPort(String name_resource, boolean tcp, boolean enabled, final String int_param_name)
/*     */   {
/* 227 */     int value = COConfigurationManager.getIntParameter(int_param_name);
/*     */     
/* 229 */     final UPnPMapping mapping = addMapping(name_resource, tcp, value, enabled);
/*     */     
/* 231 */     mapping.addListener(new UPnPMappingListener()
/*     */     {
/*     */ 
/*     */ 
/*     */       public void mappingChanged(UPnPMapping mapping)
/*     */       {
/*     */ 
/* 238 */         COConfigurationManager.setParameter(int_param_name, mapping.getPort());
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       public void mappingDestroyed(UPnPMapping mapping) {}
/* 247 */     });
/* 248 */     addConfigListener(int_param_name, new ParameterListener()
/*     */     {
/*     */ 
/*     */ 
/*     */       public void parameterChanged(String name)
/*     */       {
/*     */ 
/*     */ 
/* 256 */         mapping.setPort(COConfigurationManager.getIntParameter(int_param_name));
/*     */       }
/*     */       
/* 259 */     });
/* 260 */     return mapping;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void addConfigPort(String name_resource, boolean tcp, final String enabler_param_name, final String int_param_name)
/*     */   {
/* 270 */     boolean enabled = COConfigurationManager.getBooleanParameter(enabler_param_name);
/*     */     
/* 272 */     final UPnPMapping mapping = addConfigPort(name_resource, tcp, enabled, int_param_name);
/*     */     
/* 274 */     mapping.addListener(new UPnPMappingListener()
/*     */     {
/*     */ 
/*     */ 
/*     */       public void mappingChanged(UPnPMapping mapping)
/*     */       {
/*     */ 
/* 281 */         COConfigurationManager.setParameter(int_param_name, mapping.getPort());
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       public void mappingDestroyed(UPnPMapping mapping) {}
/* 290 */     });
/* 291 */     addConfigListener(enabler_param_name, new ParameterListener()
/*     */     {
/*     */ 
/*     */ 
/*     */       public void parameterChanged(String name)
/*     */       {
/*     */ 
/*     */ 
/* 299 */         mapping.setEnabled(COConfigurationManager.getBooleanParameter(enabler_param_name));
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void addConfigPortX(final String name_resource, final boolean tcp, final String enabler_param_name, final String string_param_name)
/*     */   {
/* 311 */     final List config_mappings = new ArrayList();
/*     */     
/* 313 */     ParameterListener l1 = new ParameterListener()
/*     */     {
/*     */ 
/*     */ 
/*     */       public void parameterChanged(String name)
/*     */       {
/*     */ 
/* 320 */         boolean enabled = COConfigurationManager.getBooleanParameter(enabler_param_name);
/*     */         
/* 322 */         List ports = UPnPMappingManager.this.stringToPorts(COConfigurationManager.getStringParameter(string_param_name));
/*     */         
/* 324 */         for (int i = 0; i < ports.size(); i++)
/*     */         {
/* 326 */           int port = ((Integer)ports.get(i)).intValue();
/*     */           
/* 328 */           if (config_mappings.size() <= i)
/*     */           {
/* 330 */             UPnPMapping mapping = UPnPMappingManager.this.addMapping(name_resource, tcp, port, enabled);
/*     */             
/*     */ 
/* 333 */             mapping.setEnabled(enabled);
/*     */             
/* 335 */             config_mappings.add(mapping);
/*     */           }
/*     */           else
/*     */           {
/* 339 */             ((UPnPMapping)config_mappings.get(i)).setPort(port);
/*     */           }
/*     */         }
/*     */         
/* 343 */         for (int i = ports.size(); i < config_mappings.size(); i++)
/*     */         {
/* 345 */           ((UPnPMapping)config_mappings.get(i)).setEnabled(false);
/*     */         }
/*     */         
/*     */       }
/* 349 */     };
/* 350 */     addConfigListener(string_param_name, l1);
/*     */     
/* 352 */     ParameterListener l2 = new ParameterListener()
/*     */     {
/*     */ 
/*     */ 
/*     */       public void parameterChanged(String name)
/*     */       {
/*     */ 
/* 359 */         List ports = UPnPMappingManager.this.stringToPorts(COConfigurationManager.getStringParameter(string_param_name));
/*     */         
/* 361 */         boolean enabled = COConfigurationManager.getBooleanParameter(enabler_param_name);
/*     */         
/* 363 */         for (int i = 0; i < (enabled ? ports.size() : config_mappings.size()); i++)
/*     */         {
/* 365 */           ((UPnPMapping)config_mappings.get(i)).setEnabled(enabled);
/*     */         }
/*     */         
/*     */       }
/* 369 */     };
/* 370 */     addConfigListener(enabler_param_name, l2);
/*     */     
/*     */ 
/* 373 */     l1.parameterChanged(null);
/* 374 */     l2.parameterChanged(null);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected List stringToPorts(String str)
/*     */   {
/* 381 */     str = str.replace(',', ';');
/*     */     
/* 383 */     StringTokenizer tok = new StringTokenizer(str, ";");
/*     */     
/* 385 */     List res = new ArrayList();
/*     */     
/* 387 */     while (tok.hasMoreTokens()) {
/*     */       try
/*     */       {
/* 390 */         res.add(new Integer(tok.nextToken().trim()));
/*     */       }
/*     */       catch (Throwable e)
/*     */       {
/* 394 */         Debug.out("Invalid port entry in '" + str + "'", e);
/*     */       }
/*     */     }
/*     */     
/* 398 */     return res;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public UPnPMapping addMapping(String desc_resource, boolean tcp, int port, boolean enabled)
/*     */   {
/* 410 */     UPnPMapping mapping = new UPnPMapping(desc_resource, tcp, port, enabled);
/*     */     
/* 412 */     synchronized (this.mappings)
/*     */     {
/* 414 */       this.mappings.add(mapping);
/*     */     }
/*     */     
/* 417 */     added(mapping);
/*     */     
/* 419 */     return mapping;
/*     */   }
/*     */   
/*     */ 
/*     */   public UPnPMapping[] getMappings()
/*     */   {
/* 425 */     synchronized (this.mappings)
/*     */     {
/* 427 */       UPnPMapping[] res = new UPnPMapping[this.mappings.size()];
/*     */       
/* 429 */       this.mappings.toArray(res);
/*     */       
/* 431 */       return res;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public UPnPMapping getMapping(boolean tcp, int port)
/*     */   {
/* 440 */     synchronized (this.mappings)
/*     */     {
/* 442 */       for (int i = 0; i < this.mappings.size(); i++)
/*     */       {
/* 444 */         UPnPMapping mapping = (UPnPMapping)this.mappings.get(i);
/*     */         
/* 446 */         if ((mapping.isTCP() == tcp) && (mapping.getPort() == port))
/*     */         {
/* 448 */           return mapping;
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 453 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public List getMappingEx(boolean tcp, int port)
/*     */   {
/* 461 */     List res = new ArrayList();
/*     */     
/* 463 */     synchronized (this.mappings)
/*     */     {
/* 465 */       for (int i = 0; i < this.mappings.size(); i++)
/*     */       {
/* 467 */         UPnPMapping mapping = (UPnPMapping)this.mappings.get(i);
/*     */         
/* 469 */         if ((mapping.isTCP() == tcp) && (mapping.getPort() == port))
/*     */         {
/* 471 */           res.add(mapping);
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 476 */     return res;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void added(UPnPMapping mapping)
/*     */   {
/* 483 */     mapping.addListener(new UPnPMappingListener()
/*     */     {
/*     */       public void mappingChanged(UPnPMapping mapping) {}
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       public void mappingDestroyed(UPnPMapping mapping)
/*     */       {
/* 496 */         synchronized (UPnPMappingManager.this.mappings)
/*     */         {
/* 498 */           UPnPMappingManager.this.mappings.remove(mapping);
/*     */         }
/*     */       }
/*     */     });
/*     */     
/* 503 */     for (UPnPMappingManagerListener listener : this.listeners) {
/*     */       try
/*     */       {
/* 506 */         listener.mappingAdded(mapping);
/*     */       }
/*     */       catch (Throwable e)
/*     */       {
/* 510 */         Debug.out(e);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void addListener(UPnPMappingManagerListener l)
/*     */   {
/* 519 */     this.listeners.add(l);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void removeListener(UPnPMappingManagerListener l)
/*     */   {
/* 526 */     this.listeners.remove(l);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void addConfigListener(final String param, final ParameterListener listener)
/*     */   {
/* 534 */     COConfigurationManager.addParameterListener(param, new ParameterListener()
/*     */     {
/*     */ 
/*     */ 
/*     */       public void parameterChanged(String name)
/*     */       {
/*     */ 
/*     */ 
/* 542 */         UPnPMappingManager.this.async_dispatcher.dispatch(new AERunnable()
/*     */         {
/*     */ 
/*     */           public void runSupport()
/*     */           {
/*     */ 
/* 548 */             UPnPMappingManager.8.this.val$listener.parameterChanged(UPnPMappingManager.8.this.val$param);
/*     */           }
/*     */         });
/*     */       }
/*     */     });
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/plugins/upnp/UPnPMappingManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */