/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.esp.gephifileopener;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
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
import org.openide.windows.WindowManager;

/**
 *
 * @author Thomas
 */
@ServiceProvider(service = Tool.class)
public class nodeSelector implements Tool {
    private EditWindowController edc;
    private editCPPTopComponent ectc;

    @Override
    public void select() {
        edc=Lookup.getDefault().lookup(EditWindowController.class);
        edc.openEditWindow();
        ectc = findInstance();
    }

    private editCPPTopComponent findInstance(){
        return (editCPPTopComponent) WindowManager.getDefault().findTopComponent("editCPPTopComponent");
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
                        ectc.editNode(nodes[0]);
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
                return new ImageIcon(getClass().getResource("/org/esp/gephifileopener/page_white_cplusplus.png"));
            }

            @Override
            public String getName() {
                return NbBundle.getMessage(editCPPTopComponent.class, "OpenIDE-Module-Name");
            }

            @Override
            public String getDescription() {
                return NbBundle.getMessage(editCPPTopComponent.class, "OpenIDE-Module-Short-Description");
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
}
