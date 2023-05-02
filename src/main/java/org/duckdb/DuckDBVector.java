package org.duckdb;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;

public class DuckDBVector {
	
	public DuckDBVector(String duckdb_type, int length,  boolean[] nullmask) {
		super();
		this.duckdb_type = DuckDBResultSetMetaData.TypeNameToType(duckdb_type);
		this.length = length;
		this.nullmask = nullmask;
	}
	protected DuckDBColumnType duckdb_type;
	protected int length;
	protected boolean[] nullmask;
	protected ByteBuffer constlen_data = null;
	protected Object[] varlen_data = null;

	public Object getObject(int columnIndex) throws SQLException {
		switch (duckdb_type) {
		case INTEGER:
			return getInt(columnIndex);
		case BIGINT:
			return getLong(columnIndex);
		default:
			throw new SQLFeatureNotSupportedException(duckdb_type.toString());
		}
	}

	protected ByteBuffer getbuf(int columnIndex, int typeWidth) {
		ByteBuffer buf = constlen_data;
		buf.order(ByteOrder.LITTLE_ENDIAN);
		buf.position(columnIndex * typeWidth);
		return buf;
	}

	public long getLong(int columnIndex) throws SQLException {
		if (duckdb_type == DuckDBColumnType.BIGINT || duckdb_type == DuckDBColumnType.TIMESTAMP) {
			return getbuf(columnIndex, 8).getLong();
		}
		Object o = getObject(columnIndex);
		if (o instanceof Number) {
			return ((Number) o).longValue();
		}
		return Long.parseLong(o.toString());
	}

	public int getInt(int columnIndex) throws SQLException {
		if (duckdb_type == DuckDBColumnType.INTEGER) {
			return getbuf(columnIndex, 4).getInt();
		}
		Object o = getObject(columnIndex);
		if (o instanceof Number) {
			return ((Number) o).intValue();
		}
		return Integer.parseInt(o.toString());
	}
}
