/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.ggnet.dwoss.uniqueunit.ui.product;

import javafx.scene.Node;
import javafx.scene.control.Cell;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.util.Callback;
import javafx.util.StringConverter;

/**
 *
 * A modified TextFieldTableCell, with the only difference of exception handling
 * for NumberFormatExceptions fired by it's own StringConverter on cell edits.
 * While not recommended, this class can be used for every TableColumn type
 * Number. This implementation is specifically designed to provide proper
 * editing on a supertype of Number TableColumn with a PropertyValueFactory. If
 * you want to use this class unrecommended, set the cellFactory on your
 * TableColumn of Type Number by calling static method
 * forTableColumn(StringConverter converter). The implemented difference can be
 * found in method static TextField createTextField(final Cell cell, final
 * StringConverter converter) in line 91.
 *
 * @param <S> type of the TableView
 * @param <T> type of the TableColumn
 */
public class CustomTableCell<S, T> extends TextFieldTableCell<S, T> {

    private TextField textField;

    public CustomTableCell(StringConverter<T> converter)
    {
        super(converter);
        setConverter(converter);
    }

    @Override
    public void startEdit()
    {
        if (!isEditable()
                || !getTableView().isEditable()
                || !getTableColumn().isEditable())
        {
            return;
        }
        super.startEdit();

        if (isEditing())
        {
            if (textField == null)
            {
                textField = createTextField(this, getConverter());
            }

            startEdit(this, getConverter(), null, null, textField);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateItem(T item, boolean empty)
    {
        super.updateItem(item, empty);
        updateItem(this, getConverter(), null, null, textField);

    }

    static <T> TextField createTextField(final Cell<T> cell, final StringConverter<T> converter)
    {
        final TextField textField = new TextField(getItemText(cell, converter));

        // Use onAction here rather than onKeyReleased (with check for Enter),
        // as otherwise we encounter RT-34685
        textField.setOnAction(event ->
        {
            if (converter == null)
            {
                throw new IllegalStateException(
                        "Attempting to convert text input into Object, but provided "
                        + "StringConverter is null. Be sure to set a StringConverter "
                        + "in your cell factory.");
            }
            /**
             * original code:
             * cell.commitEdit(converter.fromString(textField.getText()));
             * event.consume();
             */

            try
            {
                cell.commitEdit(converter.fromString(textField.getText()));
            } catch (NumberFormatException e)
            {
                cell.cancelEdit();
            } finally
            {

                event.consume();
            }
        });
        textField.setOnKeyReleased(t ->
        {
            if (t.getCode() == KeyCode.ESCAPE)
            {
                cell.cancelEdit();
                t.consume();
            }
        });
        return textField;
    }

    private static <T> String getItemText(Cell<T> cell, StringConverter<T> converter)
    {
        return converter == null
                ? cell.getItem() == null ? "" : cell.getItem().toString()
                : converter.toString(cell.getItem());
    }

    static <T> void updateItem(final Cell<T> cell,
            final StringConverter<T> converter,
            final TextField textField)
    {
        updateItem(cell, converter, null, null, textField);
    }

    static <T> void updateItem(final Cell<T> cell,
            final StringConverter<T> converter,
            final HBox hbox,
            final Node graphic,
            final TextField textField)
    {
        if (cell.isEmpty())
        {
            cell.setText(null);
            cell.setGraphic(null);
        } else
        {
            if (cell.isEditing())
            {
                if (textField != null)
                {
                    textField.setText(getItemText(cell, converter));
                }
                cell.setText(null);

                if (graphic != null)
                {
                    hbox.getChildren().setAll(graphic, textField);
                    cell.setGraphic(hbox);
                } else
                {
                    cell.setGraphic(textField);
                }
            } else
            {
                cell.setText(getItemText(cell, converter));
                cell.setGraphic(graphic);
            }
        }
    }

    static <T> void startEdit(final Cell<T> cell,
            final StringConverter<T> converter,
            final HBox hbox,
            final Node graphic,
            final TextField textField)
    {
        if (textField != null)
        {
            textField.setText(getItemText(cell, converter));
        }
        cell.setText(null);

        if (graphic != null)
        {
            hbox.getChildren().setAll(graphic, textField);
            cell.setGraphic(hbox);
        } else
        {
            cell.setGraphic(textField);
        }

        textField.selectAll();

        // requesting focus so that key input can immediately go into the
        // TextField (see RT-28132)
        textField.requestFocus();
    }

    public static <S, T> Callback<TableColumn<S, T>, TableCell<S, T>> forTableColumn(
            final StringConverter<T> converter)
    {
        return list -> new CustomTableCell<S, T>(converter);
    }
}
