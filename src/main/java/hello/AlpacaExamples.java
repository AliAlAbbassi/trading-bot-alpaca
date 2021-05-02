package hello;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

import net.jacobpeterson.abstracts.enums.SortDirection;
import net.jacobpeterson.alpaca.AlpacaAPI;
import net.jacobpeterson.alpaca.enums.activity.ActivityType;
import net.jacobpeterson.alpaca.rest.exception.AlpacaAPIRequestException;
import net.jacobpeterson.domain.alpaca.accountactivities.AccountActivity;
import net.jacobpeterson.domain.alpaca.accountactivities.NonTradeActivity;
import net.jacobpeterson.domain.alpaca.accountactivities.TradeActivity;
import net.jacobpeterson.domain.alpaca.accountconfiguration.AccountConfiguration;

public class AlpacaExamples {

    AlpacaAPI alpacaAPI;

    public AlpacaExamples(AlpacaAPI alpacaAPI) {
        this.alpacaAPI = alpacaAPI;
    }

    public void logAccountActivity() {
        try {
            // Print all 'AccountActivity's on 12/23/2020
            List<AccountActivity> accountActivities = alpacaAPI.getAccountActivities(null, null,
                    ZonedDateTime.of(2020, 12, 23, 0, 0, 0, 0, ZoneId.of("America/New_York")), SortDirection.ASCENDING,
                    null, null, (ActivityType[]) null);
            for (AccountActivity accountActivity : accountActivities) {
                if (accountActivity instanceof TradeActivity) {
                    System.out.println("TradeActivity: " + (TradeActivity) accountActivity);
                } else if (accountActivity instanceof NonTradeActivity) {
                    System.out.println("NonTradeActivity: " + (NonTradeActivity) accountActivity);
                }
            }
        } catch (AlpacaAPIRequestException e) {
            e.printStackTrace();
        }
    }

    public void blockNewOrders() {
        try {
            // Update the 'AccountConfiguration' to block new orders
            AccountConfiguration accountConfiguration = alpacaAPI.getAccountConfiguration();
            accountConfiguration.setSuspendTrade(true);

            alpacaAPI.setAccountConfiguration(accountConfiguration);
        } catch (AlpacaAPIRequestException e) {
            e.printStackTrace();
        }
    }

}
