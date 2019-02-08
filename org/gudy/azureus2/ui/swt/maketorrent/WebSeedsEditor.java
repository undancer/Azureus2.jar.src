/*     */ package org.gudy.azureus2.ui.swt.maketorrent;
/*     */ 
/*     */ import java.net.URL;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.Set;
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
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.Control;
/*     */ import org.eclipse.swt.widgets.Display;
/*     */ import org.eclipse.swt.widgets.Event;
/*     */ import org.eclipse.swt.widgets.Label;
/*     */ import org.eclipse.swt.widgets.Listener;
/*     */ import org.eclipse.swt.widgets.Menu;
/*     */ import org.eclipse.swt.widgets.MenuItem;
/*     */ import org.eclipse.swt.widgets.Shell;
/*     */ import org.eclipse.swt.widgets.Text;
/*     */ import org.eclipse.swt.widgets.Tree;
/*     */ import org.eclipse.swt.widgets.TreeItem;
/*     */ import org.gudy.azureus2.ui.swt.Messages;
/*     */ import org.gudy.azureus2.ui.swt.Utils;
/*     */ import org.gudy.azureus2.ui.swt.components.shell.ShellFactory;
/*     */ import org.gudy.azureus2.ui.swt.mainwindow.Colors;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class WebSeedsEditor
/*     */ {
/*     */   WebSeedsEditorListener listener;
/*     */   String oldName;
/*     */   String currentName;
/*     */   boolean anonymous;
/*     */   Map webseeds;
/*     */   Display display;
/*     */   Shell shell;
/*     */   Text textName;
/*     */   Tree treeGroups;
/*     */   TreeEditor editor;
/*     */   TreeItem itemEdited;
/*     */   Button btnSave;
/*     */   Button btnCancel;
/*     */   Menu menu;
/*     */   
/*     */   public WebSeedsEditor(String name, Map webseeds, WebSeedsEditorListener listener)
/*     */   {
/*  69 */     this(name, webseeds, listener, false);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public WebSeedsEditor(String name, Map webseeds, WebSeedsEditorListener listener, boolean _anonymous)
/*     */   {
/*  79 */     this.oldName = name;
/*  80 */     if (name != null) {
/*  81 */       this.currentName = name;
/*     */     } else
/*  83 */       this.currentName = "";
/*  84 */     this.listener = listener;
/*  85 */     this.anonymous = _anonymous;
/*  86 */     this.webseeds = new HashMap(webseeds);
/*  87 */     createWindow();
/*     */   }
/*     */   
/*     */   private void createWindow()
/*     */   {
/*  92 */     this.display = Display.getCurrent();
/*  93 */     this.shell = ShellFactory.createShell(67680);
/*  94 */     Messages.setLanguageText(this.shell, "wizard.webseedseditor.edit.title");
/*  95 */     Utils.setShellIcon(this.shell);
/*  96 */     GridLayout layout = new GridLayout();
/*  97 */     layout.numColumns = 3;
/*  98 */     this.shell.setLayout(layout);
/*     */     
/*     */ 
/*     */ 
/* 102 */     if (!this.anonymous)
/*     */     {
/* 104 */       Label labelName = new Label(this.shell, 0);
/* 105 */       Messages.setLanguageText(labelName, "wizard.multitracker.edit.name");
/*     */       
/* 107 */       this.textName = new Text(this.shell, 2048);
/* 108 */       this.textName.setText(this.currentName);
/* 109 */       GridData gridData = new GridData(768);
/* 110 */       gridData.horizontalSpan = 2;
/* 111 */       Utils.setLayoutData(this.textName, gridData);
/* 112 */       this.textName.addModifyListener(new ModifyListener() {
/*     */         public void modifyText(ModifyEvent arg0) {
/* 114 */           WebSeedsEditor.this.currentName = WebSeedsEditor.this.textName.getText();
/* 115 */           WebSeedsEditor.this.computeSaveEnable();
/*     */         }
/*     */       });
/*     */     }
/*     */     
/* 120 */     this.treeGroups = new Tree(this.shell, 2048);
/* 121 */     GridData gridData = new GridData(1808);
/* 122 */     gridData.horizontalSpan = 3;
/* 123 */     gridData.heightHint = 150;
/* 124 */     Utils.setLayoutData(this.treeGroups, gridData);
/*     */     
/* 126 */     this.treeGroups.addMouseListener(new MouseAdapter()
/*     */     {
/*     */ 
/*     */ 
/*     */       public void mouseDoubleClick(MouseEvent arg0)
/*     */       {
/*     */ 
/* 133 */         if (WebSeedsEditor.this.treeGroups.getSelectionCount() == 1) {
/* 134 */           TreeItem treeItem = WebSeedsEditor.this.treeGroups.getSelection()[0];
/* 135 */           String type = (String)treeItem.getData("type");
/* 136 */           if (type.equals("tracker")) {
/* 137 */             WebSeedsEditor.this.editTreeItem(treeItem);
/*     */           }
/*     */           
/*     */         }
/*     */       }
/* 142 */     });
/* 143 */     Label labelSeparator = new Label(this.shell, 258);
/* 144 */     gridData = new GridData(768);
/* 145 */     gridData.horizontalSpan = 3;
/* 146 */     Utils.setLayoutData(labelSeparator, gridData);
/*     */     
/*     */ 
/*     */ 
/* 150 */     Label label = new Label(this.shell, 0);
/* 151 */     gridData = new GridData(768);
/* 152 */     Utils.setLayoutData(label, gridData);
/*     */     
/* 154 */     Composite cButtons = new Composite(this.shell, 0);
/* 155 */     gridData = new GridData(768);
/* 156 */     gridData.horizontalSpan = 3;
/* 157 */     Utils.setLayoutData(cButtons, gridData);
/* 158 */     GridLayout layoutButtons = new GridLayout();
/* 159 */     layoutButtons.numColumns = 3;
/* 160 */     cButtons.setLayout(layoutButtons);
/* 161 */     label = new Label(cButtons, 0);
/* 162 */     gridData = new GridData(768);
/* 163 */     Utils.setLayoutData(label, gridData);
/*     */     
/* 165 */     this.btnSave = new Button(cButtons, 8);
/* 166 */     gridData = new GridData();
/* 167 */     gridData.widthHint = 70;
/* 168 */     gridData.horizontalAlignment = 3;
/* 169 */     Utils.setLayoutData(this.btnSave, gridData);
/* 170 */     Messages.setLanguageText(this.btnSave, "wizard.multitracker.edit.save");
/* 171 */     this.btnSave.addListener(13, new Listener() {
/*     */       public void handleEvent(Event e) {
/* 173 */         WebSeedsEditor.this.update();
/* 174 */         WebSeedsEditor.this.shell.dispose();
/*     */       }
/*     */       
/* 177 */     });
/* 178 */     this.btnCancel = new Button(cButtons, 8);
/* 179 */     gridData = new GridData();
/* 180 */     gridData.horizontalAlignment = 3;
/* 181 */     gridData.widthHint = 70;
/* 182 */     Utils.setLayoutData(this.btnCancel, gridData);
/* 183 */     Messages.setLanguageText(this.btnCancel, "Button.cancel");
/* 184 */     this.btnCancel.addListener(13, new Listener() {
/*     */       public void handleEvent(Event e) {
/* 186 */         WebSeedsEditor.this.shell.dispose();
/*     */       }
/*     */       
/* 189 */     });
/* 190 */     this.shell.setDefaultButton(this.btnSave);
/*     */     
/* 192 */     this.shell.addListener(31, new Listener() {
/*     */       public void handleEvent(Event e) {
/* 194 */         if (e.character == '\033') {
/* 195 */           WebSeedsEditor.this.shell.dispose();
/*     */         }
/*     */         
/*     */       }
/*     */       
/* 200 */     });
/* 201 */     computeSaveEnable();
/* 202 */     refresh();
/* 203 */     constructMenu();
/*     */     
/* 205 */     this.editor = new TreeEditor(this.treeGroups);
/* 206 */     this.treeGroups.addSelectionListener(new SelectionAdapter() {
/*     */       public void widgetSelected(SelectionEvent arg0) {
/* 208 */         if ((WebSeedsEditor.this.itemEdited != null) && (!WebSeedsEditor.this.editor.getEditor().isDisposed()))
/* 209 */           WebSeedsEditor.this.itemEdited.setText(((Text)WebSeedsEditor.this.editor.getEditor()).getText());
/* 210 */         WebSeedsEditor.this.removeEditor();
/*     */       }
/*     */       
/* 213 */     });
/* 214 */     this.shell.pack();
/*     */     
/* 216 */     Point size = this.shell.computeSize(400, -1);
/* 217 */     this.shell.setSize(size);
/*     */     
/* 219 */     Utils.centreWindow(this.shell);
/*     */     
/* 221 */     this.shell.open();
/*     */   }
/*     */   
/*     */   private void update() {
/* 225 */     this.webseeds = new HashMap();
/* 226 */     TreeItem[] groupItems = this.treeGroups.getItems();
/*     */     
/* 228 */     for (int i = 0; i < groupItems.length; i++) {
/* 229 */       TreeItem group = groupItems[i];
/* 230 */       TreeItem[] trackerItems = group.getItems();
/* 231 */       List groupList = new ArrayList(group.getItemCount());
/* 232 */       for (int j = 0; j < trackerItems.length; j++) {
/* 233 */         groupList.add(trackerItems[j].getText());
/*     */       }
/* 235 */       this.webseeds.put(group.getText(), groupList);
/*     */     }
/*     */     
/* 238 */     this.listener.webSeedsChanged(this.oldName, this.currentName, this.webseeds);
/* 239 */     this.oldName = this.currentName;
/*     */   }
/*     */   
/*     */   private void computeSaveEnable()
/*     */   {
/* 244 */     boolean enabled = (this.anonymous) || (!"".equals(this.currentName));
/*     */     
/* 246 */     if (enabled)
/*     */     {
/* 248 */       TreeItem[] groupItems = this.treeGroups.getItems();
/*     */       
/*     */ 
/* 251 */       for (int i = 0; i < groupItems.length; i++) {
/* 252 */         TreeItem group = groupItems[i];
/* 253 */         TreeItem[] trackerItems = group.getItems();
/* 254 */         for (int j = 0; j < trackerItems.length; j++)
/*     */         {
/* 256 */           if (!validURL(trackerItems[j].getText()))
/*     */           {
/* 258 */             enabled = false;
/*     */             
/*     */             break label100;
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */     label100:
/* 266 */     if (enabled != this.btnSave.getEnabled())
/*     */     {
/* 268 */       this.btnSave.setEnabled(enabled);
/*     */     }
/*     */   }
/*     */   
/*     */   private void refresh() {
/* 273 */     this.treeGroups.removeAll();
/* 274 */     Iterator iter = this.webseeds.entrySet().iterator();
/* 275 */     while (iter.hasNext()) {
/* 276 */       Map.Entry entry = (Map.Entry)iter.next();
/* 277 */       TreeItem itemRoot = newGroup((String)entry.getKey());
/* 278 */       Iterator iter2 = ((List)entry.getValue()).iterator();
/* 279 */       while (iter2.hasNext()) {
/* 280 */         String url = (String)iter2.next();
/* 281 */         newTracker(itemRoot, url);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   private void constructMenu() {
/* 287 */     this.menu = new Menu(this.shell, 0);
/* 288 */     this.menu.addListener(22, new Listener()
/*     */     {
/*     */       public void handleEvent(Event e) {
/* 291 */         MenuItem[] items = WebSeedsEditor.this.menu.getItems();
/* 292 */         for (int i = 0; i < items.length; i++) {
/* 293 */           items[i].dispose();
/*     */         }
/*     */         
/*     */ 
/*     */ 
/* 298 */         final TreeItem treeItem = WebSeedsEditor.this.treeGroups.getSelection()[0];
/* 299 */         String type = (String)treeItem.getData("type");
/* 300 */         if (type.equals("tracker"))
/*     */         {
/* 302 */           MenuItem item = new MenuItem(WebSeedsEditor.this.menu, 0);
/* 303 */           Messages.setLanguageText(item, "wizard.multitracker.edit.deletetracker");
/* 304 */           item.addListener(13, new Listener() {
/*     */             public void handleEvent(Event arg0) {
/* 306 */               treeItem.dispose();
/*     */             }
/*     */             
/* 309 */           });
/* 310 */           item = new MenuItem(WebSeedsEditor.this.menu, 0);
/* 311 */           Messages.setLanguageText(item, "wizard.multitracker.edit.edit");
/* 312 */           item.addListener(13, new Listener() {
/*     */             public void handleEvent(Event arg0) {
/* 314 */               WebSeedsEditor.this.editTreeItem(treeItem);
/*     */             }
/*     */           });
/*     */         }
/* 318 */         else if (type.equals("group"))
/*     */         {
/*     */ 
/* 321 */           MenuItem item = new MenuItem(WebSeedsEditor.this.menu, 0);
/* 322 */           Messages.setLanguageText(item, "wizard.webseedseditor.edit.newseed");
/* 323 */           item.addListener(13, new Listener() {
/*     */             public void handleEvent(Event arg0) {
/* 325 */               TreeItem itemTracker = WebSeedsEditor.this.newTracker(treeItem, "http://");
/* 326 */               WebSeedsEditor.this.editTreeItem(itemTracker);
/*     */             }
/*     */           });
/*     */         }
/*     */       }
/* 331 */     });
/* 332 */     this.treeGroups.setMenu(this.menu);
/*     */   }
/*     */   
/*     */   private void editTreeItem(final TreeItem item)
/*     */   {
/* 337 */     Control oldEditor = this.editor.getEditor();
/* 338 */     if (oldEditor != null) {
/* 339 */       oldEditor.dispose();
/*     */     }
/* 341 */     this.itemEdited = item;
/*     */     
/* 343 */     final Text text = new Text(this.treeGroups, 2048);
/* 344 */     text.setText(item.getText());
/* 345 */     text.setForeground(item.getForeground());
/* 346 */     text.setSelection(item.getText().length());
/* 347 */     text.addListener(14, new Listener() {
/*     */       public void handleEvent(Event e) {
/* 349 */         String url = text.getText();
/* 350 */         if (WebSeedsEditor.this.validURL(url)) {
/* 351 */           text.setForeground(null);
/* 352 */           item.setForeground(null);
/*     */         } else {
/* 354 */           text.setForeground(Colors.colorError);
/* 355 */           item.setForeground(Colors.colorError);
/*     */         }
/* 357 */         item.setText(url);
/* 358 */         WebSeedsEditor.this.computeSaveEnable();
/* 359 */         WebSeedsEditor.this.removeEditor();
/*     */       }
/*     */       
/* 362 */     });
/* 363 */     text.addListener(24, new Listener() {
/*     */       public void handleEvent(Event e) {
/* 365 */         String url = text.getText();
/* 366 */         if (WebSeedsEditor.this.validURL(url)) {
/* 367 */           text.setForeground(null);
/* 368 */           item.setForeground(null);
/*     */         } else {
/* 370 */           text.setForeground(Colors.colorError);
/* 371 */           item.setForeground(Colors.colorError);
/*     */         }
/* 373 */         item.setText(url);
/* 374 */         WebSeedsEditor.this.computeSaveEnable();
/*     */       }
/*     */       
/* 377 */     });
/* 378 */     text.addKeyListener(new KeyAdapter() {
/*     */       public void keyReleased(KeyEvent keyEvent) {
/* 380 */         if (keyEvent.character == '\033') {
/* 381 */           WebSeedsEditor.this.removeEditor();
/*     */ 
/*     */         }
/*     */         
/*     */       }
/*     */       
/*     */ 
/* 388 */     });
/* 389 */     this.editor.horizontalAlignment = 16384;
/* 390 */     this.editor.grabHorizontal = true;
/* 391 */     this.editor.minimumWidth = 50;
/*     */     
/* 393 */     Rectangle r = text.computeTrim(0, 0, 100, text.getLineHeight());
/* 394 */     this.editor.minimumHeight = r.height;
/*     */     
/*     */ 
/*     */ 
/* 398 */     this.editor.setEditor(text, item);
/*     */     
/*     */ 
/* 401 */     text.setFocus();
/*     */   }
/*     */   
/*     */ 
/*     */   private boolean validURL(String str)
/*     */   {
/*     */     try
/*     */     {
/* 409 */       URL url = new URL(str);
/*     */       
/* 411 */       String prot = url.getProtocol().toLowerCase();
/*     */       
/* 413 */       if (prot.startsWith("http"))
/*     */       {
/* 415 */         return true;
/*     */       }
/*     */       
/* 418 */       return false;
/*     */     }
/*     */     catch (Throwable e) {}
/*     */     
/* 422 */     return false;
/*     */   }
/*     */   
/*     */   private void removeEditor()
/*     */   {
/* 427 */     Control oldEditor = this.editor.getEditor();
/* 428 */     if (oldEditor != null)
/* 429 */       oldEditor.dispose();
/*     */   }
/*     */   
/*     */   private TreeItem newGroup(String name) {
/* 433 */     TreeItem item = new TreeItem(this.treeGroups, 0);
/* 434 */     item.setData("type", "group");
/* 435 */     item.setText(name);
/* 436 */     return item;
/*     */   }
/*     */   
/*     */   private TreeItem newTracker(TreeItem root, String url) {
/* 440 */     TreeItem item = new TreeItem(root, 0);
/* 441 */     item.setText(url);
/* 442 */     item.setData("type", "tracker");
/* 443 */     root.setExpanded(true);
/* 444 */     return item;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/maketorrent/WebSeedsEditor.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */