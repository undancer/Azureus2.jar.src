/*     */ package org.gudy.azureus2.ui.swt.views.configsections;
/*     */ 
/*     */ import com.aelitis.azureus.ui.swt.imageloader.ImageLoader;
/*     */ import java.applet.Applet;
/*     */ import java.applet.AudioClip;
/*     */ import java.io.File;
/*     */ import java.net.URI;
/*     */ import org.eclipse.swt.graphics.Image;
/*     */ import org.eclipse.swt.layout.GridData;
/*     */ import org.eclipse.swt.layout.GridLayout;
/*     */ import org.eclipse.swt.widgets.Button;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.Event;
/*     */ import org.eclipse.swt.widgets.FileDialog;
/*     */ import org.eclipse.swt.widgets.Group;
/*     */ import org.eclipse.swt.widgets.Label;
/*     */ import org.eclipse.swt.widgets.Listener;
/*     */ import org.eclipse.swt.widgets.Text;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.core3.util.AEThread2;
/*     */ import org.gudy.azureus2.core3.util.Constants;
/*     */ import org.gudy.azureus2.ui.swt.Messages;
/*     */ import org.gudy.azureus2.ui.swt.Utils;
/*     */ import org.gudy.azureus2.ui.swt.config.BooleanParameter;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ConfigSectionInterfaceAlerts
/*     */   implements UISWTConfigSection
/*     */ {
/*     */   private static final String INTERFACE_PREFIX = "ConfigView.section.interface.";
/*     */   private static final String LBLKEY_PREFIX = "ConfigView.label.";
/*     */   private static final String STYLE_PREFIX = "ConfigView.section.style.";
/*     */   private static final int REQUIRED_MODE = 0;
/*     */   
/*     */   public String configSectionGetParentSection()
/*     */   {
/*  57 */     return "style";
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public String configSectionGetName()
/*     */   {
/*  64 */     return "interface.alerts";
/*     */   }
/*     */   
/*     */   public void configSectionSave() {}
/*     */   
/*     */   public void configSectionDelete()
/*     */   {
/*  71 */     ImageLoader imageLoader = ImageLoader.getInstance();
/*  72 */     imageLoader.releaseImage("openFolderButton");
/*     */   }
/*     */   
/*     */   public int maxUserMode() {
/*  76 */     return 0;
/*     */   }
/*     */   
/*     */   public Composite configSectionCreate(Composite parent) {
/*  80 */     Image imgOpenFolder = null;
/*  81 */     ImageLoader imageLoader = ImageLoader.getInstance();
/*  82 */     imgOpenFolder = imageLoader.getImage("openFolderButton");
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*  87 */     Composite cSection = new Composite(parent, 0);
/*  88 */     GridData gridData = new GridData(272);
/*     */     
/*  90 */     cSection.setLayoutData(gridData);
/*  91 */     GridLayout layout = new GridLayout();
/*  92 */     layout.marginWidth = 0;
/*     */     
/*  94 */     cSection.setLayout(layout);
/*     */     
/*  96 */     Composite cArea = new Composite(cSection, 0);
/*  97 */     layout = new GridLayout();
/*  98 */     layout.marginHeight = 0;
/*  99 */     layout.marginWidth = 0;
/* 100 */     layout.numColumns = 4;
/* 101 */     cArea.setLayout(layout);
/* 102 */     cArea.setLayoutData(new GridData(768));
/*     */     
/*     */ 
/*     */ 
/* 106 */     playSoundWhen(cArea, imgOpenFolder, "Play Download Finished Announcement", "Play Download Finished Announcement Text", "playdownloadspeech", "Play Download Finished", "Play Download Finished File", "playdownloadfinished");
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
/* 117 */     playSoundWhen(cArea, imgOpenFolder, "Play Download Error Announcement", "Play Download Error Announcement Text", "playdownloaderrorspeech", "Play Download Error", "Play Download Error File", "playdownloaderror");
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
/* 129 */     playSoundWhen(cArea, imgOpenFolder, "Play File Finished Announcement", "Play File Finished Announcement Text", "playfilespeech", "Play File Finished", "Play File Finished File", "playfilefinished");
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
/* 141 */     playSoundWhen(cArea, imgOpenFolder, "Play Notification Added Announcement", "Play Notification Added Announcement Text", "playnotificationaddedspeech", "Play Notification Added", "Play Notification Added File", "playnotificationadded");
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
/* 152 */     boolean isAZ3 = COConfigurationManager.getStringParameter("ui").equals("az3");
/*     */     
/* 154 */     if (isAZ3)
/*     */     {
/* 156 */       BooleanParameter p = new BooleanParameter(cArea, "Request Attention On New Download", "ConfigView.label.dl.add.req.attention");
/*     */       
/* 158 */       gridData = new GridData();
/* 159 */       gridData.horizontalSpan = 3;
/* 160 */       p.setLayoutData(gridData);
/*     */     }
/*     */     
/* 163 */     BooleanParameter activate_win = new BooleanParameter(cArea, "Activate Window On External Download", "ConfigView.label.show.win.on.add");
/*     */     
/* 165 */     gridData = new GridData();
/* 166 */     gridData.horizontalSpan = 3;
/* 167 */     activate_win.setLayoutData(gridData);
/*     */     
/* 169 */     BooleanParameter no_auto_activate = new BooleanParameter(cArea, "Reduce Auto Activate Window", "ConfigView.label.reduce.auto.activate");
/*     */     
/* 171 */     gridData = new GridData();
/* 172 */     gridData.horizontalSpan = 3;
/* 173 */     no_auto_activate.setLayoutData(gridData);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 178 */     Group gPopup = new Group(cSection, 0);
/* 179 */     Messages.setLanguageText(gPopup, "label.popups");
/* 180 */     layout = new GridLayout();
/* 181 */     layout.numColumns = 2;
/* 182 */     gPopup.setLayout(layout);
/* 183 */     gPopup.setLayoutData(new GridData(768));
/*     */     
/* 185 */     BooleanParameter popup_dl_added = new BooleanParameter(gPopup, "Popup Download Added", "ConfigView.label.popupdownloadadded");
/*     */     
/* 187 */     gridData = new GridData();
/* 188 */     gridData.horizontalSpan = 2;
/* 189 */     popup_dl_added.setLayoutData(gridData);
/*     */     
/* 191 */     BooleanParameter popup_dl_completed = new BooleanParameter(gPopup, "Popup Download Finished", "ConfigView.label.popupdownloadfinished");
/*     */     
/* 193 */     gridData = new GridData();
/* 194 */     gridData.horizontalSpan = 2;
/* 195 */     popup_dl_completed.setLayoutData(gridData);
/*     */     
/* 197 */     BooleanParameter popup_dl_error = new BooleanParameter(gPopup, "Popup Download Error", "ConfigView.label.popupdownloaderror");
/*     */     
/* 199 */     gridData = new GridData();
/* 200 */     gridData.horizontalSpan = 2;
/* 201 */     popup_dl_error.setLayoutData(gridData);
/*     */     
/* 203 */     BooleanParameter popup_file_completed = new BooleanParameter(gPopup, "Popup File Finished", "ConfigView.label.popupfilefinished");
/*     */     
/* 205 */     gridData = new GridData();
/* 206 */     gridData.horizontalSpan = 2;
/* 207 */     popup_file_completed.setLayoutData(gridData);
/*     */     
/* 209 */     BooleanParameter disable_sliding = new BooleanParameter(gPopup, "GUI_SWT_DisableAlertSliding", "ConfigView.section.style.disableAlertSliding");
/*     */     
/* 211 */     gridData = new GridData();
/* 212 */     gridData.horizontalSpan = 2;
/* 213 */     disable_sliding.setLayoutData(gridData);
/*     */     
/*     */ 
/* 216 */     BooleanParameter show_alert_timestamps = new BooleanParameter(gPopup, "Show Timestamp For Alerts", "ConfigView.label.popup.timestamp");
/*     */     
/* 218 */     gridData = new GridData();
/* 219 */     gridData.horizontalSpan = 2;
/* 220 */     show_alert_timestamps.setLayoutData(gridData);
/*     */     
/*     */ 
/* 223 */     Label label = new Label(gPopup, 64);
/* 224 */     Messages.setLanguageText(label, "ConfigView.label.popup.autohide");
/* 225 */     label.setLayoutData(new GridData());
/* 226 */     IntParameter auto_hide_alert = new IntParameter(gPopup, "Message Popup Autoclose in Seconds", 0, 86400);
/*     */     
/* 228 */     gridData = new GridData();
/* 229 */     gridData.horizontalSpan = 1;
/* 230 */     auto_hide_alert.setLayoutData(gridData);
/*     */     
/* 232 */     return cSection;
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
/*     */ 
/*     */   private void playSoundWhen(final Composite cArea, Image imgOpenFolder, String announceEnableConfig, String announceKeyConfig, String announceResource, String playEnableConfig, String playKeyConfig, String playResource)
/*     */   {
/* 246 */     if (Constants.isOSX)
/*     */     {
/*     */ 
/* 249 */       new BooleanParameter(cArea, announceEnableConfig, "ConfigView.label." + announceResource);
/*     */       
/*     */ 
/* 252 */       StringParameter d_speechParameter = new StringParameter(cArea, announceKeyConfig);
/* 253 */       GridData gridData = new GridData();
/* 254 */       gridData.horizontalSpan = 3;
/* 255 */       gridData.widthHint = 150;
/* 256 */       d_speechParameter.setLayoutData(gridData);
/* 257 */       ((Text)d_speechParameter.getControl()).setTextLimit(40);
/*     */     }
/*     */     
/* 260 */     new BooleanParameter(cArea, playEnableConfig, "ConfigView.label." + playResource);
/*     */     
/*     */ 
/*     */ 
/* 264 */     GridData gridData = new GridData(768);
/*     */     
/* 266 */     final StringParameter e_pathParameter = new StringParameter(cArea, playKeyConfig, "");
/*     */     
/* 268 */     if (e_pathParameter.getValue().length() == 0)
/*     */     {
/* 270 */       e_pathParameter.setValue("<default>");
/*     */     }
/*     */     
/* 273 */     e_pathParameter.setLayoutData(gridData);
/*     */     
/* 275 */     Button d_browse = new Button(cArea, 8);
/*     */     
/* 277 */     d_browse.setImage(imgOpenFolder);
/*     */     
/* 279 */     imgOpenFolder.setBackground(d_browse.getBackground());
/*     */     
/* 281 */     d_browse.setToolTipText(MessageText.getString("ConfigView.button.browse"));
/*     */     
/* 283 */     d_browse.addListener(13, new Listener() {
/*     */       public void handleEvent(Event event) {
/* 285 */         FileDialog dialog = new FileDialog(cArea.getShell(), 65536);
/*     */         
/* 287 */         dialog.setFilterExtensions(new String[] { "*.wav" });
/*     */         
/*     */ 
/* 290 */         dialog.setFilterNames(new String[] { "*.wav" });
/*     */         
/*     */ 
/*     */ 
/* 294 */         dialog.setText(MessageText.getString("ConfigView.section.interface.wavlocation"));
/*     */         
/* 296 */         final String path = dialog.open();
/*     */         
/* 298 */         if (path != null)
/*     */         {
/* 300 */           e_pathParameter.setValue(path);
/*     */           
/* 302 */           new AEThread2("SoundTest") {
/*     */             public void run() {
/*     */               try {
/* 305 */                 Applet.newAudioClip(new File(path).toURI().toURL()).play();
/*     */                 
/* 307 */                 Thread.sleep(2500L);
/*     */ 
/*     */               }
/*     */               catch (Throwable e) {}
/*     */             }
/*     */             
/*     */ 
/*     */           }.start();
/*     */         }
/*     */       }
/* 317 */     });
/* 318 */     Label d_sound_info = new Label(cArea, 64);
/* 319 */     Messages.setLanguageText(d_sound_info, "ConfigView.section.interface.wavlocation.info");
/*     */     
/* 321 */     gridData = new GridData(768);
/* 322 */     gridData.widthHint = 100;
/* 323 */     Utils.setLayoutData(d_sound_info, gridData);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/configsections/ConfigSectionInterfaceAlerts.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */