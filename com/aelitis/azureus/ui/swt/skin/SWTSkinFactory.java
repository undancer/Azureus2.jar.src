/*    */ package com.aelitis.azureus.ui.swt.skin;
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
/*    */ public class SWTSkinFactory
/*    */ {
/*    */   public static SWTSkin getInstance()
/*    */   {
/* 29 */     return SWTSkin.getDefaultInstance();
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public static SWTSkin getNonPersistentInstance(ClassLoader classLoader, String skinPath, String mainSkinFile)
/*    */   {
/* 38 */     return new SWTSkin(classLoader, skinPath, mainSkinFile);
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/skin/SWTSkinFactory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */