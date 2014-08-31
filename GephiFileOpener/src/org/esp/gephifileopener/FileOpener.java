/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.esp.gephifileopener;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import org.gephi.data.attributes.api.AttributeController;
import org.gephi.data.attributes.api.AttributeRow;
import org.gephi.data.attributes.api.AttributeTable;
import org.gephi.graph.api.Node;
import org.gephi.tools.api.EditWindowController;
import org.gephi.tools.spi.NodeClickEventListener;
import org.gephi.tools.spi.Tool;
import org.gephi.tools.spi.ToolEventListener;
import org.gephi.tools.spi.ToolSelectionType;
import org.gephi.tools.spi.ToolUI;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Thomas
 */
@ServiceProvider(service = Tool.class)
public class FileOpener implements Tool {
    private EditWindowController edc;
    private final String column = "cppFile";

    @Override
    public void select() {
        edc=Lookup.getDefault().lookup(EditWindowController.class);
        edc.openEditWindow();
    }

    @Override
    public void unselect() {
        edc.disableEdit();
        edc.closeEditWindow();
    }

    @Override
    public ToolEventListener[] getListeners() {
        return new ToolEventListener[]{new NodeClickEventListener() {

                @Override
                public void clickNodes(Node[] nodes) {
                    if (nodes.length > 0) {
                        edc.editNode(nodes[0]);
                        AttributeTable table = Lookup.getDefault().lookup(AttributeController.class).getModel().getNodeTable();
                        if (table.hasColumn(column)) {
                            AttributeRow row = (AttributeRow) nodes[0].getNodeData().getAttributes();
                            Object value;
                            if ((value = row.getValue(column)) != null) {
                                File cpp = new File(value.toString());
                                editFile(cpp);
                            }
                        }
                    } else {
                        edc.disableEdit();
                    }
                }
            }
        };
    }

    @Override
    public ToolUI getUI() {
        return new ToolUI() {

            @Override
            public JPanel getPropertiesBar(Tool tool) {
                return new JPanel();
            }

            @Override
            public Icon getIcon() {
                return new ImageIcon(getClass().getResource("/org/esp/gephifileopener/fileopener.png"));
            }

            @Override
            public String getName() {
                return NbBundle.getMessage(FileOpener.class, "OpenIDE-Module-Name");
            }

            @Override
            public String getDescription() {
                return NbBundle.getMessage(FileOpener.class, "OpenIDE-Module-Short-Description");
            }

            @Override
            public int getPosition() {
                return 0;
            }
        };
    }

    @Override
    public ToolSelectionType getSelectionType() {
        return ToolSelectionType.SELECTION;
    }
    
  public boolean editFile(final File file) {
    if (!Desktop.isDesktopSupported()) {
      return false;
    }

    Desktop desktop = Desktop.getDesktop();
    if (!desktop.isSupported(Desktop.Action.OPEN)) {
      return false;
    }

    try {
      desktop.open(file);
    } catch (IOException e) {
      // Log an error
      return false;
    }

    return true;
  }
}
