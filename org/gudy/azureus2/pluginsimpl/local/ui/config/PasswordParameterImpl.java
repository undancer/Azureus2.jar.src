/*     */ package org.gudy.azureus2.pluginsimpl.local.ui.config;
/*     */ 
/*     */ import java.security.MessageDigest;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.SHA1Hasher;
/*     */ import org.gudy.azureus2.plugins.ui.config.PasswordParameter;
/*     */ import org.gudy.azureus2.pluginsimpl.local.PluginConfigImpl;
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
/*     */ 
/*     */ 
/*     */ public class PasswordParameterImpl
/*     */   extends ParameterImpl
/*     */   implements PasswordParameter
/*     */ {
/*     */   protected byte[] defaultValue;
/*     */   protected int encoding_type;
/*     */   
/*     */   public PasswordParameterImpl(PluginConfigImpl config, String key, String label, int _encoding_type, byte[] _default_value)
/*     */   {
/*  50 */     super(config, key, label);
/*     */     
/*  52 */     this.encoding_type = _encoding_type;
/*     */     
/*  54 */     if (_default_value == null)
/*     */     {
/*  56 */       this.defaultValue = new byte[0];
/*     */     }
/*     */     else
/*     */     {
/*  60 */       this.defaultValue = encode(_default_value);
/*     */     }
/*     */     
/*  63 */     config.notifyParamExists(getKey());
/*     */     
/*  65 */     COConfigurationManager.setByteDefault(getKey(), this.defaultValue);
/*     */   }
/*     */   
/*     */   public byte[] getDefaultValue()
/*     */   {
/*  70 */     return this.defaultValue;
/*     */   }
/*     */   
/*     */ 
/*     */   public void setValue(String plain_password)
/*     */   {
/*     */     byte[] encoded;
/*     */     
/*     */     byte[] encoded;
/*  79 */     if ((plain_password == null) || (plain_password.length() == 0))
/*     */     {
/*  81 */       encoded = new byte[0];
/*     */     }
/*     */     else
/*     */     {
/*  85 */       encoded = encode(plain_password);
/*     */     }
/*     */     
/*  88 */     this.config.setUnsafeByteParameter(getKey(), encoded);
/*     */   }
/*     */   
/*     */ 
/*     */   public int getEncodingType()
/*     */   {
/*  94 */     return this.encoding_type;
/*     */   }
/*     */   
/*     */ 
/*     */   public byte[] getValue()
/*     */   {
/* 100 */     return this.config.getUnsafeByteParameter(getKey(), getDefaultValue());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected byte[] encode(String str)
/*     */   {
/*     */     try
/*     */     {
/* 111 */       return encode(this.encoding_type == 3 ? str.getBytes("UTF-8") : str.getBytes());
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/* 115 */       Debug.out(e);
/*     */     }
/* 117 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected byte[] encode(byte[] bytes)
/*     */   {
/* 125 */     if (this.encoding_type == 2)
/*     */     {
/* 127 */       SHA1Hasher hasher = new SHA1Hasher();
/*     */       
/* 129 */       return hasher.calculateHash(bytes);
/*     */     }
/* 131 */     if (this.encoding_type == 3) {
/*     */       try
/*     */       {
/* 134 */         return MessageDigest.getInstance("md5").digest(bytes);
/*     */       }
/*     */       catch (Throwable e)
/*     */       {
/* 138 */         Debug.printStackTrace(e);
/*     */       }
/*     */     }
/*     */     
/* 142 */     return bytes;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/pluginsimpl/local/ui/config/PasswordParameterImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */