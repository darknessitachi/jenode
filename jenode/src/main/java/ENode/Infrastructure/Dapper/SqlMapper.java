package ENode.Infrastructure.Dapper;

//
// License: http://www.apache.org/licenses/LICENSE-2.0 
// Home page: http://code.google.com/p/dapper-dot-net/
//
// Note: to build on C# 3.0 + .NET 3.5, include the CSHARP30 compiler symbol (and yes,
// I know the difference between language and runtime versions; this is a compromise).
// 







public final class SqlMapper
{
	/** 
	 Implement this interface to pass an arbitrary db specific set of parameters to Dapper
	 
	*/
	public interface IDynamicParameters
	{
		/** 
		 Add all the parameters needed to the command just before it executes
		 
		 @param command The raw command prior to execution
		 @param identity Information about the query
		*/
		void AddParameters(IDbCommand command, Identity identity);
	}

	/** 
	 Implement this interface to change default mapping of reader columns to type memebers
	 
	*/
	public interface ITypeMap
	{
		/** 
		 Finds best constructor
		 
		 @param names DataReader column names
		 @param types DataReader column types
		 @return Matching constructor or default one
		*/
		java.lang.reflect.Constructor FindConstructor(String[] names, java.lang.Class[] types);

		/** 
		 Gets mapping for constructor parameter
		 
		 @param constructor Constructor to resolve
		 @param columnName DataReader column name
		 @return Mapping implementation
		*/
		IMemberMap GetConstructorParameter(java.lang.reflect.Constructor constructor, String columnName);

		/** 
		 Gets member mapping for column
		 
		 @param columnName DataReader column name
		 @return Mapping implementation
		*/
		IMemberMap GetMember(String columnName);
	}

	/** 
	 Implements this interface to provide custom member mapping
	 
	*/
	public interface IMemberMap
	{
		/** 
		 Source DataReader column name
		 
		*/
		String getColumnName();

		/** 
		  Target member type
		 
		*/
		java.lang.Class getMemberType();

		/** 
		 Target property
		 
		*/
		PropertyInfo getProperty();

		/** 
		 Target field
		 
		*/
		java.lang.reflect.Field getField();

		/** 
		 Target constructor parameter
		 
		*/
		ParameterInfo getParameter();
	}

	private static Link<java.lang.Class, Action<IDbCommand, Boolean>> bindByNameCache;
	private static Action<IDbCommand, Boolean> GetBindByName(java.lang.Class commandType)
	{
		if (commandType == null) // GIGO
		{
			return null;
		}
		Action<IDbCommand, Boolean> action = null;
		RefObject<Action<IDbCommand, Boolean>> tempRef_action = new RefObject<Action<IDbCommand, Boolean>>(action);
		boolean tempVar = Link<java.lang.Class, Action<IDbCommand, Boolean>>.TryGet(bindByNameCache, commandType, tempRef_action);
			action = tempRef_action.argvalue;
		if (tempVar)
		{
			return action;
		}
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
		var prop = commandType.GetProperty("BindByName", BindingFlags.Public | BindingFlags.Instance);
		action = null;
		ParameterInfo[] indexers;
		java.lang.reflect.Method setter;
		if (prop != null && prop.CanWrite && prop.PropertyType == Boolean.class && ((indexers = prop.GetIndexParameters()) == null || indexers.length == 0) && (setter = prop.GetSetMethod()) != null)
		{
			DynamicMethod method = new DynamicMethod(commandType.getName() + "_BindByName", null, new java.lang.Class[] { IDbCommand.class, Boolean.class });
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
			var il = method.GetILGenerator();
			il.Emit(OpCodes.Ldarg_0);
			il.Emit(OpCodes.Castclass, commandType);
			il.Emit(OpCodes.Ldarg_1);
			il.EmitCall(OpCodes.Callvirt, setter, null);
			il.Emit(OpCodes.Ret);
			action = (Action<IDbCommand, Boolean>)method.CreateDelegate(Action<IDbCommand, Boolean>.class);
		}
		// cache it            
		RefObject<Link<java.lang.Class, Action<IDbCommand, Boolean>>> tempRef_bindByNameCache = new RefObject<Link<java.lang.Class, Action<IDbCommand, Boolean>>>(bindByNameCache);
		RefObject<Action<IDbCommand, Boolean>> tempRef_action2 = new RefObject<Action<IDbCommand, Boolean>>(action);
		Link<java.lang.Class, Action<IDbCommand, Boolean>>.TryAdd(tempRef_bindByNameCache, commandType, tempRef_action2);
		bindByNameCache = tempRef_bindByNameCache.argvalue;
		action = tempRef_action2.argvalue;
		return action;
	}
	/** 
	 This is a micro-cache; suitable when the number of terms is controllable (a few hundred, for example),
	 and strictly append-only; you cannot change existing values. All key matches are on **REFERENCE**
	 equality. The type is fully thread-safe.
	 
	*/
//C# TO JAVA CONVERTER TODO TASK: The C# 'class' constraint has no equivalent in Java:
	private static class Link<TKey extends class, TValue>
	{
		public static boolean TryGet(Link<TKey, TValue> link, TKey key, RefObject<TValue> value)
		{
			while (link != null)
			{
				if ((Object)key == (Object)link.getKey())
				{
					value.argvalue = link.getValue();
					return true;
				}
				link = link.getTail();
			}
			value.argvalue = null;
			return false;
		}
		public static boolean TryAdd(RefObject<Link<TKey, TValue>> head, TKey key, RefObject<TValue> value)
		{
			boolean tryAgain;
			do
			{
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
				var snapshot = Interlocked.CompareExchange(head, null, null);
				TValue found = null;
				RefObject<TValue> tempRef_found = new RefObject<TValue>(found);
				boolean tempVar = TryGet(snapshot, key, tempRef_found);
					found = tempRef_found.argvalue;
				if (tempVar)
				{ // existing match; report the existing value instead
					value.argvalue = found;
					return false;
				}
				Link<TKey, TValue> newNode = new Link<TKey, TValue>(key, value.argvalue, snapshot);
				// did somebody move our cheese?
				tryAgain = Interlocked.CompareExchange(head, newNode, snapshot) != snapshot;
			} while (tryAgain);
			return true;
		}
		private Link(TKey key, TValue value, Link<TKey, TValue> tail)
		{
			setKey(key);
			setValue(value);
			setTail(tail);
		}
		private TKey privateKey;
		public final TKey getKey()
		{
			return privateKey;
		}
		private void setKey(TKey value)
		{
			privateKey = value;
		}
		private TValue privateValue;
		public final TValue getValue()
		{
			return privateValue;
		}
		private void setValue(TValue value)
		{
			privateValue = value;
		}
		private Link<TKey, TValue> privateTail;
		public final Link<TKey, TValue> getTail()
		{
			return privateTail;
		}
		private void setTail(Link<TKey, TValue> value)
		{
			privateTail = value;
		}
	}
	private static class CacheInfo
	{
		private DeserializerState privateDeserializer;
		public final DeserializerState getDeserializer()
		{
			return privateDeserializer;
		}
		public final void setDeserializer(DeserializerState value)
		{
			privateDeserializer = value.clone();
		}
		private Func<IDataReader, Object>[] privateOtherDeserializers;
		public final Func<IDataReader, Object>[] getOtherDeserializers()
		{
			return privateOtherDeserializers;
		}
	public Func<IDataReader, Object> [] void setOtherDeserializers(Func<IDataReader, Object>[] value);
		{
			privateOtherDeserializers = value;
		}
		private Action<IDbCommand, Object> privateParamReader;
		public final Action<IDbCommand, Object> getParamReader()
		{
			return privateParamReader;
		}
		public final void setParamReader(Action<IDbCommand, Object> value)
		{
			privateParamReader = value;
		}
		private int hitCount;
		public final int GetHitCount()
		{
			RefObject<Integer> tempRef_hitCount = new RefObject<Integer>(hitCount);
			int tempVar = Interlocked.CompareExchange(tempRef_hitCount, 0, 0);
			hitCount = tempRef_hitCount.argvalue;
			return tempVar;
		}
		public final void RecordHit()
		{
			RefObject<Integer> tempRef_hitCount = new RefObject<Integer>(hitCount);
			Interlocked.Increment(tempRef_hitCount);
			hitCount = tempRef_hitCount.argvalue;
		}
	}
	private static int GetColumnHash(IDataReader reader)
	{
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to an 'unchecked' block in Java:
		unchecked
		{
			int colCount = reader.FieldCount, hash = colCount;
			for (int i = 0; i < colCount; i++)
			{ // binding code is only interested in names - not types
				Object tmp = reader.GetName(i);
				hash = (hash * 31) + (tmp == null ? 0 : tmp.hashCode());
			}
			return hash;
		}
	}
//C# TO JAVA CONVERTER WARNING: Java does not allow user-defined value types. The behavior of this class will differ from the original:
//ORIGINAL LINE: struct DeserializerState
	private final static class DeserializerState
	{
		public int Hash;
		public Func<IDataReader, Object> Func;

		public DeserializerState(int hash, Func<IDataReader, Object> func)
		{
			Hash = hash;
			Func = func;
		}

		public DeserializerState clone()
		{
			DeserializerState varCopy = new DeserializerState();

			varCopy.Hash = this.Hash;
			varCopy.Func = this.Func;

			return varCopy;
		}
	}

	/** 
	 Called if the query cache is purged via PurgeQueryCache
	 
	*/
//C# TO JAVA CONVERTER TODO TASK: Events are not available in Java:
//	public static event EventHandler QueryCachePurged;
	private static void OnQueryCachePurged()
	{
		EventHandler handler = QueryCachePurged;
		if (handler != null)
		{
			handler(null, EventArgs.Empty);
		}
	}
//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
//#if CSHARP30
	private static final java.util.HashMap<Identity, CacheInfo> _queryCache = new java.util.HashMap<Identity, CacheInfo>();
	// note: conflicts between readers and writers are so short-lived that it isn't worth the overhead of
	// ReaderWriterLockSlim etc; a simple lock is faster
	private static void SetQueryCache(Identity key, CacheInfo value)
	{
		synchronized (_queryCache)
		{
			_queryCache.put(key, value);
		}
	}
	private static boolean TryGetQueryCache(Identity key, RefObject<CacheInfo> value)
	{
		synchronized (_queryCache)
		{
			return (value.argvalue = _queryCache.get(key)) != null;
		}
	}
	private static void PurgeQueryCacheByType(java.lang.Class type)
	{
		synchronized (_queryCache)
		{
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
//C# TO JAVA CONVERTER TODO TASK: There is no Java equivalent to LINQ queries:
//C# TO JAVA CONVERTER TODO TASK: Lambda expressions and anonymous methods are not converted by C# to Java Converter:
			var toRemove = _queryCache.keySet().Where(id => id.type == type).toArray();
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
			for (var key : toRemove)
			{
				_queryCache.remove(key);
			}
		}
	}
	/** 
	 Purge the query cache 
	 
	*/
	public static void PurgeQueryCache()
	{
		synchronized (_queryCache)
		{
			 _queryCache.clear();
		}
		OnQueryCachePurged();
	}
//#else
	private static final System.Collections.Concurrent.ConcurrentDictionary<Identity, CacheInfo> _queryCache = new System.Collections.Concurrent.ConcurrentDictionary<Identity, CacheInfo>();
	private static void SetQueryCache(Identity key, CacheInfo value)
	{
		RefObject<Integer> tempRef_collect = new RefObject<Integer>(collect);
		boolean tempVar = Interlocked.Increment(tempRef_collect) == COLLECT_PER_ITEMS;
			collect = tempRef_collect.argvalue;
		if (tempVar)
		{
			CollectCacheGarbage();
		}
		_queryCache.put(key, value);
	}

	private static void CollectCacheGarbage()
	{
		try
		{
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
			for (var pair : _queryCache)
			{
				if (pair.getValue().GetHitCount() <= COLLECT_HIT_COUNT_MIN)
				{
					CacheInfo cache = null;
					RefObject<CacheInfo> tempRef_cache = new RefObject<CacheInfo>(cache);
					_queryCache.TryRemove(pair.getKey(), tempRef_cache);
					cache = tempRef_cache.argvalue;
				}
			}
		}

		finally
		{
			RefObject<Integer> tempRef_collect = new RefObject<Integer>(collect);
			Interlocked.Exchange(tempRef_collect, 0);
			collect = tempRef_collect.argvalue;
		}
	}

	private static final int COLLECT_PER_ITEMS = 1000, COLLECT_HIT_COUNT_MIN = 0;
	private static int collect;
	private static boolean TryGetQueryCache(Identity key, RefObject<CacheInfo> value)
	{
		if ((value.argvalue = _queryCache.get(key)) != null)
		{
			value.argvalue.RecordHit();
			return true;
		}
		value.argvalue = null;
		return false;
	}

	/** 
	 Purge the query cache 
	 
	*/
	public static void PurgeQueryCache()
	{
		_queryCache.clear();
		OnQueryCachePurged();
	}

	private static void PurgeQueryCacheByType(java.lang.Class type)
	{
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
		for (var entry : _queryCache)
		{
			CacheInfo cache = null;
			if (entry.getKey().type == type)
			{
				RefObject<CacheInfo> tempRef_cache = new RefObject<CacheInfo>(cache);
				_queryCache.TryRemove(entry.getKey(), tempRef_cache);
				cache = tempRef_cache.argvalue;
			}
		}
	}

	/** 
	 Return a count of all the cached queries by dapper
	 
	 @return 
	*/
	public static int GetCachedSQLCount()
	{
		return _queryCache.size();
	}

	/** 
	 Return a list of all the queries cached by dapper
	 
	 @param ignoreHitCountAbove
	 @return 
	*/
//C# TO JAVA CONVERTER TODO TASK: C# optional parameters are not converted to Java:
//ORIGINAL LINE: public static IEnumerable<Tuple<string, string, int>> GetCachedSQL(int ignoreHitCountAbove = int.MaxValue)
	public static Iterable<Tuple<String, String, Integer>> GetCachedSQL(int ignoreHitCountAbove)
	{
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
//C# TO JAVA CONVERTER TODO TASK: Lambda expressions and anonymous methods are not converted by C# to Java Converter:
		var data = _queryCache.Select(pair => Tuple.Create(pair.getKey().connectionString, pair.getKey().sql, pair.getValue().GetHitCount()));
		if (ignoreHitCountAbove < Integer.MAX_VALUE)
		{
//C# TO JAVA CONVERTER TODO TASK: There is no Java equivalent to LINQ queries:
//C# TO JAVA CONVERTER TODO TASK: Lambda expressions and anonymous methods are not converted by C# to Java Converter:
			data = data.Where(tuple => tuple.Item3 <= ignoreHitCountAbove);
		}
		return data;
	}

	/** 
	 Deep diagnostics only: find any hash collisions in the cache
	 
	 @return 
	*/
	public static Iterable<Tuple<Integer, Integer>> GetHashCollissions()
	{
		java.util.HashMap<Integer, Integer> counts = new java.util.HashMap<Integer, Integer>();
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
		for (var key : _queryCache.keySet())
		{
			int count = 0;
			if (!((count = counts.get(key.hashCode)) != null))
			{
				counts.put(key.hashCode, 1);
			}
			else
			{
				counts.put(key.hashCode, count + 1);
			}
		}
//C# TO JAVA CONVERTER TODO TASK: There is no Java equivalent to LINQ queries:
		return from pair in counts where pair.getValue() > 1 select Tuple.Create(pair.getKey(), pair.getValue());

	}
//#endif


	private static java.util.HashMap<java.lang.Class, DbType> typeMap;

	static
	{
		typeMap = new java.util.HashMap<java.lang.Class, DbType>();
		typeMap.put(Byte.class, DbType.Byte);
		typeMap.put(Byte.class, DbType.SByte);
		typeMap.put(Short.class, DbType.Int16);
		typeMap.put(Short.class, DbType.UInt16);
		typeMap.put(Integer.class, DbType.Int32);
		typeMap.put(Integer.class, DbType.UInt32);
		typeMap.put(Long.class, DbType.Int64);
		typeMap.put(Long.class, DbType.UInt64);
		typeMap.put(Float.class, DbType.Single);
		typeMap.put(Double.class, DbType.Double);
		typeMap.put(java.math.BigDecimal.class, DbType.Decimal);
		typeMap.put(Boolean.class, DbType.Boolean);
		typeMap.put(String.class, DbType.String);
		typeMap.put(Character.class, DbType.StringFixedLength);
		typeMap.put(Guid.class, DbType.Guid);
		typeMap.put(java.util.Date.class, DbType.DateTime);
		typeMap.put(DateTimeOffset.class, DbType.DateTimeOffset);
		typeMap.put(TimeSpan.class, DbType.Time);
		typeMap.put(byte[].class, DbType.Binary);
		typeMap.put(Byte.class, DbType.Byte);
		typeMap.put(Byte.class, DbType.SByte);
		typeMap.put(Short.class, DbType.Int16);
		typeMap.put(Short.class, DbType.UInt16);
		typeMap.put(Integer.class, DbType.Int32);
		typeMap.put(Integer.class, DbType.UInt32);
		typeMap.put(Long.class, DbType.Int64);
		typeMap.put(Long.class, DbType.UInt64);
		typeMap.put(Float.class, DbType.Single);
		typeMap.put(Double.class, DbType.Double);
		typeMap.put(java.math.BigDecimal.class, DbType.Decimal);
		typeMap.put(Boolean.class, DbType.Boolean);
		typeMap.put(Character.class, DbType.StringFixedLength);
		typeMap.put(Guid.class, DbType.Guid);
		typeMap.put(java.util.Date.class, DbType.DateTime);
		typeMap.put(DateTimeOffset.class, DbType.DateTimeOffset);
		typeMap.put(TimeSpan.class, DbType.Time);
		typeMap.put(Object.class, DbType.Object);
	}

