package base64

object Decode {
  def decodeWith(alphabet: Alphabet)(xs: Array[Char]) =
    xs.filterNot(_ == '=')
      .map(alphabet.values.indexOf(_))
      .map(i => toBinaryStr(i.toByte).substring(2))
      .flatten
      .grouped(8)
      .map(g => toInt(g).toChar)
      .mkString("")

  def apply(chars: Array[Char]) =
    decodeWith(StdAlphabet)(chars)

  def urlSafe =
    decodeWith(URLSafeAlphabet)_
}
