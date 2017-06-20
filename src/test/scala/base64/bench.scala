package base64

import org.apache.commons.codec.binary.Base64
import io.netty.handler.codec.base64.{ Base64 => NettyBase64 }
import io.netty.buffer.Unpooled

object Bench {
  val bytes =  "Man is distinguished, not only by his reason, but by this singular passion from other animals, which is a lust of the mind, that by a perseverance of delight in the continued and indefatigable generation of knowledge, exceeds the short vehemence of any carnal pleasure.".getBytes
  
  val encoded = "TWFuIGlzIGRpc3Rpbmd1aXNoZWQsIG5vdCBvbmx5IGJ5IGhpcyByZWFzb24sIGJ1dCBieSB0aGlzIHNpbmd1bGFyIHBhc3Npb24gZnJvbSBvdGhlciBhbmltYWxzLCB3aGljaCBpcyBhIGx1c3Qgb2YgdGhlIG1pbmQsIHRoYXQgYnkgYSBwZXJzZXZlcmFuY2Ugb2YgZGVsaWdodCBpbiB0aGUgY29udGludWVkIGFuZCBpbmRlZmF0aWdhYmxlIGdlbmVyYXRpb24gb2Yga25vd2xlZGdlLCBleGNlZWRzIHRoZSBzaG9ydCB2ZWhlbWVuY2Ugb2YgYW55IGNhcm5hbCBwbGVhc3VyZS4=".getBytes

  def main(args: Array[String]) {

    def repeat(times: Int)(f: => Unit) = {
      val before = System.currentTimeMillis
      for (i <- 0 to times)(f)
      System.currentTimeMillis - before
    }

    def run(times: Int = 1000, log: Boolean = false) = {
      val apache = repeat(times) { Base64.encodeBase64(bytes) }
      val netty = repeat(times) {
        NettyBase64.encode(Unpooled.copiedBuffer(bytes))
      }
      val ours = repeat(times) {
        Encode(bytes)
      }

      val apacheDec = repeat(times) { Base64.decodeBase64(encoded) }
      val nettyDec = repeat(times) {
        NettyBase64.decode(Unpooled.copiedBuffer(encoded))
      }
      val oursDec = repeat(times) {
        Decode(encoded)
      }

      if (log) {
        println("enc apache commons (byte arrays) took %s ms" format apache) // 97ms / 15000
        println("enc netty (byte buf)             took %s ms" format netty) // 95ms / 15000
        println("enc ours (byte arrays)           took %s ms" format ours) // 121ms / 15000

        println("dec apache commons (byte arrays) took %s ms" format apacheDec) // 71ms / 15000
        println("dec netty (byte buf)             took %s ms" format nettyDec) // 171ms / 15000
        println("dec ours (byte arrays)           took %s ms" format oursDec)   // 85ms / 15000
      }
    }

    // warmup
    run()

    // bench
    run(times = 15000, log = true)
  } 
}

