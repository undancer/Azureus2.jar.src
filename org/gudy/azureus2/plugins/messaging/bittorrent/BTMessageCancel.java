/*    */ package org.gudy.azureus2.plugins.messaging.bittorrent;
/*    */ 
/*    */ import com.aelitis.azureus.core.peermanager.messaging.Message;
/*    */ import com.aelitis.azureus.core.peermanager.messaging.bittorrent.BTCancel;
/*    */ import org.gudy.azureus2.pluginsimpl.local.messaging.MessageAdapter;
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
/*    */ public class BTMessageCancel
/*    */   extends MessageAdapter
/*    */ {
/*    */   private final BTCancel cancel;
/*    */   
/*    */   protected BTMessageCancel(Message core_msg)
/*    */   {
/* 33 */     super(core_msg);
/* 34 */     this.cancel = ((BTCancel)core_msg);
/*    */   }
/*    */   
/*    */ 
/* 38 */   public int getPieceNumber() { return this.cancel.getPieceNumber(); }
/*    */   
/* 40 */   public int getPieceOffset() { return this.cancel.getPieceOffset(); }
/*    */   
/* 42 */   public int getLength() { return this.cancel.getLength(); }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/messaging/bittorrent/BTMessageCancel.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */