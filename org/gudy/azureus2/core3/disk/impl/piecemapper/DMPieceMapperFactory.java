/*    */ package org.gudy.azureus2.core3.disk.impl.piecemapper;
/*    */ 
/*    */ import org.gudy.azureus2.core3.disk.impl.piecemapper.impl.PieceMapperImpl;
/*    */ import org.gudy.azureus2.core3.torrent.TOTorrent;
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
/*    */ public class DMPieceMapperFactory
/*    */ {
/*    */   public static DMPieceMapper create(TOTorrent torrent)
/*    */   {
/* 32 */     return new PieceMapperImpl(torrent);
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/disk/impl/piecemapper/DMPieceMapperFactory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */