/*    */ package com.aelitis.azureus.ui.swt.skin;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public abstract interface SWTSkinObjectListener
/*    */ {
/*    */   public static final int EVENT_SHOW = 0;
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public static final int EVENT_HIDE = 1;
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public static final int EVENT_SELECT = 2;
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public static final int EVENT_DESTROY = 3;
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public static final int EVENT_CREATED = 4;
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public static final int EVENT_CREATE_REQUEST = 5;
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public static final int EVENT_LANGUAGE_CHANGE = 6;
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public static final int EVENT_DATASOURCE_CHANGED = 7;
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/* 71 */   public static final String[] NAMES = { "Show", "Hide", "Select", "Destroy", "Created", "Create Request", "Lang Change", "DS Change" };
/*    */   
/*    */   public abstract Object eventOccured(SWTSkinObject paramSWTSkinObject, int paramInt, Object paramObject);
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/skin/SWTSkinObjectListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */