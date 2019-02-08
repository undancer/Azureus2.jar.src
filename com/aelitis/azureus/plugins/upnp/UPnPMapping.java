/*     */ package com.aelitis.azureus.plugins.upnp;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
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
/*     */ public class UPnPMapping
/*     */ {
/*     */   public static final int PT_DEFAULT = 1;
/*     */   public static final int PT_PERSISTENT = 2;
/*     */   public static final int PT_TRANSIENT = 3;
/*     */   protected String resource_name;
/*     */   protected boolean tcp;
/*     */   protected int port;
/*     */   protected boolean enabled;
/*  43 */   protected int persistent = 1;
/*     */   
/*  45 */   protected List listeners = new ArrayList();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected UPnPMapping(String _resource_name, boolean _tcp, int _port, boolean _enabled)
/*     */   {
/*  54 */     this.resource_name = _resource_name;
/*  55 */     this.tcp = _tcp;
/*  56 */     this.port = _port;
/*  57 */     this.enabled = _enabled;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setPersistent(int _persistent)
/*     */   {
/*  64 */     this.persistent = _persistent;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getPersistent()
/*     */   {
/*  70 */     return this.persistent;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isTCP()
/*     */   {
/*  76 */     return this.tcp;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getPort()
/*     */   {
/*  82 */     return this.port;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setPort(int _port)
/*     */   {
/*  89 */     if (this.port != _port)
/*     */     {
/*  91 */       this.port = _port;
/*     */       
/*  93 */       changed();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isEnabled()
/*     */   {
/* 100 */     return this.enabled;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setEnabled(boolean _enabled)
/*     */   {
/* 107 */     if (this.enabled != _enabled)
/*     */     {
/* 109 */       this.enabled = _enabled;
/*     */       
/* 111 */       changed();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public String getString()
/*     */   {
/* 118 */     return getString(getPort());
/*     */   }
/*     */   
/*     */ 
/*     */   public String getString(int port)
/*     */   {
/*     */     String name;
/*     */     
/*     */     String name;
/* 127 */     if (MessageText.keyExists(this.resource_name))
/*     */     {
/* 129 */       name = MessageText.getString(this.resource_name);
/*     */     }
/*     */     else
/*     */     {
/* 133 */       name = this.resource_name;
/*     */     }
/*     */     
/* 136 */     return name + " (" + (isTCP() ? "TCP" : "UDP") + "/" + port + ")";
/*     */   }
/*     */   
/*     */ 
/*     */   public void destroy()
/*     */   {
/* 142 */     for (int i = 0; i < this.listeners.size(); i++)
/*     */     {
/* 144 */       ((UPnPMappingListener)this.listeners.get(i)).mappingDestroyed(this);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   protected void changed()
/*     */   {
/* 151 */     for (int i = 0; i < this.listeners.size(); i++)
/*     */     {
/* 153 */       ((UPnPMappingListener)this.listeners.get(i)).mappingChanged(this);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void addListener(UPnPMappingListener l)
/*     */   {
/* 161 */     this.listeners.add(l);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void removeListener(UPnPMappingListener l)
/*     */   {
/* 168 */     this.listeners.remove(l);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/plugins/upnp/UPnPMapping.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */