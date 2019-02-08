/*     */ package org.gudy.azureus2.pluginsimpl.local.ui.components;
/*     */ 
/*     */ import com.aelitis.azureus.core.util.CopyOnWriteList;
/*     */ import java.util.Properties;
/*     */ import org.gudy.azureus2.plugins.ui.components.UIComponent;
/*     */ import org.gudy.azureus2.plugins.ui.components.UIPropertyChangeEvent;
/*     */ import org.gudy.azureus2.plugins.ui.components.UIPropertyChangeListener;
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
/*     */ public class UIComponentImpl
/*     */   implements UIComponent
/*     */ {
/*  37 */   protected Properties properties = new Properties();
/*     */   
/*  39 */   protected CopyOnWriteList<UIPropertyChangeListener> listeners = new CopyOnWriteList();
/*     */   
/*     */ 
/*     */ 
/*     */   protected UIComponentImpl()
/*     */   {
/*  45 */     this.properties.put("enabled", Boolean.TRUE);
/*  46 */     this.properties.put("visible", Boolean.TRUE);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setEnabled(boolean enabled)
/*     */   {
/*  53 */     setProperty("enabled", Boolean.valueOf(enabled));
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean getEnabled()
/*     */   {
/*  59 */     return ((Boolean)getProperty("enabled")).booleanValue();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setVisible(boolean visible)
/*     */   {
/*  66 */     setProperty("visible", Boolean.valueOf(visible));
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean getVisible()
/*     */   {
/*  72 */     return ((Boolean)getProperty("visible")).booleanValue();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setProperty(final String property_type, final Object property_value)
/*     */   {
/*  80 */     final Object old_value = this.properties.get(property_type);
/*     */     
/*  82 */     this.properties.put(property_type, property_value);
/*     */     
/*  84 */     UIPropertyChangeEvent ev = new UIPropertyChangeEvent()
/*     */     {
/*     */ 
/*     */       public UIComponent getSource()
/*     */       {
/*     */ 
/*  90 */         return UIComponentImpl.this;
/*     */       }
/*     */       
/*     */ 
/*     */       public String getPropertyType()
/*     */       {
/*  96 */         return property_type;
/*     */       }
/*     */       
/*     */ 
/*     */       public Object getNewPropertyValue()
/*     */       {
/* 102 */         return property_value;
/*     */       }
/*     */       
/*     */ 
/*     */       public Object getOldPropertyValue()
/*     */       {
/* 108 */         return old_value;
/*     */       }
/*     */     };
/*     */     
/* 112 */     for (UIPropertyChangeListener listener : this.listeners)
/*     */     {
/* 114 */       listener.propertyChanged(ev);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public Object getProperty(String property_type)
/*     */   {
/* 122 */     return this.properties.get(property_type);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void addPropertyChangeListener(UIPropertyChangeListener l)
/*     */   {
/* 129 */     this.listeners.add(l);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void removePropertyChangeListener(UIPropertyChangeListener l)
/*     */   {
/* 136 */     this.listeners.remove(l);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/pluginsimpl/local/ui/components/UIComponentImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */