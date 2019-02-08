/*     */ package com.aelitis.azureus.core.proxy.impl;
/*     */ 
/*     */ import com.aelitis.azureus.core.networkmanager.admin.NetworkAdmin;
/*     */ import com.aelitis.azureus.core.proxy.AEProxyAddressMapper;
/*     */ import com.aelitis.azureus.core.proxy.AEProxyAddressMapper.AppliedPortMapping;
/*     */ import com.aelitis.azureus.core.proxy.AEProxyAddressMapper.PortMapping;
/*     */ import java.net.InetAddress;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.net.MalformedURLException;
/*     */ import java.net.URL;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.util.AEMonitor;
/*     */ import org.gudy.azureus2.core3.util.AENetworkClassifier;
/*     */ import org.gudy.azureus2.core3.util.ByteFormatter;
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
/*     */ public class AEProxyAddressMapperImpl
/*     */   implements AEProxyAddressMapper
/*     */ {
/*  47 */   protected static final AEProxyAddressMapper singleton = new AEProxyAddressMapperImpl();
/*     */   protected boolean enabled;
/*     */   protected String prefix;
/*     */   protected long next_value;
/*     */   
/*  52 */   public static AEProxyAddressMapper getSingleton() { return singleton; }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  60 */   protected final Map<String, String> map = new HashMap();
/*  61 */   protected final Map<String, String> reverse_map = new HashMap();
/*     */   
/*  63 */   protected final AEMonitor this_mon = new AEMonitor("AEProxyAddressMapper");
/*     */   
/*  65 */   final Map<Integer, PortMappingImpl> port_mappings = new HashMap();
/*     */   
/*     */ 
/*     */   protected AEProxyAddressMapperImpl()
/*     */   {
/*  70 */     if ((COConfigurationManager.getBooleanParameter("Enable.Proxy")) && (COConfigurationManager.getBooleanParameter("Enable.SOCKS")))
/*     */     {
/*     */ 
/*  73 */       String host = COConfigurationManager.getStringParameter("Proxy.Host");
/*     */       try
/*     */       {
/*  76 */         if ((host.length() > 0) && (InetAddress.getByName(host).isLoopbackAddress()))
/*     */         {
/*     */ 
/*  79 */           this.enabled = true;
/*     */           
/*  81 */           byte[] b = new byte[120];
/*     */           
/*  83 */           for (int i = 0; i < b.length; i++)
/*     */           {
/*  85 */             b[i] = ((byte)RandomUtils.nextInt(256));
/*     */           }
/*     */           
/*  88 */           this.prefix = ByteFormatter.encodeString(b);
/*     */         }
/*     */       }
/*     */       catch (Throwable e) {
/*  92 */         Debug.printStackTrace(e);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public String internalise(String address)
/*     */   {
/* 101 */     if (!this.enabled)
/*     */     {
/* 103 */       return address;
/*     */     }
/*     */     
/* 106 */     if (address.length() < 256)
/*     */     {
/* 108 */       return address;
/*     */     }
/*     */     
/*     */     String target;
/*     */     try
/*     */     {
/* 114 */       this.this_mon.enter();
/*     */       
/* 116 */       target = (String)this.reverse_map.get(address);
/*     */       
/* 118 */       if (target == null)
/*     */       {
/* 120 */         StringBuilder target_b = new StringBuilder(256);
/*     */         
/* 122 */         target_b.append(this.prefix);
/* 123 */         target_b.append(this.next_value++);
/*     */         
/* 125 */         while (target_b.length() < 255)
/*     */         {
/* 127 */           target_b.append("0");
/*     */         }
/*     */         
/* 130 */         target = target_b.toString();
/*     */         
/* 132 */         this.map.put(target, address);
/*     */         
/* 134 */         this.reverse_map.put(address, target);
/*     */       }
/*     */     }
/*     */     finally {
/* 138 */       this.this_mon.exit();
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 143 */     return target;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public String externalise(String address)
/*     */   {
/* 150 */     if ((!this.enabled) || (address.length() < 255))
/*     */     {
/* 152 */       return address;
/*     */     }
/*     */     
/* 155 */     String target = (String)this.map.get(address);
/*     */     
/* 157 */     if (target == null)
/*     */     {
/* 159 */       target = address;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 164 */     return target;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public URL internalise(URL url)
/*     */   {
/* 171 */     if (!this.enabled)
/*     */     {
/* 173 */       return url;
/*     */     }
/*     */     
/* 176 */     String host = url.getHost();
/*     */     
/* 178 */     if (host.length() < 256)
/*     */     {
/* 180 */       return url;
/*     */     }
/*     */     
/* 183 */     String new_host = internalise(host);
/*     */     
/* 185 */     String url_str = url.toString();
/*     */     
/* 187 */     int pos = url_str.indexOf(host);
/*     */     
/* 189 */     if (pos == -1)
/*     */     {
/* 191 */       Debug.out("inconsistent url '" + url_str + "' / '" + host + "'");
/*     */       
/* 193 */       return url;
/*     */     }
/*     */     
/* 196 */     String new_url_str = url_str.substring(0, pos) + new_host + url_str.substring(pos + host.length());
/*     */     
/*     */     try
/*     */     {
/* 200 */       return new URL(new_url_str);
/*     */     }
/*     */     catch (MalformedURLException e)
/*     */     {
/* 204 */       Debug.printStackTrace(e);
/*     */     }
/* 206 */     return url;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public URL externalise(URL url)
/*     */   {
/* 214 */     if (!this.enabled)
/*     */     {
/* 216 */       return url;
/*     */     }
/*     */     
/* 219 */     String host = url.getHost();
/*     */     
/* 221 */     if (host.length() < 255)
/*     */     {
/* 223 */       return url;
/*     */     }
/*     */     
/* 226 */     String new_host = externalise(host);
/*     */     
/* 228 */     String url_str = url.toString();
/*     */     
/* 230 */     int pos = url_str.indexOf(host);
/*     */     
/* 232 */     if (pos == -1)
/*     */     {
/* 234 */       Debug.out("inconsistent url '" + url_str + "' / '" + host + "'");
/*     */       
/* 236 */       return url;
/*     */     }
/*     */     
/* 239 */     String new_url_str = url_str.substring(0, pos) + new_host + url_str.substring(pos + host.length());
/*     */     
/*     */     try
/*     */     {
/* 243 */       return new URL(new_url_str);
/*     */     }
/*     */     catch (MalformedURLException e)
/*     */     {
/* 247 */       Debug.printStackTrace(e);
/*     */     }
/* 249 */     return url;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public AEProxyAddressMapper.PortMapping registerPortMapping(int local_port, String ip)
/*     */   {
/* 259 */     PortMappingImpl mapping = new PortMappingImpl(ip, local_port, null, null);
/*     */     
/* 261 */     synchronized (this.port_mappings)
/*     */     {
/* 263 */       this.port_mappings.put(Integer.valueOf(local_port), mapping);
/*     */     }
/*     */     
/* 266 */     return mapping;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public AEProxyAddressMapper.PortMapping registerPortMapping(int local_port, String ip, Map<String, Object> properties)
/*     */   {
/* 275 */     PortMappingImpl mapping = new PortMappingImpl(ip, local_port, properties, null);
/*     */     
/* 277 */     synchronized (this.port_mappings)
/*     */     {
/* 279 */       this.port_mappings.put(Integer.valueOf(local_port), mapping);
/*     */     }
/*     */     
/* 282 */     return mapping;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public AEProxyAddressMapper.AppliedPortMapping applyPortMapping(InetAddress address, int port)
/*     */   {
/*     */     PortMappingImpl mapping;
/*     */     
/*     */ 
/*     */ 
/* 294 */     synchronized (this.port_mappings)
/*     */     {
/* 296 */       mapping = (PortMappingImpl)this.port_mappings.get(Integer.valueOf(port)); }
/*     */     InetSocketAddress result;
/*     */     InetSocketAddress result;
/* 299 */     if (mapping == null)
/*     */     {
/* 301 */       result = new InetSocketAddress(address, port);
/*     */     }
/*     */     else
/*     */     {
/* 305 */       InetAddress bind_ip = NetworkAdmin.getSingleton().getSingleHomedServiceBindAddress();
/*     */       
/* 307 */       if ((bind_ip == null) || (bind_ip.isAnyLocalAddress()))
/*     */       {
/* 309 */         bind_ip = null;
/*     */       }
/*     */       InetSocketAddress result;
/* 312 */       if (((bind_ip == null) && (address.isLoopbackAddress())) || ((bind_ip != null) && (bind_ip.equals(address))))
/*     */       {
/*     */ 
/* 315 */         String ip = mapping.getIP();
/*     */         InetSocketAddress result;
/* 317 */         if (AENetworkClassifier.categoriseAddress(ip) == "Public")
/*     */         {
/* 319 */           result = new InetSocketAddress(ip, port);
/*     */ 
/*     */ 
/*     */         }
/*     */         else
/*     */         {
/*     */ 
/* 326 */           result = InetSocketAddress.createUnresolved(ip, 6881);
/*     */         }
/*     */       }
/*     */       else {
/* 330 */         result = new InetSocketAddress(address, port);
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 336 */     return new AppliedPortMappingImpl(result, mapping == null ? null : mapping.getProperties(), null);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private class PortMappingImpl
/*     */     implements AEProxyAddressMapper.PortMapping
/*     */   {
/*     */     private final String ip;
/*     */     
/*     */     private final int port;
/*     */     
/*     */     private final Map<String, Object> properties;
/*     */     
/*     */ 
/*     */     private PortMappingImpl(int _ip, Map<String, Object> _port)
/*     */     {
/* 353 */       this.ip = _ip;
/* 354 */       this.port = _port;
/* 355 */       this.properties = _properties;
/*     */     }
/*     */     
/*     */ 
/*     */     private String getIP()
/*     */     {
/* 361 */       return this.ip;
/*     */     }
/*     */     
/*     */ 
/*     */     public Map<String, Object> getProperties()
/*     */     {
/* 367 */       return this.properties;
/*     */     }
/*     */     
/*     */ 
/*     */     public void unregister()
/*     */     {
/* 373 */       synchronized (AEProxyAddressMapperImpl.this.port_mappings)
/*     */       {
/* 375 */         AEProxyAddressMapperImpl.this.port_mappings.remove(Integer.valueOf(this.port));
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private static class AppliedPortMappingImpl
/*     */     implements AEProxyAddressMapper.AppliedPortMapping
/*     */   {
/*     */     private final InetSocketAddress address;
/*     */     
/*     */     private final Map<String, Object> properties;
/*     */     
/*     */ 
/*     */     private AppliedPortMappingImpl(InetSocketAddress _address, Map<String, Object> _properties)
/*     */     {
/* 392 */       this.address = _address;
/* 393 */       this.properties = _properties;
/*     */     }
/*     */     
/*     */ 
/*     */     public InetSocketAddress getAddress()
/*     */     {
/* 399 */       return this.address;
/*     */     }
/*     */     
/*     */ 
/*     */     public Map<String, Object> getProperties()
/*     */     {
/* 405 */       return this.properties;
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/proxy/impl/AEProxyAddressMapperImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */