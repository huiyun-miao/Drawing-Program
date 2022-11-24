import scalafx.scene.canvas.GraphicsContext
import scalafx.scene.control.MenuItem
import scalafx.scene.paint.Color
import scalafx.scene.shape.Rectangle

class DrawRect(gc: GraphicsContext) {

  val canvas = gc.canvas

  val rect = new Rectangle()
  rect.disable = true
  rect.setFill(Color.Transparent)
  val rectItem = new MenuItem("Rectangle")

  var startX = 0.0
  var startY = 0.0

  rectItem.setOnAction(e => {

    canvas.setOnMouseClicked(e => { })

    canvas.setOnMousePressed(e => {
      startX = e.getX
      startY = e.getY
      rect.setStroke(gc.stroke)
      rect.setStrokeWidth(gc.lineWidth)
    })

    canvas.setOnMouseDragged(e => {
      val x = if (e.getX < startX) e.getX else startX
      val y = if (e.getY < startY) e.getY else startY
      rect.setX(x)
      rect.setY(y)
      rect.setWidth(Math.abs(startX - e.getX))
      rect.setHeight(Math.abs(startY - e.getY))
    })

    canvas.setOnMouseReleased(e=> {
      rect.setStroke(Color.Transparent)
      val op = RectOperation(rect.getX, rect.getY, rect.getWidth, rect.getHeight)
      op.draw(gc)
      DrawingApp.record(op)
    })

  })

}
