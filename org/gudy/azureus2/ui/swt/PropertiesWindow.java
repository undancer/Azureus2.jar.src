/*     */ package org.gudy.azureus2.ui.swt;
/*     */ 
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import org.eclipse.swt.custom.ScrolledComposite;
/*     */ import org.eclipse.swt.events.ControlAdapter;
/*     */ import org.eclipse.swt.events.ControlEvent;
/*     */ import org.eclipse.swt.graphics.Point;
/*     */ import org.eclipse.swt.graphics.Rectangle;
/*     */ import org.eclipse.swt.layout.GridData;
/*     */ import org.eclipse.swt.layout.GridLayout;
/*     */ import org.eclipse.swt.widgets.Button;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.Event;
/*     */ import org.eclipse.swt.widgets.Label;
/*     */ import org.eclipse.swt.widgets.Listener;
/*     */ import org.eclipse.swt.widgets.Shell;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.ui.swt.components.BufferedLabel;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class PropertiesWindow
/*     */ {
/*     */   private final Shell shell;
/*  43 */   private Map<String, BufferedLabel> field_map = new HashMap();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public PropertiesWindow(String object_name, String[] keys, String[] values)
/*     */   {
/*  51 */     this.shell = ShellFactory.createMainShell(112);
/*     */     
/*  53 */     this.shell.setText(MessageText.getString("props.window.title", new String[] { object_name }));
/*     */     
/*  55 */     Utils.setShellIcon(this.shell);
/*     */     
/*  57 */     GridLayout layout = new GridLayout();
/*  58 */     layout.numColumns = 3;
/*  59 */     this.shell.setLayout(layout);
/*     */     
/*  61 */     final ScrolledComposite scrollable = new ScrolledComposite(this.shell, 768);
/*  62 */     GridData gridData = new GridData(4, 4, true, true);
/*  63 */     gridData.horizontalSpan = 3;
/*     */     
/*  65 */     Utils.setLayoutData(scrollable, gridData);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*  70 */     final Composite main = new Composite(scrollable, 0);
/*     */     
/*  72 */     layout = new GridLayout();
/*  73 */     layout.marginHeight = 0;
/*  74 */     layout.marginWidth = 0;
/*     */     
/*  76 */     layout.numColumns = 2;
/*  77 */     main.setLayout(layout);
/*     */     
/*  79 */     scrollable.setContent(main);
/*  80 */     scrollable.setExpandVertical(true);
/*  81 */     scrollable.setExpandHorizontal(true);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*  86 */     scrollable.addControlListener(new ControlAdapter() {
/*     */       public void controlResized(ControlEvent e) {
/*  88 */         Rectangle r = scrollable.getClientArea();
/*  89 */         scrollable.setMinSize(main.computeSize(r.width, -1));
/*     */       }
/*     */       
/*  92 */     });
/*  93 */     gridData = new GridData(1808);
/*  94 */     gridData.horizontalSpan = 3;
/*  95 */     Utils.setLayoutData(main, gridData);
/*     */     
/*  97 */     for (int i = 0; i < keys.length; i++)
/*     */     {
/*  99 */       if ((keys[i] != null) && (values[i] != null))
/*     */       {
/*     */ 
/*     */ 
/*     */ 
/* 104 */         BufferedLabel msg_label = new BufferedLabel(main, 0);
/*     */         
/* 106 */         String key = keys[i];
/* 107 */         String msg; String msg; if (key.length() == 0) {
/* 108 */           msg = ""; } else { String msg;
/* 109 */           if ((key.startsWith("!")) && (key.endsWith("!"))) {
/* 110 */             msg = key.substring(1, key.length() - 1);
/*     */           } else {
/* 112 */             msg = MessageText.getString(key);
/*     */           }
/*     */         }
/* 115 */         String value = values[i];
/*     */         
/*     */ 
/*     */ 
/* 119 */         if (value.equals("<null>"))
/*     */         {
/* 121 */           msg_label.setText(msg);
/*     */           
/* 123 */           value = "";
/*     */         }
/*     */         else {
/* 126 */           msg_label.setText(msg + ":");
/*     */         }
/*     */         
/* 129 */         gridData = new GridData();
/* 130 */         gridData.verticalAlignment = 16;
/* 131 */         Utils.setLayoutData(msg_label, gridData);
/*     */         
/* 133 */         BufferedLabel val_label = new BufferedLabel(main, 64);
/* 134 */         val_label.setText(value);
/* 135 */         gridData = new GridData(768);
/* 136 */         gridData.horizontalIndent = 6;
/* 137 */         Utils.setLayoutData(val_label, gridData);
/*     */         
/* 139 */         this.field_map.put(key, val_label);
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 144 */     Label labelSeparator = new Label(this.shell, 258);
/* 145 */     gridData = new GridData(768);
/* 146 */     gridData.horizontalSpan = 3;
/* 147 */     Utils.setLayoutData(labelSeparator, gridData);
/*     */     
/*     */ 
/*     */ 
/* 151 */     new Label(this.shell, 0);
/*     */     
/* 153 */     Button bOk = new Button(this.shell, 8);
/* 154 */     Messages.setLanguageText(bOk, "Button.ok");
/* 155 */     gridData = new GridData(896);
/* 156 */     gridData.grabExcessHorizontalSpace = true;
/* 157 */     gridData.widthHint = 70;
/* 158 */     Utils.setLayoutData(bOk, gridData);
/* 159 */     bOk.addListener(13, new Listener() {
/*     */       public void handleEvent(Event e) {
/* 161 */         PropertiesWindow.this.close();
/*     */       }
/*     */       
/* 164 */     });
/* 165 */     Button bCancel = new Button(this.shell, 8);
/* 166 */     Messages.setLanguageText(bCancel, "Button.cancel");
/* 167 */     gridData = new GridData(128);
/* 168 */     gridData.grabExcessHorizontalSpace = false;
/* 169 */     gridData.widthHint = 70;
/* 170 */     Utils.setLayoutData(bCancel, gridData);
/* 171 */     bCancel.addListener(13, new Listener() {
/*     */       public void handleEvent(Event e) {
/* 173 */         PropertiesWindow.this.close();
/*     */       }
/*     */       
/* 176 */     });
/* 177 */     this.shell.setDefaultButton(bOk);
/*     */     
/* 179 */     this.shell.addListener(31, new Listener() {
/*     */       public void handleEvent(Event e) {
/* 181 */         if (e.character == '\033') {
/* 182 */           PropertiesWindow.this.close();
/*     */         }
/*     */       }
/*     */     });
/*     */     
/* 187 */     if (!Utils.linkShellMetricsToConfig(this.shell, "PropWin")) {
/* 188 */       int shell_width = 400;
/*     */       
/* 190 */       int main_height = main.computeSize(shell_width, -1).y;
/*     */       
/* 192 */       main_height = Math.max(main_height, 250);
/*     */       
/* 194 */       main_height = Math.min(main_height, 500);
/*     */       
/* 196 */       int shell_height = main_height + 50;
/*     */       
/* 198 */       this.shell.setSize(shell_width, shell_height);
/*     */     }
/*     */     
/* 201 */     Utils.centreWindow(this.shell);
/*     */     
/* 203 */     this.shell.open();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void updateProperty(final String key, final String value)
/*     */   {
/* 211 */     Utils.execSWTThread(new Runnable()
/*     */     {
/*     */ 
/*     */       public void run()
/*     */       {
/* 216 */         BufferedLabel label = (BufferedLabel)PropertiesWindow.this.field_map.get(key);
/*     */         
/* 218 */         if ((label != null) && (!label.isDisposed()))
/*     */         {
/* 220 */           label.setText(value);
/*     */         }
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */   protected void close()
/*     */   {
/* 229 */     if (!this.shell.isDisposed())
/*     */     {
/* 231 */       this.shell.dispose();
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/PropertiesWindow.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */