package com.aelitis.azureus.core.metasearch;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Map;

public abstract interface MetaSearch
{
  public abstract MetaSearchManager getManager();
  
  public abstract Engine importFromBEncodedMap(Map<String, Object> paramMap)
    throws IOException;
  
  public abstract Engine[] search(ResultListener paramResultListener, SearchParameter[] paramArrayOfSearchParameter, String paramString, int paramInt);
  
  public abstract Engine[] search(ResultListener paramResultListener, SearchParameter[] paramArrayOfSearchParameter, String paramString, Map<String, String> paramMap, int paramInt);
  
  public abstract Engine[] search(Engine[] paramArrayOfEngine, ResultListener paramResultListener, SearchParameter[] paramArrayOfSearchParameter, String paramString, int paramInt);
  
  public abstract Engine[] search(Engine[] paramArrayOfEngine, ResultListener paramResultListener, SearchParameter[] paramArrayOfSearchParameter, String paramString, Map<String, String> paramMap, int paramInt);
  
  public abstract String getFUD();
  
  public abstract Engine[] getEngines(boolean paramBoolean1, boolean paramBoolean2);
  
  public abstract Engine getEngine(long paramLong);
  
  public abstract Engine getEngineByUID(String paramString);
  
  public abstract void addEngine(Engine paramEngine);
  
  public abstract Engine addEngine(long paramLong)
    throws MetaSearchException;
  
  public abstract Engine createRSSEngine(String paramString, URL paramURL)
    throws MetaSearchException;
  
  public abstract void removeEngine(Engine paramEngine);
  
  public abstract int getEngineCount();
  
  public abstract void enginePreferred(Engine paramEngine);
  
  public abstract void exportEngines(File paramFile)
    throws MetaSearchException;
  
  public abstract void addListener(MetaSearchListener paramMetaSearchListener);
  
  public abstract void removeListener(MetaSearchListener paramMetaSearchListener);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/metasearch/MetaSearch.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */