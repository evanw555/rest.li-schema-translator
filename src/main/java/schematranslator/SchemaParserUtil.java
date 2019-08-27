package schematranslator;

import com.linkedin.data.avro.SchemaTranslator;
import com.linkedin.data.schema.AbstractSchemaParser;
import com.linkedin.data.schema.DataSchema;
import com.linkedin.data.schema.SchemaParser;
import com.linkedin.data.schema.grammar.PdlSchemaParser;
import com.linkedin.data.schema.resolver.DefaultDataSchemaResolver;
import java.util.List;


public class SchemaParserUtil {
  public static DataSchema parsePdsc(String text) {
    SchemaParser parser = new SchemaParser();
    parser.parse(text);
    return getDataSchema(parser);
  }

  public static DataSchema parsePdl(String text) {
    PdlSchemaParser parser = new PdlSchemaParser(new DefaultDataSchemaResolver());
    parser.parse(text);
    return getDataSchema(parser);
  }

  public static DataSchema parseAvro(String text) {
    return SchemaTranslator.avroToDataSchema(text);
  }

  /**
   * Given an {@link AbstractSchemaParser}, return the top-level data schema in the parser's results, if there are
   * no errors.
   * @param schemaParser schema parser which has completed parsing
   * @return one top-level data schema
   * @throws IllegalStateException if there were errors parsing, or if the amount of top-level data schemas is not 1
   */
  private static DataSchema getDataSchema(AbstractSchemaParser schemaParser) {
    if (schemaParser.hasError()) {
      throw new IllegalStateException(schemaParser.errorMessage());
    }

    final List<DataSchema> topLevelDataSchemas = schemaParser.topLevelDataSchemas();

    if (topLevelDataSchemas == null || topLevelDataSchemas.isEmpty()) {
      throw new IllegalStateException("No data schemas were successfully parsed.");
    }

    if (topLevelDataSchemas.size() > 1) {
      throw new IllegalStateException("Multiple top-level data schemas were parsed.");
    }

    return topLevelDataSchemas.get(0);
  }
}
