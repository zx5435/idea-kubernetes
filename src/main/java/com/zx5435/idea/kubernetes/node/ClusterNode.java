package com.zx5435.idea.kubernetes.node;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.JBPopupMenu;
import com.intellij.openapi.ui.MessageDialogBuilder;
import com.zx5435.idea.kubernetes.model.ClusterModel;
import com.zx5435.idea.kubernetes.model.IResModel;
import com.zx5435.idea.kubernetes.other.ClusterEditDialog;
import com.zx5435.idea.kubernetes.model.KubeConfig;
import com.zx5435.idea.kubernetes.service.StorageSevice;
import io.fabric8.kubernetes.api.model.Namespace;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author zx5435
 */
@Slf4j
public class ClusterNode extends ATreeNode {

    @Getter
    private final List<ITreeNode> childElements = new ArrayList<>();

    public ClusterNode(KubeConfig kubeConfig, IResModel model) {
        String label = kubeConfig.getName();
        setLabel(label);
        setModel(model);
        setCtx(new ClusterModel(kubeConfig, getModel()));

        childElements.add(new FolderNode("Namespaces", getModel(), Namespace.class));
        childElements.add(new FolderNode.WorkloadsNode());
        childElements.add(new FolderNode.NetworksNode());
        childElements.add(new FolderNode.ConfigurationsNode());
        childElements.add(new FolderNode.VolumesNode());
    }

    @Override
    public JBPopupMenu getMenu(Project project) {
        JBPopupMenu menu = new JBPopupMenu();

        JMenuItem b1 = new JMenuItem("Edit", AllIcons.Actions.Edit);
        b1.addActionListener(e -> {
            new ClusterEditDialog(getLabel(), getCtx().getKubeConfig().getContent()).show();
        });
        menu.add(b1);

        JMenuItem b2 = new JMenuItem("Delete", AllIcons.Actions.Close);
        b2.addActionListener(e -> {
            boolean b = MessageDialogBuilder.yesNo("Are you sure", "delete is cluster").guessWindowAndAsk();
            if (b) {
                StorageSevice storage = ServiceManager.getService(StorageSevice.class);
                storage.deleteByName(getLabel());
            }
        });
        menu.add(b2);

        return menu;
    }

}
