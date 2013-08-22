package base64

import org.apache.commons.codec.binary.Base64
import io.netty.handler.codec.base64.{ Base64 => NettyBase64 }
import io.netty.buffer.{ ByteBuf, Unpooled }
import com.google.caliper.SimpleBenchmark

class Base64Benchmark extends SimpleBenchmark {
  def timeApacheEnc(n: Int) = for (i <- 0 to n) {
    Base64.encodeBase64(Bench.bytes)
  }

  def timeApacheDec(n: Int) = for (i <- 0 to n) {
    Base64.decodeBase64(Bench.encoded)
  }

  def timeOurEnc(n: Int) = for (i <- 0 to n) {
    val b = Bench.bytebuffer
    b.rewind()
    Encode(b)
  }

  def timeOurDecode(n: Int) = for (i <- 0 to n) {
    val b = Bench.encodedBb
    b.rewind()
    Decode(b)
  }

  def timeOurUrlsafeEnc(n: Int) = for (i <- 0 to n) {
    val b = Bench.bytebuffer
    b.rewind()
    Encode.urlSafe(b)
  }

  def timeOurUrlsafeDec(n: Int) = for (i <- 0 to n) {
    val b = Bench.encodedBb
    b.rewind()
    Decode.urlSafe(b)
  }
}

