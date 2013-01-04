/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.gephi.plugins.layout.noverlap;

import javax.swing.Icon;
import javax.swing.JPanel;
import org.gephi.layout.spi.Layout;
import org.gephi.layout.spi.LayoutBuilder;
import org.gephi.layout.spi.LayoutUI;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Mathieu Jacomy
 */
@ServiceProvider(service = LayoutBuilder.class)
public class NoverlapLayoutBuilder implements LayoutBuilder {
    private NoverlapLayoutUI ui = new NoverlapLayoutUI();

    public String getName() {
       return NbBundle.getMessage(NoverlapLayoutBuilder.class, "name");
    }

    public LayoutUI getUI() {
        return ui;
    }

    public Layout buildLayout() {
        return new NoverlapLayout(this);
    }

    private static class NoverlapLayoutUI implements LayoutUI {

        public String getDescription() {
            return NbBundle.getMessage(NoverlapLayoutUI.class, "OpenIDE-Module-Long-Description");
        }

        public Icon getIcon() {
            return null;
        }

        public JPanel getSimplePanel(Layout layout) {
            return null;
        }

        public int getQualityRank() {
            return -1;
        }

        public int getSpeedRank() {
            return -1;
        }
    }
}