	public static final String LinqBinary = "System.Data.Linq.Binary";
	public static DbType LookupDbType(java.lang.Class type, String name)
	{
		DbType dbType;
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
		var nullUnderlyingType = Nullable.GetUnderlyingType(type);
		if (nullUnderlyingType != null)
		{
			type = nullUnderlyingType;
		}
		if (type.IsEnum)
		{
			type = Enum.GetUnderlyingType(type);
		}
		if ((dbType = typeMap.get(type)) != null)
		{
			return dbType;
		}
		if (LinqBinary.equals(type.FullName))
		{
			return DbType.Binary;
		}
		if (Iterable.class.IsAssignableFrom(type))
		{
			return DynamicParameters.EnumerableMultiParameter;
		}


		throw new NotSupportedException(String.format("The member %1$s of type %2$s cannot be used as a parameter value", name, type));
	}


	/** 
	 Identity of a cached query in Dapper, used for extensability
	 
	*/
	public static class Identity implements IEquatable<Identity>
	{
		public final Identity ForGrid(java.lang.Class primaryType, int gridIndex)
		{
			return new Identity(sql, commandType, connectionString, primaryType, parametersType, null, gridIndex);
		}

		public final Identity ForGrid(java.lang.Class primaryType, java.lang.Class[] otherTypes, int gridIndex)
		{
			return new Identity(sql, commandType, connectionString, primaryType, parametersType, otherTypes, gridIndex);
		}
		/** 
		 Create an identity for use with DynamicParameters, internal use only
		 
		 @param type
		 @return 
		*/
		public final Identity ForDynamicParameters(java.lang.Class type)
		{
			return new Identity(sql, commandType, connectionString, this.type, type, null, -1);
		}

		public Identity(String sql, CommandType commandType, IDbConnection connection, java.lang.Class type, java.lang.Class parametersType, java.lang.Class[] otherTypes)
		{
			this(sql, commandType, connection.ConnectionString, type, parametersType, otherTypes, 0);
		}
		private Identity(String sql, CommandType commandType, String connectionString, java.lang.Class type, java.lang.Class parametersType, java.lang.Class[] otherTypes, int gridIndex)
		{
			this.sql = sql;
			this.commandType = commandType;
			this.connectionString = connectionString;
			this.type = type;
			this.parametersType = parametersType;
			this.gridIndex = gridIndex;
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to an 'unchecked' block in Java:
			unchecked
			{
				hashCode = 17; // we *know* we are using this in a dictionary, so pre-compute this
				hashCode = hashCode * 23 + commandType.hashCode();
				hashCode = hashCode * 23 + (new Integer(gridIndex)).hashCode();
				hashCode = hashCode * 23 + (sql == null ? 0 : sql.hashCode());
				hashCode = hashCode * 23 + (type == null ? 0 : type.hashCode());
				if (otherTypes != null)
				{
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
					for (var t : otherTypes)
					{
						hashCode = hashCode * 23 + (t == null ? 0 : t.hashCode());
					}
				}
				hashCode = hashCode * 23 + (connectionString == null ? 0 : connectionString.hashCode());
				hashCode = hashCode * 23 + (parametersType == null ? 0 : parametersType.hashCode());
			}
		}

		/** 
		 
		 
		 @param obj
		 @return 
		*/
		@Override
		public boolean equals(Object obj)
		{
			return equals((Identity)((obj instanceof Identity) ? obj : null));
		}
		/** 
		 The sql
		 
		*/
		public String sql;
		/** 
		 The command type 
		 
		*/
		public CommandType commandType;

		/** 
		 
		 
		*/
		public int hashCode, gridIndex;
		/** 
		 
		 
		*/
		public java.lang.Class type;
		/** 
		 
		 
		*/
		public String connectionString;
		/** 
		 
		 
		*/
		public java.lang.Class parametersType;
		/** 
		 
		 
		 @return 
		*/
		@Override
		public int hashCode()
		{
			return hashCode;
		}
		/** 
		 Compare 2 Identity objects
		 
		 @param other
		 @return 
		*/
		public final boolean equals(Identity other)
		{
			return other != null && gridIndex == other.gridIndex && type == other.type && sql.equals(other.sql) && commandType.equals(other.commandType) && connectionString.equals(other.connectionString) && parametersType == other.parametersType;
		}
	}

//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
//#if CSHARP30
	/** 
	 Execute parameterized SQL  
	 
	 @return Number of rows affected
	*/
//C# TO JAVA CONVERTER TODO TASK: Extension methods are not available in Java:
//ORIGINAL LINE: public static int Execute(this IDbConnection cnn, string sql, object param)
	public static int Execute(IDbConnection cnn, String sql, Object param)
	{
		return Execute(cnn, sql, param, null, null, null);
	}

	/** 
	 Execute parameterized SQL
	 
	 @return Number of rows affected
	*/
//C# TO JAVA CONVERTER TODO TASK: Extension methods are not available in Java:
//ORIGINAL LINE: public static int Execute(this IDbConnection cnn, string sql, object param, IDbTransaction transaction)
	public static int Execute(IDbConnection cnn, String sql, Object param, IDbTransaction transaction)
	{
		return Execute(cnn, sql, param, transaction, null, null);
	}

	/** 
	 Execute parameterized SQL
	 
	 @return Number of rows affected
	*/
//C# TO JAVA CONVERTER TODO TASK: Extension methods are not available in Java:
//ORIGINAL LINE: public static int Execute(this IDbConnection cnn, string sql, object param, CommandType commandType)
	public static int Execute(IDbConnection cnn, String sql, Object param, CommandType commandType)
	{
		return Execute(cnn, sql, param, null, null, commandType);
	}

	/** 
	 Execute parameterized SQL
	 
	 @return Number of rows affected
	*/
//C# TO JAVA CONVERTER TODO TASK: Extension methods are not available in Java:
//ORIGINAL LINE: public static int Execute(this IDbConnection cnn, string sql, object param, IDbTransaction transaction, CommandType commandType)
	public static int Execute(IDbConnection cnn, String sql, Object param, IDbTransaction transaction, CommandType commandType)
	{
		return Execute(cnn, sql, param, transaction, null, commandType);
	}

	/** 
	 Executes a query, returning the data typed as per T
	 
	 @return A sequence of data of the supplied type; if a basic type (int, string, etc) is queried then the data from the first column in assumed, otherwise an instance is
	 created per row, and a direct column-name===member-name mapping is assumed (case insensitive).
	 
	*/
//C# TO JAVA CONVERTER TODO TASK: Extension methods are not available in Java:
//ORIGINAL LINE: public static IEnumerable<T> Query<T>(this IDbConnection cnn, string sql, object param)
	public static <T> Iterable<T> Query(IDbConnection cnn, String sql, Object param)
	{
		return Query<T>(cnn, sql, param, null, true, null, null);
	}

	/** 
	 Executes a query, returning the data typed as per T
	 
	 @return A sequence of data of the supplied type; if a basic type (int, string, etc) is queried then the data from the first column in assumed, otherwise an instance is
	 created per row, and a direct column-name===member-name mapping is assumed (case insensitive).
	 
	*/
//C# TO JAVA CONVERTER TODO TASK: Extension methods are not available in Java:
//ORIGINAL LINE: public static IEnumerable<T> Query<T>(this IDbConnection cnn, string sql, object param, IDbTransaction transaction)
	public static <T> Iterable<T> Query(IDbConnection cnn, String sql, Object param, IDbTransaction transaction)
	{
		return Query<T>(cnn, sql, param, transaction, true, null, null);
	}

	/** 
	 Executes a query, returning the data typed as per T
	 
	 @return A sequence of data of the supplied type; if a basic type (int, string, etc) is queried then the data from the first column in assumed, otherwise an instance is
	 created per row, and a direct column-name===member-name mapping is assumed (case insensitive).
	 
	*/
//C# TO JAVA CONVERTER TODO TASK: Extension methods are not available in Java:
//ORIGINAL LINE: public static IEnumerable<T> Query<T>(this IDbConnection cnn, string sql, object param, CommandType commandType)
	public static <T> Iterable<T> Query(IDbConnection cnn, String sql, Object param, CommandType commandType)
	{
		return Query<T>(cnn, sql, param, null, true, null, commandType);
	}

	/** 
	 Executes a query, returning the data typed as per T
	 
	 @return A sequence of data of the supplied type; if a basic type (int, string, etc) is queried then the data from the first column in assumed, otherwise an instance is
	 created per row, and a direct column-name===member-name mapping is assumed (case insensitive).
	 
	*/
//C# TO JAVA CONVERTER TODO TASK: Extension methods are not available in Java:
//ORIGINAL LINE: public static IEnumerable<T> Query<T>(this IDbConnection cnn, string sql, object param, IDbTransaction transaction, CommandType commandType)
	public static <T> Iterable<T> Query(IDbConnection cnn, String sql, Object param, IDbTransaction transaction, CommandType commandType)
	{
		return Query<T>(cnn, sql, param, transaction, true, null, commandType);
	}

	/** 
	 Execute a command that returns multiple result sets, and access each in turn
	 
	*/
//C# TO JAVA CONVERTER TODO TASK: Extension methods are not available in Java:
//ORIGINAL LINE: public static GridReader QueryMultiple(this IDbConnection cnn, string sql, object param, IDbTransaction transaction)
	public static GridReader QueryMultiple(IDbConnection cnn, String sql, Object param, IDbTransaction transaction)
	{
		return QueryMultiple(cnn, sql, param, transaction, null, null);
	}

	/** 
	 Execute a command that returns multiple result sets, and access each in turn
	 
	*/
//C# TO JAVA CONVERTER TODO TASK: Extension methods are not available in Java:
//ORIGINAL LINE: public static GridReader QueryMultiple(this IDbConnection cnn, string sql, object param, CommandType commandType)
	public static GridReader QueryMultiple(IDbConnection cnn, String sql, Object param, CommandType commandType)
	{
		return QueryMultiple(cnn, sql, param, null, null, commandType);
	}

	/** 
	 Execute a command that returns multiple result sets, and access each in turn
	 
	*/
//C# TO JAVA CONVERTER TODO TASK: Extension methods are not available in Java:
//ORIGINAL LINE: public static GridReader QueryMultiple(this IDbConnection cnn, string sql, object param, IDbTransaction transaction, CommandType commandType)
	public static GridReader QueryMultiple(IDbConnection cnn, String sql, Object param, IDbTransaction transaction, CommandType commandType)
	{
		return QueryMultiple(cnn, sql, param, transaction, null, commandType);
	}
//#endif
	/** 
	 Execute parameterized SQL  
	 
	 @return Number of rows affected
	*/
//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
//#if CSHARP30
//C# TO JAVA CONVERTER TODO TASK: Extension methods are not available in Java:
//ORIGINAL LINE: public static int Execute(this IDbConnection cnn, string sql, object param, IDbTransaction transaction, Nullable<int> commandTimeout, Nullable<CommandType> commandType)
	public static int Execute(IDbConnection cnn, String sql, Object param, IDbTransaction transaction, Integer commandTimeout, CommandType commandType)
//#else
//C# TO JAVA CONVERTER TODO TASK: Extension methods are not available in Java:
//ORIGINAL LINE: public static int Execute(this IDbConnection cnn, string sql, dynamic param = null, IDbTransaction transaction = null, Nullable<int> commandTimeout = null, Nullable<CommandType> commandType = null)
//C# TO JAVA CONVERTER TODO TASK: C# optional parameters are not converted to Java:
	public static int Execute(IDbConnection cnn, String sql, dynamic param, IDbTransaction transaction, Integer commandTimeout, CommandType commandType)
//#endif
	{
		Iterable multiExec = (Iterable)(((Object)param instanceof Iterable) ? (Object)param : null);
		Identity identity;
		CacheInfo info = null;
		if (multiExec != null && !(multiExec instanceof String))
		{
			boolean isFirst = true;
			int total = 0;
//C# TO JAVA CONVERTER NOTE: The following 'using' block is replaced by its Java equivalent:
//			using (var cmd = SetupCommand(cnn, transaction, sql, null, null, commandTimeout, commandType))
			IDbCommand cmd = SetupCommand(cnn, transaction, sql, null, null, commandTimeout, commandType);
			try
			{

				String masterSql = null;
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
				for (var obj : multiExec)
				{
					if (isFirst)
					{
						masterSql = cmd.CommandText;
						isFirst = false;
						identity = new Identity(sql, cmd.CommandType, cnn, null, obj.getClass(), null);
						info = GetCacheInfo(identity);
					}
					else
					{
						cmd.CommandText = masterSql; // because we do magic replaces on "in" etc
						cmd.Parameters.Clear(); // current code is Add-tastic
					}
					info.ParamReader(cmd, obj);
					total += cmd.ExecuteNonQuery();
				}
			}
			finally
			{
				cmd.dispose();
			}
			return total;
		}

		// nice and simple
		if ((Object)param != null)
		{
			identity = new Identity(sql, commandType, cnn, null, (Object)param == null ? null : ((Object)param).getClass(), null);
			info = GetCacheInfo(identity);
		}
		return ExecuteCommand(cnn, transaction, sql, (Object)param == null ? null : info.getParamReader(), (Object)param, commandTimeout, commandType);
	}
//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
//#if !CSHARP30
	/** 
	 Return a list of dynamic objects, reader is closed after the call
	 
	*/
//C# TO JAVA CONVERTER TODO TASK: Extension methods are not available in Java:
//ORIGINAL LINE: public static IEnumerable<dynamic> Query(this IDbConnection cnn, string sql, dynamic param = null, IDbTransaction transaction = null, bool buffered = true, Nullable<int> commandTimeout = null, Nullable<CommandType> commandType = null)
//C# TO JAVA CONVERTER TODO TASK: C# optional parameters are not converted to Java:
	public static Iterable<dynamic> Query(IDbConnection cnn, String sql, dynamic param, IDbTransaction transaction, boolean buffered, Integer commandTimeout, CommandType commandType)
	{
		return Query<DapperRow>(cnn, sql, (Object)((param instanceof Object) ? param : null), transaction, buffered, commandTimeout, commandType);
	}
//#else
	/** 
	 Return a list of dynamic objects, reader is closed after the call
	 
	*/
//C# TO JAVA CONVERTER TODO TASK: Extension methods are not available in Java:
//ORIGINAL LINE: public static IEnumerable<IDictionary<string, object>> Query(this IDbConnection cnn, string sql, object param)
	public static Iterable<java.util.Map<String, Object>> Query(IDbConnection cnn, String sql, Object param)
	{
		return Query(cnn, sql, param, null, true, null, null);
	}

	/** 
	 Return a list of dynamic objects, reader is closed after the call
	 
	*/
//C# TO JAVA CONVERTER TODO TASK: Extension methods are not available in Java:
//ORIGINAL LINE: public static IEnumerable<IDictionary<string, object>> Query(this IDbConnection cnn, string sql, object param, IDbTransaction transaction)
	public static Iterable<java.util.Map<String, Object>> Query(IDbConnection cnn, String sql, Object param, IDbTransaction transaction)
	{
		return Query(cnn, sql, param, transaction, true, null, null);
	}

	/** 
	 Return a list of dynamic objects, reader is closed after the call
	 
	*/
//C# TO JAVA CONVERTER TODO TASK: Extension methods are not available in Java:
//ORIGINAL LINE: public static IEnumerable<IDictionary<string, object>> Query(this IDbConnection cnn, string sql, object param, Nullable<CommandType> commandType)
	public static Iterable<java.util.Map<String, Object>> Query(IDbConnection cnn, String sql, Object param, CommandType commandType)
	{
		return Query(cnn, sql, param, null, true, null, commandType);
	}

	/** 
	 Return a list of dynamic objects, reader is closed after the call
	 
	*/
//C# TO JAVA CONVERTER TODO TASK: Extension methods are not available in Java:
//ORIGINAL LINE: public static IEnumerable<IDictionary<string, object>> Query(this IDbConnection cnn, string sql, object param, IDbTransaction transaction, Nullable<CommandType> commandType)
	public static Iterable<java.util.Map<String, Object>> Query(IDbConnection cnn, String sql, Object param, IDbTransaction transaction, CommandType commandType)
	{
		return Query(cnn, sql, param, transaction, true, null, commandType);
	}

	/** 
	 Return a list of dynamic objects, reader is closed after the call
	 
	*/
//C# TO JAVA CONVERTER TODO TASK: Extension methods are not available in Java:
//ORIGINAL LINE: public static IEnumerable<IDictionary<string,object>> Query(this IDbConnection cnn, string sql, object param, IDbTransaction transaction, bool buffered, Nullable<int> commandTimeout, Nullable<CommandType> commandType)
	public static Iterable<java.util.Map<String,Object>> Query(IDbConnection cnn, String sql, Object param, IDbTransaction transaction, boolean buffered, Integer commandTimeout, CommandType commandType)
	{
		return Query<java.util.Map<String, Object>>(cnn, sql, param, transaction, buffered, commandTimeout, commandType);
	}
//#endif

	/** 
	 Executes a query, returning the data typed as per T
	 
	 the dynamic param may seem a bit odd, but this works around a major usability issue in vs, if it is Object vs completion gets annoying. Eg type new [space] get new object
	 @return A sequence of data of the supplied type; if a basic type (int, string, etc) is queried then the data from the first column in assumed, otherwise an instance is
	 created per row, and a direct column-name===member-name mapping is assumed (case insensitive).
	 
	*/
//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
//#if CSHARP30
//C# TO JAVA CONVERTER TODO TASK: Extension methods are not available in Java:
//ORIGINAL LINE: public static IEnumerable<T> Query<T>(this IDbConnection cnn, string sql, object param, IDbTransaction transaction, bool buffered, Nullable<int> commandTimeout, Nullable<CommandType> commandType)
	public static <T> Iterable<T> Query(IDbConnection cnn, String sql, Object param, IDbTransaction transaction, boolean buffered, Integer commandTimeout, CommandType commandType)
//#else
//C# TO JAVA CONVERTER TODO TASK: Extension methods are not available in Java:
//ORIGINAL LINE: public static IEnumerable<T> Query<T>(this IDbConnection cnn, string sql, dynamic param = null, IDbTransaction transaction = null, bool buffered = true, Nullable<int> commandTimeout = null, Nullable<CommandType> commandType = null)
//C# TO JAVA CONVERTER TODO TASK: C# optional parameters are not converted to Java:
	public static <T> Iterable<T> Query(IDbConnection cnn, String sql, dynamic param, IDbTransaction transaction, boolean buffered, Integer commandTimeout, CommandType commandType)
//#endif
	{
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
		var data = QueryInternal<T>(cnn, sql, (Object)((param instanceof Object) ? param : null), transaction, commandTimeout, commandType);
		return buffered ? data.ToList() : data;
	}

	/** 
	 Execute a command that returns multiple result sets, and access each in turn
	 
	*/
//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
//#if CSHARP30
//C# TO JAVA CONVERTER TODO TASK: Extension methods are not available in Java:
//ORIGINAL LINE: public static GridReader QueryMultiple(this IDbConnection cnn, string sql, object param, IDbTransaction transaction, Nullable<int> commandTimeout, Nullable<CommandType> commandType)
	public static GridReader QueryMultiple(IDbConnection cnn, String sql, Object param, IDbTransaction transaction, Integer commandTimeout, CommandType commandType)
//#else
//C# TO JAVA CONVERTER TODO TASK: Extension methods are not available in Java:
//ORIGINAL LINE: public static GridReader QueryMultiple(this IDbConnection cnn, string sql, dynamic param = null, IDbTransaction transaction = null, Nullable<int> commandTimeout = null, Nullable<CommandType> commandType = null)
//C# TO JAVA CONVERTER TODO TASK: C# optional parameters are not converted to Java:
	public static GridReader QueryMultiple(IDbConnection cnn, String sql, dynamic param, IDbTransaction transaction, Integer commandTimeout, CommandType commandType)
//#endif
	{
		Identity identity = new Identity(sql, commandType, cnn, GridReader.class, (Object)param == null ? null : ((Object)param).getClass(), null);
		CacheInfo info = GetCacheInfo(identity);

		IDbCommand cmd = null;
		IDataReader reader = null;
		boolean wasClosed = cnn.State == ConnectionState.Closed;
		try
		{
			if (wasClosed)
			{
				cnn.Open();
			}
			cmd = SetupCommand(cnn, transaction, sql, info.getParamReader(), (Object)param, commandTimeout, commandType);
			reader = cmd.ExecuteReader(wasClosed ? CommandBehavior.CloseConnection : CommandBehavior.Default);

			GridReader result = new GridReader(cmd, reader, identity);
			wasClosed = false; // *if* the connection was closed and we got this far, then we now have a reader
			// with the CloseConnection flag, so the reader will deal with the connection; we
			// still need something in the "finally" to ensure that broken SQL still results
			// in the connection closing itself
			return result;
		}
		catch (java.lang.Exception e)
		{
			if (reader != null)
			{
				if (!reader.IsClosed)
				{
					try
					{
						cmd.Cancel();
					}
					catch (java.lang.Exception e2) // don't spoil the existing exception
					{
 }
				}
				reader.dispose();
			}
			if (cmd != null)
			{
				cmd.dispose();
			}
			if (wasClosed)
			{
				cnn.Close();
			}
			throw e;
		}
	}

	/** 
	 Return a typed list of objects, reader is closed after the call
	 
	*/
//C# TO JAVA CONVERTER TODO TASK: Extension methods are not available in Java:
//ORIGINAL LINE: private static IEnumerable<T> QueryInternal<T>(this IDbConnection cnn, string sql, object param, IDbTransaction transaction, Nullable<int> commandTimeout, Nullable<CommandType> commandType)
	private static <T> Iterable<T> QueryInternal(IDbConnection cnn, String sql, Object param, IDbTransaction transaction, Integer commandTimeout, CommandType commandType)
	{
		Identity identity = new Identity(sql, commandType, cnn, T.class, param == null ? null : param.getClass(), null);
		CacheInfo info = GetCacheInfo(identity);

		IDbCommand cmd = null;
		IDataReader reader = null;

		boolean wasClosed = cnn.State == ConnectionState.Closed;
		try
		{
			cmd = SetupCommand(cnn, transaction, sql, info.getParamReader(), param, commandTimeout, commandType);

			if (wasClosed)
			{
				cnn.Open();
			}
			reader = cmd.ExecuteReader(wasClosed ? CommandBehavior.CloseConnection : CommandBehavior.Default);
			wasClosed = false; // *if* the connection was closed and we got this far, then we now have a reader
			// with the CloseConnection flag, so the reader will deal with the connection; we
			// still need something in the "finally" to ensure that broken SQL still results
			// in the connection closing itself
			DeserializerState tuple = info.getDeserializer().clone();
			int hash = GetColumnHash(reader);
			if (tuple.Func == null || tuple.Hash != hash)
			{
				info.setDeserializer(new DeserializerState(hash, GetDeserializer(T.class, reader, 0, -1, false)));
				tuple = info.getDeserializer().clone();
				SetQueryCache(identity, info);
			}

			Func<IDataReader, Object> func = tuple.Func;

			while (reader.Read())
			{
//C# TO JAVA CONVERTER TODO TASK: Java does not have an equivalent to the C# 'yield' keyword:
				yield return (T)func(reader);
			}
			// happy path; close the reader cleanly - no
			// need for "Cancel" etc
			reader.dispose();
			reader = null;
		}
		finally
		{
			if (reader != null)
			{
				if (!reader.IsClosed)
				{
					try
					{
						cmd.Cancel();
					}
					catch (java.lang.Exception e) // don't spoil the existing exception
					{
 }
				}
				reader.dispose();
			}
			if (wasClosed)
			{
				cnn.Close();
			}
			if (cmd != null)
			{
				cmd.dispose();
			}
		}
	}

	/** 
	 Maps a query to objects
	 
	 <typeparam name="TFirst">The first type in the recordset</typeparam>
	 <typeparam name="TSecond">The second type in the recordset</typeparam>
	 <typeparam name="TReturn">The return type</typeparam>
	 @param cnn
	 @param sql
	 @param map
	 @param param
	 @param transaction
	 @param buffered
	 @param splitOn The Field we should split and read the second object from (default: id)
	 @param commandTimeout Number of seconds before command execution timeout
	 @param commandType Is it a stored proc or a batch?
	 @return 
	*/
//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
//#if CSHARP30
//C# TO JAVA CONVERTER TODO TASK: Extension methods are not available in Java:
//ORIGINAL LINE: public static IEnumerable<TReturn> Query<TFirst, TSecond, TReturn>(this IDbConnection cnn, string sql, Func<TFirst, TSecond, TReturn> map, object param, IDbTransaction transaction, bool buffered, string splitOn, Nullable<int> commandTimeout, Nullable<CommandType> commandType)
	public static <TFirst, TSecond, TReturn> Iterable<TReturn> Query(IDbConnection cnn, String sql, Func<TFirst, TSecond, TReturn> map, Object param, IDbTransaction transaction, boolean buffered, String splitOn, Integer commandTimeout, CommandType commandType)
//#else
//C# TO JAVA CONVERTER TODO TASK: Extension methods are not available in Java:
//ORIGINAL LINE: public static IEnumerable<TReturn> Query<TFirst, TSecond, TReturn>(this IDbConnection cnn, string sql, Func<TFirst, TSecond, TReturn> map, dynamic param = null, IDbTransaction transaction = null, bool buffered = true, string splitOn = "Id", Nullable<int> commandTimeout = null, Nullable<CommandType> commandType = null)
//C# TO JAVA CONVERTER TODO TASK: C# optional parameters are not converted to Java:
	public static <TFirst, TSecond, TReturn> Iterable<TReturn> Query(IDbConnection cnn, String sql, Func<TFirst, TSecond, TReturn> map, dynamic param, IDbTransaction transaction, boolean buffered, String splitOn, Integer commandTimeout, CommandType commandType)
//#endif
	{
		return MultiMap<TFirst, TSecond, DontMap, DontMap, DontMap, TReturn>(cnn, sql, map, (Object)((param instanceof Object) ? param : null), transaction, buffered, splitOn, commandTimeout, commandType);
	}

	/** 
	 Maps a query to objects
	 
	 <typeparam name="TFirst"></typeparam>
	 <typeparam name="TSecond"></typeparam>
	 <typeparam name="TThird"></typeparam>
	 <typeparam name="TReturn"></typeparam>
	 @param cnn
	 @param sql
	 @param map
	 @param param
	 @param transaction
	 @param buffered
	 @param splitOn The Field we should split and read the second object from (default: id)
	 @param commandTimeout Number of seconds before command execution timeout
	 @param commandType
	 @return 
	*/
//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
//#if CSHARP30
//C# TO JAVA CONVERTER TODO TASK: Extension methods are not available in Java:
//ORIGINAL LINE: public static IEnumerable<TReturn> Query<TFirst, TSecond, TThird, TReturn>(this IDbConnection cnn, string sql, Func<TFirst, TSecond, TThird, TReturn> map, object param, IDbTransaction transaction, bool buffered, string splitOn, Nullable<int> commandTimeout, Nullable<CommandType> commandType)
	public static <TFirst, TSecond, TThird, TReturn> Iterable<TReturn> Query(IDbConnection cnn, String sql, Func<TFirst, TSecond, TThird, TReturn> map, Object param, IDbTransaction transaction, boolean buffered, String splitOn, Integer commandTimeout, CommandType commandType)
//#else
//C# TO JAVA CONVERTER TODO TASK: Extension methods are not available in Java:
//ORIGINAL LINE: public static IEnumerable<TReturn> Query<TFirst, TSecond, TThird, TReturn>(this IDbConnection cnn, string sql, Func<TFirst, TSecond, TThird, TReturn> map, dynamic param = null, IDbTransaction transaction = null, bool buffered = true, string splitOn = "Id", Nullable<int> commandTimeout = null, Nullable<CommandType> commandType = null)
//C# TO JAVA CONVERTER TODO TASK: C# optional parameters are not converted to Java:
	public static <TFirst, TSecond, TThird, TReturn> Iterable<TReturn> Query(IDbConnection cnn, String sql, Func<TFirst, TSecond, TThird, TReturn> map, dynamic param, IDbTransaction transaction, boolean buffered, String splitOn, Integer commandTimeout, CommandType commandType)
//#endif
	{
		return MultiMap<TFirst, TSecond, TThird, DontMap, DontMap, TReturn>(cnn, sql, map, (Object)((param instanceof Object) ? param : null), transaction, buffered, splitOn, commandTimeout, commandType);
	}

	/** 
	 Perform a multi mapping query with 4 input parameters
	 
	 <typeparam name="TFirst"></typeparam>
	 <typeparam name="TSecond"></typeparam>
	 <typeparam name="TThird"></typeparam>
	 <typeparam name="TFourth"></typeparam>
	 <typeparam name="TReturn"></typeparam>
	 @param cnn
	 @param sql
	 @param map
	 @param param
	 @param transaction
	 @param buffered
	 @param splitOn
	 @param commandTimeout
	 @param commandType
	 @return 
	*/
//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
//#if CSHARP30
//C# TO JAVA CONVERTER TODO TASK: Extension methods are not available in Java:
//ORIGINAL LINE: public static IEnumerable<TReturn> Query<TFirst, TSecond, TThird, TFourth, TReturn>(this IDbConnection cnn, string sql, Func<TFirst, TSecond, TThird, TFourth, TReturn> map, object param, IDbTransaction transaction, bool buffered, string splitOn, Nullable<int> commandTimeout, Nullable<CommandType> commandType)
	public static <TFirst, TSecond, TThird, TFourth, TReturn> Iterable<TReturn> Query(IDbConnection cnn, String sql, Func<TFirst, TSecond, TThird, TFourth, TReturn> map, Object param, IDbTransaction transaction, boolean buffered, String splitOn, Integer commandTimeout, CommandType commandType)
//#else
//C# TO JAVA CONVERTER TODO TASK: Extension methods are not available in Java:
//ORIGINAL LINE: public static IEnumerable<TReturn> Query<TFirst, TSecond, TThird, TFourth, TReturn>(this IDbConnection cnn, string sql, Func<TFirst, TSecond, TThird, TFourth, TReturn> map, dynamic param = null, IDbTransaction transaction = null, bool buffered = true, string splitOn = "Id", Nullable<int> commandTimeout = null, Nullable<CommandType> commandType = null)
//C# TO JAVA CONVERTER TODO TASK: C# optional parameters are not converted to Java:
	public static <TFirst, TSecond, TThird, TFourth, TReturn> Iterable<TReturn> Query(IDbConnection cnn, String sql, Func<TFirst, TSecond, TThird, TFourth, TReturn> map, dynamic param, IDbTransaction transaction, boolean buffered, String splitOn, Integer commandTimeout, CommandType commandType)
//#endif
	{
		return MultiMap<TFirst, TSecond, TThird, TFourth, DontMap, TReturn>(cnn, sql, map, (Object)((param instanceof Object) ? param : null), transaction, buffered, splitOn, commandTimeout, commandType);
	}
//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
//#if !CSHARP30
	/** 
	 Perform a multi mapping query with 5 input parameters
	 
	 <typeparam name="TFirst"></typeparam>
	 <typeparam name="TSecond"></typeparam>
	 <typeparam name="TThird"></typeparam>
	 <typeparam name="TFourth"></typeparam>
	 <typeparam name="TFifth"></typeparam>
	 <typeparam name="TReturn"></typeparam>
	 @param cnn
	 @param sql
	 @param map
	 @param param
	 @param transaction
	 @param buffered
	 @param splitOn
	 @param commandTimeout
	 @param commandType
	 @return 
	*/
//C# TO JAVA CONVERTER TODO TASK: Extension methods are not available in Java:
//ORIGINAL LINE: public static IEnumerable<TReturn> Query<TFirst, TSecond, TThird, TFourth, TFifth, TReturn>(this IDbConnection cnn, string sql, Func<TFirst, TSecond, TThird, TFourth, TFifth, TReturn> map, dynamic param = null, IDbTransaction transaction = null, bool buffered = true, string splitOn = "Id", Nullable<int> commandTimeout = null, Nullable<CommandType> commandType = null)
//C# TO JAVA CONVERTER TODO TASK: C# optional parameters are not converted to Java:
	public static <TFirst, TSecond, TThird, TFourth, TFifth, TReturn> Iterable<TReturn> Query(IDbConnection cnn, String sql, Func<TFirst, TSecond, TThird, TFourth, TFifth, TReturn> map, dynamic param, IDbTransaction transaction, boolean buffered, String splitOn, Integer commandTimeout, CommandType commandType)
	{
		return MultiMap<TFirst, TSecond, TThird, TFourth, TFifth, TReturn>(cnn, sql, map, (Object)((param instanceof Object) ? param : null), transaction, buffered, splitOn, commandTimeout, commandType);
	}
//#endif
	private static class DontMap
	{
	}
//C# TO JAVA CONVERTER TODO TASK: Extension methods are not available in Java:
//ORIGINAL LINE: static IEnumerable<TReturn> MultiMap<TFirst, TSecond, TThird, TFourth, TFifth, TReturn>(this IDbConnection cnn, string sql, object map, object param, IDbTransaction transaction, bool buffered, string splitOn, Nullable<int> commandTimeout, Nullable<CommandType> commandType)
	private static <TFirst, TSecond, TThird, TFourth, TFifth, TReturn> Iterable<TReturn> MultiMap(IDbConnection cnn, String sql, Object map, Object param, IDbTransaction transaction, boolean buffered, String splitOn, Integer commandTimeout, CommandType commandType)
	{
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
		var results = MultiMapImpl<TFirst, TSecond, TThird, TFourth, TFifth, TReturn>(cnn, sql, map, param, transaction, splitOn, commandTimeout, commandType, null, null);
		return buffered ? results.ToList() : results;
	}


//C# TO JAVA CONVERTER TODO TASK: Extension methods are not available in Java:
//ORIGINAL LINE: static IEnumerable<TReturn> MultiMapImpl<TFirst, TSecond, TThird, TFourth, TFifth, TReturn>(this IDbConnection cnn, string sql, object map, object param, IDbTransaction transaction, string splitOn, Nullable<int> commandTimeout, Nullable<CommandType> commandType, IDataReader reader, Identity identity)
	private static <TFirst, TSecond, TThird, TFourth, TFifth, TReturn> Iterable<TReturn> MultiMapImpl(IDbConnection cnn, String sql, Object map, Object param, IDbTransaction transaction, String splitOn, Integer commandTimeout, CommandType commandType, IDataReader reader, Identity identity)
	{
		identity = (identity != null) ? identity : new Identity(sql, commandType, cnn, TFirst.class, (Object)param == null ? null : ((Object)param).getClass(), new Object[] { TFirst.class, TSecond.class, TThird.class, TFourth.class, TFifth.class });
		CacheInfo cinfo = GetCacheInfo(identity);

		IDbCommand ownedCommand = null;
		IDataReader ownedReader = null;

		try
		{
			if (reader == null)
			{
				ownedCommand = SetupCommand(cnn, transaction, sql, cinfo.getParamReader(), (Object)param, commandTimeout, commandType);
				ownedReader = ownedCommand.ExecuteReader();
				reader = ownedReader;
			}
			DeserializerState deserializer = new DeserializerState();
			Func<IDataReader, Object>[] otherDeserializers = null;

			int hash = GetColumnHash(reader);
			if ((deserializer = cinfo.getDeserializer()).Func == null || (otherDeserializers = cinfo.getOtherDeserializers()) == null || hash != deserializer.Hash)
			{
				Func<IDataReader, Object>[] deserializers = GenerateDeserializers(new java.lang.Class[] { TFirst.class, TSecond.class, TThird.class, TFourth.class, TFifth.class }, splitOn, reader);
				cinfo.setDeserializer(new DeserializerState(hash, deserializers[0]));
				deserializer = cinfo.getDeserializer().clone();
				cinfo.setOtherDeserializers(deserializers.Skip(1).toArray());
				otherDeserializers = cinfo.getOtherDeserializers();
				SetQueryCache(identity, cinfo);
			}

			Func<IDataReader, TReturn> mapIt = GenerateMapper<TFirst, TSecond, TThird, TFourth, TFifth, TReturn>(deserializer.Func, otherDeserializers, map);

			if (mapIt != null)
			{
				while (reader.Read())
				{
//C# TO JAVA CONVERTER TODO TASK: Java does not have an equivalent to the C# 'yield' keyword:
					yield return mapIt(reader);
				}
			}
		}
		finally
		{
			try
			{
				if (ownedReader != null)
				{
					ownedReader.dispose();
				}
			}
			finally
			{
				if (ownedCommand != null)
				{
					ownedCommand.dispose();
				}
			}
		}
	}

	private static <TFirst, TSecond, TThird, TFourth, TFifth, TReturn> Func<IDataReader, TReturn> GenerateMapper(Func<IDataReader, Object> deserializer, Func<IDataReader, Object>[] otherDeserializers, Object map)
	{
		switch (otherDeserializers.length)
		{
			case 1:
//C# TO JAVA CONVERTER TODO TASK: Lambda expressions and anonymous methods are not converted by C# to Java Converter:
				return r => ((Func<TFirst, TSecond, TReturn>)map)((TFirst)deserializer(r), (TSecond)otherDeserializers[0](r));
			case 2:
//C# TO JAVA CONVERTER TODO TASK: Lambda expressions and anonymous methods are not converted by C# to Java Converter:
				return r => ((Func<TFirst, TSecond, TThird, TReturn>)map)((TFirst)deserializer(r), (TSecond)otherDeserializers[0](r), (TThird)otherDeserializers[1](r));
			case 3:
//C# TO JAVA CONVERTER TODO TASK: Lambda expressions and anonymous methods are not converted by C# to Java Converter:
				return r => ((Func<TFirst, TSecond, TThird, TFourth, TReturn>)map)((TFirst)deserializer(r), (TSecond)otherDeserializers[0](r), (TThird)otherDeserializers[1](r), (TFourth)otherDeserializers[2](r));
//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
//#if !CSHARP30
			case 4:
//C# TO JAVA CONVERTER TODO TASK: Lambda expressions and anonymous methods are not converted by C# to Java Converter:
				return r => ((Func<TFirst, TSecond, TThird, TFourth, TFifth, TReturn>)map)((TFirst)deserializer(r), (TSecond)otherDeserializers[0](r), (TThird)otherDeserializers[1](r), (TFourth)otherDeserializers[2](r), (TFifth)otherDeserializers[3](r));
//#endif
			default:
				throw new NotSupportedException();
		}
	}

	private static Func<IDataReader, Object>[] GenerateDeserializers(java.lang.Class[] types, String splitOn, IDataReader reader)
	{
		int current = 0;
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
		var splits = splitOn.split("[,]", -1).toArray();
		int splitIndex = 0;

//C# TO JAVA CONVERTER TODO TASK: Lambda expressions and anonymous methods are not converted by C# to Java Converter:
		Func<java.lang.Class, Integer> nextSplit = type =>
		{
			String currentSplit = splits[splitIndex].trim();
			if (splits.getLength() > splitIndex + 1)
			{
				splitIndex++;
			}

			boolean skipFirst = false;
			int startingPos = current + 1;
			// if our current type has the split, skip the first time you see it. 
			if (type != Object.class)
			{
				java.util.ArrayList<PropertyInfo> props = DefaultTypeMap.GetSettableProps(type);
				java.util.ArrayList<java.lang.reflect.Field> fields = DefaultTypeMap.GetSettableFields(type);

//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
//C# TO JAVA CONVERTER TODO TASK: Lambda expressions and anonymous methods are not converted by C# to Java Converter:
				for (var name : props.Select(p => p.getName()).Concat(fields.Select(f => f.getName())))
				{
					if (String.equals(name, currentSplit, StringComparison.OrdinalIgnoreCase))
					{
						skipFirst = true;
						startingPos = current;
						break;
					}
				}

			}

			int pos;
			for (pos = startingPos; pos < reader.FieldCount; pos++)
			{
				// some people like ID some id ... assuming case insensitive splits for now
				if (splitOn.equals("*"))
				{
					break;
				}
				if (String.equals(reader.GetName(pos), currentSplit, StringComparison.OrdinalIgnoreCase))
				{
					if (skipFirst)
					{
						skipFirst = false;
					}
					else
					{
						break;
					}
				}
			}
			current = pos;
			return pos;
		}

		java.util.ArrayList<Func<IDataReader, Object>> deserializers = new java.util.ArrayList<Func<IDataReader, Object>>();
		int split = 0;
		boolean first = true;
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
		for (var type : types)
		{
			if (type != DontMap.class)
			{
				int next = nextSplit(type);
				deserializers.add(GetDeserializer(type, reader, split, next - split, !first)); // returnNullIfFirstMissing: 
				first = false;
				split = next;
			}
		}

		return deserializers.toArray(new Func<IDataReader, Object[]{});
	}

	private static CacheInfo GetCacheInfo(Identity identity)
	{
		CacheInfo info = null;
		RefObject<CacheInfo> tempRef_info = new RefObject<CacheInfo>(info);
		boolean tempVar = !TryGetQueryCache(identity, tempRef_info);
			info = tempRef_info.argvalue;
		if (tempVar)
		{
			info = new CacheInfo();
			if (identity.parametersType != null)
			{
				if (IDynamicParameters.class.IsAssignableFrom(identity.parametersType))
				{
//C# TO JAVA CONVERTER TODO TASK: Lambda expressions and anonymous methods are not converted by C# to Java Converter:
					info.setParamReader((cmd, obj) =>)
					{
						(IDynamicParameters)((obj instanceof IDynamicParameters) ? obj : null).AddParameters(cmd, identity);
					}
				}
//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
//#if !CSHARP30
				else if (Iterable<java.util.Map.Entry<String, Object>>.class.IsAssignableFrom(identity.parametersType) && System.Dynamic.IDynamicMetaObjectProvider.class.IsAssignableFrom(identity.parametersType))
				{
//C# TO JAVA CONVERTER TODO TASK: Lambda expressions and anonymous methods are not converted by C# to Java Converter:
					info.setParamReader((cmd, obj) =>)
					{
						IDynamicParameters mapped = new DynamicParameters(obj);
						mapped.AddParameters(cmd, identity);
					}
				}
//#endif
				else
				{
					info.setParamReader(CreateParamInfoGenerator(identity, false));
				}
			}
			SetQueryCache(identity, info);
		}
		return info;
	}

	private static Func<IDataReader, Object> GetDeserializer(java.lang.Class type, IDataReader reader, int startBound, int length, boolean returnNullIfFirstMissing)
	{
//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
//#if !CSHARP30
		// dynamic is passed in as Object ... by c# design
		if (type == Object.class || type == DapperRow.class)
		{
			return GetDapperRowDeserializer(reader, startBound, length, returnNullIfFirstMissing);
		}
//#else
		if(type.IsAssignableFrom(java.util.HashMap<String,Object>.class))
		{
			return GetDictionaryDeserializer(reader, startBound, length, returnNullIfFirstMissing);
		}
//#endif
		java.lang.Class underlyingType = null;
		if (!(typeMap.containsKey(type) || type.IsEnum || LinqBinary.equals(type.FullName) || (type.IsValueType && (underlyingType = Nullable.GetUnderlyingType(type)) != null && underlyingType.IsEnum)))
		{
			return GetTypeDeserializer(type, reader, startBound, length, returnNullIfFirstMissing);
		}
		return GetStructDeserializer(type, (underlyingType != null) ? underlyingType : type, startBound);

	}

//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
//#if !CSHARP30
	private final static class DapperTable
	{
		private String[] fieldNames;
		private java.util.HashMap<String, Integer> fieldNameLookup;

		public String[] getFieldNames()
		{
			return fieldNames;
		}

		public DapperTable(String[] fieldNames)
		{
			if (fieldNames == null)
			{
				throw new ArgumentNullException("fieldNames");
			}
			this.fieldNames = fieldNames;

			fieldNameLookup = new java.util.HashMap<String, Integer>(fieldNames.length, StringComparer.Ordinal);
			// if there are dups, we want the **first** key to be the "winner" - so iterate backwards
			for (int i = fieldNames.length - 1; i >= 0; i--)
			{
				String key = fieldNames[i];
				if (key != null)
				{
					fieldNameLookup.put(key, i);
				}
			}
		}

		public int IndexOfName(String name)
		{
			int result = 0;
			return (name != null && (result = fieldNameLookup.get(name)) != null) ? result : -1;
		}
		public int AddField(String name)
		{
			if (name == null)
			{
				throw new ArgumentNullException("name");
			}
			if (fieldNameLookup.containsKey(name))
			{
				throw new InvalidOperationException("Field already exists: " + name);
			}
			int oldLen = fieldNames.length;
			RefObject<String> tempRef_fieldNames = new RefObject<String>(fieldNames);
			Array.Resize(tempRef_fieldNames, oldLen + 1); // yes, this is sub-optimal, but this is not the expected common case
			fieldNames = tempRef_fieldNames.argvalue;
			fieldNameLookup.put(name, oldLen);
			return oldLen;
		}


		public boolean FieldExists(String key)
		{
			return key != null && fieldNameLookup.containsKey(key);
		}

		public int getFieldCount()
		{
			return fieldNames.length;
		}
	}

	private final static class DapperRowMetaObject extends System.Dynamic.DynamicMetaObject
	{
		private static final java.lang.reflect.Method getValueMethod = java.util.Map<String, Object>.class.GetProperty("Item").GetGetMethod();
		private static final java.lang.reflect.Method setValueMethod = DapperRow.class.GetMethod("SetValue");

		public DapperRowMetaObject(System.Linq.Expressions.Expression expression, System.Dynamic.BindingRestrictions restrictions)
		{
			super(expression, restrictions);
		}

		public DapperRowMetaObject(System.Linq.Expressions.Expression expression, System.Dynamic.BindingRestrictions restrictions, Object value)
		{
			super(expression, restrictions, value);
		}

		private System.Dynamic.DynamicMetaObject CallMethod(java.lang.reflect.Method method, System.Linq.Expressions.Expression[] parameters)
		{
			System.Dynamic.DynamicMetaObject callMethod = new System.Dynamic.DynamicMetaObject(System.Linq.Expressions.Expression.Call(System.Linq.Expressions.Expression.Convert(Expression, LimitType), method, parameters), System.Dynamic.BindingRestrictions.GetTypeRestriction(Expression, LimitType));
			return callMethod;
		}

		@Override
		public System.Dynamic.DynamicMetaObject BindGetMember(System.Dynamic.GetMemberBinder binder)
		{
			System.Linq.Expressions.Expression[] parameters = new System.Linq.Expressions.Expression[] { System.Linq.Expressions.Expression.Constant(binder.getName()) };

			System.Dynamic.DynamicMetaObject callMethod = CallMethod(getValueMethod, parameters);

			return callMethod;
		}

		@Override
		public System.Dynamic.DynamicMetaObject BindSetMember(System.Dynamic.SetMemberBinder binder, System.Dynamic.DynamicMetaObject value)
		{
			System.Linq.Expressions.Expression[] parameters = new System.Linq.Expressions.Expression[] { System.Linq.Expressions.Expression.Constant(binder.getName()), value.Expression };

			System.Dynamic.DynamicMetaObject callMethod = CallMethod(setValueMethod, parameters);

			return callMethod;
		}
	}

	private final static class DapperRow implements System.Dynamic.IDynamicMetaObjectProvider, java.util.Map<String, Object>
	{
		private DapperTable table;
		private Object[] values;

		public DapperRow(DapperTable table, Object[] values)
		{
			if (table == null)
			{
				throw new ArgumentNullException("table");
			}
			if (values == null)
			{
				throw new ArgumentNullException("values");
			}
			getthis().table = table;
			getthis().values = values;
		}
		private final static class DeadValue
		{
			public static final DeadValue Default = new DeadValue();
			private DeadValue()
			{
			}
		}
		private int getCount()
		{
			int count = 0;
			for (int i = 0; i < values.length; i++)
			{
				if (!(values[i] instanceof DeadValue))
				{
					count++;
				}
			}
			return count;
		}

		public final boolean TryGetValue(String name, RefObject<Object> value)
		{
			int index = table.IndexOfName(name);
			if (index < 0)
			{ // doesn't exist
				value.argvalue = null;
				return false;
			}
			// exists, **even if** we don't have a value; consider table rows heterogeneous
			value.argvalue = index < values.length ? values[index] : null;
			if (value.argvalue instanceof DeadValue)
			{ // pretend it isn't here
				value.argvalue = null;
				return false;
			}
			return true;
		}

		@Override
		public String toString()
		{
			StringBuilder sb = new StringBuilder("{DapperRow");
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
			for (var kv : getthis())
			{
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
				var value = kv.getValue();
				sb.append(", ").Append(kv.getKey());
				if (value != null)
				{
					sb.append(" = '").Append(kv.getValue()).Append('\'');
				}
				else
				{
					sb.append(" = NULL");
				}
			}

			return sb.append('}').toString();
		}

		private System.Dynamic.DynamicMetaObject GetMetaObject(System.Linq.Expressions.Expression parameter)
		{
			return new DapperRowMetaObject(parameter, System.Dynamic.BindingRestrictions.Empty, getthis());
		}

		public final java.util.Iterator<java.util.Map.Entry<String, Object>> GetEnumerator()
		{
			String[] names = table.getFieldNames();
			for (var i = 0; i < names.length; i++)
			{
				Object value = i < values.length ? values[i] : null;
				if (!(value instanceof DeadValue))
				{
//C# TO JAVA CONVERTER TODO TASK: Java does not have an equivalent to the C# 'yield' keyword:
					yield return new KeyValuePair<String, Object>(names[i], value);
				}
			}
		}

		private java.util.Iterator GetEnumerator()
		{
			return GetEnumerator();
		}

//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
			///#region Implementation of ICollection<KeyValuePair<string,object>>

		private void Add(java.util.Map.Entry<String, Object> item)
		{
			java.util.Map<String, Object> dic = getthis();
			dic.put(item.getKey(), item.getValue());
		}

		private void Clear()
		{ // removes values for **this row**, but doesn't change the fundamental table
			for (int i = 0; i < values.length; i++)
			{
				values[i] = DeadValue.Default;
			}
		}

		private boolean Contains(java.util.Map.Entry<String, Object> item)
		{
			Object value = null;
			RefObject<Object> tempRef_value = new RefObject<Object>(value);
			boolean tempVar = TryGetValue(item.getKey(), tempRef_value) && equals(value, item.getValue());
			value = tempRef_value.argvalue;
			return tempVar;
		}

		private void CopyTo(java.util.Map.Entry<String, Object>[] array, int arrayIndex)
		{
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
			for (var kv : getthis())
			{
				array[arrayIndex++] = kv; // if they didn't leave enough space; not our fault
			}
		}

		private boolean Remove(java.util.Map.Entry<String, Object> item)
		{
			java.util.Map<String, Object> dic = getthis();
			return dic.remove(item.getKey());
		}

		private boolean getIsReadOnly()
		{
			return false;
		}

//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
			///#endregion

//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
			///#region Implementation of IDictionary<string,object>

		private boolean ContainsKey(String key)
		{
			int index = table.IndexOfName(key);
			if (index < 0 || index >= values.length || values[index] instanceof DeadValue)
			{
				return false;
			}
			return true;
		}

		private void Add(String key, Object value)
		{
			java.util.Map<String, Object> dic = getthis();
			dic.put(key, value);
		}

		private boolean Remove(String key)
		{
			int index = table.IndexOfName(key);
			if (index < 0 || index >= values.length || values[index] instanceof DeadValue)
			{
				return false;
			}
			values[index] = DeadValue.Default;
			return true;
		}

		private Object getItem(String key)
		{
			Object val = null;
			RefObject<Object> tempRef_val = new RefObject<Object>(val);
			TryGetValue(key, tempRef_val);
			val = tempRef_val.argvalue;
			return val;
		}
		private void setItem(String key, Object value)
		{
			SetValue(key, value);
		}
		public final Object SetValue(String key, Object value)
		{
			if (key == null)
			{
				throw new ArgumentNullException("key");
			}
			int index = table.IndexOfName(key);
			if (index < 0)
			{
				index = table.AddField(key);
			}
			if (values.length <= index)
			{ // we'll assume they're doing lots of things, and
				// grow it to the full width of the table
				RefObject<Object> tempRef_values = new RefObject<Object>(values);
				Array.Resize(tempRef_values, table.getFieldCount());
				values = tempRef_values.argvalue;
			}
			return values[index] = value;
		}

		private java.util.Collection<String> getKeys()
		{
//C# TO JAVA CONVERTER TODO TASK: Lambda expressions and anonymous methods are not converted by C# to Java Converter:
			return getthis().Select(kv => kv.getKey()).toArray();
		}

		private java.util.Collection<Object> getValues()
		{
//C# TO JAVA CONVERTER TODO TASK: Lambda expressions and anonymous methods are not converted by C# to Java Converter:
			return getthis().Select(kv => kv.getValue()).toArray();
		}

//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
			///#endregion
	}
//#endif

//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
//#if !CSHARP30
	public static Func<IDataReader, Object> GetDapperRowDeserializer(IDataRecord reader, int startBound, int length, boolean returnNullIfFirstMissing)
	{
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
		var fieldCount = reader.FieldCount;
		if (length == -1)
		{
			length = fieldCount - startBound;
		}

		if (fieldCount <= startBound)
		{
			throw new IllegalArgumentException("When using the multi-mapping APIs ensure you set the splitOn param if you have keys other than Id", "splitOn");
		}

//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
		var effectiveFieldCount = fieldCount - startBound;

		DapperTable table = null;

//C# TO JAVA CONVERTER TODO TASK: Lambda expressions and anonymous methods are not converted by C# to Java Converter:
		return r =>
			{
				if (table == null)
				{
					String[] names = new String[effectiveFieldCount];
					for (int i = 0; i < effectiveFieldCount; i++)
					{
						names[i] = r.GetName(i + startBound);
					}
					table = new DapperTable(names);
				}

				Object[] values = new Object[effectiveFieldCount];

				if (returnNullIfFirstMissing)
				{
					values[0] = r.GetValue(startBound);
					if (values[0] instanceof DBNull)
					{
						return null;
					}
				}

				if (startBound == 0)
				{
					r.GetValues(values);
				}
				else
				{
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
					var begin = returnNullIfFirstMissing ? 1 : 0;
					for (var iter = begin; iter < effectiveFieldCount; ++iter)
					{
						values[iter] = r.GetValue(iter + startBound);
					}
				}
				return new DapperRow(table, values);
			}
	}
//#else
	public static Func<IDataReader, Object> GetDictionaryDeserializer(IDataRecord reader, int startBound, int length, boolean returnNullIfFirstMissing)
	{
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
		var fieldCount = reader.FieldCount;
		if (length == -1)
		{
			length = fieldCount - startBound;
		}

		if (fieldCount <= startBound)
		{
			throw new IllegalArgumentException("When using the multi-mapping APIs ensure you set the splitOn param if you have keys other than Id", "splitOn");
		}

//C# TO JAVA CONVERTER TODO TASK: Lambda expressions and anonymous methods are not converted by C# to Java Converter:
		return r =>
			 {
				 java.util.Map<String, Object> row = new java.util.HashMap<String, Object>(length);
				 for (var i = startBound; i < startBound + length; i++)
				 {
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
					 var tmp = r.GetValue(i);
					 tmp = tmp == DBNull.getValue() ? null : tmp;
					 row.put(r.GetName(i), tmp);
					 if (returnNullIfFirstMissing && i == startBound && tmp == null)
					 {
						 return null;
					 }
				 }
				 return row;
			 }
	}
//#endif
	/** 
	 Internal use only
	 
	 @param value
	 @return 
	*/
//C# TO JAVA CONVERTER TODO TASK: Java annotations will not correspond to .NET attributes:
	//[Browsable(false), EditorBrowsable(EditorBrowsableState.Never)]
	@Deprecated
	public static char ReadChar(Object value)
	{
		if (value == null || value instanceof DBNull)
		{
			throw new ArgumentNullException("value");
		}
		String s = (String)((value instanceof String) ? value : null);
		if (s == null || s.length() != 1)
		{
			throw new IllegalArgumentException("A single-character was expected", "value");
		}
		return s.charAt(0);
	}

	/** 
	 Internal use only
	 
	*/
//C# TO JAVA CONVERTER TODO TASK: Java annotations will not correspond to .NET attributes:
	//[Browsable(false), EditorBrowsable(EditorBrowsableState.Never)]
	@Deprecated
	public static Character ReadNullableChar(Object value)
	{
		if (value == null || value instanceof DBNull)
		{
			return null;
		}
		String s = (String)((value instanceof String) ? value : null);
		if (s == null || s.length() != 1)
		{
			throw new IllegalArgumentException("A single-character was expected", "value");
		}
		return s.charAt(0);
	}


	/** 
	 Internal use only
	 
	*/
//C# TO JAVA CONVERTER TODO TASK: Java annotations will not correspond to .NET attributes:
	//[Browsable(false), EditorBrowsable(EditorBrowsableState.Never)]
	@Deprecated
	public static IDbDataParameter FindOrAddParameter(IDataParameterCollection parameters, IDbCommand command, String name)
	{
		IDbDataParameter result;
		if (parameters.Contains(name))
		{
			result = (IDbDataParameter)parameters[name];
		}
		else
		{
			result = command.CreateParameter();
			result.ParameterName = name;
			parameters.Add(result);
		}
		return result;
	}

	/** 
	 Internal use only
	 
	*/
//C# TO JAVA CONVERTER TODO TASK: Java annotations will not correspond to .NET attributes:
	//[Browsable(false), EditorBrowsable(EditorBrowsableState.Never)]
	@Deprecated
	public static void PackListParameters(IDbCommand command, String namePrefix, Object value)
	{
		// initially we tried TVP, however it performs quite poorly.
		// keep in mind SQL support up to 2000 params easily in sp_executesql, needing more is rare

//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
		var list = (Iterable)((value instanceof Iterable) ? value : null);
		int count = 0;

		if (list != null)
		{
			if (FeatureSupport.Get(command.Connection).getArrays())
			{
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
				var arrayParm = command.CreateParameter();
				arrayParm.setValue(list);
				arrayParm.ParameterName = namePrefix;
				command.Parameters.Add(arrayParm);
			}
			else
			{
				boolean isString = value instanceof Iterable<String>;
				boolean isDbString = value instanceof Iterable<DbString>;
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
				for (var item : list)
				{
					count++;
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
					var listParam = command.CreateParameter();
					listParam.ParameterName = namePrefix + count;
					listParam.setValue((item != null) ? item : DBNull.getValue());
					if (isString)
					{
						listParam.Size = 4000;
						if (item != null && ((String)item).length() > 4000)
						{
							listParam.Size = -1;
						}
					}
					if (isDbString && item instanceof DbString)
					{
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
						var str = (DbString)((item instanceof DbString) ? item : null);
						str.AddParameter(command, listParam.ParameterName);
					}
					else
					{
						command.Parameters.Add(listParam);
					}
				}

				if (count == 0)
				{
					command.CommandText = Regex.Replace(command.CommandText, "[?@:]" + Regex.Escape(namePrefix), "(SELECT NULL WHERE 1 = 0)");
				}
				else
				{
//C# TO JAVA CONVERTER TODO TASK: Lambda expressions and anonymous methods are not converted by C# to Java Converter:
					command.CommandText = Regex.Replace(command.CommandText, "[?@:]" + Regex.Escape(namePrefix), match =>
					{
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
						var grp = match.getValue();
						StringBuilder sb = new StringBuilder("(").Append(grp).Append(1);
						for (int i = 2; i <= count; i++)
						{
							sb.append(',').Append(grp).Append(i);
						}
						return sb.append(')').toString();
					}
				   );
				}
			}
		}

	}

	private static Iterable<PropertyInfo> FilterParameters(Iterable<PropertyInfo> parameters, String sql)
	{
//C# TO JAVA CONVERTER TODO TASK: There is no Java equivalent to LINQ queries:
//C# TO JAVA CONVERTER TODO TASK: Lambda expressions and anonymous methods are not converted by C# to Java Converter:
		return parameters.Where(p => Regex.IsMatch(sql, "[@:]" + p.getName() + "([^a-zA-Z0-9_]+|$)", RegexOptions.IgnoreCase | RegexOptions.Multiline));
	}

	/** 
	 Internal use only
	 
	*/
	public static Action<IDbCommand, Object> CreateParamInfoGenerator(Identity identity, boolean checkForDuplicates)
	{
		java.lang.Class type = identity.parametersType;
		boolean filterParams = (identity.commandType == null ? CommandType.getText() : identity.commandType) == CommandType.getText();
		DynamicMethod dm = new DynamicMethod(String.format("ParamInfo%1$s", Guid.NewGuid()), null, new Object[] { IDbCommand.class, Object.class }, type, true);

//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
		var il = dm.GetILGenerator();

		il.DeclareLocal(type); // 0
		boolean haveInt32Arg1 = false;
		il.Emit(OpCodes.Ldarg_1); // stack is now [untyped-param]
		il.Emit(OpCodes.Unbox_Any, type); // stack is now [typed-param]
		il.Emit(OpCodes.Stloc_0); // stack is now empty

		il.Emit(OpCodes.Ldarg_0); // stack is now [command]
		il.EmitCall(OpCodes.Callvirt, IDbCommand.class.GetProperty("Parameters").GetGetMethod(), null); // stack is now [parameters]

//C# TO JAVA CONVERTER TODO TASK: There is no Java equivalent to LINQ queries:
//C# TO JAVA CONVERTER TODO TASK: Lambda expressions and anonymous methods are not converted by C# to Java Converter:
		Iterable<PropertyInfo> props = type.GetProperties().Where(p => p.GetIndexParameters().getLength() == 0).OrderBy(p => p.getName());
		if (filterParams)
		{
			props = FilterParameters(props, identity.sql);
		}
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
		for (var prop : props)
		{
			if (filterParams)
			{
				if (identity.sql.indexOf("@" + prop.getName(), StringComparison.InvariantCultureIgnoreCase) < 0 && identity.sql.indexOf(":" + prop.getName(), StringComparison.InvariantCultureIgnoreCase) < 0)
				{ // can't see the parameter in the text (even in a comment, etc) - burn it with fire
					continue;
				}
			}
			if (prop.PropertyType == DbString.class)
			{
				il.Emit(OpCodes.Ldloc_0); // stack is now [parameters] [typed-param]
				il.Emit(OpCodes.Callvirt, prop.GetGetMethod()); // stack is [parameters] [dbstring]
				il.Emit(OpCodes.Ldarg_0); // stack is now [parameters] [dbstring] [command]
				il.Emit(OpCodes.Ldstr, prop.getName()); // stack is now [parameters] [dbstring] [command] [name]
				il.EmitCall(OpCodes.Callvirt, DbString.class.GetMethod("AddParameter"), null); // stack is now [parameters]
				continue;
			}
			DbType dbType = LookupDbType(prop.PropertyType, prop.getName());
			if (dbType == DynamicParameters.EnumerableMultiParameter)
			{
				// this actually represents special handling for list types;
				il.Emit(OpCodes.Ldarg_0); // stack is now [parameters] [command]
				il.Emit(OpCodes.Ldstr, prop.getName()); // stack is now [parameters] [command] [name]
				il.Emit(OpCodes.Ldloc_0); // stack is now [parameters] [command] [name] [typed-param]
				il.Emit(OpCodes.Callvirt, prop.GetGetMethod()); // stack is [parameters] [command] [name] [typed-value]
				if (prop.PropertyType.IsValueType)
				{
					il.Emit(OpCodes.Box, prop.PropertyType); // stack is [parameters] [command] [name] [boxed-value]
				}
				il.EmitCall(OpCodes.Call, SqlMapper.class.GetMethod("PackListParameters"), null); // stack is [parameters]
				continue;
			}
			il.Emit(OpCodes.Dup); // stack is now [parameters] [parameters]

			il.Emit(OpCodes.Ldarg_0); // stack is now [parameters] [parameters] [command]

			if (checkForDuplicates)
			{
				// need to be a little careful about adding; use a utility method
				il.Emit(OpCodes.Ldstr, prop.getName()); // stack is now [parameters] [parameters] [command] [name]
				il.EmitCall(OpCodes.Call, SqlMapper.class.GetMethod("FindOrAddParameter"), null); // stack is [parameters] [parameter]
			}
			else
			{
				// no risk of duplicates; just blindly add
				il.EmitCall(OpCodes.Callvirt, IDbCommand.class.GetMethod("CreateParameter"), null); // stack is now [parameters] [parameters] [parameter]

				il.Emit(OpCodes.Dup); // stack is now [parameters] [parameters] [parameter] [parameter]
				il.Emit(OpCodes.Ldstr, prop.getName()); // stack is now [parameters] [parameters] [parameter] [parameter] [name]
				il.EmitCall(OpCodes.Callvirt, IDataParameter.class.GetProperty("ParameterName").GetSetMethod(), null); // stack is now [parameters] [parameters] [parameter]
			}
			if (dbType != DbType.Time) // https://connect.microsoft.com/VisualStudio/feedback/details/381934/sqlparameter-dbtype-dbtype-time-sets-the-parameter-to-sqldbtype-datetime-instead-of-sqldbtype-time
			{
				il.Emit(OpCodes.Dup); // stack is now [parameters] [[parameters]] [parameter] [parameter]
				EmitInt32(il, (int)dbType); // stack is now [parameters] [[parameters]] [parameter] [parameter] [db-type]

				il.EmitCall(OpCodes.Callvirt, IDataParameter.class.GetProperty("DbType").GetSetMethod(), null); // stack is now [parameters] [[parameters]] [parameter]
			}

			il.Emit(OpCodes.Dup); // stack is now [parameters] [[parameters]] [parameter] [parameter]
			EmitInt32(il, (int)ParameterDirection.Input); // stack is now [parameters] [[parameters]] [parameter] [parameter] [dir]
			il.EmitCall(OpCodes.Callvirt, IDataParameter.class.GetProperty("Direction").GetSetMethod(), null); // stack is now [parameters] [[parameters]] [parameter]

			il.Emit(OpCodes.Dup); // stack is now [parameters] [[parameters]] [parameter] [parameter]
			il.Emit(OpCodes.Ldloc_0); // stack is now [parameters] [[parameters]] [parameter] [parameter] [typed-param]
			il.Emit(OpCodes.Callvirt, prop.GetGetMethod()); // stack is [parameters] [[parameters]] [parameter] [parameter] [typed-value]
			boolean checkForNull = true;
			if (prop.PropertyType.IsValueType)
			{
				il.Emit(OpCodes.Box, prop.PropertyType); // stack is [parameters] [[parameters]] [parameter] [parameter] [boxed-value]
				if (Nullable.GetUnderlyingType(prop.PropertyType) == null)
				{ // struct but not Nullable<T>; boxed value cannot be null
					checkForNull = false;
				}
			}
			if (checkForNull)
			{
				if (dbType == DbType.String && !haveInt32Arg1)
				{
					il.DeclareLocal(Integer.class);
					haveInt32Arg1 = true;
				}
				// relative stack: [boxed value]
				il.Emit(OpCodes.Dup); // relative stack: [boxed value] [boxed value]
				Label notNull = il.DefineLabel();
				Label allDone = dbType == DbType.String ? il.DefineLabel() : (Label)null;
				il.Emit(OpCodes.Brtrue_S, notNull);
				// relative stack [boxed value = null]
				il.Emit(OpCodes.Pop); // relative stack empty
				il.Emit(OpCodes.Ldsfld, DBNull.class.GetField("Value")); // relative stack [DBNull]
				if (dbType == DbType.String)
				{
					EmitInt32(il, 0);
					il.Emit(OpCodes.Stloc_1);
				}
				if (allDone != null)
				{
					il.Emit(OpCodes.Br_S, allDone.getValue());
				}
				il.MarkLabel(notNull);
				if (prop.PropertyType == String.class)
				{
					il.Emit(OpCodes.Dup); // [string] [string]
					il.EmitCall(OpCodes.Callvirt, String.class.GetProperty("Length").GetGetMethod(), null); // [string] [length]
					EmitInt32(il, 4000); // [string] [length] [4000]
					il.Emit(OpCodes.Cgt); // [string] [0 or 1]
					Label isLong = il.DefineLabel(), lenDone = il.DefineLabel();
					il.Emit(OpCodes.Brtrue_S, isLong);
					EmitInt32(il, 4000); // [string] [4000]
					il.Emit(OpCodes.Br_S, lenDone);
					il.MarkLabel(isLong);
					EmitInt32(il, -1); // [string] [-1]
					il.MarkLabel(lenDone);
					il.Emit(OpCodes.Stloc_1); // [string]
				}
				if (LinqBinary.equals(prop.PropertyType.FullName))
				{
					il.EmitCall(OpCodes.Callvirt, prop.PropertyType.GetMethod("ToArray", BindingFlags.Public | BindingFlags.Instance), null);
				}
				if (allDone != null)
				{
					il.MarkLabel(allDone.getValue());
				}
				// relative stack [boxed value or DBNull]
			}
			il.EmitCall(OpCodes.Callvirt, IDataParameter.class.GetProperty("Value").GetSetMethod(), null); // stack is now [parameters] [[parameters]] [parameter]

			if (prop.PropertyType == String.class)
			{
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
				var endOfSize = il.DefineLabel();
				// don't set if 0
				il.Emit(OpCodes.Ldloc_1); // [parameters] [[parameters]] [parameter] [size]
				il.Emit(OpCodes.Brfalse_S, endOfSize); // [parameters] [[parameters]] [parameter]

				il.Emit(OpCodes.Dup); // stack is now [parameters] [[parameters]] [parameter] [parameter]
				il.Emit(OpCodes.Ldloc_1); // stack is now [parameters] [[parameters]] [parameter] [parameter] [size]
				il.EmitCall(OpCodes.Callvirt, IDbDataParameter.class.GetProperty("Size").GetSetMethod(), null); // stack is now [parameters] [[parameters]] [parameter]

				il.MarkLabel(endOfSize);
			}
			if (checkForDuplicates)
			{
				// stack is now [parameters] [parameter]
				il.Emit(OpCodes.Pop); // don't need parameter any more
			}
			else
			{
				// stack is now [parameters] [parameters] [parameter]
				// blindly add
				il.EmitCall(OpCodes.Callvirt, java.util.List.class.GetMethod("Add"), null); // stack is now [parameters]
				il.Emit(OpCodes.Pop); // IList.Add returns the new index (int); we don't care
			}
		}
		// stack is currently [parameters]
		il.Emit(OpCodes.Pop); // stack is now empty
		il.Emit(OpCodes.Ret);
		return (Action<IDbCommand, Object>)dm.CreateDelegate(Action<IDbCommand, Object>.class);
	}

	private static IDbCommand SetupCommand(IDbConnection cnn, IDbTransaction transaction, String sql, Action<IDbCommand, Object> paramReader, Object obj, Integer commandTimeout, CommandType commandType)
	{
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
		var cmd = cnn.CreateCommand();
		Action<IDbCommand, Boolean> bindByName = GetBindByName(cmd.getClass());
		if (bindByName != null)
		{
			bindByName(cmd, true);
		}
		if (transaction != null)
		{
			cmd.Transaction = transaction;
		}
		cmd.CommandText = sql;
		if (commandTimeout != null)
		{
			cmd.CommandTimeout = commandTimeout;
		}
		if (commandType != null)
		{
			cmd.CommandType = commandType.getValue();
		}
		if (paramReader != null)
		{
			paramReader(cmd, obj);
		}
		return cmd;
	}


	private static int ExecuteCommand(IDbConnection cnn, IDbTransaction transaction, String sql, Action<IDbCommand, Object> paramReader, Object obj, Integer commandTimeout, CommandType commandType)
	{
		IDbCommand cmd = null;
		boolean wasClosed = cnn.State == ConnectionState.Closed;
		try
		{
			cmd = SetupCommand(cnn, transaction, sql, paramReader, obj, commandTimeout, commandType);
			if (wasClosed)
			{
				cnn.Open();
			}
			return cmd.ExecuteNonQuery();
		}
		finally
		{
			if (wasClosed)
			{
				cnn.Close();
			}
			if (cmd != null)
			{
				cmd.dispose();
			}
		}
	}

	private static Func<IDataReader, Object> GetStructDeserializer(java.lang.Class type, java.lang.Class effectiveType, int index)
	{
		// no point using special per-type handling here; it boils down to the same, plus not all are supported anyway (see: SqlDataReader.GetChar - not supported!)
//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
///#pragma warning disable 618
		if (type == Character.class)
		{ // this *does* need special handling, though
//C# TO JAVA CONVERTER TODO TASK: Lambda expressions and anonymous methods are not converted by C# to Java Converter:
			return r => SqlMapper.ReadChar(r.GetValue(index));
		}
		if (type == Character.class)
		{
//C# TO JAVA CONVERTER TODO TASK: Lambda expressions and anonymous methods are not converted by C# to Java Converter:
//C# TO JAVA CONVERTER TODO TASK: Comparisons involving nullable type instances are not converted to null-value logic:
			return r => SqlMapper.ReadNullableChar(r.GetValue(index));
		}
		if (LinqBinary.equals(type.FullName))
		{
//C# TO JAVA CONVERTER TODO TASK: Lambda expressions and anonymous methods are not converted by C# to Java Converter:
			return r => Activator.CreateInstance(type, r.GetValue(index));
		}
//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
///#pragma warning restore 618

		if (effectiveType.IsEnum)
		{ // assume the value is returned as the correct type (int/byte/etc), but box back to the typed enum
//C# TO JAVA CONVERTER TODO TASK: Lambda expressions and anonymous methods are not converted by C# to Java Converter:
			return r =>
			{
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
				var val = r.GetValue(index);
				return val instanceof DBNull ? null : Enum.ToObject(effectiveType, val);
			}
		}
//C# TO JAVA CONVERTER TODO TASK: Lambda expressions and anonymous methods are not converted by C# to Java Converter:
		return r =>
		{
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
			var val = r.GetValue(index);
			return val instanceof DBNull ? null : val;
		}
	}

	private static final java.lang.reflect.Method enumParse = Enum.class.GetMethod("Parse", new java.lang.Class[] { java.lang.Class.class, String.class, Boolean.class }), getItem = IDataRecord.class.GetProperties(BindingFlags.Instance | BindingFlags.Public).Where(p => p.GetIndexParameters().Any() && p.GetIndexParameters()[0].ParameterType == Integer.class).Select(p => p.GetGetMethod()).First();

	/** 
	 Gets type-map for the given type
	 
	 @return Type map implementation, DefaultTypeMap instance if no override present
	*/
	public static ITypeMap GetTypeMap(java.lang.Class type)
	{
		if (type == null)
		{
			throw new ArgumentNullException("type");
		}
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
		var map = (ITypeMap)_typeMaps.get(type);
		if (map == null)
		{
			synchronized (_typeMaps)
			{ // double-checked; store this to avoid reflection next time we see this type
				// since multiple queries commonly use the same domain-entity/DTO/view-model type
				map = (ITypeMap)_typeMaps.get(type);
				if (map == null)
				{
					map = new DefaultTypeMap(type);
					_typeMaps.put(type, map);
				}
			}
		}
		return map;
	}

	// use Hashtable to get free lockless reading
	private static final java.util.Hashtable _typeMaps = new java.util.Hashtable();

	/** 
	 Set custom mapping for type deserializers
	 
	 @param type Entity type to override
	 @param map Mapping rules impementation, null to remove custom map
	*/
	public static void SetTypeMap(java.lang.Class type, ITypeMap map)
	{
		if (type == null)
		{
			throw new ArgumentNullException("type");
		}

		if (map == null || map instanceof DefaultTypeMap)
		{
			synchronized (_typeMaps)
			{
				_typeMaps.remove(type);
			}
		}
		else
		{
			synchronized (_typeMaps)
			{
				_typeMaps.put(type, map);
			}
		}

		PurgeQueryCacheByType(type);
	}

	/** 
	 Internal use only
	 
	 @param type
	 @param reader
	 @param startBound
	 @param length
	 @param returnNullIfFirstMissing
	 @return 
	*/
//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
//#if CSHARP30
	public static Func<IDataReader, Object> GetTypeDeserializer(java.lang.Class type, IDataReader reader, int startBound, int length, boolean returnNullIfFirstMissing)
//#else
//C# TO JAVA CONVERTER TODO TASK: C# optional parameters are not converted to Java:
//ORIGINAL LINE: public static Func<IDataReader, object> GetTypeDeserializer(Type type, IDataReader reader, int startBound = 0, int length = -1, bool returnNullIfFirstMissing = false)
	public static Func<IDataReader, Object> GetTypeDeserializer(java.lang.Class type, IDataReader reader, int startBound, int length, boolean returnNullIfFirstMissing)
//#endif
	{

		DynamicMethod dm = new DynamicMethod(String.format("Deserialize%1$s", Guid.NewGuid()), Object.class, new Object[] { IDataReader.class }, true);
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
		var il = dm.GetILGenerator();
		il.DeclareLocal(Integer.class);
		il.DeclareLocal(type);
		il.Emit(OpCodes.Ldc_I4_0);
		il.Emit(OpCodes.Stloc_0);

		if (length == -1)
		{
			length = reader.FieldCount - startBound;
		}

		if (reader.FieldCount <= startBound)
		{
			throw new IllegalArgumentException("When using the multi-mapping APIs ensure you set the splitOn param if you have keys other than Id", "splitOn");
		}

//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
//C# TO JAVA CONVERTER TODO TASK: Lambda expressions and anonymous methods are not converted by C# to Java Converter:
		var names = Enumerable.Range(startBound, length).Select(i => reader.GetName(i)).toArray();

		ITypeMap typeMap = GetTypeMap(type);

		int index = startBound;

		java.lang.reflect.Constructor specializedConstructor = null;

		if (type.IsValueType)
		{
			il.Emit(OpCodes.Ldloca_S, (byte)1);
			il.Emit(OpCodes.Initobj, type);
		}
		else
		{
			java.lang.Class[] types = new java.lang.Class[length];
			for (int i = startBound; i < startBound + length; i++)
			{
				types[i - startBound] = reader.GetFieldType(i);
			}

			if (type.IsValueType)
			{
				il.Emit(OpCodes.Ldloca_S, (byte)1);
				il.Emit(OpCodes.Initobj, type);
			}
			else
			{
				java.lang.reflect.Constructor ctor = typeMap.FindConstructor(names, types);
				if (ctor == null)
				{
//C# TO JAVA CONVERTER TODO TASK: Lambda expressions and anonymous methods are not converted by C# to Java Converter:
					String proposedTypes = "(" + DotNetToJavaStringHelper.join(", ", types.Select((t, i) => t.FullName + " " + names[i]).toArray()) + ")";
					throw new InvalidOperationException(String.format("A parameterless default constructor or one matching signature %1$s is required for %2$s materialization", proposedTypes, type.FullName));
				}

				if (ctor.GetParameters().getLength() == 0)
				{
					il.Emit(OpCodes.Newobj, ctor);
					il.Emit(OpCodes.Stloc_1);
				}
				else
				{
					specializedConstructor = ctor;
				}
			}
		}

		il.BeginExceptionBlock();
		if (type.IsValueType)
		{
			il.Emit(OpCodes.Ldloca_S, (byte)1); // [target]
		}
		else if (specializedConstructor == null)
		{
			il.Emit(OpCodes.Ldloc_1); // [target]
		}

//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
//C# TO JAVA CONVERTER TODO TASK: Lambda expressions and anonymous methods are not converted by C# to Java Converter:
		var members = (specializedConstructor != null ? names.Select(n => typeMap.GetConstructorParameter(specializedConstructor, n)) : names.Select(n => typeMap.GetMember(n))).ToList();

		// stack is now [target]

		boolean first = true;
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
		var allDone = il.DefineLabel();
		int enumDeclareLocal = -1;
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
		for (var item : members)
		{
			if (item != null)
			{
				if (specializedConstructor == null)
				{
					il.Emit(OpCodes.Dup); // stack is now [target][target]
				}
				Label isDbNullLabel = il.DefineLabel();
				Label finishLabel = il.DefineLabel();

				il.Emit(OpCodes.Ldarg_0); // stack is now [target][target][reader]
				EmitInt32(il, index); // stack is now [target][target][reader][index]
				il.Emit(OpCodes.Dup); // stack is now [target][target][reader][index][index]
				il.Emit(OpCodes.Stloc_0); // stack is now [target][target][reader][index]
				il.Emit(OpCodes.Callvirt, getItem); // stack is now [target][target][value-as-object]

				java.lang.Class memberType = item.MemberType;

				if (memberType == Character.class || memberType == Character.class)
				{
					il.EmitCall(OpCodes.Call, SqlMapper.class.GetMethod(memberType == Character.class ? "ReadChar" : "ReadNullableChar", BindingFlags.Static | BindingFlags.Public), null); // stack is now [target][target][typed-value]
				}
				else
				{
					il.Emit(OpCodes.Dup); // stack is now [target][target][value][value]
					il.Emit(OpCodes.Isinst, DBNull.class); // stack is now [target][target][value-as-object][DBNull or null]
					il.Emit(OpCodes.Brtrue_S, isDbNullLabel); // stack is now [target][target][value-as-object]

					// unbox nullable enums as the primitive, i.e. byte etc

//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
					var nullUnderlyingType = Nullable.GetUnderlyingType(memberType);
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
					var unboxType = nullUnderlyingType != null && nullUnderlyingType.IsEnum ? nullUnderlyingType : memberType;

					if (unboxType.IsEnum)
					{
						if (enumDeclareLocal == -1)
						{
							enumDeclareLocal = il.DeclareLocal(String.class).LocalIndex;
						}

						Label isNotString = il.DefineLabel();
						il.Emit(OpCodes.Dup); // stack is now [target][target][value][value]
						il.Emit(OpCodes.Isinst, String.class); // stack is now [target][target][value-as-object][string or null]
						il.Emit(OpCodes.Dup); // stack is now [target][target][value-as-object][string or null][string or null]
						StoreLocal(il, enumDeclareLocal); // stack is now [target][target][value-as-object][string or null]
						il.Emit(OpCodes.Brfalse_S, isNotString); // stack is now [target][target][value-as-object]

						il.Emit(OpCodes.Pop); // stack is now [target][target]

						il.Emit(OpCodes.Ldtoken, unboxType); // stack is now [target][target][enum-type-token]
						il.EmitCall(OpCodes.Call, java.lang.Class.class.GetMethod("GetTypeFromHandle"), null); // stack is now [target][target][enum-type]
						il.Emit(OpCodes.Ldloc_2); // stack is now [target][target][enum-type][string]
						il.Emit(OpCodes.Ldc_I4_1); // stack is now [target][target][enum-type][string][true]
						il.EmitCall(OpCodes.Call, enumParse, null); // stack is now [target][target][enum-as-object]

						il.MarkLabel(isNotString);

						il.Emit(OpCodes.Unbox_Any, unboxType); // stack is now [target][target][typed-value]

						if (nullUnderlyingType != null)
						{
							il.Emit(OpCodes.Newobj, memberType.getConstructor(new var[] { nullUnderlyingType })); // stack is now [target][target][enum-value]
						}
					}
					else if (LinqBinary.equals(memberType.FullName))
					{
						il.Emit(OpCodes.Unbox_Any, byte[].class); // stack is now [target][target][byte-array]
						il.Emit(OpCodes.Newobj, memberType.getConstructor(new java.lang.Class[] { byte[].class })); // stack is now [target][target][binary]
					}
					else
					{
						java.lang.Class dataType = reader.GetFieldType(index);
						TypeCode dataTypeCode = java.lang.Class.GetTypeCode(dataType), unboxTypeCode = java.lang.Class.GetTypeCode(unboxType);
						if (dataType == unboxType || dataTypeCode == unboxTypeCode || dataTypeCode == java.lang.Class.GetTypeCode(nullUnderlyingType))
						{
							il.Emit(OpCodes.Unbox_Any, unboxType); // stack is now [target][target][typed-value]
						}
						else
						{
							// not a direct match; need to tweak the unbox
							boolean handled = true;
							OpCode opCode = null;
							if (dataTypeCode == TypeCode.Decimal || unboxTypeCode == TypeCode.Decimal)
							{ // no IL level conversions to/from decimal; I guess we could use the static operators, but
								// this feels an edge-case
								handled = false;
							}
							else
							{
								switch (unboxTypeCode)
								{
									case TypeCode.Byte:
										opCode = OpCodes.Conv_Ovf_I1_Un;
										break;
									case TypeCode.SByte:
										opCode = OpCodes.Conv_Ovf_I1;
										break;
									case TypeCode.UInt16:
										opCode = OpCodes.Conv_Ovf_I2_Un;
										break;
									case TypeCode.Int16:
										opCode = OpCodes.Conv_Ovf_I2;
										break;
									case TypeCode.UInt32:
										opCode = OpCodes.Conv_Ovf_I4_Un;
										break;
									case TypeCode.Boolean: // boolean is basically an int, at least at this level
									case TypeCode.Int32:
										opCode = OpCodes.Conv_Ovf_I4;
										break;
									case TypeCode.UInt64:
										opCode = OpCodes.Conv_Ovf_I8_Un;
										break;
									case TypeCode.Int64:
										opCode = OpCodes.Conv_Ovf_I8;
										break;
									case TypeCode.Single:
										opCode = OpCodes.Conv_R4;
										break;
									case TypeCode.Double:
										opCode = OpCodes.Conv_R8;
										break;
									default:
										handled = false;
										break;
								}
							}
							if (handled)
							{ // unbox as the data-type, then use IL-level convert
								il.Emit(OpCodes.Unbox_Any, dataType); // stack is now [target][target][data-typed-value]
								il.Emit(opCode); // stack is now [target][target][typed-value]
								if (unboxTypeCode == TypeCode.Boolean)
								{ // compare to zero; I checked "csc" - this is the trick it uses; nice
									il.Emit(OpCodes.Ldc_I4_0);
									il.Emit(OpCodes.Ceq);
									il.Emit(OpCodes.Ldc_I4_0);
									il.Emit(OpCodes.Ceq);
								}
							}
							else
							{ // use flexible conversion
								il.Emit(OpCodes.Ldtoken, unboxType); // stack is now [target][target][value][member-type-token]
								il.EmitCall(OpCodes.Call, java.lang.Class.class.GetMethod("GetTypeFromHandle"), null); // stack is now [target][target][value][member-type]
								il.EmitCall(OpCodes.Call, Convert.class.GetMethod("ChangeType", new java.lang.Class[] { Object.class, java.lang.Class.class }), null); // stack is now [target][target][boxed-member-type-value]
								il.Emit(OpCodes.Unbox_Any, unboxType); // stack is now [target][target][typed-value]
							}

						}

					}
				}
				if (specializedConstructor == null)
				{
					// Store the value in the property/field
					if (item.Property != null)
					{
						if (type.IsValueType)
						{
							il.Emit(OpCodes.Call, DefaultTypeMap.GetPropertySetter(item.Property, type)); // stack is now [target]
						}
						else
						{
							il.Emit(OpCodes.Callvirt, DefaultTypeMap.GetPropertySetter(item.Property, type)); // stack is now [target]
						}
					}
					else
					{
						il.Emit(OpCodes.Stfld, item.Field); // stack is now [target]
					}
				}

				il.Emit(OpCodes.Br_S, finishLabel); // stack is now [target]

				il.MarkLabel(isDbNullLabel); // incoming stack: [target][target][value]
				if (specializedConstructor != null)
				{
					il.Emit(OpCodes.Pop);
					if (item.MemberType.IsValueType)
					{
						int localIndex = il.DeclareLocal(item.MemberType).LocalIndex;
						LoadLocalAddress(il, localIndex);
						il.Emit(OpCodes.Initobj, item.MemberType);
						LoadLocal(il, localIndex);
					}
					else
					{
						il.Emit(OpCodes.Ldnull);
					}
				}
				else
				{
					il.Emit(OpCodes.Pop); // stack is now [target][target]
					il.Emit(OpCodes.Pop); // stack is now [target]
				}

				if (first && returnNullIfFirstMissing)
				{
					il.Emit(OpCodes.Pop);
					il.Emit(OpCodes.Ldnull); // stack is now [null]
					il.Emit(OpCodes.Stloc_1);
					il.Emit(OpCodes.Br, allDone);
				}

				il.MarkLabel(finishLabel);
			}
			first = false;
			index += 1;
		}
		if (type.IsValueType)
		{
			il.Emit(OpCodes.Pop);
		}
		else
		{
			if (specializedConstructor != null)
			{
				il.Emit(OpCodes.Newobj, specializedConstructor);
			}
			il.Emit(OpCodes.Stloc_1); // stack is empty
		}
		il.MarkLabel(allDone);
		il.BeginCatchBlock(RuntimeException.class); // stack is Exception
		il.Emit(OpCodes.Ldloc_0); // stack is Exception, index
		il.Emit(OpCodes.Ldarg_0); // stack is Exception, index, reader
		il.EmitCall(OpCodes.Call, SqlMapper.class.GetMethod("ThrowDataException"), null);
		il.EndExceptionBlock();

		il.Emit(OpCodes.Ldloc_1); // stack is [rval]
		if (type.IsValueType)
		{
			il.Emit(OpCodes.Box, type);
		}
		il.Emit(OpCodes.Ret);

		return (Func<IDataReader, Object>)dm.CreateDelegate(Func<IDataReader, Object>.class);
	}

	private static void LoadLocal(ILGenerator il, int index)
	{
		if (index < 0 || index >= Short.MAX_VALUE)
		{
			throw new ArgumentNullException("index");
		}
		switch (index)
		{
			case 0:
				il.Emit(OpCodes.Ldloc_0);
				break;
			case 1:
				il.Emit(OpCodes.Ldloc_1);
				break;
			case 2:
				il.Emit(OpCodes.Ldloc_2);
				break;
			case 3:
				il.Emit(OpCodes.Ldloc_3);
				break;
			default:
				if (index <= 255)
				{
					il.Emit(OpCodes.Ldloc_S, (byte)index);
				}
				else
				{
					il.Emit(OpCodes.Ldloc, (short)index);
				}
				break;
		}
	}
	private static void StoreLocal(ILGenerator il, int index)
	{
		if (index < 0 || index >= Short.MAX_VALUE)
		{
			throw new ArgumentNullException("index");
		}
		switch (index)
		{
			case 0:
				il.Emit(OpCodes.Stloc_0);
				break;
			case 1:
				il.Emit(OpCodes.Stloc_1);
				break;
			case 2:
				il.Emit(OpCodes.Stloc_2);
				break;
			case 3:
				il.Emit(OpCodes.Stloc_3);
				break;
			default:
				if (index <= 255)
				{
					il.Emit(OpCodes.Stloc_S, (byte)index);
				}
				else
				{
					il.Emit(OpCodes.Stloc, (short)index);
				}
				break;
		}
	}
	private static void LoadLocalAddress(ILGenerator il, int index)
	{
		if (index < 0 || index >= Short.MAX_VALUE)
		{
			throw new ArgumentNullException("index");
		}

		if (index <= 255)
		{
			il.Emit(OpCodes.Ldloca_S, (byte)index);
		}
		else
		{
			il.Emit(OpCodes.Ldloca, (short)index);
		}
	}
	/** 
	 Throws a data exception, only used internally
	 
	 @param ex
	 @param index
	 @param reader
	*/
	public static void ThrowDataException(RuntimeException ex, int index, IDataReader reader)
	{
		RuntimeException toThrow;
		try
		{
			String name = "(n/a)", value = "(n/a)";
			if (reader != null && index >= 0 && index < reader.FieldCount)
			{
				name = reader.GetName(index);
				Object val = reader.GetValue(index);
				if (val == null || val instanceof DBNull)
				{
					value = "<null>";
				}
				else
				{
					value = String.valueOf(val) + " - " + java.lang.Class.GetTypeCode(val.getClass());
				}
			}
			toThrow = new DataException(String.format("Error parsing column %1$s (%2$s=%3$s)", index, name, value), ex);
		}
		catch (java.lang.Exception e)
		{ // throw the **original** exception, wrapped as DataException
			toThrow = new DataException(ex.getMessage(), ex);
		}
		throw toThrow;
	}
	private static void EmitInt32(ILGenerator il, int value)
	{
		switch (value)
		{
			case -1:
				il.Emit(OpCodes.Ldc_I4_M1);
				break;
			case 0:
				il.Emit(OpCodes.Ldc_I4_0);
				break;
			case 1:
				il.Emit(OpCodes.Ldc_I4_1);
				break;
			case 2:
				il.Emit(OpCodes.Ldc_I4_2);
				break;
			case 3:
				il.Emit(OpCodes.Ldc_I4_3);
				break;
			case 4:
				il.Emit(OpCodes.Ldc_I4_4);
				break;
			case 5:
				il.Emit(OpCodes.Ldc_I4_5);
				break;
			case 6:
				il.Emit(OpCodes.Ldc_I4_6);
				break;
			case 7:
				il.Emit(OpCodes.Ldc_I4_7);
				break;
			case 8:
				il.Emit(OpCodes.Ldc_I4_8);
				break;
			default:
				if (value >= -128 && value <= 127)
				{
					il.Emit(OpCodes.Ldc_I4_S, (byte)value);
				}
				else
				{
					il.Emit(OpCodes.Ldc_I4, value);
				}
				break;
		}
	}

	/** 
	 The grid reader provides interfaces for reading multiple result sets from a Dapper query 
	 
	*/
	public static class GridReader implements IDisposable
	{
		private IDataReader reader;
		private IDbCommand command;
		private Identity identity;

		public GridReader(IDbCommand command, IDataReader reader, Identity identity)
		{
			this.command = command;
			this.reader = reader;
			this.identity = identity;
		}

//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
//#if !CSHARP30

		/** 
		 Read the next grid of results, returned as a dynamic object
		 
		*/
//C# TO JAVA CONVERTER TODO TASK: C# optional parameters are not converted to Java:
//ORIGINAL LINE: public IEnumerable<dynamic> Read(bool buffered = true)
		public final Iterable<dynamic> Read(boolean buffered)
		{
			return Read<DapperRow>(buffered);
		}
//#endif

//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
//#if CSHARP30
		/** 
		 Read the next grid of results
		 
		*/
		public final <T> Iterable<T> Read()
		{
			return Read<T>(true);
		}
//#endif
		/** 
		 Read the next grid of results
		 
		*/
//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
//#if CSHARP30
		public final <T> Iterable<T> Read(boolean buffered)
//#else
//C# TO JAVA CONVERTER TODO TASK: C# optional parameters are not converted to Java:
//ORIGINAL LINE: public IEnumerable<T> Read<T>(bool buffered = true)
		public final <T> Iterable<T> Read(boolean buffered)
//#endif
		{
			if (reader == null)
			{
				throw new ObjectDisposedException(getClass().FullName, "The reader has been disposed; this can happen after all data has been consumed");
			}
			if (consumed)
			{
				throw new InvalidOperationException("Query results must be consumed in the correct order, and each result can only be consumed once");
			}
			Identity typedIdentity = identity.ForGrid(T.class, gridIndex);
			CacheInfo cache = GetCacheInfo(typedIdentity);
			DeserializerState deserializer = cache.getDeserializer().clone();

			int hash = GetColumnHash(reader);
			if (deserializer.Func == null || deserializer.Hash != hash)
			{
				deserializer = new DeserializerState(hash, GetDeserializer(T.class, reader, 0, -1, false));
				cache.setDeserializer(deserializer.clone());
			}
			consumed = true;
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
			var result = ReadDeferred<T>(gridIndex, deserializer.Func, typedIdentity);
			return buffered ? result.ToList() : result;
		}

		private <TFirst, TSecond, TThird, TFourth, TFifth, TReturn> Iterable<TReturn> MultiReadInternal(Object func, String splitOn)
		{
			Identity identity = this.identity.ForGrid(TReturn.class, new java.lang.Class[] { TFirst.class, TSecond.class, TThird.class, TFourth.class, TFifth.class }, gridIndex);
			try
			{
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
				for (var r : SqlMapper.<TFirst, TSecond, TThird, TFourth, TFifth, TReturn>MultiMapImpl(null, null, func, null, null, splitOn, null, null, reader, identity))
				{
//C# TO JAVA CONVERTER TODO TASK: Java does not have an equivalent to the C# 'yield' keyword:
					yield return r;
				}
			}
			finally
			{
				NextResult();
			}
		}

//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
//#if CSHARP30
		/** 
		 Read multiple objects from a single recordset on the grid
		 
		*/
		public final <TFirst, TSecond, TReturn> Iterable<TReturn> Read(Func<TFirst, TSecond, TReturn> func, String splitOn)
		{
			return Read<TFirst, TSecond, TReturn>(func, splitOn, true);
		}
//#endif
		/** 
		 Read multiple objects from a single recordset on the grid
		 
		*/
//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
//#if CSHARP30
		public final <TFirst, TSecond, TReturn> Iterable<TReturn> Read(Func<TFirst, TSecond, TReturn> func, String splitOn, boolean buffered)
//#else
//C# TO JAVA CONVERTER TODO TASK: C# optional parameters are not converted to Java:
//ORIGINAL LINE: public IEnumerable<TReturn> Read<TFirst, TSecond, TReturn>(Func<TFirst, TSecond, TReturn> func, string splitOn = "id", bool buffered = true)
		public final <TFirst, TSecond, TReturn> Iterable<TReturn> Read(Func<TFirst, TSecond, TReturn> func, String splitOn, boolean buffered)
//#endif
		{
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
			var result = MultiReadInternal<TFirst, TSecond, DontMap, DontMap, DontMap, TReturn>(func, splitOn);
			return buffered ? result.ToList() : result;
		}

//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
//#if CSHARP30
		/** 
		 Read multiple objects from a single recordset on the grid
		 
		*/
		public final <TFirst, TSecond, TThird, TReturn> Iterable<TReturn> Read(Func<TFirst, TSecond, TThird, TReturn> func, String splitOn)
		{
			return Read<TFirst, TSecond, TThird, TReturn>(func, splitOn, true);
		}
//#endif
		/** 
		 Read multiple objects from a single recordset on the grid
		 
		*/
//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
//#if CSHARP30
		public final <TFirst, TSecond, TThird, TReturn> Iterable<TReturn> Read(Func<TFirst, TSecond, TThird, TReturn> func, String splitOn, boolean buffered)
//#else
//C# TO JAVA CONVERTER TODO TASK: C# optional parameters are not converted to Java:
//ORIGINAL LINE: public IEnumerable<TReturn> Read<TFirst, TSecond, TThird, TReturn>(Func<TFirst, TSecond, TThird, TReturn> func, string splitOn = "id", bool buffered = true)
		public final <TFirst, TSecond, TThird, TReturn> Iterable<TReturn> Read(Func<TFirst, TSecond, TThird, TReturn> func, String splitOn, boolean buffered)
//#endif
		{
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
			var result = MultiReadInternal<TFirst, TSecond, TThird, DontMap, DontMap, TReturn>(func, splitOn);
			return buffered ? result.ToList() : result;
		}

//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
//#if CSHARP30
		/** 
		 Read multiple objects from a single record set on the grid
		 
		*/
		public final <TFirst, TSecond, TThird, TFourth, TReturn> Iterable<TReturn> Read(Func<TFirst, TSecond, TThird, TFourth, TReturn> func, String splitOn)
		{
			return Read<TFirst, TSecond, TThird, TFourth, TReturn>(func, splitOn, true);
		}
//#endif

		/** 
		 Read multiple objects from a single record set on the grid
		 
		*/
//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
//#if CSHARP30
		public final <TFirst, TSecond, TThird, TFourth, TReturn> Iterable<TReturn> Read(Func<TFirst, TSecond, TThird, TFourth, TReturn> func, String splitOn, boolean buffered)
//#else
//C# TO JAVA CONVERTER TODO TASK: C# optional parameters are not converted to Java:
//ORIGINAL LINE: public IEnumerable<TReturn> Read<TFirst, TSecond, TThird, TFourth, TReturn>(Func<TFirst, TSecond, TThird, TFourth, TReturn> func, string splitOn = "id", bool buffered = true)
		public final <TFirst, TSecond, TThird, TFourth, TReturn> Iterable<TReturn> Read(Func<TFirst, TSecond, TThird, TFourth, TReturn> func, String splitOn, boolean buffered)
//#endif
		{
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
			var result = MultiReadInternal<TFirst, TSecond, TThird, TFourth, DontMap, TReturn>(func, splitOn);
			return buffered ? result.ToList() : result;
		}



//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
//#if !CSHARP30
		/** 
		 Read multiple objects from a single record set on the grid
		 
		*/
//C# TO JAVA CONVERTER TODO TASK: C# optional parameters are not converted to Java:
//ORIGINAL LINE: public IEnumerable<TReturn> Read<TFirst, TSecond, TThird, TFourth, TFifth, TReturn>(Func<TFirst, TSecond, TThird, TFourth, TFifth, TReturn> func, string splitOn = "id", bool buffered = true)
		public final <TFirst, TSecond, TThird, TFourth, TFifth, TReturn> Iterable<TReturn> Read(Func<TFirst, TSecond, TThird, TFourth, TFifth, TReturn> func, String splitOn, boolean buffered)
		{
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
			var result = MultiReadInternal<TFirst, TSecond, TThird, TFourth, TFifth, TReturn>(func, splitOn);
			return buffered ? result.ToList() : result;
		}
//#endif

		private <T> Iterable<T> ReadDeferred(int index, Func<IDataReader, Object> deserializer, Identity typedIdentity)
		{
			try
			{
				while (index == gridIndex && reader.Read())
				{
//C# TO JAVA CONVERTER TODO TASK: Java does not have an equivalent to the C# 'yield' keyword:
					yield return (T)deserializer(reader);
				}
			}
			finally // finally so that First etc progresses things even when multiple rows
			{
				if (index == gridIndex)
				{
					NextResult();
				}
			}
		}
		private int gridIndex, readCount;
		private boolean consumed;
		private void NextResult()
		{
			if (reader.NextResult())
			{
				readCount++;
				gridIndex++;
				consumed = false;
			}
			else
			{
				// happy path; close the reader cleanly - no
				// need for "Cancel" etc
				reader.dispose();
				reader = null;

				dispose();
			}

		}
		/** 
		 Dispose the grid, closing and disposing both the underlying reader and command.
		 
		*/
		public final void dispose()
		{
			if (reader != null)
			{
				if (!reader.IsClosed && command != null)
				{
					command.Cancel();
				}
				reader.dispose();
				reader = null;
			}
			if (command != null)
			{
				command.dispose();
				command = null;
			}
		}
	}




	private static final ConcurrentDictionary<java.lang.Class, java.util.ArrayList<String>> ParamNameCache = new ConcurrentDictionary<java.lang.Class, java.util.ArrayList<String>>();

	/** Insert data into table.
	 
	 @param connection
	 @param data
	 @param table
	 @param transaction
	 @param commandTimeout
	 @return 
	*/
//C# TO JAVA CONVERTER TODO TASK: Extension methods are not available in Java:
//ORIGINAL LINE: public static int Insert(this IDbConnection connection, dynamic data, string table, IDbTransaction transaction = null, Nullable<int> commandTimeout = null)
//C# TO JAVA CONVERTER TODO TASK: C# optional parameters are not converted to Java:
	public static int Insert(IDbConnection connection, dynamic data, String table, IDbTransaction transaction, Integer commandTimeout)
	{
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
		var obj = (Object)((data instanceof Object) ? data : null);
		java.util.ArrayList<String> properties = GetProperties(obj);
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
		var columns = DotNetToJavaStringHelper.join(",", properties);
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
//C# TO JAVA CONVERTER TODO TASK: Lambda expressions and anonymous methods are not converted by C# to Java Converter:
		var values = DotNetToJavaStringHelper.join(",", properties.Select(p => "@" + p));
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
		var sql = String.format("insert into [%1$s] (%2$s) values (%3$s) select cast(scope_identity() as bigint)", table, columns, values);

		return connection.Execute(sql, obj, transaction, commandTimeout);
	}
	/** 
	 
	 Updata data for table with a specified condition.
	 @param connection
	 @param data
	 @param condition
	 @param table
	 @param transaction
	 @param commandTimeout
	 @return 
	*/
//C# TO JAVA CONVERTER TODO TASK: Extension methods are not available in Java:
//ORIGINAL LINE: public static int Update(this IDbConnection connection, dynamic data, dynamic condition, string table, IDbTransaction transaction = null, Nullable<int> commandTimeout = null)
//C# TO JAVA CONVERTER TODO TASK: C# optional parameters are not converted to Java:
	public static int Update(IDbConnection connection, dynamic data, dynamic condition, String table, IDbTransaction transaction, Integer commandTimeout)
	{
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
		var obj = (Object)((data instanceof Object) ? data : null);
		java.util.ArrayList<String> properties = GetProperties(obj);
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
//C# TO JAVA CONVERTER TODO TASK: Lambda expressions and anonymous methods are not converted by C# to Java Converter:
		var updateFields = DotNetToJavaStringHelper.join(",", properties.Select(p => p + " = @" + p));

//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
		var conditionObj = (Object)((condition instanceof Object) ? condition : null);
		java.util.ArrayList<String> whereProperties = GetProperties(conditionObj);
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
//C# TO JAVA CONVERTER TODO TASK: Lambda expressions and anonymous methods are not converted by C# to Java Converter:
		var where = DotNetToJavaStringHelper.join(" and ", whereProperties.Select(p => p + " = @" + p));

//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
		var sql = String.format("update [%1$s] set %2$s where %3$s", table, updateFields, where);

		DynamicParameters parameters = new DynamicParameters(data);
		parameters.AddDynamicParams(condition);

		return connection.Execute(sql, parameters, transaction, commandTimeout);
	}
	/** Delete data from table with a specified condition.
	 
	 @param connection
	 @param condition
	 @param table
	 @param transaction
	 @param commandTimeout
	 @return 
	*/
//C# TO JAVA CONVERTER TODO TASK: Extension methods are not available in Java:
//ORIGINAL LINE: public static int Delete(this IDbConnection connection, dynamic condition, string table, IDbTransaction transaction = null, Nullable<int> commandTimeout = null)
//C# TO JAVA CONVERTER TODO TASK: C# optional parameters are not converted to Java:
	public static int Delete(IDbConnection connection, dynamic condition, String table, IDbTransaction transaction, Integer commandTimeout)
	{
		java.util.ArrayList<String> properties = GetProperties((Object)((condition instanceof Object) ? condition : null));
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
//C# TO JAVA CONVERTER TODO TASK: Lambda expressions and anonymous methods are not converted by C# to Java Converter:
		var whereFields = DotNetToJavaStringHelper.join(" and ", properties.Select(p => p + " = @" + p));
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
		var sql = String.format("delete from [%1$s] where %2$s", table, whereFields);

		return SqlMapper.Execute(connection, sql, condition, transaction, commandTimeout);
	}
	/** Get data count from table with a specified condition.
	 
	 @param connection
	 @param condition
	 @param table
	 @param isOr
	 @param transaction
	 @param commandTimeout
	 @return 
	*/
//C# TO JAVA CONVERTER TODO TASK: Extension methods are not available in Java:
//ORIGINAL LINE: public static int GetCount(this IDbConnection connection, dynamic condition, string table, bool isOr = false, IDbTransaction transaction = null, Nullable<int> commandTimeout = null)
//C# TO JAVA CONVERTER TODO TASK: C# optional parameters are not converted to Java:
	public static int GetCount(IDbConnection connection, dynamic condition, String table, boolean isOr, IDbTransaction transaction, Integer commandTimeout)
	{
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
		var obj = (Object)((condition instanceof Object) ? condition : null);
		java.util.ArrayList<String> properties = GetProperties(obj);
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
//C# TO JAVA CONVERTER TODO TASK: Lambda expressions and anonymous methods are not converted by C# to Java Converter:
		var whereFields = isOr ? DotNetToJavaStringHelper.join(" or ", properties.Select(p => p + " = @" + p)) : DotNetToJavaStringHelper.join(" and ", properties.Select(p => p + " = @" + p));
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
		var sql = String.format("select count(*) from [%1$s] where %2$s", table, whereFields);

		return connection.<Integer>Query(sql, obj, transaction, true, commandTimeout).Single();
	}
	/** Get a field value from table with a specified condition.
	 
	 @param connection
	 @param condition
	 @param table
	 @param field
	 @param transaction
	 @param commandTimeout
	 <typeparam name="T"></typeparam>
	 @return 
	*/
//C# TO JAVA CONVERTER TODO TASK: Extension methods are not available in Java:
//ORIGINAL LINE: public static T GetValue<T>(this IDbConnection connection, dynamic condition, string table, string field, IDbTransaction transaction = null, Nullable<int> commandTimeout = null)
//C# TO JAVA CONVERTER TODO TASK: C# optional parameters are not converted to Java:
	public static <T> T GetValue(IDbConnection connection, dynamic condition, String table, String field, IDbTransaction transaction, Integer commandTimeout)
	{
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
		var obj = (Object)((condition instanceof Object) ? condition : null);
		java.util.ArrayList<String> properties = GetProperties(obj);
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
//C# TO JAVA CONVERTER TODO TASK: Lambda expressions and anonymous methods are not converted by C# to Java Converter:
		var whereFields = DotNetToJavaStringHelper.join(" and ", properties.Select(p => p + " = @" + p));
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
		var sql = String.format("select %3$s from [%1$s] where %2$s", table, whereFields, field);

		return connection.<T>Query(sql, obj, transaction, true, commandTimeout).SingleOrDefault();
	}
	/** Query all data from table.
	 
	 @param connection
	 @param table
	 @param transaction
	 @param commandTimeout
	 @return 
	*/
//C# TO JAVA CONVERTER TODO TASK: Extension methods are not available in Java:
//ORIGINAL LINE: public static IEnumerable<dynamic> QueryAll(this IDbConnection connection, string table, IDbTransaction transaction = null, Nullable<int> commandTimeout = null)
//C# TO JAVA CONVERTER TODO TASK: C# optional parameters are not converted to Java:
	public static Iterable<dynamic> QueryAll(IDbConnection connection, String table, IDbTransaction transaction, Integer commandTimeout)
	{
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
		var sql = String.format("select * from [%1$s]", table);
		return connection.Query(sql, null, transaction, true, commandTimeout);
	}
	/** Query all data from table.
	 
	 @param connection
	 @param table
	 @param transaction
	 @param commandTimeout
	 <typeparam name="T"></typeparam>
	 @return 
	*/
//C# TO JAVA CONVERTER TODO TASK: Extension methods are not available in Java:
//ORIGINAL LINE: public static IEnumerable<T> QueryAll<T>(this IDbConnection connection, string table, IDbTransaction transaction = null, Nullable<int> commandTimeout = null)
//C# TO JAVA CONVERTER TODO TASK: C# optional parameters are not converted to Java:
	public static <T> Iterable<T> QueryAll(IDbConnection connection, String table, IDbTransaction transaction, Integer commandTimeout)
	{
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
		var sql = String.format("select * from [%1$s]", table);
		return connection.<T>Query(sql, null, transaction, true, commandTimeout);
	}

	/** Query data from table with a specified condition.
	 
	 @param connection
	 @param condition
	 @param table
	 @param transaction
	 @param commandTimeout
	 @return 
	*/
//C# TO JAVA CONVERTER TODO TASK: Extension methods are not available in Java:
//ORIGINAL LINE: public static IEnumerable<dynamic> Query(this IDbConnection connection, dynamic condition, string table, IDbTransaction transaction = null, Nullable<int> commandTimeout = null)
//C# TO JAVA CONVERTER TODO TASK: C# optional parameters are not converted to Java:
	public static Iterable<dynamic> Query(IDbConnection connection, dynamic condition, String table, IDbTransaction transaction, Integer commandTimeout)
	{
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
		var obj = (Object)((condition instanceof Object) ? condition : null);
		java.util.ArrayList<String> properties = GetProperties(obj);
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
//C# TO JAVA CONVERTER TODO TASK: Lambda expressions and anonymous methods are not converted by C# to Java Converter:
		var whereFields = DotNetToJavaStringHelper.join(" and ", properties.Select(p => p + " = @" + p));
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
		var sql = String.format("select * from [%1$s] where %2$s", table, whereFields);

		return connection.Query(sql, obj, transaction, true, commandTimeout);
	}
	/** Query data from table with specified condition.
	 
	 @param connection
	 @param condition
	 @param table
	 @param transaction
	 @param commandTimeout
	 <typeparam name="T"></typeparam>
	 @return 
	*/
//C# TO JAVA CONVERTER TODO TASK: Extension methods are not available in Java:
//ORIGINAL LINE: public static IEnumerable<T> Query<T>(this IDbConnection connection, object condition, string table, IDbTransaction transaction = null, Nullable<int> commandTimeout = null)
//C# TO JAVA CONVERTER TODO TASK: C# optional parameters are not converted to Java:
	public static <T> Iterable<T> Query(IDbConnection connection, Object condition, String table, IDbTransaction transaction, Integer commandTimeout)
	{
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
		var obj = (Object)((condition instanceof Object) ? condition : null);
		java.util.ArrayList<String> properties = GetProperties(obj);
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
//C# TO JAVA CONVERTER TODO TASK: Lambda expressions and anonymous methods are not converted by C# to Java Converter:
		var whereFields = DotNetToJavaStringHelper.join(" and ", properties.Select(p => p + " = @" + p));
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
		var sql = String.format("select * from [%1$s] where %2$s", table, whereFields);

		return connection.<T>Query(sql, obj, transaction, true, commandTimeout);
	}

	/** Try to execute a given action and auto close the dbconnection after the action complete.
	 
	 @param connection
	 @param action
	*/
//C# TO JAVA CONVERTER TODO TASK: Extension methods are not available in Java:
//ORIGINAL LINE: public static void TryExecute(this IDbConnection connection, Action<IDbConnection> action)
	public static void TryExecute(IDbConnection connection, Action<IDbConnection> action)
	{
//C# TO JAVA CONVERTER NOTE: The following 'using' block is replaced by its Java equivalent:
//		using (connection)
		try
		{
			connection.Open();
			action(connection);
		}
		finally
		{
			connection.dispose();
		}
	}
	/** Try to execute a given func and auto close the dbconnection after the action complete.
	 
	 @param connection
	 @param func
	 <typeparam name="T"></typeparam>
	 @return 
	*/
//C# TO JAVA CONVERTER TODO TASK: Extension methods are not available in Java:
//ORIGINAL LINE: public static T TryExecute<T>(this IDbConnection connection, Func<IDbConnection, T> func)
	public static <T> T TryExecute(IDbConnection connection, Func<IDbConnection, T> func)
	{
//C# TO JAVA CONVERTER NOTE: The following 'using' block is replaced by its Java equivalent:
//		using (connection)
		try
		{
			connection.Open();
			return func(connection);
		}
		finally
		{
			connection.dispose();
		}
	}
	/** Try to execute a given action in transaction and auto close the dbconnection after the action complete.
	 
	 @param connection
	 @param action
	*/
//C# TO JAVA CONVERTER TODO TASK: Extension methods are not available in Java:
//ORIGINAL LINE: public static void TryExecuteInTransaction(this IDbConnection connection, Action<IDbConnection, IDbTransaction> action)
	public static void TryExecuteInTransaction(IDbConnection connection, Action<IDbConnection, IDbTransaction> action)
	{
//C# TO JAVA CONVERTER NOTE: The following 'using' block is replaced by its Java equivalent:
//		using (connection)
		try
		{
			IDbTransaction transaction = null;
			try
			{
				connection.Open();
				transaction = connection.BeginTransaction();
				action(connection, transaction);
				transaction.Commit();
			}
			catch (java.lang.Exception e)
			{
				if (transaction != null)
				{
					transaction.Rollback();
				}
				throw e;
			}
		}
		finally
		{
			connection.dispose();
		}
	}
	/** Try to execute a given func in transaction and auto close the dbconnection after the action complete.
	 
	 @param connection
	 @param func
	 <typeparam name="T"></typeparam>
	 @return 
	*/
//C# TO JAVA CONVERTER TODO TASK: Extension methods are not available in Java:
//ORIGINAL LINE: public static T TryExecuteInTransaction<T>(this IDbConnection connection, Func<IDbConnection, IDbTransaction, T> func)
	public static <T> T TryExecuteInTransaction(IDbConnection connection, Func<IDbConnection, IDbTransaction, T> func)
	{
//C# TO JAVA CONVERTER NOTE: The following 'using' block is replaced by its Java equivalent:
//		using (connection)
		try
		{
			IDbTransaction transaction = null;
			try
			{
				connection.Open();
				transaction = connection.BeginTransaction();
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
				var result = func(connection, transaction);
				transaction.Commit();
				return result;
			}
			catch (java.lang.Exception e)
			{
				if (transaction != null)
				{
					transaction.Rollback();
				}
				throw e;
			}
		}
		finally
		{
			connection.dispose();
		}
	}

	private static java.util.ArrayList<String> GetProperties(Object o)
	{
		if (o instanceof DynamicParameters)
		{
			return ((DynamicParameters)((o instanceof DynamicParameters) ? o : null)).getParameterNames().ToList();
		}

		java.util.ArrayList<String> properties = null;
		RefObject<java.util.ArrayList<String>> tempRef_properties = new RefObject<java.util.ArrayList<String>>(properties);
		boolean tempVar = ParamNameCache.TryGetValue(o.getClass(), tempRef_properties);
			properties = tempRef_properties.argvalue;
		if (tempVar)
		{
			return properties;
		}
//C# TO JAVA CONVERTER TODO TASK: Lambda expressions and anonymous methods are not converted by C# to Java Converter:
		properties = o.getClass().GetProperties(BindingFlags.GetProperty | BindingFlags.Instance | BindingFlags.Public).Select(prop => prop.getName()).ToList();
		ParamNameCache[o.getClass()] = properties;
		return properties;
	}
}