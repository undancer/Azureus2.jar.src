/*     */ package org.gudy.azureus2.ui.swt.config.wizard;
/*     */ 
/*     */ import com.aelitis.azureus.ui.UIFunctions;
/*     */ import com.aelitis.azureus.ui.UIFunctionsManager;
/*     */ import java.util.Locale;
/*     */ import org.eclipse.swt.events.DisposeEvent;
/*     */ import org.eclipse.swt.events.DisposeListener;
/*     */ import org.eclipse.swt.graphics.Font;
/*     */ import org.eclipse.swt.graphics.FontData;
/*     */ import org.eclipse.swt.layout.GridData;
/*     */ import org.eclipse.swt.layout.GridLayout;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.Event;
/*     */ import org.eclipse.swt.widgets.Label;
/*     */ import org.eclipse.swt.widgets.List;
/*     */ import org.eclipse.swt.widgets.Listener;
/*     */ import org.eclipse.swt.widgets.Shell;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.core3.util.DisplayFormatters;
/*     */ import org.gudy.azureus2.ui.swt.Messages;
/*     */ import org.gudy.azureus2.ui.swt.Utils;
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
/*     */ 
/*     */ public class LanguagePanel
/*     */   extends AbstractWizardPanel
/*     */ {
/*     */   public LanguagePanel(Wizard wizard, IWizardPanel previousPanel)
/*     */   {
/*  54 */     super(wizard, previousPanel);
/*     */   }
/*     */   
/*     */ 
/*     */   public void show()
/*     */   {
/*  60 */     this.wizard.setTitleAsResourceID("configureWizard.welcome.title");
/*     */     
/*  62 */     Composite rootPanel = this.wizard.getPanel();
/*  63 */     GridLayout layout = new GridLayout();
/*  64 */     layout.numColumns = 1;
/*  65 */     rootPanel.setLayout(layout);
/*     */     
/*  67 */     final Label lblChoose = new Label(rootPanel, 64);
/*  68 */     setChooseLabel(lblChoose);
/*  69 */     GridData gridData = new GridData(768);
/*  70 */     Utils.setLayoutData(lblChoose, gridData);
/*     */     
/*  72 */     final List lstLanguage = new List(rootPanel, 2564);
/*     */     
/*  74 */     gridData = new GridData(1808);
/*  75 */     gridData.heightHint = 350;
/*  76 */     Utils.setLayoutData(lstLanguage, gridData);
/*     */     
/*  78 */     final Locale[] locales = MessageText.getLocales(true);
/*     */     
/*  80 */     int iUsingLocale = -1;
/*  81 */     for (int i = 0; i < locales.length; i++) {
/*  82 */       Locale locale = locales[i];
/*     */       
/*  84 */       lstLanguage.add(buildName(locale));
/*  85 */       if (MessageText.isCurrentLocale(locale))
/*  86 */         iUsingLocale = i;
/*     */     }
/*  88 */     lstLanguage.select(iUsingLocale);
/*     */     
/*  90 */     lstLanguage.addListener(13, new Listener() {
/*     */       public void handleEvent(Event e) {
/*  92 */         int index = lstLanguage.getSelectionIndex();
/*  93 */         if ((index >= 0) && (index < locales.length)) {
/*  94 */           COConfigurationManager.setParameter("locale", locales[index].toString());
/*     */           
/*     */ 
/*  97 */           MessageText.loadBundle();
/*  98 */           DisplayFormatters.setUnits();
/*  99 */           DisplayFormatters.loadMessages();
/*     */           
/* 101 */           Shell shell = LanguagePanel.this.wizard.getWizardWindow();
/* 102 */           Messages.updateLanguageForControl(shell);
/* 103 */           LanguagePanel.this.setChooseLabel(lblChoose);
/*     */           
/* 105 */           shell.layout(true, true);
/*     */           
/* 107 */           lstLanguage.setRedraw(false);
/* 108 */           for (int i = 0; i < locales.length; i++) {
/* 109 */             lstLanguage.setItem(i, LanguagePanel.this.buildName(locales[i]));
/*     */           }
/* 111 */           lstLanguage.setRedraw(true);
/*     */           try
/*     */           {
/* 114 */             UIFunctions uiFunctions = UIFunctionsManager.getUIFunctions();
/* 115 */             if (uiFunctions != null) {
/* 116 */               uiFunctions.refreshLanguage();
/*     */             }
/*     */             
/*     */           }
/*     */           catch (Exception ex) {}
/*     */         }
/*     */       }
/* 123 */     });
/* 124 */     FontData[] fontData = lstLanguage.getFont().getFontData();
/* 125 */     for (int i = 0; i < fontData.length; i++) {
/* 126 */       if (fontData[i].getHeight() < 10)
/* 127 */         fontData[i].setHeight(10);
/*     */     }
/* 129 */     final Font font = new Font(rootPanel.getDisplay(), fontData);
/* 130 */     lstLanguage.setFont(font);
/*     */     
/* 132 */     lstLanguage.getShell().addListener(22, new Listener() {
/*     */       public void handleEvent(Event event) {
/* 134 */         lstLanguage.showSelection();
/*     */       }
/*     */       
/* 137 */     });
/* 138 */     lstLanguage.addDisposeListener(new DisposeListener() {
/*     */       public void widgetDisposed(DisposeEvent e) {
/* 140 */         if ((font != null) && (!font.isDisposed())) {
/* 141 */           font.dispose();
/*     */         }
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */   private void setChooseLabel(Label lblChoose)
/*     */   {
/* 150 */     String sLocaleChooseString = MessageText.getString("ConfigureWizard.language.choose");
/*     */     
/* 152 */     String sDefChooseString = MessageText.getDefaultLocaleString("ConfigureWizard.language.choose");
/*     */     
/* 154 */     if (sLocaleChooseString.equals(sDefChooseString)) {
/* 155 */       lblChoose.setText(sLocaleChooseString);
/*     */     } else {
/* 157 */       lblChoose.setText(sLocaleChooseString + "\n" + sDefChooseString);
/*     */     }
/*     */   }
/*     */   
/*     */   private String buildName(Locale locale) {
/* 162 */     StringBuilder sName = new StringBuilder();
/*     */     
/* 164 */     String sName1 = locale.getDisplayLanguage(locale);
/* 165 */     String sName2 = locale.getDisplayLanguage();
/* 166 */     sName.append(sName1);
/*     */     
/* 168 */     if (!sName1.equals(sName2)) {
/* 169 */       sName.append("/").append(sName2);
/*     */     }
/*     */     
/* 172 */     sName1 = locale.getDisplayCountry(locale);
/* 173 */     sName2 = locale.getDisplayCountry();
/* 174 */     if ((sName1.length() > 0) || (sName2.length() > 0)) {
/* 175 */       sName.append(" (");
/* 176 */       if (sName1.length() > 0) {
/* 177 */         sName.append(sName1);
/*     */       }
/* 179 */       if ((sName2.length() > 0) && (!sName1.equals(sName2))) {
/* 180 */         sName.append("/").append(sName2);
/*     */       }
/*     */       
/* 183 */       sName1 = locale.getDisplayVariant(locale);
/* 184 */       sName2 = locale.getDisplayVariant();
/* 185 */       if ((sName1.length() > 0) || (sName2.length() > 0)) {
/* 186 */         sName.append(", ");
/* 187 */         if (sName1.length() > 0) {
/* 188 */           sName.append(sName1);
/*     */         }
/* 190 */         if ((sName2.length() > 0) && (!sName1.equals(sName2))) {
/* 191 */           sName.append("/").append(sName2);
/*     */         }
/*     */       }
/*     */       
/* 195 */       sName.append(")");
/*     */     }
/*     */     
/* 198 */     return sName.toString();
/*     */   }
/*     */   
/*     */   public boolean isNextEnabled() {
/* 202 */     return true;
/*     */   }
/*     */   
/*     */   public IWizardPanel getNextPanel() {
/* 206 */     return new WelcomePanel((ConfigureWizard)this.wizard, this);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/config/wizard/LanguagePanel.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */