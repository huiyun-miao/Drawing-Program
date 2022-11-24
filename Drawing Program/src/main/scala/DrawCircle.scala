import scalafx.scene.canvas.GraphicsContext
import scalafx.scene.control.MenuItem
import scalafx.scene.paint.Color
import scalafx.scene.shape.Circle

class DrawCircle(gc: GraphicsContext) {

  val canvas = gc.canvas

  val circle = new Circle()
  circle.disable = true
  circle.setFill(Color.Transparent)
  val circleItem = new MenuItem("Circle")

  var startX = 0.0
  var startY = 0.0

  circleItem.setOnAction(e => {

    canvas.setOnMouseClicked(e => { })

    canvas.setOnMousePressed(e => {
      startX = e.getX
      startY = e.getY
      circle.setStroke(gc.stroke)
      circle.setStrokeWidth(gc.lineWidth)
    })

    canvas.setOnMouseDragged(e => {
      val distanceX = e.getX - startX
      val distanceY = e.getY - startY
      val radius = Math.min(Math.abs(distanceX) / 2, Math.abs(distanceY) / 2)
      val centerX = if (distanceX > 0) startX + radius else startX - radius
      val centerY = if (distanceY > 0) startY + radius else startY - radius

      circle.setCenterX(centerX)
      circle.setCenterY(centerY)
      circle.setRadius(radius)
    })

    canvas.setOnMouseReleased(e => {
      circle.setStroke(Color.Transparent)
      val op = OvalOperation(circle.getCenterX - circle.getRadius, circle.getCenterY - circle.getRadius, circle.getRadius * 2, circle.getRadius * 2)
      op.draw(gc)
      DrawingApp.record(op)
    })

  })

}
