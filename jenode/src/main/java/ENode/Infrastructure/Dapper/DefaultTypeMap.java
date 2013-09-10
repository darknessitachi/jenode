package ENode.Infrastructure.Dapper;

//
// License: http://www.apache.org/licenses/LICENSE-2.0 
// Home page: http://code.google.com/p/dapper-dot-net/
//
// Note: to build on C# 3.0 + .NET 3.5, include the CSHARP30 compiler symbol (and yes,
// I know the difference between language and runtime versions; this is a compromise).
// 





public final class DefaultTypeMap implements SqlMapper.ITypeMap
{
	private java.util.ArrayList<java.lang.reflect.Field> _fields;
	private java.util.ArrayList<PropertyInfo> _properties;
	private java.lang.Class _type;

	/** 
	 Creates default type map
	 
	 @param type Entity type
	*/
	public DefaultTypeMap(java.lang.Class type)
	{
		if (type == null)
		{
			throw new ArgumentNullException("type");
		}

		_fields = GetSettableFields(type);
		_properties = GetSettableProps(type);
		_type = type;
	}

	public static java.lang.reflect.Method GetPropertySetter(PropertyInfo propertyInfo, java.lang.Class type)
	{
		return propertyInfo.DeclaringType == type ? propertyInfo.GetSetMethod(true) : propertyInfo.DeclaringType.GetProperty(propertyInfo.getName(), BindingFlags.Public | BindingFlags.NonPublic | BindingFlags.Instance).GetSetMethod(true);
	}

	public static java.util.ArrayList<PropertyInfo> GetSettableProps(java.lang.Class t)
	{
//C# TO JAVA CONVERTER TODO TASK: There is no Java equivalent to LINQ queries:
//C# TO JAVA CONVERTER TODO TASK: Lambda expressions and anonymous methods are not converted by C# to Java Converter:
		return t.GetProperties(BindingFlags.Public | BindingFlags.NonPublic | BindingFlags.Instance).Where(p => GetPropertySetter(p, t) != null).ToList();
	}

	public static java.util.ArrayList<java.lang.reflect.Field> GetSettableFields(java.lang.Class t)
	{
		return t.getFields(BindingFlags.Public | BindingFlags.NonPublic | BindingFlags.Instance).ToList();
	}

	/** 
	 Finds best constructor
	 
	 @param names DataReader column names
	 @param types DataReader column types
	 @return Matching constructor or default one
	*/
	public java.lang.reflect.Constructor FindConstructor(String[] names, java.lang.Class[] types)
	{
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
		var constructors = _type.getConstructors(BindingFlags.Instance | BindingFlags.Public | BindingFlags.NonPublic);
//C# TO JAVA CONVERTER TODO TASK: There is no Java equivalent to LINQ queries:
//C# TO JAVA CONVERTER TODO TASK: Lambda expressions and anonymous methods are not converted by C# to Java Converter:
		for (java.lang.reflect.Constructor ctor : constructors.OrderBy(c => c.IsPublic ? 0 : (c.IsPrivate ? 2 : 1)).ThenBy(c => c.GetParameters().getLength()))
		{
			ParameterInfo[] ctorParameters = ctor.GetParameters();
			if (ctorParameters.length == 0)
			{
				return ctor;
			}

			if (ctorParameters.length != types.length)
			{
				continue;
			}

			int i = 0;
			for (; i < ctorParameters.length; i++)
			{
				if (!String.equals(ctorParameters[i].getName(), names[i], StringComparison.OrdinalIgnoreCase))
				{
					break;
				}
				if (types[i] == byte[].class && SqlMapper.LinqBinary.equals(ctorParameters[i].ParameterType.FullName))
				{
					continue;
				}
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
				var unboxedType = ((Nullable.GetUnderlyingType(ctorParameters[i].ParameterType)) != null) ? Nullable.GetUnderlyingType(ctorParameters[i].ParameterType) : ctorParameters[i].ParameterType;
				if (unboxedType != types[i] && !(unboxedType.IsEnum && Enum.GetUnderlyingType(unboxedType) == types[i]) && !(unboxedType == Character.class && types[i] == String.class))
				{
					break;
				}
			}

			if (i == ctorParameters.length)
			{
				return ctor;
			}
		}

		return null;
	}

	/** 
	 Gets mapping for constructor parameter
	 
	 @param constructor Constructor to resolve
	 @param columnName DataReader column name
	 @return Mapping implementation
	*/
	public SqlMapper.IMemberMap GetConstructorParameter(java.lang.reflect.Constructor constructor, String columnName)
	{
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
		var parameters = constructor.GetParameters();

//C# TO JAVA CONVERTER TODO TASK: Lambda expressions and anonymous methods are not converted by C# to Java Converter:
		return new SimpleMemberMap(columnName, parameters.FirstOrDefault(p => String.equals(p.getName(), columnName, StringComparison.OrdinalIgnoreCase)));
	}

	/** 
	 Gets member mapping for column
	 
	 @param columnName DataReader column name
	 @return Mapping implementation
	*/
	public SqlMapper.IMemberMap GetMember(String columnName)
	{
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
//C# TO JAVA CONVERTER TODO TASK: Lambda expressions and anonymous methods are not converted by C# to Java Converter:
		var property = ((_properties.FirstOrDefault(p => String.equals(p.getName(), columnName, StringComparison.Ordinal))) != null) ? _properties.FirstOrDefault(p => String.equals(p.getName(), columnName, StringComparison.Ordinal)) : _properties.FirstOrDefault(p => String.equals(p.getName(), columnName, StringComparison.OrdinalIgnoreCase));

		if (property != null)
		{
			return new SimpleMemberMap(columnName, property);
		}

//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
//C# TO JAVA CONVERTER TODO TASK: Lambda expressions and anonymous methods are not converted by C# to Java Converter:
		var field = ((_fields.FirstOrDefault(p => String.equals(p.getName(), columnName, StringComparison.Ordinal))) != null) ? _fields.FirstOrDefault(p => String.equals(p.getName(), columnName, StringComparison.Ordinal)) : _fields.FirstOrDefault(p => String.equals(p.getName(), columnName, StringComparison.OrdinalIgnoreCase));

		if (field != null)
		{
			return new SimpleMemberMap(columnName, field);
		}

		return null;
	}



}