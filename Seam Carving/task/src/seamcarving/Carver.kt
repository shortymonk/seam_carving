package seamcarving

import java.awt.Color
import java.awt.image.BufferedImage
import kotlin.math.pow
import kotlin.math.sqrt

class Carver {

    private val red = "red"
    private val green = "green"
    private val blue = "blue"

private var colorMatrix = List(0) { MutableList(0) { emptyMap<String, Int>() } }
    private var energyMatrix = mutableListOf<MutableList<Double>>()
    private var pathMatrix = Array(0) { Array(0) { emptyList<Pair<Int, Int>>() } }

    fun fillColorMatrix(image: BufferedImage) {
        val width = image.width
        val height = image.height
        colorMatrix = List(height) { MutableList(width) { emptyMap() } }
        for (y in 0 until image.height) {
            for (x in 0 until  image.width) {
                val color = Color(image.getRGB(x, y))
                val colorMap = mapOf(red to color.red, green to color.green, blue to color.blue)
                colorMatrix[y][x] = colorMap
            }
        }
    }

    fun rotateMatrix() {
        val image = getNewImage()
        val width = image.width
        val height = image.height
        colorMatrix = List(width) { MutableList(height) { emptyMap() } }
        for (y in 0 until image.width) {
            for (x in 0 until  image.height) {
                val color = Color(image.getRGB(y, x))
                val colorMap = mapOf(red to color.red, green to color.green, blue to color.blue)
                colorMatrix[y][x] = colorMap
            }
        }
    }

    private fun neighborPixels(currentPixel: Int, size: Int): Int {
        return when (currentPixel) {
            0, -1 -> 1
            size -> size - 2
            else -> currentPixel
        }
    }

    fun fillEnergyMatrix() {
        energyMatrix = MutableList(colorMatrix.size) { MutableList(colorMatrix[0].size) { 0.0 } }
        for (y in colorMatrix.indices) {
            for (x in colorMatrix[y].indices) {

                val neighborX = neighborPixels(x, colorMatrix[y].size - 1)
                val leftX = colorMatrix[y][neighborX - 1]
                val rightX = colorMatrix[y][neighborX + 1]
                val redX = leftX.getValue(red) - rightX.getValue(red)
                val greenX = leftX.getValue(green) - rightX.getValue(green)
                val blueX = (leftX.getValue(blue) - rightX.getValue(blue))
                val deltaX = redX.toDouble().pow(2) + greenX.toDouble().pow(2) + blueX.toDouble().pow(2)

                val neighborY = neighborPixels(y, colorMatrix.size - 1)
                val upY = colorMatrix[neighborY - 1][x]
                val bottomY = colorMatrix[neighborY + 1][x]
                val redY = upY.getValue(red) - bottomY.getValue(red)
                val greenY = upY.getValue(green) - bottomY.getValue(green)
                val blueY = upY.getValue(blue) - bottomY.getValue(blue)
                val deltaY = redY.toDouble().pow(2) + greenY.toDouble().pow(2) + blueY.toDouble().pow(2)

                energyMatrix[y][x] = sqrt((deltaX + deltaY))
            }
        }
    }

    fun fillRoutes() {
        pathMatrix = Array(colorMatrix.size) { Array(colorMatrix[0].size) { emptyList() } }
        for (x in pathMatrix[0].indices) pathMatrix[0][x] = listOf(0 to x)

        for (y in 1 until energyMatrix.size) {
            for (x in 0 until  energyMatrix[0].size) {
                val xRange = (x - 1).coerceIn(energyMatrix[y].indices)..(x + 1).coerceIn(energyMatrix[y].indices)
                var minX = Double.MAX_VALUE
                var minPath = emptyList<Pair<Int, Int>>()

                for (upX in xRange) {
                    if (energyMatrix[y - 1][upX] < minX) {
                        pathMatrix[y][x] = emptyList()
                        minX = energyMatrix[y - 1][upX]
                        minPath = pathMatrix[y - 1][upX]
                    }
                }
                pathMatrix[y][x] = minPath + (y to x)
                energyMatrix[y][x] = energyMatrix[y][x] + minX
            }
        }
    }

    fun dropSeam() {
        val minEnergyPath = energyMatrix.last().minOf { it }
        val minPathIndex = energyMatrix.last().indexOf(minEnergyPath)
        val shortestPath = pathMatrix.last()[minPathIndex]

        shortestPath.forEach {
            colorMatrix[it.first].removeAt(it.second)
        }
    }

    fun getNewImage(): BufferedImage {
        val image = BufferedImage(colorMatrix[0].size, colorMatrix.size, BufferedImage.TYPE_INT_RGB)

        for (y in colorMatrix.indices) {
            for (x in colorMatrix[y].indices) {
                val pixel = colorMatrix[y][x]
                val color = Color(pixel.getValue(red), pixel.getValue(green), pixel.getValue(blue))
                image.setRGB(x, y, color.rgb)
            }
        }

        return  image
    }

}