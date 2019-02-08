/*     */ package org.gudy.azureus2.pluginsimpl.local.utils;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.PipedInputStream;
/*     */ import java.io.PipedOutputStream;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import java.util.Locale;
/*     */ import java.util.Properties;
/*     */ import java.util.PropertyResourceBundle;
/*     */ import java.util.ResourceBundle;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.config.ParameterListener;
/*     */ import org.gudy.azureus2.core3.internat.LocaleUtil;
/*     */ import org.gudy.azureus2.core3.internat.LocaleUtilDecoder;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.plugins.PluginInterface;
/*     */ import org.gudy.azureus2.plugins.utils.LocaleDecoder;
/*     */ import org.gudy.azureus2.plugins.utils.LocaleListener;
/*     */ import org.gudy.azureus2.plugins.utils.LocaleUtilities;
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
/*     */ public class LocaleUtilitiesImpl
/*     */   implements LocaleUtilities
/*     */ {
/*     */   private PluginInterface pi;
/*     */   private List listeners;
/*     */   
/*     */   public LocaleUtilitiesImpl(PluginInterface _pi)
/*     */   {
/*  54 */     this.pi = _pi;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void integrateLocalisedMessageBundle(String resource_bundle_prefix)
/*     */   {
/*  61 */     MessageText.integratePluginMessages(resource_bundle_prefix, this.pi.getPluginClassLoader());
/*     */   }
/*     */   
/*     */   public void integrateLocalisedMessageBundle(ResourceBundle rb) {
/*  65 */     MessageText.integratePluginMessages(rb);
/*     */   }
/*     */   
/*     */   public void integrateLocalisedMessageBundle(Properties p)
/*     */   {
/*  70 */     ResourceBundle rb = null;
/*     */     try {
/*  72 */       PipedInputStream in_stream = new PipedInputStream();
/*  73 */       PipedOutputStream out_stream = new PipedOutputStream(in_stream);
/*  74 */       p.store(out_stream, "");
/*  75 */       out_stream.close();
/*  76 */       rb = new PropertyResourceBundle(in_stream);
/*  77 */       in_stream.close();
/*     */     } catch (IOException ioe) {
/*  79 */       return; }
/*  80 */     integrateLocalisedMessageBundle(rb);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public String getLocalisedMessageText(String key)
/*     */   {
/*  87 */     return MessageText.getString(key);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getLocalisedMessageText(String key, String[] params)
/*     */   {
/*  95 */     return MessageText.getString(key, params);
/*     */   }
/*     */   
/*     */   public boolean hasLocalisedMessageText(String key) {
/*  99 */     return MessageText.keyExists(key);
/*     */   }
/*     */   
/*     */   public String localise(String key) {
/* 103 */     String res = MessageText.getString(key);
/* 104 */     if ((res.charAt(0) == '!') && (!MessageText.keyExists(key))) {
/* 105 */       return null;
/*     */     }
/* 107 */     return res;
/*     */   }
/*     */   
/*     */   public Locale getCurrentLocale() {
/* 111 */     return MessageText.getCurrentLocale();
/*     */   }
/*     */   
/*     */ 
/*     */   public LocaleDecoder[] getDecoders()
/*     */   {
/* 117 */     LocaleUtilDecoder[] decs = LocaleUtil.getSingleton().getDecoders();
/*     */     
/* 119 */     LocaleDecoder[] res = new LocaleDecoder[decs.length];
/*     */     
/* 121 */     for (int i = 0; i < res.length; i++)
/*     */     {
/* 123 */       res[i] = new LocaleDecoderImpl(decs[i]);
/*     */     }
/*     */     
/* 126 */     return res;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void addListener(LocaleListener l)
/*     */   {
/* 133 */     if (this.listeners == null)
/*     */     {
/* 135 */       this.listeners = new ArrayList();
/*     */       
/* 137 */       COConfigurationManager.addParameterListener("locale.set.complete.count", new ParameterListener()
/*     */       {
/*     */ 
/*     */ 
/*     */         public void parameterChanged(String parameterName)
/*     */         {
/*     */ 
/*     */ 
/* 145 */           for (int i = 0; i < LocaleUtilitiesImpl.this.listeners.size(); i++) {
/*     */             try
/*     */             {
/* 148 */               ((LocaleListener)LocaleUtilitiesImpl.this.listeners.get(i)).localeChanged(MessageText.getCurrentLocale());
/*     */             }
/*     */             catch (Throwable e)
/*     */             {
/* 152 */               Debug.printStackTrace(e);
/*     */             }
/*     */           }
/*     */         }
/*     */       });
/*     */     }
/*     */     
/* 159 */     this.listeners.add(l);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void removeListener(LocaleListener l)
/*     */   {
/* 166 */     if (this.listeners == null)
/*     */     {
/* 168 */       return;
/*     */     }
/*     */     
/* 171 */     this.listeners.remove(l);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/pluginsimpl/local/utils/LocaleUtilitiesImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */