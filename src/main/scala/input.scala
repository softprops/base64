package base64

import java.nio.ByteBuffer
import java.nio.charset.Charset

@annotation.implicitNotFound(
  msg = "base64 Input[T] type class instance for type ${T} not found")
trait Input[T] {
  def apply(t: T): Array[Byte]
}

object Input {
  implicit object ByteBuffers extends Input[ByteBuffer] {
    def apply(in: ByteBuffer) = in.array
  }

  implicit object Bytes extends Input[Array[Byte]] {
    def apply(in: Array[Byte]) = in
  }

  implicit object Utf8Str extends Input[String] {
    def apply(in: String) = Bytes(in.getBytes("utf-8"))
  }

  implicit object Str extends Input[(String, Charset)] {
    def apply(in: (String, Charset)) =
      Bytes(in._1.getBytes(in._2.name()))
  }
}
