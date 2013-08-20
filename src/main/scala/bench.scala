package base64

import org.apache.commons.codec.binary.Base64
import java.nio.ByteBuffer

object Bench {
  def main(args: Array[String]) {
    val bytes =  "Man is distinguished, not only by his reason, but by this singular passion from other animals, which is a lust of the mind, that by a perseverance of delight in the continued and indefatigable generation of knowledge, exceeds the short vehemence of any carnal pleasure.".getBytes

    val bytebuffer = ByteBuffer.wrap(bytes)

    val encoded = "TWFuIGlzIGRpc3Rpbmd1aXNoZWQsIG5vdCBvbmx5IGJ5IGhpcyByZWFzb24sIGJ1dCBieSB0aGlzIHNpbmd1bGFyIHBhc3Npb24gZnJvbSBvdGhlciBhbmltYWxzLCB3aGljaCBpcyBhIGx1c3Qgb2YgdGhlIG1pbmQsIHRoYXQgYnkgYSBwZXJzZXZlcmFuY2Ugb2YgZGVsaWdodCBpbiB0aGUgY29udGludWVkIGFuZCBpbmRlZmF0aWdhYmxlIGdlbmVyYXRpb24gb2Yga25vd2xlZGdlLCBleGNlZWRzIHRoZSBzaG9ydCB2ZWhlbWVuY2Ugb2YgYW55IGNhcm5hbCBwbGVhc3VyZS4=".getBytes

    val encodedBb = ByteBuffer.wrap(encoded)

    def repeat(times: Int)(f: => Unit) = {
      val before = System.currentTimeMillis
      for (i <- 0 to times)(f)
      System.currentTimeMillis - before
    }

    def run(times: Int = 1000, log: Boolean = false) = {
      val apache = repeat(times) { Base64.encodeBase64(bytes) }
     // val ours   = repeat(times) { Encode(bytes) }
      val oursbb = repeat(times) {
        bytebuffer.rewind()
        ByteBuffers.encode(bytebuffer)
      }

      val apacheDec = repeat(times) { Base64.decodeBase64(encoded) }
      //  val oursDec   = repeat(times) { Decode(encoded) }
      val oursbbDec = repeat(times) {
        encodedBb.rewind()
        ByteBuffers.decode(encodedBb)
      }

      if (log) {
        println("enc apache commons (byte arrays) took %s ms" format apache) // 7ms / 5000
       // println("enc ours (byte arrays)           took %s ms" format ours)   // 4980ms / 5000
        println("enc ours (byte buffers)          took %s ms" format oursbb) // 43ms / 5000

        println("dec apache commons (byte arrays) took %s ms" format apacheDec) // 7ms / 5000
        //println("dec ours (byte arrays)           took %s ms" format oursDec)   // 4980ms / 5000
        println("dec ours (byte bytes)            took %s ms" format oursbbDec)   // 4980ms / 5000
      }
    }

    // warmup
    run()

    // bench
    run(times = 15000, log = true)
  } 
}
