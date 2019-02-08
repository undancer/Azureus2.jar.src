/*     */ package org.gudy.azureus2.ui.swt.mainwindow;
/*     */ 
/*     */ import org.eclipse.swt.dnd.Clipboard;
/*     */ import org.eclipse.swt.dnd.TextTransfer;
/*     */ import org.eclipse.swt.dnd.Transfer;
/*     */ import org.eclipse.swt.events.MenuAdapter;
/*     */ import org.eclipse.swt.events.MenuEvent;
/*     */ import org.eclipse.swt.events.MouseAdapter;
/*     */ import org.eclipse.swt.events.MouseEvent;
/*     */ import org.eclipse.swt.events.SelectionAdapter;
/*     */ import org.eclipse.swt.events.SelectionEvent;
/*     */ import org.eclipse.swt.widgets.Control;
/*     */ import org.eclipse.swt.widgets.Menu;
/*     */ import org.eclipse.swt.widgets.MenuItem;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.ui.swt.Utils;
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
/*     */ 
/*     */ 
/*     */ public class ClipboardCopy
/*     */ {
/*     */   public static void copyToClipBoard(String data)
/*     */   {
/*  50 */     Runnable do_it = new Runnable()
/*     */     {
/*     */ 
/*     */       public void run()
/*     */       {
/*     */ 
/*  56 */         new Clipboard(SWTThread.getInstance().getDisplay()).setContents(new Object[] { this.val$data.replaceAll("\\x00", " ") }, new Transfer[] { TextTransfer.getInstance() });
/*     */       }
/*     */     };
/*     */     
/*     */ 
/*     */ 
/*  62 */     if (Utils.isSWTThread())
/*     */     {
/*  64 */       do_it.run();
/*     */     }
/*     */     else
/*     */     {
/*  68 */       Utils.execSWTThread(do_it);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static void addCopyToClipMenu(Control control, final copyToClipProvider provider)
/*     */   {
/*  77 */     control.addMouseListener(new MouseAdapter()
/*     */     {
/*     */ 
/*     */ 
/*     */       public void mouseDown(MouseEvent e)
/*     */       {
/*     */ 
/*  84 */         if (this.val$control.isDisposed())
/*     */         {
/*  86 */           return;
/*     */         }
/*     */         
/*  89 */         final String text = provider.getText();
/*     */         
/*  91 */         if ((this.val$control.getMenu() != null) || (text == null) || (text.length() == 0))
/*     */         {
/*  93 */           return;
/*     */         }
/*     */         
/*  96 */         if ((e.button != 3) && ((e.button != 1) || (e.stateMask != 262144)))
/*     */         {
/*  98 */           return;
/*     */         }
/*     */         
/* 101 */         final Menu menu = new Menu(this.val$control.getShell(), 8);
/*     */         
/* 103 */         MenuItem item = new MenuItem(menu, 0);
/*     */         
/*     */         String msg_text_id;
/*     */         String msg_text_id;
/* 107 */         if ((provider instanceof ClipboardCopy.copyToClipProvider2))
/*     */         {
/* 109 */           msg_text_id = ((ClipboardCopy.copyToClipProvider2)provider).getMenuResource();
/*     */         }
/*     */         else
/*     */         {
/* 113 */           msg_text_id = "label.copy.to.clipboard";
/*     */         }
/*     */         
/* 116 */         item.setText(MessageText.getString(msg_text_id));
/*     */         
/* 118 */         item.addSelectionListener(new SelectionAdapter()
/*     */         {
/*     */ 
/*     */ 
/*     */           public void widgetSelected(SelectionEvent arg0)
/*     */           {
/*     */ 
/* 125 */             new Clipboard(ClipboardCopy.2.this.val$control.getDisplay()).setContents(new Object[] { text }, new Transfer[] { TextTransfer.getInstance() });
/*     */           }
/*     */           
/* 128 */         });
/* 129 */         this.val$control.setMenu(menu);
/*     */         
/* 131 */         menu.addMenuListener(new MenuAdapter()
/*     */         {
/*     */ 
/*     */ 
/*     */           public void menuHidden(MenuEvent arg0)
/*     */           {
/*     */ 
/* 138 */             if (ClipboardCopy.2.this.val$control.getMenu() == menu)
/*     */             {
/* 140 */               ClipboardCopy.2.this.val$control.setMenu(null);
/*     */             }
/*     */             
/*     */           }
/* 144 */         });
/* 145 */         menu.setVisible(true);
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static void addCopyToClipMenu(Menu menu, final String text)
/*     */   {
/* 155 */     MenuItem item = new MenuItem(menu, 0);
/*     */     
/* 157 */     String msg_text_id = "label.copy.to.clipboard";
/*     */     
/* 159 */     item.setText(MessageText.getString(msg_text_id));
/*     */     
/* 161 */     item.addSelectionListener(new SelectionAdapter()
/*     */     {
/*     */ 
/*     */ 
/*     */       public void widgetSelected(SelectionEvent arg0)
/*     */       {
/*     */ 
/* 168 */         new Clipboard(this.val$menu.getDisplay()).setContents(new Object[] { text }, new Transfer[] { TextTransfer.getInstance() });
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static void addCopyToClipMenu(Control control)
/*     */   {
/* 178 */     addCopyToClipMenu(control, new copyToClipProvider()
/*     */     {
/*     */ 
/*     */ 
/*     */       public String getText()
/*     */       {
/*     */ 
/* 185 */         return (String)this.val$control.getData();
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */   public static abstract interface copyToClipProvider
/*     */   {
/*     */     public abstract String getText();
/*     */   }
/*     */   
/*     */   public static abstract interface copyToClipProvider2
/*     */     extends ClipboardCopy.copyToClipProvider
/*     */   {
/*     */     public abstract String getMenuResource();
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/mainwindow/ClipboardCopy.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */