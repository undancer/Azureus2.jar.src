/*    */ package com.aelitis.azureus.ui.common.table.impl;
/*    */ 
/*    */ import com.aelitis.azureus.ui.common.table.TableRowCore;
/*    */ import java.util.Comparator;
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
/*    */ public class TableRowCoreSorter
/*    */   implements Comparator<TableRowCore>
/*    */ {
/*    */   public int compare(TableRowCore o1, TableRowCore o2)
/*    */   {
/* 30 */     TableRowCore parent1 = o1.getParentRowCore();
/* 31 */     TableRowCore parent2 = o2.getParentRowCore();
/* 32 */     boolean hasParent1 = parent1 != null;
/* 33 */     boolean hasParent2 = parent2 != null;
/*    */     
/* 35 */     if ((parent1 == parent2) || ((!hasParent1) && (!hasParent2))) {
/* 36 */       return o1.getIndex() - o2.getIndex();
/*    */     }
/* 38 */     if ((hasParent1) && (hasParent2)) {
/* 39 */       return parent1.getIndex() - parent2.getIndex();
/*    */     }
/* 41 */     if (hasParent1) {
/* 42 */       if (parent1 == o2) {
/* 43 */         return 1;
/*    */       }
/* 45 */       return parent1.getIndex() - o2.getIndex();
/*    */     }
/* 47 */     if (o1 == parent2) {
/* 48 */       return 0;
/*    */     }
/* 50 */     return o1.getIndex() - parent2.getIndex();
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/common/table/impl/TableRowCoreSorter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */