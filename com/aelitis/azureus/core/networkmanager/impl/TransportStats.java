/*     */ package com.aelitis.azureus.core.networkmanager.impl;
/*     */ 
/*     */ import java.io.PrintStream;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.Set;
/*     */ import java.util.TreeMap;
/*     */ import org.gudy.azureus2.core3.util.Timer;
/*     */ import org.gudy.azureus2.core3.util.TimerEvent;
/*     */ import org.gudy.azureus2.core3.util.TimerEventPerformer;
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
/*     */ public class TransportStats
/*     */ {
/*     */   private static final int PRINT_INTERVAL = 60000;
/*     */   private static final int GRANULARITY = 10;
/*  33 */   private final TreeMap read_sizes = new TreeMap();
/*  34 */   private final TreeMap write_sizes = new TreeMap();
/*     */   
/*  36 */   private long total_reads = 0L;
/*  37 */   private long total_writes = 0L;
/*     */   
/*     */   public TransportStats()
/*     */   {
/*  41 */     Timer printer = new Timer("TransportStats:Printer");
/*  42 */     printer.addPeriodicEvent(60000L, new TimerEventPerformer()
/*     */     {
/*     */       public void perform(TimerEvent ev)
/*     */       {
/*  46 */         TransportStats.this.printStats();
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */   public void bytesRead(int num_bytes_read)
/*     */   {
/*  54 */     this.total_reads += 1L;
/*  55 */     updateSizes(this.read_sizes, num_bytes_read);
/*     */   }
/*     */   
/*     */   public void bytesWritten(int num_bytes_written)
/*     */   {
/*  60 */     this.total_writes += 1L;
/*  61 */     updateSizes(this.write_sizes, num_bytes_written);
/*     */   }
/*     */   
/*     */   private void updateSizes(TreeMap io_sizes, int num_bytes)
/*     */   {
/*     */     Integer size_key;
/*     */     Integer size_key;
/*  68 */     if (num_bytes == 0) {
/*  69 */       size_key = new Integer(0);
/*     */     }
/*     */     else {
/*  72 */       size_key = new Integer(num_bytes / 10 + 1);
/*     */     }
/*     */     
/*  75 */     Long count = (Long)io_sizes.get(size_key);
/*     */     
/*  77 */     if (count == null) {
/*  78 */       io_sizes.put(size_key, new Long(1L));
/*     */     }
/*     */     else {
/*  81 */       io_sizes.put(size_key, new Long(count.longValue() + 1L));
/*     */     }
/*     */   }
/*     */   
/*     */   private void printStats()
/*     */   {
/*  87 */     System.out.println("\n------------------------------");
/*  88 */     System.out.println("***** TCP SOCKET READ SIZE STATS *****");
/*  89 */     printSizes(this.read_sizes, this.total_reads);
/*     */     
/*  91 */     System.out.println("\n***** TCP SOCKET WRITE SIZE STATS *****");
/*  92 */     printSizes(this.write_sizes, this.total_writes);
/*  93 */     System.out.println("------------------------------");
/*     */   }
/*     */   
/*     */ 
/*     */   private void printSizes(TreeMap size_map, long num_total)
/*     */   {
/*  99 */     int prev_high = 1;
/*     */     
/* 101 */     for (Iterator it = size_map.entrySet().iterator(); it.hasNext();) {
/* 102 */       Map.Entry entry = (Map.Entry)it.next();
/* 103 */       int key = ((Integer)entry.getKey()).intValue();
/* 104 */       long count = ((Long)entry.getValue()).longValue();
/*     */       
/* 106 */       long percentage = count * 100L / num_total;
/*     */       
/* 108 */       if (key == 0) {
/* 109 */         if (percentage > 3L) {
/* 110 */           System.out.println("[0 bytes]= x" + percentage + "%");
/*     */         }
/*     */       }
/*     */       else {
/* 114 */         int high = key * 10;
/*     */         
/* 116 */         if (percentage > 3L) {
/* 117 */           System.out.println("[" + prev_high + "-" + (high - 1) + " bytes]= x" + percentage + "%");
/*     */         }
/*     */         
/* 120 */         prev_high = high;
/*     */       }
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/networkmanager/impl/TransportStats.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */