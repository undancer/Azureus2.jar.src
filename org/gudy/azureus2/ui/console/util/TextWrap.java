/*    */ package org.gudy.azureus2.ui.console.util;
/*    */ 
/*    */ import java.io.PrintStream;
/*    */ import java.util.Iterator;
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
/*    */ public class TextWrap
/*    */ {
/*    */   public static void printList(Iterator text_segments, PrintStream out, String space_between_commands)
/*    */   {
/* 27 */     StringBuffer command_line_so_far = new StringBuffer("  ");
/* 28 */     while (text_segments.hasNext()) {
/* 29 */       String next_command = (String)text_segments.next();
/* 30 */       int current_length = command_line_so_far.length();
/* 31 */       if (current_length + next_command.length() + space_between_commands.length() > 79) {
/* 32 */         out.println(command_line_so_far);
/* 33 */         command_line_so_far.setLength(2);
/*    */       }
/* 35 */       command_line_so_far.append(next_command);
/* 36 */       command_line_so_far.append(space_between_commands);
/*    */     }
/* 38 */     if (command_line_so_far.length() > 2) {
/* 39 */       out.println(command_line_so_far);
/*    */     }
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/console/util/TextWrap.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */