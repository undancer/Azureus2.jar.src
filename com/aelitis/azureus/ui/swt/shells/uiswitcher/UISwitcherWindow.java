/*     */ package com.aelitis.azureus.ui.swt.shells.uiswitcher;
/*     */ 
/*     */ import java.io.PrintStream;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import org.eclipse.swt.events.DisposeEvent;
/*     */ import org.eclipse.swt.events.DisposeListener;
/*     */ import org.eclipse.swt.events.TraverseEvent;
/*     */ import org.eclipse.swt.events.TraverseListener;
/*     */ import org.eclipse.swt.graphics.Color;
/*     */ import org.eclipse.swt.graphics.Font;
/*     */ import org.eclipse.swt.graphics.FontData;
/*     */ import org.eclipse.swt.graphics.Point;
/*     */ import org.eclipse.swt.layout.GridData;
/*     */ import org.eclipse.swt.layout.GridLayout;
/*     */ import org.eclipse.swt.widgets.Button;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.Control;
/*     */ import org.eclipse.swt.widgets.Display;
/*     */ import org.eclipse.swt.widgets.Event;
/*     */ import org.eclipse.swt.widgets.Label;
/*     */ import org.eclipse.swt.widgets.Listener;
/*     */ import org.eclipse.swt.widgets.Shell;
/*     */ import org.eclipse.swt.widgets.Widget;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.ui.swt.Messages;
/*     */ import org.gudy.azureus2.ui.swt.UISwitcherUtil;
/*     */ import org.gudy.azureus2.ui.swt.Utils;
/*     */ import org.gudy.azureus2.ui.swt.components.shell.ShellFactory;
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
/*     */ public class UISwitcherWindow
/*     */ {
/*  44 */   private static String CFG_PREFIX = "window.uiswitcher.";
/*     */   
/*  46 */   private static String[] IDS = { "NewUI", "ClassicUI" };
/*     */   
/*     */ 
/*     */   private Shell shell;
/*     */   
/*     */ 
/*     */   private Button btnOk;
/*     */   
/*     */ 
/*  55 */   private int ui = -1;
/*     */   
/*  57 */   private List<Object> disposeList = new ArrayList();
/*     */   
/*     */   public UISwitcherWindow() {
/*  60 */     this(false, true);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public UISwitcherWindow(boolean standalone, final boolean allowCancel)
/*     */   {
/*  67 */     String originalUIMode = UISwitcherUtil.calcUIMode();
/*     */     try {
/*  69 */       final Button[] buttons = new Button[IDS.length];
/*     */       
/*     */ 
/*  72 */       int style = 2096;
/*  73 */       if (allowCancel) {
/*  74 */         style |= 0x40;
/*     */       }
/*  76 */       this.shell = (standalone ? new Shell(Display.getDefault(), style) : ShellFactory.createShell((Shell)null, style));
/*     */       
/*  78 */       this.shell.setText(MessageText.getString(CFG_PREFIX + "title"));
/*  79 */       Utils.setShellIcon(this.shell);
/*     */       
/*  81 */       this.shell.addDisposeListener(new DisposeListener() {
/*     */         public void widgetDisposed(DisposeEvent e) {
/*  83 */           Utils.disposeSWTObjects(UISwitcherWindow.this.disposeList);
/*  84 */           if (UISwitcherWindow.this.ui == 0)
/*     */           {
/*  86 */             COConfigurationManager.setParameter("ui", "az3");
/*  87 */           } else if (UISwitcherWindow.this.ui == 1) {
/*  88 */             COConfigurationManager.setParameter("ui", "az2");
/*     */           }
/*     */           
/*  91 */           if (UISwitcherWindow.this.ui != -1) {
/*  92 */             COConfigurationManager.setParameter("ui.asked", true);
/*  93 */             UISwitcherUtil.triggerListeners(UISwitcherUtil.calcUIMode());
/*     */           }
/*     */           
/*     */         }
/*  97 */       });
/*  98 */       GridLayout layout = new GridLayout();
/*  99 */       layout.horizontalSpacing = 0;
/* 100 */       layout.marginWidth = 5;
/* 101 */       layout.marginHeight = 0;
/* 102 */       layout.verticalSpacing = 1;
/* 103 */       this.shell.setLayout(layout);
/*     */       
/* 105 */       Label title = new Label(this.shell, 64);
/* 106 */       GridData gd = new GridData(768);
/* 107 */       gd.verticalIndent = 3;
/* 108 */       Utils.setLayoutData(title, gd);
/*     */       
/* 110 */       Messages.setLanguageText(title, CFG_PREFIX + "text");
/*     */       
/* 112 */       Listener radioListener = new Listener() {
/*     */         public void handleEvent(Event event) { int idx;
/*     */           int idx;
/* 115 */           if ((event.widget instanceof Composite)) {
/* 116 */             Long l = (Long)event.widget.getData("INDEX");
/* 117 */             idx = l.intValue();
/*     */           } else {
/* 119 */             Composite c = ((Control)event.widget).getParent();
/* 120 */             Long l = (Long)c.getData("INDEX");
/* 121 */             idx = l.intValue();
/*     */           }
/* 123 */           for (int i = 0; i < buttons.length; i++) {
/* 124 */             boolean selected = idx == i;
/* 125 */             Composite c = buttons[i].getParent();
/* 126 */             c.setBackground(selected ? c.getDisplay().getSystemColor(26) : null);
/*     */             
/*     */ 
/* 129 */             Color fg = selected ? c.getDisplay().getSystemColor(27) : null;
/*     */             
/* 131 */             Control[] children = c.getChildren();
/* 132 */             for (int j = 0; j < children.length; j++) {
/* 133 */               Control control = children[j];
/* 134 */               control.setForeground(fg);
/*     */             }
/*     */             
/* 137 */             buttons[i].setSelection(selected);
/*     */           }
/*     */           
/*     */         }
/* 141 */       };
/* 142 */       FontData[] fontData = this.shell.getFont().getFontData();
/* 143 */       fontData[0].setHeight((int)(fontData[0].getHeight() * 1.5D));
/* 144 */       fontData[0].setStyle(1);
/* 145 */       Font headerFont = new Font(this.shell.getDisplay(), fontData);
/* 146 */       this.disposeList.add(headerFont);
/*     */       
/* 148 */       Composite cCenter = new Composite(this.shell, 0);
/* 149 */       cCenter.setLayout(new GridLayout());
/* 150 */       Utils.setLayoutData(cCenter, new GridData(64));
/*     */       
/* 152 */       for (int i = 0; i < IDS.length; i++)
/*     */       {
/* 154 */         Composite c = new Composite(cCenter, 0);
/* 155 */         c.setBackgroundMode(1);
/* 156 */         gd = new GridData(768);
/* 157 */         gd.verticalIndent = 0;
/* 158 */         Utils.setLayoutData(c, gd);
/* 159 */         GridLayout gridLayout = new GridLayout(1, false);
/* 160 */         gridLayout.horizontalSpacing = 0;
/* 161 */         gridLayout.marginWidth = 5;
/* 162 */         gridLayout.marginHeight = 3;
/* 163 */         gridLayout.verticalSpacing = 0;
/* 164 */         c.setLayout(gridLayout);
/* 165 */         c.setData("INDEX", new Long(i));
/*     */         
/* 167 */         c.addListener(3, radioListener);
/*     */         
/* 169 */         buttons[i] = new Button(c, 16);
/* 170 */         buttons[i].setLayoutData(new GridData(256));
/* 171 */         Messages.setLanguageText(buttons[i], CFG_PREFIX + IDS[i] + ".title");
/* 172 */         buttons[i].setData("INDEX", new Long(i));
/* 173 */         buttons[i].addListener(13, radioListener);
/* 174 */         buttons[i].setFont(headerFont);
/*     */         
/* 176 */         buttons[i].addTraverseListener(new TraverseListener()
/*     */         {
/*     */           public void keyTraversed(TraverseEvent e) {
/* 179 */             if (e.detail == 64) {
/* 180 */               e.doit = true;
/* 181 */               e.detail = 16;
/* 182 */             } else if (e.detail == 32) {
/* 183 */               e.detail = 8;
/* 184 */               e.doit = true;
/* 185 */             } else if ((e.detail == 16) || (e.detail == 8))
/*     */             {
/* 187 */               UISwitcherWindow.this.btnOk.setFocus();
/* 188 */               e.doit = false;
/* 189 */             } else if (e.detail == 4) {
/* 190 */               e.doit = true;
/* 191 */             } else if (e.detail == 2) {
/* 192 */               e.doit = false;
/* 193 */               if (allowCancel) {
/* 194 */                 UISwitcherWindow.this.ui = -1;
/* 195 */                 UISwitcherWindow.this.shell.dispose();
/*     */               }
/*     */             } else {
/* 198 */               e.doit = false;
/*     */             }
/*     */             
/*     */           }
/*     */           
/* 203 */         });
/* 204 */         buttons[i].addListener(1, new Listener()
/*     */         {
/*     */           public void handleEvent(Event event) {
/* 207 */             if (event.keyCode == 16777217) {
/* 208 */               UISwitcherWindow.this.shell.getDisplay().getFocusControl().traverse(32);
/*     */             }
/* 210 */             else if (event.keyCode == 16777218) {
/* 211 */               UISwitcherWindow.this.shell.getDisplay().getFocusControl().traverse(64);
/*     */             }
/*     */             
/*     */           }
/*     */           
/* 216 */         });
/* 217 */         Label info = new Label(c, 64);
/* 218 */         gd = new GridData(1808);
/* 219 */         gd.horizontalIndent = 20;
/* 220 */         gd.verticalAlignment = 128;
/* 221 */         Utils.setLayoutData(info, gd);
/*     */         
/* 223 */         Messages.setLanguageText(info, CFG_PREFIX + IDS[i] + ".text");
/* 224 */         info.addListener(3, radioListener);
/*     */       }
/*     */       
/* 227 */       Event eventSelectFirst = new Event();
/* 228 */       eventSelectFirst.widget = buttons[0];
/* 229 */       radioListener.handleEvent(eventSelectFirst);
/*     */       
/* 231 */       Composite cBottom = new Composite(this.shell, 0);
/* 232 */       layout = new GridLayout(1, false);
/* 233 */       layout.marginHeight = 0;
/* 234 */       layout.marginWidth = 0;
/* 235 */       cBottom.setLayout(layout);
/* 236 */       Utils.setLayoutData(cBottom, new GridData(64));
/*     */       
/* 238 */       this.btnOk = new Button(cBottom, 8);
/* 239 */       Messages.setLanguageText(this.btnOk, "Button.ok");
/* 240 */       this.shell.setDefaultButton(this.btnOk);
/* 241 */       this.btnOk.addListener(13, new Listener() {
/*     */         public void handleEvent(Event event) {
/* 243 */           for (int i = 0; i < buttons.length; i++) {
/* 244 */             if (buttons[i].getSelection()) {
/* 245 */               UISwitcherWindow.this.ui = i;
/* 246 */               break;
/*     */             }
/*     */           }
/* 249 */           UISwitcherWindow.this.shell.dispose();
/*     */         }
/* 251 */       });
/* 252 */       gd = new GridData(128);
/* 253 */       Utils.setLayoutData(this.btnOk, gd);
/*     */       
/* 255 */       this.shell.addTraverseListener(new TraverseListener() {
/*     */         public void keyTraversed(TraverseEvent e) {
/* 257 */           if (e.detail == 2) {
/* 258 */             UISwitcherWindow.this.shell.dispose();
/* 259 */             e.doit = false;
/* 260 */             return;
/*     */           }
/* 262 */           e.doit = true;
/*     */         }
/*     */         
/* 265 */       });
/* 266 */       Point point = this.shell.computeSize(400, -1);
/* 267 */       this.shell.setSize(point);
/*     */       
/* 269 */       Utils.centreWindow(this.shell);
/*     */     } catch (Exception e) {
/* 271 */       e.printStackTrace();
/*     */     }
/*     */   }
/*     */   
/*     */   public void open() {
/* 276 */     this.shell.open();
/*     */   }
/*     */   
/*     */   public static void main(String[] args) {
/* 280 */     Display display = Display.getDefault();
/* 281 */     UISwitcherWindow window = new UISwitcherWindow(true, true);
/* 282 */     window.open();
/* 283 */     Shell shell = window.shell;
/* 284 */     while (!shell.isDisposed()) {
/* 285 */       if (!shell.getDisplay().readAndDispatch()) {
/* 286 */         shell.getDisplay().sleep();
/*     */       }
/*     */     }
/* 289 */     System.out.println(window.ui);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/shells/uiswitcher/UISwitcherWindow.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */