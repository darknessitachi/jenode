package ENode.Infrastructure.Dapper;

//
// License: http://www.apache.org/licenses/LICENSE-2.0 
// Home page: http://code.google.com/p/dapper-dot-net/
//
// Note: to build on C# 3.0 + .NET 3.5, include the CSHARP30 compiler symbol (and yes,
// I know the difference between language and runtime versions; this is a compromise).
// 





public final class CustomPropertyTypeMap implements SqlMapper.ITypeMap
{
	private java.lang.Class _type;
	private Func<java.lang.Class, String, PropertyInfo> _propertySelector;

	/** 
	 Creates custom property mapping
	 
	 @param type Target entity type
	 @param propertySelector Property selector based on target type and DataReader column name
	*/
	public CustomPropertyTypeMap(java.lang.Class type, Func<java.lang.Class, String, PropertyInfo> propertySelector)
	{
		if (type == null)
		{
			throw new ArgumentNullException("type");
		}

		if (propertySelector == null)
		{
			throw new ArgumentNullException("propertySelector");
		}

		_type = type;
		_propertySelector = propertySelector;
	}

	/** 
	 Always returns default constructor
	 
	 @param names DataReader column names
	 @param types DataReader column types
	 @return Default constructor
	*/
	public java.lang.reflect.Constructor FindConstructor(String[] names, java.lang.Class[] types)
	{
		return _type.getConstructor(new java.lang.Class[0]);
	}

	/** 
	 Not impelmeneted as far as default constructor used for all cases
	 
	 @param constructor
	 @param columnName
	 @return 
	*/
	public SqlMapper.IMemberMap GetConstructorParameter(java.lang.reflect.Constructor constructor, String columnName)
	{
		throw new NotSupportedException();
	}

	/** 
	 Returns property based on selector strategy
	 
	 @param columnName DataReader column name
	 @return Poperty member map
	*/
	public SqlMapper.IMemberMap GetMember(String columnName)
	{
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
		var prop = _propertySelector(_type, columnName);
		return prop != null ? new SimpleMemberMap(columnName, prop) : null;
	}



}