/*    */ package com.aelitis.azureus.ui.swt;
/*    */ 
/*    */ import java.util.ArrayList;
/*    */ import java.util.HashMap;
/*    */ import java.util.List;
/*    */ import java.util.Map;
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
/*    */ public class UISkinnableManagerSWT
/*    */ {
/* 30 */   static UISkinnableManagerSWT instance = new UISkinnableManagerSWT();
/*    */   
/*    */   public static UISkinnableManagerSWT getInstance() {
/* 33 */     return instance;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/* 40 */   private Map mapSkinnables = new HashMap();
/*    */   
/*    */   public UISkinnableSWTListener[] getSkinnableListeners(String id) {
/* 43 */     List listeners = (List)this.mapSkinnables.get(id);
/*    */     
/* 45 */     if (listeners == null) {
/* 46 */       return new UISkinnableSWTListener[0];
/*    */     }
/*    */     
/* 49 */     UISkinnableSWTListener[] skinListeners = new UISkinnableSWTListener[listeners.size()];
/* 50 */     skinListeners = (UISkinnableSWTListener[])listeners.toArray(skinListeners);
/* 51 */     return skinListeners;
/*    */   }
/*    */   
/*    */   public void addSkinnableListener(String id, UISkinnableSWTListener l) {
/* 55 */     List listeners = (List)this.mapSkinnables.get(id);
/*    */     
/* 57 */     if (listeners == null) {
/* 58 */       listeners = new ArrayList();
/* 59 */       listeners.add(l);
/* 60 */       this.mapSkinnables.put(id, listeners);
/*    */     } else {
/* 62 */       listeners.add(l);
/*    */     }
/*    */   }
/*    */   
/*    */   public void removeSkinnableListener(String id, UISkinnableSWTListener l) {
/* 67 */     List listeners = (List)this.mapSkinnables.get(id);
/*    */     
/* 69 */     if (listeners != null) {
/* 70 */       listeners.remove(l);
/*    */     }
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/UISkinnableManagerSWT.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */