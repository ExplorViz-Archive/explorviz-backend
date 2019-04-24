package net.explorviz.settings.services.mongo;

import net.explorviz.settings.model.UserSetting;
import org.apache.commons.lang3.NotImplementedException;
import org.bson.BsonReader;
import org.bson.BsonType;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

/**
 * Maps an entity to mongo and back
 * 
 * @param <T>
 */
public class UserSettingCodec implements Codec<UserSetting> {

  @Override
  public void encode(BsonWriter writer, UserSetting setting, EncoderContext encoderContext) {
    throw new NotImplementedException("Use morphia");
  }


  @Override
  public Class<UserSetting> getEncoderClass() {
    return UserSetting.class;
  }

  @Override
  public UserSetting decode(BsonReader reader, DecoderContext decoderContext) {
    Object value = null;
    String sid = "";
    String uid = "";


    reader.readStartDocument();


    while (reader.readBsonType() != BsonType.END_OF_DOCUMENT) {
      String fieldName = reader.readName();



      switch (fieldName) {
        case "_id":
          reader.readStartDocument();

          while (reader.readBsonType() != BsonType.END_OF_DOCUMENT) {
            String idFieldName = reader.readName();
            if (idFieldName.equals("settingId")) {
              sid = reader.readString();
            } else if (idFieldName.equals("userId")) {
              uid = reader.readString();
            }
          }
          reader.readEndDocument();
          break;
        case "value":
          switch (reader.getCurrentBsonType()) {
            case BOOLEAN:
              value = reader.readBoolean();
              break;
            case DOUBLE:
              value = reader.readDouble();
              break;
            case STRING:
              value = reader.readString();
              break;
            default:
              break;
          }
          break;
        case "className":
          reader.readString();
          break;
        case "type":
          reader.readString();
          break;
      }
    }
    reader.readEndDocument();

    return new UserSetting(uid, sid, value);
  }

}
