/*    */ package org.gudy.azureus2.pluginsimpl.local.ui;
/*    */ 
/*    */ import org.gudy.azureus2.plugins.PluginInterface;
/*    */ import org.gudy.azureus2.plugins.ui.UIManagerEvent;
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
/*    */ public class UIManagerEventAdapter
/*    */   implements UIManagerEvent
/*    */ {
/*    */   private PluginInterface pi;
/*    */   private int type;
/*    */   private Object data;
/*    */   private Object result;
/*    */   
/*    */   public UIManagerEventAdapter(PluginInterface _pi, int _type, Object _data)
/*    */   {
/* 40 */     this.pi = _pi;
/* 41 */     this.type = _type;
/* 42 */     this.data = _data;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   protected PluginInterface getPluginInterface()
/*    */   {
/* 52 */     return this.pi;
/*    */   }
/*    */   
/*    */ 
/*    */   public int getType()
/*    */   {
/* 58 */     return this.type;
/*    */   }
/*    */   
/*    */ 
/*    */   public Object getData()
/*    */   {
/* 64 */     return this.data;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */   public void setResult(Object _result)
/*    */   {
/* 71 */     this.result = _result;
/*    */   }
/*    */   
/*    */ 
/*    */   public Object getResult()
/*    */   {
/* 77 */     return this.result;
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/pluginsimpl/local/ui/UIManagerEventAdapter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */