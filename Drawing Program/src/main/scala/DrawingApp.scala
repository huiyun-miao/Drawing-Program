import scalafx.application.JFXApp3
import scalafx.scene.{Group, Scene}
import scalafx.scene.paint.Color._
import scalafx.scene.control.{Alert, ButtonType, ColorPicker, Menu, MenuBar, MenuItem}
import scalafx.Includes._
import scalafx.scene.canvas.{Canvas, GraphicsContext}
import scalafx.scene.image.Image
import io.circe.generic.auto._
import io.circe.parser._
import io.circe.syntax._
import scalafx.scene.control.Alert.AlertType
import scalafx.stage.FileChooser
import scalafx.stage.FileChooser.ExtensionFilter

import java.io._
import java.io.FileInputStream


object DrawingApp extends JFXApp3 {

  //   -----------------------------------------
  //   Define record, undo, save, load functions
  //   -----------------------------------------
  var ops = collection.mutable.Buffer[DrawOperation]()

  def record(op: DrawOperation) = {
    ops += op
  }

  def undo() = {
    ops = ops.dropRight(1)
  }

  def save(file: File) = {
    scala.util.Using(new PrintWriter(new FileWriter(file))){
      writer => writer.println(ops.asJson.noSpaces)
    }
  }

  def load(file: File) = {
    scala.util.Using(scala.io.Source.fromFile(file) ){
      reader => for{
        operations <- decode[collection.mutable.Buffer[DrawOperation]](reader.getLines().mkString(""))
      } ops ++= operations
        ops.foreach(_.draw(DrawingApp.gc))
    }
  }

  var gc: GraphicsContext = null

  //   -----------------------------------------
  //   GUI
  //   -----------------------------------------

  override def start(): Unit = {

    //   -----------------------------------------
    //   Set stage, scene, canvas, root
    //   -----------------------------------------
    stage = new JFXApp3.PrimaryStage {
      title.value = "Drawing Program"
      width = 800
      height = 500
    }

    stage.setResizable(false)

    val canvas = new Canvas(800, 500)
    gc = canvas.graphicsContext2D

    val root = new Group()
    val scene = new Scene(root)
    stage.scene = scene

    val line = new DrawLine(gc)
    val rect = new DrawRect(gc)
    val circle = new DrawCircle(gc)
    val oval = new DrawOval(gc)

    //   -----------------------------------------
    //   Colorpicker & stroke width
    //   -----------------------------------------
    val colorPicker = new ColorPicker()
    colorPicker.setValue(Black)
    DrawingApp.record(new ColorChange(colorPicker.getValue))

    colorPicker.setOnAction(e => {
      gc.setStroke(colorPicker.getValue)
      DrawingApp.record(new ColorChange(colorPicker.getValue))
    })

    gc.lineWidth = 2.0

    //   -----------------------------------------
    //   Menu bar, menu & menu item
    //   -----------------------------------------
    val menuBar = new MenuBar()
    menuBar.prefWidthProperty.bind(stage.widthProperty)

    val menuFile = new Menu("File")
    val menuEdit = new Menu("Edit")
    val menuShape = new Menu("Shape")
    val menuColor = new Menu("Color")

    menuBar.getMenus.addAll(menuFile, menuEdit, menuShape, menuColor)

    val saveItem = new MenuItem("Save")
    val openItem = new MenuItem("Open")
    val undoItem = new MenuItem("Undo")
    val clearItem = new MenuItem("Clear")
    val colorItem = new MenuItem(null, colorPicker)

    menuFile.getItems.addAll(saveItem, openItem)
    menuEdit.getItems.addAll(undoItem, clearItem)
    menuShape.getItems.addAll(line.lineItem, rect.rectItem, circle.circleItem, oval.ovalItem)
    menuColor.getItems.add(colorItem)

    //   ----------------------------------------------
    //   Set save and open on action using file chooser
    //   ----------------------------------------------
    val fileChooser = new FileChooser()
    fileChooser.getExtensionFilters.add(new ExtensionFilter("JSON files (*.json)", "*.json"))

    saveItem.setOnAction(e => {
      val file = fileChooser.showSaveDialog(stage)
      save(file)
    })

    //   Add alert dialog when the user tries to open another drawing when the current drawing exists

    openItem.setOnAction(e => {
      if (ops.length <= 1 || ops.last.isInstanceOf[ClearOperation]) {
        ops.clear()
        val file = fileChooser.showOpenDialog(stage)
        load(file)
      } else {
        val alert = new Alert(AlertType.Confirmation)
        alert.setTitle("Drawing Program")
        alert.setHeaderText(null)
        alert.setContentText("Do you want to delete the current drawing?")
        val option = alert.showAndWait()

        if (option.get == ButtonType.OK) {
          gc.clearRect(0.0, 0.0, canvas.getWidth, canvas.getHeight)
          ops.clear()
          val file = fileChooser.showOpenDialog(stage)
          load(file)
        }
      }
    })

    //   -------------------------------------------
    //   Set undo and clear on action
    //   -------------------------------------------
    undoItem.setOnAction(e => {
      gc.clearRect(0.0, 0.0, canvas.getWidth, canvas.getHeight)
      ops = ops.dropRight(1)
      ops.map(_.draw(gc))
    })

    clearItem.setOnAction(e => {
      val op = ClearOperation()
      op.draw(gc)
      record(op)
    })

    //   -----------------------------------------
    //   Add everything to root
    //   -----------------------------------------
    root.children += canvas
    root.children += menuBar
    root.children.addAll(line.line, rect.rect, circle.circle, oval.oval)

    stage.show()
  }

}
