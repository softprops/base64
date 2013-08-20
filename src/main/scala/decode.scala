package base64

object Decode {
  def decodeWith(alphabet: Alphabet)(xs: Array[Byte]) =
    xs.filterNot(_ == '=')
      .map(alphabet.reversed(_))
      .map(i => toBinaryStr(i.toByte).substring(2))
      .flatten
      .grouped(8)
      .filterNot(_.length < 8)
      .map(g => toInt(g).toChar)
      .mkString("")

  def apply(chars: Array[Byte]) =
    decodeWith(StdAlphabet)(chars)

  def urlSafe =
    decodeWith(URLSafeAlphabet)_
}
