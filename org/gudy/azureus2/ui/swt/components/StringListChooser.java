/*     */ package org.gudy.azureus2.ui.swt.components;
/*     */ 
/*     */ import org.eclipse.swt.layout.GridData;
/*     */ import org.eclipse.swt.layout.GridLayout;
/*     */ import org.eclipse.swt.widgets.Button;
/*     */ import org.eclipse.swt.widgets.Combo;
/*     */ import org.eclipse.swt.widgets.Display;
/*     */ import org.eclipse.swt.widgets.Event;
/*     */ import org.eclipse.swt.widgets.Label;
/*     */ import org.eclipse.swt.widgets.Listener;
/*     */ import org.eclipse.swt.widgets.Shell;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.core3.util.AERunnable;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.ui.swt.Utils;
/*     */ import org.gudy.azureus2.ui.swt.components.shell.ShellFactory;
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
/*     */ public class StringListChooser
/*     */ {
/*     */   private Display display;
/*     */   private Shell shell;
/*     */   private Label label;
/*     */   private Combo combo;
/*     */   private String result;
/*     */   
/*     */   public StringListChooser(final Shell parentShell)
/*     */   {
/*  44 */     this.result = null;
/*     */     
/*  46 */     this.display = parentShell.getDisplay();
/*  47 */     if ((this.display == null) || (this.display.isDisposed())) return;
/*  48 */     this.display.syncExec(new Runnable() {
/*     */       public void run() {
/*  50 */         StringListChooser.this.createShell(parentShell);
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */   private void createShell(Shell parentShell)
/*     */   {
/*  57 */     this.shell = ShellFactory.createShell(this.display, 67680);
/*  58 */     Utils.setShellIcon(this.shell);
/*     */     
/*  60 */     GridLayout layout = new GridLayout();
/*  61 */     layout.numColumns = 2;
/*  62 */     this.shell.setLayout(layout);
/*     */     
/*     */ 
/*  65 */     this.label = new Label(this.shell, 64);
/*     */     
/*  67 */     this.combo = new Combo(this.shell, 8);
/*     */     
/*  69 */     Button ok = new Button(this.shell, 8);
/*  70 */     ok.addListener(13, new Listener() {
/*     */       public void handleEvent(Event arg0) {
/*  72 */         StringListChooser.this.result = StringListChooser.this.combo.getText();
/*  73 */         StringListChooser.this.shell.dispose();
/*     */       }
/*  75 */     });
/*  76 */     ok.setText(MessageText.getString("Button.ok"));
/*     */     
/*  78 */     Button cancel = new Button(this.shell, 8);
/*  79 */     cancel.addListener(13, new Listener()
/*     */     {
/*     */       public void handleEvent(Event arg0) {
/*  82 */         StringListChooser.this.result = null;
/*     */         
/*  84 */         StringListChooser.this.shell.dispose();
/*     */       }
/*  86 */     });
/*  87 */     cancel.setText(MessageText.getString("Button.cancel"));
/*     */     
/*     */ 
/*  90 */     this.shell.addListener(12, new Listener()
/*     */     {
/*     */       public void handleEvent(Event arg0) {}
/*     */ 
/*  94 */     });
/*  95 */     GridData data = new GridData(768);
/*  96 */     data.horizontalSpan = 2;
/*  97 */     data.heightHint = 30;
/*  98 */     Utils.setLayoutData(this.label, data);
/*     */     
/* 100 */     data = new GridData(768);
/* 101 */     data.horizontalSpan = 2;
/* 102 */     Utils.setLayoutData(this.combo, data);
/*     */     
/* 104 */     data = new GridData();
/* 105 */     data.widthHint = 80;
/* 106 */     data.grabExcessHorizontalSpace = true;
/* 107 */     data.grabExcessVerticalSpace = true;
/* 108 */     data.verticalAlignment = 16777224;
/* 109 */     data.horizontalAlignment = 16777224;
/* 110 */     Utils.setLayoutData(ok, data);
/*     */     
/* 112 */     data = new GridData();
/* 113 */     data.grabExcessVerticalSpace = true;
/* 114 */     data.verticalAlignment = 16777224;
/* 115 */     data.widthHint = 80;
/* 116 */     Utils.setLayoutData(cancel, data);
/*     */     
/* 118 */     this.shell.setSize(300, 150);
/* 119 */     this.shell.layout();
/*     */     
/* 121 */     Utils.centerWindowRelativeTo(this.shell, parentShell);
/*     */   }
/*     */   
/*     */   public void setTitle(final String title)
/*     */   {
/* 126 */     Utils.execSWTThread(new AERunnable() {
/*     */       public void runSupport() {
/* 128 */         if ((StringListChooser.this.display == null) || (StringListChooser.this.display.isDisposed())) return;
/* 129 */         StringListChooser.this.shell.setText(title);
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */   public void setText(final String text) {
/* 135 */     Utils.execSWTThread(new AERunnable() {
/*     */       public void runSupport() {
/* 137 */         if ((StringListChooser.this.display == null) || (StringListChooser.this.display.isDisposed())) return;
/* 138 */         StringListChooser.this.label.setText(text.replaceAll("&", "&&"));
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */   public void addOption(final String option) {
/* 144 */     Utils.execSWTThread(new AERunnable() {
/*     */       public void runSupport() {
/* 146 */         if ((StringListChooser.this.display == null) || (StringListChooser.this.display.isDisposed())) return;
/* 147 */         StringListChooser.this.combo.add(option);
/* 148 */         if (StringListChooser.this.combo.getItemCount() == 1) {
/* 149 */           StringListChooser.this.combo.setText(option);
/*     */         }
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */   public String open() {
/* 156 */     if ((this.display == null) || (this.display.isDisposed())) return null;
/* 157 */     Utils.execSWTThread(new AERunnable()
/*     */     {
/*     */       public void runSupport()
/*     */       {
/* 161 */         if ((StringListChooser.this.display == null) || (StringListChooser.this.display.isDisposed())) {
/* 162 */           return;
/*     */         }
/*     */         try {
/* 165 */           StringListChooser.this.shell.open();
/* 166 */           while (!StringListChooser.this.shell.isDisposed()) {
/* 167 */             if (!StringListChooser.this.display.readAndDispatch()) StringListChooser.this.display.sleep();
/*     */           }
/*     */         }
/*     */         catch (Throwable e) {
/* 171 */           Debug.printStackTrace(e);
/*     */         }
/*     */         
/*     */       }
/* 175 */     });
/* 176 */     return this.result;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/components/StringListChooser.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */