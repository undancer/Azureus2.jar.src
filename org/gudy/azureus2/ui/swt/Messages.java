/*     */ package org.gudy.azureus2.ui.swt;
/*     */ 
/*     */ import java.io.PrintStream;
/*     */ import java.util.regex.Matcher;
/*     */ import java.util.regex.Pattern;
/*     */ import org.eclipse.swt.custom.CLabel;
/*     */ import org.eclipse.swt.custom.CTabFolder;
/*     */ import org.eclipse.swt.custom.CTabItem;
/*     */ import org.eclipse.swt.widgets.Button;
/*     */ import org.eclipse.swt.widgets.Canvas;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.Control;
/*     */ import org.eclipse.swt.widgets.CoolBar;
/*     */ import org.eclipse.swt.widgets.CoolItem;
/*     */ import org.eclipse.swt.widgets.Event;
/*     */ import org.eclipse.swt.widgets.Group;
/*     */ import org.eclipse.swt.widgets.Label;
/*     */ import org.eclipse.swt.widgets.Listener;
/*     */ import org.eclipse.swt.widgets.Menu;
/*     */ import org.eclipse.swt.widgets.MenuItem;
/*     */ import org.eclipse.swt.widgets.Shell;
/*     */ import org.eclipse.swt.widgets.TabFolder;
/*     */ import org.eclipse.swt.widgets.TabItem;
/*     */ import org.eclipse.swt.widgets.Table;
/*     */ import org.eclipse.swt.widgets.TableColumn;
/*     */ import org.eclipse.swt.widgets.Text;
/*     */ import org.eclipse.swt.widgets.ToolBar;
/*     */ import org.eclipse.swt.widgets.ToolItem;
/*     */ import org.eclipse.swt.widgets.Tree;
/*     */ import org.eclipse.swt.widgets.TreeColumn;
/*     */ import org.eclipse.swt.widgets.TreeItem;
/*     */ import org.eclipse.swt.widgets.Widget;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.core3.util.Constants;
/*     */ import org.gudy.azureus2.ui.swt.components.DoubleBufferedLabel;
/*     */ 
/*     */ public class Messages
/*     */ {
/*  39 */   private static final Pattern HIG_ELLIP_EXP = Pattern.compile("([\\.]{3})");
/*     */   
/*     */ 
/*     */ 
/*  43 */   private static Listener hoverListener = new Listener() {
/*     */     public void handleEvent(Event event) {
/*  45 */       Messages.updateToolTipFromData(event.widget, (event.stateMask & 0x40000) > 0);
/*     */     }
/*     */   };
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static void updateLanguageForControl(Widget widget)
/*     */   {
/*  56 */     if ((widget == null) || (widget.isDisposed())) {
/*  57 */       return;
/*     */     }
/*  59 */     updateLanguageFromData(widget, null);
/*  60 */     widget.removeListener(32, hoverListener);
/*  61 */     widget.addListener(32, hoverListener);
/*     */     
/*  63 */     if ((widget instanceof CTabFolder)) {
/*  64 */       CTabFolder folder = (CTabFolder)widget;
/*  65 */       CTabItem[] items = folder.getItems();
/*  66 */       for (int i = 0; i < items.length; i++) {
/*  67 */         updateLanguageForControl(items[i]);
/*  68 */         updateLanguageForControl(items[i].getControl());
/*     */       }
/*  70 */     } else if ((widget instanceof TabFolder)) {
/*  71 */       TabFolder folder = (TabFolder)widget;
/*  72 */       TabItem[] items = folder.getItems();
/*  73 */       for (int i = 0; i < items.length; i++) {
/*  74 */         updateLanguageForControl(items[i]);
/*  75 */         updateLanguageForControl(items[i].getControl());
/*     */       }
/*     */     }
/*  78 */     else if ((widget instanceof CoolBar)) {
/*  79 */       CoolItem[] items = ((CoolBar)widget).getItems();
/*  80 */       for (int i = 0; i < items.length; i++) {
/*  81 */         Control control = items[i].getControl();
/*  82 */         updateLanguageForControl(control);
/*     */       }
/*     */     }
/*  85 */     else if ((widget instanceof ToolBar)) {
/*  86 */       ToolItem[] items = ((ToolBar)widget).getItems();
/*  87 */       for (int i = 0; i < items.length; i++) {
/*  88 */         updateLanguageForControl(items[i]);
/*     */       }
/*     */     }
/*  91 */     else if ((widget instanceof Composite)) {
/*  92 */       Composite group = (Composite)widget;
/*  93 */       Control[] controls = group.getChildren();
/*  94 */       for (int i = 0; i < controls.length; i++) {
/*  95 */         updateLanguageForControl(controls[i]);
/*     */       }
/*  97 */       if ((widget instanceof Table)) {
/*  98 */         Table table = (Table)widget;
/*  99 */         TableColumn[] columns = table.getColumns();
/* 100 */         for (int i = 0; i < columns.length; i++) {
/* 101 */           updateLanguageFromData(columns[i], null);
/*     */         }
/* 103 */         updateLanguageForControl(table.getMenu());
/*     */         
/*     */ 
/*     */ 
/* 107 */         Event event = new Event();
/* 108 */         event.type = 39;
/* 109 */         event.widget = widget;
/* 110 */         widget.notifyListeners(39, event);
/*     */       }
/* 112 */       else if ((widget instanceof Tree)) {
/* 113 */         Tree tree = (Tree)widget;
/* 114 */         TreeItem[] treeitems = tree.getItems();
/* 115 */         for (int i = 0; i < treeitems.length; i++) {
/* 116 */           updateLanguageForControl(treeitems[i]);
/*     */         }
/*     */       }
/*     */       
/* 120 */       group.layout();
/*     */     }
/* 122 */     else if ((widget instanceof MenuItem)) {
/* 123 */       MenuItem menuItem = (MenuItem)widget;
/* 124 */       updateLanguageForControl(menuItem.getMenu());
/*     */     }
/* 126 */     else if ((widget instanceof Menu)) {
/* 127 */       Menu menu = (Menu)widget;
/* 128 */       if (menu.getStyle() == 8) {
/* 129 */         System.out.println("POP_UP");
/*     */       }
/* 131 */       MenuItem[] items = menu.getItems();
/* 132 */       for (int i = 0; i < items.length; i++) {
/* 133 */         updateLanguageForControl(items[i]);
/*     */       }
/*     */     }
/* 136 */     else if ((widget instanceof TreeItem)) {
/* 137 */       TreeItem treeitem = (TreeItem)widget;
/* 138 */       TreeItem[] treeitems = treeitem.getItems();
/* 139 */       for (int i = 0; i < treeitems.length; i++) {
/* 140 */         updateLanguageFromData(treeitems[i], null);
/* 141 */         updateLanguageForControl(treeitems[i]);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public static void setLanguageText(Widget widget, String key)
/*     */   {
/* 148 */     setLanguageText(widget, key, false);
/*     */   }
/*     */   
/*     */   public static void setLanguageText(Widget widget, String key, String[] params) {
/* 152 */     setLanguageText(widget, key, params, false);
/*     */   }
/*     */   
/*     */   public static void setLanguageText(Widget widget, String key, boolean setTooltipOnly) {
/* 156 */     setLanguageText(widget, key, null, setTooltipOnly);
/*     */   }
/*     */   
/*     */   private static void setLanguageText(Widget widget, String key, String[] params, boolean setTooltipOnly)
/*     */   {
/* 161 */     widget.setData(key);
/* 162 */     if (!setTooltipOnly)
/* 163 */       updateLanguageFromData(widget, params);
/* 164 */     widget.removeListener(32, hoverListener);
/* 165 */     widget.addListener(32, hoverListener);
/*     */   }
/*     */   
/*     */   private static void updateToolTipFromData(Widget widget, boolean showKey) {
/* 169 */     String key = (String)widget.getData();
/* 170 */     if (key == null) {
/* 171 */       return;
/*     */     }
/* 173 */     if ((widget instanceof Control)) {
/* 174 */       if (showKey) {
/* 175 */         ((Control)widget).setToolTipText(key);
/* 176 */         return;
/*     */       }
/* 178 */       if (!key.endsWith(".tooltip")) {
/* 179 */         key = key + ".tooltip";
/*     */       }
/* 181 */       String toolTip = MessageText.getString(key);
/* 182 */       if (!toolTip.equals('!' + key + '!')) {
/* 183 */         ((Control)widget).setToolTipText(toolTip);
/*     */       }
/* 185 */     } else if ((widget instanceof ToolItem)) {
/* 186 */       if (!key.endsWith(".tooltip")) {
/* 187 */         key = key + ".tooltip";
/*     */       }
/* 189 */       String toolTip = MessageText.getString(key);
/* 190 */       if (!toolTip.equals('!' + key + '!')) {
/* 191 */         ((ToolItem)widget).setToolTipText(toolTip.replaceAll("Meta\\+", Constants.isOSX ? "Cmd+" : "Ctrl+"));
/*     */       }
/*     */     }
/* 194 */     else if ((widget instanceof TableColumn)) {
/* 195 */       if (!key.endsWith(".info")) {
/* 196 */         key = key + ".info";
/*     */       }
/* 198 */       String toolTip = MessageText.getString(key, (String)null);
/* 199 */       if (toolTip == null) {
/* 200 */         toolTip = MessageText.getString(key.substring(0, key.length() - 5), (String)null);
/*     */       }
/*     */       
/* 203 */       if (toolTip != null) {
/*     */         try {
/* 205 */           ((TableColumn)widget).setToolTipText(toolTip);
/*     */         }
/*     */         catch (NoSuchMethodError e) {}
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   private static void updateLanguageFromData(Widget widget, String[] params)
/*     */   {
/* 215 */     if ((widget == null) || (widget.isDisposed())) {
/* 216 */       return;
/*     */     }
/*     */     
/* 219 */     if (widget.getData() != null) {
/* 220 */       String key = null;
/*     */       try {
/* 222 */         key = (String)widget.getData();
/*     */       }
/*     */       catch (ClassCastException e) {}
/*     */       
/* 226 */       if (key == null) return;
/* 227 */       if (key.endsWith(".tooltip"))
/*     */         return;
/*     */       String message;
/*     */       String message;
/* 231 */       if (params == null)
/*     */       {
/* 233 */         message = MessageText.getString((String)widget.getData());
/*     */       }
/*     */       else {
/* 236 */         message = MessageText.getString((String)widget.getData(), params);
/*     */       }
/*     */       
/* 239 */       if ((widget instanceof MenuItem)) {
/* 240 */         MenuItem menuItem = (MenuItem)widget;
/* 241 */         boolean indent = menuItem.getData("IndentItem") != null;
/*     */         
/* 243 */         if (Constants.isOSX) {
/* 244 */           message = HIG_ELLIP_EXP.matcher(message).replaceAll("…");
/*     */         }
/* 246 */         menuItem.setText(indent ? "  " + message : message);
/*     */         
/* 248 */         if (menuItem.getAccelerator() != 0) {
/* 249 */           KeyBindings.setAccelerator(menuItem, (String)menuItem.getData());
/*     */         }
/* 251 */       } else if ((widget instanceof TableColumn)) {
/* 252 */         TableColumn tc = (TableColumn)widget;
/* 253 */         tc.setText(message);
/* 254 */       } else if ((widget instanceof Label))
/*     */       {
/*     */ 
/* 257 */         ((Label)widget).setText(message.replaceAll("& ", "&& "));
/* 258 */       } else if ((widget instanceof CLabel)) {
/* 259 */         ((CLabel)widget).setText(message.replaceAll("& ", "&& "));
/* 260 */       } else if ((widget instanceof Group)) {
/* 261 */         ((Group)widget).setText(message);
/* 262 */       } else if ((widget instanceof Button)) {
/* 263 */         ((Button)widget).setText(message);
/* 264 */       } else if ((widget instanceof CTabItem)) {
/* 265 */         ((CTabItem)widget).setText(message);
/* 266 */       } else if ((widget instanceof TabItem)) {
/* 267 */         ((TabItem)widget).setText(message);
/* 268 */       } else if ((widget instanceof TreeItem)) {
/* 269 */         ((TreeItem)widget).setText(message);
/* 270 */       } else if ((widget instanceof Shell)) {
/* 271 */         ((Shell)widget).setText(message);
/* 272 */       } else if ((widget instanceof ToolItem)) {
/* 273 */         ((ToolItem)widget).setText(message);
/* 274 */       } else if ((widget instanceof Text)) {
/* 275 */         ((Text)widget).setText(message);
/* 276 */       } else if ((widget instanceof TreeColumn)) {
/* 277 */         ((TreeColumn)widget).setText(message);
/* 278 */       } else if ((widget instanceof DoubleBufferedLabel)) {
/* 279 */         ((DoubleBufferedLabel)widget).setText(message);
/* 280 */       } else if (!(widget instanceof Canvas))
/*     */       {
/*     */ 
/* 283 */         org.gudy.azureus2.core3.util.Debug.out("No cast for " + widget.getClass().getName());
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public static void setLanguageTooltip(Widget widget, String key) {
/* 289 */     if ((widget == null) || (widget.isDisposed())) {
/* 290 */       return;
/*     */     }
/*     */     
/* 293 */     widget.setData(key);
/* 294 */     updateTooltipLanguageFromData(widget);
/*     */   }
/*     */   
/*     */   public static void updateTooltipLanguageFromData(Widget widget) {
/* 298 */     if ((widget == null) || (widget.isDisposed())) {
/* 299 */       return;
/*     */     }
/* 301 */     if (widget.getData() != null) {
/* 302 */       String sToolTip = MessageText.getString((String)widget.getData());
/* 303 */       if ((widget instanceof CLabel)) {
/* 304 */         ((CLabel)widget).setToolTipText(sToolTip);
/* 305 */       } else if ((widget instanceof Label)) {
/* 306 */         ((Label)widget).setToolTipText(sToolTip);
/* 307 */       } else if ((widget instanceof Text)) {
/* 308 */         ((Text)widget).setToolTipText(sToolTip);
/* 309 */       } else if ((widget instanceof Canvas)) {
/* 310 */         ((Canvas)widget).setToolTipText(sToolTip);
/* 311 */       } else if ((widget instanceof Composite)) {
/* 312 */         ((Composite)widget).setToolTipText(sToolTip);
/* 313 */       } else if ((widget instanceof Control)) {
/* 314 */         ((Control)widget).setToolTipText(sToolTip);
/*     */       } else {
/* 316 */         System.out.println("No cast for " + widget.getClass().getName());
/*     */       }
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/Messages.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */