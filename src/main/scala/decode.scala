package base64

import java.util.Arrays

object Decode {

  def urlSafe[T : Input](in: T) =
    decodeWith(URLSafeAlphabet)(in)

  def apply[T : Input](in: T) =
    decodeWith(StdAlphabet)(in)

  /**
   *  @return Either[(Int, Int), ByteBuffer] where the Left is
   *    is the position of an invalid byte and its decimal prepresentation
   *    and the Right contains a ByteBuffer with the base64 decoded preresentation
   *    of in.
   */
  def decodeWith[T : Input](alphabet: Alphabet)(ins: T) = {
    val in = implicitly[Input[T]].apply(ins)
    val len = in.size
    val len34 = len * 3 / 4
    val out = new Array[Byte](len34)
    val b4 = new Array[Byte](4)
    val index = alphabet.reversed
    val readBounds = len

    def read(
      at: Int = 0,
      b4Posn: Int = 0,
      outOffset: Int = 0
    ): Either[(Int, Int), Int] = if (at >= readBounds) Right(outOffset) else {
      val sbiCrop = (in(at) & 0x7f).toByte  // Only the low seven bits
      val sbiDecode = index(sbiCrop)
      val nextByte = at + 1
      if (sbiDecode >= WhiteSpaceEnc) {
        if (sbiDecode >= EqEnc) {
          b4.update(b4Posn, sbiCrop)
          val nextB4Posn = b4Posn + 1
          if (nextB4Posn > 3) {
            val cnt = dec4to3(
              b4, 0, out, outOffset, index
            )
            val curOffset = outOffset + cnt
            if (sbiCrop == Pad) Right(curOffset) else read(
              nextByte, 0, curOffset
            )
          } else read(nextByte, nextB4Posn, outOffset)
        } else read(nextByte, b4Posn, outOffset)
      } else Left((at, index(at) & 0xFF))
    }

    read().right.map {
      case len => Arrays.copyOf(out, len)
    }
  }

  private def dec4to3(
    in: Array[Byte],
    inOffset: Int,
    out: Array[Byte],
    outOffset: Int,
    index: Array[Byte]
  ): Int = {
    if (in(inOffset + 2) == Pad) { // Dk==
      val outBuff = ((index(in(inOffset)) & 0xFF)      << 18) |
                    ((index(in(inOffset + 1)) & 0xFF ) << 12)
      out.update(outOffset, (outBuff >>> 16).toByte)
      1
    } else if (in(inOffset + 3) == Pad) { // DkL=
      val outBuff = ((index(in(inOffset)) & 0xFF)     << 18) |
                    ((index(in(inOffset + 1)) & 0xFF) << 12) |
                    ((index(in(inOffset + 2)) & 0xFF) <<  6)
      out.update(outOffset, (outBuff >>> 16).toByte)
      out.update(outOffset + 1, (outBuff >>> 8).toByte)
      2
    } else { // DkLE
      val outBuff = ((index(in(inOffset)) & 0xFF)     << 18) |
                    ((index(in(inOffset + 1)) & 0xFF) << 12) |
                    ((index(in(inOffset + 2)) & 0xFF) << 6)  |
                    ((index(in(inOffset + 3)) & 0xFF))
      out.update(outOffset, (outBuff >> 16).toByte)
      out.update(outOffset + 1, (outBuff >> 8).toByte)
      out.update(outOffset + 2, (outBuff).toByte)
      3
    }
  }
}
