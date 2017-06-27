package base64;

import scala.util.Either;

public class JavaTest {
  public static void main(String[] args) {
    // type class instances
    Input<String> strs = Input$.MODULE$.Utf8Str();
    Input<byte[]> bytes = Input$.MODULE$.Bytes();
    // to and fro
    Either<Decode.Failure, byte[]> result = 
      Decode.apply(
        Encode.apply("test", false, false, strs),
        bytes);
    // print result
    if (result.isRight()) {
      System.out.println(
        String.format("encoded then decoded '%s'",
               new String(result.right().get())));
    }
  }
}
