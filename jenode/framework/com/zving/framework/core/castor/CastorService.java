package com.zving.framework.core.castor;

import com.zving.framework.core.exception.CastorNotFoundException;
import com.zving.framework.extend.AbstractExtendService;
import com.zving.framework.extend.IExtendItem;
import com.zving.framework.utility.ObjectUtil;
import java.util.Date;

public class CastorService extends AbstractExtendService<ICastor>
{
  private boolean innerCastorLoaded = false;

  public static CastorService getInstance()
  {
    return (CastorService)AbstractExtendService.findInstance(CastorService.class);
  }

  public static Object toType(Object obj, Class<?> type) {
    if (obj == null) {
      if (ObjectUtil.in(new Object[] { type, Integer.class, Integer.TYPE, Long.class, Long.TYPE, Float.class, Float.TYPE, Double.class, Double.TYPE })) {
        return Integer.valueOf(0);
      }
      return obj;
    }
    if (type.isInstance(obj)) {
      return obj;
    }
    if (type == String.class) {
      return obj.toString();
    }
    if ((type == Integer.class) || (type == Integer.TYPE))
      return IntCastor.getInstance().cast(obj, type);
    if ((type == Long.class) || (type == Long.TYPE))
      return LongCastor.getInstance().cast(obj, type);
    if ((type == Float.class) || (type == Float.TYPE))
      return FloatCastor.getInstance().cast(obj, type);
    if ((type == Double.class) || (type == Double.TYPE))
      return DoubleCastor.getInstance().cast(obj, type);
    if ((type == Boolean.class) || (type == Boolean.TYPE))
      return BooleanCastor.getInstance().cast(obj, type);
    if (type == Date.class)
      return DateCastor.getInstance().cast(obj, type);
    if (type == [Ljava.lang.String.class)
      return StringArrayCastor.getInstance().cast(obj, type);
    if ((type == [I.class) || (type == [Ljava.lang.Integer.class))
      return StringArrayCastor.getInstance().cast(obj, type);
    if ((type == [J.class) || (type == [Ljava.lang.Long.class))
      return LongArrayCastor.getInstance().cast(obj, type);
    if ((type == [F.class) || (type == [Ljava.lang.Float.class))
      return FloatArrayCastor.getInstance().cast(obj, type);
    if ((type == [D.class) || (type == [Ljava.lang.Double.class))
      return DoubleArrayCastor.getInstance().cast(obj, type);
    if ((type == [Z.class) || (type == [Ljava.lang.Boolean.class)) {
      return BooleanArrayCastor.getInstance().cast(obj, type);
    }
    for (ICastor castor : getInstance().getAll()) {
      if (castor.canCast(type)) {
        return castor.cast(obj, type);
      }
    }

    throw new CastorNotFoundException(type.getClass().getName());
  }

  private void init()
  {
    if (!this.innerCastorLoaded) {
      register(IntCastor.getInstance());
      register(LongCastor.getInstance());
      register(FloatCastor.getInstance());
      register(DoubleCastor.getInstance());
      register(DateCastor.getInstance());
      register(IntArrayCastor.getInstance());
      register(LongArrayCastor.getInstance());
      register(FloatArrayCastor.getInstance());
      register(DoubleArrayCastor.getInstance());
      register(BooleanCastor.getInstance());
      register(BooleanArrayCastor.getInstance());
      register(StringArrayCastor.getInstance());
      register(GenricArrayCastor.getInstance());
      this.innerCastorLoaded = true;
    }
  }

  public void register(IExtendItem item) {
    init();
    super.register(item);
  }
}