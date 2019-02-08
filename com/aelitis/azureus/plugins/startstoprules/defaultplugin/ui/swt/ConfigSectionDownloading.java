/*     */ package com.aelitis.azureus.plugins.startstoprules.defaultplugin.ui.swt;
/*     */ 
/*     */ import org.eclipse.swt.events.MouseAdapter;
/*     */ import org.eclipse.swt.events.MouseEvent;
/*     */ import org.eclipse.swt.layout.GridData;
/*     */ import org.eclipse.swt.layout.GridLayout;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.Control;
/*     */ import org.eclipse.swt.widgets.Display;
/*     */ import org.eclipse.swt.widgets.Group;
/*     */ import org.eclipse.swt.widgets.Label;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.ui.swt.Messages;
/*     */ import org.gudy.azureus2.ui.swt.Utils;
/*     */ import org.gudy.azureus2.ui.swt.config.IntListParameter;
/*     */ import org.gudy.azureus2.ui.swt.config.IntParameter;
/*     */ import org.gudy.azureus2.ui.swt.config.Parameter;
/*     */ import org.gudy.azureus2.ui.swt.config.ParameterChangeAdapter;
/*     */ import org.gudy.azureus2.ui.swt.config.ParameterChangeListener;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ConfigSectionDownloading
/*     */   implements UISWTConfigSection
/*     */ {
/*     */   public String configSectionGetParentSection()
/*     */   {
/*  50 */     return "queue";
/*     */   }
/*     */   
/*     */   public String configSectionGetName() {
/*  54 */     return "queue.downloading";
/*     */   }
/*     */   
/*     */ 
/*     */   public void configSectionSave() {}
/*     */   
/*     */   public void configSectionDelete() {}
/*     */   
/*     */   public int maxUserMode()
/*     */   {
/*  64 */     return 0;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Composite configSectionCreate(Composite parent)
/*     */   {
/*  73 */     Composite cDownloading = new Composite(parent, 0);
/*     */     
/*  75 */     GridLayout layout = new GridLayout();
/*  76 */     layout.numColumns = 2;
/*  77 */     layout.marginHeight = 0;
/*  78 */     cDownloading.setLayout(layout);
/*  79 */     GridData gridData = new GridData(272);
/*  80 */     Utils.setLayoutData(cDownloading, gridData);
/*     */     
/*     */ 
/*     */ 
/*  84 */     Label linkLabel = new Label(cDownloading, 0);
/*  85 */     linkLabel.setText(MessageText.getString("ConfigView.label.please.visit.here"));
/*  86 */     linkLabel.setData("http://wiki.vuze.com/w/Downloading_Rules");
/*  87 */     linkLabel.setCursor(linkLabel.getDisplay().getSystemCursor(21));
/*  88 */     linkLabel.setForeground(Colors.blue);
/*  89 */     gridData = new GridData();
/*  90 */     gridData.horizontalSpan = 2;
/*  91 */     Utils.setLayoutData(linkLabel, gridData);
/*  92 */     linkLabel.addMouseListener(new MouseAdapter() {
/*     */       public void mouseDoubleClick(MouseEvent arg0) {
/*  94 */         Utils.launch((String)((Label)arg0.widget).getData());
/*     */       }
/*     */       
/*     */       public void mouseDown(MouseEvent arg0) {
/*  98 */         Utils.launch((String)((Label)arg0.widget).getData());
/*     */       }
/* 100 */     });
/* 101 */     ClipboardCopy.addCopyToClipMenu(linkLabel);
/*     */     
/*     */ 
/*     */ 
/* 105 */     Label label = new Label(cDownloading, 0);
/* 106 */     Messages.setLanguageText(label, "label.prioritize.downloads.based.on");
/*     */     
/* 108 */     String[] orderLabels = { MessageText.getString("label.order"), MessageText.getString("label.seed.count"), MessageText.getString("label.speed") };
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 114 */     int[] orderValues = { 0, 1, 2 };
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 120 */     final IntListParameter sort_type = new IntListParameter(cDownloading, "StartStopManager_Downloading_iSortType", orderLabels, orderValues);
/*     */     
/*     */ 
/*     */ 
/* 124 */     Group gSpeed = new Group(cDownloading, 0);
/* 125 */     gridData = new GridData(768);
/*     */     
/* 127 */     layout = new GridLayout();
/* 128 */     layout.numColumns = 2;
/*     */     
/* 130 */     gSpeed.setLayout(layout);
/* 131 */     gridData = new GridData(768);
/* 132 */     gridData.horizontalSpan = 2;
/* 133 */     Utils.setLayoutData(gSpeed, gridData);
/*     */     
/* 135 */     gSpeed.setText(MessageText.getString("label.speed.options"));
/*     */     
/*     */ 
/* 138 */     label = new Label(gSpeed, 64);
/* 139 */     gridData = new GridData(768);
/* 140 */     gridData.horizontalSpan = 2;
/* 141 */     gridData.widthHint = 300;
/* 142 */     Utils.setLayoutData(label, gridData);
/* 143 */     Messages.setLanguageText(label, "ConfigView.label.downloading.info");
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 148 */     label = new Label(gSpeed, 0);
/* 149 */     Messages.setLanguageText(label, "ConfigView.label.downloading.testTime");
/* 150 */     gridData = new GridData();
/* 151 */     final IntParameter testTime = new IntParameter(gSpeed, "StartStopManager_Downloading_iTestTimeSecs");
/* 152 */     testTime.setLayoutData(gridData);
/* 153 */     testTime.setMinimumValue(60);
/*     */     
/*     */ 
/*     */ 
/* 157 */     label = new Label(gSpeed, 0);
/* 158 */     Messages.setLanguageText(label, "ConfigView.label.downloading.reTest");
/* 159 */     gridData = new GridData();
/* 160 */     final IntParameter reTest = new IntParameter(gSpeed, "StartStopManager_Downloading_iRetestTimeMins");
/* 161 */     reTest.setLayoutData(gridData);
/* 162 */     reTest.setMinimumValue(0);
/*     */     
/* 164 */     ParameterChangeListener listener = new ParameterChangeAdapter()
/*     */     {
/*     */ 
/*     */ 
/*     */       public void parameterChanged(Parameter p, boolean caused_internally)
/*     */       {
/*     */ 
/*     */ 
/* 172 */         boolean is_speed = ((Integer)sort_type.getValueObject()).intValue() == 2;
/*     */         
/* 174 */         testTime.setEnabled(is_speed);
/* 175 */         reTest.setEnabled(is_speed);
/*     */       }
/*     */       
/* 178 */     };
/* 179 */     sort_type.addChangeListener(listener);
/*     */     
/* 181 */     listener.parameterChanged(null, false);
/*     */     
/* 183 */     return cDownloading;
/*     */   }
/*     */   
/*     */   private void controlsSetEnabled(Control[] controls, boolean bEnabled) {
/* 187 */     for (int i = 0; i < controls.length; i++) {
/* 188 */       if ((controls[i] instanceof Composite))
/* 189 */         controlsSetEnabled(((Composite)controls[i]).getChildren(), bEnabled);
/* 190 */       controls[i].setEnabled(bEnabled);
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/plugins/startstoprules/defaultplugin/ui/swt/ConfigSectionDownloading.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */