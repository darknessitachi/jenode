package ENode.Infrastructure.Concurrent;

/** An extension class to provide utility lock mechanism.
 
*/
public final class ReaderWriterLockSlimExtensions
{
	/** An atom read action wrapper.
	 
	 @param readerWriterLockSlim
	 @param action
	 @exception ArgumentNullException
	*/
//C# TO JAVA CONVERTER TODO TASK: Extension methods are not available in Java:
//ORIGINAL LINE: public static void AtomRead(this ReaderWriterLockSlim readerWriterLockSlim, Action action)
	public static void AtomRead(ReaderWriterLockSlim readerWriterLockSlim, Action action)
	{
		if (readerWriterLockSlim == null)
		{
			throw new ArgumentNullException("readerWriterLockSlim");
		}
		if (action == null)
		{
			throw new ArgumentNullException("action");
		}

		readerWriterLockSlim.EnterReadLock();

		try
		{
			action();
		}
		finally
		{
			readerWriterLockSlim.ExitReadLock();
		}
	}
	/** An atom read func wrapper.
	 
	 @param readerWriterLockSlim
	 @param function
	 <typeparam name="T"></typeparam>
	 @return 
	 @exception ArgumentNullException
	*/
//C# TO JAVA CONVERTER TODO TASK: Extension methods are not available in Java:
//ORIGINAL LINE: public static T AtomRead<T>(this ReaderWriterLockSlim readerWriterLockSlim, Func<T> function)
	public static <T> T AtomRead(ReaderWriterLockSlim readerWriterLockSlim, Func<T> function)
	{
		if (readerWriterLockSlim == null)
		{
			throw new ArgumentNullException("readerWriterLockSlim");
		}
		if (function == null)
		{
			throw new ArgumentNullException("function");
		}

		readerWriterLockSlim.EnterReadLock();

		try
		{
			return function();
		}
		finally
		{
			readerWriterLockSlim.ExitReadLock();
		}
	}
	/** An atom write action wrapper.
	 
	 @param readerWriterLockSlim
	 @param action
	 @exception ArgumentNullException
	*/
//C# TO JAVA CONVERTER TODO TASK: Extension methods are not available in Java:
//ORIGINAL LINE: public static void AtomWrite(this ReaderWriterLockSlim readerWriterLockSlim, Action action)
	public static void AtomWrite(ReaderWriterLockSlim readerWriterLockSlim, Action action)
	{
		if (readerWriterLockSlim == null)
		{
			throw new ArgumentNullException("readerWriterLockSlim");
		}
		if (action == null)
		{
			throw new ArgumentNullException("action");
		}

		readerWriterLockSlim.EnterWriteLock();

		try
		{
			action();
		}
		finally
		{
			readerWriterLockSlim.ExitWriteLock();
		}
	}
	/** An atom write func wrapper.
	 
	 @param readerWriterLockSlim
	 @param function
	 <typeparam name="T"></typeparam>
	 @return 
	 @exception ArgumentNullException
	*/
//C# TO JAVA CONVERTER TODO TASK: Extension methods are not available in Java:
//ORIGINAL LINE: public static T AtomWrite<T>(this ReaderWriterLockSlim readerWriterLockSlim, Func<T> function)
	public static <T> T AtomWrite(ReaderWriterLockSlim readerWriterLockSlim, Func<T> function)
	{
		if (readerWriterLockSlim == null)
		{
			throw new ArgumentNullException("readerWriterLockSlim");
		}
		if (function == null)
		{
			throw new ArgumentNullException("function");
		}

		readerWriterLockSlim.EnterWriteLock();

		try
		{
			return function();
		}
		finally
		{
			readerWriterLockSlim.ExitWriteLock();
		}
	}
}