package com.aelitis.azureus.core.pairing;

import java.io.File;
import java.io.IOException;
import java.util.List;
import org.gudy.azureus2.plugins.tracker.web.TrackerWebPageRequest;
import org.gudy.azureus2.plugins.tracker.web.TrackerWebPageResponse;

public abstract interface PairingManager
{
  public static final String CONFIG_SECTION_ID = "Pairing";
  
  public abstract boolean isEnabled();
  
  public abstract boolean isSRPEnabled();
  
  public abstract void setGroup(String paramString);
  
  public abstract String getGroup();
  
  public abstract List<PairedNode> listGroup()
    throws PairingException;
  
  public abstract List<PairedService> lookupServices(String paramString)
    throws PairingException;
  
  public abstract String getAccessCode()
    throws PairingException;
  
  public abstract String peekAccessCode();
  
  public abstract String getReplacementAccessCode()
    throws PairingException;
  
  public abstract File getQRCode();
  
  public abstract void setSRPPassword(char[] paramArrayOfChar);
  
  public abstract PairedService addService(String paramString, PairedServiceRequestHandler paramPairedServiceRequestHandler);
  
  public abstract PairedService getService(String paramString);
  
  public abstract void setEnabled(boolean paramBoolean);
  
  public abstract void setSRPEnabled(boolean paramBoolean);
  
  public abstract String getStatus();
  
  public abstract String getSRPStatus();
  
  public abstract String getLastServerError();
  
  public abstract boolean hasActionOutstanding();
  
  public abstract PairingTest testService(String paramString, PairingTestListener paramPairingTestListener)
    throws PairingException;
  
  public abstract boolean handleLocalTunnel(TrackerWebPageRequest paramTrackerWebPageRequest, TrackerWebPageResponse paramTrackerWebPageResponse)
    throws IOException;
  
  public abstract void recordRequest(String paramString1, String paramString2, boolean paramBoolean);
  
  public abstract void addListener(PairingManagerListener paramPairingManagerListener);
  
  public abstract void removeListener(PairingManagerListener paramPairingManagerListener);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/pairing/PairingManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */