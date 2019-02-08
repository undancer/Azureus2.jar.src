/*     */ package org.gudy.azureus2.ui.swt.maketorrent;
/*     */ 
/*     */ import com.aelitis.azureus.util.MapUtils;
/*     */ import java.io.File;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import org.eclipse.swt.dnd.DragSource;
/*     */ import org.eclipse.swt.dnd.DragSourceEvent;
/*     */ import org.eclipse.swt.dnd.DragSourceListener;
/*     */ import org.eclipse.swt.dnd.DropTarget;
/*     */ import org.eclipse.swt.dnd.DropTargetAdapter;
/*     */ import org.eclipse.swt.dnd.DropTargetEvent;
/*     */ import org.eclipse.swt.dnd.FileTransfer;
/*     */ import org.eclipse.swt.dnd.TextTransfer;
/*     */ import org.eclipse.swt.dnd.Transfer;
/*     */ import org.eclipse.swt.events.DisposeEvent;
/*     */ import org.eclipse.swt.events.DisposeListener;
/*     */ import org.eclipse.swt.events.KeyEvent;
/*     */ import org.eclipse.swt.events.KeyListener;
/*     */ import org.eclipse.swt.events.SelectionEvent;
/*     */ import org.eclipse.swt.events.SelectionListener;
/*     */ import org.eclipse.swt.layout.GridData;
/*     */ import org.eclipse.swt.layout.GridLayout;
/*     */ import org.eclipse.swt.layout.RowLayout;
/*     */ import org.eclipse.swt.widgets.Button;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.DirectoryDialog;
/*     */ import org.eclipse.swt.widgets.FileDialog;
/*     */ import org.eclipse.swt.widgets.Tree;
/*     */ import org.eclipse.swt.widgets.TreeColumn;
/*     */ import org.eclipse.swt.widgets.TreeItem;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.core3.util.AETemporaryFileHandler;
/*     */ import org.gudy.azureus2.core3.util.BEncoder;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.FileUtil;
/*     */ import org.gudy.azureus2.ui.swt.Messages;
/*     */ import org.gudy.azureus2.ui.swt.SimpleTextEntryWindow;
/*     */ import org.gudy.azureus2.ui.swt.Utils;
/*     */ import org.gudy.azureus2.ui.swt.mainwindow.TorrentOpener;
/*     */ import org.gudy.azureus2.ui.swt.wizard.AbstractWizardPanel;
/*     */ import org.gudy.azureus2.ui.swt.wizard.IWizardPanel;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class BYOPanel
/*     */   extends AbstractWizardPanel<NewTorrentWizard>
/*     */ {
/*     */   private Tree tree;
/*     */   
/*     */   public BYOPanel(NewTorrentWizard wizard, IWizardPanel<NewTorrentWizard> previous)
/*     */   {
/*  57 */     super(wizard, previous);
/*     */     
/*  59 */     wizard.byo_map = null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void show()
/*     */   {
/*  68 */     ((NewTorrentWizard)this.wizard).setTitle(MessageText.getString("wizard.newtorrent.byo"));
/*  69 */     ((NewTorrentWizard)this.wizard).setCurrentInfo(MessageText.getString("wizard.newtorrent.byo.info"));
/*  70 */     Composite panel = ((NewTorrentWizard)this.wizard).getPanel();
/*  71 */     GridLayout layout = new GridLayout();
/*  72 */     layout.numColumns = 1;
/*  73 */     panel.setLayout(layout);
/*     */     
/*     */ 
/*     */ 
/*  77 */     this.tree = new Tree(panel, 2050);
/*  78 */     this.tree.setHeaderVisible(true);
/*  79 */     TreeColumn treeColumn = new TreeColumn(this.tree, 0);
/*  80 */     Messages.setLanguageText(treeColumn, "label.torrent.structure");
/*  81 */     treeColumn.setWidth(Utils.adjustPXForDPI(180));
/*  82 */     treeColumn = new TreeColumn(this.tree, 0);
/*  83 */     Messages.setLanguageText(treeColumn, "label.original.file");
/*  84 */     treeColumn.setWidth(Utils.adjustPXForDPI(500));
/*  85 */     GridData gridData = new GridData(1808);
/*  86 */     this.tree.setLayoutData(gridData);
/*     */     
/*  88 */     createDropTarget(this.tree);
/*  89 */     createDragSource(this.tree);
/*     */     
/*  91 */     this.tree.addSelectionListener(new SelectionListener()
/*     */     {
/*     */       public void widgetSelected(SelectionEvent e) {}
/*     */       
/*     */       public void widgetDefaultSelected(SelectionEvent e) {
/*  96 */         BYOPanel.this.editSelected();
/*  97 */         e.doit = false;
/*     */       }
/*     */       
/* 100 */     });
/* 101 */     this.tree.addKeyListener(new KeyListener()
/*     */     {
/*     */       public void keyReleased(KeyEvent e) {}
/*     */       
/*     */       public void keyPressed(KeyEvent e) {
/* 106 */         if (e.keyCode == 16777227) {
/* 107 */           BYOPanel.this.editSelected();
/* 108 */         } else if (e.keyCode == 127) {
/* 109 */           TreeItem[] selection = BYOPanel.this.tree.getSelection();
/* 110 */           for (TreeItem treeItem : selection) {
/* 111 */             Object data = treeItem.getData();
/* 112 */             treeItem.dispose();
/*     */           }
/*     */           
/*     */         }
/*     */       }
/* 117 */     });
/* 118 */     Composite cButtons = new Composite(panel, 0);
/* 119 */     Utils.setLayout(cButtons, new RowLayout());
/* 120 */     cButtons.setLayoutData(new GridData(768));
/*     */     
/* 122 */     Button btnAddContainer = new Button(cButtons, 8);
/* 123 */     Messages.setLanguageText(btnAddContainer, "button.add.container");
/* 124 */     btnAddContainer.addSelectionListener(new SelectionListener() {
/*     */       public void widgetSelected(SelectionEvent e) {
/* 126 */         SimpleTextEntryWindow entryWindow = new SimpleTextEntryWindow("wizard.newtorrent.byo.addcontainer.title", "wizard.newtorrent.byo.addcontainer.text");
/*     */         
/*     */ 
/* 129 */         entryWindow.setPreenteredText("files", true);
/* 130 */         entryWindow.prompt();
/* 131 */         if (entryWindow.hasSubmittedInput()) {
/* 132 */           BYOPanel.this.createContainer(null, entryWindow.getSubmittedInput());
/*     */         }
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */       public void widgetDefaultSelected(SelectionEvent e) {}
/* 139 */     });
/* 140 */     Button btnAddFiles = new Button(cButtons, 8);
/* 141 */     Messages.setLanguageText(btnAddFiles, "OpenTorrentWindow.addFiles");
/* 142 */     btnAddFiles.addSelectionListener(new SelectionListener() {
/*     */       public void widgetSelected(SelectionEvent e) {
/* 144 */         FileDialog fDialog = new FileDialog(Utils.findAnyShell(), 4098);
/* 145 */         fDialog.setFilterPath(TorrentOpener.getFilterPathData());
/* 146 */         fDialog.setText(MessageText.getString("MainWindow.dialog.choose.file"));
/* 147 */         if (fDialog.open() != null) {
/* 148 */           String[] fileNames = fDialog.getFileNames();
/* 149 */           File last_file = null;
/* 150 */           for (String fileName : fileNames) {
/* 151 */             File f = new File(fDialog.getFilterPath(), fileName);
/* 152 */             BYOPanel.this.addFilename(f);
/* 153 */             last_file = f;
/*     */           }
/*     */           
/* 156 */           if (last_file != null)
/*     */           {
/* 158 */             TorrentOpener.setFilterPathData(last_file.getAbsolutePath());
/*     */           }
/*     */         }
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */       public void widgetDefaultSelected(SelectionEvent e) {}
/* 166 */     });
/* 167 */     Button btnAddFolder = new Button(cButtons, 8);
/* 168 */     Messages.setLanguageText(btnAddFolder, "OpenTorrentWindow.addFiles.Folder");
/* 169 */     btnAddFolder.addSelectionListener(new SelectionListener() {
/*     */       public void widgetSelected(SelectionEvent e) {
/* 171 */         DirectoryDialog fDialog = new DirectoryDialog(Utils.findAnyShell(), 0);
/* 172 */         fDialog.setFilterPath(TorrentOpener.getFilterPathData());
/* 173 */         fDialog.setMessage(MessageText.getString("MainWindow.dialog.choose.folder"));
/* 174 */         String path = fDialog.open();
/* 175 */         if (path != null) {
/* 176 */           File f = new File(path);
/* 177 */           BYOPanel.this.addFilename(f);
/*     */           
/* 179 */           if (f.isDirectory()) {
/* 180 */             TorrentOpener.setFilterPathData(f.getAbsolutePath());
/*     */           }
/*     */         }
/*     */       }
/*     */       
/*     */ 
/*     */       public void widgetDefaultSelected(SelectionEvent e) {}
/*     */     });
/*     */     
/*     */     Iterator iterator;
/* 190 */     if (((NewTorrentWizard)this.wizard).byo_map != null) {
/* 191 */       List list = (List)((NewTorrentWizard)this.wizard).byo_map.get("file_map");
/* 192 */       if (list != null) {
/* 193 */         for (iterator = list.iterator(); iterator.hasNext();) {
/* 194 */           Map map = (Map)iterator.next();
/* 195 */           String target = MapUtils.getMapString(map, "target", null);
/* 196 */           List path = MapUtils.getMapList(map, "logical_path", null);
/* 197 */           if ((target != null) && (path != null)) {
/* 198 */             File targetFile = new File(target);
/* 199 */             if (path.size() == 1) {
/* 200 */               addFilename(targetFile, (String)path.get(0), null, true);
/*     */             } else {
/* 202 */               TreeItem[] items = this.tree.getItems();
/* 203 */               TreeItem parent = null;
/* 204 */               for (int i = 0; i < path.size() - 1; i++) {
/* 205 */                 TreeItem lastParent = parent;
/* 206 */                 String name = (String)path.get(i);
/*     */                 
/* 208 */                 boolean found = false;
/* 209 */                 for (TreeItem item : items) {
/* 210 */                   if (item.getText().equals(name)) {
/* 211 */                     parent = item;
/* 212 */                     found = true;
/* 213 */                     break;
/*     */                   }
/*     */                 }
/* 216 */                 if (!found) {
/* 217 */                   parent = createContainer(lastParent, name);
/*     */                 }
/* 219 */                 items = parent.getItems();
/*     */               }
/* 221 */               String name = (String)path.get(path.size() - 1);
/* 222 */               addFilename(targetFile, name, parent, false);
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   private void createDragSource(final Tree tree) {
/* 231 */     Transfer[] types = { TextTransfer.getInstance() };
/* 232 */     int operations = 2;
/*     */     
/* 234 */     final DragSource source = new DragSource(tree, operations);
/* 235 */     source.setTransfer(types);
/* 236 */     source.addDragListener(new DragSourceListener()
/*     */     {
/*     */       public void dragStart(DragSourceEvent event) {
/* 239 */         TreeItem[] selection = tree.getSelection();
/* 240 */         event.doit = (selection.length > 0);
/* 241 */         tree.setData("dragging", Integer.valueOf(1));
/*     */       }
/*     */       
/*     */       public void dragSetData(DragSourceEvent event) {
/* 245 */         event.data = "drag";
/* 246 */         event.detail = 2;
/*     */       }
/*     */       
/*     */       public void dragFinished(DragSourceEvent event) {
/* 250 */         tree.setData("dragging", null);
/*     */       }
/*     */       
/*     */ 
/* 254 */     });
/* 255 */     tree.addDisposeListener(new DisposeListener() {
/*     */       public void widgetDisposed(DisposeEvent e) {
/* 257 */         source.dispose();
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */   protected void editSelected() {
/* 263 */     TreeItem[] selection = this.tree.getSelection();
/* 264 */     if (selection.length == 1) {
/* 265 */       SimpleTextEntryWindow entryWindow = new SimpleTextEntryWindow("wizard.newtorrent.byo.editname.title", "wizard.newtorrent.byo.editname.text");
/*     */       
/*     */ 
/* 268 */       entryWindow.setPreenteredText(selection[0].getText(), false);
/* 269 */       entryWindow.prompt();
/* 270 */       if (entryWindow.hasSubmittedInput()) {
/* 271 */         selection[0].setText(entryWindow.getSubmittedInput());
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   private void createDropTarget(final Tree tree) {
/* 277 */     final DropTarget dropTarget = new DropTarget(tree, 6);
/*     */     
/* 279 */     dropTarget.setTransfer(new Transfer[] { TextTransfer.getInstance(), FileTransfer.getInstance() });
/*     */     
/*     */ 
/*     */ 
/* 283 */     dropTarget.addDropListener(new DropTargetAdapter() {
/*     */       public void dragOver(DropTargetEvent event) {
/* 285 */         event.detail = 16;
/* 286 */         event.feedback = 24;
/* 287 */         if ((event.item instanceof TreeItem)) {
/* 288 */           TreeItem item = (TreeItem)event.item;
/*     */           
/* 290 */           if (tree.getData("dragging") != null) {
/* 291 */             TreeItem[] selection = tree.getSelection();
/* 292 */             boolean ok = true;
/* 293 */             for (TreeItem treeItem : selection) {
/* 294 */               if (treeItem == item) {
/* 295 */                 ok = false;
/* 296 */                 break;
/*     */               }
/* 298 */               if (item.getData() == null)
/*     */               {
/* 300 */                 if (treeItem.getParentItem() == item) {
/* 301 */                   ok = false;
/* 302 */                   break;
/*     */                 }
/*     */               }
/* 305 */               else if (treeItem.getParentItem() == item.getParentItem()) {
/* 306 */                 ok = false;
/* 307 */                 break;
/*     */               }
/*     */             }
/*     */             
/* 311 */             if (!ok) {
/* 312 */               event.detail = 0;
/* 313 */               return;
/*     */             }
/*     */           }
/*     */           
/*     */ 
/* 318 */           if (item.getData() == null) {
/* 319 */             event.feedback |= 0x1;
/*     */           } else {
/* 321 */             event.feedback |= 0x4;
/*     */           }
/*     */         }
/*     */       }
/*     */       
/*     */       public void drop(DropTargetEvent event) {
/* 327 */         if ((event.data instanceof String[])) {
/* 328 */           String[] sourceNames = (String[])event.data;
/* 329 */           if (sourceNames == null)
/* 330 */             event.detail = 0;
/* 331 */           if (event.detail == 0) {
/* 332 */             return;
/*     */           }
/* 334 */           for (String droppedFileStr : sourceNames) {
/* 335 */             File droppedFile = new File(droppedFileStr);
/* 336 */             BYOPanel.this.addFilename(droppedFile, (TreeItem)event.item);
/*     */           }
/* 338 */         } else if ("drag".equals(event.data)) {
/* 339 */           TreeItem[] selection = tree.getSelection();
/* 340 */           for (TreeItem treeItem : selection) {
/* 341 */             if (!treeItem.isDisposed()) {
/* 342 */               BYOPanel.this.moveItem(treeItem, (TreeItem)event.item);
/*     */             }
/*     */             
/*     */           }
/*     */         }
/*     */       }
/* 348 */     });
/* 349 */     tree.addDisposeListener(new DisposeListener() {
/*     */       public void widgetDisposed(DisposeEvent e) {
/* 351 */         dropTarget.dispose();
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */   protected void addFilename(File file)
/*     */   {
/* 358 */     addFilename(file, file.getName(), null, false);
/*     */   }
/*     */   
/*     */   protected void addFilename(File file, TreeItem parent) {
/* 362 */     addFilename(file, file.getName(), parent, false);
/*     */   }
/*     */   
/*     */   protected void addFilename(File file, String name, TreeItem parent, boolean init) {
/* 366 */     if ((parent != null) && (parent.getData() != null)) {
/* 367 */       parent = parent.getParentItem();
/*     */     }
/* 369 */     TreeItem firstItem = this.tree.getItemCount() > 0 ? this.tree.getItem(0) : null;
/* 370 */     if ((firstItem != null) && (firstItem.getData() != null)) {
/* 371 */       parent = createContainer(null, file.getParentFile().getName());
/* 372 */     } else if (parent == null) {
/* 373 */       parent = firstItem;
/*     */     }
/*     */     
/* 376 */     if (!file.exists())
/*     */     {
/* 378 */       createContainer(null, name);
/*     */     }
/*     */     
/* 381 */     TreeItem treeItem = parent == null ? new TreeItem(this.tree, 0) : new TreeItem(parent, 0);
/*     */     
/* 383 */     treeItem.setText(new String[] { name, file.getAbsolutePath() });
/*     */     
/*     */ 
/*     */ 
/* 387 */     treeItem.setData(file);
/*     */     
/* 389 */     if (parent != null) {
/* 390 */       parent.setExpanded(true);
/*     */     }
/*     */     
/* 393 */     ((NewTorrentWizard)this.wizard).setNextEnabled(this.tree.getItemCount() >= 1);
/*     */   }
/*     */   
/*     */   private TreeItem createContainer(TreeItem parent, String name) {
/* 397 */     TreeItem[] selection = this.tree.getSelection();
/*     */     
/* 399 */     if (parent == null) {
/* 400 */       if ((selection.length == 1) && (selection[0].getData() == null)) {
/* 401 */         parent = selection[0];
/* 402 */       } else if (selection.length > 0) {
/* 403 */         parent = selection[0].getParentItem();
/*     */       }
/*     */       else {
/* 406 */         TreeItem firstItem = this.tree.getItemCount() > 0 ? this.tree.getItem(0) : null;
/* 407 */         if ((firstItem != null) && (firstItem.getData() == null)) {
/* 408 */           parent = firstItem;
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 413 */     TreeItem item = parent == null ? new TreeItem(this.tree, 0, 0) : new TreeItem(parent, 0, 0);
/* 414 */     item.setText(new String[] { name, MessageText.getString("label.container.display") });
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 419 */     while (this.tree.getItemCount() > 1) {
/* 420 */       TreeItem itemToMove = this.tree.getItem(1);
/* 421 */       moveItem(itemToMove, item);
/*     */     }
/* 423 */     item.setExpanded(true);
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
/* 434 */     return item;
/*     */   }
/*     */   
/*     */   private void moveItem(TreeItem itemToMove, TreeItem parent) {
/* 438 */     if (parent == null) {
/* 439 */       if (this.tree.getItemCount() == 0) {
/* 440 */         return;
/*     */       }
/* 442 */       parent = this.tree.getItem(0);
/*     */     }
/* 444 */     File parentFile = (File)parent.getData();
/* 445 */     if ((parentFile != null) && (!parentFile.isDirectory())) {
/* 446 */       parent = parent.getParentItem();
/*     */     }
/* 448 */     TreeItem itemNew = new TreeItem(parent, 0);
/* 449 */     for (int i = 0; i < this.tree.getColumnCount(); i++) {
/* 450 */       itemNew.setText(i, itemToMove.getText(i));
/*     */     }
/* 452 */     File file = (File)itemToMove.getData();
/* 453 */     itemNew.setData(file);
/* 454 */     while (itemToMove.getItemCount() > 0) {
/* 455 */       TreeItem subitemToMove = itemToMove.getItem(0);
/* 456 */       moveItem(subitemToMove, itemNew);
/*     */     }
/* 458 */     itemToMove.dispose();
/*     */   }
/*     */   
/*     */   public IWizardPanel<NewTorrentWizard> getNextPanel() {
/* 462 */     if (this.tree.getItemCount() == 1)
/*     */     {
/* 464 */       TreeItem item = this.tree.getItem(0);
/* 465 */       String name = item.getText();
/* 466 */       File file = (File)item.getData();
/* 467 */       if ((file != null) && (file.getName().equals(name)) && (file.exists())) {
/* 468 */         String parent = file.getParent();
/* 469 */         if (parent != null) {
/* 470 */           ((NewTorrentWizard)this.wizard).setDefaultOpenDir(parent);
/*     */         }
/*     */         
/* 473 */         if (file.isDirectory()) {
/* 474 */           ((NewTorrentWizard)this.wizard).directoryPath = file.getAbsolutePath();
/* 475 */           ((NewTorrentWizard)this.wizard);((NewTorrentWizard)this.wizard).create_mode = 2;
/*     */           
/* 477 */           return new SavePathPanel((NewTorrentWizard)this.wizard, this);
/*     */         }
/* 479 */         ((NewTorrentWizard)this.wizard).singlePath = file.getAbsolutePath();
/* 480 */         ((NewTorrentWizard)this.wizard);((NewTorrentWizard)this.wizard).create_mode = 1;
/* 481 */         return new SavePathPanel((NewTorrentWizard)this.wizard, this);
/*     */       }
/*     */     }
/*     */     
/* 485 */     Map map = new HashMap();
/*     */     
/* 487 */     List<Map> list = new ArrayList();
/*     */     
/* 489 */     map.put("file_map", list);
/*     */     
/* 491 */     buildList(list, this.tree.getItems());
/*     */     
/* 493 */     ((NewTorrentWizard)this.wizard).byo_map = map;
/*     */     try
/*     */     {
/* 496 */       ((NewTorrentWizard)this.wizard).byo_desc_file = AETemporaryFileHandler.createTempFile();
/*     */       
/* 498 */       FileUtil.writeBytesAsFile(((NewTorrentWizard)this.wizard).byo_desc_file.getAbsolutePath(), BEncoder.encode(map));
/*     */ 
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/* 503 */       Debug.out(e);
/*     */     }
/*     */     
/* 506 */     return new SavePathPanel((NewTorrentWizard)this.wizard, this);
/*     */   }
/*     */   
/*     */   private void buildList(List list, TreeItem[] items) {
/* 510 */     for (TreeItem treeItem : items) {
/* 511 */       if ((treeItem != null) && (!treeItem.isDisposed()))
/*     */       {
/*     */ 
/*     */ 
/* 515 */         TreeItem[] subItems = treeItem.getItems();
/* 516 */         File file = (File)treeItem.getData();
/* 517 */         if (file != null) {
/* 518 */           Map m = new HashMap();
/*     */           
/* 520 */           list.add(m);
/*     */           
/* 522 */           List<String> path = new ArrayList();
/*     */           do {
/* 524 */             path.add(0, treeItem.getText());
/* 525 */             treeItem = treeItem.getParentItem();
/* 526 */           } while (treeItem != null);
/*     */           
/*     */ 
/* 529 */           m.put("logical_path", path);
/* 530 */           m.put("target", file.getAbsolutePath());
/*     */         }
/* 532 */         if (subItems.length > 0) {
/* 533 */           buildList(list, subItems);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/maketorrent/BYOPanel.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */