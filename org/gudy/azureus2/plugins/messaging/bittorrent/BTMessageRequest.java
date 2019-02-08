/*    */ package org.gudy.azureus2.plugins.messaging.bittorrent;
/*    */ 
/*    */ import com.aelitis.azureus.core.peermanager.messaging.Message;
/*    */ import com.aelitis.azureus.core.peermanager.messaging.bittorrent.BTRequest;
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
/*    */ public class BTMessageRequest
/*    */   extends MessageAdapter
/*    */ {
/*    */   private final BTRequest request;
/*    */   
/*    */   protected BTMessageRequest(Message core_msg)
/*    */   {
/* 33 */     super(core_msg);
/* 34 */     this.request = ((BTRequest)core_msg);
/*    */   }
/*    */   
/*    */ 
/* 38 */   public int getPieceNumber() { return this.request.getPieceNumber(); }
/*    */   
/* 40 */   public int getPieceOffset() { return this.request.getPieceOffset(); }
/*    */   
/* 42 */   public int getLength() { return this.request.getLength(); }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/messaging/bittorrent/BTMessageRequest.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */