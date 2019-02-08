/*     */ package org.gudy.azureus2.ui.swt.views.configsections;
/*     */ 
/*     */ import com.aelitis.azureus.ui.swt.imageloader.ImageLoader;
/*     */ import org.eclipse.swt.graphics.Image;
/*     */ import org.eclipse.swt.layout.GridData;
/*     */ import org.eclipse.swt.layout.GridLayout;
/*     */ import org.eclipse.swt.widgets.Button;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.Control;
/*     */ import org.eclipse.swt.widgets.DirectoryDialog;
/*     */ import org.eclipse.swt.widgets.Event;
/*     */ import org.eclipse.swt.widgets.Label;
/*     */ import org.eclipse.swt.widgets.Listener;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.core3.util.Constants;
/*     */ import org.gudy.azureus2.ui.swt.Messages;
/*     */ import org.gudy.azureus2.ui.swt.Utils;
/*     */ import org.gudy.azureus2.ui.swt.config.BooleanParameter;
/*     */ import org.gudy.azureus2.ui.swt.config.ChangeSelectionActionPerformer;
/*     */ import org.gudy.azureus2.ui.swt.config.IAdditionalActionPerformer;
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
/*     */ public class ConfigSectionFileMove
/*     */   implements UISWTConfigSection
/*     */ {
/*     */   private Image imgOpenFolder;
/*     */   
/*     */   public String configSectionGetParentSection()
/*     */   {
/*  47 */     return "files";
/*     */   }
/*     */   
/*     */   public String configSectionGetName() {
/*  51 */     return "files.move";
/*     */   }
/*     */   
/*     */   public void configSectionSave() {}
/*     */   
/*     */   public void configSectionDelete()
/*     */   {
/*  58 */     ImageLoader imageLoader = ImageLoader.getInstance();
/*  59 */     imageLoader.releaseImage("openFolderButton");
/*     */   }
/*     */   
/*     */   public int maxUserMode() {
/*  63 */     return 1;
/*     */   }
/*     */   
/*     */ 
/*     */   public Composite configSectionCreate(Composite parent)
/*     */   {
/*  69 */     int userMode = COConfigurationManager.getIntParameter("User Mode");
/*     */     
/*  71 */     Composite gFile = new Composite(parent, 0);
/*     */     
/*  73 */     GridLayout layout = new GridLayout();
/*  74 */     layout.numColumns = 2;
/*  75 */     layout.marginHeight = 0;
/*  76 */     gFile.setLayout(layout);
/*     */     
/*  78 */     ImageLoader imageLoader = ImageLoader.getInstance();
/*  79 */     this.imgOpenFolder = imageLoader.getImage("openFolderButton");
/*     */     
/*     */ 
/*  82 */     createMoveOnEventGrouping(gFile, "ConfigView.label.movecompleted", "Move Completed When Done", "Completed Files Directory", "Move Torrent When Done", "Move Torrent When Done Directory", "Move Only When In Default Save Dir", null);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  90 */     createMoveOnEventGrouping(gFile, "ConfigView.label.moveremoved", "File.move.download.removed.enabled", "File.move.download.removed.path", "File.move.download.removed.move_torrent", "File.move.download.removed.move_torrent_path", "File.move.download.removed.only_in_default", "File.move.download.removed.move_partial");
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  99 */     if (userMode > 0)
/*     */     {
/*     */ 
/* 102 */       BooleanParameter copyDontMove = new BooleanParameter(gFile, "Copy And Delete Data Rather Than Move", "ConfigView.label.copyanddeleteratherthanmove");
/*     */       
/*     */ 
/* 105 */       GridData gridData = new GridData();
/* 106 */       gridData.horizontalSpan = 2;
/* 107 */       copyDontMove.setLayoutData(gridData);
/*     */       
/* 109 */       if (Constants.isWindows)
/*     */       {
/* 111 */         BooleanParameter moveIfSameDrive = new BooleanParameter(gFile, "Move If On Same Drive", "ConfigView.label.moveifsamedrive");
/*     */         
/*     */ 
/* 114 */         gridData = new GridData();
/* 115 */         gridData.horizontalSpan = 2;
/* 116 */         gridData.horizontalIndent = 25;
/* 117 */         moveIfSameDrive.setLayoutData(gridData);
/*     */         
/* 119 */         IAdditionalActionPerformer derp = new ChangeSelectionActionPerformer(moveIfSameDrive);
/*     */         
/* 121 */         copyDontMove.setAdditionalActionPerformer(derp);
/*     */       }
/*     */     }
/*     */     
/* 125 */     BooleanParameter subdirIsDefault = new BooleanParameter(gFile, "File.move.subdir_is_default", "ConfigView.label.subdir_is_in_default");
/*     */     
/* 127 */     GridData gridData = new GridData();
/* 128 */     gridData.horizontalSpan = 2;
/* 129 */     subdirIsDefault.setLayoutData(gridData);
/*     */     
/* 131 */     return gFile;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private void createMoveOnEventGrouping(final Composite gFile, String enable_section_label, String move_when_done_setting, String move_path_setting, String move_torrent_setting, String move_torrent_dir_setting, String move_when_in_save_dir_setting, String move_partial_downloads_setting)
/*     */   {
/* 144 */     BooleanParameter moveCompleted = new BooleanParameter(gFile, move_when_done_setting, enable_section_label);
/*     */     
/* 146 */     GridData gridData = new GridData();
/* 147 */     GridLayout layout = null;
/* 148 */     gridData.horizontalSpan = 2;
/* 149 */     moveCompleted.setLayoutData(gridData);
/*     */     
/* 151 */     Composite gMoveCompleted = new Composite(gFile, 0);
/* 152 */     gridData = new GridData(768);
/* 153 */     gridData.horizontalIndent = 25;
/* 154 */     gridData.horizontalSpan = 2;
/* 155 */     Utils.setLayoutData(gMoveCompleted, gridData);
/* 156 */     layout = new GridLayout();
/* 157 */     layout.marginHeight = 0;
/* 158 */     layout.marginWidth = 4;
/* 159 */     layout.numColumns = 4;
/* 160 */     gMoveCompleted.setLayout(layout);
/*     */     
/*     */ 
/*     */ 
/* 164 */     Label lDir = new Label(gMoveCompleted, 0);
/* 165 */     Messages.setLanguageText(lDir, "ConfigView.label.directory");
/*     */     
/* 167 */     gridData = new GridData(768);
/* 168 */     final StringParameter movePath = new StringParameter(gMoveCompleted, move_path_setting);
/* 169 */     gridData.horizontalSpan = 2;
/* 170 */     movePath.setLayoutData(gridData);
/*     */     
/* 172 */     Button browse3 = new Button(gMoveCompleted, 8);
/* 173 */     browse3.setImage(this.imgOpenFolder);
/* 174 */     this.imgOpenFolder.setBackground(browse3.getBackground());
/* 175 */     browse3.setToolTipText(MessageText.getString("ConfigView.button.browse"));
/*     */     
/* 177 */     browse3.addListener(13, new Listener() {
/*     */       public void handleEvent(Event event) {
/* 179 */         DirectoryDialog dialog = new DirectoryDialog(gFile.getShell(), 65536);
/*     */         
/* 181 */         dialog.setFilterPath(movePath.getValue());
/* 182 */         dialog.setText(MessageText.getString("ConfigView.dialog.choosemovepath"));
/* 183 */         String path = dialog.open();
/* 184 */         if (path != null) {
/* 185 */           movePath.setValue(path);
/*     */         }
/*     */         
/*     */       }
/*     */       
/*     */ 
/* 191 */     });
/* 192 */     BooleanParameter moveTorrent = new BooleanParameter(gMoveCompleted, move_torrent_setting, "ConfigView.label.movetorrent");
/*     */     
/* 194 */     gridData = new GridData();
/* 195 */     gridData.horizontalSpan = 4;
/* 196 */     moveTorrent.setLayoutData(gridData);
/*     */     
/*     */ 
/*     */ 
/* 200 */     Composite cTorrentDir = new Composite(gMoveCompleted, 0);
/* 201 */     gridData = new GridData(768);
/* 202 */     gridData.horizontalSpan = 4;
/* 203 */     cTorrentDir.setLayoutData(gridData);
/* 204 */     layout = new GridLayout();
/* 205 */     layout.marginHeight = 0;
/* 206 */     layout.marginWidth = 0;
/* 207 */     layout.numColumns = 3;
/* 208 */     cTorrentDir.setLayout(layout);
/*     */     
/* 210 */     Label lTorrentDir = new Label(cTorrentDir, 0);
/* 211 */     Messages.setLanguageText(lTorrentDir, "ConfigView.label.directory.if.different");
/* 212 */     gridData = new GridData();
/* 213 */     gridData.horizontalIndent = 25;
/* 214 */     Utils.setLayoutData(lTorrentDir, gridData);
/*     */     
/* 216 */     gridData = new GridData(768);
/* 217 */     final StringParameter moveTorrentPath = new StringParameter(cTorrentDir, move_torrent_dir_setting);
/* 218 */     moveTorrentPath.setLayoutData(gridData);
/*     */     
/* 220 */     Button browse4 = new Button(cTorrentDir, 8);
/* 221 */     browse4.setImage(this.imgOpenFolder);
/* 222 */     this.imgOpenFolder.setBackground(browse4.getBackground());
/* 223 */     browse4.setToolTipText(MessageText.getString("ConfigView.button.browse"));
/*     */     
/* 225 */     browse4.addListener(13, new Listener() {
/*     */       public void handleEvent(Event event) {
/* 227 */         DirectoryDialog dialog = new DirectoryDialog(gFile.getShell(), 65536);
/*     */         
/* 229 */         dialog.setFilterPath(moveTorrentPath.getValue());
/* 230 */         dialog.setText(MessageText.getString("ConfigView.dialog.choosemovepath"));
/* 231 */         String path = dialog.open();
/* 232 */         if (path != null) {
/* 233 */           moveTorrentPath.setValue(path);
/*     */         }
/*     */         
/*     */       }
/* 237 */     });
/* 238 */     final IAdditionalActionPerformer grayPathAndButton3 = new ChangeSelectionActionPerformer(new Control[] { moveTorrentPath.getControl(), browse4 });
/*     */     
/*     */ 
/* 241 */     moveTorrent.setAdditionalActionPerformer(grayPathAndButton3);
/*     */     
/*     */ 
/*     */ 
/* 245 */     BooleanParameter moveOnly = new BooleanParameter(gMoveCompleted, move_when_in_save_dir_setting, "ConfigView.label.moveonlyusingdefaultsave");
/*     */     
/* 247 */     gridData = new GridData();
/* 248 */     gridData.horizontalSpan = 3;
/* 249 */     moveOnly.setLayoutData(gridData);
/*     */     
/*     */ 
/*     */ 
/* 253 */     if (move_partial_downloads_setting != null) {
/* 254 */       BooleanParameter movePartial = new BooleanParameter(gMoveCompleted, move_partial_downloads_setting, "ConfigView.label.movepartialdownloads");
/*     */       
/* 256 */       gridData = new GridData();
/* 257 */       gridData.horizontalSpan = 3;
/* 258 */       movePartial.setLayoutData(gridData);
/*     */     }
/*     */     
/* 261 */     Control[] controls3 = { gMoveCompleted };
/*     */     
/* 263 */     IAdditionalActionPerformer grayPathAndButton2 = new ChangeSelectionActionPerformer(controls3);
/*     */     
/*     */ 
/* 266 */     moveCompleted.setAdditionalActionPerformer(grayPathAndButton2);
/* 267 */     moveCompleted.setAdditionalActionPerformer(new ChangeSelectionActionPerformer(new Control[0])
/*     */     {
/*     */       public void performAction()
/*     */       {
/* 271 */         grayPathAndButton3.performAction();
/*     */       }
/*     */     });
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/configsections/ConfigSectionFileMove.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */