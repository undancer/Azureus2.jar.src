/*     */ package com.aelitis.azureus.core.devices.impl;
/*     */ 
/*     */ import com.aelitis.azureus.core.AzureusCore;
/*     */ import com.aelitis.azureus.core.AzureusCoreFactory;
/*     */ import com.aelitis.azureus.core.AzureusCoreRunningListener;
/*     */ import com.aelitis.azureus.core.devices.DeviceInternetGateway;
/*     */ import com.aelitis.azureus.plugins.upnp.UPnPMapping;
/*     */ import com.aelitis.azureus.plugins.upnp.UPnPPlugin;
/*     */ import com.aelitis.azureus.plugins.upnp.UPnPPluginService;
/*     */ import com.aelitis.azureus.plugins.upnp.UPnPPluginService.serviceMapping;
/*     */ import com.aelitis.net.upnp.UPnPDevice;
/*     */ import com.aelitis.net.upnp.UPnPRootDevice;
/*     */ import com.aelitis.net.upnp.services.UPnPWANConnection;
/*     */ import java.io.IOException;
/*     */ import java.net.URL;
/*     */ import java.net.URLConnection;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import java.util.TreeSet;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.core3.util.Debug;
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
/*     */ public class DeviceInternetGatewayImpl
/*     */   extends DeviceUPnPImpl
/*     */   implements DeviceInternetGateway
/*     */ {
/*     */   private static final int CHECK_MAPPINGS_PERIOD = 30000;
/*     */   private static final int CHECK_MAPPINGS_TICK_COUNT = 6;
/*     */   private static UPnPPlugin upnp_plugin;
/*     */   private boolean mapper_enabled;
/*     */   private UPnPPluginService[] current_services;
/*     */   private UPnPMapping[] current_mappings;
/*     */   
/*     */   static
/*     */   {
/*  53 */     AzureusCoreFactory.addCoreRunningListener(new AzureusCoreRunningListener()
/*     */     {
/*     */       public void azureusCoreRunning(AzureusCore core)
/*     */       {
/*     */         try {
/*  58 */           PluginInterface pi_upnp = core.getPluginManager().getPluginInterfaceByClass(UPnPPlugin.class);
/*     */           
/*     */ 
/*  61 */           if (pi_upnp != null)
/*     */           {
/*  63 */             DeviceInternetGatewayImpl.access$002((UPnPPlugin)pi_upnp.getPlugin());
/*     */           }
/*     */         }
/*     */         catch (Throwable e) {}
/*     */       }
/*     */     });
/*     */   }
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
/*     */   protected DeviceInternetGatewayImpl(DeviceManagerImpl _manager, UPnPDevice _device, List<UPnPWANConnection> _connections)
/*     */   {
/*  82 */     super(_manager, _device, 1);
/*     */     
/*  84 */     updateStatus(0);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected DeviceInternetGatewayImpl(DeviceManagerImpl _manager, Map _map)
/*     */     throws IOException
/*     */   {
/*  94 */     super(_manager, _map);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected boolean updateFrom(DeviceImpl _other, boolean _is_alive)
/*     */   {
/* 102 */     if (!super.updateFrom(_other, _is_alive))
/*     */     {
/* 104 */       return false;
/*     */     }
/*     */     
/* 107 */     if (!(_other instanceof DeviceInternetGatewayImpl))
/*     */     {
/* 109 */       Debug.out("Inconsistent");
/*     */       
/* 111 */       return false;
/*     */     }
/*     */     
/* 114 */     DeviceInternetGatewayImpl other = (DeviceInternetGatewayImpl)_other;
/*     */     
/* 116 */     return true;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void updateStatus(int tick_count)
/*     */   {
/* 124 */     super.updateStatus(tick_count);
/*     */     
/* 126 */     if (tick_count % 6 != 0)
/*     */     {
/* 128 */       return;
/*     */     }
/*     */     
/* 131 */     this.mapper_enabled = ((upnp_plugin != null) && (upnp_plugin.isEnabled()));
/*     */     
/* 133 */     UPnPDevice device = getUPnPDevice();
/*     */     
/* 135 */     if ((this.mapper_enabled) && (device != null))
/*     */     {
/* 137 */       this.current_services = upnp_plugin.getServices(device);
/*     */       
/* 139 */       this.current_mappings = upnp_plugin.getMappings();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected URL getPresentationURL(UPnPDevice device)
/*     */   {
/* 147 */     URL url = super.getPresentationURL(device);
/*     */     
/* 149 */     if (url == null)
/*     */     {
/*     */       try
/*     */       {
/*     */ 
/* 154 */         URL loc = device.getRootDevice().getLocation();
/*     */         
/* 156 */         URL test_loc = new URL(loc.getProtocol() + "://" + loc.getHost() + "/");
/*     */         
/* 158 */         test_loc.openConnection().connect();
/*     */         
/* 160 */         return test_loc;
/*     */       }
/*     */       catch (Throwable e) {}
/*     */     }
/*     */     
/*     */ 
/* 166 */     return url;
/*     */   }
/*     */   
/*     */ 
/*     */   protected Set<mapping> getRequiredMappings()
/*     */   {
/* 172 */     Set<mapping> res = new TreeSet();
/*     */     
/* 174 */     UPnPMapping[] required_mappings = this.current_mappings;
/*     */     
/* 176 */     if (required_mappings != null)
/*     */     {
/* 178 */       for (UPnPMapping mapping : required_mappings)
/*     */       {
/* 180 */         if (mapping.isEnabled())
/*     */         {
/* 182 */           res.add(new mapping(mapping));
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 187 */     return res;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected Set<mapping> getActualMappings(UPnPPluginService service)
/*     */   {
/* 194 */     UPnPPluginService.serviceMapping[] actual_mappings = service.getMappings();
/*     */     
/* 196 */     Set<mapping> actual = new TreeSet();
/*     */     
/* 198 */     for (UPnPPluginService.serviceMapping act_mapping : actual_mappings)
/*     */     {
/* 200 */       mapping m = new mapping(act_mapping);
/*     */       
/* 202 */       actual.add(m);
/*     */     }
/*     */     
/* 205 */     return actual;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void getDisplayProperties(List<String[]> dp)
/*     */   {
/* 212 */     super.getDisplayProperties(dp);
/*     */     
/* 214 */     addDP(dp, "device.router.is_mapping", this.mapper_enabled);
/*     */     
/* 216 */     UPnPPluginService[] services = this.current_services;
/*     */     
/* 218 */     String req_map_str = "";
/*     */     
/* 220 */     Set<mapping> required = getRequiredMappings();
/*     */     
/* 222 */     for (mapping m : required)
/*     */     {
/* 224 */       req_map_str = req_map_str + (req_map_str.length() == 0 ? "" : ",") + m.getString();
/*     */     }
/*     */     
/* 227 */     addDP(dp, "device.router.req_map", req_map_str);
/*     */     
/* 229 */     if (services != null)
/*     */     {
/* 231 */       for (UPnPPluginService service : services)
/*     */       {
/* 233 */         Set<mapping> actual = getActualMappings(service);
/*     */         
/* 235 */         String act_map_str = "";
/*     */         
/* 237 */         for (mapping m : actual)
/*     */         {
/* 239 */           if (required.contains(m))
/*     */           {
/* 241 */             act_map_str = act_map_str + (act_map_str.length() == 0 ? "" : ",") + m.getString();
/*     */           }
/*     */         }
/*     */         
/* 245 */         String service_name = MessageText.getString("device.router.con_type", new String[] { service.getService().getConnectionType() });
/*     */         
/* 247 */         addDP(dp, "!    " + service_name + "!", act_map_str);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   protected static class mapping
/*     */     implements Comparable<mapping>
/*     */   {
/*     */     private boolean is_tcp;
/*     */     
/*     */     private int port;
/*     */     
/*     */ 
/*     */     protected mapping(UPnPMapping m)
/*     */     {
/* 263 */       this.is_tcp = m.isTCP();
/* 264 */       this.port = m.getPort();
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     protected mapping(UPnPPluginService.serviceMapping m)
/*     */     {
/* 271 */       this.is_tcp = m.isTCP();
/* 272 */       this.port = m.getPort();
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     public int compareTo(mapping o)
/*     */     {
/* 279 */       int res = this.port - o.port;
/*     */       
/* 281 */       if (res == 0)
/*     */       {
/* 283 */         res = (this.is_tcp ? 1 : 0) - (o.is_tcp ? 1 : 0);
/*     */       }
/*     */       
/* 286 */       return res;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     public boolean equals(Object _other)
/*     */     {
/* 293 */       if ((_other instanceof mapping))
/*     */       {
/* 295 */         mapping other = (mapping)_other;
/*     */         
/* 297 */         return (this.is_tcp == other.is_tcp) && (this.port == other.port);
/*     */       }
/*     */       
/*     */ 
/* 301 */       return false;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     public int hashCode()
/*     */     {
/* 308 */       return (this.port << 16) + (this.is_tcp ? 1 : 0);
/*     */     }
/*     */     
/*     */ 
/*     */     public String getString()
/*     */     {
/* 314 */       return (this.is_tcp ? "TCP" : "UDP") + " " + this.port;
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/devices/impl/DeviceInternetGatewayImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */