package base64

import java.nio.ByteBuffer

object ByteBuffers {
  val WhiteSpaceEnc = -5
  val EqEnc = -1

  def decode = decodeWith(StdAlphabet)_

  def decodeWith(alphabet: Alphabet)(in: ByteBuffer) = {
    val len = in.remaining()
    val off = in.position()
    val len34 = len * 3 / 4
    val out = ByteBuffer.allocate(len34).order(in.order())
    val b4 = new Array[Byte](4)
    val index = alphabet.reversed
    def read(
      at: Int = off,
      b4Posn: Int = 0,
      outBuffPosn: Int = 0)
      (pred: Int => Boolean): Either[String, Int] = if (pred(at)) Right(outBuffPosn) else {
      val sbiCrop = (in.get(at) & 0x7f).toByte  // Only the low seven bits
      val sbiDecode = index(sbiCrop)
      val nextByte = at + 1
      if (sbiDecode >= WhiteSpaceEnc) {
        if (sbiDecode >= EqEnc) {
          b4.update(b4Posn, sbiCrop)
          val nextB4Posn = b4Posn + 1
          if (nextB4Posn > 3) {
            if (sbiCrop == PadB) Right(outBuffPosn) else read(
              nextByte, 0, outBuffPosn + dec4to3(
                b4, 0, out, outBuffPosn, index
              )
            )(pred)
          } else read(nextByte, nextB4Posn, outBuffPosn)(pred)
        } else read(nextByte, b4Posn, outBuffPosn)(pred)
      } else Left(
          "bad Base64 input character at %d: %d" format(
            at, (index(at) & 0xFF)
          )
      )
    }
    read()(_ >= off + len).right.map {
      case bytes =>
        println("read %d bytes, expecting %s" format(bytes, len34))
        out.slice()
    }
  }

  def b(i: Byte): Byte = i

  def dec4to3(
    in: Array[Byte], inOffset: Int, out: ByteBuffer,
    outOffset: Int, index: Array[Byte]
  ): Int = {
    if (in(inOffset + 2) == PadB) { // Dk==
      val outBuff = ((index(in(inOffset)) & 0xFF)      << 18) |
                    ((index(in(inOffset + 1)) & 0xFF ) << 12)
      out.put(outOffset, (outBuff >>> 16).toByte)
      1
    } else if (in(inOffset + 3) == PadB) { // DkL=
      val outBuff = ((index(in(inOffset)) & 0xFF)     << 18) |
                    ((index(in(inOffset + 1)) & 0xFF) << 12) |
                    ((index(in(inOffset + 2)) & 0xFF) <<  6)
      out.put(outOffset, (outBuff >>> 16).toByte)
      out.put(outOffset + 1, (outBuff >>> 8).toByte)
      2
    } else { // DkLE
      val outBuff = ((index(in(inOffset)) & 0xFF)     << 18) |
                    ((index(in(inOffset + 1)) & 0xFF) << 12) |
                    ((index(in(inOffset + 2)) & 0xFF) << 6)  |
                    ((index(in(inOffset + 3)) & 0xFF))
      out.put(outOffset, (outBuff >> 16).toByte)
      out.put(outOffset + 1, (outBuff >> 8).toByte)
      out.put(outOffset + 2, (outBuff).toByte)
      3
    }
  }

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
    (numSigBytes: @annotation.switch) match {
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
