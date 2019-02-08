/*     */ package org.gudy.azureus2.ui.swt.views.configsections;
/*     */ 
/*     */ import org.eclipse.swt.layout.GridData;
/*     */ import org.eclipse.swt.layout.GridLayout;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.Group;
/*     */ import org.eclipse.swt.widgets.Label;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.ui.swt.Messages;
/*     */ import org.gudy.azureus2.ui.swt.Utils;
/*     */ import org.gudy.azureus2.ui.swt.config.BooleanParameter;
/*     */ import org.gudy.azureus2.ui.swt.config.ChangeSelectionActionPerformer;
/*     */ import org.gudy.azureus2.ui.swt.config.IntParameter;
/*     */ import org.gudy.azureus2.ui.swt.config.Parameter;
/*     */ import org.gudy.azureus2.ui.swt.config.ParameterChangeAdapter;
/*     */ import org.gudy.azureus2.ui.swt.config.StringParameter;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ConfigSectionTrackerClient
/*     */   implements UISWTConfigSection
/*     */ {
/*     */   public String configSectionGetParentSection()
/*     */   {
/*  44 */     return "tracker";
/*     */   }
/*     */   
/*     */   public String configSectionGetName() {
/*  48 */     return "tracker.client";
/*     */   }
/*     */   
/*     */ 
/*     */   public void configSectionSave() {}
/*     */   
/*     */   public void configSectionDelete() {}
/*     */   
/*     */   public int maxUserMode()
/*     */   {
/*  58 */     return 2;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public Composite configSectionCreate(Composite parent)
/*     */   {
/*  66 */     int userMode = COConfigurationManager.getIntParameter("User Mode");
/*     */     
/*     */ 
/*  69 */     Composite gMainTab = new Composite(parent, 0);
/*  70 */     GridData gridData = new GridData(272);
/*  71 */     gMainTab.setLayoutData(gridData);
/*  72 */     GridLayout layout = new GridLayout();
/*  73 */     layout.numColumns = 3;
/*  74 */     gMainTab.setLayout(layout);
/*     */     
/*     */ 
/*     */ 
/*  78 */     Group scrapeGroup = new Group(gMainTab, 0);
/*  79 */     Messages.setLanguageText(scrapeGroup, "ConfigView.group.scrape");
/*  80 */     GridLayout gridLayout = new GridLayout();
/*  81 */     gridLayout.numColumns = 1;
/*  82 */     scrapeGroup.setLayout(gridLayout);
/*     */     
/*  84 */     gridData = new GridData(768);
/*  85 */     gridData.horizontalSpan = 3;
/*  86 */     scrapeGroup.setLayoutData(gridData);
/*     */     
/*  88 */     Label label = new Label(scrapeGroup, 64);
/*  89 */     label.setLayoutData(Utils.getWrappableLabelGridData(1, 768));
/*  90 */     Messages.setLanguageText(label, "ConfigView.section.tracker.client.scrapeinfo");
/*     */     
/*  92 */     BooleanParameter scrape = new BooleanParameter(scrapeGroup, "Tracker Client Scrape Enable", "ConfigView.section.tracker.client.scrapeenable");
/*     */     
/*     */ 
/*     */ 
/*  96 */     BooleanParameter scrape_stopped = new BooleanParameter(scrapeGroup, "Tracker Client Scrape Stopped Enable", "ConfigView.section.tracker.client.scrapestoppedenable");
/*     */     
/*     */ 
/*     */ 
/* 100 */     scrape.setAdditionalActionPerformer(new ChangeSelectionActionPerformer(scrape_stopped.getControls()));
/*     */     
/* 102 */     new BooleanParameter(scrapeGroup, "Tracker Client Scrape Single Only", "ConfigView.section.tracker.client.scrapesingleonly");
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 107 */     Group infoGroup = new Group(gMainTab, 0);
/* 108 */     Messages.setLanguageText(infoGroup, "label.information");
/* 109 */     gridLayout = new GridLayout();
/* 110 */     gridLayout.numColumns = 2;
/* 111 */     infoGroup.setLayout(gridLayout);
/* 112 */     gridData = new GridData(768);
/* 113 */     gridData.horizontalSpan = 3;
/* 114 */     infoGroup.setLayoutData(gridData);
/*     */     
/*     */ 
/*     */ 
/* 118 */     gridData = new GridData();
/* 119 */     gridData.horizontalSpan = 2;
/*     */     
/* 121 */     new BooleanParameter(infoGroup, "Tracker Client Send OS and Java Version", "ConfigView.section.tracker.sendjavaversionandos").setLayoutData(gridData);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 126 */     BooleanParameter showWarnings = new BooleanParameter(infoGroup, "Tracker Client Show Warnings", "ConfigView.section.tracker.client.showwarnings");
/* 127 */     gridData = new GridData();
/* 128 */     gridData.horizontalSpan = 2;
/* 129 */     showWarnings.setLayoutData(gridData);
/*     */     
/*     */ 
/*     */ 
/* 133 */     BooleanParameter excludeLAN = new BooleanParameter(infoGroup, "Tracker Client Exclude LAN", "ConfigView.section.tracker.client.exclude_lan");
/* 134 */     gridData = new GridData();
/* 135 */     gridData.horizontalSpan = 2;
/* 136 */     excludeLAN.setLayoutData(gridData);
/*     */     
/*     */ 
/*     */ 
/* 140 */     Group protocolGroup = new Group(gMainTab, 0);
/* 141 */     Messages.setLanguageText(protocolGroup, "label.protocol");
/* 142 */     gridLayout = new GridLayout();
/* 143 */     gridLayout.numColumns = 2;
/* 144 */     protocolGroup.setLayout(gridLayout);
/* 145 */     gridData = new GridData(768);
/* 146 */     gridData.horizontalSpan = 3;
/* 147 */     protocolGroup.setLayoutData(gridData);
/*     */     
/*     */ 
/*     */ 
/* 151 */     BooleanParameter enableTCP = new BooleanParameter(protocolGroup, "Tracker Client Enable TCP", "ConfigView.section.tracker.client.enabletcp");
/* 152 */     gridData = new GridData();
/* 153 */     gridData.horizontalSpan = 2;
/* 154 */     enableTCP.setLayoutData(gridData);
/*     */     
/*     */ 
/*     */ 
/* 158 */     BooleanParameter enableUDP = new BooleanParameter(protocolGroup, "Server Enable UDP", "ConfigView.section.server.enableudp");
/* 159 */     gridData = new GridData();
/* 160 */     gridData.horizontalSpan = 2;
/* 161 */     enableUDP.setLayoutData(gridData);
/*     */     
/*     */ 
/*     */ 
/* 165 */     BooleanParameter enableUDPProbe = new BooleanParameter(protocolGroup, "Tracker UDP Probe Enable", "ConfigView.section.server.enableudpprobe");
/* 166 */     gridData = new GridData();
/* 167 */     gridData.horizontalSpan = 2;
/* 168 */     enableUDPProbe.setLayoutData(gridData);
/*     */     
/* 170 */     enableUDP.setAdditionalActionPerformer(new ChangeSelectionActionPerformer(enableUDPProbe.getControls()));
/*     */     BooleanParameter enableDNS;
/* 172 */     if (userMode > 1)
/*     */     {
/* 174 */       enableDNS = new BooleanParameter(protocolGroup, "Tracker DNS Records Enable", "ConfigView.section.server.enablednsrecords");
/*     */     }
/*     */     
/*     */ 
/* 178 */     if (userMode > 0)
/*     */     {
/*     */ 
/*     */ 
/* 182 */       Group overrideGroup = new Group(gMainTab, 0);
/* 183 */       Messages.setLanguageText(overrideGroup, "ConfigView.group.override");
/* 184 */       gridLayout = new GridLayout();
/* 185 */       gridLayout.numColumns = 2;
/* 186 */       overrideGroup.setLayout(gridLayout);
/*     */       
/* 188 */       gridData = new GridData(768);
/* 189 */       gridData.horizontalSpan = 3;
/* 190 */       overrideGroup.setLayoutData(gridData);
/*     */       
/*     */ 
/* 193 */       label = new Label(overrideGroup, 64);
/* 194 */       label.setLayoutData(Utils.getWrappableLabelGridData(1, 768));
/* 195 */       Messages.setLanguageText(label, "ConfigView.label.overrideip");
/*     */       
/* 197 */       StringParameter overrideip = new StringParameter(overrideGroup, "Override Ip", "");
/* 198 */       GridData data = new GridData(768);
/* 199 */       data.widthHint = 100;
/* 200 */       overrideip.setLayoutData(data);
/*     */       
/* 202 */       label = new Label(overrideGroup, 64);
/* 203 */       label.setLayoutData(Utils.getWrappableLabelGridData(1, 768));
/* 204 */       Messages.setLanguageText(label, "ConfigView.label.announceport");
/*     */       
/* 206 */       StringParameter tcpOverride = new StringParameter(overrideGroup, "TCP.Listen.Port.Override");
/* 207 */       data = new GridData();
/* 208 */       data.widthHint = 50;
/* 209 */       tcpOverride.setLayoutData(data);
/*     */       
/* 211 */       tcpOverride.addChangeListener(new ParameterChangeAdapter()
/*     */       {
/*     */         public void stringParameterChanging(Parameter p, String toValue) {
/* 214 */           if (toValue.length() == 0) {
/* 215 */             return;
/*     */           }
/*     */           
/*     */           try
/*     */           {
/* 220 */             int portVal = Integer.parseInt(toValue);
/* 221 */             if ((portVal >= 0) && (portVal <= 65535))
/* 222 */               return;
/*     */           } catch (NumberFormatException e) {}
/* 224 */           p.setValue("");
/*     */         }
/*     */         
/* 227 */       });
/* 228 */       label = new Label(overrideGroup, 64);
/* 229 */       label.setLayoutData(Utils.getWrappableLabelGridData(1, 768));
/* 230 */       Messages.setLanguageText(label, "ConfigView.label.noportannounce");
/*     */       
/* 232 */       BooleanParameter noPortAnnounce = new BooleanParameter(overrideGroup, "Tracker Client No Port Announce");
/* 233 */       data = new GridData();
/* 234 */       noPortAnnounce.setLayoutData(data);
/*     */       
/* 236 */       label = new Label(overrideGroup, 64);
/* 237 */       label.setLayoutData(Utils.getWrappableLabelGridData(1, 768));
/* 238 */       Messages.setLanguageText(label, "ConfigView.label.maxnumwant");
/*     */       
/* 240 */       IntParameter numwant = new IntParameter(overrideGroup, "Tracker Client Numwant Limit", 0, 100);
/* 241 */       data = new GridData();
/* 242 */       numwant.setLayoutData(data);
/*     */       
/* 244 */       label = new Label(overrideGroup, 64);
/* 245 */       label.setLayoutData(Utils.getWrappableLabelGridData(1, 768));
/* 246 */       Messages.setLanguageText(label, "ConfigView.label.minannounce");
/*     */       
/* 248 */       IntParameter minmininterval = new IntParameter(overrideGroup, "Tracker Client Min Announce Interval");
/* 249 */       data = new GridData();
/* 250 */       minmininterval.setLayoutData(data);
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 255 */       if (userMode > 1)
/*     */       {
/*     */ 
/*     */ 
/* 259 */         label = new Label(gMainTab, 0);
/* 260 */         Messages.setLanguageText(label, "ConfigView.section.tracker.client.connecttimeout");
/* 261 */         gridData = new GridData();
/* 262 */         IntParameter connect_timeout = new IntParameter(gMainTab, "Tracker Client Connect Timeout");
/* 263 */         connect_timeout.setLayoutData(gridData);
/* 264 */         label = new Label(gMainTab, 0);
/*     */         
/*     */ 
/*     */ 
/* 268 */         label = new Label(gMainTab, 0);
/* 269 */         Messages.setLanguageText(label, "ConfigView.section.tracker.client.readtimeout");
/* 270 */         gridData = new GridData();
/* 271 */         IntParameter read_timeout = new IntParameter(gMainTab, "Tracker Client Read Timeout");
/* 272 */         read_timeout.setLayoutData(gridData);
/* 273 */         label = new Label(gMainTab, 0);
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 279 */         gridData = new GridData();
/* 280 */         gridData.horizontalSpan = 2;
/*     */         
/* 282 */         new BooleanParameter(gMainTab, "Tracker Key Enable Client", "ConfigView.section.tracker.enablekey").setLayoutData(gridData);
/*     */         
/*     */ 
/* 285 */         label = new Label(gMainTab, 0);
/*     */         
/*     */ 
/*     */ 
/*     */ 
/* 290 */         gridData = new GridData();
/* 291 */         gridData.horizontalSpan = 2;
/*     */         
/* 293 */         new BooleanParameter(gMainTab, "Tracker Separate Peer IDs", "ConfigView.section.tracker.separatepeerids").setLayoutData(gridData);
/*     */         
/*     */ 
/* 296 */         label = new Label(gMainTab, 64);
/* 297 */         label.setLayoutData(Utils.getWrappableLabelGridData(1, 768));
/* 298 */         Messages.setLanguageText(label, "ConfigView.section.tracker.separatepeerids.info");
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 304 */     return gMainTab;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/configsections/ConfigSectionTrackerClient.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */