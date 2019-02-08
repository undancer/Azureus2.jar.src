/*     */ package com.aelitis.azureus.core.tracker;
/*     */ 
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
/*     */ public abstract class TrackerPeerSourceAdapter
/*     */   implements TrackerPeerSource
/*     */ {
/*     */   public int getType()
/*     */   {
/*  32 */     return 0;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getName()
/*     */   {
/*  38 */     return "";
/*     */   }
/*     */   
/*     */ 
/*     */   public int getStatus()
/*     */   {
/*  44 */     return 0;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getStatusString()
/*     */   {
/*  50 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getSeedCount()
/*     */   {
/*  56 */     return -1;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getLeecherCount()
/*     */   {
/*  62 */     return -1;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getPeers()
/*     */   {
/*  68 */     return -1;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getCompletedCount()
/*     */   {
/*  74 */     return -1;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getLastUpdate()
/*     */   {
/*  80 */     return 0;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getSecondsToUpdate()
/*     */   {
/*  86 */     return -1;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getInterval()
/*     */   {
/*  92 */     return -1;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getMinInterval()
/*     */   {
/*  98 */     return -1;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isUpdating()
/*     */   {
/* 104 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean canManuallyUpdate()
/*     */   {
/* 110 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */   public void manualUpdate()
/*     */   {
/* 116 */     Debug.out("derp");
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean canDelete()
/*     */   {
/* 122 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */   public void delete()
/*     */   {
/* 128 */     Debug.out("derp");
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/tracker/TrackerPeerSourceAdapter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */