package com.aelitis.azureus.core.stats;

import java.util.Map;
import java.util.Set;

public abstract interface AzureusCoreStatsProvider
{
  public abstract void updateStats(Set<String> paramSet, Map<String, Object> paramMap);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/stats/AzureusCoreStatsProvider.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */