package ENode.Infrastructure.Concurrent;

/** A class provide the functionality to lock object by value object.
 
*/
public final class LockUtility
{
	private static class LockObject
	{
		private int privateCounter;
		public final int getCounter()
		{
			return privateCounter;
		}
		public final void setCounter(int value)
		{
			privateCounter = value;
		}
	}

	private static final java.util.Hashtable LockPool = new java.util.Hashtable();

	/** Lock an action by a given key value object.
	 
	 @param key
	 @param action
	*/
	public static void Lock(Object key, Action action)
	{
		LockObject lockObj = GetLockObject(key);
		try
		{
			synchronized (lockObj)
			{
				action();
			}
		}
		finally
		{
			ReleaseLockObject(key, lockObj);
		}
	}

	private static void ReleaseLockObject(Object key, LockObject lockObj)
	{
		lockObj.setCounter(lockObj.getCounter() - 1);
		synchronized (LockPool)
		{
			if (lockObj.getCounter() == 0)
			{
				LockPool.remove(key);
			}
		}
	}
	private static LockObject GetLockObject(Object key)
	{
		synchronized (LockPool)
		{
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
			var lockObj = (LockObject)((LockPool.get(key) instanceof LockObject) ? LockPool.get(key) : null);
			if (lockObj == null)
			{
				lockObj = new LockObject();
				LockPool.put(key, lockObj);
			}
			lockObj.Counter++;
			return lockObj;
		}
	}
}