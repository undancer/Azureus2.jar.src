/*    */ package org.gudy.azureus2.ui.swt.osx;
/*    */ 
/*    */ import java.io.OutputStreamWriter;
/*    */ import java.io.PrintStream;
/*    */ import java.io.PrintWriter;
/*    */ import java.net.Socket;
/*    */ import org.gudy.azureus2.core3.util.Constants;
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
/*    */ public class Start
/*    */ {
/*    */   public Start(String[] args)
/*    */   {
/* 34 */     Socket sck = null;
/* 35 */     PrintWriter pw = null;
/*    */     try {
/* 37 */       System.out.println("StartSocket: passing startup args to already-running Azureus java process.");
/*    */       
/* 39 */       sck = new Socket("127.0.0.1", Constants.INSTANCE_PORT);
/*    */       
/* 41 */       pw = new PrintWriter(new OutputStreamWriter(sck.getOutputStream(), "UTF8"));
/*    */       
/* 43 */       StringBuilder buffer = new StringBuilder("Azureus Start Server Access;args;");
/*    */       
/* 45 */       for (int i = 0; i < args.length; i++) {
/* 46 */         String arg = args[i].replaceAll("&", "&&").replaceAll(";", "&;");
/* 47 */         buffer.append(arg);
/* 48 */         buffer.append(';');
/*    */       }
/*    */       
/* 51 */       pw.println(buffer.toString());
/* 52 */       pw.flush(); return;
/*    */     } catch (Exception e) {
/* 54 */       Debug.printStackTrace(e);
/*    */     } finally {
/*    */       try {
/* 57 */         if (pw != null) {
/* 58 */           pw.close();
/*    */         }
/*    */       } catch (Exception e) {}
/*    */       try {
/* 62 */         if (sck != null) {
/* 63 */           sck.close();
/*    */         }
/*    */       } catch (Exception e) {}
/*    */     }
/*    */   }
/*    */   
/*    */   public static void main(String[] args) {
/* 70 */     new Start(args);
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/osx/Start.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */