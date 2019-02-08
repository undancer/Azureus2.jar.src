/*     */ package org.gudy.azureus2.ui.swt;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import org.eclipse.swt.events.DisposeEvent;
/*     */ import org.eclipse.swt.events.DisposeListener;
/*     */ import org.eclipse.swt.events.FocusEvent;
/*     */ import org.eclipse.swt.events.FocusListener;
/*     */ import org.eclipse.swt.events.KeyAdapter;
/*     */ import org.eclipse.swt.events.KeyEvent;
/*     */ import org.eclipse.swt.events.MenuEvent;
/*     */ import org.eclipse.swt.events.MenuListener;
/*     */ import org.eclipse.swt.events.ModifyEvent;
/*     */ import org.eclipse.swt.events.ModifyListener;
/*     */ import org.eclipse.swt.events.MouseEvent;
/*     */ import org.eclipse.swt.events.MouseMoveListener;
/*     */ import org.eclipse.swt.events.MouseTrackAdapter;
/*     */ import org.eclipse.swt.events.PaintEvent;
/*     */ import org.eclipse.swt.events.PaintListener;
/*     */ import org.eclipse.swt.events.SelectionAdapter;
/*     */ import org.eclipse.swt.events.SelectionEvent;
/*     */ import org.eclipse.swt.graphics.Color;
/*     */ import org.eclipse.swt.graphics.GC;
/*     */ import org.eclipse.swt.graphics.Point;
/*     */ import org.eclipse.swt.graphics.Rectangle;
/*     */ import org.eclipse.swt.layout.GridData;
/*     */ import org.eclipse.swt.layout.GridLayout;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.Menu;
/*     */ import org.eclipse.swt.widgets.MenuItem;
/*     */ import org.eclipse.swt.widgets.Shell;
/*     */ import org.eclipse.swt.widgets.Text;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.config.ParameterListener;
/*     */ import org.gudy.azureus2.core3.config.StringList;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.core3.util.Constants;
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
/*     */ 
/*     */ public class TextWithHistory
/*     */ {
/*     */   private static final int MAX_MATCHES = 10;
/*     */   private static final int MAX_HISTORY = 64;
/*     */   private final boolean disabled;
/*     */   private final String config_prefix;
/*     */   private final Text text;
/*     */   private java.util.List<String> history;
/*     */   private Shell current_shell;
/*     */   private org.eclipse.swt.widgets.List list;
/*     */   private boolean mouse_entered;
/*     */   private boolean menu_visible;
/*     */   
/*     */   public TextWithHistory(String _config_prefix, Text _text)
/*     */   {
/*  89 */     this.config_prefix = _config_prefix;
/*  90 */     this.text = _text;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  97 */     this.disabled = Constants.isLinux;
/*     */     
/*  99 */     if (this.disabled)
/*     */     {
/* 101 */       return;
/*     */     }
/*     */     
/* 104 */     loadHistory();
/*     */     
/* 106 */     this.text.addModifyListener(new ModifyListener()
/*     */     {
/*     */ 
/*     */       public void modifyText(ModifyEvent e)
/*     */       {
/* 111 */         if (!COConfigurationManager.getBooleanParameter(TextWithHistory.this.config_prefix + ".enabled", true))
/*     */         {
/* 113 */           if (TextWithHistory.this.current_shell != null)
/*     */           {
/* 115 */             TextWithHistory.this.current_shell.dispose();
/*     */           }
/*     */           
/* 118 */           return;
/*     */         }
/*     */         
/* 121 */         String current_text = TextWithHistory.this.text.getText().trim();
/*     */         
/* 123 */         TextWithHistory.this.handleSearch(current_text, false);
/*     */       }
/*     */       
/* 126 */     });
/* 127 */     this.text.addFocusListener(new FocusListener()
/*     */     {
/*     */ 
/*     */       public void focusLost(FocusEvent e)
/*     */       {
/* 132 */         final Shell shell = TextWithHistory.this.current_shell;
/*     */         
/* 134 */         if (shell != null)
/*     */         {
/* 136 */           Utils.execSWTThreadLater(TextWithHistory.this.mouse_entered ? 500 : 0, new Runnable()
/*     */           {
/*     */ 
/*     */ 
/*     */             public void run()
/*     */             {
/*     */ 
/*     */ 
/* 144 */               if ((TextWithHistory.this.current_shell == shell) && (!TextWithHistory.this.menu_visible))
/*     */               {
/* 146 */                 shell.dispose();
/*     */               }
/*     */             }
/*     */           });
/*     */         }
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */       public void focusGained(FocusEvent e) {}
/* 156 */     });
/* 157 */     this.text.addKeyListener(new KeyAdapter() {
/*     */       public void keyPressed(KeyEvent e) {
/* 159 */         int key = e.keyCode;
/*     */         
/* 161 */         if ((TextWithHistory.this.list == null) || (TextWithHistory.this.list.isDisposed()))
/*     */         {
/*     */ 
/*     */ 
/* 165 */           if (key == 16777218) {
/* 166 */             String current_text = TextWithHistory.this.text.getText().trim();
/* 167 */             if (current_text.length() == 0) {
/* 168 */               TextWithHistory.this.handleSearch(current_text, true);
/* 169 */               e.doit = false;
/*     */             }
/*     */           }
/* 172 */           return;
/*     */         }
/*     */         
/* 175 */         if (key == 16777218) {
/* 176 */           e.doit = false;
/* 177 */           int curr = TextWithHistory.this.list.getSelectionIndex();
/* 178 */           curr++;
/* 179 */           if (curr < TextWithHistory.this.list.getItemCount()) {
/* 180 */             TextWithHistory.this.list.setSelection(curr);
/*     */           }
/* 182 */         } else if (key == 16777217) {
/* 183 */           int curr = TextWithHistory.this.list.getSelectionIndex();
/* 184 */           curr--;
/* 185 */           if (curr < 0) {
/* 186 */             TextWithHistory.this.list.deselectAll();
/*     */           } else {
/* 188 */             TextWithHistory.this.list.setSelection(curr);
/*     */           }
/* 190 */           e.doit = false;
/* 191 */         } else if (((key == 13) || (key == 10)) && 
/* 192 */           (TextWithHistory.this.fireSelected())) {
/* 193 */           e.doit = false;
/*     */         }
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private void handleSearch(String current_text, boolean force)
/*     */   {
/* 205 */     java.util.List<String> current_matches = match(current_text);
/*     */     
/* 207 */     if ((current_text.length() == 0) || (current_matches.size() == 0))
/*     */     {
/* 209 */       if (!force)
/*     */       {
/* 211 */         if (this.current_shell != null)
/*     */         {
/* 213 */           this.current_shell.dispose();
/*     */         }
/*     */         
/* 216 */         return;
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 221 */     if (this.current_shell == null)
/*     */     {
/* 223 */       this.mouse_entered = false;
/* 224 */       this.menu_visible = false;
/*     */       
/* 226 */       this.current_shell = new Shell(this.text.getShell(), 524296);
/*     */       
/* 228 */       this.current_shell.addDisposeListener(new DisposeListener()
/*     */       {
/*     */         public void widgetDisposed(DisposeEvent e)
/*     */         {
/* 232 */           TextWithHistory.this.current_shell = null;
/* 233 */           TextWithHistory.this.list = null;
/* 234 */           TextWithHistory.this.mouse_entered = false;
/* 235 */           TextWithHistory.this.menu_visible = false;
/*     */         }
/*     */         
/*     */       });
/*     */     }
/*     */     else
/*     */     {
/* 242 */       String[] items = this.list.getItems();
/*     */       
/* 244 */       if (items.length == current_matches.size())
/*     */       {
/* 246 */         boolean same = true;
/*     */         
/* 248 */         for (int i = 0; i < items.length; i++) {
/* 249 */           if (!items[i].equals(current_matches.get(i))) {
/* 250 */             same = false;
/* 251 */             break;
/*     */           }
/*     */         }
/*     */         
/* 255 */         if (same)
/*     */         {
/* 257 */           return;
/*     */         }
/*     */       }
/*     */       
/* 261 */       Utils.disposeComposite(this.current_shell, false);
/*     */     }
/*     */     
/* 264 */     GridLayout layout = new GridLayout();
/*     */     
/* 266 */     layout.marginHeight = 0;
/* 267 */     layout.marginWidth = 0;
/*     */     
/* 269 */     this.current_shell.setLayout(layout);
/*     */     
/* 271 */     Color background = this.text.getBackground();
/*     */     
/* 273 */     this.current_shell.setBackground(background);
/*     */     
/* 275 */     final Composite comp = new Composite(this.current_shell, 0);
/* 276 */     comp.setLayoutData(new GridData(1808));
/*     */     
/* 278 */     layout = new GridLayout();
/*     */     
/* 280 */     layout.marginHeight = 0;
/* 281 */     layout.marginWidth = 0;
/* 282 */     layout.marginLeft = 2;
/* 283 */     layout.marginRight = 2;
/* 284 */     layout.marginBottom = 2;
/*     */     
/* 286 */     comp.setLayout(layout);
/*     */     
/* 288 */     comp.setBackground(background);
/*     */     
/* 290 */     comp.addPaintListener(new PaintListener()
/*     */     {
/*     */       public void paintControl(PaintEvent e) {
/* 293 */         GC gc = e.gc;
/*     */         
/* 295 */         gc.setForeground(Colors.dark_grey);
/*     */         
/* 297 */         Rectangle bounds = comp.getBounds();
/*     */         
/* 299 */         gc.drawLine(0, 0, 0, bounds.height - 1);
/* 300 */         gc.drawLine(bounds.width - 1, 0, bounds.width - 1, bounds.height - 1);
/* 301 */         gc.drawLine(0, bounds.height - 1, bounds.width - 1, bounds.height - 1);
/*     */       }
/*     */       
/* 304 */     });
/* 305 */     this.list = new org.eclipse.swt.widgets.List(comp, 0);
/* 306 */     this.list.setLayoutData(new GridData(1808));
/*     */     
/* 308 */     for (String match : current_matches)
/*     */     {
/* 310 */       this.list.add(match);
/*     */     }
/*     */     
/* 313 */     this.list.setFont(this.text.getFont());
/* 314 */     this.list.setBackground(background);
/*     */     
/* 316 */     this.list.deselectAll();
/*     */     
/* 318 */     this.list.addMouseMoveListener(new MouseMoveListener()
/*     */     {
/*     */       public void mouseMove(MouseEvent e) {
/* 321 */         int item_height = TextWithHistory.this.list.getItemHeight();
/*     */         
/* 323 */         int y = e.y;
/*     */         
/* 325 */         int item_index = y / item_height;
/*     */         
/* 327 */         if (TextWithHistory.this.list.getSelectionIndex() != item_index) {
/* 328 */           TextWithHistory.this.list.setSelection(item_index);
/*     */         }
/*     */         
/*     */       }
/* 332 */     });
/* 333 */     this.list.addMouseTrackListener(new MouseTrackAdapter()
/*     */     {
/* 335 */       public void mouseEnter(MouseEvent e) { TextWithHistory.this.mouse_entered = true; }
/*     */       
/*     */       public void mouseExit(MouseEvent e) {
/* 338 */         TextWithHistory.this.list.deselectAll();
/* 339 */         TextWithHistory.this.mouse_entered = false;
/*     */       }
/*     */       
/* 342 */     });
/* 343 */     this.list.addSelectionListener(new SelectionAdapter()
/*     */     {
/*     */ 
/*     */       public void widgetSelected(SelectionEvent e)
/*     */       {
/* 348 */         TextWithHistory.this.fireSelected();
/*     */       }
/*     */       
/* 351 */     });
/* 352 */     Menu menu = new Menu(this.list);
/*     */     
/* 354 */     this.list.setMenu(menu);
/*     */     
/*     */ 
/*     */ 
/* 358 */     MenuItem mi = new MenuItem(menu, 8);
/*     */     
/* 360 */     mi.setText(MessageText.getString("label.clear.history"));
/*     */     
/* 362 */     mi.addSelectionListener(new SelectionAdapter()
/*     */     {
/*     */ 
/*     */       public void widgetSelected(SelectionEvent e)
/*     */       {
/* 367 */         TextWithHistory.this.clearHistory();
/*     */       }
/*     */       
/* 370 */     });
/* 371 */     mi = new MenuItem(menu, 2);
/*     */     
/*     */ 
/*     */ 
/* 375 */     mi = new MenuItem(menu, 8);
/*     */     
/* 377 */     mi.setText(MessageText.getString("label.disable.history"));
/*     */     
/* 379 */     mi.addSelectionListener(new SelectionAdapter()
/*     */     {
/*     */ 
/*     */       public void widgetSelected(SelectionEvent e)
/*     */       {
/* 384 */         COConfigurationManager.setParameter(TextWithHistory.this.config_prefix + ".enabled", false);
/*     */       }
/*     */       
/* 387 */     });
/* 388 */     menu.addMenuListener(new MenuListener()
/*     */     {
/*     */       public void menuShown(MenuEvent e)
/*     */       {
/* 392 */         TextWithHistory.this.menu_visible = true;
/*     */       }
/*     */       
/*     */       public void menuHidden(MenuEvent e) {
/* 396 */         TextWithHistory.this.menu_visible = false;
/*     */       }
/*     */       
/* 399 */     });
/* 400 */     this.current_shell.pack(true);
/* 401 */     this.current_shell.layout(true, true);
/*     */     
/* 403 */     Rectangle bounds = this.text.getBounds();
/*     */     
/* 405 */     Point shell_pos = this.text.toDisplay(0, bounds.height + (Constants.isOSX ? 2 : 0));
/*     */     
/* 407 */     this.current_shell.setLocation(shell_pos);
/*     */     
/* 409 */     Rectangle shell_size = this.current_shell.getBounds();
/*     */     
/* 411 */     shell_size.width += 4;
/*     */     
/* 413 */     if (shell_size.width > bounds.width)
/*     */     {
/* 415 */       shell_size.width = bounds.width;
/*     */     }
/* 417 */     else if ((shell_size.width < 200) && (bounds.width >= 200))
/*     */     {
/* 419 */       shell_size.width = 200;
/*     */     }
/*     */     
/* 422 */     this.current_shell.setBounds(shell_size);
/*     */     
/* 424 */     this.current_shell.setVisible(true);
/*     */   }
/*     */   
/*     */ 
/*     */   private boolean fireSelected()
/*     */   {
/* 430 */     String[] selection = this.list.getSelection();
/*     */     
/* 432 */     if (selection.length > 0)
/*     */     {
/* 434 */       String chars = selection[0];
/*     */       
/* 436 */       this.text.setText(chars);
/*     */       
/* 438 */       this.text.setSelection(chars.length());
/*     */       
/* 440 */       if (this.current_shell != null)
/*     */       {
/* 442 */         this.current_shell.dispose();
/*     */       }
/*     */       
/* 445 */       return true;
/*     */     }
/*     */     
/*     */ 
/* 449 */     if (this.current_shell != null)
/*     */     {
/* 451 */       this.current_shell.dispose();
/*     */     }
/*     */     
/* 454 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private java.util.List<String> match(String str)
/*     */   {
/* 462 */     str = str.trim();
/*     */     
/* 464 */     java.util.List<String> matches = new ArrayList();
/*     */     
/* 466 */     for (String h : this.history)
/*     */     {
/* 468 */       h = h.trim();
/*     */       
/* 470 */       if (h.startsWith(str))
/*     */       {
/* 472 */         matches.add(h);
/*     */         
/* 474 */         if (matches.size() == 10) {
/*     */           break;
/*     */         }
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 481 */     return matches;
/*     */   }
/*     */   
/*     */ 
/*     */   private void loadHistory()
/*     */   {
/* 487 */     COConfigurationManager.addAndFireParameterListener(this.config_prefix + ".data", new ParameterListener()
/*     */     {
/*     */ 
/*     */       public void parameterChanged(String name)
/*     */       {
/* 492 */         StringList sl = COConfigurationManager.getStringListParameter(name);
/*     */         
/* 494 */         TextWithHistory.this.history = new ArrayList();
/*     */         
/* 496 */         if (sl != null)
/*     */         {
/* 498 */           TextWithHistory.this.history.addAll(Arrays.asList(sl.toArray()));
/*     */         }
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */   private void clearHistory()
/*     */   {
/* 507 */     String key = this.config_prefix + ".data";
/*     */     
/* 509 */     StringList sl = COConfigurationManager.getStringListParameter(key);
/*     */     
/* 511 */     sl.clear();
/*     */     
/* 513 */     COConfigurationManager.setParameter(key, sl);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void addHistory(String str)
/*     */   {
/* 520 */     if (this.disabled)
/*     */     {
/* 522 */       return;
/*     */     }
/*     */     
/* 525 */     str = str.trim();
/*     */     
/* 527 */     String key = this.config_prefix + ".data";
/*     */     
/* 529 */     StringList sl = COConfigurationManager.getStringListParameter(key);
/*     */     
/* 531 */     sl.clear();
/*     */     
/* 533 */     sl.add(str);
/*     */     
/* 535 */     for (String h : this.history)
/*     */     {
/* 537 */       if (!str.startsWith(h))
/*     */       {
/* 539 */         sl.add(h);
/*     */         
/* 541 */         if (sl.size() == 64) {
/*     */           break;
/*     */         }
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 548 */     COConfigurationManager.setParameter(key, sl);
/*     */     
/* 550 */     COConfigurationManager.setDirty();
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/TextWithHistory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */