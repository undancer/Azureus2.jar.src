package com.aelitis.azureus.core.metasearch;

import com.aelitis.azureus.core.vuzefile.VuzeFile;
import java.util.Map;
import org.gudy.azureus2.plugins.utils.search.SearchProvider;

public abstract interface MetaSearchManager
{
  public abstract MetaSearch getMetaSearch();
  
  public abstract boolean isAutoMode();
  
  public abstract void setSelectedEngines(long[] paramArrayOfLong, boolean paramBoolean)
    throws MetaSearchException;
  
  public abstract Engine addEngine(long paramLong, int paramInt, String paramString1, String paramString2)
    throws MetaSearchException;
  
  public abstract boolean isImportable(VuzeFile paramVuzeFile);
  
  public abstract Engine importEngine(Map paramMap, boolean paramBoolean)
    throws MetaSearchException;
  
  public abstract Engine getEngine(SearchProvider paramSearchProvider);
  
  public abstract boolean getProxyRequestsEnabled();
  
  public abstract void setProxyRequestsEnabled(boolean paramBoolean);
  
  public abstract void addListener(MetaSearchManagerListener paramMetaSearchManagerListener);
  
  public abstract void removeListener(MetaSearchManagerListener paramMetaSearchManagerListener);
  
  public abstract void log(String paramString);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/metasearch/MetaSearchManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */