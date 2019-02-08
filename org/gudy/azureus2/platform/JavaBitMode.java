/*    */ package org.gudy.azureus2.platform;
/*    */ 
/*    */ import java.io.PrintStream;
/*    */ import org.gudy.azureus2.core3.util.Constants;
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
/*    */ public class JavaBitMode
/*    */ {
/*    */   public static void main(String[] args)
/*    */   {
/* 26 */     String prop = System.getProperty("sun.arch.data.model");
/* 27 */     if (prop == null) prop = System.getProperty("com.ibm.vm.bitmode");
/* 28 */     if (prop == null) prop = Constants.is64Bit ? "64" : "32";
/* 29 */     System.out.print(prop);
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/platform/JavaBitMode.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */