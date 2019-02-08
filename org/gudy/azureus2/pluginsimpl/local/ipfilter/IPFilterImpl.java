/*     */ package org.gudy.azureus2.pluginsimpl.local.ipfilter;
/*     */ 
/*     */ import java.io.File;
/*     */ import org.gudy.azureus2.core3.ipfilter.BannedIp;
/*     */ import org.gudy.azureus2.core3.ipfilter.BlockedIp;
/*     */ import org.gudy.azureus2.core3.ipfilter.IpFilter;
/*     */ import org.gudy.azureus2.core3.ipfilter.IpFilterManager;
/*     */ import org.gudy.azureus2.core3.ipfilter.IpFilterManagerFactory;
/*     */ import org.gudy.azureus2.core3.ipfilter.IpRange;
/*     */ import org.gudy.azureus2.plugins.ipfilter.IPBanned;
/*     */ import org.gudy.azureus2.plugins.ipfilter.IPBlocked;
/*     */ import org.gudy.azureus2.plugins.ipfilter.IPFilter;
/*     */ import org.gudy.azureus2.plugins.ipfilter.IPFilterException;
/*     */ import org.gudy.azureus2.plugins.ipfilter.IPRange;
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
/*     */ public class IPFilterImpl
/*     */   implements IPFilter
/*     */ {
/*     */   protected IpFilter filter;
/*     */   
/*     */   public IPFilterImpl()
/*     */   {
/*  45 */     this.filter = IpFilterManagerFactory.getSingleton().getIPFilter();
/*     */   }
/*     */   
/*     */ 
/*     */   public File getFile()
/*     */   {
/*  51 */     return this.filter.getFile();
/*     */   }
/*     */   
/*     */ 
/*     */   public void reload()
/*     */     throws IPFilterException
/*     */   {
/*     */     try
/*     */     {
/*  60 */       this.filter.reload();
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/*  64 */       throw new IPFilterException("IPFilter::reload fails", e);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public void save()
/*     */     throws IPFilterException
/*     */   {
/*     */     try
/*     */     {
/*  74 */       this.filter.save();
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/*  78 */       throw new IPFilterException("IPFilter::reload fails", e);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public IPRange[] getRanges()
/*     */   {
/*  85 */     IpRange[] l = this.filter.getRanges();
/*     */     
/*  87 */     IPRange[] res = new IPRange[l.length];
/*     */     
/*  89 */     for (int i = 0; i < l.length; i++)
/*     */     {
/*  91 */       res[i] = new IPRangeImpl(this, l[i]);
/*     */     }
/*     */     
/*  94 */     return res;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getNumberOfRanges()
/*     */   {
/* 100 */     return this.filter.getNbRanges();
/*     */   }
/*     */   
/*     */ 
/*     */   public int getNumberOfBlockedIPs()
/*     */   {
/* 106 */     return this.filter.getNbIpsBlocked();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public int getNumberOfBannedIPs()
/*     */   {
/* 113 */     return this.filter.getNbBannedIps();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean isInRange(String IPAddress)
/*     */   {
/* 120 */     return this.filter.isInRange(IPAddress);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public IPRange createRange(boolean this_session_only)
/*     */   {
/* 127 */     return new IPRangeImpl(this, this.filter.createRange(this_session_only));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void addRange(IPRange range)
/*     */   {
/* 134 */     if (!(range instanceof IPRangeImpl))
/*     */     {
/* 136 */       throw new RuntimeException("range must be created by createRange");
/*     */     }
/*     */     
/* 139 */     this.filter.addRange(((IPRangeImpl)range).getRange());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public IPRange createAndAddRange(String description, String start_ip, String end_ip, boolean this_session_only)
/*     */   {
/* 149 */     IPRange range = createRange(this_session_only);
/*     */     
/* 151 */     range.setDescription(description);
/*     */     
/* 153 */     range.setStartIP(start_ip);
/*     */     
/* 155 */     range.setEndIP(end_ip);
/*     */     
/* 157 */     range.checkValid();
/*     */     
/* 159 */     if (range.isValid())
/*     */     {
/* 161 */       addRange(range);
/*     */       
/* 163 */       return range;
/*     */     }
/*     */     
/* 166 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void removeRange(IPRange range)
/*     */   {
/* 173 */     if (!(range instanceof IPRangeImpl))
/*     */     {
/* 175 */       throw new RuntimeException("range must be created by createRange");
/*     */     }
/*     */     
/* 178 */     this.filter.removeRange(((IPRangeImpl)range).getRange());
/*     */   }
/*     */   
/*     */ 
/*     */   public IPBlocked[] getBlockedIPs()
/*     */   {
/* 184 */     BlockedIp[] l = this.filter.getBlockedIps();
/*     */     
/* 186 */     IPBlocked[] res = new IPBlocked[l.length];
/*     */     
/* 188 */     for (int i = 0; i < l.length; i++)
/*     */     {
/* 190 */       res[i] = new IPBlockedImpl(this, l[i]);
/*     */     }
/*     */     
/* 193 */     return res;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void block(String IPAddress)
/*     */   {
/* 200 */     this.filter.ban(IPAddress, "<plugin>", false);
/*     */   }
/*     */   
/*     */ 
/*     */   public IPBanned[] getBannedIPs()
/*     */   {
/* 206 */     BannedIp[] l = this.filter.getBannedIps();
/*     */     
/* 208 */     IPBanned[] res = new IPBanned[l.length];
/*     */     
/* 210 */     for (int i = 0; i < l.length; i++)
/*     */     {
/* 212 */       res[i] = new IPBannedImpl(l[i]);
/*     */     }
/*     */     
/* 215 */     return res;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void ban(String IPAddress, String text)
/*     */   {
/* 223 */     this.filter.ban(IPAddress, text, false);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void unban(String IPAddress)
/*     */   {
/* 230 */     this.filter.unban(IPAddress);
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isEnabled()
/*     */   {
/* 236 */     return this.filter.isEnabled();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setEnabled(boolean enabled)
/*     */   {
/* 243 */     this.filter.setEnabled(enabled);
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean getInRangeAddressesAreAllowed()
/*     */   {
/* 249 */     return this.filter.getInRangeAddressesAreAllowed();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setInRangeAddressesAreAllowed(boolean b)
/*     */   {
/* 256 */     this.filter.setInRangeAddressesAreAllowed(b);
/*     */   }
/*     */   
/*     */ 
/*     */   public void markAsUpToDate()
/*     */   {
/* 262 */     this.filter.markAsUpToDate();
/*     */   }
/*     */   
/*     */ 
/*     */   public long getLastUpdateTime()
/*     */   {
/* 268 */     return this.filter.getLastUpdateTime();
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/pluginsimpl/local/ipfilter/IPFilterImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */