/*    */ package com.aelitis.azureus.core.peermanager.piecepicker;
/*    */ 
/*    */ import com.aelitis.azureus.core.peermanager.piecepicker.impl.PiecePickerImpl;
/*    */ import org.gudy.azureus2.core3.peer.impl.PEPeerControl;
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
/*    */ public class PiecePickerFactory
/*    */ {
/*    */   public static PiecePicker create(PEPeerControl peer_control)
/*    */   {
/* 37 */     return new PiecePickerImpl(peer_control);
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/peermanager/piecepicker/PiecePickerFactory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */