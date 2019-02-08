/*     */ package org.gudy.azureus2.ui.swt.config.wizard;
/*     */ 
/*     */ import com.aelitis.azureus.core.AzureusCore;
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
/*     */ import org.eclipse.swt.widgets.Text;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.core3.ipchecker.natchecker.NatChecker;
/*     */ import org.gudy.azureus2.core3.util.AERunnable;
/*     */ import org.gudy.azureus2.core3.util.AEThread;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.ui.swt.Messages;
/*     */ import org.gudy.azureus2.ui.swt.Utils;
/*     */ import org.gudy.azureus2.ui.swt.shells.CoreWaiterSWT;
/*     */ import org.gudy.azureus2.ui.swt.wizard.AbstractWizardPanel;
/*     */ import org.gudy.azureus2.ui.swt.wizard.IWizardPanel;
/*     */ import org.gudy.azureus2.ui.swt.wizard.Wizard;
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
/*     */ public class NatPanel
/*     */   extends AbstractWizardPanel
/*     */ {
/*     */   StyledText textResults;
/*     */   Button bTestTCP;
/*     */   Button bTestUDP;
/*     */   
/*     */   public class CheckerTCP
/*     */     extends AEThread
/*     */   {
/*     */     private AzureusCore core;
/*     */     private int TCPListenPort;
/*     */     
/*     */     public CheckerTCP(AzureusCore _core, int tcp_listen_port)
/*     */     {
/*  66 */       super();
/*  67 */       this.core = _core;
/*  68 */       this.TCPListenPort = tcp_listen_port;
/*     */     }
/*     */     
/*     */     public void runSupport()
/*     */     {
/*     */       try
/*     */       {
/*  75 */         NatPanel.this.printMessage(MessageText.getString("configureWizard.nat.testing") + " TCP " + this.TCPListenPort + " ... ");
/*  76 */         NatChecker checker = new NatChecker(this.core, NetworkAdmin.getSingleton().getMultiHomedServiceBindAddresses(true)[0], this.TCPListenPort, false);
/*  77 */         switch (checker.getResult()) {
/*     */         case 1: 
/*  79 */           NatPanel.this.printMessage("\n" + MessageText.getString("configureWizard.nat.ok") + "\n" + checker.getAdditionalInfo());
/*  80 */           break;
/*     */         case 2: 
/*  82 */           NatPanel.this.printMessage("\n" + MessageText.getString("configureWizard.nat.ko") + " - " + checker.getAdditionalInfo() + ".\n");
/*  83 */           break;
/*     */         default: 
/*  85 */           NatPanel.this.printMessage("\n" + MessageText.getString("configureWizard.nat.unable") + ". \n(" + checker.getAdditionalInfo() + ").\n");
/*     */         }
/*     */       }
/*     */       finally {
/*  89 */         NatPanel.this.enableNext();
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public class CheckerUDP extends AEThread
/*     */   {
/*     */     private AzureusCore core;
/*     */     private int udp_port;
/*     */     
/*     */     public CheckerUDP(AzureusCore _core, int _udp_port) {
/* 100 */       super();
/* 101 */       this.core = _core;
/* 102 */       this.udp_port = _udp_port;
/*     */     }
/*     */     
/*     */     public void runSupport()
/*     */     {
/*     */       try
/*     */       {
/* 109 */         NetworkAdmin admin = NetworkAdmin.getSingleton();
/*     */         
/* 111 */         NetworkAdminProtocol[] inbound_protocols = admin.getInboundProtocols(this.core);
/*     */         
/* 113 */         NetworkAdminProtocol selected = null;
/*     */         
/* 115 */         for (NetworkAdminProtocol p : inbound_protocols)
/*     */         {
/* 117 */           if ((p.getType() == 3) && (p.getPort() == this.udp_port))
/*     */           {
/* 119 */             selected = p;
/*     */             
/* 121 */             break;
/*     */           }
/*     */         }
/*     */         
/* 125 */         if (selected == null)
/*     */         {
/* 127 */           selected = admin.createInboundProtocol(this.core, 3, this.udp_port);
/*     */         }
/*     */         
/* 130 */         if (selected == null)
/*     */         {
/* 132 */           NatPanel.this.printMessage("\n" + MessageText.getString("configureWizard.nat.ko") + ". \n( No UDP protocols enabled ).\n");
/*     */         }
/*     */         else
/*     */         {
/* 136 */           NatPanel.this.printMessage(MessageText.getString("configureWizard.nat.testing") + " UDP " + this.udp_port + " ... ");
/*     */           try
/*     */           {
/* 139 */             selected.test(null, true, new NetworkAdminProgressListener()
/*     */             {
/*     */ 
/*     */ 
/*     */ 
/*     */               public void reportProgress(String task)
/*     */               {
/*     */ 
/*     */ 
/* 148 */                 NatPanel.this.printMessage("\n    " + task);
/*     */               }
/*     */               
/* 151 */             });
/* 152 */             NatPanel.this.printMessage("\n" + MessageText.getString("configureWizard.nat.ok"));
/*     */           }
/*     */           catch (Throwable e)
/*     */           {
/* 156 */             NatPanel.this.printMessage("\n" + MessageText.getString("configureWizard.nat.ko") + ". " + Debug.getNestedExceptionMessage(e) + ".\n");
/*     */           }
/*     */         }
/*     */       }
/*     */       finally {
/* 161 */         NatPanel.this.enableNext();
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public NatPanel(ConfigureWizard wizard, IWizardPanel previous) {
/* 167 */     super(wizard, previous);
/*     */   }
/*     */   
/*     */   public void show() {
/* 171 */     this.wizard.setTitle(MessageText.getString("configureWizard.nat.title"));
/*     */     
/* 173 */     Composite rootPanel = this.wizard.getPanel();
/* 174 */     GridLayout layout = new GridLayout();
/* 175 */     layout.numColumns = 1;
/* 176 */     rootPanel.setLayout(layout);
/*     */     
/* 178 */     Composite panel = new Composite(rootPanel, 0);
/* 179 */     GridData gridData = new GridData(1808);
/* 180 */     Utils.setLayoutData(panel, gridData);
/* 181 */     layout = new GridLayout();
/* 182 */     layout.numColumns = 4;
/* 183 */     panel.setLayout(layout);
/*     */     
/* 185 */     Label label = new Label(panel, 64);
/* 186 */     gridData = new GridData(768);
/* 187 */     gridData.horizontalSpan = 4;
/* 188 */     Utils.setLayoutData(label, gridData);
/* 189 */     Messages.setLanguageText(label, "configureWizard.nat.message");
/*     */     
/* 191 */     label = new Label(panel, 0);
/* 192 */     gridData = new GridData();
/* 193 */     gridData.horizontalSpan = 4;
/* 194 */     Utils.setLayoutData(label, gridData);
/*     */     
/*     */ 
/*     */ 
/* 198 */     label = new Label(panel, 0);
/* 199 */     gridData = new GridData();
/* 200 */     Utils.setLayoutData(label, gridData);
/* 201 */     Messages.setLanguageText(label, "configureWizard.nat.server.tcp_listen_port");
/*     */     
/* 203 */     final Text textServerTCPListen = new Text(panel, 2048);
/* 204 */     gridData = new GridData(768);
/* 205 */     gridData.widthHint = 80;
/* 206 */     Utils.setLayoutData(textServerTCPListen, gridData);
/* 207 */     textServerTCPListen.setText("" + ((ConfigureWizard)this.wizard).serverTCPListenPort);
/* 208 */     textServerTCPListen.addListener(25, new Listener() {
/*     */       public void handleEvent(Event e) {
/* 210 */         String text = e.text;
/* 211 */         char[] chars = new char[text.length()];
/* 212 */         text.getChars(0, chars.length, chars, 0);
/* 213 */         for (int i = 0; i < chars.length; i++) {
/* 214 */           if (('0' > chars[i]) || (chars[i] > '9')) {
/* 215 */             e.doit = false;
/* 216 */             return;
/*     */           }
/*     */         }
/*     */       }
/* 220 */     });
/* 221 */     textServerTCPListen.addListener(24, new Listener() {
/*     */       public void handleEvent(Event e) {
/*     */         try {
/* 224 */           int TCPListenPort = Integer.parseInt(textServerTCPListen.getText());
/* 225 */           ((ConfigureWizard)NatPanel.this.wizard).serverTCPListenPort = TCPListenPort;
/*     */ 
/*     */         }
/*     */         catch (NumberFormatException ex) {}
/*     */       }
/*     */       
/* 231 */     });
/* 232 */     this.bTestTCP = new Button(panel, 8);
/* 233 */     Messages.setLanguageText(this.bTestTCP, "configureWizard.nat.test");
/* 234 */     gridData = new GridData();
/* 235 */     gridData.widthHint = 70;
/* 236 */     Utils.setLayoutData(this.bTestTCP, gridData);
/*     */     
/* 238 */     label = new Label(panel, 0);
/*     */     
/*     */ 
/*     */ 
/* 242 */     label = new Label(panel, 0);
/* 243 */     gridData = new GridData();
/* 244 */     Utils.setLayoutData(label, gridData);
/* 245 */     Messages.setLanguageText(label, "configureWizard.nat.server.udp_listen_port");
/*     */     
/* 247 */     final Text textServerUDPListen = new Text(panel, 2048);
/* 248 */     gridData = new GridData(768);
/* 249 */     gridData.widthHint = 80;
/* 250 */     Utils.setLayoutData(textServerUDPListen, gridData);
/* 251 */     textServerUDPListen.setText("" + ((ConfigureWizard)this.wizard).serverUDPListenPort);
/* 252 */     textServerUDPListen.addListener(25, new Listener() {
/*     */       public void handleEvent(Event e) {
/* 254 */         String text = e.text;
/* 255 */         char[] chars = new char[text.length()];
/* 256 */         text.getChars(0, chars.length, chars, 0);
/* 257 */         for (int i = 0; i < chars.length; i++) {
/* 258 */           if (('0' > chars[i]) || (chars[i] > '9')) {
/* 259 */             e.doit = false;
/* 260 */             return;
/*     */           }
/*     */         }
/*     */       }
/* 264 */     });
/* 265 */     textServerUDPListen.addListener(24, new Listener() {
/*     */       public void handleEvent(Event e) {
/*     */         try {
/* 268 */           int UDPListenPort = Integer.parseInt(textServerUDPListen.getText());
/* 269 */           ((ConfigureWizard)NatPanel.this.wizard).serverUDPListenPort = UDPListenPort;
/*     */ 
/*     */         }
/*     */         catch (NumberFormatException ex) {}
/*     */       }
/*     */       
/* 275 */     });
/* 276 */     this.bTestUDP = new Button(panel, 8);
/* 277 */     Messages.setLanguageText(this.bTestUDP, "configureWizard.nat.test");
/* 278 */     gridData = new GridData();
/* 279 */     gridData.widthHint = 70;
/* 280 */     Utils.setLayoutData(this.bTestUDP, gridData);
/*     */     
/* 282 */     label = new Label(panel, 0);
/*     */     
/*     */ 
/*     */ 
/* 286 */     this.textResults = new StyledText(panel, 2626);
/* 287 */     gridData = new GridData(1808);
/* 288 */     gridData.heightHint = 70;
/* 289 */     gridData.horizontalSpan = 4;
/* 290 */     Utils.setLayoutData(this.textResults, gridData);
/* 291 */     this.textResults.setBackground(panel.getDisplay().getSystemColor(1));
/*     */     
/* 293 */     this.bTestTCP.addListener(13, new Listener() {
/*     */       public void handleEvent(Event event) {
/* 295 */         NatPanel.this.wizard.setNextEnabled(false);
/* 296 */         NatPanel.this.bTestTCP.setEnabled(false);
/* 297 */         NatPanel.this.bTestUDP.setEnabled(false);
/* 298 */         NatPanel.this.textResults.setText("");
/* 299 */         CoreWaiterSWT.waitForCoreRunning(new AzureusCoreRunningListener()
/*     */         {
/*     */           public void azureusCoreRunning(AzureusCore core) {
/* 302 */             ConfigureWizard cw = (ConfigureWizard)NatPanel.this.wizard;
/*     */             
/* 304 */             int TCPListenPort = cw.serverTCPListenPort;
/* 305 */             NatPanel.CheckerTCP checker = new NatPanel.CheckerTCP(NatPanel.this, core, TCPListenPort);
/* 306 */             checker.start();
/*     */           }
/*     */           
/*     */         });
/*     */       }
/* 311 */     });
/* 312 */     this.bTestUDP.addListener(13, new Listener() {
/*     */       public void handleEvent(Event event) {
/* 314 */         NatPanel.this.wizard.setNextEnabled(false);
/* 315 */         NatPanel.this.bTestTCP.setEnabled(false);
/* 316 */         NatPanel.this.bTestUDP.setEnabled(false);
/* 317 */         NatPanel.this.textResults.setText("");
/* 318 */         CoreWaiterSWT.waitForCoreRunning(new AzureusCoreRunningListener()
/*     */         {
/*     */           public void azureusCoreRunning(AzureusCore core) {
/* 321 */             ConfigureWizard cw = (ConfigureWizard)NatPanel.this.wizard;
/*     */             
/* 323 */             int UDPListenPort = cw.serverUDPListenPort;
/* 324 */             NatPanel.CheckerUDP checker = new NatPanel.CheckerUDP(NatPanel.this, core, UDPListenPort);
/* 325 */             checker.start();
/*     */           }
/*     */         });
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */   public void printMessage(final String message) {
/* 333 */     Display display = this.wizard.getDisplay();
/* 334 */     if ((display == null) || (display.isDisposed()))
/* 335 */       return;
/* 336 */     display.asyncExec(new AERunnable() {
/*     */       public void runSupport() {
/* 338 */         if ((NatPanel.this.textResults == null) || (NatPanel.this.textResults.isDisposed()))
/* 339 */           return;
/* 340 */         NatPanel.this.textResults.append(message);
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */   private void enableNext() {
/* 346 */     Display display = this.wizard.getDisplay();
/* 347 */     if ((display == null) || (display.isDisposed()))
/* 348 */       return;
/* 349 */     display.asyncExec(new AERunnable() {
/*     */       public void runSupport() {
/* 351 */         if ((NatPanel.this.bTestTCP == null) || (NatPanel.this.bTestTCP.isDisposed())) {
/* 352 */           return;
/*     */         }
/* 354 */         if ((NatPanel.this.bTestUDP == null) || (NatPanel.this.bTestUDP.isDisposed())) {
/* 355 */           return;
/*     */         }
/*     */         
/* 358 */         NatPanel.this.wizard.setNextEnabled(true);
/* 359 */         NatPanel.this.bTestTCP.setEnabled(true);
/* 360 */         NatPanel.this.bTestUDP.setEnabled(true);
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */   public boolean isNextEnabled() {
/* 366 */     return true;
/*     */   }
/*     */   
/*     */   public IWizardPanel getNextPanel() {
/* 370 */     return new FilePanel((ConfigureWizard)this.wizard, this);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/config/wizard/NatPanel.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */