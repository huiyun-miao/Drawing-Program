import scalafx.scene.canvas.GraphicsContext
import scalafx.scene.control.MenuItem
import scalafx.scene.paint.Color
import scalafx.scene.shape.Ellipse

class DrawOval(gc: GraphicsContext) {

  val canvas = gc.getCanvas

  val oval = new Ellipse()
  oval.disable = true
  oval.setFill(Color.Transparent)
  val ovalItem = new MenuItem("Oval")

  var startX = 0.0
  var startY = 0.0

  ovalItem.setOnAction(e => {

    canvas.setOnMouseClicked(e => { })

    canvas.setOnMousePressed(e => {
      startX = e.getX
      startY = e.getY
      oval.setStroke(gc.stroke)
      oval.setStrokeWidth(gc.lineWidth)
    })

    canvas.setOnMouseDragged(e => {
      val centerX = (startX + e.getX) / 2
      val centerY = (startY + e.getY) / 2
      val radiusX = Math.abs(startX - e.getX) / 2
      val radiusY = Math.abs(startY - e.getY) / 2

      oval.setCenterX(centerX)
      oval.setCenterY(centerY)
      oval.setRadiusX(radiusX)
      oval.setRadiusY(radiusY)
    })

    canvas.setOnMouseReleased(e => {
      oval.setStroke(Color.Transparent)
      val op = OvalOperation(oval.getCenterX - oval.getRadiusX, oval.getCenterY - oval.getRadiusY, oval.getRadiusX * 2, oval.getRadiusY * 2)
      op.draw(gc)
      DrawingApp.record(op)
    })

  })

}
