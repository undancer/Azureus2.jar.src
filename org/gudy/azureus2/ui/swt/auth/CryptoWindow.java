/*     */ package org.gudy.azureus2.ui.swt.auth;
/*     */ 
/*     */ import com.aelitis.azureus.core.security.CryptoManager;
/*     */ import com.aelitis.azureus.core.security.CryptoManagerFactory;
/*     */ import com.aelitis.azureus.core.security.CryptoManagerPasswordHandler;
/*     */ import com.aelitis.azureus.core.security.CryptoManagerPasswordHandler.passwordDetails;
/*     */ import java.util.Arrays;
/*     */ import org.eclipse.swt.events.MouseAdapter;
/*     */ import org.eclipse.swt.events.MouseEvent;
/*     */ import org.eclipse.swt.layout.GridData;
/*     */ import org.eclipse.swt.layout.GridLayout;
/*     */ import org.eclipse.swt.widgets.Button;
/*     */ import org.eclipse.swt.widgets.Combo;
/*     */ import org.eclipse.swt.widgets.Display;
/*     */ import org.eclipse.swt.widgets.Event;
/*     */ import org.eclipse.swt.widgets.Label;
/*     */ import org.eclipse.swt.widgets.Listener;
/*     */ import org.eclipse.swt.widgets.MessageBox;
/*     */ import org.eclipse.swt.widgets.Shell;
/*     */ import org.eclipse.swt.widgets.Text;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.core3.util.AESemaphore;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.ui.swt.Messages;
/*     */ import org.gudy.azureus2.ui.swt.Utils;
/*     */ import org.gudy.azureus2.ui.swt.components.shell.ShellFactory;
/*     */ import org.gudy.azureus2.ui.swt.mainwindow.ClipboardCopy;
/*     */ import org.gudy.azureus2.ui.swt.mainwindow.Colors;
/*     */ import org.gudy.azureus2.ui.swt.mainwindow.SWTThread;
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
/*     */ public class CryptoWindow
/*     */   implements CryptoManagerPasswordHandler
/*     */ {
/*     */   private static final int DAY = 86400;
/*     */   
/*     */   public CryptoWindow()
/*     */   {
/*  57 */     this(false);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public CryptoWindow(boolean stand_alone)
/*     */   {
/*  64 */     if (!stand_alone)
/*     */     {
/*  66 */       CryptoManagerFactory.getSingleton().addPasswordHandler(this);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public int getHandlerType()
/*     */   {
/*  73 */     return 1;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public CryptoManagerPasswordHandler.passwordDetails getPassword(final int handler_type, final int action_type, final boolean last_pw_incorrect, final String reason)
/*     */   {
/*  83 */     final Display display = SWTThread.getInstance().getDisplay();
/*     */     
/*  85 */     if (display.isDisposed())
/*     */     {
/*  87 */       return null;
/*     */     }
/*     */     
/*  90 */     final cryptoDialog[] dialog = new cryptoDialog[1];
/*     */     
/*  92 */     final AESemaphore sem = new AESemaphore("CryptoWindowSem");
/*     */     try
/*     */     {
/*  95 */       if (display.getThread() == Thread.currentThread())
/*     */       {
/*  97 */         dialog[0] = new cryptoDialog(sem, display, handler_type, action_type, last_pw_incorrect, reason);
/*     */         
/*  99 */         while ((!display.isDisposed()) && (!sem.isReleasedForever()))
/*     */         {
/* 101 */           if (!display.readAndDispatch())
/*     */           {
/* 103 */             display.sleep();
/*     */           }
/*     */         }
/*     */         
/* 107 */         if (display.isDisposed())
/*     */         {
/* 109 */           return null;
/*     */         }
/*     */       } else {
/* 112 */         display.asyncExec(new Runnable()
/*     */         {
/*     */ 
/*     */           public void run()
/*     */           {
/*     */ 
/* 118 */             dialog[0] = new CryptoWindow.cryptoDialog(sem, display, handler_type, action_type, last_pw_incorrect, reason);
/*     */           }
/*     */         });
/*     */       }
/*     */     }
/*     */     catch (Throwable e) {
/* 124 */       Debug.printStackTrace(e);
/*     */       
/* 126 */       return null;
/*     */     }
/*     */     
/* 129 */     sem.reserve();
/*     */     
/* 131 */     final char[] pw = dialog[0].getPassword();
/* 132 */     final int persist_for = dialog[0].getPersistForSeconds();
/*     */     
/* 134 */     if (pw == null)
/*     */     {
/* 136 */       return null;
/*     */     }
/*     */     
/* 139 */     new CryptoManagerPasswordHandler.passwordDetails()
/*     */     {
/*     */ 
/*     */       public char[] getPassword()
/*     */       {
/*     */ 
/* 145 */         return pw;
/*     */       }
/*     */       
/*     */ 
/*     */       public int getPersistForSeconds()
/*     */       {
/* 151 */         return persist_for;
/*     */       }
/*     */     };
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void passwordOK(int handler_type, CryptoManagerPasswordHandler.passwordDetails details) {}
/*     */   
/*     */ 
/*     */ 
/*     */   protected static class cryptoDialog
/*     */   {
/*     */     private AESemaphore sem;
/*     */     
/*     */ 
/*     */     private Shell shell;
/*     */     
/*     */ 
/*     */     private char[] password;
/*     */     
/*     */ 
/*     */     private char[] password2;
/*     */     
/*     */ 
/*     */     private int persist_for_secs;
/*     */     
/*     */ 
/*     */     private boolean verify_password;
/*     */     
/*     */ 
/*     */     protected cryptoDialog(AESemaphore _sem, Display display, int handler_type, int action_type, boolean last_pw_incorrect, String reason)
/*     */     {
/* 184 */       this.sem = _sem;
/*     */       
/* 186 */       if (display.isDisposed())
/*     */       {
/* 188 */         this.sem.releaseForever();
/*     */         
/* 190 */         return;
/*     */       }
/*     */       
/* 193 */       this.shell = ShellFactory.createMainShell(67680);
/*     */       
/*     */ 
/* 196 */       Utils.setShellIcon(this.shell);
/*     */       
/* 198 */       boolean set_password = action_type == 3;
/*     */       
/* 200 */       Messages.setLanguageText(this.shell, set_password ? "security.crypto.pw.title" : "security.crypto.title");
/*     */       
/* 202 */       GridLayout layout = new GridLayout();
/* 203 */       layout.numColumns = 3;
/*     */       
/* 205 */       this.shell.setLayout(layout);
/*     */       
/*     */ 
/*     */ 
/* 209 */       if (action_type == 1)
/*     */       {
/* 211 */         Label reason_label = new Label(this.shell, 64);
/* 212 */         Messages.setLanguageText(reason_label, "security.crypto.encrypt");
/* 213 */         GridData gridData = new GridData(1808);
/* 214 */         gridData.horizontalSpan = 3;
/* 215 */         gridData.widthHint = 300;
/* 216 */         Utils.setLayoutData(reason_label, gridData);
/*     */       }
/* 218 */       else if (action_type == 2)
/*     */       {
/* 220 */         Label decrypt_label = new Label(this.shell, 64);
/* 221 */         Messages.setLanguageText(decrypt_label, "security.crypto.decrypt");
/* 222 */         GridData gridData = new GridData(1808);
/* 223 */         gridData.horizontalSpan = 3;
/* 224 */         gridData.widthHint = 300;
/* 225 */         Utils.setLayoutData(decrypt_label, gridData);
/*     */         
/*     */ 
/*     */ 
/* 229 */         Label reason_label = new Label(this.shell, 0);
/* 230 */         Messages.setLanguageText(reason_label, "security.crypto.reason");
/* 231 */         gridData = new GridData(1808);
/* 232 */         gridData.horizontalSpan = 1;
/* 233 */         Utils.setLayoutData(reason_label, gridData);
/*     */         
/* 235 */         Label reason_value = new Label(this.shell, 0);
/* 236 */         reason_value.setText(reason.replaceAll("&", "&&"));
/* 237 */         gridData = new GridData(1808);
/* 238 */         gridData.horizontalSpan = 2;
/* 239 */         Utils.setLayoutData(reason_value, gridData);
/*     */         
/*     */ 
/*     */ 
/* 243 */         if (last_pw_incorrect)
/*     */         {
/* 245 */           Label pw_wrong_label = new Label(this.shell, 64);
/* 246 */           Messages.setLanguageText(pw_wrong_label, "security.crypto.badpw");
/* 247 */           gridData = new GridData(1808);
/* 248 */           gridData.horizontalSpan = 3;
/* 249 */           Utils.setLayoutData(pw_wrong_label, gridData);
/* 250 */           pw_wrong_label.setForeground(Colors.red);
/*     */         }
/*     */       }
/*     */       
/*     */ 
/*     */ 
/* 256 */       Label password_label = new Label(this.shell, 0);
/* 257 */       Messages.setLanguageText(password_label, "security.crypto.password");
/* 258 */       GridData gridData = new GridData(1808);
/* 259 */       gridData.horizontalSpan = 1;
/* 260 */       Utils.setLayoutData(password_label, gridData);
/*     */       
/* 262 */       final Text password_value = new Text(this.shell, 2048);
/* 263 */       password_value.setEchoChar('*');
/* 264 */       password_value.setText("");
/* 265 */       gridData = new GridData(1808);
/* 266 */       gridData.horizontalSpan = 2;
/* 267 */       Utils.setLayoutData(password_value, gridData);
/*     */       
/* 269 */       password_value.addListener(24, new Listener() {
/*     */         public void handleEvent(Event event) {
/* 271 */           CryptoWindow.cryptoDialog.this.password = password_value.getText().toCharArray();
/*     */         }
/*     */       });
/*     */       
/*     */ 
/* 276 */       if ((action_type == 1) || (set_password))
/*     */       {
/*     */ 
/*     */ 
/* 280 */         this.verify_password = true;
/*     */         
/* 282 */         Label password2_label = new Label(this.shell, 0);
/* 283 */         Messages.setLanguageText(password2_label, "security.crypto.password2");
/* 284 */         gridData = new GridData(1808);
/* 285 */         gridData.horizontalSpan = 1;
/* 286 */         Utils.setLayoutData(password2_label, gridData);
/*     */         
/* 288 */         final Text password2_value = new Text(this.shell, 2048);
/* 289 */         password2_value.setEchoChar('*');
/* 290 */         password2_value.setText("");
/* 291 */         gridData = new GridData(1808);
/* 292 */         gridData.horizontalSpan = 2;
/* 293 */         Utils.setLayoutData(password2_value, gridData);
/*     */         
/* 295 */         password2_value.addListener(24, new Listener() {
/*     */           public void handleEvent(Event event) {
/* 297 */             CryptoWindow.cryptoDialog.this.password2 = password2_value.getText().toCharArray();
/*     */           }
/*     */         });
/*     */       }
/*     */       
/*     */ 
/* 303 */       if (!set_password)
/*     */       {
/*     */ 
/*     */ 
/* 307 */         Label strength_label = new Label(this.shell, 0);
/* 308 */         Messages.setLanguageText(strength_label, "security.crypto.persist_for");
/* 309 */         gridData = new GridData(1808);
/* 310 */         gridData.horizontalSpan = 1;
/* 311 */         Utils.setLayoutData(strength_label, gridData);
/*     */         
/* 313 */         String[] duration_keys = { "dont_save", "session", "day", "week", "30days", "forever" };
/* 314 */         final int[] duration_secs = { 0, -1, 86400, 604800, 2592000, Integer.MAX_VALUE };
/*     */         
/* 316 */         final Combo durations_combo = new Combo(this.shell, 12);
/*     */         
/* 318 */         for (int i = 0; i < duration_keys.length; i++)
/*     */         {
/* 320 */           String text = MessageText.getString("security.crypto.persist_for." + duration_keys[i]);
/*     */           
/* 322 */           durations_combo.add(text);
/*     */         }
/*     */         
/* 325 */         durations_combo.select(4);
/*     */         
/* 327 */         this.persist_for_secs = duration_secs[4];
/*     */         
/* 329 */         durations_combo.addListener(13, new Listener()
/*     */         {
/*     */ 
/*     */ 
/*     */           public void handleEvent(Event e)
/*     */           {
/*     */ 
/*     */ 
/* 337 */             CryptoWindow.cryptoDialog.this.persist_for_secs = duration_secs[durations_combo.getSelectionIndex()];
/*     */           }
/*     */           
/* 340 */         });
/* 341 */         gridData = new GridData(1808);
/* 342 */         gridData.horizontalSpan = 1;
/* 343 */         Utils.setLayoutData(durations_combo, gridData);
/*     */         
/* 345 */         new Label(this.shell, 0);
/*     */         
/*     */ 
/*     */ 
/* 349 */         Label linkLabel = new Label(this.shell, 0);
/* 350 */         linkLabel.setText(MessageText.getString("ConfigView.label.please.visit.here"));
/* 351 */         linkLabel.setData("http://wiki.vuze.com/w/Public_Private_Keys");
/* 352 */         linkLabel.setCursor(display.getSystemCursor(21));
/* 353 */         linkLabel.setForeground(Colors.blue);
/* 354 */         gridData = new GridData();
/* 355 */         gridData.horizontalSpan = 3;
/* 356 */         Utils.setLayoutData(linkLabel, gridData);
/* 357 */         linkLabel.addMouseListener(new MouseAdapter() {
/*     */           public void mouseDoubleClick(MouseEvent arg0) {
/* 359 */             Utils.launch((String)((Label)arg0.widget).getData());
/*     */           }
/*     */           
/*     */           public void mouseDown(MouseEvent arg0) {
/* 363 */             Utils.launch((String)((Label)arg0.widget).getData());
/*     */           }
/* 365 */         });
/* 366 */         ClipboardCopy.addCopyToClipMenu(linkLabel);
/*     */       }
/*     */       
/*     */ 
/*     */ 
/* 371 */       Label labelSeparator = new Label(this.shell, 258);
/* 372 */       gridData = new GridData(768);
/* 373 */       gridData.horizontalSpan = 3;
/* 374 */       Utils.setLayoutData(labelSeparator, gridData);
/*     */       
/*     */ 
/*     */ 
/* 378 */       new Label(this.shell, 0);
/*     */       
/* 380 */       Button bOk = new Button(this.shell, 8);
/* 381 */       Messages.setLanguageText(bOk, "Button.ok");
/* 382 */       gridData = new GridData(896);
/* 383 */       gridData.grabExcessHorizontalSpace = true;
/* 384 */       gridData.widthHint = 70;
/* 385 */       Utils.setLayoutData(bOk, gridData);
/* 386 */       bOk.addListener(13, new Listener() {
/*     */         public void handleEvent(Event e) {
/* 388 */           CryptoWindow.cryptoDialog.this.close(true);
/*     */         }
/*     */         
/* 391 */       });
/* 392 */       Button bCancel = new Button(this.shell, 8);
/* 393 */       Messages.setLanguageText(bCancel, "Button.cancel");
/* 394 */       gridData = new GridData(128);
/* 395 */       gridData.grabExcessHorizontalSpace = false;
/* 396 */       gridData.widthHint = 70;
/* 397 */       Utils.setLayoutData(bCancel, gridData);
/* 398 */       bCancel.addListener(13, new Listener() {
/*     */         public void handleEvent(Event e) {
/* 400 */           CryptoWindow.cryptoDialog.this.close(false);
/*     */         }
/*     */         
/* 403 */       });
/* 404 */       this.shell.setDefaultButton(bOk);
/*     */       
/* 406 */       this.shell.addListener(31, new Listener() {
/*     */         public void handleEvent(Event e) {
/* 408 */           if (e.character == '\033') {
/* 409 */             CryptoWindow.cryptoDialog.this.close(false);
/*     */           }
/*     */           
/*     */         }
/* 413 */       });
/* 414 */       this.shell.pack();
/*     */       
/* 416 */       Utils.centreWindow(this.shell);
/*     */       
/* 418 */       this.shell.open();
/*     */     }
/*     */     
/*     */ 
/*     */     protected void close(boolean ok)
/*     */     {
/*     */       try
/*     */       {
/* 426 */         if (ok)
/*     */         {
/* 428 */           if (this.password == null)
/*     */           {
/* 430 */             this.password = new char[0];
/*     */           }
/*     */           
/* 433 */           if (this.password2 == null)
/*     */           {
/* 435 */             this.password2 = new char[0];
/*     */           }
/*     */           
/* 438 */           if (this.verify_password)
/*     */           {
/* 440 */             if (!Arrays.equals(this.password, this.password2))
/*     */             {
/* 442 */               MessageBox mb = new MessageBox(this.shell, 33);
/*     */               
/* 444 */               mb.setText(MessageText.getString("security.crypto.password.mismatch.title"));
/*     */               
/* 446 */               mb.setMessage(MessageText.getString("security.crypto.password.mismatch"));
/*     */               
/* 448 */               mb.open();
/*     */             }
/*     */             
/*     */           }
/*     */         }
/*     */         else
/*     */         {
/* 455 */           this.password = null;
/*     */         }
/*     */         
/* 458 */         this.shell.dispose();
/*     */       }
/*     */       finally
/*     */       {
/* 462 */         this.sem.releaseForever();
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */     protected char[] getPassword()
/*     */     {
/* 469 */       return this.password;
/*     */     }
/*     */     
/*     */ 
/*     */     protected int getPersistForSeconds()
/*     */     {
/* 475 */       return this.persist_for_secs;
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/auth/CryptoWindow.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */