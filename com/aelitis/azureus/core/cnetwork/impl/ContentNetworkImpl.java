/*     */ package com.aelitis.azureus.core.cnetwork.impl;
/*     */ 
/*     */ import com.aelitis.azureus.core.cnetwork.ContentNetwork;
/*     */ import com.aelitis.azureus.core.cnetwork.ContentNetworkPropertyChangeListener;
/*     */ import com.aelitis.azureus.core.util.CopyOnWriteList;
/*     */ import com.aelitis.azureus.core.vuzefile.VuzeFile;
/*     */ import com.aelitis.azureus.core.vuzefile.VuzeFileHandler;
/*     */ import com.aelitis.azureus.util.ImportExportUtils;
/*     */ import com.aelitis.azureus.util.MapUtils;
/*     */ import java.io.IOException;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.util.BEncoder;
/*     */ import org.gudy.azureus2.core3.util.Debug;
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
/*     */ public abstract class ContentNetworkImpl
/*     */   implements ContentNetwork
/*     */ {
/*     */   protected static final long TYPE_VUZE_GENERIC = 1L;
/*     */   private static final String PP_STARTUP_NETWORK = "startup_network";
/*     */   private ContentNetworkManagerImpl manager;
/*     */   private long type;
/*     */   private long version;
/*     */   private long id;
/*     */   private String name;
/*     */   private Map<String, Object> pprop_defaults;
/*     */   
/*     */   protected static ContentNetworkImpl importFromBEncodedMapStatic(ContentNetworkManagerImpl manager, Map map)
/*     */     throws IOException
/*     */   {
/*  53 */     long type = ImportExportUtils.importLong(map, "type");
/*     */     
/*  55 */     if (type == 1L)
/*     */     {
/*  57 */       return new ContentNetworkVuzeGeneric(manager, map);
/*     */     }
/*     */     
/*     */ 
/*  61 */     throw new IOException("Unsupported network type: " + type);
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
/*  73 */   private Map<Object, Object> transient_properties = Collections.synchronizedMap(new HashMap());
/*     */   
/*  75 */   private CopyOnWriteList persistent_listeners = new CopyOnWriteList();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected ContentNetworkImpl(ContentNetworkManagerImpl _manager, long _type, long _id, long _version, String _name, Map<String, Object> _pprop_defaults)
/*     */   {
/*  86 */     this.manager = _manager;
/*  87 */     this.type = _type;
/*  88 */     this.version = _version;
/*  89 */     this.id = _id;
/*  90 */     this.name = _name;
/*     */     
/*  92 */     this.pprop_defaults = _pprop_defaults;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected ContentNetworkImpl(ContentNetworkManagerImpl _manager)
/*     */   {
/*  99 */     this.manager = _manager;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void importFromBEncodedMap(Map<String, Object> map)
/*     */     throws IOException
/*     */   {
/* 108 */     this.type = ImportExportUtils.importLong(map, "type");
/* 109 */     this.id = ImportExportUtils.importLong(map, "id");
/* 110 */     this.version = ImportExportUtils.importLong(map, "version");
/* 111 */     this.name = ImportExportUtils.importString(map, "name");
/*     */     
/* 113 */     this.pprop_defaults = ((Map)map.get("pprop_defaults"));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void exportToBEncodedMap(Map<String, Object> map)
/*     */     throws IOException
/*     */   {
/* 122 */     ImportExportUtils.exportLong(map, "type", this.type);
/* 123 */     ImportExportUtils.exportLong(map, "id", this.id);
/* 124 */     ImportExportUtils.exportLong(map, "version", this.version);
/* 125 */     ImportExportUtils.exportString(map, "name", this.name);
/*     */     
/* 127 */     if (this.pprop_defaults != null)
/*     */     {
/* 129 */       map.put("pprop_defaults", this.pprop_defaults);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void updateFrom(ContentNetworkImpl other)
/*     */     throws IOException
/*     */   {
/* 139 */     Map<String, Object> map = new HashMap();
/*     */     
/* 141 */     other.exportToBEncodedMap(map);
/*     */     
/* 143 */     importFromBEncodedMap(map);
/*     */   }
/*     */   
/*     */ 
/*     */   public long getID()
/*     */   {
/* 149 */     return this.id;
/*     */   }
/*     */   
/*     */ 
/*     */   protected long getVersion()
/*     */   {
/* 155 */     return this.version;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getName()
/*     */   {
/* 161 */     return this.name;
/*     */   }
/*     */   
/*     */ 
/*     */   protected boolean isSameAs(ContentNetworkImpl other)
/*     */   {
/*     */     try
/*     */     {
/* 169 */       Map<String, Object> map1 = new HashMap();
/* 170 */       Map<String, Object> map2 = new HashMap();
/*     */       
/* 172 */       exportToBEncodedMap(map1);
/*     */       
/* 174 */       other.exportToBEncodedMap(map2);
/*     */       
/* 176 */       return BEncoder.mapsAreIdentical(map1, map2);
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/* 180 */       Debug.out(e);
/*     */     }
/* 182 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getXSearchService(String query, boolean to_subscribe)
/*     */   {
/* 191 */     return getServiceURL(2, new Object[] { query, Boolean.valueOf(to_subscribe) });
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getContentDetailsService(String hash, String client_ref)
/*     */   {
/* 199 */     return getServiceURL(11, new Object[] { hash, client_ref });
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public String getCommentService(String hash)
/*     */   {
/* 206 */     return getServiceURL(12, new Object[] { hash });
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getProfileService(String login_id, String client_ref)
/*     */   {
/* 214 */     return getServiceURL(13, new Object[] { login_id, client_ref });
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getTorrentDownloadService(String hash, String client_ref)
/*     */   {
/* 222 */     return getServiceURL(14, new Object[] { hash, client_ref });
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public String getFAQTopicService(String topic)
/*     */   {
/* 229 */     return getServiceURL(18, new Object[] { topic });
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public String getLoginService(String message)
/*     */   {
/* 236 */     return getServiceURL(22, new Object[] { message });
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getSiteRelativeURL(String relative_url, boolean append_suffix)
/*     */   {
/* 244 */     return getServiceURL(27, new Object[] { relative_url, Boolean.valueOf(append_suffix) });
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getExternalSiteRelativeURL(String relative_url, boolean append_suffix)
/*     */   {
/* 252 */     return getServiceURL(37, new Object[] { relative_url, Boolean.valueOf(append_suffix) });
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public String getAddFriendURL(String colour)
/*     */   {
/* 259 */     return getServiceURL(28, new Object[] { colour });
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public String getSubscriptionURL(String subs_id)
/*     */   {
/* 266 */     return getServiceURL(29, new Object[] { subs_id });
/*     */   }
/*     */   
/*     */ 
/*     */   public VuzeFile getVuzeFile()
/*     */   {
/* 272 */     VuzeFile vf = VuzeFileHandler.getSingleton().create();
/*     */     
/* 274 */     Map map = new HashMap();
/*     */     try
/*     */     {
/* 277 */       exportToBEncodedMap(map);
/*     */       
/* 279 */       vf.addComponent(128, map);
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/* 283 */       Debug.printStackTrace(e);
/*     */     }
/*     */     
/* 286 */     return vf;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isStartupNetwork()
/*     */   {
/* 292 */     if (hasPersistentProperty("startup_network"))
/*     */     {
/* 294 */       return ((Boolean)getPersistentProperty("startup_network")).booleanValue();
/*     */     }
/*     */     
/* 297 */     return ((Boolean)getPersistentProperty("is_cust")).booleanValue();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setStartupNetwork(boolean b)
/*     */   {
/* 304 */     setPersistentProperty("startup_network", Boolean.valueOf(b));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setTransientProperty(Object key, Object value)
/*     */   {
/* 312 */     this.transient_properties.put(key, value);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public Object getTransientProperty(Object key)
/*     */   {
/* 319 */     return this.transient_properties.get(key);
/*     */   }
/*     */   
/*     */ 
/*     */   protected String getPropertiesKey()
/*     */   {
/* 325 */     return "cnetwork.net." + this.id + ".props";
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setPersistentProperty(String name, Object new_value)
/*     */   {
/* 333 */     synchronized (this)
/*     */     {
/* 335 */       String key = getPropertiesKey();
/*     */       
/* 337 */       if ((new_value instanceof Boolean))
/*     */       {
/* 339 */         new_value = new Long(((Boolean)new_value).booleanValue() ? 1L : 0L);
/*     */       }
/*     */       
/* 342 */       Map props = new HashMap(COConfigurationManager.getMapParameter(key, new HashMap()));
/*     */       
/* 344 */       Object old_value = props.get(key);
/*     */       
/* 346 */       if (BEncoder.objectsAreIdentical(old_value, new_value))
/*     */       {
/* 348 */         return;
/*     */       }
/*     */       
/* 351 */       props.put(name, new_value);
/*     */       
/* 353 */       COConfigurationManager.setParameter(key, props);
/*     */     }
/*     */     
/* 356 */     Iterator it = this.persistent_listeners.iterator();
/*     */     
/* 358 */     while (it.hasNext())
/*     */     {
/*     */       try
/*     */       {
/* 362 */         ((ContentNetworkPropertyChangeListener)it.next()).propertyChanged(name);
/*     */       }
/*     */       catch (Throwable e)
/*     */       {
/* 366 */         Debug.printStackTrace(e);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public Object getPersistentProperty(String name)
/*     */   {
/* 375 */     synchronized (this)
/*     */     {
/* 377 */       String key = getPropertiesKey();
/*     */       
/* 379 */       Map props = COConfigurationManager.getMapParameter(key, new HashMap());
/*     */       
/* 381 */       if (name == "source_ref")
/*     */       {
/* 383 */         return MapUtils.getMapString(props, name, MapUtils.getMapString(this.pprop_defaults, name, null));
/*     */       }
/*     */       
/*     */ 
/* 387 */       Object obj = props.get(name);
/*     */       
/* 389 */       if ((name == "auth_shown") || (name == "is_cust") || (name == "active") || (name == "in_menu") || (name == "startup_network"))
/*     */       {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 395 */         if ((obj == null) && (this.pprop_defaults != null))
/*     */         {
/* 397 */           obj = this.pprop_defaults.get(name);
/*     */         }
/*     */         
/* 400 */         if (obj == null)
/*     */         {
/* 402 */           return Boolean.valueOf(false);
/*     */         }
/*     */         
/*     */ 
/* 406 */         return Boolean.valueOf(((Long)obj).longValue() == 1L);
/*     */       }
/*     */       
/*     */ 
/* 410 */       return obj;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected boolean hasPersistentProperty(String name)
/*     */   {
/* 418 */     synchronized (this)
/*     */     {
/* 420 */       String key = getPropertiesKey();
/*     */       
/* 422 */       Map props = COConfigurationManager.getMapParameter(key, new HashMap());
/*     */       
/* 424 */       return props.containsKey(name);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   protected Map<String, Object> getPersistentPropertyDefaults()
/*     */   {
/* 431 */     return this.pprop_defaults;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void addPersistentPropertyChangeListener(ContentNetworkPropertyChangeListener listener)
/*     */   {
/* 438 */     this.persistent_listeners.add(listener);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void removePersistentPropertyChangeListener(ContentNetworkPropertyChangeListener listener)
/*     */   {
/* 445 */     this.persistent_listeners.remove(listener);
/*     */   }
/*     */   
/*     */ 
/*     */   protected void destroy()
/*     */   {
/* 451 */     String key = getPropertiesKey();
/*     */     
/* 453 */     COConfigurationManager.setParameter(key, new HashMap());
/*     */   }
/*     */   
/*     */ 
/*     */   public void remove()
/*     */   {
/* 459 */     this.manager.removeNetwork(this);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void debug(String str)
/*     */   {
/* 466 */     Debug.out(getString() + ": " + str);
/*     */   }
/*     */   
/*     */ 
/*     */   protected String getString()
/*     */   {
/* 472 */     return getID() + " - " + getName() + ": version=" + getVersion() + ", site=" + getProperty(1);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/cnetwork/impl/ContentNetworkImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */