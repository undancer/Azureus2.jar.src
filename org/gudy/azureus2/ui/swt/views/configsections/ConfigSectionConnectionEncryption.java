/*     */ package org.gudy.azureus2.ui.swt.views.configsections;
/*     */ 
/*     */ import org.eclipse.swt.layout.GridData;
/*     */ import org.eclipse.swt.layout.GridLayout;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.Control;
/*     */ import org.eclipse.swt.widgets.Group;
/*     */ import org.eclipse.swt.widgets.Label;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.ui.swt.Messages;
/*     */ import org.gudy.azureus2.ui.swt.Utils;
/*     */ import org.gudy.azureus2.ui.swt.components.LinkLabel;
/*     */ import org.gudy.azureus2.ui.swt.config.BooleanParameter;
/*     */ import org.gudy.azureus2.ui.swt.config.GenericActionPerformer;
/*     */ import org.gudy.azureus2.ui.swt.config.IAdditionalActionPerformer;
/*     */ import org.gudy.azureus2.ui.swt.config.StringListParameter;
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
/*     */ public class ConfigSectionConnectionEncryption
/*     */   implements UISWTConfigSection
/*     */ {
/*     */   private static final String CFG_PREFIX = "ConfigView.section.connection.encryption.";
/*     */   private static final int REQUIRED_MODE = 1;
/*     */   
/*     */   public int maxUserMode()
/*     */   {
/*  44 */     return 1;
/*     */   }
/*     */   
/*     */   public String configSectionGetParentSection()
/*     */   {
/*  49 */     return "server";
/*     */   }
/*     */   
/*     */   public String configSectionGetName() {
/*  53 */     return "connection.encryption";
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
/*  65 */     Composite cSection = new Composite(parent, 0);
/*     */     
/*  67 */     GridData gridData = new GridData(272);
/*  68 */     cSection.setLayoutData(gridData);
/*  69 */     GridLayout advanced_layout = new GridLayout();
/*  70 */     cSection.setLayout(advanced_layout);
/*     */     
/*  72 */     int userMode = COConfigurationManager.getIntParameter("User Mode");
/*  73 */     if (userMode < 1) {
/*  74 */       Label label = new Label(cSection, 64);
/*  75 */       gridData = new GridData();
/*  76 */       label.setLayoutData(gridData);
/*     */       
/*  78 */       String[] modeKeys = { "ConfigView.section.mode.beginner", "ConfigView.section.mode.intermediate", "ConfigView.section.mode.advanced" };
/*     */       
/*     */       String param1;
/*     */       
/*     */       String param1;
/*  83 */       if (1 < modeKeys.length) {
/*  84 */         param1 = MessageText.getString(modeKeys[1]);
/*     */       } else
/*  86 */         param1 = String.valueOf(1);
/*     */       String param2;
/*  88 */       String param2; if (userMode < modeKeys.length) {
/*  89 */         param2 = MessageText.getString(modeKeys[userMode]);
/*     */       } else {
/*  91 */         param2 = String.valueOf(userMode);
/*     */       }
/*  93 */       label.setText(MessageText.getString("ConfigView.notAvailableForMode", new String[] { param1, param2 }));
/*     */       
/*     */ 
/*  96 */       return cSection;
/*     */     }
/*     */     
/*  99 */     Group gCrypto = new Group(cSection, 0);
/* 100 */     Messages.setLanguageText(gCrypto, "ConfigView.section.connection.encryption.encrypt.group");
/* 101 */     gridData = new GridData(768);
/* 102 */     gCrypto.setLayoutData(gridData);
/* 103 */     GridLayout layout = new GridLayout();
/* 104 */     layout.numColumns = 2;
/* 105 */     gCrypto.setLayout(layout);
/*     */     
/* 107 */     Label lcrypto = new Label(gCrypto, 64);
/* 108 */     Messages.setLanguageText(lcrypto, "ConfigView.section.connection.encryption.encrypt.info");
/* 109 */     gridData = new GridData(768);
/* 110 */     gridData.horizontalSpan = 2;
/* 111 */     gridData.widthHint = 200;
/* 112 */     Utils.setLayoutData(lcrypto, gridData);
/*     */     
/* 114 */     gridData = new GridData();
/* 115 */     gridData.horizontalSpan = 2;
/* 116 */     new LinkLabel(gCrypto, gridData, "ConfigView.section.connection.encryption.encrypt.info.link", "http://wiki.vuze.com/w/Avoid_traffic_shaping");
/*     */     
/*     */ 
/*     */ 
/* 120 */     final BooleanParameter require = new BooleanParameter(gCrypto, "network.transport.encrypted.require", "ConfigView.section.connection.encryption.require_encrypted_transport");
/* 121 */     gridData = new GridData();
/* 122 */     gridData.horizontalSpan = 2;
/* 123 */     require.setLayoutData(gridData);
/*     */     
/* 125 */     String[] encryption_types = { "Plain", "RC4" };
/* 126 */     String[] dropLabels = new String[encryption_types.length];
/* 127 */     String[] dropValues = new String[encryption_types.length];
/* 128 */     for (int i = 0; i < encryption_types.length; i++) {
/* 129 */       dropLabels[i] = encryption_types[i];
/* 130 */       dropValues[i] = encryption_types[i];
/*     */     }
/*     */     
/* 133 */     Composite cEncryptLevel = new Composite(gCrypto, 0);
/* 134 */     gridData = new GridData(272);
/* 135 */     gridData.horizontalSpan = 2;
/* 136 */     cEncryptLevel.setLayoutData(gridData);
/* 137 */     layout = new GridLayout();
/* 138 */     layout.numColumns = 2;
/* 139 */     layout.marginWidth = 0;
/* 140 */     layout.marginHeight = 0;
/* 141 */     cEncryptLevel.setLayout(layout);
/*     */     
/* 143 */     Label lmin = new Label(cEncryptLevel, 0);
/* 144 */     Messages.setLanguageText(lmin, "ConfigView.section.connection.encryption.min_encryption_level");
/* 145 */     StringListParameter min_level = new StringListParameter(cEncryptLevel, "network.transport.encrypted.min_level", encryption_types[1], dropLabels, dropValues);
/*     */     
/* 147 */     Label lcryptofb = new Label(gCrypto, 64);
/* 148 */     Messages.setLanguageText(lcryptofb, "ConfigView.section.connection.encryption.encrypt.fallback_info");
/* 149 */     gridData = new GridData(768);
/* 150 */     gridData.horizontalSpan = 2;
/* 151 */     gridData.widthHint = 200;
/* 152 */     Utils.setLayoutData(lcryptofb, gridData);
/*     */     
/* 154 */     BooleanParameter fallback_outgoing = new BooleanParameter(gCrypto, "network.transport.encrypted.fallback.outgoing", "ConfigView.section.connection.encryption.encrypt.fallback_outgoing");
/* 155 */     gridData = new GridData();
/* 156 */     gridData.horizontalSpan = 2;
/* 157 */     fallback_outgoing.setLayoutData(gridData);
/*     */     
/* 159 */     final BooleanParameter fallback_incoming = new BooleanParameter(gCrypto, "network.transport.encrypted.fallback.incoming", "ConfigView.section.connection.encryption.encrypt.fallback_incoming");
/* 160 */     gridData = new GridData();
/* 161 */     gridData.horizontalSpan = 2;
/* 162 */     fallback_incoming.setLayoutData(gridData);
/*     */     
/* 164 */     final BooleanParameter use_crypto_port = new BooleanParameter(gCrypto, "network.transport.encrypted.use.crypto.port", "ConfigView.section.connection.encryption.use_crypto_port");
/* 165 */     gridData = new GridData();
/* 166 */     gridData.horizontalSpan = 2;
/* 167 */     use_crypto_port.setLayoutData(gridData);
/*     */     
/*     */ 
/* 170 */     final Control[] ap_controls = { min_level.getControl(), lmin, lcryptofb, fallback_outgoing.getControl(), fallback_incoming.getControl() };
/*     */     
/* 172 */     IAdditionalActionPerformer iap = new GenericActionPerformer(new Control[0])
/*     */     {
/*     */ 
/*     */       public void performAction()
/*     */       {
/*     */ 
/* 178 */         boolean required = require.isSelected().booleanValue();
/*     */         
/* 180 */         boolean ucp_enabled = (!fallback_incoming.isSelected().booleanValue()) && (required);
/*     */         
/* 182 */         use_crypto_port.getControl().setEnabled(ucp_enabled);
/*     */         
/* 184 */         for (int i = 0; i < ap_controls.length; i++)
/*     */         {
/* 186 */           ap_controls[i].setEnabled(required);
/*     */         }
/*     */         
/*     */       }
/* 190 */     };
/* 191 */     fallback_incoming.setAdditionalActionPerformer(iap);
/*     */     
/* 193 */     require.setAdditionalActionPerformer(iap);
/*     */     
/*     */ 
/*     */ 
/* 197 */     return cSection;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/configsections/ConfigSectionConnectionEncryption.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */