package services;

import models.Cbu;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.IOException;

public interface MethodsInterface {

    public SendMessage mainMenu(Update update);
    public SendMessage chooseForeignCurrency(Update update);
    public SendMessage inputAmountMeth(Update update, String operation, String currency) throws IOException;
    public SendMessage convertSellAndShow(Update update, String currency) throws IOException;
    public SendMessage convertBuyAndShow(Update update, String currency) throws IOException;
    public boolean isNumeric(String strNum);
    public Cbu cbuReturn (String currency) throws IOException;
}
