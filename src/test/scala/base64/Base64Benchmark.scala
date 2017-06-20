package base64

import org.apache.commons.codec.binary.Base64
import io.netty.handler.codec.base64.{ Base64 => NettyBase64 }
import io.netty.buffer.Unpooled
import com.google.caliper.SimpleBenchmark

class Base64Benchmark extends SimpleBenchmark {
  def timeApacheEnc(n: Int) = for (i <- 0 to n) {
    Base64.encodeBase64(Bench.bytes)
  }

  def timeApacheDec(n: Int) = for (i <- 0 to n) {
    Base64.decodeBase64(Bench.encoded)
  }

  def timeNettyEnc(n: Int) = for (i <- 0 to n) {
    NettyBase64.encode(Unpooled.copiedBuffer(Bench.bytes))
  }

  def timeNettyDec(n: Int) = for (i <- 0 to n) {
    NettyBase64.decode(Unpooled.copiedBuffer(Bench.encoded))
  }

  def timeOurEnc(n: Int) = for (i <- 0 to n) {
    Encode(Bench.bytes)
  }

  def timeOurDecode(n: Int) = for (i <- 0 to n) {
    Decode(Bench.encoded)
  }
}

