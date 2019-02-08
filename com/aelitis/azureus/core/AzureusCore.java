package com.aelitis.azureus.core;

import com.aelitis.azureus.core.instancemanager.AZInstanceManager;
import com.aelitis.azureus.core.nat.NATTraverser;
import com.aelitis.azureus.core.security.CryptoManager;
import com.aelitis.azureus.core.speedmanager.SpeedManager;
import java.io.File;
import org.gudy.azureus2.core3.global.GlobalManager;
import org.gudy.azureus2.core3.internat.LocaleUtil;
import org.gudy.azureus2.core3.ipfilter.IpFilterManager;
import org.gudy.azureus2.core3.tracker.host.TRHost;
import org.gudy.azureus2.plugins.PluginManager;
import org.gudy.azureus2.plugins.PluginManagerDefaults;
import org.gudy.azureus2.plugins.utils.PowerManagementListener;

public abstract interface AzureusCore
{
  public static final String CA_QUIT_VUZE = "QuitVuze";
  public static final String CA_SLEEP = "Sleep";
  public static final String CA_HIBERNATE = "Hibernate";
  public static final String CA_SHUTDOWN = "Shutdown";
  
  public abstract long getCreateTime();
  
  public abstract boolean canStart();
  
  public abstract void start()
    throws AzureusCoreException;
  
  public abstract boolean isStarted();
  
  public abstract boolean isInitThread();
  
  public abstract void stop()
    throws AzureusCoreException;
  
  public abstract void requestStop()
    throws AzureusCoreException;
  
  public abstract void checkRestartSupported()
    throws AzureusCoreException;
  
  public abstract void restart();
  
  public abstract void requestRestart()
    throws AzureusCoreException;
  
  public abstract boolean isRestarting();
  
  public abstract void executeCloseAction(String paramString1, String paramString2);
  
  public abstract void saveState();
  
  public abstract LocaleUtil getLocaleUtil();
  
  public abstract GlobalManager getGlobalManager()
    throws AzureusCoreException;
  
  public abstract PluginManagerDefaults getPluginManagerDefaults()
    throws AzureusCoreException;
  
  public abstract PluginManager getPluginManager()
    throws AzureusCoreException;
  
  public abstract TRHost getTrackerHost()
    throws AzureusCoreException;
  
  public abstract IpFilterManager getIpFilterManager()
    throws AzureusCoreException;
  
  public abstract AZInstanceManager getInstanceManager();
  
  public abstract SpeedManager getSpeedManager();
  
  public abstract CryptoManager getCryptoManager();
  
  public abstract NATTraverser getNATTraverser();
  
  public abstract File getLockFile();
  
  public abstract void createOperation(int paramInt, AzureusCoreOperationTask paramAzureusCoreOperationTask);
  
  public abstract void addLifecycleListener(AzureusCoreLifecycleListener paramAzureusCoreLifecycleListener);
  
  public abstract void removeLifecycleListener(AzureusCoreLifecycleListener paramAzureusCoreLifecycleListener);
  
  public abstract void addOperationListener(AzureusCoreOperationListener paramAzureusCoreOperationListener);
  
  public abstract void removeOperationListener(AzureusCoreOperationListener paramAzureusCoreOperationListener);
  
  public abstract void triggerLifeCycleComponentCreated(AzureusCoreComponent paramAzureusCoreComponent);
  
  public abstract void addPowerManagementListener(PowerManagementListener paramPowerManagementListener);
  
  public abstract void removePowerManagementListener(PowerManagementListener paramPowerManagementListener);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/AzureusCore.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */