/*    */ package org.gudy.azureus2.ui.console.multiuser.persist;
/*    */ 
/*    */ import java.beans.XMLDecoder;
/*    */ import java.beans.XMLEncoder;
/*    */ import java.io.BufferedOutputStream;
/*    */ import java.io.InputStream;
/*    */ import java.io.OutputStream;
/*    */ import java.io.PrintStream;
/*    */ import java.util.ArrayList;
/*    */ import java.util.Iterator;
/*    */ import java.util.List;
/*    */ import java.util.Map;
/*    */ import org.gudy.azureus2.ui.console.UserProfile;
/*    */ import org.gudy.azureus2.ui.console.multiuser.UserManager.UserManagerConfig;
/*    */ import org.gudy.azureus2.ui.console.multiuser.UserManagerPersister;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class UserManagerXMLPersist
/*    */   implements UserManagerPersister
/*    */ {
/*    */   public void doSave(OutputStream out, Map usersMap)
/*    */   {
/* 53 */     UserManager.UserManagerConfig config = new UserManager.UserManagerConfig();
/* 54 */     List users = new ArrayList(usersMap.values());
/* 55 */     config.setUsers(users);
/*    */     
/* 57 */     XMLEncoder encoder = new XMLEncoder(new BufferedOutputStream(out));
/* 58 */     encoder.writeObject(config);
/* 59 */     encoder.close();
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public void doLoad(InputStream in, Map usersMap)
/*    */   {
/* 67 */     XMLDecoder decoder = new XMLDecoder(in);
/* 68 */     UserManager.UserManagerConfig managerConfig = (UserManager.UserManagerConfig)decoder.readObject();
/* 69 */     for (Iterator iter = managerConfig.getUsers().iterator(); iter.hasNext();) {
/* 70 */       UserProfile user = (UserProfile)iter.next();
/* 71 */       usersMap.put(user.getUsername().toLowerCase(), user);
/*    */     }
/* 73 */     System.out.println("UserManager: registered " + usersMap.size() + " users");
/* 74 */     decoder.close();
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/console/multiuser/persist/UserManagerXMLPersist.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */