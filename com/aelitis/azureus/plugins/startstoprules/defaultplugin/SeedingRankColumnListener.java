/*     */ package com.aelitis.azureus.plugins.startstoprules.defaultplugin;
/*     */ 
/*     */ import java.util.Map;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationListener;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.core3.util.SystemTime;
/*     */ import org.gudy.azureus2.core3.util.TimeFormatter;
/*     */ import org.gudy.azureus2.plugins.PluginConfig;
/*     */ import org.gudy.azureus2.plugins.download.Download;
/*     */ import org.gudy.azureus2.plugins.download.DownloadStats;
/*     */ import org.gudy.azureus2.plugins.peers.PeerManager;
/*     */ import org.gudy.azureus2.plugins.peers.PeerManagerStats;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableCell;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableCellRefreshListener;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class SeedingRankColumnListener
/*     */   implements TableCellRefreshListener, COConfigurationListener
/*     */ {
/*     */   private Map downloadDataMap;
/*     */   private PluginConfig pluginConfig;
/*     */   private int minTimeAlive;
/*     */   private int iRankType;
/*     */   private boolean bDebugLog;
/*     */   private int iTimed_MinSeedingTimeWithPeers;
/*     */   
/*     */   public SeedingRankColumnListener(Map _downloadDataMap, PluginConfig pc)
/*     */   {
/*  56 */     this.downloadDataMap = _downloadDataMap;
/*  57 */     this.pluginConfig = pc;
/*  58 */     COConfigurationManager.addListener(this);
/*  59 */     configurationSaved();
/*     */   }
/*     */   
/*     */   public void refresh(TableCell cell) {
/*  63 */     Download dl = (Download)cell.getDataSource();
/*  64 */     if (dl == null) {
/*  65 */       return;
/*     */     }
/*  67 */     DefaultRankCalculator dlData = null;
/*  68 */     Object o = cell.getSortValue();
/*  69 */     if ((o instanceof DefaultRankCalculator)) {
/*  70 */       dlData = (DefaultRankCalculator)o;
/*     */     } else {
/*  72 */       dlData = (DefaultRankCalculator)this.downloadDataMap.get(dl);
/*  73 */       cell.setSortValue(dlData);
/*     */     }
/*  75 */     if (dlData == null) {
/*  76 */       return;
/*     */     }
/*     */     
/*  79 */     long sr = dl.getSeedingRank();
/*     */     
/*  81 */     String sText = "";
/*  82 */     if (sr >= 0L) {
/*  83 */       if (dlData.getCachedIsFP()) {
/*  84 */         sText = sText + MessageText.getString("StartStopRules.firstPriority") + " ";
/*     */       }
/*  86 */       if (this.iRankType == 3)
/*     */       {
/*  88 */         if (sr > 199999999L) {
/*  89 */           long timeStarted = dl.getStats().getTimeStartedSeeding();
/*     */           
/*     */ 
/*  92 */           long lMsTimeToSeedFor = this.minTimeAlive;
/*  93 */           if (this.iTimed_MinSeedingTimeWithPeers > 0) {
/*  94 */             PeerManager peerManager = dl.getPeerManager();
/*  95 */             if (peerManager != null) {
/*  96 */               int connectedLeechers = peerManager.getStats().getConnectedLeechers();
/*  97 */               if (connectedLeechers > 0)
/*  98 */                 lMsTimeToSeedFor = this.iTimed_MinSeedingTimeWithPeers;
/*     */             }
/*     */           }
/*     */           long timeLeft;
/*     */           long timeLeft;
/* 103 */           if (dl.isForceStart()) {
/* 104 */             timeLeft = 31536000L; } else { long timeLeft;
/* 105 */             if (timeStarted <= 0L) {
/* 106 */               timeLeft = lMsTimeToSeedFor;
/*     */             } else
/* 108 */               timeLeft = lMsTimeToSeedFor - (SystemTime.getCurrentTime() - timeStarted);
/*     */           }
/* 110 */           sText = sText + TimeFormatter.format(timeLeft / 1000L);
/* 111 */         } else if (sr > 0L) {
/* 112 */           sText = sText + MessageText.getString("StartStopRules.waiting");
/*     */         }
/* 114 */       } else if (sr > 0L) {
/* 115 */         sText = sText + String.valueOf(sr);
/*     */       }
/* 117 */     } else if (sr == -6L) {
/* 118 */       sText = MessageText.getString("StartStopRules.FP0Peers");
/* 119 */     } else if (sr == -3L) {
/* 120 */       sText = MessageText.getString("StartStopRules.SPratioMet");
/* 121 */     } else if (sr == -4L) {
/* 122 */       sText = MessageText.getString("StartStopRules.ratioMet");
/* 123 */     } else if (sr == -5L) {
/* 124 */       sText = MessageText.getString("StartStopRules.numSeedsMet");
/* 125 */     } else if (sr == -2L) {
/* 126 */       sText = "";
/* 127 */     } else if (sr == -7L) {
/* 128 */       sText = MessageText.getString("StartStopRules.0Peers");
/* 129 */     } else if (sr == -8L) {
/* 130 */       sText = MessageText.getString("StartStopRules.shareRatioMet");
/*     */     } else {
/* 132 */       sText = "ERR" + sr;
/*     */     }
/*     */     
/* 135 */     if (SystemTime.getCurrentTime() - dl.getStats().getTimeStartedSeeding() < this.minTimeAlive)
/* 136 */       sText = "* " + sText;
/* 137 */     cell.setText(sText);
/* 138 */     if (this.bDebugLog) {
/* 139 */       cell.setToolTip("FP:\n" + dlData.sExplainFP + "\n" + "SR:" + dlData.sExplainSR + "\n" + "TRACE:\n" + dlData.sTrace);
/*     */     }
/*     */     else {
/* 142 */       cell.setToolTip(null);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void configurationSaved()
/*     */   {
/* 150 */     this.minTimeAlive = (this.pluginConfig.getUnsafeIntParameter("StartStopManager_iMinSeedingTime") * 1000);
/*     */     
/* 152 */     this.iTimed_MinSeedingTimeWithPeers = (this.pluginConfig.getUnsafeIntParameter("StartStopManager_iTimed_MinSeedingTimeWithPeers") * 1000);
/*     */     
/* 154 */     this.iRankType = this.pluginConfig.getUnsafeIntParameter("StartStopManager_iRankType");
/* 155 */     this.bDebugLog = this.pluginConfig.getUnsafeBooleanParameter("StartStopManager_bDebugLog");
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/plugins/startstoprules/defaultplugin/SeedingRankColumnListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */