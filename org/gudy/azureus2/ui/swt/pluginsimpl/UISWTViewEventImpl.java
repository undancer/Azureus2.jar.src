/*    */ package org.gudy.azureus2.ui.swt.pluginsimpl;
/*    */ 
/*    */ import org.gudy.azureus2.ui.swt.plugins.UISWTView;
/*    */ import org.gudy.azureus2.ui.swt.plugins.UISWTViewEvent;
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
/*    */ public class UISWTViewEventImpl
/*    */   implements UISWTViewEvent
/*    */ {
/*    */   int eventType;
/*    */   Object data;
/*    */   UISWTView view;
/*    */   String parentID;
/*    */   
/*    */   public UISWTViewEventImpl(String parentID, UISWTView view, int eventType, Object data)
/*    */   {
/* 40 */     this.parentID = parentID;
/* 41 */     this.view = view;
/* 42 */     this.eventType = eventType;
/* 43 */     this.data = data;
/*    */   }
/*    */   
/*    */   public int getType() {
/* 47 */     return this.eventType;
/*    */   }
/*    */   
/*    */ 
/*    */   public String getParentID()
/*    */   {
/* 53 */     return this.parentID;
/*    */   }
/*    */   
/*    */   public Object getData() {
/* 57 */     return this.data;
/*    */   }
/*    */   
/*    */   public UISWTView getView() {
/* 61 */     return this.view;
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/pluginsimpl/UISWTViewEventImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */