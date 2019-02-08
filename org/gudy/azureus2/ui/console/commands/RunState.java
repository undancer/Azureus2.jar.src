/*     */ package org.gudy.azureus2.ui.console.commands;
/*     */ 
/*     */ import java.io.PrintStream;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import org.gudy.azureus2.core3.util.AERunStateHandler;
/*     */ import org.gudy.azureus2.ui.console.ConsoleInput;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class RunState
/*     */   extends IConsoleCommand
/*     */ {
/*     */   public RunState()
/*     */   {
/*  25 */     super("runstate", "rs");
/*     */   }
/*     */   
/*     */   public String getCommandDescriptions()
/*     */   {
/*  30 */     return "runstate\t\trs\tShows and modified the current Vuze run-state.";
/*     */   }
/*     */   
/*     */   public void execute(String commandName, ConsoleInput ci, List<String> args)
/*     */   {
/*  35 */     ci.out.println("Current run state:");
/*     */     
/*  37 */     long mode = AERunStateHandler.getResourceMode();
/*     */     
/*  39 */     Map<String, Long> mode_map = new HashMap();
/*     */     
/*  41 */     mode_map.put("all", Long.valueOf(-1L));
/*     */     
/*  43 */     for (int i = 0; i < AERunStateHandler.RS_MODES.length; i++)
/*     */     {
/*  45 */       String mode_name = AERunStateHandler.RS_MODE_NAMES[i];
/*  46 */       long mode_value = AERunStateHandler.RS_MODES[i];
/*     */       
/*  48 */       String[] bits = mode_name.split(":");
/*     */       
/*  50 */       for (String bit : bits)
/*     */       {
/*  52 */         mode_map.put(bit.trim().toLowerCase(), Long.valueOf(mode_value));
/*     */       }
/*     */     }
/*     */     
/*  56 */     boolean bad = false;
/*     */     
/*  58 */     for (String arg : args)
/*     */     {
/*  60 */       String[] bits = arg.split("=");
/*     */       
/*  62 */       if (bits.length != 2)
/*     */       {
/*  64 */         bad = true;
/*     */         
/*  66 */         break;
/*     */       }
/*     */       
/*  69 */       Long this_mode = (Long)mode_map.get(bits[0].toLowerCase());
/*     */       
/*  71 */       if (this_mode == null)
/*     */       {
/*  73 */         bad = true;
/*     */       }
/*     */       else
/*     */       {
/*  77 */         boolean on = false;
/*     */         
/*  79 */         String rhs = bits[1].toLowerCase();
/*     */         
/*  81 */         if (rhs.equals("on"))
/*     */         {
/*  83 */           on = true;
/*     */         }
/*  85 */         else if (!rhs.equals("off"))
/*     */         {
/*     */ 
/*     */ 
/*  89 */           bad = true;
/*     */         }
/*     */         
/*  92 */         if (!bad)
/*     */         {
/*  94 */           if (on)
/*     */           {
/*  96 */             mode |= this_mode.longValue();
/*     */           } else {
/*  98 */             mode &= (this_mode.longValue() ^ 0xFFFFFFFFFFFFFFFF);
/*     */           }
/*     */           
/* 101 */           AERunStateHandler.setResourceMode(mode);
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 106 */     if (bad)
/*     */     {
/* 108 */       ci.out.println("> Command 'runstate': invalid parameters (example: dui=On, all=off)");
/*     */     }
/*     */     else
/*     */     {
/* 112 */       for (int i = 0; i < AERunStateHandler.RS_MODES.length; i++)
/*     */       {
/* 114 */         String mode_name = AERunStateHandler.RS_MODE_NAMES[i];
/* 115 */         long mode_value = AERunStateHandler.RS_MODES[i];
/*     */         
/* 117 */         ci.out.println("\t" + mode_name + "=" + ((mode & mode_value) == 0L ? "Off" : "On"));
/*     */       }
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/console/commands/RunState.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */