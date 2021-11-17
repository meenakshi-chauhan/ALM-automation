package com.nagarro.driven.client.ui.api.table;

import com.nagarro.driven.client.ui.api.Element;

public interface Row<T> extends Element<T> {

  Element<T> getCell(Column<T> column);

}
