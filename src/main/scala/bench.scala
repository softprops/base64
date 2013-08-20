package base64

import org.apache.commons.codec.binary.Base64
import java.nio.ByteBuffer

object Bench {
  def main(args: Array[String]) {
    val bytes =  "Man is distinguished, not only by his reason, but by this singular passion from other animals, which is a lust of the mind, that by a perseverance of delight in the continued and indefatigable generation of knowledge, exceeds the short vehemence of any carnal pleasure.".getBytes

    val bytebuffer = ByteBuffer.wrap(bytes)

    def repeat(times: Int)(f: => Unit) = {
      val before = System.currentTimeMillis
      for (i <- 0 to times)(f)
      System.currentTimeMillis - before
    }

    def run(times: Int = 1000, log: Boolean = false) = {
      val apache = repeat(times) { Base64.encodeBase64(bytes) }
      val ours   = repeat(times) { Encode(bytes) }
      val oursbb = repeat(times) {
        bytebuffer.rewind()
        ByteBuffers.encode(bytebuffer)
      }
      if (log) {
        println("apache commons (byte arrays) took %s ms" format apache) // 7ms / 5000
        println("ours (byte arrays)           took %s ms" format ours)   // 4980ms / 5000
        println("ours (byte buffers)          took %s ms" format oursbb) // 43ms / 5000
      }
    }

    // warmup
    run()

    // bench
    run(times = 5000, log = true)   
  } 
}
