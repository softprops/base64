package base64

trait Alphabet {
  protected val Base =
    (('A' to 'Z') ++ ('a' to 'z') ++ ('0' to '9')).map(_.toByte)
  def values: IndexedSeq[Byte]
  def reversed: Array[Byte]
}

/** Standard Base64 encoding as described in second 4 of
 *  <http://tools.ietf.org/html/rfc4648#section-4>
 */
object StdAlphabet extends Alphabet {
  val values = Base ++ Vector('+', '/').map(_.toByte)
  val reversed: Array[Byte] = Array(
    -9,-9,-9,-9,-9,-9,-9,-9,-9,                 // Decimal  0 -  8
    -5,-5,                                      // Whitespace: Tab and Linefeed
    -9,-9,                                      // Decimal 11 - 12
    -5,                                         // Whitespace: Carriage Return
    -9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,     // Decimal 14 - 26
    -9,-9,-9,-9,-9,                             // Decimal 27 - 31
    -5,                                         // Whitespace: Space
    -9,-9,-9,-9,-9,-9,-9,-9,-9,-9,              // Decimal 33 - 42
    62,                                         // Plus sign at decimal 43
    -9,-9,-9,                                   // Decimal 44 - 46
    63,                                         // Slash at decimal 47
    52,53,54,55,56,57,58,59,60,61,              // Numbers zero through nine
    -9,-9,-9,                                   // Decimal 58 - 60
    -1,                                         // Equals sign at decimal 61
    -9,-9,-9,                                      // Decimal 62 - 64
    0,1,2,3,4,5,6,7,8,9,10,11,12,13,            // Letters 'A' through 'N'
    14,15,16,17,18,19,20,21,22,23,24,25,        // Letters 'O' through 'Z'
    -9,-9,-9,-9,-9,-9,                          // Decimal 91 - 96
    26,27,28,29,30,31,32,33,34,35,36,37,38,     // Letters 'a' through 'm'
    39,40,41,42,43,44,45,46,47,48,49,50,51,     // Letters 'n' through 'z'
    -9,-9,-9,-9,-9                              // Decimal 123 - 127
    ,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,       // Decimal 128 - 139
    -9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,     // Decimal 140 - 152
    -9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,     // Decimal 153 - 165
    -9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,     // Decimal 166 - 178
    -9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,     // Decimal 179 - 191
    -9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,     // Decimal 192 - 204
    -9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,     // Decimal 205 - 217
    -9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,     // Decimal 218 - 230
    -9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,     // Decimal 231 - 243
    -9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9         // Decimal 244 - 255 
  )
}

/** Base64-like encoding that is URL-safe as described in the Section 5 of
 *  <http://tools.ietf.org/html/rfc4648#section-5>
 */
object URLSafeAlphabet extends Alphabet {
  val values = Base ++ Vector('-', '_').map(_.toByte)
  val reversed: Array[Byte] = Array(
    -9,-9,-9,-9,-9,-9,-9,-9,-9,                 // Decimal  0 -  8
    -5,-5,                                      // Whitespace: Tab and Linefeed
    -9,-9,                                      // Decimal 11 - 12
    -5,                                         // Whitespace: Carriage Return
    -9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,     // Decimal 14 - 26
    -9,-9,-9,-9,-9,                             // Decimal 27 - 31
    -5,                                         // Whitespace: Space
    -9,-9,-9,-9,-9,-9,-9,-9,-9,-9,              // Decimal 33 - 42
    -9,                                         // Plus sign at decimal 43
    -9,                                         // Decimal 44
    62,                                         // Minus sign at decimal 45
    -9,                                         // Decimal 46
    -9,                                         // Slash at decimal 47
    52,53,54,55,56,57,58,59,60,61,              // Numbers zero through nine
    -9,-9,-9,                                   // Decimal 58 - 60
    -1,                                         // Equals sign at decimal 61
    -9,-9,-9,                                   // Decimal 62 - 64
    0,1,2,3,4,5,6,7,8,9,10,11,12,13,            // Letters 'A' through 'N'
    14,15,16,17,18,19,20,21,22,23,24,25,        // Letters 'O' through 'Z'
    -9,-9,-9,-9,                                // Decimal 91 - 94
    63,                                         // Underscore at decimal 95
    -9,                                         // Decimal 96
    26,27,28,29,30,31,32,33,34,35,36,37,38,     // Letters 'a' through 'm'
    39,40,41,42,43,44,45,46,47,48,49,50,51,     // Letters 'n' through 'z'
    -9,-9,-9,-9,-9                              // Decimal 123 - 127
    ,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,     // Decimal 128 - 139
    -9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,     // Decimal 140 - 152
    -9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,     // Decimal 153 - 165
    -9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,     // Decimal 166 - 178
    -9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,     // Decimal 179 - 191
    -9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,     // Decimal 192 - 204
    -9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,     // Decimal 205 - 217
    -9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,     // Decimal 218 - 230
    -9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,     // Decimal 231 - 243
    -9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9         // Decimal 244 - 255 
  )
}
