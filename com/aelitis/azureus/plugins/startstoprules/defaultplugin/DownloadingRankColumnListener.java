/*    */ package com.aelitis.azureus.plugins.startstoprules.defaultplugin;
/*    */ 
/*    */ import org.gudy.azureus2.plugins.download.Download;
/*    */ import org.gudy.azureus2.plugins.ui.tables.TableCell;
/*    */ import org.gudy.azureus2.plugins.ui.tables.TableCellRefreshListener;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class DownloadingRankColumnListener
/*    */   implements TableCellRefreshListener
/*    */ {
/*    */   private StartStopRulesDefaultPlugin plugin;
/*    */   
/*    */   public DownloadingRankColumnListener(StartStopRulesDefaultPlugin _plugin)
/*    */   {
/* 41 */     this.plugin = _plugin;
/*    */   }
/*    */   
/*    */   public void refresh(TableCell cell) {
/* 45 */     Download dl = (Download)cell.getDataSource();
/* 46 */     if (dl == null) {
/* 47 */       return;
/*    */     }
/* 49 */     DefaultRankCalculator dlData = null;
/* 50 */     Object o = cell.getSortValue();
/* 51 */     if ((o instanceof DefaultRankCalculator)) {
/* 52 */       dlData = (DefaultRankCalculator)o;
/*    */     } else {
/* 54 */       dlData = StartStopRulesDefaultPlugin.getRankCalculator(dl);
/*    */     }
/* 56 */     if (dlData == null) {
/* 57 */       return;
/*    */     }
/* 59 */     int position = dlData.dl.getPosition();
/*    */     
/* 61 */     cell.setSortValue(position);
/*    */     
/* 63 */     cell.setText("" + position);
/* 64 */     if (this.plugin.bDebugLog) {
/* 65 */       String dlr = dlData.getDLRTrace();
/* 66 */       if (dlr.length() > 0) {
/* 67 */         dlr = "AR: " + dlr + "\n";
/*    */       }
/* 69 */       cell.setToolTip(dlr + "TRACE:\n" + dlData.sTrace);
/*    */     }
/*    */     else
/*    */     {
/* 73 */       cell.setToolTip(null);
/*    */     }
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/plugins/startstoprules/defaultplugin/DownloadingRankColumnListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */