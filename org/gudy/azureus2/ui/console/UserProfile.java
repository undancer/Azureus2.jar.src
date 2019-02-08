/*     */ package org.gudy.azureus2.ui.console;
/*     */ 
/*     */ import com.aelitis.azureus.core.AzureusCoreException;
/*     */ import org.gudy.azureus2.ui.console.util.StringEncrypter;
/*     */ import org.gudy.azureus2.ui.console.util.StringEncrypter.EncryptionException;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class UserProfile
/*     */ {
/*     */   private String username;
/*     */   private String userType;
/*     */   private String encryptedPassword;
/*     */   private String defaultSaveDirectory;
/*     */   public static final String ADMIN = "admin";
/*     */   public static final String USER = "user";
/*     */   public static final String GUEST = "guest";
/*     */   public static final String DEFAULT_USER_TYPE = "admin";
/*  53 */   public static final UserProfile DEFAULT_USER_PROFILE = new UserProfile("admin", "admin");
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static boolean isValidUserType(String userType)
/*     */   {
/*  62 */     return ("admin".equals(userType)) || ("user".equals(userType)) || ("guest".equals(userType));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public UserProfile()
/*     */   {
/*  70 */     this.userType = "admin";
/*     */   }
/*     */   
/*     */   public UserProfile(String name, String userType)
/*     */   {
/*  75 */     this.username = name;
/*  76 */     setUserType(userType);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean authenticate(String password)
/*     */   {
/*     */     try
/*     */     {
/*  88 */       StringEncrypter encrypter = new StringEncrypter("DES");
/*  89 */       return encrypter.decrypt(this.encryptedPassword).equals(password);
/*     */     } catch (StringEncrypter.EncryptionException e) {
/*  91 */       throw new AzureusCoreException("Unable to decrypt password", e);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setPassword(String password)
/*     */   {
/*     */     try
/*     */     {
/* 102 */       StringEncrypter encrypter = new StringEncrypter("DES");
/* 103 */       setEncryptedPassword(encrypter.encrypt(password));
/*     */     }
/*     */     catch (StringEncrypter.EncryptionException e) {
/* 106 */       throw new AzureusCoreException("Unable to encrypt password", e);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getUsername()
/*     */   {
/* 115 */     return this.username;
/*     */   }
/*     */   
/*     */ 
/*     */   public void setUsername(String username)
/*     */   {
/* 121 */     this.username = username;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getUserType()
/*     */   {
/* 127 */     return this.userType;
/*     */   }
/*     */   
/*     */ 
/*     */   public void setUserType(String userType)
/*     */   {
/* 133 */     if (userType.equalsIgnoreCase("admin")) {
/* 134 */       userType = "admin";
/* 135 */     } else if (userType.equalsIgnoreCase("user")) {
/* 136 */       userType = "user";
/* 137 */     } else if (userType.equalsIgnoreCase("guest")) {
/* 138 */       userType = "guest";
/*     */     } else
/* 140 */       userType = "admin";
/* 141 */     this.userType = userType;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean equals(Object obj)
/*     */   {
/* 148 */     if ((obj == null) || (!(obj instanceof UserProfile)))
/* 149 */       return false;
/* 150 */     UserProfile other = (UserProfile)obj;
/* 151 */     if (getUsername() != null) {
/* 152 */       return getUsername().equals(other.getUsername());
/*     */     }
/* 154 */     if (other.getUsername() != null)
/* 155 */       return false;
/* 156 */     if (getEncryptedPassword() != null) {
/* 157 */       return getEncryptedPassword().equals(other.getEncryptedPassword());
/*     */     }
/* 159 */     if (other.getEncryptedPassword() != null) {
/* 160 */       return false;
/*     */     }
/* 162 */     return true;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getEncryptedPassword()
/*     */   {
/* 168 */     return this.encryptedPassword;
/*     */   }
/*     */   
/*     */ 
/*     */   public void setEncryptedPassword(String encryptedPassword)
/*     */   {
/* 174 */     this.encryptedPassword = encryptedPassword;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public String getDefaultSaveDirectory()
/*     */   {
/* 181 */     return this.defaultSaveDirectory;
/*     */   }
/*     */   
/*     */   public void setDefaultSaveDirectory(String newValue) {
/* 185 */     this.defaultSaveDirectory = newValue;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/console/UserProfile.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */