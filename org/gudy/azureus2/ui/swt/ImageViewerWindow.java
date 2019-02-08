/*     */ package org.gudy.azureus2.ui.swt;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.FileInputStream;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import org.eclipse.swt.custom.ScrolledComposite;
/*     */ import org.eclipse.swt.events.ControlAdapter;
/*     */ import org.eclipse.swt.events.ControlEvent;
/*     */ import org.eclipse.swt.events.DisposeEvent;
/*     */ import org.eclipse.swt.events.DisposeListener;
/*     */ import org.eclipse.swt.graphics.Image;
/*     */ import org.eclipse.swt.layout.GridData;
/*     */ import org.eclipse.swt.layout.GridLayout;
/*     */ import org.eclipse.swt.widgets.Button;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.Event;
/*     */ import org.eclipse.swt.widgets.Label;
/*     */ import org.eclipse.swt.widgets.Listener;
/*     */ import org.eclipse.swt.widgets.Shell;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.core3.util.Debug;
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
/*     */ public class ImageViewerWindow
/*     */ {
/*     */   private Shell shell;
/*     */   private Button ok;
/*     */   private Image image;
/*  48 */   private List<TextViewerWindowListener> listeners = new ArrayList();
/*     */   
/*     */ 
/*     */ 
/*     */   public ImageViewerWindow(String sTitleID, String sMessageID, File image_file)
/*     */   {
/*  54 */     this(sTitleID, sMessageID, image_file, null);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public ImageViewerWindow(String sTitleID, String sMessageID, Image img)
/*     */   {
/*  61 */     this(sTitleID, sMessageID, null, img);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private ImageViewerWindow(String sTitleID, String sMessageID, File image_file, Image img)
/*     */   {
/*  68 */     this.shell = ShellFactory.createMainShell(3184);
/*     */     
/*  70 */     if (sTitleID != null) { this.shell.setText(MessageText.keyExists(sTitleID) ? MessageText.getString(sTitleID) : sTitleID);
/*     */     }
/*  72 */     Utils.setShellIcon(this.shell);
/*     */     
/*  74 */     GridLayout layout = new GridLayout();
/*  75 */     layout.numColumns = 2;
/*  76 */     this.shell.setLayout(layout);
/*     */     
/*  78 */     Label label = new Label(this.shell, 0);
/*  79 */     if (sMessageID != null) label.setText(MessageText.keyExists(sMessageID) ? MessageText.getString(sMessageID) : sMessageID);
/*  80 */     GridData gridData = new GridData(768);
/*     */     
/*  82 */     gridData.horizontalSpan = 2;
/*  83 */     Utils.setLayoutData(label, gridData);
/*     */     
/*  85 */     final ScrolledComposite sc = new ScrolledComposite(this.shell, 768);
/*  86 */     sc.setExpandHorizontal(true);
/*  87 */     sc.setExpandVertical(true);
/*  88 */     gridData = new GridData(1808);
/*  89 */     gridData.widthHint = 500;
/*  90 */     gridData.heightHint = 400;
/*  91 */     gridData.horizontalSpan = 2;
/*  92 */     Utils.setLayoutData(sc, gridData);
/*     */     
/*  94 */     layout = new GridLayout();
/*  95 */     layout.horizontalSpacing = 0;
/*  96 */     layout.verticalSpacing = 0;
/*  97 */     layout.marginHeight = 0;
/*  98 */     layout.marginWidth = 0;
/*  99 */     sc.setLayout(layout);
/*     */     
/* 101 */     final Composite img_comp = new Composite(sc, 0);
/* 102 */     img_comp.setLayout(new GridLayout());
/*     */     
/* 104 */     Label img_label = new Label(img_comp, 2048);
/* 105 */     img_label.setAlignment(16777216);
/* 106 */     gridData = new GridData(1808);
/* 107 */     Utils.setLayoutData(img_label, gridData);
/*     */     
/* 109 */     sc.setContent(img_comp);
/* 110 */     sc.addControlListener(new ControlAdapter() {
/*     */       public void controlResized(ControlEvent e) {
/* 112 */         sc.setMinSize(img_comp.computeSize(-1, -1));
/*     */       }
/*     */     });
/*     */     
/* 116 */     if (img == null) {
/*     */       try
/*     */       {
/* 119 */         FileInputStream is = new FileInputStream(image_file);
/*     */         try
/*     */         {
/* 122 */           this.image = new Image(this.shell.getDisplay(), is);
/*     */         }
/*     */         finally
/*     */         {
/* 126 */           is.close();
/*     */         }
/*     */       } catch (Throwable e) {
/* 129 */         e.printStackTrace();
/*     */       }
/*     */       
/*     */     }
/*     */     else {
/* 134 */       this.image = img;
/*     */     }
/*     */     
/* 137 */     if (this.image != null)
/*     */     {
/* 139 */       img_label.setImage(this.image);
/*     */       
/* 141 */       img_label.addDisposeListener(new DisposeListener()
/*     */       {
/*     */ 
/*     */         public void widgetDisposed(DisposeEvent e)
/*     */         {
/*     */ 
/* 147 */           ImageViewerWindow.this.image.dispose();
/*     */         }
/*     */       });
/*     */     }
/*     */     
/* 152 */     label = new Label(this.shell, 0);
/* 153 */     gridData = new GridData(768);
/* 154 */     Utils.setLayoutData(label, gridData);
/*     */     
/* 156 */     this.ok = new Button(this.shell, 8);
/* 157 */     this.ok.setText(MessageText.getString("Button.ok"));
/* 158 */     gridData = new GridData();
/* 159 */     gridData.widthHint = 70;
/* 160 */     Utils.setLayoutData(this.ok, gridData);
/* 161 */     this.shell.setDefaultButton(this.ok);
/* 162 */     this.ok.addListener(13, new Listener() {
/*     */       public void handleEvent(Event event) {
/*     */         try {
/* 165 */           ImageViewerWindow.this.shell.dispose();
/*     */         }
/*     */         catch (Exception e) {
/* 168 */           Debug.printStackTrace(e);
/*     */         }
/*     */         
/*     */       }
/* 172 */     });
/* 173 */     this.shell.addListener(31, new Listener() {
/*     */       public void handleEvent(Event e) {
/* 175 */         if ((e.character == '\033') && 
/* 176 */           (ImageViewerWindow.this.ok.isEnabled())) {
/* 177 */           ImageViewerWindow.this.shell.dispose();
/*     */         }
/*     */         
/*     */       }
/*     */       
/* 182 */     });
/* 183 */     this.shell.addDisposeListener(new DisposeListener()
/*     */     {
/*     */ 
/*     */ 
/*     */       public void widgetDisposed(DisposeEvent arg0)
/*     */       {
/*     */ 
/* 190 */         for (ImageViewerWindow.TextViewerWindowListener l : ImageViewerWindow.this.listeners)
/*     */         {
/* 192 */           l.closed();
/*     */         }
/*     */         
/*     */       }
/* 196 */     });
/* 197 */     this.shell.pack();
/* 198 */     Utils.centreWindow(this.shell);
/* 199 */     this.shell.open();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setOKEnabled(boolean enabled)
/*     */   {
/* 206 */     this.ok.setEnabled(enabled);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void addListener(TextViewerWindowListener l)
/*     */   {
/* 213 */     this.listeners.add(l);
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isDisposed()
/*     */   {
/* 219 */     return this.shell.isDisposed();
/*     */   }
/*     */   
/*     */ 
/*     */   public void close()
/*     */   {
/* 225 */     if (!this.shell.isDisposed())
/*     */     {
/* 227 */       this.shell.dispose();
/*     */     }
/*     */   }
/*     */   
/*     */   public static abstract interface TextViewerWindowListener
/*     */   {
/*     */     public abstract void closed();
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/ImageViewerWindow.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */