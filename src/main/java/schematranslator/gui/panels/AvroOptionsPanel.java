package schematranslator.gui.panels;

import com.linkedin.data.avro.OptionalDefaultMode;
import com.linkedin.data.avro.PegasusToAvroDefaultFieldTranslationMode;
import java.awt.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import javax.swing.*;
import schematranslator.gui.SimpleDocumentListener;


public class AvroOptionsPanel extends JPanel {
  private JComboBox<OptionalDefaultMode> _avroOptionalDefault;
  private JComboBox<PegasusToAvroDefaultFieldTranslationMode> _defaultFieldTranslationMode;
  private JTextField _typerefPropertiesExcludeSet;
  private Set<String> _typerefPropertiesExcludeSetParsed = new HashSet<>();
  private JCheckBox _avroOverrideNamespace;

  private Runnable _onChange;

  public AvroOptionsPanel(Runnable onChange) {
    _onChange = onChange;

    JPanel avroOptionsPanel = new JPanel();
    avroOptionsPanel.setLayout(new GridLayout(0, 2, 16, 0));
    avroOptionsPanel.setBorder(BorderFactory.createTitledBorder("DataToAvroSchemaTranslationOptions"));

    _avroOptionalDefault = new JComboBox<>(OptionalDefaultMode.values());
    _avroOptionalDefault.addActionListener(actionEvent -> _onChange.run());
    avroOptionsPanel.add(new JLabel("generator.avro.optional.default", SwingConstants.RIGHT));
    avroOptionsPanel.add(_avroOptionalDefault);

    _defaultFieldTranslationMode = new JComboBox<>(PegasusToAvroDefaultFieldTranslationMode.values());
    _defaultFieldTranslationMode.addActionListener(actionEvent -> _onChange.run());
    avroOptionsPanel.add(new JLabel("PegasusToAvroDefaultFieldTranslationMode", SwingConstants.RIGHT));
    avroOptionsPanel.add(_defaultFieldTranslationMode);

    _typerefPropertiesExcludeSet = new JTextField();
    _typerefPropertiesExcludeSet.getDocument().addDocumentListener((SimpleDocumentListener) documentEvent -> {
      // Split the comma-separated list of properties now to avoid parsing on each schema panel update
      final String[] propertiesArray = _typerefPropertiesExcludeSet.getText().split(",");
      _typerefPropertiesExcludeSetParsed = Arrays.stream(propertiesArray)
          .map(String::trim)
          .collect(Collectors.toSet());
      _onChange.run();
    });
    avroOptionsPanel.add(new JLabel("generator.avro.typeref.properties.exclude", SwingConstants.RIGHT));
    avroOptionsPanel.add(_typerefPropertiesExcludeSet);

    _avroOverrideNamespace = new JCheckBox();
    _avroOverrideNamespace.addItemListener(itemEvent -> _onChange.run());
    avroOptionsPanel.add(new JLabel("generator.avro.namespace.override", SwingConstants.RIGHT));
    avroOptionsPanel.add(_avroOverrideNamespace);

    JScrollPane avroOptionsScrollPane = new JScrollPane(avroOptionsPanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    this.setLayout(new BorderLayout());
    this.add(avroOptionsScrollPane, BorderLayout.CENTER);
  }

  public OptionalDefaultMode getAvroOptionalDefault() {
    return (OptionalDefaultMode) _avroOptionalDefault.getSelectedItem();
  }

  public PegasusToAvroDefaultFieldTranslationMode getDefaultFieldTranslationMode() {
    return (PegasusToAvroDefaultFieldTranslationMode) _defaultFieldTranslationMode.getSelectedItem();
  }

  public Set<String> getTyperefPropertiesExcludeSet() {
    return _typerefPropertiesExcludeSetParsed;
  }

  public boolean isAvroOverrideNamespace() {
    return _avroOverrideNamespace.isSelected();
  }
}
