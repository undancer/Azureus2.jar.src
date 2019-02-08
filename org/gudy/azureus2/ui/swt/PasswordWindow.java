/*     */ package org.gudy.azureus2.ui.swt;
/*     */ 
/*     */ import com.aelitis.azureus.ui.swt.UIFunctionsManagerSWT;
/*     */ import com.aelitis.azureus.ui.swt.UIFunctionsSWT;
/*     */ import java.io.PrintStream;
/*     */ import org.eclipse.swt.events.DisposeEvent;
/*     */ import org.eclipse.swt.events.DisposeListener;
/*     */ import org.eclipse.swt.events.TraverseEvent;
/*     */ import org.eclipse.swt.events.TraverseListener;
/*     */ import org.eclipse.swt.layout.GridData;
/*     */ import org.eclipse.swt.layout.GridLayout;
/*     */ import org.eclipse.swt.widgets.Button;
/*     */ import org.eclipse.swt.widgets.Display;
/*     */ import org.eclipse.swt.widgets.Event;
/*     */ import org.eclipse.swt.widgets.Label;
/*     */ import org.eclipse.swt.widgets.Listener;
/*     */ import org.eclipse.swt.widgets.Shell;
/*     */ import org.eclipse.swt.widgets.Text;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.core3.util.AERunnable;
/*     */ import org.gudy.azureus2.core3.util.AESemaphore;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.SHA1Hasher;
/*     */ import org.gudy.azureus2.core3.util.SystemTime;
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
/*     */ 
/*     */ public class PasswordWindow
/*     */ {
/*     */   private Shell shell;
/*     */   private static boolean bOk;
/*  49 */   private static long lastSuccess = 0L;
/*     */   
/*     */   private static final long REMEMBER_SUCCESS_MS = 3000L;
/*     */   
/*  53 */   protected static AESemaphore class_sem = new AESemaphore("PasswordWindow");
/*     */   
/*  55 */   private static PasswordWindow window = null;
/*     */   
/*     */   public static boolean showPasswordWindow(Display display) {
/*  58 */     if (lastSuccess + 3000L >= SystemTime.getCurrentTime()) {
/*  59 */       return true;
/*     */     }
/*     */     
/*  62 */     final boolean bSWTThread = display.getThread() == Thread.currentThread();
/*  63 */     display.syncExec(new AERunnable() {
/*     */       public void runSupport() {
/*  65 */         if (PasswordWindow.window == null) {
/*  66 */           PasswordWindow.access$002(new PasswordWindow(this.val$display));
/*  67 */           PasswordWindow.window.open();
/*     */         } else {
/*  69 */           PasswordWindow.window.shell.setVisible(true);
/*  70 */           PasswordWindow.window.shell.forceActive();
/*     */         }
/*     */         
/*  73 */         if (bSWTThread) {
/*  74 */           PasswordWindow.window.run();
/*     */         }
/*     */       }
/*     */     });
/*     */     
/*  79 */     if (!bSWTThread) {
/*  80 */       class_sem.reserve();
/*     */     }
/*     */     
/*  83 */     lastSuccess = bOk ? SystemTime.getCurrentTime() : 0L;
/*  84 */     return bOk;
/*     */   }
/*     */   
/*     */   protected PasswordWindow(Display display) {}
/*     */   
/*     */   private void open() {
/*  90 */     bOk = false;
/*     */     
/*  92 */     this.shell = ShellFactory.createMainShell(65632);
/*  93 */     this.shell.setText(MessageText.getString("PasswordWindow.title"));
/*  94 */     Utils.setShellIcon(this.shell);
/*  95 */     GridLayout layout = new GridLayout();
/*  96 */     layout.numColumns = 2;
/*  97 */     layout.makeColumnsEqualWidth = true;
/*  98 */     this.shell.setLayout(layout);
/*     */     
/* 100 */     Label label = new Label(this.shell, 0);
/* 101 */     label.setText(MessageText.getString("PasswordWindow.passwordprotected"));
/* 102 */     GridData gridData = new GridData(768);
/* 103 */     gridData.horizontalSpan = 2;
/* 104 */     Utils.setLayoutData(label, gridData);
/*     */     
/* 106 */     final Text password = new Text(this.shell, 2048);
/* 107 */     password.setEchoChar('*');
/* 108 */     gridData = new GridData(768);
/* 109 */     gridData.horizontalSpan = 2;
/* 110 */     Utils.setLayoutData(password, gridData);
/*     */     
/* 112 */     Button ok = new Button(this.shell, 8);
/* 113 */     ok.setText(MessageText.getString("Button.ok"));
/* 114 */     gridData = new GridData(64);
/* 115 */     gridData.widthHint = 70;
/* 116 */     Utils.setLayoutData(ok, gridData);
/* 117 */     this.shell.setDefaultButton(ok);
/* 118 */     ok.addListener(13, new Listener() {
/*     */       public void handleEvent(Event event) {
/*     */         try {
/* 121 */           SHA1Hasher hasher = new SHA1Hasher();
/* 122 */           byte[] passwordText = password.getText().getBytes();
/* 123 */           byte[] encoded = hasher.calculateHash(passwordText);
/* 124 */           byte[] correct = COConfigurationManager.getByteParameter("Password", "".getBytes());
/*     */           
/* 126 */           boolean same = true;
/* 127 */           for (int i = 0; i < correct.length; i++) {
/* 128 */             if (correct[i] != encoded[i])
/* 129 */               same = false;
/*     */           }
/* 131 */           if (same) {
/* 132 */             PasswordWindow.access$302(same);
/* 133 */             PasswordWindow.this.shell.dispose();
/*     */           } else {
/* 135 */             PasswordWindow.this.close();
/*     */           }
/*     */         } catch (Exception e) {
/* 138 */           Debug.printStackTrace(e);
/*     */         }
/*     */         
/*     */       }
/* 142 */     });
/* 143 */     Button cancel = new Button(this.shell, 8);
/* 144 */     cancel.setText(MessageText.getString("Button.cancel"));
/* 145 */     gridData = new GridData(64);
/* 146 */     gridData.widthHint = 70;
/* 147 */     Utils.setLayoutData(cancel, gridData);
/* 148 */     cancel.addListener(13, new Listener()
/*     */     {
/*     */ 
/*     */       public void handleEvent(Event event)
/*     */       {
/*     */ 
/* 154 */         PasswordWindow.this.close();
/*     */       }
/*     */       
/*     */ 
/* 158 */     });
/* 159 */     this.shell.addDisposeListener(new DisposeListener() {
/*     */       public void widgetDisposed(DisposeEvent arg0) {
/* 161 */         PasswordWindow.access$002(null);
/* 162 */         PasswordWindow.class_sem.releaseAllWaiters();
/*     */       }
/*     */       
/* 165 */     });
/* 166 */     this.shell.addTraverseListener(new TraverseListener() {
/*     */       public void keyTraversed(TraverseEvent e) {
/* 168 */         if (e.detail == 2) {
/* 169 */           PasswordWindow.this.close();
/* 170 */           e.doit = false;
/*     */         }
/*     */         
/*     */       }
/* 174 */     });
/* 175 */     this.shell.addListener(21, new Listener() {
/*     */       public void handleEvent(Event arg0) {
/* 177 */         PasswordWindow.this.close();
/*     */       }
/*     */       
/* 180 */     });
/* 181 */     this.shell.pack();
/*     */     
/* 183 */     Utils.centreWindow(this.shell);
/*     */     
/* 185 */     this.shell.open();
/*     */   }
/*     */   
/*     */   protected void run() {
/* 189 */     while (!this.shell.isDisposed()) {
/* 190 */       Display d = this.shell.getDisplay();
/* 191 */       if ((!d.readAndDispatch()) && (!this.shell.isDisposed())) {
/* 192 */         d.sleep();
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   private void close() {
/* 198 */     this.shell.dispose();
/* 199 */     if (Utils.isCarbon) {
/* 200 */       UIFunctionsSWT uiFunctions = UIFunctionsManagerSWT.getUIFunctionsSWT();
/* 201 */       if (uiFunctions != null) {
/* 202 */         Shell mainShell = uiFunctions.getMainShell();
/* 203 */         if (mainShell != null) {
/* 204 */           mainShell.setMinimized(true);
/* 205 */           mainShell.setVisible(true);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public static void main(String[] args) {
/* 212 */     Display display = new Display();
/* 213 */     new Thread(new Runnable()
/*     */     {
/*     */       public void run() {
/* 216 */         System.out.println("2: " + PasswordWindow.showPasswordWindow(this.val$display));
/*     */       }
/*     */       
/* 219 */     }).start();
/* 220 */     new Thread(new Runnable()
/*     */     {
/*     */       public void run() {
/* 223 */         this.val$display.syncExec(new Runnable() {
/*     */           public void run() {
/* 225 */             System.out.println("3: " + PasswordWindow.showPasswordWindow(PasswordWindow.8.this.val$display));
/*     */           }
/*     */           
/*     */         });
/*     */       }
/* 230 */     }).start();
/* 231 */     display.asyncExec(new Runnable() {
/*     */       public void run() {
/* 233 */         System.out.println("4: " + PasswordWindow.showPasswordWindow(this.val$display));
/*     */       }
/* 235 */     });
/* 236 */     System.out.println("1: " + showPasswordWindow(display));
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/PasswordWindow.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */