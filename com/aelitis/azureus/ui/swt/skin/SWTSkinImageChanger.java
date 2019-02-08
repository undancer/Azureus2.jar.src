/*     */ package com.aelitis.azureus.ui.swt.skin;
/*     */ 
/*     */ import java.io.PrintStream;
/*     */ import org.eclipse.swt.graphics.Point;
/*     */ import org.eclipse.swt.graphics.Rectangle;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.Control;
/*     */ import org.eclipse.swt.widgets.Display;
/*     */ import org.eclipse.swt.widgets.Event;
/*     */ import org.eclipse.swt.widgets.Listener;
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
/*     */ public class SWTSkinImageChanger
/*     */   implements Listener
/*     */ {
/*     */   private static final boolean DEBUG = false;
/*     */   private final String suffix;
/*     */   private final int eventOn;
/*     */   private final int eventOff;
/*     */   private Control lastControl;
/*     */   
/*     */   public SWTSkinImageChanger(String suffix, int eventOn, int eventOff)
/*     */   {
/*  52 */     this.suffix = suffix;
/*  53 */     this.eventOn = eventOn;
/*  54 */     this.eventOff = eventOff;
/*     */   }
/*     */   
/*     */   public void handleEvent(Event event) {
/*  58 */     Control control = (Control)event.widget;
/*     */     
/*  60 */     if (control == null) {
/*  61 */       return;
/*     */     }
/*     */     
/*     */     try
/*     */     {
/*  66 */       boolean isExit = (event.type == 7) || (event.type == 27);
/*     */       
/*  68 */       if ((isExit) && (this.lastControl != null) && 
/*  69 */         (control.getParent() == this.lastControl)) {
/*     */         return;
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  77 */       SWTSkinObject skinObject = (SWTSkinObject)control.getData("SkinObject");
/*  78 */       if ((skinObject != null) && (skinObject.isDebug())) {
/*  79 */         System.out.println("exit " + skinObject);
/*     */       }
/*     */       
/*  82 */       if ((isExit) && ((skinObject instanceof SWTSkinObjectContainer)))
/*     */       {
/*  84 */         SWTSkinObjectContainer soContainer = (SWTSkinObjectContainer)skinObject;
/*  85 */         if (soContainer.getPropogation()) {
/*  86 */           Point pt = control.toDisplay(event.x, event.y);
/*  87 */           Composite composite = soContainer.getComposite();
/*  88 */           Point relPt = composite.toControl(pt);
/*     */           
/*  90 */           Rectangle bounds = composite.getClientArea();
/*  91 */           if ((bounds.contains(relPt)) && (composite.getDisplay().getActiveShell() != null))
/*     */           {
/*  93 */             if ((skinObject != null) && (skinObject.isDebug())) {
/*  94 */               System.out.println("skip " + skinObject + " because going into child");
/*     */             }
/*     */             
/*     */             return;
/*     */           }
/*     */         }
/*     */       }
/*     */       
/* 102 */       if ((isExit) && (control.getParent() != null))
/*     */       {
/* 104 */         Composite parent = control.getParent();
/* 105 */         SWTSkinObject soParent = (SWTSkinObject)parent.getData("SkinObject");
/* 106 */         if ((soParent != null) && ((soParent instanceof SWTSkinObjectContainer))) {
/* 107 */           SWTSkinObjectContainer container = (SWTSkinObjectContainer)soParent;
/* 108 */           if (container.getPropogation()) {
/* 109 */             Point pt = control.toDisplay(event.x, event.y);
/* 110 */             Point relPt = container.getComposite().toControl(pt);
/* 111 */             Rectangle bounds = parent.getClientArea();
/* 112 */             if ((bounds.contains(relPt)) && (parent.getDisplay().getActiveShell() != null))
/*     */             {
/* 114 */               if ((skinObject != null) && (skinObject.isDebug())) {
/* 115 */                 System.out.println("skip " + skinObject + " because going into parent " + bounds + ";" + relPt + ";" + parent.getDisplay().getActiveShell());
/*     */               }
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 125 */       if (skinObject != null) {
/* 126 */         String sSuffix = event.type == this.eventOn ? this.suffix : "";
/* 127 */         if ((skinObject != null) && (skinObject.isDebug())) {
/* 128 */           System.out.println(System.currentTimeMillis() + ": " + skinObject + "--" + sSuffix);
/*     */         }
/*     */         
/*     */ 
/* 132 */         Point ptMouse = control.toDisplay(0, 0);
/* 133 */         while (skinObject != null) {
/* 134 */           Rectangle bounds = skinObject.getControl().getBounds();
/* 135 */           Point pt = skinObject.getControl().toDisplay(0, 0);
/* 136 */           bounds.x = pt.x;
/* 137 */           bounds.y = pt.y;
/*     */           
/*     */ 
/* 140 */           if (!bounds.contains(ptMouse)) break;
/* 141 */           skinObject.switchSuffix(sSuffix, 2, false, false);
/* 142 */           skinObject = skinObject.getParent();
/*     */         }
/*     */         
/*     */       }
/*     */     }
/*     */     finally
/*     */     {
/* 149 */       this.lastControl = control;
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/skin/SWTSkinImageChanger.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */