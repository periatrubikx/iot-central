package org.thingsboard.server.dao.model.sql;

import org.thingsboard.server.common.data.shift.Shift;
import org.thingsboard.server.dao.model.BaseSqlEntity;
import org.thingsboard.server.dao.model.SearchTextEntity;

public abstract class AbstractShiftEntity<T extends Shift> extends BaseSqlEntity<T> implements SearchTextEntity<T> {
}
