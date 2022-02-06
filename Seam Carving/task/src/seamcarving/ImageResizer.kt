package seamcarving

import java.awt.Color
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

class ImageResizer(private var image: BufferedImage) {
    val map = ImageMap(image.width, image.height)

    fun energize() {
        for (y in 0 until image.height) {
            for (x in 0 until image.width) {
                // pixel's energy
                map.calcEnergy(x, y, image).also {
                    map.energyArray[y][x] = it
                    map.energyBuffer[y][x] = it
                }
            }
        }
    }

    fun findVerticalRoute() {
        map.prepareVerticalRoutes()
        for (y in 1 until image.height) {
            for (x in 0 until image.width) {
                map.verticalStep(x, y)
            }
        }
        map.traceVerticalRoute()

        map.shortestPath.forEach {
            val x = it.first
            val y = it.second
            image.setRGB(x, y, Color(255, 0, 0).rgb)
        }
    }

    fun carveTheSeam() {
        val newImage = BufferedImage(image.width - 1, image.height - 1, BufferedImage.TYPE_INT_RGB)
        for (x in 0 until image.width) {
            var skipper = 0
            for (y in 0 until  image.height) {
                if (x to y in map.shortestPath) skipper = 1
                val color = image.getRGB(x - skipper, y)
                newImage.setRGB(x, y, color)
            }
        }
        image = newImage
    }

    // horizontal seam
    fun findHorizontalRoute() {
        map.prepareHorizontalRoutes()
        for (x in 1 until image.width) {
            for (y in 0 until image.height) {
                map.horizontalStep(x, y)
            }
        }
        map.traceHorizontalRoute()

        map.shortestPath.forEach {
            val x = it.first
            val y = it.second
            image.setRGB(x, y, Color(255, 0, 0).rgb)
        }
    }

    fun writeImage(fileName: String) {
        val outputImageFile = File(fileName)
        ImageIO.write(image, "png", outputImageFile)
    }
}