/*    */ package com.aelitis.azureus.ui.common.viewtitleinfo;
/*    */ 
/*    */ import com.aelitis.azureus.core.util.CopyOnWriteList;
/*    */ import org.gudy.azureus2.core3.util.Debug;
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
/*    */ public class ViewTitleInfoManager
/*    */ {
/* 34 */   public static CopyOnWriteList<ViewTitleInfoListener> listeners = new CopyOnWriteList();
/*    */   
/*    */   public static void addListener(ViewTitleInfoListener l) {
/* 37 */     listeners.addIfNotPresent(l);
/*    */   }
/*    */   
/*    */   public static void removeListener(ViewTitleInfoListener l) {
/* 41 */     listeners.remove(l);
/*    */   }
/*    */   
/*    */   public static void refreshTitleInfo(ViewTitleInfo titleinfo) {
/* 45 */     if (titleinfo == null) {
/* 46 */       return;
/*    */     }
/*    */     
/* 49 */     for (ViewTitleInfoListener l : listeners) {
/*    */       try
/*    */       {
/* 52 */         l.viewTitleInfoRefresh(titleinfo);
/*    */       }
/*    */       catch (Throwable e)
/*    */       {
/* 56 */         Debug.out(e);
/*    */       }
/*    */     }
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/common/viewtitleinfo/ViewTitleInfoManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */