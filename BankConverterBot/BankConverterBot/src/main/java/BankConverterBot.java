import lombok.SneakyThrows;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

public class BankConverterBot extends TelegramLongPollingBot {

    @Override
    public String getBotUsername() {
        return "http://t.me/AliAkaConverterBot";
    }

    @Override
    public String getBotToken() {
        return "1612255673:AAFaG-hpcNWa5iwl4a0q2zziL-fF-UjuUo8";
    }

    //===== static fields =====
    static String staticBotState = "";
    static String staticOperation = "";
    static String staticForeignCurrency = "";
    static Double staticInputAmount = 0.0;

    //===== static fields =====

    @SneakyThrows
    @Override
    public void onUpdateReceived(Update update) {

        Long chatID = update.getMessage().getChatId();
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatID);
        Methods methods = new Methods();
        Message message = update.getMessage();
        String text = message.getText();

        if (update.hasMessage() && update.getMessage().hasText()) {

            if (text.equalsIgnoreCase(MyConst.button_sell)) {
                staticOperation = BotState.sellForeign;
            } else if (text.equalsIgnoreCase(MyConst.button_buy)) {
                staticOperation = BotState.buyForeign;
            }
        }
        if (staticBotState.equalsIgnoreCase(BotState.show)){

                if (staticOperation.equalsIgnoreCase(BotState.sellForeign)){
                    execute(methods.convertSellAndShow(update, staticForeignCurrency));
                } else if (staticOperation.equalsIgnoreCase(BotState.buyForeign)){
                    execute(methods.convertBuyAndShow(update, staticForeignCurrency));
                }
                staticBotState = BotState.mainMenu;
        }

        if (update.hasMessage() && update.getMessage().getText().equalsIgnoreCase("/start")) {
            sendMessage.setText("Welcome to ConverterBot, " + update.getMessage().getFrom().getFirstName() + "!");
            execute(sendMessage);
            staticBotState = BotState.mainMenu;
        }

        if (update.hasMessage() && update.getMessage().getText().equalsIgnoreCase(MyConst.button_back)) {
            staticBotState = BotState.mainMenu;
            staticOperation = "";
            staticForeignCurrency = "";
            staticInputAmount = 0.0;
        }

        switch (staticBotState) {
            case BotState.mainMenu:
                execute(methods.mainMenu(update));
                staticBotState = BotState.chooseCurrency;
                break;
            case BotState.chooseCurrency:
                execute(methods.chooseForeignCurrency(update));
                staticBotState = BotState.inputAmount;
                break;
            case BotState.inputAmount:
                staticForeignCurrency = message.getText();
                execute(methods.inputAmountMeth(update, staticOperation, staticForeignCurrency));
                staticBotState = BotState.show;
                break;
            default:
                sendMessage.setText("Wrong choice, dude!");
                staticBotState = BotState.mainMenu;
                break;
        }
    }
}
