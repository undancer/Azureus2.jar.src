/*     */ package org.gudy.azureus2.ui.swt.config;
/*     */ 
/*     */ import java.security.MessageDigest;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.Control;
/*     */ import org.eclipse.swt.widgets.Event;
/*     */ import org.eclipse.swt.widgets.Listener;
/*     */ import org.eclipse.swt.widgets.Text;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.util.AERunnable;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.SHA1Hasher;
/*     */ import org.gudy.azureus2.ui.swt.Utils;
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
/*     */ public class PasswordParameter
/*     */   extends Parameter
/*     */ {
/*     */   String name;
/*     */   Text inputField;
/*     */   
/*     */   public PasswordParameter(Composite composite, String name)
/*     */   {
/*  50 */     this(composite, name, 2);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public PasswordParameter(Composite composite, final String name, final int encoding)
/*     */   {
/*  59 */     super(name);
/*  60 */     this.name = name;
/*  61 */     this.inputField = new Text(composite, 2048);
/*  62 */     this.inputField.setEchoChar('*');
/*  63 */     byte[] value = COConfigurationManager.getByteParameter(name, "".getBytes());
/*  64 */     if (value.length > 0)
/*  65 */       this.inputField.setText("***");
/*  66 */     this.inputField.addListener(24, new Listener() {
/*     */       public void handleEvent(Event event) {
/*     */         try {
/*  69 */           String password_string = PasswordParameter.this.inputField.getText();
/*     */           
/*  71 */           byte[] password = password_string.getBytes();
/*     */           byte[] encoded;
/*  73 */           byte[] encoded; if (password.length > 0) { byte[] encoded;
/*  74 */             if (encoding == 1)
/*     */             {
/*  76 */               encoded = password;
/*     */             } else { byte[] encoded;
/*  78 */               if (encoding == 2)
/*     */               {
/*  80 */                 SHA1Hasher hasher = new SHA1Hasher();
/*     */                 
/*  82 */                 encoded = hasher.calculateHash(password);
/*     */ 
/*     */               }
/*     */               else
/*     */               {
/*     */ 
/*  88 */                 encoded = MessageDigest.getInstance("md5").digest(password_string.getBytes("UTF-8"));
/*     */               }
/*     */             }
/*  91 */           } else { encoded = password;
/*     */           }
/*     */           
/*  94 */           COConfigurationManager.setParameter(name, encoded);
/*     */         } catch (Exception e) {
/*  96 */           Debug.printStackTrace(e);
/*     */         }
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */   public void setLayoutData(Object layoutData) {
/* 103 */     Utils.adjustPXForDPI(layoutData);
/* 104 */     this.inputField.setLayoutData(layoutData);
/*     */   }
/*     */   
/*     */   public void setValue(final String value) {
/* 108 */     Utils.execSWTThread(new AERunnable() {
/*     */       public void runSupport() {
/* 110 */         if ((PasswordParameter.this.inputField == null) || (PasswordParameter.this.inputField.isDisposed()) || (PasswordParameter.this.inputField.getText().equals(value)))
/*     */         {
/* 112 */           return;
/*     */         }
/* 114 */         PasswordParameter.this.inputField.setText(value);
/*     */       }
/*     */     });
/*     */     
/* 118 */     if (!COConfigurationManager.getParameter(this.name).equals(value)) {
/* 119 */       COConfigurationManager.setParameter(this.name, value);
/*     */     }
/*     */   }
/*     */   
/*     */   public String getValue() {
/* 124 */     return this.inputField.getText();
/*     */   }
/*     */   
/*     */   public Control getControl() {
/* 128 */     return this.inputField;
/*     */   }
/*     */   
/*     */   public void setValue(Object value) {
/* 132 */     if ((value instanceof String)) {
/* 133 */       setValue((String)value);
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/config/PasswordParameter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */