/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package editor.domain.superposition;

import common.EmptyIterator;
import common.Util;
import editor.domain.NetPage;
import editor.domain.Node;
import editor.domain.PageErrorWarning;
import editor.domain.ProjectData;
import editor.domain.ProjectFile;
import editor.domain.ProjectPage;
import editor.domain.ProjectResource;
import editor.domain.ViewProfile;
import editor.domain.elements.ColorClass;
import editor.domain.elements.ConstantID;
import editor.domain.elements.TemplateVariable;
import editor.domain.grammar.NodeNamespace;
import editor.domain.grammar.ParserContext;
import editor.domain.grammar.TemplateBinding;
import editor.gui.AbstractPageEditor;
import editor.gui.RapidMeasureCmd;
import editor.gui.SharedResourceProvider;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.print.PageFormat;
import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.UUID;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

/** A composition of n subnets, with tag-based P/T synchronization.
 *
 * @author elvio
 */
public abstract class MultiNetPage extends ProjectPage implements Serializable, ComposableNet, NodeNamespace {
    
    // Current viewport position
    public Point viewportPos = new Point(0, 0);
    
    // View profile
    public ViewProfile viewProfile = new ViewProfile();
    
    // The composition operator
//    public CompositionOperator operator;
    
    // Data generated by the compose() method
    // The group of nodes. It is a Set to quickly test for duplicate synchronizations.
//    private transient HashSet<NodeGroup> groups = null;

    // Descriptor of subnets that will be composed by this multi-net page
    public final ArrayList<NetInstanceDescriptor> netsDescr = new ArrayList<>();
    // Is replication counters fully instantiatable, or are they also parametric?
//    public boolean areReplicaCountParametric;
    
    //-------------------------------------------------------------------------
    // The composed net
    private transient NetPage compNet;
    // How the composed net is visualized
    private transient String[] visualizedSubNetNames;
    private transient NetPage[] visualizedSubNets;
    
    
    // Visualization
    public NetPage[] getVisualizedSubnets() { 
        return visualizedSubNets;
    }
    public String[] getVisualizedSubnetNames() { 
        return visualizedSubNetNames;
    }
    
    protected void setCompositionSuccessfull(NetPage compNet, String[] visualizedSubNetNames, NetPage[] visualizedSubNets) {
        this.compNet = compNet;
        this.visualizedSubNetNames = visualizedSubNetNames;
        this.visualizedSubNets = visualizedSubNets;
    }
    
    // Return true if the composed net is available, even if it contains some error
    // This method is different from isPageCorrect(), which requires the compNet to be correct.
    public boolean hasComposedNet() {
        return compNet != null;
    }
    
    public MultiNetPage() {
    }
    
    
    //==========================================================================

    @Override
    public Node getNodeByUniqueName(String id) {
        return null;
    }

    @Override
    public Iterator<ColorClass> colorClassIterator() {
        @SuppressWarnings("unchecked") Iterator<ColorClass> it = EmptyIterator.INSTANCE;
        return it;
    }

    //==========================================================================
    // Composition interface
    
    public abstract String getOperatorName();
    public abstract Icon getOperatorIcon();
//    public abstract Icon getPageIcon();
    
    // Is the number of composed nets fixed or variable?
    public abstract boolean hasFixedNumOfOperators();
    
    // Get the fixed number of composed nets
    public abstract int getFixedNumOfOperators();
    
    // Is this net composable?
    public abstract boolean canComposeWith(ProjectPage page);
    
    // Is net replication count available
    public abstract boolean useReplicaCount();
    
    // Net requires to instantiate color class parameters?
    public abstract boolean requireParamBinding();
    
    // do the net composition
    protected abstract void compose(ParserContext context);

    //==========================================================================
//    @Override
//    public Iterator<NodeGroup> groupIterator() {
//        return groups.iterator();
//    }

//    @Override
//    public Iterator<ComposableNet> subnetsIterator() {
//        return new ContainedIterator<ComposableNet, NetInstanceDescriptor>(netsDescr.iterator()) {
//            @Override
//            protected ComposableNet getContained(NetInstanceDescriptor elem) {
//                return elem.net;
//            }
//        };
//    }
//
//    @Override
//    public int numNodeGroups() { 
//        return groups.size();
//    }
//
//    @Override
//    public int numSubnets() {
//        return netsDescr.size();
//    }

    @Override
    public Set<TemplateVariable> enumerateParamsForNetComposition() {
        assert isPageCorrect() && compNet != null;
        return compNet.enumerateParamsForNetComposition();
    }

    @Override
    public void instantiateParams(TemplateBinding binding) {
        assert isPageCorrect() && compNet != null;
        compNet.instantiateParams(binding);
    }

    @Override
    public NetPage getComposedNet() {
        return compNet;
    }

    @Override
    public boolean hasSubnets() {
        return true;
    }
    
    //==========================================================================
    
//    public boolean canComposeWith(ProjectPage page) {
//        return (page != null) && (page != this) && (page instanceof ComposableNet);
//    }

    @Override
    protected boolean checkPageCorrectness(boolean isNewOrModified, ProjectData proj, 
                                           Set<ProjectPage> changedPages, ProjectPage invokerPage) 
    {
        clearPageErrorsAndWarnings();
        boolean doCompose = isNewOrModified;
        boolean dependenciesAreOk = true;
        ProjectPage theInvokerPage = (invokerPage == null ? this : invokerPage);

        ParserContext context = new ParserContext(this);
        
        // Rebuild transient references to nets (by-name lookup)
//        areReplicaCountParametric = false;
        for (NetInstanceDescriptor descr : netsDescr) {
            if (descr.targetNetName == null)
                dependenciesAreOk = false; // Possible??
            else {
                ProjectPage namedPage = proj.findPageByName(descr.targetNetName);
                if (canComposeWith(namedPage)) {
                    // Validate the referenced page
                    namedPage.checkPage(proj, changedPages, theInvokerPage, descr);
                    if (!namedPage.isPageCorrect()) {
                        dependenciesAreOk = false;
                        addPageError("Page \""+namedPage.getPageName()+"\" contains errors. "
                                + "Correct these errors before doing any measure computation.", null);
                    }
                    else {
                        descr.net = (ComposableNet)namedPage;
                        if (changedPages != null)
                            doCompose = doCompose || changedPages.contains(namedPage);
                        
                        // Keep in synch the template variables binding list
                        if (requireParamBinding()) {
                            Set<TemplateVariable> pageParams = descr.net.enumerateParamsForNetComposition();
    //                        Set<TemplateVariable> pageParams = descr.net.enumerateParams();
                            for (TemplateVariable var : pageParams) {
    //                            if (!descr.isParamKnown(var.getUniqueName()))
    //                                descr.unbindParam(var.getUniqueName());
                                if (!descr.isParamBound(var.getUniqueName()))
                                    descr.bindParam(var.getUniqueName(), var.getLastBindingExpr());
                            }
                            descr.removeMissingParams(pageParams);

                            // Copy latex string of template variables
                            descr.paramRefs = new TreeMap<>();
                            for (TemplateVariable tvar : pageParams)
                                descr.paramRefs.put(tvar.getUniqueName(), tvar);

                            descr.checkBindingCorrectness(this, context);
                        }
                        
//                        System.out.println("pageParams="+pageParams.size()+
//                                " bound="+descr.instParams.binding.size());
////                                " unbound="+descr.unboundParams.size());
                    }
                    // Validate the replica count
                    descr.numReplicas.checkExprCorrectness(context, this, descr);
                }
                else {
                    addPageError("\""+descr.targetNetName+"\" is not a valid composable net name.", descr);
                    dependenciesAreOk = false;
                }
            }
        }
        if (netsDescr.isEmpty())
            addPageError("No Petri nets in composition. Add some net.", null);

        if (!dependenciesAreOk) {
            for (NetInstanceDescriptor descr : netsDescr)
                descr.net = null;
            setCompositionSuccessfull(null, null, null);
        }
        else if (compNet==null || doCompose) {
            setCompositionSuccessfull(null, null, null);
            if (isPageCorrect()) {
                System.out.println("REBUILDING "+getPageName());
                ParserContext rootContext = new ParserContext(this);
                compose(rootContext);
                
                if (compNet == null)
                    addPageError("Composition failed.", null); // should not happen
                else {
                    // Now check that the composed net is actually a valid net
                    compNet.preparePageCheck();
                    compNet.checkPage(null, null, compNet, null);
//                    compNet.addPageError("PROVA", null);
                    if (!compNet.isPageCorrect()) {
                        // Transfer the errors of the generated net into the multinet
                        for (int i=0; i<compNet.getNumErrorsAndWarnings(); i++) {
                            PageErrorWarning pew = compNet.getErrorWarning(i);
                            if (pew.isWarning())
                                addPageWarning("Composed net: "+pew.getDescription(), null);
                            else
                                addPageError("Composed net: "+pew.getDescription(), null);
                        }
                    }
                }
            }
        }
//        else {
//            System.out.println("Keeping net composition of "+getPageName());
//        }
        
        return true;
    }
    
    
    // Apply parameter substitution to a netpage
    public static void substituteParameters(NetPage page, TemplateBinding binding) {
        for (int n=0; n<page.nodes.size(); n++) {
            Node node = page.nodes.get(n);
            if (node instanceof TemplateVariable) {
                TemplateVariable tvar = (TemplateVariable)node;
                // Check if we want to instantiate this parameter
                if (binding.binding.containsKey(node.getUniqueName())) {
                    ConstantID con = new ConstantID(tvar);
                    String value = binding.getSingleValueBoundTo(tvar).getExpr();
                    if (value.isEmpty())
                        value = "???";
                    con.getConstantExpr().setExpr(value);
                    page.nodes.set(n, con);
                }
            }
        }
    }

    @Override
    public void onAnotherPageRenaming(String oldName, String newName) {
        // Keep page names in sync
        for (NetInstanceDescriptor descr : netsDescr)
            if (descr != null && descr.targetNetName.equals(oldName))
                descr.targetNetName = newName;
    }

    @Override
    public void onAnotherPageNodeRenaming(ProjectPage otherPage, String oldId, String newId) {
        // Keep template variable names in synch
        for (NetInstanceDescriptor descr : netsDescr) {
            if (descr != null && descr.targetNetName.equals(otherPage.getPageName())) {
                if (descr.isParamBound(oldId)) {
                    System.out.println("onAnotherPageNodeRenaming "+otherPage.getPageName()+" "+oldId+"->"+newId);
                    descr.renameBoundParam(oldId, newId);
                }
            }
        }
    }
    
    private static final DataFlavor dataFlavour = new DataFlavor(MultiNetPage.class, "MultiNetDef");
    @Override public DataFlavor getDataFlavour() { return dataFlavour; }

//    @Override public Icon getPageIcon() {
//        return operator.getPageIcon();
//    }
//    @Override public String getPageTypeName() {
//        return "MULTINET";
//    }
    @Override public boolean hasEditableName() { return true; }

    @Override public boolean hasClearMeasureCmd() { return false; }
    @Override public boolean canClearMeasure(File projectFile) {
        throw new UnsupportedOperationException(); 
    }
    @Override public String clearMeasures(File projectFile, Component wnd) {
        throw new UnsupportedOperationException();
    }

    // Only its composed nets can be unfolded.
    @Override public boolean canBeUnfolded() { return false; }
    @Override public boolean pageSupportsUnfolding() { return false; }
    @Override public boolean pageSupportsAlgebraTool() { return false; }

    
    @Override
    public boolean pageSupportsRG(RgType rgType) {
        return false;
    }

    @Override
    public boolean canBuildRG(RgType rgType) {
        return false;
    }

    @Override
    public boolean hasRapidMesures() { return false; }
    @Override
    public boolean pageSupportsRapidMeasure(RapidMeasureCmd rmc) {
        throw new UnsupportedOperationException(""); 
    }
    @Override
    public boolean canDoRapidMeasure(RapidMeasureCmd rmc) {
        throw new UnsupportedOperationException(""); 
    }
    
    
    

    
    @Override public void retrieveLinkedResources(Map<UUID, ProjectResource> resourceTable) {
    }
    @Override public void relinkTransientResources(Map<UUID, ProjectResource> resourceTable) {
    }
    
    //============================================
    
    @Override
    public Class getEditorClass() {
        return MultiNetEditorPanel.class;
    }

    @Override
    public boolean hasPlaceTransInv() {
        return false;
    }

    // ======== Print support ==================================
    @Override
    public boolean canPrint() {
        return false;
    }

    @Override
    public void print(Graphics2D g, PageFormat pf) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    // ======== Cut/Copy/Paste support =========================
    @Override
    public boolean canCutOrCopy() {
        if (hasFixedNumOfOperators())
            return false;
        for (NetInstanceDescriptor descr : netsDescr)
            if (descr.isSelected())
                return true;
        return false;
    }
    
    private static class DescriptorsInClipboard implements Serializable {
        public ArrayList<NetInstanceDescriptor> copiedDescr = new ArrayList<>();
    }

    @Override
    public Object copyData() {
        DescriptorsInClipboard dc = new DescriptorsInClipboard();
        for (NetInstanceDescriptor descr : netsDescr)
            if (descr.isSelected())
                dc.copiedDescr.add((NetInstanceDescriptor)Util.deepCopy(descr));
        return dc;
    }

    @Override
    public void eraseCutData(Object data) {
        if (hasFixedNumOfOperators())
            return;
        Iterator<NetInstanceDescriptor> nidIt = netsDescr.iterator();
        while (nidIt.hasNext()) {
            NetInstanceDescriptor descr = nidIt.next();
            if (descr.isSelected())
                nidIt.remove();
        }
    }

    @Override
    public void pasteData(Object data) {
        if (hasFixedNumOfOperators())
            return;
        setSelectionFlag(false);
        // Append pasted data to the end
        DescriptorsInClipboard dc = (DescriptorsInClipboard)data;
        for (NetInstanceDescriptor descr : dc.copiedDescr)
            netsDescr.add(descr);
    }

    @Override
    public void onSelectErrorWarning(PageErrorWarning errWarn) {
        for (NetInstanceDescriptor descr : netsDescr)
            descr.setSelected(false);
        if (errWarn != null)
            errWarn.selectAffectedElements(true);
    }
    
    @Override
    public void setSelectionFlag(boolean isSelected) {
        for (NetInstanceDescriptor descr : netsDescr)
            descr.setSelected(isSelected);
    }
    
    public int countSelected() {
        if (hasFixedNumOfOperators())
            return 0;
        int numSel = 0;
        for (NetInstanceDescriptor descr : netsDescr)
            if (descr.isSelected())
                numSel++;
        return numSel;
    }
    
    @Override public boolean pageSupportsPlay() { return true; }

    @Override
    public PlayCommand canPlay(ProjectFile project) {
        return PlayCommand.NO;
    }

    @Override
    public JPopupMenu getPlayDropdownMenu(ProjectFile project, Action action) {
        return null;
    }

    @Override
    public AbstractPageEditor getPlayWindow(SharedResourceProvider shActProv, JMenuItem menuItem) {
        return null;
    }
    
    
    
}
