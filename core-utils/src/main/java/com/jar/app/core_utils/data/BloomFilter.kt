package com.jar.app.core_utils.data

import java.io.Serializable
import java.nio.charset.Charset
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*
import kotlin.jvm.JvmOverloads
import kotlin.math.ceil
import kotlin.math.ln
import kotlin.math.roundToInt

class BloomFilter<E>(
    /**
     * Get expected number of bits per element when the Bloom filter is full. This value is set by the constructor
     * when the Bloom filter is created. See also getBitsPerElement().
     *
     * @return expected number of bits per element.
     */
    private val expectedBitsPerElement: Double,
    /**
     * Returns the expected number of elements to be inserted into the filter.
     * This value is the same value as the one passed to the constructor.
     *
     * @return expected number of elements.
     */
    private val expectedNumberOfElements // expected (maximum) number of elements to be added
    : Int,
    /**
     * Returns the value chosen for K.<br></br>
     * <br></br>
     * K is the optimal number of hash functions based on the size
     * of the Bloom filter and the expected number of inserted elements.
     *
     * @return optimal k.
     */
    val k // number of hash functions
    : Int
) : Serializable {
    /**
     * Return the bit set used to store the Bloom filter.
     *
     * @return bit set representing the Bloom filter.
     */
    private var bitSet: BitSet?
    private val bitSetSize: Int = ceil(expectedBitsPerElement * expectedNumberOfElements).toInt()

    private var numberOfAddedElements // number of elements actually added to the Bloom filter
            : Int

    companion object {
        val charset: Charset = Charset.forName("UTF-8") // encoding used for storing hash values as strings
        private const val hashName =
            "MD5" // MD5 gives good enough accuracy in most circumstances. Change to SHA1 if it's needed
        private var digestFunction: MessageDigest? = null
        /**
         * Generates a digest based on the contents of a String.
         *
         * @param val     specifies the input data.
         * @param charset specifies the encoding of the input data.
         * @return digest as long.
         */
        /**
         * Generates a digest based on the contents of a String.
         *
         * @param val specifies the input data. The encoding is expected to be UTF-8.
         * @return digest as long.
         */
        @JvmOverloads
        fun createHash(value: String, charset: Charset? = this.charset): Int {
            return createHash(
                value.toByteArray(charset!!)
            )
        }

        /**
         * Generates a digest based on the contents of an array of bytes.
         *
         * @param data specifies input data.
         * @return digest as long.
         */
        fun createHash(data: ByteArray?): Int {
            return createHashes(data, 1)[0]
        }

        /**
         * Generates digests based on the contents of an array of bytes and splits the result into 4-byte int's and store them in an array. The
         * digest function is called until the required number of int's are produced. For each call to digest a salt
         * is prepended to the data. The salt is increased by 1 for each call.
         *
         * @param data   specifies input data.
         * @param hashes number of hashes/int's to produce.
         * @return array of int-sized hashes
         */
        fun createHashes(data: ByteArray?, hashes: Int): IntArray {
            val result = IntArray(hashes)
            var k = 0
            var salt: Byte = 0
            while (k < hashes) {
                var digest: ByteArray
                synchronized(digestFunction!!) {
                    digestFunction?.update(salt)
                    salt++
                    digest = digestFunction?.digest(data)!!
                }
                var i = 0
                while (i < digest.size / 4 && k < hashes) {
                    var h = 0
                    for (j in i * 4 until i * 4 + 4) {
                        h = h shl 8
                        h = h or (digest[j].toInt() and 0xFF)
                    }
                    result[k] = h
                    k++
                    i++
                }
            }
            return result
        }

        init { // The digest method is reused between instances
            val tmp: MessageDigest? = try {
                MessageDigest.getInstance(hashName)
            } catch (e: NoSuchAlgorithmException) {
                null
            }
            digestFunction = tmp
        }
    }

    /**
     * Constructs an empty Bloom filter. The optimal number of hash functions (k) is estimated from the total size of the Bloom
     * and the number of expected elements.
     *
     * @param bitSetSize              defines how many bits should be used in total for the filter.
     * @param expectedNumberOElements defines the maximum number of elements the filter is expected to contain.
     */
    constructor(bitSetSize: Int, expectedNumberOElements: Int) : this(
        bitSetSize / expectedNumberOElements.toDouble(),
        expectedNumberOElements,
        (bitSetSize / expectedNumberOElements.toDouble() * ln(2.0)).roundToInt()
    )

    /**
     * Constructs an empty Bloom filter with a given false positive probability. The number of bits per
     * element and the number of hash functions is estimated
     * to match the false positive probability.
     *
     * @param falsePositiveProbability is the desired false positive probability.
     * @param expectedNumberOfElements is the expected number of elements in the Bloom filter.
     */
    constructor(falsePositiveProbability: Double, expectedNumberOfElements: Int) : this(
        ceil(-(ln(falsePositiveProbability) / ln(2.0))) / ln(2.0),  // c = k / ln(2)
        expectedNumberOfElements,
        ceil(-(ln(falsePositiveProbability) / ln(2.0))).toInt()
    )

    /**
     * Construct a new Bloom filter based on existing Bloom filter data.
     *
     * @param bitSetSize                     defines how many bits should be used for the filter.
     * @param expectedNumberOfFilterElements defines the maximum number of elements the filter is expected to contain.
     * @param actualNumberOfFilterElements   specifies how many elements have been inserted into the `filterData` BitSet.
     * @param filterData                     a BitSet representing an existing Bloom filter.
     */
    constructor(
        bitSetSize: Int,
        expectedNumberOfFilterElements: Int,
        actualNumberOfFilterElements: Int,
        filterData: BitSet?
    ) : this(bitSetSize, expectedNumberOfFilterElements) {
        bitSet = filterData
        numberOfAddedElements = actualNumberOfFilterElements
    }

    /**
     * Compares the contents of two instances to see if they are equal.
     *
     * @param obj is the object to compare to.
     * @return True if the contents of the objects are equal.
     */
    override fun equals(obj: Any?): Boolean {
        if (obj == null) {
            return false
        }
        if (javaClass != obj.javaClass) {
            return false
        }
        val other = obj as BloomFilter<E>
        if (expectedNumberOfElements != other.expectedNumberOfElements) {
            return false
        }
        if (k != other.k) {
            return false
        }
        if (bitSetSize != other.bitSetSize) {
            return false
        }
        return !(bitSet !== other.bitSet && (bitSet == null || bitSet != other.bitSet))
    }

    /**
     * Calculates a hash code for this class.
     *
     * @return hash code representing the contents of an instance of this class.
     */
    override fun hashCode(): Int {
        var hash = 7
        hash = 61 * hash + if (bitSet != null) bitSet.hashCode() else 0
        hash = 61 * hash + expectedNumberOfElements
        hash = 61 * hash + bitSetSize
        hash = 61 * hash + k
        return hash
    }

    /**
     * Calculates the expected probability of false positives based on
     * the number of expected filter elements and the size of the Bloom filter.
     * <br></br><br></br>
     * The value returned by this method is the *expected* rate of false
     * positives, assuming the number of inserted elements equals the number of
     * expected elements. If the number of elements in the Bloom filter is less
     * than the expected value, the true probability of false positives will be lower.
     *
     * @return expected probability of false positives.
     */
    fun expectedFalsePositiveProbability(): Double {
        return getFalsePositiveProbability(expectedNumberOfElements.toDouble())
    }

    /**
     * Calculate the probability of a false positive given the specified
     * number of inserted elements.
     *
     * @param numberOfElements number of inserted elements.
     * @return probability of a false positive.
     */
    fun getFalsePositiveProbability(numberOfElements: Double): Double {
        // (1 - e^(-k * n / m)) ^ k
        return Math.pow(
            1 - Math.exp(
                -k * numberOfElements
                        / bitSetSize.toDouble()
            ), k.toDouble()
        )
    }

    /**
     * Get the current probability of a false positive. The probability is calculated from
     * the size of the Bloom filter and the current number of elements added to it.
     *
     * @return probability of false positives.
     */
    val falsePositiveProbability: Double
        get() = getFalsePositiveProbability(numberOfAddedElements.toDouble())

    /**
     * Sets all bits to false in the Bloom filter.
     */
    fun clear() {
        bitSet!!.clear()
        numberOfAddedElements = 0
    }

    /**
     * Adds an object to the Bloom filter. The output from the object's
     * toString() method is used as input to the hash functions.
     *
     * @param element is an element to register in the Bloom filter.
     */
    fun add(element: E) {
        add(element.toString().toByteArray(charset))
    }

    /**
     * Adds an array of bytes to the Bloom filter.
     *
     * @param bytes array of bytes to add to the Bloom filter.
     */
    fun add(bytes: ByteArray?) {
        val hashes = createHashes(bytes, k)
        for (hash in hashes) bitSet!![Math.abs(hash % bitSetSize)] = true
        numberOfAddedElements++
    }

    /**
     * Adds all elements from a Collection to the Bloom filter.
     *
     * @param c Collection of elements.
     */
    fun addAll(c: Collection<E>) {
        for (element in c) add(element)
    }

    /**
     * Returns true if the element could have been inserted into the Bloom filter.
     * Use getFalsePositiveProbability() to calculate the probability of this
     * being correct.
     *
     * @param element element to check.
     * @return true if the element could have been inserted into the Bloom filter.
     */
    operator fun contains(element: E): Boolean {
        return contains(element.toString().toByteArray(charset))
    }

    /**
     * Returns true if the array of bytes could have been inserted into the Bloom filter.
     * Use getFalsePositiveProbability() to calculate the probability of this
     * being correct.
     *
     * @param bytes array of bytes to check.
     * @return true if the array could have been inserted into the Bloom filter.
     */
    operator fun contains(bytes: ByteArray?): Boolean {
        val hashes = createHashes(bytes, k)
        for (hash in hashes) {
            if (!bitSet!![Math.abs(hash % bitSetSize)]) {
                return false
            }
        }
        return true
    }

    /**
     * Returns true if all the elements of a Collection could have been inserted
     * into the Bloom filter. Use getFalsePositiveProbability() to calculate the
     * probability of this being correct.
     *
     * @param c elements to check.
     * @return true if all the elements in c could have been inserted into the Bloom filter.
     */
    fun containsAll(c: Collection<E>): Boolean {
        for (element in c) if (!contains(element)) return false
        return true
    }

    /**
     * Read a single bit from the Bloom filter.
     *
     * @param bit the bit to read.
     * @return true if the bit is set, false if it is not.
     */
    fun getBit(bit: Int): Boolean {
        return bitSet!![bit]
    }

    /**
     * Set a single bit in the Bloom filter.
     *
     * @param bit   is the bit to set.
     * @param value If true, the bit is set. If false, the bit is cleared.
     */
    fun setBit(bit: Int, value: Boolean) {
        bitSet!![bit] = value
    }

    /**
     * Returns the number of bits in the Bloom filter. Use count() to retrieve
     * the number of inserted elements.
     *
     * @return the size of the bitset used by the Bloom filter.
     */
    fun size(): Int {
        return bitSetSize
    }

    /**
     * Returns the number of elements added to the Bloom filter after it
     * was constructed or after clear() was called.
     *
     * @return number of elements added to the Bloom filter.
     */
    fun count(): Int {
        return numberOfAddedElements
    }

    /**
     * Get actual number of bits per element based on the number of elements that have currently been inserted and the length
     * of the Bloom filter. See also getExpectedBitsPerElement().
     *
     * @return number of bits per element.
     */
    fun getBitsPerElement(): Double {
        return bitSetSize / numberOfAddedElements.toDouble()
    }

    /**
     * Constructs an empty Bloom filter. The total length of the Bloom filter will be
     * c*n.
     *
     * @param c is the number of bits used per element.
     * @param n is the expected number of elements the filter will contain.
     * @param k is the number of hash functions used.
     */
    init {
        numberOfAddedElements = 0
        bitSet = BitSet(bitSetSize)
    }
}