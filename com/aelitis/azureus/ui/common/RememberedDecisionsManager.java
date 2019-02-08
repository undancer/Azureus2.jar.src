/*     */ package com.aelitis.azureus.ui.common;
/*     */ 
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager.ResetToDefaultsListener;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class RememberedDecisionsManager
/*     */ {
/*     */   static
/*     */   {
/*  35 */     COConfigurationManager.addResetToDefaultsListener(new COConfigurationManager.ResetToDefaultsListener()
/*     */     {
/*     */       public void reset() {}
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static void clearAll()
/*     */   {
/*  54 */     COConfigurationManager.setParameter("MessageBoxWindow.decisions", new HashMap());
/*     */   }
/*     */   
/*     */   public static int getRememberedDecision(String id) {
/*  58 */     return getRememberedDecision(id, -1);
/*     */   }
/*     */   
/*     */   public static int getRememberedDecision(String id, int onlyIfInMask) {
/*  62 */     if ((id == null) || (onlyIfInMask == 0)) {
/*  63 */       return -1;
/*     */     }
/*  65 */     Map remembered_decisions = COConfigurationManager.getMapParameter("MessageBoxWindow.decisions", new HashMap());
/*     */     
/*     */ 
/*  68 */     Long l = (Long)remembered_decisions.get(id);
/*     */     
/*  70 */     if (l != null) {
/*  71 */       int i = l.intValue();
/*  72 */       if ((onlyIfInMask == -1) || ((i & onlyIfInMask) != 0)) {
/*  73 */         return i;
/*     */       }
/*     */     }
/*     */     
/*  77 */     return -1;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static void setRemembered(String id, int value)
/*     */   {
/*  90 */     if (id == null) {
/*  91 */       return;
/*     */     }
/*     */     
/*  94 */     Map remembered_decisions = COConfigurationManager.getMapParameter("MessageBoxWindow.decisions", new HashMap());
/*     */     
/*     */ 
/*  97 */     if (value == -1) {
/*  98 */       remembered_decisions.remove(id);
/*     */     } else {
/* 100 */       remembered_decisions.put(id, new Long(value));
/*     */     }
/*     */     
/*     */ 
/* 104 */     COConfigurationManager.setParameter("MessageBoxWindow.decisions", remembered_decisions);
/*     */     
/* 106 */     COConfigurationManager.save();
/*     */   }
/*     */   
/*     */   public static void ensureLoaded() {}
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/common/RememberedDecisionsManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */