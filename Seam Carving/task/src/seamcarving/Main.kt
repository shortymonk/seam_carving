package seamcarving

import java.io.File
import javax.imageio.ImageIO

fun main(args: Array<String>) {
    val imageName = args[1]     //"./Seam Carving/task/test/blue.png"
    val inputImage = File(imageName)
    val image = ImageIO.read(inputImage)
    val newWidth = 320
    val newHeight = 50

    val resizer = ImageResizer(image)
    resizer.energize()
    resizer.findVerticalRoute()
    resizer.findHorizontalRoute()
    resizer.writeImage(args[3])
}

/*stage 5
package seamcarving

import java.awt.Color
import java.io.File
import javax.imageio.ImageIO

fun main(args: Array<String>) {
    val imageName = args[1]     //"./Seam Carving/task/test/blue.png"
    val inputImage = File(imageName)
    val image = ImageIO.read(inputImage)
    val map = ImageMap(image.width, image.height)
    // calculation of energy & vertical path
        /*first line*/
    for (y in 0 until image.height) {
        map.calcEnergy(0, 0, image).also {
            map.energyArray[0][y] = it
            map.energyBuffer[0][y] = it
        }
    }

    for (y in 1 until image.height) {
        for (x in 0 until image.width) {
            // pixel's energy
            map.calcEnergy(x, y, image).also {
                map.energyArray[y][x] = it
                map.energyBuffer[y][x] = it
            }
            // start calculation of paths
            map.verticalStep(x, y)
        }
    }

   /* map.traceVerticalRoute()
    map.verticalPath.forEach {
        val x = it.first
        val y = it.second
        image.setRGB(x, y, Color(255, 0, 0).rgb)
    }*/

    // horizontal seam
    map.refreshBuffer()

    for (x in 1 until image.width) {
        for (y in 0 until image.height) {

            // start calculation of paths
            map.horizontalStep(x, y)
        }
    }
    map.traceHorizontalRoute()

    map.horizontalPath.forEach {
        val x = it.first
        val y = it.second
        image.setRGB(x, y, Color(255, 0, 0).rgb)
    }

    val outputImageName = args[3]   //"outImage.png"
    val outputImageFile = File(outputImageName)
    ImageIO.write(image, "png", outputImageFile)
}


* */