/*     */ package org.gudy.azureus2.ui.console.multiuser.commands;
/*     */ 
/*     */ import java.io.FileNotFoundException;
/*     */ import java.io.PrintStream;
/*     */ import java.util.Collection;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import org.apache.commons.cli.CommandLine;
/*     */ import org.apache.commons.cli.Option;
/*     */ import org.apache.commons.cli.Options;
/*     */ import org.gudy.azureus2.ui.console.ConsoleInput;
/*     */ import org.gudy.azureus2.ui.console.UserProfile;
/*     */ import org.gudy.azureus2.ui.console.commands.CommandCollection;
/*     */ import org.gudy.azureus2.ui.console.commands.IConsoleCommand;
/*     */ import org.gudy.azureus2.ui.console.commands.OptionsConsoleCommand;
/*     */ import org.gudy.azureus2.ui.console.multiuser.UserManager;
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
/*     */ public class UserCommand
/*     */   extends IConsoleCommand
/*     */ {
/*  45 */   private final CommandCollection subCommands = new CommandCollection();
/*     */   
/*     */   private final UserManager userManager;
/*     */   
/*     */ 
/*     */   public UserCommand(UserManager userManager)
/*     */   {
/*  52 */     super("user");
/*  53 */     this.userManager = userManager;
/*     */     
/*  55 */     this.subCommands.add(new AddUserCommand());
/*  56 */     this.subCommands.add(new DeleteUserCommand());
/*  57 */     this.subCommands.add(new ModifyUserCommand());
/*  58 */     this.subCommands.add(new ListUsersCommand());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private UserManager getUserManager()
/*     */   {
/*  68 */     return this.userManager;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private void saveUserManagerConfig(PrintStream out)
/*     */   {
/*     */     try
/*     */     {
/*  78 */       this.userManager.save();
/*  79 */       out.println("> User Manager config saved");
/*     */     } catch (FileNotFoundException e) {
/*  81 */       out.println("> Error saving User Manager config: " + e);
/*  82 */       e.printStackTrace(out);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getCommandDescriptions()
/*     */   {
/*  91 */     return "user add|delete|list|modify <options>\tmanage users able to log in via telnet ui";
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void execute(String commandName, ConsoleInput ci, List args)
/*     */   {
/*  99 */     if (args.isEmpty())
/*     */     {
/* 101 */       printHelp(ci.out, args);
/*     */     }
/*     */     else
/*     */     {
/* 105 */       commandName = (String)args.remove(0);
/* 106 */       this.subCommands.execute(commandName, ci, args);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void printHelpExtra(PrintStream out, List args)
/*     */   {
/* 115 */     out.println("> -----");
/* 116 */     out.println("'user' syntax:");
/* 117 */     if (args.size() > 0) {
/* 118 */       String command = (String)args.remove(0);
/* 119 */       IConsoleCommand cmd = this.subCommands.get(command);
/* 120 */       if (cmd != null)
/* 121 */         cmd.printHelp(out, args);
/* 122 */       return;
/*     */     }
/* 124 */     out.println("user <command> <command options>");
/* 125 */     out.println();
/* 126 */     out.println("Available <command>s:");
/* 127 */     for (Iterator iter = this.subCommands.iterator(); iter.hasNext();) {
/* 128 */       IConsoleCommand cmd = (IConsoleCommand)iter.next();
/* 129 */       out.println(cmd.getCommandDescriptions());
/*     */     }
/* 131 */     out.println("try 'help user <command>' for more information about a particular user command");
/* 132 */     out.println("> -----");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private final class AddUserCommand
/*     */     extends OptionsConsoleCommand
/*     */   {
/*     */     public AddUserCommand()
/*     */     {
/* 144 */       super("a");
/* 145 */       getOptions().addOption(new Option("u", "username", true, "name of new user"));
/* 146 */       getOptions().addOption(new Option("p", "password", true, "password for new user"));
/* 147 */       getOptions().addOption(new Option("t", "type", true, "user type (Admin / User / Guest)"));
/* 148 */       getOptions().addOption(new Option("d", "savedirectory", true, "default torrent save directory for this user"));
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     public void execute(String commandName, ConsoleInput ci, CommandLine commandLine)
/*     */     {
/* 156 */       String userName = commandLine.getOptionValue('u');
/* 157 */       if (userName == null)
/*     */       {
/* 159 */         ci.out.println("> AddUser: (u)sername option not specified");
/* 160 */         return;
/*     */       }
/*     */       
/* 163 */       String password = commandLine.getOptionValue('p');
/* 164 */       if (password == null)
/*     */       {
/* 166 */         ci.out.println("> AddUser: (p)assword option not specified");
/* 167 */         return;
/*     */       }
/*     */       
/* 170 */       String userType = commandLine.getOptionValue('t', "admin");
/* 171 */       if (!UserProfile.isValidUserType(userType.toLowerCase()))
/*     */       {
/* 173 */         ci.out.println("> AddUser: invalid profile type '" + userType + "'. Valid values are: " + "admin" + "," + "user" + "," + "guest");
/* 174 */         return;
/*     */       }
/*     */       
/*     */ 
/* 178 */       if (UserCommand.this.getUserManager().getUser(userName) != null)
/*     */       {
/* 180 */         ci.out.println("> AddUser error: user '" + userName + "' already exists");
/* 181 */         return;
/*     */       }
/*     */       
/* 184 */       UserProfile profile = new UserProfile(userName, userType);
/* 185 */       profile.setPassword(password);
/* 186 */       String defaultSaveDirectory = commandLine.getOptionValue('d', null);
/* 187 */       profile.setDefaultSaveDirectory(defaultSaveDirectory);
/*     */       
/* 189 */       UserCommand.this.getUserManager().addUser(profile);
/* 190 */       ci.out.println("> AddUser: user '" + userName + "' added");
/* 191 */       UserCommand.this.saveUserManagerConfig(ci.out);
/*     */     }
/*     */     
/*     */     public String getCommandDescriptions() {
/* 195 */       return "add [-u user] <options>\t\ta\tadds a new user";
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private final class DeleteUserCommand
/*     */     extends OptionsConsoleCommand
/*     */   {
/*     */     public DeleteUserCommand()
/*     */     {
/* 208 */       super("d");
/* 209 */       getOptions().addOption(new Option("u", "username", true, "name of user to delete"));
/*     */     }
/*     */     
/*     */     public void execute(String commandName, ConsoleInput ci, CommandLine commandLine)
/*     */     {
/* 214 */       String userName = commandLine.getOptionValue('u');
/* 215 */       if (userName == null)
/*     */       {
/* 217 */         ci.out.println("> DeleteUser: (u)sername option not specified");
/* 218 */         return;
/*     */       }
/*     */       
/* 221 */       if (UserCommand.this.getUserManager().getUser(userName) == null)
/*     */       {
/* 223 */         ci.out.println("> DeleteUser: error - user '" + userName + "' not found");
/* 224 */         return;
/*     */       }
/*     */       
/* 227 */       UserCommand.this.getUserManager().deleteUser(userName);
/* 228 */       ci.out.println("> DeleteUser: user '" + userName + "' deleted");
/* 229 */       UserCommand.this.saveUserManagerConfig(ci.out);
/*     */     }
/*     */     
/*     */     public String getCommandDescriptions() {
/* 233 */       return "delete [-u user]\t\td\tdeletes a user";
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private final class ModifyUserCommand
/*     */     extends OptionsConsoleCommand
/*     */   {
/*     */     public ModifyUserCommand()
/*     */     {
/* 246 */       super("m");
/*     */       
/* 248 */       getOptions().addOption(new Option("u", "username", true, "name of user to modify"));
/* 249 */       getOptions().addOption(new Option("p", "password", true, "password for new user"));
/* 250 */       getOptions().addOption(new Option("t", "type", true, "user type (Admin / User / Guest)"));
/* 251 */       getOptions().addOption(new Option("d", "savedirectory", true, "default torrent save directory for this user"));
/*     */     }
/*     */     
/*     */ 
/*     */     public void execute(String commandName, ConsoleInput ci, CommandLine commandLine)
/*     */     {
/* 257 */       String userName = commandLine.getOptionValue('u');
/* 258 */       if (userName == null)
/*     */       {
/* 260 */         ci.out.println("> ModifyUser: (u)sername option not specified");
/* 261 */         return;
/*     */       }
/*     */       
/* 264 */       UserProfile profile = UserCommand.this.getUserManager().getUser(userName);
/* 265 */       if (profile == null)
/*     */       {
/* 267 */         ci.out.println("> ModifyUser: error - user '" + userName + "' not found");
/* 268 */         return;
/*     */       }
/*     */       
/* 271 */       boolean modified = false;
/*     */       
/* 273 */       String userType = commandLine.getOptionValue('t');
/* 274 */       if (userType != null)
/*     */       {
/* 276 */         if (UserProfile.isValidUserType(userType.toLowerCase()))
/*     */         {
/* 278 */           profile.setUserType(userType.toLowerCase());
/* 279 */           modified = true;
/*     */         }
/*     */         else
/*     */         {
/* 283 */           ci.out.println("> ModifyUser: invalid profile type '" + userType + "'. Valid values are: " + "admin" + "," + "user" + "," + "guest");
/* 284 */           return;
/*     */         }
/*     */       }
/*     */       
/* 288 */       String password = commandLine.getOptionValue('p');
/* 289 */       if (password != null)
/*     */       {
/* 291 */         profile.setPassword(password);
/* 292 */         modified = true;
/*     */       }
/* 294 */       String defaultSaveDirectory = commandLine.getOptionValue('d');
/*     */       
/* 296 */       if (defaultSaveDirectory != null)
/*     */       {
/* 298 */         modified = true;
/*     */         
/* 300 */         if (defaultSaveDirectory.length() > 0)
/*     */         {
/* 302 */           profile.setDefaultSaveDirectory(defaultSaveDirectory);
/*     */         }
/*     */         else {
/* 305 */           profile.setDefaultSaveDirectory(null);
/*     */         }
/*     */       }
/*     */       
/* 309 */       if (modified)
/*     */       {
/* 311 */         ci.out.println("> ModifyUser: user '" + userName + "' modified");
/* 312 */         UserCommand.this.saveUserManagerConfig(ci.out);
/*     */       }
/*     */       else {
/* 315 */         printHelp(ci.out, commandLine.getArgList());
/*     */       }
/*     */     }
/*     */     
/* 319 */     public String getCommandDescriptions() { return "modify [-u user] <options>\tm\tmodifies a user"; }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private final class ListUsersCommand
/*     */     extends IConsoleCommand
/*     */   {
/*     */     public ListUsersCommand()
/*     */     {
/* 331 */       super("l");
/*     */     }
/*     */     
/*     */     public void execute(String commandName, ConsoleInput ci, List args)
/*     */     {
/* 336 */       ci.out.println("> -----");
/* 337 */       ci.out.println("> Username\tProfile\t\tSave Directory");
/* 338 */       for (Iterator iter = UserCommand.this.getUserManager().getUsers().iterator(); iter.hasNext();) {
/* 339 */         UserProfile profile = (UserProfile)iter.next();
/* 340 */         String saveDir = profile.getDefaultSaveDirectory();
/* 341 */         if (saveDir == null) saveDir = "(default)";
/* 342 */         ci.out.println("> " + profile.getUsername() + "\t\t" + profile.getUserType() + "\t\t" + saveDir);
/*     */       }
/* 344 */       ci.out.println("> -----");
/*     */     }
/*     */     
/*     */     public String getCommandDescriptions() {
/* 348 */       return "list \t\t\t\tl\tlists all users";
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/console/multiuser/commands/UserCommand.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */