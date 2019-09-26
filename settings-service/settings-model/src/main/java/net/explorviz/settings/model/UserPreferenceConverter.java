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
  public Object decode(final Class<?> targetClass, final Object fromDbObject,
      final MappedField optionalExtraInfo) {

    final BasicDBObject basicDbO = (BasicDBObject) fromDbObject;
    final String id = basicDbO.getString("_id");

    final String uId = basicDbO.getString("userId");
    final String sId = basicDbO.getString("settingId");

    final Object value = basicDbO.get("value");

    return new UserPreference(id, uId, sId, value);
  }



}
