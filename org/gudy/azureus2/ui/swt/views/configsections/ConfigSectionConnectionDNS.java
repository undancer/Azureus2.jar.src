/*     */ package org.gudy.azureus2.ui.swt.views.configsections;
/*     */ 
/*     */ import org.eclipse.swt.layout.GridData;
/*     */ import org.eclipse.swt.layout.GridLayout;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.Label;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.ui.swt.Messages;
/*     */ import org.gudy.azureus2.ui.swt.Utils;
/*     */ import org.gudy.azureus2.ui.swt.components.LinkLabel;
/*     */ import org.gudy.azureus2.ui.swt.config.BooleanParameter;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ConfigSectionConnectionDNS
/*     */   implements UISWTConfigSection
/*     */ {
/*     */   private static final String CFG_PREFIX = "ConfigView.section.dns.";
/*     */   private static final int REQUIRED_MODE = 2;
/*     */   
/*     */   public int maxUserMode()
/*     */   {
/*  47 */     return 2;
/*     */   }
/*     */   
/*     */   public String configSectionGetParentSection()
/*     */   {
/*  52 */     return "server";
/*     */   }
/*     */   
/*     */   public String configSectionGetName() {
/*  56 */     return "DNS";
/*     */   }
/*     */   
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
/*  69 */     Composite cSection = new Composite(parent, 0);
/*     */     
/*  71 */     GridData gridData = new GridData(272);
/*     */     
/*  73 */     Utils.setLayoutData(cSection, gridData);
/*  74 */     GridLayout layout = new GridLayout();
/*  75 */     layout.numColumns = 2;
/*  76 */     cSection.setLayout(layout);
/*     */     
/*  78 */     int userMode = COConfigurationManager.getIntParameter("User Mode");
/*  79 */     if (userMode < 2) {
/*  80 */       Label label = new Label(cSection, 64);
/*  81 */       gridData = new GridData();
/*  82 */       gridData.horizontalSpan = 2;
/*  83 */       Utils.setLayoutData(label, gridData);
/*     */       
/*  85 */       String[] modeKeys = { "ConfigView.section.mode.beginner", "ConfigView.section.mode.intermediate", "ConfigView.section.mode.advanced" };
/*     */       
/*     */       String param1;
/*     */       
/*     */       String param1;
/*  90 */       if (2 < modeKeys.length) {
/*  91 */         param1 = MessageText.getString(modeKeys[2]);
/*     */       } else
/*  93 */         param1 = String.valueOf(2);
/*     */       String param2;
/*  95 */       String param2; if (userMode < modeKeys.length) {
/*  96 */         param2 = MessageText.getString(modeKeys[userMode]);
/*     */       } else {
/*  98 */         param2 = String.valueOf(userMode);
/*     */       }
/* 100 */       label.setText(MessageText.getString("ConfigView.notAvailableForMode", new String[] { param1, param2 }));
/*     */       
/*     */ 
/* 103 */       return cSection;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 108 */     Label label = new Label(cSection, 64);
/* 109 */     Messages.setLanguageText(label, "ConfigView.section.dns.info");
/* 110 */     gridData = new GridData(768);
/* 111 */     gridData.horizontalSpan = 2;
/* 112 */     gridData.widthHint = 200;
/* 113 */     Utils.setLayoutData(label, gridData);
/*     */     
/* 115 */     gridData = new GridData();
/* 116 */     gridData.horizontalSpan = 2;
/* 117 */     new LinkLabel(cSection, gridData, "ConfigView.label.please.visit.here", MessageText.getString("ConfigView.section.dns.url"));
/*     */     
/*     */ 
/* 120 */     Label comment_label = new Label(cSection, 0);
/* 121 */     Messages.setLanguageText(comment_label, "ConfigView.section.dns.alts");
/*     */     
/* 123 */     gridData = new GridData(768);
/* 124 */     StringParameter alt_servers = new StringParameter(cSection, "DNS Alt Servers");
/* 125 */     alt_servers.setLayoutData(gridData);
/*     */     
/* 127 */     BooleanParameter allow_socks = new BooleanParameter(cSection, "DNS Alt Servers SOCKS Enable", "ConfigView.section.dns.allow_socks");
/* 128 */     gridData = new GridData();
/* 129 */     gridData.horizontalSpan = 2;
/* 130 */     allow_socks.setLayoutData(gridData);
/*     */     
/*     */ 
/* 133 */     return cSection;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/configsections/ConfigSectionConnectionDNS.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */