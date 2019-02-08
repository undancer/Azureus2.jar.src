/*     */ package org.gudy.azureus2.ui.swt.maketorrent;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.Set;
/*     */ import org.eclipse.swt.layout.GridData;
/*     */ import org.eclipse.swt.layout.GridLayout;
/*     */ import org.eclipse.swt.widgets.Button;
/*     */ import org.eclipse.swt.widgets.Combo;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.Event;
/*     */ import org.eclipse.swt.widgets.Label;
/*     */ import org.eclipse.swt.widgets.Listener;
/*     */ import org.eclipse.swt.widgets.Tree;
/*     */ import org.eclipse.swt.widgets.TreeItem;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.core3.util.TrackersUtil;
/*     */ import org.gudy.azureus2.ui.swt.Messages;
/*     */ import org.gudy.azureus2.ui.swt.Utils;
/*     */ import org.gudy.azureus2.ui.swt.wizard.AbstractWizardPanel;
/*     */ import org.gudy.azureus2.ui.swt.wizard.IWizardPanel;
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
/*     */ public class WebSeedPanel
/*     */   extends AbstractWizardPanel<NewTorrentWizard>
/*     */   implements WebSeedsEditorListener
/*     */ {
/*     */   private Combo configList;
/*     */   private Tree configDetails;
/*     */   private Button btnNew;
/*     */   private Button btnEdit;
/*     */   private Button btnDelete;
/*     */   
/*     */   public WebSeedPanel(NewTorrentWizard wizard, AbstractWizardPanel<NewTorrentWizard> previous)
/*     */   {
/*  61 */     super(wizard, previous);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void show()
/*     */   {
/*  70 */     ((NewTorrentWizard)this.wizard).setTitle(MessageText.getString("wizard.webseed.title"));
/*  71 */     ((NewTorrentWizard)this.wizard).setCurrentInfo("");
/*  72 */     Composite rootPanel = ((NewTorrentWizard)this.wizard).getPanel();
/*  73 */     GridLayout layout = new GridLayout();
/*  74 */     layout.numColumns = 1;
/*  75 */     rootPanel.setLayout(layout);
/*     */     
/*  77 */     Composite panel = new Composite(rootPanel, 0);
/*  78 */     GridData gridData = new GridData(772);
/*  79 */     Utils.setLayoutData(panel, gridData);
/*  80 */     layout = new GridLayout();
/*  81 */     layout.numColumns = 3;
/*  82 */     panel.setLayout(layout);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*  87 */     Label labelTitle = new Label(panel, 0);
/*  88 */     Messages.setLanguageText(labelTitle, "wizard.webseed.configuration");
/*  89 */     gridData = new GridData();
/*  90 */     gridData.horizontalSpan = 3;
/*  91 */     Utils.setLayoutData(labelTitle, gridData);
/*     */     
/*  93 */     this.configList = new Combo(panel, 8);
/*  94 */     gridData = new GridData(768);
/*  95 */     gridData.horizontalSpan = 3;
/*  96 */     Utils.setLayoutData(this.configList, gridData);
/*  97 */     this.configList.addListener(13, new Listener() {
/*     */       public void handleEvent(Event e) {
/*  99 */         WebSeedPanel.this.updateWebSeeds();
/* 100 */         WebSeedPanel.this.refreshDetails();
/*     */       }
/*     */       
/* 103 */     });
/* 104 */     this.btnNew = new Button(panel, 8);
/* 105 */     Messages.setLanguageText(this.btnNew, "wizard.multitracker.new");
/* 106 */     gridData = new GridData();
/* 107 */     gridData.widthHint = 100;
/* 108 */     Utils.setLayoutData(this.btnNew, gridData);
/* 109 */     this.btnNew.addListener(13, new Listener() {
/*     */       public void handleEvent(Event e) {
/* 111 */         Map webseeds = new HashMap();
/* 112 */         webseeds.put("getright", new ArrayList());
/* 113 */         webseeds.put("webseed", new ArrayList());
/* 114 */         new WebSeedsEditor(null, webseeds, WebSeedPanel.this);
/*     */       }
/*     */       
/* 117 */     });
/* 118 */     this.btnEdit = new Button(panel, 8);
/* 119 */     Messages.setLanguageText(this.btnEdit, "wizard.multitracker.edit");
/* 120 */     gridData = new GridData();
/* 121 */     gridData.widthHint = 100;
/* 122 */     Utils.setLayoutData(this.btnEdit, gridData);
/* 123 */     this.btnEdit.addListener(13, new Listener() {
/*     */       public void handleEvent(Event e) {
/* 125 */         int selection = WebSeedPanel.this.configList.getSelectionIndex();
/* 126 */         String selected = WebSeedPanel.this.configList.getItem(selection);
/* 127 */         Map webseeds = TrackersUtil.getInstance().getWebSeeds();
/* 128 */         new WebSeedsEditor(selected, (Map)webseeds.get(selected), WebSeedPanel.this);
/*     */       }
/*     */       
/* 131 */     });
/* 132 */     this.btnDelete = new Button(panel, 8);
/* 133 */     Messages.setLanguageText(this.btnDelete, "wizard.multitracker.delete");
/* 134 */     gridData = new GridData(128);
/* 135 */     gridData.widthHint = 100;
/* 136 */     Utils.setLayoutData(this.btnDelete, gridData);
/* 137 */     this.btnDelete.addListener(13, new Listener() {
/*     */       public void handleEvent(Event e) {
/* 139 */         int selection = WebSeedPanel.this.configList.getSelectionIndex();
/* 140 */         String selected = WebSeedPanel.this.configList.getItem(selection);
/* 141 */         TrackersUtil.getInstance().removeWebSeed(selected);
/* 142 */         WebSeedPanel.this.refreshList("");
/* 143 */         WebSeedPanel.this.refreshDetails();
/* 144 */         WebSeedPanel.this.setEditDeleteEnable();
/*     */       }
/* 146 */     });
/* 147 */     Label labelSeparator = new Label(panel, 258);
/* 148 */     gridData = new GridData(768);
/* 149 */     gridData.horizontalSpan = 3;
/* 150 */     Utils.setLayoutData(labelSeparator, gridData);
/*     */     
/* 152 */     this.configDetails = new Tree(panel, 2048);
/* 153 */     gridData = new GridData(768);
/* 154 */     gridData.heightHint = 150;
/* 155 */     gridData.horizontalSpan = 3;
/* 156 */     Utils.setLayoutData(this.configDetails, gridData);
/*     */     
/* 158 */     refreshList(((NewTorrentWizard)this.wizard).webSeedConfig);
/* 159 */     refreshDetails();
/* 160 */     setEditDeleteEnable();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public IWizardPanel<NewTorrentWizard> getNextPanel()
/*     */   {
/* 169 */     return ((NewTorrentWizard)this.wizard).getNextPanelForMode(this);
/*     */   }
/*     */   
/*     */   public boolean isNextEnabled()
/*     */   {
/* 174 */     return true;
/*     */   }
/*     */   
/*     */   void refreshDetails() {
/* 178 */     this.configDetails.removeAll();
/* 179 */     Map webseeds = ((NewTorrentWizard)this.wizard).webseeds;
/* 180 */     Iterator iter = webseeds.entrySet().iterator();
/* 181 */     while (iter.hasNext()) {
/* 182 */       Map.Entry entry = (Map.Entry)iter.next();
/* 183 */       TreeItem itemRoot = new TreeItem(this.configDetails, 0);
/* 184 */       itemRoot.setText((String)entry.getKey());
/* 185 */       Iterator iter2 = ((List)entry.getValue()).iterator();
/* 186 */       while (iter2.hasNext()) {
/* 187 */         String url = (String)iter2.next();
/* 188 */         new TreeItem(itemRoot, 0).setText(url);
/*     */       }
/* 190 */       itemRoot.setExpanded(true);
/*     */     }
/*     */   }
/*     */   
/*     */   void setEditDeleteEnable() {
/* 195 */     if (this.configList.getItemCount() > 0) {
/* 196 */       this.btnEdit.setEnabled(true);
/* 197 */       this.btnDelete.setEnabled(true);
/*     */     } else {
/* 199 */       this.btnEdit.setEnabled(false);
/* 200 */       this.btnDelete.setEnabled(false);
/*     */     }
/*     */   }
/*     */   
/*     */   public void webSeedsChanged(String oldName, String newName, Map ws) {
/* 205 */     TrackersUtil util = TrackersUtil.getInstance();
/* 206 */     if ((oldName != null) && (!oldName.equals(newName)))
/* 207 */       util.removeWebSeed(oldName);
/* 208 */     util.addWebSeed(newName, ws);
/* 209 */     refreshList(newName);
/* 210 */     refreshDetails();
/* 211 */     setEditDeleteEnable();
/*     */   }
/*     */   
/*     */   private void refreshList(String toBeSelected) {
/* 215 */     Map webseeds = TrackersUtil.getInstance().getWebSeeds();
/* 216 */     this.configList.removeAll();
/* 217 */     Iterator iter = webseeds.keySet().iterator();
/* 218 */     while (iter.hasNext()) {
/* 219 */       this.configList.add((String)iter.next());
/*     */     }
/* 221 */     int selection = this.configList.indexOf(toBeSelected);
/* 222 */     if (selection != -1) {
/* 223 */       this.configList.select(selection);
/* 224 */     } else if (this.configList.getItemCount() > 0) {
/* 225 */       this.configList.select(0);
/*     */     }
/* 227 */     updateWebSeeds();
/*     */   }
/*     */   
/*     */   private void updateWebSeeds() {
/* 231 */     int selection = this.configList.getSelectionIndex();
/* 232 */     if (selection == -1) {
/* 233 */       ((NewTorrentWizard)this.wizard).webSeedConfig = "";
/* 234 */       ((NewTorrentWizard)this.wizard).webseeds = new HashMap();
/* 235 */       setNext();
/* 236 */       return;
/*     */     }
/* 238 */     String selected = this.configList.getItem(selection);
/* 239 */     ((NewTorrentWizard)this.wizard).webSeedConfig = selected;
/* 240 */     Map webseeds = TrackersUtil.getInstance().getWebSeeds();
/* 241 */     ((NewTorrentWizard)this.wizard).webseeds = ((Map)webseeds.get(selected));
/* 242 */     setNext();
/*     */   }
/*     */   
/*     */   private void setNext() {
/* 246 */     ((NewTorrentWizard)this.wizard).setNextEnabled(true);
/* 247 */     ((NewTorrentWizard)this.wizard).setErrorMessage("");
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/maketorrent/WebSeedPanel.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */