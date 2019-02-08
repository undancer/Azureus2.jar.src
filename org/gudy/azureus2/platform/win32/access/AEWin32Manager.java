/*    */ package org.gudy.azureus2.platform.win32.access;
/*    */ 
/*    */ import org.gudy.azureus2.platform.win32.access.impl.AEWin32AccessImpl;
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
/*    */ public class AEWin32Manager
/*    */ {
/*    */   public static AEWin32Access getAccessor(boolean fully_initialise)
/*    */   {
/* 36 */     return AEWin32AccessImpl.getSingleton(fully_initialise);
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/platform/win32/access/AEWin32Manager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */