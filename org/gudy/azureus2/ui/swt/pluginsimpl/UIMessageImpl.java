/*     */ package org.gudy.azureus2.ui.swt.pluginsimpl;
/*     */ 
/*     */ import org.gudy.azureus2.pluginsimpl.local.ui.AbstractUIMessage;
/*     */ import org.gudy.azureus2.ui.swt.Utils;
/*     */ import org.gudy.azureus2.ui.swt.shells.MessageBoxShell;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class UIMessageImpl
/*     */   extends AbstractUIMessage
/*     */ {
/*     */   public int ask()
/*     */   {
/*  38 */     final int[] result = new int[1];
/*  39 */     Utils.execSWTThread(new Runnable()
/*     */     {
/*  41 */       public void run() { result[0] = UIMessageImpl.this.ask0(); } }, false);
/*     */     
/*     */ 
/*  44 */     return result[0];
/*     */   }
/*     */   
/*     */   private int ask0() {
/*  48 */     int style = 0;
/*  49 */     switch (this.input_type) {
/*     */     case 1: 
/*  51 */       style |= 0x100;
/*     */     case 0: 
/*  53 */       style |= 0x20;
/*  54 */       break;
/*     */     
/*     */     case 5: 
/*  57 */       style |= 0x800;
/*     */     case 4: 
/*  59 */       style |= 0x400;
/*  60 */       style |= 0x100;
/*  61 */       break;
/*     */     
/*     */     case 3: 
/*  64 */       style |= 0x100;
/*     */     case 2: 
/*  66 */       style |= 0x40;
/*  67 */       style |= 0x80;
/*     */     }
/*     */     
/*     */     
/*  71 */     switch (this.message_type) {
/*     */     case 1: 
/*  73 */       style |= 0x1;
/*  74 */       break;
/*     */     case 2: 
/*  76 */       style |= 0x2;
/*  77 */       break;
/*     */     case 4: 
/*  79 */       style |= 0x4;
/*  80 */       break;
/*     */     case 3: 
/*  82 */       style |= 0x8;
/*  83 */       break;
/*     */     case 5: 
/*  85 */       style |= 0x10;
/*     */     }
/*     */     
/*     */     
/*  89 */     MessageBoxShell mb = new MessageBoxShell(style, this.title, messagesAsString());
/*     */     
/*  91 */     mb.open(null);
/*  92 */     int result = mb.waitUntilClosed();
/*     */     
/*  94 */     switch (result) {
/*     */     case 32: 
/*  96 */       return 0;
/*     */     case 64: 
/*  98 */       return 2;
/*     */     case 128: 
/* 100 */       return 3;
/*     */     case 512: 
/* 102 */       return 1;
/*     */     case 1024: 
/* 104 */       return 4;
/*     */     case 2048: 
/* 106 */       return 5;
/*     */     }
/* 108 */     return 1;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/pluginsimpl/UIMessageImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */