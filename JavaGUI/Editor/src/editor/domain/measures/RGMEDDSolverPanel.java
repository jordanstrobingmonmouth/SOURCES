/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package editor.domain.measures;

import common.Util;
import editor.domain.NetPage;
import editor.gui.net.IndeterminateListCellRenderer;

/**
 *
 * @author elvio
 */
public class RGMEDDSolverPanel extends SolverPanel {

    private boolean initializing = true;
    private RGMEDDSolverParams params = null;
    /**
     * Creates new form RGMEDDSolverPanel
     */
    public RGMEDDSolverPanel() {
        initComponents();
        Util.reformatPanelsForPlatformLookAndFeel(this);
        initializing = true;
        
        exprField_plBound.setExprListener(solverExprListener);
        
        comboBox_placeBoundMeth.setRenderer(new IndeterminateListCellRenderer());
        comboBox_varOrder.setRenderer(new IndeterminateListCellRenderer());
        for (RGMEDDSolverParams.PlaceBoundMethod pmb : RGMEDDSolverParams.PlaceBoundMethod.values())
            comboBox_placeBoundMeth.addItem(pmb);
        for (RGMEDDSolverParams.VariableOrder vo : RGMEDDSolverParams.VariableOrder.values())
            comboBox_varOrder.addItem(vo);
        
        initializing = false;
    }

    @Override
    public void initializeFor(SolverParams _params, NetPage page, NetPage evalPage, 
                              boolean simplifiedUI, MeasurePage measPage) 
    {
        initializing = true;
        
        params = (RGMEDDSolverParams)Util.deepCopy(_params);
        
        exprField_plBound.initializeFor(params.placeBound.getEditableValue(), page);
        exprField_plBound.setEnabled(params.plBoundMeth == RGMEDDSolverParams.PlaceBoundMethod.MANUALLY_SPECIFIED);
        checkBox_counterExamples.setSelected(params.genCounterExamples);
        comboBox_placeBoundMeth.setSelectedItem(params.plBoundMeth);
        comboBox_varOrder.setSelectedItem(params.varOrder);

        initializing = false;
    }

    @Override
    public void deinitialize() {
        exprField_plBound.deinitialize();
        params = null;
    }
    
    @Override
    public SolverParams currParams() {
        return params;
    }

    @Override
    public boolean areParamsCorrect() {
        if (exprField_plBound.isEnabled()) 
            return exprField_plBound.isValueValid();
        return true;
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
        comboBox_placeBoundMeth = new javax.swing.JComboBox<RGMEDDSolverParams.PlaceBoundMethod>();
        jLabel2 = new javax.swing.JLabel();
        exprField_plBound = new editor.domain.measures.ExprField();
        jLabel3 = new javax.swing.JLabel();
        comboBox_varOrder = new javax.swing.JComboBox<RGMEDDSolverParams.VariableOrder>();
        checkBox_counterExamples = new javax.swing.JCheckBox();

        jLabel1.setText("Place bound:");

        comboBox_placeBoundMeth.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboBox_placeBoundMethActionPerformed(evt);
            }
        });

        jLabel2.setText("bound:");

        jLabel3.setText("Variable order heuristic:");

        comboBox_varOrder.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboBox_varOrderActionPerformed(evt);
            }
        });

        checkBox_counterExamples.setText("Generate counter-examples/witnesses when possible.");
        checkBox_counterExamples.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkBox_counterExamplesActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(comboBox_placeBoundMeth, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(comboBox_varOrder, 0, 185, Short.MAX_VALUE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(exprField_plBound, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(checkBox_counterExamples)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(exprField_plBound, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel1)
                        .addComponent(comboBox_placeBoundMeth, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel2)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(comboBox_varOrder, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(checkBox_counterExamples)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void checkBox_counterExamplesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkBox_counterExamplesActionPerformed
        if (initializing)
            return;
        params.genCounterExamples = checkBox_counterExamples.isSelected();
        fireSolverParamsModified();
    }//GEN-LAST:event_checkBox_counterExamplesActionPerformed

    private void comboBox_placeBoundMethActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comboBox_placeBoundMethActionPerformed
        if (initializing)
            return;
        RGMEDDSolverParams.PlaceBoundMethod pbm = 
                (RGMEDDSolverParams.PlaceBoundMethod)comboBox_placeBoundMeth.getSelectedItem();
        if (pbm == params.plBoundMeth)
            return;
        params.plBoundMeth = pbm;
        fireSolverParamsModified();
    }//GEN-LAST:event_comboBox_placeBoundMethActionPerformed

    private void comboBox_varOrderActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comboBox_varOrderActionPerformed
        if (initializing)
            return;
        RGMEDDSolverParams.VariableOrder vo = 
                (RGMEDDSolverParams.VariableOrder)comboBox_varOrder.getSelectedItem();
        if (vo == params.varOrder)
            return;
        params.varOrder = vo;
        fireSolverParamsModified();
    }//GEN-LAST:event_comboBox_varOrderActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox checkBox_counterExamples;
    private javax.swing.JComboBox<RGMEDDSolverParams.PlaceBoundMethod> comboBox_placeBoundMeth;
    private javax.swing.JComboBox<RGMEDDSolverParams.VariableOrder> comboBox_varOrder;
    private editor.domain.measures.ExprField exprField_plBound;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    // End of variables declaration//GEN-END:variables
}