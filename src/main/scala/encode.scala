package base64

import java.util.Arrays

/** Base64 encodings. This implementation does not support line breaks */
object Encode {

  /** Encodes an array of bytes into a base64 encoded string
   *  which accounts for url encoding provisions */
  def urlSafe[T : Input](in: T, multiline: Boolean = false, pad: Boolean = true) =
    encodeWith(URLSafeAlphabet)(in, multiline, pad)

 /** Encodes an array of bytes into a base64 encoded string
   *   <http://www.apps.ietf.org/rfc/rfc4648.html> */
  def apply[T : Input](in: T, multiline: Boolean = false, pad: Boolean = true) =
    encodeWith(StdAlphabet)(in, multiline, pad)

  def encodeWith[T : Input](
    alphabet: Alphabet)
   (ins: T, multiline: Boolean = false, pad: Boolean = true): Array[Byte] = {
    val in = Input(ins)
    val index = alphabet.values
    val len = in.size
    val len2 = len - 2
    val estimate = (len / 3) * 4 + (if (len % 3 > 0) 4 else 0) match {
      case est => if (multiline) est + (est / MaxLine) else est
    }
    val out = new Array[Byte](estimate)

    @annotation.tailrec
    def write(d: Int = 0, e: Int = 0, col: Int = 0): (Int, Int) =
      if (d >= len2) (d, e)
      else {
        enc3to4(in, d, 3, out, e, index, pad)
        if (multiline && col + 4 >= MaxLine) {
          out.update(e + 4, NewLine)
          write(d + 3, e + 5, 0)
        } else write(d + 3, e + 4, col + 4)
      }

    val (d, e) = write()
    val fe = // extra padding
      if (d < len) {
        val updated = enc3to4(in, d, len - d, out, e, index, pad)
        e + updated
      } else e
    if (fe < out.size - 1) Arrays.copyOf(out, fe) else out
  }

  private def enc3to4(
    in: Array[Byte],
    inOffset: Int,
    numSigBytes: Int, 
    out: Array[Byte],
    outOffset: Int,
    index: IndexedSeq[Byte],
    pad: Boolean): Int = {

    //           1         2         3
    // 01234567890123456789012345678901 Bit position
    // --------000000001111111122222222 Array position from threeBytes
    // --------|    ||    ||    ||    | Six bit groups to index ALPHABET
    //          >>18  >>12  >> 6  >> 0  Right shift necessary
    //                0x3f  0x3f  0x3f  Additional AND
  
    // Create buffer with zero-padding if there are only one or two
    // significant bytes passed in the array.
    // We have to shift left 24 in order to flush out the 1's that appear
    // when Java treats a value as negative that is cast from a byte to an int.
    val inBuff = (if (numSigBytes > 0) (in(inOffset)     << 24) >>> 8 else 0)  |
                 (if (numSigBytes > 1) (in(inOffset + 1) << 24) >>> 16 else 0) |
                 (if (numSigBytes > 2) (in(inOffset + 2) << 24) >>> 24 else 0)
    (numSigBytes: @annotation.switch) match {
      case 3 =>
        out.update(outOffset,     index(inBuff >>> 18))
        out.update(outOffset + 1, index(inBuff >>> 12 & EncMask))
        out.update(outOffset + 2, index(inBuff >>> 6 & EncMask))
        out.update(outOffset + 3, index(inBuff & EncMask))
        4
      case 2 =>
        out.update(outOffset,     index(inBuff >>> 18))
        out.update(outOffset + 1, index(inBuff >>> 12 & EncMask))
        out.update(outOffset + 2, index(inBuff >>> 6 & EncMask))
        if (pad) {
          out.update(outOffset + 3, Pad)
          4
        } else 3
      case 1 =>
        out.update(outOffset,     index(inBuff >>> 18))
        out.update(outOffset + 1, index(inBuff >>> 12 & EncMask))
        if (pad) {
          out.update(outOffset + 2, Pad)
          out.update(outOffset + 3, Pad)
          4
        } else 2
      case _ =>
        0
    }
  }
}
