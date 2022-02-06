package seamcarving

import java.io.File
import javax.imageio.ImageIO

fun main(args: Array<String>) {
    val imageName = args[1]     //"./Seam Carving/task/test/blue.png"
    val inputImage = File(imageName)
    val image = ImageIO.read(inputImage)
    val newWidth = 125
    val newHeight = 50

    var tmpImage = image
    repeat(newWidth) {
        val resizer = ImageResizer(tmpImage)
        resizer.energize()
        resizer.findVerticalRoute()
        tmpImage = resizer.carveVerticalSeam()
    }

    repeat(newHeight) {
        val resizer = ImageResizer(tmpImage)
        resizer.energize()
        resizer.findHorizontalRoute()
        tmpImage = resizer.carveHorizontalSeam()
    }

    val outputImageName = args[3]   //"outImage.png"
    val outputImageFile = File(outputImageName)
    ImageIO.write(tmpImage, "png", outputImageFile)
}
