/*     */ package org.gudy.azureus2.ui.swt.views.configsections;
/*     */ 
/*     */ import org.eclipse.swt.layout.GridData;
/*     */ import org.eclipse.swt.layout.GridLayout;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.Control;
/*     */ import org.eclipse.swt.widgets.Label;
/*     */ import org.gudy.azureus2.ui.swt.Messages;
/*     */ import org.gudy.azureus2.ui.swt.Utils;
/*     */ import org.gudy.azureus2.ui.swt.config.BooleanParameter;
/*     */ import org.gudy.azureus2.ui.swt.config.ChangeSelectionActionPerformer;
/*     */ import org.gudy.azureus2.ui.swt.config.IntParameter;
/*     */ import org.gudy.azureus2.ui.swt.config.Parameter;
/*     */ import org.gudy.azureus2.ui.swt.config.ParameterChangeAdapter;
/*     */ import org.gudy.azureus2.ui.swt.config.StringListParameter;
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
/*     */ public class ConfigSectionSharing
/*     */   implements UISWTConfigSection
/*     */ {
/*     */   public String configSectionGetParentSection()
/*     */   {
/*  38 */     return "root";
/*     */   }
/*     */   
/*     */   public String configSectionGetName() {
/*  42 */     return "sharing";
/*     */   }
/*     */   
/*     */ 
/*     */   public void configSectionSave() {}
/*     */   
/*     */   public void configSectionDelete() {}
/*     */   
/*     */   public int maxUserMode()
/*     */   {
/*  52 */     return 0;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Composite configSectionCreate(Composite parent)
/*     */   {
/*  61 */     Composite gSharing = new Composite(parent, 64);
/*  62 */     GridData gridData = new GridData(272);
/*  63 */     Utils.setLayoutData(gSharing, gridData);
/*  64 */     GridLayout layout = new GridLayout();
/*  65 */     layout.numColumns = 2;
/*  66 */     layout.marginHeight = 0;
/*  67 */     gSharing.setLayout(layout);
/*     */     
/*     */ 
/*     */ 
/*  71 */     gridData = new GridData();
/*  72 */     Label protocol_lab = new Label(gSharing, 0);
/*  73 */     Messages.setLanguageText(protocol_lab, "ConfigView.section.sharing.protocol");
/*  74 */     Utils.setLayoutData(protocol_lab, gridData);
/*     */     
/*  76 */     String[] protocols = { "HTTP", "HTTPS", "UDP", "DHT" };
/*  77 */     String[] descs = { "HTTP", "HTTPS (SSL)", "UDP", "Decentralised" };
/*     */     
/*  79 */     new StringListParameter(gSharing, "Sharing Protocol", "DHT", descs, protocols);
/*     */     
/*     */ 
/*     */ 
/*  83 */     GridData grid_data = new GridData();
/*  84 */     grid_data.horizontalSpan = 2;
/*  85 */     final BooleanParameter private_torrent = new BooleanParameter(gSharing, "Sharing Torrent Private", "ConfigView.section.sharing.privatetorrent");
/*     */     
/*     */ 
/*  88 */     private_torrent.setLayoutData(grid_data);
/*     */     
/*     */ 
/*     */ 
/*  92 */     gridData = new GridData();
/*  93 */     gridData.horizontalSpan = 2;
/*  94 */     final BooleanParameter permit_dht = new BooleanParameter(gSharing, "Sharing Permit DHT", "ConfigView.section.sharing.permitdht");
/*     */     
/*     */ 
/*  97 */     permit_dht.setLayoutData(gridData);
/*     */     
/*  99 */     private_torrent.setAdditionalActionPerformer(new ChangeSelectionActionPerformer(permit_dht.getControls(), true));
/*     */     
/* 101 */     private_torrent.addChangeListener(new ParameterChangeAdapter()
/*     */     {
/*     */ 
/*     */ 
/*     */       public void parameterChanged(Parameter p, boolean caused_internally)
/*     */       {
/*     */ 
/*     */ 
/* 109 */         if (private_torrent.isSelected().booleanValue())
/*     */         {
/* 111 */           permit_dht.setSelected(false);
/*     */         }
/*     */         
/*     */       }
/*     */       
/*     */ 
/* 117 */     });
/* 118 */     gridData = new GridData();
/* 119 */     gridData.horizontalSpan = 2;
/* 120 */     new BooleanParameter(gSharing, "Sharing Add Hashes", "wizard.createtorrent.extrahashes").setLayoutData(gridData);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 125 */     gridData = new GridData();
/* 126 */     gridData.horizontalSpan = 2;
/* 127 */     BooleanParameter rescan_enable = new BooleanParameter(gSharing, "Sharing Rescan Enable", "ConfigView.section.sharing.rescanenable");
/*     */     
/*     */ 
/*     */ 
/* 131 */     rescan_enable.setLayoutData(gridData);
/*     */     
/*     */ 
/* 134 */     gridData = new GridData();
/* 135 */     gridData.horizontalIndent = 25;
/* 136 */     Label period_label = new Label(gSharing, 0);
/* 137 */     Messages.setLanguageText(period_label, "ConfigView.section.sharing.rescanperiod");
/* 138 */     Utils.setLayoutData(period_label, gridData);
/*     */     
/* 140 */     gridData = new GridData();
/* 141 */     IntParameter rescan_period = new IntParameter(gSharing, "Sharing Rescan Period");
/* 142 */     rescan_period.setMinimumValue(1);
/* 143 */     rescan_period.setLayoutData(gridData);
/*     */     
/* 145 */     rescan_enable.setAdditionalActionPerformer(new ChangeSelectionActionPerformer(rescan_period.getControls()));
/* 146 */     rescan_enable.setAdditionalActionPerformer(new ChangeSelectionActionPerformer(new Control[] { period_label }));
/*     */     
/*     */ 
/*     */ 
/* 150 */     Label comment_label = new Label(gSharing, 0);
/* 151 */     Messages.setLanguageText(comment_label, "ConfigView.section.sharing.torrentcomment");
/*     */     
/* 153 */     new Label(gSharing, 0);
/*     */     
/* 155 */     gridData = new GridData(768);
/* 156 */     gridData.horizontalIndent = 25;
/* 157 */     gridData.horizontalSpan = 2;
/* 158 */     StringParameter torrent_comment = new StringParameter(gSharing, "Sharing Torrent Comment", "");
/* 159 */     torrent_comment.setLayoutData(gridData);
/*     */     
/*     */ 
/* 162 */     gridData = new GridData();
/* 163 */     gridData.horizontalSpan = 2;
/* 164 */     BooleanParameter persistent = new BooleanParameter(gSharing, "Sharing Is Persistent", "ConfigView.section.sharing.persistentshares");
/*     */     
/*     */ 
/*     */ 
/* 168 */     persistent.setLayoutData(gridData);
/*     */     
/* 170 */     return gSharing;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/configsections/ConfigSectionSharing.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */