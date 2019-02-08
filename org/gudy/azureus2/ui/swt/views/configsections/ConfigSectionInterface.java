/*     */ package org.gudy.azureus2.ui.swt.views.configsections;
/*     */ 
/*     */ import com.aelitis.azureus.ui.common.RememberedDecisionsManager;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import org.eclipse.swt.layout.GridData;
/*     */ import org.eclipse.swt.layout.GridLayout;
/*     */ import org.eclipse.swt.widgets.Button;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.Control;
/*     */ import org.eclipse.swt.widgets.Event;
/*     */ import org.eclipse.swt.widgets.Group;
/*     */ import org.eclipse.swt.widgets.Label;
/*     */ import org.eclipse.swt.widgets.Listener;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.config.ParameterListener;
/*     */ import org.gudy.azureus2.core3.config.impl.StringListImpl;
/*     */ import org.gudy.azureus2.core3.logging.LogAlert;
/*     */ import org.gudy.azureus2.core3.logging.Logger;
/*     */ import org.gudy.azureus2.core3.util.Constants;
/*     */ import org.gudy.azureus2.core3.util.TrackersUtil;
/*     */ import org.gudy.azureus2.platform.PlatformManager;
/*     */ import org.gudy.azureus2.platform.PlatformManagerCapabilities;
/*     */ import org.gudy.azureus2.platform.PlatformManagerFactory;
/*     */ import org.gudy.azureus2.plugins.platform.PlatformManagerException;
/*     */ import org.gudy.azureus2.ui.swt.Messages;
/*     */ import org.gudy.azureus2.ui.swt.Utils;
/*     */ import org.gudy.azureus2.ui.swt.components.LinkLabel;
/*     */ import org.gudy.azureus2.ui.swt.config.BooleanParameter;
/*     */ import org.gudy.azureus2.ui.swt.config.ChangeSelectionActionPerformer;
/*     */ import org.gudy.azureus2.ui.swt.config.GenericActionPerformer;
/*     */ import org.gudy.azureus2.ui.swt.config.IAdditionalActionPerformer;
/*     */ import org.gudy.azureus2.ui.swt.config.IntParameter;
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
/*     */ public class ConfigSectionInterface
/*     */   implements UISWTConfigSection
/*     */ {
/*     */   private static final String KEY_PREFIX = "ConfigView.section.interface.";
/*     */   private static final String LBLKEY_PREFIX = "ConfigView.label.";
/*     */   private ParameterListener decisions_parameter_listener;
/*     */   
/*     */   public String configSectionGetParentSection()
/*     */   {
/*  58 */     return "root";
/*     */   }
/*     */   
/*     */   public String configSectionGetName() {
/*  62 */     return "style";
/*     */   }
/*     */   
/*     */ 
/*     */   public void configSectionSave() {}
/*     */   
/*     */   public void configSectionDelete()
/*     */   {
/*  70 */     if (this.decisions_parameter_listener != null)
/*     */     {
/*  72 */       COConfigurationManager.removeParameterListener("MessageBoxWindow.decisions", this.decisions_parameter_listener);
/*     */     }
/*     */   }
/*     */   
/*     */   public int maxUserMode()
/*     */   {
/*  78 */     return 0;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Composite configSectionCreate(Composite parent)
/*     */   {
/*  87 */     Composite cDisplay = new Composite(parent, 0);
/*     */     
/*  89 */     GridData gridData = new GridData(272);
/*     */     
/*  91 */     Utils.setLayoutData(cDisplay, gridData);
/*  92 */     GridLayout layout = new GridLayout();
/*  93 */     layout.numColumns = 1;
/*  94 */     layout.marginWidth = 0;
/*  95 */     layout.marginHeight = 0;
/*  96 */     cDisplay.setLayout(layout);
/*     */     
/*  98 */     final PlatformManager platform = PlatformManagerFactory.getPlatformManager();
/*     */     
/*     */ 
/*     */ 
/* 102 */     Group gAutoOpen = new Group(cDisplay, 0);
/* 103 */     Messages.setLanguageText(gAutoOpen, "ConfigView.label.autoopen");
/* 104 */     layout = new GridLayout(3, false);
/* 105 */     gAutoOpen.setLayout(layout);
/* 106 */     Utils.setLayoutData(gAutoOpen, new GridData(768));
/*     */     
/*     */ 
/* 109 */     Label label = new Label(gAutoOpen, 0);
/* 110 */     Messages.setLanguageText(label, "ConfigView.label.autoopen.detailstab");
/* 111 */     new BooleanParameter(gAutoOpen, "Open Details", "ConfigView.label.autoopen.dl");
/*     */     
/* 113 */     new BooleanParameter(gAutoOpen, "Open Seeding Details", "ConfigView.label.autoopen.cd");
/*     */     
/*     */ 
/*     */ 
/* 117 */     label = new Label(gAutoOpen, 0);
/* 118 */     Messages.setLanguageText(label, "ConfigView.label.autoopen.downloadbars");
/* 119 */     new BooleanParameter(gAutoOpen, "Open Bar Incomplete", "ConfigView.label.autoopen.dl");
/* 120 */     new BooleanParameter(gAutoOpen, "Open Bar Complete", "ConfigView.label.autoopen.cd");
/*     */     
/*     */ 
/*     */ 
/* 124 */     if (!Constants.isOSX)
/*     */     {
/* 126 */       new BooleanParameter(cDisplay, "Show Status In Window Title", "ConfigView.label.info.in.window.title");
/*     */     }
/*     */     
/* 129 */     new BooleanParameter(cDisplay, "Remember transfer bar location", "ConfigView.label.transferbar.remember_location");
/*     */     
/* 131 */     Composite gBarTrans = new Composite(cDisplay, 0);
/* 132 */     layout = new GridLayout(4, false);
/* 133 */     layout.marginWidth = 0;
/* 134 */     layout.marginHeight = 0;
/* 135 */     gBarTrans.setLayout(layout);
/* 136 */     gridData = new GridData(768);
/* 137 */     gridData.horizontalIndent = 25;
/* 138 */     Utils.setLayoutData(gBarTrans, gridData);
/*     */     
/* 140 */     label = new Label(gBarTrans, 0);
/* 141 */     Messages.setLanguageText(label, "label.bar.trans");
/*     */     
/* 143 */     new IntParameter(gBarTrans, "Bar Transparency", 0, 100);
/*     */     
/* 145 */     label = new Label(gBarTrans, 0);
/* 146 */     Messages.setLanguageText(label, "label.show.icon.area");
/*     */     
/* 148 */     new BooleanParameter(gBarTrans, "Transfer Bar Show Icon Area");
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 153 */     Group gSysTray = new Group(cDisplay, 0);
/* 154 */     Messages.setLanguageText(gSysTray, "ConfigView.label.systray");
/* 155 */     layout = new GridLayout();
/* 156 */     gSysTray.setLayout(layout);
/* 157 */     Utils.setLayoutData(gSysTray, new GridData(768));
/*     */     
/* 159 */     BooleanParameter est = new BooleanParameter(gSysTray, "Enable System Tray", "ConfigView.section.interface.enabletray");
/*     */     
/*     */ 
/* 162 */     BooleanParameter ctt = new BooleanParameter(gSysTray, "Close To Tray", "ConfigView.label.closetotray");
/*     */     
/* 164 */     BooleanParameter mtt = new BooleanParameter(gSysTray, "Minimize To Tray", "ConfigView.label.minimizetotray");
/*     */     
/* 166 */     BooleanParameter esttt = new BooleanParameter(gSysTray, "ui.systray.tooltip.enable", "ConfigView.label.enableSystrayToolTip");
/*     */     
/*     */ 
/* 169 */     BooleanParameter estttd = new BooleanParameter(gSysTray, "ui.systray.tooltip.next.eta.enable", "ConfigView.label.enableSystrayToolTipNextETA");
/*     */     
/* 171 */     gridData = new GridData();
/* 172 */     gridData.horizontalIndent = 25;
/* 173 */     estttd.setLayoutData(gridData);
/* 174 */     est.setAdditionalActionPerformer(new ChangeSelectionActionPerformer(ctt.getControls()));
/*     */     
/* 176 */     est.setAdditionalActionPerformer(new ChangeSelectionActionPerformer(mtt.getControls()));
/*     */     
/* 178 */     est.setAdditionalActionPerformer(new ChangeSelectionActionPerformer(esttt.getControls()));
/*     */     
/* 180 */     est.setAdditionalActionPerformer(new ChangeSelectionActionPerformer(estttd.getControls()));
/*     */     
/*     */ 
/* 183 */     esttt.setAdditionalActionPerformer(new ChangeSelectionActionPerformer(estttd.getControls()));
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 189 */     Group limit_group = new Group(cDisplay, 0);
/* 190 */     Messages.setLanguageText(limit_group, "ConfigView.label.set_ui_transfer_speeds");
/* 191 */     layout = new GridLayout();
/* 192 */     layout.numColumns = 2;
/* 193 */     limit_group.setLayout(layout);
/* 194 */     Utils.setLayoutData(limit_group, new GridData(768));
/*     */     
/* 196 */     Label limit_group_label = new Label(limit_group, 64);
/* 197 */     Utils.setLayoutData(limit_group_label, Utils.getWrappableLabelGridData(2, 512));
/* 198 */     Messages.setLanguageText(limit_group_label, "ConfigView.label.set_ui_transfer_speeds.description");
/*     */     
/* 200 */     String[] limit_types = { "download", "upload" };
/* 201 */     String limit_type_prefix = "config.ui.speed.partitions.manual.";
/* 202 */     for (int i = 0; i < limit_types.length; i++) {
/* 203 */       final BooleanParameter bp = new BooleanParameter(limit_group, "config.ui.speed.partitions.manual." + limit_types[i] + ".enabled", false, "ConfigView.label.set_ui_transfer_speeds.description." + limit_types[i]);
/* 204 */       final StringParameter sp = new StringParameter(limit_group, "config.ui.speed.partitions.manual." + limit_types[i] + ".values", "");
/* 205 */       IAdditionalActionPerformer iaap = new GenericActionPerformer(new Control[0]) {
/*     */         public void performAction() {
/* 207 */           sp.getControl().setEnabled(bp.isSelected().booleanValue());
/*     */         }
/*     */         
/* 210 */       };
/* 211 */       gridData = new GridData();
/* 212 */       gridData.widthHint = 150;
/* 213 */       sp.setLayoutData(gridData);
/* 214 */       iaap.performAction();
/* 215 */       bp.setAdditionalActionPerformer(iaap);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 220 */     new BooleanParameter(cDisplay, "Send Version Info", "ConfigView.label.allowSendVersion");
/*     */     
/*     */ 
/* 223 */     Composite cArea = new Composite(cDisplay, 0);
/* 224 */     layout = new GridLayout();
/* 225 */     layout.marginHeight = 0;
/* 226 */     layout.marginWidth = 0;
/* 227 */     layout.numColumns = 2;
/* 228 */     cArea.setLayout(layout);
/* 229 */     Utils.setLayoutData(cArea, new GridData(768));
/*     */     
/* 231 */     new LinkLabel(cArea, "ConfigView.label.version.info.link", "http://wiki.vuze.com/w/Version.azureusplatform.com");
/*     */     
/*     */ 
/* 234 */     if (!Constants.isOSX)
/*     */     {
/* 236 */       BooleanParameter confirm = new BooleanParameter(cArea, "confirmationOnExit", "ConfigView.section.style.confirmationOnExit");
/*     */       
/*     */ 
/* 239 */       gridData = new GridData();
/* 240 */       gridData.horizontalSpan = 2;
/* 241 */       confirm.setLayoutData(gridData);
/*     */     }
/*     */     
/* 244 */     cArea = new Composite(cDisplay, 0);
/* 245 */     layout = new GridLayout();
/* 246 */     layout.marginHeight = 0;
/* 247 */     layout.marginWidth = 0;
/* 248 */     layout.numColumns = 2;
/* 249 */     cArea.setLayout(layout);
/* 250 */     Utils.setLayoutData(cArea, new GridData(768));
/*     */     
/*     */ 
/*     */ 
/* 254 */     final Label clear_label = new Label(cArea, 0);
/* 255 */     Messages.setLanguageText(clear_label, "ConfigView.section.interface.cleardecisions");
/*     */     
/* 257 */     final Button clear_decisions = new Button(cArea, 8);
/* 258 */     Messages.setLanguageText(clear_decisions, "ConfigView.section.interface.cleardecisionsbutton");
/*     */     
/*     */ 
/* 261 */     clear_decisions.addListener(13, new Listener()
/*     */     {
/*     */ 
/*     */       public void handleEvent(Event event) {}
/*     */ 
/*     */ 
/* 267 */     });
/* 268 */     Label clear_tracker_label = new Label(cArea, 0);
/* 269 */     Messages.setLanguageText(clear_tracker_label, "ConfigView.section.interface.cleartrackers");
/*     */     
/* 271 */     Button clear_tracker_button = new Button(cArea, 8);
/* 272 */     Messages.setLanguageText(clear_tracker_button, "ConfigView.section.interface.cleartrackersbutton");
/*     */     
/*     */ 
/* 275 */     clear_tracker_button.addListener(13, new Listener() {
/*     */       public void handleEvent(Event event) {
/* 277 */         TrackersUtil.getInstance().clearAllTrackers(true);
/*     */       }
/*     */       
/* 280 */     });
/* 281 */     Label clear_save_path_label = new Label(cArea, 0);
/* 282 */     Messages.setLanguageText(clear_save_path_label, "ConfigView.section.interface.clearsavepaths");
/*     */     
/* 284 */     Button clear_save_path_button = new Button(cArea, 8);
/* 285 */     Messages.setLanguageText(clear_save_path_button, "ConfigView.section.interface.clearsavepathsbutton");
/*     */     
/*     */ 
/* 288 */     clear_save_path_button.addListener(13, new Listener() {
/*     */       public void handleEvent(Event event) {
/* 290 */         COConfigurationManager.setParameter("saveTo_list", new StringListImpl());
/*     */       }
/*     */       
/* 293 */     });
/* 294 */     this.decisions_parameter_listener = new ParameterListener() {
/*     */       public void parameterChanged(String parameterName) {
/* 296 */         if (clear_decisions.isDisposed())
/*     */         {
/*     */ 
/*     */ 
/* 300 */           COConfigurationManager.removeParameterListener("MessageBoxWindow.decisions", this);
/*     */ 
/*     */         }
/*     */         else
/*     */         {
/* 305 */           boolean enabled = COConfigurationManager.getMapParameter("MessageBoxWindow.decisions", new HashMap()).size() > 0;
/*     */           
/*     */ 
/* 308 */           clear_label.setEnabled(enabled);
/* 309 */           clear_decisions.setEnabled(enabled);
/*     */         }
/*     */         
/*     */       }
/* 313 */     };
/* 314 */     this.decisions_parameter_listener.parameterChanged(null);
/*     */     
/* 316 */     COConfigurationManager.addParameterListener("MessageBoxWindow.decisions", this.decisions_parameter_listener);
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
/* 343 */     if (platform.hasCapability(PlatformManagerCapabilities.RegisterFileAssociations))
/*     */     {
/* 345 */       Composite cResetAssoc = new Composite(cArea, 0);
/* 346 */       layout = new GridLayout();
/* 347 */       layout.marginHeight = 0;
/* 348 */       layout.marginWidth = 0;
/* 349 */       layout.numColumns = 2;
/* 350 */       cResetAssoc.setLayout(layout);
/* 351 */       Utils.setLayoutData(cResetAssoc, new GridData());
/*     */       
/* 353 */       label = new Label(cResetAssoc, 0);
/* 354 */       Messages.setLanguageText(label, "ConfigView.section.interface.resetassoc");
/*     */       
/* 356 */       Button reset = new Button(cResetAssoc, 8);
/* 357 */       Messages.setLanguageText(reset, "ConfigView.section.interface.resetassocbutton");
/*     */       
/* 359 */       reset.addListener(13, new Listener()
/*     */       {
/*     */         public void handleEvent(Event event) {
/*     */           try {
/* 363 */             platform.registerApplication();
/*     */           }
/*     */           catch (PlatformManagerException e)
/*     */           {
/* 367 */             Logger.log(new LogAlert(false, "Failed to register application", e));
/*     */           }
/*     */           
/*     */         }
/*     */         
/* 372 */       });
/* 373 */       new BooleanParameter(cArea, "config.interface.checkassoc", "ConfigView.section.interface.checkassoc");
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 378 */     return cDisplay;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/configsections/ConfigSectionInterface.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */