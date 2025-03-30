package components.containers.tables;

import api.PartialSearchResponse;
import api.objects.playables.Album;
import api.objects.playables.Artist;
import api.objects.playables.TrackSearch;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Callback;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.function.Consumer;

public class TrackTable<T extends TrackSearch> extends TableView<T> {
    @FXML
    TableColumn<T, T> idxCol;
    @FXML
    TableColumn<T, Album> albumCoverCol;
    @FXML
    TableColumn<T, String> titleCol;
    @FXML
    TableColumn<T, Artist> artistCol;
    @FXML
    TableColumn<T, Album> albumCol;
    @FXML
    TableColumn<T, Integer> durationCol;
    @FXML
    TableColumn<T, Integer> popularityCol;

    public void fill(PartialSearchResponse<T> tracks, Label countLabel, boolean clear)
    {
        if (clear)
            getChildren().clear();
        getItems().addAll(tracks.getData());
        if (!tracks.hasPrev() && countLabel != null)
            countLabel.setText(String.valueOf(tracks.getTotal()));
    }

    @FXML
    private void initialize() {
        getSelectionModel().selectedItemProperty().addListener((observableValue, oldVal, newVal) ->
                getTrackSelectioner().accept(newVal)
        );
        prefHeightProperty().bind(fixedCellSizeProperty().multiply(Bindings.size(getItems()).add(1.01)));
        minHeightProperty().bind(prefHeightProperty());
        maxHeightProperty().bind(prefHeightProperty());
        idxCol.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
        idxCol.setCellFactory(new Callback<>() {
            @Override
            public TableCell<T, T> call(TableColumn<T, T> param) {
                return new TableCell<>() {
                    @Override
                    protected void updateItem(T item, boolean empty) {
                        super.updateItem(item, empty);
                        setText(this.getTableRow() == null || item == null ? "" :
                                String.valueOf(this.getTableRow().getIndex() + 1));
                    }
                };
            }
        });
        albumCoverCol.setCellValueFactory(new PropertyValueFactory<>("album"));
        albumCoverCol.setCellFactory(param -> {
            final ImageView imageview = new ImageView();
            imageview.setFitHeight(28);
            imageview.setFitWidth(28);
            TableCell<T, Album> cell = new TableCell<>() {
                @Override
                public void updateItem(Album album, boolean empty) {
                    if (album != null && imageview.getImage() == null) {
                        imageview.setImage(new Image(album.getCover_small().toString(), true));
                    } else imageview.setImage(null);
                }
            };
            cell.setGraphic(imageview);
            return cell;
        });
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        artistCol.setCellValueFactory(new PropertyValueFactory<>("artist"));
        albumCol.setCellValueFactory(new PropertyValueFactory<>("album"));
        durationCol.setCellValueFactory(new PropertyValueFactory<>("duration"));
        durationCol.setCellFactory(new Callback<>() {
            @Override
            public TableCell<T, Integer> call(TableColumn<T, Integer> param) {
                return new TableCell<>() {
                    @Override
                    public void updateItem(Integer duration, boolean empty) {
                        if (duration != null)
                            this.setText(LocalTime.ofSecondOfDay(duration)
                                    .format(DateTimeFormatter.ofPattern("mm:ss")));
                        else this.setText(null);
                    }
                };
            }
        });
        popularityCol.setCellValueFactory(new PropertyValueFactory<>("rank"));
    }

    public final ObjectProperty<Consumer<T>> trackSelectionerProperty() {
        return trackSelectioner;
    }

    public final void setTrackSelectioner(Consumer<T> value) {
        trackSelectionerProperty().set(value);
    }

    public final Consumer<T> getTrackSelectioner() {
        return trackSelectionerProperty().get();
    }

    private final ObjectProperty<Consumer<T>> trackSelectioner = new ObjectPropertyBase<>() {
        @Override
        public Object getBean() {
            return TrackTable.this;
        }

        @Override
        public String getName() {
            return "userRedirectioner";
        }
    };
}
