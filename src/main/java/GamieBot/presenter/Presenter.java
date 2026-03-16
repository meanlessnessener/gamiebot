package GamieBot.presenter;

import GamieBot.presenter.requestHandling.GeneralRequestHandler;
import GamieBot.view.IView;
import GamieBot.model.Response;

import java.util.ArrayList;
import java.util.Arrays;



public class Presenter implements IEventListener{
    private final IView view;
    private final GeneralRequestHandler requestHandler;

    public Presenter(IView view) {
        this.view = view;
        this.view.setListener(this);
        this.requestHandler = new GeneralRequestHandler();
    }
    
    @Override
    public void onMessageReceived(String chatId, String text) {
        ArrayList<Response> responses = requestHandler.handleRequest(chatId, text);

        for (Response response : responses) {
            view.sendMessage(response.chatId(), response.text());
        }
    }
    
    public void run() {
        this.view.run();
    }
}
