/*     */ package com.aelitis.azureus.core.peermanager.uploadslots;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.HashSet;
/*     */ import java.util.Iterator;
/*     */ import java.util.LinkedList;
/*     */ import org.gudy.azureus2.core3.peer.PEPeer;
/*     */ import org.gudy.azureus2.core3.util.AEMonitor;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.RandomUtils;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class UploadSessionPicker
/*     */ {
/*  37 */   private final LinkedList next_optimistics = new LinkedList();
/*  38 */   private final AEMonitor next_optimistics_mon = new AEMonitor("UploadSessionPicker");
/*     */   
/*  40 */   private final LinkedList helpers = new LinkedList();
/*     */   
/*  42 */   private final DownloadingRanker down_ranker = new DownloadingRanker();
/*  43 */   private final SeedingRanker seed_ranker = new SeedingRanker();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void registerHelper(UploadHelper helper)
/*     */   {
/*     */     try
/*     */     {
/*  53 */       this.next_optimistics_mon.enter();
/*  54 */       this.helpers.add(helper);
/*     */       
/*  56 */       int priority = helper.getPriority();
/*     */       
/*     */ 
/*  59 */       for (int i = 0; i < priority; i++) {
/*  60 */         insertHelper(helper);
/*     */       }
/*     */     } finally {
/*  63 */       this.next_optimistics_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */   protected void deregisterHelper(UploadHelper helper) {
/*     */     try {
/*  69 */       this.next_optimistics_mon.enter();
/*  70 */       this.helpers.remove(helper);
/*     */       
/*  72 */       boolean rem = this.next_optimistics.removeAll(Collections.singleton(helper));
/*  73 */       if (!rem) Debug.out("!rem");
/*     */     } finally {
/*  75 */       this.next_optimistics_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */   protected void updateHelper(UploadHelper helper) {
/*     */     try {
/*  81 */       this.next_optimistics_mon.enter();
/*  82 */       int priority = helper.getPriority();
/*     */       
/*  84 */       int count = 0;
/*     */       
/*  86 */       for (Iterator it = this.next_optimistics.iterator(); it.hasNext();) {
/*  87 */         UploadHelper h = (UploadHelper)it.next();
/*  88 */         if (h == helper) {
/*  89 */           count++;
/*     */           
/*  91 */           if (count > priority) {
/*  92 */             it.remove();
/*     */           }
/*     */         }
/*     */       }
/*     */       
/*  97 */       if (count < priority) {
/*  98 */         for (int i = count; i < priority; i++) {
/*  99 */           insertHelper(helper);
/*     */         }
/*     */       }
/*     */     } finally {
/* 103 */       this.next_optimistics_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */   private void insertHelper(UploadHelper helper)
/*     */   {
/* 109 */     int pos = RandomUtils.nextInt(this.next_optimistics.size() + 1);
/* 110 */     this.next_optimistics.add(pos, helper);
/*     */   }
/*     */   
/*     */   protected int getHelperCount()
/*     */   {
/*     */     try
/*     */     {
/* 117 */       this.next_optimistics_mon.enter();
/* 118 */       return this.next_optimistics.size();
/*     */     } finally {
/* 120 */       this.next_optimistics_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */   protected UploadSession pickNextOptimisticSession()
/*     */   {
/*     */     try
/*     */     {
/* 128 */       this.next_optimistics_mon.enter();
/*     */       
/* 130 */       HashSet failed_helpers = null;
/*     */       
/* 132 */       int loops_allowed = this.next_optimistics.size();
/*     */       UploadHelper helper;
/* 134 */       while (loops_allowed > 0)
/*     */       {
/* 136 */         helper = (UploadHelper)this.next_optimistics.removeFirst();
/*     */         
/* 138 */         this.next_optimistics.addLast(helper);
/*     */         
/* 140 */         if ((failed_helpers == null) || (!failed_helpers.contains(helper)))
/*     */         {
/*     */           PEPeer peer;
/*     */           PEPeer peer;
/* 144 */           if (helper.isSeeding()) {
/* 145 */             peer = this.seed_ranker.getNextOptimisticPeer(helper.getAllPeers());
/*     */           }
/*     */           else {
/* 148 */             peer = this.down_ranker.getNextOptimisticPeer(helper.getAllPeers());
/*     */           }
/*     */           
/* 151 */           if (peer == null)
/*     */           {
/* 153 */             if (failed_helpers == null) { failed_helpers = new HashSet();
/*     */             }
/* 155 */             failed_helpers.add(helper);
/*     */           }
/*     */           else
/*     */           {
/* 159 */             return new UploadSession(peer, helper.isSeeding() ? 1 : 0);
/*     */           }
/*     */         }
/*     */         
/* 163 */         loops_allowed--;
/*     */       }
/*     */       
/* 166 */       return null;
/*     */     }
/*     */     finally {
/* 169 */       this.next_optimistics_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */   private ArrayList<PEPeer> globalGetAllDownloadPeers()
/*     */   {
/*     */     try {
/* 176 */       this.next_optimistics_mon.enter();
/* 177 */       ArrayList<PEPeer> all = new ArrayList();
/*     */       
/* 179 */       for (Iterator<PEPeer> it = this.helpers.iterator(); it.hasNext();) {
/* 180 */         UploadHelper helper = (UploadHelper)it.next();
/*     */         
/* 182 */         if (!helper.isSeeding()) {
/* 183 */           all.addAll(helper.getAllPeers());
/*     */         }
/*     */       }
/*     */       
/* 187 */       return all;
/*     */     } finally {
/* 189 */       this.next_optimistics_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected LinkedList<UploadSession> pickBestDownloadSessions(int max_sessions)
/*     */   {
/* 198 */     ArrayList<PEPeer> all_peers = globalGetAllDownloadPeers();
/*     */     
/* 200 */     if (all_peers.isEmpty()) { return new LinkedList();
/*     */     }
/* 202 */     ArrayList<PEPeer> best = this.down_ranker.rankPeers(max_sessions, all_peers);
/*     */     
/* 204 */     if (best.size() != max_sessions) {
/* 205 */       Debug.outNoStack("best.size()[" + best.size() + "] != max_sessions[" + max_sessions + "]");
/*     */     }
/*     */     
/* 208 */     if (best.isEmpty()) {
/* 209 */       return new LinkedList();
/*     */     }
/*     */     
/*     */ 
/* 213 */     LinkedList<UploadSession> best_sessions = new LinkedList();
/*     */     
/* 215 */     for (Iterator<PEPeer> it = best.iterator(); it.hasNext();) {
/* 216 */       PEPeer peer = (PEPeer)it.next();
/* 217 */       UploadSession session = new UploadSession(peer, 0);
/* 218 */       best_sessions.add(session);
/*     */     }
/*     */     
/* 221 */     return best_sessions;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/peermanager/uploadslots/UploadSessionPicker.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */