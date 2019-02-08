/*     */ package com.aelitis.azureus.ui.swt.utils;
/*     */ 
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import org.eclipse.swt.graphics.Color;
/*     */ import org.eclipse.swt.graphics.Device;
/*     */ import org.eclipse.swt.graphics.RGB;
/*     */ import org.gudy.azureus2.core3.util.Debug;
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
/*     */ public class ColorCache2
/*     */ {
/*  34 */   private static Map<RGB, CachedColorManaged> color_map = new HashMap();
/*     */   
/*     */ 
/*     */ 
/*     */   public static CachedColor getColor(Color c)
/*     */   {
/*  40 */     return new CachedColorUnmanaged(c, null);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static CachedColor getColor(Device device, RGB rgb)
/*     */   {
/*  48 */     synchronized (color_map)
/*     */     {
/*  50 */       CachedColorManaged entry = (CachedColorManaged)color_map.get(rgb);
/*     */       
/*  52 */       if (entry == null)
/*     */       {
/*  54 */         entry = new CachedColorManaged(new Color(device, rgb), null);
/*     */         
/*  56 */         color_map.put(rgb, entry);
/*     */       }
/*     */       else
/*     */       {
/*  60 */         entry.addRef();
/*     */       }
/*     */       
/*  63 */       return new CachedColorManagedFacade(entry, null);
/*     */     }
/*     */   }
/*     */   
/*     */   public static abstract interface CachedColor { public abstract Color getColor();
/*     */     
/*     */     public abstract boolean isDisposed();
/*     */     
/*     */     public abstract void dispose();
/*     */   }
/*     */   
/*     */   private static class CachedColorManaged { private Color color;
/*     */     private int ref_count;
/*     */     
/*  77 */     private CachedColorManaged(Color _color) { this.color = _color;
/*  78 */       this.ref_count = 1;
/*     */     }
/*     */     
/*     */ 
/*     */     public Color getColor()
/*     */     {
/*  84 */       return this.color;
/*     */     }
/*     */     
/*     */ 
/*     */     private void addRef()
/*     */     {
/*  90 */       this.ref_count += 1;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     private void dispose()
/*     */     {
/*  98 */       this.ref_count -= 1;
/*     */       
/*     */ 
/*     */ 
/* 102 */       if (this.ref_count == 0)
/*     */       {
/* 104 */         ColorCache2.color_map.remove(this.color.getRGB());
/*     */         
/* 106 */         this.color.dispose();
/*     */       }
/* 108 */       else if (this.ref_count < 0)
/*     */       {
/* 110 */         Debug.out("already disposed");
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   private static class CachedColorManagedFacade
/*     */     implements ColorCache2.CachedColor
/*     */   {
/*     */     private ColorCache2.CachedColorManaged delegate;
/*     */     
/*     */     private boolean disposed;
/*     */     
/*     */ 
/*     */     private CachedColorManagedFacade(ColorCache2.CachedColorManaged _delegate)
/*     */     {
/* 126 */       this.delegate = _delegate;
/*     */     }
/*     */     
/*     */ 
/*     */     public Color getColor()
/*     */     {
/* 132 */       return this.delegate.getColor();
/*     */     }
/*     */     
/*     */     /* Error */
/*     */     public boolean isDisposed()
/*     */     {
/*     */       // Byte code:
/*     */       //   0: invokestatic 46	com/aelitis/azureus/ui/swt/utils/ColorCache2:access$400	()Ljava/util/Map;
/*     */       //   3: dup
/*     */       //   4: astore_1
/*     */       //   5: monitorenter
/*     */       //   6: aload_0
/*     */       //   7: getfield 44	com/aelitis/azureus/ui/swt/utils/ColorCache2$CachedColorManagedFacade:disposed	Z
/*     */       //   10: aload_1
/*     */       //   11: monitorexit
/*     */       //   12: ireturn
/*     */       //   13: astore_2
/*     */       //   14: aload_1
/*     */       //   15: monitorexit
/*     */       //   16: aload_2
/*     */       //   17: athrow
/*     */       // Line number table:
/*     */       //   Java source line #138	-> byte code offset #0
/*     */       //   Java source line #140	-> byte code offset #6
/*     */       //   Java source line #141	-> byte code offset #13
/*     */       // Local variable table:
/*     */       //   start	length	slot	name	signature
/*     */       //   0	18	0	this	CachedColorManagedFacade
/*     */       //   4	11	1	Ljava/lang/Object;	Object
/*     */       //   13	4	2	localObject1	Object
/*     */       // Exception table:
/*     */       //   from	to	target	type
/*     */       //   6	12	13	finally
/*     */       //   13	16	13	finally
/*     */     }
/*     */     
/*     */     public void dispose()
/*     */     {
/* 147 */       synchronized (ColorCache2.color_map)
/*     */       {
/* 149 */         if (!this.disposed)
/*     */         {
/* 151 */           this.disposed = true;
/*     */           
/* 153 */           this.delegate.dispose();
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private static class CachedColorUnmanaged
/*     */     implements ColorCache2.CachedColor
/*     */   {
/*     */     private Color color;
/*     */     
/*     */ 
/*     */     private CachedColorUnmanaged(Color _color)
/*     */     {
/* 169 */       this.color = _color;
/*     */     }
/*     */     
/*     */ 
/*     */     public Color getColor()
/*     */     {
/* 175 */       return this.color;
/*     */     }
/*     */     
/*     */ 
/*     */     public boolean isDisposed()
/*     */     {
/* 181 */       return this.color.isDisposed();
/*     */     }
/*     */     
/*     */ 
/*     */     public void dispose()
/*     */     {
/* 187 */       this.color.dispose();
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/utils/ColorCache2.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */