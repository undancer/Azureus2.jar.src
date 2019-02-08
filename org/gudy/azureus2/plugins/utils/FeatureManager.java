package org.gudy.azureus2.plugins.utils;

import org.gudy.azureus2.plugins.PluginException;

public abstract interface FeatureManager
{
  public abstract Licence[] getLicences();
  
  public abstract Licence[] createLicences(String[] paramArrayOfString)
    throws PluginException;
  
  public abstract Licence addLicence(String paramString)
    throws PluginException;
  
  public abstract FeatureDetails[] getFeatureDetails(String paramString);
  
  public abstract boolean isFeatureInstalled(String paramString);
  
  public abstract void refreshLicences();
  
  public abstract void registerFeatureEnabler(FeatureEnabler paramFeatureEnabler);
  
  public abstract void unregisterFeatureEnabler(FeatureEnabler paramFeatureEnabler);
  
  public abstract void addListener(FeatureManagerListener paramFeatureManagerListener);
  
  public abstract void removeListener(FeatureManagerListener paramFeatureManagerListener);
  
  public static abstract interface FeatureDetails
  {
    public static final String PR_PUBLIC_KEY = "PublicKey";
    public static final String PR_VALID_UNTIL = "ValidUntil";
    public static final String PR_OFFLINE_VALID_UNTIL = "OfflineValidUntil";
    public static final String PR_IS_INSTALL_TIME = "IsInstallTime";
    public static final String PR_IS_TRIAL = "IsTrial";
    public static final String PR_TRIAL_USES_LIMIT = "TrialUsesLimit";
    public static final String PR_TRIAL_USES_FAIL_COUNT = "TrialUsesFailCount";
    public static final String PR_TRIAL_USES_REMAINING = "TrialUsesRemaining";
    public static final String PR_REQUIRED_PLUGINS = "Plugins";
    public static final String PR_FINGERPRINT = "Fingerprint";
    public static final String PR_RENEWAL_KEY = "RenewalKey";
    
    public abstract FeatureManager.Licence getLicence();
    
    public abstract String getID();
    
    public abstract boolean hasExpired();
    
    public abstract byte[] getEncodedProperties();
    
    public abstract byte[] getSignature();
    
    public abstract Object getProperty(String paramString);
    
    public abstract void setProperty(String paramString, Object paramObject);
  }
  
  public static abstract interface FeatureEnabler
  {
    public abstract FeatureManager.Licence[] getLicences();
    
    public abstract FeatureManager.Licence[] createLicences(String[] paramArrayOfString)
      throws PluginException;
    
    public abstract FeatureManager.Licence addLicence(String paramString);
    
    public abstract void refreshLicences();
    
    public abstract void addListener(FeatureManager.FeatureManagerListener paramFeatureManagerListener);
    
    public abstract void removeListener(FeatureManager.FeatureManagerListener paramFeatureManagerListener);
  }
  
  public static abstract interface FeatureManagerListener
  {
    public abstract void licenceAdded(FeatureManager.Licence paramLicence);
    
    public abstract void licenceChanged(FeatureManager.Licence paramLicence);
    
    public abstract void licenceRemoved(FeatureManager.Licence paramLicence);
  }
  
  public static abstract interface Licence
  {
    public static final int LS_PENDING_AUTHENTICATION = 1;
    public static final int LS_AUTHENTICATED = 2;
    public static final int LS_INVALID_KEY = 3;
    public static final int LS_CANCELLED = 4;
    public static final int LS_REVOKED = 5;
    public static final int LS_ACTIVATION_DENIED = 6;
    
    public abstract int getState();
    
    public abstract String getKey();
    
    public abstract String getShortID();
    
    public abstract FeatureManager.FeatureDetails[] getFeatures();
    
    public abstract boolean isFullyInstalled();
    
    public abstract void retryInstallation();
    
    public abstract void addInstallationListener(LicenceInstallationListener paramLicenceInstallationListener);
    
    public abstract void removeInstallationListener(LicenceInstallationListener paramLicenceInstallationListener);
    
    public abstract void remove();
    
    public static abstract interface LicenceInstallationListener
    {
      public abstract void start(String paramString);
      
      public abstract void reportActivity(String paramString1, String paramString2, String paramString3);
      
      public abstract void reportProgress(String paramString1, String paramString2, int paramInt);
      
      public abstract void complete(String paramString);
      
      public abstract void failed(String paramString, PluginException paramPluginException);
    }
  }
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/utils/FeatureManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */