/*    */ package org.gudy.azureus2.ui.console.commands;
/*    */ 
/*    */ import java.io.FileOutputStream;
/*    */ import java.io.PrintStream;
/*    */ import java.util.List;
/*    */ import org.gudy.azureus2.core3.stats.StatsWriterFactory;
/*    */ import org.gudy.azureus2.core3.stats.StatsWriterStreamer;
/*    */ import org.gudy.azureus2.ui.console.ConsoleInput;
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
/*    */ public class XML
/*    */   extends IConsoleCommand
/*    */ {
/*    */   public XML()
/*    */   {
/* 26 */     super("xml");
/*    */   }
/*    */   
/*    */   public String getCommandDescriptions() {
/* 30 */     return "xml [<file>]\t\t\t\tOutput stats in xml format (to <file> if given)";
/*    */   }
/*    */   
/*    */   public void execute(String commandName, ConsoleInput ci, List args) {
/* 34 */     StatsWriterStreamer sws = StatsWriterFactory.createStreamer(ci.getCore());
/* 35 */     String file = null;
/* 36 */     if ((args != null) && (!args.isEmpty()))
/* 37 */       file = (String)args.get(0);
/* 38 */     if (file == null) {
/*    */       try {
/* 40 */         ci.out.println("> -----");
/* 41 */         sws.write(ci.out);
/* 42 */         ci.out.println("> -----");
/*    */       } catch (Exception e) {
/* 44 */         ci.out.println("> Exception while trying to output xml stats:" + e.getMessage());
/*    */       }
/*    */     } else {
/*    */       try {
/* 48 */         FileOutputStream os = new FileOutputStream(file);
/*    */         
/*    */         try
/*    */         {
/* 52 */           sws.write(os);
/*    */         }
/*    */         finally
/*    */         {
/* 56 */           os.close();
/*    */         }
/* 58 */         ci.out.println("> XML stats successfully written to " + file);
/*    */       } catch (Exception e) {
/* 60 */         ci.out.println("> Exception while trying to write xml stats:" + e.getMessage());
/*    */       }
/*    */     }
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/console/commands/XML.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */