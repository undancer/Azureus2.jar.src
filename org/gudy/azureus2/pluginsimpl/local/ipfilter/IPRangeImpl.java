/*     */ package org.gudy.azureus2.pluginsimpl.local.ipfilter;
/*     */ 
/*     */ import org.gudy.azureus2.core3.ipfilter.IpRange;
/*     */ import org.gudy.azureus2.plugins.ipfilter.IPFilter;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class IPRangeImpl
/*     */   implements IPRange
/*     */ {
/*     */   private IPFilter filter;
/*     */   private IpRange range;
/*     */   
/*     */   protected IPRangeImpl(IPFilter _filter, IpRange _range)
/*     */   {
/*  46 */     this.filter = _filter;
/*  47 */     this.range = _range;
/*     */   }
/*     */   
/*     */ 
/*     */   protected IpRange getRange()
/*     */   {
/*  53 */     return this.range;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getDescription()
/*     */   {
/*  59 */     return this.range.getDescription();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setDescription(String str)
/*     */   {
/*  66 */     this.range.setDescription(str);
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isValid()
/*     */   {
/*  72 */     return this.range.isValid();
/*     */   }
/*     */   
/*     */ 
/*     */   public void checkValid()
/*     */   {
/*  78 */     this.range.checkValid();
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isSessionOnly()
/*     */   {
/*  84 */     return this.range.isSessionOnly();
/*     */   }
/*     */   
/*     */ 
/*     */   public String getStartIP()
/*     */   {
/*  90 */     return this.range.getStartIp();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setStartIP(String str)
/*     */   {
/*  97 */     this.range.setStartIp(str);
/*     */   }
/*     */   
/*     */ 
/*     */   public String getEndIP()
/*     */   {
/* 103 */     return this.range.getEndIp();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setEndIP(String str)
/*     */   {
/* 110 */     this.range.setEndIp(str);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setSessionOnly(boolean sessionOnly)
/*     */   {
/* 117 */     this.range.setSessionOnly(sessionOnly);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean isInRange(String ipAddress)
/*     */   {
/* 124 */     return this.range.isInRange(ipAddress);
/*     */   }
/*     */   
/*     */ 
/*     */   public void delete()
/*     */   {
/* 130 */     this.filter.removeRange(this);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean equals(Object other)
/*     */   {
/* 137 */     if (!(other instanceof IPRangeImpl))
/*     */     {
/* 139 */       return false;
/*     */     }
/*     */     
/* 142 */     return compareTo(other) == 0;
/*     */   }
/*     */   
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 148 */     int hash = getStartIP().hashCode();
/*     */     
/* 150 */     String ip = getEndIP();
/*     */     
/* 152 */     if (ip != null)
/*     */     {
/* 154 */       hash += ip.hashCode();
/*     */     }
/*     */     
/* 157 */     return hash;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public int compareTo(Object other)
/*     */   {
/* 164 */     if (!(other instanceof IPRangeImpl))
/*     */     {
/* 166 */       throw new RuntimeException("other object must be IPRange");
/*     */     }
/*     */     
/*     */ 
/* 170 */     IPRangeImpl o = (IPRangeImpl)other;
/*     */     
/* 172 */     String ip1 = getStartIP();
/* 173 */     String ip2 = o.getStartIP();
/*     */     
/* 175 */     int res = ip1.compareTo(ip2);
/*     */     
/* 177 */     if (res != 0) {
/* 178 */       return res;
/*     */     }
/*     */     
/* 181 */     ip1 = getEndIP();
/* 182 */     ip2 = o.getEndIP();
/*     */     
/* 184 */     if ((ip1 == null) && (ip2 == null)) {
/* 185 */       return 0;
/*     */     }
/*     */     
/* 188 */     if (ip1 == null) {
/* 189 */       return -1;
/*     */     }
/*     */     
/* 192 */     if (ip2 == null) {
/* 193 */       return 1;
/*     */     }
/*     */     
/* 196 */     return ip1.compareTo(ip2);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/pluginsimpl/local/ipfilter/IPRangeImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */