/*     */ package org.gudy.azureus2.ui.swt.maketorrent;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
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
/*     */ 
/*     */ public class MultiTrackerPanel
/*     */   extends AbstractWizardPanel<NewTorrentWizard>
/*     */   implements TrackerEditorListener
/*     */ {
/*     */   private Combo configList;
/*     */   private Tree configDetails;
/*     */   private Button btnNew;
/*     */   private Button btnEdit;
/*     */   private Button btnDelete;
/*     */   
/*     */   public MultiTrackerPanel(NewTorrentWizard wizard, AbstractWizardPanel<NewTorrentWizard> previous)
/*     */   {
/*  60 */     super(wizard, previous);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void show()
/*     */   {
/*  69 */     ((NewTorrentWizard)this.wizard).setTitle(MessageText.getString("wizard.multitracker.title"));
/*  70 */     ((NewTorrentWizard)this.wizard).setCurrentInfo("");
/*  71 */     Composite rootPanel = ((NewTorrentWizard)this.wizard).getPanel();
/*  72 */     GridLayout layout = new GridLayout();
/*  73 */     layout.numColumns = 1;
/*  74 */     rootPanel.setLayout(layout);
/*     */     
/*  76 */     Composite panel = new Composite(rootPanel, 0);
/*  77 */     GridData gridData = new GridData(772);
/*  78 */     Utils.setLayoutData(panel, gridData);
/*  79 */     layout = new GridLayout();
/*  80 */     layout.numColumns = 3;
/*  81 */     panel.setLayout(layout);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*  86 */     Label labelTitle = new Label(panel, 0);
/*  87 */     Messages.setLanguageText(labelTitle, "wizard.multitracker.configuration");
/*  88 */     gridData = new GridData();
/*  89 */     gridData.horizontalSpan = 3;
/*  90 */     Utils.setLayoutData(labelTitle, gridData);
/*     */     
/*  92 */     this.configList = new Combo(panel, 8);
/*  93 */     gridData = new GridData(768);
/*  94 */     gridData.horizontalSpan = 3;
/*  95 */     Utils.setLayoutData(this.configList, gridData);
/*  96 */     this.configList.addListener(13, new Listener() {
/*     */       public void handleEvent(Event e) {
/*  98 */         MultiTrackerPanel.this.updateTrackers();
/*  99 */         MultiTrackerPanel.this.refreshDetails();
/*     */       }
/*     */       
/* 102 */     });
/* 103 */     this.btnNew = new Button(panel, 8);
/* 104 */     Messages.setLanguageText(this.btnNew, "wizard.multitracker.new");
/* 105 */     gridData = new GridData();
/* 106 */     gridData.widthHint = 100;
/* 107 */     Utils.setLayoutData(this.btnNew, gridData);
/* 108 */     this.btnNew.addListener(13, new Listener() {
/*     */       public void handleEvent(Event e) {
/* 110 */         List group = new ArrayList();
/* 111 */         List tracker = new ArrayList();
/* 112 */         tracker.add(((NewTorrentWizard)MultiTrackerPanel.this.wizard).trackerURL);
/* 113 */         group.add(tracker);
/* 114 */         new MultiTrackerEditor(((NewTorrentWizard)MultiTrackerPanel.this.wizard).getWizardWindow(), null, group, MultiTrackerPanel.this);
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
/* 125 */         int selection = MultiTrackerPanel.this.configList.getSelectionIndex();
/* 126 */         String selected = MultiTrackerPanel.this.configList.getItem(selection);
/* 127 */         Map multiTrackers = TrackersUtil.getInstance().getMultiTrackers();
/* 128 */         new MultiTrackerEditor(((NewTorrentWizard)MultiTrackerPanel.this.wizard).getWizardWindow(), selected, (List)multiTrackers.get(selected), MultiTrackerPanel.this);
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
/* 139 */         int selection = MultiTrackerPanel.this.configList.getSelectionIndex();
/* 140 */         String selected = MultiTrackerPanel.this.configList.getItem(selection);
/* 141 */         TrackersUtil.getInstance().removeMultiTracker(selected);
/* 142 */         MultiTrackerPanel.this.refreshList("");
/* 143 */         MultiTrackerPanel.this.refreshDetails();
/* 144 */         MultiTrackerPanel.this.setEditDeleteEnable();
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
/* 158 */     refreshList(((NewTorrentWizard)this.wizard).multiTrackerConfig);
/* 159 */     refreshDetails();
/* 160 */     setEditDeleteEnable();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public IWizardPanel getNextPanel()
/*     */   {
/* 170 */     if (((NewTorrentWizard)this.wizard).useWebSeed) {
/* 171 */       return new WebSeedPanel((NewTorrentWizard)this.wizard, this);
/*     */     }
/*     */     
/* 174 */     return ((NewTorrentWizard)this.wizard).getNextPanelForMode(this);
/*     */   }
/*     */   
/*     */   public boolean isNextEnabled()
/*     */   {
/* 179 */     return true;
/*     */   }
/*     */   
/*     */   void refreshDetails() {
/* 183 */     this.configDetails.removeAll();
/* 184 */     List trackers = ((NewTorrentWizard)this.wizard).trackers;
/* 185 */     Iterator iter = trackers.iterator();
/* 186 */     while (iter.hasNext()) {
/* 187 */       List trackerGroup = (List)iter.next();
/* 188 */       TreeItem itemRoot = new TreeItem(this.configDetails, 0);
/* 189 */       Messages.setLanguageText(itemRoot, "wizard.multitracker.group");
/* 190 */       Iterator iter2 = trackerGroup.iterator();
/* 191 */       while (iter2.hasNext()) {
/* 192 */         String url = (String)iter2.next();
/* 193 */         new TreeItem(itemRoot, 0).setText(url);
/*     */       }
/* 195 */       itemRoot.setExpanded(true);
/*     */     }
/*     */   }
/*     */   
/*     */   void setEditDeleteEnable() {
/* 200 */     if (this.configList.getItemCount() > 0) {
/* 201 */       this.btnEdit.setEnabled(true);
/* 202 */       this.btnDelete.setEnabled(true);
/*     */     } else {
/* 204 */       this.btnEdit.setEnabled(false);
/* 205 */       this.btnDelete.setEnabled(false);
/*     */     }
/*     */   }
/*     */   
/*     */   public void trackersChanged(String oldName, String newName, List trackers) {
/* 210 */     TrackersUtil util = TrackersUtil.getInstance();
/* 211 */     if ((oldName != null) && (!oldName.equals(newName)))
/* 212 */       util.removeMultiTracker(oldName);
/* 213 */     util.addMultiTracker(newName, trackers);
/* 214 */     refreshList(newName);
/* 215 */     refreshDetails();
/* 216 */     setEditDeleteEnable();
/*     */   }
/*     */   
/*     */   private void refreshList(String toBeSelected) {
/* 220 */     Map multiTrackers = TrackersUtil.getInstance().getMultiTrackers();
/* 221 */     this.configList.removeAll();
/* 222 */     Iterator iter = multiTrackers.keySet().iterator();
/* 223 */     while (iter.hasNext()) {
/* 224 */       this.configList.add((String)iter.next());
/*     */     }
/* 226 */     int selection = this.configList.indexOf(toBeSelected);
/* 227 */     if (selection != -1) {
/* 228 */       this.configList.select(selection);
/* 229 */     } else if (this.configList.getItemCount() > 0) {
/* 230 */       this.configList.select(0);
/*     */     }
/* 232 */     updateTrackers();
/*     */   }
/*     */   
/*     */   private void updateTrackers() {
/* 236 */     int selection = this.configList.getSelectionIndex();
/* 237 */     if (selection == -1) {
/* 238 */       List group = new ArrayList();
/* 239 */       List tracker = new ArrayList();
/* 240 */       tracker.add(((NewTorrentWizard)this.wizard).trackerURL);
/* 241 */       group.add(tracker);
/* 242 */       ((NewTorrentWizard)this.wizard).trackers = group;
/* 243 */       ((NewTorrentWizard)this.wizard).multiTrackerConfig = "";
/* 244 */       setNext();
/* 245 */       return;
/*     */     }
/* 247 */     String selected = this.configList.getItem(selection);
/* 248 */     ((NewTorrentWizard)this.wizard).multiTrackerConfig = selected;
/* 249 */     Map multiTrackers = TrackersUtil.getInstance().getMultiTrackers();
/* 250 */     ((NewTorrentWizard)this.wizard).trackers = ((List)multiTrackers.get(selected));
/* 251 */     setNext();
/*     */   }
/*     */   
/*     */   private void setNext() {
/* 255 */     String trackerUrl = ((NewTorrentWizard)this.wizard).trackerURL;
/* 256 */     List groups = ((NewTorrentWizard)this.wizard).trackers;
/* 257 */     Iterator iterGroups = groups.iterator();
/* 258 */     while (iterGroups.hasNext()) {
/* 259 */       List trackers = (List)iterGroups.next();
/* 260 */       Iterator iterTrackers = trackers.iterator();
/* 261 */       while (iterTrackers.hasNext()) {
/* 262 */         String tracker = (String)iterTrackers.next();
/* 263 */         if (trackerUrl.equals(tracker))
/*     */         {
/* 265 */           ((NewTorrentWizard)this.wizard).setNextEnabled(true);
/* 266 */           ((NewTorrentWizard)this.wizard).setErrorMessage("");
/* 267 */           return;
/*     */         }
/*     */       }
/*     */     }
/* 271 */     ((NewTorrentWizard)this.wizard).setNextEnabled(false);
/* 272 */     ((NewTorrentWizard)this.wizard).setErrorMessage(MessageText.getString("wizard.multitracker.noannounce"));
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/maketorrent/MultiTrackerPanel.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */