package com.nagarro.driven.client.ui.api.table;

import com.nagarro.driven.client.ui.api.Element;
import java.util.List;

public interface Table<T> extends Element<T> {

  Row<T> getRow(Column<T> column, String value);

  Row<T> getRow(int index);

  Row<T> getRow(T locator);

  Row<T> getHeader(T headerLocator);

  List<Row<T>> getRows();

  boolean rowExists(Column<T> column, String value);
}
