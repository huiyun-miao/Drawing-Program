import scalafx.scene.canvas.GraphicsContext
import scalafx.scene.control.MenuItem
import scalafx.scene.paint.Color
import scalafx.scene.shape.Line

class DrawLine(gc: GraphicsContext) {

  val canvas = gc.getCanvas
  var line = new Line()
  line.disable = true
  val lineItem = new MenuItem("Line")
  var startX = 0.0
  var startY = 0.0
  var endX = 0.0
  var endY = 0.0

  lineItem.setOnAction(e => {

    canvas.setOnMouseClicked(e => { })

    canvas.setOnMousePressed(e => {
      startX = e.getX
      startY = e.getY
      endX = startX
      endY = startY
    })

    canvas.setOnMouseDragged(e => {
      endX = e.getX
      endY = e.getY
      line.setStartX(startX)
      line.setStartY(startY)
      line.setEndX(e.getX)
      line.setEndY(e.getY)
      line.setStroke(gc.stroke)
      line.setStrokeWidth(gc.lineWidth)
    })

    canvas.setOnMouseReleased(e=> {
      line.setStroke(Color.Transparent)
      val op = LineOperation(startX, startY, endX, endY)
      op.draw(gc)
      DrawingApp.record(op)
    })
  })


}