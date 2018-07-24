package in.dailyhunt.personalization;

import com.orbitz.fasterxml.jackson.annotation.JsonProperty;
import com.orbitz.fasterxml.jackson.annotation.JsonValue;
import java.util.List;
import java.util.Map;

public class NestedConfig {

  List<String> languages;

  Map<String, String> langModel;

  BuzzConfig buzzConfig;

}
