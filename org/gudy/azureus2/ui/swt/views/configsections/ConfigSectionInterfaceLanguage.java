/*     */ package org.gudy.azureus2.ui.swt.views.configsections;
/*     */ 
/*     */ import com.aelitis.azureus.ui.UIFunctions;
/*     */ import com.aelitis.azureus.ui.UIFunctionsManager;
/*     */ import java.util.Locale;
/*     */ import org.eclipse.swt.layout.GridData;
/*     */ import org.eclipse.swt.layout.GridLayout;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.Label;
/*     */ import org.eclipse.swt.widgets.List;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.core3.util.DisplayFormatters;
/*     */ import org.gudy.azureus2.ui.swt.Messages;
/*     */ import org.gudy.azureus2.ui.swt.config.BooleanParameter;
/*     */ import org.gudy.azureus2.ui.swt.config.Parameter;
/*     */ import org.gudy.azureus2.ui.swt.config.ParameterChangeAdapter;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ConfigSectionInterfaceLanguage
/*     */   implements UISWTConfigSection
/*     */ {
/*     */   public String configSectionGetParentSection()
/*     */   {
/*  48 */     return "style";
/*     */   }
/*     */   
/*     */   public String configSectionGetName() {
/*  52 */     return "language";
/*     */   }
/*     */   
/*     */ 
/*     */   public void configSectionSave() {}
/*     */   
/*     */   public void configSectionDelete() {}
/*     */   
/*     */   public int maxUserMode()
/*     */   {
/*  62 */     return 0;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public Composite configSectionCreate(Composite parent)
/*     */   {
/*  69 */     Composite cMain = new Composite(parent, 0);
/*  70 */     cMain.setLayoutData(new GridData(1808));
/*  71 */     GridLayout layout = new GridLayout();
/*  72 */     layout.numColumns = 1;
/*  73 */     layout.marginHeight = 0;
/*  74 */     layout.marginWidth = 0;
/*  75 */     cMain.setLayout(layout);
/*     */     
/*  77 */     Label label = new Label(cMain, 0);
/*  78 */     GridData gridData = new GridData(2);
/*  79 */     label.setLayoutData(gridData);
/*  80 */     Messages.setLanguageText(label, "MainWindow.menu.language");
/*     */     
/*  82 */     Locale[] locales = MessageText.getLocales(true);
/*     */     
/*  84 */     String[] drop_labels = new String[locales.length];
/*  85 */     String[] drop_values = new String[locales.length];
/*  86 */     int iUsingLocale = -1;
/*  87 */     for (int i = 0; i < locales.length; i++) {
/*  88 */       Locale locale = locales[i];
/*  89 */       String sName = locale.getDisplayName(locale);
/*  90 */       String sName2 = locale.getDisplayName();
/*  91 */       if (!sName.equals(sName2)) {
/*  92 */         sName = sName + " - " + sName2;
/*     */       }
/*  94 */       drop_labels[i] = (sName + " - " + locale);
/*  95 */       drop_values[i] = locale.toString();
/*  96 */       if (MessageText.isCurrentLocale(locale)) {
/*  97 */         iUsingLocale = i;
/*     */       }
/*     */     }
/* 100 */     StringListParameter locale_param = new StringListParameter(cMain, "locale", drop_labels, drop_values, false);
/*     */     
/* 102 */     gridData = new GridData(1808);
/* 103 */     gridData.minimumHeight = 50;
/* 104 */     locale_param.setLayoutData(gridData);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 109 */     if (iUsingLocale >= 0) {
/* 110 */       ((List)locale_param.getControl()).select(iUsingLocale);
/*     */     }
/* 112 */     locale_param.addChangeListener(new ParameterChangeAdapter() {
/*     */       public void parameterChanged(Parameter p, boolean caused_internally) {
/* 114 */         MessageText.loadBundle();
/* 115 */         DisplayFormatters.setUnits();
/* 116 */         DisplayFormatters.loadMessages();
/* 117 */         UIFunctions uiFunctions = UIFunctionsManager.getUIFunctions();
/* 118 */         if (uiFunctions != null) {
/* 119 */           uiFunctions.refreshLanguage();
/*     */         }
/*     */         
/*     */       }
/* 123 */     });
/* 124 */     BooleanParameter uc = new BooleanParameter(cMain, "label.lang.upper.case", false, "label.lang.upper.case");
/*     */     
/* 126 */     uc.addChangeListener(new ParameterChangeAdapter() {
/*     */       public void parameterChanged(Parameter p, boolean caused_internally) {
/* 128 */         MessageText.loadBundle(true);
/* 129 */         DisplayFormatters.setUnits();
/* 130 */         DisplayFormatters.loadMessages();
/* 131 */         UIFunctions uiFunctions = UIFunctionsManager.getUIFunctions();
/* 132 */         if (uiFunctions != null) {
/* 133 */           uiFunctions.refreshLanguage();
/*     */         }
/*     */         
/*     */       }
/* 137 */     });
/* 138 */     return cMain;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/configsections/ConfigSectionInterfaceLanguage.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */