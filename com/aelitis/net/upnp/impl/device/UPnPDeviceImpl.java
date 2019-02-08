/*     */ package com.aelitis.net.upnp.impl.device;
/*     */ 
/*     */ import com.aelitis.net.upnp.UPnP;
/*     */ import com.aelitis.net.upnp.UPnPDevice;
/*     */ import com.aelitis.net.upnp.UPnPDeviceImage;
/*     */ import com.aelitis.net.upnp.UPnPService;
/*     */ import com.aelitis.net.upnp.impl.UPnPImpl;
/*     */ import com.aelitis.net.upnp.impl.services.UPnPServiceImpl;
/*     */ import java.net.InetAddress;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ public class UPnPDeviceImpl
/*     */   implements UPnPDevice
/*     */ {
/*     */   private UPnPRootDeviceImpl root_device;
/*     */   private String device_type;
/*     */   private String friendly_name;
/*     */   private String manufacturer;
/*     */   private String manufacturer_url;
/*     */   private String model_description;
/*     */   private String model_name;
/*     */   private String model_number;
/*     */   private String model_url;
/*     */   private String presentation_url;
/*  54 */   private List devices = new ArrayList();
/*  55 */   private List services = new ArrayList();
/*  56 */   private List<UPnPDeviceImage> images = new ArrayList();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected UPnPDeviceImpl(UPnPRootDeviceImpl _root_device, String indent, SimpleXMLParserDocumentNode device_node)
/*     */   {
/*  64 */     this.root_device = _root_device;
/*     */     
/*  66 */     this.device_type = getMandatoryField(device_node, "DeviceType");
/*  67 */     this.friendly_name = getOptionalField(device_node, "FriendlyName");
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  75 */     this.manufacturer = getOptionalField(device_node, "manufacturer");
/*  76 */     this.manufacturer_url = getOptionalField(device_node, "manufacturerURL");
/*  77 */     this.model_description = getOptionalField(device_node, "modelDescription");
/*  78 */     this.model_name = getOptionalField(device_node, "modelName");
/*  79 */     this.model_number = getOptionalField(device_node, "modelNumber");
/*  80 */     this.model_url = getOptionalField(device_node, "modelURL");
/*  81 */     this.presentation_url = getOptionalField(device_node, "presentationURL");
/*     */     
/*  83 */     if (this.friendly_name == null)
/*     */     {
/*     */ 
/*     */ 
/*  87 */       String[] bits = { this.manufacturer, this.model_description, this.model_number };
/*     */       
/*  89 */       this.friendly_name = "";
/*     */       
/*  91 */       for (String bit : bits)
/*     */       {
/*  93 */         if (bit != null)
/*     */         {
/*  95 */           this.friendly_name = (this.friendly_name + (this.friendly_name.length() == 0 ? "" : "/") + bit);
/*     */         }
/*     */       }
/*     */       
/*  99 */       if (this.friendly_name.length() == 0)
/*     */       {
/* 101 */         this.friendly_name = "UPnP Device";
/*     */       }
/*     */     }
/*     */     
/* 105 */     boolean interested = this.device_type.equalsIgnoreCase("urn:schemas-upnp-org:device:WANConnectionDevice:1");
/*     */     
/* 107 */     this.root_device.getUPnP().log(indent + this.friendly_name + (interested ? " *" : ""));
/*     */     
/* 109 */     SimpleXMLParserDocumentNode service_list = device_node.getChild("ServiceList");
/*     */     
/* 111 */     if (service_list != null)
/*     */     {
/* 113 */       SimpleXMLParserDocumentNode[] service_nodes = service_list.getChildren();
/*     */       
/* 115 */       for (int i = 0; i < service_nodes.length; i++)
/*     */       {
/* 117 */         this.services.add(new UPnPServiceImpl(this, indent + "  ", service_nodes[i]));
/*     */       }
/*     */     }
/*     */     
/* 121 */     SimpleXMLParserDocumentNode dev_list = device_node.getChild("DeviceList");
/*     */     
/* 123 */     if (dev_list != null)
/*     */     {
/* 125 */       SimpleXMLParserDocumentNode[] device_nodes = dev_list.getChildren();
/*     */       
/* 127 */       for (int i = 0; i < device_nodes.length; i++)
/*     */       {
/* 129 */         this.devices.add(new UPnPDeviceImpl(this.root_device, indent + "  ", device_nodes[i]));
/*     */       }
/*     */     }
/*     */     
/* 133 */     SimpleXMLParserDocumentNode icon_list = device_node.getChild("iconList");
/* 134 */     if (icon_list != null) {
/* 135 */       SimpleXMLParserDocumentNode[] children = icon_list.getChildren();
/*     */       
/* 137 */       for (SimpleXMLParserDocumentNode child : children) {
/* 138 */         if ("icon".equalsIgnoreCase(child.getName()))
/*     */         {
/*     */ 
/*     */ 
/* 142 */           String oUrl = getOptionalField(child, "url");
/* 143 */           if (oUrl != null)
/*     */           {
/*     */ 
/*     */ 
/* 147 */             int width = -1;
/* 148 */             int height = -1;
/* 149 */             String oWidth = getOptionalField(child, "width");
/* 150 */             String oHeight = getOptionalField(child, "height");
/*     */             try {
/* 152 */               width = Integer.parseInt(oWidth);
/* 153 */               height = Integer.parseInt(oHeight);
/*     */             }
/*     */             catch (Throwable t) {}
/*     */             
/* 157 */             this.images.add(new UPnPDeviceImageImpl(width, height, oUrl, getOptionalField(child, "mime")));
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public String getAbsoluteURL(String url)
/*     */   {
/* 167 */     return this.root_device.getAbsoluteURL(url);
/*     */   }
/*     */   
/*     */ 
/*     */   public InetAddress getLocalAddress()
/*     */   {
/* 173 */     return this.root_device.getLocalAddress();
/*     */   }
/*     */   
/*     */ 
/*     */   public synchronized void clearRelativeBaseURL()
/*     */   {
/* 179 */     this.root_device.clearRelativeBaseURL();
/*     */   }
/*     */   
/*     */ 
/*     */   public synchronized void restoreRelativeBaseURL()
/*     */   {
/* 185 */     this.root_device.restoreRelativeBaseURL();
/*     */   }
/*     */   
/*     */ 
/*     */   public UPnPImpl getUPnP()
/*     */   {
/* 191 */     return (UPnPImpl)this.root_device.getUPnP();
/*     */   }
/*     */   
/*     */ 
/*     */   public UPnPRootDeviceImpl getRootDevice()
/*     */   {
/* 197 */     return this.root_device;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getDeviceType()
/*     */   {
/* 203 */     return this.device_type;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getFriendlyName()
/*     */   {
/* 209 */     return this.friendly_name;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getManufacturer()
/*     */   {
/* 215 */     return this.manufacturer;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getManufacturerURL()
/*     */   {
/* 221 */     return this.manufacturer_url;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getModelDescription()
/*     */   {
/* 227 */     return this.model_description;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getModelName()
/*     */   {
/* 233 */     return this.model_name;
/*     */   }
/*     */   
/*     */   public String getModelNumber()
/*     */   {
/* 238 */     return this.model_number;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getModelURL()
/*     */   {
/* 244 */     return this.model_url;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getPresentation()
/*     */   {
/* 250 */     return this.presentation_url == null ? null : getAbsoluteURL(this.presentation_url);
/*     */   }
/*     */   
/*     */ 
/*     */   public UPnPDevice[] getSubDevices()
/*     */   {
/* 256 */     UPnPDevice[] res = new UPnPDevice[this.devices.size()];
/*     */     
/* 258 */     this.devices.toArray(res);
/*     */     
/* 260 */     return res;
/*     */   }
/*     */   
/*     */ 
/*     */   public UPnPService[] getServices()
/*     */   {
/* 266 */     UPnPService[] res = new UPnPService[this.services.size()];
/*     */     
/* 268 */     this.services.toArray(res);
/*     */     
/* 270 */     return res;
/*     */   }
/*     */   
/*     */ 
/*     */   public UPnPDeviceImage[] getImages()
/*     */   {
/* 276 */     return (UPnPDeviceImage[])this.images.toArray(new UPnPDeviceImage[0]);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected String getOptionalField(SimpleXMLParserDocumentNode node, String name)
/*     */   {
/* 284 */     SimpleXMLParserDocumentNode child = node.getChild(name);
/*     */     
/* 286 */     if (child == null)
/*     */     {
/* 288 */       return null;
/*     */     }
/*     */     
/* 291 */     return child.getValue().trim();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected String getMandatoryField(SimpleXMLParserDocumentNode node, String name)
/*     */   {
/* 299 */     SimpleXMLParserDocumentNode child = node.getChild(name);
/*     */     
/* 301 */     if (child == null)
/*     */     {
/* 303 */       this.root_device.getUPnP().log("Mandatory field '" + name + "' is missing");
/*     */       
/* 305 */       return "<missing field '" + name + "'>";
/*     */     }
/*     */     
/* 308 */     return child.getValue().trim();
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/net/upnp/impl/device/UPnPDeviceImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */