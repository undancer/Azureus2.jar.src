/*     */ package org.gudy.azureus2.ui.swt.views.configsections;
/*     */ 
/*     */ import com.aelitis.azureus.ui.UserPrompterResultListener;
/*     */ import com.aelitis.azureus.ui.common.RememberedDecisionsManager;
/*     */ import org.eclipse.swt.events.MouseAdapter;
/*     */ import org.eclipse.swt.events.MouseEvent;
/*     */ import org.eclipse.swt.layout.GridData;
/*     */ import org.eclipse.swt.layout.GridLayout;
/*     */ import org.eclipse.swt.layout.RowLayout;
/*     */ import org.eclipse.swt.widgets.Button;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.Control;
/*     */ import org.eclipse.swt.widgets.Display;
/*     */ import org.eclipse.swt.widgets.Event;
/*     */ import org.eclipse.swt.widgets.Group;
/*     */ import org.eclipse.swt.widgets.Label;
/*     */ import org.eclipse.swt.widgets.Listener;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.ui.swt.Messages;
/*     */ import org.gudy.azureus2.ui.swt.Utils;
/*     */ import org.gudy.azureus2.ui.swt.mainwindow.ClipboardCopy;
/*     */ import org.gudy.azureus2.ui.swt.mainwindow.Colors;
/*     */ import org.gudy.azureus2.ui.swt.plugins.UISWTConfigSection;
/*     */ import org.gudy.azureus2.ui.swt.shells.MessageBoxShell;
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
/*     */ public class ConfigSectionMode
/*     */   implements UISWTConfigSection
/*     */ {
/*     */   public String configSectionGetParentSection()
/*     */   {
/*  56 */     return "root";
/*     */   }
/*     */   
/*     */   public String configSectionGetName() {
/*  60 */     return "mode";
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Composite configSectionCreate(final Composite parent)
/*     */   {
/*  87 */     final String[] links = { "http://wiki.vuze.com/w/Mode#Beginner", "http://wiki.vuze.com/w/Mode#Intermediate", "http://wiki.vuze.com/w/Mode#Advanced" };
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  93 */     int userMode = COConfigurationManager.getIntParameter("User Mode");
/*     */     
/*  95 */     Composite cMode = new Composite(parent, 64);
/*  96 */     GridData gridData = new GridData(272);
/*  97 */     cMode.setLayoutData(gridData);
/*  98 */     GridLayout layout = new GridLayout();
/*  99 */     layout.numColumns = 4;
/* 100 */     layout.marginHeight = 0;
/* 101 */     cMode.setLayout(layout);
/*     */     
/* 103 */     gridData = new GridData();
/* 104 */     gridData.horizontalSpan = 4;
/* 105 */     final Group gRadio = new Group(cMode, 64);
/* 106 */     Messages.setLanguageText(gRadio, "ConfigView.section.mode.title");
/* 107 */     gRadio.setLayoutData(gridData);
/* 108 */     Utils.setLayout(gRadio, new RowLayout(256));
/*     */     
/* 110 */     Button button0 = new Button(gRadio, 16);
/* 111 */     Messages.setLanguageText(button0, "ConfigView.section.mode.beginner");
/* 112 */     button0.setData("iMode", "0");
/* 113 */     button0.setData("sMode", "beginner.text");
/*     */     
/* 115 */     Button button1 = new Button(gRadio, 16);
/* 116 */     Messages.setLanguageText(button1, "ConfigView.section.mode.intermediate");
/* 117 */     button1.setData("iMode", "1");
/* 118 */     button1.setData("sMode", "intermediate.text");
/*     */     
/* 120 */     Button button2 = new Button(gRadio, 16);
/* 121 */     Messages.setLanguageText(button2, "ConfigView.section.mode.advanced");
/* 122 */     button2.setData("iMode", "2");
/* 123 */     button2.setData("sMode", "advanced.text");
/*     */     
/* 125 */     final Button[] selected_button = { null };
/*     */     
/* 127 */     if (userMode == 0) {
/* 128 */       selected_button[0] = button0;
/* 129 */       button0.setSelection(true);
/* 130 */     } else if (userMode == 1) {
/* 131 */       selected_button[0] = button1;
/* 132 */       button1.setSelection(true);
/*     */     } else {
/* 134 */       selected_button[0] = button2;
/* 135 */       button2.setSelection(true);
/*     */     }
/*     */     
/* 138 */     gridData = new GridData(768);
/* 139 */     final Label label = new Label(cMode, 64);
/* 140 */     gridData.horizontalSpan = 4;
/* 141 */     gridData.horizontalIndent = 10;
/* 142 */     Utils.setLayoutData(label, gridData);
/*     */     
/*     */ 
/* 145 */     final Label linkLabel = new Label(cMode, 0);
/* 146 */     linkLabel.setText(MessageText.getString("ConfigView.label.please.visit.here"));
/* 147 */     linkLabel.setData(links[userMode]);
/* 148 */     linkLabel.setCursor(linkLabel.getDisplay().getSystemCursor(21));
/* 149 */     linkLabel.setForeground(Colors.blue);
/* 150 */     gridData = new GridData(768);
/*     */     
/* 152 */     Utils.setLayoutData(linkLabel, gridData);
/* 153 */     linkLabel.addMouseListener(new MouseAdapter() {
/*     */       public void mouseDoubleClick(MouseEvent arg0) {
/* 155 */         Utils.launch((String)((Label)arg0.widget).getData());
/*     */       }
/*     */       
/* 158 */       public void mouseUp(MouseEvent arg0) { Utils.launch((String)((Label)arg0.widget).getData());
/*     */       }
/* 160 */     });
/* 161 */     gridData = new GridData(768);
/* 162 */     gridData.horizontalSpan = 4;
/* 163 */     Utils.setLayoutData(linkLabel, gridData);
/* 164 */     ClipboardCopy.addCopyToClipMenu(linkLabel);
/*     */     
/* 166 */     final Runnable setModeText = new Runnable()
/*     */     {
/*     */ 
/*     */       public void run()
/*     */       {
/*     */ 
/* 172 */         String key = "ConfigView.section.mode." + selected_button[0].getData("sMode");
/*     */         
/* 174 */         if (MessageText.keyExists(key + "1")) {
/* 175 */           key = key + "1";
/*     */         }
/*     */         
/* 178 */         label.setText("-> " + MessageText.getString(key));
/*     */       }
/*     */       
/*     */ 
/* 182 */     };
/* 183 */     setModeText.run();
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
/* 206 */     Listener radioGroup = new Listener()
/*     */     {
/*     */       public void handleEvent(Event event) {
/* 209 */         Control[] children = gRadio.getChildren();
/*     */         
/* 211 */         for (int j = 0; j < children.length; j++) {
/* 212 */           Control child = children[j];
/* 213 */           if ((child instanceof Button)) {
/* 214 */             Button button = (Button)child;
/* 215 */             if ((button.getStyle() & 0x10) != 0) { button.setSelection(false);
/*     */             }
/*     */           }
/*     */         }
/* 219 */         Button button = (Button)event.widget;
/* 220 */         button.setSelection(true);
/* 221 */         int mode = Integer.parseInt((String)button.getData("iMode"));
/* 222 */         selected_button[0] = button;
/* 223 */         setModeText.run();
/*     */         
/*     */ 
/* 226 */         linkLabel.setData(links[mode]);
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 236 */         COConfigurationManager.setParameter("User Mode", Integer.parseInt((String)button.getData("iMode")));
/*     */       }
/*     */       
/* 239 */     };
/* 240 */     button0.addListener(13, radioGroup);
/* 241 */     button1.addListener(13, radioGroup);
/* 242 */     button2.addListener(13, radioGroup);
/*     */     
/* 244 */     Label padding = new Label(cMode, 0);
/* 245 */     gridData = new GridData();
/* 246 */     gridData.horizontalSpan = 3;
/* 247 */     Utils.setLayoutData(padding, gridData);
/*     */     
/*     */ 
/*     */ 
/* 251 */     Label blank = new Label(cMode, 0);
/* 252 */     gridData = new GridData(768);
/* 253 */     gridData.horizontalSpan = 4;
/* 254 */     Utils.setLayoutData(blank, gridData);
/*     */     
/*     */ 
/* 257 */     Composite gReset = new Composite(cMode, 64);
/* 258 */     gridData = new GridData();
/* 259 */     gridData.horizontalSpan = 4;
/* 260 */     Utils.setLayoutData(gReset, gridData);
/* 261 */     layout = new GridLayout();
/* 262 */     layout.numColumns = 3;
/* 263 */     layout.marginWidth = 0;
/* 264 */     gReset.setLayout(layout);
/*     */     
/* 266 */     Label reset_label = new Label(gReset, 0);
/* 267 */     Messages.setLanguageText(reset_label, "ConfigView.section.mode.resetdefaults");
/*     */     
/* 269 */     Button reset_button = new Button(gReset, 8);
/*     */     
/* 271 */     Messages.setLanguageText(reset_button, "Button.reset");
/*     */     
/* 273 */     reset_button.addListener(13, new Listener()
/*     */     {
/*     */ 
/*     */       public void handleEvent(Event event)
/*     */       {
/*     */ 
/* 279 */         MessageBoxShell mb = new MessageBoxShell(296, MessageText.getString("resetconfig.warn.title"), MessageText.getString("resetconfig.warn"));
/*     */         
/*     */ 
/*     */ 
/*     */ 
/* 284 */         mb.setDefaultButtonUsingStyle(256);
/*     */         
/* 286 */         mb.setParent(parent.getShell());
/*     */         
/* 288 */         mb.open(new UserPrompterResultListener()
/*     */         {
/*     */ 
/*     */ 
/*     */           public void prompterClosed(int returnVal)
/*     */           {
/*     */ 
/* 295 */             if (returnVal != 32) {
/* 296 */               return;
/*     */             }
/*     */             
/* 299 */             RememberedDecisionsManager.ensureLoaded();
/*     */             
/* 301 */             COConfigurationManager.resetToDefaults();
/*     */           }
/*     */           
/*     */         });
/*     */       }
/* 306 */     });
/* 307 */     padding = new Label(gReset, 0);
/* 308 */     gridData = new GridData();
/* 309 */     Utils.setLayoutData(padding, gridData);
/*     */     
/* 311 */     return cMode;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/configsections/ConfigSectionMode.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */