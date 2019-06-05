package schematranslator;

import com.linkedin.data.avro.SchemaTranslator;
import com.linkedin.data.schema.DataSchema;
import com.linkedin.data.schema.SchemaParser;
import com.linkedin.data.schema.grammar.PdlSchemaParser;
import com.linkedin.data.schema.resolver.DefaultDataSchemaResolver;


public class SchemaParserUtil {
  public static DataSchema parsePdsc(String text) {
    SchemaParser parser = new SchemaParser();
    parser.parse(text);
    return parser.topLevelDataSchemas().get(0);
  }

  public static DataSchema parsePdl(String text) {
    PdlSchemaParser parser = new PdlSchemaParser(new DefaultDataSchemaResolver());
    parser.parse(text);
    return parser.topLevelDataSchemas().get(0);
  }

  public static DataSchema parseAvro(String text) {
    return SchemaTranslator.avroToDataSchema(text);
  }
}
