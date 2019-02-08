/*     */ package org.gudy.azureus2.ui.swt.views.configsections;
/*     */ 
/*     */ import org.eclipse.swt.layout.GridData;
/*     */ import org.eclipse.swt.layout.GridLayout;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.Control;
/*     */ import org.eclipse.swt.widgets.Label;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.ui.swt.config.BooleanParameter;
/*     */ import org.gudy.azureus2.ui.swt.config.ChangeSelectionActionPerformer;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ConfigSectionInterfaceLegacy
/*     */   implements UISWTConfigSection
/*     */ {
/*     */   private static final String LBLKEY_PREFIX = "ConfigView.label.";
/*     */   private static final int REQUIRED_MODE = 2;
/*     */   
/*     */   public Composite configSectionCreate(Composite parent)
/*     */   {
/*  56 */     Composite cSection = new Composite(parent, 0);
/*     */     
/*  58 */     GridData gridData = new GridData(272);
/*     */     
/*  60 */     cSection.setLayoutData(gridData);
/*  61 */     GridLayout layout = new GridLayout();
/*  62 */     layout.numColumns = 1;
/*  63 */     layout.marginWidth = 0;
/*  64 */     layout.marginHeight = 0;
/*  65 */     cSection.setLayout(layout);
/*     */     
/*  67 */     int userMode = COConfigurationManager.getIntParameter("User Mode");
/*  68 */     if (userMode < 2) {
/*  69 */       Label label = new Label(cSection, 64);
/*  70 */       gridData = new GridData();
/*  71 */       gridData.horizontalSpan = 2;
/*  72 */       label.setLayoutData(gridData);
/*     */       
/*  74 */       String[] modeKeys = { "ConfigView.section.mode.beginner", "ConfigView.section.mode.intermediate", "ConfigView.section.mode.advanced" };
/*     */       
/*     */       String param1;
/*     */       
/*     */       String param1;
/*  79 */       if (2 < modeKeys.length) {
/*  80 */         param1 = MessageText.getString(modeKeys[2]);
/*     */       } else
/*  82 */         param1 = String.valueOf(2);
/*     */       String param2;
/*  84 */       String param2; if (userMode < modeKeys.length) {
/*  85 */         param2 = MessageText.getString(modeKeys[userMode]);
/*     */       } else {
/*  87 */         param2 = String.valueOf(userMode);
/*     */       }
/*  89 */       label.setText(MessageText.getString("ConfigView.notAvailableForMode", new String[] { param1, param2 }));
/*     */       
/*     */ 
/*  92 */       return cSection;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*  98 */     new BooleanParameter(cSection, "GUI_SWT_bOldSpeedMenu", "ConfigView.label.use_old_speed_menus");
/*     */     
/*     */ 
/* 101 */     BooleanParameter bpCustomTab = new BooleanParameter(cSection, "useCustomTab", "ConfigView.section.style.useCustomTabs");
/*     */     
/* 103 */     Control cFancyTab = new BooleanParameter(cSection, "GUI_SWT_bFancyTab", "ConfigView.section.style.useFancyTabs").getControl();
/*     */     
/*     */ 
/* 106 */     Control[] controls = { cFancyTab };
/*     */     
/*     */ 
/* 109 */     bpCustomTab.setAdditionalActionPerformer(new ChangeSelectionActionPerformer(controls));
/*     */     
/*     */ 
/* 112 */     return cSection;
/*     */   }
/*     */   
/*     */   public int maxUserMode()
/*     */   {
/* 117 */     return 2;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void configSectionDelete() {}
/*     */   
/*     */ 
/*     */ 
/*     */   public String configSectionGetName()
/*     */   {
/* 128 */     return "interface.legacy";
/*     */   }
/*     */   
/*     */   public String configSectionGetParentSection()
/*     */   {
/* 133 */     return "style";
/*     */   }
/*     */   
/*     */   public void configSectionSave() {}
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/configsections/ConfigSectionInterfaceLegacy.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */