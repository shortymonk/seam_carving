package seamcarving

import java.io.File
import javax.imageio.ImageIO

fun main(args: Array<String>) {
    val imageName = args[1]
    val inputImage = File(imageName)
    val image = ImageIO.read(inputImage)
    val seemCarver = Carver()
    val newWidth = args[5].toInt()
    val newHeight = args[7].toInt()

    seemCarver.fillColorMatrix(image)
    repeat (newWidth) {
        seemCarver.fillEnergyMatrix()
        seemCarver.fillRoutes()
        seemCarver.dropSeam()
    }

    seemCarver.rotateMatrix()
    repeat (newHeight) {
        seemCarver.fillEnergyMatrix()
        seemCarver.fillRoutes()
        seemCarver.dropSeam()
    }

    seemCarver.rotateMatrix()

    val newImage = seemCarver.getNewImage()

    val outputImageName = args[3]
    val outputImageFile = File(outputImageName)
    ImageIO.write(newImage, "png", outputImageFile)
}
