/*     */ package com.aelitis.azureus.ui.swt.skin;
/*     */ 
/*     */ import org.gudy.azureus2.core3.util.Debug;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ public class SWTSkinObjectAdapter
/*     */   implements SWTSkinObjectListener
/*     */ {
/*     */   public Object skinObjectShown(SWTSkinObject skinObject, Object params)
/*     */   {
/*  36 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public Object skinObjectHidden(SWTSkinObject skinObject, Object params)
/*     */   {
/*  43 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public Object skinObjectSelected(SWTSkinObject skinObject, Object params)
/*     */   {
/*  50 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public Object skinObjectDestroyed(SWTSkinObject skinObject, Object params)
/*     */   {
/*  57 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public Object skinObjectCreated(SWTSkinObject skinObject, Object params)
/*     */   {
/*  64 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public Object updateLanguage(SWTSkinObject skinObject, Object params)
/*     */   {
/*  71 */     return null;
/*     */   }
/*     */   
/*     */   public Object dataSourceChanged(SWTSkinObject skinObject, Object params) {
/*  75 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public Object eventOccured(SWTSkinObject skinObject, int eventType, Object params)
/*     */   {
/*     */     try
/*     */     {
/*  84 */       switch (eventType) {
/*     */       case 0: 
/*  86 */         return skinObjectShown(skinObject, params);
/*     */       
/*     */       case 1: 
/*  89 */         return skinObjectHidden(skinObject, params);
/*     */       
/*     */       case 2: 
/*  92 */         return skinObjectSelected(skinObject, params);
/*     */       
/*     */       case 3: 
/*  95 */         return skinObjectDestroyed(skinObject, params);
/*     */       
/*     */       case 4: 
/*  98 */         return skinObjectCreated(skinObject, params);
/*     */       
/*     */       case 6: 
/* 101 */         return updateLanguage(skinObject, params);
/*     */       
/*     */       case 7: 
/* 104 */         return dataSourceChanged(skinObject, params);
/*     */       }
/*     */       
/* 107 */       return null;
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/* 111 */       Debug.out("Skin Event " + NAMES[eventType] + " caused an error", e);
/*     */     }
/* 113 */     return null;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/skin/SWTSkinObjectAdapter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */