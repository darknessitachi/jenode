package ENode.Infrastructure.Dapper;

//
// License: http://www.apache.org/licenses/LICENSE-2.0 
// Home page: http://code.google.com/p/dapper-dot-net/
//
// Note: to build on C# 3.0 + .NET 3.5, include the CSHARP30 compiler symbol (and yes,
// I know the difference between language and runtime versions; this is a compromise).
// 





public final class SimpleMemberMap implements SqlMapper.IMemberMap
{
	private String _columnName;
	private PropertyInfo _property;
	private java.lang.reflect.Field _field;
	private ParameterInfo _parameter;

	/** 
	 Creates instance for simple property mapping
	 
	 @param columnName DataReader column name
	 @param property Target property
	*/
	public SimpleMemberMap(String columnName, PropertyInfo property)
	{
		if (columnName == null)
		{
			throw new ArgumentNullException("columnName");
		}

		if (property == null)
		{
			throw new ArgumentNullException("property");
		}

		_columnName = columnName;
		_property = property;
	}

	/** 
	 Creates instance for simple field mapping
	 
	 @param columnName DataReader column name
	 @param field Target property
	*/
	public SimpleMemberMap(String columnName, java.lang.reflect.Field field)
	{
		if (columnName == null)
		{
			throw new ArgumentNullException("columnName");
		}

		if (field == null)
		{
			throw new ArgumentNullException("field");
		}

		_columnName = columnName;
		_field = field;
	}

	/** 
	 Creates instance for simple constructor parameter mapping
	 
	 @param columnName DataReader column name
	 @param parameter Target constructor parameter
	*/
	public SimpleMemberMap(String columnName, ParameterInfo parameter)
	{
		if (columnName == null)
		{
			throw new ArgumentNullException("columnName");
		}

		if (parameter == null)
		{
			throw new ArgumentNullException("parameter");
		}

		_columnName = columnName;
		_parameter = parameter;
	}

	/** 
	 DataReader column name
	 
	*/
	public String getColumnName()
	{
		return _columnName;
	}

	/** 
	 Target member type
	 
	*/
	public java.lang.Class getMemberType()
	{
		if (_field != null)
		{
			return _field.FieldType;
		}

		if (_property != null)
		{
			return _property.PropertyType;
		}

		if (_parameter != null)
		{
			return _parameter.ParameterType;
		}

		return null;
	}

	/** 
	 Target property
	 
	*/
	public PropertyInfo getProperty()
	{
		return _property;
	}

	/** 
	 Target field
	 
	*/
	public java.lang.reflect.Field getField()
	{
		return _field;
	}

	/** 
	 Target constructor parameter
	 
	*/
	public ParameterInfo getParameter()
	{
		return _parameter;
	}



}