/*     */ package com.aelitis.azureus.core.networkmanager.admin.impl;
/*     */ 
/*     */ import com.aelitis.azureus.core.networkmanager.admin.NetworkAdminASN;
/*     */ import com.aelitis.azureus.core.networkmanager.admin.NetworkAdminException;
/*     */ import java.net.InetAddress;
/*     */ import org.gudy.azureus2.core3.tracker.protocol.PRHelpers;
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
/*     */ public class NetworkAdminASNImpl
/*     */   implements NetworkAdminASN
/*     */ {
/*     */   private final String as;
/*     */   private String asn;
/*     */   private final String bgp_prefix;
/*     */   
/*     */   protected NetworkAdminASNImpl(String _as, String _asn, String _bgp)
/*     */   {
/*  47 */     this.as = _as;
/*  48 */     this.asn = _asn;
/*  49 */     this.bgp_prefix = _bgp;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getAS()
/*     */   {
/*  55 */     return this.as == null ? "" : this.as;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getASName()
/*     */   {
/*  61 */     return this.asn == null ? "" : this.asn;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void setASName(String s)
/*     */   {
/*  68 */     this.asn = s;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getBGPPrefix()
/*     */   {
/*  74 */     return this.bgp_prefix == null ? "" : this.bgp_prefix;
/*     */   }
/*     */   
/*     */ 
/*     */   public InetAddress getBGPStartAddress()
/*     */   {
/*  80 */     if (this.bgp_prefix == null)
/*     */     {
/*  82 */       return null;
/*     */     }
/*     */     try
/*     */     {
/*  86 */       return getCIDRStartAddress();
/*     */     }
/*     */     catch (NetworkAdminException e)
/*     */     {
/*  90 */       Debug.out(e);
/*     */     }
/*  92 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected InetAddress getCIDRStartAddress()
/*     */     throws NetworkAdminException
/*     */   {
/* 101 */     int pos = this.bgp_prefix.indexOf('/');
/*     */     try
/*     */     {
/* 104 */       return InetAddress.getByName(this.bgp_prefix.substring(0, pos));
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/* 108 */       throw new NetworkAdminException("Parse failure for '" + this.bgp_prefix + "'", e);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected InetAddress getCIDREndAddress()
/*     */     throws NetworkAdminException
/*     */   {
/* 118 */     int pos = this.bgp_prefix.indexOf('/');
/*     */     try
/*     */     {
/* 121 */       InetAddress start = InetAddress.getByName(this.bgp_prefix.substring(0, pos));
/*     */       
/* 123 */       int cidr_mask = Integer.parseInt(this.bgp_prefix.substring(pos + 1));
/*     */       
/* 125 */       int rev_mask = 0;
/*     */       
/* 127 */       for (int i = 0; i < 32 - cidr_mask; i++)
/*     */       {
/*     */ 
/* 130 */         rev_mask = rev_mask << 1 | 0x1;
/*     */       }
/*     */       
/* 133 */       byte[] bytes = start.getAddress(); int 
/*     */       
/* 135 */         tmp75_74 = 0; byte[] tmp75_72 = bytes;tmp75_72[tmp75_74] = ((byte)(tmp75_72[tmp75_74] | rev_mask >> 24 & 0xFF)); int 
/* 136 */         tmp92_91 = 1; byte[] tmp92_89 = bytes;tmp92_89[tmp92_91] = ((byte)(tmp92_89[tmp92_91] | rev_mask >> 16 & 0xFF)); int 
/* 137 */         tmp109_108 = 2; byte[] tmp109_106 = bytes;tmp109_106[tmp109_108] = ((byte)(tmp109_106[tmp109_108] | rev_mask >> 8 & 0xFF)); int 
/* 138 */         tmp126_125 = 3; byte[] tmp126_123 = bytes;tmp126_123[tmp126_125] = ((byte)(tmp126_123[tmp126_125] | rev_mask & 0xFF));
/*     */       
/* 140 */       return InetAddress.getByAddress(bytes);
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/* 144 */       throw new NetworkAdminException("Parse failure for '" + this.bgp_prefix + "'", e);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean matchesCIDR(InetAddress address)
/*     */   {
/* 152 */     if ((this.bgp_prefix == null) || (this.bgp_prefix.length() == 0))
/*     */     {
/* 154 */       return false;
/*     */     }
/*     */     try
/*     */     {
/* 158 */       InetAddress start = getCIDRStartAddress();
/* 159 */       InetAddress end = getCIDREndAddress();
/*     */       
/* 161 */       long l_start = PRHelpers.addressToLong(start);
/* 162 */       long l_end = PRHelpers.addressToLong(end);
/*     */       
/* 164 */       long test = PRHelpers.addressToLong(address);
/*     */       
/* 166 */       return (test >= l_start) && (test <= l_end);
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/* 170 */       Debug.printStackTrace(e);
/*     */     }
/* 172 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public InetAddress getBGPEndAddress()
/*     */   {
/* 179 */     if (this.bgp_prefix == null)
/*     */     {
/* 181 */       return null;
/*     */     }
/*     */     try
/*     */     {
/* 185 */       return getCIDREndAddress();
/*     */     }
/*     */     catch (NetworkAdminException e)
/*     */     {
/* 189 */       Debug.out(e);
/*     */     }
/* 191 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean sameAs(NetworkAdminASN other)
/*     */   {
/* 199 */     return getAS().equals(other.getAS());
/*     */   }
/*     */   
/*     */ 
/*     */   public String getString()
/*     */   {
/* 205 */     return "as=" + getAS() + ",asn=" + getASName() + ", bgp_prefx=" + getBGPPrefix() + "[" + getBGPStartAddress() + "-" + getBGPEndAddress() + "]";
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/networkmanager/admin/impl/NetworkAdminASNImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */