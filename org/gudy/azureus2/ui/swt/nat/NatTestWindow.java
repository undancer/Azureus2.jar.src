/*     */ package org.gudy.azureus2.ui.swt.nat;
/*     */ 
/*     */ import com.aelitis.azureus.core.AzureusCore;
/*     */ import com.aelitis.azureus.core.AzureusCoreFactory;
/*     */ import com.aelitis.azureus.core.AzureusCoreRunningListener;
/*     */ import com.aelitis.azureus.core.networkmanager.admin.NetworkAdmin;
/*     */ import com.aelitis.azureus.core.networkmanager.admin.NetworkAdminProgressListener;
/*     */ import com.aelitis.azureus.core.networkmanager.admin.NetworkAdminProtocol;
/*     */ import org.eclipse.swt.custom.StyledText;
/*     */ import org.eclipse.swt.layout.GridData;
/*     */ import org.eclipse.swt.layout.GridLayout;
/*     */ import org.eclipse.swt.widgets.Button;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.Display;
/*     */ import org.eclipse.swt.widgets.Event;
/*     */ import org.eclipse.swt.widgets.Label;
/*     */ import org.eclipse.swt.widgets.Listener;
/*     */ import org.eclipse.swt.widgets.Shell;
/*     */ import org.eclipse.swt.widgets.Text;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.core3.ipchecker.natchecker.NatChecker;
/*     */ import org.gudy.azureus2.core3.util.AERunnable;
/*     */ import org.gudy.azureus2.core3.util.AEThread;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.ui.swt.Messages;
/*     */ import org.gudy.azureus2.ui.swt.Utils;
/*     */ import org.gudy.azureus2.ui.swt.components.shell.ShellFactory;
/*     */ import org.gudy.azureus2.ui.swt.shells.CoreWaiterSWT;
/*     */ import org.gudy.azureus2.ui.swt.shells.CoreWaiterSWT.TriggerInThread;
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
/*     */ public class NatTestWindow
/*     */ {
/*     */   Display display;
/*     */   Button bTestTCP;
/*     */   Button bTestUDP;
/*     */   Button bApply;
/*     */   Button bCancel;
/*     */   StyledText textResults;
/*     */   int serverTCPListenPort;
/*     */   int serverUDPListenPort;
/*     */   
/*     */   public class CheckerTCP
/*     */     extends AEThread
/*     */   {
/*     */     private int TCPListenPort;
/*     */     
/*     */     public CheckerTCP(int tcp_listen_port)
/*     */     {
/*  62 */       super();
/*  63 */       this.TCPListenPort = tcp_listen_port;
/*     */     }
/*     */     
/*     */     public void runSupport()
/*     */     {
/*     */       try
/*     */       {
/*  70 */         NatTestWindow.this.printMessage(MessageText.getString("configureWizard.nat.testing") + " TCP " + this.TCPListenPort + " ... ");
/*  71 */         NatChecker checker = new NatChecker(AzureusCoreFactory.getSingleton(), NetworkAdmin.getSingleton().getMultiHomedServiceBindAddresses(true)[0], this.TCPListenPort, false);
/*  72 */         switch (checker.getResult()) {
/*     */         case 1: 
/*  74 */           NatTestWindow.this.printMessage("\n" + MessageText.getString("configureWizard.nat.ok") + "\n" + checker.getAdditionalInfo());
/*  75 */           break;
/*     */         case 2: 
/*  77 */           NatTestWindow.this.printMessage("\n" + MessageText.getString("configureWizard.nat.ko") + " - " + checker.getAdditionalInfo() + ".\n");
/*  78 */           break;
/*     */         default: 
/*  80 */           NatTestWindow.this.printMessage("\n" + MessageText.getString("configureWizard.nat.unable") + ". \n(" + checker.getAdditionalInfo() + ").\n");
/*     */         }
/*     */       }
/*     */       finally {
/*  84 */         if (NatTestWindow.this.display.isDisposed()) return;
/*  85 */         NatTestWindow.this.display.asyncExec(new AERunnable() {
/*     */           public void runSupport() {
/*  87 */             if ((NatTestWindow.this.bTestTCP != null) && (!NatTestWindow.this.bTestTCP.isDisposed()))
/*  88 */               NatTestWindow.this.bTestTCP.setEnabled(true);
/*  89 */             if ((NatTestWindow.this.bTestUDP != null) && (!NatTestWindow.this.bTestUDP.isDisposed()))
/*  90 */               NatTestWindow.this.bTestUDP.setEnabled(true);
/*  91 */             if ((NatTestWindow.this.bApply != null) && (!NatTestWindow.this.bApply.isDisposed())) {
/*  92 */               NatTestWindow.this.bApply.setEnabled(true);
/*     */             }
/*     */           }
/*     */         });
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public class CheckerUDP extends AEThread {
/*     */     private AzureusCore core;
/*     */     private int udp_port;
/*     */     
/*     */     public CheckerUDP(AzureusCore _core, int _udp_port) {
/* 105 */       super();
/* 106 */       this.core = _core;
/* 107 */       this.udp_port = _udp_port;
/*     */     }
/*     */     
/*     */     public void runSupport()
/*     */     {
/*     */       try
/*     */       {
/* 114 */         NetworkAdmin admin = NetworkAdmin.getSingleton();
/*     */         
/* 116 */         NetworkAdminProtocol[] inbound_protocols = admin.getInboundProtocols(this.core);
/*     */         
/* 118 */         NetworkAdminProtocol selected = null;
/*     */         
/* 120 */         for (NetworkAdminProtocol p : inbound_protocols)
/*     */         {
/* 122 */           if ((p.getType() == 3) && (p.getPort() == this.udp_port))
/*     */           {
/* 124 */             selected = p;
/*     */             
/* 126 */             break;
/*     */           }
/*     */         }
/*     */         
/* 130 */         if (selected == null)
/*     */         {
/* 132 */           selected = admin.createInboundProtocol(this.core, 3, this.udp_port);
/*     */         }
/*     */         
/* 135 */         if (selected == null)
/*     */         {
/* 137 */           NatTestWindow.this.printMessage("\n" + MessageText.getString("configureWizard.nat.ko") + ". \n( No UDP protocols enabled ).\n");
/*     */         }
/*     */         else
/*     */         {
/* 141 */           NatTestWindow.this.printMessage(MessageText.getString("configureWizard.nat.testing") + " UDP " + this.udp_port + " ... ");
/*     */           try
/*     */           {
/* 144 */             selected.test(null, true, new NetworkAdminProgressListener()
/*     */             {
/*     */ 
/*     */ 
/*     */ 
/*     */               public void reportProgress(String task)
/*     */               {
/*     */ 
/*     */ 
/* 153 */                 NatTestWindow.this.printMessage("\n    " + task);
/*     */               }
/*     */               
/* 156 */             });
/* 157 */             NatTestWindow.this.printMessage("\n" + MessageText.getString("configureWizard.nat.ok"));
/*     */           }
/*     */           catch (Throwable e)
/*     */           {
/* 161 */             NatTestWindow.this.printMessage("\n" + MessageText.getString("configureWizard.nat.ko") + ". " + Debug.getNestedExceptionMessage(e) + ".\n");
/*     */           }
/*     */         }
/*     */       }
/*     */       finally {
/* 166 */         if (NatTestWindow.this.display.isDisposed()) return;
/* 167 */         NatTestWindow.this.display.asyncExec(new AERunnable() {
/*     */           public void runSupport() {
/* 169 */             if ((NatTestWindow.this.bTestTCP != null) && (!NatTestWindow.this.bTestTCP.isDisposed()))
/* 170 */               NatTestWindow.this.bTestTCP.setEnabled(true);
/* 171 */             if ((NatTestWindow.this.bTestUDP != null) && (!NatTestWindow.this.bTestUDP.isDisposed()))
/* 172 */               NatTestWindow.this.bTestUDP.setEnabled(true);
/* 173 */             if ((NatTestWindow.this.bApply != null) && (!NatTestWindow.this.bApply.isDisposed())) {
/* 174 */               NatTestWindow.this.bApply.setEnabled(true);
/*     */             }
/*     */           }
/*     */         });
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public NatTestWindow() {
/* 183 */     this.serverTCPListenPort = COConfigurationManager.getIntParameter("TCP.Listen.Port");
/* 184 */     this.serverUDPListenPort = COConfigurationManager.getIntParameter("UDP.Listen.Port");
/*     */     
/* 186 */     final Shell shell = ShellFactory.createMainShell(2144);
/* 187 */     shell.setText(MessageText.getString("configureWizard.nat.title"));
/* 188 */     Utils.setShellIcon(shell);
/*     */     
/* 190 */     this.display = shell.getDisplay();
/*     */     
/* 192 */     GridLayout layout = new GridLayout();
/* 193 */     layout.numColumns = 1;
/* 194 */     shell.setLayout(layout);
/*     */     
/* 196 */     Composite panel = new Composite(shell, 0);
/* 197 */     GridData gridData = new GridData(772);
/* 198 */     Utils.setLayoutData(panel, gridData);
/* 199 */     layout = new GridLayout();
/* 200 */     layout.numColumns = 3;
/* 201 */     panel.setLayout(layout);
/*     */     
/* 203 */     Label label = new Label(panel, 64);
/* 204 */     gridData = new GridData();
/* 205 */     gridData.horizontalSpan = 3;
/* 206 */     gridData.widthHint = 400;
/* 207 */     Utils.setLayoutData(label, gridData);
/* 208 */     Messages.setLanguageText(label, "configureWizard.nat.message");
/*     */     
/* 210 */     label = new Label(panel, 0);
/* 211 */     label = new Label(panel, 0);
/* 212 */     label = new Label(panel, 0);
/* 213 */     label = new Label(panel, 0);
/*     */     
/*     */ 
/*     */ 
/* 217 */     Messages.setLanguageText(label, "configureWizard.nat.server.tcp_listen_port");
/*     */     
/* 219 */     final Text textServerTCPListen = new Text(panel, 2048);
/* 220 */     gridData = new GridData();
/* 221 */     gridData.grabExcessHorizontalSpace = true;
/* 222 */     gridData.horizontalAlignment = 4;
/* 223 */     Utils.setLayoutData(textServerTCPListen, gridData);
/* 224 */     textServerTCPListen.setText("" + this.serverTCPListenPort);
/* 225 */     textServerTCPListen.addListener(25, new Listener() {
/*     */       public void handleEvent(Event e) {
/* 227 */         String text = e.text;
/* 228 */         char[] chars = new char[text.length()];
/* 229 */         text.getChars(0, chars.length, chars, 0);
/* 230 */         for (int i = 0; i < chars.length; i++) {
/* 231 */           if (('0' > chars[i]) || (chars[i] > '9')) {
/* 232 */             e.doit = false;
/* 233 */             return;
/*     */           }
/*     */           
/*     */         }
/*     */       }
/* 238 */     });
/* 239 */     textServerTCPListen.addListener(24, new Listener() {
/*     */       public void handleEvent(Event e) {
/*     */         try {
/* 242 */           int TCPListenPort = Integer.parseInt(textServerTCPListen.getText());
/* 243 */           NatTestWindow.this.serverTCPListenPort = TCPListenPort;
/*     */ 
/*     */         }
/*     */         catch (Throwable f) {}
/*     */       }
/* 248 */     });
/* 249 */     this.bTestTCP = new Button(panel, 8);
/* 250 */     Messages.setLanguageText(this.bTestTCP, "configureWizard.nat.test");
/* 251 */     gridData = new GridData();
/* 252 */     gridData.widthHint = 70;
/* 253 */     Utils.setLayoutData(this.bTestTCP, gridData);
/*     */     
/* 255 */     label = new Label(panel, 0);
/*     */     
/*     */ 
/*     */ 
/* 259 */     Messages.setLanguageText(label, "configureWizard.nat.server.udp_listen_port");
/*     */     
/* 261 */     final Text textServerUDPListen = new Text(panel, 2048);
/* 262 */     gridData = new GridData();
/* 263 */     gridData.grabExcessHorizontalSpace = true;
/* 264 */     gridData.horizontalAlignment = 4;
/* 265 */     Utils.setLayoutData(textServerUDPListen, gridData);
/* 266 */     textServerUDPListen.setText("" + this.serverUDPListenPort);
/* 267 */     textServerUDPListen.addListener(25, new Listener() {
/*     */       public void handleEvent(Event e) {
/* 269 */         String text = e.text;
/* 270 */         char[] chars = new char[text.length()];
/* 271 */         text.getChars(0, chars.length, chars, 0);
/* 272 */         for (int i = 0; i < chars.length; i++) {
/* 273 */           if (('0' > chars[i]) || (chars[i] > '9')) {
/* 274 */             e.doit = false;
/* 275 */             return;
/*     */           }
/*     */           
/*     */         }
/*     */       }
/* 280 */     });
/* 281 */     textServerUDPListen.addListener(24, new Listener() {
/*     */       public void handleEvent(Event e) {
/*     */         try {
/* 284 */           int UDPListenPort = Integer.parseInt(textServerUDPListen.getText());
/* 285 */           NatTestWindow.this.serverUDPListenPort = UDPListenPort;
/*     */ 
/*     */         }
/*     */         catch (Throwable f) {}
/*     */       }
/* 290 */     });
/* 291 */     this.bTestUDP = new Button(panel, 8);
/* 292 */     Messages.setLanguageText(this.bTestUDP, "configureWizard.nat.test");
/* 293 */     gridData = new GridData();
/* 294 */     gridData.widthHint = 70;
/* 295 */     Utils.setLayoutData(this.bTestUDP, gridData);
/*     */     
/*     */ 
/*     */ 
/* 299 */     this.textResults = new StyledText(panel, 2626);
/* 300 */     gridData = new GridData();
/* 301 */     gridData.widthHint = 400;
/* 302 */     gridData.heightHint = 100;
/* 303 */     gridData.grabExcessVerticalSpace = true;
/* 304 */     gridData.verticalAlignment = 4;
/* 305 */     gridData.horizontalSpan = 3;
/* 306 */     Utils.setLayoutData(this.textResults, gridData);
/* 307 */     this.textResults.setBackground(panel.getDisplay().getSystemColor(1));
/*     */     
/* 309 */     this.bTestTCP.addListener(13, new Listener() {
/*     */       public void handleEvent(Event event) {
/* 311 */         NatTestWindow.this.bTestUDP.setEnabled(false);
/* 312 */         NatTestWindow.this.bTestTCP.setEnabled(false);
/* 313 */         NatTestWindow.this.bApply.setEnabled(false);
/* 314 */         NatTestWindow.this.textResults.setText("");
/*     */         
/* 316 */         CoreWaiterSWT.waitForCore(CoreWaiterSWT.TriggerInThread.ANY_THREAD, new AzureusCoreRunningListener()
/*     */         {
/*     */           public void azureusCoreRunning(AzureusCore core) {
/* 319 */             NatTestWindow.CheckerTCP checker = new NatTestWindow.CheckerTCP(NatTestWindow.this, NatTestWindow.this.serverTCPListenPort);
/* 320 */             checker.start();
/*     */           }
/*     */           
/*     */ 
/*     */         });
/*     */       }
/* 326 */     });
/* 327 */     this.bTestUDP.addListener(13, new Listener() {
/*     */       public void handleEvent(Event event) {
/* 329 */         NatTestWindow.this.bTestUDP.setEnabled(false);
/* 330 */         NatTestWindow.this.bTestTCP.setEnabled(false);
/* 331 */         NatTestWindow.this.bApply.setEnabled(false);
/* 332 */         NatTestWindow.this.textResults.setText("");
/*     */         
/* 334 */         CoreWaiterSWT.waitForCore(CoreWaiterSWT.TriggerInThread.ANY_THREAD, new AzureusCoreRunningListener()
/*     */         {
/*     */           public void azureusCoreRunning(AzureusCore core) {
/* 337 */             NatTestWindow.CheckerUDP checker = new NatTestWindow.CheckerUDP(NatTestWindow.this, core, NatTestWindow.this.serverUDPListenPort);
/* 338 */             checker.start();
/*     */           }
/*     */           
/*     */         });
/*     */       }
/* 343 */     });
/* 344 */     this.bApply = new Button(panel, 8);
/* 345 */     this.bApply.setText(MessageText.getString("Button.apply"));
/* 346 */     gridData = new GridData();
/* 347 */     gridData.widthHint = 70;
/* 348 */     gridData.grabExcessHorizontalSpace = true;
/* 349 */     gridData.horizontalAlignment = 131072;
/* 350 */     gridData.horizontalSpan = 2;
/* 351 */     Utils.setLayoutData(this.bApply, gridData);
/*     */     
/*     */ 
/* 354 */     this.bApply.addListener(13, new Listener() {
/*     */       public void handleEvent(Event event) {
/* 356 */         int old_tcp = COConfigurationManager.getIntParameter("TCP.Listen.Port");
/* 357 */         int old_udp = COConfigurationManager.getIntParameter("UDP.Listen.Port");
/* 358 */         int old_udp2 = COConfigurationManager.getIntParameter("UDP.NonData.Listen.Port");
/*     */         
/* 360 */         if (old_tcp != NatTestWindow.this.serverTCPListenPort) {
/* 361 */           COConfigurationManager.setParameter("TCP.Listen.Port", NatTestWindow.this.serverTCPListenPort);
/*     */         }
/*     */         
/* 364 */         if (old_udp != NatTestWindow.this.serverUDPListenPort) {
/* 365 */           COConfigurationManager.setParameter("UDP.Listen.Port", NatTestWindow.this.serverUDPListenPort);
/*     */           
/* 367 */           if (old_udp == old_udp2) {
/* 368 */             COConfigurationManager.setParameter("UDP.NonData.Listen.Port", NatTestWindow.this.serverUDPListenPort);
/*     */           }
/*     */         }
/*     */         
/* 372 */         COConfigurationManager.save();
/*     */         
/* 374 */         shell.close();
/*     */       }
/*     */       
/* 377 */     });
/* 378 */     this.bCancel = new Button(panel, 8);
/* 379 */     this.bCancel.setText(MessageText.getString("Button.cancel"));
/* 380 */     gridData = new GridData();
/* 381 */     gridData.widthHint = 70;
/* 382 */     Utils.setLayoutData(this.bCancel, gridData);
/* 383 */     this.bCancel.addListener(13, new Listener() {
/*     */       public void handleEvent(Event event) {
/* 385 */         shell.close();
/*     */       }
/*     */       
/* 388 */     });
/* 389 */     shell.setDefaultButton(this.bApply);
/*     */     
/* 391 */     shell.addListener(31, new Listener() {
/*     */       public void handleEvent(Event e) {
/* 393 */         if (e.character == '\033') {
/* 394 */           shell.close();
/*     */         }
/*     */         
/*     */       }
/* 398 */     });
/* 399 */     shell.pack();
/* 400 */     Utils.centreWindow(shell);
/* 401 */     shell.open();
/*     */   }
/*     */   
/*     */   public void printMessage(final String message) {
/* 405 */     if ((this.display == null) || (this.display.isDisposed()))
/* 406 */       return;
/* 407 */     this.display.asyncExec(new AERunnable() {
/*     */       public void runSupport() {
/* 409 */         if ((NatTestWindow.this.textResults == null) || (NatTestWindow.this.textResults.isDisposed()))
/* 410 */           return;
/* 411 */         NatTestWindow.this.textResults.append(message);
/*     */       }
/*     */     });
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/nat/NatTestWindow.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */