package base64

import java.nio.ByteBuffer
import java.nio.charset.Charset

@annotation.implicitNotFound(
  msg = "base64 Input[T] type class instance for type ${T} not found")
trait Input[T] {
  def apply(t: T): ByteBuffer
}

object Input {
  implicit object Direct extends Input[ByteBuffer] {
    def apply(in: ByteBuffer) = in
  }

  implicit object Bytes extends Input[Array[Byte]] {
    def apply(in: Array[Byte]) = ByteBuffer.wrap(in)
  }

  implicit object Utf8Str extends Input[String] {
    def apply(in: String) = Bytes(in.getBytes("utf-8"))
  }

  implicit object Str extends Input[(String, Charset)] {
    def apply(in: (String, Charset)) =
      Bytes(in._1.getBytes(in._2.name()))
  }
}
