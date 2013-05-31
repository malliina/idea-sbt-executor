package com.mle.idea.sbtexecutor.settings

import com.intellij.ui.{AnActionButton, AnActionButtonRunnable, CollectionListModel, ToolbarDecorator}
import com.intellij.openapi.ui.LabeledComponent
import javax.swing.{JOptionPane, JList}
import com.intellij.ui.components.JBList

/**
 *
 * @author mle
 */
class ExecuteSbtSettingsForm {
  // this piece of shit does not extend ListModel[T] but only ListModel
  private val listModel = new CollectionListModel[String]("compile", "clean")
  val sbtCommands = listModel.getItems

  def setModel(items: Seq[String]) {
    listModel.removeAll()
    items foreach (i => listModel add i)
  }

  private val sbtCommandList: JList[String] = new JBList(listModel).asInstanceOf[JList[String]]
  private val decorator = ToolbarDecorator createDecorator sbtCommandList
  decorator.setEditAction(new AnActionButtonRunnable {
    def run(e: AnActionButton) {
      // show dialog, get name
      val newValue = JOptionPane.showInputDialog("Edit SBT commandParams", sbtCommandList.getSelectedValue)
      if (newValue != null) {
        listModel.setElementAt(newValue, sbtCommandList.getSelectedIndex)
      }
    }
  })
  decorator.setAddAction(new AnActionButtonRunnable {
    def run(e: AnActionButton) {
      // show dialog, get name
      val addedCommand = JOptionPane.showInputDialog("Add SBT commandParams")
      listModel add addedCommand
    }
  })
  private val panel = decorator.createPanel()
  val comp = LabeledComponent.create(panel, "SBT commands in menu")
}
