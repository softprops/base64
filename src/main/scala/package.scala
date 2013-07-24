package object base64 {
  trait Alphabet {
    protected val Base =
      ('A' to 'Z') ++ ('a' to 'z') ++ ('0' to '9')
    def values: IndexedSeq[Char]
  }

  /** Standard Base64 encoding as described in second 4 of
   *  <http://tools.ietf.org/html/rfc4648#section-4>
   */
  object StdAlphabet extends Alphabet {
    val values = Base ++ Vector('+', '/')
  }

  /** Base64-like encoding that is URL-safe as described in the Section 5 of
   *  <http://tools.ietf.org/html/rfc4648#section-5>
   */
  object URLSafeAlphabet extends Alphabet {
    val values = Base ++ Vector('-', '_')
  }
  
  val Pad = "="

  def toBinaryStr(b: Byte) =
    Integer.toBinaryString((b & 0xFF) + 0x100).substring(1)

  def toInt(xs: Array[Char]) =
    Integer.parseInt(String.valueOf(xs), 2)
}
