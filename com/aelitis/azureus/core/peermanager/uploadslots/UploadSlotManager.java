/*     */ package com.aelitis.azureus.core.peermanager.uploadslots;
/*     */ 
/*     */ import java.io.PrintStream;
/*     */ import java.util.LinkedList;
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
/*     */ public class UploadSlotManager
/*     */ {
/*     */   private static final int EXPIRE_NORMAL = 1;
/*     */   private static final int EXPIRE_OPTIMISTIC = 3;
/*     */   private static final int EXPIRE_SEED = 6;
/*     */   public static final boolean AUTO_SLOT_ENABLE = false;
/*     */   private long last_process_time;
/*  49 */   private static final UploadSlotManager instance = new UploadSlotManager();
/*     */   
/*  51 */   public static UploadSlotManager getSingleton() { return instance; }
/*     */   
/*     */ 
/*  54 */   private final UploadSessionPicker picker = new UploadSessionPicker();
/*     */   
/*     */ 
/*  57 */   private final UploadSlot[] slots = { new UploadSlot(1), new UploadSlot(0), new UploadSlot(0), new UploadSlot(0) };
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  64 */   private long current_round = 0L;
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
/*     */   public void registerHelper(UploadHelper helper) {}
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
/*     */   public void deregisterHelper(UploadHelper helper) {}
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
/*     */   public void updateHelper(UploadHelper helper) {}
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
/*     */   private void process() {}
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
/* 222 */   int count = 0;
/*     */   
/*     */   private UploadSession getNextBestSession(LinkedList best) {
/* 225 */     this.count += 1;
/* 226 */     System.out.print("getNextBestSession [" + this.count + "] best.size=" + best.size() + "  ");
/*     */     
/* 228 */     if (!best.isEmpty()) {
/* 229 */       UploadSession session = (UploadSession)best.removeFirst();
/*     */       
/* 231 */       if (!isAlreadySlotted(session))
/*     */       {
/* 233 */         System.out.println("OK found session [" + session.getStatsTrace() + "]");
/*     */         
/* 235 */         return session;
/*     */       }
/*     */       
/* 238 */       System.out.println("FAIL already-slotted session [" + session.getStatsTrace() + "]");
/*     */       
/* 240 */       return getNextBestSession(best);
/*     */     }
/*     */     
/* 243 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private UploadSession pickOptSession()
/*     */   {
/* 251 */     int max = this.picker.getHelperCount();
/*     */     
/* 253 */     for (int i = 0; i < max; i++)
/*     */     {
/* 255 */       UploadSession session = this.picker.pickNextOptimisticSession();
/*     */       
/* 257 */       if ((session != null) && (!isAlreadySlotted(session))) {
/* 258 */         return session;
/*     */       }
/*     */     }
/*     */     
/* 262 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private boolean isAlreadySlotted(UploadSession session)
/*     */   {
/* 269 */     for (int i = 0; i < this.slots.length; i++) {
/* 270 */       UploadSession s = this.slots[i].getSession();
/* 271 */       if ((s != null) && (s.isSameSession(session))) { return true;
/*     */       }
/*     */     }
/* 274 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private void printSlotStats()
/*     */   {
/* 281 */     System.out.println("\nUPLOAD SLOTS [" + this.current_round + "x]:");
/*     */     
/* 283 */     for (int i = 0; i < this.slots.length; i++) {
/* 284 */       UploadSlot slot = this.slots[i];
/*     */       
/* 286 */       System.out.print("[" + i + "]: ");
/*     */       
/* 288 */       String slot_type = slot.getSlotType() == 0 ? "NORM" : "OPTI";
/*     */       
/* 290 */       long rem = slot.getExpireRound() - this.current_round;
/* 291 */       String remaining = " [" + rem + "]rr";
/*     */       
/* 293 */       String ses_trace = slot.getSession() == null ? "EMPTY" : slot.getSession().getStatsTrace();
/*     */       
/* 295 */       System.out.println(slot_type + remaining + " : " + ses_trace);
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/peermanager/uploadslots/UploadSlotManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */