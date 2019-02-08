/*     */ package org.gudy.azureus2.ui.console.multiuser;
/*     */ 
/*     */ import com.aelitis.azureus.core.AzureusCoreException;
/*     */ import java.io.BufferedInputStream;
/*     */ import java.io.File;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.FileNotFoundException;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.io.PrintStream;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import org.gudy.azureus2.plugins.PluginInterface;
/*     */ import org.gudy.azureus2.plugins.utils.Utilities;
/*     */ import org.gudy.azureus2.ui.console.UserProfile;
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
/*     */ public class UserManager
/*     */ {
/*     */   private static final String USER_DB_CONFIG_FILE = "console.users.properties";
/*     */   private static UserManager instance;
/*  44 */   private Map usersMap = new HashMap();
/*     */   
/*     */ 
/*     */ 
/*     */   private final String fileName;
/*     */   
/*     */ 
/*     */ 
/*     */   public UserManager(String fileName)
/*     */   {
/*  54 */     this.fileName = fileName;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public UserProfile authenticate(String username, String password)
/*     */   {
/*  67 */     UserProfile profile = getUser(username);
/*  68 */     if (profile != null)
/*     */     {
/*  70 */       if (profile.authenticate(password))
/*  71 */         return profile;
/*     */     }
/*  73 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public UserProfile getUser(String username)
/*     */   {
/*  84 */     return (UserProfile)this.usersMap.get(username.toLowerCase());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void addUser(UserProfile user)
/*     */   {
/*  93 */     this.usersMap.put(user.getUsername().toLowerCase(), user);
/*     */   }
/*     */   
/*     */   public Collection getUsers()
/*     */   {
/*  98 */     return Collections.unmodifiableCollection(this.usersMap.values());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void load()
/*     */     throws FileNotFoundException
/*     */   {
/* 111 */     BufferedInputStream bis = new BufferedInputStream(new FileInputStream(this.fileName));
/* 112 */     doLoad(bis);
/*     */   }
/*     */   
/*     */   protected void doLoad(InputStream in)
/*     */   {
/*     */     try {
/* 118 */       Class cla = Class.forName("org.gudy.azureus2.ui.console.multiuser.persist.UserManagerXMLPersist");
/*     */       
/* 120 */       UserManagerPersister persister = (UserManagerPersister)cla.newInstance();
/*     */       
/* 122 */       persister.doLoad(in, this.usersMap);
/*     */     }
/*     */     catch (ClassNotFoundException e)
/*     */     {
/* 126 */       System.err.println("No persistence service for user config, load not performed");
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/* 130 */       e.printStackTrace();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void save()
/*     */     throws FileNotFoundException
/*     */   {
/* 143 */     OutputStream out = new FileOutputStream(this.fileName);
/* 144 */     doSave(out);
/*     */   }
/*     */   
/*     */   protected void doSave(OutputStream out)
/*     */   {
/*     */     try {
/* 150 */       Class cla = Class.forName("org.gudy.azureus2.ui.console.multiuser.persist.UserManagerXMLPersist");
/*     */       
/* 152 */       UserManagerPersister persister = (UserManagerPersister)cla.newInstance();
/*     */       
/* 154 */       persister.doSave(out, this.usersMap);
/*     */     }
/*     */     catch (ClassNotFoundException e)
/*     */     {
/* 158 */       System.err.println("No persistence service for user config, save not performed");
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/* 162 */       e.printStackTrace();
/*     */     }
/*     */   }
/*     */   
/*     */   public static UserManager getInstance(PluginInterface pi)
/*     */   {
/* 168 */     synchronized (UserManager.class) {
/* 169 */       if (instance == null)
/*     */       {
/* 171 */         String azureusUserDir = pi.getUtilities().getAzureusUserDir();
/* 172 */         File dbFile = new File(azureusUserDir, "console.users.properties");
/*     */         try
/*     */         {
/* 175 */           instance = new UserManager(dbFile.getCanonicalPath());
/* 176 */           if (dbFile.exists())
/*     */           {
/* 178 */             System.out.println("loading user configuration from: " + dbFile.getCanonicalPath());
/* 179 */             instance.load();
/*     */           }
/*     */           else
/*     */           {
/* 183 */             System.out.println("file: " + dbFile.getCanonicalPath() + " does not exist. using 'null' user manager");
/*     */           }
/*     */         }
/*     */         catch (IOException e) {
/* 187 */           throw new AzureusCoreException("Unable to instantiate default user manager");
/*     */         }
/*     */       }
/*     */       
/* 191 */       return instance;
/*     */     }
/*     */   }
/*     */   
/*     */   public static final class UserManagerConfig
/*     */   {
/* 197 */     private List users = new ArrayList();
/*     */     
/*     */ 
/*     */ 
/*     */     public List getUsers()
/*     */     {
/* 203 */       return this.users;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     public void setUsers(List users)
/*     */     {
/* 210 */       this.users = users;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     public void addUser(UserProfile user)
/*     */     {
/* 219 */       this.users.add(user);
/*     */     }
/*     */     
/*     */     public void clear()
/*     */     {
/* 224 */       this.users.clear();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void deleteUser(String userName)
/*     */   {
/* 234 */     this.usersMap.remove(userName.toLowerCase());
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/console/multiuser/UserManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */