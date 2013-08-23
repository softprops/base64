package object base64 {  
  val Pad: Byte = '='
  val WhiteSpaceEnc = -5
  val EqEnc = -1
  val EncMask = 0x3f
  val MaxLine = 76
  val NewLine: Byte = '\n'
}
