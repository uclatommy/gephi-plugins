/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.esp.gephifileopener;

import java.awt.Desktop;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import jsyntaxpane.DefaultSyntaxKit;
import org.gephi.data.attributes.api.AttributeController;
import org.gephi.data.attributes.api.AttributeRow;
import org.gephi.data.attributes.api.AttributeTable;
import org.gephi.graph.api.Edge;
import org.gephi.graph.api.Graph;
import org.gephi.graph.api.GraphController;
import org.gephi.graph.api.Node;
import org.gephi.visualization.VizController;
import org.netbeans.api.settings.ConvertAsProperties;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.util.Exceptions;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
import org.openide.util.NbBundle.Messages;
import org.openide.windows.TopComponent;

/**
 * Top component which displays something.
 */
@ConvertAsProperties(
dtd = "-//org.esp.gephifileopener//editCPP//EN",
autostore = false)
@TopComponent.Description(
preferredID = "editCPPTopComponent",
persistenceType = TopComponent.PERSISTENCE_ALWAYS)
@TopComponent.Registration(mode = "output", openAtStartup = false)
@ActionID(category = "Window", id = "org.esp.gephifileopener.editCPPTopComponent")
@ActionReference(path = "Menu/Window" /*, position = 333 */)
@TopComponent.OpenActionRegistration(
displayName = "#CTL_editCPPAction",
preferredID = "editCPPTopComponent")
@Messages({
    "CTL_editCPPAction=Code Editor",
    "CTL_editCPPTopComponent=Code Editor Window",
    "HINT_editCPPTopComponent=This is a code editor window"
})
public final class editCPPTopComponent extends TopComponent {
    final private String ICON_PATH = "/org/esp/gephifileopener/page_white_cplusplus.png";
    private Node currentRootNode;
    private final Graph graph = Lookup.getDefault().lookup(GraphController.class).getModel().getGraph();
    private mosesController mc = new mosesController("");
    public editCPPTopComponent() {
        initComponents();
        setName(Bundle.CTL_editCPPTopComponent());
        setToolTipText(Bundle.HINT_editCPPTopComponent());
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        codePane = new javax.swing.JEditorPane();
        jToolBar1 = new javax.swing.JToolBar();
        saveButton = new javax.swing.JButton();
        refreshButton = new javax.swing.JButton();
        filenameField = new javax.swing.JTextField();
        jToolBar2 = new javax.swing.JToolBar();
        contSaveToggle = new javax.swing.JToggleButton();
        centerOnNode = new javax.swing.JButton();
        jSeparator3 = new javax.swing.JToolBar.Separator();
        commitButton = new javax.swing.JButton();
        updateButton = new javax.swing.JButton();
        revertButton = new javax.swing.JButton();
        repoLogButton = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JToolBar.Separator();
        diffButton = new javax.swing.JButton();
        repoBrowseButton = new javax.swing.JButton();
        jSeparator2 = new javax.swing.JToolBar.Separator();
        openButton = new javax.swing.JButton();
        parentDirButton = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        neighborNodesList = new javax.swing.JList();

        setIcon(ImageUtilities.loadImage(ICON_PATH, true));
        setName("codePanel"); // NOI18N

        codePane.setToolTipText(org.openide.util.NbBundle.getMessage(editCPPTopComponent.class, "editCPPTopComponent.codePane.toolTipText")); // NOI18N
        codePane.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                codePaneFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                codePaneFocusLost(evt);
            }
        });
        jScrollPane1.setViewportView(codePane);

        jToolBar1.setFloatable(false);
        jToolBar1.setRollover(true);

        saveButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/esp/gephifileopener/saveProject.png"))); // NOI18N
        saveButton.setToolTipText(org.openide.util.NbBundle.getMessage(editCPPTopComponent.class, "editCPPTopComponent.saveButton.toolTipText")); // NOI18N
        saveButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                saveButtonMouseClicked(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                saveButtonMouseExited(evt);
            }
        });
        jToolBar1.add(saveButton);

        refreshButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/esp/gephifileopener/refresh.png"))); // NOI18N
        refreshButton.setToolTipText(org.openide.util.NbBundle.getMessage(editCPPTopComponent.class, "editCPPTopComponent.refreshButton.toolTipText")); // NOI18N
        refreshButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                refreshButtonMouseClicked(evt);
            }
        });
        jToolBar1.add(refreshButton);

        filenameField.setText(org.openide.util.NbBundle.getMessage(editCPPTopComponent.class, "editCPPTopComponent.filenameField.text")); // NOI18N
        filenameField.setToolTipText(org.openide.util.NbBundle.getMessage(editCPPTopComponent.class, "editCPPTopComponent.filenameField.toolTipText")); // NOI18N
        jToolBar1.add(filenameField);

        jToolBar2.setFloatable(false);
        jToolBar2.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jToolBar2.setRollover(true);
        jToolBar2.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                jToolBar2FocusLost(evt);
            }
        });

        contSaveToggle.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/esp/gephifileopener/film_save.png"))); // NOI18N
        contSaveToggle.setToolTipText(org.openide.util.NbBundle.getMessage(editCPPTopComponent.class, "editCPPTopComponent.contSaveToggle.toolTipText")); // NOI18N
        contSaveToggle.setFocusable(false);
        contSaveToggle.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        contSaveToggle.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar2.add(contSaveToggle);

        centerOnNode.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/esp/gephifileopener/centerOnZero.png"))); // NOI18N
        centerOnNode.setToolTipText(org.openide.util.NbBundle.getMessage(editCPPTopComponent.class, "editCPPTopComponent.centerOnNode.toolTipText")); // NOI18N
        centerOnNode.setFocusable(false);
        centerOnNode.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        centerOnNode.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        centerOnNode.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                centerOnNodeMouseClicked(evt);
            }
        });
        jToolBar2.add(centerOnNode);
        jToolBar2.add(jSeparator3);

        commitButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/esp/gephifileopener/accept.png"))); // NOI18N
        commitButton.setToolTipText(org.openide.util.NbBundle.getMessage(editCPPTopComponent.class, "editCPPTopComponent.commitButton.toolTipText")); // NOI18N
        commitButton.setFocusable(false);
        commitButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        commitButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        commitButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                commitButtonMouseClicked(evt);
            }
        });
        jToolBar2.add(commitButton);

        updateButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/esp/gephifileopener/basket_put.png"))); // NOI18N
        updateButton.setToolTipText(org.openide.util.NbBundle.getMessage(editCPPTopComponent.class, "editCPPTopComponent.updateButton.toolTipText")); // NOI18N
        updateButton.setFocusable(false);
        updateButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        updateButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        updateButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                updateButtonMouseClicked(evt);
            }
        });
        jToolBar2.add(updateButton);

        revertButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/esp/gephifileopener/bomb.png"))); // NOI18N
        revertButton.setToolTipText(org.openide.util.NbBundle.getMessage(editCPPTopComponent.class, "editCPPTopComponent.revertButton.toolTipText")); // NOI18N
        revertButton.setFocusable(false);
        revertButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        revertButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        revertButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                revertButtonMouseClicked(evt);
            }
        });
        jToolBar2.add(revertButton);

        repoLogButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/esp/gephifileopener/comments.png"))); // NOI18N
        repoLogButton.setToolTipText(org.openide.util.NbBundle.getMessage(editCPPTopComponent.class, "editCPPTopComponent.repoLogButton.toolTipText")); // NOI18N
        repoLogButton.setFocusable(false);
        repoLogButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        repoLogButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        repoLogButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                repoLogButtonMouseClicked(evt);
            }
        });
        jToolBar2.add(repoLogButton);
        jToolBar2.add(jSeparator1);

        diffButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/esp/gephifileopener/table_row_insert.png"))); // NOI18N
        diffButton.setToolTipText(org.openide.util.NbBundle.getMessage(editCPPTopComponent.class, "editCPPTopComponent.diffButton.toolTipText")); // NOI18N
        diffButton.setFocusable(false);
        diffButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        diffButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        diffButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                diffButtonMouseClicked(evt);
            }
        });
        jToolBar2.add(diffButton);

        repoBrowseButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/esp/gephifileopener/application_side_tree.png"))); // NOI18N
        repoBrowseButton.setToolTipText(org.openide.util.NbBundle.getMessage(editCPPTopComponent.class, "editCPPTopComponent.repoBrowseButton.toolTipText")); // NOI18N
        repoBrowseButton.setFocusable(false);
        repoBrowseButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        repoBrowseButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        repoBrowseButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                repoBrowseButtonMouseClicked(evt);
            }
        });
        jToolBar2.add(repoBrowseButton);
        jToolBar2.add(jSeparator2);

        openButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/esp/gephifileopener/application_get.png"))); // NOI18N
        openButton.setToolTipText(org.openide.util.NbBundle.getMessage(editCPPTopComponent.class, "editCPPTopComponent.openButton.toolTipText")); // NOI18N
        openButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                openButtonMouseClicked(evt);
            }
        });
        jToolBar2.add(openButton);

        parentDirButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/esp/gephifileopener/folder.png"))); // NOI18N
        parentDirButton.setToolTipText(org.openide.util.NbBundle.getMessage(editCPPTopComponent.class, "editCPPTopComponent.parentDirButton.toolTipText")); // NOI18N
        parentDirButton.setFocusable(false);
        parentDirButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        parentDirButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        parentDirButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                parentDirButtonMouseClicked(evt);
            }
        });
        jToolBar2.add(parentDirButton);

        neighborNodesList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        neighborNodesList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                neighborNodesListMouseClicked(evt);
            }
        });
        neighborNodesList.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                neighborNodesListFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                neighborNodesListFocusLost(evt);
            }
        });
        jScrollPane2.setViewportView(neighborNodesList);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jToolBar1, javax.swing.GroupLayout.DEFAULT_SIZE, 470, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jToolBar2, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 285, Short.MAX_VALUE)
                    .addComponent(jToolBar2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane2))
                .addContainerGap())
        );

        getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(editCPPTopComponent.class, "editCPPTopComponent.AccessibleContext.accessibleName")); // NOI18N
    }// </editor-fold>//GEN-END:initComponents

    private void openButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_openButtonMouseClicked
        // TODO add your handling code here:
        String filename = filenameField.getText();
        File cpp = new File(filename);
        if(cpp.isFile() && cpp.exists())
        {
            if(contSaveToggle.isSelected())
            {
                checkSave(filenameField.getText()); //potential bug
            }
            editFile(cpp);
        }
    }//GEN-LAST:event_openButtonMouseClicked

    private void refreshButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_refreshButtonMouseClicked
        // TODO add your handling code here:
        setFileContent(filenameField.getText(),false);
    }//GEN-LAST:event_refreshButtonMouseClicked

    private void saveButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_saveButtonMouseClicked
        // TODO add your handling code here:
        checkSave(filenameField.getText()); //potential bug
    }//GEN-LAST:event_saveButtonMouseClicked

    private void saveButtonMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_saveButtonMouseExited
        // TODO add your handling code here:
        saveButton.setToolTipText("Save the file.");
    }//GEN-LAST:event_saveButtonMouseExited

    private void repoLogButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_repoLogButtonMouseClicked
        if(filenameField.getText() != null){
            runCMD(tsvnLog(filenameField.getText()));
        }
    }//GEN-LAST:event_repoLogButtonMouseClicked

    private void repoBrowseButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_repoBrowseButtonMouseClicked
        // TODO add your handling code here:
        if(filenameField.getText() != null){
            runCMD(tsvnRepoBrowser(filenameField.getText()));
        }
    }//GEN-LAST:event_repoBrowseButtonMouseClicked

    private void commitButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_commitButtonMouseClicked
        // TODO add your handling code here:
        if(filenameField.getText() != null){
            checkSave(filenameField.getText()); //potential bug
            SwingUtilities.invokeLater(new Runnable() 
            {
                public void run()
                {
                  runCMD(tsvnCommit(filenameField.getText()));
                }
            });
        }
    }//GEN-LAST:event_commitButtonMouseClicked

    private void updateButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_updateButtonMouseClicked
        // TODO add your handling code here:
        if(filenameField.getText() != null){
            runCMD(tsvnUpdate(filenameField.getText(), true));
            setFileContent(filenameField.getText(),false);
        }
    }//GEN-LAST:event_updateButtonMouseClicked

    private void revertButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_revertButtonMouseClicked
        // TODO add your handling code here:
        if(filenameField.getText() != null){
            runCMD(tsvnRevert(filenameField.getText()));
            setFileContent(filenameField.getText(),false);
            
        }
    }//GEN-LAST:event_revertButtonMouseClicked

    private void diffButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_diffButtonMouseClicked
        // TODO add your handling code here:
        if(filenameField.getText() != null){
            runCMD(tsvnDiff(filenameField.getText()));
        }
    }//GEN-LAST:event_diffButtonMouseClicked

    private void parentDirButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_parentDirButtonMouseClicked
        // TODO add your handling code here:
        File file = new File(filenameField.getText());
        if (!file.isDirectory() && file.isFile())
           file = file.getParentFile();
        if (file.exists() && file.isDirectory()){
            editFile(file);
        }
    }//GEN-LAST:event_parentDirButtonMouseClicked

    private void neighborNodesListMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_neighborNodesListMouseClicked
        // TODO add your handling code here:
        nodeListWrapper wrappedNode = (nodeListWrapper) neighborNodesList.getSelectedValue();
        Node gotoNode = wrappedNode.getNode();
        if(gotoNode != null)
        {
            /*
            if(evt.getClickCount() == 1 && !evt.isConsumed()){
                evt.consume();
                setPairwiseSelection(currentRootNode,graph.getEdge(gotoNode, currentRootNode));
            }
                    */
            if(evt.getClickCount() == 2 && !evt.isConsumed()){
                evt.consume();
                //VizController.getInstance().getSelectionManager().centerOnNode(gotoNode);
                editNode(gotoNode);
                VizController.getInstance().getSelectionManager().resetSelection();
                SwingUtilities.invokeLater(new Runnable() 
                {
                    public void run()
                    {
                        codePane.requestFocusInWindow();
                    }
                });
            }
        }
    }//GEN-LAST:event_neighborNodesListMouseClicked

    private void codePaneFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_codePaneFocusGained
        // TODO add your handling code here:
        VizController.getInstance().getSelectionManager().resetSelection();
        SwingUtilities.invokeLater(new Runnable() 
        {
            public void run()
            {
                setSelection();
            }
        });
    }//GEN-LAST:event_codePaneFocusGained

    private void centerOnNodeMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_centerOnNodeMouseClicked
        // TODO add your handling code here:
        if(currentRootNode != null)
        {
            VizController.getInstance().getSelectionManager().centerOnNode(currentRootNode);
            SwingUtilities.invokeLater(new Runnable() 
            {
                public void run()
                {
                    codePane.requestFocusInWindow();
                }
            });
        }
    }//GEN-LAST:event_centerOnNodeMouseClicked

    private void codePaneFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_codePaneFocusLost
        // TODO add your handling code here:
        VizController.getInstance().getSelectionManager().resetSelection();
    }//GEN-LAST:event_codePaneFocusLost

    private void neighborNodesListFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_neighborNodesListFocusLost
        // TODO add your handling code here:
        VizController.getInstance().getSelectionManager().resetSelection();
    }//GEN-LAST:event_neighborNodesListFocusLost

    private void jToolBar2FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jToolBar2FocusLost
        // TODO add your handling code here:
        VizController.getInstance().getSelectionManager().resetSelection();
    }//GEN-LAST:event_jToolBar2FocusLost

    private void neighborNodesListFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_neighborNodesListFocusGained
        // TODO add your handling code here:
        VizController.getInstance().getSelectionManager().resetSelection();
        SwingUtilities.invokeLater(new Runnable() 
        {
            public void run()
            {
                setSelection();
            }
        });
    }//GEN-LAST:event_neighborNodesListFocusGained

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton centerOnNode;
    private javax.swing.JEditorPane codePane;
    private javax.swing.JButton commitButton;
    private javax.swing.JToggleButton contSaveToggle;
    private javax.swing.JButton diffButton;
    private javax.swing.JTextField filenameField;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JToolBar.Separator jSeparator1;
    private javax.swing.JToolBar.Separator jSeparator2;
    private javax.swing.JToolBar.Separator jSeparator3;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JToolBar jToolBar2;
    private javax.swing.JList neighborNodesList;
    private javax.swing.JButton openButton;
    private javax.swing.JButton parentDirButton;
    private javax.swing.JButton refreshButton;
    private javax.swing.JButton repoBrowseButton;
    private javax.swing.JButton repoLogButton;
    private javax.swing.JButton revertButton;
    private javax.swing.JButton saveButton;
    private javax.swing.JButton updateButton;
    // End of variables declaration//GEN-END:variables
    @Override
    public void componentOpened() {
        // TODO add custom code on component opening
        DefaultSyntaxKit.initKit();
        codePane.setContentType("text/cpp");
    }
    
    @Override
    public void componentClosed() {
        // TODO add custom code on component closing
    }

    void writeProperties(java.util.Properties p) {
        // better to version settings since initial version as advocated at
        // http://wiki.apidesign.org/wiki/PropertyFiles
        p.setProperty("version", "1.0");
        // TODO store your settings
    }

    void readProperties(java.util.Properties p) {
        String version = p.getProperty("version");
        // TODO read your settings according to their version
    }
    
    public void setSelection(){
        VizController.getInstance().getSelectionManager().resetSelection();
        if(currentRootNode!=null)
        {
            Node[] nodeArray = {currentRootNode};
            VizController.getInstance().getSelectionManager().selectNodes(nodeArray);
            VizController.getInstance().getSelectionManager().selectEdges(graph.getEdges(currentRootNode).toArray());
        }
    }
    
    public void setPairwiseSelection(Node rootNode, Edge pairedEdge){
        VizController.getInstance().getSelectionManager().resetSelection();
        if(currentRootNode!=null)
        {
            VizController.getInstance().getSelectionManager().selectNode(rootNode);
            VizController.getInstance().getSelectionManager().selectEdge(pairedEdge);
        }
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
    
    public boolean setFilename(String filename)
    {
        //System.out.println(filename);
        File file = new File(filename);
        if(file.exists())
        {
            String currentFilename = filenameField.getText();
            filenameField.setText(filename);
            return true;
        }
        else
        {
            if(contSaveToggle.isSelected())
            {
                checkSave(filenameField.getText());  //potential bug if filenameField does not correspond to contents of codePane
            }
            filenameField.setText("");
            codePane.setText("");
            return false;
        }
    }
    
    public void setFileContent(final String filename, boolean respectSaveToggle){
        if(respectSaveToggle && contSaveToggle.isSelected())
        {
            checkSave(filename);
        }
        SwingUtilities.invokeLater(new Runnable() 
        {
          public void run()
          {
            if(setFilename(filename)){
                codePane.setText(readFile(filename));
                mc.setModelDirectory(Paths.get(filename).getParent().getParent().getParent().toString());
                jScrollPane1.setVisible(true);
            }
          }
        });
    }
    
    private static String readFile(String readFilename){
        String text = "Error.";
        try{
            byte[] encoded = Files.readAllBytes(Paths.get(readFilename));
            text = new String(encoded, Charset.defaultCharset());            
        } catch (IOException ex){
            Exceptions.printStackTrace(ex);
        }
        return text;
    }
    
    public void checkSave(String filename){
        String currentFile = filenameField.getText();
        File file = new File(currentFile);
        if(file.isFile() && file.exists())
        {
            if(filename.equals(currentFile)){
                saveFile(filename);
            } else{
                saveFile(currentFile);
            }
        }
    }
    
    private void saveFile(String fileToSave){
        try {
            FileWriter out = new FileWriter(fileToSave);
            out.write(codePane.getText());
            out.close();
            //System.out.println("Save path is " + Paths.get(fileToSave).getParent().getParent().toString());
            mc.updateFML(currentRootNode);
            AttributeRow row = (AttributeRow) currentRootNode.getNodeData().getAttributes();
            //System.out.println("getId(): "+ currentRootNode.getId() + "getValue(): " + row.getValue("Id").toString());
            mc.updateFML(currentRootNode,codePane.getText());
            saveButton.setToolTipText("Saved.");
            ToolTipManager.sharedInstance().mouseMoved(
                new MouseEvent(saveButton, 0, 0, 0,
                        0, 0, // X-Y of the mouse for the tool tip
                        0, false));
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        
    }
    
    public void runCMD(String cmd){
        try {
            Process process = Runtime.getRuntime().exec(cmd);
            
            // Get input streams
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
            BufferedReader stdError = new BufferedReader(new InputStreamReader(process.getErrorStream()));

            // Read command standard output
            String s;
            System.out.println("Standard output: ");
            while ((s = stdInput.readLine()) != null) {
                System.out.println(s);
            }

            // Read command errors
            System.out.println("Standard error: ");
            while ((s = stdError.readLine()) != null) {
                System.out.println(s);
            }
        } catch (IOException e) {
            e.printStackTrace(System.err);
        }
    }
    class nodeListWrapper {
        private final Node node;
        nodeListWrapper(Node inNode){
            node = inNode;
        }
        private Node getNode(){
            return node;
        }
        @Override
        public String toString(){
            AttributeRow row = (AttributeRow) node.getNodeData().getAttributes();
            return row.getValue("prod") + "_" + row.getValue("purp") + "->" + node.getNodeData().getLabel();
        }
    }
   
    public void setNeighbors(Node root, Node[] neighbors){
        ArrayList<nodeListWrapper> neighborsList = new ArrayList<nodeListWrapper>();
        for(Node node : neighbors){
            neighborsList.add(new nodeListWrapper(node));
        }
        neighborNodesList.setListData(neighborsList.toArray());
        currentRootNode = root;
    }
    
    public void editNode(Node node){
        setNeighbors(node, graph.getNeighbors(node).toArray());
        AttributeTable table = Lookup.getDefault().lookup(AttributeController.class).getModel().getNodeTable();
        String column = "cppFile";
        if (table.hasColumn(column)) 
        {
            AttributeRow row = (AttributeRow) node.getNodeData().getAttributes();
            final Object value;
            if ((value = row.getValue(column)) != null) 
            {
                setFileContent(value.toString(), true);
            }
            else
            {
                setFilename("");
            }
        }
    }
    private String tsvnLog(String filename){
        return "TortoiseProc /command:log /path:\"" + filename +"\"";
    }
    
    private String tsvnRepoBrowser(String filename){
        return "TortoiseProc /command:repobrowser /path:\"" + filename +"\"";
    }
    
    private String tsvnDiff(String filename){
        return "TortoiseProc /command:diff /path:\"" + filename +"\"";
    }
    
    private String tsvnRepoStatus(String filename){
        return "TortoiseProc /command:repostatus /path:\"" + filename +"\"";
    }
    
    private String tsvnUpdate(String filename, boolean promptRev){
        String command = "TortoiseProc /command:update /path:\"" + filename +"\"";
        if(promptRev)
        {
            command = command + " /rev";
        }
        return command;
    }
    
    private String tsvnCommit(String filename){
        return "TortoiseProc /command:commit /path:\"" + filename +"\"";
    }
    
    private String tsvnRevert(String filename){
        return "TortoiseProc /command:revert /path:\"" + filename +"\"";
    }
}
