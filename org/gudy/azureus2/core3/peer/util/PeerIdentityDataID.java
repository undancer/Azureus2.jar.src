/*    */ package org.gudy.azureus2.core3.peer.util;
/*    */ 
/*    */ import java.util.Arrays;
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
/*    */ public class PeerIdentityDataID
/*    */ {
/*    */   private final byte[] dataId;
/*    */   private final int hashcode;
/*    */   private PeerIdentityManager.DataEntry data_entry;
/*    */   
/*    */   protected PeerIdentityDataID(byte[] _data_id)
/*    */   {
/* 39 */     this.dataId = _data_id;
/*    */     
/* 41 */     this.hashcode = new String(this.dataId).hashCode();
/*    */   }
/*    */   
/*    */ 
/*    */   public byte[] getDataID()
/*    */   {
/* 47 */     return this.dataId;
/*    */   }
/*    */   
/*    */ 
/*    */   protected PeerIdentityManager.DataEntry getDataEntry()
/*    */   {
/* 53 */     return this.data_entry;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   protected void setDataEntry(PeerIdentityManager.DataEntry d)
/*    */   {
/* 60 */     this.data_entry = d;
/*    */   }
/*    */   
/*    */   public boolean equals(Object obj) {
/* 64 */     if (this == obj) return true;
/* 65 */     if ((obj != null) && ((obj instanceof PeerIdentityDataID))) {
/* 66 */       PeerIdentityDataID other = (PeerIdentityDataID)obj;
/* 67 */       return Arrays.equals(this.dataId, other.dataId);
/*    */     }
/* 69 */     return false;
/*    */   }
/*    */   
/*    */   public int hashCode() {
/* 73 */     return this.hashcode;
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/peer/util/PeerIdentityDataID.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */