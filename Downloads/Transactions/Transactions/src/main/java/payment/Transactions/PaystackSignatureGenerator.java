//package payment.Transactions;
//
//import javax.crypto.Mac;
//import javax.crypto.spec.SecretKeySpec;
//
//public class PaystackSignatureGenerator {
//   public static void main(String[] args) throws Exception {
//     String payload = "{\"event\":\"charge.success\",\"data\":{\"reference\":\"DEP-123456\"}}";
//      String secret = "sk_test_6d4411ab4887dce0e39133066ad7130ca1d85c69"; // replace with your Paystack secret key
//
//       Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
//       SecretKeySpec secret_key = new SecretKeySpec(secret.getBytes(), "HmacSHA256");
//       sha256_HMAC.init(secret_key);
//
//      byte[] hash = sha256_HMAC.doFinal(payload.getBytes());
//    StringBuilder hexString = new StringBuilder();
//      for (byte b : hash) {
//          String hex = Integer.toHexString(0xff & b);
//          if (hex.length() == 1) hexString.append('0');
//          hexString.append(hex);
//   }
//
//   System.out.println("x-paystack-signature: " + hexString.toString());
//    }
////    x-paystack-signature: 18a751b19fb7279a2893e50ac3904f4b6f3a655adb15d407b03c133b9609c9a7
//
//
//}
