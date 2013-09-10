package ENode.Snapshoting.Impl;

import ENode.Domain.*;

/** The default implementation of ISnapshotter.
 
*/
public class DefaultSnapshotter implements ISnapshotter
{
//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
		///#region Private Variables

	private IAggregateRootFactory _aggregateRootFactory;
	private IAggregateRootTypeProvider _aggregateRootTypeProvider;

//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
		///#endregion

//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
		///#region Constructors

	/** Parameterized constructor.
	 
	 @param aggregateRootFactory
	 @param aggregateRootTypeProvider
	*/
	public DefaultSnapshotter(IAggregateRootFactory aggregateRootFactory, IAggregateRootTypeProvider aggregateRootTypeProvider)
	{
		_aggregateRootFactory = aggregateRootFactory;
		_aggregateRootTypeProvider = aggregateRootTypeProvider;
	}

//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
		///#endregion

	/** Create snapshot for the given aggregate root.
	 
	 @param aggregateRoot
	 @return 
	*/
	public final Snapshot CreateSnapshot(AggregateRoot aggregateRoot)
	{
		if (aggregateRoot == null)
		{
			throw new ArgumentNullException("aggregateRoot");
		}

		if (!IsSnapshotable(aggregateRoot))
		{
			throw new InvalidOperationException(String.format("聚合根(%1$s)没有实现ISnapshotable接口或者实现了多余1个的ISnapshotable接口，不能对其创建快照。", aggregateRoot.getClass().FullName));
		}

		java.lang.Class snapshotDataType = GetSnapshotDataType(aggregateRoot);
		Object snapshotData = SnapshotterHelper.CreateSnapshot(snapshotDataType, aggregateRoot);
		String aggregateRootName = _aggregateRootTypeProvider.GetAggregateRootTypeName(aggregateRoot.getClass());

		return new Snapshot(aggregateRootName, aggregateRoot.getUniqueId(), aggregateRoot.getVersion(), snapshotData, java.util.Date.UtcNow);
	}
	/** Restore the aggregate root from the given snapshot.
	 
	 @param snapshot
	 @return 
	*/
	public final AggregateRoot RestoreFromSnapshot(Snapshot snapshot)
	{
		if (snapshot == null)
		{
			return null;
		}

		java.lang.Class aggregateRootType = _aggregateRootTypeProvider.GetAggregateRootType(snapshot.getAggregateRootName());
		AggregateRoot aggregateRoot = _aggregateRootFactory.CreateAggregateRoot(aggregateRootType);
		if (!IsSnapshotable(aggregateRoot))
		{
			throw new InvalidOperationException(String.format("聚合根(%1$s)没有实现ISnapshotable接口或者实现了多余1个的ISnapshotable接口，不能将其从某个快照还原。", aggregateRoot.getClass().FullName));
		}

		if (GetSnapshotDataType(aggregateRoot) != snapshot.getPayload().getClass())
		{
			throw new InvalidOperationException(String.format("当前聚合根的快照类型(%1$s)与要还原的快照类型(%2$s)不符", GetSnapshotDataType(aggregateRoot), snapshot.getPayload().getClass()));
		}

		aggregateRoot.InitializeFromSnapshot(snapshot);

		SnapshotterHelper.RestoreFromSnapshot(snapshot.getPayload(), aggregateRoot);

		return aggregateRoot;
	}

	private static boolean IsSnapshotable(AggregateRoot aggregateRoot)
	{
//C# TO JAVA CONVERTER TODO TASK: Lambda expressions and anonymous methods are not converted by C# to Java Converter:
		return aggregateRoot.getClass().GetInterfaces().Count(x => x.IsGenericType && x.GetGenericTypeDefinition() == ISnapshotable<>.class) == 1;
	}
	private static java.lang.Class GetSnapshotDataType(AggregateRoot aggregateRoot)
	{
//C# TO JAVA CONVERTER TODO TASK: Lambda expressions and anonymous methods are not converted by C# to Java Converter:
		return aggregateRoot.getClass().GetInterfaces().Single(x => x.IsGenericType && x.GetGenericTypeDefinition() == ISnapshotable<>.class).GetGenericArguments()[0];
	}

	private static class SnapshotterHelper
	{
		private static final java.lang.reflect.Method CreateSnapshotMethod = SnapshotterHelper.class.GetMethod("CreateSnapshot", BindingFlags.Instance | BindingFlags.DeclaredOnly | BindingFlags.NonPublic);
		private static final java.lang.reflect.Method RestoreFromSnapshotMethod = SnapshotterHelper.class.GetMethod("RestoreFromSnapshot", BindingFlags.Instance | BindingFlags.DeclaredOnly | BindingFlags.NonPublic);
		private static final SnapshotterHelper Instance = new SnapshotterHelper();

		public static Object CreateSnapshot(java.lang.Class snapshotDataType, AggregateRoot aggregateRoot)
		{
			return CreateSnapshotMethod.MakeGenericMethod(snapshotDataType).invoke(Instance, new Object[] { aggregateRoot });
		}
		public static Object RestoreFromSnapshot(Object snapshotData, AggregateRoot aggregateRoot)
		{
			return RestoreFromSnapshotMethod.MakeGenericMethod(snapshotData.getClass()).invoke(Instance, new Object[] { aggregateRoot, snapshotData });
		}

		private <TSnapshot> TSnapshot CreateSnapshot(AggregateRoot aggregateRoot)
		{
			return ((ISnapshotable<TSnapshot>)((aggregateRoot instanceof ISnapshotable<TSnapshot>) ? aggregateRoot : null)).CreateSnapshot();
		}
		private <TSnapshot> void RestoreFromSnapshot(AggregateRoot aggregateRoot, TSnapshot snapshot)
		{
			(ISnapshotable<TSnapshot>)((aggregateRoot instanceof ISnapshotable<TSnapshot>) ? aggregateRoot : null).RestoreFromSnapshot(snapshot);
		}
	}
}