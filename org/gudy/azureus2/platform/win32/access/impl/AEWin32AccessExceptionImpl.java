/*    */ package org.gudy.azureus2.platform.win32.access.impl;
/*    */ 
/*    */ import org.gudy.azureus2.platform.win32.access.AEWin32AccessException;
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
/*    */ 
/*    */ public class AEWin32AccessExceptionImpl
/*    */   extends AEWin32AccessException
/*    */ {
/*    */   public AEWin32AccessExceptionImpl(String operation, String message)
/*    */   {
/* 39 */     super(operation + ":" + message);
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/platform/win32/access/impl/AEWin32AccessExceptionImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */