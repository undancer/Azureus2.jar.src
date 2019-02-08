/*     */ package org.gudy.azureus2.ui.swt.auth;
/*     */ 
/*     */ import com.aelitis.azureus.core.networkmanager.admin.NetworkAdmin;
/*     */ import com.aelitis.azureus.ui.common.RememberedDecisionsManager;
/*     */ import java.net.InetAddress;
/*     */ import java.net.PasswordAuthentication;
/*     */ import java.net.URL;
/*     */ import java.util.Collection;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.Set;
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
/*     */ import org.gudy.azureus2.core3.security.SEPasswordListener;
/*     */ import org.gudy.azureus2.core3.security.SESecurityManager;
/*     */ import org.gudy.azureus2.core3.torrent.TOTorrent;
/*     */ import org.gudy.azureus2.core3.util.AEMonitor;
/*     */ import org.gudy.azureus2.core3.util.AERunnable;
/*     */ import org.gudy.azureus2.core3.util.AESemaphore;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.TorrentUtils;
/*     */ import org.gudy.azureus2.ui.swt.Messages;
/*     */ import org.gudy.azureus2.ui.swt.Utils;
/*     */ import org.gudy.azureus2.ui.swt.components.shell.ShellFactory;
/*     */ import org.gudy.azureus2.ui.swt.mainwindow.SWTThread;
/*     */ import org.gudy.bouncycastle.util.encoders.Base64;
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
/*     */ public class AuthenticatorWindow
/*     */   implements SEPasswordListener
/*     */ {
/*     */   private static final String CONFIG_PARAM = "swt.auth.persistent.cache";
/*  55 */   protected Map auth_cache = new HashMap();
/*     */   
/*  57 */   protected AEMonitor this_mon = new AEMonitor("AuthWind");
/*     */   
/*     */ 
/*     */   public AuthenticatorWindow()
/*     */   {
/*  62 */     SESecurityManager.addPasswordListener(this);
/*     */     
/*     */ 
/*     */ 
/*  66 */     Map cache = COConfigurationManager.getMapParameter("swt.auth.persistent.cache", new HashMap());
/*     */     try
/*     */     {
/*  69 */       Iterator it = cache.entrySet().iterator();
/*     */       
/*  71 */       while (it.hasNext())
/*     */       {
/*  73 */         Map.Entry entry = (Map.Entry)it.next();
/*     */         
/*  75 */         String key = (String)entry.getKey();
/*  76 */         Map value = (Map)entry.getValue();
/*     */         
/*  78 */         String user = new String((byte[])value.get("user"), "UTF-8");
/*  79 */         char[] pw = new String((byte[])value.get("pw"), "UTF-8").toCharArray();
/*     */         
/*  81 */         this.auth_cache.put(key, new authCache(key, new PasswordAuthentication(user, pw), true));
/*     */       }
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/*  86 */       COConfigurationManager.setParameter("swt.auth.persistent.cache", new HashMap());
/*     */       
/*  88 */       Debug.printStackTrace(e);
/*     */     }
/*     */   }
/*     */   
/*     */   protected void saveAuthCache()
/*     */   {
/*     */     try
/*     */     {
/*  96 */       this.this_mon.enter();
/*     */       
/*  98 */       HashMap map = new HashMap();
/*     */       
/* 100 */       Iterator it = this.auth_cache.values().iterator();
/*     */       
/* 102 */       while (it.hasNext())
/*     */       {
/* 104 */         authCache value = (authCache)it.next();
/*     */         
/* 106 */         if (value.isPersistent()) {
/*     */           try
/*     */           {
/* 109 */             HashMap entry_map = new HashMap();
/*     */             
/* 111 */             entry_map.put("user", value.getAuth().getUserName().getBytes("UTF-8"));
/* 112 */             entry_map.put("pw", new String(value.getAuth().getPassword()).getBytes("UTF-8"));
/*     */             
/* 114 */             map.put(value.getKey(), entry_map);
/*     */           }
/*     */           catch (Throwable e)
/*     */           {
/* 118 */             Debug.printStackTrace(e);
/*     */           }
/*     */         }
/*     */       }
/*     */       
/* 123 */       COConfigurationManager.setParameter("swt.auth.persistent.cache", map);
/*     */     }
/*     */     finally
/*     */     {
/* 127 */       this.this_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */   public void clearPasswords()
/*     */   {
/*     */     try
/*     */     {
/* 135 */       this.this_mon.enter();
/*     */       
/* 137 */       this.auth_cache = new HashMap();
/*     */       
/* 139 */       saveAuthCache();
/*     */     }
/*     */     finally
/*     */     {
/* 143 */       this.this_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public PasswordAuthentication getAuthentication(String realm, URL tracker)
/*     */   {
/*     */     try
/*     */     {
/* 153 */       this.this_mon.enter();
/*     */       
/* 155 */       return getAuthentication(realm, tracker.getProtocol(), tracker.getHost(), tracker.getPort());
/*     */     }
/*     */     finally
/*     */     {
/* 159 */       this.this_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setAuthenticationOutcome(String realm, URL tracker, boolean success)
/*     */   {
/*     */     try
/*     */     {
/* 170 */       this.this_mon.enter();
/*     */       
/* 172 */       setAuthenticationOutcome(realm, tracker.getProtocol(), tracker.getHost(), tracker.getPort(), success);
/*     */     }
/*     */     finally
/*     */     {
/* 176 */       this.this_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setAuthenticationOutcome(String realm, String protocol, String host, int port, boolean success)
/*     */   {
/*     */     try
/*     */     {
/* 189 */       this.this_mon.enter();
/*     */       
/* 191 */       String tracker = protocol + "://" + host + ":" + port + "/";
/*     */       
/* 193 */       String auth_key = realm + ":" + tracker;
/*     */       
/* 195 */       authCache cache = (authCache)this.auth_cache.get(auth_key);
/*     */       
/* 197 */       if (cache != null)
/*     */       {
/* 199 */         cache.setOutcome(success);
/*     */       }
/*     */     }
/*     */     finally {
/* 203 */       this.this_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public PasswordAuthentication getAuthentication(String realm, String protocol, String host, int port)
/*     */   {
/*     */     try
/*     */     {
/* 215 */       this.this_mon.enter();
/*     */       
/* 217 */       String tracker = protocol + "://" + host + ":" + port + "/";
/*     */       
/* 219 */       InetAddress bind_ip = NetworkAdmin.getSingleton().getSingleHomedServiceBindAddress();
/*     */       
/*     */       String self_addr;
/*     */       
/*     */       String self_addr;
/*     */       
/* 225 */       if ((bind_ip == null) || (bind_ip.isAnyLocalAddress()))
/*     */       {
/* 227 */         self_addr = "127.0.0.1";
/*     */       }
/*     */       else
/*     */       {
/* 231 */         self_addr = bind_ip.getHostAddress();
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 241 */       if ((host.equals(self_addr)) || (host.equals(COConfigurationManager.getStringParameter("Tracker IP", ""))))
/*     */       {
/*     */         try
/*     */         {
/* 245 */           byte[] pw = COConfigurationManager.getByteParameter("Tracker Password", new byte[0]);
/*     */           
/* 247 */           String str_pw = new String(Base64.encode(pw));
/*     */           
/* 249 */           return new PasswordAuthentication("<internal>", str_pw.toCharArray());
/*     */         }
/*     */         catch (Throwable e)
/*     */         {
/* 253 */           Debug.printStackTrace(e);
/*     */         }
/*     */       }
/*     */       
/* 257 */       String auth_key = realm + ":" + tracker;
/*     */       
/* 259 */       authCache cache = (authCache)this.auth_cache.get(auth_key);
/*     */       PasswordAuthentication localPasswordAuthentication2;
/* 261 */       if (cache != null)
/*     */       {
/* 263 */         PasswordAuthentication auth = cache.getAuth();
/*     */         
/* 265 */         if (auth != null)
/*     */         {
/* 267 */           return auth;
/*     */         }
/*     */       }
/*     */       
/* 271 */       String[] res = getAuthenticationDialog(realm, tracker);
/*     */       
/* 273 */       if (res == null)
/*     */       {
/* 275 */         return null;
/*     */       }
/*     */       
/*     */ 
/* 279 */       PasswordAuthentication auth = new PasswordAuthentication(res[0], res[1].toCharArray());
/*     */       
/* 281 */       boolean save_pw = res[2].equals("true");
/*     */       
/* 283 */       boolean old_entry_existed = this.auth_cache.put(auth_key, new authCache(auth_key, auth, save_pw)) != null;
/*     */       
/* 285 */       if ((save_pw) || (old_entry_existed))
/*     */       {
/* 287 */         saveAuthCache();
/*     */       }
/*     */       
/* 290 */       return auth;
/*     */     }
/*     */     finally
/*     */     {
/* 294 */       this.this_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected String[] getAuthenticationDialog(final String realm, final String location)
/*     */   {
/* 304 */     final Display display = SWTThread.getInstance().getDisplay();
/*     */     
/* 306 */     if (display.isDisposed())
/*     */     {
/* 308 */       return null;
/*     */     }
/*     */     
/* 311 */     final AESemaphore sem = new AESemaphore("SWTAuth");
/*     */     
/* 313 */     final authDialog[] dialog = new authDialog[1];
/*     */     
/* 315 */     TOTorrent torrent = TorrentUtils.getTLSTorrent();
/*     */     
/*     */     String details;
/*     */     final String details;
/*     */     final boolean is_tracker;
/* 320 */     if (torrent == null)
/*     */     {
/* 322 */       boolean is_tracker = false;
/*     */       
/* 324 */       details = TorrentUtils.getTLSDescription();
/*     */     }
/*     */     else
/*     */     {
/* 328 */       details = TorrentUtils.getLocalisedName(torrent);
/* 329 */       is_tracker = true;
/*     */     }
/*     */     try
/*     */     {
/* 333 */       if (display.getThread() == Thread.currentThread())
/*     */       {
/* 335 */         dialog[0] = new authDialog(sem, display, realm, is_tracker, location, details);
/*     */         
/* 337 */         while ((!display.isDisposed()) && (!sem.isReleasedForever()))
/*     */         {
/* 339 */           if (!display.readAndDispatch())
/*     */           {
/* 341 */             display.sleep();
/*     */           }
/*     */         }
/*     */         
/* 345 */         if (display.isDisposed())
/*     */         {
/* 347 */           return null;
/*     */         }
/*     */       }
/*     */       else {
/* 351 */         display.asyncExec(new AERunnable()
/*     */         {
/*     */ 
/*     */           public void runSupport()
/*     */           {
/*     */ 
/* 357 */             dialog[0] = new AuthenticatorWindow.authDialog(sem, display, realm, is_tracker, location, details);
/*     */           }
/*     */         });
/*     */       }
/*     */     }
/*     */     catch (Throwable e) {
/* 363 */       Debug.printStackTrace(e);
/*     */       
/* 365 */       return null;
/*     */     }
/*     */     
/* 368 */     sem.reserve();
/*     */     
/* 370 */     String user = dialog[0].getUsername();
/* 371 */     String pw = dialog[0].getPassword();
/* 372 */     String persist = dialog[0].savePassword() ? "true" : "false";
/*     */     
/* 374 */     if (user == null)
/*     */     {
/* 376 */       return null;
/*     */     }
/*     */     
/* 379 */     return new String[] { user, pw == null ? "" : pw, persist };
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected static class authDialog
/*     */   {
/*     */     private Shell shell;
/*     */     
/*     */ 
/*     */     private AESemaphore sem;
/*     */     
/*     */ 
/*     */     private String username;
/*     */     
/*     */     private String password;
/*     */     
/*     */     private boolean persist;
/*     */     
/*     */ 
/*     */     protected authDialog(AESemaphore _sem, Display display, String realm, boolean is_tracker, String target, String details)
/*     */     {
/* 401 */       this.sem = _sem;
/*     */       
/* 403 */       if (details == null)
/*     */       {
/* 405 */         details = "";
/*     */       }
/*     */       
/* 408 */       if (display.isDisposed())
/*     */       {
/* 410 */         this.sem.releaseForever();
/*     */         
/* 412 */         return;
/*     */       }
/*     */       
/* 415 */       final String ignore_key = "IgnoreAuth:" + realm + ":" + target + ":" + details.hashCode();
/*     */       
/* 417 */       if (RememberedDecisionsManager.getRememberedDecision(ignore_key) == 1)
/*     */       {
/* 419 */         Debug.out("Authentication for " + realm + "/" + target + "/" + details + " ignored as told not to ask again");
/*     */         
/* 421 */         this.sem.releaseForever();
/*     */         
/* 423 */         return;
/*     */       }
/*     */       
/* 426 */       String old_ignore_key = "IgnoreAuth:" + realm + ":" + target + ":" + details;
/*     */       
/* 428 */       int old_decision = RememberedDecisionsManager.getRememberedDecision(old_ignore_key);
/* 429 */       if (old_decision >= 0)
/*     */       {
/* 431 */         RememberedDecisionsManager.setRemembered(old_ignore_key, -1);
/*     */       }
/*     */       
/* 434 */       if (old_decision == 1)
/*     */       {
/* 436 */         Debug.out("Authentication for " + realm + "/" + target + "/" + details + " ignored as told not to ask again");
/*     */         
/* 438 */         this.sem.releaseForever();
/*     */         
/* 440 */         return;
/*     */       }
/*     */       
/* 443 */       this.shell = ShellFactory.createMainShell(67680);
/*     */       
/* 445 */       Utils.setShellIcon(this.shell);
/* 446 */       Messages.setLanguageText(this.shell, "authenticator.title");
/*     */       
/* 448 */       GridLayout layout = new GridLayout();
/* 449 */       layout.numColumns = 3;
/*     */       
/* 451 */       this.shell.setLayout(layout);
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 457 */       Label realm_label = new Label(this.shell, 0);
/* 458 */       Messages.setLanguageText(realm_label, "authenticator.realm");
/* 459 */       GridData gridData = new GridData(1808);
/* 460 */       gridData.horizontalSpan = 1;
/* 461 */       Utils.setLayoutData(realm_label, gridData);
/*     */       
/* 463 */       Label realm_value = new Label(this.shell, 0);
/* 464 */       realm_value.setText(realm.replaceAll("&", "&&"));
/* 465 */       gridData = new GridData(1808);
/* 466 */       gridData.horizontalSpan = 2;
/* 467 */       Utils.setLayoutData(realm_value, gridData);
/*     */       
/*     */ 
/*     */ 
/* 471 */       Label target_label = new Label(this.shell, 0);
/* 472 */       Messages.setLanguageText(target_label, is_tracker ? "authenticator.tracker" : "authenticator.location");
/* 473 */       gridData = new GridData(1808);
/* 474 */       gridData.horizontalSpan = 1;
/* 475 */       Utils.setLayoutData(target_label, gridData);
/*     */       
/* 477 */       Label target_value = new Label(this.shell, 0);
/* 478 */       target_value.setText(target.replaceAll("&", "&&"));
/* 479 */       gridData = new GridData(1808);
/* 480 */       gridData.horizontalSpan = 2;
/* 481 */       Utils.setLayoutData(target_value, gridData);
/*     */       
/* 483 */       if ((details != null) && (details.length() > 0))
/*     */       {
/* 485 */         Label details_label = new Label(this.shell, 0);
/* 486 */         Messages.setLanguageText(details_label, is_tracker ? "authenticator.torrent" : "authenticator.details");
/* 487 */         gridData = new GridData(1808);
/* 488 */         gridData.horizontalSpan = 1;
/* 489 */         Utils.setLayoutData(details_label, gridData);
/*     */         
/* 491 */         Label details_value = new Label(this.shell, 0);
/* 492 */         details_value.setText(details.replaceAll("&", "&&"));
/* 493 */         gridData = new GridData(1808);
/* 494 */         gridData.horizontalSpan = 2;
/* 495 */         Utils.setLayoutData(details_value, gridData);
/*     */       }
/*     */       
/*     */ 
/* 499 */       Label user_label = new Label(this.shell, 0);
/* 500 */       Messages.setLanguageText(user_label, "authenticator.user");
/* 501 */       gridData = new GridData(1808);
/* 502 */       gridData.horizontalSpan = 1;
/* 503 */       Utils.setLayoutData(user_label, gridData);
/*     */       
/* 505 */       final Text user_value = new Text(this.shell, 2048);
/* 506 */       user_value.setText("");
/* 507 */       gridData = new GridData(1808);
/* 508 */       gridData.horizontalSpan = 2;
/* 509 */       Utils.setLayoutData(user_value, gridData);
/*     */       
/* 511 */       user_value.addListener(24, new Listener() {
/*     */         public void handleEvent(Event event) {
/* 513 */           AuthenticatorWindow.authDialog.this.username = user_value.getText();
/*     */         }
/*     */         
/*     */ 
/* 517 */       });
/* 518 */       Label password_label = new Label(this.shell, 0);
/* 519 */       Messages.setLanguageText(password_label, "authenticator.password");
/* 520 */       gridData = new GridData(1808);
/* 521 */       gridData.horizontalSpan = 1;
/* 522 */       Utils.setLayoutData(password_label, gridData);
/*     */       
/* 524 */       final Text password_value = new Text(this.shell, 2048);
/* 525 */       password_value.setEchoChar('*');
/* 526 */       password_value.setText("");
/* 527 */       gridData = new GridData(1808);
/* 528 */       gridData.horizontalSpan = 2;
/* 529 */       Utils.setLayoutData(password_value, gridData);
/*     */       
/* 531 */       password_value.addListener(24, new Listener() {
/*     */         public void handleEvent(Event event) {
/* 533 */           AuthenticatorWindow.authDialog.this.password = password_value.getText();
/*     */         }
/*     */         
/*     */ 
/* 537 */       });
/* 538 */       Label blank_label = new Label(this.shell, 0);
/* 539 */       gridData = new GridData(1808);
/* 540 */       gridData.horizontalSpan = 1;
/* 541 */       Utils.setLayoutData(blank_label, gridData);
/*     */       
/* 543 */       final Button checkBox = new Button(this.shell, 32);
/* 544 */       checkBox.setText(MessageText.getString("authenticator.savepassword"));
/* 545 */       gridData = new GridData(1808);
/* 546 */       gridData.horizontalSpan = 1;
/* 547 */       Utils.setLayoutData(checkBox, gridData);
/* 548 */       checkBox.addListener(13, new Listener() {
/*     */         public void handleEvent(Event e) {
/* 550 */           AuthenticatorWindow.authDialog.this.persist = checkBox.getSelection();
/*     */         }
/*     */         
/* 553 */       });
/* 554 */       final Button dontAsk = new Button(this.shell, 32);
/* 555 */       dontAsk.setText(MessageText.getString("general.dont.ask.again"));
/* 556 */       gridData = new GridData(1808);
/* 557 */       gridData.horizontalSpan = 1;
/* 558 */       Utils.setLayoutData(dontAsk, gridData);
/* 559 */       dontAsk.addListener(13, new Listener() {
/*     */         public void handleEvent(Event e) {
/* 561 */           RememberedDecisionsManager.setRemembered(ignore_key, dontAsk.getSelection() ? 1 : 0);
/*     */ 
/*     */         }
/*     */         
/*     */ 
/* 566 */       });
/* 567 */       Label labelSeparator = new Label(this.shell, 258);
/* 568 */       gridData = new GridData(768);
/* 569 */       gridData.horizontalSpan = 3;
/* 570 */       Utils.setLayoutData(labelSeparator, gridData);
/*     */       
/*     */ 
/*     */ 
/* 574 */       new Label(this.shell, 0);
/*     */       
/* 576 */       Button bOk = new Button(this.shell, 8);
/* 577 */       Messages.setLanguageText(bOk, "Button.ok");
/* 578 */       gridData = new GridData(896);
/* 579 */       gridData.grabExcessHorizontalSpace = true;
/* 580 */       gridData.widthHint = 70;
/* 581 */       Utils.setLayoutData(bOk, gridData);
/* 582 */       bOk.addListener(13, new Listener() {
/*     */         public void handleEvent(Event e) {
/* 584 */           AuthenticatorWindow.authDialog.this.close(true);
/*     */         }
/*     */         
/* 587 */       });
/* 588 */       Button bCancel = new Button(this.shell, 8);
/* 589 */       Messages.setLanguageText(bCancel, "Button.cancel");
/* 590 */       gridData = new GridData(128);
/* 591 */       gridData.grabExcessHorizontalSpace = false;
/* 592 */       gridData.widthHint = 70;
/* 593 */       Utils.setLayoutData(bCancel, gridData);
/* 594 */       bCancel.addListener(13, new Listener() {
/*     */         public void handleEvent(Event e) {
/* 596 */           AuthenticatorWindow.authDialog.this.close(false);
/*     */         }
/*     */         
/* 599 */       });
/* 600 */       this.shell.setDefaultButton(bOk);
/*     */       
/* 602 */       this.shell.addListener(31, new Listener() {
/*     */         public void handleEvent(Event e) {
/* 604 */           if (e.character == '\033') {
/* 605 */             AuthenticatorWindow.authDialog.this.close(false);
/*     */           }
/*     */           
/*     */         }
/*     */         
/* 610 */       });
/* 611 */       this.shell.pack();
/*     */       
/* 613 */       Utils.centreWindow(this.shell);
/*     */       
/* 615 */       this.shell.open();
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     protected void close(boolean ok)
/*     */     {
/* 622 */       if (ok)
/*     */       {
/* 624 */         if (this.username == null)
/*     */         {
/* 626 */           this.username = "";
/*     */         }
/*     */         
/* 629 */         if (this.password == null)
/*     */         {
/* 631 */           this.password = "";
/*     */         }
/*     */       }
/*     */       else
/*     */       {
/* 636 */         this.username = null;
/* 637 */         this.password = null;
/*     */       }
/*     */       
/* 640 */       this.shell.dispose();
/*     */       
/* 642 */       this.sem.releaseForever();
/*     */     }
/*     */     
/*     */ 
/*     */     protected String getUsername()
/*     */     {
/* 648 */       return this.username;
/*     */     }
/*     */     
/*     */ 
/*     */     protected String getPassword()
/*     */     {
/* 654 */       return this.password;
/*     */     }
/*     */     
/*     */ 
/*     */     protected boolean savePassword()
/*     */     {
/* 660 */       return this.persist;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   protected class authCache
/*     */   {
/*     */     private String key;
/*     */     private PasswordAuthentication auth;
/*     */     private boolean persist;
/* 670 */     private int life = 5;
/*     */     
/*     */ 
/*     */     private boolean succeeded;
/*     */     
/*     */ 
/*     */ 
/*     */     protected authCache(String _key, PasswordAuthentication _auth, boolean _persist)
/*     */     {
/* 679 */       this.key = _key;
/* 680 */       this.auth = _auth;
/* 681 */       this.persist = _persist;
/*     */     }
/*     */     
/*     */ 
/*     */     protected String getKey()
/*     */     {
/* 687 */       return this.key;
/*     */     }
/*     */     
/*     */ 
/*     */     protected boolean isPersistent()
/*     */     {
/* 693 */       return this.persist;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     protected void setOutcome(boolean success)
/*     */     {
/* 700 */       if (success)
/*     */       {
/* 702 */         this.succeeded = true;
/*     */       }
/*     */       else
/*     */       {
/* 706 */         if (this.persist)
/*     */         {
/* 708 */           this.persist = false;
/*     */           
/* 710 */           AuthenticatorWindow.this.saveAuthCache();
/*     */         }
/*     */         
/* 713 */         if (!this.succeeded)
/*     */         {
/* 715 */           this.auth = null;
/*     */         }
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */     protected PasswordAuthentication getAuth()
/*     */     {
/* 723 */       if (this.succeeded)
/*     */       {
/* 725 */         return this.auth;
/*     */       }
/*     */       
/* 728 */       this.life -= 1;
/*     */       
/* 730 */       if (this.life >= 0)
/*     */       {
/* 732 */         return this.auth;
/*     */       }
/*     */       
/* 735 */       if (this.persist)
/*     */       {
/* 737 */         this.persist = false;
/*     */         
/* 739 */         AuthenticatorWindow.this.saveAuthCache();
/*     */       }
/*     */       
/* 742 */       return null;
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/auth/AuthenticatorWindow.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */