package components.tables;

import api.PartialSearchResponse;
import api.objects.playables.Album;
import api.objects.playables.Artist;
import api.objects.playables.TrackSearch;
import javafx.beans.binding.Bindings;
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
    private TableColumn<T, T> idxCol;
    @FXML
    private TableColumn<T, Album> albumCoverCol;
    @FXML
    private TableColumn<T, String> titleCol;
    @FXML
    private TableColumn<T, Artist> artistCol;
    @FXML
    private TableColumn<T, Album> albumCol;
    @FXML
    private TableColumn<T, Integer> durationCol;
    @FXML
    private TableColumn<T, Integer> popularityCol;

    private Consumer<T> rowAction = (row) -> {};

    public void fill(PartialSearchResponse<T> tracks, Label countLabel, boolean clear)
    {
        if (clear) {
            getChildren().clear();
        }
        getItems().addAll(tracks.data());
        if (!tracks.hasPrev() && countLabel != null) {
            countLabel.setText(String.valueOf(tracks.total()));
        }
    }

    public void setRowAction(Consumer<T> rowAction) {
        this.rowAction = rowAction;
    }

    @FXML
    private void initialize() {
        getSelectionModel().selectedItemProperty().addListener((observableValue, oldVal, newVal) ->
                rowAction.accept(newVal)
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
                        imageview.setImage(new Image(album.cover_small().toString(), true));
                    } else {
                        imageview.setImage(null);
                    }
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
                        if (duration != null) {
                            this.setText(LocalTime.ofSecondOfDay(duration)
                                    .format(DateTimeFormatter.ofPattern("mm:ss")));
                        }
                        else {
                            this.setText(null);
                        }
                    }
                };
            }
        });
        popularityCol.setCellValueFactory(new PropertyValueFactory<>("rank"));
    }
}
