/*     */ package org.gudy.azureus2.ui.swt.maketorrent;
/*     */ 
/*     */ import com.aelitis.azureus.ui.UserPrompterResultListener;
/*     */ import java.net.URL;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import org.eclipse.swt.custom.TreeEditor;
/*     */ import org.eclipse.swt.events.KeyAdapter;
/*     */ import org.eclipse.swt.events.KeyEvent;
/*     */ import org.eclipse.swt.events.ModifyEvent;
/*     */ import org.eclipse.swt.events.ModifyListener;
/*     */ import org.eclipse.swt.events.MouseAdapter;
/*     */ import org.eclipse.swt.events.MouseEvent;
/*     */ import org.eclipse.swt.events.SelectionAdapter;
/*     */ import org.eclipse.swt.events.SelectionEvent;
/*     */ import org.eclipse.swt.graphics.Point;
/*     */ import org.eclipse.swt.graphics.Rectangle;
/*     */ import org.eclipse.swt.layout.GridData;
/*     */ import org.eclipse.swt.layout.GridLayout;
/*     */ import org.eclipse.swt.widgets.Button;
/*     */ import org.eclipse.swt.widgets.Combo;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.Control;
/*     */ import org.eclipse.swt.widgets.Event;
/*     */ import org.eclipse.swt.widgets.Label;
/*     */ import org.eclipse.swt.widgets.Listener;
/*     */ import org.eclipse.swt.widgets.Menu;
/*     */ import org.eclipse.swt.widgets.MenuItem;
/*     */ import org.eclipse.swt.widgets.Shell;
/*     */ import org.eclipse.swt.widgets.Text;
/*     */ import org.eclipse.swt.widgets.Tree;
/*     */ import org.eclipse.swt.widgets.TreeItem;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.core3.util.TorrentUtils;
/*     */ import org.gudy.azureus2.core3.util.TrackersUtil;
/*     */ import org.gudy.azureus2.ui.swt.Messages;
/*     */ import org.gudy.azureus2.ui.swt.TextViewerWindow;
/*     */ import org.gudy.azureus2.ui.swt.TextViewerWindow.TextViewerWindowListener;
/*     */ import org.gudy.azureus2.ui.swt.Utils;
/*     */ import org.gudy.azureus2.ui.swt.components.shell.ShellFactory;
/*     */ import org.gudy.azureus2.ui.swt.mainwindow.Colors;
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
/*     */ public class MultiTrackerEditor
/*     */ {
/*     */   TrackerEditorListener listener;
/*     */   String oldName;
/*     */   String currentName;
/*     */   boolean anonymous;
/*     */   boolean showTemplates;
/*     */   List<List<String>> trackers;
/*     */   Shell shell;
/*     */   Text textName;
/*     */   Tree treeGroups;
/*     */   TreeEditor editor;
/*     */   TreeItem itemEdited;
/*     */   Button btnSave;
/*     */   Button btnCancel;
/*     */   Menu menu;
/*     */   
/*     */   public MultiTrackerEditor(Shell parent_shell, String name, List<List<String>> trackers, TrackerEditorListener listener)
/*     */   {
/*  77 */     this(parent_shell, name, trackers, listener, false);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public MultiTrackerEditor(Shell parent_shell, String name, List<List<String>> trackers, TrackerEditorListener listener, boolean anonymous)
/*     */   {
/*  88 */     this(parent_shell, name, trackers, listener, anonymous, false);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public MultiTrackerEditor(Shell parent_shell, String name, List<List<String>> trackers, TrackerEditorListener listener, boolean _anonymous, boolean _showTemplates)
/*     */   {
/* 100 */     this.oldName = name;
/* 101 */     if (name != null) {
/* 102 */       this.currentName = name;
/*     */     } else {
/* 104 */       this.currentName = "";
/*     */     }
/* 106 */     this.listener = listener;
/* 107 */     this.anonymous = _anonymous;
/* 108 */     this.showTemplates = _showTemplates;
/* 109 */     this.trackers = new ArrayList(trackers);
/* 110 */     createWindow(parent_shell);
/*     */   }
/*     */   
/*     */   private void createWindow(Shell parent_shell)
/*     */   {
/* 115 */     if (parent_shell == null) {
/* 116 */       this.shell = ShellFactory.createMainShell(2160);
/*     */     } else {
/* 118 */       this.shell = ShellFactory.createShell(parent_shell, 2160);
/*     */     }
/*     */     
/* 121 */     Messages.setLanguageText(this.shell, this.anonymous ? "wizard.multitracker.edit.title" : "wizard.multitracker.template.title");
/* 122 */     Utils.setShellIcon(this.shell);
/* 123 */     GridLayout layout = new GridLayout();
/* 124 */     layout.numColumns = 3;
/* 125 */     this.shell.setLayout(layout);
/*     */     
/*     */ 
/*     */ 
/* 129 */     if (!this.anonymous)
/*     */     {
/* 131 */       Label labelName = new Label(this.shell, 0);
/* 132 */       Messages.setLanguageText(labelName, "wizard.multitracker.edit.name");
/*     */       
/* 134 */       this.textName = new Text(this.shell, 2048);
/* 135 */       this.textName.setText(this.currentName);
/* 136 */       GridData gridData = new GridData(768);
/* 137 */       gridData.horizontalSpan = 2;
/* 138 */       this.textName.setLayoutData(gridData);
/* 139 */       this.textName.addModifyListener(new ModifyListener() {
/*     */         public void modifyText(ModifyEvent arg0) {
/* 141 */           MultiTrackerEditor.this.currentName = MultiTrackerEditor.this.textName.getText();
/* 142 */           MultiTrackerEditor.this.computeSaveEnable();
/*     */         }
/*     */       });
/*     */     }
/*     */     
/* 147 */     this.treeGroups = new Tree(this.shell, 2048);
/* 148 */     GridData gridData = new GridData(1808);
/* 149 */     gridData.horizontalSpan = 3;
/* 150 */     gridData.heightHint = 150;
/* 151 */     this.treeGroups.setLayoutData(gridData);
/*     */     
/* 153 */     this.treeGroups.addMouseListener(new MouseAdapter()
/*     */     {
/*     */ 
/*     */ 
/*     */       public void mouseDoubleClick(MouseEvent arg0)
/*     */       {
/*     */ 
/* 160 */         if (MultiTrackerEditor.this.treeGroups.getSelectionCount() == 1) {
/* 161 */           TreeItem treeItem = MultiTrackerEditor.this.treeGroups.getSelection()[0];
/* 162 */           String type = (String)treeItem.getData("type");
/* 163 */           if (type.equals("tracker")) {
/* 164 */             MultiTrackerEditor.this.editTreeItem(treeItem);
/*     */           }
/*     */         }
/*     */       }
/*     */     });
/*     */     
/* 170 */     if (this.showTemplates)
/*     */     {
/*     */ 
/*     */ 
/* 174 */       Composite cTemplate = new Composite(this.shell, 0);
/* 175 */       gridData = new GridData(768);
/* 176 */       gridData.horizontalSpan = 3;
/* 177 */       cTemplate.setLayoutData(gridData);
/* 178 */       GridLayout layoutTemplate = new GridLayout();
/* 179 */       layoutTemplate.numColumns = 5;
/* 180 */       cTemplate.setLayout(layoutTemplate);
/*     */       
/* 182 */       Label labelTitle = new Label(cTemplate, 0);
/* 183 */       Messages.setLanguageText(labelTitle, "Search.menu.engines");
/*     */       
/*     */ 
/* 186 */       final Combo configList = new Combo(cTemplate, 8);
/* 187 */       gridData = new GridData(768);
/* 188 */       configList.setLayoutData(gridData);
/*     */       
/*     */ 
/* 191 */       final List<Button> buttons = new ArrayList();
/*     */       
/* 193 */       String sel_str = COConfigurationManager.getStringParameter("multitrackereditor.last.selection", null);
/*     */       
/* 195 */       final String[] currentTemplate = { (sel_str == null) || (sel_str.length() == 0) ? null : sel_str };
/*     */       
/* 197 */       final Runnable updateSelection = new Runnable()
/*     */       {
/*     */ 
/*     */         public void run()
/*     */         {
/*     */ 
/* 203 */           int selection = configList.getSelectionIndex();
/*     */           
/* 205 */           boolean enabled = selection != -1;
/*     */           
/* 207 */           String sel_str = currentTemplate[0] = enabled ? configList.getItem(selection) : null;
/*     */           
/* 209 */           COConfigurationManager.setParameter("multitrackereditor.last.selection", sel_str == null ? "" : sel_str);
/*     */           
/* 211 */           Iterator<Button> it = buttons.iterator();
/*     */           
/* 213 */           it.next();
/*     */           
/* 215 */           while (it.hasNext())
/*     */           {
/* 217 */             ((Button)it.next()).setEnabled(enabled);
/*     */           }
/*     */           
/*     */         }
/* 221 */       };
/* 222 */       final Runnable updateTemplates = new Runnable()
/*     */       {
/*     */ 
/*     */         public void run()
/*     */         {
/*     */ 
/* 228 */           Map<String, List<List<String>>> multiTrackers = TrackersUtil.getInstance().getMultiTrackers();
/*     */           
/* 230 */           configList.removeAll();
/*     */           
/* 232 */           for (String str : multiTrackers.keySet())
/*     */           {
/* 234 */             configList.add(str);
/*     */           }
/*     */           
/* 237 */           String toBeSelected = currentTemplate[0];
/*     */           
/* 239 */           if (toBeSelected != null)
/*     */           {
/* 241 */             int selection = configList.indexOf(toBeSelected);
/*     */             
/* 243 */             if (selection != -1)
/*     */             {
/* 245 */               configList.select(selection);
/*     */             }
/* 247 */             else if (configList.getItemCount() > 0)
/*     */             {
/* 249 */               currentTemplate[0] = configList.getItem(0);
/*     */               
/* 251 */               configList.select(0);
/*     */             }
/*     */           }
/*     */           
/* 255 */           updateSelection.run();
/*     */         }
/*     */         
/* 258 */       };
/* 259 */       final TrackerEditorListener templateTEL = new TrackerEditorListener()
/*     */       {
/*     */ 
/*     */ 
/*     */ 
/*     */         public void trackersChanged(String oldName, String newName, List<List<String>> trackers)
/*     */         {
/*     */ 
/*     */ 
/* 268 */           TrackersUtil util = TrackersUtil.getInstance();
/*     */           
/* 270 */           if ((oldName != null) && (!oldName.equals(newName)))
/*     */           {
/* 272 */             util.removeMultiTracker(oldName);
/*     */           }
/*     */           
/* 275 */           util.addMultiTracker(newName, trackers);
/*     */           
/* 277 */           currentTemplate[0] = newName;
/*     */           
/* 279 */           updateTemplates.run();
/*     */         }
/*     */         
/*     */ 
/* 283 */       };
/* 284 */       final Button btnNew = new Button(cTemplate, 8);
/* 285 */       buttons.add(btnNew);
/* 286 */       Messages.setLanguageText(btnNew, "wizard.multitracker.new");
/* 287 */       btnNew.addListener(13, new Listener() {
/*     */         public void handleEvent(Event e) {
/* 289 */           List group = new ArrayList();
/* 290 */           List tracker = new ArrayList();
/* 291 */           group.add(tracker);
/* 292 */           new MultiTrackerEditor(btnNew.getShell(), null, group, templateTEL);
/*     */         }
/*     */         
/* 295 */       });
/* 296 */       configList.addListener(13, new Listener() {
/*     */         public void handleEvent(Event e) {
/* 298 */           updateSelection.run();
/*     */         }
/*     */         
/*     */ 
/* 302 */       });
/* 303 */       final Button btnEdit = new Button(cTemplate, 8);
/* 304 */       buttons.add(btnEdit);
/* 305 */       Messages.setLanguageText(btnEdit, "wizard.multitracker.edit");
/* 306 */       btnEdit.addListener(13, new Listener() {
/*     */         public void handleEvent(Event e) {
/* 308 */           Map multiTrackers = TrackersUtil.getInstance().getMultiTrackers();
/* 309 */           String selected = currentTemplate[0];
/* 310 */           new MultiTrackerEditor(btnEdit.getShell(), selected, (List)multiTrackers.get(selected), templateTEL);
/*     */         }
/*     */         
/* 313 */       });
/* 314 */       Button btnDelete = new Button(cTemplate, 8);
/* 315 */       buttons.add(btnDelete);
/* 316 */       Messages.setLanguageText(btnDelete, "wizard.multitracker.delete");
/* 317 */       btnDelete.addListener(13, new Listener() {
/*     */         public void handleEvent(Event e) {
/* 319 */           final String selected = currentTemplate[0];
/*     */           
/* 321 */           MessageBoxShell mb = new MessageBoxShell(MessageText.getString("message.confirm.delete.title"), MessageText.getString("message.confirm.delete.text", new String[] { selected }), new String[] { MessageText.getString("Button.yes"), MessageText.getString("Button.no") }, 1);
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
/* 332 */           mb.open(new UserPrompterResultListener() {
/*     */             public void prompterClosed(int result) {
/* 334 */               if (result == 0) {
/* 335 */                 TrackersUtil.getInstance().removeMultiTracker(selected);
/* 336 */                 MultiTrackerEditor.9.this.val$updateTemplates.run();
/*     */               }
/*     */               
/*     */             }
/*     */             
/*     */           });
/*     */         }
/* 343 */       });
/* 344 */       Label labelApply = new Label(cTemplate, 0);
/* 345 */       gridData = new GridData(768);
/* 346 */       gridData.horizontalSpan = 2;
/* 347 */       labelApply.setLayoutData(gridData);
/* 348 */       Messages.setLanguageText(labelApply, "apply.selected.template");
/*     */       
/* 350 */       Button btnReplace = new Button(cTemplate, 8);
/* 351 */       buttons.add(btnReplace);
/* 352 */       Messages.setLanguageText(btnReplace, "label.replace");
/* 353 */       btnReplace.addListener(13, new Listener() {
/*     */         public void handleEvent(Event e) {
/* 355 */           Map<String, List<List<String>>> multiTrackers = TrackersUtil.getInstance().getMultiTrackers();
/* 356 */           String selected = currentTemplate[0];
/* 357 */           MultiTrackerEditor.this.trackers = TorrentUtils.getClone((List)multiTrackers.get(selected));
/* 358 */           MultiTrackerEditor.this.refresh();
/* 359 */           MultiTrackerEditor.this.computeSaveEnable();
/*     */         }
/*     */         
/* 362 */       });
/* 363 */       Button btnMerge = new Button(cTemplate, 8);
/* 364 */       buttons.add(btnMerge);
/* 365 */       Messages.setLanguageText(btnMerge, "label.merge");
/* 366 */       btnMerge.addListener(13, new Listener() {
/*     */         public void handleEvent(Event e) {
/* 368 */           Map<String, List<List<String>>> multiTrackers = TrackersUtil.getInstance().getMultiTrackers();
/* 369 */           String selected = currentTemplate[0];
/* 370 */           MultiTrackerEditor.this.trackers = TorrentUtils.mergeAnnounceURLs(MultiTrackerEditor.this.trackers, (List)multiTrackers.get(selected));
/* 371 */           MultiTrackerEditor.this.refresh();
/* 372 */           MultiTrackerEditor.this.computeSaveEnable();
/*     */         }
/*     */         
/* 375 */       });
/* 376 */       Button btnRemove = new Button(cTemplate, 8);
/* 377 */       buttons.add(btnRemove);
/* 378 */       Messages.setLanguageText(btnRemove, "Button.remove");
/* 379 */       btnRemove.addListener(13, new Listener() {
/*     */         public void handleEvent(Event e) {
/* 381 */           Map<String, List<List<String>>> multiTrackers = TrackersUtil.getInstance().getMultiTrackers();
/* 382 */           String selected = currentTemplate[0];
/* 383 */           MultiTrackerEditor.this.trackers = TorrentUtils.removeAnnounceURLs(MultiTrackerEditor.this.trackers, (List)multiTrackers.get(selected), false);
/* 384 */           MultiTrackerEditor.this.refresh();
/* 385 */           MultiTrackerEditor.this.computeSaveEnable();
/*     */         }
/*     */         
/* 388 */       });
/* 389 */       updateTemplates.run();
/*     */       
/* 391 */       Utils.makeButtonsEqualWidth(buttons);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 396 */     Label labelSeparator = new Label(this.shell, 258);
/* 397 */     gridData = new GridData(768);
/* 398 */     gridData.horizontalSpan = 3;
/* 399 */     labelSeparator.setLayoutData(gridData);
/*     */     
/*     */ 
/*     */ 
/* 403 */     Composite cButtons = new Composite(this.shell, 0);
/* 404 */     gridData = new GridData(768);
/* 405 */     gridData.horizontalSpan = 3;
/* 406 */     cButtons.setLayoutData(gridData);
/* 407 */     GridLayout layoutButtons = new GridLayout();
/* 408 */     layoutButtons.numColumns = 4;
/* 409 */     cButtons.setLayout(layoutButtons);
/*     */     
/* 411 */     List<Button> buttons = new ArrayList();
/*     */     
/* 413 */     final Button btnedittext = new Button(cButtons, 8);
/* 414 */     buttons.add(btnedittext);
/* 415 */     gridData = new GridData();
/* 416 */     gridData.horizontalAlignment = 3;
/* 417 */     btnedittext.setLayoutData(gridData);
/* 418 */     Messages.setLanguageText(btnedittext, "wizard.multitracker.edit.text");
/* 419 */     btnedittext.addListener(13, new Listener()
/*     */     {
/*     */       public void handleEvent(Event e) {
/* 422 */         MultiTrackerEditor.this.btnSave.setEnabled(false);
/* 423 */         btnedittext.setEnabled(false);
/*     */         
/* 425 */         MultiTrackerEditor.this.trackers = new ArrayList();
/* 426 */         TreeItem[] groupItems = MultiTrackerEditor.this.treeGroups.getItems();
/*     */         
/* 428 */         for (int i = 0; i < groupItems.length; i++) {
/* 429 */           TreeItem group = groupItems[i];
/* 430 */           TreeItem[] trackerItems = group.getItems();
/* 431 */           List groupList = new ArrayList(group.getItemCount());
/* 432 */           for (int j = 0; j < trackerItems.length; j++) {
/* 433 */             groupList.add(trackerItems[j].getText());
/*     */           }
/* 435 */           MultiTrackerEditor.this.trackers.add(groupList);
/*     */         }
/*     */         
/* 438 */         final String old_text = TorrentUtils.announceGroupsToText(MultiTrackerEditor.this.trackers);
/*     */         
/* 440 */         final TextViewerWindow viewer = new TextViewerWindow(MultiTrackerEditor.this.shell, "wizard.multitracker.edit.text.title", "wizard.multitracker.edit.text.msg", old_text, false, false);
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 447 */         viewer.setEditable(true);
/*     */         
/* 449 */         viewer.addListener(new TextViewerWindow.TextViewerWindowListener()
/*     */         {
/*     */ 
/*     */           public void closed()
/*     */           {
/*     */             try
/*     */             {
/* 456 */               String new_text = viewer.getText();
/*     */               
/* 458 */               if (!old_text.equals(new_text))
/*     */               {
/* 460 */                 String[] lines = new_text.split("\n");
/*     */                 
/* 462 */                 StringBuilder valid_text = new StringBuilder(new_text.length() + 1);
/*     */                 
/* 464 */                 for (String line : lines)
/*     */                 {
/* 466 */                   line = line.trim();
/*     */                   
/* 468 */                   if ((line.length() <= 0) || 
/*     */                   
/* 470 */                     (MultiTrackerEditor.this.validURL(line)))
/*     */                   {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 476 */                     valid_text.append(line);
/* 477 */                     valid_text.append("\n");
/*     */                   }
/*     */                 }
/* 480 */                 MultiTrackerEditor.this.trackers = TorrentUtils.announceTextToGroups(valid_text.toString());
/*     */                 
/* 482 */                 MultiTrackerEditor.this.refresh();
/*     */               }
/*     */             }
/*     */             finally {
/* 486 */               MultiTrackerEditor.this.computeSaveEnable();
/*     */               
/* 488 */               MultiTrackerEditor.13.this.val$btnedittext.setEnabled(true);
/*     */             }
/*     */             
/*     */           }
/*     */         });
/*     */       }
/* 494 */     });
/* 495 */     Label label = new Label(cButtons, 0);
/* 496 */     gridData = new GridData(768);
/* 497 */     label.setLayoutData(gridData);
/*     */     
/* 499 */     this.btnSave = new Button(cButtons, 8);
/* 500 */     buttons.add(this.btnSave);
/* 501 */     gridData = new GridData();
/* 502 */     gridData.horizontalAlignment = 3;
/* 503 */     this.btnSave.setLayoutData(gridData);
/* 504 */     Messages.setLanguageText(this.btnSave, "wizard.multitracker.edit.save");
/* 505 */     this.btnSave.addListener(13, new Listener() {
/*     */       public void handleEvent(Event e) {
/* 507 */         MultiTrackerEditor.this.update();
/* 508 */         MultiTrackerEditor.this.shell.dispose();
/*     */       }
/*     */       
/* 511 */     });
/* 512 */     this.btnCancel = new Button(cButtons, 8);
/* 513 */     buttons.add(this.btnCancel);
/* 514 */     gridData = new GridData();
/* 515 */     gridData.horizontalAlignment = 3;
/* 516 */     this.btnCancel.setLayoutData(gridData);
/* 517 */     Messages.setLanguageText(this.btnCancel, "Button.cancel");
/* 518 */     this.btnCancel.addListener(13, new Listener() {
/*     */       public void handleEvent(Event e) {
/* 520 */         MultiTrackerEditor.this.shell.dispose();
/*     */       }
/*     */       
/* 523 */     });
/* 524 */     Utils.makeButtonsEqualWidth(buttons);
/*     */     
/* 526 */     this.shell.setDefaultButton(this.btnSave);
/*     */     
/* 528 */     this.shell.addListener(31, new Listener() {
/*     */       public void handleEvent(Event e) {
/* 530 */         if (e.character == '\033') {
/* 531 */           MultiTrackerEditor.this.shell.dispose();
/*     */         }
/*     */         
/*     */       }
/*     */       
/* 536 */     });
/* 537 */     computeSaveEnable();
/* 538 */     refresh();
/* 539 */     constructMenu();
/*     */     
/* 541 */     this.editor = new TreeEditor(this.treeGroups);
/* 542 */     this.treeGroups.addSelectionListener(new SelectionAdapter() {
/*     */       public void widgetSelected(SelectionEvent arg0) {
/* 544 */         if ((MultiTrackerEditor.this.itemEdited != null) && (!MultiTrackerEditor.this.itemEdited.isDisposed()) && (!MultiTrackerEditor.this.editor.getEditor().isDisposed())) {
/* 545 */           MultiTrackerEditor.this.itemEdited.setText(((Text)MultiTrackerEditor.this.editor.getEditor()).getText());
/*     */         }
/*     */         
/* 548 */         MultiTrackerEditor.this.removeEditor();
/*     */       }
/*     */       
/* 551 */     });
/* 552 */     Point size = this.shell.computeSize(500, -1);
/* 553 */     this.shell.setSize(size);
/*     */     
/* 555 */     Utils.centreWindow(this.shell);
/*     */     
/* 557 */     this.shell.open();
/*     */   }
/*     */   
/*     */   private void update() {
/* 561 */     this.trackers = new ArrayList();
/* 562 */     TreeItem[] groupItems = this.treeGroups.getItems();
/*     */     
/* 564 */     for (int i = 0; i < groupItems.length; i++) {
/* 565 */       TreeItem group = groupItems[i];
/* 566 */       TreeItem[] trackerItems = group.getItems();
/* 567 */       List groupList = new ArrayList(group.getItemCount());
/* 568 */       for (int j = 0; j < trackerItems.length; j++) {
/* 569 */         groupList.add(trackerItems[j].getText());
/*     */       }
/* 571 */       this.trackers.add(groupList);
/*     */     }
/*     */     
/* 574 */     this.listener.trackersChanged(this.oldName, this.currentName, this.trackers);
/* 575 */     this.oldName = this.currentName;
/*     */   }
/*     */   
/*     */   private void computeSaveEnable()
/*     */   {
/* 580 */     boolean enabled = (this.anonymous) || (!"".equals(this.currentName));
/*     */     
/* 582 */     if (enabled)
/*     */     {
/* 584 */       TreeItem[] groupItems = this.treeGroups.getItems();
/*     */       
/*     */ 
/* 587 */       for (int i = 0; i < groupItems.length; i++) {
/* 588 */         TreeItem group = groupItems[i];
/* 589 */         TreeItem[] trackerItems = group.getItems();
/* 590 */         for (int j = 0; j < trackerItems.length; j++)
/*     */         {
/* 592 */           if (!validURL(trackerItems[j].getText()))
/*     */           {
/* 594 */             enabled = false;
/*     */             
/*     */             break label100;
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */     label100:
/* 602 */     if (enabled != this.btnSave.getEnabled())
/*     */     {
/* 604 */       this.btnSave.setEnabled(enabled);
/*     */     }
/*     */   }
/*     */   
/*     */   private void refresh() {
/* 609 */     this.treeGroups.removeAll();
/* 610 */     Iterator iter = this.trackers.iterator();
/* 611 */     while (iter.hasNext()) {
/* 612 */       List trackerGroup = (List)iter.next();
/* 613 */       TreeItem itemRoot = newGroup();
/* 614 */       Iterator iter2 = trackerGroup.iterator();
/* 615 */       while (iter2.hasNext()) {
/* 616 */         String url = (String)iter2.next();
/* 617 */         newTracker(itemRoot, url);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   private void constructMenu() {
/* 623 */     this.menu = new Menu(this.shell, 0);
/* 624 */     this.menu.addListener(22, new Listener()
/*     */     {
/*     */       public void handleEvent(Event e) {
/* 627 */         MenuItem[] items = MultiTrackerEditor.this.menu.getItems();
/* 628 */         for (int i = 0; i < items.length; i++) {
/* 629 */           items[i].dispose();
/*     */         }
/*     */         
/*     */ 
/*     */ 
/* 634 */         if (MultiTrackerEditor.this.treeGroups.getSelectionCount() != 1) {
/* 635 */           MenuItem item = new MenuItem(MultiTrackerEditor.this.menu, 0);
/* 636 */           Messages.setLanguageText(item, "wizard.multitracker.edit.newgroup");
/* 637 */           item.addListener(13, new Listener() {
/*     */             public void handleEvent(Event arg0) {
/* 639 */               TreeItem group = MultiTrackerEditor.this.newGroup();
/* 640 */               TreeItem itemTracker = MultiTrackerEditor.this.newTracker(group, "http://");
/* 641 */               MultiTrackerEditor.this.editTreeItem(itemTracker);
/*     */             }
/* 643 */           });
/* 644 */           return;
/*     */         }
/*     */         
/*     */ 
/* 648 */         final TreeItem treeItem = MultiTrackerEditor.this.treeGroups.getSelection()[0];
/* 649 */         String type = (String)treeItem.getData("type");
/* 650 */         if (type.equals("tracker"))
/*     */         {
/* 652 */           MenuItem item = new MenuItem(MultiTrackerEditor.this.menu, 0);
/* 653 */           Messages.setLanguageText(item, "wizard.multitracker.edit.deletetracker");
/* 654 */           item.addListener(13, new Listener() {
/*     */             public void handleEvent(Event arg0) {
/* 656 */               treeItem.dispose();
/*     */             }
/*     */             
/* 659 */           });
/* 660 */           item = new MenuItem(MultiTrackerEditor.this.menu, 0);
/* 661 */           Messages.setLanguageText(item, "wizard.multitracker.edit.edit");
/* 662 */           item.addListener(13, new Listener() {
/*     */             public void handleEvent(Event arg0) {
/* 664 */               MultiTrackerEditor.this.editTreeItem(treeItem);
/*     */             }
/*     */           });
/*     */         }
/* 668 */         else if (type.equals("group"))
/*     */         {
/* 670 */           MenuItem item = new MenuItem(MultiTrackerEditor.this.menu, 0);
/* 671 */           Messages.setLanguageText(item, "wizard.multitracker.edit.newgroup");
/* 672 */           item.addListener(13, new Listener() {
/*     */             public void handleEvent(Event arg0) {
/* 674 */               TreeItem group = MultiTrackerEditor.this.newGroup();
/* 675 */               TreeItem itemTracker = MultiTrackerEditor.this.newTracker(group, "http://");
/* 676 */               MultiTrackerEditor.this.editTreeItem(itemTracker);
/*     */             }
/*     */             
/* 679 */           });
/* 680 */           item = new MenuItem(MultiTrackerEditor.this.menu, 0);
/* 681 */           Messages.setLanguageText(item, "wizard.multitracker.edit.deletegroup");
/* 682 */           item.addListener(13, new Listener() {
/*     */             public void handleEvent(Event arg0) {
/* 684 */               TreeItem[] subItems = treeItem.getItems();
/* 685 */               for (int i = 0; i < subItems.length; i++) {
/* 686 */                 subItems[i].dispose();
/*     */               }
/* 688 */               treeItem.dispose();
/*     */             }
/*     */             
/* 691 */           });
/* 692 */           new MenuItem(MultiTrackerEditor.this.menu, 2);
/*     */           
/* 694 */           item = new MenuItem(MultiTrackerEditor.this.menu, 0);
/* 695 */           Messages.setLanguageText(item, "wizard.multitracker.edit.newtracker");
/* 696 */           item.addListener(13, new Listener() {
/*     */             public void handleEvent(Event arg0) {
/* 698 */               TreeItem itemTracker = MultiTrackerEditor.this.newTracker(treeItem, "http://");
/* 699 */               MultiTrackerEditor.this.editTreeItem(itemTracker);
/*     */ 
/*     */             }
/*     */             
/*     */ 
/*     */ 
/*     */           });
/*     */ 
/*     */         }
/*     */         
/*     */ 
/*     */       }
/*     */       
/*     */ 
/* 713 */     });
/* 714 */     this.treeGroups.setMenu(this.menu);
/*     */   }
/*     */   
/*     */   private void editTreeItem(final TreeItem item)
/*     */   {
/* 719 */     Control oldEditor = this.editor.getEditor();
/* 720 */     if (oldEditor != null) {
/* 721 */       oldEditor.dispose();
/*     */     }
/* 723 */     this.itemEdited = item;
/*     */     
/* 725 */     final Text text = new Text(this.treeGroups, 2048);
/* 726 */     text.setText(item.getText());
/* 727 */     text.setForeground(item.getForeground());
/* 728 */     text.setSelection(item.getText().length());
/* 729 */     text.addListener(14, new Listener() {
/*     */       public void handleEvent(Event e) {
/* 731 */         String url = text.getText();
/* 732 */         if (MultiTrackerEditor.this.validURL(url)) {
/* 733 */           text.setForeground(null);
/* 734 */           item.setForeground(null);
/*     */         } else {
/* 736 */           text.setForeground(Colors.colorError);
/* 737 */           item.setForeground(Colors.colorError);
/*     */         }
/* 739 */         item.setText(url);
/* 740 */         MultiTrackerEditor.this.computeSaveEnable();
/* 741 */         MultiTrackerEditor.this.removeEditor();
/*     */       }
/*     */       
/* 744 */     });
/* 745 */     text.addListener(24, new Listener() {
/*     */       public void handleEvent(Event e) {
/* 747 */         String url = text.getText();
/* 748 */         if (MultiTrackerEditor.this.validURL(url)) {
/* 749 */           text.setForeground(null);
/* 750 */           item.setForeground(null);
/*     */         } else {
/* 752 */           text.setForeground(Colors.colorError);
/* 753 */           item.setForeground(Colors.colorError);
/*     */         }
/* 755 */         item.setText(url);
/* 756 */         MultiTrackerEditor.this.computeSaveEnable();
/*     */       }
/*     */       
/* 759 */     });
/* 760 */     text.addKeyListener(new KeyAdapter() {
/*     */       public void keyReleased(KeyEvent keyEvent) {
/* 762 */         if (keyEvent.character == '\033') {
/* 763 */           MultiTrackerEditor.this.removeEditor();
/*     */ 
/*     */         }
/*     */         
/*     */       }
/*     */       
/*     */ 
/* 770 */     });
/* 771 */     this.editor.horizontalAlignment = 16384;
/* 772 */     this.editor.grabHorizontal = true;
/* 773 */     this.editor.minimumWidth = 50;
/*     */     
/* 775 */     Rectangle r = text.computeTrim(0, 0, 100, text.getLineHeight());
/* 776 */     this.editor.minimumHeight = r.height;
/*     */     
/*     */ 
/*     */ 
/* 780 */     this.editor.setEditor(text, item);
/*     */     
/*     */ 
/* 783 */     text.setFocus();
/*     */   }
/*     */   
/*     */ 
/*     */   private boolean validURL(String str)
/*     */   {
/*     */     try
/*     */     {
/* 791 */       URL url = new URL(str);
/*     */       
/* 793 */       String prot = url.getProtocol().toLowerCase();
/*     */       
/* 795 */       if ((prot.equals("http")) || (prot.equals("https")) || (prot.equals("ws")) || (prot.equals("wss")) || (prot.equals("udp")) || (prot.equals("dht")))
/*     */       {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 802 */         return true;
/*     */       }
/*     */       
/* 805 */       return false;
/*     */     }
/*     */     catch (Throwable e) {}
/*     */     
/* 809 */     return false;
/*     */   }
/*     */   
/*     */   private void removeEditor()
/*     */   {
/* 814 */     Control oldEditor = this.editor.getEditor();
/* 815 */     if ((oldEditor != null) && (!oldEditor.isDisposed())) {
/* 816 */       oldEditor.dispose();
/*     */     }
/*     */   }
/*     */   
/*     */   private TreeItem newGroup() {
/* 821 */     TreeItem item = new TreeItem(this.treeGroups, 0);
/* 822 */     item.setData("type", "group");
/* 823 */     Messages.setLanguageText(item, "wizard.multitracker.group");
/* 824 */     return item;
/*     */   }
/*     */   
/*     */   private TreeItem newTracker(TreeItem root, String url) {
/* 828 */     TreeItem item = new TreeItem(root, 0);
/* 829 */     item.setText(url);
/* 830 */     item.setData("type", "tracker");
/* 831 */     root.setExpanded(true);
/* 832 */     return item;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/maketorrent/MultiTrackerEditor.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */