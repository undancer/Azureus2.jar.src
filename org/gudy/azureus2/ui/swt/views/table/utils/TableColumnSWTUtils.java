/*    */ package org.gudy.azureus2.ui.swt.views.table.utils;
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
/*    */ public class TableColumnSWTUtils
/*    */ {
/*    */   public static int convertColumnAlignmentToSWT(int align)
/*    */   {
/* 32 */     int swt = 0;
/* 33 */     int hAlign = align & 0x3;
/* 34 */     if (hAlign == 3) {
/* 35 */       swt = 16777216;
/* 36 */     } else if (hAlign == 1) {
/* 37 */       swt = 16384;
/* 38 */     } else if (hAlign == 2) {
/* 39 */       swt = 131072;
/*    */     } else {
/* 41 */       swt = 16384;
/*    */     }
/* 43 */     int vAlign = align & 0xFFFFFFFC;
/* 44 */     if (vAlign == 4) {
/* 45 */       swt |= 0x80;
/* 46 */     } else if (vAlign == 8) {
/* 47 */       swt |= 0x400;
/*    */     }
/* 49 */     return swt;
/*    */   }
/*    */   
/*    */   private static int convertSWTAlignmentToColumn(int align) {
/* 53 */     if ((align & 0x4000) != 0)
/* 54 */       return 1;
/* 55 */     if ((align & 0x1000000) != 0)
/* 56 */       return 3;
/* 57 */     if ((align & 0x20000) != 0) {
/* 58 */       return 2;
/*    */     }
/* 60 */     return 1;
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/table/utils/TableColumnSWTUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */