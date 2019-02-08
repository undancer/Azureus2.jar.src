/*     */ package org.gudy.azureus2.ui.swt.views.configsections;
/*     */ 
/*     */ import org.eclipse.swt.events.ModifyEvent;
/*     */ import org.eclipse.swt.events.ModifyListener;
/*     */ import org.eclipse.swt.layout.GridData;
/*     */ import org.eclipse.swt.layout.GridLayout;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.Label;
/*     */ import org.eclipse.swt.widgets.Text;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.ui.swt.Messages;
/*     */ import org.gudy.azureus2.ui.swt.Utils;
/*     */ import org.gudy.azureus2.ui.swt.config.PasswordParameter;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ConfigSectionInterfacePassword
/*     */   implements UISWTConfigSection
/*     */ {
/*     */   private static final String LBLKEY_PREFIX = "ConfigView.label.";
/*     */   private static final int REQUIRED_MODE = 0;
/*     */   Label passwordMatch;
/*     */   
/*     */   public String configSectionGetParentSection()
/*     */   {
/*  53 */     return "style";
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public String configSectionGetName()
/*     */   {
/*  60 */     return "interface.password";
/*     */   }
/*     */   
/*     */ 
/*     */   public void configSectionSave() {}
/*     */   
/*     */   public void configSectionDelete() {}
/*     */   
/*     */   public int maxUserMode()
/*     */   {
/*  70 */     return 0;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Composite configSectionCreate(Composite parent)
/*     */   {
/*  79 */     Composite cSection = new Composite(parent, 0);
/*  80 */     GridData gridData = new GridData(272);
/*     */     
/*  82 */     Utils.setLayoutData(cSection, gridData);
/*  83 */     GridLayout layout = new GridLayout();
/*  84 */     layout.marginWidth = 0;
/*  85 */     layout.numColumns = 2;
/*  86 */     cSection.setLayout(layout);
/*     */     
/*     */ 
/*     */ 
/*  90 */     Label label = new Label(cSection, 0);
/*  91 */     Messages.setLanguageText(label, "ConfigView.label.password");
/*     */     
/*  93 */     gridData = new GridData();
/*  94 */     gridData.widthHint = 150;
/*  95 */     PasswordParameter pw1 = new PasswordParameter(cSection, "Password");
/*  96 */     pw1.setLayoutData(gridData);
/*  97 */     Text t1 = (Text)pw1.getControl();
/*     */     
/*     */ 
/*     */ 
/* 101 */     label = new Label(cSection, 0);
/* 102 */     Messages.setLanguageText(label, "ConfigView.label.passwordconfirm");
/* 103 */     gridData = new GridData();
/* 104 */     gridData.widthHint = 150;
/* 105 */     PasswordParameter pw2 = new PasswordParameter(cSection, "Password Confirm");
/* 106 */     pw2.setLayoutData(gridData);
/* 107 */     Text t2 = (Text)pw2.getControl();
/*     */     
/*     */ 
/*     */ 
/* 111 */     label = new Label(cSection, 0);
/* 112 */     Messages.setLanguageText(label, "ConfigView.label.passwordmatch");
/* 113 */     this.passwordMatch = new Label(cSection, 0);
/* 114 */     gridData = new GridData();
/* 115 */     gridData.widthHint = 150;
/* 116 */     Utils.setLayoutData(this.passwordMatch, gridData);
/* 117 */     refreshPWLabel();
/*     */     
/* 119 */     t1.addModifyListener(new ModifyListener() {
/*     */       public void modifyText(ModifyEvent e) {
/* 121 */         ConfigSectionInterfacePassword.this.refreshPWLabel();
/*     */       }
/* 123 */     });
/* 124 */     t2.addModifyListener(new ModifyListener() {
/*     */       public void modifyText(ModifyEvent e) {
/* 126 */         ConfigSectionInterfacePassword.this.refreshPWLabel();
/*     */       }
/*     */       
/*     */ 
/* 130 */     });
/* 131 */     return cSection;
/*     */   }
/*     */   
/*     */ 
/*     */   private void refreshPWLabel()
/*     */   {
/* 137 */     if ((this.passwordMatch == null) || (this.passwordMatch.isDisposed()))
/* 138 */       return;
/* 139 */     byte[] password = COConfigurationManager.getByteParameter("Password", "".getBytes());
/*     */     
/* 141 */     COConfigurationManager.setParameter("Password enabled", false);
/* 142 */     if (password.length == 0) {
/* 143 */       this.passwordMatch.setText(MessageText.getString("ConfigView.label.passwordmatchnone"));
/*     */     }
/*     */     else {
/* 146 */       byte[] confirm = COConfigurationManager.getByteParameter("Password Confirm", "".getBytes());
/*     */       
/* 148 */       if (confirm.length == 0) {
/* 149 */         this.passwordMatch.setText(MessageText.getString("ConfigView.label.passwordmatchno"));
/*     */       }
/*     */       else {
/* 152 */         boolean same = true;
/* 153 */         for (int i = 0; i < password.length; i++) {
/* 154 */           if (password[i] != confirm[i])
/* 155 */             same = false;
/*     */         }
/* 157 */         if (same) {
/* 158 */           this.passwordMatch.setText(MessageText.getString("ConfigView.label.passwordmatchyes"));
/*     */           
/* 160 */           COConfigurationManager.setParameter("Password enabled", true);
/*     */         } else {
/* 162 */           this.passwordMatch.setText(MessageText.getString("ConfigView.label.passwordmatchno"));
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/configsections/ConfigSectionInterfacePassword.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */