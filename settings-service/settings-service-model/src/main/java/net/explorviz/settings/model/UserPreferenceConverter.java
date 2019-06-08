package net.explorviz.settings.model;

import com.mongodb.BasicDBObject;
import xyz.morphia.converters.SimpleValueConverter;
import xyz.morphia.converters.TypeConverter;
import xyz.morphia.mapping.MappedField;


public class UserPreferenceConverter extends TypeConverter implements SimpleValueConverter {


  public UserPreferenceConverter() {
    super(UserPreference.class);
  }

  @Override
  public Object decode(final Class<?> targetClass, final Object fromDBObject,
      final MappedField optionalExtraInfo) {

    final BasicDBObject basicDBO = (BasicDBObject) fromDBObject;
    final String id = basicDBO.getString("_id");

    final String uId = basicDBO.getString("userId");
    final String sId = basicDBO.getString("settingId");

    final Object value = basicDBO.get("value");

    return new UserPreference(id, uId, sId, value);
  }



}
