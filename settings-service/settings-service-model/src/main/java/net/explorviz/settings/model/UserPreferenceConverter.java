package net.explorviz.settings.model;

import com.mongodb.BasicDBObject;
import org.mongodb.morphia.converters.SimpleValueConverter;
import org.mongodb.morphia.converters.TypeConverter;
import org.mongodb.morphia.mapping.MappedField;


public class UserPreferenceConverter extends TypeConverter implements SimpleValueConverter {


  public UserPreferenceConverter() {
    super(UserPreference.class);
  }

  @Override
  public Object decode(final Class<?> targetClass, final Object fromDBObject,
      final MappedField optionalExtraInfo) {

    final BasicDBObject basicDBO = (BasicDBObject) fromDBObject;
    final BasicDBObject idDBO = (BasicDBObject) basicDBO.get("_id");

    final String uId = idDBO.getString("userId");
    final String sId = idDBO.getString("settingId");

    final Object value = basicDBO.get("value");

    return new UserPreference(uId, sId, value);


  }



}
