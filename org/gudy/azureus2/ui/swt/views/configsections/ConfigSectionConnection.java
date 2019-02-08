/*     */ package org.gudy.azureus2.ui.swt.views.configsections;
/*     */ 
/*     */ import org.eclipse.swt.events.MouseAdapter;
/*     */ import org.eclipse.swt.events.MouseEvent;
/*     */ import org.eclipse.swt.layout.GridData;
/*     */ import org.eclipse.swt.layout.GridLayout;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.Display;
/*     */ import org.eclipse.swt.widgets.Group;
/*     */ import org.eclipse.swt.widgets.Label;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.core3.peer.PEPeerSource;
/*     */ import org.gudy.azureus2.core3.util.AENetworkClassifier;
/*     */ import org.gudy.azureus2.core3.util.Constants;
/*     */ import org.gudy.azureus2.ui.swt.Messages;
/*     */ import org.gudy.azureus2.ui.swt.Utils;
/*     */ import org.gudy.azureus2.ui.swt.components.LinkLabel;
/*     */ import org.gudy.azureus2.ui.swt.config.BooleanParameter;
/*     */ import org.gudy.azureus2.ui.swt.config.ChangeSelectionActionPerformer;
/*     */ import org.gudy.azureus2.ui.swt.config.IntParameter;
/*     */ import org.gudy.azureus2.ui.swt.config.Parameter;
/*     */ import org.gudy.azureus2.ui.swt.config.ParameterChangeAdapter;
/*     */ import org.gudy.azureus2.ui.swt.config.StringParameter;
/*     */ import org.gudy.azureus2.ui.swt.mainwindow.ClipboardCopy;
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
/*     */ 
/*     */ 
/*     */ public class ConfigSectionConnection
/*     */   implements UISWTConfigSection
/*     */ {
/*     */   private static final String CFG_PREFIX = "ConfigView.section.connection.";
/*     */   
/*     */   public String configSectionGetParentSection()
/*     */   {
/*  51 */     return "root";
/*     */   }
/*     */   
/*     */   public String configSectionGetName() {
/*  55 */     return "server";
/*     */   }
/*     */   
/*     */ 
/*     */   public void configSectionSave() {}
/*     */   
/*     */   public void configSectionDelete() {}
/*     */   
/*     */   public int maxUserMode()
/*     */   {
/*  65 */     return 2;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Composite configSectionCreate(Composite parent)
/*     */   {
/*  75 */     int userMode = COConfigurationManager.getIntParameter("User Mode");
/*     */     
/*  77 */     Composite cSection = new Composite(parent, 0);
/*  78 */     GridData gridData = new GridData(272);
/*     */     
/*  80 */     Utils.setLayoutData(cSection, gridData);
/*  81 */     GridLayout layout = new GridLayout();
/*  82 */     cSection.setLayout(layout);
/*     */     
/*     */ 
/*     */ 
/*  86 */     Composite cMiniArea = new Composite(cSection, 0);
/*  87 */     layout = new GridLayout();
/*  88 */     layout.numColumns = 2;
/*  89 */     layout.marginHeight = 0;
/*  90 */     layout.marginWidth = 0;
/*  91 */     cMiniArea.setLayout(layout);
/*  92 */     gridData = new GridData(768);
/*  93 */     Utils.setLayoutData(cMiniArea, gridData);
/*     */     
/*  95 */     final boolean separate_ports = (userMode > 1) || (COConfigurationManager.getIntParameter("TCP.Listen.Port") != COConfigurationManager.getIntParameter("UDP.Listen.Port"));
/*     */     
/*  97 */     Label label = new Label(cMiniArea, 0);
/*  98 */     Messages.setLanguageText(label, separate_ports ? "ConfigView.label.tcplistenport" : "ConfigView.label.serverport");
/*  99 */     gridData = new GridData(128);
/* 100 */     Utils.setLayoutData(label, gridData);
/*     */     
/* 102 */     final IntParameter tcplisten = new IntParameter(cMiniArea, "TCP.Listen.Port", 1, 65535);
/*     */     
/* 104 */     gridData = new GridData();
/* 105 */     tcplisten.setLayoutData(gridData);
/*     */     
/* 107 */     tcplisten.addChangeListener(new ParameterChangeAdapter() {
/*     */       public void intParameterChanging(Parameter p, int toValue) {
/* 109 */         if (toValue == Constants.INSTANCE_PORT) {
/* 110 */           toValue = Constants.INSTANCE_PORT + 1;
/* 111 */           tcplisten.setValue(toValue);
/*     */         }
/*     */         
/* 114 */         if (!separate_ports) {
/* 115 */           COConfigurationManager.setParameter("UDP.Listen.Port", toValue);
/* 116 */           COConfigurationManager.setParameter("UDP.NonData.Listen.Port", toValue);
/*     */         }
/*     */       }
/*     */     });
/*     */     
/*     */ 
/* 122 */     if (separate_ports)
/*     */     {
/* 124 */       label = new Label(cMiniArea, 0);
/* 125 */       Messages.setLanguageText(label, "ConfigView.label.udplistenport");
/* 126 */       gridData = new GridData(128);
/* 127 */       Utils.setLayoutData(label, gridData);
/*     */       
/* 129 */       final IntParameter udp_listen = new IntParameter(cMiniArea, "UDP.Listen.Port", 1, 65535);
/*     */       
/* 131 */       gridData = new GridData();
/* 132 */       udp_listen.setLayoutData(gridData);
/*     */       
/* 134 */       final boolean MULTI_UDP = false;
/*     */       
/* 136 */       udp_listen.addChangeListener(new ParameterChangeAdapter() {
/*     */         public void intParameterChanging(Parameter p, int toValue) {
/* 138 */           if (toValue == Constants.INSTANCE_PORT) {
/* 139 */             toValue = Constants.INSTANCE_PORT + 1;
/* 140 */             udp_listen.setValue(toValue);
/*     */           }
/*     */           
/* 143 */           if (!MULTI_UDP) {
/* 144 */             COConfigurationManager.setParameter("UDP.NonData.Listen.Port", toValue);
/*     */           }
/*     */         }
/*     */       });
/*     */       
/*     */ 
/* 150 */       if (MULTI_UDP)
/*     */       {
/* 152 */         Composite cNonDataUDPArea = new Composite(cSection, 0);
/* 153 */         layout = new GridLayout();
/* 154 */         layout.numColumns = 2;
/* 155 */         layout.marginHeight = 0;
/* 156 */         layout.marginWidth = 0;
/* 157 */         cNonDataUDPArea.setLayout(layout);
/* 158 */         gridData = new GridData(768);
/* 159 */         Utils.setLayoutData(cNonDataUDPArea, gridData);
/*     */         
/* 161 */         final BooleanParameter commonUDP = new BooleanParameter(cNonDataUDPArea, "UDP.NonData.Listen.Port.Same", "ConfigView.section.connection.nondata.udp.same");
/*     */         
/* 163 */         gridData = new GridData();
/* 164 */         gridData.horizontalIndent = 16;
/* 165 */         commonUDP.setLayoutData(gridData);
/*     */         
/* 167 */         final IntParameter non_data_udp_listen = new IntParameter(cNonDataUDPArea, "UDP.NonData.Listen.Port");
/*     */         
/*     */ 
/* 170 */         non_data_udp_listen.addChangeListener(new ParameterChangeAdapter()
/*     */         {
/*     */           public void intParameterChanging(Parameter p, int toValue)
/*     */           {
/* 174 */             if (toValue == Constants.INSTANCE_PORT) {
/* 175 */               toValue = Constants.INSTANCE_PORT + 1;
/* 176 */               non_data_udp_listen.setValue(toValue);
/*     */             }
/*     */             
/*     */           }
/* 180 */         });
/* 181 */         udp_listen.addChangeListener(new ParameterChangeAdapter()
/*     */         {
/*     */ 
/*     */           public void parameterChanged(Parameter p, boolean caused_internally)
/*     */           {
/* 186 */             if (commonUDP.isSelected().booleanValue())
/*     */             {
/* 188 */               int udp_listen_port = udp_listen.getValue();
/*     */               
/* 190 */               if (udp_listen_port != Constants.INSTANCE_PORT)
/*     */               {
/* 192 */                 COConfigurationManager.setParameter("UDP.NonData.Listen.Port", udp_listen_port);
/*     */                 
/* 194 */                 non_data_udp_listen.setValue(udp_listen_port);
/*     */               }
/*     */               
/*     */             }
/*     */           }
/* 199 */         });
/* 200 */         gridData = new GridData();
/* 201 */         non_data_udp_listen.setLayoutData(gridData);
/*     */         
/* 203 */         commonUDP.setAdditionalActionPerformer(new ChangeSelectionActionPerformer(non_data_udp_listen.getControls(), true));
/*     */         
/* 205 */         commonUDP.addChangeListener(new ParameterChangeAdapter()
/*     */         {
/*     */ 
/*     */ 
/*     */           public void parameterChanged(Parameter p, boolean caused_internally)
/*     */           {
/*     */ 
/*     */ 
/* 213 */             if (commonUDP.isSelected().booleanValue())
/*     */             {
/* 215 */               int udp_listen_port = COConfigurationManager.getIntParameter("UDP.Listen.Port");
/*     */               
/* 217 */               if (COConfigurationManager.getIntParameter("UDP.NonData.Listen.Port") != udp_listen_port)
/*     */               {
/* 219 */                 COConfigurationManager.setParameter("UDP.NonData.Listen.Port", udp_listen_port);
/*     */                 
/* 221 */                 non_data_udp_listen.setValue(udp_listen_port);
/*     */               }
/*     */               
/*     */             }
/*     */           }
/* 226 */         });
/* 227 */         BooleanParameter enable_tcp = new BooleanParameter(cNonDataUDPArea, "TCP.Listen.Port.Enable", "ConfigView.section.connection.tcp.enable");
/*     */         
/* 229 */         gridData = new GridData();
/* 230 */         enable_tcp.setLayoutData(gridData);
/* 231 */         label = new Label(cNonDataUDPArea, 0);
/*     */         
/* 233 */         BooleanParameter enable_udp = new BooleanParameter(cNonDataUDPArea, "UDP.Listen.Port.Enable", "ConfigView.section.connection.udp.enable");
/*     */         
/* 235 */         gridData = new GridData();
/* 236 */         enable_udp.setLayoutData(gridData);
/* 237 */         label = new Label(cNonDataUDPArea, 0);
/*     */         
/* 239 */         enable_tcp.setAdditionalActionPerformer(new ChangeSelectionActionPerformer(tcplisten));
/*     */         
/*     */ 
/* 242 */         enable_udp.setAdditionalActionPerformer(new ChangeSelectionActionPerformer(udp_listen));
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 247 */     if (userMode > 0)
/*     */     {
/* 249 */       Composite cRandPortArea = new Composite(cMiniArea, 0);
/* 250 */       layout = new GridLayout();
/* 251 */       layout.numColumns = 4;
/* 252 */       layout.marginHeight = 0;
/* 253 */       layout.marginWidth = 0;
/* 254 */       cRandPortArea.setLayout(layout);
/* 255 */       gridData = new GridData(768);
/* 256 */       gridData.horizontalSpan = 2;
/*     */       
/* 258 */       Utils.setLayoutData(cRandPortArea, gridData);
/*     */       
/* 260 */       BooleanParameter rand_enable = new BooleanParameter(cRandPortArea, "Listen.Port.Randomize.Enable", "ConfigView.section.connection.port.rand.enable");
/*     */       
/*     */ 
/* 263 */       label = new Label(cRandPortArea, 0);
/*     */       
/* 265 */       label.setText(MessageText.getString("ConfigView.section.connection.port.rand.range"));
/* 266 */       gridData = new GridData();
/* 267 */       gridData.horizontalIndent = 20;
/* 268 */       Utils.setLayoutData(label, gridData);
/*     */       
/* 270 */       StringParameter rand_range = new StringParameter(cRandPortArea, "Listen.Port.Randomize.Range");
/* 271 */       gridData = new GridData();
/* 272 */       gridData.widthHint = 100;
/* 273 */       rand_range.setLayoutData(gridData);
/*     */       
/* 275 */       BooleanParameter rand_together = new BooleanParameter(cRandPortArea, "Listen.Port.Randomize.Together", "ConfigView.section.connection.port.rand.together");
/*     */       
/*     */ 
/* 278 */       rand_enable.setAdditionalActionPerformer(new ChangeSelectionActionPerformer(label));
/*     */       
/* 280 */       rand_enable.setAdditionalActionPerformer(new ChangeSelectionActionPerformer(new Parameter[] { rand_range, rand_together }));
/*     */     }
/*     */     
/*     */ 
/* 284 */     if (userMode > 1)
/*     */     {
/* 286 */       BooleanParameter prefer_udp = new BooleanParameter(cMiniArea, "peercontrol.prefer.udp", "ConfigView.section.connection.prefer.udp");
/*     */       
/* 288 */       gridData = new GridData();
/* 289 */       gridData.horizontalSpan = 2;
/* 290 */       prefer_udp.setLayoutData(gridData);
/*     */     }
/*     */     
/* 293 */     if (userMode < 2)
/*     */     {
/* 295 */       label = new Label(cSection, 0);
/* 296 */       gridData = new GridData();
/* 297 */       Utils.setLayoutData(label, gridData);
/* 298 */       label.setText(MessageText.getString("Utils.link.visit") + ":");
/*     */       
/* 300 */       Label linkLabel = new Label(cSection, 0);
/* 301 */       linkLabel.setText(MessageText.getString("ConfigView.section.connection.serverport.wiki"));
/*     */       
/* 303 */       linkLabel.setData("http://wiki.vuze.com/w/Why_ports_like_6881_are_no_good_choice");
/*     */       
/* 305 */       linkLabel.setCursor(linkLabel.getDisplay().getSystemCursor(21));
/* 306 */       linkLabel.setForeground(Colors.blue);
/* 307 */       gridData = new GridData();
/* 308 */       Utils.setLayoutData(linkLabel, gridData);
/* 309 */       linkLabel.addMouseListener(new MouseAdapter() {
/*     */         public void mouseDoubleClick(MouseEvent arg0) {
/* 311 */           Utils.launch((String)((Label)arg0.widget).getData());
/*     */         }
/*     */         
/*     */         public void mouseDown(MouseEvent arg0) {
/* 315 */           Utils.launch((String)((Label)arg0.widget).getData());
/*     */         }
/* 317 */       });
/* 318 */       ClipboardCopy.addCopyToClipMenu(linkLabel);
/*     */     }
/*     */     
/* 321 */     if (userMode > 0)
/*     */     {
/*     */ 
/* 324 */       Group http_group = new Group(cSection, 0);
/*     */       
/* 326 */       Messages.setLanguageText(http_group, "ConfigView.section.connection.group.http");
/*     */       
/* 328 */       GridLayout http_layout = new GridLayout();
/*     */       
/* 330 */       http_layout.numColumns = 2;
/*     */       
/* 332 */       http_group.setLayout(http_layout);
/*     */       
/* 334 */       gridData = new GridData(768);
/* 335 */       http_group.setLayoutData(gridData);
/*     */       
/* 337 */       label = new Label(http_group, 64);
/* 338 */       Messages.setLanguageText(label, "ConfigView.section.connection.group.http.info");
/*     */       
/* 340 */       new LinkLabel(http_group, "ConfigView.label.please.visit.here", "http://wiki.vuze.com/w/HTTP_Seeding");
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 345 */       BooleanParameter enable_http = new BooleanParameter(http_group, "HTTP.Data.Listen.Port.Enable", "ConfigView.section.connection.http.enable");
/*     */       
/* 347 */       gridData = new GridData();
/* 348 */       gridData.horizontalSpan = 2;
/* 349 */       enable_http.setLayoutData(gridData);
/*     */       
/* 351 */       label = new Label(http_group, 0);
/* 352 */       Messages.setLanguageText(label, "ConfigView.section.connection.http.port");
/*     */       
/* 354 */       IntParameter http_port = new IntParameter(http_group, "HTTP.Data.Listen.Port");
/*     */       
/* 356 */       gridData = new GridData();
/* 357 */       http_port.setLayoutData(gridData);
/*     */       
/* 359 */       label = new Label(http_group, 0);
/* 360 */       Messages.setLanguageText(label, "ConfigView.section.connection.http.portoverride");
/*     */       
/* 362 */       IntParameter http_port_override = new IntParameter(http_group, "HTTP.Data.Listen.Port.Override");
/*     */       
/* 364 */       gridData = new GridData();
/* 365 */       http_port_override.setLayoutData(gridData);
/*     */       
/* 367 */       enable_http.setAdditionalActionPerformer(new ChangeSelectionActionPerformer(http_port));
/* 368 */       enable_http.setAdditionalActionPerformer(new ChangeSelectionActionPerformer(http_port_override));
/*     */     }
/*     */     
/* 371 */     if (userMode > 0)
/*     */     {
/*     */ 
/*     */ 
/* 375 */       Group ws_group = new Group(cSection, 0);
/*     */       
/* 377 */       Messages.setLanguageText(ws_group, "ConfigView.section.connection.group.webseed");
/*     */       
/* 379 */       GridLayout ws_layout = new GridLayout();
/*     */       
/* 381 */       ws_layout.numColumns = 2;
/*     */       
/* 383 */       ws_group.setLayout(ws_layout);
/*     */       
/* 385 */       gridData = new GridData(768);
/* 386 */       Utils.setLayoutData(ws_group, gridData);
/*     */       
/* 388 */       new BooleanParameter(ws_group, "webseed.activation.uses.availability", "ConfigView.section.connection.webseed.act.on.avail");
/*     */     }
/*     */     
/* 391 */     if (userMode > 0)
/*     */     {
/*     */ 
/* 394 */       Group peer_sources_group = new Group(cSection, 0);
/* 395 */       Messages.setLanguageText(peer_sources_group, "ConfigView.section.connection.group.peersources");
/*     */       
/* 397 */       GridLayout peer_sources_layout = new GridLayout();
/* 398 */       peer_sources_group.setLayout(peer_sources_layout);
/*     */       
/* 400 */       gridData = new GridData(768);
/* 401 */       Utils.setLayoutData(peer_sources_group, gridData);
/*     */       
/* 403 */       label = new Label(peer_sources_group, 64);
/* 404 */       Messages.setLanguageText(label, "ConfigView.section.connection.group.peersources.info");
/*     */       
/* 406 */       gridData = new GridData();
/* 407 */       Utils.setLayoutData(label, gridData);
/*     */       
/* 409 */       for (int i = 0; i < PEPeerSource.PS_SOURCES.length; i++)
/*     */       {
/* 411 */         String p = PEPeerSource.PS_SOURCES[i];
/*     */         
/* 413 */         String config_name = "Peer Source Selection Default." + p;
/* 414 */         String msg_text = "ConfigView.section.connection.peersource." + p;
/*     */         
/* 416 */         BooleanParameter peer_source = new BooleanParameter(peer_sources_group, config_name, msg_text);
/*     */         
/*     */ 
/* 419 */         gridData = new GridData();
/* 420 */         peer_source.setLayoutData(gridData);
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 426 */       if (userMode > 1)
/*     */       {
/*     */ 
/*     */ 
/* 430 */         Group networks_group = new Group(cSection, 0);
/* 431 */         Messages.setLanguageText(networks_group, "ConfigView.section.connection.group.networks");
/*     */         
/* 433 */         GridLayout networks_layout = new GridLayout();
/* 434 */         networks_group.setLayout(networks_layout);
/*     */         
/* 436 */         gridData = new GridData(768);
/* 437 */         Utils.setLayoutData(networks_group, gridData);
/*     */         
/* 439 */         label = new Label(networks_group, 0);
/* 440 */         Messages.setLanguageText(label, "ConfigView.section.connection.group.networks.info");
/*     */         
/* 442 */         gridData = new GridData();
/* 443 */         Utils.setLayoutData(label, gridData);
/*     */         
/* 445 */         for (int i = 0; i < AENetworkClassifier.AT_NETWORKS.length; i++)
/*     */         {
/* 447 */           String nn = AENetworkClassifier.AT_NETWORKS[i];
/*     */           
/* 449 */           String config_name = "Network Selection Default." + nn;
/* 450 */           String msg_text = "ConfigView.section.connection.networks." + nn;
/*     */           
/* 452 */           BooleanParameter network = new BooleanParameter(networks_group, config_name, msg_text);
/*     */           
/*     */ 
/* 455 */           gridData = new GridData();
/* 456 */           network.setLayoutData(gridData);
/*     */         }
/*     */         
/* 459 */         label = new Label(networks_group, 0);
/* 460 */         gridData = new GridData();
/* 461 */         Utils.setLayoutData(label, gridData);
/*     */         
/* 463 */         BooleanParameter network_prompt = new BooleanParameter(networks_group, "Network Selection Prompt", "ConfigView.section.connection.networks.prompt");
/*     */         
/*     */ 
/*     */ 
/* 467 */         gridData = new GridData();
/* 468 */         network_prompt.setLayoutData(gridData);
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 475 */     return cSection;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/configsections/ConfigSectionConnection.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */