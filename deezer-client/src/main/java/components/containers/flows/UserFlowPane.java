package components.containers.flows;

import api.PartialSearchResponse;
import api.objects.utils.User;
import components.containers.cards.UserCard;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Region;

import java.util.function.Consumer;

public class UserFlowPane extends FlowPane {
    public void fill(PartialSearchResponse<User> users, Label countLabel, boolean clear, boolean tryGetNext) {
        Platform.runLater(() -> {
            if (clear)
                getChildren().clear();
            for (User user : users.getData()) {
                UserCard userCard = new UserCard();
                userCard.prefWidthProperty().bind(Bindings.add(-35, widthProperty().divide(4.2)));
                userCard.setPrefHeight(Region.USE_COMPUTED_SIZE);
                userCard.setUser(user);
                userCard.setUserRedirectioner(getUserRedirectioner());
                getChildren().add(userCard);
            }
            if (!users.hasPrev() && countLabel != null)
                countLabel.setText(String.valueOf(users.getTotal()));
        });
    }

    public final ObjectProperty<Consumer<Long>> userRedirectionerProperty() {
        return userRedirectioner;
    }

    public final void setUserRedirectioner(Consumer<Long> value) {
        userRedirectionerProperty().set(value);
    }

    public final Consumer<Long> getUserRedirectioner() {
        return userRedirectionerProperty().get();
    }

    private final ObjectProperty<Consumer<Long>> userRedirectioner = new ObjectPropertyBase<>() {
        @Override
        public Object getBean() {
            return UserFlowPane.this;
        }

        @Override
        public String getName() {
            return "userRedirectioner";
        }
    };
}
