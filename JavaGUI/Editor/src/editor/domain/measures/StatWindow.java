/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package editor.domain.measures;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.util.Map;
import java.util.TreeMap;
import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.table.DefaultTableModel;

/** View a StatResultEntry object.
 *
 * @author elvio
 */
public class StatWindow extends javax.swing.JDialog {
    
    StatResultEntry stat;

    /**
     * Creates new form ToolStatWindow
     */
    public StatWindow(JFrame parent) {
        super(parent, true);
        initComponents();
        getRootPane().setDefaultButton(buttonClose);
    }
    
    public void showFor(StatResultEntry stat) {
        this.stat = stat;
        
        DefaultListModel<Object> plm = new DefaultListModel<>();
        plm.removeAllElements();
        for (Map.Entry<String, String> b : stat.assign.binding.entrySet()) {
            plm.addElement(b.getKey()+" = "+b.getValue());
        }
        paramList.setModel(plm);
        
        // Separate the known key/value stats from the unknown
        Map<String, String> statPairs = new TreeMap<>();
        statPairs.putAll(stat.statValues);
        
        int y = 0;
        for (String[] knownPair : StatResultEntry.getKnownStatKeys()) {
            if (statPairs.containsKey(knownPair[0])) {
                // Add a label pair to the panel
                GridBagConstraints gbc0 = new GridBagConstraints(0, y, 1, 1, 0.0, 0.0, 
                    GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, 
                        new Insets(3, 3, 3, 3), 0, 0);
                GridBagConstraints gbc1 = new GridBagConstraints(1, y, 1, 1, 1.0, 0.0, 
                    GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, 
                        new Insets(3, 3, 3, 3), 0, 0);
                y++;

                JLabel keyLab = new JLabel(knownPair[1]);
                JLabel valLab = new JLabel(statPairs.get(knownPair[0]));

                panelKnownStats.add(keyLab, gbc0);
                panelKnownStats.add(valLab, gbc1);
                
                statPairs.remove(knownPair[0]);
            }
        }
        
        DefaultTableModel dtm = (DefaultTableModel)tableStat.getModel();
        dtm.setRowCount(0);
        for (Map.Entry<String, String> e : statPairs.entrySet()) {
            // Unknown stat, add to the bottom table.
            dtm.addRow(new Object[] {e.getKey(), e.getValue()});
        }
        
        pack();
        setLocationRelativeTo(getParent());
        setVisible(true);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        paramList = new javax.swing.JList<Object>();
        buttonClose = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        tableStat = new javax.swing.JTable();
        panelKnownStats = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Tool statistics");

        jLabel1.setText("Parameters:");

        paramList.setVisibleRowCount(4);
        jScrollPane1.setViewportView(paramList);

        buttonClose.setText("Close");
        buttonClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonCloseActionPerformed(evt);
            }
        });

        tableStat.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null},
                {null, null},
                {null, null},
                {null, null}
            },
            new String [] {
                "Stat Name", "Value"
            }
        ));
        jScrollPane2.setViewportView(tableStat);

        panelKnownStats.setBorder(javax.swing.BorderFactory.createTitledBorder("Main Stats:"));
        panelKnownStats.setLayout(new java.awt.GridBagLayout());

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addComponent(panelKnownStats, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 482, Short.MAX_VALUE))
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 436, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(buttonClose)
                .addGap(206, 206, 206))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addComponent(jLabel1)
                .addGap(10, 10, 10)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 67, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelKnownStats, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(buttonClose)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void buttonCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonCloseActionPerformed
        setVisible(false);
        stat = null;
        dispose();
    }//GEN-LAST:event_buttonCloseActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buttonClose;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JPanel panelKnownStats;
    private javax.swing.JList<Object> paramList;
    private javax.swing.JTable tableStat;
    // End of variables declaration//GEN-END:variables
}