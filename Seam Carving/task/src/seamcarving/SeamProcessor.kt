package seamcarving

import java.awt.Color
import java.awt.image.BufferedImage
import kotlin.math.pow
import kotlin.math.sqrt

class ImageMap(private val width: Int, private val height: Int) {

    private var pathArray = Array(height) { Array(width) { emptyList<Pair<Int, Int>>() } }
    val energyArray = Array(height) { Array(width) { Double.MAX_VALUE } }
    val energyBuffer = MutableList(height) { MutableList(width) { Double.MAX_VALUE } }
    val shortestPath= mutableListOf<Pair<Int, Int>>()

    fun prepareVerticalRoutes() {
        for (x in 0 until width) {
            pathArray[0][x] = listOf(x to 0)
            energyBuffer.clear()
            for (line in energyArray) {
                energyBuffer.add(line.toMutableList())
            }
        }
    }

    fun prepareHorizontalRoutes() {
        for (y in 0 until height) {
            pathArray[y][0] = listOf(0 to y)
            energyBuffer.clear()
            for (line in energyArray) {
                energyBuffer.add(line.toMutableList())
            }
        }
    }

    fun traceVerticalRoute() {
        shortestPath.clear()
        val routeWithMinEnergy = energyBuffer.last().indexOf(energyBuffer.last().minOf { it })
        energyBuffer.last()[routeWithMinEnergy] = Double.MAX_VALUE
        shortestPath.addAll(pathArray.last()[routeWithMinEnergy])
    }

    fun traceHorizontalRoute() {
        shortestPath.clear()
        var minY = Double.MAX_VALUE
        for (y in energyBuffer.indices) {
            if (energyBuffer[y].last() < minY) {
                shortestPath.clear()
                minY = energyBuffer[y].last()
                shortestPath.addAll(pathArray[y].last())
            }
        }
    }

    private fun takeNeighbor(point: Int, size: Int) = when (point) {
        0, -1 -> 1
        size - 1, size -> size - 2
        else -> point
    }

    fun calcEnergy(x: Int = 0, y: Int, image: BufferedImage): Double {
        val xD = takeNeighbor(x, image.width)

        val rX = Color(image.getRGB(xD - 1, y)).red - Color(image.getRGB(xD + 1, y)).red.toDouble()
        val gX = Color(image.getRGB(xD - 1, y)).green - Color(image.getRGB(xD + 1, y)).green.toDouble()
        val bX = Color(image.getRGB(xD - 1, y)).blue - Color(image.getRGB(xD + 1, y)).blue.toDouble()
        val xGradient = rX.pow(2) + gX.pow(2) + bX.pow(2)

        val yD = takeNeighbor(y, image.height)

        val rY = Color(image.getRGB(x, yD - 1)).red - Color(image.getRGB(x, yD + 1)).red.toDouble()
        val gY = Color(image.getRGB(x, yD - 1)).green - Color(image.getRGB(x, yD + 1)).green.toDouble()
        val bY = Color(image.getRGB(x, yD - 1)).blue - Color(image.getRGB(x, yD + 1)).blue.toDouble()
        val yGradient = rY.pow(2) + gY.pow(2) + bY.pow(2)

        return sqrt(xGradient + yGradient)
    }

    fun verticalStep(currentX: Int, y: Int) {
        val range = (currentX - 1).coerceIn(0 until width)..(currentX + 1).coerceIn(0 until width)
        var minXValue = Double.MAX_VALUE

        for(aboveX in range) {
            if (energyBuffer[y - 1][aboveX] < minXValue) {
                minXValue = energyBuffer[y - 1][aboveX]
                pathArray[y][currentX] = pathArray[y - 1][aboveX] + (currentX to y)
            }
        }

        energyBuffer[y][currentX] += energyBuffer[y][currentX] + minXValue
    }

    fun horizontalStep(x: Int, currentY: Int) {
        val range = (currentY - 1).coerceIn(0 until height)..(currentY + 1).coerceIn(0 until height)
        var minXValue = Double.MAX_VALUE

        for(leftY in range) {
            if (energyBuffer[leftY][x - 1] < minXValue) {
                minXValue = energyBuffer[leftY][x - 1]
                pathArray[currentY][x] = pathArray[leftY][x - 1] + (x to currentY)
            }
        }

        energyBuffer[currentY][x] += energyBuffer[currentY][x] + minXValue
    }
}
