/*    */ package org.gudy.azureus2.ui.swt.views.peer;
/*    */ 
/*    */ import com.aelitis.azureus.core.peermanager.piecepicker.util.BitFlags;
/*    */ import java.util.Arrays;
/*    */ import org.gudy.azureus2.core3.peer.PEPeer;
/*    */ import org.gudy.azureus2.core3.peer.PEPeerManager;
/*    */ import org.gudy.azureus2.core3.peer.impl.transport.PEPeerTransportProtocol;
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
/*    */ public class RemotePieceDistributionView
/*    */   extends PieceDistributionView
/*    */ {
/* 34 */   private PEPeer peer = null;
/*    */   
/*    */ 
/*    */ 
/*    */   public void dataSourceChanged(Object newDataSource)
/*    */   {
/* 40 */     if (((newDataSource instanceof Object[])) && (((Object[])newDataSource).length > 0))
/*    */     {
/* 42 */       newDataSource = ((Object[])(Object[])newDataSource)[0];
/*    */     }
/*    */     
/* 45 */     if ((newDataSource instanceof PEPeer)) {
/* 46 */       this.peer = ((PEPeer)newDataSource);
/* 47 */       this.pem = this.peer.getManager();
/*    */     } else {
/* 49 */       this.peer = null;
/* 50 */       this.pem = null;
/*    */     }
/*    */     
/* 53 */     Utils.execSWTThread(new AERunnable() {
/*    */       public void runSupport() {
/* 55 */         RemotePieceDistributionView.this.refresh();
/*    */       }
/*    */     });
/*    */   }
/*    */   
/*    */   public void refresh() {
/* 61 */     if (this.pem == null)
/* 62 */       return;
/* 63 */     if ((this.peer instanceof PEPeerTransportProtocol))
/*    */     {
/* 65 */       PEPeerTransportProtocol pet = (PEPeerTransportProtocol)this.peer;
/* 66 */       BitFlags avl = pet.getAvailable();
/* 67 */       if (avl == null) {
/* 68 */         this.hasPieces = null;
/*    */       } else {
/* 70 */         this.hasPieces = avl.flags;
/*    */       }
/*    */     }
/* 73 */     else if (this.peer.isSeed())
/*    */     {
/* 75 */       this.hasPieces = new boolean[this.pem.getPieces().length];
/* 76 */       Arrays.fill(this.hasPieces, true);
/*    */     } else {
/* 78 */       this.hasPieces = null;
/*    */     }
/* 80 */     super.refresh();
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/peer/RemotePieceDistributionView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */