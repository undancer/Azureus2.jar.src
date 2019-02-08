/*     */ package com.aelitis.net.upnp.impl.services;
/*     */ 
/*     */ import com.aelitis.net.upnp.UPnPAction;
/*     */ import com.aelitis.net.upnp.UPnPDevice;
/*     */ import com.aelitis.net.upnp.UPnPException;
/*     */ import com.aelitis.net.upnp.UPnPService;
/*     */ import com.aelitis.net.upnp.UPnPStateVariable;
/*     */ import com.aelitis.net.upnp.impl.UPnPImpl;
/*     */ import com.aelitis.net.upnp.impl.device.UPnPDeviceImpl;
/*     */ import com.aelitis.net.upnp.impl.device.UPnPRootDeviceImpl;
/*     */ import com.aelitis.net.upnp.services.UPnPSpecificService;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.net.MalformedURLException;
/*     */ import java.net.Socket;
/*     */ import java.net.URL;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import org.gudy.azureus2.core3.util.UrlUtils;
/*     */ import org.gudy.azureus2.plugins.utils.xml.simpleparser.SimpleXMLParserDocument;
/*     */ import org.gudy.azureus2.plugins.utils.xml.simpleparser.SimpleXMLParserDocumentNode;
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
/*     */ public class UPnPServiceImpl
/*     */   implements UPnPService
/*     */ {
/*     */   private final UPnPDeviceImpl device;
/*     */   private String service_type;
/*     */   private String local_desc_url;
/*     */   private String local_control_url;
/*     */   private List actions;
/*     */   private List state_vars;
/*     */   private boolean direct_invoke;
/*     */   private URL preferred_control_url;
/*     */   
/*     */   public UPnPServiceImpl(UPnPDeviceImpl _device, String indent, SimpleXMLParserDocumentNode service_node)
/*     */   {
/*  61 */     this.device = _device;
/*     */     
/*  63 */     this.service_type = service_node.getChild("ServiceType").getValue().trim();
/*     */     
/*  65 */     this.local_desc_url = service_node.getChild("SCPDURL").getValue();
/*     */     
/*  67 */     this.local_control_url = service_node.getChild("controlURL").getValue();
/*     */     
/*  69 */     this.device.getUPnP().log(indent + this.service_type + ":desc=" + this.device.getAbsoluteURL(this.local_desc_url) + ", control=" + this.device.getAbsoluteURL(this.local_control_url));
/*     */   }
/*     */   
/*     */ 
/*     */   public UPnPDevice getDevice()
/*     */   {
/*  75 */     return this.device;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getServiceType()
/*     */   {
/*  81 */     return this.service_type;
/*     */   }
/*     */   
/*     */   public boolean isConnectable()
/*     */   {
/*     */     try
/*     */     {
/*  88 */       List<URL> urls = getControlURLs();
/*     */       
/*  90 */       Iterator i$ = urls.iterator(); if (i$.hasNext()) { URL url = (URL)i$.next();
/*     */         
/*  92 */         Socket socket = new Socket();
/*     */         try
/*     */         {
/*  95 */           int port = url.getPort();
/*     */           
/*  97 */           if (port <= 0)
/*     */           {
/*  99 */             port = url.getDefaultPort();
/*     */           }
/*     */           
/* 102 */           socket.connect(new InetSocketAddress(url.getHost(), port), 5000);
/*     */           
/* 104 */           if (getPreferredControlURL() == null)
/*     */           {
/* 106 */             setPreferredControlURL(url);
/*     */           }
/*     */           
/* 109 */           return true;
/*     */         }
/*     */         finally
/*     */         {
/*     */           try {
/* 114 */             socket.close();
/*     */           }
/*     */           catch (Throwable e) {}
/*     */         }
/*     */       }
/*     */     }
/*     */     catch (Throwable e) {}
/*     */     
/*     */ 
/*     */ 
/* 124 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public UPnPAction[] getActions()
/*     */     throws UPnPException
/*     */   {
/* 132 */     if (this.actions == null)
/*     */     {
/* 134 */       loadDescription();
/*     */     }
/*     */     
/* 137 */     UPnPAction[] res = new UPnPAction[this.actions.size()];
/*     */     
/* 139 */     this.actions.toArray(res);
/*     */     
/* 141 */     return res;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public UPnPAction getAction(String name)
/*     */     throws UPnPException
/*     */   {
/* 150 */     UPnPAction[] my_actions = getActions();
/*     */     
/* 152 */     for (int i = 0; i < my_actions.length; i++)
/*     */     {
/* 154 */       if (my_actions[i].getName().equalsIgnoreCase(name))
/*     */       {
/* 156 */         return my_actions[i];
/*     */       }
/*     */     }
/*     */     
/* 160 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public UPnPStateVariable[] getStateVariables()
/*     */     throws UPnPException
/*     */   {
/* 168 */     if (this.state_vars == null)
/*     */     {
/* 170 */       loadDescription();
/*     */     }
/*     */     
/* 173 */     UPnPStateVariable[] res = new UPnPStateVariable[this.state_vars.size()];
/*     */     
/* 175 */     this.state_vars.toArray(res);
/*     */     
/* 177 */     return res;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public UPnPStateVariable getStateVariable(String name)
/*     */     throws UPnPException
/*     */   {
/* 186 */     UPnPStateVariable[] vars = getStateVariables();
/*     */     
/* 188 */     for (int i = 0; i < vars.length; i++)
/*     */     {
/* 190 */       if (vars[i].getName().equalsIgnoreCase(name))
/*     */       {
/* 192 */         return vars[i];
/*     */       }
/*     */     }
/*     */     
/* 196 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public URL getDescriptionURL()
/*     */     throws UPnPException
/*     */   {
/* 204 */     return getURL(this.device.getAbsoluteURL(this.local_desc_url));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public List<URL> getControlURLs()
/*     */     throws UPnPException
/*     */   {
/* 212 */     List<URL> result = new ArrayList();
/*     */     
/* 214 */     String control_url = this.device.getAbsoluteURL(this.local_control_url);
/*     */     
/* 216 */     URL main_url = getURL(control_url);
/*     */     
/* 218 */     result.add(main_url);
/*     */     
/* 220 */     List<URL> alt_locs = this.device.getRootDevice().getAlternativeLocations();
/*     */     
/* 222 */     if (alt_locs.size() > 0)
/*     */     {
/* 224 */       for (URL alt_loc : alt_locs)
/*     */       {
/* 226 */         URL alt_url = main_url;
/*     */         
/* 228 */         alt_url = UrlUtils.setHost(alt_url, alt_loc.getHost());
/* 229 */         alt_url = UrlUtils.setPort(alt_url, alt_loc.getPort());
/*     */         
/* 231 */         result.add(alt_url);
/*     */       }
/*     */     }
/*     */     
/* 235 */     if ((result.size() > 1) && (this.preferred_control_url != null))
/*     */     {
/* 237 */       if ((!((URL)result.get(0)).equals(this.preferred_control_url)) && (result.contains(this.preferred_control_url)))
/*     */       {
/*     */ 
/* 240 */         result.remove(this.preferred_control_url);
/* 241 */         result.add(0, this.preferred_control_url);
/*     */       }
/*     */     }
/*     */     
/* 245 */     return result;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setPreferredControlURL(URL url)
/*     */   {
/* 252 */     this.preferred_control_url = url;
/*     */   }
/*     */   
/*     */ 
/*     */   protected URL getPreferredControlURL()
/*     */   {
/* 258 */     return this.preferred_control_url;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected URL getURL(String basis)
/*     */     throws UPnPException
/*     */   {
/*     */     try
/*     */     {
/* 270 */       String lc_basis = basis.toLowerCase();
/*     */       URL target;
/* 272 */       URL root_location; if ((lc_basis.startsWith("http")) || (lc_basis.startsWith("https")))
/*     */       {
/*     */ 
/*     */ 
/* 276 */         target = new URL(basis);
/*     */ 
/*     */       }
/*     */       else
/*     */       {
/*     */ 
/* 282 */         root_location = this.device.getRootDevice().getLocation();
/*     */       }
/* 284 */       return new URL(root_location.getProtocol() + "://" + root_location.getHost() + (root_location.getPort() == -1 ? "" : new StringBuilder().append(":").append(root_location.getPort()).toString()) + (basis.startsWith("/") ? "" : "/") + basis);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     }
/*     */     catch (MalformedURLException e)
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/* 295 */       throw new UPnPException("Malformed URL", e);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void loadDescription()
/*     */     throws UPnPException
/*     */   {
/* 304 */     SimpleXMLParserDocument doc = this.device.getUPnP().downloadXML(this.device, getDescriptionURL());
/*     */     
/* 306 */     parseActions(doc.getChild("ActionList"));
/*     */     
/* 308 */     parseStateVars(doc.getChild("ServiceStateTable"));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void parseActions(SimpleXMLParserDocumentNode action_list)
/*     */   {
/* 315 */     this.actions = new ArrayList();
/*     */     
/* 317 */     SimpleXMLParserDocumentNode[] kids = action_list.getChildren();
/*     */     
/* 319 */     for (int i = 0; i < kids.length; i++)
/*     */     {
/* 321 */       this.actions.add(new UPnPActionImpl(this, kids[i]));
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void parseStateVars(SimpleXMLParserDocumentNode action_list)
/*     */   {
/* 329 */     this.state_vars = new ArrayList();
/*     */     
/* 331 */     SimpleXMLParserDocumentNode[] kids = action_list.getChildren();
/*     */     
/* 333 */     for (int i = 0; i < kids.length; i++)
/*     */     {
/* 335 */       this.state_vars.add(new UPnPStateVariableImpl(this, kids[i]));
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public UPnPSpecificService getSpecificService()
/*     */   {
/* 342 */     if (this.service_type.equalsIgnoreCase("urn:schemas-upnp-org:service:WANIPConnection:1"))
/*     */     {
/* 344 */       return new UPnPSSWANIPConnectionImpl(this);
/*     */     }
/* 346 */     if (this.service_type.equalsIgnoreCase("urn:schemas-upnp-org:service:WANPPPConnection:1"))
/*     */     {
/* 348 */       return new UPnPSSWANPPPConnectionImpl(this);
/*     */     }
/* 350 */     if (this.service_type.equalsIgnoreCase("urn:schemas-upnp-org:service:WANCommonInterfaceConfig:1"))
/*     */     {
/* 352 */       return new UPnPSSWANCommonInterfaceConfigImpl(this);
/*     */     }
/* 354 */     if (this.service_type.equalsIgnoreCase("urn:schemas-upnp-org:service:VuzeOfflineDownloaderService:1"))
/*     */     {
/* 356 */       return new UPnPSSOfflineDownloaderImpl(this);
/*     */     }
/*     */     
/*     */ 
/* 360 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean getDirectInvocations()
/*     */   {
/* 367 */     return this.direct_invoke;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setDirectInvocations(boolean force)
/*     */   {
/* 374 */     this.direct_invoke = force;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/net/upnp/impl/services/UPnPServiceImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */