package base64

import java.nio.ByteBuffer

object ByteBuffers {
  def encode = encodeWith(StdAlphabet)_

  def encodeWith(alphabet: Alphabet)(in: ByteBuffer) = {
    val len = in.remaining()
    val estEncLen = (len / 3) * 4 + (if (len % 3 > 0) 4 else 0)
    val out  = ByteBuffer.allocate(estEncLen).order(in.order())
    val raw3 = new Array[Byte](3)
    val enc4 = new Array[Byte](4)
    while (in.hasRemaining) {
      val rem = Math.min(3, in.remaining())
      in.get(raw3, 0, rem)
      enc3to4(raw3, 0, rem, enc4, 0, alphabet.values)
      out.put(enc4)
    }
    out.slice()
  }

  def enc3to4(
    in: Array[Byte], inOffset: Int, numSigBytes: Int, 
    out: Array[Byte], outOffset: Int, index: IndexedSeq[Byte]) {

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
    val inBuff = (if (numSigBytes > 0) in(inOffset)     << 24 >>> 8 else 0)  |
                 (if (numSigBytes > 1) in(inOffset + 1) << 24 >>> 16 else 0) |
                 (if (numSigBytes > 2) in(inOffset + 2) << 24 >>> 24 else 0)
    numSigBytes match {
      case 3 =>
        out.update(outOffset,     index(inBuff >>> 18))
        out.update(outOffset + 1, index(inBuff >>> 12 & 0x3f))
        out.update(outOffset + 2, index(inBuff >>> 6 & 0x3f))
        out.update(outOffset + 3, index(inBuff & 0x3f))
      case 2 =>
        out.update(outOffset,     index(inBuff >>> 18))
        out.update(outOffset + 1, index(inBuff >>> 12 & 0x3f))
        out.update(outOffset + 2, index(inBuff >>> 6 & 0x3f))
        out.update(outOffset + 3, '=');
      case 1 =>
        out.update(outOffset,     index(inBuff >>> 18))
        out.update(outOffset + 1, index(inBuff >>> 12 & 0x3f))
        out.update(outOffset + 2, '=')
        out.update(outOffset + 3, '=');
    }
  }
}
