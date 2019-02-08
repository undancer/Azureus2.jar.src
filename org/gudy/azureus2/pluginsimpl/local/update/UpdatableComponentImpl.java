/*    */ package org.gudy.azureus2.pluginsimpl.local.update;
/*    */ 
/*    */ import org.gudy.azureus2.plugins.update.UpdatableComponent;
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
/*    */ 
/*    */ public class UpdatableComponentImpl
/*    */ {
/*    */   protected UpdatableComponent comp;
/*    */   protected boolean mandatory;
/*    */   
/*    */   protected UpdatableComponentImpl(UpdatableComponent _comp, boolean _mandatory)
/*    */   {
/* 40 */     this.comp = _comp;
/* 41 */     this.mandatory = _mandatory;
/*    */   }
/*    */   
/*    */ 
/*    */   protected UpdatableComponent getComponent()
/*    */   {
/* 47 */     return this.comp;
/*    */   }
/*    */   
/*    */ 
/*    */   protected boolean isMandatory()
/*    */   {
/* 53 */     return this.mandatory;
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/pluginsimpl/local/update/UpdatableComponentImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */