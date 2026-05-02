package GamieBot.presenter;

import GamieBot.presenter.requestHandling.GeneralRequestHandler;
import GamieBot.view.IView;
import GamieBot.model.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

public class Presenter implements IEventListener {
    private static final Logger log = LoggerFactory.getLogger(Presenter.class);

    private final IView view;
    private final GeneralRequestHandler requestHandler;

    public Presenter(IView view) {
        this.view = view;
        this.view.setListener(this);
        log.info("View set to: {}", view.getClass().getName());
        this.requestHandler = new GeneralRequestHandler();
    }

    @Override
    public void onMessageReceived(String chatId, String text) {
        ArrayList<Response> responses = requestHandler.handleRequest(chatId, text);

        for (Response response : responses) {
            view.sendMessage(response.chatId(), response.text());
            log.info("Message sent to chatId {}: {}", response.chatId(), response.text());
        }
    }

    public void run() {
        this.view.run();
        log.info("Presenter started");
    }
}
