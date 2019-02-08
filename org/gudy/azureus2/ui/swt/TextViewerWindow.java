/*     */ package org.gudy.azureus2.ui.swt;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import org.eclipse.swt.SWT;
/*     */ import org.eclipse.swt.events.DisposeEvent;
/*     */ import org.eclipse.swt.events.DisposeListener;
/*     */ import org.eclipse.swt.events.KeyEvent;
/*     */ import org.eclipse.swt.events.KeyListener;
/*     */ import org.eclipse.swt.layout.GridData;
/*     */ import org.eclipse.swt.layout.GridLayout;
/*     */ import org.eclipse.swt.widgets.Button;
/*     */ import org.eclipse.swt.widgets.Display;
/*     */ import org.eclipse.swt.widgets.Event;
/*     */ import org.eclipse.swt.widgets.Label;
/*     */ import org.eclipse.swt.widgets.Listener;
/*     */ import org.eclipse.swt.widgets.Shell;
/*     */ import org.eclipse.swt.widgets.Text;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.ui.swt.components.shell.ShellFactory;
/*     */ import org.gudy.azureus2.ui.swt.mainwindow.SWTThread;
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
/*     */ public class TextViewerWindow
/*     */ {
/*     */   private Shell shell;
/*     */   private Text txtInfo;
/*     */   private Button ok;
/*  46 */   private List<TextViewerWindowListener> listeners = new ArrayList();
/*     */   
/*     */ 
/*     */ 
/*     */   public TextViewerWindow(String sTitleID, String sMessageID, String sText)
/*     */   {
/*  52 */     this(sTitleID, sMessageID, sText, true);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public TextViewerWindow(String sTitleID, String sMessageID, String sText, boolean modal)
/*     */   {
/*  59 */     this(sTitleID, sMessageID, sText, modal, false);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public TextViewerWindow(String sTitleID, String sMessageID, String sText, boolean modal, boolean defer_modal)
/*     */   {
/*  66 */     this(null, sTitleID, sMessageID, sText, modal, defer_modal);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public TextViewerWindow(Shell parent_shell, String sTitleID, String sMessageID, String sText, boolean modal, boolean defer_modal)
/*     */   {
/*  73 */     if (modal)
/*     */     {
/*  75 */       if (parent_shell == null)
/*     */       {
/*  77 */         this.shell = ShellFactory.createMainShell(68720);
/*     */       }
/*     */       else {
/*  80 */         this.shell = ShellFactory.createShell(parent_shell, 68720);
/*     */       }
/*     */       
/*     */ 
/*     */     }
/*  85 */     else if (parent_shell == null)
/*     */     {
/*  87 */       this.shell = ShellFactory.createMainShell(3184);
/*     */     }
/*     */     else
/*     */     {
/*  91 */       this.shell = ShellFactory.createShell(parent_shell, 3184);
/*     */     }
/*     */     
/*     */ 
/*  95 */     if (sTitleID != null) { this.shell.setText(MessageText.keyExists(sTitleID) ? MessageText.getString(sTitleID) : sTitleID);
/*     */     }
/*  97 */     Utils.setShellIcon(this.shell);
/*     */     
/*  99 */     GridLayout layout = new GridLayout();
/* 100 */     layout.numColumns = 2;
/* 101 */     this.shell.setLayout(layout);
/*     */     
/* 103 */     Label label = new Label(this.shell, 0);
/* 104 */     if (sMessageID != null) label.setText(MessageText.keyExists(sMessageID) ? MessageText.getString(sMessageID) : sMessageID);
/* 105 */     GridData gridData = new GridData(768);
/* 106 */     gridData.widthHint = 200;
/* 107 */     gridData.horizontalSpan = 2;
/* 108 */     Utils.setLayoutData(label, gridData);
/*     */     
/* 110 */     this.txtInfo = new Text(this.shell, 2818);
/* 111 */     gridData = new GridData(1808);
/* 112 */     gridData.widthHint = 600;
/* 113 */     gridData.heightHint = 400;
/* 114 */     gridData.horizontalSpan = 2;
/* 115 */     Utils.setLayoutData(this.txtInfo, gridData);
/* 116 */     this.txtInfo.setText(sText);
/*     */     
/* 118 */     this.txtInfo.addKeyListener(new KeyListener()
/*     */     {
/*     */       public void keyPressed(KeyEvent e) {
/* 121 */         int key = e.character;
/* 122 */         if ((key <= 26) && (key > 0)) {
/* 123 */           key += 96;
/*     */         }
/*     */         
/* 126 */         if ((key == 97) && (e.stateMask == SWT.MOD1) && 
/* 127 */           (TextViewerWindow.this.txtInfo != null)) {
/* 128 */           TextViewerWindow.this.txtInfo.selectAll();
/*     */         }
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       public void keyReleased(KeyEvent e) {}
/* 137 */     });
/* 138 */     label = new Label(this.shell, 0);
/* 139 */     gridData = new GridData(768);
/* 140 */     Utils.setLayoutData(label, gridData);
/*     */     
/* 142 */     this.ok = new Button(this.shell, 8);
/* 143 */     this.ok.setText(MessageText.getString("Button.ok"));
/* 144 */     gridData = new GridData();
/* 145 */     gridData.widthHint = 70;
/* 146 */     Utils.setLayoutData(this.ok, gridData);
/* 147 */     this.shell.setDefaultButton(this.ok);
/* 148 */     this.ok.addListener(13, new Listener() {
/*     */       public void handleEvent(Event event) {
/*     */         try {
/* 151 */           TextViewerWindow.this.shell.dispose();
/*     */         }
/*     */         catch (Exception e) {
/* 154 */           Debug.printStackTrace(e);
/*     */         }
/*     */         
/*     */       }
/* 158 */     });
/* 159 */     this.shell.addListener(31, new Listener() {
/*     */       public void handleEvent(Event e) {
/* 161 */         if ((e.character == '\033') && 
/* 162 */           (TextViewerWindow.this.ok.isEnabled())) {
/* 163 */           TextViewerWindow.this.shell.dispose();
/*     */         }
/*     */         
/*     */       }
/*     */       
/* 168 */     });
/* 169 */     this.shell.addDisposeListener(new DisposeListener()
/*     */     {
/*     */ 
/*     */ 
/*     */       public void widgetDisposed(DisposeEvent arg0)
/*     */       {
/*     */ 
/* 176 */         for (TextViewerWindow.TextViewerWindowListener l : TextViewerWindow.this.listeners)
/*     */         {
/* 178 */           l.closed();
/*     */         }
/*     */         
/*     */       }
/* 182 */     });
/* 183 */     this.shell.pack();
/* 184 */     Utils.centreWindow(this.shell);
/* 185 */     this.shell.open();
/*     */     
/* 187 */     if ((modal) && (!defer_modal)) {
/* 188 */       goModal();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public void goModal()
/*     */   {
/* 195 */     Display display = SWTThread.getInstance().getDisplay();
/*     */     
/* 197 */     while (!this.shell.isDisposed()) {
/* 198 */       if (!display.readAndDispatch()) { display.sleep();
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public void append(String str)
/*     */   {
/* 206 */     this.txtInfo.setText(this.txtInfo.getText() + str);
/*     */     
/* 208 */     this.txtInfo.setSelection(this.txtInfo.getTextLimit());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void append2(String str)
/*     */   {
/* 215 */     this.txtInfo.append(str);
/*     */     
/* 217 */     this.txtInfo.setSelection(this.txtInfo.getTextLimit());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public String getText()
/*     */   {
/* 224 */     return this.txtInfo.getText();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setText(String text)
/*     */   {
/* 231 */     this.txtInfo.setText(text);
/*     */     
/* 233 */     this.txtInfo.setSelection(this.txtInfo.getTextLimit());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setEditable(boolean editable)
/*     */   {
/* 240 */     this.txtInfo.setEditable(editable);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setOKEnabled(boolean enabled)
/*     */   {
/* 247 */     this.ok.setEnabled(enabled);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void addListener(TextViewerWindowListener l)
/*     */   {
/* 254 */     this.listeners.add(l);
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isDisposed()
/*     */   {
/* 260 */     return this.shell.isDisposed();
/*     */   }
/*     */   
/*     */ 
/*     */   public void close()
/*     */   {
/* 266 */     if (!this.shell.isDisposed())
/*     */     {
/* 268 */       this.shell.dispose();
/*     */     }
/*     */   }
/*     */   
/*     */   public static abstract interface TextViewerWindowListener
/*     */   {
/*     */     public abstract void closed();
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/TextViewerWindow.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */