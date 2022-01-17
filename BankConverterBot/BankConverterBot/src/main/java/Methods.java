import com.google.gson.Gson;
import models.Cbu;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import services.MethodsInterface;

import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

public class Methods implements MethodsInterface {

    @Override
    public SendMessage mainMenu(Update update) {

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(update.getMessage().getChatId());
        sendMessage.setText("What would you like to do?");

        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        keyboardMarkup.setResizeKeyboard(true);
        keyboardMarkup.setOneTimeKeyboard(true);

        KeyboardButton sell = new KeyboardButton(MyConst.button_sell);
        KeyboardButton buy = new KeyboardButton(MyConst.button_buy);
        KeyboardButton back = new KeyboardButton(MyConst.button_back);

        KeyboardRow keyboardRow = new KeyboardRow();
        keyboardRow.add(sell);
        keyboardRow.add(buy);

        KeyboardRow keyboardRow2 = new KeyboardRow();
        keyboardRow2.add(back);

        List<KeyboardRow> rowList = new ArrayList<>();
        rowList.add(keyboardRow);
        rowList.add(keyboardRow2);
        keyboardMarkup.setKeyboard(rowList);
        sendMessage.setReplyMarkup(keyboardMarkup);

        return sendMessage;

    }

    @Override
    public SendMessage chooseForeignCurrency(Update update) {

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(update.getMessage().getChatId());
        sendMessage.setText("Choose the currency:");

        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();

        keyboardMarkup.setResizeKeyboard(true);
        keyboardMarkup.setOneTimeKeyboard(true);

        KeyboardButton usd = new KeyboardButton(MyConst.usd);
        KeyboardButton eur = new KeyboardButton(MyConst.eur);
        KeyboardButton cny = new KeyboardButton(MyConst.cny);
        KeyboardButton back = new KeyboardButton(MyConst.button_back);

        KeyboardRow keyboardRow = new KeyboardRow();
        keyboardRow.add(usd);
        keyboardRow.add(eur);
        keyboardRow.add(cny);

        KeyboardRow keyboardRow2 = new KeyboardRow();
        keyboardRow2.add(back);

        List<KeyboardRow> rowList = new ArrayList<>();
        rowList.add(keyboardRow);
        rowList.add(keyboardRow2);
        keyboardMarkup.setKeyboard(rowList);
        sendMessage.setReplyMarkup(keyboardMarkup);

        return sendMessage;

    }

    @Override
    public SendMessage convertSellAndShow(Update update, String currency) throws IOException {

        Message message = update.getMessage();
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(update.getMessage().getChatId());
        String text = message.getText();

        Double inputAmount = 0.0;
        Double sellingResult = 0.0;

        if (isNumeric(text)) {
            inputAmount = Double.parseDouble(text);

            Cbu chosenCbu = new Cbu();
            chosenCbu = cbuReturn(currency);

            sellingResult = inputAmount * chosenCbu.Rate;

            System.out.println("inputAmount => " + inputAmount);
            System.out.println("rateUSD => " + chosenCbu.Rate);
            System.out.println("sellinResult => " + sellingResult);

            NumberFormat formatter = new DecimalFormat("#0.00");

            sendMessage.setText(inputAmount + " " + currency + " is " + sellingResult + " sums.");

            ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();

            keyboardMarkup.setResizeKeyboard(true);
            keyboardMarkup.setOneTimeKeyboard(true);

            KeyboardButton back = new KeyboardButton(MyConst.button_back);

            KeyboardRow keyboardRow = new KeyboardRow();
            keyboardRow.add(back);

            List<KeyboardRow> rowList = new ArrayList<>();
            rowList.add(keyboardRow);
            keyboardMarkup.setKeyboard(rowList);
            sendMessage.setReplyMarkup(keyboardMarkup);

        } else {
            sendMessage.setText("Wrong input!");
        }

        return sendMessage;
    }

    @Override
    public Cbu cbuReturn(String currency) throws IOException {

        Gson gson = new Gson();
        HttpGet httpGet = new HttpGet("https://cbu.uz/ru/arkhiv-kursov-valyut/json/");
        HttpClient httpClient = HttpClients.createDefault();
        HttpResponse httpResponse = httpClient.execute(httpGet);
        InputStreamReader inputStreamReader = new InputStreamReader(httpResponse.getEntity().getContent());
        Cbu[] cbuList = gson.fromJson(inputStreamReader, Cbu[].class);

        Cbu usd = new Cbu();
        Cbu eur = new Cbu();
        Cbu cny = new Cbu();
        Cbu chosenCbu = new Cbu();

        for (Cbu cbu : cbuList) {
            if (cbu.Ccy.equalsIgnoreCase("usd")) {
                usd = cbu;
            } else if (cbu.Ccy.equalsIgnoreCase("eur")) {
                eur = cbu;
            } else if (cbu.Ccy.equalsIgnoreCase("cny")) {
                cny = cbu;
            }
        }

        switch (currency) {
            case MyConst.usd:
                chosenCbu = usd;
                break;
            case MyConst.eur:
                chosenCbu = eur;
                break;
            case MyConst.cny:
                chosenCbu = cny;
                break;
        }

        return chosenCbu;
    }

    @Override
    public SendMessage convertBuyAndShow(Update update, String currency) throws IOException {
        Message message = update.getMessage();
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(update.getMessage().getChatId());
        String text = message.getText();

        Double inputAmount = 0.0;
        Double buyingResult = 0.0;

        if (isNumeric(text)) {
            inputAmount = Double.parseDouble(text);

            Cbu chosenCbu = new Cbu();
            chosenCbu = cbuReturn(currency);

            buyingResult = inputAmount / chosenCbu.Rate;

            System.out.println("inputAmount => " + inputAmount);
            System.out.println("rateUSD => " + chosenCbu.Rate);
            System.out.println("sellinResult => " + buyingResult);

            NumberFormat formatter = new DecimalFormat("#0.00");

            sendMessage.setText("For " + inputAmount + " sums => You can buy " + formatter.format(buyingResult) + " " + currency);

            ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();

            keyboardMarkup.setResizeKeyboard(true);
            keyboardMarkup.setOneTimeKeyboard(true);

            KeyboardButton back = new KeyboardButton(MyConst.button_back);

            KeyboardRow keyboardRow = new KeyboardRow();
            keyboardRow.add(back);

            List<KeyboardRow> rowList = new ArrayList<>();
            rowList.add(keyboardRow);
            keyboardMarkup.setKeyboard(rowList);
            sendMessage.setReplyMarkup(keyboardMarkup);

        } else {
            sendMessage.setText("Wrong input!");
        }

        return sendMessage;
    }

    @Override
    public SendMessage inputAmountMeth(Update update, String operation, String currency) throws IOException {

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(update.getMessage().getChatId());

        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        KeyboardButton back = new KeyboardButton(MyConst.button_back);
        KeyboardRow keyboardRow = new KeyboardRow();
        keyboardRow.add(back);
        List<KeyboardRow> rowList = new ArrayList<>();
        rowList.add(keyboardRow);
        replyKeyboardMarkup.setOneTimeKeyboard(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setKeyboard(rowList);
        sendMessage.setReplyMarkup(replyKeyboardMarkup);


        if (operation.equalsIgnoreCase(BotState.buyForeign)){
            sendMessage.setText("1 " + cbuReturn(currency).Ccy + " = " + cbuReturn(currency).Rate + "\nFor how much sums do you want to buy " + currency + "?");
        }
        if (operation.equalsIgnoreCase(BotState.sellForeign)){
            sendMessage.setText("1 " + cbuReturn(currency).Ccy + " = " + cbuReturn(currency).Rate + "\nHow much " + currency + " do you want convert into sums?");
        }


        return sendMessage;
    }

    @Override
    public boolean isNumeric(String strNum) {

        if (strNum == null) {
            return false;
        }
        try {
            double d = Double.parseDouble(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }
}
