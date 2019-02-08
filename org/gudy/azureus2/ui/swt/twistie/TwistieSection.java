/*     */ package org.gudy.azureus2.ui.swt.twistie;
/*     */ 
/*     */ import org.eclipse.swt.graphics.Color;
/*     */ import org.eclipse.swt.layout.GridData;
/*     */ import org.eclipse.swt.layout.GridLayout;
/*     */ import org.eclipse.swt.widgets.Composite;
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
/*     */ public class TwistieSection
/*     */   extends Composite
/*     */   implements ITwistieConstants
/*     */ {
/*  31 */   private TwistieContentPanel content = null;
/*     */   
/*  33 */   private TwistieLabel label = null;
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
/*     */   public TwistieSection(Composite parent, int style)
/*     */   {
/*  46 */     super(parent, 0);
/*  47 */     setBackgroundMode(2);
/*  48 */     GridLayout gLayout = new GridLayout();
/*  49 */     gLayout.marginHeight = 0;
/*  50 */     gLayout.marginWidth = 0;
/*  51 */     gLayout.verticalSpacing = 0;
/*  52 */     setLayout(gLayout);
/*     */     
/*  54 */     this.label = new TwistieLabel(this, style);
/*  55 */     this.label.setLayoutData(new GridData(4, 16777216, true, false));
/*     */     
/*  57 */     this.content = new TwistieContentPanel(this, 0);
/*  58 */     final GridData gDataExpanded = new GridData(4, 4, true, true);
/*  59 */     gDataExpanded.horizontalIndent = 10;
/*  60 */     final GridData gDataCollapsed = new GridData(4, 4, true, false);
/*     */     
/*  62 */     gDataCollapsed.heightHint = 0;
/*     */     
/*  64 */     this.content._setLayoutData(this.label.isCollapsed() ? gDataCollapsed : gDataExpanded);
/*     */     
/*     */ 
/*  67 */     this.label.addTwistieListener(new ITwistieListener() {
/*     */       public void isCollapsed(boolean value) {
/*  69 */         TwistieSection.TwistieContentPanel.access$000(TwistieSection.this.content, value ? gDataCollapsed : gDataExpanded);
/*  70 */         TwistieSection.this.layout(true, true);
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Composite getContent()
/*     */   {
/*  84 */     return this.content;
/*     */   }
/*     */   
/*     */   public void setBackground(Color color) {
/*  88 */     if ((null != this.label) && (!this.label.isDisposed())) {
/*  89 */       this.label.setBackground(color);
/*     */     }
/*     */     
/*  92 */     if ((null != this.content) && (!this.content.isDisposed())) {
/*  93 */       this.content.setBackground(color);
/*     */     }
/*     */     
/*  96 */     super.setBackground(color);
/*     */   }
/*     */   
/*     */   public void setForeground(Color color) {
/* 100 */     if ((null != this.label) && (!this.label.isDisposed())) {
/* 101 */       this.label.setForeground(color);
/*     */     }
/*     */     
/* 104 */     if ((null != this.content) && (!this.content.isDisposed())) {
/* 105 */       this.content.setForeground(color);
/*     */     }
/* 107 */     super.setForeground(color);
/*     */   }
/*     */   
/*     */   public void setEnabled(boolean enabled) {
/* 111 */     if ((null != this.label) && (!this.label.isDisposed())) {
/* 112 */       this.label.setEnabled(enabled);
/*     */     }
/* 114 */     super.setEnabled(enabled);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void addTwistieListener(ITwistieListener listener)
/*     */   {
/* 122 */     if ((null != this.label) && (!this.label.isDisposed())) {
/* 123 */       this.label.addTwistieListener(listener);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void removeTwistieListener(ITwistieListener listener)
/*     */   {
/* 132 */     if ((null != this.label) && (!this.label.isDisposed())) {
/* 133 */       this.label.removeTwistieListener(listener);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setDescription(String string)
/*     */   {
/* 143 */     if ((null != this.label) && (!this.label.isDisposed())) {
/* 144 */       this.label.setDescription(string);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setTitle(String string)
/*     */   {
/* 154 */     if ((null != this.label) && (!this.label.isDisposed())) {
/* 155 */       this.label.setTitle(string);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setToolTipText(String string)
/*     */   {
/* 165 */     if ((null != this.label) && (!this.label.isDisposed())) {
/* 166 */       this.label.setToolTipText(string);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setTwistieForeground(Color color)
/*     */   {
/* 176 */     if ((null != this.label) && (!this.label.isDisposed())) {
/* 177 */       this.label.setTwistieForeground(color);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isCollapsed()
/*     */   {
/* 184 */     return this.label.isCollapsed();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setCollapsed(boolean c)
/*     */   {
/* 191 */     this.label.setCollapsed(c);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static class TwistieContentPanel
/*     */     extends Composite
/*     */   {
/*     */     public TwistieContentPanel(Composite parent, int style)
/*     */     {
/* 204 */       super(style);
/* 205 */       setBackgroundMode(2);
/*     */     }
/*     */     
/*     */     private void _setLayoutData(GridData gData) {
/* 209 */       super.setLayoutData(gData);
/*     */     }
/*     */     
/*     */     public void setLayoutData(Object layoutData) {
/* 213 */       throw new IllegalArgumentException("This is a managed class therefore overriding its LayoutData is an illegal operation");
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/twistie/TwistieSection.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */