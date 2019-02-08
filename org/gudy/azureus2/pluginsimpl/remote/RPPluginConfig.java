/*     */ package org.gudy.azureus2.pluginsimpl.remote;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Properties;
/*     */ import org.gudy.azureus2.plugins.PluginConfig;
/*     */ import org.gudy.azureus2.plugins.PluginConfigListener;
/*     */ import org.gudy.azureus2.plugins.PluginException;
/*     */ import org.gudy.azureus2.plugins.config.ConfigParameter;
/*     */ import org.gudy.azureus2.plugins.config.PluginConfigSource;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class RPPluginConfig
/*     */   extends RPObject
/*     */   implements PluginConfig
/*     */ {
/*     */   protected transient PluginConfig delegate;
/*     */   protected transient Properties property_cache;
/*     */   public String[] cached_property_names;
/*     */   public Object[] cached_property_values;
/*     */   
/*     */   public static PluginConfig create(PluginConfig _delegate)
/*     */   {
/*  57 */     RPPluginConfig res = (RPPluginConfig)_lookupLocal(_delegate);
/*     */     
/*  59 */     if (res == null)
/*     */     {
/*  61 */       res = new RPPluginConfig(_delegate);
/*     */     }
/*     */     
/*  64 */     return res;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected RPPluginConfig(PluginConfig _delegate)
/*     */   {
/*  71 */     super(_delegate);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void _setDelegate(Object _delegate)
/*     */   {
/*  78 */     this.delegate = ((PluginConfig)_delegate);
/*     */     
/*  80 */     this.cached_property_names = new String[] { "Max Upload Speed KBs", "Max Upload Speed When Only Seeding KBs", "Max Download Speed KBs", "Max Connections Per Torrent", "Max Connections Global", "Max Downloads", "Max Active Torrents", "Max Active Torrents When Only Seeding", "Max Uploads", "Max Uploads Seeding" };
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
/*  93 */     this.cached_property_values = new Object[] { new Integer(this.delegate.getIntParameter(this.cached_property_names[0])), new Integer(this.delegate.getIntParameter(this.cached_property_names[1])), new Integer(this.delegate.getIntParameter(this.cached_property_names[2])), new Integer(this.delegate.getIntParameter(this.cached_property_names[3])), new Integer(this.delegate.getIntParameter(this.cached_property_names[4])), new Integer(this.delegate.getIntParameter(this.cached_property_names[5])), new Integer(this.delegate.getIntParameter(this.cached_property_names[6])), new Integer(this.delegate.getIntParameter(this.cached_property_names[7])), new Integer(this.delegate.getIntParameter(this.cached_property_names[8])), new Integer(this.delegate.getIntParameter(this.cached_property_names[9])) };
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Object _setLocal()
/*     */     throws RPException
/*     */   {
/* 112 */     return _fixupLocal();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void _setRemote(RPRequestDispatcher _dispatcher)
/*     */   {
/* 119 */     super._setRemote(_dispatcher);
/*     */     
/* 121 */     this.property_cache = new Properties();
/*     */     
/* 123 */     for (int i = 0; i < this.cached_property_names.length; i++)
/*     */     {
/*     */ 
/*     */ 
/* 127 */       this.property_cache.put(this.cached_property_names[i], this.cached_property_values[i]);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public RPReply _process(RPRequest request)
/*     */   {
/* 135 */     String method = request.getMethod();
/*     */     
/* 137 */     Object[] params = (Object[])request.getParams();
/*     */     
/* 139 */     if (method.equals("getPluginIntParameter[String,int]"))
/*     */     {
/* 141 */       return new RPReply(new Integer(this.delegate.getPluginIntParameter((String)params[0], ((Integer)params[1]).intValue())));
/*     */     }
/* 143 */     if (method.equals("getPluginStringParameter[String,String]"))
/*     */     {
/* 145 */       return new RPReply(this.delegate.getPluginStringParameter((String)params[0], (String)params[1]));
/*     */     }
/* 147 */     if (method.equals("setPluginParameter[String,int]"))
/*     */     {
/* 149 */       this.delegate.setPluginParameter((String)params[0], ((Integer)params[1]).intValue());
/*     */       
/* 151 */       return null;
/*     */     }
/* 153 */     if ((method.equals("getIntParameter[String,int]")) || (method.equals("getParameter[String,int]")))
/*     */     {
/*     */ 
/* 156 */       return new RPReply(new Integer(this.delegate.getIntParameter((String)params[0], ((Integer)params[1]).intValue())));
/*     */     }
/* 158 */     if (method.equals("setParameter[String,int]"))
/*     */     {
/* 160 */       this.delegate.setIntParameter((String)params[0], ((Integer)params[1]).intValue());
/*     */       
/* 162 */       return null;
/*     */     }
/* 164 */     if (method.equals("save")) {
/*     */       try
/*     */       {
/* 167 */         this.delegate.save();
/*     */         
/* 169 */         return null;
/*     */       }
/*     */       catch (PluginException e)
/*     */       {
/* 173 */         return new RPReply(e);
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 178 */     throw new RPException("Unknown method: " + method);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean isNewInstall()
/*     */   {
/* 186 */     notSupported();
/*     */     
/* 188 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getPluginConfigKeyPrefix()
/*     */   {
/* 194 */     notSupported();
/*     */     
/* 196 */     return null;
/*     */   }
/*     */   
/*     */   public float getFloatParameter(String key) {
/* 200 */     notSupported();
/*     */     
/* 202 */     return 0.0F;
/*     */   }
/*     */   
/*     */   public int getIntParameter(String key)
/*     */   {
/* 207 */     notSupported();
/*     */     
/* 209 */     return 0;
/*     */   }
/*     */   
/*     */   public int getIntParameter(String key, int default_value)
/*     */   {
/* 214 */     Integer res = (Integer)this.property_cache.get(key);
/*     */     
/* 216 */     if (res == null)
/*     */     {
/* 218 */       res = (Integer)this._dispatcher.dispatch(new RPRequest(this, "getIntParameter[String,int]", new Object[] { key, new Integer(default_value) })).getResponse();
/*     */     }
/*     */     
/* 221 */     return res.intValue();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setIntParameter(String key, int value)
/*     */   {
/* 229 */     this.property_cache.put(key, new Integer(value));
/*     */     
/* 231 */     this._dispatcher.dispatch(new RPRequest(this, "setParameter[String,int]", new Object[] { key, new Integer(value) })).getResponse();
/*     */   }
/*     */   
/*     */   public String getStringParameter(String key)
/*     */   {
/* 236 */     notSupported();
/*     */     
/* 238 */     return null;
/*     */   }
/*     */   
/*     */   public String getStringParameter(String name, String _default)
/*     */   {
/* 243 */     notSupported();
/*     */     
/* 245 */     return null;
/*     */   }
/*     */   
/*     */   public boolean getBooleanParameter(String key)
/*     */   {
/* 250 */     notSupported();
/*     */     
/* 252 */     return false;
/*     */   }
/*     */   
/*     */   public boolean getBooleanParameter(String key, boolean _default)
/*     */   {
/* 257 */     notSupported();
/*     */     
/* 259 */     return false;
/*     */   }
/*     */   
/*     */   public void setBooleanParameter(String key, boolean value)
/*     */   {
/* 264 */     notSupported();
/*     */   }
/*     */   
/*     */   public byte[] getByteParameter(String name, byte[] _default)
/*     */   {
/* 269 */     notSupported();
/*     */     
/* 271 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */   public List getPluginListParameter(String key, List default_value)
/*     */   {
/* 277 */     notSupported();
/*     */     
/* 279 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */   public void setPluginListParameter(String key, List value)
/*     */   {
/* 285 */     notSupported();
/*     */   }
/*     */   
/*     */ 
/*     */   public Map getPluginMapParameter(String key, Map default_value)
/*     */   {
/* 291 */     notSupported();
/*     */     
/* 293 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */   public void setPluginMapParameter(String key, Map value)
/*     */   {
/* 299 */     notSupported();
/*     */   }
/*     */   
/*     */   public int getPluginIntParameter(String key) {
/* 303 */     notSupported();
/*     */     
/* 305 */     return 0;
/*     */   }
/*     */   
/*     */   public int getPluginIntParameter(String key, int defaultValue)
/*     */   {
/* 310 */     Integer res = (Integer)this._dispatcher.dispatch(new RPRequest(this, "getPluginIntParameter[String,int]", new Object[] { key, new Integer(defaultValue) })).getResponse();
/*     */     
/* 312 */     return res.intValue();
/*     */   }
/*     */   
/*     */   public String getPluginStringParameter(String key)
/*     */   {
/* 317 */     notSupported();
/*     */     
/* 319 */     return null;
/*     */   }
/*     */   
/*     */   public String getPluginStringParameter(String key, String defaultValue)
/*     */   {
/* 324 */     String res = (String)this._dispatcher.dispatch(new RPRequest(this, "getPluginStringParameter[String,String]", new Object[] { key, defaultValue })).getResponse();
/*     */     
/* 326 */     return res;
/*     */   }
/*     */   
/*     */   public boolean getPluginBooleanParameter(String key)
/*     */   {
/* 331 */     notSupported();
/*     */     
/* 333 */     return false;
/*     */   }
/*     */   
/*     */   public byte[] getPluginByteParameter(String key, byte[] defaultValue)
/*     */   {
/* 338 */     notSupported();
/*     */     
/* 340 */     return null;
/*     */   }
/*     */   
/*     */   public boolean getPluginBooleanParameter(String key, boolean defaultValue)
/*     */   {
/* 345 */     notSupported();
/*     */     
/* 347 */     return false;
/*     */   }
/*     */   
/*     */   public void setPluginParameter(String key, int value)
/*     */   {
/* 352 */     this._dispatcher.dispatch(new RPRequest(this, "setPluginParameter[String,int]", new Object[] { key, new Integer(value) }));
/*     */   }
/*     */   
/*     */   public void setPluginParameter(String key, int value, boolean global)
/*     */   {
/* 357 */     notSupported();
/*     */   }
/*     */   
/*     */ 
/*     */   public void setPluginParameter(String key, String value)
/*     */   {
/* 363 */     notSupported();
/*     */   }
/*     */   
/*     */   public void setPluginParameter(String key, boolean value)
/*     */   {
/* 368 */     notSupported();
/*     */   }
/*     */   
/*     */   public void setPluginParameter(String key, byte[] value)
/*     */   {
/* 373 */     notSupported();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public ConfigParameter getParameter(String key)
/*     */   {
/* 380 */     notSupported();
/*     */     
/* 382 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public ConfigParameter getPluginParameter(String key)
/*     */   {
/* 389 */     notSupported();
/*     */     
/* 391 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean getUnsafeBooleanParameter(String key, boolean default_value)
/*     */   {
/* 399 */     notSupported();
/*     */     
/* 401 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setUnsafeBooleanParameter(String key, boolean value)
/*     */   {
/* 409 */     notSupported();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public int getUnsafeIntParameter(String key, int default_value)
/*     */   {
/* 417 */     notSupported();
/*     */     
/* 419 */     return 0;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setUnsafeIntParameter(String key, int value)
/*     */   {
/* 427 */     notSupported();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public long getUnsafeLongParameter(String key, long default_value)
/*     */   {
/* 435 */     notSupported();
/*     */     
/* 437 */     return 0L;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setUnsafeLongParameter(String key, long value)
/*     */   {
/* 445 */     notSupported();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public float getUnsafeFloatParameter(String key, float default_value)
/*     */   {
/* 453 */     notSupported();
/*     */     
/* 455 */     return 0.0F;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setUnsafeFloatParameter(String key, float value)
/*     */   {
/* 463 */     notSupported();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getUnsafeStringParameter(String key, String default_value)
/*     */   {
/* 471 */     notSupported();
/*     */     
/* 473 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setUnsafeStringParameter(String key, String value)
/*     */   {
/* 481 */     notSupported();
/*     */   }
/*     */   
/*     */ 
/*     */   public Map getUnsafeParameterList()
/*     */   {
/* 487 */     notSupported();
/*     */     
/* 489 */     return null;
/*     */   }
/*     */   
/*     */   public void save()
/*     */     throws PluginException
/*     */   {
/*     */     try
/*     */     {
/* 497 */       this._dispatcher.dispatch(new RPRequest(this, "save", null)).getResponse();
/*     */     }
/*     */     catch (RPException e)
/*     */     {
/* 501 */       Throwable cause = e.getCause();
/*     */       
/* 503 */       if ((cause instanceof PluginException))
/*     */       {
/* 505 */         throw ((PluginException)cause);
/*     */       }
/*     */       
/* 508 */       throw e;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public File getPluginUserFile(String name)
/*     */   {
/* 516 */     notSupported();
/*     */     
/* 518 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void addListener(PluginConfigListener l)
/*     */   {
/* 525 */     notSupported();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setPluginConfigKeyPrefix(String _key) {}
/*     */   
/*     */ 
/*     */   public boolean hasParameter(String x)
/*     */   {
/* 535 */     notSupported();return false; }
/* 536 */   public boolean hasPluginParameter(String x) { notSupported();return false; }
/* 537 */   public boolean removePluginParameter(String x) { notSupported();return false; }
/* 538 */   public boolean removePluginColorParameter(String x) { notSupported();return false; }
/*     */   
/* 540 */   public byte[] getByteParameter(String key) { notSupported();return null; }
/* 541 */   public float getFloatParameter(String key, float default_value) { notSupported();return 0.0F; }
/* 542 */   public long getLongParameter(String key) { notSupported();return 0L; }
/* 543 */   public long getLongParameter(String key, long default_value) { notSupported();return 0L; }
/* 544 */   public void setByteParameter(String key, byte[] value) { notSupported(); }
/* 545 */   public void setFloatParameter(String key, float value) { notSupported(); }
/* 546 */   public void setLongParameter(String key, long value) { notSupported(); }
/* 547 */   public void setStringParameter(String key, String value) { notSupported(); }
/* 548 */   public byte[] getPluginByteParameter(String key) { notSupported();return null; }
/* 549 */   public float getPluginFloatParameter(String key) { notSupported();return 0.0F; }
/* 550 */   public float getPluginFloatParameter(String key, float default_value) { notSupported();return 0.0F; }
/* 551 */   public long getPluginLongParameter(String key) { notSupported();return 0L; }
/* 552 */   public long getPluginLongParameter(String key, long default_value) { notSupported();return 0L; }
/* 553 */   public void setPluginParameter(String key, float value) { notSupported(); }
/* 554 */   public void setPluginParameter(String key, long value) { notSupported(); }
/* 555 */   public boolean getUnsafeBooleanParameter(String key) { notSupported();return false; }
/* 556 */   public byte[] getUnsafeByteParameter(String key) { notSupported();return null; }
/* 557 */   public byte[] getUnsafeByteParameter(String key, byte[] default_value) { notSupported();return null; }
/* 558 */   public float getUnsafeFloatParameter(String key) { notSupported();return 0.0F; }
/* 559 */   public int getUnsafeIntParameter(String key) { notSupported();return 0; }
/* 560 */   public long getUnsafeLongParameter(String key) { notSupported();return 0L; }
/* 561 */   public String getUnsafeStringParameter(String key) { notSupported();return null; }
/* 562 */   public void setUnsafeByteParameter(String key, byte[] value) { notSupported(); }
/*     */   
/* 564 */   public boolean getCoreBooleanParameter(String key) { notSupported();return false; }
/* 565 */   public boolean getCoreBooleanParameter(String key, boolean default_value) { notSupported();return false; }
/* 566 */   public byte[] getCoreByteParameter(String key, byte[] default_value) { notSupported();return null; }
/* 567 */   public byte[] getCoreByteParameter(String key) { notSupported();return null; }
/* 568 */   public float getCoreFloatParameter(String key) { notSupported();return 0.0F; }
/* 569 */   public float getCoreFloatParameter(String key, float default_value) { notSupported();return 0.0F; }
/* 570 */   public int getCoreIntParameter(String key) { notSupported();return 0; }
/* 571 */   public int getCoreIntParameter(String key, int default_value) { notSupported();return 0; }
/* 572 */   public String getCoreStringParameter(String key) { notSupported();return null; }
/* 573 */   public String getCoreStringParameter(String key, String default_value) { notSupported();return null; }
/* 574 */   public long getCoreLongParameter(String key) { notSupported();return 0L; }
/* 575 */   public long getCoreLongParameter(String key, long default_value) { notSupported();return 0L; }
/* 576 */   public void setCoreBooleanParameter(String key, boolean value) { notSupported(); }
/* 577 */   public void setCoreByteParameter(String key, byte[] value) { notSupported(); }
/* 578 */   public void setCoreFloatParameter(String key, float value) { notSupported(); }
/* 579 */   public void setCoreIntParameter(String key, int value) { notSupported(); }
/* 580 */   public void setCoreLongParameter(String key, long value) { notSupported(); }
/* 581 */   public void setCoreStringParameter(String key, String value) { notSupported(); }
/*     */   
/* 583 */   public int[] getCoreColorParameter(String key) { notSupported();return null; }
/* 584 */   public int[] getCoreColorParameter(String key, int[] default_value) { notSupported();return null; }
/* 585 */   public void setCoreColorParameter(String key, int[] value) { notSupported(); }
/* 586 */   public void setCoreColorParameter(String key, int[] value, boolean override) { notSupported(); }
/* 587 */   public int[] getPluginColorParameter(String key) { notSupported();return null; }
/* 588 */   public int[] getPluginColorParameter(String key, int[] default_value) { notSupported();return null; }
/* 589 */   public int[] getPluginColorParameter(String key, int[] default_value, boolean override) { notSupported();return null; }
/* 590 */   public void setPluginColorParameter(String key, int[] value) { notSupported(); }
/* 591 */   public void setPluginColorParameter(String key, int[] value, boolean override) { notSupported(); }
/* 592 */   public int[] getUnsafeColorParameter(String key) { notSupported();return null; }
/* 593 */   public int[] getUnsafeColorParameter(String key, int[] default_value) { notSupported();return null; }
/* 594 */   public void setUnsafeColorParameter(String key, int[] default_value) { notSupported(); }
/* 595 */   public void setUnsafeColorParameter(String key, int[] default_value, boolean override) { notSupported(); }
/* 596 */   public PluginConfigSource getPluginConfigSource() { notSupported();return null; }
/* 597 */   public void setPluginConfigSource(PluginConfigSource source) { notSupported(); }
/* 598 */   public PluginConfigSource enableExternalConfigSource() { notSupported();return null; }
/*     */   
/* 600 */   public void setPluginStringListParameter(String key, String[] value) { notSupported(); }
/* 601 */   public String[] getPluginStringListParameter(String key) { notSupported();return null;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/pluginsimpl/remote/RPPluginConfig.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */