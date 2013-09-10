package ENode.Infrastructure.Dapper;

//
// License: http://www.apache.org/licenses/LICENSE-2.0 
// Home page: http://code.google.com/p/dapper-dot-net/
//
// Note: to build on C# 3.0 + .NET 3.5, include the CSHARP30 compiler symbol (and yes,
// I know the difference between language and runtime versions; this is a compromise).
// 





public class DynamicParameters implements SqlMapper.IDynamicParameters
{
	public static final DbType EnumerableMultiParameter = (DbType)(-1);
	private static java.util.HashMap<SqlMapper.Identity, Action<IDbCommand, Object>> paramReaderCache = new java.util.HashMap<SqlMapper.Identity, Action<IDbCommand, Object>>();

	private java.util.HashMap<String, ParamInfo> parameters = new java.util.HashMap<String, ParamInfo>();
	private java.util.ArrayList<Object> templates;

	private static class ParamInfo
	{
		private String privateName;
		public final String getName()
		{
			return privateName;
		}
		public final void setName(String value)
		{
			privateName = value;
		}
		private Object privateValue;
		public final Object getValue()
		{
			return privateValue;
		}
		public final void setValue(Object value)
		{
			privateValue = value;
		}
		private ParameterDirection privateParameterDirection;
		public final ParameterDirection getParameterDirection()
		{
			return privateParameterDirection;
		}
		public final void setParameterDirection(ParameterDirection value)
		{
			privateParameterDirection = value;
		}
		private DbType privateDbType;
		public final DbType getDbType()
		{
			return privateDbType;
		}
		public final void setDbType(DbType value)
		{
			privateDbType = value;
		}
		private Integer privateSize;
		public final Integer getSize()
		{
			return privateSize;
		}
		public final void setSize(Integer value)
		{
			privateSize = value;
		}
		private IDbDataParameter privateAttachedParam;
		public final IDbDataParameter getAttachedParam()
		{
			return privateAttachedParam;
		}
		public final void setAttachedParam(IDbDataParameter value)
		{
			privateAttachedParam = value;
		}
	}

	/** 
	 construct a dynamic parameter bag
	 
	*/
	public DynamicParameters()
	{
	}

	/** 
	 construct a dynamic parameter bag
	 
	 @param template can be an anonymous type or a DynamicParameters bag
	*/
	public DynamicParameters(Object template)
	{
		AddDynamicParams(template);
	}

	/** 
	 Append a whole object full of params to the dynamic
	 EG: AddDynamicParams(new {A = 1, B = 2}) // will add property A and B to the dynamic
	 
	 @param param
	*/
//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
//#if CSHARP30
	public final void AddDynamicParams(Object param)
//#else
	public final void AddDynamicParams(dynamic param)
//#endif
	{
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
		var obj = (Object)((param instanceof Object) ? param : null);
		if (obj != null)
		{
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
			var subDynamic = (DynamicParameters)((obj instanceof DynamicParameters) ? obj : null);
			if (subDynamic == null)
			{
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
				var dictionary = (Iterable<java.util.Map.Entry<String, Object>>)((obj instanceof Iterable<java.util.Map.Entry<String, Object>>) ? obj : null);
				if (dictionary == null)
				{
					templates = (templates != null) ? templates : new java.util.ArrayList<Object>();
					templates.add(obj);
				}
				else
				{
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
					for (var kvp : dictionary)
					{
//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
//#if CSHARP30
						Add(kvp.getKey(), kvp.getValue(), null, null, null);
//#else
						Add(kvp.getKey(), kvp.getValue());
//#endif
					}
				}
			}
			else
			{
				if (subDynamic.parameters != null)
				{
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
					for (var kvp : subDynamic.parameters)
					{
						parameters.put(kvp.getKey(), kvp.getValue());
					}
				}

				if (subDynamic.templates != null)
				{
					templates = (templates != null) ? templates : new java.util.ArrayList<Object>();
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
					for (var t : subDynamic.templates)
					{
						templates.add(t);
					}
				}
			}
		}
	}

	/** 
	 Add a parameter to this dynamic parameter list
	 
	 @param name
	 @param value
	 @param dbType
	 @param direction
	 @param size
	*/
//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
//#if CSHARP30
	public final void Add(String name, Object value, DbType dbType, ParameterDirection direction, Integer size)
//#else
//C# TO JAVA CONVERTER TODO TASK: C# optional parameters are not converted to Java:
//ORIGINAL LINE: public void Add(string name, object value = null, Nullable<DbType> dbType = null, Nullable<ParameterDirection> direction = null, Nullable<int> size = null)
	public final void Add(String name, Object value, DbType dbType, ParameterDirection direction, Integer size)
//#endif
	{
		ParamInfo tempVar = new ParamInfo();
		tempVar.setName(name);
		tempVar.setValue(value);
		tempVar.setParameterDirection((direction != null) ? direction : ParameterDirection.Input);
		tempVar.setDbType(dbType);
		tempVar.setSize(size);
		parameters.put(Clean(name), tempVar);
	}

	private static String Clean(String name)
	{
		if (!DotNetToJavaStringHelper.isNullOrEmpty(name))
		{
//C# TO JAVA CONVERTER NOTE: The following 'switch' operated on a string member and was converted to Java 'if-else' logic:
//			switch (name[0])
//ORIGINAL LINE: case '@':
			if (name.charAt(0) == '@' || name.charAt(0) == ':' || name.charAt(0) == '?')
			{
					return name.substring(1);
			}
		}
		return name;
	}

	private void AddParameters(IDbCommand command, SqlMapper.Identity identity)
	{
		AddParameters(command, identity);
	}

	/** 
	 Add all the parameters needed to the command just before it executes
	 
	 @param command The raw command prior to execution
	 @param identity Information about the query
	*/
	protected final void AddParameters(IDbCommand command, SqlMapper.Identity identity)
	{
		if (templates != null)
		{
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
			for (var template : templates)
			{
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
				var newIdent = identity.ForDynamicParameters(template.getClass());
				Action<IDbCommand, Object> appender = null;

				synchronized (paramReaderCache)
				{
					if (!((appender = paramReaderCache.get(newIdent)) != null))
					{
						appender = SqlMapper.CreateParamInfoGenerator(newIdent, true);
						paramReaderCache.put(newIdent, appender);
					}
				}

				appender(command, template);
			}
		}

//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
		for (var param : parameters.values())
		{
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
			var dbType = param.DbType;
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
			var val = param.getValue();
			String name = Clean(param.getName());

			if (dbType == null && val != null)
			{
				dbType = SqlMapper.LookupDbType(val.getClass(), name);
			}

			if (dbType == DynamicParameters.EnumerableMultiParameter)
			{
//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
///#pragma warning disable 612, 618
				SqlMapper.PackListParameters(command, name, val);
//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
///#pragma warning restore 612, 618
			}
			else
			{

				boolean add = !command.Parameters.Contains(name);
				IDbDataParameter p;
				if (add)
				{
					p = command.CreateParameter();
					p.ParameterName = name;
				}
				else
				{
					p = (IDbDataParameter)command.Parameters[name];
				}

				p.setValue((val != null) ? val : DBNull.getValue());
				p.Direction = param.ParameterDirection;
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
				var s = (String)((val instanceof String) ? val : null);
				if (s != null)
				{
					if (s.getLength() <= 4000)
					{
						p.Size = 4000;
					}
				}
				if (param.Size != null)
				{
					p.Size = param.Size.getValue();
				}
				if (dbType != null)
				{
					p.DbType = dbType.getValue();
				}
				if (add)
				{
					command.Parameters.Add(p);
				}
				param.AttachedParam = p;
			}

		}
	}

	/** 
	 All the names of the param in the bag, use Get to yank them out
	 
	*/
	public final Iterable<String> getParameterNames()
	{
//C# TO JAVA CONVERTER TODO TASK: Lambda expressions and anonymous methods are not converted by C# to Java Converter:
		return parameters.Select(p => p.getKey());
	}


	/** 
	 Get the value of a parameter
	 
	 <typeparam name="T"></typeparam>
	 @param name
	 @return The value, note DBNull.Value is not returned, instead the value is returned as null
	*/
	public final <T> T Get(String name)
	{
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
		var val = parameters.get(Clean(name)).getAttachedParam().getValue();
		if (val == DBNull.getValue())
		{
			if (null != null)
			{
				throw new ApplicationException("Attempting to cast a DBNull to a non nullable type!");
			}
			return null;
		}
		return (T)val;
	}



}