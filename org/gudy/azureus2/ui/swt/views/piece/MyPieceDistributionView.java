/*    */ package org.gudy.azureus2.ui.swt.views.piece;
/*    */ 
/*    */ import org.gudy.azureus2.core3.download.DownloadManager;
/*    */ import org.gudy.azureus2.core3.peer.PEPiece;
/*    */ import org.gudy.azureus2.core3.util.AERunnable;
/*    */ import org.gudy.azureus2.ui.swt.Utils;
/*    */ import org.gudy.azureus2.ui.swt.views.PieceDistributionView;
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
/*    */ public class MyPieceDistributionView
/*    */   extends PieceDistributionView
/*    */ {
/*    */   public MyPieceDistributionView()
/*    */   {
/* 37 */     this.isMe = true;
/*    */   }
/*    */   
/*    */   public void dataSourceChanged(Object newDataSource) {
/* 41 */     if (((newDataSource instanceof Object[])) && (((Object[])newDataSource).length > 0))
/*    */     {
/* 43 */       newDataSource = ((Object[])(Object[])newDataSource)[0];
/*    */     }
/*    */     
/* 46 */     if ((newDataSource instanceof DownloadManager)) {
/* 47 */       this.pem = ((DownloadManager)newDataSource).getPeerManager();
/* 48 */     } else if ((newDataSource instanceof PEPiece)) {
/* 49 */       PEPiece piece = (PEPiece)newDataSource;
/* 50 */       this.pem = piece.getManager();
/*    */     } else {
/* 52 */       this.pem = null;
/*    */     }
/*    */     
/* 55 */     Utils.execSWTThread(new AERunnable() {
/*    */       public void runSupport() {
/* 57 */         MyPieceDistributionView.this.refresh();
/*    */       }
/*    */     });
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/piece/MyPieceDistributionView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */