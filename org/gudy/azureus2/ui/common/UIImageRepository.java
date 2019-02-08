/*    */ package org.gudy.azureus2.ui.common;
/*    */ 
/*    */ import java.io.InputStream;
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
/*    */ 
/*    */ 
/*    */ public class UIImageRepository
/*    */ {
/*    */   public static InputStream getImageAsStream(String name)
/*    */   {
/* 40 */     return UIImageRepository.class.getClassLoader().getResourceAsStream("org/gudy/azureus2/ui/icons/" + name);
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/common/UIImageRepository.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */