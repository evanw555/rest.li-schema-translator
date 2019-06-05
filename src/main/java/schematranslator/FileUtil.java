package schematranslator;

import com.linkedin.data.schema.DataSchema;
import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;


public class FileUtil {
  private final static String PDSC = "pdsc";
  private final static String PDL = "pdl";
  private final static String AVSC = "avsc";
  private final static String[] SCHEMA_FORMATS = new String[] { PDSC, PDL, AVSC };

  public static DataSchema openSchemaFile(Component parentComponent) throws FileNotFoundException {
    JFileChooser fileChooser = new JFileChooser();
    fileChooser.setFileFilter(new FileNameExtensionFilter("Schema Files", SCHEMA_FORMATS));
    fileChooser.setMultiSelectionEnabled(false);
    int status = fileChooser.showDialog(parentComponent, "Open");
    if (status != JFileChooser.APPROVE_OPTION) {
      return null;
    }
    File file = fileChooser.getSelectedFile();

    StringBuilder sb = new StringBuilder();
    try {
      FileReader fileReader = new FileReader(file);
      BufferedReader bufferedReader = new BufferedReader(fileReader);
      String text;
      while ((text = bufferedReader.readLine()) != null) {
        sb.append(text).append("\n");
      }
    } catch (IOException e) {
      return null;
    }
    String schemaText = sb.toString();
    String fileName = file.getName();
    String fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();

    switch (fileExtension) {
      case PDSC:
        return SchemaParserUtil.parsePdsc(schemaText);
      case PDL:
        return SchemaParserUtil.parsePdl(schemaText);
      case AVSC:
        return SchemaParserUtil.parseAvro(schemaText);
      default:
        throw new FileNotFoundException("Invalid schema file extension: " + fileExtension);
    }
  }
}
