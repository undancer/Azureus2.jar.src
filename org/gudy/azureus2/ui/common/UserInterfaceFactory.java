/*    */ package org.gudy.azureus2.ui.common;
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
/*    */ public class UserInterfaceFactory
/*    */ {
/*    */   public static IUserInterface getUI(String ui)
/*    */   {
/* 30 */     IUserInterface cui = null;
/* 31 */     String uiclass = "org.gudy.azureus2.ui." + ui + ".UI";
/*    */     try {
/* 33 */       cui = (IUserInterface)Class.forName(uiclass).newInstance();
/*    */     } catch (ClassNotFoundException e) {
/* 35 */       throw new Error("Could not find class: " + uiclass);
/*    */     } catch (InstantiationException e) {
/* 37 */       throw new Error("Could not instantiate User Interface: " + uiclass);
/*    */     } catch (IllegalAccessException e) {
/* 39 */       throw new Error("Could not access User Interface: " + uiclass);
/*    */     }
/* 41 */     return cui;
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/common/UserInterfaceFactory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */