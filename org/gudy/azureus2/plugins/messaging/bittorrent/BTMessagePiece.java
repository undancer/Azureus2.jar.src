/*    */ package org.gudy.azureus2.plugins.messaging.bittorrent;
/*    */ 
/*    */ import com.aelitis.azureus.core.peermanager.messaging.Message;
/*    */ import com.aelitis.azureus.core.peermanager.messaging.bittorrent.BTPiece;
/*    */ import java.nio.ByteBuffer;
/*    */ import org.gudy.azureus2.core3.util.DirectByteBuffer;
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
/*    */ 
/*    */ 
/*    */ public class BTMessagePiece
/*    */   extends MessageAdapter
/*    */ {
/*    */   private final BTPiece piece;
/*    */   
/*    */   protected BTMessagePiece(Message core_msg)
/*    */   {
/* 37 */     super(core_msg);
/* 38 */     this.piece = ((BTPiece)core_msg);
/*    */   }
/*    */   
/*    */ 
/* 42 */   public int getPieceNumber() { return this.piece.getPieceNumber(); }
/*    */   
/* 44 */   public int getPieceOffset() { return this.piece.getPieceOffset(); }
/*    */   
/* 46 */   public ByteBuffer getPieceData() { return this.piece.getPieceData().getBuffer((byte)1); }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/messaging/bittorrent/BTMessagePiece.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */