/*     */ package org.gudy.azureus2.ui.swt.config.wizard;
/*     */ 
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
/*     */ public class WelcomePanel
/*     */   extends AbstractWizardPanel
/*     */ {
/*     */   public WelcomePanel(ConfigureWizard wizard, IWizardPanel previous)
/*     */   {
/*  49 */     super(wizard, previous);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void show()
/*     */   {
/*  56 */     this.wizard.setTitle(MessageText.getString("configureWizard.welcome.title"));
/*     */     
/*  58 */     Display display = this.wizard.getDisplay();
/*     */     
/*  60 */     String initsMode = "";
/*  61 */     final String[] text = { "" };
/*  62 */     int userMode = COConfigurationManager.getIntParameter("User Mode");
/*  63 */     final String[] messTexts = { "ConfigView.section.mode.beginner.wiki.definitions", "ConfigView.section.mode.intermediate.wiki.host", "ConfigView.section.mode.advanced.wiki.main", "ConfigView.section.mode.intermediate.wiki.publish" };
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*  68 */     final String[] links = { "http://wiki.vuze.com/w/This_funny_word", "http://wiki.vuze.com/w/HostingFiles", "http://wiki.vuze.com/w/Main_Page", "http://wiki.vuze.com/w/PublishingFiles" };
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  74 */     Composite rootPanel = this.wizard.getPanel();
/*  75 */     GridLayout layout = new GridLayout();
/*  76 */     layout.numColumns = 1;
/*  77 */     rootPanel.setLayout(layout);
/*     */     
/*  79 */     Composite panel = new Composite(rootPanel, 0);
/*  80 */     GridData gridData = new GridData(1808);
/*  81 */     Utils.setLayoutData(panel, gridData);
/*  82 */     layout = new GridLayout();
/*  83 */     layout.numColumns = 1;
/*  84 */     panel.setLayout(layout);
/*     */     
/*  86 */     Label label0 = new Label(panel, 64);
/*  87 */     gridData = new GridData(768);
/*  88 */     label0.setLayoutData(gridData);
/*  89 */     Messages.setLanguageText(label0, "configureWizard.welcome.message");
/*     */     
/*  91 */     label0 = new Label(panel, 0);
/*     */     
/*  93 */     Label label1 = new Label(panel, 64);
/*  94 */     gridData = new GridData(768);
/*  95 */     label1.setLayoutData(gridData);
/*  96 */     Messages.setLanguageText(label1, "configureWizard.welcome.usermodes");
/*     */     
/*     */ 
/*  99 */     gridData = new GridData();
/* 100 */     final Group gRadio = new Group(panel, 64);
/* 101 */     Messages.setLanguageText(gRadio, "ConfigView.section.mode.title");
/* 102 */     Utils.setLayoutData(gRadio, gridData);
/* 103 */     Utils.setLayout(gRadio, new RowLayout(256));
/*     */     
/* 105 */     Button button0 = new Button(gRadio, 16);
/* 106 */     Messages.setLanguageText(button0, "ConfigView.section.mode.beginner");
/* 107 */     button0.setData("iMode", "0");
/* 108 */     button0.setData("sMode", "beginner.text");
/*     */     
/* 110 */     Button button1 = new Button(gRadio, 16);
/* 111 */     Messages.setLanguageText(button1, "ConfigView.section.mode.intermediate");
/* 112 */     button1.setData("iMode", "1");
/* 113 */     button1.setData("sMode", "intermediate.text");
/*     */     
/* 115 */     Button button2 = new Button(gRadio, 16);
/* 116 */     Messages.setLanguageText(button2, "ConfigView.section.mode.advanced");
/* 117 */     button2.setData("iMode", "2");
/* 118 */     button2.setData("sMode", "advanced.text");
/*     */     
/* 120 */     if (userMode == 0) {
/* 121 */       initsMode = "beginner.text";
/* 122 */       button0.setSelection(true);
/* 123 */     } else if (userMode == 1) {
/* 124 */       initsMode = "intermediate.text";
/* 125 */       button1.setSelection(true);
/*     */     } else {
/* 127 */       initsMode = "advanced.text";
/* 128 */       button2.setSelection(true);
/*     */     }
/*     */     
/* 131 */     final Label labl = new Label(panel, 64);
/* 132 */     gridData = new GridData(1808);
/* 133 */     gridData.widthHint = 380;
/* 134 */     gridData.heightHint = 50;
/* 135 */     Utils.setLayoutData(labl, gridData);
/* 136 */     text[0] = MessageText.getString("ConfigView.section.mode." + initsMode);
/* 137 */     labl.setText(text[0]);
/* 138 */     labl.addListener(13, new Listener() {
/*     */       public void handleEvent(Event event) {
/* 140 */         Utils.launch(event.text);
/*     */       }
/*     */       
/* 143 */     });
/* 144 */     Group gWiki = new Group(panel, 64);
/* 145 */     gridData = new GridData();
/* 146 */     gridData.widthHint = 350;
/* 147 */     Utils.setLayoutData(gWiki, gridData);
/* 148 */     layout = new GridLayout();
/* 149 */     layout.numColumns = 1;
/* 150 */     layout.marginHeight = 1;
/* 151 */     gWiki.setLayout(layout);
/*     */     
/* 153 */     gWiki.setText(MessageText.getString("Utils.link.visit"));
/*     */     
/* 155 */     final Label linkLabel = new Label(gWiki, 0);
/* 156 */     linkLabel.setText(MessageText.getString(messTexts[userMode]));
/* 157 */     linkLabel.setData(links[userMode]);
/* 158 */     linkLabel.setCursor(display.getSystemCursor(21));
/* 159 */     linkLabel.setForeground(Colors.blue);
/* 160 */     gridData = new GridData(768);
/* 161 */     gridData.horizontalIndent = 10;
/* 162 */     Utils.setLayoutData(linkLabel, gridData);
/* 163 */     linkLabel.addMouseListener(new MouseAdapter() {
/*     */       public void mouseDoubleClick(MouseEvent arg0) {
/* 165 */         Utils.launch((String)((Label)arg0.widget).getData());
/*     */       }
/*     */       
/* 168 */       public void mouseUp(MouseEvent arg0) { Utils.launch((String)((Label)arg0.widget).getData());
/*     */       }
/* 170 */     });
/* 171 */     ClipboardCopy.addCopyToClipMenu(linkLabel);
/*     */     
/* 173 */     final Label linkLabel1 = new Label(gWiki, 0);
/* 174 */     linkLabel1.setText(userMode == 1 ? MessageText.getString(messTexts[3]) : "");
/* 175 */     linkLabel1.setData(links[3]);
/* 176 */     linkLabel1.setCursor(display.getSystemCursor(21));
/* 177 */     linkLabel1.setForeground(Colors.blue);
/* 178 */     gridData = new GridData(768);
/* 179 */     gridData.horizontalIndent = 10;
/* 180 */     linkLabel1.setLayoutData(gridData);
/* 181 */     linkLabel1.addMouseListener(new MouseAdapter() {
/*     */       public void mouseDoubleClick(MouseEvent arg0) {
/* 183 */         Utils.launch((String)((Label)arg0.widget).getData());
/*     */       }
/*     */       
/* 186 */       public void mouseUp(MouseEvent arg0) { Utils.launch((String)((Label)arg0.widget).getData());
/*     */       }
/* 188 */     });
/* 189 */     ClipboardCopy.addCopyToClipMenu(linkLabel1);
/*     */     
/*     */ 
/* 192 */     Listener radioGroup = new Listener()
/*     */     {
/*     */       public void handleEvent(Event event) {
/* 195 */         Control[] children = gRadio.getChildren();
/*     */         
/* 197 */         for (int j = 0; j < children.length; j++) {
/* 198 */           Control child = children[j];
/* 199 */           if ((child instanceof Button)) {
/* 200 */             Button button = (Button)child;
/* 201 */             if ((button.getStyle() & 0x10) != 0) { button.setSelection(false);
/*     */             }
/*     */           }
/*     */         }
/* 205 */         Button button = (Button)event.widget;
/* 206 */         button.setSelection(true);
/* 207 */         int mode = Integer.parseInt((String)button.getData("iMode"));
/* 208 */         text[0] = MessageText.getString("ConfigView.section.mode." + (String)button.getData("sMode"));
/* 209 */         labl.setText(text[0]);
/*     */         
/* 211 */         linkLabel.setText(MessageText.getString(messTexts[mode]));
/* 212 */         linkLabel.setData(links[mode]);
/* 213 */         if (mode == 1) {
/* 214 */           linkLabel1.setText(MessageText.getString(messTexts[3]));
/* 215 */           linkLabel1.setData(links[3]);
/*     */         } else {
/* 217 */           linkLabel1.setText("");
/* 218 */           linkLabel1.setData("");
/*     */         }
/* 220 */         COConfigurationManager.setParameter("User Mode", Integer.parseInt((String)button.getData("iMode")));
/*     */       }
/*     */       
/* 223 */     };
/* 224 */     button0.addListener(13, radioGroup);
/* 225 */     button1.addListener(13, radioGroup);
/* 226 */     button2.addListener(13, radioGroup);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean isNextEnabled()
/*     */   {
/* 235 */     return true;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public IWizardPanel getNextPanel()
/*     */   {
/* 242 */     return new TransferPanel2((ConfigureWizard)this.wizard, this);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/config/wizard/WelcomePanel.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */