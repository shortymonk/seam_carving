package seamcarving

import java.awt.image.BufferedImage

class ImageResizer(private val image: BufferedImage) {
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
    }

    fun carveVerticalSeam(): BufferedImage {
        val newImage = BufferedImage(image.width - 1, image.height, BufferedImage.TYPE_INT_RGB)
        for (y in 0 until newImage.height) {
            var skipper = 0
            for (x in 0 until  newImage.width) {
                if (x to y in map.shortestPath) skipper = 1
                val color = image.getRGB(x + skipper, y)
                newImage.setRGB(x, y, color)
            }
        }
        return newImage
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
    }

    fun carveHorizontalSeam(): BufferedImage {
        val newImage = BufferedImage(image.width, image.height - 1, BufferedImage.TYPE_INT_RGB)
        for (x in 0 until newImage.width) {
            var skipper = 0
            for (y in 0 until  newImage.height) {
                if (x to y in map.shortestPath) skipper = 1
                val color = image.getRGB(x, y + skipper)
                newImage.setRGB(x, y, color)
            }
        }
        return newImage
    }
}