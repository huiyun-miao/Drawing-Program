import scalafx.scene.canvas.GraphicsContext
import scalafx.scene.paint.Color

sealed trait DrawOperation {
  def draw(gc: GraphicsContext): Unit
}

case class LineOperation(startX: Double, startY: Double, endX: Double, endY: Double) extends DrawOperation {
  def draw(gc: GraphicsContext) = gc.strokeLine(startX, startY, endX, endY)
}

case class RectOperation(x: Double, y: Double, w: Double, h: Double) extends DrawOperation {
  def draw(gc: GraphicsContext) = gc.strokeRect(x, y, w, h)
}

case class OvalOperation(x: Double, y: Double, w: Double, h: Double) extends DrawOperation {
  def draw(gc: GraphicsContext) = gc.strokeOval(x, y, w, h)
}

case class ClearOperation() extends DrawOperation {
  def draw(gc: GraphicsContext) = gc.clearRect(0.0, 0.0, gc.getCanvas.getWidth, gc.getCanvas.getHeight)
}

case class OurColor(r: Double, g: Double,b: Double, a: Double) {
  def getColor = new javafx.scene.paint.Color(r, g, b, a)
}

case class ColorChange(color: OurColor) extends DrawOperation {
  def this(c:Color) = this(OurColor(c.red, c.green, c.blue, c.opacity))
  def draw(gc: GraphicsContext) = gc.setStroke(color.getColor)
}