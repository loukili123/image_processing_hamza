import Jama.Matrix
import Jama.SingularValueDecomposition
import android.graphics.Bitmap
import android.graphics.Color
import kotlin.math.sqrt
class ImageCompressor(private val bitmap: Bitmap) {

    var limitRangeK: Int? = null
    // Step 1: Extract RGB matrices from the bitmap
    private fun extractRGBMatrices(): Array<Array<IntArray>> {
        val width = bitmap.width
        val height = bitmap.height
        val redMatrix = Array(height) { IntArray(width) }
        val greenMatrix = Array(height) { IntArray(width) }
        val blueMatrix = Array(height) { IntArray(width) }

        for (i in 0 until height) {
            for (j in 0 until width) {
                val pixel = bitmap.getPixel(j, i)
                redMatrix[i][j] = Color.red(pixel)
                greenMatrix[i][j] = Color.green(pixel)
                blueMatrix[i][j] = Color.blue(pixel)
            }
        }

        return arrayOf(redMatrix, greenMatrix, blueMatrix)
    }

    // Step 3: Combine compressed matrices to form compressed image
    fun compressImage(k: Int): Bitmap {
        val rgbMatrices = extractRGBMatrices()
        val compressedRedMatrix = applySVD(rgbMatrices[0], k)
        val compressedGreenMatrix = applySVD(rgbMatrices[1], k)
        val compressedBlueMatrix = applySVD(rgbMatrices[2], k)

        val width = compressedRedMatrix[0].size
        val height = compressedRedMatrix.size
        val compressedBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)

        for (i in 0 until height) {
            for (j in 0 until width) {
                val red = compressedRedMatrix[i][j]
                val green = compressedGreenMatrix[i][j]
                val blue = compressedBlueMatrix[i][j]
                compressedBitmap.setPixel(j, i, Color.rgb(red, green, blue))
            }
        }

        return compressedBitmap
    }

    ///////////////////////////////////////////////////////

    // Step 2: Apply SVD-like compression on matrices with rank k
    /*private fun applySVD(matrix: Array<IntArray>, k: Int): Array<IntArray> {
        val m = matrix.size
        val n = matrix[0].size
        val result = Array(m) { IntArray(n) }

        // Perform SVD-like compression by keeping only the top k singular values
        for (i in 0 until m) {
            for (j in 0 until n) {
                result[i][j] = matrix[i][j]
            }
        }

        return result
    }*/

   /* private fun applySVD(matrix: Array<IntArray>, k: Int): Array<IntArray> {
        val m = matrix.size
        val n = matrix[0].size

        // Convert the input matrix to Matrix
        val doubleMatrix = Matrix(m, n)
        for (i in 0 until m) {
            for (j in 0 until n) {
                doubleMatrix.set(i, j, matrix[i][j].toDouble())
            }
        }

        // Perform Singular Value Decomposition
        val svd = SingularValueDecomposition(doubleMatrix)

        // Get the singular values and vectors
        val s = svd.singularValues
        val u = svd.u
        val v = svd.v

        // Truncate singular values and vectors
        val sTruncated = DoubleArray(k)
        for (i in 0 until k) {
            sTruncated[i] = s[i]
        }
        val uTruncated = u.getMatrix(0, u.rowDimension - 1, 0, k - 1)
        val vTruncated = v.getMatrix(0, v.rowDimension - 1, 0, k - 1)

        // Reconstruct the compressed matrix
        val sDiagonal = Matrix(k, k)
        for (i in 0 until k) {
            for (j in 0 until k) {
                sDiagonal.set(i, j, if (i == j) sTruncated[i] else 0.0)
            }
        }
        val compressedMatrix = uTruncated.times(sDiagonal).times(vTruncated.transpose())

        // Convert the compressed matrix back to Int
        val result = Array(m) { IntArray(n) }
        for (i in 0 until m) {
            for (j in 0 until n) {
                result[i][j] = compressedMatrix.get(i, j).toInt()
            }
        }

        return result
    }*/

    private fun applySVD(matrix: Array<IntArray>, k: Int): Array<IntArray> {
        val m = matrix.size
        val n = matrix[0].size

        // Convert the input matrix to Matrix
        val doubleMatrix = Matrix(m, n)
        for (i in 0 until m) {
            for (j in 0 until n) {
                doubleMatrix.set(i, j, matrix[i][j].toDouble())
            }
        }

        // Perform Singular Value Decomposition
        val svd = SingularValueDecomposition(doubleMatrix)

        // Get the singular values and vectors
        val s = svd.singularValues
        val u = svd.u
        val v = svd.v

        // Truncate singular values and vectors
        val sTruncated = DoubleArray(k)
        for (i in 0 until k) {
            sTruncated[i] = s[i]
        }
        val uTruncated = u.getMatrix(0, u.rowDimension - 1, 0, k - 1)
        val vTruncated = v.getMatrix(0, v.rowDimension - 1, 0, k - 1)

        // Reconstruct the compressed matrix
        val sDiagonal = Matrix(k, k)
        for (i in 0 until k) {
            for (j in 0 until k) {
                sDiagonal.set(i, j, if (i == j) sTruncated[i] else 0.0)
            }
        }
        val compressedMatrix = uTruncated.times(sDiagonal).times(vTruncated.transpose())

        // Convert the compressed matrix back to Int
        val result = Array(m) { IntArray(n) }
        for (i in 0 until m) {
            for (j in 0 until n) {
                result[i][j] = compressedMatrix.get(i, j).toInt()
            }
        }


        return result
    }







}
