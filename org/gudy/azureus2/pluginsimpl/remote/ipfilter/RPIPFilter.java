/*     */ package org.gudy.azureus2.pluginsimpl.remote.ipfilter;
/*     */ 
/*     */ import java.io.File;
/*     */ import org.gudy.azureus2.plugins.ipfilter.IPBanned;
/*     */ import org.gudy.azureus2.plugins.ipfilter.IPBlocked;
/*     */ import org.gudy.azureus2.plugins.ipfilter.IPFilter;
/*     */ import org.gudy.azureus2.plugins.ipfilter.IPFilterException;
/*     */ import org.gudy.azureus2.plugins.ipfilter.IPRange;
/*     */ import org.gudy.azureus2.pluginsimpl.remote.RPException;
/*     */ import org.gudy.azureus2.pluginsimpl.remote.RPObject;
/*     */ import org.gudy.azureus2.pluginsimpl.remote.RPReply;
/*     */ import org.gudy.azureus2.pluginsimpl.remote.RPRequest;
/*     */ import org.gudy.azureus2.pluginsimpl.remote.RPRequestDispatcher;
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
/*     */ public class RPIPFilter
/*     */   extends RPObject
/*     */   implements IPFilter
/*     */ {
/*     */   protected transient IPFilter delegate;
/*     */   public long last_update_time;
/*     */   public int number_of_ranges;
/*     */   public int number_of_blocked_ips;
/*     */   
/*     */   public static IPFilter create(IPFilter _delegate)
/*     */   {
/*  55 */     RPIPFilter res = (RPIPFilter)_lookupLocal(_delegate);
/*     */     
/*  57 */     if (res == null)
/*     */     {
/*  59 */       res = new RPIPFilter(_delegate);
/*     */     }
/*     */     
/*  62 */     return res;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected RPIPFilter(IPFilter _delegate)
/*     */   {
/*  69 */     super(_delegate);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void _setDelegate(Object _delegate)
/*     */   {
/*  76 */     this.delegate = ((IPFilter)_delegate);
/*     */     
/*  78 */     this.last_update_time = this.delegate.getLastUpdateTime();
/*  79 */     this.number_of_ranges = this.delegate.getNumberOfRanges();
/*  80 */     this.number_of_blocked_ips = this.delegate.getNumberOfBlockedIPs();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public Object _setLocal()
/*     */     throws RPException
/*     */   {
/*  88 */     return _fixupLocal();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public RPReply _process(RPRequest request)
/*     */   {
/*  95 */     String method = request.getMethod();
/*     */     
/*  97 */     Object[] params = request.getParams();
/*     */     
/*  99 */     if (method.equals("createAndAddRange[String,String,String,boolean]"))
/*     */     {
/* 101 */       IPRange range = this.delegate.createAndAddRange((String)params[0], (String)params[1], (String)params[2], ((Boolean)params[3]).booleanValue());
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 107 */       if (range == null)
/*     */       {
/* 109 */         return new RPReply(null);
/*     */       }
/*     */       
/*     */ 
/* 113 */       RPIPRange rp_range = RPIPRange.create(range);
/*     */       
/* 115 */       return new RPReply(rp_range);
/*     */     }
/* 117 */     if (method.equals("getRanges"))
/*     */     {
/* 119 */       IPRange[] ranges = this.delegate.getRanges();
/*     */       
/* 121 */       RPIPRange[] rp_ranges = new RPIPRange[ranges.length];
/*     */       
/* 123 */       for (int i = 0; i < ranges.length; i++)
/*     */       {
/* 125 */         rp_ranges[i] = RPIPRange.create(ranges[i]);
/*     */       }
/*     */       
/* 128 */       return new RPReply(rp_ranges);
/*     */     }
/* 130 */     if (method.equals("save"))
/*     */       try
/*     */       {
/* 133 */         this.delegate.save();
/*     */         
/* 135 */         return null;
/*     */       }
/*     */       catch (IPFilterException e)
/*     */       {
/* 139 */         return new RPReply(e);
/*     */       }
/* 141 */     if (method.equals("getInRangeAddressesAreAllowed"))
/*     */     {
/* 143 */       return new RPReply(Boolean.valueOf(this.delegate.getInRangeAddressesAreAllowed()));
/*     */     }
/* 145 */     if (method.equals("setInRangeAddressesAreAllowed[boolean]"))
/*     */     {
/* 147 */       this.delegate.setInRangeAddressesAreAllowed(((Boolean)params[0]).booleanValue());
/*     */       
/* 149 */       return null;
/*     */     }
/* 151 */     if (method.equals("isEnabled"))
/*     */     {
/* 153 */       return new RPReply(Boolean.valueOf(this.delegate.isEnabled()));
/*     */     }
/* 155 */     if (method.equals("setEnabled[boolean]"))
/*     */     {
/* 157 */       this.delegate.setEnabled(((Boolean)params[0]).booleanValue());
/*     */       
/* 159 */       return null;
/*     */     }
/* 161 */     if (method.equals("isInRange[String]"))
/*     */     {
/* 163 */       return new RPReply(Boolean.valueOf(this.delegate.isInRange((String)params[0])));
/*     */     }
/*     */     
/* 166 */     throw new RPException("Unknown method: " + method);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public File getFile()
/*     */   {
/* 175 */     notSupported();
/*     */     
/* 177 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public IPRange createRange(boolean this_session_only)
/*     */   {
/* 185 */     notSupported();
/*     */     
/* 187 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void addRange(IPRange range)
/*     */   {
/* 194 */     notSupported();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public IPRange createAndAddRange(String description, String start_ip, String end_ip, boolean this_session_only)
/*     */   {
/* 204 */     RPIPRange resp = (RPIPRange)this._dispatcher.dispatch(new RPRequest(this, "createAndAddRange[String,String,String,boolean]", new Object[] { description, start_ip, end_ip, Boolean.valueOf(this_session_only) })).getResponse();
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 210 */     resp._setRemote(this._dispatcher);
/*     */     
/* 212 */     return resp;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void removeRange(IPRange range)
/*     */   {
/* 220 */     notSupported();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void reload()
/*     */     throws IPFilterException
/*     */   {
/* 228 */     notSupported();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public IPRange[] getRanges()
/*     */   {
/* 235 */     RPIPRange[] resp = (RPIPRange[])this._dispatcher.dispatch(new RPRequest(this, "getRanges", null)).getResponse();
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 241 */     for (int i = 0; i < resp.length; i++)
/*     */     {
/* 243 */       resp[i]._setRemote(this._dispatcher);
/*     */     }
/*     */     
/* 246 */     return resp;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean isInRange(String IPAddress)
/*     */   {
/* 253 */     Boolean res = (Boolean)this._dispatcher.dispatch(new RPRequest(this, "isInRange[String]", new Object[] { IPAddress })).getResponse();
/*     */     
/* 255 */     return res.booleanValue();
/*     */   }
/*     */   
/*     */ 
/*     */   public IPBlocked[] getBlockedIPs()
/*     */   {
/* 261 */     notSupported();
/*     */     
/* 263 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void block(String IPAddress)
/*     */   {
/* 270 */     notSupported();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public IPBanned[] getBannedIPs()
/*     */   {
/* 277 */     notSupported();
/*     */     
/* 279 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void ban(String IPAddress, String text)
/*     */   {
/* 287 */     notSupported();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void unban(String IPAddress)
/*     */   {
/* 294 */     notSupported();
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean getInRangeAddressesAreAllowed()
/*     */   {
/* 300 */     Boolean res = (Boolean)this._dispatcher.dispatch(new RPRequest(this, "getInRangeAddressesAreAllowed", null)).getResponse();
/*     */     
/* 302 */     return res.booleanValue();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setInRangeAddressesAreAllowed(boolean value)
/*     */   {
/* 309 */     this._dispatcher.dispatch(new RPRequest(this, "setInRangeAddressesAreAllowed[boolean]", new Object[] { Boolean.valueOf(value) })).getResponse();
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isEnabled()
/*     */   {
/* 315 */     Boolean res = (Boolean)this._dispatcher.dispatch(new RPRequest(this, "isEnabled", null)).getResponse();
/*     */     
/* 317 */     return res.booleanValue();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setEnabled(boolean value)
/*     */   {
/* 324 */     this._dispatcher.dispatch(new RPRequest(this, "setEnabled[boolean]", new Object[] { Boolean.valueOf(value) })).getResponse();
/*     */   }
/*     */   
/*     */ 
/*     */   public void save()
/*     */     throws IPFilterException
/*     */   {
/*     */     try
/*     */     {
/* 333 */       this._dispatcher.dispatch(new RPRequest(this, "save", null)).getResponse();
/*     */     }
/*     */     catch (RPException e)
/*     */     {
/* 337 */       if ((e.getCause() instanceof IPFilterException))
/*     */       {
/* 339 */         throw ((IPFilterException)e.getCause());
/*     */       }
/*     */       
/* 342 */       throw e;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public void markAsUpToDate()
/*     */   {
/* 349 */     notSupported();
/*     */   }
/*     */   
/*     */ 
/*     */   public long getLastUpdateTime()
/*     */   {
/* 355 */     return this.last_update_time;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getNumberOfRanges()
/*     */   {
/* 361 */     return this.number_of_ranges;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getNumberOfBlockedIPs()
/*     */   {
/* 367 */     return this.number_of_blocked_ips;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getNumberOfBannedIPs()
/*     */   {
/* 373 */     notSupported();
/*     */     
/* 375 */     return -1;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/pluginsimpl/remote/ipfilter/RPIPFilter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */