/*     */ package org.gudy.azureus2.core3.ipfilter.impl;
/*     */ 
/*     */ import java.util.Collection;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import org.gudy.azureus2.core3.ipfilter.BadIp;
/*     */ import org.gudy.azureus2.core3.ipfilter.BadIps;
/*     */ import org.gudy.azureus2.core3.util.AEMonitor;
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
/*     */ public class BadIpsImpl
/*     */   implements BadIps
/*     */ {
/*     */   private static BadIps instance;
/*  38 */   private static final AEMonitor class_mon = new AEMonitor("BadIps:class");
/*     */   
/*     */   private final Map bad_ip_map;
/*  41 */   private final AEMonitor bad_ip_map_mon = new AEMonitor("BadIps:Map");
/*     */   
/*     */   public static BadIps getInstance()
/*     */   {
/*     */     try
/*     */     {
/*  47 */       class_mon.enter();
/*     */       
/*  49 */       if (instance == null)
/*     */       {
/*  51 */         instance = new BadIpsImpl();
/*     */       }
/*     */       
/*  54 */       return instance;
/*     */     }
/*     */     finally
/*     */     {
/*  58 */       class_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */   public BadIpsImpl()
/*     */   {
/*  64 */     this.bad_ip_map = new HashMap();
/*     */   }
/*     */   
/*     */ 
/*     */   public int addWarningForIp(String ip)
/*     */   {
/*     */     try
/*     */     {
/*  72 */       this.bad_ip_map_mon.enter();
/*     */       
/*  74 */       BadIpImpl bad_ip = (BadIpImpl)this.bad_ip_map.get(ip);
/*     */       
/*  76 */       if (bad_ip == null)
/*     */       {
/*  78 */         bad_ip = new BadIpImpl(ip);
/*     */         
/*  80 */         this.bad_ip_map.put(ip, bad_ip);
/*     */       }
/*     */       
/*  83 */       return bad_ip.incrementWarnings();
/*     */     }
/*     */     finally
/*     */     {
/*  87 */       this.bad_ip_map_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public int getNbWarningForIp(String ip)
/*     */   {
/*     */     try
/*     */     {
/*  97 */       this.bad_ip_map_mon.enter();
/*     */       
/*  99 */       BadIpImpl bad_ip = (BadIpImpl)this.bad_ip_map.get(ip);
/*     */       int i;
/* 101 */       if (bad_ip == null)
/*     */       {
/* 103 */         return 0;
/*     */       }
/*     */       
/*     */ 
/* 107 */       return bad_ip.getNumberOfWarnings();
/*     */     }
/*     */     finally
/*     */     {
/* 111 */       this.bad_ip_map_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */   public BadIp[] getBadIps()
/*     */   {
/*     */     try
/*     */     {
/* 119 */       this.bad_ip_map_mon.enter();
/*     */       
/* 121 */       BadIp[] res = new BadIp[this.bad_ip_map.size()];
/*     */       
/* 123 */       this.bad_ip_map.values().toArray(res);
/*     */       
/* 125 */       return res;
/*     */     }
/*     */     finally {
/* 128 */       this.bad_ip_map_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */   public void clearBadIps()
/*     */   {
/*     */     try
/*     */     {
/* 136 */       this.bad_ip_map_mon.enter();
/*     */       
/* 138 */       this.bad_ip_map.clear();
/*     */     }
/*     */     finally
/*     */     {
/* 142 */       this.bad_ip_map_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public int getNbBadIps()
/*     */   {
/* 149 */     return this.bad_ip_map.size();
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/ipfilter/impl/BadIpsImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */