/*     */ package org.gudy.azureus2.ui.console.multiuser;
/*     */ 
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.io.PrintStream;
/*     */ import java.util.Collection;
/*     */ import junit.framework.TestCase;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class TestUserManager
/*     */   extends TestCase
/*     */ {
/*     */   private InMemoryUserManager manager;
/*     */   private UserProfile profile1;
/*     */   private UserProfile profile2;
/*     */   
/*     */   protected void setUp()
/*     */     throws Exception
/*     */   {
/*  48 */     super.setUp();
/*     */     
/*  50 */     this.manager = new InMemoryUserManager(null);
/*  51 */     this.profile1 = new UserProfile();
/*  52 */     this.profile1.setUsername("myuser1");
/*  53 */     this.profile1.setPassword("mypassword");
/*  54 */     this.manager.addUser(this.profile1);
/*  55 */     this.profile2 = new UserProfile();
/*  56 */     this.profile2.setUsername("myuser2");
/*  57 */     this.profile2.setPassword("zigzag");
/*  58 */     this.profile2.setUserType("user");
/*  59 */     this.manager.addUser(this.profile2);
/*     */   }
/*     */   
/*     */ 
/*     */   protected void tearDown()
/*     */     throws Exception
/*     */   {
/*  66 */     super.tearDown();
/*     */   }
/*     */   
/*     */   public void testLoadSave()
/*     */   {
/*  71 */     ByteArrayOutputStream out = new ByteArrayOutputStream();
/*  72 */     this.manager.save(out);
/*  73 */     System.out.println("Saved to: " + new String(out.toByteArray()));
/*  74 */     ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
/*  75 */     InMemoryUserManager newManager = new InMemoryUserManager(null);
/*  76 */     newManager.load(in);
/*  77 */     UserProfile profile3 = new UserProfile();
/*  78 */     profile3.setUserType("guest");
/*  79 */     profile3.setUsername("user3");
/*  80 */     profile3.setPassword("whatever");
/*  81 */     assertTrue(this.manager.getUsers().contains(this.profile1));
/*  82 */     assertTrue(this.manager.getUsers().contains(this.profile2));
/*  83 */     assertFalse(this.manager.getUsers().contains(profile3));
/*  84 */     assertTrue(newManager.getUsers().contains(this.profile1));
/*  85 */     assertTrue(newManager.getUsers().contains(this.profile2));
/*  86 */     assertFalse(newManager.getUsers().contains(profile3));
/*     */   }
/*     */   
/*     */   public void testAuthenticate() {
/*  90 */     assertEquals("verify authentication succeeds", this.profile1, this.manager.authenticate("myuser1", "mypassword"));
/*  91 */     assertNull("verify authentication fails", this.manager.authenticate("myuser1", "mypassword_shouldfail"));
/*     */   }
/*     */   
/*     */   private static final class InMemoryUserManager extends UserManager
/*     */   {
/*     */     public InMemoryUserManager(String fileName) {
/*  97 */       super();
/*     */     }
/*     */     
/*     */     public void save(OutputStream out)
/*     */     {
/* 102 */       doSave(out);
/*     */     }
/*     */     
/*     */     public void load(InputStream in) {
/* 106 */       doLoad(in);
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/console/multiuser/TestUserManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */