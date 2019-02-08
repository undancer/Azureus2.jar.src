/*     */ package org.gudy.azureus2.core3.ipfilter.impl;
/*     */ 
/*     */ import java.io.PrintStream;
/*     */ import java.net.InetAddress;
/*     */ import java.net.UnknownHostException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Comparator;
/*     */ import java.util.List;
/*     */ import java.util.Random;
/*     */ import org.gudy.azureus2.core3.ipfilter.IpFilterManager;
/*     */ import org.gudy.azureus2.core3.ipfilter.IpFilterManagerFactory;
/*     */ import org.gudy.azureus2.core3.ipfilter.IpRange;
/*     */ import org.gudy.azureus2.core3.logging.LogEvent;
/*     */ import org.gudy.azureus2.core3.logging.LogIDs;
/*     */ import org.gudy.azureus2.core3.logging.Logger;
/*     */ import org.gudy.azureus2.core3.tracker.protocol.PRHelpers;
/*     */ import org.gudy.azureus2.core3.util.AEMonitor;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.SystemTime;
/*     */ import org.gudy.azureus2.core3.util.UnresolvableHostManager;
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
/*     */ public class IPAddressRangeManager
/*     */ {
/*  41 */   private static final LogIDs LOGID = LogIDs.CORE;
/*     */   
/*  43 */   protected final ArrayList entries = new ArrayList();
/*     */   
/*     */   protected long total_span;
/*     */   
/*     */   protected boolean rebuild_required;
/*     */   
/*     */   protected long last_rebuild_time;
/*  50 */   protected IpRange[] mergedRanges = new IpRange[0];
/*     */   
/*  52 */   protected final AEMonitor this_mon = new AEMonitor("IPAddressRangeManager");
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void addRange(IpRange range)
/*     */   {
/*     */     try
/*     */     {
/*  63 */       this.this_mon.enter();
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  69 */       this.entries.add(range);
/*     */       
/*  71 */       this.rebuild_required = true;
/*     */     }
/*     */     finally
/*     */     {
/*  75 */       this.this_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */   public void removeRange(IpRange range) {
/*     */     try {
/*  81 */       this.this_mon.enter();
/*     */       
/*  83 */       this.entries.remove(range);
/*     */       
/*  85 */       this.rebuild_required = true;
/*     */     }
/*     */     finally
/*     */     {
/*  89 */       this.this_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Object isInRange(String ip)
/*     */   {
/*  99 */     if (this.entries.size() == 0)
/*     */     {
/* 101 */       return null;
/*     */     }
/*     */     try
/*     */     {
/* 105 */       this.this_mon.enter();
/*     */       
/* 107 */       long address_long = addressToInt(ip);
/*     */       
/* 109 */       if (address_long < 0L)
/*     */       {
/* 111 */         address_long += 4294967296L;
/*     */       }
/*     */       
/* 114 */       Object res = isInRange(address_long);
/*     */       
/*     */ 
/*     */ 
/* 118 */       return res;
/*     */     }
/*     */     finally
/*     */     {
/* 122 */       this.this_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Object isInRange(InetAddress ip)
/*     */   {
/* 132 */     if (this.entries.size() == 0)
/*     */     {
/* 134 */       return null;
/*     */     }
/*     */     try
/*     */     {
/* 138 */       this.this_mon.enter();
/*     */       
/* 140 */       long address_long = addressToInt(ip);
/*     */       
/* 142 */       if (address_long < 0L)
/*     */       {
/* 144 */         address_long += 4294967296L;
/*     */       }
/*     */       
/* 147 */       Object res = isInRange(address_long);
/*     */       
/*     */ 
/*     */ 
/* 151 */       return res;
/*     */     }
/*     */     finally
/*     */     {
/* 155 */       this.this_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   protected Object isInRange(long address_long)
/*     */   {
/*     */     try
/*     */     {
/* 164 */       this.this_mon.enter();
/*     */       
/* 166 */       checkRebuild();
/*     */       
/* 168 */       if (this.mergedRanges.length == 0)
/*     */       {
/* 170 */         return null;
/*     */       }
/*     */       
/*     */ 
/*     */ 
/* 175 */       int bottom = 0;
/* 176 */       int top = this.mergedRanges.length - 1;
/*     */       
/* 178 */       int current = -1;
/*     */       long this_start;
/* 180 */       while ((top >= 0) && (bottom < this.mergedRanges.length) && (bottom <= top))
/*     */       {
/* 182 */         current = (bottom + top) / 2;
/*     */         
/* 184 */         IpRange e = this.mergedRanges[current];
/*     */         
/* 186 */         this_start = e.getStartIpLong();
/* 187 */         long this_end = e.getMergedEndLong();
/*     */         
/* 189 */         if (address_long == this_start) {
/*     */           break;
/*     */         }
/*     */         
/* 193 */         if (address_long > this_start)
/*     */         {
/* 195 */           if (address_long <= this_end) {
/*     */             break;
/*     */           }
/*     */           
/*     */ 
/*     */ 
/*     */ 
/* 202 */           bottom = current + 1;
/*     */         } else {
/* 204 */           if (address_long == this_end) {
/*     */             break;
/*     */           }
/*     */           
/*     */ 
/*     */ 
/*     */ 
/* 211 */           if (address_long >= this_start) {
/*     */             break;
/*     */           }
/*     */           
/*     */ 
/* 216 */           top = current - 1;
/*     */         }
/*     */       }
/*     */       IpRange e;
/* 220 */       if ((top >= 0) && (bottom < this.mergedRanges.length) && (bottom <= top))
/*     */       {
/* 222 */         e = this.mergedRanges[current];
/*     */         
/* 224 */         if (address_long <= e.getEndIpLong())
/*     */         {
/* 226 */           return e;
/*     */         }
/*     */         
/* 229 */         IpRange[] merged = e.getMergedEntries();
/*     */         
/* 231 */         if (merged == null)
/*     */         {
/* 233 */           Debug.out("IPAddressRangeManager: inconsistent merged details - no entries");
/*     */           
/* 235 */           return null;
/*     */         }
/*     */         
/* 238 */         for (int i = 0; i < merged.length; i++)
/*     */         {
/* 240 */           IpRange me = merged[i];
/*     */           
/* 242 */           if ((me.getStartIpLong() <= address_long) && (me.getEndIpLong() >= address_long))
/*     */           {
/* 244 */             return me;
/*     */           }
/*     */         }
/*     */         
/* 248 */         Debug.out("IPAddressRangeManager: inconsistent merged details - entry not found");
/*     */       }
/*     */       
/* 251 */       return null;
/*     */     }
/*     */     finally
/*     */     {
/* 255 */       this.this_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   protected int addressToInt(String address)
/*     */   {
/*     */     try
/*     */     {
/* 264 */       return PRHelpers.addressToInt(address);
/*     */     }
/*     */     catch (UnknownHostException e) {}
/*     */     
/* 268 */     return UnresolvableHostManager.getPseudoAddress(address);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected int addressToInt(InetAddress address)
/*     */   {
/* 276 */     return PRHelpers.addressToInt(address);
/*     */   }
/*     */   
/*     */   protected void checkRebuild()
/*     */   {
/*     */     try
/*     */     {
/* 283 */       this.this_mon.enter();
/*     */       
/* 285 */       if (this.rebuild_required)
/*     */       {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 291 */         long now = SystemTime.getCurrentTime();
/*     */         
/* 293 */         long secs_since_last_build = (now - this.last_rebuild_time) / 1000L;
/*     */         
/*     */ 
/*     */ 
/* 297 */         if (secs_since_last_build > this.entries.size() / 2000)
/*     */         {
/* 299 */           this.last_rebuild_time = now;
/*     */           
/* 301 */           this.rebuild_required = false;
/*     */           
/* 303 */           rebuild();
/*     */         }
/*     */       }
/*     */     }
/*     */     finally {
/* 308 */       this.this_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   protected void rebuild()
/*     */   {
/* 315 */     if (Logger.isEnabled()) {
/* 316 */       Logger.log(new LogEvent(LOGID, "IPAddressRangeManager: rebuilding " + this.entries.size() + " entries starts"));
/*     */     }
/*     */     
/* 319 */     IpRange[] ents = new IpRange[this.entries.size()];
/*     */     
/* 321 */     this.entries.toArray(ents);
/*     */     
/* 323 */     for (int i = 0; i < ents.length; i++)
/*     */     {
/* 325 */       ents[i].resetMergeInfo();
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 330 */     Arrays.sort(ents, new Comparator()
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/*     */       public int compare(IpRange e1, IpRange e2)
/*     */       {
/*     */ 
/*     */ 
/* 339 */         long diff = e1.getStartIpLong() - e2.getStartIpLong();
/*     */         
/* 341 */         if (diff == 0L)
/*     */         {
/* 343 */           diff = e2.getEndIpLong() - e1.getEndIpLong();
/*     */         }
/*     */         
/* 346 */         return IPAddressRangeManager.this.signum(diff);
/*     */ 
/*     */       }
/*     */       
/*     */ 
/* 351 */     });
/* 352 */     List me = new ArrayList(ents.length);
/*     */     
/* 354 */     for (int i = 0; i < ents.length; i++)
/*     */     {
/* 356 */       IpRange entry = ents[i];
/*     */       
/* 358 */       if (!entry.getMerged())
/*     */       {
/*     */ 
/*     */ 
/*     */ 
/* 363 */         me.add(entry);
/*     */         
/* 365 */         int pos = i + 1;
/*     */         
/* 367 */         while (pos < ents.length)
/*     */         {
/* 369 */           long end_pos = entry.getMergedEndLong();
/*     */           
/* 371 */           IpRange e2 = ents[(pos++)];
/*     */           
/* 373 */           if (!e2.getMerged())
/*     */           {
/* 375 */             if (end_pos < e2.getStartIpLong())
/*     */               break;
/* 377 */             e2.setMerged();
/*     */             
/* 379 */             if (e2.getEndIpLong() > end_pos)
/*     */             {
/* 381 */               entry.setMergedEnd(e2.getEndIpLong());
/*     */               
/* 383 */               entry.addMergedEntry(e2);
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */     }
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
/* 402 */     this.mergedRanges = new IpRange[me.size()];
/*     */     
/* 404 */     me.toArray(this.mergedRanges);
/*     */     
/* 406 */     this.total_span = 0L;
/*     */     
/* 408 */     for (int i = 0; i < this.mergedRanges.length; i++)
/*     */     {
/* 410 */       IpRange e = this.mergedRanges[i];
/*     */       
/*     */ 
/*     */ 
/* 414 */       long span = e.getMergedEndLong() - e.getStartIpLong() + 1L;
/*     */       
/* 416 */       this.total_span += span;
/*     */     }
/*     */     
/*     */ 
/* 420 */     if (Logger.isEnabled()) {
/* 421 */       Logger.log(new LogEvent(LOGID, "IPAddressRangeManager: rebuilding " + this.entries.size() + " entries ends"));
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected int signum(long diff)
/*     */   {
/* 431 */     if (diff > 0L) {
/* 432 */       return 1;
/*     */     }
/*     */     
/* 435 */     if (diff < 0L) {
/* 436 */       return -1;
/*     */     }
/*     */     
/* 439 */     return 0;
/*     */   }
/*     */   
/*     */ 
/*     */   protected long getTotalSpan()
/*     */   {
/* 445 */     checkRebuild();
/*     */     
/* 447 */     return this.total_span;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static void main(String[] args)
/*     */   {
/* 455 */     IPAddressRangeManager manager = new IPAddressRangeManager();
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
/* 509 */     Random r = new Random();
/*     */     
/* 511 */     for (int i = 0; i < 1000000; i++)
/*     */     {
/* 513 */       int ip1 = r.nextInt(268435455);
/*     */       
/* 515 */       int ip2 = ip1 + r.nextInt(255);
/*     */       
/* 517 */       String start = PRHelpers.intToAddress(ip1);
/* 518 */       String end = PRHelpers.intToAddress(ip2);
/*     */       
/* 520 */       manager.addRange(new IpRangeImpl("test_" + i, start, end, true));
/*     */     }
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
/* 533 */     int num = 0;
/* 534 */     int hits = 0;
/*     */     
/*     */ 
/*     */     for (;;)
/*     */     {
/* 539 */       if (num % 1000 == 0)
/*     */       {
/* 541 */         System.out.println(num + "/" + hits);
/*     */       }
/*     */       
/*     */ 
/* 545 */       num++;
/*     */       
/* 547 */       int ip = r.nextInt();
/*     */       
/* 549 */       Object res = manager.isInRange(ip);
/*     */       
/* 551 */       if (res != null)
/*     */       {
/* 553 */         hits++;
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public ArrayList getEntries() {
/* 559 */     return this.entries;
/*     */   }
/*     */   
/*     */   public void clearAllEntries() {
/*     */     try {
/* 564 */       this.this_mon.enter();
/*     */       
/* 566 */       this.entries.clear();
/*     */       
/* 568 */       IpFilterManagerFactory.getSingleton().deleteAllDescriptions();
/*     */       
/* 570 */       this.rebuild_required = true;
/*     */     }
/*     */     finally
/*     */     {
/* 574 */       this.this_mon.exit();
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/ipfilter/impl/IPAddressRangeManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */