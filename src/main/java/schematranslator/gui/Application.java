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
import java.net.URISyntaxException;
import javax.swing.*;
import schematranslator.AppProperties;
import schematranslator.FileUtil;
import schematranslator.SchemaParserUtil;
import schematranslator.updates.UpdateChecker;
import schematranslator.updates.UpdateInfo;


public class Application implements Runnable {
  public static final String APP_NAME = "Rest.li Schema Translator";

  private DataSchema dataSchema = new RecordDataSchema(new Name("com.x.y.z.Foo"),
      RecordDataSchema.RecordType.RECORD);

  private JFrame frame;
  private JPanel masterPanel;

  private SchemaPanel pegasusPanel, avroPanel, pdlPanel;
  private JCheckBoxMenuItem showPegasus, showAvro, showPdl;

  private JComboBox<OptionalDefaultMode> avroOptionalDefault;
  private JCheckBox avroOverrideNamespace;

  private ErrorMessagePanel _errorMessagePanel;

  public Application() {
    frame = new JFrame(APP_NAME);
    frame.setLayout(new BorderLayout());

    addMenuBar();
    addMasterPanel();
    addLowerPanel();

    frame.setMinimumSize(new Dimension(1000, 600));
    frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
  }

  @Override
  public void run() {
    updatePanels(null);
    frame.setVisible(true);

    // Check for updates
    UpdateChecker updateChecker = new UpdateChecker();
    try {
      UpdateInfo updateInfo = updateChecker.checkForUpdates();
      if (updateInfo.isUpdateAvailable()) {
        frame.add(new UpdateLabel(updateInfo), BorderLayout.NORTH);
        frame.validate();
        frame.repaint();
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void addMenuBar() {
    JMenuBar menuBar = new JMenuBar();

    // Construct file menu
    JMenu fileMenu = new JMenu("File");
    fileMenu.setMnemonic(KeyEvent.VK_F);
    JMenuItem fileOpen = new JMenuItem("Open...");
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
    fileMenu.addSeparator();
    JMenuItem exit = new JMenuItem("Exit");
    exit.setMnemonic(KeyEvent.VK_X);
    exit.addActionListener((actionEvent) -> System.exit(0));
    fileMenu.add(exit);
    menuBar.add(fileMenu);

    // Construct view menu
    JMenu viewMenu = new JMenu("View");
    viewMenu.setMnemonic(KeyEvent.VK_V);
    showPegasus = new JCheckBoxMenuItem("Show PDSC", true);
    showPegasus.setMnemonic(KeyEvent.VK_P);
    showPegasus.addChangeListener((changeEvent) -> updateMasterPanel());
    viewMenu.add(showPegasus);
    showAvro = new JCheckBoxMenuItem("Show Avro", true);
    showAvro.setMnemonic(KeyEvent.VK_A);
    showAvro.addChangeListener((changeEvent) -> updateMasterPanel());
    viewMenu.add(showAvro);
    showPdl = new JCheckBoxMenuItem("Show PDL", true);
    showPdl.setMnemonic(KeyEvent.VK_L);
    showPdl.addChangeListener((changeEvent) -> updateMasterPanel());
    viewMenu.add(showPdl);
    menuBar.add(viewMenu);

    // Construct help menu
    JMenu helpMenu = new JMenu("Help");
    helpMenu.setMnemonic(KeyEvent.VK_H);
    JMenuItem checkUpdates = new JMenuItem("Check for Updates...");
    checkUpdates.setMnemonic(KeyEvent.VK_U);
    checkUpdates.addActionListener((actionEvent) -> {
      UpdateChecker updateChecker = new UpdateChecker();
      try {
        UpdateInfo updateInfo = updateChecker.checkForUpdates();
        JOptionPane.showMessageDialog(frame,
            new UpdateLabel(updateInfo),
            "Check for Updates...",
            JOptionPane.INFORMATION_MESSAGE);
      } catch (Exception e) {
        JOptionPane.showMessageDialog(frame,
            "Unable to check for updates:\n" + e.getMessage(),
            "Check for Updates...",
            JOptionPane.ERROR_MESSAGE);
      }
    });
    helpMenu.add(checkUpdates);
    helpMenu.addSeparator();
    JMenuItem aboutRestLi = new JMenuItem("About Rest.li");
    aboutRestLi.setMnemonic(KeyEvent.VK_A);
    aboutRestLi.addActionListener((actionEvent) -> {
      try {
        if (Desktop.isDesktopSupported()) {
          Desktop desktop = Desktop.getDesktop();
          if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
            desktop.browse(AppProperties.getDocsUri());
          }
        }
      } catch (URISyntaxException | IOException e) {
        System.err.println(e);
      }
    });
    helpMenu.add(aboutRestLi);
    menuBar.add(helpMenu);

    frame.setJMenuBar(menuBar);
  }

  private void addMasterPanel() {
    masterPanel = new JPanel();
    buildPanels();
    updateMasterPanel();
    frame.add(masterPanel, BorderLayout.CENTER);
  }

  private void buildPanels() {
    pegasusPanel = new SchemaPanel("PDSC", (text) -> {
      try {
        dataSchema = SchemaParserUtil.parsePdsc(text);
        _errorMessagePanel.clear();
        pegasusPanel.setError(false);
        updatePanels(pegasusPanel);
      } catch (Throwable e) {
        _errorMessagePanel.setError(e);
        pegasusPanel.setError(true);
      }
    });

    avroPanel = new SchemaPanel("Avro", (text) -> {
      try {
        dataSchema = SchemaParserUtil.parseAvro(text);
        _errorMessagePanel.clear();
        avroPanel.setError(false);
        updatePanels(avroPanel);
      } catch (Throwable e) {
        _errorMessagePanel.setError(e);
        avroPanel.setError(true);
      }
    });

    pdlPanel = new SchemaPanel("PDL", (text) -> {
      try {
        dataSchema = SchemaParserUtil.parsePdl(text);
        _errorMessagePanel.clear();
        pdlPanel.setError(false);
        updatePanels(pdlPanel);
      } catch (Throwable e) {
        _errorMessagePanel.setError(e);
        pdlPanel.setError(true);
      }
    });
  }

  private void addLowerPanel() {
    JTabbedPane tabbedPane = new JTabbedPane();

    _errorMessagePanel = new ErrorMessagePanel();
    tabbedPane.addTab("Errors", _errorMessagePanel);

    JPanel optionsPanel = new JPanel();
    optionsPanel.setBorder(BorderFactory.createTitledBorder("DataToAvroSchemaTranslationOptions"));
    optionsPanel.setLayout(new GridLayout(0, 2, 16, 0));

    avroOptionalDefault = new JComboBox<>(OptionalDefaultMode.values());
    avroOptionalDefault.addActionListener(actionEvent -> updatePanels(null));
    optionsPanel.add(new JLabel("generator.avro.optional.default", SwingConstants.RIGHT));
    optionsPanel.add(avroOptionalDefault);

    avroOverrideNamespace = new JCheckBox();
    avroOverrideNamespace.addItemListener(itemEvent -> updatePanels(null));
    optionsPanel.add(new JLabel("generator.avro.namespace.override", SwingConstants.RIGHT));
    optionsPanel.add(avroOverrideNamespace);
    tabbedPane.addTab("Avro Options", optionsPanel);

    frame.add(tabbedPane, BorderLayout.SOUTH);
  }

  private void updateMasterPanel() {
    final int numPanels = (showPegasus.getState() ? 1 : 0) + (showAvro.getState() ? 1 : 0) + (showPdl.getState() ? 1 : 0);
    masterPanel.setLayout(new GridLayout(1, numPanels));
    masterPanel.removeAll();

    if (showPegasus.getState()) {
      masterPanel.add(pegasusPanel);
    }

    if (showAvro.getState()) {
      masterPanel.add(avroPanel);
    }

    if (showPdl.getState()) {
      masterPanel.add(pdlPanel);
    }

    masterPanel.validate();
    masterPanel.repaint();
  }

  private void updatePanels(SchemaPanel except) {
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
