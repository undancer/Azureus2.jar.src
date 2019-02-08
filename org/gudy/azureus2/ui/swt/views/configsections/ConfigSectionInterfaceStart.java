/*     */ package org.gudy.azureus2.ui.swt.views.configsections;
/*     */ 
/*     */ import org.eclipse.swt.layout.GridData;
/*     */ import org.eclipse.swt.layout.GridLayout;
/*     */ import org.eclipse.swt.widgets.Button;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.Control;
/*     */ import org.eclipse.swt.widgets.Event;
/*     */ import org.eclipse.swt.widgets.Label;
/*     */ import org.eclipse.swt.widgets.Listener;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.ui.swt.Messages;
/*     */ import org.gudy.azureus2.ui.swt.UISwitcherUtil;
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
/*     */ public class ConfigSectionInterfaceStart
/*     */   implements UISWTConfigSection
/*     */ {
/*     */   public String configSectionGetParentSection()
/*     */   {
/*  40 */     return "style";
/*     */   }
/*     */   
/*     */   public String configSectionGetName() {
/*  44 */     return "start";
/*     */   }
/*     */   
/*     */ 
/*     */   public void configSectionSave() {}
/*     */   
/*     */   public void configSectionDelete() {}
/*     */   
/*     */   public int maxUserMode()
/*     */   {
/*  54 */     return 0;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public Composite configSectionCreate(Composite parent)
/*     */   {
/*  62 */     Composite cStart = new Composite(parent, 0);
/*     */     
/*  64 */     cStart.setLayoutData(new GridData(1808));
/*  65 */     GridLayout layout = new GridLayout();
/*  66 */     layout.numColumns = 1;
/*  67 */     cStart.setLayout(layout);
/*     */     
/*  69 */     int userMode = COConfigurationManager.getIntParameter("User Mode");
/*  70 */     boolean isAZ3 = COConfigurationManager.getStringParameter("ui").equals("az3");
/*     */     
/*  72 */     if (userMode >= 2) {
/*  73 */       new BooleanParameter(cStart, "ui.startfirst", "ConfigView.label.StartUIBeforeCore");
/*     */     }
/*  75 */     new BooleanParameter(cStart, "Show Splash", "ConfigView.label.showsplash");
/*  76 */     new BooleanParameter(cStart, "update.start", "ConfigView.label.checkonstart");
/*  77 */     new BooleanParameter(cStart, "update.periodic", "ConfigView.label.periodiccheck");
/*  78 */     BooleanParameter autoDownload = new BooleanParameter(cStart, "update.autodownload", "ConfigView.section.update.autodownload");
/*  79 */     BooleanParameter openDialog = new BooleanParameter(cStart, "update.opendialog", "ConfigView.label.opendialog");
/*     */     
/*  81 */     autoDownload.setAdditionalActionPerformer(new ChangeSelectionActionPerformer(new Control[] { openDialog.getControl() }, true));
/*     */     
/*     */ 
/*  84 */     new BooleanParameter(cStart, "update.anonymous", "ConfigView.label.update.anonymous");
/*     */     
/*  86 */     new Label(cStart, 0);
/*  87 */     new BooleanParameter(cStart, "Open Transfer Bar On Start", "ConfigView.label.open_transfer_bar_on_start");
/*  88 */     new BooleanParameter(cStart, "Start Minimized", "ConfigView.label.startminimized");
/*     */     
/*     */ 
/*  91 */     Composite cUISwitcher = new Composite(cStart, 0);
/*  92 */     layout = new GridLayout(2, false);
/*  93 */     layout.marginHeight = 0;
/*  94 */     layout.marginWidth = 0;
/*  95 */     cUISwitcher.setLayout(layout);
/*     */     
/*  97 */     Label ui_switcher_label = new Label(cUISwitcher, 0);
/*  98 */     Messages.setLanguageText(ui_switcher_label, "ConfigView.label.ui_switcher");
/*     */     
/* 100 */     Button ui_switcher_button = new Button(cUISwitcher, 8);
/* 101 */     Messages.setLanguageText(ui_switcher_button, "ConfigView.label.ui_switcher_button");
/*     */     
/*     */ 
/* 104 */     ui_switcher_button.addListener(13, new Listener()
/*     */     {
/*     */ 
/*     */       public void handleEvent(Event event) {}
/*     */ 
/* 109 */     });
/* 110 */     return cStart;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/configsections/ConfigSectionInterfaceStart.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */