/*     */ package org.gudy.azureus2.core3.peer.impl.control;
/*     */ 
/*     */ import org.gudy.azureus2.core3.peer.PEPeer;
/*     */ import org.gudy.azureus2.core3.peer.impl.PEPeerControl;
/*     */ import org.gudy.azureus2.core3.util.AEMonitor;
/*     */ import org.gudy.azureus2.core3.util.Ignore;
/*     */ import org.gudy.azureus2.core3.util.SystemTime;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class SuperSeedPiece
/*     */ {
/*     */   private final int pieceNumber;
/*     */   private int level;
/*     */   private long timeFirstDistributed;
/*     */   private PEPeer firstReceiver;
/*     */   private int timeToReachAnotherPeer;
/*  46 */   private static final AEMonitor class_mon = new AEMonitor("SuperSeedPiece:class");
/*     */   
/*     */   public SuperSeedPiece(PEPeerControl manager, int _pieceNumber)
/*     */   {
/*  50 */     Ignore.ignore(manager);
/*  51 */     this.pieceNumber = _pieceNumber;
/*  52 */     this.level = 0;
/*     */   }
/*     */   
/*     */   public void peerHasPiece(PEPeer peer) {
/*     */     try {
/*  57 */       class_mon.enter();
/*     */       
/*  59 */       if (this.level < 2)
/*     */       {
/*     */ 
/*  62 */         this.firstReceiver = peer;
/*  63 */         this.timeFirstDistributed = SystemTime.getCurrentTime();
/*     */         
/*  65 */         this.level = 2;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       }
/*  72 */       else if ((peer != null) && (this.firstReceiver != null) && (this.level == 2)) {
/*  73 */         this.timeToReachAnotherPeer = ((int)(SystemTime.getCurrentTime() - this.timeFirstDistributed));
/*  74 */         this.firstReceiver.setUploadHint(this.timeToReachAnotherPeer);
/*  75 */         this.level = 3;
/*     */       }
/*     */     }
/*     */     finally
/*     */     {
/*  80 */       class_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */   public int getLevel() {
/*  85 */     return this.level;
/*     */   }
/*     */   
/*     */   public void pieceRevealedToPeer() {
/*     */     try {
/*  90 */       class_mon.enter();
/*     */       
/*  92 */       this.level = 1;
/*     */     }
/*     */     finally {
/*  95 */       class_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public int getPieceNumber()
/*     */   {
/* 102 */     return this.pieceNumber;
/*     */   }
/*     */   
/*     */   public void peerLeft() {
/* 106 */     if (this.level == 1)
/* 107 */       this.level = 0;
/*     */   }
/*     */   
/*     */   public void updateTime() {
/* 111 */     if (this.level < 2)
/*     */     {
/* 113 */       return; }
/* 114 */     if (this.timeToReachAnotherPeer > 0)
/*     */     {
/* 116 */       return; }
/* 117 */     if (this.firstReceiver == null)
/*     */     {
/* 119 */       return; }
/* 120 */     int timeToSend = (int)(SystemTime.getCurrentTime() - this.timeFirstDistributed);
/* 121 */     if (timeToSend > this.firstReceiver.getUploadHint()) {
/* 122 */       this.firstReceiver.setUploadHint(timeToSend);
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/peer/impl/control/SuperSeedPiece.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */