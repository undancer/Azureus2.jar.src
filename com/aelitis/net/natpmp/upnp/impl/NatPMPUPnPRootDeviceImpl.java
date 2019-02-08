/*     */ package com.aelitis.net.natpmp.upnp.impl;
/*     */ 
/*     */ import com.aelitis.net.natpmp.NatPMPDevice;
/*     */ import com.aelitis.net.upnp.UPnP;
/*     */ import com.aelitis.net.upnp.UPnPAction;
/*     */ import com.aelitis.net.upnp.UPnPDevice;
/*     */ import com.aelitis.net.upnp.UPnPDeviceImage;
/*     */ import com.aelitis.net.upnp.UPnPException;
/*     */ import com.aelitis.net.upnp.UPnPRootDevice;
/*     */ import com.aelitis.net.upnp.UPnPRootDeviceListener;
/*     */ import com.aelitis.net.upnp.UPnPService;
/*     */ import com.aelitis.net.upnp.UPnPStateVariable;
/*     */ import com.aelitis.net.upnp.services.UPnPSpecificService;
/*     */ import com.aelitis.net.upnp.services.UPnPWANConnection;
/*     */ import com.aelitis.net.upnp.services.UPnPWANConnectionListener;
/*     */ import com.aelitis.net.upnp.services.UPnPWANConnectionPortMapping;
/*     */ import java.net.InetAddress;
/*     */ import java.net.NetworkInterface;
/*     */ import java.net.URL;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import org.gudy.azureus2.core3.util.AERunnable;
/*     */ import org.gudy.azureus2.core3.util.ThreadPool;
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
/*     */ public class NatPMPUPnPRootDeviceImpl
/*     */   implements UPnPRootDevice
/*     */ {
/*     */   private UPnP upnp;
/*     */   private NatPMPDevice nat_device;
/*  46 */   private String USN = "natpmp";
/*     */   
/*     */ 
/*     */   private URL location;
/*     */   
/*     */   private UPnPDevice device;
/*     */   
/*     */   private UPnPService[] services;
/*     */   
/*     */   private ThreadPool thread_pool;
/*     */   
/*     */ 
/*     */   public NatPMPUPnPRootDeviceImpl(UPnP _upnp, NatPMPDevice _nat_device)
/*     */     throws Exception
/*     */   {
/*  61 */     this.upnp = _upnp;
/*  62 */     this.nat_device = _nat_device;
/*     */     
/*  64 */     this.location = new URL("http://undefined/");
/*     */     
/*  66 */     this.device = new NatPMPUPnPDevice();
/*     */     
/*  68 */     this.services = new UPnPService[] { new NatPMPUPnPWANConnection() };
/*     */     
/*  70 */     this.thread_pool = new ThreadPool("NatPMPUPnP", 1, true);
/*     */   }
/*     */   
/*     */ 
/*     */   public UPnP getUPnP()
/*     */   {
/*  76 */     return this.upnp;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getUSN()
/*     */   {
/*  82 */     return this.USN;
/*     */   }
/*     */   
/*     */ 
/*     */   public URL getLocation()
/*     */   {
/*  88 */     return this.location;
/*     */   }
/*     */   
/*     */ 
/*     */   public InetAddress getLocalAddress()
/*     */   {
/*  94 */     return this.nat_device.getLocalAddress();
/*     */   }
/*     */   
/*     */ 
/*     */   public NetworkInterface getNetworkInterface()
/*     */   {
/* 100 */     return this.nat_device.getNetworkInterface();
/*     */   }
/*     */   
/*     */ 
/*     */   public String getInfo()
/*     */   {
/* 106 */     return "Nat-PMP";
/*     */   }
/*     */   
/*     */ 
/*     */   public UPnPDevice getDevice()
/*     */   {
/* 112 */     return this.device;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isDestroyed()
/*     */   {
/* 118 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */   public Map getDiscoveryCache()
/*     */   {
/* 124 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void addListener(UPnPRootDeviceListener l) {}
/*     */   
/*     */ 
/*     */ 
/*     */   public void removeListener(UPnPRootDeviceListener l) {}
/*     */   
/*     */ 
/*     */ 
/*     */   protected class NatPMPUPnPDevice
/*     */     implements UPnPDevice
/*     */   {
/*     */     protected NatPMPUPnPDevice() {}
/*     */     
/*     */ 
/*     */ 
/*     */     public String getDeviceType()
/*     */     {
/* 146 */       return "NatPMP";
/*     */     }
/*     */     
/*     */ 
/*     */     public String getFriendlyName()
/*     */     {
/* 152 */       return "NatPMP";
/*     */     }
/*     */     
/*     */ 
/*     */     public String getManufacturer()
/*     */     {
/* 158 */       return "";
/*     */     }
/*     */     
/*     */ 
/*     */     public String getManufacturerURL()
/*     */     {
/* 164 */       return "";
/*     */     }
/*     */     
/*     */ 
/*     */     public String getModelDescription()
/*     */     {
/* 170 */       return "";
/*     */     }
/*     */     
/*     */ 
/*     */     public String getModelName()
/*     */     {
/* 176 */       return "";
/*     */     }
/*     */     
/*     */ 
/*     */     public String getModelNumber()
/*     */     {
/* 182 */       return "";
/*     */     }
/*     */     
/*     */ 
/*     */     public String getModelURL()
/*     */     {
/* 188 */       return "";
/*     */     }
/*     */     
/*     */ 
/*     */     public String getPresentation()
/*     */     {
/* 194 */       return "";
/*     */     }
/*     */     
/*     */ 
/*     */     public UPnPDevice[] getSubDevices()
/*     */     {
/* 200 */       return new UPnPDevice[0];
/*     */     }
/*     */     
/*     */ 
/*     */     public UPnPService[] getServices()
/*     */     {
/* 206 */       return NatPMPUPnPRootDeviceImpl.this.services;
/*     */     }
/*     */     
/*     */ 
/*     */     public UPnPRootDevice getRootDevice()
/*     */     {
/* 212 */       return NatPMPUPnPRootDeviceImpl.this;
/*     */     }
/*     */     
/*     */     public UPnPDeviceImage[] getImages() {
/* 216 */       return new UPnPDeviceImage[0];
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected class NatPMPUPnPWANConnection
/*     */     implements UPnPWANConnection, UPnPService
/*     */   {
/*     */     private NatPMPImpl nat_impl;
/*     */     
/*     */ 
/*     */     protected NatPMPUPnPWANConnection()
/*     */       throws UPnPException
/*     */     {
/* 231 */       this.nat_impl = new NatPMPImpl(NatPMPUPnPRootDeviceImpl.this.nat_device);
/*     */     }
/*     */     
/*     */ 
/*     */     public UPnPDevice getDevice()
/*     */     {
/* 237 */       return NatPMPUPnPRootDeviceImpl.this.device;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     public String getServiceType()
/*     */     {
/* 245 */       return "urn:schemas-upnp-org:service:WANIPConnection:1";
/*     */     }
/*     */     
/*     */ 
/*     */     public String getConnectionType()
/*     */     {
/* 251 */       return "IP";
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     public List<URL> getControlURLs()
/*     */       throws UPnPException
/*     */     {
/* 259 */       return new ArrayList(0);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     public void setPreferredControlURL(URL url) {}
/*     */     
/*     */ 
/*     */ 
/*     */     public boolean isConnectable()
/*     */     {
/* 271 */       return true;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     public UPnPAction[] getActions()
/*     */       throws UPnPException
/*     */     {
/* 279 */       return new UPnPAction[0];
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     public UPnPAction getAction(String name)
/*     */       throws UPnPException
/*     */     {
/* 288 */       return null;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     public UPnPStateVariable[] getStateVariables()
/*     */       throws UPnPException
/*     */     {
/* 296 */       return new UPnPStateVariable[0];
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     public UPnPStateVariable getStateVariable(String name)
/*     */       throws UPnPException
/*     */     {
/* 305 */       return null;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     public UPnPSpecificService getSpecificService()
/*     */     {
/* 315 */       return this;
/*     */     }
/*     */     
/*     */ 
/*     */     public UPnPService getGenericService()
/*     */     {
/* 321 */       return this;
/*     */     }
/*     */     
/*     */ 
/*     */     public boolean getDirectInvocations()
/*     */     {
/* 327 */       return true;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     public void setDirectInvocations(boolean force) {}
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     public void addPortMapping(final boolean tcp, final int port, final String description)
/*     */       throws UPnPException
/*     */     {
/* 345 */       NatPMPUPnPRootDeviceImpl.this.thread_pool.run(new AERunnable()
/*     */       {
/*     */ 
/*     */         public void runSupport()
/*     */         {
/*     */ 
/*     */           try
/*     */           {
/* 353 */             NatPMPUPnPRootDeviceImpl.NatPMPUPnPWANConnection.this.nat_impl.addPortMapping(tcp, port, description);
/*     */           }
/*     */           catch (UPnPException e)
/*     */           {
/* 357 */             e.printStackTrace();
/*     */           }
/*     */         }
/*     */       });
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     public UPnPWANConnectionPortMapping[] getPortMappings()
/*     */       throws UPnPException
/*     */     {
/* 368 */       return this.nat_impl.getPortMappings();
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     public void deletePortMapping(final boolean tcp, final int port)
/*     */       throws UPnPException
/*     */     {
/* 378 */       NatPMPUPnPRootDeviceImpl.this.thread_pool.run(new AERunnable()
/*     */       {
/*     */ 
/*     */         public void runSupport()
/*     */         {
/*     */           try
/*     */           {
/* 385 */             NatPMPUPnPRootDeviceImpl.NatPMPUPnPWANConnection.this.nat_impl.deletePortMapping(tcp, port);
/*     */           }
/*     */           catch (UPnPException e)
/*     */           {
/* 389 */             e.printStackTrace();
/*     */           }
/*     */         }
/*     */       });
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     public String[] getStatusInfo()
/*     */       throws UPnPException
/*     */     {
/* 400 */       return this.nat_impl.getStatusInfo();
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     public String getExternalIPAddress()
/*     */       throws UPnPException
/*     */     {
/* 408 */       return this.nat_impl.getExternalIPAddress();
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     public void periodicallyRecheckMappings(boolean on) {}
/*     */     
/*     */ 
/*     */ 
/*     */     public int getCapabilities()
/*     */     {
/* 420 */       return -1;
/*     */     }
/*     */     
/*     */     public void addListener(UPnPWANConnectionListener listener) {}
/*     */     
/*     */     public void removeListener(UPnPWANConnectionListener listener) {}
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/net/natpmp/upnp/impl/NatPMPUPnPRootDeviceImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */