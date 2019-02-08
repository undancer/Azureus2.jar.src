/*    */ package org.gudy.azureus2.core3.disk.impl.access.impl;
/*    */ 
/*    */ import org.gudy.azureus2.core3.disk.DiskManagerCheckRequest;
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
/*    */ public class DiskManagerCheckRequestImpl
/*    */   extends DiskManagerRequestImpl
/*    */   implements DiskManagerCheckRequest
/*    */ {
/*    */   private final int piece_number;
/*    */   private final Object user_data;
/*    */   private boolean low_priority;
/* 32 */   private boolean ad_hoc = true;
/*    */   
/*    */ 
/*    */   private byte[] hash;
/*    */   
/*    */ 
/*    */ 
/*    */   public DiskManagerCheckRequestImpl(int _piece_number, Object _user_data)
/*    */   {
/* 41 */     this.piece_number = _piece_number;
/* 42 */     this.user_data = _user_data;
/*    */   }
/*    */   
/*    */ 
/*    */   protected String getName()
/*    */   {
/* 48 */     return "Check: " + this.piece_number + ",lp=" + this.low_priority + ",ah=" + this.ad_hoc;
/*    */   }
/*    */   
/*    */ 
/*    */   public int getPieceNumber()
/*    */   {
/* 54 */     return this.piece_number;
/*    */   }
/*    */   
/*    */ 
/*    */   public Object getUserData()
/*    */   {
/* 60 */     return this.user_data;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public void setLowPriority(boolean low)
/*    */   {
/* 67 */     this.low_priority = low;
/*    */   }
/*    */   
/*    */ 
/*    */   public boolean isLowPriority()
/*    */   {
/* 73 */     return this.low_priority;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public void setAdHoc(boolean _ad_hoc)
/*    */   {
/* 80 */     this.ad_hoc = _ad_hoc;
/*    */   }
/*    */   
/*    */ 
/*    */   public boolean isAdHoc()
/*    */   {
/* 86 */     return this.ad_hoc;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public void setHash(byte[] _hash)
/*    */   {
/* 93 */     this.hash = _hash;
/*    */   }
/*    */   
/*    */ 
/*    */   public byte[] getHash()
/*    */   {
/* 99 */     return this.hash;
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/disk/impl/access/impl/DiskManagerCheckRequestImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */