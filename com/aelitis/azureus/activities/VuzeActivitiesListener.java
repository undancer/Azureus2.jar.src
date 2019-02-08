package com.aelitis.azureus.activities;

public abstract interface VuzeActivitiesListener
{
  public abstract void vuzeNewsEntriesAdded(VuzeActivitiesEntry[] paramArrayOfVuzeActivitiesEntry);
  
  public abstract void vuzeNewsEntriesRemoved(VuzeActivitiesEntry[] paramArrayOfVuzeActivitiesEntry);
  
  public abstract void vuzeNewsEntryChanged(VuzeActivitiesEntry paramVuzeActivitiesEntry);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/activities/VuzeActivitiesListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */