/*     */ package org.gudy.azureus2.ui.swt.views.configsections;
/*     */ 
/*     */ import com.aelitis.azureus.core.networkmanager.admin.NetworkAdmin;
/*     */ import java.net.Socket;
/*     */ import java.nio.channels.SocketChannel;
/*     */ import org.eclipse.swt.graphics.Color;
/*     */ import org.eclipse.swt.layout.GridData;
/*     */ import org.eclipse.swt.layout.GridLayout;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.Control;
/*     */ import org.eclipse.swt.widgets.Group;
/*     */ import org.eclipse.swt.widgets.Label;
/*     */ import org.eclipse.swt.widgets.Text;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.config.impl.ConfigurationManager;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.core3.util.Constants;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.platform.PlatformManager;
/*     */ import org.gudy.azureus2.platform.PlatformManagerCapabilities;
/*     */ import org.gudy.azureus2.platform.PlatformManagerFactory;
/*     */ import org.gudy.azureus2.plugins.platform.PlatformManagerException;
/*     */ import org.gudy.azureus2.ui.swt.Messages;
/*     */ import org.gudy.azureus2.ui.swt.Utils;
/*     */ import org.gudy.azureus2.ui.swt.components.LinkLabel;
/*     */ import org.gudy.azureus2.ui.swt.config.BooleanParameter;
/*     */ import org.gudy.azureus2.ui.swt.config.IntParameter;
/*     */ import org.gudy.azureus2.ui.swt.config.Parameter;
/*     */ import org.gudy.azureus2.ui.swt.config.ParameterChangeAdapter;
/*     */ import org.gudy.azureus2.ui.swt.config.StringParameter;
/*     */ import org.gudy.azureus2.ui.swt.mainwindow.Colors;
/*     */ import org.gudy.azureus2.ui.swt.plugins.UISWTConfigSection;
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
/*     */ public class ConfigSectionConnectionAdvanced
/*     */   implements UISWTConfigSection
/*     */ {
/*     */   private static final String CFG_PREFIX = "ConfigView.section.connection.advanced.";
/*     */   private static final int REQUIRED_MODE = 2;
/*     */   
/*     */   public int maxUserMode()
/*     */   {
/*  55 */     return 2;
/*     */   }
/*     */   
/*     */   public String configSectionGetParentSection() {
/*  59 */     return "server";
/*     */   }
/*     */   
/*     */   public String configSectionGetName() {
/*  63 */     return "connection.advanced";
/*     */   }
/*     */   
/*     */ 
/*     */   public void configSectionSave() {}
/*     */   
/*     */ 
/*     */   public void configSectionDelete() {}
/*     */   
/*     */ 
/*     */   public Composite configSectionCreate(Composite parent)
/*     */   {
/*  75 */     Composite cSection = new Composite(parent, 0);
/*     */     
/*  77 */     GridData gridData = new GridData(272);
/*  78 */     Utils.setLayoutData(cSection, gridData);
/*  79 */     GridLayout advanced_layout = new GridLayout();
/*  80 */     cSection.setLayout(advanced_layout);
/*     */     
/*  82 */     int userMode = COConfigurationManager.getIntParameter("User Mode");
/*  83 */     if (userMode < 2) {
/*  84 */       Label label = new Label(cSection, 64);
/*  85 */       gridData = new GridData();
/*  86 */       Utils.setLayoutData(label, gridData);
/*     */       
/*  88 */       String[] modeKeys = { "ConfigView.section.mode.beginner", "ConfigView.section.mode.intermediate", "ConfigView.section.mode.advanced" };
/*     */       
/*     */       String param1;
/*     */       
/*     */       String param1;
/*  93 */       if (2 < modeKeys.length) {
/*  94 */         param1 = MessageText.getString(modeKeys[2]);
/*     */       } else
/*  96 */         param1 = String.valueOf(2);
/*     */       String param2;
/*  98 */       String param2; if (userMode < modeKeys.length) {
/*  99 */         param2 = MessageText.getString(modeKeys[userMode]);
/*     */       } else {
/* 101 */         param2 = String.valueOf(userMode);
/*     */       }
/* 103 */       label.setText(MessageText.getString("ConfigView.notAvailableForMode", new String[] { param1, param2 }));
/*     */       
/*     */ 
/* 106 */       return cSection;
/*     */     }
/*     */     
/* 109 */     new LinkLabel(cSection, gridData, "ConfigView.section.connection.advanced.info.link", MessageText.getString("ConfigView.section.connection.advanced.url"));
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 114 */     Group gSocket = new Group(cSection, 0);
/* 115 */     Messages.setLanguageText(gSocket, "ConfigView.section.connection.advanced.socket.group");
/* 116 */     gridData = new GridData(784);
/* 117 */     Utils.setLayoutData(gSocket, gridData);
/* 118 */     GridLayout glayout = new GridLayout();
/* 119 */     glayout.numColumns = 3;
/* 120 */     gSocket.setLayout(glayout);
/*     */     
/*     */ 
/*     */ 
/* 124 */     Label lmaxout = new Label(gSocket, 0);
/* 125 */     Messages.setLanguageText(lmaxout, "ConfigView.section.connection.network.max.simultaneous.connect.attempts");
/* 126 */     gridData = new GridData();
/* 127 */     Utils.setLayoutData(lmaxout, gridData);
/*     */     
/* 129 */     IntParameter max_connects = new IntParameter(gSocket, "network.max.simultaneous.connect.attempts", 1, 100);
/*     */     
/* 131 */     gridData = new GridData();
/* 132 */     gridData.horizontalSpan = 2;
/* 133 */     max_connects.setLayoutData(gridData);
/*     */     
/*     */ 
/*     */ 
/* 137 */     Label lmaxpout = new Label(gSocket, 0);
/* 138 */     Messages.setLanguageText(lmaxpout, "ConfigView.section.connection.network.max.outstanding.connect.attempts");
/* 139 */     gridData = new GridData();
/* 140 */     Utils.setLayoutData(lmaxpout, gridData);
/*     */     
/* 142 */     IntParameter max_pending_connects = new IntParameter(gSocket, "network.tcp.max.connections.outstanding", 1, 65536);
/*     */     
/* 144 */     gridData = new GridData();
/* 145 */     gridData.horizontalSpan = 2;
/* 146 */     max_pending_connects.setLayoutData(gridData);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 152 */     Label lbind = new Label(gSocket, 0);
/* 153 */     Messages.setLanguageText(lbind, "ConfigView.label.bindip");
/* 154 */     gridData = new GridData();
/* 155 */     Utils.setLayoutData(lbind, gridData);
/*     */     
/* 157 */     StringParameter bindip = new StringParameter(gSocket, "Bind IP", "", false);
/* 158 */     gridData = new GridData();
/* 159 */     gridData.widthHint = 100;
/* 160 */     gridData.horizontalSpan = 2;
/* 161 */     bindip.setLayoutData(gridData);
/*     */     
/* 163 */     Text lbind2 = new Text(gSocket, 10);
/* 164 */     lbind2.setTabs(8);
/* 165 */     Messages.setLanguageText(lbind2, "ConfigView.label.bindip.details", new String[] { "\t" + NetworkAdmin.getSingleton().getNetworkInterfacesAsString().replaceAll("\\\n", "\n\t") });
/*     */     
/*     */ 
/*     */ 
/* 169 */     gridData = new GridData();
/* 170 */     gridData.horizontalSpan = 3;
/* 171 */     Utils.setLayoutData(lbind2, gridData);
/*     */     
/*     */ 
/* 174 */     BooleanParameter check_bind = new BooleanParameter(gSocket, "Check Bind IP On Start", "network.check.ipbinding");
/* 175 */     gridData = new GridData();
/* 176 */     gridData.horizontalSpan = 3;
/* 177 */     check_bind.setLayoutData(gridData);
/*     */     
/* 179 */     BooleanParameter force_bind = new BooleanParameter(gSocket, "Enforce Bind IP", "network.enforce.ipbinding");
/* 180 */     gridData = new GridData();
/* 181 */     gridData.horizontalSpan = 3;
/* 182 */     force_bind.setLayoutData(gridData);
/*     */     
/* 184 */     BooleanParameter bind_icon = new BooleanParameter(gSocket, "Show IP Bindings Icon", "network.ipbinding.icon.show");
/* 185 */     gridData = new GridData();
/* 186 */     gridData.horizontalSpan = 3;
/* 187 */     bind_icon.setLayoutData(gridData);
/*     */     
/* 189 */     BooleanParameter vpn_guess_enable = new BooleanParameter(gSocket, "network.admin.maybe.vpn.enable", "network.admin.maybe.vpn.enable");
/* 190 */     gridData = new GridData();
/* 191 */     gridData.horizontalSpan = 3;
/* 192 */     vpn_guess_enable.setLayoutData(gridData);
/*     */     
/*     */ 
/* 195 */     Label lpbind = new Label(gSocket, 0);
/* 196 */     Messages.setLanguageText(lpbind, "ConfigView.section.connection.advanced.bind_port");
/* 197 */     IntParameter port_bind = new IntParameter(gSocket, "network.bind.local.port", 0, 65535);
/*     */     
/* 199 */     gridData = new GridData();
/* 200 */     gridData.horizontalSpan = 2;
/* 201 */     port_bind.setLayoutData(gridData);
/*     */     
/*     */ 
/* 204 */     Label lmtu = new Label(gSocket, 0);
/* 205 */     Messages.setLanguageText(lmtu, "ConfigView.section.connection.advanced.mtu");
/* 206 */     IntParameter mtu_size = new IntParameter(gSocket, "network.tcp.mtu.size");
/* 207 */     mtu_size.setMaximumValue(524288);
/* 208 */     gridData = new GridData();
/* 209 */     gridData.horizontalSpan = 2;
/* 210 */     mtu_size.setLayoutData(gridData);
/*     */     
/*     */ 
/*     */ 
/* 214 */     Label lsend = new Label(gSocket, 0);
/* 215 */     Messages.setLanguageText(lsend, "ConfigView.section.connection.advanced.SO_SNDBUF");
/* 216 */     final IntParameter SO_SNDBUF = new IntParameter(gSocket, "network.tcp.socket.SO_SNDBUF");
/* 217 */     gridData = new GridData();
/* 218 */     SO_SNDBUF.setLayoutData(gridData);
/*     */     
/* 220 */     final Label lsendcurr = new Label(gSocket, 0);
/* 221 */     gridData = new GridData(768);
/* 222 */     gridData.horizontalIndent = 10;
/* 223 */     Utils.setLayoutData(lsendcurr, gridData);
/*     */     
/*     */ 
/*     */ 
/* 227 */     Label lreceiv = new Label(gSocket, 0);
/* 228 */     Messages.setLanguageText(lreceiv, "ConfigView.section.connection.advanced.SO_RCVBUF");
/* 229 */     final IntParameter SO_RCVBUF = new IntParameter(gSocket, "network.tcp.socket.SO_RCVBUF");
/* 230 */     gridData = new GridData();
/* 231 */     SO_RCVBUF.setLayoutData(gridData);
/*     */     
/* 233 */     final Label lreccurr = new Label(gSocket, 0);
/* 234 */     gridData = new GridData(768);
/* 235 */     gridData.horizontalIndent = 10;
/* 236 */     Utils.setLayoutData(lreccurr, gridData);
/*     */     
/* 238 */     final Runnable buff_updater = new Runnable()
/*     */     {
/*     */ 
/*     */       public void run()
/*     */       {
/*     */ 
/* 244 */         SocketChannel sc = null;
/*     */         
/* 246 */         int snd_val = 0;
/* 247 */         int rec_val = 0;
/*     */         try
/*     */         {
/* 250 */           sc = SocketChannel.open();
/*     */           
/* 252 */           Socket socket = sc.socket();
/*     */           
/* 254 */           if (SO_SNDBUF.getValue() == 0)
/*     */           {
/* 256 */             snd_val = socket.getSendBufferSize();
/*     */           }
/*     */           
/* 259 */           if (SO_RCVBUF.getValue() == 0)
/*     */           {
/* 261 */             rec_val = socket.getReceiveBufferSize();
/*     */           }
/*     */           
/*     */ 
/*     */ 
/*     */           try
/*     */           {
/* 268 */             sc.close();
/*     */           }
/*     */           catch (Throwable e) {}
/*     */           
/*     */ 
/*     */ 
/* 274 */           if (snd_val != 0) {
/*     */             break label104;
/*     */           }
/*     */         }
/*     */         catch (Throwable e) {}finally
/*     */         {
/*     */           try
/*     */           {
/* 268 */             sc.close();
/*     */           }
/*     */           catch (Throwable e) {}
/*     */         }
/*     */         
/*     */ 
/*     */ 
/* 275 */         lsendcurr.setText("");
/*     */         break label124;
/* 277 */         label104: Messages.setLanguageText(lsendcurr, "label.current.equals", new String[] { String.valueOf(snd_val) });
/*     */         
/*     */         label124:
/* 280 */         if (rec_val == 0) {
/* 281 */           lreccurr.setText("");
/*     */         } else {
/* 283 */           Messages.setLanguageText(lreccurr, "label.current.equals", new String[] { String.valueOf(rec_val) });
/*     */         }
/*     */         
/*     */       }
/* 287 */     };
/* 288 */     buff_updater.run();
/*     */     
/* 290 */     ParameterChangeAdapter buff_listener = new ParameterChangeAdapter()
/*     */     {
/*     */ 
/*     */ 
/*     */       public void parameterChanged(Parameter p, boolean caused_internally)
/*     */       {
/*     */ 
/*     */ 
/* 298 */         buff_updater.run();
/*     */       }
/*     */       
/* 301 */     };
/* 302 */     SO_RCVBUF.addChangeListener(buff_listener);
/* 303 */     SO_SNDBUF.addChangeListener(buff_listener);
/*     */     
/*     */ 
/* 306 */     Label ltos = new Label(gSocket, 0);
/* 307 */     Messages.setLanguageText(ltos, "ConfigView.section.connection.advanced.IPDiffServ");
/* 308 */     final StringParameter IPDiffServ = new StringParameter(gSocket, "network.tcp.socket.IPDiffServ");
/* 309 */     gridData = new GridData();
/* 310 */     gridData.widthHint = 100;
/* 311 */     gridData.horizontalSpan = 2;
/* 312 */     IPDiffServ.setLayoutData(gridData);
/*     */     
/*     */ 
/*     */ 
/* 316 */     IPDiffServ.addChangeListener(new ParameterChangeAdapter()
/*     */     {
/* 318 */       final Color obg = IPDiffServ.getControl().getBackground();
/*     */       
/* 320 */       final Color ofg = IPDiffServ.getControl().getForeground();
/*     */       
/*     */       public void parameterChanged(Parameter p, boolean caused_internally) {
/* 323 */         String raw = IPDiffServ.getValue();
/* 324 */         int value = -1;
/*     */         try
/*     */         {
/* 327 */           value = Integer.decode(raw).intValue();
/*     */         }
/*     */         catch (Throwable t) {}
/*     */         
/* 331 */         if ((value < 0) || (value > 255)) {
/* 332 */           ConfigurationManager.getInstance().removeParameter("network.tcp.socket.IPDiffServ");
/*     */           
/* 334 */           if ((raw != null) && (raw.length() > 0)) {
/* 335 */             IPDiffServ.getControl().setBackground(Colors.red);
/* 336 */             IPDiffServ.getControl().setForeground(Colors.white);
/*     */           } else {
/* 338 */             IPDiffServ.getControl().setBackground(this.obg);
/* 339 */             IPDiffServ.getControl().setForeground(this.ofg);
/*     */           }
/*     */           
/* 342 */           ConfigSectionConnectionAdvanced.this.enableTOSRegistrySetting(false);
/*     */         } else {
/* 344 */           IPDiffServ.getControl().setBackground(this.obg);
/* 345 */           IPDiffServ.getControl().setForeground(this.ofg);
/*     */           
/* 347 */           ConfigSectionConnectionAdvanced.this.enableTOSRegistrySetting(true);
/*     */         }
/*     */         
/*     */       }
/*     */       
/*     */ 
/* 353 */     });
/* 354 */     Label lreadsel = new Label(gSocket, 0);
/* 355 */     Messages.setLanguageText(lreadsel, "ConfigView.section.connection.advanced.read_select", new String[] { String.valueOf(COConfigurationManager.getDefault("network.tcp.read.select.time")) });
/* 356 */     IntParameter read_select = new IntParameter(gSocket, "network.tcp.read.select.time", 10, 250);
/* 357 */     gridData = new GridData();
/* 358 */     gridData.horizontalSpan = 2;
/* 359 */     read_select.setLayoutData(gridData);
/*     */     
/* 361 */     Label lreadselmin = new Label(gSocket, 0);
/* 362 */     Messages.setLanguageText(lreadselmin, "ConfigView.section.connection.advanced.read_select_min", new String[] { String.valueOf(COConfigurationManager.getDefault("network.tcp.read.select.min.time")) });
/* 363 */     IntParameter read_select_min = new IntParameter(gSocket, "network.tcp.read.select.min.time", 0, 100);
/* 364 */     gridData = new GridData();
/* 365 */     gridData.horizontalSpan = 2;
/* 366 */     read_select_min.setLayoutData(gridData);
/*     */     
/*     */ 
/*     */ 
/* 370 */     Label lwritesel = new Label(gSocket, 0);
/* 371 */     Messages.setLanguageText(lwritesel, "ConfigView.section.connection.advanced.write_select", new String[] { String.valueOf(COConfigurationManager.getDefault("network.tcp.write.select.time")) });
/* 372 */     IntParameter write_select = new IntParameter(gSocket, "network.tcp.write.select.time", 10, 250);
/* 373 */     gridData = new GridData();
/* 374 */     gridData.horizontalSpan = 2;
/* 375 */     write_select.setLayoutData(gridData);
/*     */     
/* 377 */     Label lwriteselmin = new Label(gSocket, 0);
/* 378 */     Messages.setLanguageText(lwriteselmin, "ConfigView.section.connection.advanced.write_select_min", new String[] { String.valueOf(COConfigurationManager.getDefault("network.tcp.write.select.min.time")) });
/* 379 */     IntParameter write_select_min = new IntParameter(gSocket, "network.tcp.write.select.min.time", 0, 100);
/* 380 */     gridData = new GridData();
/* 381 */     gridData.horizontalSpan = 2;
/* 382 */     write_select_min.setLayoutData(gridData);
/*     */     
/* 384 */     new BooleanParameter(cSection, "IPV6 Enable Support", "network.ipv6.enable.support");
/*     */     
/* 386 */     new BooleanParameter(cSection, "IPV6 Prefer Addresses", "network.ipv6.prefer.addresses");
/*     */     
/* 388 */     if ((Constants.isWindowsVistaOrHigher) && (Constants.isJava7OrHigher))
/*     */     {
/* 390 */       new BooleanParameter(cSection, "IPV4 Prefer Stack", "network.ipv4.prefer.stack");
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 395 */     return cSection;
/*     */   }
/*     */   
/*     */   private void enableTOSRegistrySetting(boolean enable)
/*     */   {
/* 400 */     PlatformManager mgr = PlatformManagerFactory.getPlatformManager();
/*     */     
/* 402 */     if (mgr.hasCapability(PlatformManagerCapabilities.SetTCPTOSEnabled)) {
/*     */       try
/*     */       {
/* 405 */         mgr.setTCPTOSEnabled(enable);
/*     */       } catch (PlatformManagerException pe) {
/* 407 */         Debug.printStackTrace(pe);
/*     */       }
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/configsections/ConfigSectionConnectionAdvanced.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */