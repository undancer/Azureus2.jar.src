/*    */ package org.gudy.azureus2.platform;
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
/*    */ public final class PlatformManagerCapabilities
/*    */ {
/* 30 */   public static final PlatformManagerCapabilities GetVersion = new PlatformManagerCapabilities("getVersion");
/* 31 */   public static final PlatformManagerCapabilities CreateCommandLineProcess = new PlatformManagerCapabilities("CreateCommandLineProcess");
/* 32 */   public static final PlatformManagerCapabilities UseNativeNotification = new PlatformManagerCapabilities("UseNativeNotification");
/* 33 */   public static final PlatformManagerCapabilities UseNativeScripting = new PlatformManagerCapabilities("UseNativeScripting");
/*    */   
/* 35 */   public static final PlatformManagerCapabilities PlaySystemAlert = new PlatformManagerCapabilities("PlaySystemAlert");
/*    */   
/* 37 */   public static final PlatformManagerCapabilities GetUserDataDirectory = new PlatformManagerCapabilities("GetUserDataDirectory");
/*    */   
/* 39 */   public static final PlatformManagerCapabilities RecoverableFileDelete = new PlatformManagerCapabilities("RecoverableFileDelete");
/* 40 */   public static final PlatformManagerCapabilities RegisterFileAssociations = new PlatformManagerCapabilities("RegisterFileAssociations");
/* 41 */   public static final PlatformManagerCapabilities ShowFileInBrowser = new PlatformManagerCapabilities("ShowFileInBrowser");
/* 42 */   public static final PlatformManagerCapabilities ShowPathInCommandLine = new PlatformManagerCapabilities("ShowPathInCommandLine");
/*    */   
/* 44 */   public static final PlatformManagerCapabilities SetTCPTOSEnabled = new PlatformManagerCapabilities("SetTCPTOSEnabled");
/* 45 */   public static final PlatformManagerCapabilities CopyFilePermissions = new PlatformManagerCapabilities("CopyFilePermissions");
/* 46 */   public static final PlatformManagerCapabilities TestNativeAvailability = new PlatformManagerCapabilities("TestNativeAvailability");
/* 47 */   public static final PlatformManagerCapabilities TraceRouteAvailability = new PlatformManagerCapabilities("TraceRoute");
/* 48 */   public static final PlatformManagerCapabilities PingAvailability = new PlatformManagerCapabilities("Ping");
/*    */   
/* 50 */   public static final PlatformManagerCapabilities ComputerIDAvailability = new PlatformManagerCapabilities("CID");
/*    */   
/* 52 */   public static final PlatformManagerCapabilities RequestUserAttention = new PlatformManagerCapabilities("RequestUserAttention");
/*    */   
/* 54 */   public static final PlatformManagerCapabilities AccessExplicitVMOptions = new PlatformManagerCapabilities("AccessExplicitVMOptions");
/*    */   
/* 56 */   public static final PlatformManagerCapabilities RunAtLogin = new PlatformManagerCapabilities("RunAtLogin");
/* 57 */   public static final PlatformManagerCapabilities GetMaxOpenFiles = new PlatformManagerCapabilities("GetMaxOpenFiles");
/* 58 */   public static final PlatformManagerCapabilities PreventComputerSleep = new PlatformManagerCapabilities("PreventComputerSleep");
/*    */   
/*    */   private final String myName;
/*    */   
/*    */   private PlatformManagerCapabilities(String name)
/*    */   {
/* 64 */     this.myName = name;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public String toString()
/*    */   {
/* 72 */     return this.myName;
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/platform/PlatformManagerCapabilities.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */