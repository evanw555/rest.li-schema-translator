package schematranslator.gui;

import com.linkedin.data.avro.DataToAvroSchemaTranslationOptions;
import com.linkedin.data.avro.OptionalDefaultMode;
import com.linkedin.data.avro.SchemaTranslator;
import com.linkedin.data.schema.DataSchema;
import com.linkedin.data.schema.JsonBuilder;
import com.linkedin.data.schema.Name;
import com.linkedin.data.schema.RecordDataSchema;
import com.linkedin.data.schema.SchemaToJsonEncoder;
import com.linkedin.data.schema.SchemaToPdlEncoder;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringWriter;
import javax.swing.*;
import schematranslator.FileUtil;
import schematranslator.SchemaParserUtil;


public class Application implements Runnable {
  private DataSchema dataSchema = new RecordDataSchema(new Name("com.x.y.z.Foo"),
      RecordDataSchema.RecordType.RECORD);

  private JFrame frame;

  private SchemaPanel pegasusPanel;
  private SchemaPanel pdlPanel;
  private SchemaPanel avroPanel;

  private JComboBox<OptionalDefaultMode> avroOptionalDefault;
  private JCheckBox avroOverrideNamespace;

  public Application() {
    frame = new JFrame("Rest.li Schema Translator");
    frame.setLayout(new BorderLayout());

    //Create the menu bar.
    JMenuBar menuBar = new JMenuBar();

    //Build the first menu.
    JMenu fileMenu = new JMenu("File");
    fileMenu.setMnemonic(KeyEvent.VK_F);
    JMenuItem fileOpen = new JMenuItem("Open");
    fileOpen.setMnemonic(KeyEvent.VK_O);
    fileOpen.addActionListener((actionEvent) -> {
      try {
        DataSchema openedDataSchema = FileUtil.openSchemaFile(frame);
        if (openedDataSchema != null) {
          dataSchema = openedDataSchema;
          updatePanels(null);
        }
      } catch (FileNotFoundException e) {
        JOptionPane.showMessageDialog(frame, e.getMessage(), "Cannot open file...", JOptionPane.ERROR_MESSAGE);
      }
    });
    fileMenu.add(fileOpen);
    menuBar.add(fileMenu);
    frame.setJMenuBar(menuBar);

    JPanel panels = new JPanel();
    panels.setLayout(new GridLayout(1, 3));

    pegasusPanel = new SchemaPanel("PDSC", (text) -> {
      try {
        dataSchema = SchemaParserUtil.parsePdsc(text);
        pegasusPanel.setError(false);
        updatePanels(pegasusPanel);
      } catch (Throwable e) {
        pegasusPanel.setError(true);
      }
    });
    panels.add(pegasusPanel);

    avroPanel = new SchemaPanel("Avro", (text) -> {
      try {
        dataSchema = SchemaParserUtil.parseAvro(text);
        avroPanel.setError(false);
        updatePanels(avroPanel);
      } catch (Throwable e) {
        avroPanel.setError(true);
      }
    });
    panels.add(avroPanel);

    pdlPanel = new SchemaPanel("PDL", (text) -> {
      try {
        dataSchema = SchemaParserUtil.parsePdl(text);
        pdlPanel.setError(false);
        updatePanels(pdlPanel);
      } catch (Throwable e) {
        pdlPanel.setError(true);
      }
    });
    panels.add(pdlPanel);

    frame.add(panels, BorderLayout.CENTER);

    JPanel optionsPanel = new JPanel();
    optionsPanel.setBorder(BorderFactory.createTitledBorder("DataToAvroSchemaTranslationOptions"));
    optionsPanel.setLayout(new GridLayout(0, 4, 16, 0));

    avroOptionalDefault = new JComboBox<>(OptionalDefaultMode.values());
    avroOptionalDefault.addActionListener(actionEvent -> updatePanels(null));
    optionsPanel.add(new JLabel("generator.avro.optional.default", SwingConstants.RIGHT));
    optionsPanel.add(avroOptionalDefault);

    avroOverrideNamespace = new JCheckBox();
    avroOverrideNamespace.addItemListener(itemEvent -> updatePanels(null));
    optionsPanel.add(new JLabel("generator.avro.namespace.override", SwingConstants.RIGHT));
    optionsPanel.add(avroOverrideNamespace);

    frame.add(optionsPanel, BorderLayout.SOUTH);

    frame.setMinimumSize(new Dimension(1000, 600));
    frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
  }

  @Override
  public void run() {
    updatePanels(null);
    frame.setVisible(true);
  }

  private void updatePanels(SchemaPanel except) {
    System.out.println("Update panels at " + System.currentTimeMillis());
    try {
      if (except != pegasusPanel) {
        String pegasusText = SchemaToJsonEncoder.schemaToJson(dataSchema, JsonBuilder.Pretty.INDENTED);
        pegasusPanel.setError(false);
        pegasusPanel.setText(pegasusText);
      }

      if (except != pdlPanel) {
        StringWriter stringWriter = new StringWriter();
        SchemaToPdlEncoder schemaToPdlEncoder = new SchemaToPdlEncoder(stringWriter);
        schemaToPdlEncoder.encode(dataSchema);
        String pdlText = stringWriter.toString();
        pdlPanel.setError(false);
        pdlPanel.setText(pdlText);
      }

      if (except != avroPanel) {
        String avroText = SchemaTranslator.dataToAvroSchemaJson(dataSchema,
            new DataToAvroSchemaTranslationOptions(JsonBuilder.Pretty.INDENTED)
                .setOptionalDefaultMode((OptionalDefaultMode) avroOptionalDefault.getSelectedItem())
                .setOverrideNamespace(avroOverrideNamespace.isSelected()));
        avroPanel.setError(false);
        avroPanel.setText(avroText);
      }
    } catch (IOException e) {
      System.err.println(e.getMessage());
    }
  }
}
