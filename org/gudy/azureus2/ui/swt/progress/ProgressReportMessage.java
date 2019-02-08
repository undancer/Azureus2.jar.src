/*    */ package org.gudy.azureus2.ui.swt.progress;
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
/*    */ public class ProgressReportMessage
/*    */   implements IMessage, IProgressReportConstants
/*    */ {
/* 30 */   private String value = "";
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   private int type;
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public ProgressReportMessage(String value, int type)
/*    */   {
/* 45 */     this.value = value;
/*    */     
/* 47 */     switch (type) {
/*    */     case 2: 
/*    */     case 4: 
/* 50 */       this.type = type;
/* 51 */       break;
/*    */     default: 
/* 53 */       this.type = 8;
/*    */     }
/*    */   }
/*    */   
/*    */   public String getValue() {
/* 58 */     return this.value;
/*    */   }
/*    */   
/*    */   public int getType() {
/* 62 */     return this.type;
/*    */   }
/*    */   
/*    */   public boolean isError() {
/* 66 */     return this.type == 4;
/*    */   }
/*    */   
/*    */   public boolean isInfo() {
/* 70 */     return this.type == 2;
/*    */   }
/*    */   
/*    */   public boolean isLog() {
/* 74 */     return this.type == 8;
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/progress/ProgressReportMessage.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */