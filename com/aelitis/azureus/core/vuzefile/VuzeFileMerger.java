/*     */ package com.aelitis.azureus.core.vuzefile;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.PrintStream;
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
/*     */ 
/*     */ public class VuzeFileMerger
/*     */ {
/*     */   protected VuzeFileMerger(String[] args)
/*     */   {
/*  32 */     if (args.length != 1)
/*     */     {
/*  34 */       usage();
/*     */     }
/*     */     
/*  37 */     File input_dir = new File(args[0]);
/*     */     
/*  39 */     if (!input_dir.isDirectory())
/*     */     {
/*  41 */       usage();
/*     */     }
/*     */     try
/*     */     {
/*  45 */       File output_file = new File(args[0] + ".vuze");
/*     */       
/*  47 */       File[] files = input_dir.listFiles();
/*     */       
/*  49 */       VuzeFileHandler vfh = VuzeFileHandler.getSingleton();
/*     */       
/*  51 */       VuzeFile target = vfh.create();
/*     */       
/*  53 */       for (int i = 0; i < files.length; i++)
/*     */       {
/*  55 */         File f = files[i];
/*     */         
/*  57 */         if (!f.isDirectory())
/*     */         {
/*     */ 
/*     */ 
/*     */ 
/*  62 */           if (f.getName().endsWith(".vuze"))
/*     */           {
/*     */ 
/*     */ 
/*     */ 
/*  67 */             VuzeFile vf = vfh.loadVuzeFile(f.getAbsolutePath());
/*     */             
/*  69 */             System.out.println("Read " + f);
/*     */             
/*  71 */             VuzeFileComponent[] comps = vf.getComponents();
/*     */             
/*  73 */             for (int j = 0; j < comps.length; j++)
/*     */             {
/*  75 */               VuzeFileComponent comp = comps[j];
/*     */               
/*  77 */               target.addComponent(comp.getType(), comp.getContent());
/*     */               
/*  79 */               System.out.println("    added component: " + comp.getType());
/*     */             }
/*     */           } }
/*     */       }
/*  83 */       target.write(output_file);
/*     */       
/*  85 */       System.out.println("Wrote " + output_file);
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/*  89 */       System.err.print("Failed to merge vuze files");
/*     */       
/*  91 */       e.printStackTrace();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   protected void usage()
/*     */   {
/*  98 */     System.err.println("Usage: <dir_of_vuze_files_to_merge>");
/*     */     
/* 100 */     System.exit(1);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static void main(String[] args)
/*     */   {
/* 107 */     new VuzeFileMerger(args);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/vuzefile/VuzeFileMerger.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */