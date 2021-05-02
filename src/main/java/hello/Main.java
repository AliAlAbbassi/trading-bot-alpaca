package hello;

import net.jacobpeterson.alpaca.AlpacaAPI;

public class Main {
  public static void main(String[] args) {
    String keyID = "PKA2DFJ3IVOA6NGBF50G";
    String secret = "pRKn6W8hPWEzmvv0rx2wmOFwmmZLwVe8MDZAig3I";
    AlpacaAPI alpacaAPI = new AlpacaAPI(keyID, secret);

    AlpacaExamples yeet = new AlpacaExamples(alpacaAPI);
    yeet.logAccountActivity();
  }

}
